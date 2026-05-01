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
            var that   = this;
            var oInbox = this.getOwnerComponent().getModel("inbox");
            oInbox.setProperty("/busy", true);

            this._svc().getInbox()
                .done(function (aItems) {
                    oInbox.setProperty("/items", aItems || []);
                    if (aItems && aItems.length > 0) {
                        var oLatest = aItems[0];
                        var oReg    = that.getOwnerComponent().getModel("reg");
                        oReg.setProperty("/status",
                            that._mapPortalStatus(oLatest.status && oLatest.status.text));
                        if (oLatest.id) { oReg.setProperty("/requestId", oLatest.id); }
                    }
                })
                .fail(function () {
                    MessageBox.error("Could not load registration status. Please refresh.");
                })
                .always(function () {
                    oInbox.setProperty("/busy", false);
                });
        },

        _mapPortalStatus: function (sText) {
            if (!sText) { return "DRAFT"; }
            var s = sText.toLowerCase();
            if (s.indexOf("approv") >= 0 || s === "completed") { return "APPROVED"; }
            if (s.indexOf("reject") >= 0 || s === "failed")    { return "REJECTED"; }
            if (s === "pending submission")                    { return "DRAFT"; }
            return "PENDING";
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
