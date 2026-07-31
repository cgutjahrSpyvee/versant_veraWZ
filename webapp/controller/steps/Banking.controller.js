sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Controller, MessageToast, MessageBox) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Banking", {

        onInit: function () {
            var sBankFormUrl = sap.ui.require.toUrl("vsnt/vera/assets/GlobalVendorBankDetailForm.xlsx");
            this.byId("bankFormDownloadLink").setHref(sBankFormUrl);
        },

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onPrimaryMethodChange: function (oEvent) {
            var sKey = oEvent.getParameter("item").getKey();
            this._reg().setProperty("/banking/primaryAccount/method", sKey);
            this._validateStep();
        },

        onBankingFieldChange: function () { this._validateStep(); },

        onPrimaryBankFileChange: function (oEvent) {
            var oFile = oEvent.getParameter("files")[0];
            if (!oFile) { return; }
            var that = this;
            this._reg().setProperty("/banking/primaryAccount/bankFileName", oFile.name);
            this._svc().uploadRegistrationFile(oFile, "ACH", this._reg().getData())
                .done(function (oResult) {
                    that._reg().setProperty("/banking/primaryAccount/bankDocId", oResult.id || "");
                    MessageToast.show("Bank details form uploaded successfully.");
                    that._validateStep();
                })
                .fail(function () {
                    MessageBox.error("Bank file upload failed. Please try again.");
                    that._reg().setProperty("/banking/primaryAccount/bankFileName", "");
                });
        },

        onAddSecondaryAccount: function () {
            var aAccts = this._reg().getProperty("/banking/secondaryAccounts").slice();
            aAccts.push({
                method: "ACH", country: "US",
                routingNum: "", accountNum: "", holderName: "",
                swiftNum: "", ibanNum: "", bankFileName: "", bankDocId: ""
            });
            this._reg().setProperty("/banking/secondaryAccounts", aAccts);
        },

        onRemoveSecondaryAccount: function (oEvent) {
            var oCtx   = oEvent.getSource().getBindingContext("reg");
            var iIdx   = parseInt(oCtx.getPath().split("/").pop(), 10);
            var aAccts = this._reg().getProperty("/banking/secondaryAccounts").slice();
            aAccts.splice(iIdx, 1);
            this._reg().setProperty("/banking/secondaryAccounts", aAccts);
        },

        onNotificationEmailChange: function (oEvent) {
            var sValue = oEvent.getParameter("value");
            var bValid = !sValue || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(sValue);
            this._reg().setProperty("/ui/notificationEmailError", !bValid);
        },

        onAddNotificationEmail: function () {
            var sEmail = this._reg().getProperty("/ui/newNotificationEmail");
            if (!sEmail || this._reg().getProperty("/ui/notificationEmailError")) { return; }

            var aNotifs = this._reg().getProperty("/banking/paymentNotifications").slice();
            aNotifs.push({ email: sEmail });
            this._reg().setProperty("/banking/paymentNotifications", aNotifs);
            this._reg().setProperty("/ui/newNotificationEmail", "");
        },

        onRemoveNotificationEmail: function (oEvent) {
            var oCtx    = oEvent.getSource().getBindingContext("reg");
            var iIdx    = parseInt(oCtx.getPath().split("/").pop(), 10);
            var aNotifs = this._reg().getProperty("/banking/paymentNotifications").slice();
            aNotifs.splice(iIdx, 1);
            this._reg().setProperty("/banking/paymentNotifications", aNotifs);
        },

        _validateStep: function () {
            var oP = this._reg().getProperty("/banking/primaryAccount");
            var aMissing = [];
            if (oP.method !== "Check") {
                if (!oP.routingNum)  { aMissing.push("Routing Number"); }
                if (!oP.accountNum)  { aMissing.push("Account Number"); }
                if (!oP.holderName)  { aMissing.push("Account Holder Name"); }
                // if (!oP.bankDocId) { aMissing.push("Bank Details Form"); }
            }
            var bValid = aMissing.length === 0;
            this._reg().setProperty("/wizard/stepsValidated/4", bValid);
            this._missingFields = aMissing;
            return bValid;
        },

        getMissingFields: function () {
            return this._missingFields || [];
        }
    });
});
