# VeRA — Vendor Registration Application
## SAP Fiori on BTP Workzone • Deployed via SAP Business Application Studio

---

## Architecture

```
Browser (BTP Workzone launchpad)
    │
    │  HTTPS — same origin, no CORS
    ▼
Workzone managed Application Router
    │  Rule:  /vera-portal/*  →  destination VERA_PORTAL
    │  Auth:  XSUAA (SSO)
    │  CSRF:  enforced on POST/DELETE
    ▼
SAP BTP Connectivity Service
    │  Encrypted TLS tunnel
    ▼
SAP Cloud Connector (on-premise DMZ)
    │  Principal Propagation — maps BTP user → Portal user
    ▼
On-Premise NetWeaver Portal
    /irj/.../com.nbcu.htmlhelper     →  JCO / RFC  →  ECC
    /irj/.../com.nbcu.objectactions  →  JCO / RFC  →  ECC
    /irj/.../com.nbcu.inbox          →  JCO / RFC  →  ECC
    (all Java services unchanged)
```

**Zero changes to the on-premise `.ear` or any RFCs.** Only the UI layer is new.

---

## Project Structure

```
vera-fiori/
├── README.md                    ← You are here
├── webapp/                      ← UI5 app source
│   ├── manifest.json            ← crossNavigation inbounds for Workzone tiles
│   ├── Component.js             ← FLP shell integration
│   ├── ui5.yaml                 ← BAS run config (uses destination)
│   ├── ui5-deploy.yaml          ← BAS deploy config
│   ├── package.json             ← fiori CLI scripts
│   ├── xs-app.json              ← Approuter routing
│   ├── index.html               ← FLP sandbox for BAS preview
│   ├── index-mock.html          ← Fully offline mock mode
│   ├── model/
│   │   ├── VeRAService.js       ← Portal calls via /vera-portal/
│   │   └── models.js
│   ├── controller/
│   │   ├── App.controller.js
│   │   ├── Home.controller.js
│   │   ├── Registration.controller.js
│   │   ├── Status.controller.js
│   │   └── steps/
│   │       ├── Basic.controller.js
│   │       ├── Tax.controller.js
│   │       ├── PaymentTerms.controller.js
│   │       ├── Banking.controller.js
│   │       └── Contacts.controller.js
│   ├── view/
│   │   ├── App.view.xml
│   │   ├── Home.view.xml
│   │   ├── Registration.view.xml
│   │   ├── Status.view.xml
│   │   └── steps/
│   │       ├── Basic.view.xml
│   │       ├── Tax.view.xml
│   │       ├── PaymentTerms.view.xml
│   │       ├── Banking.view.xml
│   │       └── Contacts.view.xml
│   ├── localService/MockServer.js
│   ├── i18n/i18n.properties
│   └── css/vera.css
└── btp/
    └── BTP_SETUP.md             ← One-time Cockpit/SCC setup reference
```

---

## Developer Workflow (BAS)

### First-time Setup in BAS

1. Open **SAP Business Application Studio** → create a **Dev Space** of type **"SAP Fiori"**.
2. Clone the repo into `~/projects/`:
   ```bash
   git clone <your-repo-url> vera-fiori
   cd vera-fiori/webapp
   npm install
   ```
3. In BAS → **View → Find Command → "Fiori: Open Deploy Configuration"**.
   BAS will prompt for:
   - Target: **Cloud Foundry**
   - CF API endpoint, org, and space
   - HTML5 Application Repository service instance → `vera-html5-host`

   This rewrites the `deploy` block in `ui5-deploy.yaml` with your values.

### Daily Development

**Run with live portal data:**
```bash
npm start
```
BAS routes `/vera-portal/*` through the `VERA_PORTAL` BTP destination automatically.

**Run fully offline with mocks:**
```bash
npm run start-mock
```
Sinon intercepts all calls. No network needed.

### Deploy to BTP

Right-click `webapp/` in BAS → **Deploy → Deploy to Cloud Foundry**.
Or from terminal:
```bash
npm run deploy
```

BAS:
1. Runs `ui5 build` (excludes test files and mock mode)
2. Packages `dist/` into a zip
3. Uploads to `vera-html5-host` service instance
4. App is now live in HTML5 Application Repository

### Publish to Workzone

The three `crossNavigation.inbounds` from `manifest.json` become three tiles in **Workzone Content Manager**:
- Register as Vendor
- Maintain Vendor Record
- Vendor Registration Status

Drag them into pages, assign roles, end users see them in the Workzone launchpad.

---

## Prerequisites Checklist

One-time BTP setup — see **`btp/BTP_SETUP.md`**:

- [ ] BTP subaccount entitled for: Workzone, HTML5 App Repo, Destination, Connectivity, XSUAA
- [ ] Cloud Connector installed in on-premise DMZ, linked to BTP subaccount
- [ ] SCC system mapping for on-premise NetWeaver Portal
- [ ] SCC Principal Propagation configured
- [ ] BTP Destination `VERA_PORTAL` created in Cockpit
- [ ] Service instances: `vera-html5-host`, `vera-destination`, `vera-connectivity`
- [ ] (Optional) XSUAA role collections: `VeRA_Vendor`, `VeRA_Inviter`, `VeRA_Buyer`, `VeRA_Admin`
- [ ] Workzone site created, tiles assigned

---

## Timeline Estimate

| Phase                                          | Effort      |
|------------------------------------------------|-------------|
| Phase 1 — BTP setup, SCC, destination, BAS    | 1.5 weeks   |
| Phase 2 — Core integration (5 tabs + save)    | 3 weeks     |
| Phase 3 — Status, inbox, actions, maintain    | 2 weeks     |
| Phase 4 — Testing, vendor type matrix, edges  | 2 weeks     |
| Phase 5 — UAT, Workzone setup, cutover        | 2 weeks     |
| **Total**                                      | **~11 weeks** |
