sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Tax", {

        onInit: function () {},

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onEntityTypeSelect: function (oEvent) {
            var sType = oEvent.getParameter("selectedIndex") === 0 ? "Entity" : "Individual";
            this._reg().setProperty("/tax/entityType", sType);
            this._validateStep();
        },

        onUSPersonSelect: function (oEvent) {
            var bIsUS = oEvent.getParameter("selectedIndex") === 0;
            this._reg().setProperty("/tax/isUSPerson", bIsUS);
            this._validateStep();
        },

        onW9FileChange: function (oEvent) {
            var oFile = oEvent.getParameter("files")[0];
            if (!oFile) { return; }
            var that = this;
            this._reg().setProperty("/tax/w9FileName", oFile.name);
            this._svc().uploadFile(oFile, this._reg().getProperty("/requestId"), "ZSVRA_REQ", "W9")
                .done(function (oResult) {
                    that._reg().setProperty("/tax/w9DocId", oResult.id || "");
                    MessageToast.show("W9 uploaded successfully.");
                    that._validateStep();
                })
                .fail(function () {
                    MessageBox.error("W9 upload failed. Please try again.");
                    that._reg().setProperty("/tax/w9FileName", "");
                });
        },

        on590FileChange: function (oEvent) {
            var oFile = oEvent.getParameter("files")[0];
            if (!oFile) { return; }
            var that = this;
            this._reg().setProperty("/tax/doc590Name", oFile.name);
            this._svc().uploadFile(oFile, this._reg().getProperty("/requestId"), "ZSVRA_REQ", "590")
                .done(function (oResult) {
                    that._reg().setProperty("/tax/doc590Id", oResult.id || "");
                    MessageToast.show("590 form uploaded successfully.");
                    that._validateStep();
                })
                .fail(function () {
                    MessageBox.error("590 upload failed. Please try again.");
                    that._reg().setProperty("/tax/doc590Name", "");
                });
        },

        onFileTypeMismatch: function () {
            MessageToast.show("Invalid file type. Please upload a PDF file.");
        },

        onTaxCategoryChange: function (oEvent) {
            var sKey = oEvent.getParameter("item").getKey();
            this._reg().setProperty("/tax/taxCategory", sKey);
            if (sKey === "TaxID") {
                this._reg().setProperty("/tax/ssnNumber", "");
            } else {
                this._reg().setProperty("/tax/taxIdNumber", "");
            }
            this._validateStep();
        },

        onTaxFieldChange: function () { this._validateStep(); },

        onTaxIdBlur: function () {
            var sTaxId    = this._reg().getProperty("/tax/taxIdNumber");
            var sVendorId = this._reg().getProperty("/vendorId");
            if (!sTaxId || sTaxId.length < 9) { return; }
            this._svc().validateTaxId(sTaxId, sVendorId)
                .done(function (oResult) {
                    if (oResult && oResult.returnStatus === "1") {
                        MessageBox.warning("Tax ID already registered: " + oResult.returnMessage);
                    }
                });
        },

        _validateStep: function () {
            var oTax = this._reg().getProperty("/tax");
            var bValid =
                !!oTax.w9DocId &&
                (oTax.isUSPerson || !!oTax.doc590Id) &&
                (oTax.taxCategory === "TaxID" ? !!oTax.taxIdNumber : !!oTax.ssnNumber) &&
                !!oTax.recipientType &&
                !!oTax.exemptPayeeCode;
            this._reg().setProperty("/wizard/stepsValidated/1", bValid);
            return bValid;
        }
    });
});
