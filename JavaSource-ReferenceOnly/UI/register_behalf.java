package com.nbcu.vra;
 
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.vra.tools.help;
import com.nbcu.vra.tools.tools;
import com.nbcu.vra.ui.components;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IRole;
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sapportals.portal.prt.component.*;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class register_behalf extends AbstractPortalComponent
{
	String ServerString = "com.nbcu.html5_vra";
	
    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		IUser userObject = request.getUser();
		String userId = userObject.getUniqueName();	
	    
    	try {
    		String userType = "";
			String paymentTermUserType = "";
  			boolean userIsExternalVendor = false;
			boolean userIsInternalEmployeeBuyer = false;
			boolean userIsInternalEmployeeInviter =  false;
			boolean userIsJointVenture = false;			// ENHC0016164

			
			// Drop Down Arrays
			// Begin of comment by Naga ENHC0013660
			// Get the country code from backend
    		//String[][] arrayCountryCode = tools.setupCountryCodeArray();   
    		// End of comment by Naga ENHC0013660
    		// JCO Vars
			JCO.Table retPaymentTerms = null;
			
			// UI
	    	HttpServletResponse resp = request.getServletResponse(true);
			PrintWriter response = resp.getWriter();
			boolean accessAllowed=false; // ganesh DFCT0017729

			resp.setContentType("text/html;charset=utf-8");

			try {

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
						accessAllowed=true; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";		
						accessAllowed=true; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeInviter = true;
						userType = "1";		
						accessAllowed=true; // ganesh DFCT0017729

					}
					
					// Begin of Insert by Naga ENHC0016164
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";	
						accessAllowed=true; // ganesh DFCT0017729

					}					
					// Handle joint venture users
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsJointVenture = true;
						paymentTermUserType = "4";
						userType = "4";	
						accessAllowed=true; // ganesh DFCT0017729

					}					
					// End of Insert by Naga
					retUserRoles.nextRow();
				}	
				// ganesh DFCT0017729
				if(!accessAllowed)	{
					response.write(components.displayErrorContainer(request,"inviter","The VeRA inviter role is not assigned to your SSO.  You can request access through a CAM access request in Secure Pro. Complete instructions for Requesting the VeRA Inviter Role can be found in the VeRA End User guide posted on the SNAP home page at <a href='http://snap.inbcu.com/documents/vera/'>http://snap.inbcu.com/documents/vera/</a>."));
				} 
				// end code
				else
				{
				
				SAP_FUNCTION = m_Repository.getFunctionTemplate("Z_SF_I512_PAYMENTS_W2W");
	
				JCO.Function function = new JCO.Function(SAP_FUNCTION);
				JCO.ParameterList importList = function.getImportParameterList();
				importList.setValue("I", "I_SCREEN_FLAG");
				importList.setValue(paymentTermUserType, "I_VENDOR_CAT");
				//importList.setValue("010", "I_VENDOR_TYPE");				
				
				client.execute(function);	
				retPaymentTerms = function.getExportParameterList().getTable("IT_PAYMENT_TERMS");

				SAP_FUNCTION = m_Repository.getFunctionTemplate("Z_SFI_I486_APPVR_LIST");
	
				JCO.Function functionApprovers = new JCO.Function(SAP_FUNCTION);
				
				//Req#23 START - Code added by AGAMPA 21-Feb-2015
				JCO.ParameterList apListImportList = functionApprovers.getImportParameterList();
				apListImportList.setValue(userId, "I_REQUESTED_BY");	
				//Req#23 START - Code added by AGAMPA 21-Feb-2015
				
				client.execute(functionApprovers);	
				
				JCO.Table retApprovers = functionApprovers.getExportParameterList().getTable("IT_APPROVER_LIST");
				String I_REQUESTED_NAME = functionApprovers.getExportParameterList().getString("I_REQUESTED_NAME");
								
				
				client.disconnect();
				
				// Begin added Pranesh(04/19/2016) - ENHC0019059
									// Help Screen - 2 
					int tempVal=0;									 		 // Pranesh(04/19/2016)- ENHC0019059
					String[] helps=help.helpContent(request,"00011");		 // Pranesh(04/19/2016)- ENHC0019059
			    
					String[] documentation=help.helpContent(request,"00013");// Pranesh(04/19/2016)- ENHC0019059
					int documentLength=0;									 // Pranesh(04/19/2016)- ENHC0019059
				
			    // End added Pranesh(04/19/2016) - ENHC0019059
				
				response.write(components.displayHeader(request,"inviter") +
			    "<input type=\"hidden\" id=\"userType\" name=\"userType\" value=\""+userType+"\"/>\n"+					
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
			            "<div class=\"tab-pane active\" id=\"invite\">\n"+
			                "<div class=\"container\">\n"+
			                    "<div class=\"row\">\n"+
			                        "<div class=\"span8\">\n"+
			                            "<h3>Register on Behalf of a Vendor</h3>\n"+
			                            "<form id=\"invitationSearch\">\n"+
			                                "<div class=\"container-fluid\">\n"+
			                                    "<fieldset>\n"+
			                                        "<legend>Before you start</legend>\n"+
			                                    "</fieldset>\n"+
			                                    "<p class=\"reminder\" >Please ensure that vendor is not an existing vendor prior to request submission. Registering an existing vendor will result in a failed and cancelled registration.</p>\n"+
			                                    "<div class=\"row-fluid\">\n"+
			                                        "<div class=\"control-group span6\">\n"+
			                                            "<label class=\"control-label\">\n"+
			                                                "Vendor Name\n"+
			                                            "</label>\n"+
			                                            "<div class=\"controls\">\n"+
			                                                "<input type=\"text\" name=\"name\" class=\"input-block-level special-char-validation-search\" id=\"vendorNameSearch\" pattern=\"[a-zA-Z0-9&'*\\- ]+\" />\n"+
			                                              	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only &,-, * and '.</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"control-group span6\">\n"+
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
			                                        "<select id=\"country-select\" class=\"country-select selectIndexCountry\">\n"+
														"<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
														// Begin of Comment by Naga ENHC0013660
														// Get the country code from backend.
			                                             /*for (int x = 0; x < arrayCountryCode.length; x++) {
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
																}*/
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
			                                                    "<label class=\"control-label\">Address 1</label>\n"+
			                                                    "<div class=\"controls\">\n"+
			                                                        "<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress1\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Street Name\">\n"+
			                                                   		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: none;\">Special character are not allowed.</div>"+
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"span3\">\n"+
			                                                "<div class=\"control-group\">\n"+
			                                                    "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+
			                                                    "<div class=\"controls\">\n"+
			                                                        "<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress2\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Building or Unit Number\">\n"+
			                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"span3\">\n"+
			                                                "<div class=\"control-group\">\n"+
			                                                    "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+
			                                                    "<div class=\"controls\">\n"+
			                                                        "<input class=\"input-block-level alpha-num-validation\" type=\"text\" name=\"primaryAddress3\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Suite or Room Number\">\n"+
			                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"row-fluid validate-group\">\n"+
			                                            "<div class=\"span6\">\n"+
			                                                "<div class=\"control-group\">\n"+
			                                                    "<label class=\"control-label\">City</label>\n"+
			                                                    "<div class=\"controls\">\n"+
			                                                        "<input type=\"hidden\" name=\"primaryCountry\" />\n"+
			                                                        "<input class=\"input-block-level city special-char-validation-exceptdash\" type=\"text\" name=\"primaryAddressCity\"  pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"City\">\n"+
			                                                		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only - .</div>\n"+ 			                  
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"span3\">\n"+
			                                                "<div class=\"control-group\">\n"+
			                                                    "<label class=\"control-label\"><span class=\"state\">State</span><span class=\"prov hide\">/Province</span></label>\n"+
			                                                    "<div class=\"controls\">\n"+
			                                                        "<select class=\"input-block-level state\" name=\"primaryAddressState\">\n"+
			                                                            "<option value=\"\">Select State</option>\n"+
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
			                            	"<input type=\"hidden\" name=\"requestType\" id=\"requestType\" value=\"2\"/>\n"+	
			                            	"<input type=\"hidden\" name=\"userType\" id=\"userType\" value=\""+userType+"\"/>\n"+				                            	
			                                "<div class=\"container-fluid\">\n"+
			                                    "<fieldset>\n"+
			                                     
			                                        "<legend>Basic Information</legend>\n"+
			                                        //Req#600 START - Code added by AGAMPA 18-Feb-2015
			                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "Submitted By\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"text\" name=\"requestedBy\" disabled value=\""+I_REQUESTED_NAME+"\" class=\"input-block-level\" />\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "Requested For\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
		                                                    // Begin of Insert by Naga Enh ENHC0013666
		                                                	//"<input type=\"text\" name=\"requestedFor\" class=\"input-block-level\" />\n"+
		                                                	"<div class=\"input-append addRequestedFor\" id=\"addRequestedFor\">\n"+
			                                                	"<input type=\"text\" name=\"requestedFor\" id=\"requestedFor\" class=\"input-block-level\" />\n"+
			                			                        "<a class=\"btn fade\" id=\"searchRequestedFor\" title=\"Search for User\">\n"+
			                			                        	"<i class=\"icon-search\"></i>\n"+
			                			                        "</a>\n"+
			                			                    "</div>\n"+    
		                			                        // End of Insert by Naga
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                        //Req#600 END
			                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span5\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
			                                                    " Vendor Name\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"text\" name=\"vendorName\" required=\"required\" pattern=\"[a-zA-Z0-9&'\\- ]+\" class=\"input-block-level special-char-validation\" id=\"vendorNameInput\" />\n"+
			                                                	"<div class=\"alert alert-danger\" style=\"width: 83%; font-size: 9.5pt;display: none;\">No special characters allowed only &,- and '.</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"control-group span3\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
			                                                    " Language\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<select name=\"vendorLanguage\" required=\"required\" class=\"input-block-level\">\n"+
			                                                        "<option>English</option>\n"+
			                                                        "<option>Spanish</option>\n"+
			                                                        "<option>French</option>\n"+
			                                                    "</select>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"control-group span4\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
			                                                    "Country Doing Business In\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<select id=\"vendorCountry\" name=\"vendorCountry\" required=\"required\" class=\"input-block-level \">\n"+	// Naga ENHC0013660 added id
			                                                        "<option value=\"\">Select Country</option>\n");   
				                                   			 		// Begin of comment by Naga ENHC0013660
				                                   			 		/*
																	for (int x = 0; x < arrayCountryCode.length; x++) {
																		response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
																	}*/
																	// End of comment by Naga ENHC0013660
			                                                    response.write("</select>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                    "</fieldset>\n"+
			                                    "<fieldset>\n"+
/*			                                        "<legend>Vendor Contact</legend>\n"+
			                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "First Name\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"text\" name=\"contactFirstName\" required=\"required\" class=\"input-block-level\" />\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "Last Name\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"text\" name=\"contactLastName\" required=\"required\" class=\"input-block-level\" />\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "Email Address\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"email\" name=\"contactEmail\" required=\"required\" class=\"input-block-level\" />\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label\">\n"+
			                                                    "Phone Number\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"tel\" pattern=\"\\(\\d{3}\\) \\d{3}-\\d{4}\" required=\"required\" class=\"input-block-level phone-number\" name=\"contactPhone\" />\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n"+
			                                    "</fieldset>\n"+*/
			                                    "<fieldset>\n"+
			                                        "<legend>NBCU Integration</legend>\n"+
			                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
			                                                    " Vendor Type\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<select name=\"vendorType\" required=\"required\" class=\"input-block-level\">\n"+
			                                                        "<option value=\"\">Select Vendor Type</option>\n"+
			                                                        "<option value=\"010\">Trade Vendor</option>\n"+
																	"<option value=\"020\">Freelancer/Talent/Statistician</option>\n"+
																	"<option value=\"030\">Charitable</option>\n"+
																	"<option value=\"040\">Political Contribution</option>\n"+
																	"<option value=\"050\">Government</option>\n"+
//																	"<option value=\"060\">Refund/Reimbursement - One Time</option>\n"+	// Naga ENHC0016461
//																	"<option value=\"093\">Legal Settlement - One Time</option>\n"+	// Naga ENHC0016461
//																	"<option value=\"094\">Contest Winner - One Time</option>\n"+	// Naga ENHC0016461
																	"<option value=\"999\">One Time</option>\n"+ // Naga ENHC0016461
			                                                        "<option value=\"018\">Production/Agreement</option>\n"+// Pranesh - Added New Vendor Type (ENHC0016459)
																	//"<option value=\"070\">Petty Cash</option>\n"+ Pranesh -(04/16/2016)
																	"<option value=\"080\">Utility</option>\n"+
																	"<option value=\"090\">Comcast</option>\n"+
																	// Begin of Insert by Naga ENHC0013685
																	// Add Revenue Share Vendor type
																	// ENHC0013685 Add Garnishment Vendor type
																	"<option value=\"091\">Revenue Share</option>\n"+
																	"<option value=\"092\">Garnishment</option>\n"+
																	// End of Insert by Naga
			                                                    "</select>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                            // Modified by CGUTJAHR 1/13/15 : Enhancement #41
			                                            "<div class=\"control-group span6 subSystem\">\n"+
														// END
			                                                "<label class=\"control-label\">\n"+
			                                                    "Sub-System\n"+	
			                                                "</label>\n"+
				                                            "<div class=\"controls controls-scroll\">\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"maximo\" id=\"maximo\" /> Maximo (Technical)</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"eatec\" id=\"eatec\" /> Eatec (Food)</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"jda\" id=\"jda\" /> JDA (Merchandise)</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"costar\" id=\"costar\"/> Costar</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"vista\" id=\"vista\" /> Vista</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"compass\" id=\"compass\" /> Compass</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"compass_juice\" id=\"compass_juice\" /> Compass Juice</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"paris\" id=\"paris\" /> Paris</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"garnishment\" id=\"garnishment\" /> Garnishment</label>\n"+
																"<label class=\"checkbox\"><input type=\"checkbox\" name=\"trisepts\" id=\"trisepts\" /> Trisepts</label>\n"+   
				                                                "</div>\n"+
			                                            "</div>\n"+
			                                            // Begin of Insert by Naga ENHC0016461
			                                            // For one time vendor, user should select vendor type
			                                            "<div class=\"control-group span6 subVendorType\" style=\"display: none;\">\n"+
		                                                "<label class=\"control-label required-red\">\n"+
		                                                    " One Time Vendor\n"+
		                                                "</label>\n"+
		                                                "<div class=\"controls\">\n"+
		                                                    "<select id=\"subVendorType\" name=\"subVendorType\" required=\"required\" class=\"input-block-level\">\n"+
		                                                        "<option value=\"\">Select One Time Vendor</option>\n"+
																"<option value=\"060\">Refund/Reimbursement</option>\n"+	
																"<option value=\"093\">Legal Settlement</option>\n"+	
																"<option value=\"094\">Contest Winner</option>\n"+	
																"<option value=\"095\">Posthumous Payments</option>\n"+ // Pranesh - Added New Vendor Type (ENHC0016459)
		                                                    "</select>\n"+
		                                                "</div>\n"+
		                                                "</div>\n"+			                                            
			                                            // End of Insert by Naga			                                            
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
			                                    "<fieldset>\n"+
	                                  "<legend><label class=\"control-label required-red\">Payment Terms</label></legend>\n"); // DFCT0017452- Ganesh
			                                        if (userIsInternalEmployeeBuyer){			                                                    
			                                        response.write("<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span12 ers\">\n"+
			                                            // Modified by CGUTJAHR 1/13/15 : Enhancement #41
			                                                "<label class=\"control-label ers-label\">\n"+
														// END
			                                                    "ERS?\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<div class=\"input-append\">\n"+
			                                                        "<div id=\"ersGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
			                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
			                                                            "<select name=\"ersYesTerms\" class=\"add-on ers-yes\">\n"+
			                                                                "<option value=\"0001\">Immediate Pay</option>\n"+
			                                                            "</select>\n"+
			                                                            "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
			                                                            "<select name=\"ersNoTerms\" required=\"required\" class=\"add-on ers-no\">\n"+ // DFCT0017452- Ganesh added "required"
			                                                                "<option value=\"\">Select Terms</option>\n");
			                                                                // Set up the Environment Questions
																	        for(int i = 0; i < retPaymentTerms.getNumRows(); i++) {
																	        	if ((!retPaymentTerms.getString("TEXT1").toUpperCase().contains("DO NOT USE") && retPaymentTerms.getString("TEXT1").length()>1))
																	        		response.write( "<option value=\""+retPaymentTerms.getString("ZTERM")+"\"> "+retPaymentTerms.getString("TEXT1")+"</option>\n");
																	        	retPaymentTerms.nextRow();
																	        }
			                                                            response.write("</select>\n"+
			                                                            "<div class=\"hidden-form-elements\">\n"+
			                                                                "<input id=\"ersYes\" type=\"radio\"  required=\"required\"  name=\"ers\" value=\"yes\" />\n"+ // DFCT0017452- Ganesh added "required"
			                                                                "<input id=\"ersNo\" type=\"radio\" name=\"ers\" value=\"no\" />\n"+
			                                                            "</div>\n"+
			                                                        "</div>\n"+
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n");			                                                            
			                                        } else if (userIsInternalEmployeeInviter || userIsJointVenture){
			                                        response.write("<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span12 ers\">\n"+
			                                                //"<label class=\"control-label\">\n"+
			                                                 //   "Payment Terms\n"+
			                                                //"</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                    "<div class=\"input-append\">\n"+
			                                                        "<div id=\"ersGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
			                                                            //"<a class=\"btn no-answer active\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
			                                                            "<select name=\"ersNoTerms\" required=\"required\" class=\"add-on ers-no show\">\n"+ // DFCT0017452- Ganesh added "required"
			                                                                "<option value=\"\">Select Terms</option>\n");
			                                                                // Set up the Environment Questions
																	        for(int i = 0; i < retPaymentTerms.getNumRows(); i++) {
																	        	if ((!retPaymentTerms.getString("TEXT1").toUpperCase().contains("DO NOT USE") && retPaymentTerms.getString("TEXT1").length()>1))
																	        		response.write( "<option value=\""+retPaymentTerms.getString("ZTERM")+"\"> "+retPaymentTerms.getString("TEXT1")+"</option>\n");
																	        	retPaymentTerms.nextRow();
																	        }
			                                                            response.write("</select>\n"+
			                                                            "<div class=\"hidden-form-elements\">\n"+
			                                                                "<input id=\"ersYes\" type=\"radio\" name=\"ers\"  value=\"yes\" />\n"+ 
			                                                                "<input id=\"ersNo\" type=\"radio\" name=\"ers\" value=\"no\" checked/>\n"+
			                                                            "</div>\n"+
			                                                        "</div>\n"+
			                                                    "</div>\n"+
			                                                "</div>\n"+
			                                            "</div>\n"+
			                                        "</div>\n");			                                        	
			                                        }
			                                        response.write("<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\">\n"+
				                                                "<label class=\"control-label required-red\">\n"+
				                                                    " Approver\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<div class=\"input-append\" id=\"addApprover\">\n"+		
				                                                        "<input type=\"text\" id=\"enterApprovers\" maxlength=\"4\" required=\"required\"/>\n"+
				                                                        "<a class=\"btn fade\" id=\"searchApproverCodes\" title=\"Search Approvers\">\n"+
				                                                            "<i class=\"icon-search\"></i>\n"+
				                                                        "</a>\n"+			           
				                                                    "</div>\n"+
				                                                	/*"<select name=\"selectedApprover\" required=\"required\" class=\"add-on show\">\n"+
			                                                                "<option value=\"\">Select Approver</option>\n");
			                                                                // Set up the Environment Questions
																	        for(int i = 0; i < retApprovers.getNumRows(); i++) 
																	        {
																	        	response.write( "<option value=\""+retApprovers.getString("APPROVER_SSO")+"\"> "+retApprovers.getString("NAME")+" ("+retApprovers.getString("APPROVER_SSO")+")</option>\n");
																	        	retApprovers.nextRow();
																	        }
			                                                            response.write("</select>\n"+*/
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n");			                                        
			                                        if (userIsInternalEmployeeInviter){
				                                        response.write("<div class=\"row-fluid\">\n"+
				                                           // Modified by CGUTJAHR 1/13/15 : Enhancement #41
				                                            "<div class=\"control-group span6 annualSpend\">\n"+
														   // END
				                                                "<label class=\"control-label\">\n"+
				                                                    "Annual Spend\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<input name=\"annualSpend\" type=\"text\" class=\"input-block-level\" />\n"+
				                                                    //"<span class=\"help-block\">Please indicate currency</span>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"row-fluid\">\n"+
			                                            "<div class=\"control-group span12\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
			                                                    "Comments (No Special Characters Allowed) <i class=\"icon-question-sign tip\" title=\"Use this section to provide specific information about the goods and/or services the vendor will be providing and where the goods will be delivered or services provided.  If a Trade vendor is being offered non-standard terms, provide the reason.  Note, the information entered here is included in the Comments of the  Approval email(s) sent to the approver you select and if applicable to the Terms Approver.\"></i>\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
			                                                	// Change by Naga, make comments mandatory 999-1
			                                                    "<textarea name=\"comments\" class=\"input-block-level invitation-comment\" required=\"required\"></textarea>\n"+
			                                                 "</div>\n"+ 
			                                          "</div>\n"+
			                                        "</div>\n");
			                                        } 
			                                    response.write("</fieldset>\n"+                                   
			                                    "<div class=\"form-actions\">\n"+
			                                        "<a class=\"btn btn-large btn-success pull-left\" id=\"proceed-registration\"><i class=\"icon-envelope\"></i> Proceed to registration</a>\n"+
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
			                                    "<div id=\"collapseOne\" class=\"accordion-body collapse\">\n"+
			                                        "<div class=\"accordion-inner\">\n"+
			                                            "Call Minnow!\n"+
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
			                                            "Call Minnow!\n"+
			                                        "</div>\n"+
			                                    "</div>\n"+
			                                "</div>\n"+
			                                
			                            "</div>\n"+*/
			                     // Blocked Pranesh(04/19/2016) - ENHC0019059
			                        
			                     // Begin added Pranesh(04/19/2016) - ENHC0019059
			                        		"<h3><i class=\"icon-plus-sign-alt\"></i>Documentation</h3>\n"+
				                                 "<div class=\"accordion\" id=\"accordion2\">\n");
				                                    //response.write("-> :"+userIsInternalEmployeeInviter+" - "+documentation[0]);
				                                    if(userIsInternalEmployeeInviter || userIsInternalEmployeeBuyer || userIsJointVenture){
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
		                        response.write("<p style=\"color: red;\">" +helps[tempVal]+"</p>\n"+ 
		                        			"</div>\n"+
		                                "</div>\n"+
		                        //End added - Pranesh Code End (04/19/2016)-ENHC0019059
		                                
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
			            "<div class=\"modal-footer\">\n"+
			                "<a href=\"#\" class=\"btn\" data-dismiss=\"modal\">Cancel</a>\n"+
			                "<a href=\"#\" class=\"btn btn-primary\" id=\"add-codes\">Add Codes</a>\n"+
			            "</div>\n"+
			        "</form>\n"+
			    "</div>\n"+

			    "<div class=\"modal fade hide\" id=\"vendor-search-modal\">\n"+
			        "<div class=\"modal-header\">\n"+
			            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
			            "<h3>4 vendors found</h3>\n"+
			            "<p>If one of the results below matches your vendor, it means that the vendor is already in the system.</p>\n"+
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
			                            "<th>Country</th>\n"+
			                            //"<th>Status</th>\n"+			DFCT0013688 by Naga
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
			                            "<td><span class=\"badge badge-success\">Approved</span></td>\n"+
			                            "<td><a class=\"btn btn-inverse\"><i class=\"icon-eye-open\"></i></a>\n"+
			                        "</tr>\n"+
			                    "</tbody>\n"+
			                "</table>\n"+
			            "</div>\n"+
			        "</div>\n"+
			        "<div class=\"modal-footer\">\n"+
			            "<a href=\"#\" class=\"btn btn-danger\" data-dismiss=\"modal\">Vendor Already Exists</a>\n"+
			            // By Naga add registration differentiator
			            "<a href=\"#\" class=\"btn btn-success btn-success-reg\" data-dismiss=\"modal\">Vendor Does Not Exist</a>\n"+
			        "</div>\n"+
			    "</div>\n"+

			    "<script id=\"vendorSearchTemplate\" type=\"text/template\">\n"+
			        "<tr class=\"hide\">\n"+

			            "<td><%- vendorNum %></td>\n"+
			            "<td><%- remitNum %></td>\n"+
			            "<td><%- name %></td>\n"+
			            "<td><%- tin %></td>\n"+
			            "<td><%- address %> <strong><%- city %>, <%- state %></strong></td>\n"+
			            "<td><%- postalCode %></td>\n"+
			            "<td><%- country %></td>\n"+
			            // Begin of comment by Naga ENHC0013668
			            // Comment the status
//			            "<td>\n"+
//			                "<%\n"+
//			                "var cls = \"\"; \n"+
//			                "if (status.text == \"Pending Approval\") {\n"+
//			                    "cls = \"badge-primary\";\n"+
//			                "} else if (status.text == \"Draft\") {\n"+
//			                    "cls = \"badge-warning\";\n"+
//			                 //status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
//			                "} else if (status.text == \"In Review\") {\n"+
//			                    "cls = \"badge-success\";\n"+
//			                "} else if (status.text == \"Rejected\") {\n"+
//			                    "cls = \"badge-important\";\n"+
//			                "} \n"+
//			                "%>\n"+
//			                "<span class=\"badge <%- cls %>\"><%- status.text %></span>\n"+
//			            "</td>\n"+
			            // End of comment by Naga
			            // Begin of comment by Naga DFCT0013688
			            // Comment the maintain or view button DFCT0013688 			            
			            /*"<td>\n"+
			            	//status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
			                "<% if (status.text == \"In Review\" || status.text == \"Pending Approval\" ) { %>\n"+
			                "<a class=\"view-vendor btn btn-inverse btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-eye-open\"></i></a>\n"+
			                "<% } else { %>\n"+
			                "<a class=\"view-vendor btn btn-info btn-mini\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=1&vendorId=<%- id %>\"><i class=\"icon-pencil\"></i></a>\n"+
			                "<% } %>\n"+
			            "</td>\n"+*/

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
			    
			    // Begin of Insert by Naga ENHC0013666
			    // Modal window and search result templates
			    "<div class=\"modal hide fade\" id=\"requestedForResults\">\n"+
		        "<div class=\"modal-header\">\n"+
		            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
		            "<h3>Requested For</h3>\n"+
		        "</div>\n"+
		        "<form>\n"+
		            "<div class=\"modal-body\">\n"+
		                "<div>\n"+
		                    "<div class=\"input-append\">\n"+
		                        "<input id=\"reqFor\" type=\"text\" />\n"+
		                        "<a id=\"check-names\" class=\"btn btn-primary\">Check Names</a>\n"+
		                    "</div>\n"+
		                    "<div class=\"clearfix\"></div>\n"+
		                "</div>\n"+
		                "<div class=\"code-table\">\n"+
		                    "<div class=\"alert alert-info\">No Results</div>\n"+
			                "<table class=\"table table-condensed table-striped\">\n"+
			                  "<thead>\n"+
			                    "<tr>\n"+
			                      "<th>\n"+
			                        "Name\n"+
			                      "</th>\n"+
			                      "<th> \n"+
			                        "Title\n"+
			                      "</th>\n"+
			                      "<th>\n"+
			                        "Company\n"+
			                      "</th>\n"+
			                    "</tr>\n"+
			                  "</thead>\n"+
			                  "<tbody>\n"+
			                  "</tbody>\n"+
			                "</table>\n"+
		                "</div>\n"+
		            "</div>\n"+
		        "</form>\n"+
		    "</div>\n"+
			    
			    
		        "<script type=\"text/template\" id=\"search-requested-template\">\n"+
		          "<tr class=\"recipient\" data-sso=\"<%- sso %>\">\n"+
		            "<td>\n"+
		              "<a class=\"name\" href=\"#\" title=\"<%- lastname + \", \" + firstname %>\"><%- lastname + \", \" + firstname %></a>\n"+
		            "</td>\n"+
		            "<td>\n"+
		              "<span title=\"<%- Title %>\"><%- Title %></span>\n"+
		            "</td>\n"+
		            "<td>\n"+
		              "<span title=\"<%- Company %>\"><%- Company %></span>\n"+
		            "</td>\n"+
		          "</tr>\n"+		        
		        "</script>\n"+
			    // End of Insert by Naga 
			    

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
			        "app.page.inviterView = new app.views.InviterView({ restrictCompanyCode: false });\n"+
			    "</script>\n");
			    
				        if ((request.getServletRequest().getServerName().equalsIgnoreCase("coding.nbcuni.com") || (request.getServletRequest().getServerName().equalsIgnoreCase("vendor.nbcuni.com")))) {
				          response.write("<script src=\"/"+ServerString+"/js/sap_portal_omniture.js\"></script>\n");
		                  response.write("<script  type='text/javascript'>\n"+
								"//Omniture Code start\n"+
								"s.pageName='VRA Register on Behalf';\n"+  // Enter Page name to be tracked
								"s.server='"+request.getServletRequest().getServerName()+"';\n"+
								"s.channel='';\n"+
								"s.pageType='';\n"+
								"s.prop1= "+userId+";\n"+ //username SSO to be tracked
								"s.prop2=''; //Content Group\n"+
								"s.prop3=''; //Content Type\n"+
								"s.prop4=''; //Content Name\n"+
								"var s_code=s.t();\n"+
								"if(s_code)document.write(s_code)\n"+
								"//Omniture Code ends\n"+
							"</script>\n");  
				        }
			response.write("</body>\n"+
			"</html>\n");
			}
				
			} catch (Exception e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
				response.write(components.displayErrorContainer(request,"inviter",e1.getMessage()));
			}

		} catch (Exception e) {
			
			e.printStackTrace();

		} 
    }
}