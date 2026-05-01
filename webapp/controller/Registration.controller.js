sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageBox",
    "sap/m/MessageToast"
], function (Controller, MessageBox, MessageToast) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.Registration", {

        onInit: function () {
            var oRouter = this.getOwnerComponent().getRouter();
            oRouter.getRoute("register").attachPatternMatched(this._onRouteMatched, this);
        },

        _onRouteMatched: function (oEvent) {
            var sMode = oEvent.getParameter("arguments").mode || "register";
            this._reg().setProperty("/mode", sMode);

            var oWizard = this.byId("veraWizard");
            if (oWizard) {
                oWizard.discardProgress(oWizard.getSteps()[0]);
            }
        },

        _svc:  function () { return this.getOwnerComponent().getService(); },
        _reg:  function () { return this.getOwnerComponent().getModel("reg"); },
        _i18n: function (sKey) {
            return this.getView().getModel("i18n").getResourceBundle().getText(sKey);
        },

        // ── Cancel ───────────────────────────────────────────────────

        onCancel: function () {
            var that = this;
            MessageBox.confirm(this._i18n("cancelConfirmText"), {
                title: this._i18n("cancelConfirmTitle"),
                onClose: function (sAction) {
                    if (sAction === MessageBox.Action.OK) {
                        that.getOwnerComponent().getRouter().navTo("home");
                    }
                }
            });
        },

        // ── Wizard step completion handlers ──────────────────────────

        onStepBasicComplete:        function () { this._setStepValidated(0, true); },
        onStepTaxComplete:          function () { this._setStepValidated(1, true); },
        onStepPaymentTermsComplete: function () { this._setStepValidated(2, true); },
        onStepBankingComplete:      function () { this._setStepValidated(3, true); },
        onStepContactsComplete:     function () { this._setStepValidated(4, true); },

        _setStepValidated: function (iIndex, bValid) {
            var aValidated = this._reg().getProperty("/wizard/stepsValidated").slice();
            aValidated[iIndex] = bValid;
            this._reg().setProperty("/wizard/stepsValidated", aValidated);
        },

        // ── Wizard final submit ──────────────────────────────────────

        onWizardComplete: function () {
            var aValidated = this._reg().getProperty("/wizard/stepsValidated");
            if (!aValidated.every(function (v) { return v; })) {
                MessageBox.error(this._i18n("validationErrorText"), {
                    title: this._i18n("validationErrorTitle")
                });
                return;
            }

            var that = this;
            MessageBox.confirm(this._i18n("submitConfirmText"), {
                title: this._i18n("submitConfirmTitle"),
                onClose: function (sAction) {
                    if (sAction === MessageBox.Action.OK) { that._doSubmit(); }
                }
            });
        },

        _doSubmit: function () {
            var that     = this;
            var oRegData = this._reg().getData();
            var oPayload = this._svc().buildSavePayload(oRegData, oRegData.requestId);

            this._reg().setProperty("/ui/busy", true);

            this._svc().submitRegistration(oPayload)
                .done(function (oResult) {
                    if (oResult && oResult.code === "0") {
                        if (oResult.requestNumber) {
                            that._reg().setProperty("/requestId", oResult.requestNumber);
                        }
                        that._reg().setProperty("/status", "PENDING");
                        MessageBox.success(that._i18n("submitSuccessText"), {
                            title: that._i18n("submitSuccessTitle"),
                            onClose: function () {
                                that.getOwnerComponent().getRouter().navTo("status");
                            }
                        });
                    } else {
                        MessageBox.error(oResult ? oResult.message : that._i18n("submitErrorText"));
                    }
                })
                .fail(function () {
                    MessageBox.error(that._i18n("networkErrorText"));
                })
                .always(function () {
                    that._reg().setProperty("/ui/busy", false);
                });
        },

        // ── Next Step Navigation ─────────────────────────────────────

        onNextStep: function () {
            var oWizard = this.byId("veraWizard");
            if (oWizard) {
                oWizard.nextStep();
            }
        },

        // ── Save for Later ───────────────────────────────────────────

        onSaveForLater: function () {
            var that     = this;
            var oRegData = this._reg().getData();
            var oPayload = this._svc().buildSavePayload(oRegData, oRegData.requestId);

            this._reg().setProperty("/ui/busy", true);

            this._svc().saveForLater(oPayload)
                .done(function (oResult) {
                    if (oResult && oResult.code === "0") {
                        if (oResult.requestNumber) {
                            that._reg().setProperty("/requestId", oResult.requestNumber);
                        }
                        that._reg().setProperty("/status", "DRAFT");
                        MessageToast.show(that._i18n("saveSuccessText"), { duration: 3000 });
                    } else {
                        MessageBox.error(oResult ? oResult.message : that._i18n("saveErrorText"));
                    }
                })
                .fail(function () {
                    MessageBox.error(that._i18n("networkErrorText"));
                })
                .always(function () {
                    that._reg().setProperty("/ui/busy", false);
                });
        }
    });
});
