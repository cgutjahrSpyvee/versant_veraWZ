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
/<site-id>.<cloud-service>.<app-id>/~<cachebuster>~/Component-preload.js
```

Two things about that URL are worth internalising, because getting them wrong
cost a full day of misdiagnosis:

- **There is no version in the segment the browser is served from.** An earlier
  version of these docs said there was. `@sap/approuter` splits the first path
  segment on `/-(\d+\.){2}(\d+)$/`; with a `-N.N.N` suffix you pin a specific
  version, without it you get whatever is current. Work Zone serves version-less;
  `webapp/model/VeRAService.js` appends `-<version>` to reach the backend routes.
  Both forms work, which is why the two readings coexisted for so long.
- **`index.html` is never loaded.** Work Zone launches the app as a *Component*
  via `crossNavigation.inbounds`. The version the app reports comes from the
  manifest inlined into `Component-preload.js`.

### What actually busts the cache

Only the `~<cachebuster>~` token. The repo runtime changes it when the app's
content changes, and the approuter stamps tokenized responses with
`Cache-Control: public, max-age=31536000` — see `attachCacheBusterHeaders` in
`@sap/approuter/lib/middleware/request-handler.js`, which fires *only* when a
token is present in the URL. That is correct: a tokenized URL is immutable.

`xs-app.json` therefore keys its cache policy on the **token, not on filenames**:

```json
{ "source": "^(?!.*/~[^~/]+~/)(.*)$", "cacheControl": "no-store, must-revalidate" }
```

Anything served *without* a token resolves to whatever version is current, so it
must never be cached. Anything *under* a token stays cacheable forever. The
previous rule matched `index.html|manifest.json` by name, which guarded a file
Work Zone never loads and left `Component-preload.js` cacheable.

### Why the version is still stamped

`lib/tasks/stampAppVersion.js` (registered in `ui5-deploy.yaml`) rewrites
`sap.app.applicationVersion.version` in the build output to
`<major>.<minor>.<build number>`, where the build number is whole minutes since
2026-01-01 UTC. `webapp/manifest.json` is **not** modified — bump that by hand
when the semantic version genuinely changes.

It has to be three numeric segments, both because the repo rejects anything else
(`validation error: application version 0.7.5-20260813T155127 ... is invalid`)
and because the approuter regex above would not recognise it as a version.

The stamp is for **identification, not invalidation**. It does not defeat any
cache — it means a tester's reported version decodes to an exact build time:

```bash
date -u -d "2026-01-01 UTC + <N> minutes"     # for a reported 0.7.<N>
```

A plain `0.7.x` (small patch) means the build predates the stamping task
entirely. Consequence: **the patch shown in the deployed app is a build number,
not the semantic patch.** The semantic version lives in `webapp/manifest.json`.

### Telling a stale cache apart from a failed deploy

`CTRL-Shift-R` is **not** sufficient and its failure proves nothing. A hard
reload only bypasses the cache for the main document and the subresources the
browser finds while parsing it. UI5 loads `Component-preload.js` through its own
module loader *after* the page is up, so it is served from disk cache regardless.

Use this ladder instead, reading the About dialog after each step:

| Step | Clears | If it fixes it |
|------|--------|----------------|
| Restart the browser | in-memory cache, service workers | in-memory cache |
| DevTools → **Empty Cache and Hard Reload** | the origin's HTTP cache | HTTP cache |
| Fresh incognito window | all client-side state | client storage |
| None of the above | — | server-side: repo content or Work Zone site metadata |

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
