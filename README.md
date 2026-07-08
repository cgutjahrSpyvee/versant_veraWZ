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

# Deploy to CF (dev — uses default backend-url from mta.yaml)
cf deploy mta_archives/vera-fiori_1.0.0.mtar

# Undeploy
npm run undeploy
```

## Environment Configuration (QA / Production)

The `VeRA_Backend` destination URL differs per environment. Rather than hardcoding it, `mta.yaml` parameterizes it as `${backend-url}` and each environment overrides the value with an MTA **extension descriptor** (`.mtaext`).

| File | Environment | `backend-url` |
|------|-------------|---------------|
| `mta.yaml` (default) | Dev | dev host (`...bwz-dev-use...us10-001`) |
| `qa.mtaext` | QA | QA host |
| `prod.mtaext` | Production | Prod host (`...bwz-prod-usw...us11`) |

Pass the matching extension with `-e` at deploy time. The extension only overrides `backend-url`; everything else comes from `mta.yaml`.

```bash
cf deploy mta_archives/vera-fiori_1.0.0.mtar                 # Dev (default)
cf deploy mta_archives/vera-fiori_1.0.0.mtar -e qa.mtaext    # QA
cf deploy mta_archives/vera-fiori_1.0.0.mtar -e prod.mtaext  # Production
```

Because the destination service uses `existing_destinations_policy: update`, each deploy overwrites the `VeRA_Backend` destination with the URL for that environment — no manual cockpit edits needed.

**To change a URL:** edit the `backend-url` value in the relevant `.mtaext` (or the default in `mta.yaml` for Dev) and redeploy.

> Note: the subaccount-level `Coding_Portal` destination is managed manually in each subaccount and is **not** part of the `.mtaext` overrides. It is currently referenced as `Coding_Portal` in `approuter/xs-app.json`; because the `.mtaext` mechanism can only override values in `mta.yaml` (not the contents of `xs-app.json`), a Production deploy needs this destination to resolve correctly per subaccount.

## Deployment Mode

Uses `deploy_mode: html5-repo` in `mta.yaml` — the HTML5 app is served by Work Zone's managed approuter, not the standalone approuter. The standalone approuter only proxies `/vera-portal/*` API calls to OnPremise.
