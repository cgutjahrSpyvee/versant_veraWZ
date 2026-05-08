# VeRA - Vendor Registration Application

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
                                  OnPremise NetWeaver Portal (Coding_Portal_QA)
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
| `Coding_Portal_QA` | Subaccount | OnPremise / BasicAuth | NetWeaver Portal backend |
| `SWZ_ContentRepo` | Subaccount | OnPremise | Content repository |
| `VeRA_Backend` | Instance | Internet / NoAuth | Points to standalone approuter CF URL |
| `ui5` | Instance | Internet / NoAuth | SAPUI5 CDN (`https://ui5.sap.com`) |

## Development

```bash
# Local development
npm run start-local

# Build MTA archive
npm run build:mta

# Deploy to CF
cf deploy mta_archives/vera-fiori_1.0.0.mtar

# Undeploy
npm run undeploy
```

## Deployment Mode

Uses `deploy_mode: html5-repo` in `mta.yaml` — the HTML5 app is served by Work Zone's managed approuter, not the standalone approuter. The standalone approuter only proxies `/vera-portal/*` API calls to OnPremise.
