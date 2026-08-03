/**
 * appInfo.js
 *
 * Identifies the running build so a user can report exactly what they are
 * looking at.
 *
 * Three pieces, because no single one is enough:
 *
 *   version    sap.app.applicationVersion from the manifest. Semantic, but it
 *              only moves when someone bumps it — and it must be bumped in
 *              lockstep with mta.yaml, since VeRAService derives the backend
 *              path from it.
 *   buildToken The Work Zone cachebuster token out of the module path. Changes
 *              on every deploy, so it is what actually distinguishes two
 *              deployments of the same version.
 *   buildTime  Stamp the UI5 build writes into sap-ui-cachebuster-info.json.
 *              Human-readable, and the thing worth reading out loud.
 *
 * Everything degrades to "" instead of throwing: this is diagnostic data, and
 * it must never be the reason the app fails to start.
 */
sap.ui.define([
    "sap/ui/core/format/DateFormat",
    "sap/base/Log"
], function (DateFormat, Log) {
    "use strict";

    var oCached = null;

    // In Work Zone the module path looks like
    //   /<site-id>.<cloud-service>.<app-id>-<version>/~<cachebuster>~/...
    // Served locally it is just /resources/vsnt/vera, with no token at all.
    function parseModulePath() {
        var sPath  = sap.ui.require.toUrl("vsnt/vera") || "";
        var aToken = /~([^~/]+)~/.exec(sPath);
        return {
            path:    sPath,
            segment: sPath.split("/")[1] || "",
            token:   aToken ? aToken[1] : ""
        };
    }

    return {

        /**
         * Synchronous identity — everything except the build stamp, which needs
         * a fetch. Memoised; safe to call from anywhere.
         *
         * @param   {sap.ui.core.UIComponent} oComponent the app component
         * @returns {object} { version, buildToken, appSegment, modulePath, buildTime }
         */
        get: function (oComponent) {
            if (oCached) { return oCached; }

            var oPath    = parseModulePath();
            var sVersion = "";
            try {
                sVersion = oComponent.getManifestEntry("sap.app")
                                     .applicationVersion.version || "";
            } catch (e) {
                Log.warning("appInfo: no applicationVersion in the manifest.");
            }

            oCached = {
                version:    sVersion,
                buildToken: oPath.token,
                appSegment: oPath.segment,
                modulePath: oPath.path,
                buildTime:  ""      // filled in by loadBuildTime
            };
            return oCached;
        },

        /**
         * Reads the build stamp and caches it onto the object returned by get().
         * The file only exists in a built app, so a local dev run resolves with
         * an empty string rather than failing.
         *
         * @returns {jQuery.Promise} resolves with the formatted stamp, or ""
         */
        loadBuildTime: function () {
            var that      = this;
            var oDeferred = jQuery.Deferred();

            jQuery.ajax({
                url:      sap.ui.require.toUrl("vsnt/vera/sap-ui-cachebuster-info.json"),
                dataType: "json"
            }).done(function (oInfo) {
                // One epoch-ms entry per file; manifest.json is written on every
                // build, so it stands in for "when this bundle was made".
                var iStamp = oInfo && oInfo["manifest.json"];
                var sOut   = "";
                if (iStamp) {
                    sOut = DateFormat.getDateTimeInstance({ style: "medium" })
                                     .format(new Date(iStamp));
                }
                if (oCached) { oCached.buildTime = sOut; }
                oDeferred.resolve(sOut);
            }).fail(function () {
                // Not a built app, or the file was not deployed.
                Log.info("appInfo: no cachebuster info — build time unavailable.");
                oDeferred.resolve("");
            });

            return oDeferred.promise();
        },

        /**
         * One line a user can read out or paste into a ticket.
         * e.g. "v1.0.0 · build 1a2b3c4d · Aug 1, 2026, 2:23:11 PM"
         */
        format: function (oInfo) {
            return [
                oInfo.version ? "v" + oInfo.version : "version unknown",
                oInfo.buildToken ? "build " + oInfo.buildToken : "",
                oInfo.buildTime
            ].filter(Boolean).join(" · ");
        }
    };
});
