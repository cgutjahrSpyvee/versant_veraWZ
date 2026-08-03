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
            // TODO: drop the two pre-validated steps when Company/Approver and
            // Payment Terms are re-enabled. They live here rather than in
            // Component.init so that rebuilding the model — which every entry
            // into the wizard does — cannot silently lose them.
            return new JSONModel({
                mode:          "register",
                requestId:     null,
                inviteId:      null,
                vendorId:      null,
                requestType:   "1",
                documentType:  "",
                vendorType:    "",
                userType:      "",
                companyCodes:  [],
                approverSSO:   "",
                userEmail:     "",
                userName:      "",
                comments:      "",
                annualSpend:   "",
                requestedFor:  "",
                status:        "DRAFT",

                ui: {
                    busy:                       false,
                    paymentMethods:             [],    // [{key,text}] allowed for this vendor type
                    paymentMethodLocked:        false, // vendor type offers no choice
                    bankCountryEditable:        true,
                    newNotificationEmail:       "",
                    notificationEmailError:     false,
                    basicTouched:               false,
                    taxTouched:                 false,
                    paymentTermsTouched:        false,
                    bankingTouched:             false,
                    contactsTouched:            false,
                    companyApproverTouched:      false
                },

                wizard: {
                    //             Basic  Company  Tax    Terms  Banking  Contacts
                    stepsValidated: [false, true,  false, true,  false,   false]
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
                        bvtyp:         "",   // BVTYP of an existing bank record, if any

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

        /**
         * A fresh registration model pre-filled from the invitation the user
         * clicked. Field pairings follow the portal's maintain_invite.java,
         * which reads the same inviteData record.
         *
         * @param {object} oInv       raw inviteData row
         * @param {string} sEmail     signed-in user's email — the invite is keyed
         *                            on it, so it doubles as the contact address
         * @param {string} sRequestId REQST for this invite. Passed in rather than
         *                            read off oInv because it lives on the
         *                            response's vadminData records, not on the
         *                            invite row — VeRAService.mapInvites joins them.
         */
        createRegistrationModelFromInvite: function (oInv, sEmail, sRequestId) {
            var oModel = this.createRegistrationModel();
            var oData  = oModel.getData();

            oData.mode         = "register";
            oData.requestId    = sRequestId           || null;
            oData.inviteId     = oInv.ZZSF_VRA_EMLID  || null;
            oData.vendorId     = oInv.LIFNR           || null;
            oData.vendorType   = oInv.VEND_TYPE       || "";
            oData.userType     = oInv.ZZSF_VRA_VENDCAT || "";
            oData.approverSSO  = oInv.APPROVER_SSO    || "";
            oData.annualSpend  = oInv.ANNUAL_SPEND    || "";
            oData.comments     = oInv.INVCOMMENT      || "";
            oData.requestedFor = oInv.REQUESTED_FOR   || "";
            oData.userEmail    = sEmail || "";

            oData.basic.legalName               = oInv.VEND_NAME || "";
            oData.basic.primaryAddress.country  = oInv.LAND1     || "";
            oData.paymentTerms.selected         = oInv.ZTERM     || "";

            oData.contacts.items = [{
                name:       [oInv.FIRST_NAME, oInv.LAST_NAME].filter(Boolean).join(" "),
                email:      sEmail || "",
                phone:      oInv.TELEPHONE || "",
                fax:        "",
                department: ""
            }];

            oModel.setData(oData);
            return oModel;
        },

        /**
         * Reference data shared by every step. Deliberately its own model
         * rather than a branch of "reg": entering the wizard replaces the reg
         * model wholesale (see createRegistrationModelFromInvite), which would
         * wipe these lists and leave the Basic step's dropdowns empty on every
         * entry after the first.
         */
        createRefModel: function () {
            return new JSONModel({
                countries:  [],   // [{ country, description }]
                allRegions: [],   // [{ country, region, description }] — every country
                regions:    []    // subset of allRegions for the selected country
            });
        },

        createInboxModel: function () {
            return new JSONModel({
                items:  [],   // every invite returned for the user
                open:   [],   // subset still in flight — what Home shows
                busy:   false,
                loaded: false
            });
        }
    };
});
