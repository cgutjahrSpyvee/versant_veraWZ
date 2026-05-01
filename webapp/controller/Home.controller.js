sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "vsnt/vera/model/models"
], function (Controller, models) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.Home", {

        onInit: function () {},

        onNavStatus: function () {
            this.getOwnerComponent().getRouter().navTo("status");
        },

        onNavRegister: function () {
            this.getOwnerComponent().getModel("reg")
                .setData(models.createRegistrationModel().getData());
            this.getOwnerComponent().getRouter().navTo("register", { mode: "register" });
        },

        onNavMaintain: function () {
            this.getOwnerComponent().getRouter().navTo("register", { mode: "maintain" });
        }
    });
});
