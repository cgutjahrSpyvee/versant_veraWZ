package com.nbcu.vra;
 
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.vra.tools.help;
import com.nbcu.vra.tools.tools;
import com.nbcu.vra.ui.components;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IUser;
import com.sapportals.portal.prt.component.*;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class maintain extends AbstractPortalComponent
{
	String ServerString = "com.nbcu.html5_vra";

	public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		String userName = "";
		
		 // Added Pranesh(04/19/2016) - ENHC0019059
			IUser userObject = request.getUser();
			String userId = userObject.getUniqueName();	
			boolean accessAllowed=false; // ganesh DFCT0017729

		 // Added Pranesh(04/19/2016) - ENHC0019059
			
    	try {
			String requestID = request.getParameter("id");
			 // Added Pranesh(04/19/2016) - ENHC0019059
				String userType = "";
				String paymentTermUserType = "";
				boolean userIsJointVenture = false;
				boolean userIsExternalVendor = false;
				boolean userIsInternalEmployeeBuyer = false;
				boolean userIsInternalEmployeeInviter =  false;
			 // Added Pranesh(04/19/2016) - ENHC0019059
			
			// Drop Down Arrays
    		String[][] arrayCountryCode = tools.setupCountryCodeArray();   
    		
			
			// Request Type - invite | vendor
			String requestType = request.getParameter("type");
			
	    	HttpServletResponse resp = request.getServletResponse(true);
			PrintWriter response = resp.getWriter();
			resp.setContentType("text/html;charset=utf-8");
			
			try {
			
				
				// Begin Insert Pranesh(04/19/2016) - ENHC0019059
				 // JCO
				IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
				JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
				client.connect();
	
				IRepository m_Repository = null;
				IFunctionTemplate SAP_FUNCTION = null;
	
				m_Repository = JCO.createRepository("repository", client);

				//Get User Roles
				IRepository m_RepositoryRoles = JCO.createRepository("repository", client);
				IFunctionTemplate Z_SF_I477_GET_USER_ROLES = m_RepositoryRoles.getFunctionTemplate("Z_SF_I477_GET_USER_ROLES");

				JCO.Function functionRoles = new JCO.Function(Z_SF_I477_GET_USER_ROLES);
				JCO.ParameterList importListRoles = functionRoles.getImportParameterList();
				importListRoles.setValue(userId, "I_SSO_ID");
 
				client.execute(functionRoles);

				JCO.Table retUserRoles =  functionRoles.getTableParameterList().getTable("T_ROLES");	
				
				for(int i = 0; i < retUserRoles.getNumRows(); i++) {
					if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("DECC:FI_AP_AUTO_VND_REGISTER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))) {
						paymentTermUserType = "1";
						userIsExternalVendor = true;
						userType = "3";
						accessAllowed=false; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						paymentTermUserType = "2";
						userIsInternalEmployeeBuyer = true;
						userType = "2";
						accessAllowed=true;
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";	
						accessAllowed=true;
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeInviter = true;
						userType = "1";		
						accessAllowed=true;
					}
					// Begin of Insert by Naga ENHC0016164
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";	
						accessAllowed=true;
					}					
					// Handle joint venture users
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsJointVenture = true;
						paymentTermUserType = "4";
						userType = "4";	
						accessAllowed=true;
					}					
					// End of Insert by Naga
					retUserRoles.nextRow();
				}
			    // End Pranesh(04/19/2016) - ENHC0019059
				
					client.disconnect();
					
					// ganesh DFCT0017729
					if(!accessAllowed)	{
						response.write(components.displayErrorContainer(request,"inviter","The VeRA inviter role is not assigned to your SSO.  You can request access through a CAM access request in Secure Pro. Complete instructions for Requesting the VeRA Inviter Role can be found in the VeRA End User guide posted on the SNAP home page at <a href='http://snap.inbcu.com/documents/vera/'>http://snap.inbcu.com/documents/vera/</a>."));
					} 
					// end code
					else
					{	
					
				
				// Begin Pranesh(04/19/2016) - ENHC0019059
					int tempVal=0;                                           // Pranesh(04/19/2016)- ENHC0019059
					String[] helps=help.helpContent(request,"00011");        // Pranesh(04/19/2016)- ENHC0019059
			    
					String[] documentation=help.helpContent(request,"00013");// Pranesh(04/19/2016)- ENHC0019059
					int documentLength=0;                                    // Pranesh(04/19/2016)- ENHC0019059
				
			    // End Pranesh(04/19/2016) - ENHC0019059
				
			response.write(components.displayHeader(request,"inviter") +
				    "<div class=\"content\">\n"+
				
				        "<div class=\"container alerts\">\n"+
				            "<div class=\"alert alert-success hide fade in save-alert\">\n"+
				                "<button type=\"button\" class=\"close\" data-dismiss=\"alert\"><i class=\"icon-remove\"></i></button>\n"+
				                "<strong>Invitation Sent!</strong>\n"+
				            "</div>\n"+
				            "<div class=\"alert alert-success hide fade in admin-alert\">\n"+
				                "<button type=\"button\" class=\"close\" data-dismiss=\"alert\"><i class=\"icon-remove\"></i></button>\n"+
				                "<strong>Invitations Sent!</strong> You can still fill out the regstration form yourself. Or, you can get on with your day and let someone else do it. <i class=\"icon-smile\"></i>\n"+
				            "</div>\n"+
				        "</div>\n"+
				
				        "<div class=\"tab-content\">\n"+
				            "<div class=\"tab-pane fade in active\" id=\"invite\">\n"+
				                "<div class=\"container\">\n"+
				                    "<div class=\"row\">\n"+
				                        "<div class=\"span8\">\n"+
				                            "<h3>View & Update an Existing Vendor</h3>\n"+
				                            "<form id=\"invitationSearch\">\n"+
				                                "<div class=\"container-fluid\">\n"+
				                                    "<fieldset>\n"+
				                                        "<legend>Vendor Search</legend>\n"+
				                                    "</fieldset>\n"+
				                                    "<div class=\"row-fluid\">\n"+
				                                        "<div class=\"control-group span4\">\n"+
				                                            "<label class=\"control-label\">\n"+
				                                                "Vendor Name\n"+
				                                            "</label>\n"+
				                                            "<div class=\"controls\">\n"+
				                                                "<input type=\"text\" name=\"name\" class=\"input-block-level special-char-validation-search\" id=\"vendorNameSearch\" pattern=\"[a-zA-Z0-9&'* ]+\" />\n"+
				                                                	"<div class=\"alert alert-danger\" style=\"width: 78%; font-size: 9.5pt;display: none;\">No special characters allowed only &, * and '.</div>\n"+			                                                
				                                            
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"control-group span4\">\n"+
				                                            "<label class=\"control-label\">\n"+
				                                                "Vendor Number\n"+
				                                            "</label>\n"+
				                                            "<div class=\"controls\">\n"+
				                                                "<input type=\"text\" name=\"vendor-number\" class=\"input-block-level\" id=\"vendorNameSearch\" />\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"control-group span4\">\n"+
				                                            "<label class=\"control-label\">\n"+
				                                                "TIN/EIN/SSN\n"+
				                                            "</label>\n"+
				                                            "<div class=\"controls\">\n"+
				                                                "<input type=\"text\" name=\"tin\" class=\"input-block-level\" />\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Country</label>\n"+
				                                        "<select id=\"country-select\" class=\"country-select selectIndexCountry\">\n");
				                                            for (int x = 0; x < arrayCountryCode.length; x++) {
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
																}
				                                   			 	response.write("</select>\n"+
			                                            //"<option value=\"US\">United States</option>\n"+
			                                            //"<option value=\"CA\">Canada</option>\n"+
			                                            //"<option value=\"MX\">Mexico</option>\n"+
			                                            //"<option value=\"AU\">Australia</option>\n"+
			                                            //"<option value=\"NZ\">New Zealand</option>\n"+
			                                        //"</select>\n"+
				                                        "<!--<a class=\"btn btn-primary add-item\"><i class=\"icon-plus\"></i> Add Address</a>-->\n"+
				                                    "</div>\n"+
				                                    "<div class=\"search-address\">\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"span6\">\n"+
				                                              "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\" >Address 1</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                  "<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress1\" placeholder=\"Street Name\" pattern=\"[a-zA-Z0-9 ]+\">\n"+
			                                                   	  "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: none;\">Special character are not allowed.</div>"+				                                                
				                                                "</div>\n"+
				                                              "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"span3\">\n"+
				                                              "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label \" >Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                	"<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress2\" placeholder=\"Building or Unit Number\" pattern=\"[a-zA-Z0-9 ]+\">\n"+
			                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+				                                                
				                                                "</div>\n"+
				                                              "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"span3\">\n"+
				                                              "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\" >Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                  "<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress3\" placeholder=\"Suite or Room Number\" pattern=\"[a-zA-Z0-9 ]+\">\n"+
			                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+				                                                
				                                                "</div>\n"+
				                                              "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"row-fluid validate-group\">\n"+
				                                            "<div class=\"span6\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label\" >Country</label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input type=\"hidden\" name=\"primaryCountry\" />\n"+
				                                                        "<input class=\"input-block-level city special-char-validation-exceptdash\" type=\"text\" name=\"primaryAddressCity\"  pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"City\" >\n"+
			                                                			"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only - .</div>\n"+ 				                                                   
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"span3\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label\"><span class=\"state\">State</span><span class=\"prov hide\">/Province</span></label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<select class=\"input-block-level state\" name=\"primaryAddressState\">\n"+
				                                                            "<option>Select State</option>\n"+
				                                                            "<option>CA</option>\n"+
				                                                        "</select>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"span3\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label zip\"><span class=\"zip\">Zip</span><span class=\"postal hide\">Postal</span> Code</label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input class=\"input-block-level\" type=\"text\" name=\"primaryAddressZip\" placeholder=\"Zip-Code\">\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                    	"<div class=\"disableSearchButton\" ></div>\n"+
				                                        "<a id=\"vendorSearch\" class=\"btn btn-success pull-right\" href=\"#vendor-search-modal\">Search</a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</form>\n"+
				                            "<form id=\"invitationForm\">\n"+
				                            "<input type=\"hidden\" id=\"vendorId\" name=\"vendorId\" value=\"\"/>\n"+
				                                "<div class=\"container-fluid\">\n"+
				                                // Begin of Comment by Naga ENHC0013668
//				                                    "<fieldset>\n"+
//				                                        "<legend>Basic Information</legend>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span5\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Vendor Name\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<input type=\"text\" name=\"vendorName\" required=\"required\" class=\"input-block-level\" id=\"vendorNameInput\" pattern=\"[a-zA-Z0-9&' ]+\"/>\n"+
//				                                                	"<div class=\"alert alert-danger\" style=\"width: 78%; font-size: 9.5pt;display: none;\">No special characters allowed only & and '.</div>\n"+			                                                
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                            "<div class=\"control-group span3\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Language\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"vendorLanguage\" required=\"required\" class=\"input-block-level\">\n"+
//				                                                        "<option>English</option>\n"+
//				                                                        "<option>Spanish</option>\n"+
//				                                                        "<option>French</option>\n"+
//				                                                    "</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                            "<div class=\"control-group span4\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Country Doing Business In\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"vendorCountry\" required=\"required\" class=\"input-block-level\">\n"+
//				                                                        "<option value=\"\">Select Country</option>\n"+
//				                                                        "<option>United States</option>\n"+
//				                                                    "</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                    "</fieldset>\n"+
//				                                    "<fieldset>\n"+
//				                                        "<legend>Payment Terms</legend>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span12 ers\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "ERS?\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<div class=\"input-append\">\n"+
//				                                                        "<div id=\"ersGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
//				                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
//				                                                            "<select name=\"ersYesTerms\" class=\"add-on ers-yes\">\n"+
//				                                                                "<option>Immediate Pay</option>\n"+
//				                                                            "</select>\n"+
//				                                                            "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
//				                                                            "<select name=\"ersNoTerms\" class=\"add-on ers-no\">\n"+
//				                                                                "<option value=\"\">Select Terms</option>\n"+
//				                                                                "<option>2% 10</option>\n"+
//				                                                            "</select>\n"+
//				                                                            "<div class=\"hidden-form-elements\">\n"+
//				                                                                "<input required=\"required\" id=\"ersYes\" type=\"radio\" name=\"ers\" value=\"yes\" />\n"+
//				                                                                "<input required=\"required\" id=\"ersNo\" type=\"radio\" name=\"ers\" value=\"no\" />\n"+
//				                                                            "</div>\n"+
//				                                                        "</div>\n"+
//				                                                    "</div>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span6\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Annual Spend\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<input name=\"annualSpend\" required=\"required\" type=\"text\" class=\"input-block-level\" />\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span12\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Comments\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<textarea name=\"comments\" class=\"input-block-level invitation-comment\"></textarea>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                    "</fieldset>\n"+

//				                                    "<fieldset>\n"+
//				                                        "<legend>NBCU Integration</legend>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span6\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Vendor Type\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"vendorType\" required=\"required\" class=\"input-block-level\">\n"+
//				                                                        "<option value=\"\">Select Vendor Type</option>\n"+
//				                                                        "<option>US Trade Vendor</option>\n"+
//				                                                        "<option>Non-US Vendor</option>\n"+
//				                                                    "</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                            "<div class=\"control-group span6\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Sub-Sytem\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"subSystem\" required=\"required\" class=\"input-block-level\">\n"+
//				                                                        "<option value=\"\">Select Sub-System</option>\n"+
//				                                                        "<option>Sub-system A</option>\n"+
//				                                                    "</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                        "<div class=\"row-fluid\">\n"+
//				                                            "<div class=\"control-group span4\" id=\"companyCodes\">\n"+
//				                                                "<label class=\"control-label\">\n"+
//				                                                    "Company Codes\n"+
//				                                                "</label>\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<div class=\"input-append\">\n"+
//				                                                        "<input type=\"text\" id=\"enterCodes\" />\n"+
//				                                                        "<a class=\"btn fade\" id=\"searchCodes\" title=\"Search Company Codes\">\n"+
//				                                                            "<i class=\"icon-search\"></i>\n"+
//				                                                        "</a>\n"+
//				                                                        "<a class=\"btn btn-primary\" id=\"addCodes\">\n"+
//				                                                            "<i class=\"icon-plus\"></i>Add\n"+
//				                                                        "</a>\n"+
//				                                                    "</div>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                        "<div class=\"row-fluid\" id=\"codesEntered\">\n"+
//				                                            "<div class=\"pillbox span12\">\n"+
//				                                                "<ul class=\"unstyled\">\n"+
//				                                                "</ul>\n"+
//				                                            "</div>\n"+
//				                                        "</div>\n"+
//				                                    "</fieldset>\n"+
					                                // End of Comment by Naga
				                                	// Begin of Insert by Naga ENHC0013668
			                                    "<fieldset>\n"+
		                                        "<legend>NBCU Integration</legend>\n"+
		                                        "<div class=\"row-fluid\">\n"+
		                                            "<div class=\"control-group span6\">\n"+
		                                                "<label class=\"control-label\">\n"+
		                                                    "Vendor Type\n"+
		                                                "</label>\n"+
		                                                "<div class=\"controls\">\n"+
		                                                    "<select id=\"vendorType\" name=\"vendorType\" required=\"required\" class=\"input-block-level\">\n"+
		                                                        "<option value=\"\">Select Vendor Type</option>\n"+
		                                                        "<option value=\"010\">Trade Vendor</option>\n"+
																"<option value=\"020\">Freelancer/Talent/Statistician</option>\n"+
																"<option value=\"030\">Charitable</option>\n"+
																"<option value=\"040\">Political Contribution</option>\n"+
																"<option value=\"050\">Government</option>\n"+
																"<option value=\"999\">One Time</option>\n"+ 
																"<option value=\"018\">Production/Agreement</option>\n"+ // Added "Production/Agreement" - Pranesh (05/11/2016)-(Defect ID : 15084)
																//"<option value=\"070\">Petty Cash</option>\n"+ Pranesh(04/16/2016)
																"<option value=\"080\">Utility</option>\n"+
																"<option value=\"090\">Comcast</option>\n"+
																"<option value=\"091\">Revenue Share</option>\n"+
																"<option value=\"092\">Garnishment</option>\n"+
		                                                    "</select>\n"+
		                                                "</div>\n"+
		                                            "</div>\n"+
		                                            "<div class=\"control-group span6 subVendorType\" style=\"display: none;\">\n"+
	                                                "<label class=\"control-label required-red\">\n"+
	                                                    " One Time Vendor\n"+
	                                                "</label>\n"+
	                                                "<div class=\"controls\">\n"+
	                                                    "<select id=\"subVendorType\" name=\"subVendorType\" class=\"input-block-level\">\n"+
	                                                        "<option value=\"\">Select One Time Vendor</option>\n"+
															"<option value=\"060\">Refund/Reimbursement</option>\n"+	
															"<option value=\"093\">Legal Settlement</option>\n"+	
															"<option value=\"094\">Contest Winner</option>\n"+		
															"<option value=\"095\">Posthumous Payments</option>\n"+  // Added "Production/Agreement" - Pranesh (05/11/2016)-(Defect ID : 15084)

	                                                    "</select>\n"+
	                                                "</div>\n"+
	                                                "</div>\n"+			                                            
		                                        "</div>\n"+
		                                        "<div class=\"row-fluid\">\n"+
	                                            "<div class=\"control-group span4\" id=\"companyCodes\">\n"+
	                                                "<label class=\"control-label required-red\">\n"+
	                                                    " Company Codes\n"+
	                                                "</label>\n"+
	                                                "<div class=\"controls\">\n"+
	                                                    "<div class=\"input-append\" id=\"addCompanyCode\">\n"+
	                                                    	//maxlength="4" in id="enterCodes" added by Kermel Ruperto 7/24/2014 SSO: 206441846 Ticket: 4892870	
                                                        	"<input type=\"text\" id=\"enterCodes\" maxlength=\"4\" required=\"required\"/>\n"+
	                                                        "<a class=\"btn fade\" id=\"searchCodes\" title=\"Search Company Codes\">\n"+
	                                                            "<i class=\"icon-search\"></i>\n"+
	                                                        "</a>\n"+
	                                                        "<a class=\"btn btn-primary\" id=\"addCodes\">\n"+
	                                                            "<i class=\"icon-plus\"></i>Add\n"+
	                                                        "</a>\n"+
	                                                    "</div>\n"+
	                                                "</div>\n"+
	                                            "</div>\n"+
	                                        "</div>\n"+
	                                        "<div class=\"row-fluid\" id=\"codesEntered\">\n"+
	                                            "<div class=\"pillbox span12\">\n"+
	                                                "<ul class=\"unstyled\">\n"+
	                                                "</ul>\n"+
	                                            "</div>\n"+
	                                        "</div>\n"+
		                                    "</fieldset>\n"+				                                
				                                    // End of Insert by Naga
				                                    "<div class=\"form-actions\">\n"+
//				                                        "<a class=\"btn btn-large btn-success pull-left\" href=\"#create\" data-toggle=\"tab\">Proceed to maintain<i class=\"icon-angle-right\"></i></a>\n"+ // ENHC0013668
				                                    	"<a class=\"btn btn-large btn-success pull-left\" id=\"proceed-maintain\"><i class=\"icon-envelope\"></i> Proceed to maintain</a>\n"+	// ENHC0013668
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</form>\n"+
				                        "</div>\n"+
				                        "<div class=\"span4 sidebar\">\n"+
				                        	
				                        // Blocked Pranesh(04/19/2016) - ENHC0019059
				                        
				                            /*"<h3><i class=\"icon-plus-sign-alt\"></i>Help</h3>\n"+
				                            
				                            "<div class=\"accordion\" id=\"accordion2\">\n"+
				                                
				                            	"<div class=\"accordion-group\">\n"+
				                                    "<div class=\"accordion-heading\">\n"+
				                                        "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseOne\">How do I find company codes?\n"+
				                                        "</a>\n"+
				                                    "</div>\n"+
				                                    "<div id=\"collapseOne\" class=\"accordion-body collapse in\">\n"+
				                                        "<div class=\"accordion-inner\">\n"+
				                                            "Type a term and click search to look up codes\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                                
				                                "<div class=\"accordion-group\">\n"+
				                                    "<div class=\"accordion-heading\">\n"+
				                                        "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseTwo\">Who will receive the invitation?\n"+
				                                        "</a>\n"+
				                                    "</div>\n"+
				                                    "<div id=\"collapseTwo\" class=\"accordion-body collapse\">\n"+
				                                        "<div class=\"accordion-inner\">\n"+
				                                            "Please call your NBCUniversal contact to address this issue.\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                                
				                            "</div>\n"+*/
				                        	// Blocked Pranesh(04/19/2016) - ENHC0019059
				                        
				                     // Begin Insert Pranesh(04/19/2016) - ENHC0019059
		                        		"<h3><i class=\"icon-plus-sign-alt\"></i>Documentation</h3>\n"+
			                                 "<div class=\"accordion\" id=\"accordion2\">\n");
			                                    //response.write("-> :"+userIsInternalEmployeeInviter+" - "+documentation[0]);
			                                    if(userIsInternalEmployeeInviter  || userIsInternalEmployeeBuyer || userIsJointVenture){
			                                       response.write( 
			                                         "<div class=\"accordion-group\">\n"+
			                                            "<div class=\"accordion-heading\">\n"+
		                                                   "<a class=\"accordion-toggle\" href=\""+documentation[0]+"\" target=\"_new\">VeRA Training Materials\n"+
		                                                   "</a>\n"+
		                                                "</div>\n"+
		                                            "</div>\n");
			                                    }else if(userIsExternalVendor){
			                                      response.write(
			                                        "<div class=\"accordion-group\">\n"+
			                                           "<div class=\"accordion-heading\">\n"+
		                                                   "<a class=\"accordion-toggle\" href=\""+documentation[0]+"\" target=\"_new\">VeRA Vendor's User Guide\n"+
		                                                   "</a>\n"+
		                                               "</div>\n"+
		                                           "</div>\n");
			                                    }
			                              response.write("</div>\n"+
			                           "</div>\n"+
			                                   
			 					      "<div class=\"span4 sidebar\">\n"+
					                      "<h3><i class=\"icon-plus-sign-alt\"></i>FAQ's</h3>\n"+
					                  "<div class=\"accordion\" id=\"accordion2\">\n");
					                   while(tempVal<helps.length-1)  
					                   { 
					                	   int tempClassId=tempVal;
					                       response.write( "<div class=\"accordion-group\">\n"+
					                       "<div class=\"accordion-heading\">\n"+
					                       		"<a class=\"accordion-toggle collapsed\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseOne"+tempClassId+"\">"+helps[tempVal]+"\n"+
					                       		"</a>\n"+
					                       "</div>\n");
					                       tempVal++;
					                       response.write("<div id=\"collapseOne"+tempClassId+"\" class=\"accordion-body collapse\">\n"+
					                    		   "<div class=\"accordion-inner\">\n"+helps[tempVal]+"</div>\n"+
					                       "</div>\n"+
					                       "</div>\n");     
					                       tempVal++;
					                    }     
						         response.write("<p style=\"color: red;\">" +helps[tempVal]+"</p>\n"+ // altered by ganesh
						        		 "</div>\n"+
						        "</div>\n"+
						         //End Insert Pranesh (04/19/2016) - ENHC0019059 		  
				                       
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</div>\n"+
				
				    "<div class=\"footer\">\n"+
				        "<div class=\"container\">\n"+
				            "(c) NBCUniversal\n"+
				        "</div>\n"+
				    "</div>\n"+
				
				    "<div class=\"modal hide fade\" id=\"searchResults\">\n"+
				        "<div class=\"modal-header\">\n"+
				            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
				            "<h3>Find Company Codes</h3>\n"+
				        "</div>\n"+
				        "<form>\n"+
				            "<div class=\"modal-body\">\n"+
				                "<div>\n"+
				                    "<div class=\"input-append\">\n"+
				                        "<input id=\"codes\" type=\"text\" />\n"+
				                        "<a id=\"search-codes\" class=\"btn btn-primary\">Search Codes</a>\n"+
				                    "</div>\n"+
				                    "<div class=\"clearfix\"></div>\n"+
				                "</div>\n"+
				                "<div class=\"code-table\">\n"+
				                    "<div class=\"alert alert-info\">No Results</div>\n"+
				                    "<table class=\"table table-condensed table-striped\">\n"+
				                        "<thead>\n"+
				                            "<tr>\n"+
				                                "<th id=\"actions\"></th>\n"+
				                                "<th>Code\n"+
				                                "</th>\n"+
				                                "<th>Company\n"+
				                                "</th>\n"+
				                            "</tr>\n"+
				                        "</thead>\n"+
				                        "<tbody>\n"+
				
				                        "</tbody>\n"+
				                    "</table>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            // Commented the modal footer ENHC0013668
//				            "<div class=\"modal-footer\">\n"+										
//				                "<a href=\"#\" class=\"btn\" data-dismiss=\"modal\">Cancel</a>\n"+
//				                "<a href=\"#\" class=\"btn btn-primary\" id=\"add-codes\">Add Codes</a>\n"+
//				            "</div>\n"+
				        "</form>\n"+
				    "</div>\n"+
				
				    "<div class=\"modal fade hide\" id=\"vendor-search-modal\">\n"+
				        "<div class=\"modal-header\">\n"+
				            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
				            "<h3>4 vendors found - </h3>\n"+
				            "<p>Vendors who self-registered or are pending approval cannot be edited, but is available for viewing.</p>\n"+
				        "</div>\n"+
				        "<div class=\"modal-body\">\n"+
				
				            "<div class=\"alert alert-info\">No Results</div>\n"+
				            "<div class=\"paginator\">\n"+
				                "<input type=\"hidden\" class=\"pageSize\" value=\"7\"/>\n"+
				                "<div class=\"pagination\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"wrapper\">\n"+
				                "<table class=\"table table-condensed table-striped\">\n"+
				                    "<thead>\n"+
				                        "<tr>\n"+
				                            "<th>Vendor #</th>\n"+
				                            "<th>Remit #</th>\n"+
				                            "<th>Vendor Name</th>\n"+
				                            "<th>TIN/EIN/SSN</th>\n"+
				                            "<th>Address</th>\n"+
				                            "<th>Zip/Postal</th>\n"+
				                            "<th>Country</th>\n"+				// ENHC0013668
//				                            "<th>Status</th>\n"+				// ENHC0013668
				                            "<th></th>\n"+
				                        "</tr>\n"+
				                    "</thead>\n"+
				                    "<tbody>\n"+
				                        "<tr>\n"+
				                            "<td>1000111</td>\n"+
				                            "<td>1000111</td>\n"+
				                            "<td>Vendor 1</td>\n"+
				                            "<td>1001101110</td>\n"+
				                            "<td><span>2001 Olympic Blvd.</span><strong>Atlanta, GA</strong></td>\n"+
				                            "<td>90000</td>\n"+
				                            "<td>US</td>\n"+
//				                            "<td><span class=\"badge badge-success\">Approved</span></td>\n"+	// 998
				                            "<td><a class=\"btn btn-inverse\"><i class=\"icon-eye-open\"></i></a>\n"+
				                        "</tr>\n"+
				                    "</tbody>\n"+
				                "</table>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</div>\n"+
				
				    "<script id=\"vendorSearchTemplate\" type=\"text/template\">\n"+
				        "<tr class=\"hide\">\n"+
				
				            "<td><%- vendorNum %></td>\n"+
				            "<td><%- remitNum %></td>\n"+
				            "<td><%- name %> <% if(reqType == \"1\"){ %>\n <strong>(MAINTAINED BY VENDOR)</strong><% } %>\n</td>\n"+
				           
				            // Begin of Insert - Pranesh(04/21/2016) - ENHC0018725
				            // changed Y to SOURCING (04/29/2016) 
				            	//"<td><%- name %> <% if(reqType == \"1\"){ %>\n <strong>(MAINTAINED BY VENDOR)</strong><% } else if(isSourcingRelevant==\"SOURCING\") { %>\n<strong>(Contact sourcing for updates at contactsourcing@nbcuni.com)</strong> <%}%>\n</td>\n"+
				            	// Blocked - Pranesh - (04/29/2016)- Defect ID : 15051
				            // End of Insert   - Pranesh(04/21/2016) - ENHC0018725
				            
				            
				            "<td><%- tin %></td>\n"+
				            "<td><%- address %> <strong><%- city %>, <%- state %></strong></td>\n"+
				            "<td><%- postalCode %></td>\n"+
				            "<td><%- country %></td>\n"+
				            // Begin of comment by Naga ENHC0013668
//				            "<td>\n"+
//				                "<%\n"+
//				                "var cls = \"\"; \n"+
//				                "if (status.text == \"Pending Approval\") {\n"+
//				                    "cls = \"badge-primary\";\n"+
//				                "} else if (status.text == \"Draft\") {\n"+
//				                    "cls = \"badge-warning\";\n"+
//				                  //status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
//				                "} else if (status.text == \"In Review\") {\n"+
//				                    "cls = \"badge-success\";\n"+
//				                "} else if (status.text == \"Rejected\") {\n"+
//				                    "cls = \"badge-important\";\n"+
//				                "} \n"+
//				                "%>\n"+
//				                "<span class=\"badge <%- cls %>\"><%- status.text %></span>\n"+
//				            "</td>\n"+
				            // End of comment by Naga 
				            "<td>\n"+
							/*
				              Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
				             
				            //status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
			                "<% if (status.text == \"In Review\" || status.text == \"Pending Approval\" ) { %>\n"+
			                	"<a class=\"view-vendor btn btn-inverse btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-eye-open\"></i></a>\n"+ // ENHC0013668
//			                
			                /* Begin Insert - Pranesh(04/25/2016) - ENHC0018725
			                "<% } else if(isSourcing===\"false\"){ %>\n"+
			                	//Changed Y to SOURCING(04/29/2016)
		                		"<%if(reqType == \"1\" || isSourcingRelevant==\"SOURCING\"){ %>\n"+
		                			"<i class=\"icon-ban-circle tip\" title=\"\" data-index=\"\" data-original-title=\"This is maintained by Vendor\"></i>\n"+ // ENHC0013668
		                		"<% } else { %>\n"+
		                			"<a id=<%- vendorNum %> class=\"view-vendor btn btn-info btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
		                		"<% } %>\n"+
		                	"<% } else if(isSourcing===\"true\") { %>\n"+
		                		//"<%if(isSourcingRelevant==\"Y\"){ %>\n"+
		                			"<a id=<%- vendorNum %> class=\"view-vendor btn btn-info btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
		                		//"<% } %>\n"+
//								Begin of Insert and Comment by Naga ENHC0013668
			               
		                   /*End of Insert - Pranesh(04/25/2016) - ENHC0018725
			                
			                "<% } else { %>\n"+
			                //"<a id=<%- vendorNum %> class=\"view-vendor btn btn-inverse btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			               
			                // Blocked Pranesh(04/25/2016)
			                	//"<a id=<%- vendorNum %> class=\"view-vendor btn btn-info btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			                // Blocked Pranesh(04/25/2016)
			                
//			                "<% } else { %>\n"+
//			                "<a class=\"view-vendor btn btn-info btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			                "<% } %>\n"+				                
//			                End of Insert and Comment by Naga
			                */
			                
			                
				          //status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
			                "<% if (status.text == \"In Review\" || status.text == \"Pending Approval\" ) { %>\n"+
			                "<a class=\"view-vendor btn btn-inverse btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-eye-open\"></i></a>\n"+ // ENHC0013668
			                //Begin of Insert and Comment by Naga ENHC0013668
			                "<% } else if(reqType == \"1\"){ %>\n"+
			                "<i class=\"icon-ban-circle tip\" title=\"\" data-index=\"\" data-original-title=\"This is maintained by Vendor\"></i>\n"+ // ENHC0013668
			                "<% } else { %>\n"+
			                //"<a id=<%- vendorNum %> class=\"view-vendor btn btn-inverse btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			                "<a id=<%- vendorNum %> class=\"view-vendor btn btn-info btn-mini btn-success-maintain\" data-dismiss=\"modal\" href=\"#\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			                //"<% } else { %>\n"+
			                //"<a class=\"view-vendor btn btn-info btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-pencil\"></i></a>\n"+ // ENHC0013668
			                "<% } %>\n"+				                
			                //End of Insert and Comment by Naga





				            "</td>\n"+
				        "</tr>\n"+
				    "</script>\n"+
				
				    "<script id=\"codeSearchTemplateSingle\" type=\"text/template\">\n"+
				        "<tr>\n"+
				            "<td>\n"+
				                "<a data-code=\"<%- Code %>\"><%- Code %></a>\n"+
				            "</td>\n"+
				            "<td>\n"+
				                "<span><%- Description %></span>\n"+
				            "</td>\n"+
				        "</tr>\n"+
				    "</script>\n"+
				
				    "<script id=\"codeSearchTemplateMultiple\" type=\"text/template\">\n"+
				        "<tr>\n"+
				            "<td>\n"+
				                "<input type=\"checkbox\" value=\"<%- Code %>\" />\n"+
				            "</td>\n"+
				            "<td>\n"+
				            "<span><%- Code %></span>\n"+
				            "</td>\n"+
				            "<td>\n"+
				                "<span><%- Description %></span>\n"+
				            "</td>\n"+
				        "</tr>\n"+
				    "</script>\n"+
			        "<script src=\"/"+ServerString+"/js/jquery.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/extras/modernizr-custom.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/extras/mousepress.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/polyfiller.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/bootstrap.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/underscore-min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/backbone-min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.maskedinput.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/moment.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/daterangepicker.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/spritely.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/bootstrap-paginator.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/common.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/inviter.js\"></script>\n"+			        
				    "<script>\n"+			
				       "app.page.inviterView = new app.views.InviterView({ restrictCompanyCode: true });\n"+
				    "</script>\n"+
			     "</body>\n"+
	     		    "</html>");

				}
				
			} catch (Exception e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
				response.write("Error : "+e1.getLocalizedMessage());				
			}

		} catch (Exception e) {
			e.printStackTrace();

		} 
    }
}