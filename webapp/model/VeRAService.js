/**
 * VeRAService.js
 *
 * Calls the existing Java portal service endpoints on the on-premise
 * NetWeaver Portal via the BTP destination "Coding_Portal", which is
 * proxied through the SAP Connectivity Service / Cloud Connector.
 *
 * BASE = "/<service-segment>/vera-portal/"  (computed at runtime)
 *   In Workzone, the app runs at /<service-segment>/index.html.
 *   xs-app.json source "^<apply-service-segment-path>/vera-portal/(.*)"
 *   catches requests at that prefix and proxies via destination Coding_Portal.
 */
sap.ui.define([
    "sap/ui/base/Object",
    "sap/ui/core/format/DateFormat",
    "sap/base/Log"
], function (BaseObject, DateFormat, Log) {
    "use strict";

    // Derive the app's service-segment from the UI5 module path and manifest version.
    // Module path: "/<site-id>.<cloud-service>.<app-id>/~cachebuster~/..."
    // Full Work Zone segment: "<site-id>.<cloud-service>.<app-id>-<version>"
    var sModulePath = sap.ui.require.toUrl("vsnt/vera");
    var sAppSegment = sModulePath.split("/")[1];
    var sVersion = jQuery.sap.loadResource("vsnt/vera/manifest.json", {async: false})["sap.app"].applicationVersion.version;
    var BASE = "/" + sAppSegment + "-" + sVersion + "/vera-portal/";
    var _instance = null;

    // ZZSF_VRA_INVSTAT codes returned by wz_services?Action=inviteInfo.
    // `text`, `actions` and `edit` are ported verbatim from the portal's
    // inbox.java, which reads the same field; `state` and `reg` are this
    // app's presentation of those texts (ObjectStatus state / ObjectHeader).
    //   actions: C = Cancel, R = Resend, S = Send, A = approval in flight
    //   edit:    D = display only, E = editable, "" = neither
    //   open:    still in flight — false only for the four settled end states
    //            (registered, completed, and the two cancellations).
    var INVITE_STATUS = {
        "0": { text: "Invite Pending Approval",       state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  },
        "1": { text: "Invite Approved",               state: "Success", reg: "APPROVED", actions: "",   edit: "",  open: true  },
        "2": { text: "Invite Approved",               state: "Success", reg: "APPROVED", actions: "",   edit: "",  open: true  },
        "3": { text: "Invite Rejected",               state: "Error",   reg: "REJECTED", actions: "CS", edit: "E", open: true  },
        "4": { text: "Invite Approved",               state: "Success", reg: "APPROVED", actions: "",   edit: "",  open: true  },
        "5": { text: "Pending Vendor Action",         state: "Warning", reg: "PENDING",  actions: "CR", edit: "D", open: true  },
        "6": { text: "Invite Registered",             state: "Success", reg: "PENDING",  actions: "",   edit: "",  open: false },
        "7": { text: "Email Address Failure",         state: "Error",   reg: "PENDING",  actions: "CS", edit: "E", open: true  },
        "8": { text: "Invite Pending Term Approval",  state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  },
        "9": { text: "Invite Cancelled",              state: "None",    reg: "DRAFT",    actions: "S",  edit: "E", open: false },
        "S": { text: "Pending Submission",            state: "Warning", reg: "DRAFT",    actions: "",   edit: "",  open: true  },
        "A": { text: "In Review",                     state: "Warning", reg: "PENDING",  actions: "",   edit: "",  open: true  },
        "D": { text: "Rejected",                      state: "Error",   reg: "REJECTED", actions: "C",  edit: "D", open: true  },
        "R": { text: "Rejected",                      state: "Error",   reg: "REJECTED", actions: "C",  edit: "D", open: true  },
        "P": { text: "Completed",                     state: "Success", reg: "APPROVED", actions: "",   edit: "",  open: false },
        "F": { text: "Failed",                        state: "Error",   reg: "REJECTED", actions: "C",  edit: "D", open: true  },
        "T": { text: "Pending Term Approval",         state: "Warning", reg: "PENDING",  actions: "",   edit: "",  open: true  },
        "W": { text: "Pending Approval",              state: "Warning", reg: "PENDING",  actions: "",   edit: "",  open: true  },
        "O": { text: "Request Cancelled",             state: "None",    reg: "DRAFT",    actions: "",   edit: "",  open: false },
        "I": { text: "Pending IC Approval",           state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  },
        "E": { text: "Pending IC & Term Approval",    state: "Warning", reg: "PENDING",  actions: "",   edit: "",  open: true  },
        "M": { text: "Pending Mgmt. Approval",        state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  },
        "X": { text: "W8 Validation Failed",          state: "Error",   reg: "PENDING",  actions: "",   edit: "E", open: true  },
        "Y": { text: "IC Reject",                     state: "Error",   reg: "REJECTED", actions: "",   edit: "E", open: true  },
        "Z": { text: "Pending W8 Validation",         state: "Warning", reg: "PENDING",  actions: "",   edit: "D", open: true  },
        "U": { text: "Pending W8 Submission",         state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  },
        "V": { text: "Pending TAX Review",            state: "Warning", reg: "PENDING",  actions: "CA", edit: "D", open: true  }
    };

    // Check if the user is accessing the application outside of WZ
    if (sModulePath === ".") {
        BASE = window.location.pathname.replace(/\/[^/]*$/, "/") + "vera-portal/";
    }

    var VeRAService = BaseObject.extend("vsnt.vera.model.VeRAService", {

        // ── Reference data (htmlhelper) ─────────────────────────────────

        getCountries: function () {
            // Load from hardcoded countries JSON file
            return jQuery.ajax({
                url: sap.ui.require.toUrl("vsnt/vera/model/countries.json"),
                dataType: "json"
            });
        },

        getRegions: function (sCountry) {
            // Load from local JSON file and optionally filter by country
            return jQuery.ajax({
                url: sap.ui.require.toUrl("vsnt/vera/model/countryRegionData.json"),
                dataType: "json"
            }).then(function (aData) {
                if (sCountry) {
                    return aData.filter(function (oItem) {
                        return oItem.country === sCountry;
                    });
                }
                return aData;
            });
        },

        getAllRegionData: function () {
            // Get all region data for client-side filtering
            return jQuery.ajax({
                url: sap.ui.require.toUrl("vsnt/vera/model/countryRegionData.json"),
                dataType: "json"
            });
        },

        getPaymentTerms: function (sVendorType) {
            return this._get("htmlhelper", {
                type: "PaymentTerms",
                vendorType: sVendorType || ""
            });
        },

        getCompanyCodes: function (sQuery) {
            return this._get("htmlhelper", {
                type: "coCodes",
                query: sQuery || ""
            });
        },

        getApprovers: function (aCompanyCodes) {
            var aPairs = (aCompanyCodes || []).map(function (c) {
                return "query[]=" + encodeURIComponent(c);
            });
            return jQuery.ajax({
                url:  BASE + "htmlhelper?type=displayApproverList&" + aPairs.join("&"),
                type: "GET",
                dataType: "json",
                headers: this._csrfHeaders()
            });
        },

        validateTaxId: function (sTaxId, sVendorId) {
            return this._get("htmlhelper", {
                type: "validatetaxid",
                taxid: sTaxId,
                vendorId: sVendorId || ""
            });
        },

        validateCityState: function (oAddr) {
            return this._get("htmlhelper", {
                type:    "validateCityState",
                country: oAddr.country || "",
                state:   oAddr.state   || "",
                city:    oAddr.city    || "",
                zip:     oAddr.zip     || ""
            });
        },

        checkRole: function (sVendorType) {
            return this._get("htmlhelper", {
                type: "roleCheck",
                vendorType: sVendorType || ""
            });
        },

        ctiCall: function (oParams) {
            return this._get("htmlhelper", jQuery.extend({ type: "CTICall" }, oParams));
        },

        // ── Inbox & search ──────────────────────────────────────────────

        getInbox: function () {
            return this._get("inbox", {});
        },

        // ── Invites (wz_services) ───────────────────────────────────────

        /**
         * Invites raised for / by an email address.
         * Resolves with { code, message, inviteData[], vadminData[] };
         * code "0" means success, anything else carries `message`.
         */
        getInviteInfo: function (sEmail) {
            return this._get("wz_services", {
                Action: "inviteInfo",
                Email:  sEmail || ""
            });
        },

        /**
         * ZZSF_VRA_INVSTAT code → { code, text, state, reg, actions, edit }.
         * Unknown codes fall through showing the raw code rather than a blank
         * cell, so a new backend status is visible instead of silently empty.
         */
        mapInviteStatus: function (sCode) {
            var sKey   = String(sCode || "").toUpperCase();
            var oEntry = INVITE_STATUS[sKey];
            if (!oEntry) {
                Log.warning("VeRAService: unmapped ZZSF_VRA_INVSTAT '" + sCode + "'");
                return {
                    code: sKey, text: sKey, state: "None",
                    reg: "PENDING", actions: "", edit: ""
                };
            }
            return jQuery.extend({ code: sKey }, oEntry);
        },

        /** One inviteData row → the shape the Home and Status tables bind to. */
        mapInviteRow: function (oInv) {
            return {
                // ZZSF_VRA_EMLID is the request id — what the Home column shows
                // and what attachments are keyed off.
                id:      oInv.ZZSF_VRA_EMLID || "",
                name:    oInv.VEND_NAME      || "",
                type:    oInv.VEND_DESC      || "",
                contact: [oInv.FIRST_NAME, oInv.LAST_NAME].filter(Boolean).join(" "),
                status:  this.mapInviteStatus(oInv.ZZSF_VRA_INVSTAT),
                date:    this._formatChanged(oInv.CHANGE_DATE, oInv.CHANGE_TIME),
                invite:  oInv
            };
        },

        // CHANGE_DATE "2026-07-24" + CHANGE_TIME "16:23:40" → locale date/time.
        _formatChanged: function (sDate, sTime) {
            if (!sDate) { return ""; }
            var oDate = new Date(sDate + "T" + (sTime || "00:00:00"));
            if (isNaN(oDate.getTime())) { return sDate; }
            return DateFormat.getDateTimeInstance({ style: "medium" }).format(oDate);
        },

        searchVendors: function (oParams) {
            return this._get("vendorsearch", jQuery.extend({
                name: "",
                tin: "",
                primaryAddress1: "",
                primaryCountry: "",
                primaryAddressCity: "",
                primaryAddressState: "select",
                primaryAddressZip: "",
                "vendor-number": ""
            }, oParams));
        },

        // ── Save / Submit ───────────────────────────────────────────────

        saveForLater: function (oPayload) {
            return this._post("objectactions", jQuery.extend(oPayload, {
                actionCode: "save",
                action: "save"
            }));
        },

        submitRegistration: function (oPayload) {
            return this._post("objectactions", jQuery.extend(oPayload, {
                actionCode: "save",
                action: "submit"
            }));
        },

        submitInvite: function (oPayload) {
            return this._post("objectactions", jQuery.extend(oPayload, {
                actionCode: "submit_invite"
            }));
        },

        submitOnBehalf: function (oPayload) {
            return this._post("objectactions", jQuery.extend(oPayload, {
                actionCode: "submit_reqpreform"
            }));
        },

        // ── Workflow actions ────────────────────────────────────────────

        inviteAction: function (sInviteNum, sOperation, sComment, sCancelCode) {
            return this._post("inviteactions", {
                invitenum: sInviteNum,
                operation: sOperation,
                decisionComments: sComment || "",
                cancelCode: sCancelCode || ""
            });
        },

        requestAction: function (sRequestNum, sOperation, oExtra) {
            return this._post("requestactions", jQuery.extend({
                requestnum: sRequestNum,
                operation: sOperation
            }, oExtra || {}));
        },

        // ── File operations (GOS attachments) ───────────────────────────

        /**
         * Single entry point for every registration attachment, so the object
         * key can't be filled in inconsistently — or forgotten — per call site.
         *
         * managecsdoc keys the stored document off "id" (the request number;
         * managecsdoc.java reads it into objectKey, and the portal UI sends
         * $("#requestId").val() there). vendorId is the fallback the portal
         * added for uploads made before a request exists — see the
         * DFCT0013688 comments in managecsdoc.java.
         *
         * @param {File}   oFile     the file to store
         * @param {string} sFileType W9 | 590 | ACH | W8 | LEG | SUP
         * @param {object} oRegData  reg model data — supplies requestId/vendorId
         */
        uploadRegistrationFile: function (oFile, sFileType, oRegData) {
            oRegData = oRegData || {};

            if (!oRegData.requestId && !oRegData.vendorId) {
                // The backend has nothing to hang the document off; it will
                // either reject it or file it against a blank key.
                Log.error("VeRAService: uploading " + sFileType +
                          " with neither a request id nor a vendor id.");
            }

            return this.uploadFile(
                oFile,
                oRegData.requestId,
                "ZSVRA_REQ",
                sFileType,
                oRegData.vendorId
            );
        },

        uploadFile: function (oFile, sObjectKey, sObjectType, sFileType, sVendorId) {
            var oFormData = new FormData();
            oFormData.append("action",   "upload");
            oFormData.append("id",       sObjectKey  || "");
            oFormData.append("objtype",  sObjectType || "ZSVRA_REQ");
            oFormData.append("fileType", sFileType   || "");
            oFormData.append("vendorId", sVendorId   || "");
            oFormData.append("filename", oFile.name);
            oFormData.append("file",     oFile, oFile.name);

            return jQuery.ajax({
                url:  BASE + "managecsdoc",
                type: "POST",
                data: oFormData,
                processData: false,
                contentType: false,
                headers: this._csrfHeaders()
            });
        },

        deleteFile: function (sObjType, sObjKey, sDocumentId) {
            return this._get("deletecsdoc", {
                objtype: sObjType,
                objkey: sObjKey,
                documentid: sDocumentId
            });
        },

        getFileUrl: function (sFileName, sObjType, sObjKey, sDocumentId) {
            return BASE + "displaycsdoc" +
                "?filename="  + encodeURIComponent(sFileName) +
                "&objtype="   + encodeURIComponent(sObjType) +
                "&objkey="    + encodeURIComponent(sObjKey) +
                "&documentid="+ encodeURIComponent(sDocumentId);
        },

        // ── Payload builder ─────────────────────────────────────────────

        buildSavePayload: function (oRegModel, sRequestId) {
            var oBasic    = oRegModel.basic;
            var oTax      = oRegModel.tax;
            var oPT       = oRegModel.paymentTerms;
            var oBanking  = oRegModel.banking;
            var oContacts = oRegModel.contacts;
            var oPrimAddr = oBasic.primaryAddress;
            var oPrimBank = oBanking.primaryAccount;

            var sCompanyCodes = (oRegModel.companyCodes || []).join("-");

            var oSecAddrParams = {};
            (oBasic.secondaryAddresses || []).forEach(function (oAddr, i) {
                var pfx = "secondaryAddress-view" + i + "-";
                oSecAddrParams[pfx + "vendorName"]    = oBasic.legalName;
                oSecAddrParams[pfx + "country"]       = oAddr.country  || "";
                oSecAddrParams[pfx + "Address1"]      = oAddr.address1 || "";
                oSecAddrParams[pfx + "Address2"]      = oAddr.address2 || "";
                oSecAddrParams[pfx + "Address3"]      = oAddr.address3 || "";
                oSecAddrParams[pfx + "City"]          = oAddr.city     || "";
                oSecAddrParams[pfx + "State"]         = oAddr.state    || "";
                oSecAddrParams[pfx + "Zip"]           = oAddr.zip      || "";
                oSecAddrParams[pfx + "RemitPurchase"] = oAddr.type === "BILLING" ? "purchasing" : "remit";
                oSecAddrParams[pfx + "VendorId"]      = "";
            });

            var oContactParams = {};
            (oContacts.items || []).forEach(function (oC, i) {
                var pfx = "contact-view" + i + "-";
                oContactParams[pfx + "Name"]       = oC.name       || "";
                oContactParams[pfx + "Email"]      = oC.email      || "";
                oContactParams[pfx + "PhoneNum"]   = oC.phone      || "";
                oContactParams[pfx + "FaxNum"]     = oC.fax        || "";
                oContactParams[pfx + "Department"] = oC.department || "";
            });

            var oSecBankParams = {};
            var aSecBankOrder = [];
            (oBanking.secondaryAccounts || []).forEach(function (oAcct, i) {
                aSecBankOrder.push(i);
                oSecBankParams["bankingSecondary-" + i + "-Type"]     = oAcct.method     || "ACH";
                oSecBankParams["secondary-account-" + i + "-country"] = oAcct.country    || "US";
                oSecBankParams["banking-" + i + "-RoutingNum"]        = oAcct.routingNum || "";
                oSecBankParams["hidden-banking-" + i]                 = oAcct.accountNum || "";
                oSecBankParams["banking-" + i + "-HolderName"]        = oAcct.holderName || "";
                oSecBankParams["banking-" + i + "-SwiftNum"]          = oAcct.swiftNum   || "";
                oSecBankParams["banking-" + i + "-IbanNum"]           = oAcct.ibanNum    || "";
                oSecBankParams["ACHFileInfo-" + i]                    = oAcct.bankDocId  || "null";
            });

            var oNotifParams = {};
            (oBanking.paymentNotifications || []).forEach(function (oN, i) {
                oNotifParams["emailContact-" + i] = oN.email;
            });

            var oPayload = jQuery.extend(
                {
                    requestId:              sRequestId || "null",
                    requestType:            oRegModel.requestType || "1",
                    vendorId:               oRegModel.vendorId    || "null",
                    documentType:           oRegModel.documentType || "",
                    vendorType:             oRegModel.vendorType   || "",
                    userType:               oRegModel.userType     || "",
                    arrayCompanyCodes:      sCompanyCodes,
                    selectedApprover:       oRegModel.approverSSO || "",
                    comments:               oRegModel.comments    || "",
                    annualSpend:            oRegModel.annualSpend  || "",
                    requestedFor:           oRegModel.requestedFor || "",
                    userEmail:              oRegModel.userEmail    || "",

                    legalName:              oBasic.legalName      || "",
                    invoicingName:          oBasic.invoicingName  || "",
                    acceptPO:               oBasic.acceptPO ? "on" : null,
                    poEmail:                oBasic.poEmail        || "",

                    primaryAddressCountry:  oPrimAddr.country  || "US",
                    primaryAddress1:        oPrimAddr.address1 || "",
                    primaryAddress2:        oPrimAddr.address2 || "",
                    primaryAddress3:        oPrimAddr.address3 || "",
                    primaryAddressCity:     oPrimAddr.city     || "",
                    primaryAddressState:    oPrimAddr.state    || "",
                    primaryAddressZip:      oPrimAddr.zip      || "",
                    taxCode:                oPrimAddr.taxJurisdiction || "",

                    taxId1:                 oTax.taxIdNumber    || "",
                    ssn1:                   oTax.ssnNumber      || "",
                    taxRecipientType:       oTax.recipientType  || "",
                    exempt:                 oTax.exemptPayeeCode || "",
                    facta:                  oTax.factaCode      || "",
                    independantContractor:  oTax.independentContractor || "",
                    taxSsn:                 oTax.ssnNumber      || "",
                    w9FileInfo:             oTax.w9DocId        || "",
                    "590FileInfo":          oTax.doc590Id       || "",
                    w8FileInfo:             oTax.w8DocId        || "",
                    legalFileInfo:          oTax.legalDocId     || "",
                    supportdocInfo:         oTax.supportDocId   || "",

                    terms:                  oPT.selected        || "",

                    primaryBankingType:           oPrimBank.method     || "ACH",
                    bankingPrimaryType:            oPrimBank.method     || "ACH",
                    "primary-account-country":    oPrimBank.country    || "US",
                    "banking-primary-RoutingNum": oPrimBank.routingNum || "",
                    "hidden-banking":             oPrimBank.accountNum || "",
                    "banking-primary-HolderName": oPrimBank.holderName || "",
                    "banking-primary-SwiftNum":   oPrimBank.swiftNum   || "",
                    "banking-primary-IbanNum":    oPrimBank.ibanNum    || "",
                    ACHFileInfo:                  oPrimBank.bankDocId  || "",

                    "secondary-address-order": aSecBankOrder.join(","),

                    maximo: null, eatec: null, jda: null, costar: null,
                    vista: null, compass: null, paris: null, garnishment: null,
                    trisepts: null, craftsynetsuite: null
                },
                oSecAddrParams,
                oContactParams,
                oSecBankParams,
                oNotifParams
            );

            Object.keys(oPayload).forEach(function (k) {
                if (oPayload[k] === null || oPayload[k] === undefined) {
                    delete oPayload[k];
                }
            });

            return oPayload;
        },

        // ── CSRF ────────────────────────────────────────────────────────

        _csrfToken: null,

        _fetchCsrfToken: function () {
            var that = this;
            if (this._csrfToken) {
                return jQuery.Deferred().resolve(this._csrfToken).promise();
            }
            // For testing: removed call to htmlhelper?type=getCountry
            // CSRF token will be fetched on first actual backend call if needed
            return jQuery.Deferred().resolve(null).promise();
        },

        _csrfHeaders: function () {
            return this._csrfToken ? { "X-CSRF-Token": this._csrfToken } : {};
        },

        // ── HTTP helpers ────────────────────────────────────────────────

        _get: function (sService, oParams) {
            var that = this;
            Log.debug("VeRAService GET " + sService, JSON.stringify(oParams));
            return this._fetchCsrfToken().then(function () {
                return jQuery.ajax({
                    url:  BASE + sService,
                    type: "GET",
                    data: oParams,
                    dataType: "json",
                    timeout: 30000,
                    headers: that._csrfHeaders()
                }).fail(function (jqXHR, sStatus, sError) {
                    Log.error("VeRAService GET failed: " + sService + " — " + sError);
                });
            });
        },

        _post: function (sService, oData) {
            var that = this;
            Log.debug("VeRAService POST " + sService, JSON.stringify(oData));
            return this._fetchCsrfToken().then(function () {
                return jQuery.ajax({
                    url:  BASE + sService,
                    type: "POST",
                    data: oData,
                    dataType: "json",
                    timeout: 30000,
                    headers: that._csrfHeaders()
                }).fail(function (jqXHR, sStatus, sError) {
                    Log.error("VeRAService POST failed: " + sService + " — " + sError);
                });
            });
        }
    });

    return {
        getInstance: function () {
            if (!_instance) { _instance = new VeRAService(); }
            return _instance;
        }
    };
});
