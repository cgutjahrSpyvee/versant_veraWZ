$(document).ready(function() {
	function beauty() {



  /* ONE CODE FIX */

  $('#addCodes').click(function (e) {
    $('#codesEntered .pillbox ul').show();
    $('#contactInformation').show();
    var $pillListItem = $('<li></li>'),
        $pillLabel = $('<span class="badge badge-info"></span>'),
        $pillClose = $('<span class="pill-close">&times;</span>'),
        $hiddenInput = $('<div class="hidden-form-elements"><input type="text" /></div>'),
        pill = $pillListItem.append($pillLabel, $pillClose, $hiddenInput),
        labelContent = $pillLabel.append($('#enterCodes').val());
    $('#codesEntered .pillbox ul').append($pillListItem);
    $(".pill-close").click( function (e) {
        e.stopPropagation();
        $(this).parent().hide();
        $('#companyCodes').show();
        $('#codesEntered .pillbox ul').hide();
    });
    $('#companyCodes').hide();
  });

  $('#searchResults table a').click(function (e) {
    $('#enterCodes').val($(this).text())
  });

  /* ------------ */















		    $('.beauty .background').animate({ 'background-position-x': '-100000px'}, 150000, 'linear', beauty);
		}
		beauty();

  $('.tip').tooltip();
  $('[data-toggle="popover"]').popover({
    html: true
  });
  $(".save").click(function() {
     $(".save-alert").removeClass('hide');
  });
  $(".navbar .nav li .dropdown-toggle").click(function() {
     $('[data-toggle="popover"]').popover('hide');
  });
  $(".phone-number").mask("(999) 999-9999");

  $('#vendor-search-modal .btn-success').click(function() {
    $('#invitationSearch').hide();
    $('#invitationForm').show();
  });

  $('#vendor-search-modal .btn-success').click(function() {
    $('#invitationSearch').hide();
    $('#invitationForm').show();
    $('#vendorNameInput').val($('#vendorNameSearch').val());
  });

  $('#statusRange').daterangepicker(
  {
      ranges: {
          'Past 7 days': [moment().subtract('days', 6), new Date()],
          'Past 2 weeks': [moment().subtract('days', 13), new Date()],
          'Past 30 days': [moment().subtract('days', 29), new Date()],
          'Last 3 months': [moment().subtract('month', 1), new Date()],
          'Last 6 months': [moment().subtract('month', 6), new Date()],
          'Last 12 months': [moment().subtract('month', 12), new Date()]
      },
      opens: 'right',
      format: 'MM/DD/YYYY',
      separator: ' to ',
      startDate: moment().subtract('days', 29),
      endDate: new Date(),
      locale: {
          applyLabel: 'Submit',
          fromLabel: 'From',
          toLabel: 'To',
          customRangeLabel: 'Custom Range',
          daysOfWeek: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
          monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
          firstDay: 1
      },
      showWeekNumbers: true,
      buttonClasses: ['btn'],
      dateLimit: false
  },
  function (start, end) {
      if (!start || !end) {
          return; // todo: causing a null exception on the clear button; need to investigate
      }
      $('#statusRange span').html(end.format("MM/DD/YYYY") + ' - ' + start.format("MM/DD/YYYY"));
  });

  $('#statusRange span').html("Select Date Range");

  $('#updateRange').daterangepicker(
  {
      ranges: {
          'Past 7 days': [moment().subtract('days', 6), new Date()],
          'Past 2 weeks': [moment().subtract('days', 13), new Date()],
          'Past 30 days': [moment().subtract('days', 29), new Date()],
          'Last 3 months': [moment().subtract('month', 1), new Date()],
          'Last 6 months': [moment().subtract('month', 6), new Date()],
          'Last 12 months': [moment().subtract('month', 12), new Date()]
      },
      opens: 'right',
      format: 'MM/DD/YYYY',
      separator: ' to ',
      startDate: moment().subtract('days', 29),
      endDate: new Date(),
      locale: {
          applyLabel: 'Submit',
          fromLabel: 'From',
          toLabel: 'To',
          customRangeLabel: 'Custom Range',
          daysOfWeek: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
          monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
          firstDay: 1
      },
      showWeekNumbers: true,
      buttonClasses: ['btn'],
      dateLimit: false
  },
  function (start, end) {
      if (!start || !end) {
          return; // todo: causing a null exception on the clear button; need to investigate
      }
      $('#updateRange span').html(end.format("MM/DD/YYYY") + ' - ' + start.format("MM/DD/YYYY"));
  });

  $('#updateRange span').html("Select Date Range");

  /* Inviter */

  $('.ers .yes-answer').click(function (e) {
    $('.ers .input-append select.add-on').removeClass('show');
    $('.ers .input-append select.add-on.ers-yes').addClass('show');
  });

  $('.ers .no-answer').click(function (e) {
    $('.ers .input-append select.add-on').removeClass('show');
    $('.ers .input-append select.add-on.ers-no').addClass('show');
  });

  $('#enterCodes').keydown(function (e) {
       $('#searchCodes').addClass('in tip');
       $('.tip').tooltip();
  });

  $('#searchCodes').click(function (e) {
       $('#searchResults').modal('show');
  });

  $('#divison').change(function (e) {
    $('#codesEntered .pillbox ul').show();
    $('#contactInformation').show();
    $('#codesEntered .pillbox ul').append('<li><span class="badge badge-info">A550</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A556</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A560</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A565</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A570</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A575</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A580</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A590</span><span class="pill-close">&times;</span></li><li><span class="badge badge-info">A595</span><span class="pill-close">&times;</span></li>');
    $(".pill-close").click( function (e) {
        e.stopPropagation();
        $(this).parent().hide();
    });
  });

  $(".add-primary-address").click(function() {
	   $("#address1").addClass('active');
	   $(".add-primary-address").removeClass('shown');
	   $(".add-additional-address").addClass('shown');
	   $(".country").addClass('shown');
	   $('.primary-country span').html($('.add-address select').val());
	});
  
  //$('#terms').modal('show');
  $("#terms .btn-success").click(function() {
  	$('#terms').modal('hide');
  	$('#admin').modal('show');
  });
  $("#admin .btn-success").click(function() {
  	$('#admin').modal('hide');
  	$(".admin-alert").removeClass('hide');
  });
  $("#closeAdmin").click(function() {
  	$('#admin').modal('hide');
  });
  $(".add-additional-address").click(function() {
    $('.address-collapse').hide();
	  var addressHTML = $('#addressTemplate').html();
	  $('#addressContainer').append(addressHTML);
	  $(".remit-option .btn-group .btn").click(function (e) {
	  	$(this).parent().parent().find('.row-fluid').addClass('shown');
	  });
	  $('#addressContainer .address:last-child legend span').html($('.add-address select').val());
	  $('.tip').tooltip();
   });

  $("#addressContainer").on('click', '.address legend', function(e){
    toggleElement.call(e.target, ".address-collapse");
  });

  function toggleElement(selector){
    if($(this).siblings(selector).css("display") === "none"){
      $(selector).hide();
      $(this).siblings(selector).show();
    }
  }

  $(".account-container").on('click', 'legend', function(e){
    toggleElement.call(e.target, ".account-form");
  });


  $(".contact-container").on('click', 'legend', function(e){
    toggleElement.call(e.target, ".contact-form");
  });


  $("#addAdmin").click(function() {
	  var $pillListItem = $('<li></li>'),
	  	  $pillLabel = $('<span class="badge badge-info"></span>'),
	  	  $pillClose = $('<span class="pill-close">&times;</span>'),
	  	  pill = $pillListItem.append($pillLabel, $pillClose),
	  	  labelContent = $pillLabel.append($('#adminInput').val());

	  $('.pillbox ul').append($pillListItem);
	  $(".pill-close").click( function (e) {
		    e.stopPropagation();
		    $(this).parent().hide();
		});  	
   });

  // Tab Progression

  $("#tab1 .form-actions .btn-success").click(function() {
  	$('#wizard li:first-child').addClass('complete');
  	$('#wizard li:first-child').removeClass('active');
  	$('#wizard li:first-child + li').addClass('active');
  	$('#wizard li:first-child + li').removeClass('disabled');
  	$('#wizard li:first-child a, #wizard li:first-child + li a').attr("data-toggle", "tab");
  	$('#wizard li:first-child a').attr("href", "#tab1");
  	$('#wizard li:first-child + li a').attr("href", "#tab2");
    });

  $("#tab2 .form-actions .btn-success").click(function() {
  	$('#wizard li:first-child + li').addClass('complete');
  	$('#wizard li:first-child + li').removeClass('active');
  	$('#wizard li:first-child + li + li').addClass('active');
  	$('#wizard li:first-child + li + li').removeClass('disabled');
  	$('#wizard li:first-child + li a, #wizard li:first-child + li + li a').attr("data-toggle", "tab");
  	$('#wizard li:first-child + li a').attr("href", "#tab2");
  	$('#wizard li:first-child + li + li a').attr("href", "#tab3");
    });

  $("#tab3 .form-actions .btn-success").click(function() {
  	$('#wizard li:first-child + li + li').addClass('complete');
  	$('#wizard li:first-child + li + li').removeClass('active');
  	$('#wizard li:first-child + li + li + li').addClass('active');
  	$('#wizard li:first-child + li + li + li').removeClass('disabled');
  	$('#wizard li:first-child + li + li a, #wizard li:first-child + li + li + li a').attr("data-toggle", "tab");
  	$('#wizard li:first-child + li + li a').attr("href", "#tab3");
  	$('#wizard li:first-child + li + li + li a').attr("href", "#tab4");
    });

  $("#tab4 .form-actions .btn-success").click(function() {
  	$('#wizard li:first-child + li + li + li').addClass('complete');
  	$('#wizard li:first-child + li + li + li').removeClass('active');
  	$('#wizard li:first-child + li + li + li + li').addClass('active');
  	$('#wizard li:first-child + li + li + li + li').removeClass('disabled');
  	$('#wizard li:first-child + li + li + li a, #wizard li:first-child + li + li + li + li a').attr("data-toggle", "tab");
  	$('#wizard li:first-child + li + li + li a').attr("href", "#tab4");
  	$('#wizard li:first-child + li + li + li + li a').attr("href", "#tab5");
    });

  // Tax Information

  $(".tax-id .btn-group .btn:first-child").click(function() {
  	$('.tax-legend').hide();
  	$('.tax-upload').addClass('in');
  	$('.social-input').removeClass('in');
  	$('.tax-input').addClass('in');
  });

  $(".tax-id .btn-group .btn:first-child + .btn").click(function() {
  	$('.tax-legend').hide();
  	$('.tax-upload').addClass('in');
  	$('.social-input').addClass('in');
  	$('.tax-input').removeClass('in');
  });

  // Payment

  $(".primary-account .add-account-button").click(function() {
     $(".primary-account .account-form").show();
     $(".primary-account .method").show();
     $(".primary-account .add-account-button").removeClass('shown');
     $(".secondary-account-button").addClass('shown');
     $('.primary-account legend .country').html($('.add-account-button select').val());
  });

  $(".secondary-account-button a").click(function() {
    $(".account-form").hide();
    var accountHTML = $('#accountTemplate').html();
    $('.account-container').append(accountHTML);
    $(".secondary-account legend .country").last().html($('.secondary-account-button select').val());
    $('.tip').tooltip();
    $(".dropdown-menu li a").click(function (e) {
      $(this).parent().parent().parent().find($(".dropdown-toggle .method-text")).text($(this).text());
    });
   });


  // Contact

  $(".add-contact").click(function() {
    $(".contact-form").hide();
    var contactHTML = $('#contactTemplate').html();
    $('.contact-container').append(contactHTML);
    $('.tip').tooltip();
   });

  // Accordion

  $(".address .btn-mini").click(function (e) {
    $('.address .accordion-heading').removeClass('active');
    $(this).parent().addClass('active');
  });

  // Addresses

  $(".remit-option .btn-group .btn").click(function (e) {
      $(this).parent().parent().find('.row-fluid').addClass('shown');
    });

});