/*global $, console, moment, app, Backbone, window, _, alert */
$(document).ready(function () {
  
});

(function (global) {
	
    "use strict";

    /**
     * SearchVendorModalView: Renders search results in modal.
     * Events Raised: showSuccessMessage, showVendorInput
     */
    app.views.SearchVendorModalView = Backbone.View.extend({
        el: "#vendor-search-modal",
        rowTemplate: $("#vendorSearchTemplate")[0]?_.template($("#vendorSearchTemplate").html()):false,
        events: {
            "click .btn-success-invite": "showNewVendor",
            "click .btn-success-reg": "showNewVendorReg", // By Naga
            "click .btn-success-maintain": "maintainVendor", // By Naga
            "click .btn-danger": "vendorExists",
            "change .pageSize": "configurePagination"
        },
      
        initialize: function () {
            _.bindAll(this, "render", "show", "showNewVendor", "vendorExists", "configurePagination");

            this.paginationView = new app.views.PaginationView({ el: this.$(".pagination"), items: this.$("tbody > tr") });
        },
        
        configurePagination: function () {
            this.paginationView.configurePagination(this.$("tbody > tr"), this.$(".pageSize").val());
        },
        render: function () {
            var that = this,
                table = this.$("table"),
                noResults = this.$("div.alert.alert-info"),
                tbody = this.$("tbody"),
                resultsText;
            tbody.empty();

            if (this.collection.length === 1) {
                resultsText = "1 vendor found";
            } else {
                resultsText = this.collection.length + " vendors found";
            }

            this.$(".modal-header h3").text(resultsText);
            
            if (this.collection.length === 0) {
                table.hide();
                noResults.show();
            } else {
                table.show();
                noResults.hide();
                _.each(this.collection, function (itm) {
                    tbody.append(that.rowTemplate(itm));
                });
            }

            return this;
        },

        show: function () {
            this.$el.modal("show");
            return this;
        },

        setData: function (data) {
            this.collection = data || [];
            this.render();
            this.configurePagination();
            return this;
        },
        showNewVendor: function () {
            this.$el.trigger("showInvitationForm");
            this.$el.trigger("showSuccessMessage", ["Invite a new Vendor"]);
        },
        
        // Begin of Insert by Naga
        showNewVendorReg: function () {
            this.$el.trigger("showInvitationForm");
            this.$el.trigger("showSuccessMessage", ["Register a new Vendor"]);
        },        
        // End of Insert by Naga

        // Begin of Insert by Naga
        maintainVendor: function (e) {
        	var vendorNumber = e.currentTarget.id;
        	var vendorType;
//        	var vendorDetails = this.collection.get({"vendorNum":vendorNumber});
//        	var vendorDetails = this.collection.findWhere({id:vendorNumber});
            _.each(this.collection, function (itm) {
                if(itm.vendorNum==vendorNumber){
                	vendorType = itm.vendorType;
                }
            });
//        	var vendorDetails = this.collection.where({"vendorNum":vendorNumber});
//        	var vendorType = "010";
            this.$el.trigger("showMaintainForm",[vendorNumber,vendorType]);
//        	this.$el.trigger("showInvitationForm");
//            this.$el.trigger("showSuccessMessage", ["Register a new Vendor"]);
        },        
        // End of Insert by Naga
        
        vendorExists: function () {
            this.$el.trigger("showSuccessMessage", ["Nothing else is required, the vendor exists"]);
        }

    });

    /**
     * FindCompanyModalView: Responsible for searching for variaus company codes
     * Events:
     *    showSpinner
     *    hideSpinner
     */
    app.views.FindCompanyCodeModalView = Backbone.View.extend({
        el: "#searchResults",
        events: {
            "click #search-codes": "search",
            "click #add-codes": "addCodes",
            "click a[data-code]": "addCode"
        },
        multipleSelectTemplate: $("#codeSearchTemplateMultiple")[0]?_.template($("#codeSearchTemplateMultiple").html()):false,
        singleSelectTemplate: $("#codeSearchTemplateSingle")[0]?_.template($("#codeSearchTemplateSingle").html()):false,
        
        collection: [],
        initialize: function () {
            _.bindAll(this, "show", "search", "hide", "addCodes", "addCode");
            this.restrictCompanyCode = this.options.restrictCompanyCode;
        },
        
        render: function () {
            var table = this.$("table"),
                noResult = this.$("div.alert.alert-info"),
                tbody = this.$("tbody"),
                template = this.restrictCompanyCode ? this.singleSelectTemplate : this.multipleSelectTemplate;

            if (this.restrictCompanyCode) {
                table.find("#actions").hide();
            } else {
                table.find("#actions").show();
            }
            
            if (this.collection.length === 0) {
                table.hide();
                noResult.show();
            } else {
                tbody.empty();
                _.each(this.collection, function (item) {
                    tbody.append(template(item));
                });
                table.show();
                noResult.hide();
            }

            return this;
        },
        
        search: function () {
            var that = this;
            this.$el.trigger("showSpinner");

            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=coCodes",
                data: { query: this.$("#codes").val() },
                type: "POST",
                dataType: "json"
            }).done(function (data) {
                that.collection = data || [];
                that.render();
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
        },
        addCode: function(e) {
            $("#invitationForm").trigger("addCodes", [[$(e.currentTarget).data("code")]]);
            this.hide();
        },
        addCodes: function (e) {
            var codes = [];
            e.preventDefault();
            this.hide();

            $("input[type=checkbox]:checked").each(function () {
               var coCodeValue = $(this).val();
               
               if (coCodeValue !== "on"){
                   codes.push($(this).val());
               }
               
            });
            
            if (codes.length >= 0) {
                $("#invitationForm").trigger("addCodes", [codes]);
            }
        },
        
        show: function () {
            this.$el.modal('show');
            return this;
        },
        
        hide: function () {
            this.$el.modal("hide");
            return this;
        }
    });

    // Begin of Insert by Naga	ENHC0019060
    /**
     * Approval / Rejection View
     */
    app.views.decisionModalView = Backbone.View.extend({
        el: "#decisionWindow",
        deletionIndex: null,
        events: {
            "click .ok": "goAhead",
            "click .cancel": "cancel",
            "focusin .contactPerson": "showButton",  
            "click #searchContactPerson":"searchForContactPersons",
        },
        initialize: function () {
            _.bindAll(this, "goAhead","cancel", "show", "hide","approveWindow","rejectWindow","showButton","searchForContactPersons","showContactPersonInModal","checkValidity");
            this.$el.modal({
                backdrop: 'static',
                keyboard: false,
                show: false
            });
            
            this.contactPersonModalView       = new app.views.ContactPersonModalView( );
        },
        goAhead: function (e) {
        	if(this.checkValidity()){
            	this.$el.trigger("submitDecision");
                this.hide();
        	}
            e.preventDefault();
        },
        approveWindow:function(){
        	var status = $("#status")
        	// Prep the modal window
        	this.$(".headerText").text("Approve Request");
        	this.$(".ok").text("Approve");
        	this.$(".decisionComments").removeAttr("required");
        	this.$(".decisionComments-label").removeClass("required-red");        	
//        	this.$(".contactPerson").prop("required","required");
//        	this.$(".contactPerson-label").addClass("required-red");

        	this.show();
        },
        rejectWindow:function(){
        	this.$(".headerText").text("Reject Request");
        	this.$(".ok").text("Reject");
        	this.$(".decisionComments").prop("required","required");
//        	this.$(".contactPerson").prop("required","required");
//        	this.$(".contactPerson-label").addClass("required-red");
        	this.$(".decisionComments-label").addClass("required-red");
        	this.show();
     	
        },
        cancel: function(e){
        	this.$(".decisionComments").val("");
        	this.$(".contactPerson").val("");        	
        	this.trigger("close");
            this.hide();
            e.preventDefault();        	
        },
        show: function () {
            this.$el.modal("show");
        },
        hide: function () {
            this.$el.modal("hide");
        },
        showButton:function(){
	        this.$('#searchContactPerson').addClass('in tip');
	        this.$('.tip').tooltip();        	
        },
        searchForContactPersons:function(){
        	var that = this;
        	this.contactPersonModalView.employeeCollection.reset();
        	this.showContactPersonInModal();
            $("#contactPersonResults").trigger("search");
            this.contactPersonModalView.show();  
//        	$("#contactPersonResults").modal({
//        		"backdrop":"static"
//        	});               
        },
        showContactPersonInModal:function(){
            $('#contactPersonName').val($('.contactPerson').val());        	
        },
        checkValidity:function(){
        	var isValid = true;
        	this.$("input,textarea").each(function(idx,itm){
        		if(!$(itm).checkValidity()){
        			isValid = false;
        		}
        	});
        	
        	if(!isValid){
        		this.$('.modalAlerts').html('<div class="alert alert-danger fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>Please fill the required fields</span></div>');
        	}
        	this.alertFadeOut(800);
        	
        	return isValid;
        },
        alertFadeOut: function (delay) {
            window.setTimeout(function () {
                this.$(".modalAlerts .alert").fadeOut("slow");
            }, delay);
        }
    });
    // End of Insert by Naga
    // Begin of Insert by Naga ENHC0013666
    
    var Employee = Backbone.Model.extend({
    	defaults: {
    		Company: "",
    		Department: "",
    		Title: "",
    		email: "",
    		firstname: "",
    		lastname: "",
    		sso: ""
    }
    });
    
    var Employees = Backbone.Collection.extend({
    	model:Employee,
    	comparator: function (line) {
    		return line.get("lastname") + "," + line.get("firstname");
        }
    });
    
    /**
     * SearchApproverModelView: Responsible for looking up Approvers
     */
    app.views.SearchApproverModelView = Backbone.View.extend({
        el: "#requestedForResults",
        events: {
            "click #check-names": "search",
            "click .name": "addRequestedFor",
            "search":"search"	
        },
        recipientsTemplate: _.template($.trim($("#search-requested-template").html())),		
        
        collection: [],

        
        initialize: function () {
            _.bindAll(this, "show", "search", "hide");
          this.employeeCollection = new Employees();  
        },
        
        render: function () {
            var table = this.$("table"),
                noResult = this.$("div.alert.alert-info"),
                tbody = this.$("tbody");
            
            if (this.employeeCollection.length === 0) {
                table.hide();
                noResult.show();
            } else {
                tbody.empty();
                var that = this;
                var employee = null;
                that.employeeCollection.each(function(employee){
                	tbody.append(that.recipientsTemplate(employee.attributes));
                });
              
                table.show();
                noResult.hide();
            }

            return this;
        },
        
        search: function () {
        	// Check if user has entered anything to search
        	
        	// Reset the collection for each search
        	this.employeeCollection.reset();
        	
        	var searchCriteria = this.$("#reqFor").val();
        	if( !(searchCriteria == null || searchCriteria == "") ){
	        	var that = this;
		        $.ajax({
		        type: "GET",
		        url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.userlist",
		        data: {
		            q: this.$("#reqFor").val(),
		            type: "Requested For"
		        },
		        dataType: "json"
		        }).done(function (data) {
		        	if(data.employees.length === 0)
		        		that.employeeCollection.reset();
		        	else{
		                $.each(data.employees, function (index, employee) {
		                	that.employeeCollection.add({Company:employee.Company,Department:employee.Department,Title:employee.Title,email:employee.email,firstname:employee.firstname,lastname:employee.lastname,sso:employee.sso});
		                });
		        		
		        	}
	                that.render();	        	
		        	
		        }).always(function () {
		            //that.$el.trigger("hideSpinner");
		        });        
        	}else{
        		this.render();
        	}
        },
        
        show: function () {
            this.$el.modal('show');
            return this;
        },
        
        hide: function () {
            this.$el.modal("hide");
            return this;
        },
        addRequestedFor: function(e){
        	var name = $(e.target).text();
            var sso =  $(e.target).closest("tr").data("sso");
            
            $("#requestedFor").val(name);
            this.hide();
        }
    });
        
    
    /**
     * RequestedModalView: Responsible for looking up Requested for SSO
     */
    app.views.RequestModelView = Backbone.View.extend({
        el: "#requestedForResults",
        events: {
            "click #check-names": "search",
            "click .name": "addRequestedFor",
            "search":"search"	
        },
        recipientsTemplate: _.template($.trim($("#search-requested-template").html())),		
        
        collection: [],

        
        initialize: function () {
            _.bindAll(this, "show", "search", "hide");
          this.employeeCollection = new Employees();  
        },
        
        render: function () {
            var table = this.$("table"),
                noResult = this.$("div.alert.alert-info"),
                tbody = this.$("tbody");
            
            if (this.employeeCollection.length === 0) {
                table.hide();
                noResult.show();
            } else {
                tbody.empty();
                var that = this;
                var employee = null;
                that.employeeCollection.each(function(employee){
                	tbody.append(that.recipientsTemplate(employee.attributes));
                });
              
                table.show();
                noResult.hide();
            }

            return this;
        },
        
        search: function () {
        	// Check if user has entered anything to search
        	
        	// Reset the collection for each search
        	this.employeeCollection.reset();
        	
        	var searchCriteria = this.$("#reqFor").val();
        	if( !(searchCriteria == null || searchCriteria == "") ){
	        	var that = this;
		        $.ajax({
		        type: "GET",
		        url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.userlist",
		        data: {
		            q: this.$("#reqFor").val(),
		            type: "Requested For"
		        },
		        dataType: "json"
		        }).done(function (data) {
		        	if(data.employees.length === 0)
		        		that.employeeCollection.reset();
		        	else{
		                $.each(data.employees, function (index, employee) {
		                	that.employeeCollection.add({Company:employee.Company,Department:employee.Department,Title:employee.Title,email:employee.email,firstname:employee.firstname,lastname:employee.lastname,sso:employee.sso});
		                });
		        		
		        	}
	                that.render();	        	
		        	
		        }).always(function () {
		            //that.$el.trigger("hideSpinner");
		        });        
        	}else{
        		this.render();
        	}
        },
        
        show: function () {
            this.$el.modal('show');
            return this;
        },
        
        hide: function () {
            this.$el.modal("hide");
            return this;
        },
        addRequestedFor: function(e){
        	var name = $(e.target).text();
            var sso =  $(e.target).closest("tr").data("sso");
            
            $("#requestedFor").val(name);
            this.hide();
        }
    });
    
    // End of Insert by Naga
    
    // Begin of Insert by Naga
    /**
     * ContactPersonModalView: Responsible for looking up Contact Person SSO
     */
    app.views.ContactPersonModalView = Backbone.View.extend({
        el: "#contactPersonResults",
        events: {
            "click #check-names": "search",
            "click .name": "addContactPerson",
            "search":"search"	
        },
        recipientsTemplate: _.template($.trim($("#search-requested-template").html())),		
        
        collection: [],

        
        initialize: function () {
            _.bindAll(this, "show", "search", "hide");
          this.employeeCollection = new Employees();  
        },
        
        render: function () {
            var table = this.$("table"),
                noResult = this.$("div.alert.alert-info"),
                tbody = this.$("tbody");
            
            if (this.employeeCollection.length === 0) {
                table.hide();
                noResult.show();
            } else {
                tbody.empty();
                var that = this;
                var employee = null;
                that.employeeCollection.each(function(employee){
                	tbody.append(that.recipientsTemplate(employee.attributes));
                });
              
                table.show();
                noResult.hide();
            }

            return this;
        },
        
        search: function () {
        	// Check if user has entered anything to search
        	
        	// Reset the collection for each search
        	this.employeeCollection.reset();
        	
        	var searchCriteria = this.$("#contactPersonName").val();
        	if( !(searchCriteria == null || searchCriteria == "") ){
	        	var that = this;
		        $.ajax({
		        type: "GET",
		        url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.userlist",
		        data: {
		            q: this.$("#contactPersonName").val(),
		            type: "Contact Person"
		        },
		        dataType: "json"
		        }).done(function (data) {
		        	if(data.employees.length === 0)
		        		that.employeeCollection.reset();
		        	else{
		                $.each(data.employees, function (index, employee) {
		                	that.employeeCollection.add({Company:employee.Company,Department:employee.Department,Title:employee.Title,email:employee.email,firstname:employee.firstname,lastname:employee.lastname,sso:employee.sso});
		                });
		        		
		        	}
	                that.render();	        	
		        	
		        }).always(function () {
		            //that.$el.trigger("hideSpinner");
		        });        
        	}else{
        		this.render();
        	}
        },
        
        show: function () {
            this.$el.modal('show');
            return this;
        },
        
        hide: function () {
            this.$el.modal("hide");
            return this;
        },
        addContactPerson: function(e){
        	var name = $(e.target).text();
            var sso =  $(e.target).closest("tr").data("sso");
            
            $(".contactPerson").val(name);
            this.hide();
        }
    });    
    // End of Insert by Naga
    
    /**
     * InvitationFormView: Responsible for managing the invitation form
     * Event Listeners: 
     *      $("#invitationForm").trigger("showInvitationForm", [vendorName]);
     */
    app.views.InvitationFormView = Backbone.View.extend({
        el: "#invitationForm",
        events: {
            "showInvitationForm": "showInvitationForm",
            "showMaintainForm": "showMaintainForm",			// ENHC0013668
            "submitDecision":"submitDecision",	// Naga ENHC0019060
            "click .ers .yes-answer": "clickErsYes",
            "click .ers .no-answer": "clickErsNo",
            "click .srcYes": "clickSrcYes",                   // Pranesh  - ENHC0018725 
            "click .srcNo": "clickSrcNo",                     // Pranesh  - ENHC0018725 
            "click #sendInvitation": "submitInvitation",
            "click #resendInvitationBtn": "resendInvitation",
            "click #cancelInvitationBtn": "cancelInvitation",
            "click #approveButton":"approveInvite",		// Naga ENHC0019060
            "click #rejectButton": "rejectInvite",			// Naga ENHC0019060
            "click #resendApprovalBtn": "resendApproval",	//ENHC0013682
            "click #searchCodes": "showFindCompanyCodeModal",
            "focusin #enterCodes": "showCompanyCodeSearchButton",
            "change #enterCodes": "showCodeInModal",
            "focusin #addApprover": "showApproverSearchButton",
            "change #addApprover": "showCodeInModal",            
            "click #addCodes": "addCode",
            "click .pill-close": "removeCode",
            "addCodes": "addCodesHandler",
            "click #proceed-registration": "proceedRegistration",
            "click #proceed-maintain": "proceedMaintain",			// ENHC0013668
            "change select[name='vendorType']": "populateTerms",
            "change select[name='subVendorType']": "populateTerms",	// Naga ENHC0016461 One Time Vendor
            "focusin #requestedFor": "showButton",  //Naga Enh ENHC0013666
            "click #searchRequestedFor": "searchForRecipients",  //Naga Enh ENHC0013666      
            "change #requestedFor":"showRequestedForInModal" //Naga Enh ENHC0013666	
        },
        decisionMode: undefined,							// ENHC0019060
        
        recipientsTemplate: _.template($.trim($("#search-requested-template").html())),		// ENHC0013666
        initialize: function () {
            _.bindAll(this, "show", "clickErsNo", "clickErsYes", "submitInvitation",
            	"resendInvitation", "cancelInvitation", "setErsValidation",
                "checkValidity", "showFindCompanyCodeModal", "showCompanyCodeSearchButton",
                "addCode", "addCodes", "removeCode", "addCodesHandler", "handleCompanyCodeInputVisibility",
                "proceedRegistration","proceedMaintain", "populateTerms","resendApproval", //ENHC0013682
                "submitDecision","approveInvite","rejectInvite" ,"clickSrcYes","clickSrcNo"); 	// ENHC0019060  Pranesh  - ENHC0018725("clickSrcYes","clickSrcNo")
            this.restrictCompanyCode = this.options.restrictCompanyCode;
            
            this.resendInvitationView = new app.views.ResendInvite();
            this.cancelInvitationView = new app.views.CancelInvite();
            
            this.findCompanyCodeModal = new app.views.FindCompanyCodeModalView({ restrictCompanyCode: this.restrictCompanyCode });
            this.findApproverModel       = new app.views.SearchApproverModelView( );	
            this.requestedModel       = new app.views.RequestModelView( );					// ENHC0013666
            this.decisionModalView = new app.views.decisionModalView();						// Naga ENHC0019060

            if(!$('#invitationSearch')[0])
            	$('#invitationForm').show();

        },
        addCodesHandler: function (e, codes) {
            this.addCodes(codes);
        },
        
        addCodes: function (codes) {
            var that = this,
                $pillListItem,
                $pillLabel,
                $pillClose,
                $hiddenInput;

            _.each(codes, function (itm) {
                $pillListItem = $('<li></li>');
                $pillLabel = $('<span class="badge badge-info"></span>');
                $pillClose = $('<span class="pill-close">&times;</span>');
                $hiddenInput = $('<input name="companyCodes" type="hidden" />');

                $hiddenInput.val(itm);
                $pillListItem.append($pillLabel, $pillClose, $hiddenInput);
                $pillLabel.append(itm);

                $('#codesEntered .pillbox ul').show();

                var existingCodes = $('#codesEntered .pillbox ul').text();
                
                if (existingCodes.indexOf(itm) === -1){
	                $('#codesEntered .pillbox ul').append($pillListItem);
	                $(".pill-close").click(that.removeCode);
                }
            });

            // Remove Required Value
            $('#enterCodes').removeAttr('required');
            this.handleCompanyCodeInputVisibility();

        },

        handleCompanyCodeInputVisibility: function() {
            if (this.restrictCompanyCode) {
                if ($('#codesEntered .pillbox ul li').length === 0) {
                    $("#companyCodes").show();
                } else {
                    $("#companyCodes").hide();
                }
            }
        },
        removeCode: function (e) {
            var target = $(e.currentTarget),
                siblings = target.parent().siblings().length;
            e.stopPropagation();
            if (siblings === 0) {
                target.parents("ul").hide();
                $('#enterCodes').attr('required','required')
                
            }
            
            target.parent().remove();
            this.handleCompanyCodeInputVisibility();
        },
        
        addCode: function () {
            var input = $("#enterCodes"),
                that = this;
            
            /* changes done by Kermel dated: 07/28/2014 */  
            var setAddCompanyCodeError = function (message)
            {
                var addCompanyCodeWarning = $("#addCompanyCodeWarning");
        				
                if(addCompanyCodeWarning.length === 0)
                {
                	addCompanyCodeWarning = 
                    $(document.createElement("div"))
                    .attr("id", "addCompanyCodeWarning")
                    .css("margin-bottom", "20px")
                    .addClass("alert")
                    .addClass("alert-danger")
                    .hide();
        					
                    $("#codesEntered").append(addCompanyCodeWarning);
                }
        				
                addCompanyCodeWarning.text(message);
                addCompanyCodeWarning.slideDown();
            }
            
            var removeAddCompanyCodeWarning = function ()
            {      					
                $("#addCompanyCodeWarning").stop(true, true).slideUp(function(){$(this).remove();});
            }
            /* End of changes */
            
            if (input.val() !== "") {
                input.removeClass("user-error");
                
                $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=coCodes",
                data: { query: input.val()},
                type: "POST",
                dataType: "json",
                context: that,
                success: function(data){
                    
                    if(data.length == 1){
                	   this.addCodes([data[0].Code]);
                        removeAddCompanyCodeWarning();
                    }
                    else if(data.length > 1){
                	   this.showFindCompanyCodeModal();
                        removeAddCompanyCodeWarning();
                    }
                    else{
                    	setAddCompanyCodeError("Incorrect company code. Please, try again.");
                    }
                }
                });
                
            } else {
                input.addClass("user-error");
                return;
            }
           
            input.val("");
        },
        
        showCompanyCodeSearchButton: function () {
            $('#searchCodes').addClass('in tip');
            $('.tip').tooltip();
        },
        showApproverSearchButton: function () {
            $('#searchApproverCodes').addClass('in tip');
            $('.tip').tooltip();
        },
        showCodeInModal: function(){
            $('#codes').val($('#enterCodes').val());
        },
        // Begin of Insert by Naga ENHC0013666
        showRequestedForInModal: function(){
            $('#reqFor').val($('#requestedFor').val());
        },        
        // End of Insert by Naga
        showFindCompanyCodeModal: function () {
            this.findCompanyCodeModal.collection = [];
            this.findCompanyCodeModal
                .render()
                .show();
        },
        
        setErsValidation: function () {
            if ($("input[name=ers]").checkValidity()) {
                $("#ersGroup").removeClass("user-error");
            } else {
                $("#ersGroup").addClass("user-error");
            }
        },
        
        checkValidity: function () {
            var result = $(this.el).checkValidity();
            
            var isValid = true;
            this.$("input, select, textarea").each(function () {
                if (!$(this).checkValidity()) {
                    isValid = false;
                }
            });            

            this.setErsValidation();

            return result;
        },
        proceedRegistration: function (e) {
            e.preventDefault();
            var that = this;
            if (!this.checkValidity()) {
                this.$el.trigger("showErrorMessage", ["Please correct the form fields"]);
                return;
            }

            this.$el.trigger("showSpinner");
            var requestType = $('#requestType').val();   
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.objectactions?actionCode=submit_reqpreform",
                dataType: "json",
                type: "POST",
                data: this.$el.serialize()
            }).done(function (data) {
                if (data.code === "0") {
                    window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=2&requestId=" + data.message;
                } else {
                    //that.$el.trigger("showErrorMessage", ["The invitation submission was invalid"]);
                	that.$el.trigger("showErrorMessage", [data.message]);
                }
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
        },
        // Begin of Insert by Naga ENHC0013668
        proceedMaintain: function (e) {
            e.preventDefault();
            var that = this;
            if (!this.checkValidity()) {
                this.$el.trigger("showErrorMessage", ["Please correct the form fields"]);
                return;
            }
            var vendorId = $("#vendorId").val();
            var vendorType = $("#vendorType").val();
//            var companyCodes = $("#companyCodes").val();
              var companyCodes = this.$el.find("input[name=companyCodes]").val();
            if(companyCodes == null || companyCodes.length == 0){
            	// Make the company codes input field required to highlight it
            	$("#enterCodes").addClass("user-error").prop("required","required");
                this.$el.trigger("showErrorMessage", ["Please correct the form fields"]);
                return;            	
            }
            
            if(vendorType=="999"){
            	vendorType = $("#subVendorType").val();
            }
            window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?vendorId=" + vendorId+"&vendorType="+vendorType+"&companyCodes="+companyCodes;
//            this.$el.trigger("showSpinner");
//            var requestType = $('#requestType').val();   
//            $.ajax({
//                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.objectactions?actionCode=submit_reqpreform",
//                dataType: "json",
//                type: "POST",
//                data: this.$el.serialize()
//            }).done(function (data) {
//                if (data.code === "0") {
//                    window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=2&requestId=" + data.message;
//                } else {
//                    //that.$el.trigger("showErrorMessage", ["The invitation submission was invalid"]);
//                	that.$el.trigger("showErrorMessage", [data.message]);
//                }
//            }).always(function () {
//                that.$el.trigger("hideSpinner");
//            });
        },        
        // End of Insert by Naga
        submitInvitation: function () {
            var that = this;
            if (!this.checkValidity()) {
                this.$el.trigger("showErrorMessage", ["Please correct the form fields"]);
                return;
            }
            var requestId = this.$("#invitenum")?"&requestId="+this.$("#invitenum").val():"";
            this.$el.trigger("showSpinner");
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.objectactions?requestType=1&actionCode=submit_invite"+requestId,
                dataType: "json",
                type: "POST",
                data: this.$el.serialize()
            }).done(function (data) {
                if (data.code === "0") {
                    window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + encodeURIComponent("Invitation Sent"));
                } else {
                    //that.$el.trigger("showErrorMessage", ["The invitation submission was invalid"]);
                	that.$el.trigger("showErrorMessage", [data.message]);
                }
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
        },
        // Begin of Insert by Naga ENHC0019060
        submitDecision:function(){
            var that = this;
            this.$el.trigger("showSpinner");
            var operation = (this.decisionMode === "Approve")?"1":"2";
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.inviteactions?operation="+operation+"&invitenum="+this.$("#invitenum").val(),
                dataType: "json",
                type: "POST",
                data: this.$el.serialize()
            }).done(function (data) {
                if (data.code === "0") {
                	$('#itemViewModal').modal('hide');
                	window.parent.jQuery('#itemViewModal').modal('hide');
                } else {
                	that.$el.trigger("showErrorMessage", [data.message]);
                }
            }).fail(function(data){
            	if (data.code === "0")
            		that.$el.trigger("showErrorMessage", [data.message]);
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });    	
        },
        approveInvite: function(){
        	this.decisionMode = "Approve";
        	this.decisionModalView.approveWindow();
        },
        rejectInvite: function(){
        	this.decisionMode = "Reject";
        	this.decisionModalView.rejectWindow();
        },
        // End of Insert by Naga        
        resendApproval: function(){
            var that = this;
            this.$el.trigger("showSpinner");
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.inviteactions?operation=A&invitenum="+this.$("#invitenum").val(),
                dataType: "json",
                type: "POST",
                data: this.$el.serialize()
            }).done(function (data) {
                if (data.code === "0") {
                    window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + data.message+"&type=success");
                } else {
                    //that.$el.trigger("showErrorMessage", ["The invitation submission was invalid"]);
                	that.$el.trigger("showErrorMessage", [data.message]);
                }
            }).fail(function(data){
            	if (data.code === "0")
            		window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + data.message+"&type=error");
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
                	
        },
        resendInvitation: function () {
            this.resendInvitationView.show();
        },  
        
        cancelInvitation: function () {
        	this.cancelInvitationView.show();
        },  
        
        
        clickErsYes: function () {
            $('.ers .input-append select.add-on').removeClass('show');
            $('.ers .input-append select.add-on.ers-yes').addClass('show');
            this.$("select[name=ersNoTerms]").removeAttr("required");
            this.$("#ersYes").prop("checked", true);
            this.setErsValidation();
        },
        
        // Pranesh  - ENHC0018725  
        clickSrcYes: function () {
            this.$("#srcYes").prop("checked", true);
            this.$("#srcstatus").val("Y");
        },
        clickSrcNo: function () {
            this.$("#srcNo").prop("checked", true);
            this.$("#srcstatus").val("N");
        },
       // Pranesh  - ENHC0018725   
       
        clickErsNo: function () {
            $('.ers .input-append select.add-on').removeClass('show');
            $('.ers .input-append select.add-on.ers-no').addClass('show');
            var select = this.$("select[name=ersNoTerms]");
            select.removeAttr("required");
            //select.prop("required", "required");
            this.$("#ersNo").prop("checked", true);
        },
        populateTerms: function(e){
            var terms;
            var select = this.$("select[name=ersNoTerms]");
            var vendorType = this.$('select[name=vendorType] option:selected').val();
            var requestType = $('#requestType').val();  
            var userType = $('#userType').val();
            
            // Modified by CGUTJAHR 1/13/14 : Enhancement #41
            // Hide Fields if Refund is selected
            // ENHC0016461 -- Add Legal Settlement and Contest Winner to the list

//            if (vendorType == "060" || vendorType == "093" || vendorType == "094"){	// ENHC0016461
              if (vendorType == "999" || vendorType == "060" || vendorType == "093" || vendorType == "094" || vendorType == "095"){	// ENHC0016461,Pranesh 
            	// Begin of Insert by Naga ENHC0013683
            	// Uncheck Garnishment if it was defaulted before
            	$("#garnishment").prop('checked',false);
            	// End of Insert by Naga            	
            	$('.subSystem').hide(); 
            	$('.subVendorType').show();					// ENHC0016461
            	$("#subVendorType").prop("required", true); // ENHC0016461
            	$('.annualSpend').hide(); 
            	$(".yes-answer").hide();
            	$(".no-answer").trigger( "click" );
            	$(".no-answer").hide();
            	$(".ers-label").hide();
            	$('input:checkbox').removeAttr('checked');
            } else {
             	$('.subSystem').show(); 
            	$('.annualSpend').show(); 
            	$(".yes-answer").show();  
            	$(".no-answer").show();  
            	$(".ers-label").show();
            	$('.subVendorType').hide();						// ENHC0016461	
            	$("#subVendorType").prop("required", false);    // ENHC0016461        		
            	
            	// Begin of Insert by Naga ENHC0013683 
            	if(vendorType == "092") // If it is garnishment vendor type set the Garnishment to true
            		$("#garnishment").prop('checked',true);
            	else
            		$("#garnishment").prop('checked',false);
            	// End of Insert by Naga
            }
	    //END
             // ENHC0016461 If it is one time vendor wait for one time vendor type to be selected
             // If the vendor type 
              if(vendorType == "999"){
            	  var oneTimeVendorType = this.$('select[name=subVendorType] option:selected').val();
            	  if(!(oneTimeVendorType == "")){
            		  vendorType = oneTimeVendorType;
            	  }
              }
             // Do not populate terms if it is 999
             if(!(vendorType == "999") ) 
             { 
	             $.ajax({
	                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=inviterPaymentTerms",
	                type: "POST",
	                data: { vendorCat: userType,vendorType: vendorType, requestType : requestType},
	                dataType: "json",
	                success: function(data){
	                    terms = data;
	                     //console.log(terms);
	                     select.html('');
	               $.each(terms, function(key, value){
                 	  var option = '<option value="' +  value.Key + '">' + value.Description + '</option>';// DFCT0017546 ganesh removed blank for some vendors

	                      if(value.Key=="" && ((vendorType == "010") || (vendorType == "018")))
	                      {
	                    	  option=option;
	                      }
	                      else if(value.Key==""){
	                    	  option="";
	                      }
	                      if(!option==""){
                    	  select.append(option);
	                      }
	                        });
	                },
	                
	                error: function(error){
	                     console.log("Error: populateTerms failed. Status:" + error.status);
	                }
	            });
        	}
         },
        show: function (e, vendorName) {
            $('#invitationSearch').hide();
            $('#invitationForm').show();
            if (!!vendorName) {
                $('#vendorNameInput').val(vendorName);
            }
            return this;
        },
        // Begin of Insert by Naga Enh ENHC0013666
        showButton: function () {
            this.$('#searchRequestedFor').addClass('in tip');
            this.$('.tip').tooltip();
        },        
        searchForRecipients: function () {
            var that = this;
            
            
            this.requestedModel.employeeCollection.reset();
            // Copy the Requested For data to Modal
            this.showRequestedForInModal();
            $("#requestedForResults").trigger("search");
            this.requestedModel
            .show();
        }
        // End of Insert by Naga
    });
    

    
    // Begin of change by Naga ENHC0013660
    // Get the country and region from backend
    
    var Country = Backbone.Model.extend({
    	defaults: {
    		country: "",
    		description: ""
    }
    });
    
    var Countries = Backbone.Collection.extend({
    	model:Country
    });
	var Region = Backbone.Model.extend({
		defaults: {
			country: "",
			region: "",
			description: ""

		}
	});

	var Regions = Backbone.Collection.extend({
		model: Region
	});       
	// End of change by Naga ENHC0013660
	
    /**
     * InvitationSearchView: Main view for everything under the "Invite" tab of the page
     * Raises Events
     *     showInvitationForm
     */
    app.views.InvitationSearchView = Backbone.View.extend({
        el: "#invitationSearch",
        events: {
            "click #vendorSearch": "clickSearch",
            "change #country-select": "setAddress"
        },
        initialize: function () {
            this.searchVendorModal = new app.views.SearchVendorModalView();
            _.bindAll(this, "clickSearch", "setAddress");
            
            this.countryCollection = new Countries();	// Naga ENHC0013660
            this.stateCollection = new Regions();		// Naga ENHC0013660
            this.setCountry(); 							// Naga ENHC0013660
            this.setStates(); 							// Naga ENHC0013660
            this.setAddress();
        },
        // Begin of Insert by Naga ENHC0013660 
        setCountry: function(){
        	var that = this;
        	var result = app.utils.countryService.getCountries();
        	this.countryCollection.add(result);
        	
            this.$("#country-select").empty();
            this.$("#country-select").append($("<option/>").attr("value", "").text("Select"));
            this.countryCollection.each(function(itm) {
                that.$("#country-select").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")));
            });     
            
            // Set the country drop down in Register on behalf 
            this.$("#vendorCountry").empty();
            this.$("#vendorCountry").append($("<option/>").attr("value", "").text("Select"));
            this.countryCollection.each(function(itm) {
                that.$("#vendorCountry").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")));
            });     
            this.$("#vendorCountry").val("US");
            
        //	End of Insert by Naga ENHC0013660         	
        },        
        setStates: function(){
        	var that = this;
        	var result = app.utils.countryService.getProvinces("US");
        	this.stateCollection.add(result);
//    		End of Insert by Naga ENHC0013660         	
        },
        // End of Insert by Naga ENHC0013660

        setAddress: function () {
        var that = this,
            country = this.$("#country-select").val();
        this.$("input[name=primaryCountry]").val(this.$("#country-select").val());
        this.$("select.state").empty();
        this.$("select.state").append($("<option/>").text("Select"));
        this.stateCollection.each(function(itm) {
    	   if(itm.get("country")===country)
            that.$("select.state").append($("<option/>").attr("value", itm.get("region")).text(itm.get("description")));
        });

        if (country !== "US") {
            this.$(".prov").show();
            this.$("span.zip").hide();
            this.$("span.postal").show();
            this.$("input[name=primaryAddressZip]").prop("placeholder", "Postal Code");
        } else {
            this.$(".prov").hide();
            this.$("span.zip").show();
            this.$("span.postal").hide();
            this.$("input[name=primaryAddressZip]").prop("placeholder", "Zip Code");
        }
        
        $('.search-address').show();
        $(this).parent().hide();
    },        
        
        /*setAddress: function () {
            var that = this,
                country = this.$("#country-select").val();
            this.$("input[name=primaryCountry]").val(this.$("#country-select").val());
            this.$("select.state").empty();
            this.$("select.state").append($("<option/>").text("Select"));
            
            _.each(app.utils.countryService.getProvinces(country), function(itm) {
                that.$("select.state").append($("<option/>").attr("value", itm.code).text(itm.description));
            });

            if (country !== "US") {
                this.$(".prov").show();
                this.$("span.zip").hide();
                this.$("span.postal").show();
                this.$("input[name=primaryAddressZip]").prop("placeholder", "Postal Code");
            } else {
                this.$(".prov").hide();
                this.$("span.zip").show();
                this.$("span.postal").hide();
                this.$("input[name=primaryAddressZip]").prop("placeholder", "Zip Code");
            }
            
            $('.search-address').show();
            $(this).parent().hide();
        },*/
        show: function () {
            $('#invitationSearch').show();
            $('#invitationForm').hide();
            return this;
        },

        clickSearch: function (e) {
            var that = this;
            $("body").trigger("showSpinner");
            $.ajax({
                type: "POST",
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.vendorsearch",
                data: this.$el.serialize(),
                dataType: "json"
            }).done(function (data) {
                that.searchVendorModal
                    .setData(data)
                    .show();
            }).always(function () {
                $("body").trigger("hideSpinner");
            });
            
            e.preventDefault();
        }
    });

    /**
     * InviterView: Root view, based on the common BodyView
     */
    app.views.InviterView = app.views.BaseView.extend({
        events: {
            "showInvitationForm": "showInvitationForm",
            "showMaintainForm": "showMaintainForm",			// ENHC0013668
            "showVendorSearch": "showVendorSearch"
        },
        initialize: function () {
            this.base();
            this.restrictCompanyCode = this.options.restrictCompanyCode;

            _.bindAll(this, "showInvitationForm", "showVendorSearch", "render","showMaintainForm");
            
            if (Backbone.history.location.hash === "#oneCode") {
                this.restrictCompanyCode = true;
            }
            
            if($('#invitationSearch')[0])
            	this.invitationSearchView = new app.views.InvitationSearchView();
            this.invitationFormView = new app.views.InvitationFormView({ restrictCompanyCode: this.restrictCompanyCode });

            // Start debug code, should be removed in production
            if (Backbone.history.location.hash === "#form") {
                this.invitationFormView.show();
            }

            if (Backbone.history.location.hash === "#oneCode") {
                this.restrictCompanyCode = true;
                this.invitationFormView.show();
            }
            // End debug code, should be removed in production
            
            this.countryCollection = new Countries(); // Naga ENHC0013660
            this.setCountry(); // Naga ENHC0013660

            this.render();
        },
        
        // Begin of Insert by Naga ENHC0013660 
        setCountry: function(){
        	var that = this;
        	var result = app.utils.countryService.getCountries();
        	this.countryCollection.add(result);
        	
            this.$("#vendorCountry").empty();
            //this.$("#vendorCountry").append($("<option/>").text("Select"));
            this.$("#vendorCountry").append($("<option/>").attr("value","").text("Select"));//DFCT0016806 changed by ganesh
            this.countryCollection.each(function(itm) {
                that.$("#vendorCountry").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")));
            });        	
        	
             
		},	
		//	End of Insert by Naga ENHC0013660        
        
        render: function () {
            return this;
        },

        showInvitationForm: function () {
            this.invitationFormView.show();
        },
        // Begin of Insert by Naga ENHC0013668
        showMaintainForm: function (event,vendorNumber,vendorType) {
        	// Set the vendor number
        	$("#vendorId").val(vendorNumber);
        	//if(vendorType=="060" || vendorType=="093" || vendorType=="094"){
if(vendorType=="060" || vendorType=="093" || vendorType=="094" || vendorType=="095"){ // Added 095 - Pranesh (05/12/2016)-(Defect:15085)
        		$("#vendorType").val("999");
        		$("#subVendorType").val(vendorType);
        		$(".subVendorType").show();
        		$("#vendorType").prop('disabled', 'disabled');
        		$("#subVendorType").prop('disabled', 'disabled');
        		
        	}else if(vendorType=="000"){
        		// Do nothing
        	}else{
        		$("#vendorType").val(vendorType);
        		$("#vendorType").prop('disabled', 'disabled');
        	}
            this.invitationFormView.show();
        },        
        // End of Insert by Naga
        
        showVendorSearch: function () {
            this.invitationSearchView.show();
        }
    });
    /**
     * Cancel Invite: Responsible for cancel reason selection
     * Events:
     */
    app.views.CancelInvite = Backbone.View.extend({
        el: "#cancelInvite",
        events: {
            "click #confirmCancel": "confirmCancel",
            "click #noCancel": "noCancel"
        },       
        collection: [],
        initialize: function () {
            _.bindAll(this, "show", "hide", "confirmCancel", "noCancel", "checkValidity");
        },
        
        render: function () {
            return this;
        },
        checkValidity: function () {
        	var isValid = true;
        	this.$("select").each(function(idx, itm) {
                if (!$(itm).checkValidity()) {
                    isValid = false;
                }
            });
            return isValid;
        },
        confirmCancel: function(){
            var that = this;
            if (!this.checkValidity()) {
                this.$el.trigger("showErrorMessage", ["Please select cancel reason"]);
                return;
            }

            this.$el.trigger("showSpinner");
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.inviteactions?operation=C&invitenum="+this.$("#invitenum").val()+"&cancelCode="+this.$("#cancelReason").val(),
                type: "GET",
                data: this.$el.serialize(),
                dataType: "json"
            }).done(function (data) {
            	if(data.code==="0")
                window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + encodeURIComponent("Invitation Canceled "));
            	else
            	{
                    that.$el.trigger("showErrorMessage", ["Error "+data.message]);
                    that.hide();
                    return;
            	}
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
        },
        noCancel: function(){
        	this.hide();
        },
        show: function () {
            this.$el.modal('show');
            return this;
        },
        
        hide: function () {
            this.$el.modal("hide");
            return this;
        }

    });
    /**
     * Resend Invite: Responsible for invitation resend
     * Events:
     */
    app.views.ResendInvite = Backbone.View.extend({
        el: "#resendInvite",
        events: {
            "click #confirmResend": "confirmResend",
            "click #cancelResend": "cancelResend"
        },       
        collection: [],
        initialize: function () {
            _.bindAll(this, "show", "hide", "confirmResend", "cancelResend");
        },
        
        render: function () {
            return this;
        },
        confirmResend: function(){
            var that = this;

            this.$el.trigger("showSpinner");
            $.ajax({
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.inviteactions?operation=R&invitenum="+this.$("#invitenum").val(),
                type: "GET",
                data: this.$el.serialize(),
                dataType: "json"
            }).done(function (data) {
            	if(data.code == 0)
                window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + encodeURIComponent("Invitation Resent"));
            	else
            	{
                    that.$el.trigger("showErrorMessage", ["Error "+data.message]);
                    that.hide();
                    return;
            	}
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });        	
        },
        cancelResend: function(){
        	this.hide();
        },
        show: function () {
            this.$el.modal('show');
            return this;
        },
        hide: function () {
            this.$el.modal("hide");
            return this;
        }
    });    
}(window));
