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
            this.setModel(models.createRegistrationModel(), "reg");
            this.setModel(models.createInboxModel(), "inbox");

            this._veraService = VeRAService.getInstance();

            this.getRouter().initialize();

            this._setShellTitle("VeRA — Vendor Registration");
            this._initFLPNavigation();
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
