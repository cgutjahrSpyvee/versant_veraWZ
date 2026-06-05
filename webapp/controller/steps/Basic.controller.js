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
                })
                .fail(function () {
                    sap.base.Log.error("VeRA: Failed to load countries data");
                });

            // Load all region data for filtering
            this.getOwnerComponent().getService().getAllRegionData()
                .done(function (aAllRegions) {
                    that._reg().setProperty("/ui/allRegions", aAllRegions || []);
                    // Initialize regions based on current country selection
                    that._filterRegionsByCountry();
                })
                .fail(function () {
                    sap.base.Log.error("VeRA: Failed to load region data");
                });
        },

        _svc: function () { return this.getOwnerComponent().getService(); },
        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onCountryChange: function (oEvent) {
            // Clear the state selection when country changes
            this._reg().setProperty("/basic/primaryAddress/state", "");
            this._filterRegionsByCountry();
            this._validateStep();
        },

        _filterRegionsByCountry: function () {
            var sCountry = this._reg().getProperty("/basic/primaryAddress/country");
            var aAllRegions = this._reg().getProperty("/ui/allRegions") || [];
            
            if (sCountry) {
                var aFilteredRegions = aAllRegions.filter(function (oRegion) {
                    return oRegion.country === sCountry;
                });
                this._reg().setProperty("/ui/regions", aFilteredRegions);
            } else {
                this._reg().setProperty("/ui/regions", []);
            }
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
