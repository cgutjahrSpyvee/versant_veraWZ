package com.nbcu.vra;
 
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.vra.tools.tools;
import com.nbcu.vra.ui.components;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IUser;
import com.sapportals.portal.prt.component.*;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class vra_inbox extends AbstractPortalComponent
{

	String ServerString = "com.nbcu.html5_vra";

	public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3"; 	
		String userName = "";
		IUser userObject = request.getUser();
		String userId = userObject.getUniqueName();	
		boolean accessAllowed=false; // ganesh DFCT0017729
    	try {
    		String requestID = request.getParameter("id");
			 // Added Pranesh(04/19/2016) - ENHC0019059
				String userType = "";
				String paymentTermUserType = "";
				boolean userIsJointVenture = false;
				boolean userIsExternalVendor = false;
				boolean userIsInternalEmployeeBuyer = false;
				boolean userIsInternalEmployeeInviter =  false;
		
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
						accessAllowed=true; // ganesh DFCT0017729

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
						accessAllowed=true; // ganesh DFCT0017729

						userType = "2";							
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
			    // End Pranesh(04/19/2016) - ENHC0019059
				
					client.disconnect();
					
					// ganesh DFCT0017729
					if(!accessAllowed)	{
						response.write(components.displayErrorContainer(request,"inviter","The VeRA inviter role is not assigned to your SSO.  You can request access through a CAM access request in Secure Pro. Complete instructions for Requesting the VeRA Inviter Role can be found in the VeRA End User guide posted on the SNAP home page at <a href='http://snap.inbcu.com/documents/vera/'>http://snap.inbcu.com/documents/vera/</a>."));
					} 
					// end code
					else			
					{			
				// Global Alert - Based on iView Property
				String strAlertMessage = profile.getProperty("alertMessage");
				if (strAlertMessage.length()>0)
					throw new Exception (strAlertMessage);		
				
			
	
				response.write(components.displayHeader(request,"inviter") +

			    "<div class=\"content\">\n"+
			    	"<div class=\"container alerts\">\n"+
	                	//Added by Kermel Ruperto 8/4/2014
			    		"<div class=\"alert alert-danger\" id=\"browserWarning\" style=\"display: none;\">\n"+
							"<div id=\"alert-close\">X</div>"+
							"<strong>Warning!</strong> You are using a non compatible browser. Please, update it to a latest version. Select your browser:<br />"+
							"<a href=\"http://windows.microsoft.com/en-us/internet-explorer/download-ie\" target=\"_blank\" style=\"color: #000;\">Interne Explorer</a>&nbsp;&nbsp;"+
							"<a href=\"https://www.google.com/intl/en_us/chrome/browser/\" target=\"_blank\" style=\"color: #000;\">Chrome</a>&nbsp;&nbsp;"+
							"<a href=\"https://www.mozilla.org/en-US/firefox/new/\" target=\"_blank\" style=\"color: #000;\">Firefox</a>&nbsp;&nbsp;"+
							"<a href=\"http://support.apple.com/downloads/#safari\" target=\"_blank\" style=\"color: #000;\">Safari</a>"+
						"</div>\n"+
                    	//End Kermel Ruperto
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
			            "<div class=\"tab-pane active\" id=\"status\">\n"+
			                "<div class=\"container\">\n"+
			                    "<div class=\"row\">\n"+
			                        "<div class=\"span12\">\n"+
			                            "<h2>Request Status</h2>\n"+
			                            "<form>\n"+
			                                "<div class=\"pagination-small pagination-centered pull-left\">\n"+
			                                    "<div class=\"pagination\"></div>\n"+
			                                "</div>\n"+
			                                "<select class=\"pageSize pull-right result-amount\">\n"+
			                                    "<option value=\"5\">5 per page</option>\n"+
			                                    "<option value=\"10\">10 per page</option>\n"+
			                                    "<option value=\"15\">15 per page</option>\n"+
			                                "</select>\n"+
			                                "<div class=\"clearfix\"></div>\n"+
			                                "<div id=\"vendor-accordion\" class=\"container-fluid maintain\">\n"+
			                                    "<div class=\"row-fluid maintain-header\">\n"+
			                                    	//Req#13 START - Code added by AGAMPA 18-Feb-2015 
			                                    	"<div class=\"span1c1 span1\">\n"+
			                                            "<span>Date</span>\n"+
			                                        "</div>\n"+
				                                    "<div class=\"span1c2 span1\">\n"+
			                                            "<span>ID#</span>\n"+
			                                        "</div>\n"+
				                                  /*  "<div class=\"span1c2 span1\">\n"+
			                                            "<span>Invite ID</span>\n"+
			                                        "</div>\n"+	*/
			                                        //Req#13 END
			                                        "<div class=\"span2\">\n"+
			                                            "<span>Source</span>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"span3\">\n"+	
			                                            "<span>Vendor Name</span>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"span1\">\n"+
			                                            "<span>Vendor #</span>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"span2\">\n"+
			                                            "<span>Status</span>\n"+
			                                        "</div>\n"+
			                                        "<div class=\"span1\">\n"+
			                                            "<span></span>\n"+
			                                        "</div>\n"+
			                                    "</div>\n"+
			                                    "<div class=\"row-fluid no-results badge badge-info\" style=\"display:none\">\n"+
			                                        "No results found\n"+
			                                    "</div>\n"+
			                                "</div>\n"+
			                            "</form>\n"+
			                        "</div>\n"+
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

			    "<script type=\"text/template\" id=\"status-row\">\n"+
					"<%\n"+
					"var hasErrorMessages = (typeof(errorMessages) === \"undefined\"? false : true);\n"+
					"%>\n"+
			        "<div class=\"row-fluid item<%-  (hasErrorMessages ? \" vendor-accordion-header\" : \"\") %><%- alt? \" alt\" : \"\" %>\">\n"+
			        	//Req#13 START - Code added by AGAMPA 18-Feb-2015
			            "<div class=\"span1c1 span1\">\n"+
			                "<span class=\"vendor-name\"><%- date %></span>\n"+
			            "</div>\n"+
			            "<div class=\"span1c2 span1\">\n"+
			                "<span class=\"vendor-name\"><%- id %></span>\n"+
			            "</div>\n"+
			            /*"<div class=\"span1c2 span1\">\n"+
			                "<span class=\"vendor-name\"><%- invId %></span>\n"+
			            "</div>\n"+	*/	
			            //Req#13 END
			            "<div class=\"span2\">\n"+
			                "<span class=\"vendor-name\"><%- source %></span>\n"+
			            "</div>\n"+
			            "<div class=\"span3\">\n"+
			                "<span class=\"vendor-name\"><%- name %></span>\n"+
			            "</div>\n"+
			            "<div class=\"span1\">\n"+
			                "<span class=\"vendor-name\"><%- vendorNum %></span>\n"+
			            "</div>\n"+
			            "<div class=\"span2\">\n"+
			                "<%\n"+
			                "var cls = \"\"; \n"+
			                // ENHC0013658 Request Cancelled and Invite Rejected should be displayed in Red
			                "if (status.text == \"Rejected\" || status.text == \"Request Cancelled\" || status.text == \"Invite Rejected\" || status.text == \"W8 Validation Failed\" || status.text == \"IC Reject\" || status.text == \"Pending W8 Submission\") { \n"+
			                    "cls = \"badge-important\";\n"+
			                "} else if (status.text == \"Pending Approval\") {\n"+
			                    "cls = \"badge-success\";\n"+
			                "} else if (status.text == \"Draft\") {\n"+
			                    "cls = \"badge-warning\";\n"+
			                "} else {\n"+
			                    "cls = \"badge-success\";\n"+
			                "}\n"+
			                "%>\n"+
							"<% if(hasErrorMessages){%>\n"+
							"<span class=\"vendor-accordion-trigger badge <%- cls %>\"\n"+
								"data-vendor-accordion-row=\"<%- activationIndex %>\"\n"+
							">\n"+"&nbsp;"+
							"<% }else{ %>\n"+
							"<span class=\"badge <%- cls %>\">\n"+
							"<% } %>\n"+
							"<%- status.text %>\n"+
							"</span>\n"+
			            "</div>\n"+
			            "<div class=\"span1\">\n"+
			            	"<% if ( source != \"Invite\") { %>\n"+
			            	//status.text == \"Approved\" changed by Kermel Ruperto 10-13-2014
			            	//status.text == \"Old Version\" is changed to status.text == \"Request Cancelled\", change by Naga 04/01/15 Enh 14
			                "<% if (status.text == \"In Review\" || status.text == \"Pending Approval\" || status.text == \"Pending Approval\" || status.text == \"Completed\" || status.text == \"Request Cancelled\" || status.text == \"Pending IC Approval\" || status.text == \"Pending IC & Term Approval\" || status.text == \"Waiting (For Term Approval)\" || status.text == \"Pending Mgmt. Approval\" || status.text == \"Pending Term Approval\" || status.text == \"Pending W8 Submission\" || status.text == \"Pending W8 Validation\" || status.text == \"Pending TAX Review\") { %>\n"+
			                "<a class=\"view-vendor btn btn-inverse btn-block\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=2&requestId=<%- id %>\"><i class=\"icon-eye-open\"></i></a>\n"+
			                //status.text == \"Saved\" changed by Kermel Ruperto 10-13-2014
			                "<% } else if (status.text == \"Pending Submission\" || status.text == \"Rejected\" || status.text == \"Failed\" || status.text == \"W8 Validation Failed\" || status.text == \"IC Reject\"){ %>\n"+
			                "<a class=\"view-vendor btn btn-info btn-block\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor?requestType=2&requestId=<%- id %>\"><i class=\"icon-pencil\"></i></a>\n"+
			                "<% } %>\n"+
			                "<% } else { %>\n"+	
			                
			                //Req#1,2,3,4,5  START. Modified by AGAMPA 23-Feb-2015. Added new function.
			                "<% if (btnType == \"D\") { %>\n"+
			                "<a class=\"view-vendor btn btn-inverse btn-block\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_invite?requestId=<%- id %>&mode=D&actkey=<%- actionkey %>\"><i class=\"icon-eye-open\"></i></a>\n"+
			                "<% } else if (btnType == \"E\"){ %>\n"+
			                "<a class=\"view-vendor btn btn-info btn-block\" href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_invite?requestId=<%- id %>&mode=E&actkey=<%- actionkey %>\"><i class=\"icon-pencil\"></i></a>\n"+
			                "<% } %>\n"+
			                "<% } %>\n"+
			                //Req#12,3,4,5 ENDS HERE.
			                
			                
			            "</div>\n"+
			        "</div>\n"+
					
					"<% if(hasErrorMessages) { %>\n"+
					"<div class=\"row-fluid error-message-panel\">\n"+
						"<% for(var i=0;i<errorMessages.length;++i){ %>\n"+
							"<%- errorMessages[i] %>\n"+
							"<% if(i != errorMessages.length - 1){ %>\n"+
								"<hr>\n"+
							"<% } %>\n"+
						"<% } %>\n"+
			        "</div>\n"+
					"<% } %>\n"+
			        
			    "</script>\n"+
			    "<script src=\"/"+ServerString+"/js/jquery.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/extras/modernizr-custom.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/extras/mousepress.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/polyfiller.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/bootstrap.min.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/underscore-min.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/backbone-min.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/jquery.maskedinput.min.js\"></script>\n"+
				"<script src=\"/"+ServerString+"/js/jquery-ui-1.10.4.accordion.min.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/moment.min.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/daterangepicker.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/bootstrap-paginator.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/spritely.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/common.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/status.js\"></script>\n"+
			    "<script src=\"/"+ServerString+"/js/ua-parser.min.js\"></script>\n"+
			    //ua-parser.min.js added by Kermel Ruperto 8/1/2014
			
			    "<script>\n"+
			        "app.page.statusView = new app.views.StatusView();\n"+
			
			    "</script>\n"+
			    //Added by Kermel Ruperto 8/4/2014
			    "<script>\n"+
			    "$(document).ready(function(){\n"+
			    "var browserList = [\"Chrome\", \"IE\", \"Safari\", \"Firefox\", \"Opera\"];\n"+
			    "var browserInfo = new UAParser();\n"+
			    "var version = browserInfo.getBrowser();\n"+
			    "if($.inArray(version, browserList)){\n"+
			    "switch(version.name){\n"+
			    "case browserList[0]:\n"+
			    "if(version.major < 25){\n"+
			    "$(\"#browserWarning\").slideDown(\"slow\").delay(1800000).slideUp(\"slow\");\n"+
			    "$(\".registration\").addClass(\"remove\")"+ //Naga	DFCT0014114
			    "}\n"+
			    "break;\n"+
			    "case browserList[1]:\n"+
			    "if(version.major < 9){\n"+
			    "$(\"#browserWarning\").slideDown(\"slow\").delay(1800000).slideUp(\"slow\");\n"+
			    "$(\".registration\").addClass(\"remove\")"+ //Naga	DFCT0014114
			    "}\n"+
			    "break;\n"+
			    "case browserList[2]:\n"+
				"if(version.major < 5){\n"+
			    "$(\"#browserWarning\").slideDown(\"slow\").delay(1800000).slideUp(\"slow\");\n"+
			    "$(\".registration\").addClass(\"remove\")"+ //Naga DFCT0014114
				"}\n"+
			    "break;\n"+
				"case browserList[3]:\n"+
			    "if(version.major < 12){\n"+
				"$(\"#browserWarning\").slideDown(\"slow\").delay(1800000).slideUp(\"slow\");\n"+
				"$(\".registration\").addClass(\"remove\")"+ //Naga DFCT0014114
			    "}\n"+
				"break;\n"+
			    "case browserList[4]:\n"+
				"if(version.major < 15){\n"+
			    "$(\"#browserWarning\").slideDown(\"slow\").delay(1800000).slideUp(\"slow\");\n"+
			    "$(\".registration\").addClass(\"remove\")"+ //Naga DFCT0014114
				"}\n"+
			    "break;\n"+
				"}\n"+
			    "}\n"+
			    "$(\"#alert-close\").click(function(){$(\"#browserWarning\").stop().slideUp(\"slow\");});\n"+
				"})\n;"+
				""+
			    "</script>\n");
			    //End Kermel Ruperto
				        if ((request.getServletRequest().getServerName().equalsIgnoreCase("coding.nbcuni.com") || (request.getServletRequest().getServerName().equalsIgnoreCase("vendor.nbcuni.com")))) {
				          response.write("<script src=\"/"+ServerString+"/js/sap_portal_omniture.js\"></script>\n");
		                  response.write("<script  type='text/javascript'>\n"+
								"//Omniture Code start\n"+
								"s.pageName='VRA Inbox';\n"+  // Enter Page name to be tracked
								"s.server='"+request.getServletRequest().getServerName()+"';\n"+
								"s.channel='';\n"+
								"s.pageType='';\n"+
								"s.prop1= "+userName+";\n"+ //username SSO to be tracked
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
				//response.write(e1.getLocalizedMessage());	
				response.write(components.displayErrorContainer(request,"mailbox",e1.getMessage()));
			}

		} catch (Exception e) {
			
			e.printStackTrace();

		} 
    }

}