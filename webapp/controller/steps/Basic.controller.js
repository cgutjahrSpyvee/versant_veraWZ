sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Basic", {

        onInit: function () {
            var that = this;

            // The country and region lists belong to the Component (see
            // Component.loadReferenceData) — this step only narrows the region
            // list to whichever country is selected.
            this.getOwnerComponent().getReferenceData().done(function () {
                that._filterRegionsByCountry();
            });

            // onInit runs once — the router caches the wizard view — but the reg
            // model is rebuilt from the invite on every entry, so watch the
            // country property rather than filtering only at startup.
            this._oCountryBinding = this._reg().bindProperty("/basic/primaryAddress/country");
            this._oCountryBinding.attachChange(this._filterRegionsByCountry, this);

            // The property binding only fires when the country actually
            // changes, so re-entering the wizard with the same country as last
            // time would leave the region list untouched. Recomputing per entry
            // is free, and it is what Banking does with its own rules.
            this.getOwnerComponent().getRouter().getRoute("register")
                .attachPatternMatched(this._filterRegionsByCountry, this);
        },

        onExit: function () {
            if (this._oCountryBinding) {
                this._oCountryBinding.detachChange(this._filterRegionsByCountry, this);
                this._oCountryBinding.destroy();
                this._oCountryBinding = null;
            }
            this.getOwnerComponent().getRouter().getRoute("register")
                .detachPatternMatched(this._filterRegionsByCountry, this);
        },

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },
        _ref: function () { return this.getOwnerComponent().getModel("ref"); },

        onCountryChange: function (oEvent) {
            // Whatever state was picked belongs to the previous country.
            this._reg().setProperty("/basic/primaryAddress/state", "");
            this._filterRegionsByCountry();
            this._validateStep();
        },

        _filterRegionsByCountry: function () {
            var sCountry    = this._reg().getProperty("/basic/primaryAddress/country");
            var aAllRegions = this._ref().getProperty("/allRegions") || [];

            this._ref().setProperty("/regions", sCountry
                ? aAllRegions.filter(function (oRegion) {
                    return oRegion.country === sCountry;
                })
                : []);
        },

        onBasicFieldChange: function () { this._validateStep(); },

        onVendorTypeChange: function (oEvent) {
            var sVendorType = oEvent.getParameter("selectedItem").getKey();
            this._reg().setProperty("/vendorType", sVendorType);
            this._validateStep();
        },

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
            // Nothing to validate on a read-only request — see Banking.
            if (!this._reg().getProperty("/ui/editable")) { return true; }

            var oBasic = this._reg().getProperty("/basic");
            var oAddr  = oBasic.primaryAddress;
            var sVendorType = this._reg().getProperty("/vendorType");

            var aMissing = [];
            if (!oBasic.legalName) { aMissing.push("Legal Name"); }
            // TODO: re-enable when Vendor Type is restored
            // if (!sVendorType)      { aMissing.push("Vendor Type"); }
            if (!oAddr.country)    { aMissing.push("Country"); }
            if (!oAddr.address1)   { aMissing.push("Address Line 1"); }
            if (!oAddr.city)       { aMissing.push("City"); }
            if (!oAddr.state)      { aMissing.push("State"); }
            if (!oAddr.zip)        { aMissing.push("Zip Code"); }
            if (oBasic.acceptPO && !oBasic.poEmail) { aMissing.push("PO Email Address"); }

            var bValid = aMissing.length === 0;
            this._reg().setProperty("/wizard/stepsValidated/0", bValid);
            this._missingFields = aMissing;
            return bValid;
        },

        getMissingFields: function () {
            return this._missingFields || [];
        }
    });
});
