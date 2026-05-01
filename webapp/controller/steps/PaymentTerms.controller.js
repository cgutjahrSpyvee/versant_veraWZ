sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.PaymentTerms", {

        onInit: function () {
            var that = this;
            var oReg = this.getOwnerComponent().getModel("reg");
            var sVendorType = oReg.getProperty("/vendorType");

            this.getOwnerComponent().getService().getPaymentTerms(sVendorType)
                .done(function (aTerms) {
                    oReg.setProperty("/paymentTerms/availableTerms", aTerms || []);
                });
        },

        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onPaymentTermSelect: function (oEvent) {
            var oItem = oEvent.getParameter("listItem");
            if (!oItem) { return; }

            var aData = oItem.getCustomData();
            var sKey  = "";
            for (var i = 0; i < aData.length; i++) {
                if (aData[i].getKey() === "termKey") {
                    sKey = aData[i].getValue();
                    break;
                }
            }

            this._reg().setProperty("/paymentTerms/selected", sKey);
            this._reg().setProperty("/wizard/stepsValidated/2", !!sKey);
        }
    });
});
