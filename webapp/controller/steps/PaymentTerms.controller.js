sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/base/Log"
], function (Controller, Log) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.PaymentTerms", {

        _lastVendorType: null,

        onInit: function () {
            this._loadPaymentTerms();
        },

        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        loadPaymentTerms: function () {
            this._loadPaymentTerms();
        },

        _loadPaymentTerms: function () {
            var oReg = this.getOwnerComponent().getModel("reg");
            var sVendorType = oReg.getProperty("/vendorType") || "";

            var aExisting = oReg.getProperty("/paymentTerms/availableTerms") || [];
            if (sVendorType === this._lastVendorType && aExisting.length > 0) {
                return;
            }
            this._lastVendorType = sVendorType;

            this.getOwnerComponent().getService().getPaymentTerms(sVendorType)
                .done(function (aTerms) {
                    oReg.setProperty("/paymentTerms/availableTerms", aTerms || []);
                })
                .fail(function () {
                    Log.error("VeRA: Failed to load payment terms");
                    oReg.setProperty("/paymentTerms/availableTerms", []);
                });
        },

        onPaymentTermSelect: function (oEvent) {
            var sKey = oEvent.getParameter("selectedItem").getKey();
            this._reg().setProperty("/paymentTerms/selected", sKey);
            this._reg().setProperty("/wizard/stepsValidated/2", !!sKey);
        },

        _validateStep: function () {
            var sSelected = this._reg().getProperty("/paymentTerms/selected");
            var bValid = !!sSelected;
            this._reg().setProperty("/wizard/stepsValidated/2", bValid);
            this._missingFields = bValid ? [] : ["Payment Term"];
            return bValid;
        },

        getMissingFields: function () {
            return this._missingFields || [];
        }
    });
});
