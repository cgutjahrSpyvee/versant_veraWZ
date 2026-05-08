/**
 * VeRAService.js
 *
 * Calls the existing Java portal service endpoints on the on-premise
 * NetWeaver Portal via the BTP destination "Coding_Portal_QA", which is
 * proxied through the SAP Connectivity Service / Cloud Connector.
 *
 * BASE = "/<service-segment>/vera-portal/"  (computed at runtime)
 *   In Workzone, the app runs at /<service-segment>/index.html.
 *   xs-app.json source "^<apply-service-segment-path>/vera-portal/(.*)"
 *   catches requests at that prefix and proxies via destination Coding_Portal_QA.
 */
sap.ui.define([
    "sap/ui/base/Object",
    "sap/base/Log"
], function (BaseObject, Log) {
    "use strict";

    // In Workzone / HTML5 App Repo the app is served at /<service-segment>/index.html.
    // XHR must go through that same prefix so the managed approuter picks up the
    // xs-app.json route "<apply-service-segment-path>/vera-portal/(.*)".
    // window.location.pathname.replace strips the filename, leaving "/<service-segment>/".
    var BASE = window.location.pathname.replace(/\/[^/]*$/, "/") + "vera-portal/";
    var _instance = null;

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
