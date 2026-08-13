/**
 * serviceError.js
 *
 * Turns a failed VeRAService call into something a user can act on and a
 * support session can start from.
 *
 * A failed backend call reaches the UI as a jqXHR the app then throws away, so
 * every technical failure — an expired session, a Cloud Connector that is down,
 * a stale build pointing at a route that no longer exists — produced the same
 * "please refresh" popup. They need different things done about them, and only
 * the response tells them apart.
 *
 * Two outputs, because a MessageBox shows its details collapsed:
 *
 *   headline  One sentence naming the cause, appended to the caller's own
 *             message so it is on screen in a screenshot without anyone having
 *             to expand anything.
 *   details   The full dump for the "Show details" section — request, status,
 *             correlation ids, response snippet and the build it happened on.
 *             This is what gets pasted into a ticket.
 *
 * Everything is defensive: this runs on the error path, and a diagnostic that
 * throws would replace a bad popup with no popup at all.
 */
sap.ui.define([
    "sap/base/security/encodeXML",
    "vsnt/vera/model/appInfo"
], function (encodeXML, appInfo) {
    "use strict";

    var MAX_BODY = 400;

    /**
     * What the status code means for this app specifically. The generic HTTP
     * meanings are no use to a user; what matters is which of the four things
     * between the browser and the portal broke.
     */
    function explain(iStatus, sTextStatus) {
        if (sTextStatus === "timeout") {
            return "The portal service did not respond within 30 seconds. It may be " +
                   "under load or the Cloud Connector may be down.";
        }
        if (sTextStatus === "abort") {
            return "The request was cancelled before it finished — usually because " +
                   "the page was reloaded or navigated away from.";
        }
        if (sTextStatus === "parsererror") {
            // dataType "json" against an HTML body. In practice this is the
            // XSUAA login page or a gateway error page coming back with a 200.
            return "The service answered with something that is not JSON, which " +
                   "normally means a login page or a gateway error page was " +
                   "returned instead of data. Your session has most likely expired " +
                   "— reload the page to sign in again.";
        }

        if (iStatus === 0) {
            return "No response was received at all. Check your network connection; " +
                   "if it is fine, the BTP destination or the Cloud Connector to the " +
                   "portal is unreachable.";
        }
        if (iStatus === 401 || iStatus === 403) {
            return "The service refused the request as unauthorised. Your session " +
                   "has expired or your user is not authorised for the portal " +
                   "destination — reload the page to sign in again.";
        }
        if (iStatus === 404) {
            // The app is served from a version-less Work Zone path while the
            // backend routes are versioned, so a browser on a cached copy of an
            // older build asks for a route segment that is no longer deployed.
            return "The backend route was not found. This usually means the browser " +
                   "is running a cached copy of an older build whose service path no " +
                   "longer exists — do a hard reload (Ctrl+Shift+R). Compare the " +
                   "request path below with the build shown underneath it.";
        }
        if (iStatus === 407) {
            return "A proxy demanded authentication before the request reached the " +
                   "portal service.";
        }
        if (iStatus >= 500) {
            return "The portal service reported a server-side error. The detail " +
                   "below is what it sent back.";
        }
        if (iStatus >= 400) {
            return "The service rejected the request as invalid.";
        }
        return "The call failed for an unrecognised reason.";
    }

    /**
     * Correlation ids, for anyone who has to find this request in the approuter
     * or Cloud Connector logs. Which of these is present depends on how far the
     * request got, so all of them are read and the empty ones dropped.
     */
    function readTraceIds(jqXHR) {
        var aNames = ["x-vcap-request-id", "x-correlationid", "x-correlation-id",
                      "x-request-id", "sap-perf-fesrec"];
        var aFound = [];
        aNames.forEach(function (sName) {
            var sValue = "";
            try {
                sValue = jqXHR.getResponseHeader(sName) || "";
            } catch (e) {
                /* headers unreadable (status 0) — nothing to report */
            }
            if (sValue) { aFound.push(sName + "=" + sValue); }
        });
        return aFound.join(", ");
    }

    /**
     * A readable slice of the response body. Collapsed onto one line because
     * the interesting part of an HTML error page is its title and first heading,
     * and both are lost in the indentation otherwise.
     */
    function readBody(jqXHR) {
        var sBody = "";
        try {
            if (jqXHR.responseJSON) {
                sBody = JSON.stringify(jqXHR.responseJSON);
            } else {
                sBody = String(jqXHR.responseText || "");
            }
        } catch (e) {
            return "";
        }
        sBody = sBody.replace(/\s+/g, " ").trim();
        if (!sBody) { return ""; }
        return sBody.length > MAX_BODY ? sBody.slice(0, MAX_BODY) + " …" : sBody;
    }

    function stamp() {
        try {
            return new Date().toISOString();
        } catch (e) {
            return "";
        }
    }

    // Label/value pairs, HTML-escaped and laid out for the FormattedText the
    // MessageBox puts its details into. Only <strong> and <br> are used; both
    // survive that control's sanitiser. Braces are escaped as well — a JSON
    // response body is full of them, and MessageBox escapes its own detail
    // objects the same way to keep them out of UI5's binding parser.
    function render(aRows) {
        return aRows
            .filter(function (aRow) { return aRow[1]; })
            .map(function (aRow) {
                return "<strong>" + encodeXML(aRow[0]) + ":</strong> " +
                       encodeXML(String(aRow[1])).replace(/{/g, "&#x007B;");
            })
            .join("<br>");
    }

    return {

        /**
         * Describes a failed ajax call.
         *
         * @param   {object} jqXHR       the failed jqXHR
         * @param   {string} sTextStatus jQuery's status ("error", "timeout",
         *                               "parsererror", "abort")
         * @param   {string} sErrorText  jQuery's errorThrown
         * @param   {object} [oContext]  extra rows for the dump, e.g. { User: "…" }
         * @returns {object} { headline, details, status, textStatus }
         */
        describe: function (jqXHR, sTextStatus, sErrorText, oContext) {
            var oXhr     = jqXHR || {};
            var iStatus  = Number(oXhr.status) || 0;
            var sStatus  = sTextStatus || "error";
            var oRequest = oXhr.veraRequest || {};
            var oInfo    = appInfo.get();

            var sHttp = iStatus
                ? iStatus + " " + (oXhr.statusText || sErrorText || "")
                : "no response (" + sStatus + ")";

            var aRows = [
                ["Request",  oRequest.method && oRequest.url
                                ? oRequest.method + " " + oRequest.url
                                : ""],
                ["Action",   oRequest.action || ""],
                ["Status",   sHttp.trim() + (iStatus ? " (" + sStatus + ")" : "")],
                ["Response", readBody(oXhr)],
                ["Trace",    readTraceIds(oXhr)]
            ];

            Object.keys(oContext || {}).forEach(function (sKey) {
                aRows.push([sKey, oContext[sKey]]);
            });

            aRows.push(["Build", appInfo.format(oInfo)]);
            aRows.push(["Path",  oInfo.modulePath]);
            aRows.push(["Time",  stamp()]);

            return {
                headline:   explain(iStatus, sStatus),
                details:    render(aRows),
                status:     iStatus,
                textStatus: sStatus
            };
        },

        /**
         * The same dump for a failure that never reached the network — a
         * response that arrived intact but could not be understood.
         *
         * @param   {Error}  oError    the exception thrown while reading it
         * @param   {object} [oData]   the payload that could not be read
         * @param   {object} [oContext] extra rows, as for describe
         * @returns {object} { headline, details }
         */
        describeData: function (oError, oData, oContext) {
            var oInfo   = appInfo.get();
            var sSample = "";
            try {
                sSample = JSON.stringify(oData || {});
            } catch (e) {
                sSample = "(payload not serialisable)";
            }
            if (sSample.length > MAX_BODY) {
                sSample = sSample.slice(0, MAX_BODY) + " …";
            }

            var aRows = [
                ["Cause",   (oError && oError.message) || "unknown"],
                ["Payload", sSample]
            ];

            Object.keys(oContext || {}).forEach(function (sKey) {
                aRows.push([sKey, oContext[sKey]]);
            });

            aRows.push(["Build", appInfo.format(oInfo)]);
            aRows.push(["Time",  stamp()]);

            return {
                headline: "The service answered, but the response was not in the " +
                          "shape this app expects. This is a defect rather than " +
                          "something you can clear by retrying — please report it " +
                          "with the detail below.",
                details:  render(aRows)
            };
        }
    };
});
