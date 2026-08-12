sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "vsnt/vera/model/paymentMethods"
], function (Controller, MessageToast, MessageBox, paymentMethods) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Banking", {

        onInit: function () {
            var sBankFormUrl = sap.ui.require.toUrl("vsnt/vera/assets/GlobalVendorBankDetailForm.xlsx");
            this.byId("bankFormDownloadLink").setHref(sBankFormUrl);

            // The allowed methods hang off /ui, which setData rebuilds on every
            // entry into the wizard, and this onInit runs only once — so
            // recompute on each entry rather than just here.
            this.getOwnerComponent().getRouter().getRoute("register")
                .attachPatternMatched(this._applyPaymentMethodRules, this);

            // Vendor type is editable in the Basic step, so the rules have to
            // follow it mid-wizard too.
            this._oTypeBinding = this._reg().bindProperty("/vendorType");
            this._oTypeBinding.attachChange(this._applyPaymentMethodRules, this);

            this._applyPaymentMethodRules();
        },

        onExit: function () {
            if (this._oTypeBinding) {
                this._oTypeBinding.detachChange(this._applyPaymentMethodRules, this);
                this._oTypeBinding.destroy();
                this._oTypeBinding = null;
            }
        },

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        /**
         * Rebuilds the method choices for the current vendor type and, when the
         * standing selection is no longer one of them, falls back to the
         * portal's default. See model/paymentMethods.js for the rules.
         */
        _applyPaymentMethodRules: function () {
            var oReg     = this._reg();
            var sType    = oReg.getProperty("/vendorType") || "";
            var oPrim    = oReg.getProperty("/banking/primaryAccount") || {};
            var aOptions = paymentMethods.getOptions(sType);
            var sDefault = paymentMethods.getDefault(
                sType, oPrim.country, oPrim.bvtyp, !!oPrim.bvtyp);

            // With no choice on offer the single option has to be the default
            // itself. getOptions hardcodes Check for that type while getDefault
            // can resolve to ACH, and the resulting mismatch would have the
            // SegmentedButton force-select its only item and write it back over
            // whatever the request actually carries.
            if (paymentMethods.isLocked(sType) && sDefault) {
                aOptions = [{ key: sDefault, text: sDefault }];
            }

            oReg.setProperty("/ui/paymentMethods", aOptions);
            oReg.setProperty("/ui/paymentMethodLocked", paymentMethods.isLocked(sType));

            var bStillValid = aOptions.some(function (o) {
                return o.key === oPrim.method;
            });
            if (!bStillValid) {
                oReg.setProperty("/banking/primaryAccount/method", sDefault);
            }

            this._applyCountryRules();
            this._validateStep();
        },

        // ACH/Wire constrain the bank country differently per vendor type.
        _applyCountryRules: function () {
            var oReg  = this._reg();
            var oRule = paymentMethods.resolveCountry(
                oReg.getProperty("/vendorType"),
                oReg.getProperty("/banking/primaryAccount/method")
            );

            if (oRule.country !== null) {
                oReg.setProperty("/banking/primaryAccount/country", oRule.country);
            }
            oReg.setProperty("/ui/bankCountryEditable", oRule.editable);
        },

        onPrimaryMethodChange: function (oEvent) {
            var sKey = oEvent.getParameter("item").getKey();
            this._reg().setProperty("/banking/primaryAccount/method", sKey);
            this._applyCountryRules();
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
            var oReg   = this._reg();
            var aAccts = oReg.getProperty("/banking/secondaryAccounts").slice();
            var sType  = oReg.getProperty("/vendorType") || "";

            // A new account starts on whatever this vendor type defaults to,
            // not a blanket ACH — the toggle may not even offer ACH.
            var sMethod = paymentMethods.getDefault(sType, "", "", false);
            var oRule   = paymentMethods.resolveCountry(sType, sMethod);

            aAccts.push({
                method: sMethod,
                country: oRule.country === null ? "US" : oRule.country,
                bvtyp: "",
                routingNum: "", accountNum: "", holderName: "",
                swiftNum: "", ibanNum: "",
                bankFileName: "", bankDocId: "", bankFileUrl: ""
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
            // A read-only request is being read, not filled in. Registration
            // marks every step valid up front so the wizard can be paged
            // through; recomputing here would undo that and strand the user.
            if (!this._reg().getProperty("/ui/editable")) { return true; }

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
