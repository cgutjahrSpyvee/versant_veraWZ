sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Basic", {

        onInit: function () {
            // Load reference data on first activation
            var that = this;
            this.getOwnerComponent().getService().getCountries()
                .done(function (aCountries) {
                    that._reg().setProperty("/ui/countries", aCountries || []);
                });
            this.getOwnerComponent().getService().getRegions()
                .done(function (aRegions) {
                    that._reg().setProperty("/ui/regions", aRegions || []);
                });
        },

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onBasicFieldChange: function () { this._validateStep(); },

        onAcceptPOChange: function (oEvent) {
            var bAccept = oEvent.getParameter("selected");
            if (!bAccept) {
                this._reg().setProperty("/basic/poEmail", "");
            }
            this._validateStep();
        },

        onZipChange: function () {
            var oAddr = this._reg().getProperty("/basic/primaryAddress");
            if (!oAddr.zip || !oAddr.country) { return; }
            var that = this;
            this._svc().validateCityState(oAddr)
                .done(function (oResult) {
                    if (oResult && oResult.taxJurisdiction) {
                        that._reg().setProperty("/basic/primaryAddress/taxJurisdiction",
                            oResult.taxJurisdiction);
                    }
                });
        },

        onAddSecondaryAddress: function () {
            var sType = this.byId("secondaryAddressType").getSelectedKey();
            if (!sType) { return; }
            var aAddrs = this._reg().getProperty("/basic/secondaryAddresses").slice();
            aAddrs.push({
                type:     sType,
                country:  "US",
                address1: "", address2: "", address3: "",
                city: "", state: "", zip: ""
            });
            this._reg().setProperty("/basic/secondaryAddresses", aAddrs);
        },

        onRemoveSecondaryAddress: function (oEvent) {
            var oCtx   = oEvent.getSource().getBindingContext("reg");
            var sPath  = oCtx.getPath();
            var iIdx   = parseInt(sPath.split("/").pop(), 10);
            var aAddrs = this._reg().getProperty("/basic/secondaryAddresses").slice();
            aAddrs.splice(iIdx, 1);
            this._reg().setProperty("/basic/secondaryAddresses", aAddrs);
        },

        _validateStep: function () {
            var oBasic = this._reg().getProperty("/basic");
            var oAddr  = oBasic.primaryAddress;
            var bValid =
                !!oBasic.legalName &&
                !!oAddr.country && !!oAddr.address1 &&
                !!oAddr.city && !!oAddr.state && !!oAddr.zip &&
                (!oBasic.acceptPO || !!oBasic.poEmail);
            this._reg().setProperty("/wizard/stepsValidated/0", bValid);
            return bValid;
        }
    });
});
