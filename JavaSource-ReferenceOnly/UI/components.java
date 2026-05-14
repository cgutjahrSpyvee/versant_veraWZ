package com.nbcu.vra.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IUser;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class components {
	
		static String ServerString = "com.nbcu.html5_vra";
		
		public static String displayHeader(IPortalComponentRequest request, String cssFileName){
		  return  displayHeaderInt(request,cssFileName);
		}

	    public static String displayErrorContainer(IPortalComponentRequest request, String cssFileName, String errorMessage){
	    	return displayErrorContainerInt(request, cssFileName, errorMessage);
	    }
		
		public static String displayDueInDays(String dueInDate){
			return displayDueInDaysInternal(dueInDate);
		}
		
		public static String displayPrettyDate(String dueInDate){
			return displayPrettyDateInternal(dueInDate);
		}

		
	    private static String displayHeaderInt(IPortalComponentRequest request,String cssFileName){    	
			IUser userObject = request.getUser();
			String WFSystemAlias = "SAP_R3";			
	    	String userId = userObject.getUniqueName();
			String userRealName = userObject.getFirstName()+" "+userObject.getLastName(); 
			String mode = request.getParameter("mode");							// ENHC0019060
			String requestId = request.getParameter("requestId");				// ENHC0019060

			boolean userIsExternalVendor = false;
			boolean userIsInternalEmployeeBuyer = false;
			boolean userIsInternalEmployeeInviter =  false;
		
			//Get User Roles
			//get a client service
			IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);

			JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);

			// connect to SAP system
			 client.connect();
			
			IRepository m_RepositoryRoles = JCO.createRepository("repository", client);
			IFunctionTemplate Z_SF_I477_GET_USER_ROLES = m_RepositoryRoles.getFunctionTemplate("Z_SF_I477_GET_USER_ROLES");

			JCO.Function functionRoles = new JCO.Function(Z_SF_I477_GET_USER_ROLES);
			JCO.ParameterList importListRoles = functionRoles.getImportParameterList();
			importListRoles.setValue(userId, "I_SSO_ID");
 
			client.execute(functionRoles);

			JCO.Table retUserRoles =  functionRoles.getTableParameterList().getTable("T_ROLES");	
			
			client.disconnect();
			
			for(int i = 0; i < retUserRoles.getNumRows(); i++) {
				if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("DECC:FI_AP_AUTO_VND_REGISTER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))) {
					userIsExternalVendor = true;
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeBuyer = true;
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;	
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeInviter = true;
				}
				// Begin of Insert by Naga ENHC0016164
				else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
					}
				else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeBuyer = true;					
				}				
				// End of Insert by Naga
				retUserRoles.nextRow();
			}	
			
			
	    	String result="<!DOCTYPE html>\n"+
	    	"<!--[if IE 7 ]> <html lang=\"en\" class=\"ie7\"> <![endif]-->\n"+
	 	 	"<!--[if IE 8 ]> <html lang=\"en\" class=\"ie8\"> <![endif]-->\n"+
	 	 	"<!--[if IE 9 ]> <html lang=\"en\" class=\"ie9\"> <![endif]-->\n"+
	    	"<!--[if (gt IE 9)|!(IE)]><!--> <html lang=\"en\"> <!--<![endif]-->\n"+
			"<head>\n"+
			    "<title>NBCUniversal</title>\n"+
			    "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">\n"+			
			    "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n"+	
			    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"+
			    "<link rel=\"shortcut icon\" href=\"/"+ServerString+"/img/favicon.ico\" type=\"image/x-icon\" />\n"+
			    "<link rel=\"apple-touch-icon\" href=\"/"+ServerString+"/img/SNAP-icon-57x57.png\" />\n"+	
			    "<link rel=\"apple-touch-icon\" sizes=\"72x72\" href=\"/"+ServerString+"/img/SNAP-icon-72x72.png\" />\n"+	
			    "<link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"/"+ServerString+"/img/SNAP-icon-114x114.png\" />\n"+
			    "<link href=\"/"+ServerString+"/css/bootstrap.metro.min.css\" rel=\"stylesheet\" media=\"screen\">\n"+
			    "<link href=\"/"+ServerString+"/css/bootstrap-responsive.min.css\" rel=\"stylesheet\" media=\"screen\">\n"+
			    "<link href=\"/"+ServerString+"/css/font-awesome.css\" rel=\"stylesheet\">\n"+
			    "<link href=\"/"+ServerString+"/css/daterangepicker.css\" rel=\"stylesheet\" media=\"screen\">\n"+			    
			    "<link href=\"/"+ServerString+"/css/"+cssFileName+".css\" rel=\"stylesheet\" media=\"screen\">\n"+
			"</head>\n"+

			"<body class=\"registration\">\n"+

			        "<div id=\"spinner\" class=\"spinner-small\">\n"+
			        	"<!--[if IE]><iframe class=\"cover\" src=\"#\" style=\"border:none\"></iframe><![endif]-->\n"+
			            "<div></div>\n"+
			        "</div>\n"+

			        "<div class=\"navbar navbar-fixed-top\">\n"+
			            "<div class=\"navbar-inner\">\n"+
			                "<div class=\"container\">\n"+
			                    "<a class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\n"+
			                        "<span class=\"icon-bar\"></span>\n"+
			                        "<span class=\"icon-bar\"></span>\n"+
			                        "<span class=\"icon-bar\"></span>\n"+
			                    "</a>\n"+
			                    "<a class=\"brand\" href=\"#\"></a>\n"+
			                    "<div class=\"nav-collapse collapse\">\n"+
			                        "<ul class=\"nav pull-right\">\n"+
			                            "<li>\n"+
			                                "<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">\n"+
			                                    "<i class=\"icon-user\"></i>"+userRealName+"\n"+
			                                    "<b class=\"caret\"></b>\n"+
			                                "</a>\n"+
			                            "</li>\n"+
			                            "<li><a href=\"/irj/servlet/prt/portal/prtroot/com.sap.portal.navigation.masthead.LogOutComponent?logout_submit=true\"><i class=\"icon-lock\"></i>Logout</a></li>\n"+
			                        "</ul>\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n";
				            	// Begin of Modification by Naga 990
				            	// When in Modal Window, do not display the buttons
				            	if(mode!=null&&mode.equalsIgnoreCase("Approval")){
				            		requestId = requestId.replaceFirst("^0+(?!$)", "");
				            		result = result +
				            		"<div class=\"header approve\">\n"+
							        	"<div class=\"container\">\n"+
							            	"<div class=\"row\">\n"+
							            			"<div class=\"span8\">\n"+
					            		 				"<h2 class=\"pull-left\"><i class=\"icon-reply returnIcon tip\" title=\"Back to Inbox\"></i> Vendor Details</h2>"+
					            						"<ul class=\"request-id pull-right\">"+
					            						  "<label class=\"header-request-label\">Request / Invite #</label>"+	
					            						  "<span class=\"badge\">"+requestId+"</span>"+
					            						"</ul>\n"+
				            						"</div>\n";
				            	}else{
				            		result = result +
				            	"<div class=\"header\">\n"+
						        	"<div class=\"container\">\n"+
						           	"<div class=\"row\">\n"+				            		
					                "<div class=\"span8\">\n"+
				                    "<ul class=\"nav nav-pills\">\n"+
				                        "<li>\n"+	
				                            "<a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.inbox_vra\">Status</a>\n"+
				                        "</li>\n"+
				                        "<!-- userIsInternalEmployeeInviter:"+userIsInternalEmployeeInviter+" : userIsInternalEmployeeBuyer:"+userIsInternalEmployeeBuyer+" : userIsExternalVendor:"+userIsExternalVendor+" -->";
				                        if (userIsExternalVendor){
											// Remove Maintain for Vendor DFCT0013688 By Naga
					                    	result = result+ "<li>\n"+
					                            "<a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor\">Maintain</a>\n"+
					                        "</li>\n";				                 
											// End of Remove 			                        	
				                        }
	    								if (userIsInternalEmployeeBuyer || userIsInternalEmployeeInviter){
					                    	result = result+ "<li>\n"+
					                            "<a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.vendor_invite\">Invite</a>\n"+
					                        "</li>\n";				                        	
				                        }
				                        if (userIsInternalEmployeeBuyer || userIsInternalEmployeeInviter){				                        
					                        result = result+ "<li>\n"+
					                            "<a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.register_behalf\">Register</a>\n"+
					                        "</li>\n"+
					                        // Remove Maintain for Non Vendors also by Naga DFCT0013688
					                        
					                        "<li>\n"+
					                            "<a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.maintain_vendor_search\">Maintain</a>\n"+
					                        "</li>\n"; 
					                        // End Remove End Un remove 
				                        }
				                     result = result+ "</ul>\n"+
				                "</div>\n";
				            	}
				                
				            	result = result +
				            "</div>\n"+
				        "</div>\n"+
				    "</div>\n";	    	
	    	return result;
	    }
	    
	    private static String displayErrorContainerInt(IPortalComponentRequest request, String cssFileName, String errorMessage){
	    	
	    	String result = components.displayHeader(request,cssFileName)+
				"<div class=\"header\">\n"+
					"<div class=\"container\">\n"+
						"<h3>\n"+
							"System Message\n"+
						"</h3>\n"+
					"</div>\n"+
				"</div>\n"+			
			    "<div class='content'>\n"+
		        	"<div class='container'>\n"+						
		        		"<h3>"+errorMessage+"</h3>\n"+
		        	"</div>\n"+
		        "</div>\n"+
				"<div class=\"beauty\">\n"+
					"<div class=\"background\"></div>\n"+
				"</div>\n"+					
				"<div class=\"circle-container\">\n"+
					"<div class=\"circles\">\n"+
						"<div></div>\n"+
						"<div></div>\n"+
						"<div></div>\n"+
						"<div></div>\n"+
						"<div></div>\n"+
						"<div></div>\n"+
					"</div>\n"+
				"</div>\n"+         
				"<div class=\"footer\">\n"+
				        "<div class=\"container\">\n"+
				            "(c) NBCUniversal\n"+
				        "</div>\n"+
			    "</div>\n"+        
						 "<script src=\"https://code.jquery.com/jquery.js\"></script>\n"+
					     "<script src=\"/"+ServerString+"/js/bootstrap.min.js\"></script>\n"+
				"</body>\n"+
			"</html>\n";
			
	    	return result;
	    }
	    
	    private static String displayDueInDaysInternal(String dueInDate){
	 
	    	String returnedNumDays = "999";
	    	
	    	try {
	    		
		    	Calendar cal1 = new GregorianCalendar();
		    	Calendar cal2 = new GregorianCalendar();
	
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			    Date date1 = new Date();
			    Date date = sdf.parse(dueInDate);
			    cal2.setTime(date);
			    Date date2 = cal2.getTime();
	
			    long returnedNumDaysLong = ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
		    
			    returnedNumDays = returnedNumDaysLong+"";
			    
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}

	    	return returnedNumDays;
	    }
	    
	    private static String displayPrettyDateInternal(String dateString){
	 
	    	String returnedDate = "01-01-1900";
	    	
	    	try {
	    		
				final String NEW_FORMAT = "MM-dd-yyyy";
				final String OLD_FORMAT = "yyyy-MM-dd";
				
				String oldDateString = dateString;
				
				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d = sdf.parse(oldDateString);
				sdf.applyPattern(NEW_FORMAT);
				returnedDate = sdf.format(d);
				
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}

	    	return returnedDate;
	    }	    
	    
}