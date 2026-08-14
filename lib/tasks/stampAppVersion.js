/**
 * stampAppVersion.js
 *
 * UI5 custom build task. Rewrites sap.app.applicationVersion.version in the
 * *build output* to "<major>.<minor>.<build number>", e.g. "0.7.323511".
 *
 * The format is forced on us: the HTML5 Application Repository rejects anything
 * that is not three numeric segments —
 *
 *   validation error: application version 0.7.5-20260813T155127 in
 *   manifest.json file is invalid
 *
 * (@sap/html5-app-deployer README: "The version format must be xx.xx.xx,
 * whereas x is a digit.") So no semver pre-release suffix, and the patch
 * position is the only place a per-build value can go. major.minor still come
 * from webapp/manifest.json and keep their meaning; the hand-maintained patch
 * is replaced in the deployed artifact only.
 *
 * Why — and what this does NOT do. The app is served by Work Zone out of the
 * HTML5 Application Repository at
 *
 *   /<site-id>.<cloud-service>.<app-id>/~<cachebuster>~/...
 *
 * Note there is no version in the segment the browser is served from. An
 * earlier version of this comment claimed there was, and used that to argue
 * that stamping the version busts the browser cache. It does not: cache
 * busting is done entirely by the ~token~, which the repo runtime changes when
 * the app's content changes.
 *
 * Both forms of the segment are valid, which is what made this confusing.
 * @sap/approuter parses the first path segment with
 *
 *   appVersionRegex = /-(\d+\.){2}(\d+)$/      (dynamic-routing-utils.js)
 *
 * and strips a trailing "-N.N.N" to split it into app name + app version. With
 * the suffix you pin one specific version; without it you get whatever version
 * is current. Work Zone serves the app version-less (current), while
 * webapp/model/VeRAService.js appends "-<version>" to reach the backend routes.
 *
 * That regex is also the real reason for the three-numeric-segment rule above:
 * a version that does not match it is not recognised as a version at all, and
 * the whole segment is then read as the app name.
 *
 * So what is the stamp still for? Identification, not invalidation — it is how
 * a tester's "v0.7.323977" in the About dialog can be decoded to an exact build
 * time, which is what let us tell a stale browser cache apart from a failed
 * deploy. Keep it for that; do not rely on it to defeat a cache.
 *
 * webapp/manifest.json on disk is never touched — the hand-maintained semver
 * keeps its meaning and the working tree stays clean. Only the built copy is
 * stamped, and only for the deploy build (ui5-deploy.yaml); a local `npm run
 * build` keeps the plain version.
 *
 * Must run before generateComponentPreload: manifest.json is bundled into
 * Component-preload.js, and a stamp applied after that would leave the bundled
 * copy disagreeing with the standalone one.
 *
 * Never fails the build. A stamp is a diagnostic aid, not a reason to block a
 * deploy — if the manifest cannot be read or parsed, log it and leave it alone.
 */
"use strict";

// Whole minutes since this instant are the build number. Chosen over epoch
// milliseconds to keep the digit count low — the repo's validator is
// server-side and undocumented on length, and the only example SAP publishes is
// two digits ("1.0.10"). Minutes since 2026-01-01 is six digits today and stays
// under seven until 2045. Resolution is one minute, which cannot collide in
// practice: a full build:mta + cf deploy takes longer than that.
var BUILD_EPOCH_MS = Date.UTC(2026, 0, 1);

/**
 * @returns {number} whole minutes elapsed since BUILD_EPOCH_MS
 */
function buildNumber() {
    return Math.floor((Date.now() - BUILD_EPOCH_MS) / 60000);
}

module.exports = async function ({ workspace, options, log }) {
    const oLog = log || console;
    const sPath = "/resources/" + options.projectNamespace + "/manifest.json";

    const oResource = await workspace.byPath(sPath);
    if (!oResource) {
        oLog.warn("stamp-app-version: no manifest at " + sPath + " — nothing stamped.");
        return;
    }

    let oManifest;
    const sSource = await oResource.getString();
    try {
        oManifest = JSON.parse(sSource);
    } catch (e) {
        oLog.warn("stamp-app-version: manifest.json is not valid JSON (" + e.message +
                  ") — leaving the version alone.");
        return;
    }

    const oAppVersion = oManifest["sap.app"] && oManifest["sap.app"].applicationVersion;
    if (!oAppVersion || !oAppVersion.version) {
        oLog.warn("stamp-app-version: no sap.app.applicationVersion.version — nothing stamped.");
        return;
    }

    const sSourceVersion = String(oAppVersion.version);
    const aParts = sSourceVersion.split(".");
    if (aParts.length < 2 || !/^\d+$/.test(aParts[0]) || !/^\d+$/.test(aParts[1])) {
        oLog.warn("stamp-app-version: version '" + sSourceVersion + "' is not " +
                  "<major>.<minor>.… — leaving it alone. The HTML5 App Repo " +
                  "requires three numeric segments, so this deploy will fail " +
                  "validation unless the manifest is fixed.");
        return;
    }

    const sStamped = aParts[0] + "." + aParts[1] + "." + buildNumber();

    oAppVersion.version = sStamped;
    // Two-space indent matches the checked-in manifest; keeps a diff of the
    // built file against the source readable.
    await oResource.setString(JSON.stringify(oManifest, null, 2) + "\n");
    await workspace.write(oResource);

    oLog.info("stamp-app-version: applicationVersion " + sSourceVersion + " -> " + sStamped);
};
