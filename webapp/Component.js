/**
 * Component.js
 * UI5 Component root for BTP Workzone / Fiori Launchpad hosting.
 */
sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/Device",
    "sap/m/MessageBox",
    "sap/base/Log",
    "vsnt/vera/model/models",
    "vsnt/vera/model/VeRAService"
], function (UIComponent, Device, MessageBox, Log, models, VeRAService) {
    "use strict";

    return UIComponent.extend("vsnt.vera.Component", {

        metadata: {
            manifest: "json",
            interfaces: ["sap.ui.core.IAsyncContentCreation"]
        },

        init: function () {
            UIComponent.prototype.init.apply(this, arguments);

            this.setModel(models.createDeviceModel(), "device");
            this.setModel(models.createRegistrationModel(), "reg");
            this.setModel(models.createInboxModel(), "inbox");
            this.setModel(models.createRefModel(), "ref");

            this._veraService = VeRAService.getInstance();

            // Country/region lists are fetched once for the app's lifetime.
            this._pRefData = this.loadReferenceData();

            this.getRouter().initialize();

            this._setShellTitle("VeRA — Vendor Registration");
            this._initFLPNavigation();
            this._handleIntentNavigation();
            this._loadUserInfo();

            // Start fetching invites immediately so the Home list is populated
            // by the time the user has looked at the tiles.
            this.loadInvites();
        },

        getService: function () {
            return this._veraService;
        },

        /**
         * Fills the "inbox" model from wz_services?Action=inviteInfo for the
         * signed-in user. Owned by the Component rather than a controller so
         * Home and Status share one fetch and one copy of the data.
         *
         * Resolves with the mapped rows; resolves with [] on any failure,
         * having already reported it. Never rejects.
         */
        loadInvites: function () {
            var that      = this;
            var oSvc      = this._veraService;
            var oInbox    = this.getModel("inbox");
            var oDeferred = jQuery.Deferred();

            oInbox.setProperty("/busy", true);

            var fnDone = function (aItems, sError) {
                oInbox.setProperty("/busy", false);
                oInbox.setProperty("/loaded", true);
                oInbox.setProperty("/items", aItems);
                oInbox.setProperty("/open", aItems.filter(function (o) {
                    return o.status.open;
                }));
                if (sError) { MessageBox.error(sError); }
                oDeferred.resolve(aItems);
            };

            // wz_services keys the list off the signed-in user's email, which
            // arrives asynchronously from the FLP UserInfo service.
            this.getUserInfo().done(function (oUser) {
                oSvc.getInviteInfo(oUser.email)
                    .done(function (oData) {
                        if (oData && oData.code !== "0") {
                            fnDone([], oData.message || "Could not load your invitations.");
                            return;
                        }
                        var aItems;
                        try {
                            aItems = ((oData && oData.inviteData) || [])
                                .map(oSvc.mapInviteRow, oSvc);
                        } catch (e) {
                            Log.error("Component: failed to map invite data — " + e.message);
                            fnDone([], "Could not read your invitations.");
                            return;
                        }
                        fnDone(aItems);
                        that._syncRegStatus(aItems);
                    })
                    .fail(function () {
                        fnDone([], "Could not load your invitations. Please refresh.");
                    });
            });

            return oDeferred.promise();
        },

        /**
         * Fills the "ref" model with the country and region lists. Owned by the
         * Component rather than the Basic step because the step's onInit runs
         * only once — the wizard view is cached by the router — while the reg
         * model it used to write into is rebuilt on every entry.
         *
         * Resolves once both requests have settled; a failure is logged and
         * leaves that list empty rather than blocking the step.
         *
         * @returns {jQuery.Promise} resolves with no value; never rejects
         */
        loadReferenceData: function () {
            var oRef = this.getModel("ref");
            var oSvc = this._veraService;

            var pCountries = oSvc.getCountries()
                .done(function (aCountries) {
                    oRef.setProperty("/countries", aCountries || []);
                })
                .fail(function () {
                    Log.error("Component: failed to load the country list.");
                });

            var pRegions = oSvc.getAllRegionData()
                .done(function (aRegions) {
                    oRef.setProperty("/allRegions", aRegions || []);
                })
                .fail(function () {
                    Log.error("Component: failed to load the region list.");
                });

            var oDeferred = jQuery.Deferred();
            jQuery.when(pCountries, pRegions).always(function () {
                oDeferred.resolve();
            });
            return oDeferred.promise();
        },

        /**
         * Resolves once loadReferenceData has settled, so steps can filter
         * against the lists without racing the initial fetch.
         */
        getReferenceData: function () {
            return this._pRefData || jQuery.Deferred().resolve().promise();
        },

        // Drive the Status page's ObjectHeader from the most recent invite.
        _syncRegStatus: function (aItems) {
            if (!aItems.length) { return; }
            var oLatest = aItems[0];
            var oReg    = this.getModel("reg");
            oReg.setProperty("/status", oLatest.status.reg);
            if (oLatest.id) { oReg.setProperty("/requestId", oLatest.id); }
        },

        _setShellTitle: function (sTitle) {
            try {
                var oShellService = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("ShellUIService");
                if (oShellService) {
                    oShellService.setTitle(sTitle);
                }
            } catch (e) {
                /* outside FLP — ignore */
            }
        },

        _handleIntentNavigation: function () {
            try {
                var oParsed = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("URLParsing").parseShellHash(
                        window.location.hash
                    );
                var sAction = oParsed && oParsed.action;

                if (sAction === "status") {
                    this.getRouter().navTo("status");
                }
                // Every other action — including "register", which the Workzone
                // tile uses — lands on Home. The wizard is no longer a valid
                // entry point on its own: it has to be seeded from an invite,
                // so it is reachable only by opening a row in the invite list.
            } catch (e) {
                /* outside FLP — no intent routing */
            }
        },

        /**
         * Resolves with { name, email } once the FLP UserInfo service answers,
         * or with empty strings when running outside the launchpad. Never
         * rejects, so callers need no separate error path.
         */
        getUserInfo: function () {
            return this._pUserInfo ||
                jQuery.Deferred().resolve({ name: "", email: "" }).promise();
        },

        _loadUserInfo: function () {
            var that      = this;
            var oDeferred = jQuery.Deferred();
            this._pUserInfo = oDeferred.promise();

            try {
                var oUserService = sap.ushell.Container.getServiceAsync("UserInfo");
                oUserService.then(function (oService) {
                    var oUser = oService.getUser ? oService.getUser() : null;

                    // Full name — service method, then User object, then first+last.
                    var sName = (oService.getFullName && oService.getFullName()) ||
                                (oUser && oUser.getFullName && oUser.getFullName()) || "";
                    if (!sName && oUser && oUser.getFirstName) {
                        sName = [oUser.getFirstName(), oUser.getLastName && oUser.getLastName()]
                                    .filter(Boolean).join(" ");
                    }

                    // Email — service method, then User object, then attribute.
                    var sEmail = (oService.getEmail && oService.getEmail()) ||
                                 (oUser && oUser.getEmail && oUser.getEmail()) || "";

                    var oReg = that.getModel("reg");
                    oReg.setProperty("/userEmail", sEmail);
                    oReg.setProperty("/userName", sName);
                    that._seedPrimaryContact(sName, sEmail);
                    oDeferred.resolve({ name: sName, email: sEmail });
                }, function () {
                    oDeferred.resolve({ name: "", email: "" });
                });
            } catch (e) {
                /* outside FLP — no user info */
                oDeferred.resolve({ name: "", email: "" });
            }
        },

        /**
         * TEST ONLY — replaces the signed-in user's email so the invite list
         * can be pulled for somebody else without re-authenticating. Overrides
         * the cached UserInfo promise (which is what loadInvites reads), keeps
         * the reg model in step, and refetches.
         *
         * @param {string} sEmail email to impersonate
         * @returns {jQuery.Promise} resolves with the refetched invite rows
         */
        setTestUserEmail: function (sEmail) {
            var oReg  = this.getModel("reg");
            var sName = oReg.getProperty("/userName") || "";

            this._pUserInfo = jQuery.Deferred()
                .resolve({ name: sName, email: sEmail }).promise();
            oReg.setProperty("/userEmail", sEmail);

            // The primary contact was seeded from the old email; while it is
            // still the only, untouched contact, keep it pointing at the user.
            var aContacts = oReg.getProperty("/contacts/items");
            if (aContacts && aContacts.length === 1) {
                oReg.setProperty("/contacts/items/0/email", sEmail);
            }

            Log.info("Component: test user email set to " + sEmail);
            return this.loadInvites();
        },

        // Pre-populate the primary (first) contact with the current user's
        // name and email, so long as no contacts have been entered yet.
        _seedPrimaryContact: function (sName, sEmail) {
            var oReg      = this.getModel("reg");
            var aContacts = oReg.getProperty("/contacts/items");
            if (aContacts && aContacts.length === 0) {
                oReg.setProperty("/contacts/items", [{
                    name: sName, email: sEmail, phone: "", fax: "", department: ""
                }]);
            }
        },

        _initFLPNavigation: function () {
            try {
                var oShellService = sap.ushell && sap.ushell.Container &&
                    sap.ushell.Container.getService("ShellUIService");
                if (oShellService) {
                    oShellService.setHierarchy([]);
                    oShellService.setRelatedApps([]);
                }
            } catch (e) {
                /* outside FLP — ignore */
            }
        },

        crossAppNavigate: function (sSemanticObject, sAction, oParams) {
            try {
                var oCrossNav = sap.ushell.Container.getService("CrossApplicationNavigation");
                oCrossNav.toExternal({
                    target: { semanticObject: sSemanticObject, action: sAction },
                    params: oParams || {}
                });
            } catch (e) {
                this.getRouter().navTo("home");
            }
        },

        getContentDensityClass: function () {
            if (!this._sContentDensityClass) {
                this._sContentDensityClass = Device.system.desktop
                    ? "sapUiSizeCompact"
                    : "sapUiSizeCozy";
            }
            return this._sContentDensityClass;
        },

        destroy: function () {
            UIComponent.prototype.destroy.apply(this, arguments);
        }
    });
});
