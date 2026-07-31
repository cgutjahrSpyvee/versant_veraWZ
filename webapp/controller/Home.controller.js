sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "vsnt/vera/model/models"
], function (Controller, models) {
    "use strict";

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
