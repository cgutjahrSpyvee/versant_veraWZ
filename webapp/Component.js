/**
 * Component.js
 * UI5 Component root for BTP Workzone / Fiori Launchpad hosting.
 */
sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/Device",
    "vsnt/vera/model/models",
    "vsnt/vera/model/VeRAService"
], function (UIComponent, Device, models, VeRAService) {
    "use strict";

    return UIComponent.extend("vsnt.vera.Component", {

        metadata: {
            manifest: "json",
            interfaces: ["sap.ui.core.IAsyncContentCreation"]
        },

        init: function () {
            UIComponent.prototype.init.apply(this, arguments);

            this.setModel(models.createDeviceModel(), "device");
            var oRegModel = models.createRegistrationModel();
            // TODO: remove these when steps are re-enabled
            oRegModel.setProperty("/wizard/stepsValidated/1", true);  // Company/Approver
            oRegModel.setProperty("/wizard/stepsValidated/3", true);  // Payment Terms
            this.setModel(oRegModel, "reg");
            this.setModel(models.createInboxModel(), "inbox");

            this._veraService = VeRAService.getInstance();

            this.getRouter().initialize();

            this._setShellTitle("VeRA — Vendor Registration");
            this._initFLPNavigation();
            this._handleIntentNavigation();
            this._loadUserEmail();
        },

        getService: function () {
            return this._veraService;
        },

        _setShellTitle: function (sTitle) {
            try {
                var oShellService = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("ShellUIService");
                if (oShellService) {
                    oShellService.setTitle(sTitle);
                }
            } catch (e) {
                /* outside FLP — ignore */
            }
        },

        _handleIntentNavigation: function () {
            try {
                var oComponentData = this.getComponentData();
                var oStartupParams = oComponentData && oComponentData.startupParameters || {};
                var oParsed = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("URLParsing").parseShellHash(
                        window.location.hash
                    );
                var sAction = oParsed && oParsed.action;

                if (sAction === "register") {
                    var sMode = oStartupParams.mode ? oStartupParams.mode[0] : "register";
                    this.getRouter().navTo("register", { mode: sMode });
                } else if (sAction === "status") {
                    this.getRouter().navTo("status");
                }
                // "maintain" and other actions stay on home for now
            } catch (e) {
                /* outside FLP — no intent routing */
            }
        },

        _loadUserEmail: function () {
            var that = this;
            try {
                var oUserService = sap.ushell.Container.getServiceAsync("UserInfo");
                oUserService.then(function (oService) {
                    var sEmail = oService.getEmail();
                    that.getModel("reg").setProperty("/userEmail", sEmail || "");
                });
            } catch (e) {
                /* outside FLP — no user info */
            }
        },

        _initFLPNavigation: function () {
            try {
                var oShellService = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("ShellUIService");
                if (oShellService) {
                    oShellService.setHierarchy([]);
                    oShellService.setRelatedApps([]);
                }
            } catch (e) {
                /* outside FLP — ignore */
            }
        },

        crossAppNavigate: function (sSemanticObject, sAction, oParams) {
            try {
                var oCrossNav = sap.ushell.Container.getService("CrossApplicationNavigation");
                oCrossNav.toExternal({
                    target: { semanticObject: sSemanticObject, action: sAction },
                    params: oParams || {}
                });
            } catch (e) {
                this.getRouter().navTo("home");
            }
        },

        getContentDensityClass: function () {
            if (!this._sContentDensityClass) {
                this._sContentDensityClass = Device.system.desktop
                    ? "sapUiSizeCompact"
                    : "sapUiSizeCozy";
            }
            return this._sContentDensityClass;
        },

        destroy: function () {
            UIComponent.prototype.destroy.apply(this, arguments);
        }
    });
});
