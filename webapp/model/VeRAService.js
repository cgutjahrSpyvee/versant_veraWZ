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
    "sap/base/Log",
    "vsnt/vera/model/paymentMethods"
], function (BaseObject, DateFormat, Log, paymentMethods) {
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

    // INVSTAT codes on inviteREQData — the IT_INVITE_REQ ("inbox") rows that
    // wz_services?Action=inviteInfo returns alongside the invites. This is the
    // registration request's own status; the invite's ZZSF_VRA_INVSTAT above is
    // FYI only once a request exists.
    //
    // `text` is ported from inbox.java, which reads the same field.
    //
    // `edit` is narrower than the portal's own request-row rule. vra_inbox.java
    // renders a pencil for S, R, D, F, X and Y; the decision here is that an
    // existing request is display-only in every status except a rejection, so
    // only the three rejections open for editing:
    //   R  Rejected        D  Rejected (auto)        Y  IC Reject
    // S (Pending Submission), F (Failed) and X (W8 Validation Failed) are
    // deliberately display-only despite the portal allowing edits.
    //   edit: E = editable, D = display only
    var REQUEST_STATUS = {
        "S": { text: "Pending Submission",         state: "Warning", reg: "DRAFT",    edit: "D", open: true  },
        "A": { text: "In Review",                  state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "W": { text: "Pending Approval",           state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "R": { text: "Rejected",                   state: "Error",   reg: "REJECTED", edit: "E", open: true  },
        "D": { text: "Rejected",                   state: "Error",   reg: "REJECTED", edit: "E", open: true  },
        "F": { text: "Failed",                     state: "Error",   reg: "REJECTED", edit: "D", open: true  },
        "X": { text: "W8 Validation Failed",       state: "Error",   reg: "PENDING",  edit: "D", open: true  },
        "Y": { text: "IC Reject",                  state: "Error",   reg: "REJECTED", edit: "E", open: true  },
        "P": { text: "Completed",                  state: "Success", reg: "APPROVED", edit: "D", open: false },
        "O": { text: "Request Cancelled",          state: "None",    reg: "DRAFT",    edit: "D", open: false },
        "I": { text: "Pending IC Approval",        state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "E": { text: "Pending IC & Term Approval", state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "M": { text: "Pending Mgmt. Approval",     state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "T": { text: "Pending Term Approval",      state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "U": { text: "Pending W8 Submission",      state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "V": { text: "Pending TAX Review",         state: "Warning", reg: "PENDING",  edit: "D", open: true  },
        "Z": { text: "Pending W8 Validation",      state: "Warning", reg: "PENDING",  edit: "D", open: true  }
    };

    /**
     * REQST is zero-padded to ten characters on some tables and not on others,
     * so the two sides of the join are compared on the number itself. Stripping
     * the padding also collapses the backend's "no request" sentinel
     * "0000000000" (inbox.java:85) to "", which is what the callers test for.
     */
    function normaliseReqId(vId) {
        return String(vId === null || vId === undefined ? "" : vId)
            .trim().replace(/^0+/, "");
    }

    /**
     * Every id an IT_EMSG row could be keyed on, normalised, blanks dropped.
     *
     * inbox.java:85-91 has REQST as the request number, with "0000000000"
     * meaning "no request, this belongs to an invite" and the key falling back
     * to ZZSF_VRA_EMLID + "01". The live inviteInfo payload does it the other
     * way round: the message rows carry REQST "0000610084" — the *invite* id —
     * with ZZSF_VRA_EMLID "0000000000", while the request behind that invite is
     * 0000111601. Rather than pick a side, both ends of the join publish every
     * id they hold and a message sticks on any overlap; mapInvites warns when a
     * row matches nothing, which is what will tell us the settled convention.
     */
    function messageKeys() {
        var mSeen = {};
        var aKeys = [];
        Array.prototype.forEach.call(arguments, function (vId) {
            var sKey = normaliseReqId(vId);
            if (!sKey || mSeen[sKey]) { return; }
            mSeen[sKey] = true;
            aKeys.push(sKey);
        });
        return aKeys;
    }

    /**
     * The notes on one row as a single block of text, so they read as one
     * message rather than a stack of separate ones. A lone note is left as it
     * is; two or more are bulleted, the same way Registration.controller lists
     * the fields still to fill in. The newlines need help to survive rendering
     * — see renderWhitespace on the popover Text and .veraMessageStrip in
     * css/vera.css.
     *
     * @param   {object[]} aMessages mapInviteMessages output for one row
     * @returns {string} "" when there are none
     */
    function joinMessages(aMessages) {
        if (aMessages.length < 2) {
            return aMessages.length ? aMessages[0].text : "";
        }
        return aMessages.map(function (oMsg) {
            return "• " + oMsg.text;
        }).join("\n");
    }

    /**
     * INVSTAT 0-9 are invite-lifecycle codes: the inbox row is still tracking
     * the invitation itself, so there is no registration request behind the id
     * yet. Every other code is a request-lifecycle code — see REQUEST_STATUS.
     */
    function isInviteLifecycle(sCode) {
        return /^[0-9]$/.test(String(sCode || "").trim());
    }

    /**
     * What a request was for, stamped onto its jqXHR so a failure can name it.
     * The full URL is the point of it: the backend segment carries the app
     * version, so seeing the path that 404'd is what identifies a browser
     * running a stale build.
     */
    function describeRequest(sMethod, sService, oParams, oBody) {
        var sQuery = oParams ? jQuery.param(oParams) : "";
        return {
            method:  sMethod,
            service: sService,
            action:  (oParams && oParams.Action) || (oBody && oBody.Action) ||
                     (oParams && oParams.type) || "",
            url:     BASE + sService + (sQuery ? "?" + sQuery : "")
        };
    }

    var CONTROL_ESCAPES = {
        "\b": "\\b", "\f": "\\f", "\n": "\\n", "\r": "\\r", "\t": "\\t"
    };

    /**
     * Escape raw control characters that appear *inside* JSON string literals.
     *
     * The portal writes free text — rejection reasons, invite comments — into
     * its JSON without escaping, so an approver who presses Enter mid-sentence
     * puts a bare CR/LF in the payload. JSON.parse rejects that, and jQuery
     * reports the whole 200 response as a parsererror. Control characters
     * outside strings are legal whitespace and are left alone, so structure
     * (including pretty-printed responses) is untouched.
     */
    function escapeControlChars(sText) {
        var bInString = false, bEscaped = false, aOut = [], i, sChar, iCode;
        for (i = 0; i < sText.length; i++) {
            sChar = sText.charAt(i);
            if (bEscaped) {
                bEscaped = false;
            } else if (bInString && sChar === "\\") {
                bEscaped = true;
            } else if (sChar === "\"") {
                bInString = !bInString;
            } else if (bInString) {
                iCode = sText.charCodeAt(i);
                if (iCode < 0x20) {
                    aOut.push(CONTROL_ESCAPES[sChar] ||
                        "\\u" + ("000" + iCode.toString(16)).slice(-4));
                    continue;
                }
            }
            aOut.push(sChar);
        }
        return aOut.join("");
    }

    // Handed to jQuery in place of its own "text json" converter. The happy
    // path is still a plain JSON.parse; only a payload that would otherwise
    // have failed pays for the repair pass.
    var JSON_CONVERTERS = {
        "text json": function (sText) {
            try {
                return JSON.parse(sText);
            } catch (oErr) {
                var oData = JSON.parse(escapeControlChars(sText));
                Log.warning("VeRAService: repaired unescaped control " +
                    "character(s) in JSON response (" + oErr.message + ")");
                return oData;
            }
        }
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
                converters: JSON_CONVERTERS,
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
         * A submitted request's full detail — a 1:1 dump of the
         * Z_SFI_I510_VRA_VENDISP export tables (CT_LFA1, CT_LFBK, CT_KNVK,
         * CT_REQ, CT_FILES, …) plus a CS_RETURN structure.
         *
         * @param {string} sAdminSSO  ADMIN_SSO, off the invite's vadminData row
         * @param {string} sRequestId REQST, zero-padded as the backend sent it
         */
        getRequestDetail: function (sAdminSSO, sRequestId) {
            return this._get("wz_services", {
                Action:    "displayRequest",
                AdminSSO:  sAdminSSO  || "",
                ReqId: sRequestId || ""
            });
        },

        /**
         * Did a displayRequest response succeed? It reports failure through
         * CS_RETURN (TYPE "E" with a message — see maintain_vendor.java:261)
         * rather than the code/message envelope the other wz_services actions
         * use, so this understands both.
         *
         * @returns {object} { ok, message }
         */
        readDetailReturn: function (oData) {
            if (!oData) {
                return { ok: false, message: "" };
            }

            // A CS_RETURN of type E with no message is not a failure — the
            // legacy check required both.
            var oReturn  = oData.CS_RETURN || {};
            var sMessage = String(oReturn.MESSAGE || "").trim();
            if (String(oReturn.TYPE || "").toUpperCase() === "E" && sMessage) {
                return { ok: false, message: sMessage };
            }

            // The servlet may still wrap the RFC dump in the usual envelope.
            if (oData.code !== undefined && oData.code !== null && oData.code !== "0") {
                return { ok: false, message: oData.message || "" };
            }

            return { ok: true, message: sMessage };
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
                    reg: "PENDING", actions: "", edit: "", open: true
                };
            }
            return jQuery.extend({ code: sKey }, oEntry);
        },

        /**
         * INVSTAT code on an inviteREQData row → { code, text, state, reg,
         * edit, open }. Unknown codes fall through showing the raw code and are
         * treated as display-only, so a new backend status can never silently
         * open a request for editing.
         */
        mapRequestStatus: function (sCode) {
            var sKey   = String(sCode || "").trim().toUpperCase();

            // An inbox row can still be carrying the invitation's own status
            // (0-9) when no request has been raised behind the id yet. That is
            // expected, not an unmapped code, so it reads from INVITE_STATUS.
            if (isInviteLifecycle(sKey)) {
                return this.mapInviteStatus(sKey);
            }

            var oEntry = REQUEST_STATUS[sKey];
            if (!oEntry) {
                Log.warning("VeRAService: unmapped inviteREQData INVSTAT '" + sCode + "'");
                return {
                    code: sKey, text: sKey, state: "None",
                    reg: "PENDING", edit: "D", open: true
                };
            }
            return jQuery.extend({ code: sKey }, oEntry);
        },

        /**
         * inviteREQData (IT_INVITE_REQ) rows → the request records an invite is
         * matched against. These rows carry no ZZSF_VRA_EMLID, so REQST is the
         * only key back to an invite — see resolveInviteTarget.
         *
         * @param   {object[]} aRows raw inviteREQData records
         * @returns {object[]} mapped request rows
         */
        mapRequests: function (aRows) {
            return (aRows || []).map(function (oRow) {
                return {
                    reqId:    oRow.REQST     || "",
                    source:   oRow.SOURCE    || "",
                    name:     oRow.VEND_NAME || "",
                    vendorId: oRow.LIFNR     || "",
                    status:   this.mapRequestStatus(oRow.INVSTAT),
                    date:     this._formatChanged(oRow.CHANGE_DATE, oRow.CHANGE_TIME),
                    request:  oRow
                };
            }, this);
        },

        /**
         * Which form an invite row opens, and the status to show for it.
         *
         * The request id on the invite (REQST, joined in from vadminData) is
         * matched against the inbox rows:
         *   no match                → new request, empty form  ("register")
         *   match, invite code 0-9  → no request raised yet, empty form too
         *   match, request status   → existing request; "edit" for a rejection,
         *                             "display" for everything else
         *
         * The 0-9 carve-out matters because a matching REQST is not on its own
         * proof of a request: the sample response has REQST 0000111601 on both
         * sides with INVSTAT "5" (Pending Vendor Action), meaning the invite
         * has been sent and the vendor has filled in nothing. Matching on REQST
         * alone would open that display-only against an empty form.
         *
         * @param   {object}   oRow      a mapped invite row, carrying reqId
         * @param   {object[]} aRequests mapRequests output
         * @returns {object} { mode, request, status } to merge onto the row
         */
        resolveInviteTarget: function (oRow, aRequests) {
            var sWanted = normaliseReqId(oRow && oRow.reqId);

            var oMatch = sWanted && (aRequests || []).filter(function (oReq) {
                return normaliseReqId(oReq.reqId) === sWanted;
            })[0];

            if (!oMatch || isInviteLifecycle(oMatch.status.code)) {
                return {
                    mode:    "register",
                    request: null,
                    status:  oRow.inviteStatus
                };
            }

            return {
                mode:    oMatch.status.edit === "E" ? "edit" : "display",
                request: oMatch,
                status:  oMatch.status
            };
        },

        /**
         * inviteIT_EMSG (IT_EMSG) rows → the backend notes shown against an
         * inbox row: why a request was rejected, what needs re-submitting.
         *
         * Severity is not read. inbox.java takes only REQST, ZZSF_VRA_EMLID and
         * MESSAGE off the table, vra_inbox.java:293 renders every one of them in
         * the same red panel, and the live payload returns MSGTY "" — so MSGTY
         * is carried through unmapped and the UI paints them all as errors. The
         * day the backend starts filling it in, this is where it gets mapped.
         *
         * @param   {object} oData the whole inviteInfo response
         * @returns {object[]} mapped message rows, in SEQNO order
         */
        mapInviteMessages: function (oData) {
            // The servlet publishes the RFC's table under a prefixed name;
            // accept the bare one too so a rename does not silently drop them.
            var aRows = (oData && (oData.inviteIT_EMSG || oData.IT_EMSG)) || [];

            return aRows.filter(function (oRow) {
                return oRow && String(oRow.MESSAGE || "").trim();
            }).map(function (oRow) {
                return {
                    // ABAP long text arrives pre-wrapped at its own line width,
                    // so the reason an approver typed as one sentence comes
                    // back with hard breaks in the middle of it. Those used to
                    // be invisible (they broke the parse outright — see
                    // escapeControlChars); now that the text survives, collapse
                    // the wrapping so it re-flows to whatever width it is shown
                    // at instead of snapping mid-phrase.
                    text:  String(oRow.MESSAGE).replace(/\s+/g, " ").trim(),
                    // Zero-padded in the payload, so compared as a number.
                    seq:   parseInt(oRow.SEQNO, 10) || 0,
                    msgty: oRow.MSGTY || "",
                    keys:  messageKeys(oRow.REQST, oRow.ZZSF_VRA_EMLID)
                };
            }).sort(function (oLeft, oRight) {
                return oLeft.seq - oRight.seq;
            });
        },

        /**
         * A whole inviteInfo response → the rows the Home and Status tables
         * bind to.
         *
         * The request number is not on the invite row: REQST lives on the
         * vadminData records, which carry ZZSF_VRA_EMLID as the join key. An
         * invite with no vadminData match simply has no request yet, and gets
         * an empty reqId rather than a missing property.
         *
         * Each row then picks up `mode`, `request` and the effective `status`
         * from resolveInviteTarget — `status` is the request's once one exists,
         * and the invite's own status stays on `inviteStatus` as FYI — plus any
         * `messages` the backend attached to it.
         *
         * @param   {object} oData the { inviteData, vadminData, inviteREQData, inviteIT_EMSG } response
         * @returns {object[]} mapped rows, in the order the backend returned them
         */
        mapInvites: function (oData) {
            var aInvites  = (oData && oData.inviteData) || [];
            var aVAdmin   = (oData && oData.vadminData) || [];
            var aRequests = this.mapRequests(oData && oData.inviteREQData);
            var aMessages = this.mapInviteMessages(oData);

            // vadminData is versioned (VERSN), so an invite can appear more
            // than once. Which version carries the live REQST is not settled,
            // so this keeps the long-standing last-wins behaviour but says so
            // out loud when the versions actually disagree.
            //
            // ADMIN_SSO rides along on the same row: it is the only place the
            // SSO that displayRequest needs is published to this client.
            var mRequestByInvite = {};
            aVAdmin.forEach(function (oRow) {
                if (!oRow || !oRow.ZZSF_VRA_EMLID || !oRow.REQST) { return; }
                var oPrev = mRequestByInvite[oRow.ZZSF_VRA_EMLID];
                if (oPrev && normaliseReqId(oPrev.reqId) !== normaliseReqId(oRow.REQST)) {
                    Log.warning("VeRAService: vadminData has conflicting REQST for invite " +
                        oRow.ZZSF_VRA_EMLID + " — '" + oPrev.reqId + "' then '" + oRow.REQST +
                        "'; using the later row.");
                }
                mRequestByInvite[oRow.ZZSF_VRA_EMLID] = {
                    reqId: oRow.REQST,
                    sso:   oRow.ADMIN_SSO || ""
                };
            });

            // Which messages were claimed, so the ones that matched no invite
            // can be named below rather than disappearing quietly.
            var mClaimed = {};

            var aRows = aInvites.map(function (oInv) {
                var oAdmin  = mRequestByInvite[oInv.ZZSF_VRA_EMLID] || {};
                var oMapped = this.mapInviteRow(oInv, oAdmin.reqId, oAdmin.sso);
                jQuery.extend(oMapped, this.resolveInviteTarget(oMapped, aRequests));

                // Both ids the row answers to, plus the EMLID + "01" form
                // inbox.java:88 builds for a message with no request behind it.
                var aRowKeys = messageKeys(oMapped.reqId, oMapped.id, oMapped.id + "01");

                oMapped.messages = aMessages.filter(function (oMsg, iMsg) {
                    var bMine = oMsg.keys.some(function (sKey) {
                        return aRowKeys.indexOf(sKey) !== -1;
                    });
                    if (bMine) {
                        if (mClaimed[iMsg]) {
                            Log.warning("VeRAService: message '" + oMsg.text +
                                "' matches more than one invite — also on " +
                                oMapped.id + "; the join keys are ambiguous.");
                        }
                        mClaimed[iMsg] = true;
                    }
                    return bMine;
                });
                oMapped.messageCount = oMapped.messages.length;
                oMapped.messagesText = joinMessages(oMapped.messages);

                return oMapped;
            }, this);

            aMessages.forEach(function (oMsg, iMsg) {
                if (mClaimed[iMsg]) { return; }
                Log.warning("VeRAService: message '" + oMsg.text + "' matches no " +
                    "invite — its keys are [" + oMsg.keys.join(", ") + "] and no " +
                    "row answers to any of them; it will not be shown.");
            });

            return aRows;
        },

        /**
         * One inviteData row → a table row. Carries `inviteStatus` only; the
         * effective `status` is added by mapInvites via resolveInviteTarget,
         * because it depends on whether a request exists behind the reqId.
         * `messages` and `messageCount` are added there too, for the same
         * reason: the join needs the reqId this row has just been given.
         *
         * @param {object} oInv       raw inviteData record
         * @param {string} sRequestId REQST for this invite, "" if none yet
         * @param {string} sAdminSso  ADMIN_SSO from the same vadminData row
         */
        mapInviteRow: function (oInv, sRequestId, sAdminSso) {
            var sSso = sAdminSso || oInv.ADMIN_SSO || "";

            // Only worth saying when there is a request to go and fetch:
            // without an SSO, displayRequest cannot be called for this row.
            if (sRequestId && !sSso) {
                Log.warning("VeRAService: invite " + oInv.ZZSF_VRA_EMLID +
                    " has request " + sRequestId + " but no ADMIN_SSO — its " +
                    "detail cannot be fetched.");
            }

            return {
                // The invitation's own id. Distinct from reqId below.
                id:      oInv.ZZSF_VRA_EMLID || "",
                // REQST, joined in from vadminData — the request number the
                // backend keys attachments and saves off.
                reqId:   sRequestId          || "",
                // ADMIN_SSO from vadminData — displayRequest's AdminSSO param.
                adminSso: sSso,
                name:    oInv.VEND_NAME      || "",
                type:    oInv.VEND_DESC      || "",
                contact: [oInv.FIRST_NAME, oInv.LAST_NAME].filter(Boolean).join(" "),
                // FYI only once a request exists — see resolveInviteTarget.
                inviteStatus: this.mapInviteStatus(oInv.ZZSF_VRA_INVSTAT),
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

        // ── Request detail (wz_services?Action=displayRequest) ──────────
        //
        // The response is a raw dump of the Z_SFI_I510_VRA_VENDISP export
        // tables, so these mappers are the SAP-shape → reg-model boundary.
        // Every field mapping below is taken from the portal page that read
        // the same RFC, UI/maintain_vendor.java; line references are kept so
        // the two can be diffed. All pure and all tolerant of empty tables —
        // a live response can and does come back with most of them empty.

        /**
         * A whole displayRequest response → data for the reg model.
         *
         * The RFC wins wherever it has a value and the invite seed fills the
         * gaps, mirroring maintain_vendor.java:748, where CT_REQ's vendor type
         * is only taken when the caller did not already know one.
         *
         * @param   {object} oResponse the { CS_RETURN, CT_* } response
         * @param   {object} oSeed     reg-model data already built from the invite
         * @returns {object} data to hand to JSONModel.setData
         */
        mapRequestDetail: function (oResponse, oSeed) {
            var oData     = jQuery.extend({}, oSeed || {});
            var oRes      = oResponse || {};
            var aLfa1     = oRes.CT_LFA1 || [];
            var oPrimary  = aLfa1[0]     || {};
            var oReq      = (oRes.CT_REQ || [])[0] || {};
            var aLfb1     = oRes.CT_LFB1 || [];
            var oFiles    = this.mapFiles(oRes.CT_FILES);

            // The request number the rest of the app keys attachments and
            // saves off. CT_REQ is the authority; anything else is a bug
            // worth seeing rather than silently following.
            var sRequestId = oReq.REQST || oData.requestId || "";
            if (oReq.REQST && oData.requestId &&
                    normaliseReqId(oReq.REQST) !== normaliseReqId(oData.requestId)) {
                Log.warning("VeRAService: displayRequest for " + oData.requestId +
                    " returned REQST " + oReq.REQST + "; using the returned one.");
            }
            oData.requestId = sRequestId;

            var oNames   = this.mapVendorNames(oPrimary);
            var sPoEmail = oPrimary.SMTP_ADDR || oReq.SMTP_ADDR || "";

            oData.vendorType   = oReq.VEND_TYPE         || oData.vendorType   || "";
            oData.userType     = oReq.ZZSF_VRA_VENDCAT  || oData.userType     || "";
            oData.requestType  = oReq.REQTY             || oData.requestType  || "1";
            oData.approverSSO  = oReq.APPROVER_SSO      || oData.approverSSO  || "";
            oData.comments     = oReq.COMMENT1          || oData.comments     || "";
            oData.annualSpend  = oReq.ANNUAL_SPEND      || oData.annualSpend  || "";
            oData.requestedFor = oReq.REQUESTED_FOR     || oData.requestedFor || "";
            oData.vendorId     = oData.vendorId || oPrimary.LIFNR || null;
            oData.companyCodes = this.mapCompanyCodes(aLfb1);

            // STATS uses the same letters as an inviteREQData INVSTAT, so the
            // existing table covers it rather than a second one.
            if (oReq.STATS) {
                oData.status = this.mapRequestStatus(oReq.STATS).reg;
            }

            oData.basic = jQuery.extend({}, oData.basic, {
                legalName:      oNames.legalName     || oData.basic.legalName || "",
                invoicingName:  oNames.invoicingName || "",
                poEmail:        sPoEmail,
                // maintain_vendor.java:1286 infers "Accept P.O.?" from the PO
                // email alone; the flag is only read in registration.java:197.
                acceptPO:       !!sPoEmail || oReq.ZZSF_VRA_PORECV === "X",
                primaryAddress: this.mapDetailAddress(oPrimary),
                secondaryAddresses: this.mapSecondaryAddresses(aLfa1)
            });

            oData.tax      = this.mapDetailTax(oPrimary, oReq, oFiles, sRequestId);
            oData.contacts = { items: this.mapDetailContacts(oRes.CT_KNVK) };

            oData.paymentTerms = {
                availableTerms: this.mapPaymentTermsCatalog(oRes.CT_ZTERMS),
                // Only row 0's ZTERM is the selection (maintain_vendor.java:730).
                selected: (aLfb1[0] && aLfb1[0].ZTERM) || oData.paymentTerms.selected || ""
            };

            oData.banking = jQuery.extend(
                this.mapBankAccounts(oRes.CT_LFBK, oRes.CT_BNKA, oRes.CT_IBAN,
                    oFiles, sRequestId),
                { paymentNotifications: this.mapNotificationEmails(oRes.CT_ADR6) }
            );

            // Kept for support: buildSavePayload builds a whitelist, so this
            // can never leak back into a POST.
            oData._detail = oRes;

            return oData;
        },

        /**
         * NAME1/NAME2/NAME3 → the two names the form shows.
         * maintain_vendor.java:722-727 — with no invoicing name the legal name
         * is the split-at-35 pair NAME1+NAME3; with one, NAME2 holds the legal
         * name and the pair becomes the invoicing name.
         */
        mapVendorNames: function (oRow) {
            var o     = oRow || {};
            var sPair = [o.NAME1, o.NAME3].filter(Boolean).join(" ").trim();

            if (!o.NAME2) {
                return { legalName: sPair, invoicingName: "" };
            }
            return { legalName: o.NAME2, invoicingName: sPair };
        },

        // One CT_LFA1 row → the address shape both primary and secondary use.
        mapDetailAddress: function (oRow) {
            var o = oRow || {};
            return {
                country:         o.LAND1      || "",
                address1:        o.STRAS      || "",
                address2:        o.STR_SUPPL1 || "",
                address3:        o.STR_SUPPL2 || "",
                city:            o.ORT01      || "",
                state:           o.REGIO      || "",
                zip:             o.PSTLZ      || "",
                taxJurisdiction: o.TXJCD      || ""
            };
        },

        /**
         * CT_LFA1 rows 1..n — row 0 is the primary address
         * (maintain_vendor.java:1898). KTOKK is the address role: B001 is a
         * purchasing address, R001 or blank a remit one (:2062-2073).
         */
        mapSecondaryAddresses: function (aLfa1) {
            return (aLfa1 || []).slice(1).map(function (oRow) {
                return jQuery.extend(this.mapDetailAddress(oRow), {
                    type: oRow.KTOKK === "B001" ? "BILLING" : "MAILING"
                });
            }, this);
        },

        /**
         * Tax fields, which come from three places: the ID numbers off
         * CT_LFA1, the classification codes off CT_REQ, and the W-9/590
         * documents off CT_FILES.
         */
        mapDetailTax: function (oLfa1Row, oReqRow, oFiles, sRequestId) {
            var oLfa1 = oLfa1Row || {};
            var oReq  = oReqRow  || {};
            var o     = oFiles   || {};

            // STCD1 is the SSN, STCD2 the tax ID — confirmed both ways by the
            // save path, objectactions.java:1045-1046. The category is decided
            // by whichever one is populated (maintain_vendor.java:2924, :3111).
            var sTaxId = oLfa1.STCD2 || "";
            var sSsn   = oLfa1.STCD1 || "";

            return {
                entityType:   "Entity",
                isUSPerson:   true,
                taxCategory:  (!sTaxId && sSsn) ? "SSN" : "TaxID",
                taxIdNumber:  sTaxId,
                ssnNumber:    sSsn,

                recipientType:    oReq.ZZSF_VRA_QSREC    || "",
                exemptPayeeCode:  oReq.ZZSF_VRA_EXMPTPC  || "",
                factaCode:        oReq.ZZSF_VRA_EXMPTFRC || "",
                independentContractor: "",

                w9FileName: o.W9.name,
                w9DocId:    o.W9.id,
                w9Url:      this._docUrl(o.W9, sRequestId),
                doc590Name: o["590"].name,
                doc590Id:   o["590"].id,
                doc590Url:  this._docUrl(o["590"], sRequestId),

                // Captured because CT_FILES carries them, but with no UI: the
                // form has no W-8 / legal / support document section yet.
                w8DocId:          o.W8.id,
                w8FileName:       o.W8.name,
                legalDocId:       o.LEG.id,
                legalFileName:    o.LEG.name,
                supportDocId:     o.SUP.id,
                supportFileName:  o.SUP.name
            };
        },

        /**
         * CT_FILES → one slot per document type, plus the per-secondary-account
         * bank forms, which are filed under the account's BVTYP rather than a
         * fixed type (objectactions.java:1990).
         *
         * Types are W9 / W8 / ACH / 590 / LEG / SUP — maintain_vendor.java:637.
         */
        mapFiles: function (aFiles) {
            var oOut = {
                W9: { name: "", id: "" }, W8:  { name: "", id: "" },
                ACH:{ name: "", id: "" }, LEG: { name: "", id: "" },
                SUP:{ name: "", id: "" }, "590": { name: "", id: "" },
                byBvtyp: {}
            };

            (aFiles || []).forEach(function (oRow) {
                if (!oRow) { return; }
                var sType = String(oRow.FILE_TYPE || "").toUpperCase();
                var oSlot = {
                    name: oRow.ACT_FILE_NAME || "",
                    id:   oRow.OBJECT_ID     || ""
                };
                if (oOut.hasOwnProperty(sType) && sType !== "byBvtyp") {
                    oOut[sType] = oSlot;
                } else if (sType) {
                    oOut.byBvtyp[sType] = oSlot;
                }
            });

            return oOut;
        },

        // A displaycsdoc link for an already-stored document, or "" when there
        // is nothing to link to. Built here rather than in a formatter so the
        // views do not need the service just to reach BASE.
        _docUrl: function (oSlot, sRequestId) {
            if (!oSlot || !oSlot.id || !oSlot.name) { return ""; }
            return this.getFileUrl(oSlot.name, "Z_REQUEST", sRequestId || "", oSlot.id);
        },

        /**
         * The payment method is not stored — the portal reconstructs it from
         * the bank record every time (maintain_vendor.java:4142-4182 and
         * :4244-4252). Check writes no CT_LFBK row at all, and BVTYP encodes
         * the rest: 01 = US ACH, U01 = US Wire, W01 = foreign Wire.
         */
        derivePaymentMethod: function (oLfbkRow) {
            if (!oLfbkRow || !oLfbkRow.BANKS) { return paymentMethods.CHECK; }
            var sBvtyp = String(oLfbkRow.BVTYP || "");
            if (sBvtyp.indexOf("W") !== -1 || sBvtyp.indexOf("U") !== -1) {
                return paymentMethods.WIRE;
            }
            return paymentMethods.ACH;
        },

        /**
         * CT_LFBK (+ the SWIFT and IBAN side tables) → the banking branch.
         *
         * The primary account is the row whose BVTYP contains "01"
         * (maintain_vendor.java:289); every other row is a secondary account.
         * No primary row means the vendor is paid by check.
         */
        mapBankAccounts: function (aLfbk, aBnka, aIban, oFiles, sRequestId) {
            var aRows    = (aLfbk || []).filter(Boolean);
            var that     = this;
            var oFileMap = (oFiles && oFiles.byBvtyp) || {};

            var iPrimary = -1;
            aRows.forEach(function (oRow, i) {
                if (iPrimary === -1 && String(oRow.BVTYP || "").indexOf("01") !== -1) {
                    iPrimary = i;
                }
            });

            function mapAccount(oRow) {
                var sBvtyp = oRow.BVTYP || "";
                var oDoc   = oFileMap[sBvtyp.toUpperCase()] || { name: "", id: "" };
                return {
                    method:       that.derivePaymentMethod(oRow),
                    country:      oRow.BANKS || "",
                    bvtyp:        sBvtyp,
                    routingNum:   oRow.BANKL || "",
                    accountNum:   oRow.BANKN || "",
                    holderName:   oRow.KOINH || "",
                    swiftNum:     that._findSwift(aBnka, oRow),
                    ibanNum:      that._findIban(aIban, oRow),
                    bankFileName: oDoc.name,
                    bankDocId:    oDoc.id,
                    bankFileUrl:  that._docUrl(oDoc, sRequestId)
                };
            }

            // No bank record at all — check payment. Keep the model's own
            // defaults for the empty fields rather than inventing values.
            var oPrimaryAccount = iPrimary === -1
                ? {
                    method: paymentMethods.CHECK, country: "", bvtyp: "",
                    routingNum: "", accountNum: "", holderName: "",
                    swiftNum: "", ibanNum: "",
                    bankFileName: (oFiles && oFiles.ACH.name) || "",
                    bankDocId:    (oFiles && oFiles.ACH.id)   || "",
                    bankFileUrl:  this._docUrl(oFiles && oFiles.ACH, sRequestId)
                }
                : jQuery.extend(mapAccount(aRows[iPrimary]), {
                    // The primary account's own form is filed under ACH.
                    bankFileName: (oFiles && oFiles.ACH.name) || "",
                    bankDocId:    (oFiles && oFiles.ACH.id)   || "",
                    bankFileUrl:  this._docUrl(oFiles && oFiles.ACH, sRequestId)
                });

            return {
                primaryAccount: oPrimaryAccount,
                secondaryAccounts: aRows.filter(function (oRow, i) {
                    return i !== iPrimary;
                }).map(mapAccount)
            };
        },

        // CT_BNKA carries the SWIFT code for a bank key. The portal also
        // compared an account number here, against a field the save path never
        // writes (maintain_vendor.java:359 reads BANKA, objectactions.java:1900
        // writes BNKLZ), which makes that half of the join dead — so this
        // matches on the bank key alone.
        _findSwift: function (aBnka, oLfbkRow) {
            var oHit = (aBnka || []).filter(function (oRow) {
                return oRow && oRow.BANKL && oRow.BANKL === oLfbkRow.BANKL;
            })[0];
            return (oHit && oHit.SWIFT) || "";
        },

        // CT_IBAN is keyed on bank key + account number, and that join does
        // match what the save writes (objectactions.java:1909).
        _findIban: function (aIban, oLfbkRow) {
            var oHit = (aIban || []).filter(function (oRow) {
                return oRow && oRow.BANKL === oLfbkRow.BANKL &&
                    oRow.BANKN === oLfbkRow.BANKN;
            })[0];
            return (oHit && oHit.IBAN) || "";
        },

        // CT_ADR6 → the Banking step's payment-notification list. Positional
        // rows carrying nothing but an address (maintain_vendor.java:4956).
        mapNotificationEmails: function (aAdr6) {
            return (aAdr6 || []).filter(function (oRow) {
                return oRow && oRow.SMTP_ADDR;
            }).map(function (oRow) {
                return { email: oRow.SMTP_ADDR };
            });
        },

        // CT_KNVK → the Contacts step. maintain_vendor.java:5083-5087; PARNR
        // and SEQNO are not used, contacts are identified by row order.
        mapDetailContacts: function (aKnvk) {
            return (aKnvk || []).filter(Boolean).map(function (oRow) {
                return {
                    name:       oRow.NAME1       || "",
                    email:      oRow.SMTP_ADDR   || "",
                    phone:      oRow.TELF1       || "",
                    fax:        oRow.FAX_NUMBER  || "",
                    department: oRow.ABTNR       || ""
                };
            });
        },

        // CT_ZTERMS → the terms value help. Rows with no text are skipped, as
        // the portal skipped them (maintain_vendor.java:3912).
        mapPaymentTermsCatalog: function (aZterms) {
            return (aZterms || []).filter(function (oRow) {
                return oRow && oRow.TEXT1;
            }).map(function (oRow) {
                return { Key: oRow.ZTERM || "", Description: oRow.TEXT1 };
            });
        },

        // CT_LFB1 carries one row per company code (maintain_vendor.java:733).
        mapCompanyCodes: function (aLfb1) {
            var aOut = [];
            (aLfb1 || []).forEach(function (oRow) {
                if (oRow && oRow.BUKRS && aOut.indexOf(oRow.BUKRS) === -1) {
                    aOut.push(oRow.BUKRS);
                }
            });
            return aOut;
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
         * Attachments are keyed on the request id alone: managecsdoc reads the
         * "id" form field into objectKey, and the document id it returns is
         * what later travels to Z_SFI_I508_VRA_VENSAVE as an ET_FILES row
         * (OBJECT_ID = this id, REQST = the request id, FILE_TYPE = sFileType).
         * See buildSavePayload for the *FileInfo fields that carry it.
         *
         * Without a request id there is nothing to hang the document off, so
         * the upload is refused rather than filed against a blank key.
         *
         * A response without a document id means the attachment was never
         * filed — ZZFI_I508_VERA_ATTACHMENT_SAVE returned no E_ATTACHMENT_ID,
         * or managecsdoc wrote "Error:…" instead of its JSON. Rejecting here
         * keeps the caller from reporting success on an id it can't send.
         *
         * @param   {File}   oFile     the file to store
         * @param   {string} sFileType W9 | 590 | ACH | W8 | LEG | SUP
         * @param   {object} oRegData  reg model data — supplies requestId
         * @returns {jQuery.Promise} rejects immediately when no request id
         */
        uploadRegistrationFile: function (oFile, sFileType, oRegData) {
            var oData = oRegData || {};

            if (!oData.requestId) {
                Log.error("VeRAService: refusing to upload " + sFileType +
                          " — no request id on the registration.");
                return jQuery.Deferred()
                    .reject(null, "error", "missing request id").promise();
            }

            return this.uploadFile(oFile, oData.requestId, "Z_REQUEST", sFileType)
                .then(function (oResult) {
                    if (!oResult || !oResult.id) {
                        Log.error("VeRAService: managecsdoc returned no document id for " +
                                  sFileType + " — " + JSON.stringify(oResult));
                        return jQuery.Deferred()
                            .reject(null, "error", "no document id").promise();
                    }
                    return oResult;
                });
        },

        uploadFile: function (oFile, sObjectKey, sObjectType, sFileType) {
            var oFormData = new FormData();
            oFormData.append("action",   "upload");
            oFormData.append("id",       sObjectKey  || "");
            oFormData.append("objtype",  sObjectType || "Z_REQUEST");
            oFormData.append("fileType", sFileType   || "");
            // No "filename" field: managecsdoc takes the name off the file part
            // itself, and only when it hasn't already seen a filename form
            // field — sending both makes it re-append the extension ("W9.pdf"
            // stored as "W9.pdf.pdf"). The portal's own uploader sends only
            // id/objtype/action/fileType (vendor.js registerFileUploads).
            oFormData.append("file",     oFile, oFile.name);

            return jQuery.ajax({
                url:  BASE + "managecsdoc",
                type: "POST",
                data: oFormData,
                processData: false,
                contentType: false,
                // managecsdoc writes its JSON through a plain PrintWriter with
                // no content type, so jQuery would otherwise hand back a
                // string and every .id read would come out undefined.
                dataType: "json",
                converters: JSON_CONVERTERS,
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

                    // *FileInfo carry the managecsdoc document ids. Each
                    // non-empty one becomes an ET_FILES row on
                    // Z_SFI_I508_VRA_VENSAVE — OBJECT_ID = the id, REQST = the
                    // request id, FILE_TYPE = W9/590/W8/LEG/SUP. objectactions
                    // skips any whose length is <= 1, so "" means "no file".
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
                var oXhr = jQuery.ajax({
                    url:  BASE + sService,
                    type: "GET",
                    data: oParams,
                    dataType: "json",
                    converters: JSON_CONVERTERS,
                    timeout: 30000,
                    headers: that._csrfHeaders()
                });
                // The jqXHR carries no record of what was asked for, and by the
                // time a caller's fail handler runs that is exactly what it
                // needs to report. See model/serviceError.js, which reads this.
                oXhr.veraRequest = describeRequest("GET", sService, oParams);
                return oXhr.fail(function (jqXHR, sStatus, sError) {
                    Log.error("VeRAService GET failed: " + oXhr.veraRequest.url +
                        " — HTTP " + jqXHR.status + " " + sStatus +
                        (sError ? " (" + sError + ")" : ""));
                });
            });
        },

        _post: function (sService, oData) {
            var that = this;
            Log.debug("VeRAService POST " + sService, JSON.stringify(oData));
            return this._fetchCsrfToken().then(function () {
                var oXhr = jQuery.ajax({
                    url:  BASE + sService,
                    type: "POST",
                    data: oData,
                    dataType: "json",
                    converters: JSON_CONVERTERS,
                    timeout: 30000,
                    headers: that._csrfHeaders()
                });
                // Body params are deliberately not put in the URL here — a POST
                // payload can carry the whole registration.
                oXhr.veraRequest = describeRequest("POST", sService, null, oData);
                return oXhr.fail(function (jqXHR, sStatus, sError) {
                    Log.error("VeRAService POST failed: " + oXhr.veraRequest.url +
                        " — HTTP " + jqXHR.status + " " + sStatus +
                        (sError ? " (" + sError + ")" : ""));
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
