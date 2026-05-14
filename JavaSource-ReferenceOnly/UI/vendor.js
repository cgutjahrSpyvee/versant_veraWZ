/*global $,  Backbone, _, app, Shared, bootstrap_alert */
(function () {
    "use strict";
    /**
     * AddAdministratorModalView: The modal to send invitiations to administer this vendor
     *
     */
    app.views.AddAdministratorModalView = Backbone.View.extend({
        el: "#admin",
        events: {
            "click .btn-success": "sendInvites",
            "click #closeAdmin": "close",
            "click #addAdmin": "addAdmin",
            "click .pill-close": "remove"
        },
        administrators: {},
        initialize: function () {
            _.bindAll(this, "sendInvites", "close", "addAdmin", "remove", "show");
        },
        sendInvites: function (e) {
            var prop,
                data = [],
                that = this;
            this.close();
            e.preventDefault();

            for (prop in this.administrators) {
                if (this.administrators.hasOwnProperty(prop)) {
                    data.push(this.administrators[prop]);
                }
            }
            this.$el.trigger("showSpinner");
            $.ajax({
                type: "POST",
                url: "json/invitationAction.php",
                data: JSON.stringify(data),
                contentType: "application/json",
                dataType: "json"
            }).done(function (result) {
                if (result.code === "0") {
                    that.$el.trigger("showSuccessMessage", ["Administrators added. You can still fill out the registration form."]);
                } else {
                    that.$el.trigger("showErrorMessage", [result.message]);
                }
            }).always(function () {
                that.$el.trigger("hideSpinner");
            });
        },
        close: function () {
            $("#admin").modal('hide');
        },
        show: function () {
            $("#admin").modal("show");
        },
        addAdmin: function (e) {
            e.preventDefault();
            if (!this.$("form").checkValidity()) {
                return;
            }
            var $pillListItem = $('<li></li>'),
                    $pillLabel = $('<span class="badge badge-info"></span>'),
                    $pillClose = $('<span class="pill-close">&times;</span>'),
                    $firstName = this.$("input[name=firstName]"),
                    $lastName = this.$("input[name=lastName]"),
                    $phone = this.$("input[name=phone]"),
                    $language = this.$("select[name=language]"),
                    $email = this.$("input[name=email]"),
                    administrator = {
                        firstName: $firstName.val(),
                        lastName: $lastName.val(),
                        phone: $phone.val(),
                        language: $language.val(),
                        email: $email.val()
                    };

            $pillListItem.append($pillLabel, $pillClose);
            $pillLabel.append($('#adminInput').val());

            this.administrators[administrator.email] = administrator;

            $('.pillbox ul').append($pillListItem);

            $firstName.val("");
            $lastName.val("");
            $phone.val("");
            $language[0].selectedIndex = 0;
            $email.val("");

        },
        remove: function (e) {
            var key = $(e.target).siblings(".badge").text();

            e.stopPropagation();

            delete this.administrators[key];

            $(e.target).parent().hide();
        }
    });

    /**
     * TermsModalView: The agree to terms modal
     */
    app.views.TermsModalView = Backbone.View.extend({
        el: "#terms",
        events: {
            "click .btn-success": "agree",
            "click input[name=agreeTerms]": "checkAgreeTerms"
        },
        initialize: function () {
            _.bindAll(this, "agree", "checkAgreeTerms", "show", "hide");
            this.$el.modal({
                backdrop: 'static',
                keyboard: false,
                show: false
            });
        },
        checkAgreeTerms: function (e) {
            if (this.$(e.currentTarget).is(":checked")) {
                this.$(".modal-footer").removeClass("terms-disabled");
            } else {
                this.$(".modal-footer").addClass("terms-disabled");
            }
        },
        agree: function (e) {
            this.hide();
            //this.$el.trigger("showAddAdministrators");
            e.preventDefault();
        },
        show: function () {
            this.$el.modal("show");
        },
        hide: function () {
            this.$el.modal("hide");
        }
    });
    
    /**
     * ValidateAddressModalView: Address Validation MOdal
     */
    app.views.ValidateAddressModalView = Backbone.View.extend({
        el: "#validateAddress",
        events: {
            "click .continue": "select",
            "click .ok" : "hide",
            "showModal" : "show",
            "click .cancel": "cancel" // DFCT0016721- ganesh
        },
        initialize: function (options) {
            _.bindAll(this, "select", "show", "hide","cancel");// DFCT0016721- ganesh cancel added
            this.$el.modal({
                backdrop: 'static',
                keyboard: false,
                show: false
            });
        },
        // start DFCT0016721- ganesh
        cancel: function(e){
        	var hiddenVal=$("#taxJus").val();
        	if(hiddenVal=="primaryAddress")
        	{
        		$('input[name="primaryAddressCity"]').val("");
            	$('input[name="primaryAddressZip"]').val("");
        	}
        	else
        	{
        	var city=hiddenVal+"City";
        	var zip=hiddenVal+"Zip";
        	$('input[name="'+city+'"]').val("");
        	$('input[name="'+zip+'"]').val("");
        	}
            this.parent.updateTaxCode(["", "", "", ""]); // ganesh DFCT0017546
        	this.trigger("close");
            this.hide();
        //    e.preventDefault();   // ganesh DFCT0017546	
        },
        // end DFCT0016721- ganesh
        select: function (e) {
            var selected = $('input[name="selectTaxCode"]:checked'),
            taxCode = selected.val(),
            state = selected.siblings('span.state').html(),
            city = selected.siblings('span.city').html(),
            zip = selected.siblings('span.zip').text();
	    
		taxCode=taxCode.replace(" ","");// DFCT0017230 ganesh
            state=state.replace(" ","");// DFCT0017230 ganesh
            city=city.replace(" ","");// DFCT0017230 ganesh
            zip=zip.replace(" ","");// DFCT0017230 ganesh

            if(taxCode){
                this.parent.updateTaxCode([taxCode, state, city, zip]);
                this.hide();//ganesh (04/20/2016) - DFCT0016721
                e.preventDefault();
                $("#countryAlerts").html("");// DFCT0016721- ganesh
            }
            else{
                	$("#countryAlerts").html("");// DFCT0016721- ganesh
            		$("#countryAlerts").append("<br/><b style='color:red;font-size: 16px'>Select Address from this list</b>");// ganesh DFCT0016721 
            }
        },
        show: function (e, parent) {
            this.$el.modal("show");
            this.parent = parent;
        },
        hide: function () {
            this.$el.modal("hide");
        }
    });
    
    // Begin of Insert by Naga 1228
    /**
     * CTIModalView: The agree to terms modal
     */
    app.views.CTIModalView = Backbone.View.extend({
        el: "#CTIModal",
        events: {
            "click .btn-success": "agree",
        },
        initialize: function () {
            _.bindAll(this, "agree", "show", "hide");
            this.$el.modal({
                backdrop: 'static',
                keyboard: false,
                show: false
            });
            $("#CTIURL").html("<a target=_blank href=\""+this.options.ctiurl+"\">"+this.options.ctiurl+"</a>\n");
            $("#CTIREGCODE").html("Client ID: <strong>" + this.options.ctiuser + "</strong>    Registration Code: <strong>" + this.options.ctiregcode +"</strong>");
            
        },
        agree: function (e) {
        	window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message="+this.options.message+"&type=success";
            this.hide();
            e.preventDefault();
        },
        show: function () {
            this.$el.modal("show");
        },
        hide: function () {
            this.$el.modal("hide");
        }
    });
    
    // End of Insert by Naga
    
    // Begin of Insert by Naga
    /**
     * CTIModalView: The agree to terms modal
     */
    app.views.ValidateDelete = Backbone.View.extend({
        el: "#validateDelete",
        deletionIndex: null,
        events: {
            "click .ok": "goAhead",
            "click .cancel": "cancel",
        },
        initialize: function () {
            _.bindAll(this, "goAhead","cancel", "show", "hide","display");
            this.$el.modal({
                backdrop: 'static',
                keyboard: false,
                show: false
            });
//            this.deletionIndex = this.options.deletionIndex;
        },
        goAhead: function (e) {
//        	this.$el.trigger("removeItem",[this.deletionIndex]);
        	var index = this.deletionIndex;
        	
//        	this.$el.trigger("removeItem",[index]);
//        	$("#address-list-container").trigger("removeItem",[index]);
        	this.trigger("remove",[index]);
            this.hide();
            e.preventDefault();
        },
        display: function(index){
        	this.deletionIndex = index;
        	this.show();
        },
        cancel: function(e){
        	this.trigger("close");
            this.hide();
            e.preventDefault();        	
        },
        show: function () {
            this.$el.modal("show");
        },
        hide: function () {
            this.$el.modal("hide");
        }
    });    
    // End of Insert by Naga
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
            "focusin .contactPerson": "showButton",  //Naga ENHC0019060
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
        	var status = $("#status").val();// added ganesh
        	// Prep the modal window
        	this.$(".headerText").text("Approve Request");
        	this.$(".ok").text("Approve");
        	if(status == "I" || status == "E"){
            	this.$(".decisionComments").hide().prop("disabled","disabled");
            	this.$(".decisionComments-label").hide();
            	this.$(".approvalReason").prop("disabled","");// added by ganesh
            	this.$(".approvalReason").show().prop("required","required"); // Changed DFCT0016892 - Sudheer
            	this.$(".approvalReason-label").show().addClass("required-red"); // Changed DFCT0016892 - Sudheer
            	this.$(".rejectionReason").hide().prop("disabled","disabled");
            	this.$(".rejectionReason-label").hide();
            	this.$(".contactPerson").prop("required","required");
            	this.$(".contactPerson-label").addClass("required-red");
        	}else{
            	this.$(".contactPerson").hide();
            	this.$(".contactPerson").prop("required",""); // Changed DFCT0016892 - Sudheer
            	this.$(".contactPerson-label").hide();
            	this.$(".approvalReason").hide();
            	this.$(".approvalReason").prop("required",""); // Changed DFCT0016892 - Sudheer
            	this.$(".approvalReason-label").hide();
            	this.$(".rejectionReason").hide();
            	this.$(".rejectionReason-label").hide();
            	this.$(".decisionComments").removeAttr("required");
            	this.$(".decisionComments-label").removeClass("required-red");            	
        	}
//            	this.$(".decisionComments").prop("required","required");
//            	this.$(".contactPerson").prop("required","required");
//            	this.$(".contactPerson-label").addClass("required-red");
//            	this.$(".decisionComments-label").addClass("required-red");


        	this.show();
        },
        rejectWindow:function(){
        	var status = $("#status").val();// added ganesh
        	this.$(".headerText").text("Reject Request");
        	this.$(".ok").text("Reject");
        	if(status == "I" || status == "E"){
            	this.$(".decisionComments").hide().prop("disabled","disabled");
            	this.$(".decisionComments-label").hide();
            	this.$(".approvalReason").hide().prop("disabled","disabled");
            	this.$(".approvalReason-label").hide();
            	this.$(".rejectionReason").prop("disabled","");// added by ganesh
            	this.$(".rejectionReason").show().prop("required","required"); // Changed DFCT0016892 - Sudheer
            	this.$(".rejectionReason-label").show().addClass("required-red"); // Changed DFCT0016892 - Sudheer
            	this.$(".contactPerson").prop("required","required");
            	this.$(".contactPerson-label").addClass("required-red");
        		
        	}else{
            	this.$(".contactPerson").hide();
            	this.$(".contactPerson").prop("required",""); // Changed DFCT0016892 - Sudheer
            	this.$(".contactPerson-label").hide();
            	this.$(".approvalReason").hide();
            	this.$(".approvalReason").prop("required",""); // Changed DFCT0016892 - Sudheer
            	this.$(".approvalReason-label").hide();
            	this.$(".rejectionReason").hide();
            	this.$(".rejectionReason-label").hide();         	
            	this.$(".decisionComments").prop("required","required").removeAttr("disabled");
            	this.$(".decisionComments-label").addClass("required-red");
        	}
//        	this.$(".decisionComments").prop("required","required");
//        	this.$(".contactPerson").prop("required","required");
//        	this.$(".contactPerson-label").addClass("required-red");
//        	this.$(".decisionComments-label").addClass("required-red");
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
        recipientsTemplate: _.template($.trim($("#search-contact-template").html())),		
        
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
    // End of Insert by Naga    
    
    
    
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
     * BasicAddressView: responsible for managing the address information on the basic tab
     */
    app.views.BasicAddressView = Backbone.View.extend({
        events: {
            "change .address-country": "switchCountry",
            "blur .zip" : "validateAddress",
            "blur .city" : "validateAddress",
            "change .zip" : "validateAddress",//DFCT0016721
            "change .city" : "validateAddress",//DFCT0016721
            "blur .state" : "validateAddress",
            "blur .alpha-num-validation":"alphaNumValidation",
            "blur .alpha-num-slash-validation":"alphaNumSlashValidation",	// Naga ENHC0013660
            "blur .special-char-validation":"specialCharValidation",
            "change .special-char-validation-exceptdash":"specialCharValidationExceptDash"
             
        },
        secondaryAddressTemplate: _.template($("#secondaryAddressTemplate").html()),
        initialize: function (options) {
            _.bindAll(this, "switchCountry", "renderCountry", "renderSecondaryAddress", "validateAddress", "addressFieldsChanged", "updateTaxCode");
            

            this.stateCollection = new Regions();		// Naga ENHC0013660

            this.setStates(); 							// Naga ENHC0013660            
            var country = this.$('select.address-country option:selected'); // Naga ENHC0013660
            this.renderCountry(country.val());
            //this.renderCountry('US');					// Naga ENHC0013660
            var that = this;
        },
        
        // Begin of Insert by Naga ENHC0013660 
        setStates: function(){
        	var that = this;
        	var result = app.utils.countryService.getProvinces("US");
        	this.stateCollection.add(result);
       	
        },
        // End of Insert by Naga ENHC0013660        
        
        
        updateTaxCode: function(taxCode){
            var city = this.$el.find('input.city'),
                state = this.$el.find('.state'),
                zip = this.$el.find('input.zip');
            this.$el.find('input.taxCode').val(taxCode[0]);
            state.val(taxCode[1]);
            city.val(taxCode[2]);
            zip.val(taxCode[3]);

            city.data('val', city.val());
            state.data('val', state.val());
            zip.data('val', zip.val());
        },

        alphaNumValidation:function(e)
        {
            this.validationErrorMessage(/[^a-zA-Z0-9 ]/g, $(e.currentTarget));     
        },
        
        // Begin of Insert by Naga ENHC0013660
        alphaNumSlashValidation:function(e)
        {
            this.validationErrorMessage(/[^a-zA-Z0-9-&/' ]/g, $(e.currentTarget));   // Pranesh - (04/21/2016)ENHC0013678
        },      
        // End of Insert by Naga 
        specialCharValidation:function(e)
        {
            //this.validationErrorMessage(/[^\w&' ]/g, $(e.currentTarget));
        	this.validationErrorMessage(/[^\w-&/' ]/g, $(e.currentTarget));          // Pranesh - (04/26/2016)- Added( - / ) ENHC0013678
        },

        specialCharValidationExceptDash:function(e)
        {
        	this.validationErrorMessage(/[^\w- ]/g, $(e.currentTarget));     
        },

        validationErrorMessage:function(regex,target)
        {
            if(!target.val().match(regex))
            { 
                target.next().slideUp(); 
                target.removeClass("user-error"); 
            } 
            else 
            { 
                target.next().slideDown(); 
                target.addClass("user-error");       
            }
        },

        switchCountry: function (e) {
            this.renderCountry($(e.currentTarget).val());
        },
        renderSecondaryAddress: function (parent, id) {
            this.$el = $(this.secondaryAddressTemplate({ index: id, id: id }));
            parent.append(this.$el);
            this.el = "#secondary-address-" + this.cid;
            this.delegateEvents(this.events);
           
            $('.tip').tooltip();

            return this;
        },
        
        renderCountry: function (country,noClear) {
            var that = this,
                option;
            this.$(".address-country").val(country);
            this.$(".address1").prop("required", true);
            this.$(".city").prop("required", true);
            this.$(".zip").removeAttr("required");                  //Pranesh-(21/04/2016) ENHC0013678
            if (country === "US") {
                this.$(".province-label").hide();
                this.$(".state").prop("required", true).show();     //Pranesh-(26/04/2016) ENHC0013678
                 //this.$(".state").removeAttr("required").show();  //Pranesh-(21/04/2016) ENHC0013678 
                this.$(".postal-code-label").hide();
            } else {
                this.$(".province-label").show();
               // this.$(".state").removeAttr("required").show(); //Pranesh-(21/04/2016) ENHC0013678 
                this.$(".state").prop("required", true).show();   //Pranesh-(21/04/2016) ENHC0013678
                this.$(".postal-code-label").show();
            }
          
            // Begin of code Pranesh-(21/04/2016) ENHC0013678
            if (country === "US" || country === "CA") { 
               this.$(".zip").addClass("required-red");          //Pranesh-(21/04/2016) ENHC0013678 
               this.$(".zip").prop("required", true); 
            }
            else {
             this.$(".zip").removeAttr("required"); 
             this.$(".zip").removeClass("required-red");         //Pranesh-(21/04/2016) ENHC0013678 
             this.$(".zip").removeClass("user-error");
            }
            // End of code Pranesh-(21/04/2016) ENHC0013678
            
            var selectedState = this.$(".state").val();
            
            this.$(".state").empty();
            this.$(".state").append("<option value=''>Select</option>");
            
            
            // Begin of Insert and Comment by Naga ENHC0013660

            this.stateCollection.each(function(itm) {									
            	if(itm.get("country")===country){
                option = $("<option/>");
                
                if(selectedState && selectedState === itm.get("region"))
                	option.val(itm.get("region")).text(itm.get("description")).selected(true);
                else
                	option.val(itm.get("region")).text(itm.get("description"));
                
                that.$(".state").append(option);
            	}																			
            });
            
            
/*            _.each(app.utils.countryService.getProvinces(country), function (itm) {	
                option = $("<option/>");
                
                if(selectedState && selectedState === itm.code)
                	option.val(itm.code).text(itm.description).selected(true);
                else
                	option.val(itm.code).text(itm.description);
                
                that.$(".state").append(option);
            }); */            
            // End of Insert and Comment by Naga ENHC0013660
            return this;
        },
        addressFieldsChanged: function(){
            var country = this.$('.address-country option:selected'),
               city = this.$('.city'),
               state = this.$('.state option:selected'),
               zip = this.$('input.zip');
            if((this.$('.address-country').data('val') == country.val()) && (city.data('val') == city.val()) && (this.$('.state').data('val') == state.val()) && (zip.data('val') == zip.val())){
        
                return false;
            }else{
               
                return true;
            }
           
        },
        validateAddress: function(e){
            var that = this,
                country = this.$('select.address-country option:selected'),
                city = this.$('input.city'),
                state = this.$('select.state option:selected'),
                zip = this.$('input.zip'),
                taxCode = this.$('input.taxCode');
          //DFCT0016721 start
            var hiddenValue=zip.attr('name');
            hiddenValue=hiddenValue.replace("Zip","");
            $("#taxJus").val(hiddenValue);
          //DFCT0016721 end  
            // This address validation should only happen for countries US / CA DFCT0014114
            if(country.val() && city.val() && state.val() && zip.val() && this.addressFieldsChanged()&&(country.val() === 'US' || country.val() === "CA")){
                this.$el.trigger("showSpinner");

                //save values to data attributes   
                this.$('.address-country').data('val', country.val());
                city.data('val', city.val());
                this.$('.state').data('val', state.val());
                zip.data('val', zip.val());
                $.ajax({
                    url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=validateCityState",
                    type: "POST",
                    async: false,
                    data: { c: country.val(), city: city.val(), state: state.val(), zip: zip.val()},
                    dataType: "json"
                }).done(function (data) {
                     
                    if (data[0].code === "0") {
                        $('#validateAddress').trigger('showModal', [that]);
                        $('#validateAddress .modal-footer a').removeClass('continue').addClass('ok').html('OK');
                    }else if(data[0].code === "1"){
                        var taxCode;
                        if(data[0].message.length == 1){
                            taxCode = data[0].message[0].taxCode;
                        if(city.val().trim().toUpperCase()==data[0].message[0].city.trim() && state.val().trim()==data[0].message[0].state.trim() && zip.val().trim()==data[0].message[0].zip.trim())  
                        {                            
                            that.$('input.city').val(data[0].message[0].city);//DFCT0016721
                            that.$('select.state').val(data[0].message[0].state);//DFCT0016721
                            that.$('input.zip').val(data[0].message[0].zip);//DFCT0016721
                        }
                        else{
                        	
                        	 $(".continue").removeAttr("style");// DFCT0016721
                             var html = 'We have found more than one city that matches the zip code you entered. Please choose which city/zip/state you want to use: <br><br><div id="countryAlerts" class="country alerts"></div><br>'; // DFCT0016721
                             for(var i = 0; i < data[0].message.length; i++){
                                 var entry = data[0].message[i];
                                 html += '<div><label><input type="radio" name="selectTaxCode" value="' + entry.taxCode + '" required><span class="city"> ' + entry.city + '</span>, <span class="state">' + entry.state + '</span><span class="zip"> ' + entry.zip + '</span></label></div>';
                             }
                             
                             $('#validateAddress .modal-body').html(html);
                             $('#validateAddress .modal-header h3').html('Please select the correct address:  ');
                             $('#validateAddress').trigger('showModal', [that]);
                             taxCode = that.$('input.taxCode:checked').val();
                       }
                            that.$el.trigger("hideSpinner");
                }
                        // Begin of change by Naga DFCT0014114
                        // When no results do not show the popup
//                        else if(data[0].message.length == 0){
                      
                        //start DFCT0016721- ganesh
                        else if(data[0].message.length == 0){   
                        	
                        	if(country.val()=="US" || country.val()=="CA")
                        	{
                        		 $("#taxCodeValidate").attr("style","display:none;");
                        		  var html='No match Found.Please enter valid city/zip/state';
                        		  $('#validateAddress .modal-body').html(html);
                                  $('#validateAddress').trigger('showModal', [that]);
                           }
                        	// end DFCT0016721- ganesh
                        }
                        // End of change by Naga 
                        else{
                        	 $(".continue").removeAttr("style");// DFCT0016721
                            var html = 'We have found more than one city that matches the zip code you entered. Please choose which city/zip/state you want to use: <br><br><div id="countryAlerts" class="country alerts"></div><br>'; // DFCT0016721
                            for(var i = 0; i < data[0].message.length; i++){
                                var entry = data[0].message[i];
                                html += '<div><label><input type="radio" name="selectTaxCode" value="' + entry.taxCode + '" required><span class="city"> ' + entry.city + '</span>, <span class="state">' + entry.state + '</span><span class="zip"> ' + entry.zip + '</span></label></div>';
                            }
                            
                            $('#validateAddress .modal-body').html(html);
                            $('#validateAddress .modal-header h3').html('Please select the correct address:  ');
                            $('#validateAddress').trigger('showModal', [that]);
                            taxCode = that.$('input.taxCode:checked').val();
                            
                        }
                        
                        that.$('input.taxCode').val(taxCode);
                    }
                }).always(function () {
                    that.$el.trigger("hideSpinner");
                }).fail(function(data){
                    console.log('failed');
                });
            }
        }
    });
    
    /**
     * BasicAddressListView: responsible for managing the address list on the basic tab
     */
    app.views.BasicAddressListView = Backbone.View.extend({
        el: "#address-list-container",
        primaryAddressView: null,
        secondaryAddressViews: {},
        secondaryAddressTotal: $("#numberOfSecondaryAddress").val(),
        events: {
            "click #add-address": "addAddressItem",
            //Req#50 START Code added by AGAMPA 21 Feb 2015
            "click #add-sec-address": "validateAndAddAddressItem", //Req#50 END
            "click #secondary-address .edit-item": "editItem",
//            "removeItem": "removeItem", 							 // ENHC0013668 1228            
//            "click #secondary-address .icon-remove": "removeItem", // ENHC0013668 1228
            "click #secondary-address .icon-remove": "confirmDelete", // ENHC0013668 1228
//            "click #secondary-address .icon-trash": "confirmDelete", // ENHC0013668 1228            
            "click .remit-button": "remitButton",
            "click .purchasing-button": "purchasingButton",
            "change select[name=primaryAddressCountry]": "changeCountry"
        },
        initialize: function () {
            var that = this,
                address;
            _.bindAll(this, "addAddressItem", "validateCountrySelect", "validateAndAddAddressItem", "editItem", "removeItem", "remitButton", "purchasingButton", "changeCountry", "confirmDelete","closeClicked");
            
            this.countryCollection = new Countries();	// Naga ENHC0013660
			//this.setCountry(); 							// Naga ENHC0013660
			
            if (!this.$("#primary-address").hasClass("hide")) {
                address = new app.views.BasicAddressView({ el: this.$("#primary-address") });
                // address.renderCountry(address.$(".address-country").val());
                this.primaryAddressView = address;
                address.$el.show();
            }

            this.$("#secondary-address .accordion-group").each(function () {
                address = new app.views.BasicAddressView({ el: this });
                that.secondaryAddressViews[address.$(".icon-remove").data("index")] = address;  // ENHC0013668 1228
//                that.secondaryAddressViews[address.$(".icon-trash").data("index")] = address;  // ENHC0013668 1228
            });
            //Req#50 START Code added by AGAMPA 21 Feb 2015
            this.$(".sec-address-country").change(function(e)
    	    {
            	that.validateCountrySelect($(e.currentTarget));     
    	    });  
            //Reql#50 END
            this.validateAddressModalView = new app.views.ValidateAddressModalView();

            //this.$("#secondary-address").collapse();
            this.validateDelete = new app.views.ValidateDelete();			// ENHC0013668
        },
        
		//	Begin of Insert by Naga ENHC0013660         	
        setCountry: function(){
        	var that = this;
        	var result = app.utils.countryService.getCountries();
        	this.countryCollection.add(result);
        	
        	// Set Country drop down in Primary and Secondary addresses
        	var selectedCountry = this.$(".address-country").val();
        	this.$(".address-country").empty();
        	this.$(".address-country").append($("<option/>").text("Select"));
            this.countryCollection.each(function(itm) {
            	if(selectedCountry && selectedCountry === itm.get("country"))
            		that.$(".address-country").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")).selected(true));
            	else
            		that.$(".address-country").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")));
            });        	
            
            // Set Country drop down at Secondary Address button
            var selectedSecCountry = this.$(".sec-address-country").val();
        	this.$(".sec-address-country").empty();
        	this.$(".sec-address-country").append($("<option/>").text("Select"));
            this.countryCollection.each(function(itm) {
            	if(selectedSecCountry && selectedSecCountry === itm.get("country"))
            		that.$(".sec-address-country").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")).selected(true));
            	else
            		that.$(".sec-address-country").append($("<option/>").attr("value", itm.get("country")).text(itm.get("description")));
            });               
        	
            
        }, 			
		//	End of Insert by Naga ENHC0013660                 
        remitButton: function (e) {
            $(e.currentTarget).parent().parent().find('.purchasing-contact').removeClass('shown');
            // ENHC0013668 Do not make all the purchasing emails as not required, only make the relavant one
//            this.$(".purchasing-email").removeAttr("required").removeClass("user-error");  // ENHC0013668
            $(e.currentTarget).parent().parent().find("[name*=purchasingEmail]").removeAttr("required").removeClass("user-error"); // ENHC0013668
            //$('input[name="secondaryAddress-view1-RemitPurchase"][value="remit"]').prop('checked', true);
            $(e.currentTarget).parent().find("[name*=secondaryAddress-view][name$=RemitPurchase][value=remit]").prop('checked', true);            
            e.preventDefault();
        },
        purchasingButton: function (e) {
            $(e.currentTarget).parent().parent().find('.purchasing-contact').addClass('shown');
            //$(e.currentTarget).parent().find("[name*=secondaryAddress-view]");
            $(e.currentTarget).parent().find("[name*=secondaryAddress-view][name$=RemitPurchase][value=purchasing]").prop('checked', true);
            // ENHC0013668 Do not make all the purchasing emails as required, only make the relavant one 
            // this.$(".purchasing-email").prop("required", true); ENHC0013668 
            $(e.currentTarget).parent().parent().find("[name*=purchasingEmail]").prop("required", true); // ENHC0013668
            e.preventDefault();
        },
        editItem: function (e) {
            var $button = $(e.currentTarget),
                collapseTarget = $button.attr("href");

            this.revealSecondaryAddress($(collapseTarget), $button.parent());
            e.preventDefault();
        },
        revealSecondaryAddress: function ($collapseTarget, $collapseHeader) {
            var header;
            $("#secondary-address .collapse.in").each(function () {
                header = $(this).parents(".accordion-group").find(".vendor-name").val() || $(this).parents(".accordion-group").find(".address1").val() || "Blank";
                $(this).parents(".accordion-group").find(".item-label").text(header);
                $(this).collapse("toggle");
            });
            $("#secondary-address .accordion-heading.active").removeClass("active");
            $collapseTarget.collapse("toggle");
            if (!$collapseTarget.hasClass("in")) {
                $collapseTarget.collapse("toggle");
            }
            
            $collapseHeader.addClass("active");
        },
//      Begin of Comment and Insert by Naga ENHC0013668 1228        
//        removeItem: function (e) {
//            var index = $(e.currentTarget).data("index"),
//                secondaryAddress = this.secondaryAddressViews[index];
//            
//            secondaryAddress.remove();
//            delete this.secondaryAddressViews[index];
//            
//            e.preventDefault();
//        },
        removeItem: function (deletionIndex) {
        	this.validateDelete.off("remove");
        	this.validateDelete.off("close");
        	
            var secondaryAddress = this.secondaryAddressViews[deletionIndex];
            
            secondaryAddress.remove();
            delete this.secondaryAddressViews[deletionIndex];
            
        },
        closeClicked: function(){
        	this.validateDelete.off("remove");
        	this.validateDelete.off("close");

        },
        confirmDelete: function (e) {
        	this.validateDelete.on("remove",this.removeItem);
        	this.validateDelete.on("close",this.closeClicked);
        	
            this.validateDelete.display($(e.currentTarget).data("index"));
            e.preventDefault();
        },        
//      End of Comment and Insert by Naga 
        
      //Req#50 START Code added by AGAMPA 21 Feb 2015
        validateCountrySelect: function(target){
        	var country = target.val();
            if(country != "")
            { 
                target.removeClass("user-error");
            	return true;
            } 
            else
            {
                return false;
            }
        },
        validateAndAddAddressItem: function (){
        	var target 	= this.$(".country-select");
        	
        	if(this.validateCountrySelect(target))
            	this.addAddressItem();
        	else {
                target.addClass("user-error");
                this.$el.trigger("showErrorMessage", ["Please select country for secondary address"]);
        	}
        },
        //Req#50 END
        addAddressItem: function () {
            this.secondaryAddressTotal++;
            var secondaryAddress,
                country = this.$(".country-select").val(),
                secondaryCollapseTarget,
                id = this.secondaryAddressTotal;
            if (!!this.primaryAddressView) {
                secondaryAddress = new app.views.BasicAddressView()
                    .renderSecondaryAddress(this.$("#secondary-address"), id)
                    .renderCountry(country);
                this.secondaryAddressViews[id] = secondaryAddress;
              
                secondaryCollapseTarget = secondaryAddress.$(".edit-item").attr("href");
                this.revealSecondaryAddress($(secondaryCollapseTarget), secondaryAddress.$(".accordion-heading"));
            } else {
                this.primaryAddressView = new app.views.BasicAddressView({ el: "#primary-address" });
                this.primaryAddressView.renderCountry(country).$el.show();
                this.triggerTaxLabelUpdate(country);
            }
            this.$(".country-select").val("US");
        },
        triggerTaxLabelUpdate: function(country) {
            $("#tab2").trigger("setUploadLabel", [country]);
        },
        changeCountry: function(e) {
            var country = $(e.currentTarget).val();
            this.triggerTaxLabelUpdate(country);
        }
    });

    /**
     * BasicDivisertyView: responsible for the diversity functionality
     */
    app.views.BasicDiversityView = Backbone.View.extend({
        el: "#diversity",
        events: {
            "click .diversity-begin": "beginDiversityInput",
            "click .diversity-finish .btn": "nextDiversitySection",
            "click .diversity-edit": "toggleDiversitySection",
            "keyup .controls.ethnic input": "ethnicityInput",
            "keyup .controls.gender input": "genderInput",
            "keyup .controls.members-served input": "membersServedInput"
        },
        initialize: function () {
            _.bindAll(this, "beginDiversityInput", "nextDiversitySection", "toggleDiversitySection", "ethnicityInput", "genderInput", "membersServedInput");
        },
        calculateTotal: function (input) {
            var total = 0;
            $(input).closest(".diversity-form").find(".ethnicity-input").each(function () {
                var number = parseInt($(this).val(), 10);
                if (!isNaN(number)) {
                    total += number;
                }
            });
            return total;
        },
        updateDiversityBarUI: function (input, total) {
            var $accordionGroup = $(input).closest('.accordion-group');
            $accordionGroup.find('.accordion-heading').tooltip({
                title: 'Too much diversity. Check your math.',
                trigger: 'manual'
            });
            
            if (total > 100) {
                $accordionGroup.find('.accordion-heading').tooltip('show');
                $accordionGroup.find('.progress-ethnic').addClass('progress-overflow');
            } else {
                $(input).closest(".accordion-group").find('.accordion-heading').tooltip('hide');
                $accordionGroup.find('.progress-ethnic').removeClass('progress-overflow');
            }

            if (!isNaN(parseInt($(input).val(), 0))) {
                $accordionGroup.find('.progress-ethnic').find('.' + $(input).data('diversity')).css("width", $(input).val() + "%");
            } else {
                $accordionGroup.find('.progress-ethnic').find('.' + $(input).data('diversity')).css("width", "0%");
            }
        },
        membersServedInput: function (e) {

            var input = e.currentTarget,
                $accordionGroup = $(input).closest('.accordion-group');

            $accordionGroup.find('.accordion-heading').tooltip({
                title: 'Too much diversity. Check your math.',
                trigger: 'manual'
            });

            if($(input).val() > 100){
                $accordionGroup.find('.accordion-heading').attr('data-original-title', "Too much diversity. Check your math.");
                if($accordionGroup.find('.accordion-heading').siblings('.tooltip')){
                    $accordionGroup.find('.accordion-heading').tooltip('show');
                }
            }else if($(input).val() === '' || ($(input).val() >= 0 && $(input).val() <= 100)){
                $accordionGroup.find('.accordion-heading').tooltip('hide');
            }else{
                $accordionGroup.find('.accordion-heading').attr('data-original-title', "Too little diversity. Check your math.");
                if($accordionGroup.find('.accordion-heading').siblings('.tooltip')){
                    $accordionGroup.find('.accordion-heading').tooltip('show');
                }
            }
        },
        genderInput: function (e) {
            var wtotal = 0,
                input = e.currentTarget,
                total = this.calculateTotal(input),
                $accordionGroup = $(input).closest('.accordion-group');

            $(input).closest(".diversity-form").find(".women").each(function () {
                var number = parseInt($(this).val(), 10);
                if (!isNaN(number)) {
                    wtotal += number;
                }
            });
            if (wtotal > 100) {
                $('.progress-women').addClass('progress-overflow');
            } else {
                $('.progress-women').removeClass('progress-overflow');
            }
            if (!isNaN(parseInt($(input).val(), 0))) {
                $accordionGroup.find('.progress-women').find('.women').css("width", $(input).val() + "%");
            } else {
                $accordionGroup.find('.progress-women').find('.women').css("width", "0%");
            }

            this.setComplateUIWithWomen(input, total);
        },
        setComplateUIWithWomen: function (input, total) {
            var $accordionGroup = $(input).closest('.accordion-group');

            if (total === 100 && !!$(input).closest('.diversity-form').find('.women').val().length && $(input).closest('.diversity-form').find('.women').val() <= 100) {
                $accordionGroup.find('.diversity-complete').addClass('shown');
            } else {
                $accordionGroup.find('.diversity-complete').removeClass('shown');
            }
        },
        ethnicityInput: function (e) {
            var input = e.currentTarget,
                total = this.calculateTotal(input);

            this.updateDiversityBarUI(input, total);
            this.setComplateUIWithWomen(input, total);

        },
        toggleDiversitySection: function (e) {
            var $this = $(e.currentTarget),
                parent = $this.data("parent"),
                actives = parent && $(parent).find('.collapse.in'),
                target = $this.attr('data-target');

            $(parent).find(".diversity-edit").addClass("shown");
            $this.removeClass("shown");

            if (actives && !!actives.length) {
                actives.collapse("hide");
            }

            $(target).collapse('toggle');
        },
        nextDiversitySection: function (e) {
            var $target = $(e.currentTarget);
            $target.hide();
            $target.closest('.accordion-group').find('.accordion-body').collapse('hide');
            $target.closest('.accordion-group').next().show();
            $target.closest('.accordion-group').next().find('.accordion-body').collapse('show');
            $target.closest('.accordion-group').next().find('.diversity-edit').removeClass('shown');
            $target.closest('.accordion-group').find('.diversity-edit').addClass('shown');
        },
        beginDiversityInput: function (e) {
            $(e.currentTarget)
                .collapse('toggle')
                .hide();
        }
    });
    
    /**
     * BasicTabView: responsible for the form on the basic tab
     */
    app.views.BasicTabView = Backbone.View.extend({
        el: "#tab1",
        events: {
            "click #poCheckbox": "acceptPoHandler",
            "click .questions .btn-group a": "environmentQuestionAnswers",
            "click .form-actions .btn-success": "advanceToTax"
        },
        initialize: function () {
            _.bindAll(this, "acceptPoHandler", "checkValidity", "environmentQuestionAnswers", 
               "advanceToTax", "regionsValid");
            this.addressListView = new app.views.BasicAddressListView();
            this.divisertyGroupView = new app.views.BasicDiversityView();
        },
        advanceToTax: function (e) {
        	
            if (this.checkValidity()) {
            	
            	if (($("#vendorType").val() === "040") || ($("#vendorType").val() === "050") || ($("#vendorType").val() === "060")) {
	                this.$el
	                .trigger("prepareTaxTab")
	                .trigger("completeBasicTab")
	                .trigger("prepareBankingTab")
	                .trigger("showBankingTab");  
	                
				// Begin of Insert by Naga ENHC0013683
				// Hide tax tab for Garnishment Vendor
            	} else if($("#vendorType").val() === "092"){
	                this.$el
	                .trigger("prepareTaxTab")
	                .trigger("completeBasicTab")
	                .trigger("prepareTermsTab")
	                .trigger("showTermsTab");            		
	            // End of Insert by Naga     
            	} else {            	
            		this.$el.trigger("prepareTaxTab")
                    .trigger("completeBasicTab")
                    .trigger("showTaxTab");
            	}
            	// this.$(window).scrollTop(0,0);
            }
            e.preventDefault();
            $('html, body').animate({ scrollTop: 0 }, 0);
        },
        regionsValid: function () {
            var that,
                view = this,
                errors = false,
                country,
                city,
                state,
                zip;

            this.$(".validate-group").each(function () {
                that = this;
                country = $(this).find("input[type=hidden]");
                city = $(this).find("input[name*=City]");
                state = $(this).find("select[name*=State]");
                zip = $(this).find("input[name*=Zip]");

                view.$el.trigger("showSpinner");
                $.ajax({
                    url: "json/validateRegion.php",
                    type: "POST",
                    async: false,
                    data: { country: country.val(), city: city.val(), state: state.val(), zip: zip.val()},
                    dataType: "json"
                }).done(function (data) {
                    if (data.code !== "0") {
                        $(that).addClass("error");
                        errors = true;
                    }
                }).always(function () {
                    view.$el.trigger("hideSpinner");
                });

                if (errors) {
                    country.addClass("user-error");
                    city.addClass("user-error");
                    state.addClass("user-error");
                    zip.addClass("user-error");
                } else {
                    country.removeClass("user-error");
                    city.removeClass("user-error");
                    state.removeClass("user-error");
                    zip.removeClass("user-error");
                }
            });
            
            return !errors;
        },

        environmentQuestionAnswers: function(e) {
            var $target = $(e.currentTarget),
                $describe = $target.parent().parent().parent().find('.describe');

            if ($target.hasClass('yes-answer')) {
                $describe.find("textarea").prop("required", true);
                $describe.show();
            } else {
                $describe.find("textarea").removeAttr("required").removeClass("user-error");
                $describe.hide();
            }
            $target.parent().parent().parent().next().show();
        },
        acceptPoHandler: function () {
            if (this.$("#poCheckbox").is(':checked')) {
                this.$("#poCheckbox").closest('.control-group').find('.disabled-overlay').hide();
                this.$("input[name=poEmail]").prop("required", true);

            } else {
                this.$("#poCheckbox").closest('.control-group').find('.disabled-overlay').show();
                this.$("input[name=poEmail]").removeAttr("required")
                    .removeClass("user-error");
            }
        },
        checkValidity: function () {
            var isValid = true;
            //regionsValid = this.regionsValid();
            this.$("input, select, textarea").each(function(idx, itm) {
                if (!$(itm).checkValidity()) {
                    isValid = false;
                }
            });
            
            if(!isValid) {
                this.$el.trigger("showErrorMessage", ["Check Required Fields and Remove Any Invalid Characters"]);
            }
            //return isValid && regionsValid;
            return isValid;
        }
    });
    
    /** 
     * TermsTabView: Responsible for the form validation of the terms tab
     */
    app.views.TermsTabView = Backbone.View.extend({
        el: "#tab3",
        events: {
            "click .back": "backToTax",
            "click .toggle-terms": "selectTermsOption",
            "click .continue": "advanceToBanking"
        },
        initialize: function () {
            _.bindAll(this, "checkValidity", "backToTax", "selectTermsOption", "advanceToBanking");
        },
        advanceToBanking: function (e) {
            e.preventDefault();
            if (!this.checkValidity()) {
                return;
            }
           
            this.$el
                .trigger("completeTermsTab")
                .trigger("prepareBankingTab")
                .trigger("showBankingTab");
            
            $('html, body').animate({ scrollTop: 0 }, 0);
        },
        selectTermsOption: function (e) {
            var target = $(e.currentTarget).data("target"),
                parent = $(e.currentTarget).data("parent"),
                option = "." + $(e.currentTarget).data("option");

            this.$(parent + " .collapse.in").each(function () {
                $(this).collapse("toggle");
            });

            $(target).collapse("toggle");
            if (!$(target).hasClass("in")) {
                $(target).collapse("toggle");
            }
            
            $(option).prop("checked", true);
            this.checkValidity();
            e.preventDefault();
        },
        backToTax: function (e) {
        	// Begin of Insert and comment by Naga ENHC0013683
        	// Hide tax tab for Garnishment Vendor
        	//this.$el.trigger("showTaxTab");
            if ($("#vendorType").val() === "092") {
           	 this.$el.trigger("showBasicTab");
            } else {
            	this.$el.trigger("showTaxTab");
            }        	
        	// End of Insert and comment by Naga
            
            e.preventDefault();
        },
        checkValidity: function () {
            var isValid = true;
            this.$("input, select, textarea").each(function () {
                if (!$(this).checkValidity()) {
                    isValid = false;
                }
            });

            if (!isValid) {
                this.$el.trigger("showErrorMessage", ["Please select the terms option"]);
                this.$("#termsInfo").addClass("user-error");
            } else {
                this.$("#termsInfo").removeClass("user-error");
            }
                
            return isValid;
        }
    });
    
    /**
     * TaxTabView: Responsible for all of the functionality in the tax tab
     * Events
     *      setUploadLabel: $("#tab2").trigger("setUploadLabel", [country])
     */
    app.views.TaxTabView = Backbone.View.extend({
        el: "#tab2",
        events: {
            "click .back": "backToBasic",
            "click .questions .btn-group a": "environmentQuestionAnswers",
            "change #recipientType": "updateQuestionnaire",
            //Begin of Insert CTI w8 Foreign vendor
            "change #vendorTaxResidenceCountry": "callCTI",
            "click #vendorEntityGroup": "updateW8QuesEntity",
            "click #vendorEntityLocGroup": "updateW8QuesEntityLoc",
            "click #vendorIndvLocGroup": "updateW8QuesIndvLoc",
            "click #vendorIndvResidenceGroup": "updateW8QuesIndvRes",
            "click #vendorIndvPresenceGroup": "updateW8QuesIndvPresence",
            //End of Insert CTI w8 Foreign vendor
            "click #tax-taxid": "showTaxId",
            "click #tax-ssn": "showSsn",
//            "blur #tax-taxid-id": "validateTaxId", // by ganesh
            "focusout #tax-taxid-id": "validateTaxId", // by ganesh // 831
            "focusout #tax-social-id": "validateTaxId", // by ganesh // 831

            "click #tax-taxid-id": "taxIdUnmask", // by ganesh
            "click #tax-social-id": "ssnIdUnmask", // by ganesh
//            "blur #tax-social-id": "validateTaxId", // by ganesh
            "focus #tax-taxid-id": "taxIdUnmask", // by ganesh
            "focus #tax-social-id": "ssnIdUnmask", // by ganesh
            // "blur .tax-taxid": "validateTaxId", // By Naga 999 - 2// ignored
			// lines Ganesh
            // "blur .tax-social": "validateTaxId", // By Naga 999 - 2// ignored
			// lines Ganesh
            "click .exempt": "updateExmPC",
            "click .facta": "updateExmFactC",
            "click .continue": "advanceToTerms",
	    "change #solePropGroup": "updateLEQ",
            "setUploadLabel": "setUploadLabel"
        },
        initialize: function () {
            _.bindAll(this, "backToBasic", "updateQuestionnaire", "callCTI", "updateW8QuesEntity",
            		"updateW8QuesEntityLoc", "updateW8QuesIndvLoc",	"updateW8QuesIndvRes", "updateW8QuesIndvPresence",
            		"showTaxId", "showSsn", "updateExmPC", "updateExmFactC", "showSelection", "advanceToTerms", "checkValidity", "setUploadLabel",
                    "setUploadLabel","validateTaxId", "taxIdUnmask", "ssnIdUnmask");
         // added last two unmasking methods in above binding -ganesh
            //start -ganesh    
            var temptax=this.$('#temp-tax-id').val();
            var tempssn=this.$('#temp-social-id').val();
           if(temptax.length!==9)
           {
        	   this.$(".tax-taxid").mask("99-9999999");	// ENHC0016170  
           }
           else if(tempssn.length!==9){
        	   
          this.$(".tax-social").mask("999-99-9999"); // ENHC0016170
           }
           
           //end -ganesh    
//	          this.$(".tax-taxid").maskX("999999999");	// ENHC0016170
//	          this.$(".tax-social").maskX("999999999"); // ENHC0016170
            
            
            
            //Modify by Kermel Ruperto (SSO: 206441846)
            // Begin of Insert and Comment by Naga ENHC0016170
            var ssnVal = this.$(".tax-social").val();
            var taxVal = this.$(".tax-taxid").val();
//            var ssnVal = this.$("#tax-social-id-original").val();
//            var taxVal = this.$("#tax-taxid-id-original").val();
//            
            
            
            // End of Insert and Comment by Naga 
            
		    // Begin of Insert by Naga ENHC0013685
            // Hide questionnaire if it is Revenue Share Vendor
            // ENHC0013683 Hide questionnaire if it is Garnishment
            if($("#vendorType").val()==="091" || $("#vendorType").val()==="092"){
            	$("#questionexpayee").hide();
            	$("#questionfacta").hide();
            	//$("#solePropGroup").hide();
            	//$("LEQ").hide();
//            	$(".questions").hide();	// ENHC0013668 Impacting other questions, not needed now but we need if questionnaire are un commented
            }
		
		    // End of Insert by Naga
            // Begin of Insert by Naga ENHC0016458 & ENHC0016461
            // ENHC0016458 Independent Contractor questionnaire has to be removed from Utility
            // ENHC0016461 Independent Contractor questionnarie has to be removed and also Supplier Environmnetal Qns from Legal Settlement and Contest Winner
            
            // Added 095,030 - Pranesh(04/16/2016)
//            if($("#vendorType").val()==="080" || $("#vendorType").val()==="091" || $("#vendorType").val()==="093" || $("#vendorType").val()==="094"  || $("#vendorType").val()==="095" || $("#vendorType").val()==="030"){
         	if(($("#vendorType").val() === "091" || $("#vendorType").val() === "092" || $("#vendorType").val() === "093" || $("#vendorType").val() === "094" || $("#vendorType").val() === "095" || $("#vendorType").val() === "030" || $("#vendorType").val() === "040" || $("#vendorType").val() === "050" || $("#vendorType").val() === "999" || $("#vendorType").val() === "080")){	// Ganesh DFCT0017114
            $(".contractor").hide();
            	// Independent Contractor questions are no more required in this case
            	$("[name='independantContractor']").removeAttr("required","");
            	$("#LEQ").hide();
            	$("#LEQ").removeAttr("required","");// Added Pranesh(04/13/2016)  - ENHC0016459
            	
            }
            // Begin of Comment by Naga ENHC0018723
            // Hide Supplier Enviromental Practices
//            if($("#vendorType").val()==="093" || $("#vendorType").val()==="094"){
//            	$("#SEP").hide();
//            }
            // End of Comment by Naga
            
            // End of Insert by Naga
 
            
            if(taxVal)
            {
                elem = $('#tax-taxid');
                this.doShowTaxId(elem);
            }
            else if(ssnVal)
            
            {
                elem = $('#tax-ssn');
                this.doShowSsn(elem);
            }
            else
            {
                var taxInput = $('input[name="taxSsn"][value="tax"]').prop('checked'),
                    ssnInput = $('input[name="taxSsn"][value="ssn"]').prop('checked'),
                    elem;
                
                if(taxInput){
                    elem = $('#tax-taxid');
                    this.doShowTaxId(elem);
                }else if (ssnInput){
                    elem = $('#tax-ssn');
                    this.doShowSsn(elem);
                }
            }
            //End Kermel Ruperto
            $(elem).addClass('active');

            var recepType = $('#recipientType');
            
            if(recepType)
            {               
                this.updateQuestionnaire(recepType);
            }
            $("#ENTITYINDV").slideDown();
            
            this.taxRequiredField($("#vendorType").val());// Added Pranesh(04/17/2016)

        },
        
     // Added Pranesh(04/17/2016)
        taxRequiredField: function (vType) {
         if( vType=='R010' || vType=='V010' || 
        	 vType=='R020' || vType=='V020' ||
        	 vType=='R030' || vType=='V030' ||
        	 vType=='R093' || vType=='V093' ||
        	 vType=='R094' || vType=='V094' ){
        	 	$("#taxexmpc").attr("required","required");
         }else{
        	 	$("#taxexmpc").attr("required","");
         }
        },
        // Added Pranesh(04/17/2016)
        
        // Code Start -Ganesh
        // Field unmasking on ssnid input click
        taxIdUnmask: function (e) {
            var taxdata = $("#temp-tax-id").val();
            if (taxdata.length===9)
            {
            	
                $('#tax-taxid-id').val(taxdata);
                $('#tax-taxid-id').mask("99-9999999");
                $('#tax-social-id').val("");
                $('#temp-social-id').val("");
            }
            else{
            	$('#tax-taxid-id').mask("99-9999999");
            	
            }

        },
        // Field unmasking on taxid input click
        ssnIdUnmask: function (e) {
            var ssndata = $("#temp-social-id").val();
            if (ssndata.length===9)
            {
                $('#tax-social-id').val(ssndata);
                $('#tax-social-id').mask("999-99-9999");
                $('#tax-taxid-id').val('');
                $('#temp-tax-id').val('');

            }
            else{
            	 $('#tax-social-id').mask("999-99-9999");
            	
            }
        },
        // code End-Ganesh
     
        advanceToTerms: function (e) {
            if (this.checkValidity()) {
            	if (($("#vendorType").val() === "093") || ($("#vendorType").val() === "094")  || ($("#vendorType").val() === "095")){// Added (095) - Pranesh (04/28/2015)-(Defect ID : 15048),to bypass TERM Tab screen
            		this.$el.trigger("completeTaxTab")
	                .trigger("prepareBankingTab")
	                .trigger("showBankingTab"); 
            	} else{
            		this.$el.trigger("prepareTermsTab")
                    .trigger("completeTaxTab")
                    .trigger("showTermsTab");
            	}
            }
            e.preventDefault();
            $('html, body').animate({ scrollTop: 0 }, 0);
        },
        checkValidity: function () {
            var isValid = true;
            // Begin of Insert by Naga
            var vendorEntity = $("input[name=vendorEntity]:checked").val();
            var vendorEntityLoc = $("input[name=vendorEntityLoc]:checked").val();
            var vendorIndvLoc = $("input[name=vendorIndvLoc]:checked").val();
            var vendorIndvResidence = $("input[name=vendorIndvResidence]:checked").val();
            var vendorIndvPresence = $("input[name=vendorIndvPresence]:checked").val();
            var reqType = $("#requestType").val();
            
        	var entityDomesticVendor,
    		individualDomesticVendor,
    		entityForeignVendor,
    		individualForeignVendor;
    	
	    	if(vendorEntity == 4 && vendorEntityLoc == 1){
	    		entityForeignVendor = true;
	    	}else if(vendorEntity == 3 && (vendorIndvLoc == 2 && vendorIndvResidence == 2 && vendorIndvPresence == 2)){
	    		individualForeignVendor = true;
	    	}else if(vendorEntity == 4){
	    		// There are cases where user might not have filled in all the questions. But anyway those are supposed to be filled and let the user get error
	    		entityDomesticVendor = true;
	    	}else if(vendorEntity == 3){
	    		// There are cases where user might not have filled in all the questions. But anyway those are supposed to be filled and let the user get error
	    		individualDomesticVendor = true;
	    	}            
            
            // End of Insert by Naga
            
            this.$("input, select, radio, textarea").each(function(idx, itm) {
                if (!$(itm).checkValidity()) {
                	// Begin of Insert by Naga
                	// Based on Domestic or Foreign vendor qns validations have to be done / ignored.
                    
                	// START DFCT0016715 - Validity error for IC
                	var tmpVal = $("input[name=independantContractor]:checked").val(); 
                	
                	// Added Pranesh (04/15/2016)- ENHC0016459
                	var taxInput = false;
                    if($('#tax-taxid').hasClass('active')){
                    	taxInput = true;
                    }
                    
                    var ssnInput = false;
                    if($('#tax-ssn').hasClass('active')){
                    	ssnInput = true;
                    }
                    // Added Pranesh Pranesh (04/15/2016) - ENHC0016459
                	
                    // END DFCT0016715 - Validity error for IC
                	
                	if(entityForeignVendor){
                		if($("#INDIVLOC").has(itm).length){
                    	}else if($("#PERMRES").has(itm).length){
                    	}else if($("#SUBPRESENCE").has(itm).length){
                    	}else if(reqType == 1 && $("#W8VERIFY").has(itm).length){ // ENHC0013673
                    	}else if(reqType == 2 && $("#TAXRES").has(itm).length){	// ENHC0013673
//                    	}else if(reqType == 2 && $("#W8PREP").has(itm).length){	// ENHC0013673 1228
                    	}else if($("#W9UPLOAD").has(itm).length){
                    	}else if($("#TAXCATEGORY").has(itm).length){
                    	}else if($("#TAXEXEMPT").has(itm).length){
//                    	}else if($("#SEP").has(itm).length){		// ENHC0018723
                    	}else{
                    		isValid = false;
                    	}
                	}else if(individualForeignVendor){
                		if($("#ENTITYLOC").has(itm).length){
                    	}else if(reqType == 1 && $("#W8VERIFY").has(itm).length){	// ENHC0013673
                    	}else if(reqType == 2 && $("#TAXRES").has(itm).length){		// ENHC0013673
//                    	}else if(reqType == 2 && $("#W8PREP").has(itm).length){		// ENHC0013673 1228
                    	}else if($("#W9UPLOAD").has(itm).length){
                    	}else if($("#TAXCATEGORY").has(itm).length){
                    	}else if($("#TAXEXEMPT").has(itm).length){
//                    	}else if($("#SEP").has(itm).length){			// ENHC0018723
                    	}else{
                    		isValid = false;
                    	}                		
                	}else if(entityDomesticVendor){
                		if($("#INDIVLOC").has(itm).length){
                    	}else if($("#PERMRES").has(itm).length){
                    	}else if($("#SUBPRESENCE").has(itm).length){
                    	//}else if($("#W9UPLOAD").has(itm).length){   // Added - Pranesh(04/13/2016), Blocked - Pranesh(04/14/2016)  			 - ENHC0016459
                    	}else if(reqType == 1 && $("#W8VERIFY").has(itm).length){  // Pranesh - (05/17/2016) - (Defect:15095)
                    	}else if($("#W8VERIFY").has(itm).length){	  // ENHC0013673,Blocked Pranesh(04/13/2016), Released - Pranesh(04/14/2016) - ENHC0016459
                    	}else if($("#TAXRES").has(itm).length){       // ENHC0013673,Blocked Pranesh(04/13/2016), Released - Pranesh(04/14/2016) - ENHC0016459
//                    	}else if($("#W8PREP").has(itm).length){		  // ENHC0013673	1228
                    	}else{
                    		//(vendorType - 095) - Added by Pranesh(05-04-2016) - ENHC0016459 
                    		// To bypass Exempt Payee Code validation for this specific vendortype
                    		
//                      	if(!($("#vendorType").val() === "091" || $("#vendorType").val() === "092"  || $("#vendorType").val() === "093" || $("#vendorType").val() === "095" || $("#vendorType").val() === "030" || $("#vendorType").val() === "040" || $("#vendorType").val() === "050" || $("#vendorType").val() === "999" || $("#vendorType").val() === "080")){	// Ganesh DFCT0017114
                            if ((($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")  || ($("#vendorType").val() === "018") || ($("#vendorType").val() === "090"))) { // Ganesh DFCT0017114
                    		// Exempt and Facta questionnaire is hidden from Revenue share and Garnishment Vendor types
                            		isValid = false;
                              
                        		// START DFCT0016715 - IC validity issue for option none of the above,Pranesh (04/15/16) - ENHC0016459
                        		if ($(itm).parent().parent().attr("id")== "solePropGroup" || $(itm).parent().parent().attr("id")== "independantContractor" ) {
                                    if ( ssnInput == true ){
                                    isValid = true;
                                    }
                                           }
                                if ($(itm).attr("name").indexOf("taxSsnQ3") > -1) {
                                    if ( taxInput == true && tmpVal == 4 ){
                                    isValid = true;
                                    }
                                    }
                                // END - IC validity issue for option none of the above,Pranesh (04/15/16) - ENHC0016459
                        		
        	                    if($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc") {
        	                    	$(itm).next().addClass("user-error");
        	                    }
        	                    
        	                    
                        	}else{	// ENHC0013685 &&  ENHC0013683
                        		if(!($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc" || $(itm).attr("name") == "independantContractor" || $(itm).attr("name").indexOf("taxSsnQ3") > -1))	// ENHC0013685 &&  ENHC0013683// Ganesh DFCT0017114
                        			isValid = false;	// ENHC0013685 &&  ENHC0013683
                        	}
                            if($(itm).attr("name") == "taxexmpc") { // ganesh DFCT0017543
    	                    	$(itm).next().addClass("user-error");
    	                    }
                        	// ENHC0013685 &&  ENHC0013683
                            // Added by CGUTJAHR #60
                        	// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
                        	
                        	// Added -018 - Pranesh(04/13/2016)  - ENHC0016459
                        	
                        	// Pranesh (05/23/2016) - Did nothing
                        	if (($("#vendorId").val()==null || $("#vendorId").val().length == 0)&&( ($("#vendorType").val() === "010") || ($("#vendorType").val() === "020") || ($("#vendorType").val() === "018") || ($("#vendorType").val() === "090"))) {
        	                    if($(itm).attr("name").indexOf("taxSsnQ3") > -1) {
        	                    	$(itm).closest('.accordion-heading').prev().addClass("question-user-error");
        	                    }
        	                    
        	                    // Begin of Comment and Insert by Naga ENHC0016123
        	                    /*if($(itm).parent().parent().attr("id")== "solePropGroup") {
        	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
        	                    }*/
        	                    if($(itm).parent().parent().attr("id")== "solePropGroup"){
        	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
        	                    }
        	                    // End of Comment and Insert by Naga
        	                    
                            }                    		
                    	}
                	}else if(individualDomesticVendor){
                		if($("#ENTITYLOC").has(itm).length){
                		}else if(reqType == 1 && $("#W8VERIFY").has(itm).length){    // Pranesh - (05/17/2016) - (Defect:15095)
                    	}else if($("#W8VERIFY").has(itm).length){		// ENHC0013673,Blocked Pranesh(04/13/2016)  - ENHC0016459  // Pranesh - (05/17/2016) - (Defect:15095)
                    	//}else if($("#TAXRES").has(itm).length){		// ENHC0013673,Blocked Pranesh(04/13/2016)  - ENHC0016459
                    	//}else if($("#W9UPLOAD").has(itm).length){       // Added - Pranesh(04/13/2016),Blocked Pranesh(04/15/2016)- ENHC0016459
//                    	}else if($("#W8PREP").has(itm).length){			// ENHC0013673	1228
                    	}else if(vendorIndvResidence==null&&($("#PERMRES").has(itm).length)){
                    	}else if(vendorIndvPresence==null&&($("#SUBPRESENCE").has(itm).length)){
                    	}else{
                    		//(vendorType -095) - Added by Pranesh - ENHC0016459 - (05-04-2016)
                    		// To bypass Exempt Payee Code validation for this specific vendortype
//                     	if(!($("#vendorType").val() === "091" || $("#vendorType").val() === "092" || $("#vendorType").val() === "093" || $("#vendorType").val() === "095" || $("#vendorType").val() === "030" || $("#vendorType").val() === "040" || $("#vendorType").val() === "050" || $("#vendorType").val() === "999" || $("#vendorType").val() === "080")){	// Ganesh DFCT0017114
                            if ((($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")  || ($("#vendorType").val() === "018") || ($("#vendorType").val() === "090"))) { // Ganesh DFCT0017114

                    		isValid = false;
                               
                        		// START DFCT0016715 - IC validity issue for option none of the above,Pranesh (04/15/16) - ENHC0016459
                        		if ($(itm).parent().parent().attr("id")== "solePropGroup" || $(itm).parent().parent().attr("id")== "independantContractor" ) {
                                    if ( ssnInput == true ){
                                    isValid = true;
                                    }
                                           }
                                if ($(itm).attr("name").indexOf("taxSsnQ3") > -1) {
                                    if ( taxInput == true && tmpVal == 4 ){
                                    isValid = true;
                                    }
                                    }
                                // END - IC validity issue for option none of the above,Pranesh (04/15/16) - ENHC0016459
        	                   
                                if($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc") {
        	                    	$(itm).next().addClass("user-error");
        	                    }
                       	}else{	// ENHC0013685 &&  ENHC0013683
                        		if(!($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc" || $(itm).attr("name") == "independantContractor" || $(itm).attr("name").indexOf("taxSsnQ3") > -1))	// ENHC0013685 &&  ENHC0013683// Ganesh DFCT0017114
                        			isValid = false; // ENHC0013685 &&  ENHC0013683
                        	}// ENHC0013685 &&  ENHC0013683
                            // Added by CGUTJAHR #60
                        	// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
                            if($(itm).attr("name") == "taxexmpc") { // ganesh DFCT0017543
    	                    	$(itm).next().addClass("user-error");
    	                    }
                        	// Added 018 - Pranesh (04/13/2016)  - ENHC0016459
                            if ((($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")  || ($("#vendorType").val() === "018") || ($("#vendorType").val() === "090"))) {
        	                    if($(itm).attr("name").indexOf("taxSsnQ3") > -1) {
        	                    	$(itm).closest('.accordion-heading').prev().addClass("question-user-error");
        	                    }
        	                    
        	                    // Begin of Comment and Insert by Naga ENHC0016123
        	                    /*if($(itm).parent().parent().attr("id")== "solePropGroup") {
        	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
        	                    }*/
        	                    if($(itm).parent().parent().attr("id")== "solePropGroup"){
        	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
        	                    }
        	                    // End of Comment and Insert by Naga
        	                    
                            }                    		
                    	}                		
                	}else{
                		isValid = false;
                	}
                	if(entityDomesticVendor || individualDomesticVendor){
                	// End of Insert by Naga
//                	if(!($("#vendorType").val() === "091" || $("#vendorType").val() === "092")){	// ENHC0013685 &&  ENHC0013683
//                		// Exempt and Facta questionnaire is hidden from Revenue share and Garnishment Vendor types
//                		isValid = false;
//	                    if($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc") {
//	                    	$(itm).next().addClass("user-error");
//	                    }
//                	}else{	// ENHC0013685 &&  ENHC0013683
//                		if(!($(itm).attr("id") == "taxexmpc" || $(itm).attr("id") == "taxexmfactc"))	// ENHC0013685 &&  ENHC0013683
//                			isValid = false;															// ENHC0013685 &&  ENHC0013683
//                	}// ENHC0013685 &&  ENHC0013683
//                    // Added by CGUTJAHR #60
//                    if (($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")) {
//	                    if($(itm).attr("name").indexOf("taxSsnQ3") > -1) {
//	                    	$(itm).closest('.accordion-heading').prev().addClass("question-user-error");
//	                    }
//	                    
//	                    // Begin of Comment and Insert by Naga ENHC0016123
//	                    /*if($(itm).parent().parent().attr("id")== "solePropGroup") {
//	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
//	                    }*/
//	                    if($(itm).parent().parent().attr("id")== "solePropGroup"){
//	                    	$(itm).parent().parent().parent().prev().addClass("question-user-error");
//	                    }
//	                    // End of Comment and Insert by Naga
//	                    
//                    }
                }
                    // End #60                   
                    
                }
                if(!isValid) return false; // Added - Pranesh(04/17/2016)
            });
            
            /*            if (this.$(".ssn").is(":checked") || this.$(".tax").is(":checked")) {
                            this.$(".taxInfo").removeClass("user-error");
                        } else {
                            this.$("#taxInfo").addClass("user-error");
                            this.$el.trigger("showErrorMessage", ["Please select a tax option"]);
                            return isValid;
                        }*/
            
            if(!isValid) {
                this.$el.trigger("showErrorMessage", ["Check Error Messages, Required Fields, Questions, and Remove Any Invalid Characters"]);
            }
            return isValid;
        },
        setUploadLabel: function(e, country) {
            var taxInfoUpload = this.$('#taxInfo').find('.upload-forms .pull-left label');
            var socialUpload = this.$('#social').find('.upload-forms .pull-left label');
            var taxInfoDownload =  this.$('#taxInfo').find(".upload-forms .pull-right");
            var socialDownload = this.$('#social').find(".upload-forms .pull-right");
            var taxForm = '';
            if (country === "US") {
                taxForm = "W9";
            } else {
                taxForm = "W8";
            }
            taxInfoUpload.text("Upload " + taxForm);
            socialUpload.text("Upload " + taxForm);
            taxInfoDownload.html('<i class="icon-file"></i>Download ' + taxForm);
            socialDownload.html('<i class="icon-file"></i>Download ' + taxForm);
        },
	//Bug #49 Start
	updateLEQ: function(e)
	{
        // Begin of Insert by Naga ENHC0013685
        // Hide questionnaire if it is Revenue Share Vendor
        // ENHC0013683 Hide questionnaire if it is Garnishment Vendor	
        if($("#vendorType").val()==="091" || $("#vendorType").val()==="092"){
        	return;
        }
        // End of Insert	
		var tmpVal = $("input[name=independantContractor]:checked").val();
	
		if(e == "ssn"){
			
			$("#LEQ").slideDown();
			
			// Added - ganesh - (05/26/2016)-  DFCT0017158
			if($("#requestType").val()=="2" && ($("#vendorType").val()=="095"  || $("#vendorType").val()=="030" ))
			{
				$("[name^='taxSsnQ']").prop("required","");
	            $("input[name=independantContractor]").prop("required", "");

			}
			else if($("#requestType").val()=="2")
			{
				$("[name^='taxSsnQ']").prop("required","required");
			}else if( $("#requestType").val()=="1" && ( $("#vendorType").val()=="010" || $("#vendorType").val()=="020" || $("#vendorType").val()=="018" || $("#vendorType").val()=="090" ) ) { 
					$("[name^='taxSsnQ']").prop("required","required");
				}
			else{
				$("[name^='taxSsnQ']").prop("required","");

			}
			// Added - ganesh - (05/26/2016)-  DFCT0017158
			
		} else if(e == "taxid") {
			
			if(tmpVal == 1 || tmpVal == 2 || tmpVal == 3) { // Naga ENHC0016123
			   
				$("#LEQ").slideDown();
				// Added - ganesh - (05/26/2016)-  DFCT0017158
					if( $("#requestType").val()=="1" && ( $("#vendorType").val()=="010" || $("#vendorType").val()=="020" || $("#vendorType").val()=="018" || $("#vendorType").val()=="090" ) ) { 
						$("[name^='taxSsnQ']").prop("required","required");
					}
			    // Added - ganesh - (05/26/2016)-  DFCT0017158
			} else {
				$("#LEQ").slideUp();
			}
		
		} else if(tmpVal == 1 || tmpVal == 2 || tmpVal == 3) {	// Naga ENHC0016123
			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
			
			// Added 018 Pranesh(04/13/2016) - ENHC0016459
			// Removed 095,bcoz IC questions are not mandatory for "Posthumous Payments" - Pranesh(04/14/2016) - ENHC0016459
			
			//Begin Added - Pranesh - (05/23/2016) - DFCT0017158
			var requestType=$("#requestType").val(); 
		 if ((($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")  || $("#vendorType").val()=="018" || $("#vendorType").val()=="090")) {
				$("[name^='taxSsnQ']").prop("required","required");
//				 $("#LEQ").find(".accordion-body").addClass("question-user-error");
			}
		 else if (!(($("#vendorType").val() === "010") || ($("#vendorType").val() === "020")  || $("#vendorType").val()=="018" || $("#vendorType").val()=="090")) {
		
		            $("[name^='taxSsnQ']").removeAttr("required","");

			 }
			//End Added - Pranesh - (05/23/2016) - DFCT0017158
			
			$("#LEQ").slideDown();
	    	//$("#solePropGroup").parent().parent().removeClass("question-user-error"); // ENHC0016123
			$("#solePropGroup").parent().prev().removeClass("question-user-error"); // ENHC0016123
		} else {
            $("[name^='taxSsnQ']").removeAttr("required","");
			$("#LEQ").slideUp();
	    	//$("#solePropGroup").parent().parent().removeClass("question-user-error"); ENHC0016123
	    
			$("#solePropGroup").parent().prev().removeClass("question-user-error");  // ENHC0016123
		}
	},
	//Bug #49 End
        //Req#603 START Code change by AGAMPA 23Feb2015
        updateQuestionnaire: function(e){
        	var target = $(e.currentTarget);
        	var selectVal = target.val();        	
      	
        	// Begin of comment by Naga ENHC0018723
        	// Hide Supplier Environmental Questions
//        	if(selectVal!=null && selectVal != "" && selectVal != "05" && selectVal != "01")
//        	{
//        		$("#SEP").slideDown();
//        	}else{
//			$("#SEP").slideUp();
//		}
        	// End of comment by Naga
        },
        //Begin of Insert CTI w8 Foreign vendor
	    callCTI: function(e){
        	var that = this,
        		taxCountryCTI = this.$('#vendorTaxResidenceCountry option:selected');
	        var usrName = $("#userName").val();
	        var reqId = $("#requestId").val();
	        var reqType = $("#requestType").val();
            var vendorEntity = $("input[name=vendorEntity]:checked").val();
            var vendorEntityLoc = $("input[name=vendorEntityLoc]:checked").val();
            var vendorIndvLoc = $("input[name=vendorIndvLoc]:checked").val();
            var vendorIndvResidence = $("input[name=vendorIndvResidence]:checked").val();
            var vendorIndvPresence = $("input[name=vendorIndvPresence]:checked").val();	        
	        //if registered by vendor - call CTI
        	//if(reqType === "1"){
		            
		        // CTI call should only happen for foreign vendor
		        if(taxCountryCTI.val() !== ""){
//		        	$("#W8PREP").slideUp();		// ENHC0013673	1228
		            this.$el.trigger("showSpinner");
		
		            $.ajax({
		                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=CTICall",
		                type: "POST",
		                async: false,
		                data: { taxCountryCTI: taxCountryCTI.val(), SSO: usrName, reqIdCTI: reqId, reqTypeCTI: reqType, vendorEntity:vendorEntity,vendorEntityLoc:vendorEntityLoc,vendorIndvLoc:vendorIndvLoc,vendorIndvResidence:vendorIndvResidence,vendorIndvPresence:vendorIndvPresence},
		                dataType: "json"
		            }).done(function (data) {
	                    var err = data[0].message;
	                    if(err!=null&&err.length){
	                    	that.$el.trigger("showErrorMessage", [data[0].message]);
	                    }else if(data[0].CTI.length == 1){
		                    for(var i = 0; i < data[0].CTI.length; i++){
		                        var entry = data[0].CTI[i];
		                        var tcnty =  entry.taxCountry;
		                        $("#urlCTI").val(entry.url);				// ENHC0013673	1228
		                        $("#ernamCTI").val(entry.clientId);			// ENHC0013673	1228
		                        $("#regCodeCTI").val(entry.registrationcode);	// ENHC0013673	1228
//		                        $("#CTIURL").html("<a target=_blank href=\""+entry.url+"\">"+entry.url+"</a>\n");
////		                        $("#CTIREGCODE").text("Client ID: "+entry.clientId+"    Registration Code: "+entry.registrationcode);
//		                        $("#CTIREGCODE").html("Client ID: <strong>" + entry.clientId + "</strong>    Registration Code: <strong>" + entry.registrationcode +"</strong>");
////		                        $("#W8PREP").slideDown();		// ENHC0013673
		                    }
		                    
		                }
		            }).always(function () {
		                that.$el.trigger("hideSpinner");
		            }).fail(function(data){
		                console.log('failed');
		            });
			        
		        	//for foreign vendor clear tax id 2701
//		        	this.$(".tax-social").val("");
//		        	this.$(".tax-taxid").val("");
		        	//$("input[name=independantContractor]").prop('checked', false);
		        	this.$("#vendorTaxResidenceCountry").prop("required", false);
		        }
		        //else if country option is changed back to "select one"
		        else{
//		        	$("#W8PREP").slideUp();			// ENHC0013673	1228
		        }
	        //}
	    },
	    callCTIOnBehalf: function(elem){
        	var that = this;
        	var taxCountry = "";
	        var usrName = $("#userName").val();
	        var reqId = $("#requestId").val();
	        var reqType = $("#requestType").val();
            var vendorEntity = $("input[name=vendorEntity]:checked").val();
            var vendorEntityLoc = $("input[name=vendorEntityLoc]:checked").val();
            var vendorIndvLoc = $("input[name=vendorIndvLoc]:checked").val();
            var vendorIndvResidence = $("input[name=vendorIndvResidence]:checked").val();
            var vendorIndvPresence = $("input[name=vendorIndvPresence]:checked").val();	        
	        //if registered by vendor - call CTI
        	//if(reqType === "2"){
		            
		            this.$el.trigger("showSpinner");
		
		            $.ajax({
		                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=CTICall",
		                type: "POST",
		                async: false,
		                data: { taxCountryCTI: taxCountry, SSO: usrName, reqIdCTI: reqId, reqTypeCTI: reqType,vendorEntity: vendorEntity,vendorEntityLoc: vendorEntityLoc,vendorIndvLoc: vendorIndvLoc,vendorIndvResidence: vendorIndvResidence,vendorIndvPresence: vendorIndvPresence},
		                dataType: "json"
		            }).done(function (data) {
		                 
		                    var err;
		                    err = data[0].message;
		                    
		                    if(data[0].message == ""){
		                    	$("#W8VERIFY").slideDown();
		                    }
		                    else{
		                    	that.$el.trigger("showErrorMessage", [data[0].message]);
		                    }
		            }).always(function () {
		                that.$el.trigger("hideSpinner");
		            }).fail(function(data){
		                console.log('failed');
		            });
	        //}
	    },
        updateW8QuesEntity: function(e){
        	var tmpVal = $("input[name=vendorEntity]:checked").val();
        	$("input[name=vendorEntityLoc]").prop('checked', false);
    		$("input[name=vendorIndvLoc]").prop('checked', false);
    		$("input[name=vendorIndvResidence]").prop('checked', false);
    		$("input[name=vendorIndvPresence]").prop('checked', false);
    		$("#PERMRES").slideUp();
    		$("#SUBPRESENCE").slideUp();
    		$("#TAXRES").slideUp();			// ENHC0013673
//    		$("#W8PREP").slideUp();			// ENHC0013673	1228
			$("#W8VERIFY").slideUp();		// ENHC0013673
			$("#TAXCATEGORY").slideUp();
			$("#TAXEXEMPT").slideUp();
    		$("#W9UPLOAD").slideUp();
    		if(tmpVal == 4)
        	{
        		$("#ENTITYLOC").slideDown();
        		//clear entity loc
        		$("input[name=vendorEntityLoc]").prop('checked', false);
        		$("#INDIVLOC").slideUp();
        	}else{
        		$("#ENTITYLOC").slideUp();
        		$("#INDIVLOC").slideDown();
        		//clear indv loc
        		$("input[name=vendorIndvLoc]").prop('checked', false);
			}
        },
        updateW8QuesEntityLoc: function(e){
        	var tmpVal = $("input[name=vendorEntityLoc]:checked").val();
        	var reqType = $("#requestType").val();
        	$("input[name=vendorIndvLoc]").prop('checked', false);
    		$("input[name=vendorIndvResidence]").prop('checked', false);
    		$("input[name=vendorIndvPresence]").prop('checked', false);
    		$("#INDIVLOC").slideUp();
    		$("#PERMRES").slideUp();
			$("#SUBPRESENCE").slideUp();
			$("#vendorTaxResidenceCountry").val("");
        	if(tmpVal == 1)
        	{
        		//if foreign vendor clear tax id 2701
//        		this.$(".tax-social").val("");
//            	this.$(".tax-taxid").val("");
//            	$('#temp-tax-id').val('');			// Ganesh's change
//            	$('#temp-social-id').val('');       // Ganesh's change     	 
//            	$("input[name=taxSsn]").prop('checked', false);
//        		$("input[name=independantContractor]").prop('checked', false);
//        		$("#W9UPLOAD").find("[type=file]").next().remove(); // ENHC0013668 
//            	$("#W9UPLOAD").find('.fileName').remove();  // ENHC0013668
//            	$("#W9UPLOAD").find('.icon-remove').remove();  // ENHC0013668
        		$("#TAXCATEGORY").slideUp();
        		$("#TAXEXEMPT").slideUp();
        		$("#W9UPLOAD").slideUp();
//        		$("#SEP").slideUp();	// ENHC0018723
    	        //if register by vendor show tax country - call CTI
            	if(reqType === "1"){
        			$("#TAXRES").slideDown();		// ENHC0013673
//            		$("#W8PREP").slideUp();			// ENHC0013673 1228
        			$("#W8VERIFY").slideUp();		// ENHC0013673
        		}
        		//register on behalf - do not show tcnty
            	else{
            		$("#TAXRES").slideUp();			//	ENHC0013673
//            		$("#W8PREP").slideUp();			//	ENHC0013673	1228
//            		$("#W8VERIFY").slideDown();		//	ENHC0013673
            		this.callCTIOnBehalf("");
        		}
        	}else{
        		$("#TAXRES").slideUp();				//	ENHC0013673
//        		$("#W8PREP").slideUp();				//	ENHC0013673 1228
    			$("#W8VERIFY").slideUp();			//	ENHC0013673
        		//show tax categories
        		$("#TAXCATEGORY").slideDown();
        		$("#TAXEXEMPT").slideDown();
        		$("#W9UPLOAD").slideDown();
//        		$("#SEP").slideDown();				// ENHC0018723
        	}
 // code start -ganesh
            
//            $('#tax-social-id').val('');
//        	$('#temp-social-id').val('');
//        	$('#tax-taxid-id').val('');
//        	$('#temp-tax-id').val('');
            
            //code end-ganesh 	
        	
//        	$('#temp-tax-id').val('');
//        	$('#temp-social-id').val('');        	
        },
        updateW8QuesIndvLoc: function(e){
		
        	var tmpVal = $("input[name=vendorIndvLoc]:checked").val();
        	$("input[name=vendorEntityLoc]").prop('checked', false);
    		$("input[name=vendorIndvResidence]").prop('checked', false);
    		$("input[name=vendorIndvPresence]").prop('checked', false);
        	$("#ENTITYLOC").slideUp();
    		$("#SUBPRESENCE").slideUp();
    		$("#TAXRES").slideUp();			//	ENHC0013673
//    		$("#W8PREP").slideUp();			//	ENHC0013673 1228
			$("#W8VERIFY").slideUp();		//	ENHC0013673
			if(tmpVal == 1)
        	{
        		$("#PERMRES").slideUp();
        		$("#TAXCATEGORY").slideDown();
        		$("#TAXEXEMPT").slideDown();
        		$("#W9UPLOAD").slideDown();
        		        	


}else if(tmpVal == 2){
        		$("#PERMRES").slideDown();
        		$("#TAXCATEGORY").slideUp();
        		$("#TAXEXEMPT").slideUp();
        		$("#W9UPLOAD").slideUp();

				     	}
        },
        updateW8QuesIndvRes: function(e){
        	var tmpVal = $("input[name=vendorIndvResidence]:checked").val();
        	$("input[name=vendorEntityLoc]").prop('checked', false);
    		$("input[name=vendorIndvPresence]").prop('checked', false);
        	$("#ENTITYLOC").slideUp();
    		$("#TAXRES").slideUp();			//	ENHC0013673			
//    		$("#W8PREP").slideUp();			//	ENHC0013673 1228
			$("#W8VERIFY").slideUp();		//	ENHC0013673
			if(tmpVal == 1)
        	{
        		$("#SUBPRESENCE").slideUp();
        		$("#TAXCATEGORY").slideDown();
        		$("#TAXEXEMPT").slideDown();
        		$("#W9UPLOAD").slideDown();
        	}else if(tmpVal == 2){
        		$("#SUBPRESENCE").slideDown();
        		$("#TAXCATEGORY").slideUp();
        		$("#TAXEXEMPT").slideUp();
        		$("#W9UPLOAD").slideUp();
        	}
        },
        updateW8QuesIndvPresence: function(e){
			
        	var tmpVal = $("input[name=vendorIndvPresence]:checked").val();
        	var reqType = $("#requestType").val();
        	$("input[name=vendorEntityLoc]").prop('checked', false);
        	$("#ENTITYLOC").slideUp();
        	$("#vendorTaxResidenceCountry").val("");
        	if(tmpVal == 1)
        	{
        		$("#TAXRES").slideUp();			//	ENHC0013673
//        		$("#W8PREP").slideUp();			//	ENHC0013673 1228
    			$("#W8VERIFY").slideUp();		//	ENHC0013673
        		//show tax categories
        		$("#TAXCATEGORY").slideDown();
        		$("#TAXEXEMPT").slideDown();
        		$("#W9UPLOAD").slideDown();
//        		$("#SEP").slideDown();				// ENHC0018723
        	}else if(tmpVal == 2 && !(vendorType=="095") ){
        	
        	        		
//        		//if foreign vendor clear tax id 2701
//        		this.$(".tax-social").val("");
//            	this.$(".tax-taxid").val("");
//            	$("input[name=taxSsn]").prop('checked', false);
//        		$("input[name=independantContractor]").prop('checked', false);
//            	$("#W9UPLOAD").find("[type=file]").next().remove();	// ENHC0013668 
//            	$("#W9UPLOAD").find('.fileName').remove();		// ENHC0013668 
//            	$("#W9UPLOAD").find('.icon-remove').remove();	// ENHC0013668 
        		$("#TAXCATEGORY").slideUp();
        		$("#TAXEXEMPT").slideUp();
        		$("#W9UPLOAD").slideUp();
//        		$("#SEP").slideUp();			// ENHC0018723
    	        //if register by vendor show tax country - call CTI
            	if(reqType === "1"){
        			$("#TAXRES").slideDown();	//	ENHC0013673
//            		$("#W8PREP").slideUp();		//	ENHC0013673 1228
        			$("#W8VERIFY").slideUp();	//	ENHC0013673
        		}
            	//register on behalf - do not show tcnty
            	else{
            		$("#TAXRES").slideUp();		//	ENHC0013673
//            		$("#W8PREP").slideUp();		//	ENHC0013673	1228
//            		$("#W8VERIFY").slideDown();	//	ENHC0013673
            		this.callCTIOnBehalf("");
        		}
        	}
        },//End of Insert CTI w8 Foreign vendor
        updateExmPC: function(e){
        	var target = $(e.currentTarget);
        	$("#taxexmpc").val(target.val());
        	$("#taxexmpc").next().removeClass("user-error");
        	$(".taxexmpc").remove();
        },
        updateExmFactC: function(e){
        	var target = $(e.currentTarget);
        	$("#taxexmfactc").val(target.val());
        	$("#taxexmfactc").next().removeClass("user-error");
        	$(".taxexmfactc").remove();
        },
        //Req#603 END
        showTaxId: function (e) {
            
        	 if (this.$(".tax-social").val())
             {
                 this.$(".tax-social").val("");
                 this.$("#temp-social-id").val("");//ganesh
                 this.$(".tax-social").setCustomValidity(''); // Naga	// ENHC0016165
                 this.$(".tax-social").next().slideUp();
                 // this.$("#tax-social-id-original").val(""); // Naga ENHC0016170
             }
            
            this.doShowTaxId(e.currentTarget);
            e.preventDefault();

            //<< Jorge Perez (206443532)
            var group = $("#taxid").find(".accordion-group");
            //group.hide().find("[type=radio]").attr("required","").hide().first().show();
            //group.first().show().find(".accordion-question").addClass("question-user-error");
            group.first().show().find(".accordion-question");
            //$("#social").find("[type=radio]").removeAttr("required").hide();
            if($("#taxid").find("[type=file]").next().length === 0)
                $("#taxid").find("[type=file]").attr("required","").addClass("user-error");;

            //>>
	    this.updateLEQ("taxid");
        },
        showSsn: function (e) {
            
            if (this.$(".tax-taxid").val())
            {	
            	this.$("#temp-tax-id").val("");// ganesh
                this.$(".tax-taxid").val("");
                this.$(".tax-taxid").setCustomValidity(''); // Naga ENHC0016165
                this.$(".tax-taxid").next().slideUp();
                // this.$("#tax-taxid-id-original").val(""); // Naga ENHC0016170
            }

            
            this.doShowSsn(e.currentTarget);
            e.preventDefault();

            //<< Jorge Perez (206443532)
            var group = $("#social").find(".accordion-group");
            group.find("[type=radio]").attr("required","");
            //group.find(".accordion-question").addClass("question-user-error");
            //$("#taxid").find("[type=radio]").removeAttr("required").hide(); // Naga ENHC0016123
            if($("#social").find("[type=file]").next().length === 0)
                $("#social").find("[type=file]").attr("required","").addClass("user-error");

            //>>
	    this.updateLEQ("ssn");
        },
        doShowTaxId: function (elem){
            var target = $(elem).data("target"),
               option = $(elem).data("option");
            $("#tax-taxid-id").attr("required","required"); // ganesh
            $("#tax-social-id").removeAttr("required");      // ganesh   
            $("#tax-social-id").setCustomValidity('');       // ganesh614 DFCT0017158 
            // Blocked Pranesh(04/20/2016) - ENHC0016459
            // IndependantContractor option make it select - (Production Vendor)
            //$('input[name=independantContractor]').prop('checked',false);// ganesh,
            
            this.showSelection(target, option);
            this.$("#social textarea").each(function () {
                $(this).removeAttr("required").removeClass("user-error");
            });
            
            //Added by Craig ..
            var vendorType = $("#vendorType").val();
            // Naga ENHC0013685 Add Revenue Share Vendor type 091
            // Naga ENHC0013683 Add Garnishment Vendor type 092
            // Naga ENHC0016458 & ENHC0016461 Add Utility, Legal Settlement and Contest Winner
            // Added 018,095 - Pranesh (04/13/2016) - ENHC0016459
            if ((vendorType === "010") || (vendorType === "020") || (vendorType === "030")  || (vendorType === "090") || (vendorType === "091") || (vendorType === "092") || (vendorType === "080") || (vendorType === "093") || (vendorType === "094") || (vendorType === "018") || (vendorType === "095")  ){
                this.$(".tax-social").prop("required", false); 
                this.$(".tax-taxid").prop("required", true);               
            }
            //Req#51 START Code added by AGAMPA 
            //$("#social").find("[type=text]").removeAttr("required","");          
            //$("#taxid").find("[type=text]").attr("required","required");
            //Req#56 START Code added by Craig 
            // Begin of comment and insert by Naga
//            $("[name^='taxSsnQ']").removeAttr("required","");
            
         // Added - ganesh - (05/26/2016)-  DFCT0017158
        	if($("#requestType").val()=="2" && ($("#vendorType").val()=="095" || $("#vendorType").val()=="030"))
			{
	            $("input[name=independantContractor]").removeAttr("required");
			}
        	else{
            $("input[name=independantContractor]").prop("required", "required");
        	}
            var tmpVal = $("input[name=independantContractor]:checked").val();
            if(!(tmpVal == 1 || tmpVal == 2 || tmpVal == 3) && tmpVal==4) {
            	$("[name^='taxSsnQ']").removeAttr("required","");
            }else if( $("#requestType").val()=="1" && ( $("#vendorType").val()=="010" || $("#vendorType").val()=="020" || $("#vendorType").val()=="018" || $("#vendorType").val()=="090" ) ) { 
            	$("[name^='taxSsnQ']").prop("required","required");
            }
         // Added - ganesh - (05/26/2016)-  DFCT0017158
            
            // End of comment and insert
            //$("[name='independantContractor']").attr("required","required");
            //Req#56 End                
            //Req#51 End       
            
	    this.updateLEQ("taxid");
            //this.$(".tax-taxid").prop("required", true);
            //this.$(".tax-social").removeAttr("required").removeClass("user-error");
        },
        doShowSsn: function (elem){
            var target = $(elem).data("target"),
                option = $(elem).data("option");
            
            $("#tax-taxid-id").removeAttr("required");   // ganesh
            $("#tax-social-id").attr("required","required");      // ganesh   
            $("#tax-taxid-id").setCustomValidity('');			  // ganesh614 DFCT0017158 
            $("input[name=independantContractor]").prop('checked', false);// ganesh - blocked - ganesh (05/26/2016) -  DFCT0017158
            
            this.showSelection(target, option);
            this.$("#taxid textarea").each(function () {
                $(this).removeAttr("required").removeClass("user-error");
            });
            
           //ganesh - (05/26/2016)-  DFCT0017158
            if($("#requestType").val()=="1" && !( $("#vendorType").val()=="010" || $("#vendorType").val()=="020" || $("#vendorType").val()=="030"  || $("#vendorType").val()=="018" || $("#vendorType").val()=="090")) {
            	$("[name^='taxSsnQ']").removeAttr("required","");
            }
            
            //Added by Craig .. 
            var vendorType = $("#vendorType").val();       
            // Naga ENHC0013685 Add Revenue Share Vendor type 091
            // Naga ENHC0013683 Add Revenue Share Vendor type 092
            // Naga ENHC0016458 & ENHC0016461 Add Utility, Legal Settlement and Contest Winner
           
            // Added -018,095 - Pranesh(04/13/2016)  - ENHC0016459
            if ((vendorType === "010") || (vendorType === "020") || (vendorType === "030") || (vendorType === "090") || (vendorType === "091") || (vendorType === "092") || (vendorType === "080") || (vendorType === "093") || (vendorType === "094") || (vendorType === "018") || (vendorType === "095")      ){
                this.$(".tax-taxid").prop("required", false); 
                this.$(".tax-social").prop("required", true);               
            }   

            //Req#51 START Code added by AGAMPA 
            //$("#taxid").find("[type=text]").removeAttr("required","");
            //$("#social").find("[type=text]").attr("required","required");
            //Req#56 START Code added by Craig 
            $("[name='independantContractor']").removeAttr("required","");
            //$("[name^='taxSsnQ']").attr("required","required");
            //Req#56 END               
            //Req#51 END
                     
            
	    this.updateLEQ("ssn");
            
            //$('input:radio[name=independantContractor]')[0].checked = true;
            //$('input:radio[name=independantContractor]')[1].checked = false;

            
            //this.$(".tax-taxid").removeAttr("required").removeClass("user-error");
            //this.$(".tax-social").prop("required", true);
        },
        showSelection: function (target, option) {
            this.$(".taxid-ssn.collapse.in").each(function () {
                $(this).collapse("toggle");
            });
            this.$(target).collapse("toggle");

            if (!this.$(target).hasClass("in")) {
                this.$(target).collapse("toggle");
            }
            this.$("." + option).prop("checked", true);
            this.$("#taxInfo").removeClass("user-error");
        },
        backToBasic: function (e) {
            this.$el.trigger("showBasicTab");
            e.preventDefault();
        },
        environmentQuestionAnswers: function (e) {
            var $target = $(e.currentTarget),
                $describe = $target.parent().parent().parent().find('.describe');

            if ($target.hasClass('yes-answer')) {
                $describe.find("textarea").prop("required", true);
                $describe.show();
            } else {
                $describe.find("textarea").removeAttr("required").removeClass("user-error");
                $describe.hide();
            }
            
            // Add CGUTJAHR #60
            // Remove required feature for Legal Questions
            $target.parents(".accordion-heading").prev().removeClass("question-user-error");
            // END 
        },
        // Begin of Insert by Naga 999 - 2
        validateTaxId: function(e){
            var that = this,
            	length,
                 taxId = this.$(e.currentTarget),
                 vendorId = $("#vendorId"),
//                 taxId = $('#'+$(e.currentTarget).attr('input-original')), // ENHC0016170
//                 taxIdBaseValue =  $(e.currentTarget).attr('value'),// ganesh831
               taxIdBaseValue =$(e.currentTarget).attr('value'),// ganesh831
                 taxIdCurrentValue = taxId.val(),
            	 taxIdCurrentValue = taxIdCurrentValue.replace(/-/g, ''),
            	 taxIdInput = this.$(e.currentTarget); 
//
//            
//            if($("#temp-tax-id").val()!=="")
//            {
//         	   taxIdBaseValue=$("#temp-tax-id").val();
//            }
//            else if($("#temp-social-id").val()!=="")
//            {
//         	   taxIdBaseValue=$("#temp-social-id").val();
//            }
//            
            
                 if($("#requestType").val() === "1"){
                	 e.preventDefault();
                	
                	 // code start -ganesh
                     var tempdata =taxIdCurrentValue;
                     var tempcomp=taxId.val();
                     if (tempcomp.length === 10 && !($('#tax-taxid-id').val().indexOf('_') >= 0))
                     {
                    	 $('#tax-taxid-id').unmask();
                     	 $('#temp-tax-id').val(taxIdCurrentValue);
                     	 tempdata=$('#temp-tax-id').val();
                     	$('#tax-taxid-id').attr('value',"XX-XXX" + tempdata.substring(5,9));
                     	$('#tax-taxid-id').val("XX-XXX" + tempdata.substring(5,9));
                     	$('#tax-social-id').val('');
                     	$('#temp-social-id').val('');
                   
                     } else if (tempcomp.length === 11 && !($('#tax-social-id').val().indexOf('_') >= 0))
                     {
                     	$('#tax-social-id').unmask();
                     	$('#temp-social-id').val(taxIdCurrentValue);
                     	tempdata=$('#temp-social-id').val();
                         $('#tax-social-id').attr('value',"XXX-XX-" + tempdata.substring(5,9));
                         $('#tax-social-id').val("XXX-XX-" + tempdata.substring(5,9));
                         $('#tax-taxid-id').val('');
                         $('#temp-tax-id').val('');
                     }
                     // code end-ganesh
                	 
                	 return;
                 }
//            	 if(taxIdBaseValue===taxIdCurrentValue){
//            		 // In this case there is no change in the data, so if any error message raised should be removed
//             		$(e.currentTarget).next().slideUp(); 
//             		$(e.currentTarget).removeClass("user-error");
//             		$(e.currentTarget).setCustomValidity('');            		 
//            		return;
//            	 }
            
//            	 //code start-ganesh  
//                 if(taxIdCurrentValue.indexOf('_') >= 0 || taxIdCurrentValue.length<9)
//                 {
//                	this.$('#temp-social-id').val('');
//                	this.$('#temp-tax-id').val('');
//                 return;
//                 }
//                 //code end- ganesh
                
            	// Validate Tax Id
                this.$el.trigger("showSpinner");
                $.ajax({
                    url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=validatetaxid",
                    type: "POST",
                    async: false,
                    data: { taxid: taxId.val(),vendorId:vendorId.val() },
                    dataType: "json"
                }).done(function (response) {
                    
                	// Check the status
                	// If Success do nothing
                	if(response.returnStatus === "S" || $("#requestType").val()=="1" ) { // ganesh523 - DFCT0017158
                		$(e.currentTarget).next().slideUp(); 
                		$(e.currentTarget).removeClass("user-error");
                		$(e.currentTarget).setCustomValidity('');
                		
                		 // code start -ganesh
                        var tempdata =taxIdCurrentValue;
                        var tempcomp=taxId.val();
                        if (tempcomp.length === 10 && !($('#tax-taxid-id').val().indexOf('_') >= 0))
                        {
                           	 $('#tax-taxid-id').unmask();
                        	 $('#temp-tax-id').val(taxIdCurrentValue);
                        	 tempdata=$('#temp-tax-id').val();
                        	$('#tax-taxid-id').attr('value',"XX-XXX" + tempdata.substring(5,9));
                        	$('#tax-taxid-id').val("XX-XXX" + tempdata.substring(5,9));
                        	$('#tax-social-id').val('');
                        	$('#temp-social-id').val('');
                      
                        } else if (tempcomp.length === 11 && !($('#tax-social-id').val().indexOf('_') >= 0))
                        {
                        	$('#tax-social-id').unmask();
                        	$('#temp-social-id').val(taxIdCurrentValue);
                        	tempdata=$('#temp-social-id').val();
                            $('#tax-social-id').attr('value',"XXX-XX-" + tempdata.substring(5,9));
                            $('#tax-social-id').val("XXX-XX-" + tempdata.substring(5,9));
                            $('#tax-taxid-id').val('');
                            $('#temp-tax-id').val('');
                        }
                        // code end-ganesh
                		
                		
                	}else {
                		if($("#requestType").val()=="2") // ganesh523 - DFCT0017158
                        { 								 // ganesh523 - DFCT0017158 9:23 PM 
                       		$(e.currentTarget).next().slideDown(); 
                       		$(e.currentTarget).addClass("user-error");
                       		$(e.currentTarget).setCustomValidity(response.returnMessage);
                       		var html = '<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: block; word-wrap: break-word;\">' + response.returnMessage +'</div>\n';
                       		$(e.currentTarget).next().html(html);
//                		$(e.currentTarget).trigger("showErrorMessage", [response.returnMessage]);
                		//that.$el.trigger("showErrorMessage", [response.returnMessage]);
                		
                		//code start-ganesh
                       		$('#tax-social-id').val('');
                       		$('#temp-social-id').val('');
                       		$('#tax-taxid-id').val('');
                       		$('#temp-tax-id').val('');
                       	//code end-ganesh 
                        }// ganesh523 - DFCT0017158 
                	}
                }).always(function () {
                    that.$el.trigger("hideSpinner");
                }).fail(function(data){
                    console.log('failed');
                });
        }
        // End of Insert by Naga 999 - 2
    });

    /**
     * BankingAccountView: responsible for the functionaity of the account sections.
     */
  
    //Replaced by separate js file "BankAccount.js" - Pranesh (04/20/2016)
    /*
    app.views.BankingAccountView = Backbone.View.extend({
        events: {
            "click .typeOption": "accountTypeSelect",
            "click .secondaryTypeOption": "secondaryAccountTypeSelect",		// ENHC0013668
            "change .country": "countrySelect",
            "change .secondary-country": "secondaryCountrySelect",	// Naga 999
            "click .currency .no-answer": "noUsCurrency",
            "click .currency .yes-answer": "yesUsCurrency", 
            "click .add-i-bank": "addIntermediaryBank", 
            "click .remove-ibank" : "removeIbank",
            "click .edit-ibank-item" : "editIbank",
            "click #banking-AccountNum-":"unmaskBankAccount"  // added by ganesh

        },
        intermediaryTemplate: _.template($('#intermediaryAccount').html()), 
        templates: {},
        secondaryAccountID: 0,
        secondaryAccountTemplate:  _.template($("#secondaryAccountTemplate").html()),
        initialize: function () {
            _.bindAll(this, "accountTypeSelect", "secondaryAccountTypeSelect", "countrySelect", "setAccountInformation", "secondaryCountrySelect","renderSecondaryAccount", "setAccountTypeUI", "addIntermediaryBank", "removeIbank", "editIbank", "revealIbank",
            		"unmaskBankAccount");// unmaskBankAccount added by ganesh
            
            this.key = this.options.key;
            
            this.templates["US:ACH"] = _.template($("#banking-usach").html());
            this.templates["US:WIRE"] = _.template($("#banking-uswire").html());
            this.templates["CA|MX:?"] = _.template($("#banking-camx").html());
            this.templates["AU|NZ:?"] = _.template($("#banking-aunz").html());
            this.templates.OTHER = _.template($("#banking-others").html());
            this.templates["US:ACH-P"] = _.template($("#banking-usach-primary").html());
            this.templates["US:WIRE-P"] = _.template($("#banking-uswire-primary").html());
            this.templates["CA|MX:?-P"] = _.template($("#banking-camx-primary").html());
            this.templates["AU|NZ:?-P"] = _.template($("#banking-aunz-primary").html());
            this.templates["OTHER-P"] = _.template($("#banking-others-primary").html());            
            //this.setAccountInformation();
        },
       // code start ganesh
        unmaskBankAccount: function(e){
        	
        	
        },
        // code end ganesh
        
        
        
        addIntermediaryBank: function(e){
            var count,
                id,
                el,
                secondary;
            if(this.isPrimary){
                id = "primary";
                el = "#ibank-accordion";
                count = this.$(el).find('.accordion-group').length + 1;
                secondary = "";
            }else{
                id = "secondary-account-" + this.cid;
                el = "#secondary" + this.secondaryAccountID + "-ibank-accordion";
                count = this.$(el).find('.accordion-group').length + 1;
                secondary = "secondary" + this.secondaryAccountID + "-";
            }
            var $button = $(e.currentTarget),
               collapseTarget = $button.data("target");
            this.revealIbank($(collapseTarget), $button.parent().parent(), el);
            this.$(el).append(this.intermediaryTemplate({id : id, count : count, parent: el, secondary : secondary }));
        },
        removeIbank: function(e){
            this.$(e.currentTarget).parent().parent().parent("div.accordion-group").remove();
        }, 
        editIbank: function(e){
            var $button = $(e.currentTarget),
                collapseTarget = $button.data("target"),
                el = $button.data('parent');
            this.revealIbank($(collapseTarget), $button.parent().parent(), el);  
        },
        revealIbank: function($collapseTarget, $collapseHeader, el){
            var header, selected_currency_val, 
               selected_currency_text = "No currency selected", 
               selected_country_val, 
               selected_country_text = "No country selected", 
               bank_account, 
               el_heading = el + " .accordion-heading.active";
            $(el).find(".collapse.in").each(function () {
                //
                selected_currency_val = $(this).find('.select-currency select').val();
                if(selected_currency_val){
                    selected_currency_text = $(this).find('.select-currency option:selected').text();
                }
                selected_country_val = $(this).find('.select-country select').val();
                if(selected_country_val){
                    selected_country_text = $(this).find('.select-country option:selected').text();
                }
                bank_account = $(this).find('.ibank-account input').val();
                if(!bank_account){
                    bank_account = "Empty account";
                }

                header = selected_currency_text + " - " + selected_country_text + " - " + bank_account;
                
                $(this).siblings().find(".ibank-header").text(header);
                $(this).collapse("toggle");
            });
            $(el_heading).each(function() {
                $(this).removeClass("active");
            });
            $collapseTarget.collapse("toggle");
            if (!$collapseTarget.hasClass("in")) {
                $collapseTarget.collapse("toggle");
            }
            $collapseHeader.addClass("active");
        },
        
        renderSecondaryAccount: function (parent, country, id) {
        	// Begin of Comment and Insert by Naga DFCT0013582
        	//this.$el = $(this.secondaryAccountTemplate({ id: id,country:country,type:"ACH"}));
        	// Make it two digit if it is one digit
        	var fileType = id+1;
        	if(fileType<10){
        		fileType = new Array( 1 + (/\./.test( fileType ) ? 2 : 1) ).join( '0' ) + fileType; 
        	}
        	this.$el = $(this.secondaryAccountTemplate({ id: id,country:country,type:fileType,objectId:null,fileName:null})); 
        	// End of Comment and Insert by Naga
            
            parent.append(this.$el);
            this.el = "#secondary-account-container-" + id;
            
            this.delegateEvents(this.events);

//            this.$(".country").val(country); // Secondary account will secondary country 
            this.country = country;
//            // ENHC0016458 If it is political Contribution default the country to US
//            if(($("#vendorType").val() === "040")){
//            	country = "US";
//            	this.country = "US";
//            }
            if (country !== "US") {
                this.accountType = "Wire"; 
                this.setAccountTypeUI();
            }
            // Naga 999, pass true if it is secondary account
            this.setAccountInformation(true);		
            this.secondaryAccountID = id;
            this.$el.trigger("registerFileUploads");
            // Begin of comment by Naga DFCT0013582
            //this.$el.find("[name$='RoutingNum']").trigger("blur");
            // End of comment by Naga            
            return this;
        },

        renderSecondaryAccountInitialize: function (parent, account) {
            this.$el = $(this.secondaryAccountTemplate(account));
            parent.append(this.$el);
            this.el = "#secondary-account-container-" + account.id;
            this.delegateEvents(this.events);

//            this.$(".country").val(account.country);	// Secondary account will secondary country 
            this.$(".secondary-country").val(account.country);
            
            this.country = account.country;
            if (this.country !== "US" || account.type.match(/[U]/)) {
                this.accountType = "Wire";
                this.setAccountTypeUI();
            }
            this.setAccountInformationInitialize(account);
            this.secondaryAccountID = account.id;
            this.$el.trigger("registerFileUploads");
            // Begin of comment by Naga DFCT0013582
            //this.$el.find("[name$='RoutingNum']").trigger("blur");
            // End of comment by Naga
            return this;
        },

        noUsCurrency: function (e) {
            e.preventDefault();
            $(e.currentTarget).parent().next('.add-on.currency-no').removeClass('show');
            this.$(".primary-currency").prop("required", true);
            this.$(".intermdiary-bank-account").prop("required", true);
        },
        yesUsCurrency: function (e) {
            e.preventDefault();
            $(e.currentTarget).parent().next('.add-on.currency-no').addClass('show');
            this.$(".primary-currency").removeAttr("required").removeClass("user-error");
            this.$(".intermdiary-bank-account").removeAttr("required").removeClass("user-error");
        },
         accountTypeSelect: function (e) {
            e.preventDefault();
	    // Modified by CGUTJAHR 1/15/15 : Enhancement #41
            var vendorType = $("#vendorType").val();   
            var selectedValue = $(e.currentTarget).text();
	    // END
            
            // If it is ACH country can be defaulted to US
            if(selectedValue === "ACH"){
            	this.$(".country").val("US");
            	if(this.country)
            		this.country = "US";
            }else if(selectedValue === "Wire"){
            	this.$(".country").val("");
            	if(this.country)
            		this.country = "";
            }
            this.$(".dropdown-toggle").dropdown("toggle");
	    // Modified by CGUTJAHR 1/15/15 : Enhancement #41	    
            //Change by Jorge L Perez
            this.accountType = $(e.currentTarget).text();
            this.accountTypeNumber = $(e.currentTarget).parents(".accordion-group").find(".type").val();
            this.$(".type-text").text(this.accountType);
            this.$(".type-account").val(this.accountType);
            this.$("#primaryBankingType").val(this.accountType);
            
            // Begin of Comment and Insert by Naga ENHC0013683
            // ENHC0013683 Garnishment Vendor, should have check option
            // ENHC0016458 & ENHC0016461 
            // Government, Political, Utility, Legal Settlement, Contest Winner have check, 
            // so flow has to be adjusted accordingly.


            //if ((vendorType == "060") && (selectedValue == "CHECK")) {
            
            // (vendorType -095,030,018) - Added by Pranesh(11-04-2016) - ENHC0016459 
            // remnoved 030 -Pranesh - 04/19/2016
            if (((vendorType == "060") || (vendorType == "040") || (vendorType == "050") || (vendorType == "080") || (vendorType == "092") || (vendorType == "093") || (vendorType == "094")  || (vendorType == "095") || (vendorType == "018") ) && (selectedValue == "CHECK")) {
            // End of Comment and Insert by Naga
            	$("#primary-account").hide();
            	$("#payment-notifications").hide();
            } else {
            	$("#primary-account").show();
            	$("#payment-notifications").show();            	
                //Change by Jorge L Perez
                //this.accountType = $(e.currentTarget).text();
                this.setAccountTypeUI();
                // Naga 999, this still has to be checked but for now sending false
                this.setAccountInformation(false);           	
            }
            
            // Begin of Comment and Insert by Naga ENHC0013683            
            // if ((vendorType == "060") && (selectedValue == "ACH")) {
            // ENHC0016458 & ENHC0016461 
            // For Government, Legal Settlement, Contest Winner Wire is not allowed. So disable primary country when ACH  
            // so flow has to be adjusted accordingly.    
            // ACH only accounts should have the country disabled, others do not need it
            // Governemnt is no more ACH only
            // || (vendorType == "050")
            
            //(vendorType - 018) - Added by Pranesh(11,13-04-2016) - ENHC0016459
            if (((vendorType == "060")  || (vendorType == "092") || (vendorType == "093") || (vendorType == "094") || (vendorType == "018") ) && (selectedValue == "ACH")) {
            // End of Comment and Insert by Naga ENHC0013683            	
            	$('[name=primary-account-country]').val('US');
            	$('[name=primary-account-country]').prop('disabled', 'disabled');
            	
            // Begin of Insert by Naga 	
            }else if (((vendorType == "060") || (vendorType == "092") || (vendorType == "093") || (vendorType == "094")) && (selectedValue == "Wire")){
            	$('[name=primary-account-country]').val('US');
            	$('[name=primary-account-country]').removeAttr('disabled');            	
            }else if( ( (vendorType == "018") && (selectedValue == "Wire")  )){//Begin Of Insert by Pranesh(13-04-2016) - ENHC0016459
    			$('[name=primary-account-country]').val('');
    			$('[name=primary-account-country]').removeAttr('disabled'); 
    		}
            //End Of Insert by Pranesh(13-04-2016) - ENHC0016459
    
            // End of Insert
            
            // Begin of Insert by Naga ENHC0016458
            // If it is Political and Utility, user can select ACH and WIRE. 
            // If ACH default the country to US, if WIRE empty the country so that user can select the country.
            if((vendorType == "040")||(vendorType == "080")){
            	if(selectedValue == "ACH"){
            		$('[name=primary-account-country]').val('US');
            	}else if(selectedValue == "WIRE"){	
            		$('[name=primary-account-country]').val('');
            	}
            }
            // End of Insert by Naga
            
            //END
        },
        
        // Begin of Insert by Naga	ENHC0013668
        // During addition of US Wire to Trade Vendor, secondary accounts do not have toggle capability and that leads to this effort
        secondaryAccountTypeSelect: function (e) {
            e.preventDefault();
            var vendorType = $("#vendorType").val();   
            var selectedValue = $(e.currentTarget).text();
            
            // If it is ACH country can be defaulted to US
            if(selectedValue === "ACH"){
            	this.$(".secondary-country").val("US");
            	// As secondary country is ACH, country can be changed to US in this case. 
            	this.country = "US";
//            	if(this.country)
//            		this.country = "US";
            }else if(selectedValue === "Wire"){
            	this.$(".secondary-country").val("");
            	if(this.country)
            		this.country = "";
            }
            this.$(".dropdown-toggle").dropdown("toggle");
            this.accountType = $(e.currentTarget).text();
            this.accountTypeNumber = $(e.currentTarget).parents(".accordion-group").find(".type").val();
            this.$(".type-text").text(this.accountType);
            this.$(".type-account").val(this.accountType);
            this.setAccountTypeUI();
            this.setAccountInformation(true);           	
        },        
        // End of Insert by Naga
        setAccountTypeUI: function () {
        	var vendorType = $("#vendorType").val(); 
            
        	this.$(".type-text").text(this.accountType);
            this.$(".type-account").val(this.accountType);
//             if(!this.country)
//                this.country = "US"
            var type = this.$(".type");
            var newID = type.val();
            if (this.country !== "US") {
            	
            	// Begin of Comment and Insert by Naga ENHC0013683
            	// ENHC0016458 Add more vendor types to this condition
            	// ENHC0016461 Not adding Legal Settlement and Contest Winner as I think we do not need it here
            	// ENHC0016461 Legal Settlement and Contest Winner have to be added
            	//if (vendorType === "060"){
            	if (vendorType === "060" || vendorType === "092" || vendorType === "040" || vendorType === "080" || vendorType === "050" || vendorType === "093" || vendorType === "094"){
            	// End of Comment and Insert by Naga 
            		// HIDE ACH?
            	}
            	// Begin of Insert by Naga ENHC0013668
            	// For all vendors with WIRE, US Wire should be allowed
            	// If the country is not US, which gets emptied when WIRE is selected, do not remove the toggle
            	else{
            		// Nothing happens here, this is just to avoid toggle hide.
            	}
            	// End of Insert by Naga
            	// Begin of Comment by Naga ENHC0013668
//            	else {
//	                this.$(".accordion-heading .dropdown-toggle").removeAttr("data-toggle");
//	                this.$(".accordion-heading .dropdown-toggle .caret").hide();
//	                this.$(".accordion-heading .dropdown-menu").hide();
//            	}
            	// End of Comment by Naga 
                if (this.$(".currency .no-answer").hasClass("active")) {
                    this.$(".primary-currency").prop("required", true);
                    this.$(".intermdiary-bank-account").prop("required", true);
                }
                if(this.accountType === "Wire")
                {
                    if(newID.match(/[WU]/))
                        type.val("W" + newID.substr(1)) 
                    else if(this.el.id === "primaryAccount")
                        type.val(this.accountType)
                    else
                        type.val("W" + newID)
                }
                
            } else {
            	
            	// Begin of Comment and Insert by Naga ENHC0013683
            	// ENHC0016458 Add more vendor types to this condition
            	// ENHC0016461 Not adding Legal Settlement and Contest Winner as I think we do not need it here            	
            	//if (vendorType === "060"){
            	if (vendorType === "060" || vendorType === "092" || vendorType === "040" || vendorType === "080" || vendorType === "050"){
            	// End of Comment and Insert by Naga              		
            		// HIDE WIRE?
            	} else {
	                this.$(".accordion-heading .dropdown-toggle").attr("data-toggle", "dropdown");
	                this.$(".accordion-heading .dropdown-toggle .caret").show();
	                this.$(".accordion-heading .dropdown-menu").removeAttr("style");
            	}
            	
                this.$(".primary-currency").removeAttr("required").removeClass("user-error");
                this.$(".intermdiary-bank-account").removeAttr("required").removeClass("user-error");
                
                 if(this.accountType === "ACH")
                {
                    if(this.el.id === "primaryAccount")
                        type.val(this.accountType)    
                    else if(newID.match(/[WU]/))
                        type.val(newID.substr(1))
                     
                } else if(this.accountType === "Wire")
                {
                	// Begin of Insert by Naga ENHC0013668
                	// For primary Account primarybankingtype is set with ACH/Wire/Check.
                	if(this.el.id === "primaryAccount")
                        type.val(this.accountType) 
                    else    
                	// End of Insert by Naga
                    if(newID.match(/[WU]/))
                        type.val("U" + newID.substr(1)) 
                    else
                        type.val("U" + newID)
                }
            }
        },
        countrySelect: function (e) {
            e.preventDefault();
            var vendorType = $("#vendorType").val();
            
            this.country = $(e.currentTarget).val();
            if (this.country !== "US") {
                this.accountType = "Wire";
            }
            // Begin of Insert by Naga ENHC0013668
//            else{
//            	// Do not change account type, it should have already been set
//            }
            // End of Insert by Naga
            // Begin of comment by Naga ENHC0013668
//            else {
//                this.accountType = "ACH";
//            }
            // End of comment by Naga
            this.setAccountTypeUI();
            // Naga 999, Primary country select, so send false
            this.setAccountInformation(false);
        },
        
        // Begin of change by Naga ENHC0013668
        secondaryCountrySelect: function (e) {
            e.preventDefault();
            this.country = $(e.currentTarget).val();
            
            if (this.country !== "US") {
                this.accountType = "Wire";
            } 
            this.setAccountTypeUI();
            // Naga 999, Secondary bank Country select, send true
            this.setAccountInformation(true);
        },        
        // End of change by Naga 999
        isBankingInfoRequired: function()
        {
             var vendortypeElement = $("#vendorType");
            var requesttypeElement = $("#requestType");
            var vendortype = vendortypeElement.val();
            var requesttype = requesttypeElement.val();
            
            // Naga ENHC0013685 Add Revenue Share Vendor type 091
            // Naga ENHC0013683 Add Garnishment Vendor type 092
            // Naga ENHC0016461 Add Legal Settlement and Contest Winner
         
            // Added -018,095 - Pranesh(04/13/2016)  - ENHC0016459
            var allvendorTypes = ["010", "020", "030",  "040", "050", "060", "070", "080", "090", "091", "092","093","094","095","018"];
            //var validatevendorTypeArray = ["030", "040", "050", "070", "080"]; ENHC0016458
            var validatevendorTypeArray = ["030","040", "070"];	// ENHC0016458 
            var result = false;

            if($.inArray(vendortype, allvendorTypes) !== -1)
            {
                result = true;
                if($.inArray(vendortype, validatevendorTypeArray) !== -1 && (requesttype === '2')) 
                {
                    if($("#requestType").val() === "2")
                    {
                        result = false;
                    }
                }
            }

            return result;
        },
        // Naga 999, added isSecondary flag
        setAccountInformation: function (isSecondary) {	
            var country,
                accountType = (this.accountType || "ACH").toUpperCase(),
                templateKey,
                template;
            // Begin of Insert by Naga ENHC0013668
            if(isSecondary){
            	country = (this.country || this.$(".secondary-country").val() || "").toUpperCase();
            	
            	if(country && !this.$(".secondary-country").val()){
            		this.$(".secondary-country").val(country);
            	}
            }else{
            	country = (this.country || this.$(".country").val() || "").toUpperCase();
            }
            // End of Insert by Naga
            if (country !== "US") {
                this.$(".accept-usd").show();
            } else {
                this.$(".accept-usd").hide();
            }

//            // ENHC0016458 This text should be Upload ACH Form for Political Contribution
//            if($("#vendorType").val() === "040"){
//            	this.$(".upload-label").text("Upload ACH Form");
//            }
//            else
            if (accountType === "ACH") {
            	if($("#vendorId").val()==null || $("#vendorId").val().length == 0){
                    this.$(".upload-label").text("Upload ACH Form");
                    this.$(".upload-label").addClass("required-red"); // Added by Pranesh - ENHC0016459 - (11-04-2016)
                    this.$("[type=file]").addClass("user-error");    //  Added by Pranesh (04/16/2016) - ENHC0016459
            	}else{
//                    this.$(".upload-label").text("Upload ACH Form(Please upload new ACH form if there is change in bank details)");
                    this.$(".upload-label").html("Upload ACH Form(<strong>Please upload new ACH form if there is change in bank details</strong>)");
            	}

                // Begin of Insert by Naga ENHC0013668
                this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/ACH%20OnBoardingtemplate.doc");
                // End of Insert by Naga
//                $("a").attr("href", "http://www.google.com/")
                // Naga DFCT0013582 This Validation is not needed as it may have file attached during initialize
                if($("#requestType").val() === "2" && this.isBankingInfoRequired()) {
                	//this.$(".upload-label").prepend("<i class=\"icon-asterisk\"></i> ")//code commented by AGAMPA
                    this.$("[type=file]").addClass("user-error").attr("required","")
                } else {
                    this.$("[type=file]").removeClass("user-error").removeAttr("required","")
                }
                 

            } else {
            			
            	if($("#vendorId").val()==null || $("#vendorId").val().length == 0){
            		this.$(".upload-label").text("Upload Wire Form");
            		this.$(".upload-label").addClass("required-red"); // Added by Pranesh (04/17/2016)- ENHC0016459 
                    this.$("[type=file]").addClass("user-error");    //  Added by Pranesh (04/17/2016)- ENHC0016459
            	}else{
//            		this.$(".upload-label").text("Upload Wire Form(Please upload new WIRE form if there is change in wire banking details)");
            		this.$(".upload-label").html("Upload Wire Form(<strong>Please upload new WIRE form if there is change in wire banking details</strong>)");
            	}            	
                
                // Begin of Insert by Naga ENHC0013668
                if(country === "US"){
                	this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_US_OnBoardingtemplate.doc");
                }else{
                	this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_OnBoardingtemplate.doc");
                }
                // End of Insert by Naga
              // Naga DFCT0013582 This Validation is not needed as it may have file attached during initialize
                if($("#requestType").val() === "2" && this.isBankingInfoRequired())
                    this.$("[type=file]").addClass("user-error").attr("required","")
                else
                    this.$("[type=file]").removeClass("user-error").removeAttr("required","")
				

            }

            switch (country + ":" + accountType) {
                case "US:ACH":
                    templateKey = "US:ACH";
                    break;
                case "US:WIRE":
                    templateKey = "US:WIRE";
                    break;
                case "CA:ACH":
                case "CA:WIRE":
                case "MX:ACH":
                case "MX:WIRE":
                    templateKey = "CA|MX:?";
                    
                    break;
                case "AU:ACH":
                case "AU:WIRE":
                case "NZ:ACH":
                case "NZ:WIRE":
                    templateKey = "AU|NZ:?";
                    break;
                default:
                    templateKey = "OTHER";
            }
            if(!isSecondary){
            	templateKey = templateKey+"-P";
            }            
            template = this.templates[templateKey];

            this.$(".account-type").empty().append(template({ id: this.key || this.$el.data("id"), 
                                                              accountNum:"",
                                                              routingNum:"",
                                                              accountHolder:"",
                                                              fileName:"",
                                                              ibanNum:"",
                                                              swiftAccountNum:"",
                                                              tempaccountNum:""}));
            
            
//            // Begin of Insert by Naga ENHC0016170
//            // As the content is getting regenerated, add secure number mask again
//            if(isSecondary){
//            	var id = this.key || this.$el.data("id");
//            	// Mask secondary account number
//            	var secondaryAccountID = "banking-"+id+"-AccountNum";
//            	$("#"+secondaryAccountID).securenumbermask();
//            }else{
//            	// Mask primary account number
//            	$("#banking-primary-AccountNum").securenumbermask( );            	
//            }
//            // End of Insert            
            
            // TEST - Doubt blocked,Pranesh (04/18/2016)
            	this.$el.find("[name$=AccountNum]").trigger("blur");
            // TEST - Doubt blocked,Pranesh (04/18/2016)
            
            if(($("#vendorType").val()=== "030" || $("#vendorType").val()=== "040")){
            	 $("[name='banking-primary-RoutingNum']").addClass("user-error");//Pranesh(04/18/206)
            	 $("[name='banking-primary-AccountNum']").addClass("user-error");//Pranesh(04/18/206)
            	 $("[name='banking-primary-HolderName']").addClass("user-error");//Pranesh(04/18/206)
             }
        },

         setAccountInformationInitialize: function (account) {
            var country = (this.country || this.$(".country").val() || "").toUpperCase(),
                accountType = (!account.type.match(/[UW]/))?"ACH":"WIRE",
                templateKey,
                template;
            if (country !== "US") {
                this.$(".accept-usd").show();
            } else {
                this.$(".accept-usd").hide();
            }

           if (accountType === "ACH") {
	           	if($("#vendorId").val()==null || $("#vendorId").val().length == 0){
	                this.$(".upload-label").text("Upload ACH Form");            		
	        	}else{
//	                this.$(".upload-label").text("Upload ACH Form (Please upload new ACH form if there is change in bank details)");
	                this.$(".upload-label").html("Upload ACH Form (<strong>Please upload new ACH form if there is change in bank details</strong>)");
	        	}
                // Begin of Insert by Naga ENHC0013668
                this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/ACH%20OnBoardingtemplate.doc");
                // End of Insert by Naga                
                // Naga DFCT0013582 This Validation is not needed as it may have file attached during initialize
                if($("#requestType").val() === "2" && this.isBankingInfoRequired()) {
                	//this.$(".upload-label").prepend("<i class=\"icon-asterisk\"></i> ")//CODE COMMENTED BY AGAMPA
                    this.$("[type=file]").addClass("user-error").attr("required","")
                    
                } else {
                    this.$("[type=file]").removeClass("user-error").removeAttr("required","")
                }

            } else {
            	if($("#vendorId").val()==null || $("#vendorId").val().length == 0){
            		this.$(".upload-label").text("Upload Wire Form");
            	}else{
//            		this.$(".upload-label").text("Upload Wire Form(Please upload new WIRE form if there is change in wire banking details)");
            		this.$(".upload-label").html("Upload Wire Form(<strong>Please upload new WIRE form if there is change in wire banking details</strong>)");
            	} 
                // Begin of Insert by Naga ENHC0013668
                if(country === "US"){
                	this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_US_OnBoardingtemplate.doc");
                }else{
                	this.$(".blank-download-template").attr("href","/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_OnBoardingtemplate.doc");
                }
                // End of Insert by Naga                
                // Naga DFCT0013582 This Validation is not needed as it may have file attached during initialize
                if($("#requestType").val() === "2" && this.isBankingInfoRequired())
                    this.$("[type=file]").addClass("user-error").attr("required","")
                else
                    this.$("[type=file]").removeClass("user-error").removeAttr("required","")
				
            }

            switch (country + ":" + accountType) {
                case "US:ACH":
                    templateKey = "US:ACH";
                    break;
                case "US:WIRE":
                    templateKey = "US:WIRE";
                    break;
                case "CA:ACH":
                case "CA:WIRE":
                case "MX:ACH":
                case "MX:WIRE":
                    templateKey = "CA|MX:?";
                    
                    break;
                case "AU:ACH":
                case "AU:WIRE":
                case "NZ:ACH":
                case "NZ:WIRE":
                    templateKey = "AU|NZ:?";
                    break;
                default:
                    templateKey = "OTHER";
            }
            template = this.templates[templateKey];
            account.id = this.$el.find(".remove-secondary").data("id");
            this.$(".account-type").empty().append(template(account));
//            // Begin of Insert by Naga ENHC0016170
//            // As the content is getting generated, add secure number mask 
//           	// Mask secondary account number
//           	var secondaryAccountID = "banking-"+account.id+"-AccountNum";
//           	$("#"+secondaryAccountID).securenumbermask();
//            // End of Insert               
            this.$el.find("[name$=AccountNum]").trigger("blur"); 
        }

    });*/ 
    //Replaced by separate js file "BankAccount.js" - Pranesh (04/20/2016)

    /**
     * BankingTabView: Responsible for the banking functionality
     */
    app.views.BankingTabView = Backbone.View.extend({
        el: "#tab4",
        secondaryAccounts: {},
        secondaryAccountsTotal: 0,
        events: {
            //"blur .bank_account": "addEmailContactonBlur",    // Added By Deepti, commented out by Philippe A. (206432942)
        	
        	// Begin of comment and insert by Naga DFCT0013582
        	// Separting the validation logic for primary and secondary accounts where everything in secondary account is mandatory
        	/*"blur [name='banking-primary-RoutingNum']":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .banking-routing-num
            "blur [name='banking-primary-AccountNum']":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .bank_account
            "blur [name='banking-primary-HolderName']":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .bank_account
            "blur input.banking-routing-num[name^=banking-]":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .banking-routing-num
            "blur input.header-input[name^=banking-]":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .bank_account
            "blur input.holder-name[name^=banking-]":"validateSecondaryBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .bank_account
			*/
            "blur [name='banking-primary-RoutingNum']":"validatePrimaryBankDetails", // .banking-routing-num
            "blur [name='banking-primary-AccountNum']":"validatePrimaryBankDetails", // .bank_account
            "blur [name='banking-primary-HolderName']":"validatePrimaryBankDetails", // .bank_account
            "blur input.banking-routing-num[name^=banking-]":"validateSecondaryBankDetails", // .banking-routing-num
            "blur input.header-input[name^=banking-]":"validateSecondaryBankDetails", // .bank_account
            "blur input.holder-name[name^=banking-]":"validateSecondaryBankDetails", // .bank_account 
        	// End of comment and insert by Naga//
            "blur [name='emailContact-1']":"validateBankRoutingAndAccountNumber", // Added by Added by Philippe A. (206432942) .bank_account
            "click .add-item": "addSecondaryAccount",
            "click .edit-item": "editItem",
//            "click .remove-secondary": "removeItem",	// ENHC0013668
            "click .remove-secondary": "confirmDelete", // ENHC0013668           
            "click .back": "backToTerms",
            "click .continue": "advanceToContacts", 
            "click .add-email-contact": "addEmailContact", 
            "click .edit-email-item": "editEmail", 
//            "click .remove-email": "removeEmail",		// ENHC0013668
            "click .remove-email": "confirmEmailDelete", // ENHC0013668	
            "click #banking-primary-AccountNum": "primaryBankAccountUnmask" ,// ganesh
            "click input.header-input[name^=banking-]": "secondaryBankAccountUnmask"  //  ganesh
            	
        },
        addEmailTemplate: _.template($('#addEmailTemplate').html()), 
        initialize: function () {
            var that = this,
                account;
            _.bindAll(this, "checkValidity", "addSecondaryAccount", "revealSecondaryAccount", "removeItem", "editItem", "backToTerms", "advanceToContacts", "addEmailContact",
            		"editEmail", "removeEmail", "revealEmail","secondaryBankAccountUnmask","confirmDelete","closeClicked","confirmEmailDelete","isBankingInfoRequired");//isBankingInfoRequired added DFCT0017543
            
            //method bankAccountUnmask added -ganesh
            this.primaryAccountView = new app.views.BankingAccountView({ el: "#primaryAccount", key: "primary" });
            this.primaryAccountView.isPrimary = true;
//            $("#banking-primary-AccountNum").securenumbermask( ); // ENHC0016170
            
            // Begin of Insert by Naga
            // If it is ACH only vendor type, Country should be disabled for selection
            if($("#vendorType").val() === "093" || $("#vendorType").val() === "094" || $("#vendorType").val() === "060" ){
            	var country = $("#primary-account-country").val();
            	if(country === "US"){
            		$('[name=primary-account-country]').prop('disabled', 'disabled');
            	}
            }
            
            
//            if(country)
            // End of Insert by Naga
            
            
            this.$("#secondary-account div.accordion-group").each(function () {
                account = new app.views.BankingAccountView({ el: this });
                that.secondaryAccounts[account.$(".icon-remove").data("id")] = account;
            });
            this.validateBankRoutingAndAccountNumber(); // Added by Added by Philippe A. (206432942
            this.addSecondaryAccountInitialize();
            
            

            if(!this.isBankingInfoRequired())
            {
            	// Begin of Comment and Insert by Naga DFCT0013582
            	// Limit this to primary bank account
                //var allAccounts = $("#account-list-container");
            	var allAccounts = $("#all-accounts");
                // End of Comment and Insert by Naga
                allAccounts.find("input").removeClass("user-error").removeAttr("required");
                allAccounts.find(".bankRoutingOrAccountNumberWarning").css("display","none")
            }
            if(this.$('#email-accordion').children().length == 0)
            {
            	this.addEmailContact();
            	this.$(".remove-email").attr("style","display:none;");
            }
            
            this.validateDelete = new app.views.ValidateDelete();			// ENHC0013668            
        },
        //unmask code -ganesh  
        primaryBankAccountUnmask:function(e)
        {
        	if($("#hidden-banking").val())
        	{
        	if($("#hidden-banking").val().indexOf("X")<0)
        	{
        		$("#banking-primary-AccountNum").val($("#hidden-banking").val());
        	}
        	}
        	else
        	{
        		$("#banking-primary-AccountNum").val('');
        		$("#hidden-banking").val('');
        		
        	}
        	
        }
        ,
        secondaryBankAccountUnmask:function(e)
        {
        	var target = $(e.currentTarget);
            var parent = target.parents(".accordion-group");
            var hiddenNumElement = parent.find("[name*=hidden-banking]").attr("id");
            var accountNumElement = parent.find("[name$=AccountNum]").attr("id");
            
            var hiddenValue=parent.find("[name*=hidden-banking]").val();
            var idname=parent.find("[name$=AccountNum]").attr("id");
            if(hiddenValue)
            {
            if(idname==="banking-primary-AccountNum")
            {
            	$("#banking-primary-AccountNum").val(this.$("#hidden-banking").val());
            }
            	
            else 
        	{
        		$("#"+accountNumElement).val(hiddenValue);
        		//window.alert(hiddenNumElement+":"+hiddenValue+"also id:");
        	}
            }
//        	else
//        	{
//        		accountNumElement.val('');
//        		hiddenNumElement.val('');
//        		
//        	}
        	
        }
        ,
       // unmask code end - ganesh 
        
        
        addEmailContact: function(e){
            if(e)e.preventDefault(); 
            //determine email id
            var count = 1,
                $button = e?$(e.currentTarget):false;
            
            if(this.$('#email-accordion').children().length > 0){
                count  = this.$('#email-accordion').children().length + 1;
            }
            
            var validateDelegate = this.validateBankRoutingAndAccountNumber;
            
            var accordion = this.$('#email-accordion').append(this.addEmailTemplate({count : count}));

            $(accordion).find("[name='emailContact-"+count+"']").on("blur", function(){validateDelegate();});
            
            
            this.revealEmail($("#email" + count), $("#email" + count).siblings(".accordion-heading"));
            
            this.validateBankRoutingAndAccountNumber(); // Added by Added by Philippe A. (206432942)
        },
        advanceToContacts: function (e) {
            e.preventDefault();
            if (this.checkValidity()) {
                this.$el.trigger("prepareContactsTab")
                    .trigger("completeBankingTab")
                    .trigger("showContactsTab");
            }
            $('html, body').animate({ scrollTop: 0 }, 0);
        },
        backToTerms: function (e) {
        	// ENHC0016458 Political Contribution and Goverment should go back to Basic tab
        	// ENHC0016461 Legal Settlement and Contest Winner should go back to Tax
            if ($("#vendorType").val() === "060" || $("#vendorType").val() === "040" || $("#vendorType").val() === "050") { // ENHC0016458
            	 this.$el.trigger("showBasicTab");
            // Begin of Insert by Naga ENHC0016461
            } else if($("#vendorType").val() === "093" || $("#vendorType").val() === "094"|| $("#vendorType").val() === "095"){ // Added - Pranesh(05/17/2016)-Defect : 15095

            	this.$el.trigger("showTaxTab");
            // End of Insert by Naga	 
            } else {
            	 this.$el.trigger("showTermsTab");
            }
            e.preventDefault();
        },
        addSecondaryAccount: function (e) {
            this.secondaryAccountsTotal++;
            e.preventDefault();
        	$("#payment-notifications").show();    // DFCT0017543        	
            var country = this.$("select.select-country").val(),
                secondaryCollapseTarget,
                secondaryAccount = new app.views.BankingAccountView(), 
                id= this.secondaryAccountsTotal;
            this.$("#secondary-accounts-legend").show();
            this.secondaryAccounts[id] = secondaryAccount;
            secondaryAccount.renderSecondaryAccount(this.$("#secondary-account"), country, id);

            secondaryCollapseTarget = secondaryAccount.$(".edit-item").data("target");
    
            this.revealSecondaryAccount($(secondaryCollapseTarget), secondaryAccount.$(".accordion-heading"));
        },

        addSecondaryAccountInitialize: function () {
            if($("#secondaryAccountJson").html())
            {
            var secondaryAccountsJson = eval('(' + $("#secondaryAccountJson").html() + ')').secondaryAccount;
            var that = this;
            $.each(secondaryAccountsJson,function(i, val)
            {
                that.secondaryAccountsTotal++;
                var country = val.country,
                secondaryCollapseTarget,
                secondaryAccount = new app.views.BankingAccountView(), 
                id= that.secondaryAccountsTotal;
                val.id = id;
                that.$("#secondary-accounts-legend").show();
                that.secondaryAccounts[id] = secondaryAccount;

                secondaryAccount.renderSecondaryAccountInitialize(that.$("#secondary-account"), val);
                secondaryCollapseTarget = secondaryAccount.$(".edit-item").data("target");
                that.revealSecondaryAccount($(secondaryCollapseTarget), secondaryAccount.$(".accordion-heading"));
            });
            this.collapseCloseSecondaryAccount();
            }

        },

        collapseCloseSecondaryAccount: function(){
               var routingNum,
                countryCode,
                type,
                typeAccount,
                header = [];
            $("#secondary-account .secondary-account.collapse.in").each(function () {
                routingNum = $(this).parents(".accordion-group").find(".banking-routing-num").val();
                countryCode = $(this).parents(".accordion-group").find(".country").val();
                type = $(this).parents(".accordion-group").find(".type").val();
                typeAccount = $(this).parents(".accordion-group").find(".type-account").val();

                 //if (countryCode) {
                //    header.push(countryCode);
                //}
                
                //Added by Jorge Perez
                //if (countryCode === "US")
                //{
                //     if(typeAccount !== "ACH")
                //        header.push("U")
                //}else
                //{
                //    if(typeAccount === "ACH")
                //        header.push("U")
                //     else
                //       header.push("W")
                //}      
                if(type)
                {
                   var sum1 = parseInt(type.substring(1))+1;
                    if (!type.match(/[WU]/)) 
                        header.push((sum1 < 10)? "0"+sum1:sum1);
                    else
                    {
                        header.push(type.charAt(0) + ((sum1 < 10)? "0"+sum1:sum1));
                    }

                    if (routingNum) {
                        routingNum = " - #" + routingNum;
                    }
                }
               
                else
                    routingNum = "";
                //$(this).parents(".accordion-group").find(".item-label").text(header.join(" - "));
                $(this).parents(".accordion-group").find(".item-label").text(header.join("") + routingNum);
                $(this).collapse("toggle");
            });
            $("#secondary-account .accordion-heading.active").removeClass("active");
        },
        
        checkValidity: function () {
            var isValid = true;
            this.$("input, select, textarea").each(function () {
                if (!$(this).checkValidity()) {
                	// Begin of Insert and Comment by Naga ENHC0016458
                	var emailAddress=$(this).attr("name").indexOf("emailContact");
                	var emailValue="empty";
                	if(emailAddress)
                	{
                		emailValue=$(this).val();
                	}
                	else{
                		emailValue="";
                	}
                	// When a vendor type has Check option in primary account and in secondary accounts we should not ignore secondary account required checks
                	if(!(($(this).attr("name").indexOf("primary") > -1 || $(this).attr("name").indexOf("emailContact") > -1 )&&($("#primaryBankingType").val() === "CHECK"))) {
                		isValid = false;	
                	}
                	var secondAccountExist=$(".secondary-account").attr("id");
                	if (typeof secondAccountExist === "undefined") {
                	}
                	else if($("[name='emailContact']").val() == "" && (secondAccountExist.indexOf("secondary-account-")> -1) && $("#primaryBankingType").val() === "CHECK") { // DFCT0017543 Ganesh
                		 isValid = false;	
                	}
                	else if(emailValue=== "" && !$(this).attr("name").indexOf("emailContact")>0 ) { // DFCT0017543 Ganesh
               		 isValid = false;	
               	}
//                	isValid = false;
                	// End of Insert and Comment by Naga
                	
                    
                    
                }
            });
            
            // If primary account is check this validation is not needed
//            if(!($("#primaryBankingType").val() === "CHECK"))	// ENHC0016458 Naga
//            	isValid = isValid && this.validateBankRoutingAndAccountNumber(); // Added by Added by Philippe A. (206432942)
//	    
            // Per the logic written above this code will be commented ENHC0016458
	    // Modified by CGUTJAHR 1/15/15 : Enhancement #41
//            if ( $("#primaryBankingType").val() === "CHECK" ){// DFCT0017543 Ganesh
//            	isValid = true;
//            }
            //END
	    
            if (!isValid) {
                this.$el.trigger("showErrorMessage", ["Check Required Fields and Remove Any Invalid Characters"]);
            }
            return isValid;
        },
        revealSecondaryAccount: function ($collapseTarget, $collapseHeader) {
            this.collapseCloseSecondaryAccount();
            $collapseTarget.collapse("show");
            if (!$collapseTarget.hasClass("in")) {
                $collapseTarget.collapse("toggle");
            }
            $collapseHeader.addClass("active");
        },
        editItem: function (e) {
            var $button = $(e.currentTarget),
                collapseTarget = $button.data("target");

            this.revealSecondaryAccount($(collapseTarget), $button.parent());
            e.preventDefault();
        },
        // Begin of Comment and Insert by Naga ENHC0013668
//        removeItem: function (e) {
//            var index = $(e.currentTarget).data("id"),
//                secondaryAccount = this.secondaryAccounts[index];
//            secondaryAccount.remove();
//            delete this.secondaryAccounts[index];
//
//            e.preventDefault();
//        }, 
			removeItem: function (deletionIndex) {
	        	this.validateDelete.off("remove");
	        	this.validateDelete.off("close");
	        	
	            var secondaryAccount = this.secondaryAccounts[deletionIndex];
	            
	            secondaryAccount.remove();
	            delete this.secondaryAccounts[deletionIndex];
	            
	        },
			
	        closeClicked: function(){
	        	this.validateDelete.off("remove");
	        	this.validateDelete.off("close");
	
	        },
	
	        confirmDelete: function (e) {
	        	this.validateDelete.on("remove",this.removeItem);
	        	this.validateDelete.on("close",this.closeClicked);
	        	
	            this.validateDelete.display($(e.currentTarget).data("id"));
	            e.preventDefault();
	        },        
        // End of Comment and Insert by Naga
        editEmail: function(e){
            var $button = $(e.currentTarget),
                collapseTarget = $button.data("target");
            this.revealEmail($(collapseTarget), $button.parent().parent());    
        }, 
        // Begin of Comment and Insert by Naga ENHC0013668
//        removeEmail: function(e){
//            $(e.currentTarget).parents("div.accordion-group").remove();
//            
//            //<<< Added by Added by Philippe A. (206432942)
//            if($("[name='banking-primary-RoutingNum']").val() != "" 
//            && $("[name='banking-primary-AccountNum']").val() != "")
//            {
//                this.validateBankRoutingAndAccountNumber();
//            }
//            //>>>
//        },
        removeEmail: function (deletionTarget) {
        	this.validateDelete.off("remove");
        	this.validateDelete.off("close");
        	
        	$(deletionTarget).parents("div.accordion-group").remove();
          //<<< Added by Added by Philippe A. (206432942)
          if($("[name='banking-primary-RoutingNum']").val() != "" 
          && $("[name='banking-primary-AccountNum']").val() != "")
          {
              this.validateBankRoutingAndAccountNumber();
          }
          //>>>
            
        },

        confirmEmailDelete: function (e) {
        	this.validateDelete.on("remove",this.removeEmail);
        	this.validateDelete.on("close",this.closeClicked);
        	
            this.validateDelete.display(e.currentTarget);
            e.preventDefault();
        },        
        
        // End of Comment and Insert by Naga
        revealEmail: function($collapseTarget, $collapseHeader){
            var header;
            $("div.collapse[id^=emailContact]").stop(true,true).animate({ "height": "0px" },1);
            //update email header with value from input
            $("#email-accordion").find(".collapse.in").each(function () {
                header = $(this).parents(".accordion-group").find("input").val() || "Blank Name!";
                $(this).parents(".accordion-group").find(".email-header").text(header);
                //$(this).collapse("toggle");
               
            });

            $("#email-accordion .accordion-heading.active").each(function() {
                $(this).removeClass("active");
            });
            //$collapseTarget.collapse("toggle");
            $collapseTarget.stop(true,true).animate({ "height": "+=50px" },1);
            if (!$collapseTarget.hasClass("in")) {
                $collapseTarget.collapse("toggle");
            }
            $collapseHeader.addClass("active");
        },
        //validateVendorRequestTypesCombinations() added by Kermel Ruperto 07-10-2014
        isBankingInfoRequired: function()
        {
            var vendortypeElement = $("#vendorType");
            var requesttypeElement = $("#requestType");
            var vendortype = vendortypeElement.val();
            var requesttype = requesttypeElement.val();
    
            // Naga ENHC0013685 Add Revenue Share Vendor type 091
            // Naga ENHC0013683 Add Garnishment Vendor type 092
            // Naga ENHC0016461 Add Legal Settlement and Contest Winner

         // Added -018,095 - Pranesh(04/13/2016)  - ENHC0016459
            var allvendorTypes = ["010", "020", "030",  "040", "050", "060", "070", "080", "090", "091", "092", "093", "094","095","018"];
            //var validatevendorTypeArray = ["030", "040", "050", "070", "080"]; ENHC0016458
            var validatevendorTypeArray = [ "040", "070"];	// ENHC0016458//DEFT15060 030 removed
            var result = false;

            if($.inArray(vendortype, allvendorTypes) !== -1)
            {
                result = true;
                if($.inArray(vendortype, validatevendorTypeArray) !== -1 && (requesttype === '2')) 
                {
                    if($("#requestType").val() === "2")
                        result = false;
                }
            }

            return result;
        },

validateBankRoutingAndAccountNumber: function (e)
{
    var routingNumError = false;
    var accountNumError = false;
    var accountHolderError = false;//DEFT15060
    var success = false; // DFCT0017846 changed from true
            
    var routingNumElement = $("[name='banking-primary-RoutingNum']");
    var accountNumElement = $("[name='banking-primary-AccountNum']");
    var accountHolderElement = $("[name='banking-primary-HolderName']");//DEFT15060
    var countryVal        = $("[name='primary-account-country']");
    var routingNum = routingNumElement.val();
    var accountNum = accountNumElement.val();
    var accountHolder = accountHolderElement.val();//DEFT15060
    
            
    var removeBankRoutingOrAccountNumberWarning = function ()
    {
        routingNumElement.removeClass("user-error");
        accountNumElement.removeClass("user-error");
        accountHolderElement.removeClass("user-error");//DEFT15060
        routingNumElement.removeAttr("required");
        accountNumElement.removeAttr("required");
        accountHolderElement.removeAttr("required");//DEFT15060
    }
            
    var setBankRoutingAndAccountNumberAsRequired = function(){
        routingNumElement.attr("required", "");
        accountNumElement.attr("required", "");
        accountHolderElement.attr("required", "");//DEFT15060
   }
            
    var bankingInfoCompletionStatus = {"complete": 0, "incomplete":1, "empty": 2};
            
    var bankInfoStatus;
    if(routingNum != "" && accountNum != "" && accountHolder != ""){  //DEFT15060 added && accountHolder != ""
        bankInfoStatus = bankingInfoCompletionStatus.complete;
    }
    else if(routingNum != "" || accountNum != "" || accountHolder != ""){  //DEFT15060 added || accountHolder != ""
        bankInfoStatus = bankingInfoCompletionStatus.incomplete;
    }
    else{
        bankInfoStatus = bankingInfoCompletionStatus.empty;
    }
            
    var bankInfoRequired = false;
            
    if (bankInfoStatus == bankingInfoCompletionStatus.empty || bankInfoStatus == bankingInfoCompletionStatus.incomplete)
    {
//        bankInfoRequired = this.isBankingInfoRequired();// ganesh 
        //if/else added by Kermel Ruperto 07-10-2014
        if(bankInfoRequired)
        {
            setBankRoutingAndAccountNumberAsRequired();
            // Comment by Naga Enh 70, remove message
            //setBankRoutingOrAccountNumberWarning("Bank Account # and Bank Routing # required");
            routingNumError = routingNum == "";
            accountNumError = accountNum == "";
            accountHolderError = accountHolder =="";//DEFT15060
            success = false;
        }
        else if(bankInfoStatus == bankingInfoCompletionStatus.incomplete)
        {

            setBankRoutingAndAccountNumberAsRequired();
            if(routingNum == "")
            {
            	// Comment by Naga Enh 70, remove message
                //setBankRoutingOrAccountNumberWarning("If Bank Account # is provided, Bank Routing # is required");
                routingNumError = true;
                success = false;
            }
                        
            if(accountNum == "")
            {
            	// Comment by Naga ENHC0015302, remove message
            	//setBankRoutingOrAccountNumberWarning("If Bank Routing # is provided, Bank Account # is required");
                accountNumError = true;
                success = false;
            }
          //DEFT15060 start
            if(accountHolder == "")
            {
            	accountHolderError = true;
                success = false;
            }
          //DEFT15060 end
        }
    }
    else{
        var routingNumberPattern = "^[0-9]{9}$";
        var accountNumberPattern = "^[0-9X]*$";
                
        if (!routingNum)
            routingNum = "";
        if (!accountNum)
            routingNum = "";
        // By Naga 998 , do the routing number check only for US for non us ignore the bank key size
        if((!routingNum.match(routingNumberPattern))&&(countryVal == "US"))
        {
            routingNumError = true;
            success = false;
            setBankRoutingOrAccountNumberWarning("Only 9 digit numbers are allowed for Bank Routing #");
        }
        else if(!accountNum.match(accountNumberPattern))
        {
            setBankRoutingOrAccountNumberWarning("Invalid account number");
            accountNumError = true;
            success = false;
        }
      //DEFT15060 start
        if(accountHolder == "")
        {
        	accountHolderError = true;
            success = false;
        }
      //DEFT15060 end
        
    }
            
    if(routingNumError)
    {
        routingNumElement.addClass("user-error");
    }
    else
    {
        routingNumElement.removeClass("user-error");
    }
            
    if(accountNumError)
    {
        accountNumElement.addClass("user-error");
    }
    else
    {
        accountNumElement.removeClass("user-error");
    }
    //DEFT15060 start
    if(accountHolderError)
    {
        accountHolderElement.addClass("user-error");
    }
    else
    {
        accountHolderElement.removeClass("user-error");
    }
    //DEFT15060 end        
   if(success)
    {
        removeBankRoutingOrAccountNumberWarning();
    }

            
    if(!success)
    {
                
        this.$el.trigger("showBankingTab");
    }
                
    return success;
},


// Begin of Insert by Naga DFCT0013582
validatePrimaryBankDetails: function(e){
	var flag = "Primary";
	this.validateSecondaryBankRoutingAndAccountNumber(e,flag);	
},

validateSecondaryBankDetails: function(e){
	var flag = "Secondary";
	this.validateSecondaryBankRoutingAndAccountNumber(e,flag);
},

// End of Insert by Naga DFCT0013582

validateSecondaryBankRoutingAndAccountNumber: function (e,accountType)
{
    var routingNumError = false;
    var accountNumError = false;
    var accountHolderError = false; //DEFT15060
    var success = false; // DFCT0017846 changed from true
    var target = $(e.currentTarget);
    var parent = target.parents(".accordion-group");
    var routingNumElement = parent.find("[name$=RoutingNum]");
    var accountNumElement = parent.find("[name$=AccountNum]");
    var accountHolderElement = parent.find("[name$=HolderName]");//DEFT15060
  //  var holderNameElement = parent.find("[name$=HolderName]");//DEFT15060
    var countryVal = parent.find("[name$='country']").val();
    var typeAccount = (parent.find(".type-text").html())? parent.find(".type-text").html():this.$el.find("#primaryAccount").find(".type-text").html();
    var routingNum = routingNumElement.val();
    var accountNum = accountNumElement.val();
    var accountHolder = accountHolderElement.val();//DEFT15060
    
  //code start- ganesh // bank account masking
    var accountNumElement1 = parent.find("[name*=hidden-banking]").attr("id");
       
    
    if(accountNum.length>3)
    var acountLength=accountNum.length;
    var firstNumber=acountLength-4;
    	var lastNumber=acountLength;
    	if(accountNum.indexOf("X")<0)
    	{
    		
    		
    	var maskingSymbol="";
    	for(var i=0;i<firstNumber;i++)
    	{
    		maskingSymbol=maskingSymbol+"X";
    	}
    var idname=parent.find("[name$=AccountNum]").attr("id");
    if(idname==="banking-primary-AccountNum")	    	
    {
    $("#hidden-banking").val(this.$("#banking-primary-AccountNum").val());
    $("#hidden-banking").attr("value",this.$("#banking-primary-AccountNum").val());
    $("#banking-primary-AccountNum").attr("value",maskingSymbol+accountNum.substring(firstNumber,lastNumber));
    $("#banking-primary-AccountNum").val(maskingSymbol+accountNum.substring(firstNumber,lastNumber));
    }
    else
    {	$("#"+accountNumElement1).val(accountNum);	
    	$("#"+accountNumElement1).attr("value",accountNum);
    	parent.find("[name$=AccountNum]").val(maskingSymbol+accountNum.substring(firstNumber,lastNumber));	
    	parent.find("[name$=AccountNum]").attr("value",maskingSymbol+accountNum.substring(firstNumber,lastNumber));
    	//window.alert(accountNumElement1+":"+idname+":"+accountNum);
    }
    	}
    //code end - ganesh

    //variables added by Kermel Ruperto 07-10-2014
    var vendortypeElement = $("#vendorType");
    var requesttypeElement = $("#requestType");
    var vendortype = vendortypeElement.val();
    var requesttype = requesttypeElement.val();
    //Kermel
            
        //validateVendorRequestTypesCombinations() added by Kermel Ruperto 07-10-2014
        function isBankingInfoRequired()
        {
        	// Naga ENHC0013685 Add Revenue Share Vendor type 091
        	// Naga ENHC0013683 Add Garnishment Vendor type 092
        	// Naga ENHC0016461 Add Legal Settlement and Contest Winner
        	// Added -018,095 - Pranesh(04/13/2016)  - ENHC0016459
            var allvendorTypes = ["010", "020", "030",  "040", "050", "060", "070", "080", "090", "091", "092", "093", "094","095","018"];
            //var validatevendorTypeArray = ["030", "040", "050", "070", "080"]; ENHC0016458
            var validatevendorTypeArray = [ "040", "070"];	// ENHC0016458 //DEFT15060 030 removed
            var result = false;

            if($.inArray(vendortype, allvendorTypes) !== -1)
            {
                result = true;
                if($.inArray(vendortype, validatevendorTypeArray) !== -1 && (requesttype === '2')) 
                {
                    if($("#requestType").val() === "2")
                        result = false;
                }
            }

            return result;
        }

    var setBankRoutingOrAccountNumberWarning = function (message)
    {
        var bankRoutingOrAccountNumberWarning = parent.find(".bankRoutingOrAccountNumberWarning");
                
        if(bankRoutingOrAccountNumberWarning.length === 0)
        {
            bankRoutingOrAccountNumberWarning.slideUp();
        }
                
        bankRoutingOrAccountNumberWarning.text(message);
        bankRoutingOrAccountNumberWarning.slideDown();
    }
            
    var removeBankRoutingOrAccountNumberWarning = function ()
    {
        routingNumElement.removeClass("user-error");
        accountNumElement.removeClass("user-error");
        accountHolderElement.removeClass("user-error");  //DEFT15060
        routingNumElement.removeAttr("required");
        accountNumElement.removeAttr("required");
        accountHolderElement.removeAttr("required"); //DEFT15060

        parent.find(".bankRoutingOrAccountNumberWarning").stop(true, true).slideUp();
    }
            
    var setBankRoutingAndAccountNumberAsRequired = function(){
        routingNumElement.attr("required", "");
        accountNumElement.attr("required", "");
        accountHolderElement.attr("required",""); //DEFT15060

//        if(countryVal === "US" && typeAccount ==="ACH")//DEFT15060
//            holderNameElement.attr("required","");//DEFT15060
    }
            
    var bankingInfoCompletionStatus = {"complete": 0, "incomplete":1, "empty": 2};
            
    var bankInfoStatus;
   var file = target.parents(".accordion-body").find("[type=file]");
        if(countryVal === "US" && typeAccount ==="ACH")
    {
        if(routingNum != "" && accountNum != "" && accountHolder != ""){ //DEFT15060 added  &&  accountHolder != ""
            bankInfoStatus = bankingInfoCompletionStatus.complete;
            if(!isBankingInfoRequired() && file.next().length === 0)
               file.addClass("user-error").attr("required");
        }
        else if(routingNum != "" || accountNum != "" || accountHolder != ""){//DEFT15060 added  ||  accountHolder != ""
            bankInfoStatus = bankingInfoCompletionStatus.incomplete;
            if(!isBankingInfoRequired() && file.next().length === 0)
                file.addClass("user-error").attr("required");
        }
        else{
            bankInfoStatus = bankingInfoCompletionStatus.empty;
            // Do not do this for secondary accounts as all the details are mandatory irrespective of vendor type
            if(!accountType=="Secondary")	// Naga DFCT0013582
            	if(!isBankingInfoRequired() && file.next().length === 0)
            		file.removeClass("user-error").removeAttr("required");
        }
    }
    else
    {
        if(routingNum != "" && accountNum != "" && accountHolder != "" ){//DEFT15060 added  &&  accountHolder != ""
            bankInfoStatus = bankingInfoCompletionStatus.complete;
            if(!isBankingInfoRequired() && file.next().length === 0)
               file.addClass("user-error").attr("required");
        }
        else if(routingNum != "" || accountNum != "" || accountHolder != ""){ //DEFT15060 added  ||  accountHolder != ""
            bankInfoStatus = bankingInfoCompletionStatus.incomplete;
            if(!isBankingInfoRequired() && file.next().length === 0)
                file.addClass("user-error").attr("required");
        }
        else{
            bankInfoStatus = bankingInfoCompletionStatus.empty;
            // Do not do this for secondary accounts as all the details are mandatory irrespective of vendor type
            if(!accountType=="Secondary")	// Naga DFCT0013582            
            	if(!isBankingInfoRequired() && file.next().length === 0)
            		file.removeClass("user-error").removeAttr("required");
        }
    }
            
        var bankInfoRequired = false;
            
    if (bankInfoStatus == bankingInfoCompletionStatus.empty || bankInfoStatus == bankingInfoCompletionStatus.incomplete)
    {
        bankInfoRequired = isBankingInfoRequired();
        //if/else added by Kermel Ruperto 07-10-2014
        if(bankInfoRequired)
        {
            setBankRoutingAndAccountNumberAsRequired();
            
            		routingNumError = routingNum == "";
            		accountNumError = accountNum == "";
            		accountHolderError = accountHolder == "";//DEFT15060
            		success = false;
        }
        else if(bankInfoStatus == bankingInfoCompletionStatus.incomplete)
        {

            setBankRoutingAndAccountNumberAsRequired();
            // Begin of comment and insert by Naga ENHC0015302
            /*if(routingNum == "")
            {
                setBankRoutingOrAccountNumberWarning("If Bank Account # is provided, Bank Routing # is required");
                routingNumError = true;
                success = false;
            }
                        
            if(countryVal === "US" && typeAccount ==="ACH" && accountNum == "")
            {
                setBankRoutingOrAccountNumberWarning("If Bank Routing #, Bank Account # and Holder Name are required");
                accountNumError = true;
                success = false;
            }
            else
            {
                setBankRoutingOrAccountNumberWarning("If Bank Routing # is provided, Bank Account # is required");
                accountNumError = true;
                success = false;
            }

            if( countryVal === "US" && typeAccount ==="ACH" && holderName == "")
            {
                setBankRoutingOrAccountNumberWarning("If Bank Routing #, Bank Account # and Holder Name are required");
                accountNumError = true;
                success = false;
            }*/
            
            if(routingNum == ""){
                routingNumError = true;
                success = false;            	
            }
            if (accountNum = ""){
                accountNumError = true;
                success = false;
            }
          //DEFT15060 start
            if (accountHolder = ""){
                accountHolderError = true;
                success = false;
            }
          //DEFT15060 end
            // End of comment and insert by Naga 
        }
        // Begin of Insert by Naga 
        else if(bankInfoStatus == bankingInfoCompletionStatus.empty){
        	// When empty, make all details related to secondary account as required
            if(accountType=="Secondary"){
           	 	setBankRoutingAndAccountNumberAsRequired();
           	 	accountNumError = true;
           	 	accountHolderError = true; //DEFT15060
           	 	routingNumError = true;
           	 	success = false;
            }	
        }
        // End of Insert by Naga

    }
    else{
        var routingNumberPattern = "^[0-9]{9}$";
        var accountNumberPattern = "^[0-9X]*$";
                
        // By Naga 998 , do the routing number check only for US for non us ignore the bank key size
        if((!routingNum.match(routingNumberPattern))&&(countryVal == "US"))
        {
            routingNumError = true;
            success = false;
            setBankRoutingOrAccountNumberWarning("Only 9 digit numbers are allowed for Bank Routing #");
        }
        else if(!accountNum.match(accountNumberPattern))
        {
            setBankRoutingOrAccountNumberWarning("Invalid account number");
            accountNumError = true;
            success = false;
        }
        
        //DEFT15060 start
        if(accountHolder == "")
        {
        	accountHolderError = true;
            success = false;
        }
      //DEFT15060 end
    }
            
    if(routingNumError)
    {
        routingNumElement.addClass("user-error");
    }
    else
    {
        routingNumElement.removeClass("user-error");
    }
    
        if(accountNumError)
        {
            accountNumElement.addClass("user-error");
        }
        else
        {
            accountNumElement.removeClass("user-error");
        }

            if(accountHolderError)
            {
            	accountHolderElement.addClass("user-error");
            }
            else
            {
            	accountHolderElement.removeClass("user-error")
            }

   
    if(success)
    {
        removeBankRoutingOrAccountNumberWarning();
    }

            
    if(!success)
    {
                
        this.$el.trigger("showBankingTab");
    }
                
    return success;
}
});
    
/**
 * ContactView: Responsible for rendering self on the page
 */
app.views.ContactView = Backbone.View.extend({
	templates: {},
    initialize: function () {
        _.bindAll(this, "render");
        // Begin of Insert by Naga ENHC0016458
        // Contact is optional for Government and Utility
        this.templates["REQ"] = _.template($("#contactTemplate").html());
        this.templates["NONREQ"] = _.template($("#contactTemplateNonReq").html());    
        // End of Insert
        
    },

    template: _.template($("#contactTemplate").html()),
    render: function (parent, id) {
    	// Begin of Insert by Naga ENHC0016458
    	var template;
    	if(!($("#vendorType").val() === "050" || $("#vendorType").val() === "080")){
    		 template = this.templates["REQ"];
    	}else{
    		template = this.templates["NONREQ"];
    	}
    	// End of Insert 
    	//        this.$el = $(this.template({ id: id}));	// By Naga ENHC0016458
    	this.$el = $(template({ id: id}));					// By Naga ENHC0016458
        parent.append(this.$el);
        this.el = "#secondary-contact-" + id;
        this.delegateEvents(this.events);

        return this;
    }
});
    
/**
 * ContactsTabView: Responsible for the contacts tab
 */
app.views.ContactsTabView = Backbone.View.extend({
    el: "#tab5",
    events: {
        "click .back": "backToBanking",
        "click .edit-item": "editItem",
        "click .add-contact": "addContact",
        "click .icon-remove": "removeItem"
    },
    secondaryContactViews: {},
    secondaryContactTotal: 0,
    initialize: function () {
        var that = this,
            contact;
        _.bindAll(this, "backToBanking", "editItem", "addContact", "removeItem");
            
        this.$("#secondary-contact div.accordion-group").each(function () {
            contact = new app.views.ContactView({ el: this });
            that.secondaryContactViews[contact.$(".icon-remove").data("id")] = contact;
        });
        
        var collapseTarget = this.$("#contactTemplateBtn").data("target");
        this.revealSecondaryContact($(collapseTarget), this.$("#contactTemplateBtn").parent());
        this.$("#contactTemplateRmBtn").attr("style","display:none;");
        if(this.$("input").length == 0){
        	this.addContact();
        	this.$(".icon-remove.tip").attr("style","display:none;");
        }
    },
    addContact: function (e) {
        if(e)
        e.preventDefault();
        this.secondaryContactTotal++;
        var secondaryCollapseTarget,
           secondaryContact = new app.views.ContactView(),
           id = this.secondaryContactTotal;

        secondaryContact
            .render(this.$("#secondary-contact"), id);

        this.secondaryContactViews[id] = secondaryContact;
        secondaryCollapseTarget = secondaryContact.$(".edit-item").data("target");
        this.revealSecondaryContact($(secondaryCollapseTarget), secondaryContact.$(".accordion-heading"));

        return this;
    },
    backToBanking: function (e) {
        e.preventDefault();

        this.$el.trigger("showBankingTab");
    },
    removeItem: function (e) {
        var id = $(e.currentTarget).data("id"),
            secondaryContact = this.secondaryContactViews[id];
        secondaryContact.remove();
        delete this.secondaryContactViews[id];

        e.preventDefault();
    },
    editItem: function (e) {
        var $button = $(e.currentTarget),
            collapseTarget = $button.data("target");

        this.revealSecondaryContact($(collapseTarget), $button.parent());
        e.preventDefault();

    },
    revealSecondaryContact: function ($collapseTarget, $collapseHeader) {
        var header;
        $("#secondary-contact .collapse.in").each(function () {
            header = $(this).parents(".accordion-group").find(".name").val() || "Blank Name!";
            $(this).parents(".accordion-group").find(".item-label").text(header);
            $(this).collapse("toggle");
        });
        $("#secondary-contact .accordion-heading.active").each(function() {
            $(this).removeClass("active");
        });
        $collapseTarget.collapse("toggle");
        if (!$collapseTarget.hasClass("in")) {
            $collapseTarget.collapse("toggle");
        }
        $collapseHeader.addClass("active");
    },
    checkValidity: function () {
        var isValid = true;
        this.$("input, select, textarea").each(function () {
            if (!$(this).checkValidity()) {
                isValid = false;
            }
        });

        if (!isValid) {
            this.$el.trigger("showErrorMessage", ["Check Required Fields and Remove Any Invalid Characters"]);
//            $('html, body').animate({
//                scrollTop: $("#basicTab").offset().top
//            }, 2000);
        }
        return isValid;
    }
});

/**
 * Registration View: the main view for the registration page
 * Event listeners:
 *   showAddAdministrators: shows the add administartions view
 *   prepareTaxTab: sets up the bootstrap attributes and classes on the tax tab
 *   prepareTermsTab: configures bootstrap for the tab to be clickable
 *   prepareContactsTab: configures bootstrap for the tab to be clickable
 *   showBasicTab: programatically reveals the tab
 *   showTermsTab: programatically reveals the tab
 *   showBankingTab: programatically reveals the tab
 *   showContactsTab: programatically reveals the tab
 *   showTaxTab: reveals the tax tab
 *   completeBasicTab: marks the basic tab as complete
 *   completeTaxTab: marks the basic tab as complete
 *   completeTermsTab: marks the basic tab as complete
 *   completeBankingTab: marks the basic tab as complete
 *   completeContactsTab: marks the basic tab as complete
 *   registerFileUploads: re-configures the file uplaods
 */
app.views.RegistrationView = app.views.BaseView.extend({
    events: {
        "showAddAdministrators": "addAdministrators",
        "submitDecision":"submitDecision",	// Naga ENHC0019060
        "click .btn-group[data-toggle=buttons-radio] a": "trackButtonGroupState",
        "click .navbar .nav li .dropdown-toggle": "vendorNameDropdown",

        "click #resendApprovalBtn": "resendApproval",	// Naga ENHC0013682
        "click #cancelRequestBtn": "cancelRequest", 	// Naga ENHC0013682
        "click #approveButton":"approveRequest",		// Naga ENHC0019060
        "click #rejectButton": "rejectRequest",			// Naga ENHC0019060
        
        "registerFileUploads": "registerFileUploads",

        "prepareTaxTab": "prepareTaxTab",
        "prepareTermsTab": "prepareTermsTab",
        "prepareBankingTab": "prepareBankingTab",
        "prepareContactsTab": "prepareContactsTab",

        "showTaxTab": "showTaxTab",
        "showBasicTab": "showBasicTab",
        "showTermsTab": "showTermsTab",
        "showBankingTab": "showBankingTab",
        "showContactsTab": "showContactsTab",

        "completeBasicTab": "completeBasicTab",
        "completeTaxTab": "completeTaxTab",
        "completeTermsTab": "completeTermsTab",
        "completeBankingTab": "completeBankingTab",
        "completeContactsTab": "completeContactsTab",
            
        "click .save": "saveForm",
        "click .submit": "saveForm",
        "click .resubmit": "saveForm",
//        "click .icon-remove": "removeFile",
        "click .remove-file": "removeFile",			// To avoid confusion with other Deletes ENHC0013668
        "change input[type=file]": "trackFileChange",
        "change input": "trackChanges",
        "keyup input": "trackChanges",
        "keyup textarea": "trackChanges",
        "change select": "trackChanges",
        "change textarea": "trackChanges"
    },
    mode: undefined,
    decisionMode: undefined,
        
    initialize: function () {
        this.base();
        var that = this;
        $("#errorMessages").hide();
        _.bindAll(this, "addAdministrators", "trackButtonGroupState", "prepareTaxTab", "showTaxTab", "completeBasicTab", "saveForm", "showBasicTab",
            "showTermsTab", "completeTaxTab", "prepareTermsTab", "completeTermsTab", "prepareBankingTab", "showBankingTab", "prepareContactsTab",
            "completeBankingTab", "completeContactsTab", "vendorNameDropdown", "configurePage", "trackChanges", "registerFileUploads", "resendApproval", 
            "cancelRequest","makeAppReadOnly","submitDecision","approveRequest","rejectRequest"); // ENHC0019060 Make the application read only
           
        $( ".shows-error-message" ).prepend($(document.createElement("span")).addClass("accordion-glyph"));
        this.vendorAccordion = $( ".sidebar" ).accordion({beforeActivate: function( event, ui ) {
            var active = that.vendorAccordion.accordion("option", "active");
            if(active !== false)
            {
                $(".shows-error-message .accordion-glyph").removeClass("expanded");
            }
            else{
                $(".shows-error-message .accordion-glyph").addClass("expanded");
            }
        }, activate: function(event, ui){
            var active = that.vendorAccordion.accordion("option", "active");
        },header:".shows-error-message", icons:false, collapsible: true, active: false, disabled:false, heightStyle: "content"});
            
            
        // modals
        this.addAdministratorModalView = new app.views.AddAdministratorModalView();
        this.termsModalView = new app.views.TermsModalView();
        this.decisionModalView = new app.views.decisionModalView();	// Naga ENHC0019060

        //tabs
        this.basicTabView = new app.views.BasicTabView();
        this.taxTabView = new app.views.TaxTabView();
        this.termsTabView = new app.views.TermsTabView();
        this.bankingTabView = new app.views.BankingTabView();
        this.contactsTabView = new app.views.ContactsTabView();
        
        this.cancelRequestView = new app.views.CancelRequest();		// Naga ENHC0013682 User may have ability to cancel request

        this.mode = this.options.mode;
        this.from = this.options.from;
        this.registerFileUploads();

        //todo: start, debug code to be removed after integration

        if (Backbone.history.location.hash === "#maintain") {
            this.mode = "maintain";
        }
            
        if (Backbone.history.location.hash === "#locked") {
            this.mode = "locked";
        }

        if (Backbone.history.location.hash === "#taxTab") {
            this.completeBasicTab();
            this.prepareTaxTab();
            this.showTaxTab();
            return;
        }
        if (Backbone.history.location.hash === "#termsTab") {
            this.completeBasicTab();
            this.completeTaxTab();
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.showTermsTab();
            return;
        }
        if (Backbone.history.location.hash === "#bankingTab") {
            this.completeBasicTab();
            this.completeTaxTab();
            this.completeTermsTab();
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.prepareBankingTab();
            this.showBankingTab();
            return;
        }
        if (Backbone.history.location.hash === "#contactsTab") {
            this.completeBasicTab();
            this.completeTaxTab();
            this.completeTermsTab();
            this.completeBankingTab();
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.prepareBankingTab();
            this.prepareContactsTab();
            this.showContactsTab();
            return;
        } 
            
        var tAndC = $("#tAndC").val();
        if (Backbone.history.location.hash !== "#skipTerms" && this.mode !== "behalf" && this.mode !== "maintain" && this.mode !== "approval" && tAndC !== "X") {  // Added Approval ENHC0019060
            this.termsModalView.show();
        }
        // todo: end debug code
            
        this.configurePage();

        if (!!this.mode && this.mode !== "behalf" && this.mode !== "maintain" && this.mode !== "approval") {		// Added approval ENHC0019060
            this.termsModalView.show();
        }
        // Begin of Insert by Naga ENHC0019060
        // Make the application read only if it is locked.
    	if(this.mode === "locked" || this.mode === "approval"){
    		this.makeAppReadOnly();
    	}
        // End of Insert by Naga        
    },
    registerFileUploads: function () {
    	var that2 = this;
        $("input[type=file]").bind("fileuploadsubmit", function (e, data) {
            var requestId = $("#requestId").val(),
                docType = $("#uploadDocumentType").val(),
            	fileType = $(this).attr("fileType"),
            	vendorId = $("#vendorId").val();		// Naga -- DFCT0013688 
            // Naga fix production issue to handle attachments scenario when there is no open request

            // Added venorId to JSON DFCT0013688
            data.formData = { id: requestId, objtype: docType, action: 'upload', fileType:fileType ,vendorId: vendorId};
        });
        $("input[type=file]").fileupload({
            dataType: 'json',
            send: function(e, data) {
        		that2.$el.trigger("showSpinner");
              },
            done: function (e, data) {
                var that = this;
                $(that).parent().find('.fileName').remove();
                $(that).parent().find('.icon-remove').remove();
                if(data.result.name === "taxw9")
                  $("#taxInfo").find("[type=file]").next().remove();
                //$(that).parent().append($("<span/>").text(data.result.name).addClass("fileName"));
                $(that).parent().append($("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+data.result.id+"&filename="+data.result.name+"\" target=\"_new\">"+data.result.name+"</a></span>"));
                $(that).parent().append("<i class=\"icon-remove remove-file\"></i>");
                $(that).parent().find("[type=hidden]").val(data.result.id);                  
                
                //$.each(data.result.files, function (index, file) {
                //   $(that).parent().append($("<span/>").text(file.name));
                //});
              
                $(that).removeClass("user-error").removeAttr("required");
                $(that).removeClass("user-error").prop("disabled",true);             
                $(that).next(".fileRequiredError").hide();
                
                that2.$el.trigger("hideSpinner");
                    
            },
            fail: function (e, data) {
                // console.log(data);
            }
        });
    },
    trackChanges: function () {
        if (this.mode !== "maintain") {
            return; // nothing to do
        }

        this.$el.addClass("resubmit");
    },
    configurePage: function () {
        if (this.mode === "maintain") {
            this.$el.attr("class", "maintain");
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.prepareBankingTab();
            this.prepareContactsTab();
        } else if (this.mode === "locked") {
            this.$el.attr("class", "maintain locked");
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.prepareBankingTab();
            this.prepareContactsTab();
        // Begin of Insert by Naga ENHC0019060
        } else if (this.mode === "approval") {
            this.$el.attr("class", "maintain locked");
            this.prepareTaxTab();
            this.prepareTermsTab();
            this.prepareBankingTab();
            this.prepareContactsTab();            
        // End of Insert by Naga    
        } else {
            this.$el.attr("class", "registration");
        }
    },
    
    // Begin of Insert by Naga ENHC0019060
    makeAppReadOnly: function(){
    	this.$("input, select, radio, textarea").each(function(idx,itm){
    		if(itm.name === "agreeTerms" || 
    				itm.name === "decisionComments" || 
    				itm.name === "contactPerson" ||
    				itm.name === "rejectionReason" ||
    				itm.name === "approvalReason" ||
    				itm.id === "contactPersonName" ||
    				itm.type === "hidden"){
    			// Do not make these read only
    		}else{
    			$(itm).prop('disabled','disabled');
    		}
    	});
    	// Terms input should not be disabled
    	this.$el.find('[name="agreeTerms"]').prop('disabled','');
    },
    // End of Insert by Naga

    vendorNameDropdown: function (e) {
        e.preventDefault();
        this.$('[data-toggle="popover"]').popover('hide');
    },
    showTermsTab: function () {
        this.$("#termsTab").trigger("click");
    },
    showBankingTab: function () {
        this.$("#bankingTab").trigger("click");
    },
    showContactsTab: function () {
        this.$("#contactsTab").trigger("click");
    },
    prepareTermsTab: function () {
        this.$('#termsTab').attr("data-toggle", "tab")
            .attr("href", "#tab3")
            .parent().removeClass("disabled");
    },
    prepareBankingTab: function () {
        this.$('#bankingTab')
            .attr("data-toggle", "tab")
            .attr("href", "#tab4")
            .parent().removeClass("disabled");
            
    },
    prepareContactsTab: function () {
        this.$('#contactsTab').attr("data-toggle", "tab")
            .attr("href", "#tab5")
            .parent().removeClass("disabled");
    },
    completeContactsTab: function (evt, incomplete) {
        if (!!incomplete) {
            this.$("#contactTab").parent().removeClass('complete');
        } else {
            this.$("#contactsTab").parent().addClass('complete');
        }
    },
    completeTaxTab: function (evt, incomplete) {
        if (!!incomplete) {
            this.$("#taxTab").parent().removeClass('complete');
        } else {
            this.$("#taxTab").parent().addClass('complete');
        }
    },
    completeBankingTab: function (evt, incomplete) {
        if (!!incomplete) {
            this.$("#bankingTab").parent().removeClass('complete');
        } else {
            this.$("#bankingTab").parent().addClass('complete');
        }
    },
    completeTermsTab: function (evt, incomplete) {
        if (!!incomplete) {
            this.$("#termsTab").parent().removeClass('complete');
        } else {
            this.$("#termsTab").parent().addClass('complete');
        }
    },
    saveForm: function (e) {
        var that = this,
            action = $(e.currentTarget).val(),
            url = $("#main").attr("action"),
            messageNumber = "";
            
        if (action !== "save") {
            if (!this.basicTabView.checkValidity()) {
                this.showBasicTab();
                return;
            }
            
            // Begin of Insert by Naga ENHC0016458 and ENHC0016461
            if (!(($("#vendorType").val() === "060") || ($("#vendorType").val() === "040" ) || ($("#vendorType").val() === "050" ) || ($("#vendorType").val() === "092" ))){
	            if (!this.taxTabView.checkValidity()) {
	                this.showTaxTab();
	                return;
	            }
            }
            if (!(($("#vendorType").val() === "060") || ($("#vendorType").val() === "040" ) || ($("#vendorType").val() === "050" ) || ($("#vendorType").val() === "093" ) || ($("#vendorType").val() === "094" ))){            
	            if (!this.termsTabView.checkValidity()) {
	                this.showTermsTab();
	                return;
	            }            
            }
            // End of Insert
            
        	
            if (!this.bankingTabView.checkValidity()) {
                this.showBankingTab();
                return;
            }
            
            if ($("#vendorType").val() != "060"){
	            if (!this.contactsTabView.checkValidity()) {
	                this.showContactsTab();
	                return;
	            }
            }
        }
        $("input[name=action]").val(action);

        this.$el.trigger("showSpinner");

        var x = [];
        $.each($("#secondary-account").find(".type"),function(i)
        {
          var value = $(this).val();
          if(value.match(/[WU]/))
            value = value.substring(1);
          x.push(value)
        });
        if(x.length > 0)
            $("#secondary-address-order").val(x);
        var postdata = $("#main").serialize();
        $.ajax({
            type: "POST",
            url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.objectactions?actionCode=save",
            data: $("#main").serialize(),
            dataType: "json"
        }).done(function (response) {
        	messageNumber = parseInt(response.messageNumber);
            if (response.code === "0") {
            
            	
            	if(action === "save"){

            		that.$el.trigger("showSuccessMessage", [response.message]);


            	}
                $("#requestId").val(response.requestNumber);
                if (action !== "save") {
                	// Begin of Insert and Comment by Naga 03/30/15 ENHC0015302
                	// Instead of displaying a custom message, message retrieved from backend is relayed.
                	
                    //window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=Request Submitted Successfully";
                	if(messageNumber<100){
                		
                		// Determine if foreign vendor
                        var vendorEntity = $("input[name=vendorEntity]:checked").val();
                        var vendorEntityLoc = $("input[name=vendorEntityLoc]:checked").val();
                        var vendorIndvLoc = $("input[name=vendorIndvLoc]:checked").val();
                        var vendorIndvResidence = $("input[name=vendorIndvResidence]:checked").val();
                        var vendorIndvPresence = $("input[name=vendorIndvPresence]:checked").val();
                        var reqType = $("#requestType").val();
                        
                        var foreignVendor;
            	    	if(vendorEntity == 4 && vendorEntityLoc == 1){
            	    		foreignVendor = true;
            	    	}else if(vendorEntity == 3 && (vendorIndvLoc == 2 && vendorIndvResidence == 2 && vendorIndvPresence == 2)){
            	    		foreignVendor = true;
            	    	}
            	    	if(foreignVendor&&reqType==1){
                    		that.ctiModalView = new app.views.CTIModalView({ctiurl:$("#urlcti").val(),ctiuser:$("#ernamcti").val(),ctiregcode:$("#regCodecti").val(),message:response.message});
                    		that.ctiModalView.show();            	    		
            	    	}else{
            	    		window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message="+response.message+"&type=success";
            	    	}
        //        		window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message="+response.message+"&type=success";
                	}else{
                		// This scenario may not arise
                		that.$el.trigger("showSuccessMessage", [response.message]);
                	}
                	// End of Insert and Comment by Naga                    
                }                    
            } else {
                
                // Begin of Insert by Naga 03/30/15 ENHC0015302
                // Also redirect error message to Status page. In future this will be done only if the message number is in certain range.
                // For now all the error messages from Submit will be sent to Status page.
            	//that.$el.trigger("showErrorMessage", [response.message]);
            	
            	if(action==="save"){
            		that.$el.trigger("showErrorMessage", [response.message]);
            	}
                if( action!="save"){
                	if(messageNumber<100){
                		window.location = "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message="+response.message+"&type=error";
                	}
                	// Begin of Insert by Naga ENHC0013673  271215
                	// When the error message number is 200, take it to tax tab.
                	else if(messageNumber<200){
                		if( messageNumber === 101){
                			// Upload Support Documentation is required
                			that.$("#tab1").find("input[name=supportdoc]").addClass("user-error");
                		}
                		that.showBasicTab();					// ENHC0013673                		
                		that.$el.trigger("showErrorMessage", [response.message]);
                	}else if(messageNumber<300){
                		if(messageNumber === 201){
                			// W9 form is required
                			that.$("#tab2").find("input[name=taxw9]").addClass("user-error");
                		}else if(messageNumber === 202){// start Ganesh DFCT0016893 -(04/28/2016)
                			// start Ganesh DFCT0016893
                            // IC Qns is required
                          //  that.$("#tab2").find("input[name^='taxSsnQ']").closest('.accordion-body').addClass("question-user-error");
                        $("#LEQ").find(".accordion-body").addClass("question-user-error");
                 }
                            // end Ganesh DFCT0016893
                		that.showTaxTab();					// ENHC0013673                		
                		that.$el.trigger("showErrorMessage", [response.message]);
                	}else if(messageNumber<400){
                		that.showTermsTab();					// ENHC0013673                		
                		that.$el.trigger("showErrorMessage", [response.message]);
                	}else if(messageNumber<500){
                		if(messageNumber === 401){
                			// Primary Bank form is required
                			that.$("#tab4").find("input[name=primaryACH]").addClass("user-error");
                		}else if(messageNumber > 401){
                			// Find the id
                			var id = messageNumber.toString();
                			id = id.substr(1,2);
                			id = id - 1;
                			// Make the form required and set the error
                			that.$("#tab4").find("input[name=bankingSecondary-"+id+"-AchForm]").addClass("user-error");
                			// Expand the secondary account only if it is already not expanded
                			if(!that.$("#secondary-account-container-"+id).find(".edit-item").parent().hasClass("active")){
//                				that.$("#secondary-account-container-"+id).find(".edit-item").trigger("click");
                				var collapseTarget = that.$("#secondary-account-container-"+id).find(".edit-item").data("target");
                				$(collapseTarget).collapse("toggle");
                				$(collapseTarget).css("height","auto");
                				that.$("#secondary-account-container-"+id).find(".edit-item").parent().addClass("active");
                			}
                			
                		}
                		that.showBankingTab();					// ENHC0013673                		
                		that.$el.trigger("showErrorMessage", [response.message]);
                	}else if(messageNumber<600){
                		that.showContactsTab();					// ENHC0013673                		
                		that.$el.trigger("showErrorMessage", [response.message]);
                	}
                	// End of Insert by Naga 
                	
                	
                	else{

                		that.$el.trigger("showErrorMessage", [response.message]);
                	}
                	
                }
                // End of Insert by Naga                
            }
        }).always(function () {
            that.$el.trigger("hideSpinner");
            // Begin of Insert by Naga ENHC0015302
            // For certain scenarios user should be taken to basic tab. Message number range is used to decide that.
            if(messageNumber>100&&messageNumber<200){
            	$("#basicTab").trigger("click");
            }
            // End of Insert by Naga
        });

        e.preventDefault();
    },
   removeFile:function(e){
    	 var $target = $(e.currentTarget);
    	 if ($target.parent().find("[type=file]").attr('name') !== "tax590") {
    		 if ($target.parent().find("[type=file]").attr('name') == "taxw9") {
     			 // Naga ENHC0013685 Add Revenue Share Vendor type 091
    			 // Naga ENHC0013683 Add Garnishment Vendor type 092 Removed
    			 // Naga ENHC0016458 Add Utility
    			 // Naga ENHC0016461 Add Legal Settelment and Contest Winner
    			 
    			 // Added -018 - Pranesh(04/13/2016)   - ENHC0016459
    			 // Removed -095 - Pranesh(04/14/2016) - ENHC0016459
    			 if (($("#vendorId").val()==null || $("#vendorId").val().length == 0)&&(($("#vendorType").val() == "010") ||($("#vendorType").val() == "020") || ($("#vendorType").val() == "060") || ($("#vendorType").val() == "090") || ($("#vendorType").val() == "091") || ($("#vendorType").val() == "080") || ($("#vendorType").val() == "093") || ($("#vendorType").val() == "094") || ($("#vendorType").val() == "018"))) {
        			 $target.parent().find("[type=file]").addClass("user-error").attr("required","required"); 
    			 }
    			 
    		 // Begin of Insert by Naga ENHC0015302
    		 // Remove file in primary bank account whose information is not required should not make the file required
    		 // 998 Do this only if it is non vendor login
    			 
    		 }else if(($target.parent().find("[type=file]").attr('name') == "primaryACH")){
    			 // Naga ENHC0013685 Add Revenue Share Vendor type 091
    			 // Naga ENHC0013683 Add Revenue Share Vendor type 092 Removed
    			 // Naga ENHC0016458 Add Political, Government, Utility
    			 // Naga ENHC0016461 Add Legal Settelment and Contest Winner    			 
//    			 if(($("#requestType").val() == "2")&&($("#vendorType").val() == "010") ||($("#vendorType").val() == "020") || ($("#vendorType").val() == "030") || ($("#vendorType").val() == "090") || ($("#vendorType").val() == "091") || ($("#vendorType").val() == "040") || ($("#vendorType").val() == "050") || ($("#vendorType").val() == "080") || ($("#vendorType").val() == "093") || ($("#vendorType").val() == "094"))
    			
    			 //Added -018,095 - Pranesh(04/13/2016)  - ENHC0016459
   				 if(($("#requestType").val() != "1" && ( $("#vendorId").val()==null || $("#vendorId").val().length == 0 ))&&(($("#vendorType").val() == "010") ||($("#vendorType").val() == "020") || ($("#vendorType").val() == "030") || ($("#vendorType").val() == "090") || ($("#vendorType").val() == "091") || ($("#vendorType").val() == "040") || ($("#vendorType").val() == "050") || ($("#vendorType").val() == "080") || ($("#vendorType").val() == "093") || ($("#vendorType").val() == "094") || ($("#vendorType").val() == "095") || ($("#vendorType").val() == "018")    ))
    				 $target.parent().find("[type=file]").addClass("user-error").attr("required","required");
    		 }
    		 // End of Insert by Naga
    		 // Begin of Insert by Naga 998
    		 // Bank forms are not required for vendor login
    		 // Primary Account is handled above , we have to handle secondary accounts now
    		 else if(($("#requestType").val() == 1 || !( $("#vendorId").val()==null || $("#vendorId").val().length == 0 ))&&($target.parent().find("[type=file]").attr('name').indexOf("bankingSecondary")> -1)){
    		 }
    		 // End of Insert by Naga
    		 else {
    			 //Start - Pranesh - (05/17/2016) - (Defect:15095)
    		     $target.parent().find("[type=file]").addClass("user-error").attr("required","required");
//    		     if( (!($("#vendorId").val()==null)) || $("#vendorId").val().length > 0){
//    		    	 $target.parent().find("[type=file]").removeClass("user-error").removeAttr("required","");
//    		     }else{
//    		    	 $target.parent().find("[type=file]").addClass("user-error").attr("required","required");
//    		     }
    		     //End - Pranesh - (05/17/2016) - (Defect:15095)
    		 }
    	 }
    	 $target.parent().find("[type=hidden]").val("");    
    	 $target.parent().find("[type=file]").prop("disabled",false);  	 
    	 $target.parent().find(".fileRequiredError").show();   
    	 $target.parent().find("span").remove();   	
    	 $target.parent().find(".icon-remove").remove();
    	 
    },
    // Begin of Insert by Naga ENHC0013682
    resendApproval: function(){
        var that = this;
        this.$el.trigger("showSpinner");
        $.ajax({
            url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.requestactions?operation=A&requestnum="+$("#requestId").val(),
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

    cancelRequest: function () {
    	this.cancelRequestView.show();
    },    
    // End of Insert by Naga
    trackButtonGroupState: function(e) {
        var $target = $(e.currentTarget),
            buttonIndex = $target.index(),
            $hiddenRadio = $target.parent().find('.hidden-form-elements').find('input');

        $hiddenRadio.prop('checked', false);
        $hiddenRadio.eq(buttonIndex).prop('checked', true);

        //<< Jorge Perez (206443532)
        if(!$target.hasClass("btn-info"))
        {
            $hiddenRadio.removeAttr("required");       
            var parent =  $target.parents(".accordion-group").first();
            if(parent.next().css("display") === "none" && $target.hasClass("yes-answer") && parent.find("[name=independantContractor]").length)
            {
                parent.nextAll(".accordion-group").slideDown().find(".accordion-question").addClass("question-user-error");
                parent.nextAll(".accordion-group").find("[type=radio]").attr("required", "");

            }
            else if(parent.find("[name=independantContractor]").length && !$target.hasClass("yes-answer"))
            {
                 parent.nextAll(".accordion-group").slideUp().find(".accordion-question").removeClass("question-user-error");
                 parent.nextAll(".accordion-group").find("[type=radio]").removeAttr("required","")
                 parent.nextAll(".accordion-group").find(".btn").removeClass("active")
             }
            parent.find(".accordion-question").removeClass("question-user-error");

        }
        //>>
        $($hiddenRadio[0]).trigger("change");	
    },
    addAdministrators: function() {
        this.addAdministratorModalView.show();
    },
    // Begin of Insert by Naga ENHC0019060
    submitDecision:function(){
        var that = this;
        this.$el.trigger("showSpinner");
        var operation = (this.decisionMode === "Approve")?"1":"2";
        var postData = $("#main").serialize();
        $.ajax({
//            url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.requestactions?operation="+operation+"&requestnum="+$("#requestId").val(),
            url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.requestactions?operation="+operation+"&requestnum="+$("#requestId").val()+"&decisionComments="+$(".decisionComments").val()+"&contactPerson="+$(".contactPerson").val()+"&approvalReason="+$(".approvalReason").val()+"&rejectionReason="+$(".rejectionReason").val(),// DFCT0017924 
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
    approveRequest: function(){
    	this.decisionMode = "Approve";
    	this.decisionModalView.approveWindow();
    },
    rejectRequest: function(){
    	this.decisionMode = "Reject";
    	this.decisionModalView.rejectWindow();
    },
    // End of Insert by Naga
    completeBasicTab: function (evt, incomplete) {
        if (!!incomplete) {
            $("#basicTab").parent().removeClass('complete');
        } else {
            $("#basicTab").parent().addClass('complete');
        }
    },
    showTaxTab: function () {
        $("#taxTab").trigger("click");
    },
    showBasicTab: function () {
        $("#basicTab").trigger("click");
    },
    prepareTaxTab: function () {
        $('#taxTab, #basicTab').attr("data-toggle", "tab");
        $('#basicTab').attr("href", "#tab1")
            .parent().removeClass("disabled");
        $('#taxTab').attr("href", "#tab2")
            .parent().removeClass("disabled");
    },
    trackFileChange: function (e) {
        var $field = $(e.currentTarget),
            $label = $field.siblings(".uploaded-file"),
            $uploadIcon = $field.siblings(".upload-file");//,
        //$reup = $field.siblings(".reupload-file");

        function getFilename(path) {
            var lastIndexOf = path.lastIndexOf("\\");
            if (lastIndexOf >= 0) {
                return path.substring(lastIndexOf + 1);
            }
            return path;
        }

        $label.find("a").text(getFilename($field.val()));
        $label.show();
        //$reup.show();
        $uploadIcon.hide();
    }
});

// Begin of Insert by Naga ENHC0013682
/**
 * Cancel Invite: Responsible for cancel reason selection
 * Events:
 */
app.views.CancelRequest = Backbone.View.extend({
    el: "#cancelRequest",
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
            url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.requestactions?operation=C&requestnum="+$("#requestId").val()+"&cancelCode="+this.$("#cancelReason").val(),
            type: "GET",
            data: this.$el.serialize(),
            dataType: "json"
        }).done(function (data) {
        	if(data.code==="0")
            window.location.replace("/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra?message=" + encodeURIComponent(data.message)+"&type=success");
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
// End of Insert by Naga
}());

