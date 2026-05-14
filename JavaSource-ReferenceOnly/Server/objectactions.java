package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.html5_vra.portalservices.tools.kmlogger;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class objectactions extends AbstractPortalComponent
{
	
	
	
    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    	String returnCode = "";
    	String returnStatus = "";
    	String resultAction = "";
    	String windowMessage = "";
    	String result = "";
    	String strOutput = "";
    	String errorRFCMessage = "";
    	String requestId = request.getParameter("requestId");
		String action = request.getParameter("actionCode");
		String WFSystemAlias = "SAP_R3";
		String stringTester = "";
		String messageNumber = "";

    	try {
			
    		HttpServletResponse resp = request.getServletResponse(true);
    		IPortalComponentProfile profile = request.getComponentContext().getProfile();  		
		
    		PrintWriter response = resp.getWriter();
			String sampleReturn = "";
			String subSystemString = "";

			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	
			
			//Get User ID
			String userId = request.getUser().getName();
			String userEmail = request.getUser().getEmail();

			// Added begin of code - Pranesh(04/27/2016) - 
				boolean userIsInternalEmployeeBuyer = false;
			// Added end of code   - Pranesh(04/27/2016) - 
			
			//Logic
			returnCode = "0";
			try {

				//get a client service
				IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
				JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
				IRepository m_Repository = JCO.createRepository("repository", client);	
		
				// connect to SAP system
				client.connect();				
				
				// To get user role
				// Added begin of code - Pranesh(04/27/2016) - ENHC0018725  
				
				IFunctionTemplate Z_SF_I477_GET_USER_ROLES = m_Repository.getFunctionTemplate("Z_SF_I477_GET_USER_ROLES");

				JCO.Function functionRoles = new JCO.Function(Z_SF_I477_GET_USER_ROLES);
				JCO.ParameterList importListRoles = functionRoles.getImportParameterList();
				importListRoles.setValue(userId, "I_SSO_ID");
 
				client.execute(functionRoles);
 
				JCO.Table retUserRoles =  functionRoles.getTableParameterList().getTable("T_ROLES");	
				
				for(int i = 0; i < retUserRoles.getNumRows(); i++) {
					// Begin of Insert by Naga ENHC0016164
					if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
					}else{
						userIsInternalEmployeeBuyer = false;
					}
					// End of Insert by Naga 
					retUserRoles.nextRow();
				}
				
				// Added End of code - Pranesh(04/27/2016)- ENHC0018725  
				
				
				
				// TODO: Submit Invite
				if (action.equalsIgnoreCase("submit_invite")){
					
						String strRequestNumber = "";
					    String ersValue = "";
					    String ersTerms = "";
						String userType = request.getParameter("userType");	
						String subCategory = request.getParameter("subCategory");	//ENHC0021830 
					    String valMaximo = request.getParameter("maximo");
					    String valEatec = request.getParameter("eatec");
					    String valJda = request.getParameter("jda");
					    String valCostar = request.getParameter("costar");
					    String valVista = request.getParameter("vista");
					    String valCompass = request.getParameter("compass");
					    String valAim = request.getParameter("AIM");//ENHC0025368
					    String valParis = request.getParameter("paris");
					    String valGarnishment = request.getParameter("garnishment");
					    String valTrisepts = request.getParameter("trisepts");
					    String valCraftsynetsuite = request.getParameter("craftsynetsuite");
					    String ersFormValue = request.getParameter("ers");
						String vendorName = request.getParameter("vendorName");
					    String vendorLanguage = request.getParameter("vendorLanguage");
					    String vendorCountry = request.getParameter("vendorCountry");
					    String contactFirstName = request.getParameter("contactFirstName");
					    String contactLastName = request.getParameter("contactLastName");
					    String contactEmail = request.getParameter("contactEmail");
					    String ersYesTerms = request.getParameter("ersYesTerms");
					    String ersNoTerms = request.getParameter("ersNoTerms");
					    String annualSpend = request.getParameter("annualSpend");
					    String comments = request.getParameter("comments");
					    String vendorType = request.getParameter("vendorType");
					    String oneTimeVendorType = request.getParameter("subVendorType");	// ENHC0016461
					    String contactPhone = request.getParameter("contactPhone");
					    String approverSSO = request.getParameter("selectedApprover");
						//Req#100 START - Code added by AGAMPA 18-Feb-2015
					    String requestedFor = request.getParameter("requestedFor");					    
						//Req#100 END					    
					    
					 // start DFCT0017114 removal of Special chars and space     
					    contactPhone=contactPhone.replace("-", "");
					    contactPhone=contactPhone.replace("(", "");
					    contactPhone=contactPhone.replace(")", "");
					    // end DFCT0017114 removal of Special chars and space   

					    
					    // Added - Pranesh(04/20/2016) - ENHC0018725  
					    	/*
					    	 *  Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
					    		String inviteSourcingRelevantTag="";
								inviteSourcingRelevantTag=request.getParameter("srcstatus");
							*/
						// Added - Pranesh(04/20/2016) - ENHC0018725  

							
					    ArrayList<String> companyCodes = new ArrayList<String>();
					    
					    String inviteNumber = request.getParameter("invitenum");	// ENHC0025336 Changed from inviteNumber to invitenum
					    try {
					    	String[] urlParamters = request.getServletRequest().getParameterValues("companyCodes");						    	
					    
					    
						     for (int i = 0; i < urlParamters.length; i++) {
							     companyCodes.add(urlParamters[i]);					     
							 }
						     
						     
					    } catch (Exception coCodeEx){			    	
					    	throw new Exception ("Company Code Required");						    	
					    }

					    
					    if(ersFormValue != null){
						    if(ersFormValue.equalsIgnoreCase("yes")){
						    	ersValue = "X";	
						    	ersTerms = ersYesTerms;
						    } else {
						    	ersValue = "";
						    	ersTerms = ersNoTerms;	
						    }
					    }
					    
						//Build SubSystem String
					    if (valMaximo != null)
					    	if (valMaximo.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"T"; 

					    if (valEatec != null)
					    	if (valEatec.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"F"; 
					    
					    if (valJda != null)
					    	if (valJda.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"M"; 

					    if (valCostar != null)
					    	if (valCostar.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"C"; 					    

					    if (valVista != null)
					    	if (valVista.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"V"; 						    
	
					    if (valCompass != null)
					    	if (valCompass.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"P"; 	
					    
					    if (valAim != null)//ENHC0025368
					    	if (valAim.equalsIgnoreCase("on"))//ENHC0025368
					    		subSystemString = subSystemString+"J"; 					    
	
					    if (valParis != null)
					    	if (valParis.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"R"; 		
	
					    if (valGarnishment != null)
					    	if (valGarnishment.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"G"; 						    

					    if (valTrisepts != null)
					    	if (valTrisepts.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"S"; 	
					    
					    if (valCraftsynetsuite != null)
					    	if (valCraftsynetsuite.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"N"; 	
					    
						IFunctionTemplate functionTemplate = m_Repository.getFunctionTemplate("Z_SFI_I507_VRA_VENDOR_INVITE");
			
						JCO.Function function = new JCO.Function(functionTemplate);
						JCO.ParameterList importList= function.getImportParameterList();
						JCO.Structure importTable = function.getImportParameterList().getStructure("ES_INVITE");
						JCO.Table importCompanyCodeTable = function.getImportParameterList().getTable("ET_CCODE");
						JCO.Structure retReturn = function.getExportParameterList().getStructure("IT_RETURN");						
						
						importList.setValue(contactEmail, "E_EMAIL");	
						
						if(inviteNumber != null && inviteNumber.trim().length()>0)
						{
							importTable.setValue(inviteNumber, "ZZSF_VRA_EMLID");
						}
						
						importTable.setValue(vendorName, "VEND_NAME");
						importTable.setValue(userId, "INVITE_SSO");						
						importTable.setValue(vendorLanguage, "SPRAS");
						importTable.setValue(vendorCountry, "LAND1");
						importTable.setValue(contactFirstName, "FIRST_NAME");
						importTable.setValue(contactLastName, "LAST_NAME");		// ganesh								
						importTable.setValue(contactPhone, "TELEPHONE");
					
						// Added - Pranesh (04/27/2016) - ENHC0018725
							/*
							 Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
							if(userIsInternalEmployeeBuyer){
								if(inviteSourcingRelevantTag.equals("Y")){
									//importTable.setValue("Y", "KONZS"); // Blocked -Pranesh(04/29/2016)
									importTable.setValue("SOURCING", "KONZS");
							}else{
									//importTable.setValue("N", "KONZS"); // Blocked -Pranesh(04/29/2016)
									importTable.setValue("", "KONZS");
								}	
							}else{
								//importTable.setValue("N", "KONZS");     // Blocked -Pranesh(04/29/2016)
								importTable.setValue("", "KONZS");
							}
							*/
						// Added - Pranesh (04/27/2016) - ENHC0018725
						
						importTable.setValue(ersValue, "XERSY");
						importTable.setValue(ersTerms, "ZTERM");
						if(annualSpend!=null)//DFCT0019308
						importTable.setValue(replaceSplChar(annualSpend), "ANNUAL_SPEND");// DFCT0018632
						importTable.setValue(comments, "INVCOMMENT");
						// Begin of Comment and Insert by Naga ENHC0016461
//						importTable.setValue(vendorType, "VEND_TYPE");
						if(!vendorType.equals("999")){
							importTable.setValue(vendorType, "VEND_TYPE");
						}else{
							importTable.setValue(oneTimeVendorType, "VEND_TYPE");
						}		
						if(subCategory!=null && vendorType.equals("018"))// ENHC0021830 
						{
							importTable.setValue(subCategory, "SUB_CAT_ID");
						}
						// End of Comment and Insert by Naga
						importTable.setValue(subSystemString, "J_1KFREPRE");
						importTable.setValue(userType, "ZZSF_VRA_VENDCAT");						
						importTable.setValue("1", "SOURCE");
						importTable.setValue(approverSSO, "APPROVER_SSO");		
							
						//Req#600 START - Code added by AGAMPA 18-Feb-2015
						if(requestedFor != null && requestedFor.length() > 0)
							importTable.setValue(requestedFor, "REQUESTED_FOR");
						//Req#600 END
					
						//Loop Through Company Codes
						
						for (int i = 0; i < companyCodes.size(); i++) {
							importCompanyCodeTable.appendRow();
							importCompanyCodeTable.setValue(companyCodes.get(i).toUpperCase(), "BUKRS");								
						}
						
			 	 		client.execute(function);
			 	 		
			 	 		resultAction = "Invite Submitted for Approval";
			 	 		
			 	 		//Return
			 	 		returnStatus = (String)retReturn.getValue("TYPE"); // Naga ENHC0015302
			 	 		if (retReturn.getString("TYPE").equalsIgnoreCase("S")){
			 	 			strRequestNumber = retReturn.getString("MESSAGE_V1");		 	 		
			 	 		} else {
			 	 			errorRFCMessage = retReturn.getString("MESSAGE");	
			 	 			returnCode = "1";
			 	 			
			 	 		}
			 	 	resultAction = strRequestNumber;
			 	 	
				//
			 	 // TODO : Submit Pre Req				 	 		
				} else if (action.equalsIgnoreCase("submit_reqpreform")){
					
					    String strRequestNumber = "";
						String ersValue = "";
					    String ersTerms = "";
						String userType = request.getParameter("userType");	
						String subCategory = request.getParameter("subCategory");	//ENHC0021830 
					    String valMaximo = request.getParameter("maximo");
					    String valEatec = request.getParameter("eatec");
					    String valJda = request.getParameter("jda");
					    String valCostar = request.getParameter("costar");
					    String valVista = request.getParameter("vista");
					    String valCompass = request.getParameter("compass");
					    String valAim = request.getParameter("AIM");//ENHC0025368
					    String valParis = request.getParameter("paris");
					    String valGarnishment = request.getParameter("garnishment");
					    String valTrisepts = request.getParameter("trisepts");
					    String valCraftsynetsuite = request.getParameter("craftsynetsuite");
					    String ersFormValue = request.getParameter("ers");
						String vendorName = request.getParameter("vendorName");
					    String vendorLanguage = request.getParameter("vendorLanguage");
					    String vendorCountry = request.getParameter("vendorCountry");
					    String contactFirstName = request.getParameter("contactFirstName");
					    String contactLastName = request.getParameter("contactLastName");
					    String contactEmail = request.getParameter("contactEmail");
					    String ersYesTerms = request.getParameter("ersYesTerms");
					    String ersNoTerms = request.getParameter("ersNoTerms");
					    String annualSpend = request.getParameter("annualSpend");
					    String comments = request.getParameter("comments");
					    String vendorType = request.getParameter("vendorType");
					    String oneTimeVendorType = request.getParameter("subVendorType");
					    String contactPhone = request.getParameter("contactPhone");
					    String requestType = request.getParameter("requestType");
					    String approverSSO = request.getParameter("selectedApprover");
					    ArrayList<String> companyCodes = new ArrayList<String>();
						//Req#100 START - Code added by AGAMPA 18-Feb-2015
					    String requestedFor = request.getParameter("requestedFor");					    
						//Req#100 END						    
					    try {
					    	String[] urlParamters = request.getServletRequest().getParameterValues("companyCodes");						    	
					    
					    
						     for (int i = 0; i < urlParamters.length; i++) {
							     companyCodes.add(urlParamters[i]);					     
							 }
						     
						     
					    } catch (Exception coCodeEx){			    	
					    	throw new Exception ("Company Code Required");						    	
					    }

					    
					    if(ersFormValue != null){
						    if(ersFormValue.equalsIgnoreCase("yes")){
						    	ersValue = "X";	
						    	ersTerms = ersYesTerms;
						    } else {
						    	ersValue = "";
						    	ersTerms = ersNoTerms;	
						    }
					    }
					    
						//Build SubSystem String
					    if (valMaximo != null)
					    	if (valMaximo.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"T"; 

					    if (valEatec != null)
					    	if (valEatec.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"F"; 
					    
					    if (valJda != null)
					    	if (valJda.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"M"; 

					    if (valCostar != null)
					    	if (valCostar.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"C"; 					    

					    if (valVista != null)
					    	if (valVista.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"V"; 						    
	
					    if (valCompass != null)
					    	if (valCompass.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"P"; 	
					    
					    if (valAim != null)//ENHC0025368
					    	if (valAim.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"J"; 					    
	
					    if (valParis != null)
					    	if (valParis.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"R"; 		
	
					    if (valGarnishment != null)
					    	if (valGarnishment.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"G"; 						    

					    if (valTrisepts != null)
					    	if (valTrisepts.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"S"; 	
					    
					     if (valCraftsynetsuite != null)
					    	if (valCraftsynetsuite.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"N"; 	
					    
						IFunctionTemplate functionTemplate = m_Repository.getFunctionTemplate("Z_SFI_I508_VRA_VENSAVE");
			
						JCO.Function function = new JCO.Function(functionTemplate);
						JCO.ParameterList importList = function.getImportParameterList();

						JCO.Structure retES_REQ = function.getImportParameterList().getStructure("ES_REQ");
						JCO.Table retET_LFA1 = function.getImportParameterList().getTable("ET_LFA1");
						JCO.Table retET_LFB1 = function.getImportParameterList().getTable("ET_LFB1");						
						JCO.Table retET_LFM1 = function.getImportParameterList().getTable("ET_LFM1");
						JCO.Structure retReturn = function.getExportParameterList().getStructure("IS_RETURN");
						
						importList.setValue(userId, "E_SSO");
						
						retES_REQ.setValue(requestType, "REQTY");
						retES_REQ.setValue("S", "STATS");
						retES_REQ.setValue(userEmail, "ADMIN_EMAIL");
//						retES_REQ.setValue(vendorType, "VEND_TYPE");		// ENHC0016461 Redundant code
						retES_REQ.setValue(userId, "ERNAM");
						retES_REQ.setValue(approverSSO, "APPROVER_SSO");	
						retES_REQ.setValue("X", "ZZSF_VRA_TNC");
						//Req#100 START - Code added by AGAMPA 18-Feb-2015
						if(requestedFor != null && requestedFor.length() > 0)
							retES_REQ.setValue(requestedFor, "REQUESTED_FOR");
						//Req#100 END						
						
						// retET_LFA1
						retET_LFA1.appendRow();
						if(vendorName.length()>35)
						{
						retET_LFA1.setValue(vendorName.substring(0, 35), "NAME1");
						retET_LFA1.setValue(vendorName.substring(35, vendorName.length()), "NAME3");
						}
						else{
							retET_LFA1.setValue(vendorName, "NAME1");
	
						}
						retET_LFA1.setValue(subSystemString,"J_1KFREPRE");							
						
						// retET_LFM1
						retET_LFM1.appendRow();						
						retET_LFM1.setValue(ersValue,"XERSY");
						if(subCategory!=null && vendorType.equals("018"))// ENHC0021830 
						{
							retES_REQ.setValue(subCategory, "SUB_CAT_ID");
						}
						// retES_REQ
						if(annualSpend!=null)//DFCT0019308
						retES_REQ.setValue(replaceSplChar(annualSpend), "ANNUAL_SPEND");// DFCT0018632
						retES_REQ.setValue(comments, "COMMENT1");
						// Begin of Comment and Insert by Naga ENHC0016461
//						retES_REQ.setValue(vendorType, "VEND_TYPE");
						
						if(!vendorType.equals("999")){
							retES_REQ.setValue(vendorType, "VEND_TYPE");
						}else{
							retES_REQ.setValue(oneTimeVendorType, "VEND_TYPE");
						}
						// End of Comment and Insert by Naga
						retES_REQ.setValue(userType, "ZZSF_VRA_VENDCAT");

						
						//Loop Through Company Codes
						//retET_LFB1
						for (int i = 0; i < companyCodes.size(); i++) {
							retET_LFB1.appendRow();
							retET_LFB1.setValue(companyCodes.get(i).toUpperCase(), "BUKRS");	
							retET_LFB1.setValue(ersTerms, "ZTERM");								
						}	

						//retET_LFM1
						for (int i = 0; i < companyCodes.size(); i++) {
							retET_LFM1.appendRow();
							retET_LFM1.setValue(ersTerms, "ZTERM");								
						}
						
			 	 		client.execute(function);
			 	 		
						//Return
			 	 		returnStatus = (String)retReturn.getValue("TYPE");	//Naga ENHC0015302
			 	 		if (retReturn.getString("TYPE").equalsIgnoreCase("S")){
			 	 			strRequestNumber = retReturn.getString("MESSAGE_V1");		 	 		
			 	 		} else {
			 	 			errorRFCMessage = retReturn.getString("MESSAGE");	
			 	 			returnCode = "1";
			 	 			
			 	 		}
			 	 		
			 	 		
			 	 		strOutput = strOutput +"-"+retES_REQ;
			 	 		strOutput = strOutput +"-"+retReturn;
			 	 		resultAction = strRequestNumber;
			 	 		
			 	 // TODO : Save for Later	
				} else if (action.equalsIgnoreCase("save")){
					
						ArrayList<String> arraySecondaryAddress = new ArrayList<String>();
						ArrayList<String> arrayContactAddress = new ArrayList<String>();
						ArrayList<String> arraySecondaryBankingAddress = new ArrayList<String>();
						ArrayList<String> arrayPaymentNotificationEmail = new ArrayList<String>();
						
						Enumeration<String> urlParamters = request.getServletRequest().getParameterNames();

						//Count Number of Secondary Addresses
						while (urlParamters.hasMoreElements()){
							String key = (String)urlParamters.nextElement(); 
							
							if (key.startsWith("emailContact-")){
								String s = key;
								s = s.substring(s.indexOf("emailContact-") + 13);
								//s = s.substring(0, s.indexOf("-"));
								
								if (!arrayPaymentNotificationEmail.contains(s))
								
									arrayPaymentNotificationEmail.add(s);	
								
							} else if (key.startsWith("secondaryAddress-view")){
								String s = key;
								s = s.substring(s.indexOf("secondaryAddress-view") + 21);
								s = s.substring(0, s.indexOf("-"));
								
								if (!arraySecondaryAddress.contains(s))
									arraySecondaryAddress.add(s);								
							
							} else if (key.startsWith("contact-view")){
								String s = key;
								s = s.substring(s.indexOf("contact-view") + 12);
								s = s.substring(0, s.indexOf("-"));
								
								if (!arrayContactAddress.contains(s))
									arrayContactAddress.add(s);	
								
							}  else if (key.startsWith("banking-view")){
								String s = key;
								s = s.substring(s.indexOf("banking-view") + 0);
								s = s.substring(0, s.indexOf("-"));
								
								if (!arraySecondaryBankingAddress.contains(s))
									arraySecondaryBankingAddress.add(s);								
							} 
							
						}						
						
						String acceptPO = request.getParameter("acceptPO");
						if (acceptPO == null){
							acceptPO = "";
						} else {
							acceptPO = "X";
						}

						String ersValue = request.getParameter("ersValue");
						String userType = request.getParameter("userType");
						String subCategory = request.getParameter("subCategory");	//ENHC0021830 
//						String subSystems = request.getParameter("subSystems"); ENHC0013668
						String companyCodes = request.getParameter("arrayCompanyCodes"); 
						String requestType = request.getParameter("requestType"); 						
						String actionType = request.getParameter("action"); 
						String vendorId = request.getParameter("vendorId");
						String documentType = request.getParameter("documentType");
						String legalName = request.getParameter("legalName");
						String invoicingName = request.getParameter("invoicingName");
						String minorityCode = request.getParameter("minorityCode");
						String industryCode = request.getParameter("industryCode");
						String poEmail = request.getParameter("poEmail");
						String basicQ1 = request.getParameter("basicQ1");
						String basicQ2 = request.getParameter("basicQ2");
						String basicQ3 = request.getParameter("basicQ3");
						String basicQ4 = request.getParameter("basicQ4");
						String basicQ1Describe = request.getParameter("basicQ1Describe");
						String basicQ2Describe = request.getParameter("basicQ2Describe");
						String basicQ3Describe = request.getParameter("basicQ3Describe");
						String basicQ4Describe = request.getParameter("basicQ4Describe");
						String taxExempt = request.getParameter("taxExempt");
						String organizationFocus = request.getParameter("organizationFocus");
						String companyScale = request.getParameter("companyScale");
						String FPNA = request.getParameter("FPNA");
						String organizationDescription = request.getParameter("organizationDescription");
						String bodDiveristyAmericanIndian = request.getParameter("bodDiveristyAmericanIndian");
						String bodDiversityPacificIslander = request.getParameter("bodDiversityPacificIslander");
						String bodDiversityAfricanAmerican = request.getParameter("bodDiversityAfricanAmerican");
						String bodDiversityHispanic = request.getParameter("bodDiversityHispanic");
						String bodDiversityWhite = request.getParameter("bodDiversityWhite");
						String bodDiversityWomen = request.getParameter("bodDiversityWomen");
						String ssDiversityAmericanIndian = request.getParameter("ssDiversityAmericanIndian");
						String ssDiveristyPacificIslander = request.getParameter("ssDiveristyPacificIslander");
						String ssDiversityAfricanAmerican = request.getParameter("ssDiversityAfricanAmerican");
						String ssDiversityHispanic = request.getParameter("ssDiversityHispanic");
						String ssDiversityWhite = request.getParameter("ssDiversityWhite");
						String ssDiversityWomen = request.getParameter("ssDiversityWomen");
						String msDiversityAmericanIndian = request.getParameter("msDiversityAmericanIndian");
						String msDiversityPacificIslander = request.getParameter("msDiversityPacificIslander");
						String msDiversityAfricanAmerican = request.getParameter("msDiversityAfricanAmerican");
						String msDiversityWomen = request.getParameter("msDiversityWomen");
						String msDiversityDisability = request.getParameter("msDiversityDisability");
						String msDiversityHispanc = request.getParameter("msDiversityHispanc");
						String msDiversityWhite = request.getParameter("msDiversityWhite");
						String msDiversityVetrans = request.getParameter("msDiversityVetrans");
						String msDiversityGay = request.getParameter("msDiversityGay");
						String primaryAddressCountry = request.getParameter("primaryAddressCountry");
						String primaryAddress1 = request.getParameter("primaryAddress1");
						String primaryAddress2 = request.getParameter("primaryAddress2");
						String primaryAddress3 = request.getParameter("primaryAddress3");
						String primaryAddressCity = request.getParameter("primaryAddressCity").trim();
						String primaryAddressState = request.getParameter("primaryAddressState");
						String primaryAddressZip = request.getParameter("primaryAddressZip").trim();
						String taxJurisdiction = request.getParameter("taxCode");
						String taxId1 = request.getParameter("taxId1");//added by ganesh
					//	String taxId1 = request.getParameter("taxId1"); // Naga ENHC0016170 , replaced with a new field
//						String taxId1 = request.getParameter("tax-taxid-id-original"); // Naga ENHC0016170 , replaced with a new field						
						String approverSSO = request.getParameter("selectedApprover");
						String comments = request.getParameter("comments");
						String annualSpend = request.getParameter("annualSpend");
						String w9FileInfo = request.getParameter("w9FileInfo");
						String legalFileInfo = request.getParameter("legalFileInfo");			// ENHC0016461
						String w8FileInfo = request.getParameter("w8FileInfo");			// ENHC0013673					
					    String FileInfo590 = request.getParameter("590FileInfo");
					    String ACHFileInfo = request.getParameter("ACHFileInfo");
					    // Begin of Insert by Naga ENHC0013668
					    String supportdocInfo = request.getParameter("supportdocInfo");
					    String valMaximo = request.getParameter("maximo");
					    String valEatec = request.getParameter("eatec");
					    String valJda = request.getParameter("jda");
					    String valCostar = request.getParameter("costar");
					    String valVista = request.getParameter("vista");
					    String valCompass = request.getParameter("compass");
					    String valAim = request.getParameter("AIM");//ENHC0025368
					    String valParis = request.getParameter("paris");
					    String valGarnishment = request.getParameter("garnishment");
					    String valTrisepts = request.getParameter("trisepts");	
					    String valCraftsynetsuite = request.getParameter("craftsynetsuite");
					    // End of Insert by Naga
						
						if (taxId1 != null )
							taxId1 = taxId1.replaceAll("-", "");							
						
						if (vendorId.equalsIgnoreCase("null"))
							vendorId = "";
							
						
						//Change by CMG March 13
						//String recepientType = request.getParameter("recepientType"); 
						String recepientType = request.getParameter("taxRecipientType");
						
						
						//Req#603 START Code Change by AGAMPA
//						String tax_exempt = request.getParameter("tax_exempt"); 
//						String tax_facta = request.getParameter("tax_facta");
//						String ssn_exempt = request.getParameter("ssn_exempt"); 
//						String ssn_facta = request.getParameter("ssn_facta"); 
						String exempt = request.getParameter("exempt"); 
						String facta = request.getParameter("facta");
						//Req#603 END
						String independantContractor = request.getParameter("independantContractor");
						String taxSsn = request.getParameter("taxSsn");
						if (taxSsn != null ){
							taxSsn = taxSsn.replaceAll("-", "");
							taxSsn = taxSsn.replaceAll("_", "");
						}
						
						// Begin of Comment and Insert by Naga ENHC0016170
						// New field will have the correct value
						
						String ssn1 = request.getParameter("ssn1");
//						String ssn1 = request.getParameter("tax-social-id-original");
						// End of Comment and Insert by Naga
						if (ssn1 != null ){
							ssn1 = ssn1.replaceAll("-", "");	
							ssn1 = ssn1.replaceAll("_", "");							
						}
						String terms = request.getParameter("terms");
						if (terms != null )
							terms = terms.replaceAll("-", "");	
						
						String vendorType = request.getParameter("vendorType");
						String taxContractorQ1 = request.getParameter("taxContractorQ1");
						String taxContractorQ2 = request.getParameter("taxContractorQ2");
						String taxContractorQ3 = request.getParameter("taxContractorQ3");
						String taxContractorQ4 = request.getParameter("taxContractorQ4");
						String taxContractorQ5 = request.getParameter("taxContractorQ5");
						String taxContractorQ6 = request.getParameter("taxContractorQ6");
						String taxContractorQ7 = request.getParameter("taxContractorQ7");
						String taxContractorQ8 = request.getParameter("taxContractorQ8");
						String taxContractorQ9 = request.getParameter("taxContractorQ9");
						String taxContractorQ10 = request.getParameter("taxContractorQ10");
						String taxContractorQ11 = request.getParameter("taxContractorQ11");
						String taxContractorQ12 = request.getParameter("taxContractorQ12");
						String taxContractorQ13 = request.getParameter("taxContractorQ13");
						String taxContractorQ14 = request.getParameter("taxContractorQ14");
						String taxContractorQ15 = request.getParameter("taxContractorQ15");
						String taxContractorQ16 = request.getParameter("taxContractorQ16");
						String taxContractorQ17 = request.getParameter("taxContractorQ17");
						String taxContractorQ18 = request.getParameter("taxContractorQ18");
						String taxContractorQ19 = request.getParameter("taxContractorQ19");
						String taxContractorQ20 = request.getParameter("taxContractorQ20");
						String taxContractorQ21 = request.getParameter("taxContractorQ21");						
						String taxContractorQ30 = request.getParameter("taxContractorQ30");	
						String taxContractorQ31 = request.getParameter("taxContractorQ31");	
						String taxContractorQ32 = request.getParameter("taxContractorQ32");	
						String taxContractorQ33 = request.getParameter("taxContractorQ33");							
						String taxContractorQ34 = request.getParameter("taxContractorQ34");							
						String taxContractorQ35 = request.getParameter("taxContractorQ35");	
						String taxSsnQ1 = request.getParameter("taxSsnQ1");
						String taxSsnQ2 = request.getParameter("taxSsnQ2");
						String taxSsnQ3 = request.getParameter("taxSsnQ3");
						String taxSsnQ4 = request.getParameter("taxSsnQ4");
						String taxSsnQ5 = request.getParameter("taxSsnQ5");
						String taxSsnQ6 = request.getParameter("taxSsnQ6");
						String taxSsnQ7 = request.getParameter("taxSsnQ7");
						String taxSsnQ8 = request.getParameter("taxSsnQ8");
						String taxSsnQ9 = request.getParameter("taxSsnQ9");
						String taxSsnQ10 = request.getParameter("taxSsnQ10");
						String taxSsnQ11 = request.getParameter("taxSsnQ11");
						String taxSsnQ12 = request.getParameter("taxSsnQ12");
						String taxSsnQ13 = request.getParameter("taxSsnQ13");
						String taxSsnQ14 = request.getParameter("taxSsnQ14");
						String taxSsnQ15 = request.getParameter("taxSsnQ15");
						String taxSsnQ16 = request.getParameter("taxSsnQ16");
						String taxSsnQ17 = request.getParameter("taxSsnQ17");
						String taxSsnQ18 = request.getParameter("taxSsnQ18");
						String taxSsnQ19 = request.getParameter("taxSsnQ19");
						String taxSsnQ20 = request.getParameter("taxSsnQ20");
						String taxSsnQ21 = request.getParameter("taxSsnQ21");
						String taxSsnQ30 = request.getParameter("taxSsnQ30");
						String taxSsnQ31 = request.getParameter("taxSsnQ31");
						String taxSsnQ32 = request.getParameter("taxSsnQ32");
						String taxSsnQ33 = request.getParameter("taxSsnQ33");
						String taxSsnQ34 = request.getParameter("taxSsnQ34");
						String taxSsnQ35 = request.getParameter("taxSsnQ35");						
						//Begin of Insert CTI w8 Foreign vendor
						String vendEntQ1 = request.getParameter("vendorEntity");
						String vendEntQ2 = request.getParameter("vendorEntityLoc");
						String vendEntQ3 = request.getParameter("vendorIndvLoc");
						String vendEntQ4 = request.getParameter("vendorIndvResidence");
						String vendEntQ5 = request.getParameter("vendorIndvPresence");
						String vendEntQ6 = request.getParameter("w8JustOnBehalfVen");
						String vendTaxResCountry = request.getParameter("taxResidenceCountry");
						
						// Added - Pranesh(04/20/2016) - ENHC0018725  
						
							/*
							 *  Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
								String sourcingRelevant="";
								sourcingRelevant=request.getParameter("sourcingrelevant");
							 */
						
						// Added - Pranesh(04/20/2016) - ENHC0018725 
						
						//End of Insert CTI w8 Foreign vendor
						String bankingPrimaryType = request.getParameter("bankingPrimaryType");
						String primary_account_country = request.getParameter("primary-account-country");
						String banking_primary_RoutingNum = request.getParameter("banking-primary-RoutingNum");
						if(banking_primary_RoutingNum!=null)
							banking_primary_RoutingNum=splCharRemoval(banking_primary_RoutingNum);
						// Begin of Comment and Insert by Naga ENHC0016170
						// Reverting this for some time
						// Change in the field name holding the actual value
						String banking_primary_AccountNum = replaceSpace(request.getParameter("hidden-banking"));//  DFCT0018702 ganesh	
						if(banking_primary_AccountNum!=null)
							banking_primary_AccountNum=splCharRemoval(banking_primary_AccountNum);
						// End of Comment and Insert by Naga 
						String banking_primary_SwiftNum = request.getParameter("banking-primary-SwiftNum");
						String banking_primary_HolderName = request.getParameter("banking-primary-HolderName");
						String banking_primary_IbanNum = request.getParameter("banking-primary-IbanNum");
						String primary_int_currency1 = request.getParameter("primary-int-currency1");
						String primary_int_country1 = request.getParameter("primary-int-country1");
						String primary_int_account1 = request.getParameter("primary-int-account1");	
						//Modified CGUTJAHR : VeRA Enhancement #41
						String primaryBankingType = request.getParameter("primaryBankingType"); 
						// END
						//Req#100 START - Code added by AGAMPA 18-Feb-2015
					    String requestedFor = request.getParameter("requestedFor");					    
						//Req#100 END
					    
						IFunctionTemplate functionTemplate = m_Repository.getFunctionTemplate("Z_SFI_I508_VRA_VENSAVE");
			
						JCO.Function function = new JCO.Function(functionTemplate);
						JCO.ParameterList importList= function.getImportParameterList();
						JCO.Structure retES_REQ = function.getImportParameterList().getStructure("ES_REQ");
						JCO.Table retET_LFA1 = function.getImportParameterList().getTable("ET_LFA1");
						//JCO.Table retET_REQ = function.getImportParameterList().getTable("ET_REQ");
						JCO.Table retET_ADR6 = function.getImportParameterList().getTable("ET_ADR6");
						JCO.Table retET_ANSWER = function.getImportParameterList().getTable("ET_ANSWER");
						JCO.Table retET_LFB1 = function.getImportParameterList().getTable("ET_LFB1");;
						JCO.Table retET_IBAN = function.getImportParameterList().getTable("ET_IBAN");;			
						JCO.Table retET_TBCN21 = function.getImportParameterList().getTable("ET_TBCN21");
						JCO.Table retET_BNKA = function.getImportParameterList().getTable("ET_BNKA");
						JCO.Table retET_LFBK = function.getImportParameterList().getTable("ET_LFBK");						
						JCO.Table retET_KNVK = function.getImportParameterList().getTable("ET_KNVK");
						JCO.Table retET_LFM1 = function.getImportParameterList().getTable("ET_LFM1");
						JCO.Table retET_FILES = function.getImportParameterList().getTable("ET_FILES");
						
						importList.setValue(userId, "E_SSO");

						// retES_REQ
						String workflowStatus = "W";
						if (actionType.equalsIgnoreCase("save"))
							workflowStatus = "S";
						
						if ( !requestId.equalsIgnoreCase("null"))
							retES_REQ.setValue(requestId, "REQST");
						
						retES_REQ.setValue(workflowStatus, "STATS");					
						retES_REQ.setValue(minorityCode, "VRA_MINDK");
						retES_REQ.setValue(industryCode, "VRA_BRSCH");
						retES_REQ.setValue(userEmail, "ADMIN_EMAIL");
						retES_REQ.setValue(recepientType, "ZZSF_VRA_QSREC");
						retES_REQ.setValue(vendorType, "VEND_TYPE");
						if(subCategory!=null && vendorType.equals("018"))// ENHC0021830 
						{
							retES_REQ.setValue(subCategory, "SUB_CAT_ID");
						}
						retES_REQ.setValue(acceptPO, "ZZSF_VRA_PORECV");	
						retES_REQ.setValue(userId, "ERNAM");
						retES_REQ.setValue(userType, "ZZSF_VRA_VENDCAT");
						retES_REQ.setValue(approverSSO, "APPROVER_SSO");
						if(annualSpend!=null)//DFCT0019308
						retES_REQ.setValue(replaceSplChar(annualSpend), "ANNUAL_SPEND");// DFCT0018632
						retES_REQ.setValue(comments, "COMMENT1");
						retES_REQ.setValue("X", "ZZSF_VRA_TNC");
						
						
						// Blocked by Pranesh - (04/28/2016) - Its not required (dn't unblock)
						/*Pranesh - (04/27/2016) - ENHC0018725 
						if(userIsInternalEmployeeBuyer){
							if(sourcingRelevant.equals("Y")){
								retES_REQ.setValue("Y", "KONZS");
							}else{
								retES_REQ.setValue("N", "KONZS");
							}	
						}else{
							retES_REQ.setValue("N", "KONZS");
						}
					   Pranesh - (04/27/2016) - ENHC0018725*/
						
						
						//Req#100 START - Code added by AGAMPA 18-Feb-2015
						if(requestedFor != null && requestedFor.length() > 0)
							retES_REQ.setValue(requestedFor, "REQUESTED_FOR");
						//Req#100 END							
						// Value for Request Type
						retES_REQ.setValue(requestType, "REQTY");						
						//Req#603 START Code changed by AGAMPA 24Feb2015
//						if (tax_exempt != null)
//							retES_REQ.setValue(tax_exempt, "ZZSF_VRA_EXMPTPC");						
//
//						if (tax_facta != null)
//							retES_REQ.setValue(tax_facta, "ZZSF_VRA_EXMPTFRC");						
//
//						if (ssn_exempt != null)
//							retES_REQ.setValue(ssn_exempt, "ZZSF_VRA_EXMPTPC");						
//
//						if (ssn_facta != null)
//							retES_REQ.setValue(ssn_facta, "ZZSF_VRA_EXMPTFRC");
						

						if (exempt != null)
							retES_REQ.setValue(exempt, "ZZSF_VRA_EXMPTPC");						

						if (facta != null)
							retES_REQ.setValue(facta, "ZZSF_VRA_EXMPTFRC");				
						//Req#603 END
						strOutput = strOutput +"-"+retES_REQ;
										
						//retET_FILES
						if (w9FileInfo!=null&&w9FileInfo.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(w9FileInfo, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("W9","FILE_TYPE");													
						}
						// Begin of Insert by Naga ENHC0016461
						if (legalFileInfo!=null&&legalFileInfo.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(legalFileInfo, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("LEG","FILE_TYPE");													
						}
						// End of Insert by Naga
						
						// Begin of Insert by Naga ENHC0013668
						if (supportdocInfo!=null&&supportdocInfo.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(supportdocInfo, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("SUP","FILE_TYPE");													
						}
						// End of Insert by Naga						
						

						//Begin of Insert CTI w8 Foreign vendor
						if (w8FileInfo.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(w8FileInfo, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("W8","FILE_TYPE");													
						}


						
						if (FileInfo590.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(FileInfo590, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("590","FILE_TYPE");													
						}
						// Begin of comment by Naga DFCT0013582
						/*if (ACHFileInfo.length()>1){
							retET_FILES.appendRow();						
							retET_FILES.setValue(ACHFileInfo, "OBJECT_ID");
							retET_FILES.setValue(requestId,"REQST");
							retET_FILES.setValue("ACH","FILE_TYPE");													
						} */
						// End of comment by Naga DFCT0013582
						
						//retET_LFA1 - Basic Tab
						retET_LFA1.appendRow();
						// Begin of comment and insert by Naga ENHC0013668
//						retET_LFA1.setValue(subSystems, "J_1KFREPRE");
						//Build SubSystem String
					    if (valMaximo != null)
					    	if (valMaximo.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"T"; 

					    if (valEatec != null)
					    	if (valEatec.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"F"; 
					    
					    if (valJda != null)
					    	if (valJda.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"M"; 

					    if (valCostar != null)
					    	if (valCostar.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"C"; 					    

					    if (valVista != null)
					    	if (valVista.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"V"; 						    
	
					    if (valCompass != null)
					    	if (valCompass.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"P"; 	
					    
					    if (valAim != null)//ENHC0025368
					    	if (valAim.equalsIgnoreCase("on"))//ENHC0025368
					    		subSystemString = subSystemString+"J"; 					    
	
					    if (valParis != null)
					    	if (valParis.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"R"; 		
	
					    if (valGarnishment != null)
					    	if (valGarnishment.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"G"; 						    

					    if (valTrisepts != null)
					    	if (valTrisepts.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"S";	
					    
					    if (valCraftsynetsuite != null)
					    	if (valCraftsynetsuite.equalsIgnoreCase("on"))
					    		subSystemString = subSystemString+"N"; 
					    
						retET_LFA1.setValue(subSystemString, "J_1KFREPRE");
						// End of comment and insert by Naga
						// Added by CGUTJAHR 1/13/15 : Enhancement #41
						if (invoicingName == null)
							invoicingName = "";
						// END
						
						if(invoicingName.length()<1){
							// START  DFCT0016715-legal name truncation# 
							// retET_LFA1.setValue(legalName, "NAME1");		
							List<String> legalNames = new ArrayList<String>(); 
							// If more than 35 characters, split into NAME1 and NAME3
							if(legalName.length()>35)
							{
							legalNames.add(legalName.substring(0,35));
							legalNames.add(legalName.substring(35,legalName.length()));
							retET_LFA1.setValue(legalNames.get(0), "NAME1"); 
							retET_LFA1.setValue(legalNames.get(1), "NAME3");
							} 
							// If less than 35 characters, Only NAME1
							else
							{
							retET_LFA1.setValue(legalName, "NAME1"); 
							}
							// END  DFCT0016715-legal name truncation# 
						} else {
							
							if (invoicingName.length()>35){
								List<String> invoicingNames = new ArrayList<String>();
								// START  DFCT0016715-invoicing name truncation# 
								// If invoicing name is more than 35, split to NAME1 and NAME3
								//int index = 0;
								//while (index<invoicingName.length()) {
								//   invoicingNames.add(invoicingName.substring(index, Math.min(index+30,invoicingName.length())));
								//    index+=35;
								//}
								invoicingNames.add(invoicingName.substring(0,35));
								invoicingNames.add(invoicingName.substring(35,invoicingName.length()));
								// END  DFCT0016715-invoicing name truncation# 
								retET_LFA1.setValue(invoicingNames.get(0), "NAME1");	
								retET_LFA1.setValue(invoicingNames.get(1), "NAME3");
									
							} else {
								retET_LFA1.setValue(invoicingName, "NAME1");								
							}
							
							retET_LFA1.setValue(legalName, "NAME2");
						}
							
						retET_LFA1.setValue(primaryAddress1, "STRAS");
						retET_LFA1.setValue(primaryAddress2, "STR_SUPPL1");
						retET_LFA1.setValue(primaryAddress3, "STR_SUPPL2");						
						retET_LFA1.setValue(primaryAddressCountry, "LAND1");						
						retET_LFA1.setValue(primaryAddressCity, "ORT01");
						retET_LFA1.setValue(primaryAddressState, "REGIO");
						retET_LFA1.setValue(primaryAddressZip, "PSTLZ");
						retET_LFA1.setValue(ssn1, "STCD1");						
						retET_LFA1.setValue(taxId1, "STCD2");
						retET_LFA1.setValue(poEmail, "SMTP_ADDR");	
						retET_LFA1.setValue("0001", "KTOKK");	
						retET_LFA1.setValue(vendorId, "LIFNR");	
						retET_LFA1.setValue(taxJurisdiction, "TXJCD");		
						
						// Pranesh - (04/20/2016) - ENHC0018725 
						
						/*
						 * Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
							if(userIsInternalEmployeeBuyer){
								if(sourcingRelevant.equals("Y")){
									//retET_LFA1.setValue("S", "KONZS");	// Blocked -Pranesh(04/29/2016)
									retET_LFA1.setValue("SOURCING", "KONZS");
								}else{
									//retET_LFA1.setValue("N", "KONZS");	// Blocked -Pranesh(04/29/2016)
									retET_LFA1.setValue("", "KONZS");
								}	
							}else{
								retET_LFA1.setValue("", "KONZS");
							}
						*/
						
						// Pranesh - (04/20/2016) - ENHC0018725	            // Blocked -Pranesh(04/29/2016)
						
						Iterator<String> arraySecondaryAddressIter = arraySecondaryAddress.iterator();

						while(arraySecondaryAddressIter.hasNext()){
							String item = arraySecondaryAddressIter.next();
							retET_LFA1.appendRow();
							String secondaryAddress_view0_type = "R001";// DFCT0019063 added R001 default
							String secondaryAddress_view0_vendorName = request.getParameter("secondaryAddress-view"+item+"-vendorName");
							String secondaryAddress_view0_country = request.getParameter("secondaryAddress-view"+item+"-country");
							String secondaryAddress_view0_Address1 = request.getParameter("secondaryAddress-view"+item+"-Address1");
							String secondaryAddress_view0_Address2 = request.getParameter("secondaryAddress-view"+item+"-Address2");
							String secondaryAddress_view0_Address3 = request.getParameter("secondaryAddress-view"+item+"-Address3");
							String secondaryAddress_view0_City = request.getParameter("secondaryAddress-view"+item+"-City").trim();
							String secondaryAddress_view0_State = request.getParameter("secondaryAddress-view"+item+"-State");
							String secondaryAddress_view0_Province = request.getParameter("secondaryAddress-view"+item+"-Province");
							String secondaryAddress_view0_Zip = request.getParameter("secondaryAddress-view"+item+"-Zip").trim();
							String secondaryAddress_view0_purchasingEmail = request.getParameter("secondaryAddress-view"+item+"-purchasingEmail");
							String secondaryAddress_view0_purchasingFax = request.getParameter("secondaryAddress-view"+item+"-purchasingFax");
							String secondaryAddress_view0_RemitPurchase = request.getParameter("secondaryAddress-view"+item+"-RemitPurchase");
							String secondaryAddress_view0_taxCode = request.getParameter("secondaryAddress-view"+item+"-taxCode");
							String secondaryAddress_view0_VendorId = request.getParameter("secondaryAddress-view"+item+"-VendorId");
							
							

							if (secondaryAddress_view0_RemitPurchase != null){								
								if (secondaryAddress_view0_RemitPurchase.equalsIgnoreCase("purchasing")) {
									secondaryAddress_view0_type = "B001";								
								} else if (secondaryAddress_view0_RemitPurchase.equalsIgnoreCase("remit")) {
									secondaryAddress_view0_type = "R001";
									secondaryAddress_view0_purchasingEmail = "";
									secondaryAddress_view0_purchasingFax = "";
								}
							}
							
							if(secondaryAddress_view0_vendorName.length()>35)
							{
							retET_LFA1.setValue(secondaryAddress_view0_vendorName.substring(0, 35), "NAME1");
							retET_LFA1.setValue(secondaryAddress_view0_vendorName.substring(35, secondaryAddress_view0_vendorName.length()), "NAME3");
							}
							else{
								retET_LFA1.setValue(secondaryAddress_view0_vendorName, "NAME1");
		
							}
//							retET_LFA1.setValue(secondaryAddress_view0_vendorName, "NAME1");
							retET_LFA1.setValue(secondaryAddress_view0_Address1, "STRAS");
							retET_LFA1.setValue(secondaryAddress_view0_Address2, "STR_SUPPL1");
							retET_LFA1.setValue(secondaryAddress_view0_Address3, "STR_SUPPL2");						
							retET_LFA1.setValue(secondaryAddress_view0_country, "LAND1");						
							retET_LFA1.setValue(secondaryAddress_view0_City, "ORT01");
							retET_LFA1.setValue(secondaryAddress_view0_State, "REGIO");
							retET_LFA1.setValue(secondaryAddress_view0_Zip, "PSTLZ");
							retET_LFA1.setValue(secondaryAddress_view0_purchasingEmail, "SMTP_ADDR");								
							retET_LFA1.setValue(secondaryAddress_view0_purchasingFax, "TELFX");
							retET_LFA1.setValue(secondaryAddress_view0_type,"KTOKK");
							retET_LFA1.setValue(secondaryAddress_view0_taxCode,"TXJCD");
							if(secondaryAddress_view0_VendorId == null)
								secondaryAddress_view0_VendorId = "";
							retET_LFA1.setValue(secondaryAddress_view0_VendorId, "LIFNR");	


						}
						
						//retET_KNVK
						Iterator<String> arrayContactAddressIter = arrayContactAddress.iterator();
						
						while(arrayContactAddressIter.hasNext()){
							String item = arrayContactAddressIter.next();
							
							retET_KNVK.appendRow();							
							String contact_view0_Name = request.getParameter("contact-view"+item+"-Name");
							String contact_view0_Email = request.getParameter("contact-view"+item+"-Email");
							String contact_view0_PhoneNum = request.getParameter("contact-view"+item+"-PhoneNum");
							String contact_view0_FaxNum = request.getParameter("contact-view"+item+"-FaxNum");
							String contact_view0_Department = request.getParameter("contact-view"+item+"-Department");

							retET_KNVK.setValue(contact_view0_Name, "NAME1");
							retET_KNVK.setValue(contact_view0_Email, "SMTP_ADDR");
							retET_KNVK.setValue(contact_view0_PhoneNum, "TELF1");
							retET_KNVK.setValue(contact_view0_FaxNum, "FAX_NUMBER");
							retET_KNVK.setValue(contact_view0_Department, "ABTNR");					
						}	
						
						strOutput = strOutput+retET_KNVK;						
						// retET_ANSWER
						if (basicQ1 != null){
							if (basicQ1.equalsIgnoreCase("yes")){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00001", "QSEQNR");	
								retET_ANSWER.setValue("1", "ANSWER");	
								retET_ANSWER.setValue(basicQ1Describe, "ACOMMENT");									
							} else {
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00001", "QSEQNR");	
								retET_ANSWER.setValue("2", "ANSWER");	
								retET_ANSWER.setValue("", "ACOMMENT");									
							}
						}
						
						if (basicQ2 != null){
							if (basicQ2.equalsIgnoreCase("yes")){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00002", "QSEQNR");	
								retET_ANSWER.setValue("1", "ANSWER");	
								retET_ANSWER.setValue(basicQ2Describe, "ACOMMENT");									
							} else {
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00002", "QSEQNR");	
								retET_ANSWER.setValue("2", "ANSWER");	
								retET_ANSWER.setValue("", "ACOMMENT");									
							}									
						}	
						
						if (basicQ3 != null){
							if (basicQ3.equalsIgnoreCase("yes")){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00003", "QSEQNR");	
								retET_ANSWER.setValue("1", "ANSWER");	
								retET_ANSWER.setValue(basicQ3Describe, "ACOMMENT");									
							} else {
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00003", "QSEQNR");	
								retET_ANSWER.setValue("2", "ANSWER");	
								retET_ANSWER.setValue("", "ACOMMENT");									
							}							
						}
						
						if (basicQ4 != null){
							if (basicQ4.equalsIgnoreCase("yes")){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00004", "QSEQNR");	
								retET_ANSWER.setValue("1", "ANSWER");	
								retET_ANSWER.setValue(basicQ4Describe, "ACOMMENT");									
							} else {
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00001", "QGROUP");
								retET_ANSWER.setValue("00004", "QSEQNR");	
								retET_ANSWER.setValue("2", "ANSWER");	
								retET_ANSWER.setValue("", "ACOMMENT");									
							}								
						}							

						if (taxExempt != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00002", "QGROUP");
							retET_ANSWER.setValue("00001", "QSEQNR");	
							retET_ANSWER.setValue(taxExempt, "ANSWER");								
						}
						
						if (organizationFocus != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00002", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");	
							retET_ANSWER.setValue(organizationFocus, "ANSWER");								
						}
						
						if (companyScale != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00002", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");	
							retET_ANSWER.setValue(companyScale, "ANSWER");								
						}

						if (FPNA != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00002", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");	
							retET_ANSWER.setValue(FPNA, "ANSWER");								
						}						

						if (organizationDescription != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00002", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");	
							retET_ANSWER.setValue(organizationDescription, "ACOMMENT");								
						}
						
						if (bodDiveristyAmericanIndian != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00001", "QSEQNR");	
							retET_ANSWER.setValue(bodDiveristyAmericanIndian, "ANSWER");							
						}

						if (bodDiversityPacificIslander != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");	
							retET_ANSWER.setValue(bodDiversityPacificIslander, "ANSWER");							
						}

						if (bodDiversityAfricanAmerican != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");	
							retET_ANSWER.setValue(bodDiversityAfricanAmerican, "ANSWER");							
						}	

						if (bodDiversityHispanic != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");	
							retET_ANSWER.setValue(bodDiversityHispanic, "ANSWER");							
						}

						if (bodDiversityWhite != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");	
							retET_ANSWER.setValue(bodDiversityWhite, "ANSWER");							
						}						

						if (bodDiversityWomen != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00003", "QGROUP");
							retET_ANSWER.setValue("00006", "QSEQNR");	
							retET_ANSWER.setValue(bodDiversityWomen, "ANSWER");							
						}							

						if (ssDiversityAmericanIndian != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00001", "QSEQNR");	
							retET_ANSWER.setValue(ssDiversityAmericanIndian, "ANSWER");							
						}							

						if (ssDiveristyPacificIslander != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");	
							retET_ANSWER.setValue(ssDiveristyPacificIslander, "ANSWER");							
						}

						if (ssDiversityAfricanAmerican != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");	
							retET_ANSWER.setValue(ssDiversityAfricanAmerican, "ANSWER");							
						}	

						if (ssDiversityHispanic != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");	
							retET_ANSWER.setValue(ssDiversityHispanic, "ANSWER");							
						}

						if (ssDiversityWhite != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");	
							retET_ANSWER.setValue(ssDiversityWhite, "ANSWER");							
						}						

						if (ssDiversityWomen != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00004", "QGROUP");
							retET_ANSWER.setValue("00006", "QSEQNR");	
							retET_ANSWER.setValue(ssDiversityWomen, "ANSWER");							
						}							
	
						if (msDiversityAmericanIndian != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00001", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityAmericanIndian, "ANSWER");							
						}							

						if (msDiversityPacificIslander != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityPacificIslander, "ANSWER");							
						}

						if (msDiversityAfricanAmerican != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityAfricanAmerican, "ANSWER");							
						}	

						if (msDiversityHispanc != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityHispanc, "ANSWER");							
						}

						if (msDiversityWhite != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityWhite, "ANSWER");							
						}						

						if (msDiversityWomen != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00006", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityWomen, "ANSWER");							
						}													

						if (msDiversityVetrans != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00007", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityVetrans, "ANSWER");							
						}							

						if (msDiversityDisability != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00008", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityDisability, "ANSWER");							
						}
						
						if (msDiversityGay != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00005", "QGROUP");
							retET_ANSWER.setValue("00009", "QSEQNR");	
							retET_ANSWER.setValue(msDiversityGay, "ANSWER");							
						}						
						
						if (taxId1.length()>1) {

							if (independantContractor  != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00001", "QSEQNR");							
								retET_ANSWER.setValue(independantContractor, "ANSWER");							
							}
						}
						String tempIndependantContractor = "";
						
						if (independantContractor == null ) {
							tempIndependantContractor = "";
						} else {
							tempIndependantContractor = independantContractor;
						}
						
						//if ((ssn1.length()>1) || (tempIndependantContractor.equalsIgnoreCase("1"))){ // Naga ENHC0016123
						if ((ssn1.length()>1) || 
							(tempIndependantContractor.equalsIgnoreCase("1")) || // Naga ENHC0016123
							(tempIndependantContractor.equalsIgnoreCase("2")) || // Naga ENHC0016123
							(tempIndependantContractor.equalsIgnoreCase("3"))){ //  Naga ENHC0016123
							
							if (independantContractor  == null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00001", "QSEQNR");							
								retET_ANSWER.setValue("1", "ANSWER");		
							}
							
							// Added by CG March 16	
							if (taxSsnQ30 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00030", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ30, "ANSWER");							
							} 						
	
							if (taxSsnQ31 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00031", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ31, "ANSWER");							
							} 	
							
							if (taxSsnQ32 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00032", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ32, "ANSWER");							
							} 	
	
							if (taxSsnQ33 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00033", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ33, "ANSWER");							
							} 	
						
							if (taxSsnQ34 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00034", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ34, "ANSWER");							
							} 	
						
							if (taxSsnQ35 != null){
								retET_ANSWER.appendRow();
								retET_ANSWER.setValue("00006", "QGROUP");
								retET_ANSWER.setValue("00035", "QSEQNR");							
								retET_ANSWER.setValue(taxSsnQ35, "ANSWER");							
							} 	
							
							// End Add
								
								
						} else if ((taxId1 == null) && (ssn1.length()<1))  {
							
								if (taxContractorQ1 != null){
									retET_ANSWER.appendRow();
									retET_ANSWER.setValue("00006", "QGROUP");
									retET_ANSWER.setValue("00001", "QSEQNR");							
									retET_ANSWER.setValue(taxContractorQ1, "ANSWER");	
								}
						}

						if (taxContractorQ2 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ2, "ANSWER");							
						}	
						
						if (taxContractorQ3 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ3, "ANSWER");							
						}		
						
						if (taxContractorQ4 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ4, "ANSWER");							
						}
						if (taxContractorQ5 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ5, "ANSWER");							
						}							

						if (taxContractorQ6 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00006", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ6, "ANSWER");							
						}
						
						if (taxContractorQ7 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00007", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ7, "ANSWER");							
						}		
						
						if (taxContractorQ8 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00008", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ8, "ANSWER");							
						}								

						if (taxContractorQ9 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00009", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ9, "ANSWER");							
						}							

						if (taxContractorQ10 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00010", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ10, "ANSWER");							
						}		

						if (taxContractorQ11 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00011", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ11, "ANSWER");							
						}		
						
						if (taxContractorQ12 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00012", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ12, "ANSWER");							
						}											

						if (taxContractorQ13 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00013", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ13, "ANSWER");							
						}		
						
						if (taxContractorQ14 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00014", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ14, "ANSWER");							
						}

						if (taxContractorQ15 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00015", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ15, "ANSWER");							
						}		
						
						if (taxContractorQ16 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00016", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ16, "ANSWER");							
						}											

						if (taxContractorQ17 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00017", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ17, "ANSWER");							
						}		
						
						if (taxContractorQ18 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00018", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ18, "ANSWER");							
						}		

						if (taxContractorQ19 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00019", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ19, "ANSWER");							
						}		
						
						if (taxContractorQ20 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00020", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ20, "ANSWER");							
						}	
						
						if (taxContractorQ21 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00021", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ21, "ANSWER");							
						}
						if (taxContractorQ30 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00030", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ30, "ANSWER");							
						}	
						if (taxContractorQ31 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00031", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ31, "ANSWER");							
						}	
						if (taxContractorQ32 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00032", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ32, "ANSWER");							
						}	
						if (taxContractorQ33 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00033", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ33, "ANSWER");							
						}	
						if (taxContractorQ34 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00034", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ34, "ANSWER");							
						}							
						if (taxContractorQ35 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00035", "QSEQNR");							
							retET_ANSWER.setValue(taxContractorQ35, "ANSWER");							
						}
						
						// Hard Coding the Answer to the default
/*						if (ssn1.length()>1) {
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00001", "QSEQNR");							
							retET_ANSWER.setValue("1", "ANSWER");							
						}*/

						if (taxSsnQ2 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00002", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ2, "ANSWER");							
						} 	
						
						if (taxSsnQ3 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00003", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ3, "ANSWER");							
						}	
						
						if (taxSsnQ4 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00004", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ4, "ANSWER");							
						}	

						if (taxSsnQ5 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00005", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ5, "ANSWER");							
						}						

						if (taxSsnQ6 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00006", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ6, "ANSWER");							
						}	
						
						if (taxSsnQ7 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00007", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ7, "ANSWER");							
						}	
						
						if (taxSsnQ8 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00008", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ8, "ANSWER");							
						}							

						if (taxSsnQ9 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00009", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ9, "ANSWER");							
						}						

						if (taxSsnQ10 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00010", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ10, "ANSWER");							
						}	

						if (taxSsnQ11 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00011", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ11, "ANSWER");							
						}		
						
						if (taxSsnQ12 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00012", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ12, "ANSWER");							
						}											

						if (taxSsnQ13 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00013", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ13, "ANSWER");							
						} 		
						
						if (taxSsnQ14 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00014", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ14, "ANSWER");							
						}

						if (taxSsnQ15 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00015", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ15, "ANSWER");							
						}		
						
						if (taxSsnQ16 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00016", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ16, "ANSWER");							
						}											

						if (taxSsnQ17 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00017", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ17, "ANSWER");							
						}		
						
						if (taxSsnQ18 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00018", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ18, "ANSWER");							
						}		

						if (taxSsnQ19 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00019", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ19, "ANSWER");							
						}		
						
						if (taxSsnQ20 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00020", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ20, "ANSWER");							
						}	
						
						if (taxSsnQ21 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00006", "QGROUP");
							retET_ANSWER.setValue("00021", "QSEQNR");							
							retET_ANSWER.setValue(taxSsnQ21, "ANSWER");							
						} 
						
						//retET_LFB1
						
						//Begin of Insert CTI w8 Foreign vendor
						if (vendEntQ1 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0001", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ1, "ANSWER");
						}
						if (vendEntQ2 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0002", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ2, "ANSWER");
						}
						if (vendEntQ3 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0003", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ3, "ANSWER");
						}
						if (vendEntQ4 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0004", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ4, "ANSWER");
						}
						if (vendEntQ5 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0005", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ5, "ANSWER");
						}
						if (vendEntQ6 != null){
							retET_ANSWER.appendRow();
							retET_ANSWER.setValue("00008", "QGROUP");
							retET_ANSWER.setValue("0006", "QSEQNR");
							retET_ANSWER.setValue(vendEntQ6, "ACOMMENT");
						}
						//End of Insert CTI w8 Foreign vendor
						
						// For each company code
						String values[] = companyCodes.split("-");
						for(int i=0;i<values.length;i++){
							retET_LFB1.appendRow();
							retET_LFB1.setValue(values[i].toUpperCase(), "BUKRS");
							retET_LFB1.setValue(terms, "ZTERM");							
						}
						
						// Modified by CGUTJAHR : 1/15/15 : Enhancement #41
						if (primaryBankingType.equalsIgnoreCase("ACH"))
							primary_account_country = "US";
						
						if ( !primaryBankingType.equalsIgnoreCase("Check") ) {
							// Begin of Comment and Insert Insert by Naga ENHC0013668
//							if (primary_account_country.equalsIgnoreCase("US")){
//								primaryBankingType = "01";
//							} 
							if (primary_account_country.equalsIgnoreCase("US")&&primaryBankingType.equalsIgnoreCase("ACH")){
							primaryBankingType = "01";
							} 
							else if (primary_account_country.equalsIgnoreCase("US")&&primaryBankingType.equalsIgnoreCase("Wire")){
							primaryBankingType = "U01";	
							}
							// End of Comment and Insert Insert by Naga
							else {
								primaryBankingType = "W01";								
							}
							//retET_LFBK
							retET_LFBK.appendRow();
							retET_LFBK.setValue(primary_account_country, "BANKS");						
							retET_LFBK.setValue(banking_primary_RoutingNum, "BANKL");
							retET_LFBK.setValue(banking_primary_AccountNum, "BANKN");	
							retET_LFBK.setValue(primaryBankingType, "BVTYP");						
							retET_LFBK.setValue(banking_primary_HolderName, "KOINH");
							
							// Begin of Insert by Naga DFCT0013582
							if (ACHFileInfo.length()>1){
								retET_FILES.appendRow();						
								retET_FILES.setValue(ACHFileInfo, "OBJECT_ID");
								retET_FILES.setValue(requestId,"REQST");
								retET_FILES.setValue(primaryBankingType,"FILE_TYPE");													
							}
							//End of Insert by Naga DFCT0013582							
							
							//retET_BNKA
							retET_BNKA.appendRow();
							retET_BNKA.setValue(banking_primary_SwiftNum, "SWIFT");
							// Begin of Insert by Naga ENHC0013668
							retET_BNKA.setValue(primary_account_country, "BANKS");
							retET_BNKA.setValue(banking_primary_RoutingNum, "BANKL");
							retET_BNKA.setValue(banking_primary_AccountNum, "BNKLZ");
							// End of Insert by Naga
							
							//retET_IBAN
							retET_IBAN.appendRow();						
							retET_IBAN.setValue(banking_primary_IbanNum, "IBAN");	
							// Begin of Insert by Naga ENHC0013668
							retET_IBAN.setValue(primary_account_country, "BANKS");
							retET_IBAN.setValue(banking_primary_RoutingNum, "BANKL");
							retET_IBAN.setValue(banking_primary_AccountNum, "BANKN");
							// End of Insert by Naga							

						}// ganesh DFCT0017543	changed from below commented lines to here 
							// RER_ADR6
							Iterator<String> arrayPaymentNotificationEmailIter = arrayPaymentNotificationEmail.iterator();
							while(arrayPaymentNotificationEmailIter.hasNext()){
								String item = arrayPaymentNotificationEmailIter.next();
								
								String email=request.getParameter("emailContact-"+item);
									if(emailValidate(email.trim()))// DFCT0018501
									{
										retET_ADR6.appendRow();	
										retET_ADR6.setValue(email,"SMTP_ADDR");
									}
									else
										{
											retET_ADR6.appendRow();	
											retET_ADR6.setValue("","SMTP_ADDR");
										}
						//	}//	ganesh DFCT0017543						
							
						}
						// END
						
						//Jorge (206443532)
						//New logic to store Secondary Address
						String secondaryAccount = request.getParameter("secondary-address-order");
						
						if ( secondaryAccount != null && !secondaryAccount.isEmpty() ) {
							 String[] secondaryAccountOrder = request.getParameter("secondary-address-order").split(",");

							 resultAction += "LENGTH: " + secondaryAccountOrder.length;
							//String secondaryAccountOrder = request.getParameter("secondary-address-order");
							for (int i = 0; i < secondaryAccountOrder.length ;i++ ) {
								//String item = arraySecondaryBankingAddressIter.next();
								int item = Integer.parseInt(secondaryAccountOrder[i]); 
								
								//String bankingSecondary_view_Country = request.getParameter("bankingSecondary-view"+item+"-Country");
								String secType = request.getParameter("bankingSecondary-"+item+"-Type");
								int bankingNum;
								char substring = ' ';
								if(secType.startsWith("U") || secType.startsWith("W"))
								{
									substring = secType.charAt(0);
									bankingNum = Integer.parseInt(secType.substring(1)); 
								}
								else
									 bankingNum = Integer.parseInt(secType);

								String banking_view_Type = null;
								if (bankingNum < 9) {
									if(substring != ' ')
										banking_view_Type = substring+"0"+String.valueOf(bankingNum+1);
									else
										banking_view_Type = "0"+String.valueOf(bankingNum+1);

								}
								else
									if(substring != ' ')
										banking_view_Type = substring+String.valueOf(bankingNum+1);
									else
										banking_view_Type = String.valueOf(bankingNum+1);

								
								String secondary_account_view_country = request.getParameter("secondary-account-"+item+"-country");
								String banking_view_RoutingNum = request.getParameter("banking-"+item+"-RoutingNum");
								if(banking_view_RoutingNum!=null)
									banking_view_RoutingNum=splCharRemoval(banking_view_RoutingNum);
								// Begin of Comment and Insert by Naga ENHC0016170 
								String banking_view_AccountNum = replaceSpace(request.getParameter("hidden-banking-"+item));// DFCT0018702 ganesh
								if(banking_view_AccountNum!=null)
									banking_view_AccountNum=splCharRemoval(banking_view_AccountNum);
								
								String banking_view_SwiftNum = request.getParameter("banking-"+item+"-SwiftNum");
								String banking_view_IbanNum = request.getParameter("banking-"+item+"-IbanNum");
								if(banking_view_IbanNum!=null)
									banking_view_IbanNum=splCharRemoval(banking_view_IbanNum);
								String banking_view_HolderName = request.getParameter("banking-"+item+"-HolderName");
								
								// Begin of Insert by Naga DFCT0013582
								String objectid = request.getParameter("ACHFileInfo-"+item);
								if(objectid!=null&&!objectid.equals("null")&&objectid.trim().length()>0){
									retET_FILES.appendRow();						
									retET_FILES.setValue(objectid, "OBJECT_ID");
									retET_FILES.setValue(requestId,"REQST");
									retET_FILES.setValue(banking_view_Type,"FILE_TYPE");									
								}
								// End of Insert by Naga 								

								//if (banking_view_Type.equalsIgnoreCase("wire")){
								//	banking_view_Type = "W"+String.format("%03d", i+1);
								//} else {
									//banking_view_Type = "A"+String.format("%03d", i+1);
								//banking_view_Type = String.format("%2d", i+1);
								//}
								
							// Modified by CGUTJAHR : 1/15/15 : Enhancement #41
							// Naga ENHC0016460 Secondary accounts are possible even when primary has check.	
//							if ( !primaryBankingType.equalsIgnoreCase("Check") ) { 	ENHC0016460								
								retET_LFBK.appendRow();
								retET_LFBK.setValue(secondary_account_view_country, "BANKS");	
								retET_LFBK.setValue(banking_view_Type, "BVTYP");	
								retET_LFBK.setValue(banking_view_RoutingNum, "BANKL");
								retET_LFBK.setValue(banking_view_AccountNum, "BANKN");	
								retET_LFBK.setValue(banking_view_HolderName, "KOINH");	

								//retET_BNKA
								retET_BNKA.appendRow();
								retET_BNKA.setValue(banking_view_SwiftNum, "SWIFT");
								// Begin of Insert by Naga ENHC0013668
								retET_BNKA.setValue(secondary_account_view_country, "BANKS");
								retET_BNKA.setValue(banking_view_RoutingNum, "BANKL");
								retET_BNKA.setValue(banking_view_AccountNum, "BNKLZ");
								// End of Insert by Naga
								
								//
								retET_IBAN.appendRow();						
								retET_IBAN.setValue(banking_view_IbanNum, "IBAN");	
								// Begin of Insert by Naga ENHC0013668
								retET_IBAN.setValue(secondary_account_view_country, "BANKS");								
								retET_IBAN.setValue(banking_view_RoutingNum, "BANKL");
								retET_IBAN.setValue(banking_view_AccountNum, "BANKN");
								// End of Insert by Naga								
//							}			// ENHC0016460
							
							//resultAction += "[{banking_view_Type: " + banking_view_Type + ", "+
				 	 		//"secondary_account_view_country: " + secondary_account_view_country + ", "+
				 	 		//"banking_view_RoutingNum: " + banking_view_RoutingNum + ", "+
				 	 		//"banking_view_AccountNum: " + banking_view_AccountNum + ", "+
				 	 		//"banking_view_SwiftNum: " + banking_view_SwiftNum + ", "+
				 	 		//"banking_view_IbanNum: " + banking_view_IbanNum + ",}], ";
								
							}
						}
						
						//resultAction += "Rows: " + retET_LFBK.getNumRows() + "  " + retET_LFBK;
						//Iterator<String> arraySecondaryBankingAddressIter = arraySecondaryBankingAddress.iterator();

						
						
						//old logic not working
//						int i = 0;
//						while(arraySecondaryBankingAddressIter.hasNext()){
//							i++;
//							String item = arraySecondaryBankingAddressIter.next();
//												
//							//String bankingSecondary_view_Country = request.getParameter("bankingSecondary-view"+item+"-Country");
//							String banking_view_Type = request.getParameter("bankingSecondary-"+i+"-Type");
//							String secondary_account_view_country = request.getParameter("secondary-account-"+i+"-country");
//							String banking_view_RoutingNum = request.getParameter("banking-view"+item+"-RoutingNum");
//							String banking_view_AccountNum = request.getParameter("banking-view"+item+"-AccountNum");
//							String banking_view_SwiftNum = request.getParameter("banking-view"+item+"-SwiftNum");
//							String banking_view_IbanNum = request.getParameter("banking-view"+item+"-IbanNum");
//	
//							//if (banking_view_Type.equalsIgnoreCase("wire")){
//							//	banking_view_Type = "W"+String.format("%03d", i+1);
//							//} else {
//								//banking_view_Type = "A"+String.format("%03d", i+1);
//							//banking_view_Type = String.format("%2d", i+1);
//							//}
//							
//							retET_LFBK.appendRow();
//							retET_LFBK.setValue(secondary_account_view_country, "BANKS");						
//							retET_LFBK.setValue(banking_view_RoutingNum, "BANKL");
//							retET_LFBK.setValue(banking_view_AccountNum, "BANKN");	
//							retET_LFBK.setValue(banking_view_Type, "BVTYP");	
//							
//							//retET_BNKA
//							retET_BNKA.appendRow();
//							retET_BNKA.setValue(banking_view_SwiftNum, "SWIFT");							
//							
//							//
//							retET_IBAN.appendRow();						
//							retET_IBAN.setValue(banking_view_IbanNum, "IBAN");	
//						resultAction += "Item: "+item + "[{banking_view_Type: " + banking_view_Type + ", "+
//			 	 		"secondary_account_view_country: " + secondary_account_view_country + ", "+
//			 	 		"banking_view_RoutingNum: " + banking_view_RoutingNum + ", "+
//			 	 		"banking_view_AccountNum: " + banking_view_AccountNum + ", "+
//			 	 		"banking_view_SwiftNum: " + banking_view_SwiftNum + ", "+
//			 	 		"banking_view_IbanNum: " + banking_view_IbanNum + ",}], ";
//														
//						}
						
						strOutput = strOutput+","+arraySecondaryBankingAddress+","+retET_LFBK+","+retET_LFA1+","+retET_ANSWER;

			 	 		client.execute(function);
			 	 		
			 	 		JCO.Structure retReturn = function.getExportParameterList().getStructure("IS_RETURN");
			 	 		requestId = function.getExportParameterList().getString("IV_REQST");
				 	 	//Naga ENHC0015302 Display the success message and error message based on the backend result. 
			 	 		// 03/30/15
			 	 		// Begin of Insert and comment by Naga
			 	 		returnStatus 	= (String)retReturn.getValue("TYPE");	 
			 	 		resultAction 	= (String)retReturn.getValue("MESSAGE");
			 	 		errorRFCMessage = (String)retReturn.getValue("MESSAGE");
			 	 		messageNumber   = (String)retReturn.getValue("NUMBER");
			 	 		
			 	 		//resultAction = "Data Saved ("+requestId+")";
			 	 		//resultAction = returnStatus;
			 	 		
					} 
	

					if(returnStatus.equalsIgnoreCase("S")){
						 returnCode = "0";
					}else{
						returnCode = "1";
					}
					
					if (returnCode.equalsIgnoreCase("0")){
						// For success resultAction will have request id / success message based on scenario
						windowMessage = resultAction;						
					} else {
						windowMessage = errorRFCMessage;
					}				
	
					client.disconnect();
					
			} catch (Exception ex){
				ex.printStackTrace();
				returnCode = "1";
				windowMessage = ex.getLocalizedMessage();
			}
			
			// Begin of Insert by Naga
			// Handle double quotes in error message
			windowMessage = windowMessage.replace("\"","\\\"");
			// End of Insert by Naga
			// UI
			result = "{\"code\":\""+returnCode+"\",\"requestNumber\":\""+requestId+"\",\"message\":\""+windowMessage+"\",\"messageNumber\":\""+messageNumber+"\"}";
			
			response.write(result);

			
			if ( kmLoggingActive.equalsIgnoreCase("true")){				
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("VRA_ObjectActions",DateTime+","+requestId+","+userId+","+action+","+strOutput+","+result);
			}
			
    	} catch (Exception ey) {		
				ey.printStackTrace();
				res.write("Exception:"+ey.toString());
		}
    }
 // Remove <> DFCT0018632
    public String replaceSplChar(String input)
    {	String temp;
    	temp=input.replace("<", " ");
    	temp=temp.replace(">", " ");
    	return temp;
    }
 // Remove White Space DFCT0018702
    public String replaceSpace(String input)
    {	
    String temp= input.replaceAll("\\s+","");
    	return temp;
    }
 // Validate eMail DFCT0018501
    public boolean emailValidate(String email)
    {	
    	String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();    	
    }
    
    private String splCharRemoval(String inputStringLine)
    {
    	String outcome=inputStringLine;
        java.util.regex.Pattern pt = java.util.regex.Pattern.compile("[^a-zA-Z0-9]");
        java.util.regex.Matcher match= pt.matcher(outcome);
        while(match.find())
        {
            String s= match.group();
            outcome=outcome.replaceAll("\\"+s, "");
        }
      	
    	return outcome;
    }

    
    
}