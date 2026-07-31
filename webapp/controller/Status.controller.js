sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageBox",
    "sap/m/MessageToast"
], function (Controller, MessageBox, MessageToast) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.Status", {

        onInit: function () {
            var oRouter = this.getOwnerComponent().getRouter();
            oRouter.getRoute("status").attachPatternMatched(this._onRouteMatched, this);
        },

        _svc: function () { return this.getOwnerComponent().getService(); },

        _onRouteMatched: function () { this._loadInbox(); },

        _loadInbox: function () {
            return this.getOwnerComponent().loadInvites();
        },

        onCancelRequest: function (oEvent) {
            var sId  = this._getRequestIdFromEvent(oEvent);
            var that = this;
            MessageBox.confirm("Cancel registration request " + sId + "?", {
                onClose: function (sAction) {
                    if (sAction === MessageBox.Action.OK) {
                        that._svc().requestAction(sId, "C")
                            .done(function (oResult) {
                                if (oResult && oResult.code === "0") {
                                    MessageToast.show("Request cancelled.");
                                    that._loadInbox();
                                } else {
                                    MessageBox.error(oResult ? oResult.message : "Cancel failed.");
                                }
                            });
                    }
                }
            });
        },

        onResendRequest: function (oEvent) {
            var sId = this._getRequestIdFromEvent(oEvent);
            this._svc().requestAction(sId, "R")
                .done(function (oResult) {
                    if (oResult && oResult.code === "0") {
                        MessageToast.show("Resent successfully.");
                    } else {
                        MessageBox.error(oResult ? oResult.message : "Resend failed.");
                    }
                });
        },

        _getRequestIdFromEvent: function (oEvent) {
            var oButton = oEvent.getSource();
            var aData   = oButton.getCustomData() || [];
            for (var i = 0; i < aData.length; i++) {
                if (aData[i].getKey() === "requestId") { return aData[i].getValue(); }
            }
            return "";
        },

        onNavHome:     function () { this.getOwnerComponent().getRouter().navTo("home"); },
        onNavRegister: function () { this.getOwnerComponent().getRouter().navTo("register", { mode: "register" }); },
        onNavMaintain: function () { this.getOwnerComponent().getRouter().navTo("register", { mode: "maintain" }); }
    });
});
