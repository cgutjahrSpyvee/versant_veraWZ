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

    // IT_EMSG — the backend's notes on a row. Which id it is keyed on is the
    // open question the tolerant join in VeRAService.mapInvites works around,
    // so the caller passes both fields explicitly and the fixtures below cover
    // each convention. MSGTY comes back blank from the live service.
    function mockMessage(sReqst, sEmlId, sSeq, sText) {
        return {
            MANDT: "", REQST: sReqst, VERSN: "000", SEQNO: sSeq,
            ZZSF_VRA_EMLID: sEmlId, MSGTY: "", MESSAGE: sText
        };
    }

    // ── displayRequest row builders ────────────────────────────────────────
    // The response is a raw dump of the Z_SFI_I510_VRA_VENDISP export tables,
    // so each builder is one table row with every field the mapper reads.

    function mockLfa1(o) {
        return jQuery.extend({
            MANDT: "", REQST: "", VERSN: "000", SEQNO: "0000000000",
            NAME1: "", NAME2: "", NAME3: "", LAND1: "", ORT01: "", PSTLZ: "",
            REGIO: "", STRAS: "", STR_SUPPL1: "", STR_SUPPL2: "",
            KTOKK: "0001", LIFNR: "", SMTP_ADDR: "", TXJCD: "",
            STCD1: "", STCD2: "", TELF1: "", TELFX: "", J_1KFREPRE: ""
        }, o);
    }

    // BVTYP is the payment method in disguise: 01 = US ACH, U01 = US Wire,
    // W01 = foreign Wire, and Check writes no row at all.
    function mockLfbk(sBvtyp, sBankl, sBankn, sKoinh, sBanks) {
        return {
            MANDT: "", REQST: "", VERSN: "000",
            BANKS: sBanks || "US", BANKL: sBankl, BANKN: sBankn,
            BVTYP: sBvtyp, KOINH: sKoinh, BKREF: "", XEZER: ""
        };
    }

    function mockKnvk(sName, sAbtnr, sPhone, sFax, sEmail) {
        return {
            MANDT: "100", REQST: "", VERSN: "000", SEQNO: "0000000001",
            PARNR: "0000000000", KUNNR: "", NAMEV: "", NAME1: sName,
            ABTNR: sAbtnr, PAFKT: "", LIFNR: "", LOEVM: "", ADRNP_2: "",
            PRSNR: "", TELF1: sPhone, FAX_NUMBER: sFax, SMTP_ADDR: sEmail
        };
    }

    function mockFile(sType, sName, sObjectId) {
        return {
            MANDT: "100", REQST: "", FILE_TYPE: sType,
            ACT_FILE_NAME: sName, OBJECT_ID: sObjectId
        };
    }

    function mockReq(sReqId, sStats, o) {
        return jQuery.extend({
            MANDT: "100", REQST: sReqId, VERSN: "000", STATS: sStats,
            REQTY: "1", REQACTION: "00", ADMIN_EMAIL: "AMIT.CHAPATWALA@MADIBA.COM",
            ERNAM: "206423070", ERDAT: "2026-07-28", ERZET: "15:50:09",
            ZREJECT_REASON: "", ZMANUAL_REJECT: "", DESCR: "",
            VEND_TYPE: "010", ZZSF_VRA_VENDGRP: "", ZZSF_VRA_VENDCAT: "1",
            VRA_BRSCH: "", VRA_MINDK: "", ZZSF_VRA_PORECV: "",
            ZZSF_VRA_TNC: "", COMMENT1: "testing", COMMENT2: "", COMMENT3: "",
            COMMENT4: "", ZZSF_VRA_POEX: "", ANNUAL_SPEND: "",
            ZZSF_VRA_QSREC: "00", ZZSF_VRA_EXMPTPC: "", ZZSF_VRA_EXMPTFRC: "",
            APPROVER_SSO: "", REQUESTED_FOR: "", AP_REVIEWER: "",
            AP_DATE: "0000-00-00", CANCEL_REASON: "", ZCANCEL_PERSON: "",
            SUB_CAT_ID: "000"
        }, o);
    }

    // Every CT_* table defaults to empty, which is the point: a live response
    // routinely comes back with most of them empty, and the mapper has to
    // survive that. Each fixture fills in only the tables it is testing.
    function mockDisplay(o) {
        return JSON.stringify(jQuery.extend({
            CS_RETURN: {
                TYPE: "", ID: "", NUMBER: "000", MESSAGE: "", LOG_NO: "",
                LOG_MSG_NO: "000000", MESSAGE_V1: "", MESSAGE_V2: "",
                MESSAGE_V3: "", MESSAGE_V4: "", PARAMETER: "", ROW: "0",
                FIELD: "", SYSTEM: ""
            },
            CT_ADR6: [], CT_ANSWER: [], CT_BNKA: [], CT_CONTROL: [],
            CT_CTI: [], CT_EMSG: [], CT_FILES: [], CT_IBAN: [], CT_KNVK: [],
            CT_LFA1: [], CT_LFB1: [], CT_LFBK: [], CT_LFBW: [], CT_LFM1: [],
            CT_LFZA: [], CT_REQ: [], CT_REQUIRED: [], CT_TBCN2: [],
            CT_TBCN21: [], CT_WYT3: [], CT_ZTERMS: []
        }, o));
    }

    var JSON_HEADER = { "Content-Type": "application/json" };

    return {

        init: function () {
            var oServer = sinon.fakeServer.create();
            oServer.autoRespond     = true;
            oServer.autoRespondAfter = 200;

            // ORDER MATTERS, AND IT IS LAST-WINS. sinon walks its response
            // list backwards (sap/ui/thirdparty/sinon.js, processRequest:
            // "for (var i = responses.length - 1; i >= 0; i--)"), so the LAST
            // registered matching entry answers the request. Every catch-all
            // below therefore sits ABOVE the specific routes it would
            // otherwise swallow. Do not "tidy" them to the bottom of their
            // group — that silently blanks every fixture above them.

            // ── htmlhelper ─────────────────────────────────────────────────
            oServer.respondWith(/\/vera-portal\/htmlhelper.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({})
            ]);

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

            // ── wz_services ────────────────────────────────────────────────
            // Catch-all first — see the ordering note at the top of init.
            oServer.respondWith(/\/vera-portal\/wz_services.*/, [
                200,
                { "Content-Type": "application/json" },
                JSON.stringify({ code: "0", message: "" })
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
                    ],
                    // The notes shown under a row's status. Keyed both ways on
                    // purpose — see mockMessage and the join in mapInvites.
                    inviteIT_EMSG: [
                        // 2 (rejected) — keyed the way the live payload does it:
                        // REQST holds the *invite* id, EMLID is the sentinel.
                        // Out of SEQNO order here, to prove the sort.
                        mockMessage("0000610085", "0000000000", "0000000002",
                            "Tax form not legible"),
                        mockMessage("0000610085", "0000000000", "0000000001",
                            "Banking details do not match the account name"),
                        // 3 (in flight) — keyed the inbox.java:85 way, on the
                        // request number.
                        mockMessage("0000111603", "0000000000", "0000000001",
                            "Awaiting proof of insurance"),
                        // Neither id belongs to any invite → warns and is dropped.
                        mockMessage("0000999999", "0000000000", "0000000001",
                            "Orphaned message, should not appear"),
                        // Blank MESSAGE → skipped before it ever reaches a row.
                        mockMessage("0000610085", "0000000000", "0000000003", "   ")
                    ]
                })
            ]);

            // ── wz_services: displayRequest ────────────────────────────────
            // Registered after inviteInfo, and the per-request ones after the
            // generic one — last match wins, see the note at the top.
            //
            // The generic case is the unmapped-request path: CS_RETURN type E
            // with a message, which is how the RFC reports failure.
            oServer.respondWith(/\/vera-portal\/wz_services.*Action=displayRequest.*/, [
                200, JSON_HEADER,
                mockDisplay({
                    CS_RETURN: {
                        TYPE: "E", MESSAGE: "No data found for this request.",
                        ID: "", NUMBER: "042"
                    }
                })
            ]);

            // 0000111602 — the rich edit case. Two addresses, an invoicing
            // name, a tax ID, primary + secondary bank accounts, contacts,
            // notification emails and three documents.
            oServer.respondWith(/\/vera-portal\/wz_services.*RequestId=0000111602.*/, [
                200, JSON_HEADER,
                mockDisplay({
                    CT_LFA1: [
                        mockLfa1({
                            REQST: "0000111602",
                            // NAME2 present, so NAME1+NAME3 is the invoicing
                            // name and NAME2 the legal one.
                            NAME1: "Rejected Vendor", NAME2: "Rejected Vendor Co LLC",
                            NAME3: "Holdings",
                            LAND1: "US", STRAS: "1200 Market Street",
                            STR_SUPPL1: "Suite 400", ORT01: "Philadelphia",
                            REGIO: "PA", PSTLZ: "19107", TXJCD: "PA0000000",
                            SMTP_ADDR: "po@rejectedvendor.example",
                            STCD2: "123456789", KTOKK: "0001", LIFNR: "0000500123"
                        }),
                        mockLfa1({
                            REQST: "0000111602", NAME1: "Rejected Vendor Co LLC",
                            LAND1: "US", STRAS: "88 Billing Way", ORT01: "Camden",
                            REGIO: "NJ", PSTLZ: "08103", KTOKK: "B001"
                        })
                    ],
                    CT_LFBK: [
                        mockLfbk("01",  "031000053", "1234567890", "Rejected Vendor Co LLC", "US"),
                        mockLfbk("02",  "021000021", "9876543210", "Rejected Vendor Co LLC", "US")
                    ],
                    CT_BNKA: [
                        { BANKS: "US", BANKL: "031000053", SWIFT: "PNCCUS33", BANKA: "PNC Bank" }
                    ],
                    CT_IBAN: [
                        { BANKS: "US", BANKL: "031000053", BANKN: "1234567890",
                          IBAN: "US64PNCC0310000531234567890" }
                    ],
                    CT_KNVK: [
                        mockKnvk("Dana Whitfield", "0009", "215 555 0134", "215 555 0135",
                            "dana.whitfield@rejectedvendor.example"),
                        mockKnvk("Sam Okoro", "0002", "215 555 0177", "",
                            "sam.okoro@rejectedvendor.example"),
                        mockKnvk("Billing Desk", "ZALT", "", "", "ap@rejectedvendor.example")
                    ],
                    CT_ADR6: [
                        { SMTP_ADDR: "remittance@rejectedvendor.example" },
                        { SMTP_ADDR: "treasury@rejectedvendor.example" }
                    ],
                    CT_FILES: [
                        mockFile("W9",  "W9-RejectedVendor-2026.pdf", "DOC0000000901"),
                        mockFile("590", "CA590-RejectedVendor.pdf",   "DOC0000000902"),
                        mockFile("ACH", "BankDetails-PNC.pdf",        "DOC0000000903")
                    ],
                    // REQST 0000000000 on these two, exactly as the live
                    // response has it — only CT_REQ carries the real number.
                    CT_LFB1: [
                        { MANDT: "", REQST: "0000000000", VERSN: "000", BUKRS: "A083",
                          ZWELS: "C", ZTERM: "0013", REPRF: "X", AKONT: "" },
                        { MANDT: "", REQST: "0000000000", VERSN: "000", BUKRS: "A090",
                          ZWELS: "C", ZTERM: "0013", REPRF: "X", AKONT: "" }
                    ],
                    CT_LFM1: [
                        { MANDT: "", REQST: "0000000000", VERSN: "000", EKORG: "A001",
                          WAERS: "USD", ZTERM: "0013", XERSY: "" }
                    ],
                    CT_ZTERMS: [
                        { ZTERM: "0012", TEXT1: "Net 45 Days",  TERM_FLAG: "" },
                        { ZTERM: "0013", TEXT1: "Net 60 Days",  TERM_FLAG: "Y" },
                        { ZTERM: "0035", TEXT1: "Net 75 Days",  TERM_FLAG: "" },
                        // Blank text — the mapper drops it, as the portal did.
                        { ZTERM: "0099", TEXT1: "",             TERM_FLAG: "" }
                    ],
                    CT_REQ: [
                        mockReq("0000111602", "R", {
                            VEND_TYPE: "010", COMMENT1: "Rejected — resubmit with a W-9.",
                            ANNUAL_SPEND: "250000", ZZSF_VRA_QSREC: "02",
                            ZZSF_VRA_EXMPTPC: "14", ZZSF_VRA_PORECV: "X"
                        })
                    ]
                })
            ]);

            // 0000111603 — display, and a Wire account: BVTYP U01 with a
            // non-US bank country, which is also a country the (hidden) bank
            // country Select does not list, and a REGIO the region list has no
            // entry for. Both should render blank rather than being rewritten.
            oServer.respondWith(/\/vera-portal\/wz_services.*RequestId=0000111603.*/, [
                200, JSON_HEADER,
                mockDisplay({
                    CT_LFA1: [
                        mockLfa1({
                            REQST: "0000111603", NAME1: "In Flight Vendor Ltd",
                            LAND1: "GB", STRAS: "10 Finsbury Square",
                            ORT01: "London", REGIO: "LDN", PSTLZ: "EC2A 1AF",
                            STCD2: "987654321"
                        })
                    ],
                    CT_LFBK: [
                        mockLfbk("U01", "BARCGB22", "60161331926819", "In Flight Vendor Ltd", "GB")
                    ],
                    CT_KNVK: [
                        mockKnvk("Priya Raman", "0018", "44 20 7946 0011", "",
                            "priya.raman@inflight.example")
                    ],
                    CT_LFB1: [
                        { MANDT: "", REQST: "0000000000", VERSN: "000", BUKRS: "A083",
                          ZWELS: "C", ZTERM: "0035", REPRF: "X", AKONT: "" }
                    ],
                    CT_ZTERMS: [
                        { ZTERM: "0013", TEXT1: "Net 60 Days", TERM_FLAG: "" },
                        { ZTERM: "0035", TEXT1: "Net 75 Days", TERM_FLAG: "Y" }
                    ],
                    CT_REQ: [
                        mockReq("0000111603", "W", {
                            COMMENT1: "Awaiting approval.", ZZSF_VRA_QSREC: "04"
                        })
                    ]
                })
            ]);

            // 0000111605 — the crash-bait case, reproducing the live sample:
            // one near-empty CT_LFA1 row and every other table empty. No bank
            // record at all, so the method has to resolve to Check.
            //
            // Its CT_REQ also carries a REQST that disagrees with the one
            // asked for, which the live service did too — the mapper should
            // take the returned one and log the mismatch.
            oServer.respondWith(/\/vera-portal\/wz_services.*RequestId=0000111605.*/, [
                200, JSON_HEADER,
                mockDisplay({
                    CT_LFA1: [ mockLfa1({ NAME1: "MYMEDIA", KTOKK: "0001" }) ],
                    CT_REQ:  [ mockReq("0000111595", "P", { COMMENT1: "testing" }) ]
                })
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
