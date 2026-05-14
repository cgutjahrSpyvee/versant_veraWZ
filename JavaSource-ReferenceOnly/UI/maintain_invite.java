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
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class maintain_invite extends AbstractPortalComponent
{

	String ServerString = "com.nbcu.html5_vra";

	public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	String WFSystemAlias = "SAP_R3";
		IUser userObject = request.getUser();		
	    String userId = userObject.getUniqueName();		
	    String inviteNumber = request.getParameter("requestId");
//	    String mailid       = request.getParameter("venmail");
	    String mode = request.getParameter("mode");
	    String actkey = request.getParameter("actkey");
	    // Begin of Comment and Insert by Naga
	    // ENHC0019060 Adjust the logic for requests coming from SNAP Inbox for approval.
	    String isApproval = request.getParameter("mode");
	    
	    if(isApproval!=null&&isApproval.equalsIgnoreCase("approval")){
	    	mode = "D";
	    	actkey = "";
	    }
	    // End of Comment and Insert by Naga
	    try {
			String userType = "";
			String paymentTermUserType = "";
			
			boolean userIsJointVenture = false;
			boolean userIsExternalVendor = false;
			boolean userIsInternalEmployeeBuyer = false;
			boolean userIsInternalEmployeeInviter =  false;
						
    		// Drop Down Arrays
    		String[][] arrayCountryCode = tools.setupCountryCodeArray();   
    		
    		// JCO Vars
    		JCO.Table retPaymentTerms = null;
    					
			// UI
	    	HttpServletResponse resp = request.getServletResponse(true);
			PrintWriter response = resp.getWriter();
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
						userIsExternalVendor = true; // Added Pranesh(04/19/2016) - ENHC0019059
						userType = "3";
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";						
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeInviter = true;
						userType = "1";
						//Temp
						paymentTermUserType = "1";
					}
					// Begin of Insert by Naga ENHC0016164
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						paymentTermUserType = "2";
						userType = "2";						
					}
					else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsJointVenture = true;
						paymentTermUserType = "4";
						userType = "4";						
					}					
					// End of Insert by Naga 
					retUserRoles.nextRow();
				}
				
				// Begin of Insert by Naga ENHC0019060
				if(isApproval.equalsIgnoreCase("approval")){
					userIsInternalEmployeeInviter = true;
					userType = "1";
					paymentTermUserType = "1";
				}
				// End of Insert by Naga
							
				SAP_FUNCTION = m_Repository.getFunctionTemplate("Z_SFI_I486_APPVR_LIST");
	
				JCO.Function functionApprovers = new JCO.Function(SAP_FUNCTION);
				
				//Req#23 START - Code added by AGAMPA 21-Feb-2015
				JCO.ParameterList apListImportList = functionApprovers.getImportParameterList();
				apListImportList.setValue(userId, "I_REQUESTED_BY");			
				//Req#23 START - Code added by AGAMPA 21-Feb-2015
				// Begin of Insert by Naga ENHC0019060
					if(isApproval.equalsIgnoreCase("approval")){
						apListImportList.setValue("A", "I_MODE");	
					}
					
				// End of Insert by Naga
				
				client.execute(functionApprovers);	
				
				JCO.Table retApprovers = functionApprovers.getExportParameterList().getTable("IT_APPROVER_LIST");
				String I_REQUESTED_NAME = functionApprovers.getExportParameterList().getString("I_REQUESTED_NAME");
							
				
				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("ZSFI_I507_VRA_INVITE_DISPLAY");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();
				
				importList.setValue(inviteNumber, "IM_INVITE_ID");
//				importList.setValue(mailid, "IM_EMAIL");
				
				client.execute(function);
				
				JCO.Table compCod			= function.getExportParameterList().getTable("EX_IT_CCODE");	//internal table ZSFS_I507_INVCC
				JCO.Structure invDetails 	= function.getExportParameterList().getStructure("EX_S_INVITE");
				String contactEmail = function.getExportParameterList().getString("EX_EMAIL");
							
				
				SAP_FUNCTION = m_Repository.getFunctionTemplate("Z_SF_I512_PAYMENTS_W2W");
	
				JCO.Function functionTerms = new JCO.Function(SAP_FUNCTION);
				importList = functionTerms.getImportParameterList();
				importList.setValue("I", "I_SCREEN_FLAG");
				importList.setValue(paymentTermUserType, "I_VENDOR_CAT");
 				importList.setValue(invDetails.getString("VEND_TYPE"), "I_VENDOR_TYPE");	
 				//importList.setValue(vendorCat, "I_VENDOR_CAT");				
				
				client.execute(functionTerms);	
				retPaymentTerms = functionTerms.getExportParameterList().getTable("IT_PAYMENT_TERMS");				
				
				int ccLength = compCod.getNumRows();
				String compCodeString = "";
				
				for(int ind=0;ind<ccLength;ind++)
				{
					compCodeString += compCod.getString("BUKRS");
					
					if(compCod.nextRow())
					{
						compCodeString +=";";
					}
				}				

				String inviteNumber2 	= invDetails.getString("ZZSF_VRA_EMLID");
				
				// Change by CMG 
				//String requestedBy 		= invDetails.getString("INVITE_SSO");	
				String requestedBy 		= I_REQUESTED_NAME;	
				String contactFirstName = invDetails.getString("FIRST_NAME");
				String contactLastName 	= invDetails.getString("LAST_NAME");
				String contactPhone 	= invDetails.getString("TELEPHONE");
				String vendorLanguage 	= invDetails.getString("SPRAS");
				String vendorCountry 	= invDetails.getString("LAND1");
				String vendorName 		= invDetails.getString("VEND_NAME");
				String vendorType 		= invDetails.getString("VEND_TYPE");
				String subSystemString 	= invDetails.getString("J_1KFREPRE");
				//String CHANGE 		= invDetails.getString("ADMIN_SSO");
				//String source 		= invDetails.getString("SOURCE");
				String compCode 		= compCodeString;
				String ersTerms 		= invDetails.getString("ZTERM");
				String ersValue 		= invDetails.getString("XERSY");
				String annualSpend 		= invDetails.getString("ANNUAL_SPEND");
				String comments 		= invDetails.getString("INVCOMMENT");
				//String CHANGE 		= invDetails.getString("ZZSF_VRA_INVSTAT");
				String status 			= invDetails.getString("ZZSF_VRA_INVSTAT");				// ENHC0019060
				//String CHANGE 		= invDetails.getString("ZZSF_VRA_VENDGRP");
				userType 				= invDetails.getString("ZZSF_VRA_VENDCAT");
				//String CHANGE 		= invDetails.getString("ZZSF_VRA_PORECV");
				//String CHANGE 		= invDetails.getString("ZZSSO_UPD");
				//String CHANGE 		= invDetails.getString("ZZSF_VRA_POEX");
				String selectedApprover = invDetails.getString("APPROVER_SSO");					
				String requestedFor 	= invDetails.getString("REQUESTED_FOR");
				
				String srcValue   = invDetails.getString("KONZS");// Pranesh(04/27/2016) - ENHC0018725 
				
				String disabled			= mode.indexOf("D") != -1?"disabled":"";
				
				
				// Begin of Pranesh(04/19/2016) - ENHC0019059
				
					int tempVal=0;                                          // Pranesh(04/19/2016)-ENHC0019059
					String[] helps=help.helpContent(request,"00011");       // Pranesh(04/19/2016)-ENHC0019059
			    
					String[] documentation=help.helpContent(request,"00013");//Pranesh(04/19/2016)-ENHC0019059
					int documentLength=0;									// Pranesh(04/19/2016)-ENHC0019059
				
			    // End of Pranesh(04/19/2016) - ENHC0019059
				
				
				response.write(components.displayHeader(request,"inviter") +					
				"<form id=\"invitationForm\">\n"+
						"<div class=\"content maintain "+((disabled.equals("disabled"))?"locked":"")+"\">\n"+
			        "<div class=\"tab-content maintain-invite\">\n"+
/*			        "<div>contactEmail :"+contactEmail+"</div>"+
			        "<div>requestedBy :"+requestedBy+"</div>"+
			        "<div>contactFirstName:"+contactFirstName+"</div>"+
			        "<div>contactLastName :"+contactLastName+"</div>"+
			        "<div>contactPhone :"+contactPhone+"</div>"+
			        "<div>vendorLanguage :"+vendorLanguage+"</div>"+
			        "<div>vendorCountry :"+vendorCountry+"</div>"+
			        "<div>vendorName :"+vendorName+"</div>"+
			        "<div>vendorType :"+vendorType+"</div>"+
			        "<div>subSystemString:"+subSystemString+"</div>"+
			        "<div>compCode :"+compCode+"</div>"+
			        "<div>ersTerms :"+ersTerms+"</div>"+
			        "<div>ersValue :"+ersValue+"</div>"+
			        "<div>annualSpend :"+annualSpend+"</div>"+
			        "<div>comments :"+comments+"</div>"+
			        "<div>userType:"+userType+"</div>"+
			        "<div>selectedApprover:"+selectedApprover+"</div>"+
			        "<div>requestedFor :"+requestedFor+"</div>"+
			        "<div>disabled:"+disabled+"</div>"+*/
			            "<div class=\"tab-pane active\" id=\"invite\">\n"+
			                "<div class=\"container\">\n"+
			                    "<div class=\"row\">\n"+
			                    	"<div class=\"alert alert-info\" id=\"lockedAlert\">\n"+
			                    		"You cannot edit the form while it is in approval.\n"+
						            "</div>\n"+
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
			                    
			                        "<div class=\"span8\">\n"+
		                            	"<div class=\"form-actions\" style=\"background-color:transparent;\">"+
		                            		"<h3 style=\"float:left\">Vendor Invitation</h3>\n"+
		                                    //"<div class=\"form-actions\">\n"+
		                                    //((actkey.indexOf("S") != -1)?"<a class=\"btn btn-large btn-success pull-left\" id=\"sendInvitation\"><i class=\"icon-envelope\"></i>Send Invitation</a>\n":"")+
	                                        ((actkey.indexOf("R") != -1)?"<a class=\"btn btn-resend btn-success badge badge-success pull-right\" id=\"resendInvitationBtn\"><i class=\"icon-repeat\"></i>Resend Invite</a>\n":"")+		                            	
	                                        ((actkey.indexOf("C") != -1)?"<a class=\"btn btn-cancel btn-success  badge badge-success pull-right\" id=\"cancelInvitationBtn\"><i class=\"icon-remove\"></i>Cancel</a>\n":"")+
	                                        // Begin of Insert by Naga ENHC0013682
	                                        // Show Resend for Approval based on action key
	                                        ((actkey.indexOf("A") != -1)?"<a class=\"btn btn-resend btn-success badge badge-success pull-right\" id=\"resendApprovalBtn\"><i class=\"icon-repeat\"></i>Resend for Approval</a>\n":"")+
	                                        // End of Insert by Naga
		                                    // Begin of Insert by Naga  ENHC0019060
	                                        // When coming from SNAP Inbox display Approve and Reject Button
	                                        (((status.equals("0") || status.equals("8") || status.equals("M"))&&isApproval.equalsIgnoreCase("approval"))? // ganesh added  || status.equals("M")
					                            	"<div class=\"btn-group pull-right\">"+
														"<a class=\"btn btn-success\" id=\"approveButton\">Approve</a>"+
						                            	"<a class=\"btn btn-danger\" id=\"rejectButton\">Reject</a>"+
					                            	"</div>"
	                                        		:"")+
	                                        // End of Insert by Naga
		                                    //"</div>\n"+
		                            	"</div>\n"+
			                           
			                            	"<input type=\"hidden\" name=\"requestType\" id=\"requestType\" value=\"1\"/>\n"+
			                            	"<input type=\"hidden\" name=\"invitenum\" id=\"invitenum\" value=\""+inviteNumber2+"\"/>\n"+			                            	
			                            	"<input type=\"hidden\" name=\"userType\" id=\"userType\" value=\""+userType+"\"/>\n"+		                            	
			                                "<div class=\"container-fluid\">\n"+
			                                	"<div class=\"maintainBox\" style=\"position:relative\">"+
									        		"<div class=\"locked-overlay\"></div>\n"+
				                                    "<fieldset>\n"+
				                                        "<legend>Basic Information</legend>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\">\n"+
				                                                "<label class=\"control-label\">\n"+
				                                                    "Submitted By\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
			                                                    "<input type=\"text\" name=\"requestedBy\" disabled value=\""+requestedBy+"\" class=\"input-block-level\" />\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group span6\">\n"+
				                                                "<label class=\"control-label\">\n"+
				                                                    "Requested For\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                	// Begin of Insert by Naga Enh ENHC0013666
				                                                    //"<input type=\"text\" name=\"requestedFor\" "+disabled+" value=\""+requestedFor+"\" class=\"input-block-level\" />\n"+
					                                                "<div class=\"input-append addRequestedFor\" id=\"addRequestedFor\">\n"+
					                                            		"<input type=\"text\" id=\"requestedFor\" name=\"requestedFor\" "+disabled+" value=\""+requestedFor+"\" class=\"input-block-level\" />\n"+
					                                            		"<a class=\"btn fade\" id=\"searchRequestedFor\" title=\"Search for User\">\n"+
					                                            			"<i class=\"icon-search\"></i>\n"+
					                                            		"</a>\n"+
					                                            	"</div>\n"+  				                                                
				                                                    // End of Insert by Naga
				                                                  	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Non numeric characters are not allowed.</div>"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span5\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Vendor Name (Max. 35 characters)\n"+ // Added (Max. 35 characters) - Pranesh - (04/28/2016) -(ALD-15049)
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<input type=\"text\" maxlength=\"35\" name=\"vendorName\" "+disabled+" value=\""+vendorName+"\" required=\"required\" pattern=\"[a-zA-Z0-9&'\\- ]+\" class=\"input-block-level special-char-validation\" id=\"vendorNameInput\" />\n"+
				                                                	"<div class=\"alert alert-danger\" style=\"width: 83%; font-size: 9.5pt;display: none;\">No special characters allowed only &,- and '.</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group span3\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Language\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"vendorLanguage\" "+disabled+" value=\""+vendorLanguage+"\" required=\"required\" class=\"input-block-level\">\n"+
				                                                        "<option "+(vendorCountry.equals(vendorLanguage)?"selected":"")+">English</option>\n"+
				                                                        "<option "+(vendorCountry.equals(vendorLanguage)?"selected":"")+">Spanish</option>\n"+
				                                                        "<option "+(vendorCountry.equals(vendorLanguage)?"selected":"")+">French</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group span4\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Country Doing Business In\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"vendorCountry\" "+disabled+" required=\"required\" class=\"input-block-level\">\n"+
				                                                        "<option value=\"\">Select Country</option>\n");                                                                   
																		for (int x = 0; x < arrayCountryCode.length; x++) {
																			response.write("<option "+(vendorCountry.equals(arrayCountryCode[x][0])?"selected":"")+" value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
																		}
				                                                    response.write("</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</fieldset>\n"+
				                                    "<fieldset>\n"+
				                                        "<legend>Vendor Contact</legend>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "First Name\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<input type=\"text\" name=\"contactFirstName\" "+disabled+" value=\""+contactFirstName+"\" required=\"required\" class=\"input-block-level alpha-validation\" />\n"+
				                                                	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Numbers and special character are not allowed.</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Last Name\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<input type=\"text\" name=\"contactLastName\" "+disabled+" value=\""+contactLastName+"\" required=\"required\" class=\"input-block-level alpha-validation\" />\n"+
				                                                  	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Numbers and special character are not allowed.</div>"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Email Address\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<input type=\"email\" name=\"contactEmail\" "+disabled+" value=\""+contactEmail+"\" required=\"required\" class=\"input-block-level\" />\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Phone Number\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                "<input class=\"input-block-level phone-validation\" value=\""+contactPhone+"\" required=\"required\" type=\"tel\" name=\"contactPhone\"  >\n"+ 											//ganesh DFCT0017114
//			                                                    "<input type=\"tel\" pattern=\"\\(\\d{3}\\) \\d{3}-\\d{4}\" "+disabled+" value=\""+contactPhone+"\" required=\"required\" class=\"input-block-level phone-number\" name=\"contactPhone\" />\n"+
				                                                  	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Numbers only allowed.('-' allowed for US & Canada)</div>"+		//ganesh DFCT0017114	
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</fieldset>\n"+
				                                    "<fieldset>\n"+
				                                        "<legend>NBCU Integration</legend>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Vendor Type\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"vendorType\" "+disabled+" required=\"required\" class=\"input-block-level\">\n"+
				                                                        "<option value=\"\">Select Vendor Type</option>\n"+
				                                                        "<option "+(vendorType.equals("010")?"selected":"")+" value=\"010\">Trade Vendor</option>\n"+
																		"<option "+(vendorType.equals("020")?"selected":"")+" value=\"020\">Freelancer/Talent/Statistician</option>\n"+
																		"<option "+(vendorType.equals("030")?"selected":"")+" value=\"030\">Charitable</option>\n"+
																		"<option "+(vendorType.equals("040")?"selected":"")+" value=\"040\">Political Contribution</option>\n"+
																		"<option "+(vendorType.equals("050")?"selected":"")+" value=\"050\">Government</option>\n"+
//																		"<option "+(vendorType.equals("060")?"selected":"")+" value=\"060\">Refund/Reimbursement</option>\n"+	// Naga ENHC0016461
																		"<option "+((vendorType.equals("060") || vendorType.equals("093") || vendorType.equals("094"))?"selected":"")+" value=\"999\">One Time</option>\n"+   // Naga ENHC0016461
																		//"<option "+(vendorType.equals("070")?"selected":"")+" value=\"070\">Petty Cash</option>\n"+ - Pranesh(04/16/2016)
																		"<option "+(vendorType.equals("018")?"selected":"")+" value=\"018\">Production/Agreement</option>\n"+  // Added "Production/Agreement" - Pranesh (05/11/2016)-(Defect ID : 15084)
																		"<option "+(vendorType.equals("080")?"selected":"")+" value=\"080\">Utility</option>\n"+
																		"<option "+(vendorType.equals("090")?"selected":"")+" value=\"090\">Comcast</option>\n"+
																		// Begin of Insert by Naga ENHC0013685
																		"<option "+(vendorType.equals("091")?"selected":"")+" value=\"091\">Revenue Share</option>\n"+
																		"<option "+(vendorType.equals("092")?"selected":"")+" value=\"092\">Garnishment</option>\n"+
																		// End of Insert by Naga 
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            ((vendorType.equals("060") || vendorType.equals("093") || vendorType.equals("094"))?"<div class=\"control-group span6 subSystem\" style=\"display: none;\">\n":"<div class=\"control-group span6 subSystem\">\n")+ // ENHC0016461
//				                                            "<div class=\"control-group span6 subSystem\">\n"+				// ENHC0016461
				                                                "<label class=\"control-label\">\n"+
				                                                    "Sub-System\n"+	
				                                                "</label>\n"+
					                                            "<div class=\"controls controls-scroll\">\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("T") != -1?"checked":"")+"   type=\"checkbox\" name=\"maximo\" id=\"maximo\" /> Maximo (Technical)</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("F") != -1?"checked":"")+"   type=\"checkbox\" name=\"eatec\" id=\"eatec\" /> Eatec (Food)</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("M") != -1?"checked":"")+"   type=\"checkbox\" name=\"jda\" id=\"jda\" /> JDA (Merchandise)</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("C") != -1?"checked":"")+"   type=\"checkbox\" name=\"costar\" id=\"costar\"/> Costar</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("V") != -1?"checked":"")+"   type=\"checkbox\" name=\"vista\" id=\"vista\" /> Vista</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("P") != -1?"checked":"")+"   type=\"checkbox\" name=\"compass\" id=\"compass\" /> Compass</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("J") != -1?"checked":"")+"   type=\"checkbox\" name=\"compass_juice\" id=\"compass_juice\" /> Compass Juice</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("R") != -1?"checked":"")+"   type=\"checkbox\" name=\"paris\" id=\"paris\" /> Paris</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("G") != -1?"checked":"")+"   type=\"checkbox\" name=\"garnishment\" id=\"garnishment\" /> Garnishment</label>\n"+
																	"<label class=\"checkbox\"><input "+disabled+" "+(subSystemString.indexOf("S") != -1?"checked":"")+"   type=\"checkbox\" name=\"trisepts\" id=\"trisepts\" /> Trisepts</label>\n"+   
					                                                "</div>\n"+
				                                            "</div>\n"+
				                                            // Begin of Insert by Naga ENHC0016461
				                                            // For one time vendor, user should select vendor type
				                                            ((vendorType.equals("060") || vendorType.equals("093") || vendorType.equals("094"))?"<div class=\"control-group span6 subVendorType\">\n":"<div class=\"control-group span6 subVendorType\" style=\"display: none;\">\n")+ // ENHC0016461				                                            
//				                                            "<div class=\"control-group span6 subVendorType\">\n"+ // ENHC0016461
//			                                                "<label class=\"control-label required-red\">\n"+
			                                                ((vendorType.equals("060") || vendorType.equals("093") || vendorType.equals("094"))?"<label class=\"control-label required-red\">\n":"<label class=\"control-label\">\n")+
			                                                    " One Time Vendor\n"+
			                                                "</label>\n"+
			                                                "<div class=\"controls\">\n"+
//			                                                    "<select id=\"subVendorType\" name=\"subVendorType\" required=\"required\" class=\"input-block-level\">\n"+
			                                                    ((vendorType.equals("060") || vendorType.equals("093") || vendorType.equals("094"))?"<select id=\"subVendorType\" name=\"subVendorType\" required=\"required\" class=\"input-block-level\">\n":"<select id=\"subVendorType\" name=\"subVendorType\" class=\"input-block-level\">\n")+
			                                                        "<option value=\"\">Select One Time Vendor</option>\n"+
			                                                        "<option "+(vendorType.equals("060")?"selected":"")+" value=\"060\">Refund/Reimbursement</option>\n"+
			                                                        "<option "+(vendorType.equals("093")?"selected":"")+" value=\"093\">Legal Settlement</option>\n"+
			                                                        "<option "+(vendorType.equals("094")?"selected":"")+" value=\"094\">Contest Winner</option>\n"+
			                                                    "</select>\n"+
			                                                "</div>\n"+
			                                                "</div>\n"+			                                            
				                                            // End of Insert by Naga				                                            
				                                        "</div>\n"+
				                                        
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span6\" id=\"companyCodes\">\n"+
			                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Company Codes\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<div class=\"input-append\" id=\"addCompanyCode\">\n"+
			                                                        	"<input type=\"text\" id=\"enterCodes\" "+disabled+" maxlength=\"4\" "+ ((compCode.split(";").length > 0 )?"":"required=\"required\")")+" />\n"+
				                                                        "<a class=\"btn fade\" id=\"searchCodes\" "+disabled+" title=\"Search Company Codes\">\n"+
				                                                            "<i class=\"icon-search\"></i>\n"+
				                                                        "</a>\n"+
				                                                        "<a class=\"btn btn-primary\" "+disabled+" id=\"addCodes\">\n"+
				                                                            "<i class=\"icon-plus\"></i>Add\n"+
				                                                        "</a>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n");
				                                        
				                                        
				                                                    /* Blocked Temp Pranesh (05/02/2016) - Defect ID : 15051 
				                                     // Begin of insert Pranesh (04/27/2016) - ENHC0018725 
						                                 if(userIsInternalEmployeeBuyer){
						                                	 	response.write(
						                                	 			//ers
						                                	 			"<div class=\"span3 srcYesNo\">\n"+
						                                	 				"<label class=\"control-label\">\n"+"Sourcing Vendor ?"+"</label>\n"+
						                                	 					"<div class=\"questions\">\n"+
						                                	 						"<div class=\"accordion-heading\">\n"+
						                                	 						    	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
						                                	 							   		"<a "+disabled+" class=\"btn srcYes yes-answer "+(srcValue.equals("SOURCING")?"active":"")+"\" id=\"srcYes\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
						                                	 							   		"<a "+disabled+" class=\"btn srcNo "+(srcValue.equals("")?"active":"")+"\" id=\"srcNo\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
						                                	 							   	"<div class=\"hidden-form-elements\">\n"+
							                                                            "</div>\n"+
							                                                            	"<input type=\"hidden\" id=\"srcstatus\" name=\"srcstatus\" value=\"N\"/>\n"+	// Pranesh
						                                	 				            "</div>\n"+
						                                	 				        "</div>\n"+
						                                	 				    "</div>\n"+
						                                	 		"</div>\n"+
						                                	 	"</div>\n");
						                                 }
						                              // End of insert Pranesh (04/27/2016) - ENHC0018725 
				                                        
				                                       
				                                        */
				                                        
				                                        
				                                        
				                                        
						                response.write("<div class=\"row-fluid\" id=\"codesEntered\">\n"+
				                                            "<div class=\"pillbox span12\">\n"+
				                                                "<ul class=\"unstyled\"  style=\"display: block;\">\n");
			                                                    String[] tempCode = compCode.split(";");

			                                                    for(int i =0; i < tempCode.length ; i++)
			                                                        response.write("<li><span class=\"badge badge-info\">"+tempCode[i]+"</span><span class=\"pill-close\">&times;</span><input name=\"companyCodes\" type=\"hidden\" value=\""+tempCode[i]+"\"></li>");
				                                                response.write("</ul>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</fieldset>\n"+
				                                    "<fieldset>\n"+
				                                        "<legend>Payment Terms</legend>\n");
				                                        if (userIsInternalEmployeeBuyer){			                                                    
				                                        response.write("<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span12 ers\">\n"+
				                                                "<label class=\"control-label ers-label\">\n"+
				                                                    "ERS?\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<div class=\"input-append\">\n"+
				                                                        "<div id=\"ersGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                            "<a "+disabled+" class=\"btn yes-answer "+(ersValue.equals("")?"":"active")+"\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                                            "<select name=\"ersYesTerms\" class=\"add-on ers-yes "+(ersValue.equals("")?"":"show user-success")+"\">\n"+
				                                                                "<option value=\"0001\">Immediate Pay</option>\n"+
				                                                            "</select>\n"+
				                                                            "<a "+disabled+" class=\"btn no-answer "+(ersValue.equals("")?"active":"")+"\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                            "<select name=\"ersNoTerms\" class=\"add-on ers-no "+(ersValue.equals("")?"show user-success":"")+"\">\n"+
				                                                                "<option value=\"\">Select Terms</option>\n");
																		        for(int i = 0; i < retPaymentTerms.getNumRows(); i++) {
																		        	if ((!retPaymentTerms.getString("TEXT1").toUpperCase().contains("DO NOT USE") && retPaymentTerms.getString("TEXT1").length()>1))
																		        		response.write( "<option value=\""+retPaymentTerms.getString("ZTERM")+"\" "+(ersTerms.equals(retPaymentTerms.getString("ZTERM"))?"selected":"")+"> "+retPaymentTerms.getString("TEXT1")+"</option>\n");
																		        	retPaymentTerms.nextRow();
																		        }
				                                                            response.write("</select>\n"+
				                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                "<input id=\"ersYes\" type=\"radio\" name=\"ers\" value=\""+(ersValue.equals("")?"no":"yes")+"\" />\n"+
				                                                                "<input id=\"ersNo\" type=\"radio\" name=\"ers\" value=\""+(ersValue.equals("")?"yes":"no")+"\" />\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n");
				                                        } else if (userIsInternalEmployeeInviter){
				                                        response.write("<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span12 ers\">\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<div class=\"input-append\">\n"+
				                                                        "<div id=\"ersGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                            "<select name=\"ersNoTerms\" "+disabled+" class=\"add-on ers-no show\">\n"+
				                                                                "<option value=\"\">Select Terms</option>\n");
				                                                                // Set up the Environment Questions
				                                        						response.write(ersTerms+" - "+functionTerms);
																		        for(int i = 0; i < retPaymentTerms.getNumRows(); i++) {
																		        	if ((!retPaymentTerms.getString("TEXT1").toUpperCase().contains("DO NOT USE") && retPaymentTerms.getString("TEXT1").length()>1))
																		        		//response.write( "<option value=\""+retPaymentTerms.getString("ZTERM")+"\"> "+retPaymentTerms.getString("TEXT1")+"</option>\n");
																		        		response.write( "<option value=\""+retPaymentTerms.getString("ZTERM")+"\" "+(ersTerms.equals(retPaymentTerms.getString("ZTERM"))?"selected":"")+"> "+retPaymentTerms.getString("TEXT1")+"</option>\n");
						
																		        	retPaymentTerms.nextRow();
																		        }
				                                                            response.write("</select>\n"+
				                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                "<input id=\"ersYes\" type=\"radio\" name=\"ers\" value=\"yes\" />\n"+
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
					                                                    "Approver\n"+
					                                                "</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                	"<select name=\"selectedApprover\" "+disabled+" required=\"required\" class=\"add-on show\">\n"+
				                                                                "<option value=\"\">Select Approver</option>\n");
				                                                                // Set up the Environment Questions
																		        for(int i = 0; i < retApprovers.getNumRows(); i++) {
																		        	response.write( "<option value=\""+retApprovers.getString("APPROVER_SSO")+"\" "+(selectedApprover.equals(retApprovers.getString("APPROVER_SSO"))?"selected":"")+"> "+retApprovers.getString("NAME")+" ("+retApprovers.getString("APPROVER_SSO")+")</option>\n");
																		        	retApprovers.nextRow();
																		        }
				                                                            response.write("</select>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                        "</div>\n");
				                                        if (userIsInternalEmployeeInviter){
					                                        response.write("<div class=\"row-fluid\">\n"+
					                                            "<div class=\"control-group span6 annualSpend\">\n"+
					                                                "<label class=\"control-label\">\n"+
					                                                    "Annual Spend\n"+
					                                                "</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                    "<input name=\"annualSpend\" type=\"text\" "+disabled+" value=\""+annualSpend+"\" class=\"input-block-level\" />\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                        "</div>\n"+
					                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"control-group span12\">\n"+
				                                                "<label class=\"control-label required-red\">\n"+
				                                                    "Comments (No Special Characters Allowed)<i class=\"icon-question-sign tip\" title=\"Use this section to provide specific information about the goods and/or services the vendor will be providing and where the goods will be delivered or services provided.  If a Trade vendor is being offered non-standard terms, provide the reason.  Note, the information entered here is included in the Comments of the  Approval email(s) sent to the approver you select and if applicable to the Terms Approver.\"></i>\n"+
				                                                "</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<textarea name=\"comments\" "+disabled+" class=\"input-block-level invitation-comment\" required=\"required\">"+comments+"</textarea>\n"+
				                                                 "</div>\n"+ 
				                                          "</div>\n"+
				                                        "</div>\n");
				                                        } 
				                                    response.write("</fieldset>\n"+
			                                    "</div>"+
			                                    "<div class=\"form-actions\">\n"+
			                                    ((actkey.indexOf("S") != -1)?"<a class=\"btn btn-large btn-success pull-left\" id=\"sendInvitation\"><i class=\"icon-envelope\"></i>Send Invitation</a>\n":"")+
		                                        //((actkey.indexOf("R") != -1)?"<a class=\"btn btn-large btn-resend btn-success pull-right\" style=\"border-left:1px;border-left-color:#ffffff;border-left-style: solid;\" id=\"resendInvitationBtn\"><i class=\"icon-repeat\"></i>Resend Invite</a>\n":"")+		                            	
		                                       // ((actkey.indexOf("C") != -1)?"<a class=\"btn btn-large btn-cancel btn-success pull-right\" id=\"cancelInvitationBtn\"><i class=\"icon-remove\"></i>Cancel</a>\n":"")+		                            		
			                                        
			                                    "</div>\n"+
			                                "</div>\n"+
			                           // "</form>\n"+
			                        "</div>\n"+
			                        "<div class=\"span4 sidebar\">\n"+
			                        
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
			                                            "Instructions Here\n"+
			                                        "</div>\n"+
			                                    "</div>\n"+
			                                "</div>\n"+
			                                
			                            "</div>\n"+*/
			                            
			                        //Begin Of Insert by Pranesh -(04/19/2016) - ENHC0019059
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
					                        response.write("<p style=\"color: red;\">" +helps[tempVal]+"</p>\n"+ // altered by ganesh
					                            "</div>\n"+
					                                "</div>\n"+
					                		
					                //End Of Insert by Pranesh -(04/19/2016) - ENHC0019059
			                        
			                            
			                        "</div>\n"+
			                        
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n"+
			    "</div>\n"+
			    // Begin of Insert by Naga ENHC0019060
			    "<div id=\"decisionWindow\" class=\"modal fade hide\" >\n"+
		    	"<div class=\"modal-header\">\n"+
		    		"<h3 class=\"headerText\">Approve / Reject</h3>\n"+
		            "<div class=\"modalAlerts\">\n"+
		            "</div>\n"+
		    	"</div>\n"+
		    	"<div class=\"modal-body\">\n"+
		    		"<div>"+
		    			"<label class=\"decisionComments-label\">Comment (No Special Characters Allowed)</label>"+
				    	"<textarea name=\"decisionComments\" class=\"decisionComments\">\n" +
				    	"</textarea>\n"+
			    	"</div>"+
			    	// Marked as not required, so commenting the html, js changes and css changes are not commented yet.
//			    	"<div>"+
//			    		"<label class=\"contactPerson-label\">HR Contact Person</label>"+
//			    		"<div class=\"controls\">"+
//			    		"<div class=\"input-append contactPersonHolder\">"+
//				    	"<input type=\"text\" name=\"contactPerson\" class=\"contactPerson\">\n"+
//				    	"</input>"+
//                        "<a class=\"btn fade\" id=\"searchContactPerson\" title=\"Search for Contact Person\">\n"+
//                    	"<i class=\"icon-search\"></i>\n"+
//                    	"</a>\n"+
//                    	"</div>"+
//                    	"</div>"+
//                	"</div>"+
		    	"</div>\n"+
		    	"<div class=\"modal-footer\">\n"+
				    "<a href=\"#\" class=\"btn cancel\">Cancel</a>\n"+
				    "<a href=\"#\" class=\"btn btn-primary ok\">Approve</a>\n"+
		    	"</div>\n"+					    	
		    "</div>\n"+
				// End of Insert by Naga				    
			    "</form>\n"+
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
			    "<div class=\"modal hide fade\" id=\"resendInvite\">\n"+
			        "<div class=\"modal-header\">\n"+
						"<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
			            "<h3>Resend Invitation</h3>\n"+
			        "</div>\n"+
			        "<form  id=\"resendInvite\">\n"+
			        	"<input type=\"hidden\" name=\"invitenum\" id=\"invitenum\" value=\""+inviteNumber2+"\"/>\n"+
			            "<div class=\"modal-body\">\n"+
							"<div class=\"control-group span5\">\n"+						
							"</div>\n"+
			            "</div>\n"+
			            "<div class=\"modal-footer\">\n"+
			            	"<span class=\"footerLabel\">Are you sure you want to continue?</span>\n"+
			                "<a href=\"#\" class=\"btn btn-primary\" id=\"confirmResend\">Yes</a>\n"+			            	
			                "<a href=\"#\" class=\"btn btn-primary\" id=\"cancelResend\">No</a>\n"+			            
			            "</div>\n"+
			        "</form>\n"+
			    "</div>\n"+	
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
				    // Begin of Insert by Naga ENHC0019060
				    // Modal window and search result templates
			        // Even though it looks to be redundant modal it is done intentionally
				    "<div class=\"modal hide fade\" id=\"contactPersonResults\">\n"+
				        "<div class=\"modal-header\">\n"+
				            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
				            "<h3>HR Contact Person</h3>\n"+
				            "<br>"+
				        "</div>\n"+
			        "<form>\n"+
			            "<div class=\"modal-body\">\n"+
			                "<div>\n"+
			                    "<div class=\"input-append\">\n"+
			                        "<input id=\"contactPersonName\" type=\"text\" />\n"+
			                        "<a id=\"check-names\" class=\"btn btn-primary\">Check Names</a>\n"+
			                        "<br><br>\n"+
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
					    	"<div class=\"modal-footer\">\n"+
					    	"<br>"+
						    "</div>\n"+	
			            "</div>\n"+
			        "</form>\n"+
				    "</div>\n"+
				    // End of Insert by Naga			        
			    
			    "<div class=\"modal hide fade\" id=\"cancelInvite\">\n"+
			        "<div class=\"modal-header\">\n"+
						"<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
			            "<h3>Please select cancel reason</h3>\n"+
			        "</div>\n"+
			        "<form id=\"cancelInvite\">\n"+
			        	"<input type=\"hidden\" name=\"invitenum\" id=\"invitenum\" value=\""+inviteNumber2+"\"/>\n"+
			            "<div class=\"modal-body\">\n"+
			            "<div class=\"control-group span5\">\n"+
	                        "<label class=\"control-label\">\n"+
	                            "Cancel Reason\n"+
	                        "</label>\n"+
	                        "<div class=\"controls\">\n"+
	                            "<select name=\"cancelReason\" id=\"cancelReason\" required=\"required\">\n"+
	                                "<option value=\"\">Select Cancel Reason</option>\n"+
	                                "<option value=\"01\">Payment terms were not accepted</option>\n"+
									"<option value=\"02\">Order/service cancelled</option>\n"+
									"<option value=\"03\">Incorrect vendor contact information</option>\n"+
									"<option value=\"04\">Vendor already existing in SAP</option>\n"+
									"<option value=\"05\">Duplicate invitation sent</option>\n"+
									"<option value=\"06\">Not a US Vendor</option>\n"+
									"<option value=\"07\">Vendor created via Support Central</option>\n"+
									"<option value=\"08\">Other</option>\n"+
	                            "</select>\n"+
	                        "</div>\n"+
			            "</div>\n"+
			            "<div class=\"modal-footer\">\n"+
			            	"<span class=\"footerLabel\">Are you sure you want to continue?</span>\n"+
			                "<a href=\"#\" class=\"btn btn-primary\" id=\"confirmCancel\">Yes</a>\n"+
			                "<a href=\"#\" class=\"btn btn-primary\" id=\"noCancel\">No</a>\n"+
			            "</div>\n"+
			        "</form>\n"+
			    "</div>\n"+	    

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
			        "app.page.inviterView = new app.views.InviterView({ restrictCompanyCode: false });\n"+
			    "</script>\n");
			    
				        if ((request.getServletRequest().getServerName().equalsIgnoreCase("coding.nbcuni.com") || (request.getServletRequest().getServerName().equalsIgnoreCase("vendor.nbcuni.com")))) {
				          response.write("<script src=\"/"+ServerString+"/js/sap_portal_omniture.js\"></script>\n");
		                  response.write("<script  type='text/javascript'>\n"+
								"//Omniture Code start\n"+
								"s.pageName='VRA Invite';\n"+  // Enter Page name to be tracked
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