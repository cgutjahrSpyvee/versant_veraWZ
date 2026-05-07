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
