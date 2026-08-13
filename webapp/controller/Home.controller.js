sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/Dialog",
    "sap/m/Button",
    "sap/m/Input",
    "sap/m/Label",
    "sap/m/MessageBox",
    "sap/m/MessageToast",
    "sap/m/Text",
    "sap/ui/layout/form/SimpleForm",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
    "vsnt/vera/model/appInfo"
], function (Controller, Dialog, Button, Input, Label, MessageBox, MessageToast, Text,
             SimpleForm, Filter, FilterOperator, appInfo) {
    "use strict";

    var rEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    return Controller.extend("vsnt.vera.controller.Home", {

        onInit: function () {
            var oRouter = this.getOwnerComponent().getRouter();

            // The Component starts the first fetch during init; refresh on every
            // return to Home so a just-submitted invite shows up.
            //
            // "status" is an alias for this page, kept so the #VeRA-status
            // intent and the post-submit redirect still resolve. It differs
            // only in landing on the All filter.
            oRouter.getRoute("home").attachPatternMatched(this._onRouteMatched, this);
            oRouter.getRoute("status").attachPatternMatched(this._onRouteMatched, this);

            this._applyInviteFilter();
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

        _onRouteMatched: function (oEvent) {
            // Arriving on the status alias means "show me everything" — that
            // entry point has always listed the settled requests too.
            if (oEvent.getParameter("name") === "status") {
                this._inbox().setProperty("/filter", "all");
            }
            this._applyInviteFilter();

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

        onInviteFilterChange: function () { this._applyInviteFilter(); },

        _inbox: function () { return this.getOwnerComponent().getModel("inbox"); },

        /**
         * Narrows the list to the open requests, or shows the lot.
         *
         * status/open is set per status code in VeRAService's lookup tables and
         * is false only for the settled end states — completed, cancelled and
         * registered. Filtering here rather than keeping a second pre-filtered
         * array means one copy of the data and one place the rule lives.
         */
        _applyInviteFilter: function () {
            var oTable   = this.byId("invitesTable");
            var oBinding = oTable && oTable.getBinding("items");
            if (!oBinding) { return; }

            // Safe to set before the rows arrive — a client binding re-applies
            // its filters whenever the model data changes.
            oBinding.filter(this._inbox().getProperty("/filter") === "all"
                ? []
                : [new Filter("status/open", FilterOperator.EQ, true)]);
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
         * opening a row seeds a registration from that invite and navigates
         * into the form.
         *
         * Which form depends on whether a request already sits behind the
         * invite's REQST — VeRAService.resolveInviteTarget put that on the row
         * as `mode`: "register" (empty form), "edit" or "display". For the
         * latter two the request's own data is fetched first; the Component
         * owns that, and the navigation waits for it.
         */
        onInvitePress: function (oEvent) {
            var that       = this;
            var oComponent = this.getOwnerComponent();

            // The fetch shows itself as inbox busy, which blanks the table.
            // Guard rather than queue a second one.
            if (oComponent.getModel("inbox").getProperty("/busy")) { return; }

            var oRow = oEvent.getParameter("listItem").getBindingContext("inbox").getObject();

            oComponent.seedRegistrationFromInvite(oRow).done(function (oResult) {
                if (!oResult.ok) {
                    // Staying put beats opening a read-only form with nothing
                    // but the invite's few fields in it — that is
                    // indistinguishable from a request that really is empty.
                    // oResult.diag is present only for a technical failure —
                    // its headline names the cause on screen, its details go in
                    // the dialog's "Show details" section for a ticket.
                    var oDiag = oResult.diag;
                    MessageBox.error(
                        (oResult.message || that._i18n("requestLoadErrorText")) +
                            (oDiag ? "\n\n" + oDiag.headline : ""),
                        {
                            title:   that._i18n("requestLoadErrorTitle"),
                            details: oDiag ? oDiag.details : undefined,
                            contentWidth: "35rem"
                        }
                    );
                    return;
                }
                oComponent.getRouter().navTo("register", { mode: oResult.mode });
            });
        },

        _i18n: function (sKey) {
            return this.getView().getModel("i18n").getResourceBundle().getText(sKey);
        }
    });
});
