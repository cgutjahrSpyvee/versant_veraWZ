sap.ui.define([
    "sap/ui/model/json/JSONModel",
    "sap/ui/Device"
], function (JSONModel, Device) {
    "use strict";

    return {

        createDeviceModel: function () {
            var oModel = new JSONModel(Device);
            oModel.setDefaultBindingMode("OneWay");
            return oModel;
        },

        createRegistrationModel: function () {
            return new JSONModel({
                mode:          "register",
                requestId:     null,
                vendorId:      null,
                requestType:   "1",
                documentType:  "",
                vendorType:    "",
                userType:      "",
                companyCodes:  [],
                approverSSO:   "",
                comments:      "",
                annualSpend:   "",
                requestedFor:  "",
                status:        "DRAFT",

                ui: {
                    busy:                       false,
                    countries:                  [],
                    regions:                    [],
                    newNotificationEmail:       "",
                    notificationEmailError:     false,
                    basicTouched:               false,
                    taxTouched:                 false,
                    paymentTermsTouched:        false,
                    bankingTouched:             false,
                    contactsTouched:            false
                },

                wizard: {
                    stepsValidated: [false, false, false, false, false]
                },

                basic: {
                    legalName:           "",
                    invoicingName:       "",
                    acceptPO:            false,
                    poEmail:             "",
                    primaryAddress: {
                        country:          "",
                        address1:         "",
                        address2:         "",
                        address3:         "",
                        city:             "",
                        state:            "",
                        zip:              "",
                        taxJurisdiction:  ""
                    },
                    secondaryAddresses: []
                },

                tax: {
                    entityType:           "Entity",
                    isUSPerson:           true,
                    w9FileName:           "",
                    w9DocId:              "",
                    doc590Name:           "",
                    doc590Id:             "",
                    w8DocId:              "",
                    legalDocId:           "",
                    supportDocId:         "",
                    taxCategory:          "TaxID",
                    taxIdNumber:          "",
                    ssnNumber:            "",
                    recipientType:        "",
                    exemptPayeeCode:      "",
                    factaCode:            "",
                    independentContractor:""
                },

                paymentTerms: {
                    availableTerms: [],
                    selected:       ""
                },

                banking: {
                    primaryAccount: {
                        method:        "ACH",
                        country:       "US",
                        routingNum:    "",
                        accountNum:    "",
                        holderName:    "",
                        swiftNum:      "",
                        ibanNum:       "",
                        bankFileName:  "",
                        bankDocId:     ""
                    },
                    secondaryAccounts:    [],
                    paymentNotifications: []
                },

                contacts: {
                    items: []
                }
            });
        },

        createInboxModel: function () {
            return new JSONModel({
                items: [],
                busy:  false
            });
        }
    };
});
