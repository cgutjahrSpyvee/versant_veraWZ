# VeRA Fiori Deployment Configuration Fixes

## Summary
The vera-fiori project has been updated to match the working deployment configuration from project1. The project is now configured for MTA-based deployment to BTP Cloud Foundry with Workzone integration.

## Changes Made

### 1. Created `mta.yaml` (Root Level)
- **Purpose**: Multi-Target Application descriptor for Cloud Foundry deployment
- **Key configurations**:
  - Application ID: `vera-fiori`
  - Service name: `vsnt.vera`
  - Three modules: destination-content, app-content, and the main HTML5 app
  - Three resources: destination service, XSUAA, and HTML5 app repository
  - Build command: `npm run build:cf`
  - Deploy mode: `html5-repo`

### 2. Created `xs-security.json` (Root Level)
- **Purpose**: XSUAA security configuration
- **Configuration**:
  - App name: `vera-fiori`
  - Tenant mode: `dedicated`
  - Empty scopes and role-templates (can be extended as needed)

### 3. Created `ui5-deploy.yaml` (Root Level)
- **Purpose**: UI5 build configuration for Cloud Foundry deployment
- **Key features**:
  - SpecVersion: `4.0` (upgraded from 3.0)
  - Excludes test and localService folders
  - Uses `ui5-task-zipper` to create deployment archive
  - Archive name: `vsnt.vera.zip`
  - Includes `xs-app.json` in the archive

### 4. Copied `xs-app.json` to Root Level
- **Purpose**: Application router configuration for Workzone
- **Updated routes**:
  - Added Workzone content repository route (`<apply-service-segment-path>`)
  - Kept existing VERA_PORTAL destination route for backend API calls
  - Added UI5 resources routes (resources and test-resources)
  - Kept HTML5 app repository runtime service route

### 5. Updated `package.json`
- **Added devDependencies**:
  - `ui5-task-zipper`: ^3.4.x (for creating deployment archives)
  - `rimraf`: ^5.0.5 (for cleaning build artifacts)
  - `mbt`: ^1.2.29 (Multi-Target Application build tool)

- **Updated scripts**:
  - `deploy`: Changed from `fiori verify` to `fiori cfDeploy`
  - Added `build:cf`: UI5 build with preload and cache buster for CF deployment
  - Added `build:mta`: Clean and build MTA archive
  - Added `undeploy`: Undeploy application and clean up services

### 6. Installed Dependencies
- Ran `npm install` to install all new dependencies

## File Structure Comparison

### Before:
```
vera-fiori/
├── package.json (missing dependencies & scripts)
├── ui5.yaml
├── ui5-local.yaml
└── webapp/
    ├── ui5-deploy.yaml (wrong location)
    ├── xs-app.json (wrong location)
    └── ...
```

### After:
```
vera-fiori/
├── mta.yaml ✓ NEW
├── xs-security.json ✓ NEW
├── ui5-deploy.yaml ✓ NEW (moved from webapp/)
├── xs-app.json ✓ NEW (copied from webapp/)
├── package.json ✓ UPDATED
├── ui5.yaml
├── ui5-local.yaml
└── webapp/
    ├── ui5-deploy.yaml (old - can be removed)
    ├── xs-app.json (original - kept for reference)
    └── ...
```

## Deployment Instructions

### Option 1: Using Fiori Tools (Recommended)
```bash
cd /home/user/projects/vera-fiori
npm run deploy
```

### Option 2: Using MBT (Manual)
```bash
cd /home/user/projects/vera-fiori
npm run build:mta
cf deploy mta_archives/vera-fiori_1.0.0.mtar
```

### Option 3: Step-by-step
```bash
# 1. Build the application
npm run build:cf

# 2. Build MTA archive
npm run build:mta

# 3. Deploy to Cloud Foundry
cf login
cf deploy mta_archives/vera-fiori_1.0.0.mtar
```

## Service Requirements

The deployment will create/require these BTP services:
- **HTML5 Application Repository** (app-host plan): `vera-html5-service`
- **Destination Service** (lite plan): `vera-destination-service`
- **XSUAA** (application plan): `vera-xsuaa-service`

## Workzone Integration

After deployment:
1. The app will be registered in the HTML5 Application Repository
2. Access Workzone Content Manager to add the app to your site
3. The three navigation intents from `manifest.json` will appear as separate tiles:
   - `VeRA-register` → Register as Vendor
   - `VeRA-maintain` → Maintain Vendor Record
   - `VeRA-status` → Vendor Registration Status

## Notes

- The `VERA_PORTAL` destination route is preserved for backend API calls
- The Workzone managed approuter will handle authentication and routing
- The old `webapp/ui5-deploy.yaml` and `webapp/xs-app.json` can be removed if desired
- Security vulnerabilities reported by npm audit should be addressed separately

## Differences from BTP_SETUP.md

The `BTP_SETUP.md` file describes a simplified deployment approach without `mta.yaml` or `xs-security.json`. However, since project1 (which deploys successfully) uses the MTA approach, this configuration follows that pattern for consistency and reliability.
