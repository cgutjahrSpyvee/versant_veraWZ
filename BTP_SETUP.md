# BTP Setup Reference
## For BAS Deployment (Workzone managed approuter)

This file is **reference documentation only** — no deployment artifacts.
All configuration happens through the BTP Cockpit UI.

---

## 1. Service Instances

In BTP Cockpit → Subaccount → Space → Service Instances → Create:

| Service                              | Plan         | Instance Name           | Purpose                          |
|--------------------------------------|--------------|-------------------------|----------------------------------|
| HTML5 Application Repository         | app-host     | `vera-html5-host`       | Stores built app content         |
| HTML5 Application Repository         | app-runtime  | `vera-html5-runtime`    | Serves app at runtime            |
| Destination                          | lite         | `vera-destination`      | Resolves destination configs     |
| Connectivity                         | lite         | `vera-connectivity`     | Tunnel to Cloud Connector        |
| Authorization and Trust Management   | application  | `vera-xsuaa`            | OAuth / authentication           |

**No `xs-security.json` file needed** — when creating the XSUAA instance via Cockpit, paste security config (scopes, role templates, role collections) into the wizard form, or configure roles later.

---

## 2. VERA_PORTAL Destination

**BTP Cockpit → Connectivity → Destinations → New Destination:**

```
Name:              VERA_PORTAL
Type:              HTTP
URL:               https://<portal-host>:<port>/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.
Proxy Type:        OnPremise
Authentication:    PrincipalPropagation

Additional Properties:
  HTML5.DynamicDestination = true
  HTML5.Timeout            = 60000
  WebIDEEnabled            = true
  sap-client               = 100
```

Click **Check Connection** — expect "Connection to 'VERA_PORTAL' established."
A 403 error here usually means Principal Propagation isn't configured correctly in Cloud Connector.

---

## 3. Cloud Connector

In SCC Admin UI (`https://<scc-host>:8443`):

**A. Subaccount mapping**
```
Add Subaccount → paste your BTP subaccount credentials
```

**B. System mapping (Cloud to On-Premise)**
```
Back-end Type:    SAP NetWeaver Application Server Java
Protocol:         HTTPS
Internal Host:    <actual-portal-hostname>
Internal Port:    <actual-portal-port>
Virtual Host:     <portal-hostname>           (matches destination URL)
Virtual Port:     <portal-port>
Principal Type:   X.509 Certificate

Resources (URL paths):
  /irj/    → Accessible (and sub-paths)
```

**C. Principal Propagation**
```
Configuration → On-Premise → Principal Propagation
  Issuer:           Trusted BTP Subaccount Certificate
  Subject Pattern:  CN=${email}    (or ${name} — match portal UME field)
  Upload CA cert that signed user certificates for on-premise system
```

---

## 4. XSUAA Roles (optional)

If you need fine-grained role-based access, create role collections:

| Role Collection | Description                       | Assign to                  |
|-----------------|-----------------------------------|----------------------------|
| VeRA_Vendor     | External vendor self-service      | External vendor IdP group  |
| VeRA_Inviter    | NBCU employee — invite vendors    | Internal employee group    |
| VeRA_Buyer      | NBCU buyer — register on behalf   | Sourcing team group        |
| VeRA_Admin      | Full VeRA administration          | AP admin group             |

If on-premise portal already handles all authorization via NetWeaver UME roles, skip this — authorization flows through Principal Propagation to the portal's existing role checks (Java services call `Z_SF_I477_GET_USER_ROLES` for each request).

---

## 5. Workzone Content Setup

**BTP Cockpit → SAP Build Work Zone → Open application.**

**A. Channel Manager → Add Content Channel:**
```
Provider:    HTML5 Apps
Description: VeRA App Content
```

Your deployed app appears automatically once registered in HTML5 Application Repository.

**B. Content Manager:**

The three `crossNavigation.inbounds` from `manifest.json` become three selectable apps:
- `VeRA-register` → "Register as Vendor"
- `VeRA-maintain` → "Maintain Vendor Record"
- `VeRA-status`   → "Vendor Registration Status"

**C. Assign apps to roles → Assign roles to pages → Assign pages to a site.**

End users access via:
`https://<subdomain>.launchpad.cfapps.<region>.hana.ondemand.com/site/<site-id>`

---

## Summary: What Lives Where

| Configuration              | Where it lives                        |
|----------------------------|---------------------------------------|
| UI5 app source             | Git repo → `webapp/`                   |
| Routing rules              | Git repo → `webapp/xs-app.json`        |
| App metadata & tiles       | Git repo → `webapp/manifest.json`      |
| Deploy target              | Git repo → `webapp/ui5-deploy.yaml`    |
| Destination `VERA_PORTAL`  | BTP Cockpit UI (not in Git)            |
| Cloud Connector mapping    | SCC Admin UI (not in Git)              |
| XSUAA scopes / roles       | BTP Cockpit UI (not in Git)            |
| Workzone tiles / pages     | Workzone Content Manager (not in Git)  |

You don't have to manage `mta.yaml`, `xs-security.json`, a custom approuter module, or MBT build pipelines. BAS + the Workzone managed approuter handle all of that.
