/**
 * MockServer.js
 *
 * Sinon-based fake server that intercepts all calls to /vera-portal/*
 * and returns canned responses. Used by index-mock.html for fully
 * offline development without needing the real on-premise portal.
 */
sap.ui.define([
    "sap/ui/thirdparty/sinon"
], function (sinon) {
    "use strict";

    var BASE = "/vera-portal/";

    // Row builders for the inviteInfo fixture below. Only the fields the app
    // reads vary per case; the rest are held at the values Amit's live response
    // returned, so the mock stays shaped like the real thing.
    function mockInvite(sEmlId, sName, sInvStat) {
        return {
            ZZSF_VRA_EMLID: sEmlId, VERSN: "003",
            CHANGE_DATE: "2026-08-03", CHANGE_TIME: "14:58:09",
            INVITE_SSO: "206423070", FIRST_NAME: "onemedia", LAST_NAME: "corp",
            TELEPHONE: "888 8888888", SPRAS: "E", LAND1: "US",
            VEND_NAME: sName, VEND_TYPE: "010", VEND_DESC: "Trade Vendor",
            J_1KFREPRE: "", ADMIN_SSO: "", SOURCE: "1", LIFNR: "",
            ZTERM: "0013", TEXT1: "Net 60 Days", XERSY: "",
            ANNUAL_SPEND: "60000", INVCOMMENT: "Craig testing",
            ZZSF_VRA_INVSTAT: sInvStat, ZZSF_VRA_VENDGRP: "",
            ZZSF_VRA_VENDCAT: "1", ZZSF_VRA_PORECV: "", ZZSSO_UPD: "X",
            ZZSF_VRA_POEX: "", APPROVER_SSO: "", REQUESTED_FOR: "",
            CANCEL_REASON: "", KONZS: "", ZCANCEL_PERSON: "",
            SUB_CAT_ID: "000", SUB_CAT_DESC: "",
            SUBMIT_FNAME: "Amit", SUBMIT_LNAME: "Chapatwala"
        };
    }

    function mockVAdmin(sEmlId, sReqId, sName) {
        return {
            MANDT: "100", ADMIN_SSO: "206423070", ZZSF_VRA_EMLID: sEmlId,
            REQST: sReqId, VERSN: "000", SOURCE: "1", LIFNR: "",
            ADMIN_SSO_SUB: "206423070", FIRST_NAME: "onemedia",
            LAST_NAME: "corp", TELEPHONE: "888 8888888", SPRAS: "E",
            ZZSSO_UPD: "X", VEND_NAME: sName
        };
    }

    function mockRequest(sReqId, sInvStat, sName) {
        return {
            CHANGE_DATE: "2026-08-03", SOURCE: "Vendor Request",
            VEND_NAME: sName, LIFNR: "", INVSTAT: sInvStat, REQST: sReqId
        };
    }

    return {

        init: function () {
            var oServer = sinon.fakeServer.create();
            oServer.autoRespond     = true;
            oServer.autoRespondAfter = 200;

            // ── htmlhelper ─────────────────────────────────────────────────
            oServer.respondWith(/\/vera-portal\/htmlhelper\?type=getCountry.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify([
                    { country: "US", description: "United States" },
                    { country: "CA", description: "Canada" },
                    { country: "GB", description: "United Kingdom" },
                    { country: "MX", description: "Mexico" },
                    { country: "AU", description: "Australia" },
                    { country: "FR", description: "France" },
                    { country: "DE", description: "Germany" }
                ])
            ]);

            oServer.respondWith(/\/vera-portal\/htmlhelper\?type=getRegion.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify([
                    { region: "CA", description: "California" },
                    { region: "NY", description: "New York" },
                    { region: "TX", description: "Texas" },
                    { region: "FL", description: "Florida" },
                    { region: "WA", description: "Washington" },
                    { region: "IL", description: "Illinois" }
                ])
            ]);

            oServer.respondWith(/\/vera-portal\/htmlhelper\?type=PaymentTerms.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify([
                    { Key: "NET20", Description: "Net 20 Days" },
                    { Key: "NET30", Description: "Net 30 Days (nonstandard)" },
                    { Key: "NET45", Description: "Net 45 Days" },
                    { Key: "NET60", Description: "Net 60 Days" },
                    { Key: "NET75", Description: "Net 75 Days" },
                    { Key: "2.50%DISC15DAYS", Description: "2.50% Disc 15 Days, 1.50% 30 Days, Net 75" },
                    { Key: "IMM",   Description: "Immediate Payment" }
                ])
            ]);

            oServer.respondWith(/\/vera-portal\/htmlhelper\?type=validatetaxid.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ returnStatus: "0", returnMessage: "Tax ID is valid" })
            ]);

            oServer.respondWith(/\/vera-portal\/htmlhelper\?type=validateCityState.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ taxJurisdiction: "CA90210" })
            ]);

            oServer.respondWith(/\/vera-portal\/htmlhelper.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({})
            ]);

            // ── inbox ──────────────────────────────────────────────────────
            oServer.respondWith(/\/vera-portal\/inbox.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify([
                    {
                        id:        "REQ-2026-001",
                        name:      "Test Vendor Company LLC",
                        status:    { text: "Pending Submission" },
                        date:      "04/16/2026",
                        source:    "Registration",
                        actionkey: ""
                    },
                    {
                        id:        "INV-2026-002",
                        name:      "Sample Vendor Inc",
                        status:    { text: "Pending Approval" },
                        date:      "04/10/2026",
                        source:    "Invite",
                        actionkey: "RC"
                    },
                    {
                        id:        "REQ-2025-089",
                        name:      "Acme Corporation",
                        status:    { text: "Completed" },
                        date:      "11/22/2025",
                        source:    "Registration",
                        actionkey: ""
                    }
                ])
            ]);

            // ── wz_services: inviteInfo ────────────────────────────────────
            // Shaped exactly like the live response: inviteData (the invites),
            // vadminData (carries REQST, joined on ZZSF_VRA_EMLID) and
            // inviteREQData (the IT_INVITE_REQ inbox rows, joined on REQST).
            //
            // The five invites below cover every branch of
            // VeRAService.resolveInviteTarget — see the comment on each.
            oServer.respondWith(/\/vera-portal\/wz_services.*Action=inviteInfo.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({
                    code: "0",
                    message: "",
                    inviteData: [
                        // 1. REQST matches but the inbox row is still on the
                        //    invite's own status (5) — no request behind it yet,
                        //    so this opens an empty, editable form. This is
                        //    Amit's sample response, field for field.
                        mockInvite("0000610084", "onemedia",          "5"),
                        // 2. Request rejected → editable, pre-filled.
                        mockInvite("0000610085", "Rejected Vendor Co", "5"),
                        // 3. Request pending approval → display only.
                        mockInvite("0000610086", "In Flight Vendor",   "5"),
                        // 4. No vadminData row at all → no REQST → empty form.
                        mockInvite("0000610087", "Brand New Vendor",   "5"),
                        // 5. Request completed → display only, and status.open
                        //    is false so Home's list drops it while the Status
                        //    page (bound to /items) still shows it.
                        mockInvite("0000610088", "Finished Vendor",    "6")
                    ],
                    vadminData: [
                        mockVAdmin("0000610084", "0000111601", "onemedia"),
                        mockVAdmin("0000610085", "0000111602", "Rejected Vendor Co"),
                        mockVAdmin("0000610086", "0000111603", "In Flight Vendor"),
                        // 0000610087 deliberately absent — case 4.
                        mockVAdmin("0000610088", "0000111605", "Finished Vendor")
                    ],
                    inviteREQData: [
                        mockRequest("0000111601", "5", "onemedia"),
                        mockRequest("0000111602", "R", "Rejected Vendor Co"),
                        mockRequest("0000111603", "W", "In Flight Vendor"),
                        mockRequest("0000111605", "P", "Finished Vendor")
                    ]
                })
            ]);

            oServer.respondWith(/\/vera-portal\/wz_services.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ code: "0", message: "" })
            ]);

            // ── vendor search ──────────────────────────────────────────────
            oServer.respondWith(/\/vera-portal\/vendorsearch.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ vendors: [], code: "0" })
            ]);

            // ── save / submit ──────────────────────────────────────────────
            oServer.respondWith("POST", /\/vera-portal\/objectactions.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({
                    code:          "0",
                    message:       "Saved successfully",
                    requestNumber: "REQ-2026-001"
                })
            ]);

            // ── workflow actions ───────────────────────────────────────────
            oServer.respondWith("POST", /\/vera-portal\/(invite|request)actions.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ code: "0", message: "Action completed" })
            ]);

            // ── file operations ────────────────────────────────────────────
            oServer.respondWith("POST", /\/vera-portal\/managecsdoc.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({
                    id:       "MOCK-DOC-" + Date.now(),
                    code:     "0",
                    message:  "Upload successful"
                })
            ]);

            oServer.respondWith(/\/vera-portal\/deletecsdoc.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ code: "0", message: "Deleted" })
            ]);

            return oServer;
        }
    };
});
