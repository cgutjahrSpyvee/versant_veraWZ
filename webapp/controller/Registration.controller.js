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

        /**
         * mode is "register" (empty form from an invite), "edit" (an existing
         * request whose status still allows changes) or "display" (an existing
         * request that is read-only). Home resolves it per the invite's REQST —
         * see VeRAService.resolveInviteTarget.
         */
        _onRouteMatched: function (oEvent) {
            var sMode      = oEvent.getParameter("arguments").mode || "register";
            var bEditable  = sMode !== "display";

            this._reg().setProperty("/mode", sMode);
            this._reg().setProperty("/ui/editable", bEditable);

            // Nothing is being filled in on a read-only request, so every step
            // counts as validated — otherwise the wizard refuses to page past
            // the first one and the rest of the request can't be seen.
            if (!bEditable) {
                this._reg().setProperty("/wizard/stepsValidated",
                    this._reg().getProperty("/wizard/stepsValidated").map(function () {
                        return true;
                    }));
            }

            var oWizard = this.byId("veraWizard");
            if (oWizard) {
                oWizard.discardProgress(oWizard.getSteps()[0]);
            }
        },

        _isEditable: function () { return this._reg().getProperty("/ui/editable"); },

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

        onStepBasicComplete:            function () { this._setStepValidated(0, true); },
        onStepCompanyApproverComplete:  function () { this._setStepValidated(1, true); },
        onStepPaymentTermsComplete:     function () { this._setStepValidated(3, true); },

        onStepTaxComplete: function _afterTax() {
            // Reload payment terms when entering the Payment Terms step,
            // since vendor type is now known from the Basic step
            var oPaymentTermsView = this.byId("paymentTermsView");
            if (oPaymentTermsView) {
                var oController = oPaymentTermsView.getController();
                if (oController && oController.loadPaymentTerms) {
                    oController.loadPaymentTerms();
                }
            }
            this._setStepValidated(2, true);
        },
        onStepBankingComplete:          function () { this._setStepValidated(4, true); },
        onStepContactsComplete:         function () { this._setStepValidated(5, true); },

        _setStepValidated: function (iIndex, bValid) {
            var aValidated = this._reg().getProperty("/wizard/stepsValidated").slice();
            aValidated[iIndex] = bValid;
            this._reg().setProperty("/wizard/stepsValidated", aValidated);
        },

        // ── Wizard final submit ──────────────────────────────────────

        onWizardComplete: function () {
            // The submit button is hidden in display mode; this is the backstop.
            if (!this._isEditable()) { return; }

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
            if (!oWizard) { return; }

            // A read-only request is being paged through, not filled in, so
            // there is nothing to validate and nothing to mark as touched.
            if (!this._isEditable()) {
                oWizard.nextStep();
                return;
            }

            var aSteps = oWizard.getSteps();
            var oCurrentStep = oWizard.getProgressStep();
            var iCurrentIndex = aSteps.indexOf(oCurrentStep);

            var aStepConfig = [
                { viewId: "basicView",             touchedFlag: "/ui/basicTouched" },
                // TODO: re-enable when Company/Approver step is restored
                // { viewId: "companyApproverView",   touchedFlag: "/ui/companyApproverTouched" },
                { viewId: "taxView",               touchedFlag: "/ui/taxTouched" },
                // TODO: re-enable when Payment Terms step is restored
                // { viewId: "paymentTermsView",      touchedFlag: "/ui/paymentTermsTouched" },
                { viewId: "bankingView",            touchedFlag: "/ui/bankingTouched" },
                { viewId: "contactsView",           touchedFlag: "/ui/contactsTouched" }
            ];

            if (iCurrentIndex >= 0 && iCurrentIndex < aStepConfig.length) {
                var oConfig = aStepConfig[iCurrentIndex];
                this._reg().setProperty(oConfig.touchedFlag, true);

                var oSubView = this.byId(oConfig.viewId);
                if (oSubView) {
                    var oStepController = oSubView.getController();
                    if (oStepController && oStepController._validateStep) {
                        var bValid = oStepController._validateStep();
                        if (!bValid) {
                            var aMissing = oStepController.getMissingFields
                                ? oStepController.getMissingFields() : [];
                            var sMsg = this._i18n("completeRequiredFields");
                            if (aMissing.length > 0) {
                                sMsg += "\n\n" + aMissing.map(function (s) {
                                    return "\u2022 " + s;
                                }).join("\n");
                            }
                            MessageBox.warning(sMsg, {
                                title: this._i18n("validationErrorTitle")
                            });
                            return;
                        }
                    }
                }
            }

            oWizard.nextStep();
        },

        // ── Save for Later ───────────────────────────────────────────

        onSaveForLater: function () {
            if (!this._isEditable()) { return; }

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
