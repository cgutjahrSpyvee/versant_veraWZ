# Versant Vendor Workzone - Vendor Registration Application

SAPUI5 Fiori application for vendor registration and maintenance, deployed to SAP BTP Cloud Foundry and served via SAP Build Work Zone.

## Architecture

The application uses a **dual-approuter** architecture to enable OnPremise connectivity through SAP Cloud Connector while remaining accessible via SAP Build Work Zone (Launchpad):

```
User Browser
    |
SAP Build Work Zone (managed approuter)
    |
    |-- Static content (HTML/JS/CSS) --> HTML5 Apps Repository
    |
    |-- /vera-portal/* API calls --> VeRA_Backend destination
                                        |
                                  Standalone Approuter (vera-fiori-approuter)
                                        |
                                  Cloud Connector (Location ID: VRASCCD)
                                        |
                                  OnPremise NetWeaver Portal (Coding_Portal)
```

- **Work Zone managed approuter** handles user authentication and serves the HTML5 app from the HTML5 Apps Repository
- **Standalone approuter** (`approuter/`) acts as a backend proxy, tunneling API requests through the Connectivity Service and Cloud Connector to the OnPremise system
- The `VeRA_Backend` destination (instance-level, defined in `mta.yaml` init_data) points to the standalone approuter's CF URL

## Key Files

| File | Purpose |
|------|---------|
| `mta.yaml` | MTA deployment descriptor |
| `xs-app.json` | Routing rules for Work Zone managed approuter (bundled in `vsnt.vera.zip`) |
| `approuter/xs-app.json` | Routing rules for standalone approuter (OnPremise proxy) |
| `approuter/package.json` | Standalone approuter dependencies (`@sap/approuter`) |
| `xs-security.json` | XSUAA security configuration |
| `webapp/model/VeRAService.js` | API service layer - all backend calls via `/vera-portal/` |
| `ui5-deploy.yaml` | UI5 build config for CF deployment (archive name: `vsnt.vera`) |
| `lib/tasks/stampAppVersion.js` | Build task that stamps a unique `applicationVersion` per deploy (see "Cache busting") |

## BTP Services

| Service | Instance Name | Purpose |
|---------|--------------|---------|
| Destination | `vera-destination-service` | Route resolution for `VeRA_Backend`, `ui5`, etc. |
| Connectivity | `vera-connectivity-service` | Cloud Connector tunnel to OnPremise |
| XSUAA | `vera-xsuaa-service` | Authentication and authorization |
| HTML5 Apps Repo | `vera-html5-service` | Hosts the built UI5 app (`app-host` plan) |

## BTP Destinations

| Name | Level | Type | Purpose |
|------|-------|------|---------|
| `Coding_Portal` | Subaccount | OnPremise / BasicAuth | NetWeaver Portal backend |
| `SWZ_ContentRepo` | Subaccount | OnPremise | Content repository |
| `VeRA_Backend` | Instance | Internet / NoAuth | Points to standalone approuter CF URL |
| `ui5` | Instance | Internet / NoAuth | SAPUI5 CDN (`https://ui5.sap.com`) |

## Development

```bash
# Local development
npm run start-local

# Build MTA archive
npm run build:mta

# Build + deploy in one step (see "Deploying" below)
npm run deploy:dev

# Undeploy
npm run undeploy
```

## Deploying

Always deploy with the composite scripts. They rebuild the archive first, so it
is not possible to ship an `.mtar` left over from an earlier build — the failure
mode that used to look like "my fix didn't deploy".

```bash
npm run deploy:dev     # Dev  (default backend-url from mta.yaml)
npm run deploy:qa      # QA   (-e qa.mtaext)
npm run deploy:prod    # Prod (-e prod.mtaext)
```

After the deploy finishes: **Build Work Zone → Content Manager → Content
Providers → the HTML5 Apps provider → Update content**, then open the site. Work
Zone caches the app metadata it got from the HTML5 Apps Repository; without the
re-sync it can keep pointing at the previous version for a while.

## Cache busting — making sure testers get the new build

The app is served by Work Zone's managed approuter out of the HTML5 Applications
Repository, at:

```
/<site-id>.<cloud-service>.<app-id>-<version>/~<cachebuster>~/index.html
```

The `~<cachebuster>~` token changes on every deploy and keeps the inner
resources fresh, but the **outer segment only changes when the manifest version
changes**. Redeploying the same version left `index.html` on a URL the browser
had already cached, so testers kept seeing the previous build until they cleared
their cache.

Two things now prevent that:

1. **The version is stamped at build time.** `lib/tasks/stampAppVersion.js` (a
   UI5 custom task registered in `ui5-deploy.yaml`) rewrites
   `sap.app.applicationVersion.version` in the build output to
   `<major>.<minor>.<build number>`, e.g. `0.7.323517`. Every deploy therefore
   gets a distinct URL segment and nothing can be served from cache.
   `webapp/manifest.json` is **not** modified — bump that by hand when the
   semantic version genuinely changes.

   The build number is whole minutes since 2026-01-01 UTC. It has to go in the
   patch position because the HTML5 Application Repository rejects anything that
   is not three numeric segments — a semver pre-release suffix fails validation:

   ```
   validation error: application version 0.7.5-20260813T155127
   in manifest.json file is invalid
   ```

   Consequence: **the patch shown in the deployed app is a build number, not the
   semantic patch.** `major.minor` still carry meaning; the full semantic
   version lives in `webapp/manifest.json` and in git.
2. **The entry point is marked `no-store`.** `xs-app.json` has a route above the
   catch-all that sets `Cache-Control: no-store, must-revalidate` on
   `index.html` and `manifest.json`. The tokenized resources under the catch-all
   stay cacheable, which is the point of the cachebuster.

### Verifying which build a tester is on

The app reports its own identity — no guessing required. On the Home screen the
footer button shows `v<version>`; clicking it opens an About dialog with the
version, cachebuster token, build time, and the app path segment. Ask testers to
read that out before filing a "this didn't deploy" bug. The same line is written
to the browser console at startup (`VeRA v… · build … · …`).

## Environment Configuration (QA / Production)

The `VeRA_Backend` destination URL differs per environment. Rather than hardcoding it, `mta.yaml` parameterizes it as `${backend-url}` and each environment overrides the value with an MTA **extension descriptor** (`.mtaext`).

| File | Environment | `backend-url` |
|------|-------------|---------------|
| `mta.yaml` (default) | Dev | dev host (`...bwz-dev-use...us10-001`) |
| `qa.mtaext` | QA | QA host |
| `prod.mtaext` | Production | Prod host (`...bwz-prod-usw...us11`) |

Pass the matching extension with `-e` at deploy time. The extension only overrides `backend-url`; everything else comes from `mta.yaml`.

```bash
npm run deploy:dev     # Dev (default)
npm run deploy:qa      # QA
npm run deploy:prod    # Production
```

Because the destination service uses `existing_destinations_policy: update`, each deploy overwrites the `VeRA_Backend` destination with the URL for that environment — no manual cockpit edits needed.

**To change a URL:** edit the `backend-url` value in the relevant `.mtaext` (or the default in `mta.yaml` for Dev) and redeploy.

> Note: the subaccount-level `Coding_Portal` destination is managed manually in each subaccount and is **not** part of the `.mtaext` overrides. It is currently referenced as `Coding_Portal` in `approuter/xs-app.json`; because the `.mtaext` mechanism can only override values in `mta.yaml` (not the contents of `xs-app.json`), a Production deploy needs this destination to resolve correctly per subaccount.

## Deployment Mode

Uses `deploy_mode: html5-repo` in `mta.yaml` — the HTML5 app is served by Work Zone's managed approuter, not the standalone approuter. The standalone approuter only proxies `/vera-portal/*` API calls to OnPremise.
