sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast",
    "sap/m/MessageBox",
    "sap/m/Token",
    "sap/m/SelectDialog",
    "sap/m/StandardListItem",
    "sap/ui/model/json/JSONModel",
    "sap/ui/model/Filter",
    "sap/ui/core/Item"
], function (Controller, MessageToast, MessageBox, Token, SelectDialog, StandardListItem, JSONModel, Filter, Item) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.CompanyApprover", {

        onInit: function () {},

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        // ── Company Code Search ────────────────────────────────────────

        onSearchCompanyCodes: function () {
            var sQuery = this.byId("companyCodeInput").getValue().trim();
            if (!sQuery) {
                MessageToast.show("Enter a company code to search.");
                return;
            }

            var that = this;
            this._svc().getCompanyCodes(sQuery)
                .done(function (aResults) {
                    if (!aResults || aResults.length === 0) {
                        MessageToast.show("No company codes found.");
                        return;
                    }
                    that._showCompanyCodeDialog(aResults);
                })
                .fail(function () {
                    MessageBox.error("Failed to search company codes.");
                });
        },

        _showCompanyCodeDialog: function (aResults) {
            var that = this;
            var oModel = new JSONModel(aResults);

            if (this._oCoCodeDialog) {
                this._oCoCodeDialog.destroy();
            }

            this._oCoCodeDialog = new SelectDialog({
                title: "Select Company Code",
                multiSelect: true,
                items: {
                    path: "/",
                    template: new StandardListItem({
                        title: "{Code}",
                        description: "{Description}"
                    })
                },
                confirm: function (oEvent) {
                    var aContexts = oEvent.getParameter("selectedContexts");
                    if (!aContexts || aContexts.length === 0) { return; }

                    var aCodes = that._reg().getProperty("/companyCodes").slice();
                    var oTokenizer = that.byId("companyCodeTokenizer");

                    aContexts.forEach(function (oCtx) {
                        var sCode = oCtx.getObject().Code;
                        if (aCodes.indexOf(sCode) === -1) {
                            aCodes.push(sCode);
                            oTokenizer.addToken(new Token({
                                key: sCode,
                                text: sCode + " - " + oCtx.getObject().Description
                            }));
                        }
                    });

                    that._reg().setProperty("/companyCodes", aCodes);
                    that.byId("companyCodeInput").setValue("");
                    that._loadApprovers();
                    that._validateStep();
                }
            });

            this._oCoCodeDialog.setModel(oModel);
            this._oCoCodeDialog.open();
        },

        onCompanyCodeRemove: function (oEvent) {
            var aRemovedTokens = oEvent.getParameter("tokens");
            var aCodes = this._reg().getProperty("/companyCodes").slice();
            var oTokenizer = this.byId("companyCodeTokenizer");

            aRemovedTokens.forEach(function (oToken) {
                var iIdx = aCodes.indexOf(oToken.getKey());
                if (iIdx > -1) { aCodes.splice(iIdx, 1); }
                oTokenizer.removeToken(oToken);
            });

            this._reg().setProperty("/companyCodes", aCodes);
            this._reg().setProperty("/approverSSO", "");
            this._loadApprovers();
            this._validateStep();
        },

        // ── Approver Loading ───────────────────────────────────────────

        _loadApprovers: function () {
            var aCodes = this._reg().getProperty("/companyCodes");
            var oSelect = this.byId("approverSelect");

            // Clear existing items except the placeholder
            oSelect.removeAllItems();
            oSelect.addItem(new Item({ key: "", text: "Select one" }));

            if (!aCodes || aCodes.length === 0) {
                this._reg().setProperty("/approverSSO", "");
                return;
            }

            var that = this;
            this._svc().getApprovers(aCodes)
                .done(function (aApprovers) {
                    if (!aApprovers || aApprovers.length === 0) {
                        MessageToast.show("No approvers found for selected company codes.");
                        return;
                    }
                    aApprovers.forEach(function (oAppr) {
                        oSelect.addItem(new Item({
                            key: oAppr.sso,
                            text: oAppr.sso + " (" + oAppr.firstname + " " + oAppr.lastname + ")"
                        }));
                    });
                })
                .fail(function () {
                    MessageBox.error("Failed to load approvers.");
                });
        },

        onApproverChange: function () {
            this._validateStep();
        },

        // ── Validation ─────────────────────────────────────────────────

        _validateStep: function () {
            this._reg().setProperty("/ui/companyApproverTouched", true);

            var aCodes = this._reg().getProperty("/companyCodes");
            var sApprover = this._reg().getProperty("/approverSSO");
            var aMissing = [];

            if (!aCodes || aCodes.length === 0) { aMissing.push("Company Code"); }
            // TODO: re-enable approver requirement
            // if (!sApprover) { aMissing.push("Approver"); }

            var bValid = aMissing.length === 0;
            this._reg().setProperty("/wizard/stepsValidated/1", bValid);
            this._missingFields = aMissing;
            return bValid;
        },

        getMissingFields: function () {
            return this._missingFields || [];
        }
    });
});
