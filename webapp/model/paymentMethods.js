/**
 * paymentMethods.js
 *
 * Which payment methods a vendor may use, which one is preselected, and how
 * that choice constrains the bank country.
 *
 * Ported from the portal's server-rendered banking section — the rules are not
 * table-driven there either, they are hardcoded branches in
 * UI/maintain_vendor.java (~4120-4321) plus the country coupling in
 * UI/vendor.js (~2906-2932). Line references are kept on each rule so the two
 * can be diffed when the portal changes.
 *
 * The method travels to the backend as the literal "ACH" / "Wire" / "Check"
 * (payload field primaryBankingType); Server/objectactions.java is what turns
 * it into the SAP bank type BVTYP — 01 / U01 / W01 — and it writes no LFBK row
 * at all for Check. Nothing here should pre-translate that.
 */
sap.ui.define([], function () {
    "use strict";

    var ACH   = "ACH";
    var WIRE  = "Wire";
    var CHECK = "Check";

    // maintain_vendor.java:4142 — the vendor types that route through the
    // check-eligible branch. Everything else takes the ACH/Wire branch.
    var CHECK_BRANCH = ["060", "092", "050", "040", "080", "093", "094"];

    // maintain_vendor.java:4214 — inside the check branch only Government also
    // gets Wire offered.
    var CHECK_BRANCH_WIRE = ["050"];

    // maintain_vendor.java:4188/4223 — Garnishment renders no dropdown at all,
    // so the default is the only thing the vendor can be paid by.
    var NO_CHOICE = ["092"];

    // maintain_vendor.java:4237 and :4275 — Posthumous, Production/Agreement
    // and Charitable default to Check on the ACH/Wire branch and are the only
    // types there that may also pick Check.
    var CHECK_ALLOWED = ["095", "018", "030"];

    // maintain_vendor.java:4162 — Utility is the one check-branch type that
    // starts on ACH rather than Check when no bank record exists yet.
    var UTILITY = "080";

    // vendor.js:2906 — ACH pins the country to US and locks the field.
    var ACH_PINS_COUNTRY = ["060", "092", "093", "094", "018"];
    // vendor.js:2912 — same types on Wire keep US but may change it.
    var WIRE_KEEPS_US    = ["060", "092", "093", "094"];
    // vendor.js:2926 — Political Contribution and Utility swing the country
    // with the method: ACH means US, Wire means "pick one".
    var COUNTRY_FOLLOWS_METHOD = ["040", "080"];

    function has(aList, sType) {
        return aList.indexOf(sType) !== -1;
    }

    function label(sKey) {
        return sKey === CHECK ? "Check" : sKey;
    }

    return {

        ACH:   ACH,
        WIRE:  WIRE,
        CHECK: CHECK,

        /**
         * The methods this vendor type may be paid by, in the order the portal
         * lists them.
         *
         * @param   {string} sVendorType two-or-three digit VEND_TYPE
         * @returns {object[]} [{ key, text }] for direct aggregation binding
         */
        getOptions: function (sType) {
            var sVendorType = sType || "";
            var aKeys;

            if (has(NO_CHOICE, sVendorType)) {
                // The vendor gets whatever the default resolves to, no toggle.
                aKeys = [CHECK];
            } else if (has(CHECK_BRANCH, sVendorType)) {
                aKeys = [ACH, CHECK];
                if (has(CHECK_BRANCH_WIRE, sVendorType)) { aKeys.push(WIRE); }
            } else if (has(CHECK_ALLOWED, sVendorType)) {
                aKeys = [CHECK, ACH, WIRE];
            } else {
                aKeys = [ACH, WIRE];
            }

            return aKeys.map(function (sKey) {
                return { key: sKey, text: label(sKey) };
            });
        },

        /**
         * True when the vendor type offers no choice at all, so the control
         * should render read-only rather than as a toggle.
         */
        isLocked: function (sType) {
            return has(NO_CHOICE, sType || "");
        },

        /**
         * The preselected method.
         *
         * @param {string}  sVendorType two-or-three digit VEND_TYPE
         * @param {string}  sCountry    current bank country, may be empty
         * @param {string}  sBvtyp      BVTYP of an existing bank record, if any
         * @param {boolean} bHasBankRecord whether the vendor already has one
         */
        getDefault: function (sType, sBankCountry, sBankType, bHasBankRecord) {
            var sVendorType = sType        || "";
            var sCountry    = sBankCountry || "";
            var sBvtyp      = sBankType    || "";

            if (has(CHECK_BRANCH, sVendorType)) {
                // maintain_vendor.java:4160-4182
                if (!bHasBankRecord) {
                    return sVendorType === UTILITY ? ACH : CHECK;
                }
                if (!sCountry)         { return CHECK; }
                if (sCountry === "US") { return ACH; }
                // Only Government carries on to Wire here; for the other types
                // the portal genuinely leaves the method blank.
                return has(CHECK_BRANCH_WIRE, sVendorType) ? WIRE : "";
            }

            // maintain_vendor.java:4230-4252
            if (!sCountry) {
                return has(CHECK_ALLOWED, sVendorType) ? CHECK : ACH;
            }
            if (sBvtyp.indexOf("W") !== -1 || sBvtyp.indexOf("U") !== -1) {
                return WIRE;
            }
            return ACH;
        },

        /**
         * How the chosen method constrains the bank country.
         *
         * @returns {object} { country, editable } — a null country means the
         *                   rules say nothing, so leave whatever is there.
         */
        resolveCountry: function (sType, sMethod) {
            var sVendorType = sType || "";

            // vendor.js:2926 — these two swing with the method either way.
            if (has(COUNTRY_FOLLOWS_METHOD, sVendorType)) {
                if (sMethod === ACH)  { return { country: "US", editable: true }; }
                if (sMethod === WIRE) { return { country: "",   editable: true }; }
                return { country: null, editable: true };
            }

            if (sMethod === ACH && has(ACH_PINS_COUNTRY, sVendorType)) {
                return { country: "US", editable: false };
            }
            if (sMethod === WIRE && has(WIRE_KEEPS_US, sVendorType)) {
                return { country: "US", editable: true };
            }
            if (sMethod === WIRE && sVendorType === "018") {
                return { country: "", editable: true };
            }

            // objectactions.java:1859 forces US for ACH on save regardless of
            // vendor type, so show that rather than letting the form disagree
            // with what will be stored.
            if (sMethod === ACH) {
                return { country: "US", editable: false };
            }

            return { country: null, editable: true };
        }
    };
});
