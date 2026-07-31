sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/Dialog",
    "sap/m/Button",
    "sap/m/Input",
    "sap/m/Label",
    "sap/m/MessageToast",
    "vsnt/vera/model/models"
], function (Controller, Dialog, Button, Input, Label, MessageToast, models) {
    "use strict";

    var rEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return Controller.extend("vsnt.vera.controller.Home", {

        onInit: function () {
            // The Component starts the first fetch during init; refresh on every
            // return to Home so a just-submitted invite shows up.
            this.getOwnerComponent().getRouter()
                .getRoute("home").attachPatternMatched(this._onRouteMatched, this);
        },

        _onRouteMatched: function () {
            // The Component already fetched for the first render; only refetch
            // when the user comes back to Home, and never while one is in flight.
            if (!this._bFirstMatchSeen) {
                this._bFirstMatchSeen = true;
                return;
            }
            this.onRefreshInvites();
        },

        onRefreshInvites: function () {
            var oComponent = this.getOwnerComponent();
            if (!oComponent.getModel("inbox").getProperty("/busy")) {
                oComponent.loadInvites();
            }
        },

        /**
         * TEST ONLY — prompts for an email address and reloads the invite list
         * as if that user were signed in.
         */
        onChangeTestEmail: function () {
            var that    = this;
            var oBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();

            if (this._oTestEmailDialog) {
                this._oTestEmailDialog.destroy();
            }

            var oInput = new Input({
                value: this.getOwnerComponent().getModel("reg").getProperty("/userEmail"),
                type: "Email",
                width: "100%",
                liveChange: function () { oInput.setValueState("None"); }
            });

            var fnApply = function () {
                var sEmail = (oInput.getValue() || "").trim();
                if (!rEmail.test(sEmail)) {
                    oInput.setValueState("Error");
                    oInput.setValueStateText(oBundle.getText("testEmailInvalid"));
                    return;
                }
                that._oTestEmailDialog.close();
                that.getOwnerComponent().setTestUserEmail(sEmail);
                MessageToast.show(sEmail);
            };

            oInput.attachSubmit(fnApply);   // Enter in the field applies

            this._oTestEmailDialog = new Dialog({
                title: oBundle.getText("testEmailTitle"),
                contentWidth: "22rem",
                content: [
                    new Label({ text: oBundle.getText("testEmailLabel"), labelFor: oInput })
                        .addStyleClass("sapUiTinyMarginBegin sapUiTinyMarginTop"),
                    oInput
                ],
                beginButton: new Button({
                    text: oBundle.getText("btnOk"),
                    type: "Emphasized",
                    press: fnApply
                }),
                endButton: new Button({
                    text: oBundle.getText("btnCancel"),
                    press: function () { that._oTestEmailDialog.close(); }
                }),
                afterClose: function () {
                    that._oTestEmailDialog.destroy();
                    that._oTestEmailDialog = null;
                }
            }).addStyleClass(this.getOwnerComponent().getContentDensityClass());

            this.getView().addDependent(this._oTestEmailDialog);
            this._oTestEmailDialog.open();
        },

        /**
         * An invitation is the only way into the registration wizard, so
         * opening a row seeds a fresh registration from that invite and
         * navigates into the form.
         */
        onInvitePress: function (oEvent) {
            var oComponent = this.getOwnerComponent();
            var oRow       = oEvent.getParameter("listItem").getBindingContext("inbox").getObject();
            var sEmail     = oComponent.getModel("reg").getProperty("/userEmail");

            oComponent.getModel("reg").setData(
                models.createRegistrationModelFromInvite(oRow.invite, sEmail).getData()
            );
            oComponent.getRouter().navTo("register", { mode: "register" });
        }
    });
});
