/**
 * appInfo.js
 *
 * Identifies the running build so a user can report exactly what they are
 * looking at.
 *
 * Three pieces, because no single one is enough:
 *
 *   version    sap.app.applicationVersion from the manifest. In a deployed
 *              build this is "<major>.<minor>.<build number>" — lib/tasks/
 *              stampAppVersion.js replaces the patch with a build number on
 *              every CF build, so it is unique per deploy. It is also the tail
 *              of the Work Zone path segment the app is served from, which is
 *              what keeps browsers off a cached copy of the previous deploy.
 *              The patch you see here is therefore NOT the hand-maintained one
 *              in webapp/manifest.json; that lives in git. A local build keeps
 *              the plain semver.
 *   buildToken The Work Zone cachebuster token out of the module path. Changes
 *              on every deploy, so it distinguishes two deployments even if the
 *              version somehow did not move.
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
    //   /<site-id>.<cloud-service>.<app-id>/~<cachebuster>~/...
    // Note there is no version in that segment — confirmed off a live request:
    // the app is *served* from the version-less path, while the backend routes
    // sit under "<same segment>-<version>", which is why VeRAService appends it.
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
         * The component is only needed for the version. Callers that have no
         * handle on it — the error path, which just wants to name the build —
         * may omit it and get whatever the Component's own call already cached.
         *
         * @param   {sap.ui.core.UIComponent} [oComponent] the app component
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

            // A component-less call before init would otherwise cache an empty
            // version for the lifetime of the app.
            if (!oComponent) {
                return {
                    version:    "",
                    buildToken: oPath.token,
                    appSegment: oPath.segment,
                    modulePath: oPath.path,
                    buildTime:  ""
                };
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
                // One epoch-ms entry per file — but the entries are each
                // resource's *source* mtime, not the time of the build. Most
                // files are copied through, so their stamp is whenever someone
                // last edited them: manifest.json used to be read here and
                // reported the date of the last manual manifest edit, which is
                // not what anyone means by "built at".
                //
                // Component-preload.js is generated from scratch on every
                // build, so its mtime is the only entry that is genuinely the
                // build time. Fall back to manifest.json for a build that
                // somehow has no preload.
                var iStamp = oInfo && (oInfo["Component-preload.js"] ||
                                       oInfo["manifest.json"]);
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
