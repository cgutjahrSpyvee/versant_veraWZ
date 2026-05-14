/*global window, app, Backbone, $, _, location */

(function (global) {
    "use strict";

    /**
     * Common namespaces
     */
    global.app = global.app || {};
    app.views = app.views || {};
    app.models = app.models || {};
    app.page = app.page || {};
    app.utils = app.utils || {};

    app.utils.url = {
        param: function(name) {
            name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
            var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
                results = regex.exec(location.search);
            return !!results ? decodeURIComponent(results[1].replace(/\+/g, " ")) : "";
        }
    };
    
    app.utils.countryService = {
        provinces: {},
        countries: {},		// Naga ENHC0013660
        
        // Begin of comment by Naga ENHC0013660        
        /*getCountries: function () {
            var that = this;
            if (!!this.countries) {
                return this.countries;
            }
            $("body").trigger("showSpinner");
            $.ajax({
                type: "GET",
                url: "json/countries.php",
                contentType: "application/json",
                dataType: "json",
                async: false
            }).done(function (result) {
                that.countries = result;
            }).always(function () {
                $("body").trigger("hideSpinner");
            });
            return this.countries; 
        },*/
      /*  getProvinces: function (country) {
            var that = this;
            if (!!this.provinces[country]) {
                return this.provinces[country];
            }

            $("body").trigger("showSpinner");
            $.ajax({
                type: "GET",
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=getProvinces&c=" + country,
                contentType: "application/json",
                dataType: "json",
                async: false
            }).done(function (result) {
                //that.provinces[country] = result;
            	that.provinces = result;
            }).always(function () {
                $("body").trigger("hideSpinner");
            });
              return this.provinces;	
            //return this.provinces[country];
        }, */
        // End of comment by Naga ENHC0013660
        
//		Begin of Insert by Naga ENHC0013660	        
        getProvinces: function (country) {
        	var that = this;
            if (this.provinces.length > 1) {
                return this.provinces;
            }        	
        	$("body").trigger("showSpinner");
            $.ajax({
                type: "GET",
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=getRegion",
                contentType: "application/json",
                dataType: "json",
                async: false
            }).done(function (result) {
                //that.provinces[country] = result;
            	that.provinces = result;
            }).fail(function (result){
            	
            	var resultinfo = result;
            	
            }).always(function () {
                $("body").trigger("hideSpinner");
            });

            //return this.provinces[country];
            return this.provinces;
        },    
        
        getCountries: function () {
        	var that = this;
            if (this.countries.length > 1) {
                return this.countries;
            }         	
        	$("body").trigger("showSpinner");
            $.ajax({
                type: "GET",
                url: "/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.htmlhelper?type=getCountry",
                contentType: "application/json",
                dataType: "json",
                async: false
            }).done(function (result) {
                //that.provinces[country] = result;
            	that.countries = result;
            }).always(function () {
                $("body").trigger("hideSpinner");
            });

            //return this.provinces[country];
            return this.countries;
        }          
//		End of Insert by Naga ENHC0013660        
        
    };
    
  
    
    /**
     * Pagination View
     * Common for client-side pagination
     */
    app.views.PaginationView = Backbone.View.extend({
        events: {
            "page-changed": "pageChanged",
            "change .pageSize": "configurePagination"
        },

        initialize: function () {
            _.bindAll(this, "pageChanged", "configurePagination", "setPage");

            this.$el.bootstrapPaginator({
                size: "small",
                itemTexts: function (type, page) {
                    switch (type) {
                        case "next":
                            return '<i class="icon-caret-right"></i>';
                        case "prev":
                            return '<i class="icon-caret-left"></i>';
                        case "page":
                            return page;
                        default:
                            return "";
                    }
                },
                shouldShowPage: function (type) {
                    switch (type) {
                        case "first":
                        case "last":
                            return false;
                        default:
                            return true;
                    }
                },
                onPageChanged: this.pageChanged
            });
        },
        pageChanged: function (e, oldPage, newPage) {
            if(typeof(this.options.pageChangeDelegate) === "function")
            {
                this.options.pageChangeDelegate();
            }
            
            this.setPage(newPage);
        },
        setPage: function (currentPage) {
            var items = this.items,
                total = items.length,
                pageSize = this.pageSize,
                i;

            items.each(function () {
                $(this).addClass("hide");
            });

            for (i = (currentPage * pageSize) - pageSize; i < currentPage * pageSize; i++) {
                if (i >= total) {
                    break;
                }
                $(items[i]).removeClass("hide");
            }
        },
        configurePagination: function (items, pageSize) {
            this.pageSize = parseInt(pageSize, 10);
            this.items = items;

            var totalPages = Math.ceil(this.items.length / pageSize);

            if (this.items.length === 0) {
                this.$el.hide();
                return;
            }
            this.$el.bootstrapPaginator({
                currentPage: 1,
                totalPages: totalPages
            });
            this.$el.show();
            this.$el.bootstrapPaginator("showFirst");
            this.setPage(1);
        }
    });


    /**
     * BodyView: base view for other root views
     * Event Listeners:
     *    $("body").trigger("showSuccessMessage", ["message"]): Show a success message
     *    $("body").trigger("showErrorMessage", ["message"]): Shows an error message
     *    $("body").trigger("showSpinner"): Show the activity spinner
     *    $("body").trigger("hideSpinner"): Hides the activity spinner
     */
    app.views.BaseView = Backbone.View.extend({
        el: "body",
        spinnerCount: 0,
        
        baseEvents: {
            "showSuccessMessage": "successAlert",
            "showErrorMessage": "errorAlert",
            "showSpinner": "showSpinner",
            "hideSpinner": "hideSpinner",
            "firstinvalid form": "maskErrorMessages"
        },
        
        base: function () {
            this.events = $.extend(this.baseEvents, this.events);
            _.bindAll(this, "beautify", "beautifyBack", "successAlert", "errorAlert", "alertFadeOut", "showSpinner", "hideSpinner", "maskErrorMessages",
                "addStyleElements");

            $.webshims.polyfill("forms forms-ext");
            $.ajaxSetup({ cache: false }); // globally disable cache
            this.$(".phone-number").mask("(999) 999-9999");
        

            this.$('.tip').tooltip();
            this.$('[data-toggle="popover"]').popover({
                html: true
            });

            this.addStyleElements();
            this.$('#spinner > div').sprite({ fps: 19, no_of_frames: 19 });
           

            this.beautify();
        },

        addStyleElements: function () {
            if (this.$(".circle-container").length === 0) {
                this.$el.append('<div class="circle-container"><div class="circles"><div></div><div></div><div></div><div></div><div></div><div></div></div></div>');
            }


            if (this.$(".beauty").length === 0) {
                this.$el.append('<div class="beauty"><div class="background"></div></div>');
            }
        },
        maskErrorMessages: function (e) {
            e.preventDefault();
            return false;
        },
        showSpinner: function () {
            if (this.spinnerCount === 0) {
                $("#spinner").show();
            }
            this.spinnerCount += 1;
        },

        hideSpinner: function () {
            this.spinnerCount = (--this.spinnerCount <= 0) ? 0 : this.spinnerCount;
            if (this.spinnerCount === 0) {
                $("#spinner").hide();
            }
        },
        
        beautify: function () {
           // $('.beauty .background').animate({ 'background-position-x': '-10000px' }, 150000, 'linear', this.beautifyBack);
        },
        beautifyBack: function () {
            //$('.beauty .background').animate({ 'background-position-x': '0px' }, 150000, 'linear', this.beautify);
        },
        successAlert: function (event, message, delay) {
            this.$('.alerts').html('<div class="alert alert-success fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');
            this.alertFadeOut(delay || 10000);
            $('html, body').animate({ scrollTop: 0 }, 0);
        },

        errorAlert: function (event, message, delay) {
            this.$('.alerts').html('<div class="alert alert-danger fade in"><a class="close" data-dismiss="alert"><i class="icon-remove"></i></a><span>' + message + '</span></div>');
            this.alertFadeOut(delay || 15000);
            $('html, body').animate({ scrollTop: 0 }, 0);
        },

        alertFadeOut: function (delay) {
            window.setTimeout(function () {
                this.$(".alerts .alert").fadeOut("slow");
            }, delay);
        }
    });

    $(".alpha-validation").change(function(e)
    {
        validationErrorMessage(/[^a-zA-Z ]/g, $(e.currentTarget));     
    });
   // start DFCT0017114- ganesh
    $(".phone-validation").change(function(e)
    	    {
    		var selectedCountry=$('#vendorCountry').val();
    		if(selectedCountry=="US" || selectedCountry=="CA" )
    		{
    			validationErrorMessage(/[^0-9- ()]/g, $(e.currentTarget));     
    		}else
    		{
    			validationErrorMessage(/[^0-9 ]/g, $(e.currentTarget));     
        	}
    	    });
    // clearing phone number when business doing country change
    $("#vendorCountry").change(function(e)
    	    {
    		$(".phone-validation").val("");
    		var selectedCountry=$('#vendorCountry').val();
    		if(selectedCountry=="US" || selectedCountry=="CA" )
    		{
    		    $(".phone-validation").mask("(999) 999-9999");// DFCT0017114
    			$(".phone-validation").attr("pattern","\\(\\d{3}\\) \\d{3}-\\d{4}");	
    			$(".phone-validation").attr("type","tel");
    			
    		}
    		else{
    			$(".phone-validation").unmask();// DFCT0017114
    			$(".phone-validation").removeAttr("pattern","");
    			$(".phone-validation").removeAttr("type","");
    		}
    	    });
   // end DFCT0017114- ganesh


    $(".num-validation").change(function(e)
    {
        validationErrorMessage(/[^0-9 ]/g, $(e.currentTarget));     
    });
    
    $(".alpha-num-validation").change(function(e)
    {
        validationErrorMessage(/[^a-zA-Z0-9 ]/g, $(e.currentTarget));     
    });
  
    $(".special-char-validation").change(function(e)
    {
        validationErrorMessage(/[^\w&'/\- ]/g, $(e.currentTarget));     
    });
 
    //ganesh test decisionComments DFCT0017735
    $('.decisionComments').keyup(function()
    		{
    			var yourInput = $(this).val();
    			var re = /[‘'"“”’`~!@#$%^&?_|+\<>\{\}\[\]\\\/]/gi;
    			var isSplChar = re.test(yourInput);
    			if(isSplChar)
    			{
    			//	var no_spl_char = yourInput.replace(/[`~!@#$%^&*()_|+\-=?;:'",.<>\{\}\[\]\\\/]/gi, ' '); 

    				
    				var no_spl_char = yourInput.replace(/[‘'"“”’`~!@#$%^&?_|+\<>\{\}\[\]\\\/]/gi, ' ');
    				$(this).val(no_spl_char);
    			}
    		});
   $('.invitation-comment').keyup(function()
	{
		var yourInput = $(this).val();
		var re = /[‘'"“”’`~!@#$%^&?_|+\<>\{\}\[\]\\\/]/gi;
		var isSplChar = re.test(yourInput);
		if(isSplChar)
		{
			var no_spl_char = yourInput.replace(/[‘'"“”’`~!@#$%^&?_|+\<>\{\}\[\]\\\/]/gi, ' ');
			$(this).val(no_spl_char);
		}
	});
 
// end 
   
    $(".special-char-validation-exceptdash").change(function(e)
    {
        validationErrorMessage(/[^\w- ]/g, $(e.currentTarget));     
    });

    $(".special-char-validation-search").change(function(e)
    {
        validationErrorMessage(/[^\w&'\-\* ]/g, $(e.currentTarget));     
    });

    // Begin of Insert by Naga
    $(".num-hyphen-validation").change(function(e)
    {
    	validationErrorMessage(/[^0-9- ]/g, $(e.currentTarget));     
    });    
    // End of Insert by Naga
    
    // Begin of Insert by Naga
    $('.icon-reply').on('click', function (e) {
    	$('#itemViewModal').modal('hide');
    	window.parent.jQuery('#itemViewModal').modal('hide');
    });     
    // End of Insert by Naga
    
    function validationErrorMessage(regex,target)
    {
        if(!target.val().match(regex))
        { 
            target.next().slideUp(); 
            target.removeClass("user-error"); 
            if($(".user-error").length != 0)
               $(".disableSearchButton").css("display","block");   
            else
                $(".disableSearchButton").css("display","none");      
        } 
        else 
        { 
            target.next().slideDown(); 
            target.addClass("user-error"); 
            $(".disableSearchButton").css("display","block");       
        }
    }
    
    $(".selectIndexCountry").prop("selectedIndex", -1);
}(window));