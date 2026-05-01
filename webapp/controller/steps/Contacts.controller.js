sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("vsnt.vera.controller.steps.Contacts", {

        onInit: function () {},

        _reg: function () { return this.getOwnerComponent().getModel("reg"); },

        onAddContact: function () {
            var aContacts = this._reg().getProperty("/contacts/items").slice();
            aContacts.push({
                name: "", email: "", phone: "", fax: "", department: ""
            });
            this._reg().setProperty("/contacts/items", aContacts);
            this._validateStep();
        },

        onRemoveContact: function (oEvent) {
            var oCtx      = oEvent.getSource().getBindingContext("reg");
            var iIdx      = parseInt(oCtx.getPath().split("/").pop(), 10);
            var aContacts = this._reg().getProperty("/contacts/items").slice();
            aContacts.splice(iIdx, 1);
            this._reg().setProperty("/contacts/items", aContacts);
            this._validateStep();
        },

        onContactFieldChange: function () { this._validateStep(); },

        _validateStep: function () {
            var aContacts = this._reg().getProperty("/contacts/items");
            var bValid = aContacts.length > 0 &&
                aContacts.every(function (c) {
                    return !!c.name && !!c.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(c.email);
                });
            this._reg().setProperty("/wizard/stepsValidated/4", bValid);
            return bValid;
        }
    });
});
