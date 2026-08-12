/**
 * mask.js
 *
 * Display-only masking for the three values a loaded request carries that
 * should not be shown in full: SSN, tax ID and bank account number.
 *
 * Ported from the portal, which masked the same three server-side before
 * writing them into the page — UI/maintain_vendor.java:694-705 for the two tax
 * numbers and :326-341 for the account number.
 *
 * Two deliberate differences from the portal:
 *
 *   - It masked in edit mode too and then leaned on a "does it contain an X?"
 *     check to avoid saving the mask back. These are view formatters instead,
 *     so the model keeps the raw value and an edited request round-trips.
 *   - Where it blanked a value it could not mask, these pass it through. A
 *     value that is already masked (the RFC appears to do so sometimes — hence
 *     the portal's own X check at :326) must not be masked twice, and a short
 *     value is better shown than silently dropped.
 */
sap.ui.define([], function () {
    "use strict";

    // Already masked upstream, or nothing to mask.
    function passThrough(sValue) {
        return !sValue || String(sValue).indexOf("X") !== -1;
    }

    function lastFour(sValue) {
        return String(sValue).slice(-4);
    }

    return {

        /**
         * 123456789 → XXX-XX-6789. Only a full 9-digit SSN is masked, matching
         * maintain_vendor.java:695 — anything else is not an SSN shape and is
         * left as it came.
         */
        ssn: function (sValue) {
            if (passThrough(sValue)) { return sValue || ""; }
            var s = String(sValue).replace(/\D/g, "");
            if (s.length !== 9) { return String(sValue); }
            return "XXX-XX-" + lastFour(s);
        },

        /**
         * 123456789 → XX-XXX6789, the portal's EIN mask
         * (maintain_vendor.java:701).
         */
        taxId: function (sValue) {
            if (passThrough(sValue)) { return sValue || ""; }
            var s = String(sValue).replace(/\D/g, "");
            if (s.length !== 9) { return String(sValue); }
            return "XX-XXX" + lastFour(s);
        },

        /**
         * Everything but the last four digits, whatever the length
         * (maintain_vendor.java:328-337).
         */
        account: function (sValue) {
            if (passThrough(sValue)) { return sValue || ""; }
            var s = String(sValue);
            if (s.length <= 4) { return s; }
            return new Array(s.length - 4 + 1).join("X") + lastFour(s);
        }
    };
});
