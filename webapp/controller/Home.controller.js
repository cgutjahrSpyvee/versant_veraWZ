sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/Dialog",
    "sap/m/Button",
    "sap/m/Input",
    "sap/m/Label",
    "sap/m/MessageToast",
    "sap/m/Text",
    "sap/ui/layout/form/SimpleForm",
    "vsnt/vera/model/models",
    "vsnt/vera/model/appInfo"
], function (Controller, Dialog, Button, Input, Label, MessageToast, Text,
             SimpleForm, models, appInfo) {
    "use strict";

    var rEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return Controller.extend("vsnt.vera.controller.Home", {

        onInit: function () {
            // The Component starts the first fetch during init; refresh on every
            // return to Home so a just-submitted invite shows up.
            this.getOwnerComponent().getRouter()
                .getRoute("home").attachPatternMatched(this._onRouteMatched, this);

            this._showVersionOnButton();
        },

        // The version label is the whole point of the footer button, so show it
        // as soon as it is known and again once the build stamp arrives.
        _showVersionOnButton: function () {
            var that     = this;
            var oButton  = this.byId("appVersionButton");
            var oAppInfo = appInfo.get(this.getOwnerComponent());

            oButton.setText("v" + (oAppInfo.version || "?"));
            appInfo.loadBuildTime().done(function () {
                oButton.setTooltip(appInfo.format(that._appInfo()));
            });
        },

        _appInfo: function () {
            return appInfo.get(this.getOwnerComponent());
        },

        /**
         * Full build identity. The summary sits in a text field rather than
         * behind a Copy button because the Clipboard API is off limits here
         * (@sap-ux/fiori-tools/sap-no-navigator) — a selectable field lets the
         * user copy it with the keyboard instead of reading a token out
         * character by character.
         */
        onShowAppInfo: function () {
            var that     = this;
            var oBundle  = this.getOwnerComponent().getModel("i18n").getResourceBundle();
            var oAppInfo = this._appInfo();

            if (this._oAppInfoDialog) { this._oAppInfoDialog.destroy(); }

            var aRows = [
                [oBundle.getText("aboutVersion"),   oAppInfo.version    || "—"],
                [oBundle.getText("aboutBuild"),     oAppInfo.buildToken || "—"],
                [oBundle.getText("aboutBuiltAt"),   oAppInfo.buildTime  || "—"],
                [oBundle.getText("aboutAppPath"),   oAppInfo.appSegment || "—"]
            ];

            var aContent = [];
            aRows.forEach(function (aRow) {
                aContent.push(new Label({ text: aRow[0] }));
                aContent.push(new Text({ text: aRow[1] })
                    .addStyleClass("sapUiTinyMarginBottom"));
            });

            aContent.push(new Label({ text: oBundle.getText("aboutReportThis") }));
            aContent.push(new Input({
                value: appInfo.format(oAppInfo),
                width: "100%",
                // Left editable so the text can be selected and copied; the
                // dialog is thrown away on close, so edits go nowhere.
                tooltip: oBundle.getText("aboutReportThis")
            }));

            this._oAppInfoDialog = new Dialog({
                title: oBundle.getText("aboutTitle"),
                contentWidth: "24rem",
                content: [
                    new SimpleForm({
                        editable: true,
                        layout: "ResponsiveGridLayout",
                        labelSpanXL: 4, labelSpanL: 4, labelSpanM: 4, labelSpanS: 4,
                        content: aContent
                    })
                ],
                endButton: new Button({
                    text: oBundle.getText("btnClose"),
                    press: function () { that._oAppInfoDialog.close(); }
                }),
                afterClose: function () {
                    that._oAppInfoDialog.destroy();
                    that._oAppInfoDialog = null;
                }
            }).addStyleClass(this.getOwnerComponent().getContentDensityClass());

            this.getView().addDependent(this._oAppInfoDialog);
            this._oAppInfoDialog.open();
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
                models.createRegistrationModelFromInvite(
                    oRow.invite, sEmail, oRow.reqId
                ).getData()
            );
            oComponent.getRouter().navTo("register", { mode: "register" });
        }
    });
});
