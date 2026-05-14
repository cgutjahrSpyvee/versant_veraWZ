package com.nbcu.vra;
 
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.vra.tools.*;
import com.nbcu.vra.ui.components;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IRole;
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class registration extends AbstractPortalComponent
{
	String ServerString = "com.nbcu.html5_vra";

	public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		String userName = "";
    	try {
    		
			String requestID = request.getParameter("id");		
			// Request Type - invite | vendor
			String requestType = request.getParameter("type");
			String hidePrimaryAddress = "hide";
			String hideNonUSState = "";
			String legalName = "";
			String invoicingName = "";
			String minorityCode = "";
			String industryCode = "";
			String acceptPO = "";
			String poEmailAddress = "";
			String environmentQuestion = "";
			String orgFocus = "";
			String ageGroup = "";
			String geoArea = "";
			String typeOfArea = "";
			String FCPADesignation = "";
			String primaryAddress1 = "";
			String primaryAddress2 = "";
			String primaryAddress3 = "";
			String primaryCountry = "";
			String primaryCity = "";
			String primaryState = "";
			String primaryZip = "";
			String taxID = "";
			String socialSecurityNumber = "";
			String independentContractor = "";
			String percentCATax = "";
			String terms = "";
			String bankingPrimaryCountry = "";
			String bankingPrimaryRoutingBSB = "";
			String bankingPrimaryBankAccount = "";
			String bankingPrimaryEmailContact = "";
			String bankingPrimarySWIFTAcocunt = "";
			String bankingPrimaryIBAN = "";
			String bankingPrimaryAcceptUSCurrency = "";
			boolean environmentCodeofConduct = false;
			String environmentCodeofConductComment = "";			
			boolean envrionmentSustainability = false;
			String envrionmentSustainabilityComment = "";			
			boolean envrionmentSocialHealth = false;
			String envrionmentSocialHealthComment = "";			
			boolean environmentOccupational = false;
			String environmentOccupationalComment = "";			
			String diversityBoardofDirectorsAMERICANINDIANALASKAN = "";
			String diversityBoardofDirectorsASIANPACIFICISLANDER = "";
			String diversityBoardofDirectorsBLANKAFRICANAMERICAN = "";
			String diversityBoardofDirectorsHISPANICLATINO = "";
			String diversityBoardofDirectorsWHITE = "";
			String diversityBoardofDirectorsWOMEN = "";
			String diversitySrStaffAMERICANINDIANALASKAN = "";
			String diversitySrStaffASIANPACIFICISLANDER = "";
			String diversitySrStaffBLANKAFRICANAMERICAN = "";
			String diversitySrStaffHISPANICLATINO = "";
			String diversitySrStaffWHITE = "";
			String diversitySrStaffWOMEN = "";	
			String diversityMembersAMERICANINDIANALASKAN = "";
			String diversityMembersASIANPACIFICISLANDER = "";
			String diversityMembersBLANKAFRICANAMERICAN = "";
			String diversityMembersHISPANICLATINO = "";
			String diversityMembersWHITE = "";
			String diversityMembersWOMEN = "";		
			String diversityMembersGAY= "";				
			String diversityMembersVETRAN= "";	
			
			// Drop Down Arrays
			String[][] arrayMinorityCode = tools.setupMinorityCodeArray();
			String[][] arrayIndustryCode = tools.setupIndustryCodeArray();
			String[][] arrayCountryCode = tools.setupCountryCodeArray();
			String[][] arrayRecipientType = tools.setupRecipientTypeArray();
			
			// JCO Vars
			JCO.Table retCT_LFA1 = null;
			JCO.Table retCT_REQ = null;
			JCO.Table retCT_ANSWER = null;
			JCO.Table retCT_BNKA = null;
			JCO.Table retCT_LFB1 = null;
			JCO.Table retCT_ADR6 = null;
			JCO.Table retCT_IBAN = null;			
			JCO.Table retCT_TBCN21 = null;
			JCO.Table retCT_KNVK = null;
			
	    	HttpServletResponse resp = request.getServletResponse(true);
			PrintWriter response = resp.getWriter();
			resp.setContentType("text/html;charset=utf-8");
			
			try {
	
				IUser userObject = request.getUser();
				userName = userObject.getUniqueName();
				boolean userHasRequiredRole = false;
				
				Iterator userRoles = userObject.getRoles(true);
				while (userRoles.hasNext()) {
					String rolestr = (String) userRoles.next();
					IRole r = UMFactory.getRoleFactory().getRole(rolestr);
					String root = r.getUniqueName().toUpperCase();
					if (root.contains("CAM"))
						userHasRequiredRole = true;	
				}
				
				//if (!userHasRequiredRole)
				//	throw new Exception ("Please note you do not have the required role. Please contact system support");			
				
	
				if (requestID != null) {
					//get a client service
					IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
	
					JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
	
					// connect to SAP system
					 client.connect();
	
					// Item Query
					IRepository m_Repository = null;
					IFunctionTemplate SAP_FUNCTION = null;
	
					m_Repository = JCO.createRepository("repository", client);
	
					SAP_FUNCTION = m_Repository.getFunctionTemplate("Z_SFI_I510_VRA_VENDISP");
	
					JCO.Function function = new JCO.Function(SAP_FUNCTION);
					JCO.ParameterList importList = function.getImportParameterList();
					importList.setValue(requestID, "ADMIN_SSO");			
					importList.setValue("0", "SOURCE");
	 
					client.execute(function);	
					
					retCT_LFA1 = function.getExportParameterList().getTable("CT_LFA1");
					retCT_REQ = function.getExportParameterList().getTable("CT_REQ");
					retCT_ADR6 = function.getExportParameterList().getTable("CT_ADR6");
					retCT_ANSWER = function.getExportParameterList().getTable("CT_ANSWER");
					retCT_LFB1 = function.getExportParameterList().getTable("CT_LFB1");;
					retCT_IBAN = function.getExportParameterList().getTable("CT_IBAN");;			
					retCT_TBCN21 = function.getExportParameterList().getTable("CT_TBCN21");
					retCT_BNKA = function.getExportParameterList().getTable("CT_TBCN21");
					retCT_KNVK = function.getExportParameterList().getTable("CT_KNVK");
					
					client.disconnect();						
					
					
					// Set up Primary Address
						if(!retCT_LFA1.isEmpty()){
							hidePrimaryAddress = "";
							
							legalName = retCT_LFA1.getString("NAME1");
							invoicingName = retCT_LFA1.getString("NAME2");
							primaryAddress1 = retCT_LFA1.getString("STRAS");
							primaryAddress2 = retCT_LFA1.getString("STR_SUPPL1");
							primaryAddress3 = retCT_LFA1.getString("STR_SUPPL2");
							primaryCountry = retCT_LFA1.getString("LAND1");
							primaryCity = retCT_LFA1.getString("ORT01");
							primaryState = retCT_LFA1.getString("REGIO");
							primaryZip = retCT_LFA1.getString("PSTLZ");
							socialSecurityNumber = retCT_LFA1.getString("STCD1");
							taxID = retCT_LFA1.getString("STCD2");	
						}
	
						if(!retCT_REQ.isEmpty()){
							minorityCode = retCT_REQ.getString("VRA_MINDK");
							industryCode = retCT_REQ.getString("VRA_BRSCH");
							acceptPO = retCT_REQ.getString("ZZSF_VRA_PORECV");
							poEmailAddress = retCT_REQ.getString("SMTP_ADDR");
						}
						
						// Check to see if Primary Address is in the US
						if (retCT_LFA1.getString("LAND1").equalsIgnoreCase("US")){
							hideNonUSState = "style=\"display: none;\"";	
						} 
					
						// Set up the Environment Questions
						if (!retCT_ANSWER.isEmpty()){
							int maxRows = retCT_ANSWER.getNumRows();
				            for(int i = 0; i < maxRows; i++) {

				             //Set up Environment
			            		if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00001")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("1")){
					            			environmentCodeofConduct = true;
					            		}
					            		environmentCodeofConductComment = retCT_ANSWER.getString("ACOMMENT");	
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){	
					            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("1")){
					            			envrionmentSustainability = true;
					            		}
					            		envrionmentSustainabilityComment = retCT_ANSWER.getString("ACOMMENT");	
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){	
					            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("1")){
					            			envrionmentSocialHealth = true;
					            		}	
					            		envrionmentSocialHealthComment = retCT_ANSWER.getString("ACOMMENT");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("1")){
					            			environmentOccupational = true;
					            		}
					            		environmentOccupationalComment = retCT_ANSWER.getString("ACOMMENT");
					            	}   
					            	
				            	//Set up DIVERSITY BOARD OF DIRECTORS
					            } else	if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00003")){				            		
						            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
						            		diversityBoardofDirectorsAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
						            		diversityBoardofDirectorsASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
						            		diversityBoardofDirectorsBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
						            		diversityBoardofDirectorsHISPANICLATINO = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
						            		diversityBoardofDirectorsWHITE = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
						            		diversityBoardofDirectorsWOMEN = retCT_ANSWER.getString("ANSWER");
						            	} 

				            	//Set up DIVERSITY SENIOR STAFF						            	
					            } else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00004")){				            		
						            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
						            		diversitySrStaffAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
						            		diversitySrStaffASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
						            		diversitySrStaffBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
						            		diversitySrStaffHISPANICLATINO = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
						            		diversitySrStaffWHITE = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
						            		diversitySrStaffWOMEN = retCT_ANSWER.getString("ANSWER");
						            	} 
				           
						           //Set up DIVERSITY SENIOR STAFF		
					            	} else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00005")){				            		
						            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
						            		diversityMembersAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
						            		diversityMembersASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
						            		diversityMembersBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
						            		diversityMembersGAY = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
						            		diversityMembersHISPANICLATINO = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
						            		diversityMembersWHITE = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00007")){					            		
						            		diversityMembersWOMEN = retCT_ANSWER.getString("ANSWER");
						            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00008")){					            		
						            		diversityMembersVETRAN = retCT_ANSWER.getString("ANSWERS");
						            	}			            		
					            	}
				            	
				            }
						} 
						
						//Set up Banking Information
						if(!retCT_BNKA.isEmpty()){

/*							primaryBankingAddress1 = retCT_LFA1.getString("STRAS");
							primaryAddress2 = retCT_LFA1.getString("STR_SUPPL1");
							primaryAddress3 = retCT_LFA1.getString("STR_SUPPL2");
							bankingPrimaryCountry = retCT_LFA1.getString("LAND1");
							primaryCity = retCT_LFA1.getString("ORT01");
							primaryState = retCT_LFA1.getString("REGIO");
							primaryZip = retCT_LFA1.getString("PSTLZ");
							socialSecurityNumber = retCT_LFA1.getString("STCD1");
							taxID = retCT_LFA1.getString("STCD2")*/;	
						}						
				}
								
				response.write(components.displayHeader(request,"vendor") +
				        
						"<form id=\"main\" action=\"../json/saveVendor.json\" method=\"POST\">\n"+
						"<input type=\"hidden\" name=\"vendorId\" value=\""+requestID+"\"/>\n"+
						"<input id=\"documentType\" type=\"hidden\" name=\"documentType\" value=\"12\"/>\n"+

				        "<div class=\"content\">\n"+

				            "<div class=\"container alerts\">\n"+
				            "</div>\n"+

				            "<div class=\"container\">\n"+
				                "<div class=\"row\">\n"+
				                    "<div class=\"span8\">\n"+
				                     	"<div class=\"alert alert-info\" id=\"lockedAlert\">\n"+
                            				"You cannot edit the form while it is in approval.\n"+
                            			"</div>\n"+
				                        "<ul class=\"nav nav-tabs\" id=\"wizard\">\n"+
				                            "<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"termsTab\"><i class=\"icon-ok\"></i>Terms</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"bankingTab\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"contactsTab\"><i class=\"icon-ok\"></i>Contacts</a></li>\n"+
				                        "</ul>\n"+
				                        "<div class=\"tab-content\">\n"+
				                        	"<div class=\"locked-overlay\"></div>\n"+
				                            "<div class=\"tab-pane active fade in\" id=\"tab1\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<h1>Vendor Name</h1>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"span6\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label\">Legal Name <i class=\"icon-question-sign tip\" title=\"Name Line 1 on tax form\"></i></label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input required class=\"input-block-level\" type=\"text\" name=\"legalName\" placeholder=\"Legal Name\" value=\""+legalName+"\">\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"span6\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label\">Invoicing Name <i class=\"icon-question-sign tip\" title=\"Name Line 2 on tax form\"></i></label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input class=\"input-block-level\" type=\"text\" name=\"invoicingName\" placeholder=\"Invoicing Name\" value=\""+invoicingName+"\">\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<h1>Additional Information</h1>\n"+
				                                        "<div class=\"additional-info\">\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Minority Code</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"minorityCode\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n");
																			for (int x = 0; x < arrayMinorityCode.length; x++) {
																					if (arrayMinorityCode[x][0].equalsIgnoreCase(minorityCode)){
																						response.write("<option value=\""+arrayMinorityCode[x][0]+" selected\">"+arrayMinorityCode[x][1]+"</option>");				
																					} else {
																						response.write("<option value=\""+arrayMinorityCode[x][0]+"\">"+arrayMinorityCode[x][1]+"</option>");											
																					}
																			}
				                                                    response.write("</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Industry Code</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"industryCode\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n");
																			for (int x = 0; x < arrayIndustryCode.length; x++) {
																					if (arrayIndustryCode[x][0].equalsIgnoreCase(industryCode)){
																						response.write("<option value=\""+arrayIndustryCode[x][0]+" selected\">"+arrayIndustryCode[x][1]+"</option>");
																					} else {
																						response.write("<option value=\""+arrayIndustryCode[x][0]+"\">"+arrayIndustryCode[x][1]+"</option>");
																					}																				
																			}
				                                                    response.write("</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n");
				                                            	if (acceptPO.equalsIgnoreCase("x")){
							                                         response.write("<div class=\"control-group\">\n"+		                                                    
						                                                "<label class=\"control-label checkbox\">\n"+
						                                                    "Accept P.O.?\n"+
						                                                    "<input type=\"checkbox\" id=\"poCheckbox\" name=\"acceptPO\" checked/></label>\n"+
						                                                "<div class=\"controls\">\n"+
						                                                    "<div class=\"input-prepend po-email\">\n"+
						                                                        "<div class=\"disabled-overlay\" style=\"display: none;\"></div>\n"+
						                                                        "<span class=\"add-on tip\" title=\"Email Address for P.O.\"><i class=\"icon-envelope\"></i></span>\n"+
						                                                        "<input type=\"email\" name=\"poEmail\" placeholder=\"P.O. Email Address\">\n"+
						                                                    "</div>\n"+
						                                                "</div>\n"+
						                                            "</div>\n");				                                            		
				                                            	} else {
							                                         response.write("<div class=\"control-group\">\n"+			                                                    
						                                                "<label class=\"control-label checkbox\">\n"+
						                                                    "Accept P.O.?\n"+
						                                                    "<input type=\"checkbox\" id=\"poCheckbox\" name=\"acceptPO\" /></label>\n"+
						                                                "<div class=\"controls\">\n"+
						                                                    "<div class=\"input-prepend po-email\">\n"+
						                                                        "<div class=\"disabled-overlay\" style=\"display: block;\"></div>\n"+
						                                                        "<span class=\"add-on tip\" title=\"Email Address for P.O.\"><i class=\"icon-envelope\"></i></span>\n"+
						                                                        "<input type=\"email\" name=\"poEmail\" placeholder=\"P.O. Email Address\" value=\""+poEmailAddress+"\">\n"+
						                                                    "</div>\n"+
						                                                "</div>\n"+
						                                            "</div>\n");				                                            		
				                                            	}
				                                          response.write("<div class=\"clearfix\"></div>\n"+
				                                        "</div>\n"+
				                                        "<h1>Environment</h1>\n"+
				                                        "<div class=\"accordion questions\" id=\"environmentalQuestions\">\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<span class=\"badge badge-inverse\">1</span>\n"+
				                                                "<div class=\"accordion-body collapse in\">\n"+
				                                                    "<div class=\"accordion-question\">\n"+
				                                                        "Has the NBCUniversal Supplier developed a Code of Conduct  based on the International Labor Organization, United Nations Global Compact and other internationally recognized standards, to clarify company expectations in the areas of labor practices, health and safety, environmental management and business integrity?\n"+
				                                                        "<div class=\"describe\">\n"+
				                                                            "Describe\n"+
				                                                          "<textarea name=\"basicQ1Describe\" class=\"input-block-level\"></textarea>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                        "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                        "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                        "<div class=\"hidden-form-elements\">\n"+
				                                                            "<input type=\"radio\" name=\"basicQ1\" value=\"yes\" />\n"+
				                                                            "<input type=\"radio\" name=\"basicQ1\" value=\"no\" />\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<span class=\"badge badge-inverse\">2</span>\n"+
				                                                "<div class=\"accordion-body collapse in\">\n"+
				                                                    "<div class=\"accordion-question\">\n"+
				                                                        "Has  the NBCUniversal  Supplier established Environmental Sustainability improvement goals and objectives to manage the design and packaging of products;  reduce greenhouse gas emissions, waste and water usage?\n"+
				                                                        "<div class=\"describe\">\n"+
				                                                            "Describe\n"+
				                                                          "<textarea name=\"basicQ2Describe\" class=\"input-block-level\"></textarea>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                        "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                        "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                        "<div class=\"hidden-form-elements\">\n"+
				                                                            "<input type=\"radio\" name=\"basicQ2\" value=\"yes\" />\n"+
				                                                            "<input type=\"radio\" name=\"basicQ2\" value=\"no\" />\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<span class=\"badge badge-inverse\">3</span>\n"+
				                                                "<div class=\"accordion-body collapse in\">\n"+
				                                                    "<div class=\"accordion-question\">\n"+
				                                                        "Does the NBCUniversal Supplier maintain a social, health, safety and environmental compliance assurance audit program to monitor and verify  performance within company operations and in the supply chain?\n"+
				                                                        "<div class=\"describe\">\n"+
				                                                            "Describe\n"+
				                                                          "<textarea name=\"basicQ3Describe\" class=\"input-block-level\"></textarea>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                        "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                        "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                        "<div class=\"hidden-form-elements\">\n"+
				                                                            "<input type=\"radio\" name=\"basicQ3\" value=\"yes\" />\n"+
				                                                            "<input type=\"radio\" name=\"basicQ3\" value=\"no\" />\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<span class=\"badge badge-inverse\">4</span>\n"+
				                                                "<div class=\"accordion-body collapse in\">\n"+
				                                                    "<div class=\"accordion-question\">\n"+
				                                                        "Does the NBCUniversal Supplier maintain an Occupational, Health and Safety, Social and/or Environmental Management System that is certified to ISO 18000, ISO 8000, ISO 14000,  REACH, RoHS and/or WEEE?\n"+
				                                                        "<div class=\"describe\">\n"+
				                                                            "Describe\n"+
				                                                          "<textarea name=\"basicQ4Describe\" class=\"input-block-level\"></textarea>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                        "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                        "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                        "<div class=\"hidden-form-elements\">\n"+
				                                                            "<input type=\"radio\" name=\"basicQ4\" value=\"yes\" />\n"+
				                                                            "<input type=\"radio\" name=\"basicQ4\" value=\"no\" />\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<h1>Diversity</h1>\n"+
				                                        "<div class=\"diversity-info\">\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Organization Focus</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"organizationFocus\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n"+
				                                                        "<option>Organization Focus</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Age Group</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"ageGroup\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n"+
				                                                        "<option>Age Group</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Geographical Area</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"geographicalArea\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n"+
				                                                        "<option>Geographical Area</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">Type of Area</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"typeOfArea\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n"+
				                                                        "<option>Type of Area</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"control-group\">\n"+
				                                                "<label class=\"control-label\">FCPA Designation</label>\n"+
				                                                "<div class=\"controls\">\n"+
				                                                    "<select name=\"fcpaDesignation\" required>\n"+
				                                                        "<option value=\"\">Select One</option>\n"+
				                                                        "<option>FCPA Designation Code</option>\n"+
				                                                    "</select>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"clearfix\"></div>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"control-group\">\n"+
				                                            "<label class=\"control-label\">Description of Organization</label>\n"+
				                                            "<div class=\"controls\">\n"+
				                                                "<textarea required name=\"organizationDescription\" class=\"input-block-level diversity-textarea\"></textarea>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<label>For this section, please indicate which groups listed respresent sections of your company. Please place a 0 in the fields that are not represented in your organization.</label>\n"+
				                                        "<div class=\"accordion questions diversity\" id=\"diversity\">\n"+
				                                            "<div class=\"accordion-group diveristy-start\">\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"diversity-group\">\n"+
				                                                        "<label>\n"+
				                                                            "Board of Directors <i class=\"icon-ok diversity-complete\"></i>\n"+
				                                                        "</label>\n"+
				                                                        "<a class=\"btn btn-mini btn-primary diversity-begin\" data-toggle=\"collapse\" href=\"#BoardOfDirectors\" data-parent=\"#diversity\">Begin</a>\n"+
				                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#BoardOfDirectors\" data-parent=\"#diversity\" id=\"TestDiversity\"><i class=\"icon-pencil\"></i>Edit</a>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-body collapse\" id=\"BoardOfDirectors\">\n"+
				                                                    "<div class=\"diversity-progress\">\n"+
				                                                        "<div class=\"progress progress-ethnic\">\n"+
				                                                            "<div class=\"bar bar-inverse american-indian\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse pacific-islander\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse african-american\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse hispanic-latino\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse ethnic-white\" style=\"width: 0\"></div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"progress progress-women\">\n"+
				                                                            "<div class=\"bar bar-inverse women\" style=\"width: 0;\"></div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"diversity-form\">\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">American Indian/Alaskan</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiveristyAmericanIndian\" data-diversity=\"american-indian\" class=\" ethnicity-input \" value=\""+diversityBoardofDirectorsAMERICANINDIANALASKAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Asian/Pacific Islander</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiversityPacificIslander\" class=\"ethnicity-input\" data-diversity=\"pacific-islander\" value=\""+diversityBoardofDirectorsASIANPACIFICISLANDER+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Black/African American</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiversityAfricanAmerican\" data-diversity=\"african-american\" class=\"ethnicity-input \" value=\""+diversityBoardofDirectorsBLANKAFRICANAMERICAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Hispanic/Latino</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiversityHispanic\" data-diversity=\"hispanic-latino\" class=\"ethnicity-input \" value=\""+diversityBoardofDirectorsHISPANICLATINO+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">White</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiversityWhite\" data-diversity=\"ethnic-white\" class=\" ethnicity-input \" value=\""+diversityBoardofDirectorsWHITE+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Women</label>\n"+
				                                                                "<div class=\"controls gender\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"bodDiversityWomen\" class=\"women\" value=\""+diversityBoardofDirectorsWOMEN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"clearfix\"></div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"diversity-finish\">\n"+
				                                                        "<a class=\"btn btn-primary pull-right\">Next <i class=\"icon-angle-right\"></i></a>\n"+
				                                                        "<div class=\"clearfix\"></div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"diversity-group\">\n"+
				                                                        "<label>\n"+
				                                                            "Senior Staff <i class=\"icon-question-sign tip\" title=\"Executive Director/President/CEO, etc. and their direct reports\"></i><i class=\"icon-ok diversity-complete\"></i>\n"+
				                                                        "</label>\n"+
				                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#SeniorStaff\" data-parent=\"#diversity\"><i class=\"icon-pencil\"></i>Edit</a>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-body collapse\" id=\"SeniorStaff\">\n"+
				                                                    "<div class=\"diversity-progress\">\n"+
				                                                        "<div class=\"progress progress-ethnic\">\n"+
				                                                            "<div class=\"bar bar-inverse american-indian\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse pacific-islander\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse african-american\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse hispanic-latino\" style=\"width: 0\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse ethnic-white\" style=\"width: 0\"></div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"progress progress-women\">\n"+
				                                                            "<div class=\"bar bar-inverse women\" style=\"width: 0;\"></div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"diversity-form\">\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">American Indian/Alaskan</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"ssDiversityAmericanIndian\" data-diversity=\"american-indian\" class=\" ethnicity-input\" value=\""+diversitySrStaffAMERICANINDIANALASKAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Asian/Pacific Islander</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"ssDiveristyPacificIslander\" class=\"ethnicity-input\" data-diversity=\"pacific-islander\" value=\""+diversitySrStaffASIANPACIFICISLANDER+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Black/African American</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"ssDiversityAfricanAmerican\" data-diversity=\"african-american\" class=\"ethnicity-input \" value=\""+diversitySrStaffBLANKAFRICANAMERICAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Hispanic/Latino</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"ssDiversityHispanic\" data-diversity=\"hispanic-latino\" class=\"ethnicity-input \" value=\""+diversitySrStaffHISPANICLATINO+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">White</label>\n"+
				                                                                "<div class=\"controls ethnic\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"ssDiversityWhite\" data-diversity=\"ethnic-white\" class=\" ethnicity-input \" value=\""+diversitySrStaffWHITE+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Women</label>\n"+
				                                                                "<div class=\"controls gender\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" class=\"women\" name=\"ssDiversityWomen\" value=\""+diversitySrStaffWOMEN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"clearfix\"></div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"diversity-finish\">\n"+
				                                                        "<a class=\"btn btn-primary pull-right\">Next <i class=\"icon-angle-right\"></i></a>\n"+
				                                                        "<div class=\"clearfix\"></div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<div class=\"accordion-heading\">\n"+
				                                                    "<div class=\"diversity-group\">\n"+
				                                                        "<label>\n"+
				                                                            "Members Served <i class=\"icon-question-sign tip\" title=\"Populations or membership served by the organization\"></i><i class=\"icon-ok diversity-complete\"></i>\n"+
				                                                        "</label>\n"+
				                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#MembersServed\" data-parent=\"#diversity\"><i class=\"icon-pencil\"></i>Edit</a>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"accordion-body collapse\" id=\"MembersServed\">\n"+
				                                                    "<div class=\"diversity-progress\">\n"+
				                                                        "<div class=\"progress progress-ethnic\">\n"+
				                                                            "<div class=\"bar bar-inverse american-indian\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse pacific-islander\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse african-american\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse gay\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse hispanic-latino\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse ethnic-white\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse women-served\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse disability\" style=\"width: 0;\"></div>\n"+
				                                                            "<div class=\"bar bar-inverse veterans\" style=\"width: 0;\"></div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"diversity-form\">\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">American Indian/Alaskan</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityAmericanIndian\" data-diversity=\"american-indian\" class=\"ethnicity-input\" value=\""+diversityMembersAMERICANINDIANALASKAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Asian/Pacific Islander</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityPacificIslander\" class=\"ethnicity-input\" data-diversity=\"pacific-islander\" value=\""+diversityMembersASIANPACIFICISLANDER+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Black/African American</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityAfricanAmerican\" data-diversity=\"african-american\" class=\"ethnicity-input \" value=\""+diversityMembersBLANKAFRICANAMERICAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Gay/Lesbian/Bisexual/Transgender</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityGay\" data-diversity=\"gay\" class=\"ethnicity-input\" value=\""+diversityMembersGAY+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"diversity-column\">\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Hispanic/Latino</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityHispanc\" data-diversity=\"hispanic-latino\" class=\"ethnicity-input \" value=\""+diversityMembersHISPANICLATINO+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">White</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityWhite\" data-diversity=\"ethnic-white\" class=\"ethnicity-input \" value=\""+diversityMembersWHITE+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Women</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityWomen\" data-diversity=\"women-served\" class=\"ethnicity-input\" value=\""+diversityMembersWOMEN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">People with Disabilities</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityDisability\" data-diversity=\"disability\" class=\"ethnicity-input\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"control-group\">\n"+
				                                                                "<label class=\"control-label\">Veterans</label>\n"+
				                                                                "<div class=\"controls members-served\">\n"+
				                                                                    "<div class=\"input-append\">\n"+
				                                                                        "<input type=\"text\" name=\"msDiversityVetrans\" data-diversity=\"veterans\" class=\"ethnicity-input\" value=\""+diversityMembersVETRAN+"\">\n"+
				                                                                        "<span class=\"add-on\">%</span>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"clearfix\"></div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<div id=\"address-list-container\">\n"+
				                                            "<div class=\"country-input\">\n"+
				                                                "<h1>Primary Address</h1>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"address "+hidePrimaryAddress+" primary-item\" id=\"primary-address\">\n"+
				                                                "<div class=\"accordion-group\">\n"+
				                                                    "<div class=\"accordion-body collapse in\" id=\"primary1\">\n"+
				                                                        "<div class=\"row-fluid\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">Country</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<select class=\"input-block-level address-country\" required=\"required\" name=\"primaryAddressCountry\">\n"+
				                                                                        "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.);
														                                        //Req#603 START Code added by AGAMPA
										                  										String defaultCountry = "US";
										                  										if(primaryCountry != null && !primaryCountry.equals(""))
										                  											defaultCountry = primaryCountry;
										                  										//Req#603 END
																								for (int x = 0; x < arrayCountryCode.length; x++) {
																										if (arrayCountryCode[x][0].equalsIgnoreCase(defaultCountry)){
																											response.write("<option value=\""+arrayCountryCode[x][0]+" selected\">"+arrayCountryCode[x][1]+"</option>");
																										} else {
																											response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
																										}																				
																								}
				                                                                        response.write("</select>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"row-fluid\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">Address 1</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level address1\" type=\"text\" name=\"primaryAddress1\" placeholder=\"Street Name\" value=\""+primaryAddress1+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level address2\" type=\"text\" name=\"primaryAddress2\" placeholder=\"Building or Unit Number\" value=\""+primaryAddress2+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level address3\" type=\"text\" name=\"primaryAddress3\" placeholder=\"Suite or Room Number\" value=\""+primaryAddress3+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"row-fluid validate-group\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">City</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level city\" type=\"text\" name=\"primaryAddressCity\" placeholder=\"City\" value=\""+primaryCity+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label\">State<span class=\"province-label hide\">/Province</span></label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<select class=\"input-block-level state\" name=\"primaryAddressState\">\n"+
				                                                                        "</select>\n"+
				                                                                       // "<input class=\"input-block-level province hide\" type=\"text\" name=\"primaryAddressProvince\" "+hideNonUSState+"/ value=\""+primaryState+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label zip\">Zip<span class=\"postal-code-label hide\">/Postal</span> Code</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level zip\" type=\"text\" name=\"primaryAddressZip\" placeholder=\"Zip-Code\" value=\""+primaryZip+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<h1 class=\"secondary-item-legend\" id=\"secondary-addresses-legend\">Secondary Addresses</h1>\n"+
				                                            "</div>\n" +
				                                            "<div class=\"accordion address secondary-item\" id=\"secondary-address\">");
				                                            
															// TODO Secondary Addresses
				                                            // --- Display existing Addresses 
				                                            if(retCT_LFA1 != null){                                      	
				                                            	int maxRows = retCT_LFA1.getNumRows();
				                                            	
				                                            	// Ignore the Primary Address
				                                            	retCT_LFA1.nextRow();
				                                            	for(int i = 0; i < maxRows; i++) {
				                                            		
				                                            		String secondaryLegalName = retCT_LFA1.getString("NAME1");
																	String secondaryAddress1 = retCT_LFA1.getString("STRAS");
																	String secondaryAddress2 = retCT_LFA1.getString("STR_SUPPL1");
																	String secondaryAddress3 = retCT_LFA1.getString("STR_SUPPL2");
																	String secondaryCountry = retCT_LFA1.getString("LAND1");
																	String secondaryCity = retCT_LFA1.getString("ORT01");
																	String secondaryState = retCT_LFA1.getString("REGIO");
																	String secondaryZip = retCT_LFA1.getString("PSTLZ");
				                                            		
																	response.write("<div class=\"accordion-group\" id=\"secondary-address-group-view"+i+"\">\n"+
															            "<div class=\"accordion-heading\">\n"+
															                "<label class=\"item-label\">"+secondaryLegalName+"</label>\n"+
															                "<i class=\"icon-remove tip\" title=\"\" data-index=\""+i+"\" data-original-title=\"Remove\"></i>\n"+
															                "<a class=\"btn btn-mini edit-item\" href=\"#secondary-address-view"+i+"\" data-parent=\"#secondary-address\">\n"+
															                    "<i class=\"icon-pencil\"></i>Edit\n"+
															                "</a>\n"+
															                "<div class=\"clearfix\"></div>\n"+
															            "</div>\n"+
															            "<div class=\"accordion-body secondary-item collapse\" id=\"secondary-address-view"+i+"\" style=\"height: 0px;\">\n"+
															
															                "<div class=\"accordion-inner\">\n"+
															                    "<div class=\"row-fluid\">\n"+
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Vendor Name (Optional)</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level vendor-name\" type=\"text\" name=\"secondaryAddress-view"+i+"-vendorName\" placeholder=\"Vendor Name\" value=\""+secondaryLegalName+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Country</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<select class=\"input-block-level address-country\" required=\"required\" name=\"secondaryAddress-view"+i+"-country\">\n"+
															                                    "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
																										for (int x = 0; x < arrayCountryCode.length; x++) {
																												if (arrayCountryCode[x][0].equalsIgnoreCase(secondaryCountry)){
																													response.write("<option value=\""+arrayCountryCode[x][0]+" selected\">"+arrayCountryCode[x][1]+"</option>");
																												} else {
																													response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
																												}																				
																										}
															                                    response.write("</select>\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"row-fluid\">\n"+
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Address 1</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level header-input address1\" required=\"\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address1\" placeholder=\"Street Name\" value=\""+secondaryAddress1+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"\" data-placement=\"right\" data-original-title=\"Building or Unit Number\" ></i></label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level address2\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address2\" placeholder=\"Building or Unit Number\" value=\""+secondaryAddress2+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"\" data-placement=\"right\" data-original-title=\"Suite or Room Number\"></i></label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level address3\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address3\" placeholder=\"Suite or Room Number\" value=\""+secondaryAddress3+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"row-fluid validate-group\">\n"+
															
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">City</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level city\" required=\"\" type=\"text\" name=\"secondaryAddress-view"+i+"-City\" placeholder=\"City\" value=\""+secondaryCity+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">State<span class=\"province-label hide\" style=\"display: none;\">/Province</span></label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<select class=\"input-block-level state\" name=\"secondaryAddress-view"+i+"-State\" required=\"\">\n"+
															                                        "<option value=\"\">Select State</option>\n"+
															                                        "<option>CA</option>\n"+
															                                    "</select>\n"+
															                                    "<input type=\"text\" class=\"input-block-level province hide\" name=\"secondaryAddress-view"+i+"-Province\" style=\"display: none;\" value=\""+secondaryState+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label zip\" name=\"secondaryview"+i+"Zip\">Zip<span class=\"postal-code-label hide\" style=\"display: none;\">/Postal</span> Code</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level zip\" type=\"text\" required=\"\" name=\"secondaryAddress-view"+i+"-Zip\" placeholder=\"Zip-Code\" value=\""+secondaryZip+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"remit-option\">\n"+
															                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
															                            "<button type=\"button\" class=\"btn btn-info remit-button\"><i class=\"icon-check-sign\"></i>Remit</button>\n"+
															                            "<button type=\"button\" class=\"btn btn-info purchasing-button\"><i class=\"icon-check-sign\"></i>Purchasing</button>\n"+
															                            "<div class=\"hidden-form-elements\">\n"+
															                                "<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"remit\">\n"+
															                                "<input type=\"radio\" class=\"purchasing\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"purchasing\">\n"+
															                            "</div>\n"+
															                            "<a href=\"#\" class=\"tip\" data-html=\"true\" data-placement=\"top\" data-title=\"&lt;label&gt;Remit&lt;/label&gt;&lt;p&gt;Decription of what a remit address is.&lt;/p&gt;&lt;label&gt;Purchasing&lt;/label&gt;&lt;p&gt;Decription of what a purchasing address is.&lt;/p&gt;\" data-original-title=\"\" title=\"\">What is the difference?</a>\n"+
															                        "</div>\n"+
															                        "<div class=\"purchasing-contact\">\n"+
															                            "<span class=\"caret\"></span>\n"+
															                            "<div class=\"row-fluid\">\n"+
															                                "<div class=\"span6\">\n"+
															                                    "<div class=\"control-group\">\n"+
															                                        "<label class=\"control-label\">Email <i class=\"icon-exclamation-sign tip\" data-placement=\"right\" title=\"\" data-original-title=\"Make sure this is the correct!\"></i></label>\n"+
															                                        "<div class=\"controls\">\n"+
															                                            "<input class=\"input-block-level purchasing-email\" type=\"email\" name=\"secondaryAddress-view"+i+"-purchasingEmail\" placeholder=\"person@email.com\">\n"+
															                                        "</div>\n"+
															                                    "</div>\n"+
															                                "</div>\n"+
															                                "<div class=\"span6\">\n"+
															                                    "<div class=\"control-group\">\n"+
															                                        "<label class=\"control-label\">Fax</label>\n"+
															                                        "<div class=\"controls\">\n"+
															                                            "<input class=\"input-block-level fax phone-number\" type=\"text\" name=\"secondaryAddress-view"+i+"-purchasingFax\" placeholder=\"(XXX) XXX-XXXX\">\n"+
															                                        "</div>\n"+
															                                    "</div>\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                "</div>\n"+
															            "</div>\n"+
															        "</div>\n");
				                                            		
				                                            		retCT_LFA1.nextRow();
				                                            	}
				                                            	
				                                            } else {
				                                             response.write("<div class=\"accordion address secondary-item\" id=\"secondary-address\"></div>\n");
				                                            
				                                            }
				                                            // --- End Display existing Addresses
				                                            
				                                            response.write("</div>" +
				                                            "<hr>\n"+
				                                            "<div class=\"input-append\">\n"+
				                                            	//Req#50 START Code added by AGAMPA 21 Feb 2015
				                                                "<select id=\"country-select\" class=\"country-select sec-address-country\">\n"+ //Req#50 END
				                                                    "<option value=\"US\">United States</option>\n"+
				                                                    "<option value=\"CA\">Canada</option>\n"+
				                                                    "<option value=\"MX\">Mexico</option>\n"+
				                                                    "<option value=\"AU\">Australia</option>\n"+
				                                                    "<option value=\"NZ\">New Zealand</option>\n"+
				                                                    "<option>Other</option>\n"+
				                                                "</select>\n"+
				                                              //Req#50 START Code added by AGAMPA 21 Feb 2015
				                                                "<a id=\"add-sec-address\" class=\"btn btn-primary add-item\"><i class=\"icon-plus\"></i>Add Address</a>\n"+ //Req#50 END
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<a id=\"fromBasic\" class=\"btn btn-success continue\" href=\"#tab2\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				                                      //Req#51 START Code change by AGAMPA
				                                        //"<a class=\"btn btn-success resubmit\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            //TODO Tab2
				                           "<div class=\"tab-pane fade\" id=\"tab2\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<h1>Select one:</h1>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"span12\">\n"+
				                                                "<div id=\"taxInfo\" class=\"btn-group vendor-accordion\" data-toggle=\"buttons-radio\">\n"+
				                                                    "<div class=\"accordion-group\">\n"+
				                                                        "<a class=\"btn btn-block btn-large btn-info\" data-target=\"#taxid\" data-option=\"tax\" data-parent=\"#taxInfo\" id=\"tax-taxid\" value=\""+taxID+"\"><i class=\"icon-check-sign\"></i>Tax ID</a>\n"+
				                                                        "<div id=\"taxid\" class=\"taxid-ssn collapse\">\n"+
				                                                            "<span></span>\n"+
				                                                            "<div class=\"control-group pull-left\">\n"+
				                                                                "<label class=\"control-label\">\n"+
				                                                                    "Enter Tax-ID Number\n"+
				                                                                "</label>\n"+
				                                                                "<div class=\"controls\">\n"+
				                                                                    "<input type=\"text\" class=\"tax-taxid\" pattern=\"\\d{2}-\\d{7}\" size=\"10\" maxlength=\"10\" name=\"taxId1\" placeholder=\"XX-XXXXXXX\">\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"clearfix\"></div>\n"+
				                                                            "<div class=\"container-fluid tax-dropdowns\">\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Recipient Type\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<select required=\"required\" class=\"input-block-level\">\n"+
				                                                                                "<option>Select One</option>\n"+
				                                                                            "</select>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Exempt Payee Code\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<div class=\"multi-line-select\">\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> An organization exempt from tax under section 501(a), any IRA, or a custodial account under section 403(b)(7) if the account satisfies the requirements of section 401(f)(2)\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> The United States or any of its agencies or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A foreign government or any of its political subdivisions, agencies, or instrumentalities \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A corporation\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A dealer in securities or commodities required to register in the United States, the District of Columbia, or a possession of the United States \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A futures commission merchant registered with the Commodity Futures Trading Commission \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A real estate investment trust \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> An entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A common trust fund operated by a bank under section 584(a) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A financial institution  \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A middleman known in the investment community as a nominee or custodian   \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" />   A trust exempt from tax under section 664 or described in section 4947 \n"+
				                                                                                "</label>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Exempt for FACTA Reporting Code\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<div class=\"multi-line-select\">\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> An organization exempt from tax under section 501(a), or any individual retirement plan as defined in section 7701(a)(37) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> The United States or any of its agencies or instrumentalities \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" />   A corporation the stock of which is regularly traded on one or more established securities markets, as described in Reg, section 1.1472-1(c)(1)(i) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A corporation that is a member of the same expanded affiliated group as a corporation described in Reg. section 1.1472-1(c)(1)(i) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" />   A dealer in securities, commodities, or derivative financial instruments (including notional principal contracts, futures, forwards, and options) that is registered as such under the laws of the United States or any State \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A real estate investment trust \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A regulated investment company as defined in section 851 or an entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A common trust fund as defined in section 584(a) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A bank as defined in section 581\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A broker  \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A trust exempt from tax under section 664 or described in section 4947 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A tax-exempt trust under a section 403(b) plan or section 457(g) plan \n"+
				                                                                                "</label>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"upload-forms\">\n"+
				                                                                "<div class=\"control-group pull-left\">\n"+
				                                                                    "<label class=\"control-label\">\n"+
				                                                                        "Upload W9\n"+
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input type=\"file\" name=\"taxw9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<a class=\"pull-right\" href=\"#\"><i class=\"icon-file\"></i> Download Blank W9</a>\n"+
				                                                                "<div class=\"clearfix\"></div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"accordion questions contractor\">\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Are you an individual/independent contractor?\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"independantContractor\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"independantContractor\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Has  the NBCUniversal  Supplier established Environmental Sustainability improvement goals and objectives to manage the design and packaging of products;  reduce greenhouse gas emissions, waste and water usage?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ1Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ1\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ1\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU tell worker where, when and how to do work?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ2Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ2\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ2\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU train worker?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ3Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ3\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ3\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Are worker's services integral to NBCU's operation?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ4Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ4\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ4\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Must worker personally provide services?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ5Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ5\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ5\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU pay assistants to worker?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ6Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ6\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ6\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Ongoing NBCU/worker relationship?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ7Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ7\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ7\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU set work schedule?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ8Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ8\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ8\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Full-time worker devotion to NBCU?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ9Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ9\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ9\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Work performed on NBCU premises?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ10\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ10\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ10\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU direct sequence of work?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ11\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ11\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ11\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU require regular reports?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ12\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ12\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ12\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Paid by hour/week (vs. flat fee for job/project?)\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ13Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ13\" value=\"yes\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ13\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "NBCU pay travel and expenses?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ14Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ14\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ14\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "NBCU furnish tools, equipment, materials?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ15Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ15\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ15\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker failure to invest in his/her business? What does this mean?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ16Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ16\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ16\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker protected from profit or loss?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ17Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ17\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ17\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker exclusive to NBCU?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ18Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ18\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ18\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker unavailable to broader public?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ19Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ19\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ19\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "May NBCU discharge worker other than for failure to meet contract specs?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ20Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ20\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ20\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Can worker terminate early without liability for failure to perform contract?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxContractorQ21Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ21\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxContractorQ21\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                    "<div class=\"accordion-group\">\n"+
				                                                        "<a class=\"btn btn-block btn-large btn-info\" data-target=\"#social\" data-option=\"ssn\" data-parent=\"#taxInfo\" id=\"tax-ssn\"><i class=\"icon-check-sign\"></i>Social Security Number</a>\n"+
				                                                        "<div class=\"hidden-form-elements\">\n"+
				                                                            "<input type=\"radio\" name=\"taxSsn\" required class=\"tax\" value=\"tax\" />\n"+
				                                                            "<input type=\"radio\" name=\"taxSsn\" required class=\"ssn\" value=\"ssn\" />\n"+
				                                                        "</div>\n"+
				                                                        "<div id=\"social\" class=\"taxid-ssn collapse\">\n"+
				                                                            "<span></span>\n"+
				                                                            "<div class=\"control-group pull-left\">\n"+
				                                                                "<label class=\"control-label\">\n"+
				                                                                    "Enter Social Security Number\n"+
				                                                                "</label>\n"+
				                                                                "<div class=\"controls\">\n"+
				                                                                    "<input type=\"text\" class=\"tax-social\" pattern=\"\\d{3}-\\d{2}-\\d{4}\" name=\"ssn1\" size=\"11\" maxlength=\"11\" placeholder=\"XXX-XX-XXXX\" value\""+socialSecurityNumber+"\">\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"clearfix\"></div>\n"+
				                                                            "<div class=\"container-fluid tax-dropdowns\">\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Recipient Type\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<select required=\"required\" class=\"input-block-level\">\n"+
				                                                                                "<option>Select One</option>\n"+
				                                                                            "</select>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Exempt Payee Code\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<div class=\"multi-line-select\">\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> An organization exempt from tax under section 501(a), any IRA, or a custodial account under section 403(b)(7) if the account satisfies the requirements of section 401(f)(2)\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> The United States or any of its agencies or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A foreign government or any of its political subdivisions, agencies, or instrumentalities \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A corporation\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A dealer in securities or commodities required to register in the United States, the District of Columbia, or a possession of the United States \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A futures commission merchant registered with the Commodity Futures Trading Commission \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A real estate investment trust \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> An entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A common trust fund operated by a bank under section 584(a) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A financial institution  \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" /> A middleman known in the investment community as a nominee or custodian   \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line1\" />   A trust exempt from tax under section 664 or described in section 4947 \n"+
				                                                                                "</label>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"row-fluid\">\n"+
				                                                                    "<div class=\"control-group span12\">\n"+
				                                                                        "<label class=\"control-label\">\n"+
				                                                                            "Exempt for FACTA Reporting Code\n"+
				                                                                        "</label>\n"+
				                                                                        "<div class=\"controls\">\n"+
				                                                                            "<div class=\"multi-line-select\">\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> An organization exempt from tax under section 501(a), or any individual retirement plan as defined in section 7701(a)(37) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> The United States or any of its agencies or instrumentalities \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" />   A corporation the stock of which is regularly traded on one or more established securities markets, as described in Reg, section 1.1472-1(c)(1)(i) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A corporation that is a member of the same expanded affiliated group as a corporation described in Reg. section 1.1472-1(c)(1)(i) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" />   A dealer in securities, commodities, or derivative financial instruments (including notional principal contracts, futures, forwards, and options) that is registered as such under the laws of the United States or any State \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A real estate investment trust \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A regulated investment company as defined in section 851 or an entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A common trust fund as defined in section 584(a) \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A bank as defined in section 581\n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A broker  \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A trust exempt from tax under section 664 or described in section 4947 \n"+
				                                                                                "</label>\n"+
				                                                                                "<label class=\"radio\">\n"+
				                                                                                    "<input type=\"radio\" name=\"multi-line2\" /> A tax-exempt trust under a section 403(b) plan or section 457(g) plan \n"+
				                                                                                "</label>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"upload-forms\">\n"+
				                                                                "<div class=\"control-group pull-left\">\n"+
				                                                                    "<label class=\"control-label\">\n"+
				                                                                        "Upload W9\n"+
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input type=\"file\" name=\"taxw9\" />\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<a class=\"pull-right\" href=\"#\"><i class=\"icon-file\"></i> Download Blank W9</a>\n"+
				                                                                "<div class=\"clearfix\"></div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"accordion questions contractor\">\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Has  the NBCUniversal  Supplier established Environmental Sustainability improvement goals and objectives to manage the design and packaging of products;  reduce greenhouse gas emissions, waste and water usage?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ1Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ1\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ1\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU tell worker where, when and how to do work?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ2Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ2\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ2\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU train worker?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ3Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ3\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ3\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Are worker's services integral to NBCU's operation?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ4Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ4\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ4\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Must worker personally provide services?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ5Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ5\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ5\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU pay assistants to worker?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ6Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ6\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ6\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Ongoing NBCU/worker relationship?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ7Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ7\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ7\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU set work schedule?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ8Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ8\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ8\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Full-time worker devotion to NBCU?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ9Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ9\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ9\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Work performed on NBCU premises?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ10Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ10\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ10\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU direct sequence of work?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ11Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ11\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ11\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Does NBCU require regular reports?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ12Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ12\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ12\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Paid by hour/week (vs. flat fee for job/project?)\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ13Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ13\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ13\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "NBCU pay travel and expenses?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ14Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ14\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ14\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "NBCU furnish tools, equipment, materials?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ15Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ15\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ15\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker failure to invest in his/her business? What does this mean?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ16Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ16\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ16\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker protected from profit or loss?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ17Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ17\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ17\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker exclusive to NBCU?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ18Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ18\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ18\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Worker unavailable to broader public?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ19Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ19\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ19\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "May NBCU discharge worker other than for failure to meet contract specs?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ20Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ20\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ20\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<div class=\"accordion-group\">\n"+
				                                                                    "<div class=\"accordion-body collapse in\">\n"+
				                                                                        "<div class=\"accordion-question\">\n"+
				                                                                            "Can worker terminate early without liability for failure to perform contract?\n"+
				                                                                            "<div class=\"describe\">\n"+
				                                                                                "Describe\n"+
				                                                                              "<textarea name=\"taxSsnQ21Desc\" class=\"input-block-level\"></textarea>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                    "<div class=\"accordion-heading\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
				                                                                            "<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                            "<div class=\"hidden-form-elements\">\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ21\" value=\"yes\" />\n"+
				                                                                                "<input type=\"radio\" name=\"taxSsnQ21\" value=\"no\" />\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<hr>\n"+
				                                                "<div class=\"california-alert\">\n"+
				                                                    "<h3><i class=\"icon-bolt\"></i>Warning</h3>\n"+
				                                                    "<div class=\"alert-message\">\n"+
				                                                        "<p>Vendors who provide independent services, win prizes as a contestant in a show, or rent real/personal property in California, must have a <strong>590 Form</strong> on file with NBCU or will be subject to <strong>7% CA Franchise tax withholding.</strong></p>\n"+
				                                                        "<div class=\"upload-forms\">\n"+
				                                                            "<div class=\"control-group pull-left\">\n"+
				                                                                "<label class=\"control-label\">\n"+
				                                                                    "Upload 590\n"+
				                                                                "</label>\n"+
				                                                                "<div class=\"controls\">\n"+
				                                                                    "<input type=\"file\" name=\"tax590\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<a class=\"pull-right\" href=\"#\"><i class=\"icon-file\"></i> Download Blank 590</a>\n"+
				                                                            "<div class=\"clearfix\"></div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<a class=\"btn back\" href=\"#tab1\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<a class=\"btn btn-success continue\" href=\"#tab3\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				                                      //Req#51 START Code change by AGAMPA
				                                       // "<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            
				                            
				                            "<div class=\"tab-pane fade\" id=\"tab3\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<div id=\"termsInfo\" class=\"btn-group vendor-accordion\" data-toggle=\"buttons-radio\">\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<a class=\"btn btn-block btn-large btn-info toggle-terms\" id=\"terms-210\" data-target=\"#terms1\" data-option=\"210\" data-parent=\"#termsInfo\"><i class=\"icon-check-sign\"></i>2% 10</a>\n"+
				                                                "<div id=\"terms1\" class=\"collapse\">\n"+
				                                                    "<span></span>\n"+
				                                                    "<div class=\"terms-copy\">\n"+
				                                                        "<h4>Terms &amp; Conditions</h4>\n"+
				                                                        "Donec posuere tincidunt cursus. Ut consectetur elit vel neque euismod ac varius mi iaculis. Vivamus ultricies cursus posuere. Phasellus et leo enim, non vulputate turpis. In ultrices orci nec quam commodo eu rutrum urna dapibus. Integer sed mauris massa, scelerisque adipiscing lacus. Aliquam erat volutpat. Nam mollis pharetra felis, eu elementum diam condimentum nec. Etiam vestibulum ultricies massa sit amet pharetra.\n"+
				"\n"+
				                                                        "Proin a aliquet mi. Aliquam tortor leo, tristique et vestibulum non, rhoncus at erat. Aliquam quis lacus vitae nisi fringilla placerat sed a libero. Suspendisse placerat tincidunt dapibus. Phasellus rhoncus iaculis orci eu pulvinar. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum a nisl eros, at posuere sem. Sed ut odio at ipsum faucibus ultricies. Morbi nunc magna, pellentesque at placerat at, facilisis a massa. Suspendisse tempus, quam vitae tempor faucibus, libero nibh vulputate dui, sed malesuada mauris nulla eget felis. Duis dui nunc, mollis id malesuada a, gravida at leo. Ut ac porttitor tortor. Sed dapibus facilisis ligula, varius porta neque malesuada vel. Cras tellus nisi, mollis ac faucibus a, feugiat ut arcu. Vestibulum in magna non nisl pharetra dapibus ut ac mauris.\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<a class=\"btn btn-block btn-large btn-info toggle-terms\" id=\"terms-130\" data-target=\"#terms2\" data-option=\"130\" data-parent=\"#termsInfo\"><i class=\"icon-check-sign\"></i>1% 30</a>\n"+
				                                                "<div id=\"terms2\" class=\"collapse\">\n"+
				                                                    "<span></span>\n"+
				                                                    "<div class=\"terms-copy\">\n"+
				                                                        "<h4>Terms &amp; Conditions</h4>\n"+
				                                                        "Donec posuere tincidunt cursus. Ut consectetur elit vel neque euismod ac varius mi iaculis. Vivamus ultricies cursus posuere. Phasellus et leo enim, non vulputate turpis. In ultrices orci nec quam commodo eu rutrum urna dapibus. Integer sed mauris massa, scelerisque adipiscing lacus. Aliquam erat volutpat. Nam mollis pharetra felis, eu elementum diam condimentum nec. Etiam vestibulum ultricies massa sit amet pharetra.\n"+
				"\n"+
				                                                        "Proin a aliquet mi. Aliquam tortor leo, tristique et vestibulum non, rhoncus at erat. Aliquam quis lacus vitae nisi fringilla placerat sed a libero. Suspendisse placerat tincidunt dapibus. Phasellus rhoncus iaculis orci eu pulvinar. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum a nisl eros, at posuere sem. Sed ut odio at ipsum faucibus ultricies. Morbi nunc magna, pellentesque at placerat at, facilisis a massa. Suspendisse tempus, quam vitae tempor faucibus, libero nibh vulputate dui, sed malesuada mauris nulla eget felis. Duis dui nunc, mollis id malesuada a, gravida at leo. Ut ac porttitor tortor. Sed dapibus facilisis ligula, varius porta neque malesuada vel. Cras tellus nisi, mollis ac faucibus a, feugiat ut arcu. Vestibulum in magna non nisl pharetra dapibus ut ac mauris.\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion-group\">\n"+
				                                                "<a class=\"btn btn-block btn-large btn-info toggle-terms\" id=\"terms-net75\" data-target=\"#terms3\" data-option=\"net75\" data-parent=\"#termsInfo\"><i class=\"icon-check-sign\"></i>Net 75</a>\n"+
				                                                "<div id=\"terms3\" class=\"collapse\">\n"+
				                                                    "<span></span>\n"+
				                                                    "<div class=\"terms-copy\">\n"+
				                                                        "<h4>Terms &amp; Conditions</h4>\n"+
				                                                        "Donec posuere tincidunt cursus. Ut consectetur elit vel neque euismod ac varius mi iaculis. Vivamus ultricies cursus posuere. Phasellus et leo enim, non vulputate turpis. In ultrices orci nec quam commodo eu rutrum urna dapibus. Integer sed mauris massa, scelerisque adipiscing lacus. Aliquam erat volutpat. Nam mollis pharetra felis, eu elementum diam condimentum nec. Etiam vestibulum ultricies massa sit amet pharetra.\n"+
				"\n"+
				                                                        "Proin a aliquet mi. Aliquam tortor leo, tristique et vestibulum non, rhoncus at erat. Aliquam quis lacus vitae nisi fringilla placerat sed a libero. Suspendisse placerat tincidunt dapibus. Phasellus rhoncus iaculis orci eu pulvinar. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum a nisl eros, at posuere sem. Sed ut odio at ipsum faucibus ultricies. Morbi nunc magna, pellentesque at placerat at, facilisis a massa. Suspendisse tempus, quam vitae tempor faucibus, libero nibh vulputate dui, sed malesuada mauris nulla eget felis. Duis dui nunc, mollis id malesuada a, gravida at leo. Ut ac porttitor tortor. Sed dapibus facilisis ligula, varius porta neque malesuada vel. Cras tellus nisi, mollis ac faucibus a, feugiat ut arcu. Vestibulum in magna non nisl pharetra dapibus ut ac mauris.\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"hidden-form-elements\">\n"+
				                                                "<input type=\"radio\" required name=\"terms\" class=\"210\" value=\"2%10\" />\n"+
				                                                "<input type=\"radio\" required name=\"terms\" class=\"130\" value=\"1%30\" />\n"+
				                                                "<input type=\"radio\" required name=\"terms\" class=\"net75\" value=\"net75\" />\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<a class=\"btn back\" href=\"#tab2\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<a class=\"btn btn-success continue\" href=\"#tab4\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				                                      //Req#51 START Code change by AGAMPA
				                                        //"<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            "<div class=\"tab-pane fade\" id=\"tab4\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<div id=\"account-list-container\">\n"+
				                                            "<!-- Does not validate, legend is only allowed as immediate child form or fieldset, also cannot have nested divs -->\n"+
				                                            "<div id=\"primaryAccount\" class=\"payment-method-disabled\">\n"+
				                                                "<div class=\"pseudo-legend\">Primary Account\n"+
				                                                "<div class=\"btn-group\">\n"+
				                                                    "<div class=\"disabled-link\"></div>\n"+
				                                                    "<a class=\"btn btn-info dropdown-toggle\" href=\"#\">\n"+
				                                                        "<label class=\"type-text\">ACH</label>\n"+
				                                                        "<span class=\"caret\"></span>\n"+
				                                                    "</a>\n"+
				                                                    "<ul class=\"dropdown-menu\">\n"+
				                                                        "<li>\n"+
				                                                            "<a class=\"typeOption\">ACH</a>\n"+
				                                                        "</li>\n"+
				                                                        "<li>\n"+
				                                                            "<a class=\"typeOption\">Wire</a>\n"+
				                                                        "</li>\n"+
				                                                    "</ul>\n"+
				                                                    "<input type=\"hidden\" class=\"type\" name=\"bankingPrimaryType\" value=\"ACH\" />\n"+
				                                                "</div>\n"+
				                                                "</div>\n"+

				                                                "<div class=\"address primary-item\" id=\"primary-account\">\n"+
				                                                    "<div class=\"accordion-group\">\n"+
				                                                        "<div class=\"accordion-body collapse in\">\n"+
				                                                            "<div class=\"row-fluid\">\n"+
				                                                                "<div class=\"span6\">\n"+
				                                                                    "<select class=\"input-block-level country\" name=\"primary-account-country\">\n"+
				                                                                        "<option value=\"US\">United States</option>\n"+
				                                                                        "<option value=\"CA\">Canada</option>\n"+
				                                                                        "<option value=\"MX\">Mexico</option>\n"+
				                                                                        "<option value=\"AU\">Austraila</option>\n"+
				                                                                        "<option value=\"NZ\">New Zealand</option>\n"+
				                                                                        "<option>Other</option>\n"+
				                                                                    "</select>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"row-fluid account-type\">\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"row-fluid accept-usd\">\n"+
				                                                                "<div class=\"control-group span12 currency\">\n"+
				                                                                    "<label class=\"control-label\">\n"+
				                                                                        "Accept US Currency?\n"+
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                                                            "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                        "</div>\n"+
				                                                                        "<div class=\"add-on currency-no\">\n"+
				                                                                            "<div class=\"span4\">\n"+
				                                                                                "<div class=\"control-group\">\n"+
				                                                                                    "<label class=\"control-label\">Select Currency</label>\n"+
				                                                                                    "<div class=\"controls\">\n"+
				                                                                                        "<select class=\"input-block-level primary-currency\" name=\"bankingPrimaryCurrency\">\n"+
				                                                                                            "<option value=\"\">Select Currency</option>\n"+
				                                                                                            "<option>USD</option>\n"+
				                                                                                            "<option>EUR</option>\n"+
				                                                                                        "</select>\n"+
				                                                                                    "</div>\n"+
				                                                                                "</div>\n"+
				                                                                            "</div>\n"+
				                                                                            "<div class=\"span6\">\n"+
				                                                                                "<div class=\"control-group\">\n"+
				                                                                                    "<label class=\"control-label\">Intermediary Bank Account #</label>\n"+
				                                                                                    "<div class=\"controls\">\n"+
				                                                                                        "<input class=\"input-block-level intermdiary-bank-account\" type=\"text\" name=\"bankingPrimaryIntermediary\" placeholder=\"Bank Account #\">\n"+
				                                                                                    "</div>\n"+
				                                                                                "</div>\n"+
				                                                                            "</div>\n"+
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                           "<div class=\"upload-forms\">\n"+
				                                                                "<div class=\"control-group pull-left\">\n"+
				                                                                    "<label class=\"control-label\">\n"+
				                                                                        "Upload ACH Form\n"+
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input type=\"file\" name=\"primaryACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                "<a class=\"pull-right\" href=\"#\"><i class=\"icon-file\"></i> Download Blank ACH</a>\n"+
				                                                                "<div class=\"clearfix\"></div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"accordion\">\n"+
				                                                "<h1 id=\"secondary-accounts-legend\" class=\"secondary-item-legend hide\">Secondary Accounts</h1>\n"+
				                                                "<div class=\"address secondary-item\" id=\"secondary-account\">\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                            "<hr>\n"+
				                                            "<div class=\"input-append secondary-account-button\">\n"+
				                                                "<select class=\"select-country sec-account-country\">\n"+
				                                                    "<option value=\"US\">United States</option>\n"+
				                                                    "<option value=\"CA\">Canada</option>\n"+
				                                                    "<option value=\"MX\">Mexico</option>\n"+
				                                                    "<option value=\"AU\">Austraila</option>\n"+
				                                                    "<option value=\"NZ\">New Zealand</option>\n"+
				                                                    "<option>Other</option>\n"+
				                                                "</select>\n"+
				                                                "<a id=\"add-sec-account-address\" class=\"btn btn-primary add-account add-item\"><i class=\"icon-plus\"></i>Add Account</a>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<button class=\"btn back\" href=\"#tab3\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</button>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<a class=\"btn btn-success continue\" href=\"#tab5\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				                                        //Req#51 START Code change by AGAMPA
				                                        //"<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            "<div class=\"tab-pane fade\" id=\"tab5\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\" id=\"contact-list-view\">\n"+
				                                        "<div class=\"accordion\">\n"+
				                                            "<h1>Contacts</h1>\n"+
				                                            //TODO: Contacts
				                                            "<div class=\"address secondary-item\" id=\"secondary-contact\">\n");
				                                           // --- Display existing Contacts 
				                                            if(retCT_KNVK != null){                                      	
				                                            	int maxRows = retCT_KNVK.getNumRows();

				                                            	for(int i = 0; i < maxRows; i++) {
				                                            
				                                            		String contactFirstName = retCT_KNVK.getString("NAMEV");
				                                            		String contactLastName = retCT_KNVK.getString("NAME1");
				                                            		String contactDepartment = retCT_KNVK.getString("ABTNR");
				                                            		String contactPhone = retCT_KNVK.getString("TELF1");
				                                            		String contactFax = retCT_KNVK.getString("FAX_NUMBER");				                                            		
				                                            		String contactEmail = retCT_KNVK.getString("NAMEV");	

															        response.write("<div class=\"accordion-group single-item\">\n"+
															            "<div class=\"accordion-heading\">\n"+
															                "<label class=\"item-label\">"+contactFirstName+" "+contactLastName+"</label>\n"+
															                "<i class=\"icon-remove tip\" data-id=\"view"+i+"\" title=\"Remove\"></i>\n"+
															                "<a class=\"btn btn-mini edit-item\" data-target=\"#contact-view"+i+"\" data-parent=\"#secondary-contact\">\n"+
															                    "<i class=\"icon-pencil\"></i>Edit\n"+
															                "</a>\n"+
															                "<div class=\"clearfix\"></div>\n"+
															            "</div>\n"+
															            "<div class=\"accordion-body collapse\" id=\"contact-view"+i+"\" style=\"height: 0px;\">\n"+
															                "<div class=\"row-fluid\">\n"+
															                    "<div class=\"span6\">\n"+
															                        "<div class=\"control-group\">\n"+
															                            "<label class=\"control-label\">Name</label>\n"+
															                            "<div class=\"controls\">\n"+
															                                "<input class=\"input-block-level header-input name\" required=\"\" type=\"text\" name=\"contact-view"+i+"-Name\" placeholder=\"Name\" value=\""+contactFirstName+" "+contactLastName+"\">\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"span6\">\n"+
															                        "<div class=\"control-group\">\n"+
															                            "<label class=\"control-label\">Email Address</label>\n"+
															                            "<div class=\"controls\">\n"+
															                                "<input class=\"input-block-level email\" required=\"\" type=\"email\" name=\"contact-view"+i+"-Email\" placeholder=\"Email\" value=\""+contactDepartment+"\">\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                "</div>\n"+
															                "<div class=\"row-fluid\">\n"+
															                    "<div class=\"span4\">\n"+
															                        "<div class=\"control-group\">\n"+
															                            "<label class=\"control-label\">Phone Number</label>\n"+
															                            "<div class=\"controls\">\n"+
															                                "<input class=\"input-block-level phone\" required=\"\" type=\"tel\" name=\"contact-view"+i+"-PhoneNum\" placeholder=\"Phone Number\" value=\""+contactPhone+"\">\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"span4\">\n"+
															                        "<div class=\"control-group\">\n"+
															                            "<label class=\"control-label\">Fax Number</label>\n"+
															                            "<div class=\"controls\">\n"+
															                                "<input class=\"input-block-level fax\" type=\"tel\" name=\"contact-view"+i+"-FaxNum\" placeholder=\"FaxNum\" value=\""+contactFax+"\">\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"span4\">\n"+
															                        "<div class=\"control-group\">\n"+
															                            "<label class=\"control-label\">Department</label>\n"+
															                            "<div class=\"controls\">\n"+
															                                "<select class=\"input-block-level department\" required=\"\" name=\"contact-view"+i+"-Department\">\n"+
															                                    "<option>Marketing\n"+
															                                    "</option>\n"+
															                                    "<option>Legal\n"+
															                                    "</option>\n"+
															                                "</select>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                "</div>\n"+
															            "</div>\n"+
															        "</div>\n");			                                            		
				                                            		
															        retCT_KNVK.nextRow();
				                                            	}
				                                            }					                                           
				                                            
				                                            
				                                            
				                                            response.write("</div>\n"+
				                                        "</div>\n"+
				                                        "<hr>\n"+
				                                        "<a class=\"btn btn-primary add-contact add-item\"><i class=\"icon-plus\"></i>Add Contact</a>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<a class=\"btn back\" href=\"#tab4\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<button name=\"action\" class=\"btn btn-success submit\" value=\"submit\" type=\"submit\">Submit <i class=\"icon-ok\"></i></button>\n"+
				                                        //Req#51 START Code change by AGAMPA
				                                        //"<button name=\"action\" class=\"btn btn-success resubmit\" value=\"resubmit\"></button>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4 sidebar\">\n"+
				                        "<h3><i class=\"icon-plus-sign-alt\"></i>Help</h3>\n"+
				                        "<div class=\"accordion\" id=\"accordion2\">\n"+
				                            "<div class=\"accordion-group\">\n"+
				                                "<div class=\"accordion-heading\">\n"+
				                                    "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseOne\">What is my \"Invoicing Name?\"\n"+
				                                    "</a>\n"+
				                                "</div>\n"+
				                                "<div id=\"collapseOne\" class=\"accordion-body collapse in\">\n"+
				                                    "<div class=\"accordion-inner\">\n"+
				                                        "It is the name that will be listed on your invoice.\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            "<div class=\"accordion-group\">\n"+
				                                "<div class=\"accordion-heading\">\n"+
				                                    "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseTwo\">The terms listed don't apply to me.\n"+
				                                    "</a>\n"+
				                                "</div>\n"+
				                                "<div id=\"collapseTwo\" class=\"accordion-body collapse\">\n"+
				                                    "<div class=\"accordion-inner\">\n"+
				                                        "Please call your NBCUniversal contact to address this issue.\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				"\n"+
				        "<div class=\"footer\">\n"+
				            "<div class=\"container\">\n"+
				                "(c) NBCUniversal\n"+
/*				            "</div>\n"+
				        "</div>\n"+
				"\n"+
				        "<div class=\"beauty\">\n"+
				            "<div class=\"background\"></div>\n"+
				        "</div>\n"+
				"\n"+
				        "<div class=\"circle-container\">\n"+
				            "<div class=\"circles\">\n"+
				                "<div></div>\n"+
				                "<div></div>\n"+
				                "<div></div>\n"+
				                "<div></div>\n"+
				                "<div></div>\n"+
				                "<div></div>\n"+*/
				            "</div>\n"+
				        "</div>\n"+
				"\n"+
				        "<div id=\"terms\" class=\"modal hide fade\">\n"+
				            "<div class=\"modal-header\">\n"+
				                "<h3>Terms &amp; Conditions</h3>\n"+
				            "</div>\n"+
				            "<div class=\"modal-body\">\n"+
				                "<div>\n"+
				                    "<i class=\"icon-new-icon\"></i>ipsum dolor sit amet, consectetur adipiscing elit. Nam condimentum erat a mi pellentesque et tincidunt elit adipiscing. Nunc sit amet magna lacus, sed ornare urna. Vivamus placerat felis feugiat lorem blandit porttitor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Integer tempus, purus ac sollicitudin malesuada, magna sem tristique lorem, sit amet lobortis urna erat eget tortor. Donec tempor erat et libero ullamcorper accumsan. Nulla arcu lacus, laoreet tempor egestas quis, facilisis ut mi. Sed porta purus ac quam tempor varius. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer luctus suscipit tortor a iaculis.\n"+
				              "Donec vestibulum mattis cursus. Fusce eu purus eros, eget ornare nisi. Morbi non ornare sem. Ut luctus rutrum orci vitae mattis. Vestibulum blandit placerat tortor a euismod. Proin ultrices porttitor fermentum. Nulla massa quam, vehicula in lobortis ullamcorper, vestibulum eu mi. Duis vehicula ipsum non lectus posuere sit amet pellentesque dui suscipit.\n"+
				              "Ut luctus dui ac elit elementum eget rutrum nisi sodales. Proin imperdiet auctor nunc in sodales. Quisque nulla dolor, egestas sed malesuada nec, vehicula suscipit nunc. Phasellus iaculis ligula ac dui dignissim quis commodo metus pulvinar. Quisque rutrum hendrerit hendrerit. Aliquam sagittis malesuada nisi ultrices condimentum. Praesent velit purus, tempus et consequat et, viverra non justo. Quisque interdum, turpis eu consectetur ultricies, odio ante vestibulum augue, vel venenatis augue eros in orci. In viverra ipsum vehicula ligula consectetur vulputate.\n"+
				              "Donec posuere tincidunt cursus. Ut consectetur elit vel neque euismod ac varius mi iaculis. Vivamus ultricies cursus posuere. Phasellus et leo enim, non vulputate turpis. In ultrices orci nec quam commodo eu rutrum urna dapibus. Integer sed mauris massa, scelerisque adipiscing lacus. Aliquam erat volutpat. Nam mollis pharetra felis, eu elementum diam condimentum nec. Etiam vestibulum ultricies massa sit amet pharetra.\n"+
				              "Proin a aliquet mi. Aliquam tortor leo, tristique et vestibulum non, rhoncus at erat. Aliquam quis lacus vitae nisi fringilla placerat sed a libero. Suspendisse placerat tincidunt dapibus. Phasellus rhoncus iaculis orci eu pulvinar. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum a nisl eros, at posuere sem. Sed ut odio at ipsum faucibus ultricies. Morbi nunc magna, pellentesque at placerat at, facilisis a massa. Suspendisse tempus, quam vitae tempor faucibus, libero nibh vulputate dui, sed malesuada mauris nulla eget felis. Duis dui nunc, mollis id malesuada a, gravida at leo. Ut ac porttitor tortor. Sed dapibus facilisis ligula, varius porta neque malesuada vel. Cras tellus nisi, mollis ac faucibus a, feugiat ut arcu. Vestibulum in magna non nisl pharetra dapibus ut ac mauris.\n"+
				                "</div>\n"+
				                "<label class=\"checkbox pull-right\">\n"+
				                    "<input type=\"checkbox\" name=\"agreeTerms\" />\n"+
				                    "I accept the terms &amp; conditions\n"+
				                "</label>\n"+
				            "</div>\n"+
				            "<div class=\"modal-footer terms-disabled\">\n"+
				                "<div class=\"disabled-overlay\"></div>\n"+
				                "<a href=\"#\" class=\"btn btn-success\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				            "</div>\n"+
				        "</div>\n"+
				        "<input type=\"hidden\" name=\"action\" />\n"+
				    "</form>\n"+
				"\n"+
				    "<div id=\"admin\" class=\"modal hide fade\">\n"+
				        "<div class=\"modal-header\">\n"+
				            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
				            "<h3>Add Administrators</h3>\n"+
				        "</div>\n"+
				        "<div class=\"modal-body\">\n"+
				            "<p>\n"+
				                "If you like, you can add administrators to login and fill out the registration form.\n"+
				            "</p>\n"+
				            "<form>\n"+
				                "<div class=\"container-fluid\">\n"+
				                    "<div id=\"adminList\" class=\"pillbox\">\n"+
				                        "<ul class=\"unstyled\">\n"+
				                        "</ul>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">\n"+
				                                    "First Name\n"+
				                                "</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input name=\"firstName\" type=\"text\" class=\"input-block-level\" required />\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">\n"+
				                                    "Last Name\n"+
				                                "</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input type=\"text\" name=\"lastName\" class=\"input-block-level\" required />\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">\n"+
				                                    "Phone Number\n"+
				                                "</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input type=\"tel\" name=\"phone\" class=\"input-block-level\" required />\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">\n"+
				                                    "Language\n"+
				                                "</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<select class=\"input-block-level\" name=\"language\">\n"+
				                                        "<option>English</option>\n"+
				                                        "<option>Spanish</option>\n"+
				                                    "</select>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span12\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">\n"+
				                                    "Email Address\n"+
				                                "</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<div class=\"input-append\">\n"+
				                                        "<input type=\"email\" name=\"email\" id=\"adminInput\" required />\n"+
				                                        "<a class=\"btn btn-primary\" id=\"addAdmin\"><i class=\"icon-plus\"></i>Add Administrator</a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</form>\n"+
				        "</div>\n"+
				        "<div class=\"modal-footer\">\n"+
				            "<a href=\"#\" class=\"btn\" id=\"closeAdmin\">Skip &amp; Complete Form</a>\n"+
				            "<a href=\"#\" class=\"btn btn-success\"><i class=\"icon-envelope-alt\"></i>Send Invitations</a>\n"+
				        "</div>\n"+
				    "</div>\n"+
				"\n"+
				    "<script type=\"text/template\" id=\"contactTemplate\">\n"+
				        "<div class=\"accordion-group single-item\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip\" data-id=\"<%- id %>\" title=\"Remove\"></i>\n"+
				                "<a class=\"btn btn-mini edit-item\" data-target=\"#contact-<%- id %>\" data-parent=\"#secondary-contact\">\n"+
				                    "<i class=\"icon-pencil\"></i>Edit\n"+
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse\" id=\"contact-<%- id %>\">\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span6\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Name</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level header-input name\" required type=\"text\" name=\"contact-<%- id %>-Name\" placeholder=\"Name\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span6\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Email Address</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level email\" required type=\"email\" name=\"contact-<%- id %>-Email\" placeholder=\"Email\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Phone Number</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level phone\" required type=\"tel\" name=\"contact-<%- id %>-PhoneNum\" placeholder=\"Phone Number\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Fax Number</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level fax\" type=\"tel\" name=\"contact-<%- id %>-FaxNum\" placeholder=\"FaxNum\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Department</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<select class=\"input-block-level department\" required name=\"contact-<%- id %>-Department\">\n"+
				                                    "<option>Marketing\n"+
				                                    "</option>\n"+
				                                    "<option>Legal\n"+
				                                    "</option>\n"+
				                                "</select>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
				    "<script type=\"text/template\" id=\"secondaryAccountTemplate\">\n"+
				        "<div class=\"accordion-group single-item\" id=\"secondary-account-container-<%- id %>\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<div class=\"hidden-form-elements\">\n"+
				                    "<input type=\"text\" value=\"\" name=\"bankingSecondary-<%- id %>-Country\" />\n"+
				                "</div>\n"+
				                "<div class=\"btn-group\">\n"+
				                    "<a class=\"btn btn-info dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">\n"+
				                        "<label class=\"type-text\">ACH</label>\n"+
				                        "<span class=\"caret\"></span>\n"+
				                    "</a>\n"+
				                    "<ul class=\"dropdown-menu\">\n"+
				                        "<li>\n"+
				                            "<a class=\"typeOption\">ACH</a>\n"+
				                        "</li>\n"+
				                        "<li>\n"+
				                            "<a class=\"typeOption\">Wire</a>\n"+
				                        "</li>\n"+
				                    "</ul>\n"+
				                "</div>\n"+
				                "<div class=\"hidden-form-elements\">\n"+
				                    "<input type=\"hidden\" name=\"bankingSecondary-<%- id %>-Country\" />\n"+
				                "</div>\n"+
				                "<input type=\"hidden\" class=\"type\" name=\"bankingSecondary-<%- id %>-Type\" value=\"ACH\" />\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip\" title=\"Remove\" data-id=\"<%- id %>\"></i>\n"+
				                "<a class=\"btn btn-mini edit-account edit-item accordion-toggle\" data-target=\"#secondary-account-<%- id %>\" data-parent=\"#secondary-account\">\n"+
				                    "<i class=\"icon-pencil\"></i>Edit\n"+
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse\" id=\"secondary-account-<%- id %>\">\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span6\">\n"+
				                        "<select class=\"input-block-level country\" name=\"secondary-account-<%- id %>-country\">\n"+
				                            "<option value=\"US\">United States</option>\n"+
				                            "<option value=\"CA\">Canada</option>\n"+
				                            "<option value=\"MX\">Mexico</option>\n"+
				                            "<option value=\"AU\">Austraila</option>\n"+
				                            "<option value=\"NZ\">New Zealand</option>\n"+
				                            "<option>Other</option>\n"+
				                        "</select>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                "<div class=\"row-fluid account-type\"></div>\n"+
				                "<div class=\"row-fluid currency-container accept-usd\">\n"+
				                    "<div class=\"control-group span12 currency\">\n"+
				                        "<label class=\"control-label\">\n"+
				                            "Accept US Currency?\n"+
				                        "</label>\n"+
				                        "<div class=\"controls\">\n"+
				                            "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                            "</div>\n"+
				                            "<div class=\"add-on currency-no\">\n"+
				                                "<div class=\"span4\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Select Currency</label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<select class=\"input-block-level primary-currency\" name=\"bankingSecondary-<%- id %>-Currency\">\n"+
				                                                "<option value=\"\">Select Currency</option>\n"+
				                                                "<option>USD</option>\n"+
				                                                "<option>EUR</option>\n"+
				                                            "</select>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                                "<div class=\"span6\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Intermediary Bank Account #</label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<input class=\"input-block-level intermdiary-bank-account\" type=\"text\" name=\"bankingSecondary-<%- id %>-Intermediary\" placeholder=\"Bank Account #\">\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                "<div class=\"upload-forms\">\n"+
					                "<div class=\"upload-forms\">\n"+
					                    "<div class=\"control-group pull-left\">\n"+
					                        "<label class=\"control-label\">\n"+
					                            "Upload ACH Form\n"+
					                        "</label>\n"+
					                        "<div class=\"controls\">\n"+
					                            "<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n"+
					                        "</div>\n"+
					                    "</div>\n"+
					                    "<a class=\"pull-right\" href=\"#\"><i class=\"icon-file\"></i> Download Blank ACH</a>\n"+
					                    "<div class=\"clearfix\"></div>\n"+
					                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+

				    "<script type=\"text/template\" id=\"secondaryAddressTemplate\">\n"+
				        "<div class=\"accordion-group\" id=\"secondary-address-group-<%- index %>\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip\" title=\"Remove\" data-index=\"<%- index %>\"></i>\n"+
				                "<a class=\"btn btn-mini edit-item\" href=\"#secondary-address-<%- id %>\" data-parent=\"#secondary-address\">\n"+
				                    "<i class=\"icon-pencil\"></i>Edit\n"+
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse secondary-item\" id=\"secondary-address-<%- id %>\">\n"+

				                "<div class=\"accordion-inner\">\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">Vendor Name (Optional)</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level vendor-name\" type=\"text\" name=\"secondaryAddress-<%- id %>-vendorName\" placeholder=\"Vendor Name\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">Country</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                //TODO
				                                    "<select class=\"input-block-level address-country\" required=\"required\" name=\"secondaryAddress-<%- id %>-country\">\n"+
				                                    	    "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
				                                            //"<option value=\"US\">United States</option>\n");
															for (int x = 0; x < arrayCountryCode.length; x++) {
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
															}
				                                    response.write("</select>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">Address 1</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level header-input address1\" required type=\"text\" name=\"secondaryAddress-<%- id %>-Address1\" placeholder=\"Street Name\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level address2\" type=\"text\" name=\"secondaryAddress-<%- id %>-Address2\" placeholder=\"Building or Unit Number\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level address3\" type=\"text\" name=\"secondaryAddress-<%- id %>-Address3\" placeholder=\"Suite or Room Number\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid validate-group\">\n"+

				                    	"<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">City</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level city\" required type=\"text\" name=\"secondaryAddress-<%- id %>-City\" placeholder=\"City\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label\">State<span class=\"province-label hide\">/Province</span></label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<select class=\"input-block-level state\" name=\"secondaryAddress-<%- id %>-State\">\n"+
				                                        "<option value=\"\">Select State</option>\n"+
				                                        "<option>CA</option>\n"+
				                                    "</select>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label zip\" name=\"secondary<%- id %>Zip\">Zip<span class=\"postal-code-label hide\">/Postal</span> Code</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level zip\" type=\"text\" required name=\"secondaryAddress-<%- id %>-Zip\" placeholder=\"Zip-Code\">\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"remit-option\">\n"+
				                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                            "<button type=\"button\" class=\"btn btn-info remit-button\"><i class=\"icon-check-sign\"></i>Remit</button>\n"+
				                            "<button type=\"button\" class=\"btn btn-info purchasing-button\"><i class=\"icon-check-sign\"></i>Purchasing</button>\n"+
				                            "<div class=\"hidden-form-elements\">\n"+
				                                "<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-<%- id %>-RemitPurchase\" value=\"remit\" />\n"+
				                                "<input type=\"radio\" class=\"purchasing\" name=\"secondaryAddress-<%- id %>-RemitPurchase\" value=\"purchasing\" />\n"+
				                            "</div>\n"+
				                            "<a href=\"#\" class=\"tip\" data-html=\"true\" data-placement=\"top\" data-title=\"<label>Remit</label><p>Decription of what a remit address is.</p><label>Purchasing</label><p>Decription of what a purchasing address is.</p>\">What is the difference?</a>\n"+
				                        "</div>\n"+
				                        "<div class=\"purchasing-contact\">\n"+
				                            "<span class=\"caret\"></span>\n"+
				                            "<div class=\"row-fluid\">\n"+
				                                "<div class=\"span6\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Email <i class=\"icon-exclamation-sign tip\" data-placement=\"right\" title=\"Make sure this is the correct!\"></i></label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<input class=\"input-block-level purchasing-email\" type=\"email\" name=\"secondaryAddress-<%- id %>-purchasingEmail\" placeholder=\"person@email.com\">\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                                "<div class=\"span6\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Fax</label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<input class=\"input-block-level fax phone-number\" type=\"text\" name=\"secondaryAddress-<%- id %>-purchasingFax\" placeholder=\"(XXX) XXX-XXXX\">\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
				"\n"+
				    "<script id=\"banking-usach\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Routing #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
				    "<script id=\"banking-uswire\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Routing #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+

				    "<script id=\"banking-camx\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+

				    "<script id=\"banking-aunz\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">BSB #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">IBAN #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+

				    "<script id=\"banking-others\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Key</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">IBAN #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+

				    "<script src=\"/"+ServerString+"/js/jquery.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/extras/modernizr-custom.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/extras/mousepress.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/polyfiller.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/underscore-min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/backbone-min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/bootstrap.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/spritely.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.form.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.maskedinput.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/common.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/vendor.js\"></script>\n"+

				    "<script>\n");
				    	
				      if (requestID != null) {
				    	  response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"maintain\" }); // page is in maintain mode\n");			    	  
				      } else {
				    	  response.write("app.page.registrationView = new app.views.RegistrationView(); // default configuration\n");			    	  
				      }
				      
				      //"app.page.registrationView = new app.views.RegistrationView({ mode: \"locked\" }); // page is resubmitted\n"+
				    response.write("</script>\n");
				    		
				        if ((request.getServletRequest().getServerName().equalsIgnoreCase("coding.nbcuni.com") || (request.getServletRequest().getServerName().equalsIgnoreCase("vendor.nbcuni.com")))) {
				          response.write("<script src=\"/"+ServerString+"/js/sap_portal_omniture.js\"></script>\n");
		                  response.write("<script  type='text/javascript'>\n"+
								"//Omniture Code start\n"+
								"s.pageName='VRA Registration';\n"+  // Enter Page name to be tracked
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