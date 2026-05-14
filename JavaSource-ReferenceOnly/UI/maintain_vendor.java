package com.nbcu.vra;
 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class maintain_vendor extends AbstractPortalComponent
{
	String ServerString = "com.nbcu.html5_vra";
	public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		String userName = "";
		
    	try {
  			userName = request.getUser().getName();

			String requestId = request.getParameter("requestId");
			String vendorId = request.getParameter("vendorId"); 
			String vendorType = request.getParameter("vendorType"); // ENHC0013668
			String companyCode = request.getParameter("companyCodes"); // ENHC0013668
			String mode = request.getParameter("mode");				// ENHC0019060
			//String requestType = request.getParameter("requestType");
			boolean accessAllowed=false; // ganesh DFCT0017729

			if(mode==null){	// ENHC0019060
				mode="";
			}

			boolean showDifferentCurrencyMessage = false;
			boolean userIsExternalVendor = false;
			boolean userIsInternalEmployeeBuyer = false;
			boolean userIsInternalEmployeeInviter =  false;
			boolean userIsJointVenture = false;			// ENHC0016164
			String comments = "";
			String annualSpend = "";
			String approverSSO = "";
			String requestType = "";
			String userType = "";
			String decisionVendorType = "";
			String status = "";
			boolean checkAllowed = false;
			String tAndC = "";
			String ersValue = "";
			String subSystems = "";
			String companyCodes = "";
//			String vendorType = "";					// ENHC0013668
			String taxRecipientType = "";
			//Begin of Insert CTI w8 Foreign vendor variables added
			boolean isCTICalled =  false;
			String ernamCTI = "";
			String reqIdCTI = "";
			String regCodeCTI = "";
			String urlCTI = "";
			String tcntyCTI = "";
			String[] ForeignVendorQuestonsArray = new String[6];
			//End of Insert CTI w8 Foreign vendor variables added 
			//new variables added by AGAMPA on 6-Mar-2015
			String exempt ="";
			String fatca = "";
			//code changes ends here
			String taxExempt = "";
			String organizationFocus = "";
			String companyScale = "";
			String FPNADesig = "";
			String orgDesc = "";			
			String hidePrimaryAddress = "";//Issue 57 fix prev value hidePrimaryAddress = "hide";
			String hideNonUSState = "";
			String legalName = "";
			String name1 = "";
			String name2 = "";		
			String name3 = "";			
			String invoicingName = "";
			String minorityCode = "";
			String industryCode = "";
			//String acceptPO = "";
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
			String taxJurisdiction = "";
			String taxID = "";
			String socialSecurityNumber = "";
			String temptaxID = "";// ganesh
			String tempsocialSecurityNumber = "";// ganesh
			String tempbankingPrimaryBankAccount="";//ganesh
			String independentContractor = "";
			String percentCATax = "";
			String terms = "";
			String bankingPrimaryCountry = "";
			String bankingPrimaryAccountType = ""; 		// ENHC0013668
			String bankingPrimaryRoutingBSB = "";
			String bankingPrimaryBankAccount = "";
			String bankingPrimaryEmailContact = "";
			String bankingPrimarySWIFTAcocunt = "";
			String bankingPrimaryHolderName = "";
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
			String diversityMembersDisability = "";
			String diversityMembersGAY= "";				
			String diversityMembersVETRAN = "";
			String[][] arraySecondaryAccount = null;
			String[] TaxCheckBoxeArray = new String[27];
			String FileNameW9 = "";
			String GUIDW9 = "";
			String FileNameLegal = "";			// ENHC0016461
			String GUIDLEG = "";				// ENHC0016461
			String FileNameSupport = "";		// ENHC0013668
			String GUIDSUP = "";				// ENHC0013668
			String FileNameW8 = "";
			String GUIDW8 = "";			
			String FileName590 = "";
			String GUID590 = "";			
			String FileNameACH = "";
			String GUIDACH = "";			
			
			
			// Begin of Insert by Naga ENHC0013668
			if(vendorType==null){
				vendorType="";
			}
			if(companyCode==null){
				companyCode = "";
			}
			// End of Insert by Naga 
			// Drop Down Arrays
			String[][] arrayMinorityCode = tools.setupMinorityCodeArray();
			String[][] arrayIndustryCode = tools.setupIndustryCodeArray();
			String[][] arrayCountryCode = null; 
				
				//tools.setupCountryCodeArray();	// Naga ENHC0013660
			String[][] arrayRecipientType = tools.setupRecipientTypeArray();
			String[][] arrayTaxExempt = tools.setupTaxExemptArray();
			String[][] arrayrganizationFocus = tools.setupOrganizationFocusArray();
			String[][] arrayCompanyScale = tools.setupCompanyScaleArray();
			String[][] arrayFPNADesigFocus = tools.setupFPNADesigArray();
			String[][] arrayUSStates = null; 
				//tools.setupUSStatesArray(); // Naga ENHC0013660
			String[][] arrayTaxRecipientType = tools.setupTaxRecipientTypeArray();
			String[][] arrayContactDepartment = tools.setupContactDepartmentArray();
			
			JCO.Structure	retCS_RETURN = null; // ENHC0019060
			
			// JCO Vars
			JCO.Table retCT_LFA1 = null;
			JCO.Table retCT_LFBK = null;			
			JCO.Table retCT_REQ = null;
			JCO.Table retCT_ANSWER = null;
			JCO.Table retCT_BNKA = null;
			JCO.Table retCT_LFB1 = null;
			JCO.Table retCT_ADR6 = null;
			JCO.Table retCT_IBAN = null;			
			JCO.Table retCT_TBCN21 = null;
			JCO.Table retCT_KNVK = null;
			JCO.Table retCT_ZTERMS = null;
			JCO.Table retCT_LFM1 = null;
			JCO.Table retCT_FILES = null;
			//Begin of Insert CTI w8 Foreign vendor
			JCO.Table retCT_CTI = null;
			//End of Insert CTI w8 Foreign vendor
			
			List<String> errorMessageList = new ArrayList<String>();
			
	    	HttpServletResponse resp = request.getServletResponse(true);
			PrintWriter response = resp.getWriter();
			resp.setContentType("text/html;charset=utf-8");
			
			try {
				IUser userObject = request.getUser();

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
				importList.setValue(userName, "ADMIN_SSO");
				importList.setValue(vendorId, "VENDOR");
				importList.setValue(requestId, "REQST");
				importList.setValue(companyCode, "I_BUKRS"); // ENHC0013668
				

				importList.setValue("0", "SOURCE");
 
				client.execute(function);
				
				retCT_LFA1 = function.getExportParameterList().getTable("CT_LFA1");
				retCT_LFBK = function.getExportParameterList().getTable("CT_LFBK");	
				retCT_FILES = function.getExportParameterList().getTable("CT_FILES");
				retCT_BNKA = function.getExportParameterList().getTable("CT_BNKA");		// ENHC0013668
				retCT_IBAN = function.getExportParameterList().getTable("CT_IBAN");		// ENHC0013668
				// Begin of Insert by Naga ENHC0019060
				retCS_RETURN = function.getExportParameterList().getStructure("CS_RETURN");			// ENHC0019060
				// Check if there are any errors
				if(retCS_RETURN.getString("TYPE").equalsIgnoreCase("E")&&retCS_RETURN.getString("MESSAGE").trim().length()>0){
					Exception e = new Exception(retCS_RETURN.getString("MESSAGE"));
					throw e;
				}
				
				// End of Insert by Naga
            	//<<< Added by Added by Jorge L. (206443532)
            	if(retCT_LFBK.getNumRows() == 0)
            	{
            	}
            	// Begin of comment by Naga ENHC0016461
            	// There could be scenarios where there is only record but it is not primary.
//            	else if(retCT_LFBK.getNumRows() == 1)
//            	{
//            		retCT_LFBK.lastRow();
//            		bankingPrimaryRoutingBSB = retCT_LFBK.getString("BANKL");
//					bankingPrimaryBankAccount = retCT_LFBK.getString("BANKN");
//					bankingPrimarySWIFTAcocunt = retCT_LFBK.getString("BANKL");
//					bankingPrimaryHolderName = retCT_LFBK.getString("KOINH");
//					bankingPrimaryCountry = retCT_LFBK.getString("BANKS");
//
//					bankingPrimaryIBAN = "";
//            	}
            	// End of comment
            	else //if(retCT_LFBK.getNumRows() > 1)
            	{
            		// Begin of Insert by Naga ENHC0016461
            		// Determine if there is a primary bank
					retCT_LFBK.firstRow();
					boolean primaryExists = false;
					for(int i = 0; i < retCT_LFBK.getNumRows(); i++) {
						if(retCT_LFBK.getString(7).contains("01"))
							primaryExists = true;
						retCT_LFBK.nextRow();
					}
					retCT_LFBK.firstRow();
					if(primaryExists && retCT_LFBK.getNumRows() == 1){
						// Do not create an array
					} else if(primaryExists){
						arraySecondaryAccount = new String[retCT_LFBK.getNumRows()-1][16]; // ENHC0016461// ganesh changed 13 to 14
						// ENHC0013668  Changed to 16 to include SWIFT and IBAN #
					}else{
						arraySecondaryAccount = new String[retCT_LFBK.getNumRows()][16]; // ENHC0016461// ganesh changed 13 to 14
						// ENHC0013668  Changed to 16 to include SWIFT and IBAN #
					}            		
            		// End of Insert by Naga
					//arraySecondaryAccount = new String[retCT_LFBK.getNumRows()-1][11]; // Naga Secondary Bank Account Issue
					//arraySecondaryAccount = new String[retCT_LFBK.getNumRows()-1][13]; // Naga Secondary Bank Account Issue

					
					// ENHC0016461 Primary may exist or may not
					boolean primaryStored = false;
					int index = 0;
					int arrayIndex = 0;	// ENHC0016461
					// ENHC0016461 As index is pointing to LFBK which could also include primary.
					// creating arrayindex which will only be iterated for secondary accounts so it stays consistent with array.
//	                  while(index < arraySecondaryAccount.length || !primaryStored) { // ENHC0016461
	                  while(index < retCT_LFBK.getNumRows()) { // ENHC0016461	                	  

						if(retCT_LFBK.getString(7).contains("01") && !primaryStored)
							
						{
							bankingPrimaryRoutingBSB = retCT_LFBK.getString("BANKL");
							//code start- altered by -ganesh
							tempbankingPrimaryBankAccount=retCT_LFBK.getString("BANKN");
							if(tempbankingPrimaryBankAccount.length()>3 && !tempbankingPrimaryBankAccount.contains("X"))
							{
										int accountLength=tempbankingPrimaryBankAccount.length();
				                    	int firstNumber=accountLength-4;
				                    	int lastNumber=accountLength;
				                    	String maskingSymbol="";
				             for(int x=0;x<firstNumber;x++)     	
				             {
				            	 maskingSymbol=maskingSymbol+"X";
				             }
				              			                    	
				                    	bankingPrimaryBankAccount=maskingSymbol+tempbankingPrimaryBankAccount.substring(firstNumber,lastNumber);
							}
							else{
								tempbankingPrimaryBankAccount="";
								bankingPrimaryBankAccount="";
								
							}
							//code end ganesh
							//bankingPrimaryBankAccount = retCT_LFBK.getString("BANKN"); //code altered -check above code- ganesh

//							bankingPrimarySWIFTAcocunt = retCT_LFBK.getString("BANKL");		// Mapped to wrong field
							bankingPrimaryHolderName = retCT_LFBK.getString("KOINH");
                    		bankingPrimaryCountry = retCT_LFBK.getString("BANKS");
                    		bankingPrimaryAccountType	= retCT_LFBK.getString("BVTYP");	// ENHC0013668
//							bankingPrimaryIBAN = "";										// ENHC0013668 Not mapped to anything
							primaryStored = true;
							index++;			// ENHC0016461
							// Begin of Insert by Naga ENHC0013668
							// Get the SWIFT code
							retCT_BNKA.firstRow();
							for(int idx = 0;idx<retCT_BNKA.getNumRows();idx++){
								String bankKey = retCT_BNKA.getString("BANKL");
								String accountNum = retCT_BNKA.getString("BANKA");
								if(bankKey.equals(bankingPrimaryRoutingBSB)&&accountNum.equals(tempbankingPrimaryBankAccount)){
									bankingPrimarySWIFTAcocunt = retCT_BNKA.getString("SWIFT");
									break;
								}
								retCT_BNKA.nextRow();
							}
							retCT_BNKA.firstRow();
							// Get the IBAN #
							retCT_IBAN.firstRow();
							for(int idx = 0;idx<retCT_IBAN.getNumRows();idx++){
								String bankKey = retCT_IBAN.getString("BANKL");
								String accountNum = retCT_IBAN.getString("BANKN");
								if(bankKey.equals(bankingPrimaryRoutingBSB)&&accountNum.equals(tempbankingPrimaryBankAccount)){
									bankingPrimaryIBAN = retCT_IBAN.getString("IBAN");
									break;
								}
								retCT_IBAN.nextRow();
							}
							retCT_IBAN.firstRow();							
							// End of Insert by Naga
						}else
						{
							// ENHC0016461 , All index references are replaced by arrayIndex.

							arraySecondaryAccount[arrayIndex][0] = retCT_LFBK.getString(0);
							arraySecondaryAccount[arrayIndex][1] = retCT_LFBK.getString(1);
							arraySecondaryAccount[arrayIndex][2] = retCT_LFBK.getString(2);
							arraySecondaryAccount[arrayIndex][3] = retCT_LFBK.getString(3);
							arraySecondaryAccount[arrayIndex][4] = retCT_LFBK.getString(4);
							//arraySecondaryAccount[arrayIndex][5] = "11111";//ganesh

							//arraySecondaryAccount[arrayIndex][5] = retCT_LFBK.getString(5);//ganesh
							arraySecondaryAccount[arrayIndex][6] = retCT_LFBK.getString(6); 
							arraySecondaryAccount[arrayIndex][7] = retCT_LFBK.getString(7);
							arraySecondaryAccount[arrayIndex][8] = retCT_LFBK.getString(8);
							arraySecondaryAccount[arrayIndex][9] = retCT_LFBK.getString(9);
							arraySecondaryAccount[arrayIndex][10] = retCT_LFBK.getString(10);
							arraySecondaryAccount[arrayIndex][13] =retCT_LFBK.getString(5) ;
							// Begin of Insert by Naga ENHC0013668
							// Get the SWIFT code
							retCT_BNKA.firstRow();
							arraySecondaryAccount[arrayIndex][14] = ""; // Initialize it
							for(int idx = 0;idx<retCT_BNKA.getNumRows();idx++){
								String bankKey = retCT_BNKA.getString("BANKL");
								String accountNum = retCT_BNKA.getString("BANKA");
								if(bankKey.equals(arraySecondaryAccount[arrayIndex][4])&&accountNum.equals(arraySecondaryAccount[arrayIndex][13])){
									if(retCT_BNKA.getString("SWIFT")==null){
									}else{
										arraySecondaryAccount[arrayIndex][14] = retCT_BNKA.getString("SWIFT");	
									}
									
									break;
								}
								retCT_BNKA.nextRow();
							}
							retCT_BNKA.firstRow();
							// Get the IBAN #
							retCT_IBAN.firstRow();
							arraySecondaryAccount[arrayIndex][15] = ""; // Initialize it
							for(int idx = 0;idx<retCT_IBAN.getNumRows();idx++){
								String bankKey = retCT_IBAN.getString("BANKL");
								String accountNum = retCT_IBAN.getString("BANKN");
								
								if(bankKey.equals(arraySecondaryAccount[arrayIndex][4])&&accountNum.equals(arraySecondaryAccount[arrayIndex][13])){
									if(retCT_IBAN.getString("IBAN")==null){
									}else{
										arraySecondaryAccount[arrayIndex][15] = retCT_IBAN.getString("IBAN");	
									}
									
									break;
								}
								retCT_IBAN.nextRow();
							}
							retCT_IBAN.firstRow();							
							// End of Insert by Naga							
							//Naga	DFCT0013582
							// Populate the file name and object id
							// Determine the file name and object id based on type
							//code started -ganesh
							{
							//	arraySecondaryAccount[arrayIndex][13] = retCT_LFBK.getString(6);
								String tempAccount=retCT_LFBK.getString("BANKN");;
								String maskingSymbol="";
								
								if(tempAccount.length()>3 && !tempAccount.contains("X"))
								{
											int accountLength=tempAccount.length();
					                    	int firstNumber=accountLength-4;
					                    	int lastNumber=accountLength;
					                    	
					             for(int x=0;x<firstNumber;x++)     	
					             {
					            	 maskingSymbol=maskingSymbol+"X";
					             }
					              			                    	
					             arraySecondaryAccount[arrayIndex][5]=maskingSymbol+tempAccount.substring(firstNumber,lastNumber);
								}
								else
								{
									arraySecondaryAccount[arrayIndex][5]=retCT_LFBK.getString(5);
								}
						}

							//code end ganesh     
							
							
							
							
							retCT_FILES.firstRow();
							for(int i = 0; i < retCT_FILES.getNumRows(); i++) {
								String fileType = retCT_FILES.getString("FILE_TYPE"); //Naga DFCT0013582
								// Check the length of the file, if it is three characters remove the first character
								if(fileType.length()>2)
									fileType = fileType.substring(1);
								if ((retCT_LFBK.getString(7).contains(fileType))) {
									arraySecondaryAccount[arrayIndex][11] = retCT_FILES.getString("ACT_FILE_NAME");
								    arraySecondaryAccount[arrayIndex][12] = retCT_FILES.getString("OBJECT_ID");
								} 
								retCT_FILES.nextRow();
							}	
							
							arrayIndex++;         
							//End Naga	DFCT0013582							
							
							index++;
						}
						retCT_LFBK.nextRow();
						
	                  }	
	                  if(arraySecondaryAccount!=null)	// ENHC0016461
	                  tools.sortSecondaryAccount(arraySecondaryAccount);
                  }
                //>>>
				retCT_REQ = function.getExportParameterList().getTable("CT_REQ");
				retCT_ADR6 = function.getExportParameterList().getTable("CT_ADR6");
				retCT_ANSWER = function.getExportParameterList().getTable("CT_ANSWER");
				retCT_LFB1 = function.getExportParameterList().getTable("CT_LFB1");
//				retCT_IBAN = function.getExportParameterList().getTable("CT_IBAN");		// ENHC0013668 moved it up	
				retCT_TBCN21 = function.getExportParameterList().getTable("CT_TBCN21");	
//				retCT_BNKA = function.getExportParameterList().getTable("CT_TBCN21");	// ENHC0013668 moved it up
				retCT_KNVK = function.getExportParameterList().getTable("CT_KNVK");
				retCT_ZTERMS = function.getExportParameterList().getTable("CT_ZTERMS");
				retCT_LFM1 = function.getExportParameterList().getTable("CT_LFM1");
				//Begin of Insert CTI w8 Foreign vendor
				retCT_CTI = function.getExportParameterList().getTable("CT_CTI");
				//End of Insert CTI w8 Foreign vendor

				//Get User Roles
				IRepository m_RepositoryRoles = JCO.createRepository("repository", client);
				IFunctionTemplate Z_SF_I477_GET_USER_ROLES = m_RepositoryRoles.getFunctionTemplate("Z_SF_I477_GET_USER_ROLES");

				JCO.Function functionRoles = new JCO.Function(Z_SF_I477_GET_USER_ROLES);
				JCO.ParameterList importListRoles = functionRoles.getImportParameterList();
				importListRoles.setValue(userName, "I_SSO_ID");
 
				client.execute(functionRoles);

				JCO.Table retUserRoles =  functionRoles.getTableParameterList().getTable("T_ROLES");	
				
				for(int i = 0; i < retUserRoles.getNumRows(); i++) {
					if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("DECC:FI_AP_AUTO_VND_REGISTER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))) {
						userIsExternalVendor = true;
						//userType = "3";
						//userType = "2";
						requestType = "1";
						accessAllowed=true; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						//userType = "2";
						requestType = "2";
						accessAllowed=true; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeBuyer = true;
						//userType = "2";
						requestType = "2";	
						accessAllowed=true; // ganesh DFCT0017729

					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						userIsInternalEmployeeInviter = true;
						//userType = "1";
						requestType = "2";
						accessAllowed=true; // ganesh DFCT0017729

					}
					 // Begin of Insert by Naga ENHC0016164
					 else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
							userIsInternalEmployeeBuyer = true;
							//userType = "2";
							requestType = "2";		
							accessAllowed=true; // ganesh DFCT0017729

						}
					 else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
							userIsJointVenture = true;
							//userType = "2";
							requestType = "2";	
							accessAllowed=true; // ganesh DFCT0017729

						}
					  // End of Insert by Naga
					retUserRoles.nextRow();
				}
				
				//Get Error Messages
				IRepository m_RepositoryErrorMessages = JCO.createRepository("repository", client);
				IFunctionTemplate Z_SF_I504_DISPLAY_INBOX_W2W_GET_ERROR_MESSAGES = m_RepositoryErrorMessages.getFunctionTemplate("Z_SF_I504_DISPLAY_INBOX_W2W");

				JCO.Function functionErrorMessages = new JCO.Function(Z_SF_I504_DISPLAY_INBOX_W2W_GET_ERROR_MESSAGES);
				JCO.ParameterList importListErrorMessages = functionErrorMessages.getImportParameterList();
				importListErrorMessages.setValue(userName, "I_SSO");
 
				client.execute(functionErrorMessages);

				JCO.Table errorLogTable =  functionErrorMessages.getExportParameterList().getTable("IT_EMSG");
				
				{
					int maxRows = 500;
				
					if (errorLogTable.getNumRows() < maxRows)
						maxRows = errorLogTable.getNumRows();
						
					for(int i = 0; i < maxRows; i++) {
						if(requestId != null)
						if(requestId.equals(errorLogTable.getString("REQST")))
							{
								errorMessageList.add(errorLogTable.getString("MESSAGE"));
							}
					
						errorLogTable.nextRow();
					}
				}
				// Get countries and regions
				IRepository m_RepositoryCountries = JCO.createRepository("repository", client);
				IFunctionTemplate ZSFI_I507_GET_COUNTRIES_STATES = m_RepositoryCountries.getFunctionTemplate("ZSFI_I507_GET_COUNTRIES_STATES");

				JCO.Function functionCountries = new JCO.Function(ZSFI_I507_GET_COUNTRIES_STATES);
				client.execute(functionCountries);

				JCO.Table countries =  functionCountries.getExportParameterList().getTable("EX_T_COUNTRIES");
				JCO.Table regions   = functionCountries.getExportParameterList().getTable("EX_T_STATES");
				{
					// Populate countries
					int maxRows = countries.getNumRows();
					arrayCountryCode = new String[maxRows][2];
					for(int index=0;index<maxRows;index++){
						arrayCountryCode[index][0] = countries.getString("LAND1");
						arrayCountryCode[index][1] = countries.getString("LANDX");
						countries.nextRow();
					}
					
					// Populate Regions
					maxRows = regions.getNumRows();
					arrayUSStates = new String[maxRows][3];
					for(int index=0;index<maxRows;index++){
						arrayUSStates[index][0] = regions.getString("LAND1");
						arrayUSStates[index][1] = regions.getString("BLAND");
						arrayUSStates[index][2] = regions.getString("BEZEI");
						regions.nextRow();
					}
				}
				
				client.disconnect();						
				
				// Begin of Pranesh(04/19/2016) - ENHC0019059
							
				//Help Screen - 2
					int tempVal=0;// Pranesh(04/19/2016) - ENHC0019059
					String[] helps=help.helpContent(request,"00012");        // Pranesh(04/19/2016)- ENHC0019059
			    
					String[] documentation=help.helpContent(request,"00013");// Pranesh(04/19/2016)- ENHC0019059
					int documentLength=0;// Pranesh(04/18/2016)
				
			    // End of Pranesh(04/19/2016) - ENHC0019059
				
				// Set up Files
				retCT_FILES.firstRow(); //Naga	DFCT0013582
				for(int i = 0; i < retCT_FILES.getNumRows(); i++) {	
					if (retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("W9")) {
						FileNameW9 = retCT_FILES.getString("ACT_FILE_NAME");
						GUIDW9 = retCT_FILES.getString("OBJECT_ID");
					} else if (retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("W8")) {
						FileNameW8 = retCT_FILES.getString("ACT_FILE_NAME");
						GUIDW8 = retCT_FILES.getString("OBJECT_ID");
					} else if (retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("ACH")) {
						FileNameACH = retCT_FILES.getString("ACT_FILE_NAME");
						GUIDACH = retCT_FILES.getString("OBJECT_ID");						
					} else if (retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("590")) {
						FileName590 = retCT_FILES.getString("ACT_FILE_NAME");
						GUID590 = retCT_FILES.getString("OBJECT_ID");
					} 
					// Begin of Insert by Naga
					// Handle legal settlement file
					else if(retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("LEG")){
						FileNameLegal = retCT_FILES.getString("ACT_FILE_NAME");
						GUIDLEG = retCT_FILES.getString("OBJECT_ID");
					}
					// End of Insert 
						
					// Begin of Insert by Naga	ENHC0013668
					// Handle legal settlement file
					else if(retCT_FILES.getString("FILE_TYPE").equalsIgnoreCase("SUP")){
						FileNameSupport = retCT_FILES.getString("ACT_FILE_NAME");
						GUIDSUP = retCT_FILES.getString("OBJECT_ID");
					}
					// End of Insert 					
					retCT_FILES.nextRow();
				}
				// Set up Primary Address
					if(!retCT_LFA1.isEmpty()){
						hidePrimaryAddress = "";
						poEmailAddress = retCT_LFA1.getString("SMTP_ADDR");
						subSystems = retCT_LFA1.getString("J_1KFREPRE");						
						name1 = retCT_LFA1.getString("NAME1");
						name2 = retCT_LFA1.getString("NAME2");
						name3 = retCT_LFA1.getString("NAME3");
						primaryAddress1 = retCT_LFA1.getString("STRAS");
						primaryAddress2 = retCT_LFA1.getString("STR_SUPPL1");
						primaryAddress3 = retCT_LFA1.getString("STR_SUPPL2");
						primaryCountry = retCT_LFA1.getString("LAND1");
						primaryCity = retCT_LFA1.getString("ORT01");
						primaryState = retCT_LFA1.getString("REGIO");
						primaryZip = retCT_LFA1.getString("PSTLZ");
						taxJurisdiction = retCT_LFA1.getString("TXJCD");
						
						//Added by Kermel Ruperto SSO:206441846. By recomendation of Deepak and Steven
						if(vendorId == null || vendorId.isEmpty())
						{
							vendorId = retCT_LFA1.getString("LIFNR");
						}
						// socialSecurityNumber = retCT_LFA1.getString("STCD1");// code altered check below code -ganesh
						// taxID = retCT_LFA1.getString("STCD2");//ganesh
						// start -ganesh

						tempsocialSecurityNumber = retCT_LFA1.getString("STCD1");
						if(tempsocialSecurityNumber.length()==9)
							{
							socialSecurityNumber = "XXX-XX-"+tempsocialSecurityNumber.substring(5, 9);
							}
									
						temptaxID = retCT_LFA1.getString("STCD2");
						if(temptaxID.length()==9)
						{
						 taxID ="XX-XXX" + temptaxID.substring(5, 9);
						}
					
						// end ganesh
						
						// Check to see if Primary Address is in the US
						if (retCT_LFA1.getString("LAND1").equalsIgnoreCase("US")){
							hideNonUSState = "style=\"display: none;\"";	
						} 
					
					}

					  // Begin of Insert by Naga
						String disableTerms			= (vendorId!=null&&vendorId.trim().length()>0&&!requestType.equals("1"))?" disabled ":"";
						String toggleTerms          = (vendorId!=null&&vendorId.trim().length()>0&&!requestType.equals("1"))?"":"toggle-terms";
						String termsDisplayOnly     = (vendorId!=null&&vendorId.trim().length()>0&&!requestType.equals("1"))?" locked ":"";    

					  // End of Insert by Naga
						if(name2.length()<1){
							legalName = name1+" "+name3; // DFCT0016715 added by Ganesan 							
						} else {
							invoicingName = name1 +" "+name3;							
							legalName = name2;
						}				

					if(!retCT_LFB1.isEmpty()){
						terms = retCT_LFB1.getString("ZTERM");	
					}
					
					for (int i=0;i<retCT_LFB1.getNumRows(); i++){
						companyCodes = companyCodes +retCT_LFB1.getString("BUKRS");
						
						if (retCT_LFB1.nextRow())
							companyCodes = companyCodes+",";
					}
			
					
					if(!retCT_REQ.isEmpty()){
						minorityCode = retCT_REQ.getString("VRA_MINDK");
						industryCode = retCT_REQ.getString("VRA_BRSCH");
						//acceptPO = retCT_REQ.getString("ZZSF_VRA_PORECV");
						taxRecipientType = retCT_REQ.getString("ZZSF_VRA_QSREC");
						// Begin of comment and insert by Naga	// ENHC0013668
//						vendorType = retCT_REQ.getString("VEND_TYPE");
						if(vendorType==null||vendorType.trim().length()==0){
							vendorType = retCT_REQ.getString("VEND_TYPE");
						}
						// End of comment and insert by Naga
						status = retCT_REQ.getString("STATS"); 
						approverSSO = retCT_REQ.getString("APPROVER_SSO");
						comments = retCT_REQ.getString("COMMENT1");
						annualSpend = retCT_REQ.getString("ANNUAL_SPEND");
						tAndC =  retCT_REQ.getString("ZZSF_VRA_TNC");
						userType = retCT_REQ.getString("ZZSF_VRA_VENDCAT");
						
						//code added by AGAMPA on 6-Mar-2015.
						
						exempt = retCT_REQ.getString("ZZSF_VRA_EXMPTPC");
						fatca = retCT_REQ.getString("ZZSF_VRA_EXMPTFRC");
						
						// Begin of change by Naga , DFCT0013688 
						// Scenario where Vendor clicks Maintain directly
						
							if(requestId==null||requestId==""){
								if(retCT_REQ.getString("REQST")!=null&&retCT_REQ.getString("REQST")!="")
									requestId = retCT_REQ.getString("REQST");
							}
						// End of change by Naga
						
					}
				
					// Set up the Environment Questions
					if (!retCT_ANSWER.isEmpty()){
						int maxRows = retCT_ANSWER.getNumRows();
			            for(int i = 0; i < maxRows; i++) {

			             //Set up Environment
		            		if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00001")){				            		
				            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
				            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("0000000001")){
				            			environmentCodeofConduct = true;
				            		}
				            		environmentCodeofConductComment = retCT_ANSWER.getString("ACOMMENT");	
				            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){	
				            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("0000000001")){
				            			envrionmentSustainability = true;
				            		}
				            		envrionmentSustainabilityComment = retCT_ANSWER.getString("ACOMMENT");	
				            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){	
				            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("0000000001")){
				            			envrionmentSocialHealth = true;
				            		}	
				            		envrionmentSocialHealthComment = retCT_ANSWER.getString("ACOMMENT");
				            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
				            		if (retCT_ANSWER.getString("ANSWER").equalsIgnoreCase("0000000001")){
				            			environmentOccupational = true;
				            		}
				            		environmentOccupationalComment = retCT_ANSWER.getString("ACOMMENT");
				            	}   
				            	
			            	//Set up DIVERSITY BOARD OF DIRECTORS
				            } else	if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00003")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		diversityBoardofDirectorsAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		diversityBoardofDirectorsASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		diversityBoardofDirectorsBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		diversityBoardofDirectorsHISPANICLATINO = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		diversityBoardofDirectorsWHITE = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
					            		diversityBoardofDirectorsWOMEN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} 

			            	//Set up DIVERSITY SENIOR STAFF						            	
				            } else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00004")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		diversitySrStaffAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		diversitySrStaffASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		diversitySrStaffBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		diversitySrStaffHISPANICLATINO = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		diversitySrStaffWHITE = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
					            		diversitySrStaffWOMEN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} 
			           
					           //Set up DIVERSITY SENIOR STAFF		
				            	} else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00005")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		diversityMembersAMERICANINDIANALASKAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		diversityMembersASIANPACIFICISLANDER = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		diversityMembersBLANKAFRICANAMERICAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		 diversityMembersHISPANICLATINO = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		diversityMembersWHITE = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
					            		diversityMembersWOMEN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00007")){					            		
					            		diversityMembersVETRAN = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00008")){					            		
					            		  diversityMembersDisability  = retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00009")){					            		
					            		  diversityMembersGAY= retCT_ANSWER.getString("ANSWER").replaceFirst("^0+(?!$)", "");
					            	}
					           //Charity
				            	} else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00002")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		taxExempt = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		orgFocus = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		companyScale = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		FPNADesig = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		orgDesc = retCT_ANSWER.getString("ACOMMENT");
					            	}			            		

		            		//TaxCheckBoxes
				            	} else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00006")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		TaxCheckBoxeArray[0] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		TaxCheckBoxeArray[1] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		TaxCheckBoxeArray[2] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		TaxCheckBoxeArray[3] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		TaxCheckBoxeArray[4] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
					            		TaxCheckBoxeArray[5] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00007")){					            		
					            		TaxCheckBoxeArray[6] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00008")){					            		
					            		TaxCheckBoxeArray[7] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00009")){					            		
					            		TaxCheckBoxeArray[8] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00010")){					            		
					            		TaxCheckBoxeArray[9] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00011")){					            		
					            		TaxCheckBoxeArray[10] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00012")){					            		
					            		TaxCheckBoxeArray[11] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00013")){					            		
					            		TaxCheckBoxeArray[12] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00014")){					            		
					            		TaxCheckBoxeArray[13] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00015")){					            		
					            		TaxCheckBoxeArray[14] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00016")){					            		
					            		TaxCheckBoxeArray[15] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00017")){					            		
					            		TaxCheckBoxeArray[16] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00018")){					            		
					            		TaxCheckBoxeArray[17] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00019")){					            		
					            		TaxCheckBoxeArray[18] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00020")){					            		
					            		TaxCheckBoxeArray[19] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00021")){					            		
					            		TaxCheckBoxeArray[20] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00030")){					            		
					            		TaxCheckBoxeArray[21] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00031")){					            		
					            		TaxCheckBoxeArray[22] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00032")){					            		
					            		TaxCheckBoxeArray[23] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00033")){					            		
					            		TaxCheckBoxeArray[24] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00034")){					            		
					            		TaxCheckBoxeArray[25] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00035")){					            		
					            		TaxCheckBoxeArray[26] = retCT_ANSWER.getString("ANSWER");
					            	}	          		
				            	//Begin of Insert CTI w8 Foreign vendor questions
				            	} else if (retCT_ANSWER.getString("QGROUP").equalsIgnoreCase("00008")){				            		
					            	if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00001")){					            		
					            		ForeignVendorQuestonsArray[0] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00002")){					            		
					            		ForeignVendorQuestonsArray[1] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00003")){					            		
					            		ForeignVendorQuestonsArray[2] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00004")){					            		
					            		ForeignVendorQuestonsArray[3] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00005")){					            		
					            		ForeignVendorQuestonsArray[4] = retCT_ANSWER.getString("ANSWER");
					            	} else if (retCT_ANSWER.getString("QSEQNR").equalsIgnoreCase("00006")){					            		
					            		ForeignVendorQuestonsArray[5] = retCT_ANSWER.getString("ACOMMENT");
					            	}//End of Insert CTI w8 Foreign vendor questions
				            	}		            		
		            		
			            	retCT_ANSWER.nextRow();
			            }
					} 
					
					if(!retCT_LFM1.isEmpty()){
						ersValue = retCT_LFM1.getString("XERSY");
					}
					
					//Begin of Insert CTI w8 Foreign vendor
					if(!retCT_CTI.isEmpty()){
						isCTICalled =  true;	
					}
					for (int i=0;i<retCT_CTI.getNumRows(); i++){
						ernamCTI = retCT_CTI.getString("ACC_NUM");
						reqIdCTI = retCT_CTI.getString("REQST");
						regCodeCTI = retCT_CTI.getString("RCODE");
						urlCTI = retCT_CTI.getString("URL");
						tcntyCTI = retCT_CTI.getString("TCNTY");
						
						retCT_CTI.nextRow();
					}
					if(ernamCTI!=null){
						ernamCTI = "<strong>" + ernamCTI + "</strong>";
					}
					if(regCodeCTI!=null){
						regCodeCTI = "<strong>"+ regCodeCTI +"</strong>";
					}
					//End of Insert CTI w8 Foreign vendor
					
					String bankingRequiredDisplay = "";
					String bankingRequiredInput = "";
					String applyUserError=""; // Added - Pranesh - (05/03/2016)
					// Begin of Insert by Naga 998
					String bankingFormRequiredDisplay = "";
					String bankingFormRequiredInput = "";
					// End of Insert by Naga 998
					// Begin of Insert by Naga ENHC0016458
					String contactsRequiredDisplay = "";
					String contactsRequiredInput = "";
					
					// Begin of Insert by Naga ENHC0019060
//					String readOnly = "";
					String disableButton = "";
					String hideButton = "";
					String applicationMode = "";
		    		if ((status.equalsIgnoreCase("S")) ||(status.equalsIgnoreCase("F")) ||(status.equalsIgnoreCase("R")) || (status.equalsIgnoreCase("D")) || (status.equalsIgnoreCase("X")) || (status.equalsIgnoreCase("Y"))){
		    			if (userIsInternalEmployeeInviter || userIsInternalEmployeeBuyer || userIsJointVenture){
		    				applicationMode = "maintain";
		    			} else {
		    								    				
		    			}
		    		
		    		} else {
		    			applicationMode = "locked";
//		    			readOnly = "disabled";
		    			disableButton = "disableButton";
		    			hideButton = "hideButton";
						if(termsDisplayOnly.trim().length()==0){
							termsDisplayOnly = " locked ";
						}
		    		}
					// End of Insert by Naga
		    		// Begin of Insert by Naga
		    		// If it is coming from Inbox, simply make it display only
		    		if(mode.equalsIgnoreCase("approval")){
		    			applicationMode = "locked";
//		    			readOnly = "disabled";
		    			disableButton = "disableButton";
		    			hideButton = "hideButton";
						if(termsDisplayOnly.trim().length()==0){
							termsDisplayOnly = " locked ";
						}		    			
		    		}
		    		// End of Insert by Naga
		    		// Added 095 - Pranesh - Test - (04/14/2016)
					if (!(vendorType.equalsIgnoreCase("050")) || (vendorType.equalsIgnoreCase("080")) || (vendorType.equalsIgnoreCase("095")) ){			
						contactsRequiredDisplay = "required-red";
						contactsRequiredInput = "required";					
					}					
					
					// End of Insert by Naga
					
	
					// Naga ENHC0013685 Add Revenue Share Vendor type 091
					// Naga ENHC0013683 Add Garnishment Vendor type 092
					// Naga ENHC0016458 Add Government Vendor type 050
					
					// Added 095,018,030 - Pranesh(04/16/2016) - ENHC0016459
					if ((vendorType.equalsIgnoreCase("010")) || (vendorType.equalsIgnoreCase("020")) || (vendorType.equalsIgnoreCase("030")) || (vendorType.equalsIgnoreCase("040")) || (vendorType.equalsIgnoreCase("050")) || (vendorType.equalsIgnoreCase("060")) || (vendorType.equalsIgnoreCase("080")) || (vendorType.equalsIgnoreCase("090")) || (vendorType.equalsIgnoreCase("091")) || (vendorType.equalsIgnoreCase("092")) || (vendorType.equalsIgnoreCase("093")) || (vendorType.equalsIgnoreCase("094"))|| (vendorType.equalsIgnoreCase("018")) || (vendorType.equalsIgnoreCase("095"))  ){			
						bankingRequiredDisplay = "required-red";
						bankingRequiredInput = "required";					
					}
					
				if ( requestId == null){
					requestId = "";
					decisionVendorType = "V"+vendorType;
				} else {
					decisionVendorType = "R"+vendorType;
				}
				
				// ENHC0016458, check allowed logic has to be changed.
				// Removing Political Contribution, Government, Utility from this list.
//				if ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){
				
				// Added 095,018 - Pranesh(04/16/2016) - ENHC0016459
				
				if ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R070")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R095")) ){
					checkAllowed = true;
				}					
				response.write(components.displayHeader(request,"vendor"));
				if(arraySecondaryAccount != null)
				{
				//id=\"requestType\" added by Kermel Ruperto 07-10-2014
					response.write("<script id=\"secondaryAccountJson\" type=\"application/json\">"+
					"{\"secondaryAccount\":[");
					for (int j = 0; j < arraySecondaryAccount.length; j++) 
					{
						// If there is a blank record skip it
						if(arraySecondaryAccount[j][7] == null || arraySecondaryAccount[j][7].trim().equals(" ") || arraySecondaryAccount[j][7].equals("")){
							continue;
						}
						response.write("{" +
								"\"country\":\"" +arraySecondaryAccount[j][3]+"\""+
								",\"routingNum\":\"" + arraySecondaryAccount[j][4]+"\""+
								",\"accountNum\":\"" +arraySecondaryAccount[j][5]+"\""+
								",\"type\":\"" +arraySecondaryAccount[j][7] +"\""+
								",\"accountHolder\":\"" +arraySecondaryAccount[j][10]+"\""+
								",\"fileName\":\"" +arraySecondaryAccount[j][11]+"\""+
								",\"objectId\":\"" +arraySecondaryAccount[j][12]+"\""+ //Naga DFCT0013582								
								",\"swiftAccountNum\":\""+arraySecondaryAccount[j][14]+"\""+		// ENHC0013668
								",\"ibanNum\":\""+arraySecondaryAccount[j][15]+"\""+				// ENHC0013668
								",\"tempaccountNum\":\"" +arraySecondaryAccount[j][13]+"\""+ // ganesh
								"},");
					}
					response.write("]}\n</script>");
					}
					
				
				// Added Role Check to Avoid unauthorised access DFCT0017729 and allowed SNAP Request

					if(!mode.equalsIgnoreCase("approval") && !accessAllowed)	{
					response.write("<div style=\"margin:0px 5%\"><b>The VeRA inviter role is not assigned to your SSO.  You can request access through a CAM access request in Secure Pro. Complete instructions for Requesting the VeRA Inviter Role can be found in the VeRA End User guide posted on the SNAP home page at <a href='http://snap.inbcu.com/documents/vera/'>http://snap.inbcu.com/documents/vera/</a>.</b></div>");
				} 
				// end code
				else
				{
				response.write("<form id=\"main\" onkeypress=\"return event.keyCode != 13;\" action=\"../json/saveVendor.json\" method=\"POST\">\n"+ // DFCT0016721 ganesh autosave prevent on enter
						"<input type=\"hidden\" id=\"vendorId\" name=\"vendorId\" value=\""+vendorId+"\"/>\n"+
						"<input type=\"hidden\" id=\"requestId\" name=\"requestId\" value=\""+requestId+"\"/>\n"+
						"<input type=\"hidden\" name=\"vendorType\" id=\"vendorType\" value=\""+vendorType+"\"/>\n"+	
						"<input type=\"hidden\" name=\"decisionVendorType\" value=\""+decisionVendorType+"\"/>\n"+							
						"<input type=\"hidden\" id=\"requestType\" name=\"requestType\" value=\""+requestType+"\"/>\n"+							
						"<input type=\"hidden\" id=\"uploadDocumentType\" name=\"uploadDocumentType\" value=\"Z_REQUEST\"/>\n"+
						"<input type=\"hidden\" id=\"arrayCompanyCodes\" name=\"arrayCompanyCodes\" value=\""+companyCodes+"\"/>\n"+
						"<input type=\"hidden\" id=\"arrayCompanyCodes\" name=\"subSystems\" value=\""+subSystems+"\"/>\n"+
						"<input type=\"hidden\" id=\"ersValue\" name=\"ersValue\" value=\""+ersValue+"\"/>\n"+
						"<input type=\"hidden\" id=\"userName\" name=\"userName\" value=\""+userName+"\"/>\n"+
						"<input type=\"hidden\" id=\"userType\" name=\"userType\" value=\""+userType+"\"/>\n"+	
						"<input type=\"hidden\" id=\"selectedApprover\" name=\"selectedApprover\" value=\""+approverSSO+"\"/>\n"+							
						"<input type=\"hidden\" id=\"numberOfSecondaryAddress\" name=\"numberOfSecondaryAddress\" value=\""+retCT_LFA1.getNumRows()+"\"/>\n"+							
						"<input type=\"hidden\" id=\"annualSpend\" name=\"annualSpend\" value=\""+annualSpend+"\"/>\n"+							
						"<input type=\"hidden\" id=\"comments\" name=\"comments\" value=\""+comments+"\"/>\n"+	
						"<input type=\"hidden\" id=\"tAndC\" name=\"tAndC\" value=\""+tAndC+"\"/>\n"+	
						"<input type=\"hidden\" id=\"urlcti\" name=\"tAndC\" value=\""+urlCTI+"\"/>\n"+		// ENHC0013673 1228
						"<input type=\"hidden\" id=\"ernamcti\" name=\"tAndC\" value=\""+ernamCTI+"\"/>\n"+	// ENHC0013673 1228
						"<input type=\"hidden\" id=\"regCodecti\" name=\"tAndC\" value=\""+regCodeCTI+"\"/>\n"+	// ENHC0013673 1228
						"<input type=\"hidden\" id=\"status\" value=\""+status+"\"/>\n"+	// ENHC0019060
						"<input type=\"hidden\" id=\"taxJus\" name=\"taxJus\" value=\"\" />"+//DFCT0016721

						// ENHC0013673
						"<div class=\"content\">\n"+
				
				            "<div class=\"container alerts\">\n"+
				            "</div>\n"+
				
				            "<div class=\"container\">\n"+
				            	
				            	// Begin of Insert by Naga ENHC0013682
				            	"<div class=\"row nonbsformaction\">\n" +
				            		"<div class=\"span8\">\n" +
	                            	"<div style=\"background-color:transparent;\">"+
	                            	((!mode.equalsIgnoreCase("approval"))&&(!(requestType.equals("1")))&&((status.equalsIgnoreCase("S") || status.equalsIgnoreCase("T") || status.equalsIgnoreCase("W") || status.equalsIgnoreCase("R") || status.equalsIgnoreCase("F") || status.equalsIgnoreCase("I") || status.equalsIgnoreCase("M") || status.equalsIgnoreCase("E")))?"<a class=\"btn btn-cancel btn-success  badge badge-success pull-right\" id=\"cancelRequestBtn\"><i class=\"icon-remove\"></i>Cancel</a>\n":"")+
	                            	((!mode.equalsIgnoreCase("approval"))&&(!(requestType.equals("1")))&&((status.equalsIgnoreCase("W") || status.equalsIgnoreCase("T") || status.equalsIgnoreCase("I") || status.equalsIgnoreCase("M") || status.equalsIgnoreCase("E")))?"<a class=\"btn btn-resend btn-success badge badge-success pull-right action\" id=\"resendApprovalBtn\"><i class=\"icon-repeat\"></i>Resend for Approval</a>\n":""));
									if(mode.equalsIgnoreCase("approval")&&(status.equals("W")||status.equals("I")||status.equals("E")||status.equals("M")||status.equals("V")||status.equals("T"))){
										response.write(
				                            	"<div class=\"btn-group pull-right\">"+
													"<a class=\"btn btn-success\" id=\"approveButton\">Approve</a>"+
					                            	"<a class=\"btn btn-danger\" id=\"rejectButton\">Reject</a>"+
					                            "</div>"												
												);
									}
	                            	response.write(
                                    "</div>\n"+
				            		"</div>" +
				            		"<div class=\"span4\">\n" +
				            		"</div>" +
				            	"</div>"+
				            	
				            	// End of Insert by Naga
				                "<div class=\"row\">\n"+
				                    "<div class=\"span8\">\n"+
				                        "<div class=\"alert alert-info\" id=\"lockedAlert\">\n"+
				                            "You cannot edit the form while it is in approval.\n"+
				                        "</div>\n"+
//				                        // Begin of Insert by Naga ENHC0013682
//		                            	"<div class=\"form-actions\" style=\"background-color:transparent;\">"+
//		                            	((status.equalsIgnoreCase("S") || status.equalsIgnoreCase("W") || status.equalsIgnoreCase("R") || status.equalsIgnoreCase("F") || status.equalsIgnoreCase("I") || status.equalsIgnoreCase("E"))?"<a class=\"btn btn-cancel btn-success  badge badge-success pull-right\" id=\"cancelRequestBtn\"><i class=\"icon-remove\"></i>Cancel</a>\n":"")+
//		                            	((status.equalsIgnoreCase("W") || status.equalsIgnoreCase("T") || status.equalsIgnoreCase("I") || status.equalsIgnoreCase("E"))?"<a class=\"btn btn-resend btn-success badge badge-success pull-right\" id=\"resendApprovalBtn\"><i class=\"icon-repeat\"></i>Resend for Approval</a>\n":"")+
//                                        "</div>\n"+				                        
//				                        // End of Insert by Naga
				                        "<ul class=\"nav nav-tabs\" id=\"wizard\">\n");
										// Modified by CGUTJAHR 1/13/15  : Enhancement #41
										// Begin of Insert by Naga ENHC0013683
										// Hide tax tab for Garnishment Vendor
										if ( vendorType.contains("092")){
				                            response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
						                            "<li class=\"hidden\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
						                            "<li class=\"disabled\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+
						                            "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\" ><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
						                            "<li class=\"disabled\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Contacts</a></li>\n");
				                        // End of Insert by Naga				                            
				                        // Begin of Insert by Naga ENHC0016458 and ENHC0016461    
										} else if ( vendorType.contains("040") || vendorType.contains("050")){
				                            response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
						                            "<li class=\"hidden\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
						                            "<li class=\"hidden\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+//DFCT0016953
						                            "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
						                            "<li class=\"disabled\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Contacts</a></li>\n");											
										} else if ( vendorType.contains("093") || vendorType.contains("094")){
				                            response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
						                            "<li class=\"disabled\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
						                            "<li class=\"hidden\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+//DFCT0016953
						                            "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
				                            		"<li class=\"disabled\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Contacts</a></li>\n"); 									
										} else
										// End of Insert by Naga
											
				                        if ( !vendorType.contains("060")){
				                        	/*
				                        	 //before change - Pranesh - 05-04-2016 - ENHC0016459
				                        	 response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"termsTab\"><i class=\"icon-ok\"></i>Terms</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"bankingTab\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
				                            "<li class=\"disabled\"><a id=\"contactsTab\"><i class=\"icon-ok\"></i>Contacts</a></li>\n");
				                        	 */
				                        	
				                        	//Begin Of Insert by Pranesh - 05-04-2016 - ENHC0016459
											//Hide Contacts Tab specfic to VendorType 095
				                        	if ( vendorType.contains("095")){ // Added - Pranesh (04/28/2015)-(Defect ID : 15048),Hidden terms tab
				                        		response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
									            "<li class=\"disabled\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
									            "<li class=\"hidden\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+//DFCT0016953
									            "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
									            "<li class=\"hidden\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Contacts</a></li>\n");
				                        	}else{
				                        		response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
				                        	    "<li class=\"disabled\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
				                        	    "<li class=\"disabled\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+//DFCT0016953
				                        	    "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Banking</a><span></span></li>\n"+
				                        		"<li class=\"disabled\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Contacts</a></li>\n");
				                        	}
				                        	//End Of Insert by Pranesh - 05-04-2016 - ENHC0016459
				                        	
				                        } else {
				                            response.write("<li class=\"active\"><a id=\"basicTab\"><i class=\"icon-ok\"></i>Basic</a><span></span></li>\n"+
				                            "<li class=\"hidden\"><a id=\"taxTab\"><i class=\"icon-ok\"></i>Tax</a><span></span></li>\n"+
				                            "<li class=\"hidden\"><a id=\"termsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\"></i>Payment Terms</a><span></span></li>\n"+//DFCT0016953
				                            "<li class=\"disabled\"><a id=\"bankingTab\" style=\"font-size:medium;\"><i class=\"icon-ok\" ></i>Banking</a><span></span></li>\n"+
				                            "<li class=\"hidden\"><a id=\"contactsTab\" style=\"font-size:medium;\"><i class=\"icon-ok\" ></i>Contacts</a></li>\n");				                        	
				                        }
				                        response.write("</ul>\n"+
										// END
				                        "<div class=\"tab-content\">\n"+
				                            "<div class=\"locked-overlay\"></div>\n"+
				                            "<div class=\"tab-pane active fade in\" id=\"tab1\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<h1>Vendor Name</h1>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"span6\">\n"+
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label required-red\"> Legal Name <i class=\"icon-question-sign tip\" title=\"Name Line 1 on tax form\"></i></label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input required class=\"input-block-level special-char-validation\" type=\"text\" name=\"legalName\" pattern=\"[a-zA-Z0-9&'\\- \\/]+\" placeholder=\"Legal Name\" maxlength=\"70\" value=\""+legalName+"\">\n"+  //DFCT0016715 Increased to 70 characters
				                                                    	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only /,&,- and '.</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                  // Modified by CGUTJAHR on 1/13/15 : Enhancement #41
				                                            "</div>\n");
				                        		  // ENHC0016461 Add Legal Settlement and Contest Winner
				                        		  // Added vendor type - 095, Pranesh(12-04-2016) - ENHC0016459
								                  if ( !(vendorType.contains("060") || vendorType.contains("093") || vendorType.contains("094") || vendorType.contains("095"))){
				                                            response.write("<div class=\"span6\">\n"+
												  // END
				                                                "<div class=\"control-group\">\n"+
				                                                    "<label class=\"control-label\">Invoicing Name <i class=\"icon-question-sign tip\" title=\"Name Line 2 on tax form\"></i></label>\n"+
				                                                    "<div class=\"controls\">\n"+
				                                                        "<input class=\"input-block-level special-char-validation\" type=\"text\" name=\"invoicingName\"  pattern=\"[a-zA-Z0-9&'\\- ]+\" placeholder=\"Invoicing Name\"  maxlength=\"70\" value=\""+invoicingName+"\">\n"+  //DFCT0016715 Increased to 70 characters
				        				                                "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only &,- and '.</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        "<h1>Additional Information</h1>\n"+
				                                        "<div class=\"additional-info\">\n"+
				                                        	// Begin of comment by Naga ENHC0018723
//				                                            "<div class=\"control-group\">\n"+
//				                                                "<label class=\"control-label required-red\"> Minority Code <i class=\"icon-question-sign tip\" title=\"A business that is at least 51 percent owned by, and whose management and daily business operations are controlled by one or more ethnic minorities, women, veteran, or LGBT who are U.S. citizens.\"></i></label>\n"+
//				                                                "<!-- minority: "+retCT_REQ+"-->\n"+
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"minorityCode\" required>\n"+
//				                                                        "<option value=\"\">Select One </option><!--"+minorityCode+"-->\n");
//																			for (int x = 0; x < arrayMinorityCode.length; x++) {
//																					if (arrayMinorityCode[x][0].equalsIgnoreCase(minorityCode)){
//																						response.write("<option value=\""+arrayMinorityCode[x][0]+"\" selected>"+arrayMinorityCode[x][1]+"</option>");				
//																					} else {
//																						response.write("<option value=\""+arrayMinorityCode[x][0]+"\">"+arrayMinorityCode[x][1]+"</option>");											
//																					}
//																			}
//				                                                    response.write("</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
//				                                            "<div class=\"control-group\">\n"+
//				                                                "<label class=\"control-label required-red\"> Industry Code</label>\n"+
//				                                                "<!-- "+industryCode+"-->\n"+				                                                
//				                                                "<div class=\"controls\">\n"+
//				                                                    "<select name=\"industryCode\" required>\n"+
//				                                                        "<option value=\"\">Select One</option>\n");
//																			for (int x = 0; x < arrayIndustryCode.length; x++) {
//																					if (arrayIndustryCode[x][0].equalsIgnoreCase(industryCode)){
//																						response.write("<option value=\""+arrayIndustryCode[x][0]+"\" selected>"+arrayIndustryCode[x][1]+"</option>");
//																					} else {
//																						response.write("<option value=\""+arrayIndustryCode[x][0]+"\">"+arrayIndustryCode[x][1]+"</option>");
//																					}																				
//																			}
//				                                                    response.write("</select>\n"+
//				                                                "</div>\n"+
//				                                            "</div>\n"+
				                                        // End of comment by Naga
				                                             "<!-- "+poEmailAddress+"-->\n");
				                                            	if (poEmailAddress.length()>1){
							                                         response.write("<div class=\"control-group\">\n"+		                                                    
						                                                "<label class=\"control-label checkbox\">\n"+
						                                                    "Accept P.O.?\n"+
						                                                    "<input type=\"checkbox\" id=\"poCheckbox\" name=\"acceptPO\" checked/></label>\n"+
						                                                "<div class=\"controls span4\">\n"+// Added span4 - Pranesh(04/20/2016) - ENHC0018725
						                                                    "<div class=\"input-prepend po-email\">\n"+
						                                                        "<div class=\"disabled-overlay\" style=\"display: none;\"></div>\n"+
						                                                        "<span class=\"add-on tip\" title=\"Email Address for P.O.\"><i class=\"icon-envelope\"></i></span>\n"+
						                                                        "<input type=\"email\" name=\"poEmail\" placeholder=\"P.O. Email Address\" value=\""+poEmailAddress+"\">\n"+
						                                                    "</div>\n"+
						                                                "</div>\n");
						                                          //"</div>\n");	Blocked Pranesh(04/20/2016) - ENHC0018725  		                                            		
				                                            	} else {
							                                         response.write("<div class=\"control-group\">\n"+			                                                    
						                                                "<label class=\"control-label checkbox\">\n"+
						                                                    "Accept P.O.?\n"+
						                                                    "<input type=\"checkbox\" id=\"poCheckbox\" name=\"acceptPO\" /></label>\n"+
						                                                "<div class=\"controls span4\">\n"+// Added span4 - Pranesh(04/20/2016) - ENHC0018725
						                                                    "<div class=\"input-prepend po-email\">\n"+
						                                                        "<div class=\"disabled-overlay\" style=\"display: block;\"></div>\n"+
						                                                        "<span class=\"add-on tip\" title=\"Email Address for P.O.\"><i class=\"icon-envelope\"></i></span>\n"+
						                                                        "<input type=\"email\" name=\"poEmail\" placeholder=\"P.O. Email Address\">\n"+
						                                                    "</div>\n"+
						                                                "</div>\n");
							                                     //"</div>\n");	Blocked Pranesh(04/20/2016) - ENHC0018725                                            		
				                                            	}
				                                 response.write("</div>\n");
				                                 
				                                 
				                                 /* Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
				                                 // Begin of insert Pranesh (04/20/2016) - ENHC0018725 
				                                 if(userIsInternalEmployeeBuyer){
				                                	 	response.write(
				                                	 			"<div class=\"span3\">\n"+
				                                	 				"<label class=\"control-label\">\n"+"Sourcing Vendor ?"+"</label>\n"+
				                                	 					"<div class=\"questions\">\n"+
				                                	 					    "<div class=\"accordion-heading\">\n"+
				                                	 						   "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                	 							   "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                	 							   "<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                	 						     "<div class=\"hidden-form-elements\">\n"+
				                                	 								"<input type=\"radio\" id=\"sourcingrelevant\" name=\"sourcingrelevant\" value=\"Y\" required=\"required\">\n"+
				                                	 								"<input type=\"radio\" id=\"sourcingrelevant\" name=\"sourcingrelevant\" value=\"N\" class=\"user-success\" required=\"required\">\n"+
				                                	 						     "</div>\n"+
				                                	 				         "</div>\n"+
				                                	 				       "</div>\n"+
				                                	 				   "</div>\n"+
				                                	 			"</div>\n");
				                                 		}
				                                  // End of insert Pranesh (04/20/2016) - ENHC0018725 
								                  */
								               }
								                  //End Change
				                                          response.write("<div class=\"clearfix\"></div>\n"+
				                                        "</div>\n");
				                                          	response.write(""+
				                                            ((requestType.equals("1")||vendorId==null||vendorId.trim().length()==0)?"<div class=\"control-group subSystem\" style=\"display: none;\">\n":"<div class=\"control-group subSystem\">\n")+ 
				                                                "<label class=\"control-label\">\n"+
				                                                    "Sub-System\n"+	
				                                                "</label>\n"+
					                                            "<div class=\"controls controls-scroll\">\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("T") != -1?"checked ":"")+"   type=\"checkbox\" name=\"maximo\" id=\"maximo\" /> Maximo (Technical)</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("F") != -1?"checked ":"")+"   type=\"checkbox\" name=\"eatec\" id=\"eatec\" /> Eatec (Food)</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("M") != -1?"checked ":"")+"   type=\"checkbox\" name=\"jda\" id=\"jda\" /> JDA (Merchandise)</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("C") != -1?"checked ":"")+"   type=\"checkbox\" name=\"costar\" id=\"costar\"/> Costar</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("V") != -1?"checked ":"")+"   type=\"checkbox\" name=\"vista\" id=\"vista\" /> Vista</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("P") != -1?"checked ":"")+"   type=\"checkbox\" name=\"compass\" id=\"compass\" /> Compass</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("J") != -1?"checked ":"")+"   type=\"checkbox\" name=\"compass_juice\" id=\"compass_juice\" /> Compass Juice</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("R") != -1?"checked ":"")+"   type=\"checkbox\" name=\"paris\" id=\"paris\" /> Paris</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("G") != -1?"checked ":"")+"   type=\"checkbox\" name=\"garnishment\" id=\"garnishment\" /> Garnishment</label>\n"+
																	"<label class=\"checkbox\"><input "+(subSystems.indexOf("S") != -1?"checked ":"")+"   type=\"checkbox\" name=\"trisepts\" id=\"trisepts\" /> Trisepts</label>\n"+   
					                                                "</div>\n"+
				                                            "</div>\n");			                                          

				                                        // TODO : About the Org
				                                        if ((decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R030")))
				                                        {             					                               
					                                        response.write("<h1>About the Organization</h1>\n"+
					                                        "<div class=\"diversity-info row-fluid\">\n"+
					                                            "<div class=\"control-group span4\">\n"+
					                                                "<label class=\"control-label required-red\"> Tax Exempt Status</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                    "<select name=\"taxExempt\" required>\n"+
					                                                    "<!-- "+taxExempt+" -->\n"+
					                                                        "<option value=\"\">Select One</option>\n");
																			for (int x = 0; x < arrayTaxExempt.length; x++) {
																					if (arrayTaxExempt[x][0].equalsIgnoreCase(taxExempt)){
																						response.write("<option value=\""+arrayTaxExempt[x][0]+"\" selected>"+arrayTaxExempt[x][1]+"</option>");
																					} else {
																						response.write("<option value=\""+arrayTaxExempt[x][0]+"\">"+arrayTaxExempt[x][1]+"</option>");
																					}																				
																			}	                                                        
																	response.write("</select>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                            "<div class=\"control-group span8\">\n"+
					                                                "<label class=\"control-label required-red\"> Organization Focus</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                    "<select name=\"organizationFocus\" required>\n"+
					                                                    "<!-- "+orgFocus+" -->\n"+	
					                                                        "<option value=\"\">Select One</option>\n");				                                                    
																		for (int x = 0; x < arrayrganizationFocus.length; x++) {
																				if (arrayrganizationFocus[x][0].equalsIgnoreCase(orgFocus)){
																					response.write("<option value=\""+arrayrganizationFocus[x][0]+"\" selected>"+arrayrganizationFocus[x][1]+"</option>");
																				} else {
																					response.write("<option value=\""+arrayrganizationFocus[x][0]+"\">"+arrayrganizationFocus[x][1]+"</option>");
																				}																				
																		}				                                                    
					                                                       response.write("</select>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                        "</div>\n"+
					                                        "<div class=\"diversity-info row-fluid\">\n"+
					                                            "<div class=\"control-group span6\">\n"+
					                                                "<label class=\"control-label\">Scale</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                    "<select name=\"companyScale\">\n"+
					                                                    "<!-- "+companyScale+" -->\n"+	
					                                                        "<option value=\"\">Select One</option>\n");
																		for (int x = 0; x < arrayCompanyScale.length; x++) {
																				if (arrayCompanyScale[x][0].equalsIgnoreCase(companyScale)){
																					response.write("<option value=\""+arrayCompanyScale[x][0]+"\" selected>"+arrayCompanyScale[x][1]+"</option>");
																				} else {
																					response.write("<option value=\""+arrayCompanyScale[x][0]+"\">"+arrayCompanyScale[x][1]+"</option>");
																				}																				
																		}	
					                                                    response.write("</select>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                            "<div class=\"control-group span6\">\n"+
					                                                "<label class=\"control-label\">FPNA Designation</label>\n"+
					                                                "<div class=\"controls\">\n"+
					                                                    "<select name=\"FPNA\">\n"+
					                                                    "<!-- "+FPNADesig+" -->\n"+
					                                                    "<option value=\"\">Select One</option>\n");				                                                    
																		for (int x = 0; x < arrayFPNADesigFocus.length; x++) {
																				if (arrayFPNADesigFocus[x][0].equalsIgnoreCase(FPNADesig)){
																					response.write("<option value=\""+arrayFPNADesigFocus[x][0]+"\" selected>"+arrayFPNADesigFocus[x][1]+"</option>");
																				} else {
																					response.write("<option value=\""+arrayFPNADesigFocus[x][0]+"\">"+arrayFPNADesigFocus[x][1]+"</option>");
																				}																				
																		}	
					                                                    response.write("</select>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                        "</div>\n"+
					                                        "<div class=\"control-group\">\n"+
					                                            "<label class=\"control-label\">Description of Organization</label>\n"+
					                                            "<div class=\"controls\">\n"+
					                                                "<textarea name=\"organizationDescription\" class=\"input-block-level diversity-textarea\">"+orgDesc+"</textarea>\n"+
					                                            "</div>\n"+
					                                        "</div>\n"+
					                                        "<br/>\n");
				                                        }            
				                                        // TODO: Diversity
				                                        if ((decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R030")))
				                                        {             
					                                        response.write("<h1>Diversity</h1>\n"+
					                                        "<label>Please provide your best estimate for the below information:</label>\n"+
					                                        "<label>For this section, please indicate which groups listed represent sections of your company. Please place a 0 in the fields that are not represented in your organization.</label>\n"+
					                                        "<div class=\"accordion questions diversity\" id=\"diversity\">\n"+
					                                            "<div class=\"accordion-group diveristy-start\">\n"+
					                                                "<div class=\"accordion-heading\">\n"+
					                                                    "<div class=\"diversity-group\">\n"+
					                                                        "<label>\n"+
					                                                            "Board of Directors <i class=\"icon-ok diversity-complete\"></i>\n"+
					                                                        "</label>\n"+
					                                                        "<a class=\"btn btn-mini btn-primary diversity-begin\" data-toggle=\"collapse\" href=\"#BoardOfDirectors\" data-parent=\"#diversity\">Begin</a>\n"+
					                                                        ((!applicationMode.equalsIgnoreCase("locked"))?
					                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#BoardOfDirectors\" data-parent=\"#diversity\" id=\"TestDiversity\"><i class=\"icon-pencil\"></i>Edit</a>\n":
				                                                        	"<a class=\"btn btn-mini diversity-edit\" data-target=\"#BoardOfDirectors\" data-parent=\"#diversity\" id=\"TestDiversity\"><i class=\"icon-chevron-sign-down\"></i> Expand</a>\n")+	
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
					                                                            "Senior Staff <i class=\"icon-question-sign tip\" title=\"Senior staff includes the Executive Director, President, CEO, etc. and their direct reports\"></i><i class=\"icon-ok diversity-complete\"></i>\n"+
					                                                        "</label>\n"+
					                                                        ((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
					                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#SeniorStaff\" data-parent=\"#diversity\"><i class=\"icon-pencil\"></i>Edit</a>\n":
					                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#SeniorStaff\" data-parent=\"#diversity\"><i class=\"icon-chevron-sign-down\"></i> Expand</a>\n")+	
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
					                                                        ((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
					                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#MembersServed\" data-parent=\"#diversity\"><i class=\"icon-pencil\"></i>Edit</a>\n":
					                                                        "<a class=\"btn btn-mini diversity-edit\" data-target=\"#MembersServed\" data-parent=\"#diversity\"><i class=\"icon-chevron-sign-down\"></i> Expand</a>\n")+ // ENHC0019060
					                                                    "</div>\n"+
					                                                "</div>\n"+
					                                                "<div class=\"accordion-body collapse\" id=\"MembersServed\">\n"+
					                                                    "<div class=\"diversity-progress\">\n"+
					                                                        "<div class=\"progress progress-ethnic\">\n"+
					                                                            "<div class=\"bar bar-inverse american-indian\" style=\"width: 0\"></div>\n"+
					                                                            "<div class=\"bar bar-inverse pacific-islander\" style=\"width: 0\"></div>\n"+
					                                                            "<div class=\"bar bar-inverse african-american\" style=\"width: 0\"></div>\n"+
					                                                            "<div class=\"bar bar-inverse hispanic-latino\" style=\"width: 0\"></div>\n"+
					                                                            "<div class=\"bar bar-inverse ethnic-white\" style=\"width: 0\"></div>\n"+
					                                                        "</div>\n"+
					                                                    "</div>\n"+
					                                                    "<div class=\"diversity-form\">\n"+
					                                                        "<div class=\"diversity-column\">\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">American Indian/Alaskan</label>\n"+
					                                                                "<div class=\"controls ethnic\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityAmericanIndian\" data-diversity=\"american-indian\"  class=\"ethnicity-input\" value=\""+diversityMembersAMERICANINDIANALASKAN+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">Asian/Pacific Islander</label>\n"+
					                                                                "<div class=\"controls ethnic\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityPacificIslander\" class=\"ethnicity-input\" data-diversity=\"pacific-islander\"  value=\""+diversityMembersASIANPACIFICISLANDER+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">Black/African American</label>\n"+
					                                                                "<div class=\"controls ethnic\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityAfricanAmerican\" data-diversity=\"african-american\" class=\"ethnicity-input \"  value=\""+diversityMembersBLANKAFRICANAMERICAN+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">Women</label>\n"+
					                                                                "<div class=\"controls members-served\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityWomen\" data-diversity=\"women-served\" class=\"member-served-input\" value=\""+diversityMembersWOMEN+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">People with Disabilities</label>\n"+
					                                                                "<div class=\"controls members-served\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityDisability\" data-diversity=\"disability\" class=\"member-served-input\" value=\""+diversityMembersDisability+"\">\n"+
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
					                                                                        "<input type=\"text\" name=\"msDiversityHispanc\" data-diversity=\"hispanic-latino\" class=\"ethnicity-input \" value=\""+diversityMembersHISPANICLATINO+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">White</label>\n"+
					                                                                "<div class=\"controls ethnic\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityWhite\" data-diversity=\"ethnic-white\" class=\"ethnicity-input \" value=\""+diversityMembersWHITE+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group diversity-blank\">\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">Veterans</label>\n"+
					                                                                "<div class=\"controls members-served\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityVetrans\" data-diversity=\"veterans\" class=\"member-served-input\" value=\""+diversityMembersVETRAN+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                            "<div class=\"control-group\">\n"+
					                                                                "<label class=\"control-label\">Gay/Lesbian/Bisexual/Transgender</label>\n"+
					                                                                "<div class=\"controls members-served\">\n"+
					                                                                    "<div class=\"input-append\">\n"+
					                                                                        "<input type=\"text\" name=\"msDiversityGay\" data-diversity=\"gay\" class=\"member-served-input\" value=\""+diversityMembersGAY+"\">\n"+
					                                                                        "<span class=\"add-on\">%</span>\n"+
					                                                                    "</div>\n"+
					                                                                "</div>\n"+
					                                                            "</div>\n"+
					                                                        "</div>\n"+
					                                                        "<div class=\"clearfix\"></div>\n"+
					                                                    "</div>\n"+
					                                                "</div>\n"+
					                                            "</div>\n"+
					                                        "</div>\n");
				                                        }
				                                        response.write("<div id=\"address-list-container\">\n"+
				                                            "<div class=\"country-input\">\n"+
				                                                "<h1>Primary Address</h1>\n"+
				                                            "</div>\n"+
				                                            "<div class=\"address "+hidePrimaryAddress+" primary-item\" id=\"primary-address\">\n"+
				                                                "<div class=\"accordion-group\">\n"+
				                                                    "<div class=\"accordion-body collapse in\" id=\"primary1\">\n"+
				                                                        "<div class=\"row-fluid\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label required-red\">Country</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<select class=\"input-block-level address-country\" required=\"required\" name=\"primaryAddressCountry\">\n" +
				                                                                        "<option value=\"\">Select One</option>\n"+//Req#50, Code added by AGAMPA on 2-19-2015.
				                                                                        "<!-- "+primaryCountry+" -->\n");
				                                        										//Req#603 START Code added by AGAMPA
				                                        										String defaultCountry = "US";
				                                        										if(primaryCountry != null && !primaryCountry.equals(""))
				                                        											defaultCountry = primaryCountry;
				                                        										//Req#603 END
				                                        										

																								for (int x = 0; x < arrayCountryCode.length; x++) {
																										if (arrayCountryCode[x][0].equalsIgnoreCase(defaultCountry)){
																											response.write("<option value=\""+arrayCountryCode[x][0]+"\" selected>"+arrayCountryCode[x][1]+"</option>");
																										} else {
																											response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
																										}																				
																								}
				                                                                        response.write("</select>\n"+
				                                                                        "<input id=\"defaultPCountryCode\" type=\"hidden\" value=\""+defaultCountry+"\" />\n"+		
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"row-fluid\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label required-red\">Address 1</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        // Change by Naga ENHC0013660, remove alpha-num-validation from Address1 of both primary and secondary.
				                                                                    	// Only slash should be allowed, previous change is corrected accordingly
				                                                                        //"<input class=\"input-block-level alpha-num-validation address1\" type=\"text\" name=\"primaryAddress1\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Street Name\" value=\""+primaryAddress1+"\">\n"+
				                                                                    	"<input class=\"input-block-level alpha-num-slash-validation address1\" type=\"text\" name=\"primaryAddress1\"  maxlength=\"35\" type=\"text\" name=\"primaryAddress1\"  pattern=\"[a-zA-Z0-9-&/' ]+\" value=\""+primaryAddress1+"\">\n"+ // ENHC0013668// Pranesh-(21/04/2016) ENHC0013678+
				                                                                		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: none;\">No special characters allowed only -&'/</div>"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
//				                                                                    "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+ ENHC0013668
				                                                                "<label class=\"control-label\">Address 2 </label>\n"+ // ENHC0013668
				                                                                    "<div class=\"controls\">\n"+
//				                                                                        "<input class=\"input-block-level alpha-num-validation address2\" type=\"text\" name=\"primaryAddress2\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Building or Unit Number\" value=\""+primaryAddress2+"\">\n"+ ENHC0013668
//				                                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+ // ENHC0013668				                                                                    
				                                                                    "<input class=\"input-block-level alpha-num-slash-validation address2\" type=\"text\" maxlength=\"40\" type=\"text\" name=\"primaryAddress2\"  pattern=\"[a-zA-Z0-9-&/' ]+\" value=\""+primaryAddress2+"\">\n"+ // ENHC0013668 // Pranesh-(21/04/2016) ENHC0013678
				                                                                   		"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only -&'/</div>"+ // ENHC0013668
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
//				                                                                    "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+ ENHC0013668
				                                                                    "<label class=\"control-label\">Address 3 </label>\n"+ // ENHC0013668
				                                                                    "<div class=\"controls\">\n"+
//				                                                                    "<input class=\"input-block-level alpha-num-validation address3\" type=\"text\" name=\"primaryAddress3\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Suite or Room Number\" value=\""+primaryAddress3+"\">\n"+ ENHC0013668
//				                                                                    "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+ // ENHC0013668				                                                                    
				                                                                    "<input class=\"input-block-level alpha-num-slash-validation address3\" type=\"text\" name=\"primaryAddress3\"  maxlength=\"40\" type=\"text\" name=\"primaryAddress3\"  pattern=\"[a-zA-Z0-9-&/' ]+\" value=\""+primaryAddress3+"\">\n"+ // ENHC0013668//Pranesh-(21/04/2016) ENHC0013678
				                                                                    	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only -&' /</div>"+ // ENHC0013668
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                        "<div class=\"row-fluid validate-group\">\n"+
				                                                            "<div class=\"span6\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                    "<label class=\"control-label required-red\">City</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level city special-char-validation-exceptdash\" type=\"text\" name=\"primaryAddressCity\"  pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"City\" value=\""+primaryCity+"\">\n"+
								                                                		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only - .</div>\n"+ 				                                                                  
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                // Added Region Pranesh-(21/04/2016) ENHC0013678
				                                                                    "<label class=\"control-label required-red\">State<span class=\"province-label hide\">/Province/Region</span></label>\n"+
				                                                                   "<!-- State: "+primaryState+" -->\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<select class=\"input-block-level state\" name=\"primaryAddressState\">\n");	
				                                                                        // Begin of Insert and comment by Naga ENHC0013660
				                                                                        /*
				                                                                        if (primaryCountry.equalsIgnoreCase("US")){
																							for (int x = 0; x < arrayUSStates.length; x++) {
																									if (arrayUSStates[x][0].equalsIgnoreCase( )){
																										response.write("<option value=\""+arrayUSStates[x][0]+"\" selected>"+arrayUSStates[x][1]+"</option>");
																									} else {
																										response.write("<option value=\""+arrayUSStates[x][0]+"\">"+arrayUSStates[x][1]+"</option>");
																									}																				
																							}				                                                                        	
				                                                                        }*/
				                                                                        
				                                                                        
																						for (int x = 0; x < arrayUSStates.length; x++) {
																								if (arrayUSStates[x][0].equalsIgnoreCase(defaultCountry)&&arrayUSStates[x][1].equalsIgnoreCase(primaryState)){
																									response.write("<option value=\""+arrayUSStates[x][1]+"\" selected>"+arrayUSStates[x][2]+"</option>");
																								} else if(arrayUSStates[x][0].equalsIgnoreCase(defaultCountry)){
																									response.write("<option value=\""+arrayUSStates[x][1]+"\">"+arrayUSStates[x][2]+"</option>");
																								}																				
																						} 				                                                                        	
		                                                                        
				                                                                        // End of Insert and comment by Naga ENHC0013660
				                                                                        response.write("</select>\n"+
				                                                                        "<input id=\"defaultPState\" type=\"hidden\" value=\""+primaryState+"\" />\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"span3\">\n"+
				                                                                "<div class=\"control-group\">\n"+
				                                                                // Removed required-red,Pranesh-(21/04/2016) ENHC0013678 
				                                                                    "<label class=\"control-label zip \">Zip<span class=\"postal-code-label hide \">/Postal</span> Code</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<input class=\"input-block-level zip special-char-validation-exceptdash\" type=\"text\" name=\"primaryAddressZip\" pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"Zip-Code\" value=\""+primaryZip+"\">\n"+ // Pranesh Defect : 15130 -(05/20/2016)
				                                                                        "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed expect -</div>"+ // Pranesh Defect : 15130 added line -(05/20/2016)
				                                                                        "<input type=\"hidden\" class=\"taxCode\"name=\"taxCode\" value=\""+taxJurisdiction+"\">\n"+
		                                                                   		        "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n");
				                                                // Edited by CGUTJAHR 1/13/15 : Enhancement #41
    							                        		// ENHC0016461 Add Legal Settlement and Contest Winner
				                                                // Added vendor type - 095, Pranesh(12-04-2016) - ENHC0016459
    											                if ( !(vendorType.contains("060") || vendorType.contains("093") || vendorType.contains("094") || vendorType.contains("095")   ))
    											                	response.write("<h1 class=\"secondary-item-legend\" id=\"secondary-addresses-legend\">Secondary Addresses</h1>\n");
				                                               // END
				                                               
				                                            response.write("</div>\n");
				                                            
			                                                // Edited by CGUTJAHR 1/13/15 : Enhancement #41
							                        		// ENHC0016461 Add Legal Settlement and Contest Winner
				                                            // Added vendor type - 095, Pranesh(12-04-2016) - ENHC0016459
											                if ( !(vendorType.contains("060") || vendorType.contains("093") || vendorType.contains("094") || vendorType.contains("095") )){
				                                             
				                                            response.write("<div class=\"accordion address secondary-item\" id=\"secondary-address\">");
				                                            
															// TODO Secondary Addresses
				                                            // --- Display existing Addresses 
				                                            if(retCT_LFA1 != null){                                      	
				                                            	int maxRows = retCT_LFA1.getNumRows();
				                                            	
				                                            	// Ignore the Primary Address
				                                            	retCT_LFA1.nextRow();
				                                            	
				                                            	for(int i = 1; i < maxRows; i++) {
				                                            		
				                                            		String secondaryLegalName = retCT_LFA1.getString("NAME1");
																	String secondaryAddress1 = retCT_LFA1.getString("STRAS");
																	String secondaryAddress2 = retCT_LFA1.getString("STR_SUPPL1");
																	String secondaryAddress3 = retCT_LFA1.getString("STR_SUPPL2");
																	String secondaryCountry = retCT_LFA1.getString("LAND1");
																	String secondaryCity = retCT_LFA1.getString("ORT01");
																	String secondaryState = retCT_LFA1.getString("REGIO");
																	String secondaryZip = retCT_LFA1.getString("PSTLZ");
				                                            		String secondaryType = retCT_LFA1.getString("KTOKK");
				                                            		String secondaryEmail = retCT_LFA1.getString("SMTP_ADDR");
				                                            		String secondaryFax = retCT_LFA1.getString("TELFX");
				                                            		String secondaryTaxCode = retCT_LFA1.getString("TXJCD");
				                                            		String secondaryVendorId = retCT_LFA1.getString("LIFNR");				                                            		
				                                            		
                                                        //Added by Philippe
																	response.write(
                                  "<div class=\"accordion-group\" id=\"secondary-address-group-view"+i+"\">\n"+
                                  "<input type=\"hidden\" name=\"secondaryAddress-view"+i+"-VendorId\" value=\""+secondaryVendorId+"\">\n"+
//                                  										"<div class=\"accordion-heading\">\n"+						// ENHC0013668 1228 added active
															            "<div class=\"accordion-heading active\">\n"+						// ENHC0013668 1228 added active
															                "<label class=\"item-label\">"+secondaryLegalName+"</label>\n"+		
															                "<i class=\"icon-remove tip "+hideButton+"\" title=\"\" data-index=\""+i+"\" data-original-title=\"Remove\"></i>\n"+ // ENHC0013668 1228 changed remove to trash
//															                "<i class=\"icon-trash tip\" title=\"\" data-index=\""+i+"\" data-original-title=\"Remove\"></i>\n"+ // ENHC0013668 1228 changed remove to trash
															                "<a class=\"btn btn-mini edit-item\" href=\"#secondary-address-view"+i+"\" data-parent=\"#secondary-address\">\n"+
															                ((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
															                    "<i class=\"icon-pencil\"></i>Edit\n":
															                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+		// ENHC0019060
															                "</a>\n"+
															                "<div class=\"clearfix\"></div>\n"+
															            "</div>\n"+
//															            "<div class=\"accordion-body secondary-item collapse\" id=\"secondary-address-view"+i+"\" style=\"height: 0px;\">\n"+ // ENHC0013668 1228
															            "<div class=\"accordion-body secondary-item in collapse\" id=\"secondary-address-view"+i+"\" style=\"height: auto\">\n"+ // ENHC0013668 1228															
															                "<div class=\"accordion-inner\">\n"+
															                    "<div class=\"row-fluid\">\n"+
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label required-red\">Vendor Name</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level vendor-name special-char-validation\" type=\"text\" name=\"secondaryAddress-view"+i+"-vendorName\"  pattern=\"[a-zA-Z0-9&'\\- \\/]+\" placeholder=\"Vendor Name\" maxlength=\"35\" value=\""+secondaryLegalName+"\">\n"+
															                             		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 9.5pt;display: none;\">No special characters allowed only /,&,- and '.</div>\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label\">Country</label>\n"+
															                                "<!-- "+secondaryCountry+" -->\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<select class=\"input-block-level address-country\" required=\"required\" name=\"secondaryAddress-view"+i+"-country\">\n"+
															                                    "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
															                                    
																										for (int x = 0; x < arrayCountryCode.length; x++) {
																												if (arrayCountryCode[x][0].equalsIgnoreCase(secondaryCountry)){
																													response.write("<option value=\""+arrayCountryCode[x][0]+"\" selected>"+arrayCountryCode[x][1]+"</option>");
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
															                                "<label class=\"control-label required-red \">Address 1</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                	// Change by Naga ENHC0013660, remove alpha-num-validation from Address1 of both primary and secondary.
															                                	// Only slash should be allowed, previous change is corrected accordingly
															                                    //"<input class=\"input-block-level header-input address1 alpha-num-validation\" required=\"\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address1\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Street Name\" value=\""+secondaryAddress1+"\">\n"+
															                                	"<input class=\"input-block-level header-input address1 alpha-num-slash-validation\" required=\"\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address1\"  pattern=\"[a-zA-Z0-9/ ]+\" placeholder=\"Street Name\" value=\""+secondaryAddress1+"\">\n"+
				                                                                				"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: none;\">No special characters allowed only /</div>"+   
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
//															                                "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"\" data-placement=\"right\" data-original-title=\"Building or Unit Number\" ></i></label>\n"+ ENHC0013668
															                            "<label class=\"control-label\">Address 2 </label>\n"+ // ENHC0013668
															                                "<div class=\"controls\">\n"+
//															                                    "<input class=\"input-block-level address2 alpha-num-validation\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address2\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Building or Unit Number\" value=\""+secondaryAddress2+"\">\n"+  ENHC0013668
//															                                "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+    // ENHC0013668															                                
															                                "<input class=\"input-block-level address2 alpha-num-slash-validation\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address2\"  pattern=\"[a-zA-Z0-9 /]+\" value=\""+secondaryAddress2+"\">\n"+	  // ENHC0013668			 											                                
				                                                                   				"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only /</div>"+    // ENHC0013668
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
//															                                "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"\" data-placement=\"right\" data-original-title=\"Suite or Room Number\"></i></label>\n"+ 	// ENHC0013668
															                            "<label class=\"control-label\">Address 3 </label>\n"+			// ENHC0013668												                            
															                                "<div class=\"controls\">\n"+
//															                                    "<input class=\"input-block-level address3 alpha-num-validation\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address3\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Suite or Room Number\" value=\""+secondaryAddress3+"\">\n"+	ENHC0013668
//															                                	"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+ // ENHC0013668
															                                    "<input class=\"input-block-level address3 alpha-num-slash-validation\" type=\"text\" name=\"secondaryAddress-view"+i+"-Address3\"  pattern=\"[a-zA-Z0-9 /]+\" value=\""+secondaryAddress3+"\">\n"+	// ENHC0013668															                                    
				                                                                   				"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only /</div>"+ // ENHC0013668    															                                
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"row-fluid validate-group\">\n"+
															
															                        "<div class=\"span6\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label required-red\">City</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level city special-char-validation-exceptdash\" required=\"\" type=\"text\" pattern=\"[a-zA-Z0-9- ]+\" name=\"secondaryAddress-view"+i+"-City\"  placeholder=\"City\" value=\""+secondaryCity+"\">\n"+
										                                                		"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only - .</div>\n"+ 													                                	
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                                              
															                                "<label class=\"control-label required-red \">State<span class=\"province-label hide\" style=\"display: none;\">/Province</span></label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<select class=\"input-block-level state\" name=\"secondaryAddress-view"+i+"-State\" required=\"\">\n"+
															                                        "<option value=\"\">Select State</option>\n");	
															                                    	// Begin of Insert and Comment by Naga ENHC0013660
															                                        // Get the Countries and Regions from backend.
															                                    	/*
							                                                                        if (secondaryCountry.equalsIgnoreCase("US")){
																										for (int x = 0; x < arrayUSStates.length; x++) {
																												if (arrayUSStates[x][0].equalsIgnoreCase(secondaryState)){
																													response.write("<option value=\""+arrayUSStates[x][0]+"\" selected>"+arrayUSStates[x][1]+"</option>");
																												} else {
																													response.write("<option value=\""+arrayUSStates[x][0]+"\">"+arrayUSStates[x][1]+"</option>");
																												}																				
																										}				                                                                        	
							                                                                        }*/
							                                                                        
																									for (int x = 0; x < arrayUSStates.length; x++) {
																										if (arrayUSStates[x][0].equalsIgnoreCase(secondaryCountry)&&arrayUSStates[x][1].equalsIgnoreCase(secondaryState)){
																											response.write("<option value=\""+arrayUSStates[x][1]+"\" selected>"+arrayUSStates[x][2]+"</option>");
																										} else if(arrayUSStates[x][0].equalsIgnoreCase(secondaryCountry)){
																											response.write("<option value=\""+arrayUSStates[x][1]+"\">"+arrayUSStates[x][2]+"</option>");
																										}																				
																									}							                                                                        
							                                                                        // End of Insert and Comment by Naga ENHC0013660
							                                                                        response.write("</select>\n"+												                                        
															                                    "<input type=\"text\" class=\"input-block-level province hide\" name=\"secondaryAddress-view"+i+"-Province\"  pattern=\"[a-zA-Z0-9 ]+\" style=\"display: none;\" value=\""+secondaryState+"\">\n"+
															                                "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                        "<div class=\"span3\">\n"+
															                            "<div class=\"control-group\">\n"+
															                                "<label class=\"control-label zip required-red\"  name=\"secondaryview"+i+"Zip\">Zip<span class=\"postal-code-label hide\" style=\"display: none;\">/Postal</span> Code</label>\n"+
															                                "<div class=\"controls\">\n"+
															                                    "<input class=\"input-block-level zip special-char-validation-exceptdash\" type=\"text\" required=\"\" name=\"secondaryAddress-view"+i+"-Zip\" pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"Zip-Code\" value=\""+secondaryZip+"\">\n"+ // Pranesh Defect : 15130 -(05/20/2016)
															                                    "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only except -</div>"+ // Pranesh Defect : 15130 added line -(05/20/2016)
															                                    "<input type=\"hidden\" class=\"taxCode\"name=\"secondaryAddress-view"+i+"-taxCode\" value=\""+secondaryTaxCode+"\">\n"+
				                                                                   		   "</div>\n"+
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"remit-option\">\n"+
															                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
							                                                                String showAdditionalPurchaseInfo = "";
								                                                            if (secondaryType.equalsIgnoreCase("R001")){
								                                                            	response.write("<button type=\"button\" class=\"btn btn-info remit-button "+disableButton+" active\"><i class=\"icon-check-sign\"></i>Remit</button>&nbsp;&nbsp;&nbsp;\n");
								                                                            // Begin of Insert by Naga ENHC0015302
								                                                            // If it is not purchasing keep Remit as Default
								                                                            } else if(secondaryType.equalsIgnoreCase("")){
								                                                            	response.write("<button type=\"button\" class=\"btn btn-info remit-button "+disableButton+" active\"><i class=\"icon-check-sign\"></i>Remit</button>&nbsp;&nbsp;&nbsp;\n");
								                                                            // End of Insert by Naga ENHC0015302	
								                                                            } else {
								                                                            	response.write("<button type=\"button\" class=\"btn btn-info remit-button "+disableButton+" \"><i class=\"icon-check-sign\"></i>Remit</button>&nbsp;&nbsp;&nbsp;\n");                                                        	
								                                                            }
	
								                                                            if (secondaryType.equalsIgnoreCase("B001")){
								                                                            	showAdditionalPurchaseInfo = "shown";
								                                                            	response.write("<button type=\"button\" class=\"btn btn-info purchasing-button "+disableButton+" active\"><i class=\"icon-check-sign\"></i>Purchasing</button>\n");                                                      	
								                                                            } else {
								                                                            	response.write("<button type=\"button\" class=\"btn btn-info purchasing-button "+disableButton+" \"><i class=\"icon-check-sign\"></i>Purchasing</button>\n"); 							                                                            	
								                                                            }
							                                                            
															                            response.write("<div class=\"hidden-form-elements\">\n");
									
																	                       if (secondaryType.equalsIgnoreCase("R001")){
								                                                            	response.write("<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"remit\" checked>\n");
									                                                            // Begin of Insert by Naga ENHC0015302
									                                                            // If it is not purchasing keep Remit as Default
									                                                       } else if(secondaryType.equalsIgnoreCase("")){
									                                                    	   response.write("<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"remit\" checked>\n");
									                                                            // End of Insert by Naga ENHC0015302	
								                                                            	
								                                                            } else {
								                                                            	response.write("<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"remit\">\n");                                                        	
								                                                            }
	
								                                                            if (secondaryType.equalsIgnoreCase("B001")){
								                                                            	response.write("<input type=\"radio\" class=\"purchasing\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"purchasing\" checked>\n");                                                      	
								                                                            } else {
								                                                            	response.write("<input type=\"radio\" class=\"purchasing\" name=\"secondaryAddress-view"+i+"-RemitPurchase\" value=\"purchasing\">\n"); 							                                                            	
								                                                            }						                            

															                                
															              response.write("</div>\n"+
															                            "<a href=\"#\" class=\"tip\" data-html=\"true\" data-placement=\"top\" data-title=\"&lt;label&gt;Remit&lt;/label&gt;&lt;p&gt;Decription of what a remit address is.&lt;/p&gt;&lt;label&gt;Purchasing&lt;/label&gt;&lt;p&gt;Decription of what a purchasing address is.&lt;/p&gt;\" data-original-title=\"\" title=\"\">What is the difference?</a>\n"+
															                        "</div>\n"+
															                        "<div class=\"purchasing-contact "+showAdditionalPurchaseInfo+"\">\n"+
															                            "<span class=\"caret\"></span>\n"+
															                            "<div class=\"row-fluid\">\n"+
															                                "<div class=\"span6\">\n"+
															                                    "<div class=\"control-group\">\n"+
															                                        "<label class=\"control-label\">Email <i class=\"icon-question-sign tip\" data-placement=\"right\" title=\"\" data-original-title=\"Email address of the person who should receive an electronic copy of Purchase Order\"></i></label>\n"+
															                                        "<div class=\"controls\">\n"+
															                                            "<input class=\"input-block-level purchasing-email\" type=\"email\" name=\"secondaryAddress-view"+i+"-purchasingEmail\" placeholder=\"person@email.com\" value=\""+secondaryEmail+"\">\n"+
															                                        "</div>\n"+
															                                    "</div>\n"+
															                                "</div>\n"+
															                                "<div class=\"span6\">\n"+
															                                    "<div class=\"control-group\">\n"+
															                                        "<label class=\"control-label\">Fax</label>\n"+
															                                        "<div class=\"controls\">\n"+
															                                            "<input class=\"input-block-level fax phone-number\" type=\"text\" name=\"secondaryAddress-view"+i+"-purchasingFax\" placeholder=\"(XXX) XXX-XXXX\" value=\""+secondaryFax+"\">\n"+
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
				                                                "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
				                                                for (int x = 0; x < arrayCountryCode.length; x++) {
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
																}
				                                   			 	response.write("</select>\n"+
				                                                    //"<option value=\"US\">United States</option>\n"+
				                                                    //"<option value=\"CA\">Canada</option>\n"+
				                                                    //"<option value=\"MX\">Mexico</option>\n"+
				                                                    //"<option value=\"AU\">Australia</option>\n"+
				                                                    //"<option value=\"NZ\">New Zealand</option>\n"+
				                                                    //"<option>Other</option>\n"+
				                                                //"</select>\n"+
				                                   			 	//Req#50 START Code added by AGAMPA 21 Feb 2015
				                                                "<a id=\"add-sec-address\" class=\"btn btn-primary add-item "+disableButton+ "\"><i class=\"icon-plus\"></i>Add Address</a>\n"+ //Req#50 END
				                                            "</div>\n");
			                                               }
			                                            // END
				                         response.write("</div>\n");
	                                                // Begin of Insert by Naga ENHC0013668
				                         				if(vendorId!=null&&vendorId.trim().length()>0){
		                                                response.write(	
		                                                "<hr>"+
			                                            "<div class=\"control-group pull-left\" style=\"display: block;\">\n");
			                                                 	                         			
		                         						response.write("<label class=\"control-label \">\n");
                                                     response.write(" Upload Support Doc\n"+
                                                     				"</label>\n"+
                                                     "<div class=\"controls\">\n");

			                                			if (FileNameSupport.length()>1){
				                                          		response.write("<input disabled type=\"file\" name=\"supportdoc\" fileType=\"SUP\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
//				                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
				                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDSUP+"&filename="+FileNameSupport+"\" target=\"_new\">"+FileNameSupport+"</a></span><i class=\"icon-remove remove-file "+hideButton+"\" fileId=\""+GUIDSUP+"\"></i>");
				                                          	} else {
				                                           		response.write("<input type=\"file\" name=\"supportdoc\" fileType=\"SUP\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");				                                                                    		
//				                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
				                                           }
			                                			
			                                            response.write("<input type=\"hidden\" name=\"supportdocInfo\" value=\""+GUIDSUP+"\">\n"+
	                                                    "</div>\n"+
		                                                "</div>\n"+
			                                            "<div class=\"clearfix\"></div>\n");
//	                                                    +
//		                                                "<hr>\n");
				                         				}	
	                                                response.write(
			                                         
	                                            // End of Insert by Naga				                        		 
				                                       "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n");
				                         				if (vendorType.contains("060") || vendorType.contains("040") || vendorType.contains("050")) {
				                         					response.write("<a id=\"fromBasic\" class=\"btn btn-success continue\" href=\"#tab4\">Continue <i class=\"icon-angle-right\"></i></a>\n");
				                         				// Begin of Insert by Naga ENHC0013683
				                         				// Hide Tax tab from Garnishment vendor ( 092 )
				                         				}else if(vendorType.contains("092")){	
				                         					response.write("<a id=\"fromBasic\" class=\"btn btn-success continue\" href=\"#tab3\">Continue <i class=\"icon-angle-right\"></i></a>\n");
				                         				// End of Insert by Naga	
				                         				} else {
				                         					response.write("<a id=\"fromBasic\" class=\"btn btn-success continue\" href=\"#tab2\">Continue <i class=\"icon-angle-right\"></i></a>\n");				                         					
				                         				}
				                         				//Req#51 START Code change by AGAMPA 
				                                        //response.write("<a class=\"btn btn-success actions-resubmit\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                         			response.write("</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            "<div class=\"tab-pane fade\" id=\"tab2\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
					                                    
				                         			
				                                  //Begin of Insert CTI w8 Foreign vendor -------------
					                                    //"<div class=\"control-group pull-left\">\n");
				                         				
					                                  "<div class=\"row-fluid\">\n"+
			                         					  "<div class=\"control-group pull-left span12\">\n");
					                         			  String requiredVendorQuestionsNotification = "";
		                                            	  if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020"))|| (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092"))) {
		                                            	    requiredVendorQuestionsNotification = "required-red";
		                                                  }
		                                            	  String vendorEntityValue = "";
		                                            	  if(ForeignVendorQuestonsArray[0]!=null){
		                                            		  vendorEntityValue = ForeignVendorQuestonsArray[0];
                                                          }
		                                                  String vendorEntityLocValue = "";
		                                                  if(ForeignVendorQuestonsArray[1]!=null){
		                                                	  vendorEntityLocValue = ForeignVendorQuestonsArray[1];
                                                          }
		                                                  String vendorIndvLocValue = "";
		                                                  if(ForeignVendorQuestonsArray[2]!=null){
		                                                   	vendorIndvLocValue = ForeignVendorQuestonsArray[2];
	                                                      }
		                                                  String vendorIndvResidenceValue = "";
		                                                  if(ForeignVendorQuestonsArray[3]!=null){
		                                                   	vendorIndvResidenceValue = ForeignVendorQuestonsArray[3];
	                                                      }
		                                                  String vendorIndvPresenceValue = "";
		                                                  if(ForeignVendorQuestonsArray[4]!=null){
		                                                   	vendorIndvPresenceValue = ForeignVendorQuestonsArray[4];
	                                                      }
		                                                  String defaultTcnty = "";
						                         		  if(tcntyCTI!=null){
						                         			defaultTcnty = tcntyCTI;
	                                                      }
						                         		  String w8JustOnBehalfVen = "";
		                                                  if(ForeignVendorQuestonsArray[5]!=null){
		                                                    w8JustOnBehalfVen = ForeignVendorQuestonsArray[5];
	                                                      }
		                                                  boolean showEntLoc =  false;
		                                                  boolean showIndivLoc =  false;
		                                                  boolean showPermRes =  false;
		                                                  boolean showSubPresence =  false;
                                                  	      // ENHC0013673  // Hide CTI related changes until CTI is implemented		                                                  
		                                            	  boolean showTaxResCnty =  false;		// ENHC0013673
		                                            	  boolean showW8Prep =  false; 			// ENHC0013673

		                                            	  boolean showW8Verify =  false;		// ENHC0013673
		                                            	  boolean showTaxCategory =  false;
		                                            	  boolean showW9Upload =  false;
		                                            	  if(vendorEntityValue.equals("0000000004"))
		                                            	  {
		                                            		  showEntLoc = true;
		                                            		  //entity is foreign
			                                                  if(vendorEntityLocValue.equals("0000000001")){
			                                                	  //is vendor user
				                                            	  if(requestType == "1"){
				                                            		  showTaxResCnty = true;	// ENHC0013673
					                                            	  if(isCTICalled){
					                                            		  showW8Prep =  true;	// ENHC0013673
					                                            	  }
			                                            		  }
				                                            	  //request on behalf
				                                            	  else{
				                                            		  showW8Verify = true;		// ENHC0013673
			                                            		  }
		                                            		  }
			                                                  //not foreign
		                                            		  else if(vendorEntityLocValue.equals("0000000002")){
		                                            			  showTaxCategory =  true;
		                                            			  showW9Upload =  true;
		                                            		  }	
		                                            	  }
		                                            	  else if(vendorEntityValue.equals("0000000003")){
		                                            		  showEntLoc = false;
		                                            		  showIndivLoc =  true;
		                                            		  //citizen
		                                            		  if(vendorIndvLocValue.equals("0000000001")){
		                                            			  showTaxCategory =  true;
		                                            			  showW9Upload =  true;
		                                            		  }
		                                            		  //not citizen
		                                            		  else if(vendorIndvLocValue.equals("0000000002")){
		                                            			  showPermRes =  true;
		                                            			  //permanent resident
		                                            			  if(vendorIndvResidenceValue.equals("0000000001")){
		                                            				  showTaxCategory =  true;
		                                            				  showW9Upload =  true;
		                                            			  }
		                                            			  //not permanent resident
		                                            			  else if(vendorIndvResidenceValue.equals("0000000002")){
		                                            				  showSubPresence =  true;
		                                            				  //substantial presence
		                                            				  if(vendorIndvPresenceValue.equals("0000000001")){
		                                            					  showTaxCategory =  true;
		                                            					  showW9Upload =  true;
		                                            				  }
		                                            				  //foreign
		                                            				  else if(vendorIndvPresenceValue.equals("0000000002")){
		                                            					  //is vendor user
						                                            	  if(requestType == "1"){
						                                            		  showTaxResCnty = true;	// ENHC0013673
						                                            		  if(isCTICalled){
							                                            		  showW8Prep =  true;	// ENHC0013673
							                                            	  }
					                                            		  }
						                                            	  //request on behalf
						                                            	  else{
						                                            		  showW8Verify = true;	// ENHC0013673
					                                            		  }
		                                            				  }
		                                            			  }
		                                            		  }
		                                            	  }
		                                            	  //Begin Entity type
		                                            	  response.write(
							                         				"<div id=\"ENTITYINDV\" class=\"accordion questions vendor\" style=\"display: block;\">\n"+
			                                                   			"<div class=\"accordion-group\" style=\"display: block;\">\n"+
			                                                    			"<div class=\"accordion-body collapse in\">\n");
			                                                    response.write("<div class=\"accordion-question "+requiredVendorQuestionsNotification+"\">\n");
			                                                    		response.write(				                                                                           
			                                                    				//"Are you an entity or an individual?\n"+
			                                                    				"<!-- "+vendorEntityValue+" -->\n"+
			                                                    				"Are you an entity or an individual?\n"+
			                                                    				"</div>\n"+
							                         						"</div>\n"+
				                                                    		"<div class=\"accordion-heading\">\n"+
				                                                        		"<div id=\"vendorEntityGroup\" class=\"btn-group\">\n" +
				                                                            		"<label>\n" +
				                                                            			"<input type=\"radio\" name=\"vendorEntity\" value=\"4\" "+(vendorEntityValue.equals("0000000004")?"checked":"")+" required/>\n" +
				                                                            			"Entity\n"+
				                                                            		"</label>\n" +
				                                                            		"<label>\n" +
				                                                            			"<input type=\"radio\" name=\"vendorEntity\" value=\"3\" "+(vendorEntityValue.equals("0000000003")?"checked":"")+"/>\n" +
				                                                            			"Individual\n"+
				                                                            		"</label>\n"+
				                                                            	"</div>\n"+
				                                                    		"</div>\n");
							                         			response.write(
							                         					"</div>\n"+
																	"</div>\n");
								                          //End Entity type

							                         	    //Begin Entity location
								                         		response.write(
								                         			"<div id=\"ENTITYLOC\" style=\"display:"+(showEntLoc?"block":"none")+";\">\n");
							                         		response.write(
							                         				"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
			                                                   			"<div class=\"accordion-group\" style=\"display: block;\">\n"+
			                                                    			"<div class=\"accordion-body collapse in\">\n");
			                                                    response.write("<div class=\"accordion-question "+requiredVendorQuestionsNotification+"\">\n");
			                                                    		response.write(				                                                                           
			                                                    				//"Is the entity organized,formed or incorporated outside of the US?\n"+
			                                                    				"<!-- "+vendorEntityLocValue+" -->\n"+
			                                                    				"Is the entity organized,formed or incorporated outside of the US?\n"+
			                                                    				"</div>\n"+
							                         						"</div>\n"+
				                                                    		"<div class=\"accordion-heading\">\n"+
				                                                        		"<div id=\"vendorEntityLocGroup\" class=\"btn-group\">\n" +
				                                                            		"<label>\n" +
				                                                            			"<input type=\"radio\" name=\"vendorEntityLoc\" value=\"1\" "+(vendorEntityLocValue.equals("0000000001")?"checked":"")+" required/>\n" +
				                                                            			"Yes, Entity is foreign\n"+
				                                                            		"</label>\n" +
				                                                            		"<label>\n" +
				                                                            			"<input type=\"radio\" name=\"vendorEntityLoc\" value=\"2\" "+(vendorEntityLocValue.equals("0000000002")?"checked":"")+"/>\n" +
				                                                            			"No, Entity is not foreign\n"+
				                                                            		"</label>\n"+
				                                                            	"</div>\n"+
				                                                    		"</div>\n");
							                         			response.write(
							                         					"</div>\n"+
																	"</div>\n");
								                         		response.write(
								                         			"</div>\n");//ENTITYLOC
							                         		//End Entity location

						                         			//Begin individual
								                         			response.write("<div id=\"INDIVLOC\" style=\"display:"+(showIndivLoc?"block":"none")+";\">\n");
						                         			response.write(
						                         				"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-body collapse in\">\n");
		                                                    response.write(
		                                                    		"<div class=\"accordion-question "+requiredVendorQuestionsNotification+"\">\n");
						                         			response.write(				                                                                           
		                                                    		//"Are you US Citizen?\n"+
						                         					"<!-- "+vendorIndvLocValue+" -->\n"+
						                         					"Are you US Citizen?\n"+
		                                                    		"</div>\n"+
		                                                    		"</div>\n"+
		                                                    		"<div class=\"accordion-heading\">\n"+
		                                                        		"<div id=\"vendorIndvLocGroup\" class=\"btn-group\">\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvLoc\" value=\"1\" "+(vendorIndvLocValue.equals("0000000001")?"checked":"")+" required/>\n" +
		                                                            			"Yes, you are a US person\n"+
		                                                            		"</label>\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvLoc\" value=\"2\" "+(vendorIndvLocValue.equals("0000000002")?"checked":"")+"/>\n" +
		                                                            			"No, not a US person\n"+
		                                                            		"</label>\n"+
		                                                            	"</div>\n"+
		                                                    		"</div>\n");
						                         			response.write(
						                         					"</div>\n"+
																"</div>\n");
								                         			response.write(
								                         			"</div>\n");//INDIVLOC
						                         			//End individual
						                         			
						                         			
						                         			//Begin permanent residence
								                         			response.write(
								                         			"<div id=\"PERMRES\" style=\"display:"+(showPermRes?"block":"none")+";\">\n");
						                         			response.write(
						                         				"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-body collapse in\">\n");
		                                                    response.write(
		                                                    		"<div class=\"accordion-question "+requiredVendorQuestionsNotification+"\">\n");
						                         			response.write(				                                                                           
		                                                    		//"Are you a permanent resident of the US?\n"+
						                         					"<!-- "+vendorIndvResidenceValue+" -->\n"+
						                         					"Are you a permanent resident of the US?\n"+
		                                                    		"</div>\n"+
		                                                    		"</div>\n"+
		                                                    		"<div class=\"accordion-heading\">\n"+
		                                                        		"<div id=\"vendorIndvResidenceGroup\" class=\"btn-group\">\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvResidence\" value=\"1\" "+(vendorIndvResidenceValue.equals("0000000001")?"checked":"")+" required/>\n" +
		                                                            			"Yes, you are a US person\n"+
		                                                            		"</label>\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvResidence\" value=\"2\" "+(vendorIndvResidenceValue.equals("0000000002")?"checked":"")+"/>\n" +
		                                                            			"No, not a US person\n"+
		                                                            		"</label>\n"+
		                                                            	"</div>\n"+
		                                                    		"</div>\n");
						                         			response.write(
						                         					"</div>\n"+
																"</div>\n");
								                         	response.write(
								                         			"</div>\n");//PERMRES
						                         			//End permanent residence
						                         			
						                         			
						                         			//Begin substantial presence
								                         			response.write(
								                         			"<div id=\"SUBPRESENCE\" style=\"display:"+(showSubPresence?"block":"none")+";\">\n");
						                         			response.write(
						                         				"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
		                                                    		"<div class=\"accordion-body collapse in\">\n");
		                                                    response.write(
		                                                    		"<div class=\"accordion-question "+requiredVendorQuestionsNotification+"\">\n");
						                         			response.write(				                                                                           
		                                                    		//"Do you have a substantial presence in the US(183 days+)?\n"+
						                         					"<!-- "+vendorIndvPresenceValue+" -->\n"+
						                         					"Do you have a substantial presence in the US(183 days+)?"+
						                         					"<i class=\"icon-question-sign tip\" data-placement=\"left\" title=\"\" data-original-title=\"You are considered to have a substantial presence in the US for the calendar year if present in the US at least 183 days during the 3-year period calculated as follows:  days present in the current year (must be at least 31 days in current year), 1/3 of the days in the first year before the current year and 1/6 of the days in the second year before the current year.  For example, if you were in the US for 122 days in each of the 3-year period, you would exactly meet this test:  (1 x 122=122 days; 1/3 x 122=41 days; 1/6 x 122=20 days; = 183 days).  For exceptions to the substantial presence test, please refer to IRS Publication 519.\"></i>\n"+
		                                                    		"</div>\n"+
		                                                    		"</div>\n"+
		                                                    		"<div class=\"accordion-heading\">\n"+
		                                                        		"<div id=\"vendorIndvPresenceGroup\" class=\"btn-group\">\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvPresence\" value=\"1\" "+(vendorIndvPresenceValue.equals("0000000001")?"checked":"")+" required/>\n" +
		                                                            			"Yes, you are a US person\n"+
		                                                            		"</label>\n" +
		                                                            		"<label>\n" +
		                                                            			"<input type=\"radio\" name=\"vendorIndvPresence\" value=\"2\" "+(vendorIndvPresenceValue.equals("0000000002")?"checked":"")+"/>\n" +
		                                                            			"No, not a US person\n"+
		                                                            		"</label>\n"+
		                                                            	"</div>\n"+
		                                                    		"</div>\n");
						                         			response.write(
						                         					"</div>\n"+
																"</div>\n");
								                         			response.write(
								                         			"</div>\n");//SUBPRESENCE
								                         	//End substantial presence
							                         	response.write(
											               	  "</div>\n"+//control-group
									                       	 "</div>\n");//row-fluid
							                         	
							                         			
							                         			
							                         			
							                         			//Begin select Tax Residence Country
							                         			response.write(
							                         			"<div id=\"TAXRES\" style=\"display:"+(showTaxResCnty?"block":"none")+";\">\n");
							                         			String excludeForeignVendorCountry = "US";
                        										response.write(
							                         			"<div class=\"row-fluid\">\n"+
							                         				"<div class=\"span6\">\n"+
				                                                    "<div class=\"control-group\">\n"+
				                                                        "<label class=\"control-label "+requiredVendorQuestionsNotification+"\">\n"+
				                                                            "Tax Residence Country\n"+
				                                                        "</label>\n"+
				                                                        "<div class=\"controls\">\n"+
				                                                        // Removed required=\"required\" Pranesh (04/17/2016)
				                                                            "<select id=\"vendorTaxResidenceCountry\" name=\"taxResidenceCountry\"  class=\"input-block-level\">\n"+
				                                                            "<!-- "+tcntyCTI+" -->\n"+  
				                                                            "<!-- "+defaultTcnty+" -->\n"+  
				                                                            "<option value=\"\">Select One</option>\n");
				    															for (int x = 0; x < arrayCountryCode.length; x++) {
					    																if (arrayCountryCode[x][0].equalsIgnoreCase(excludeForeignVendorCountry)){
					    																	continue;
					    																}
					    																if (arrayCountryCode[x][0].equalsIgnoreCase(defaultTcnty)){
																							response.write("<option value=\""+arrayCountryCode[x][0]+" \" selected>"+arrayCountryCode[x][1]+"</option>");
																						} else {
																							response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
																						}																				
																				}                                               
					      				                                        response.write("</select>\n"+
					                                                    "</div>\n"+
					                                                "</div>\n"+
					                                                "</div>\n"+
				                                                "</div>\n");
				      				                            response.write("</div>\n");//TAXRES
				      				                            //End select Tax Residence Country
			      				                                
				      				                            // Begin of Insert ENHC0013673	1228
//					      				                    	response.write(
//									                         			"<div id=\"W8PREP\" style=\"display:"+(showW8Prep?"block":"none")+";\">\n");
//					      				                        //Begin w8 prepare
//			      				                                response.write(
//			      				                                	"<div class=\"row-fluid\">\n"+
//			      				                            		"<div class=\"control-group pull-left span12\">\n");
//																response.write(
//																		"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
//																		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
//																			"<div class=\"accordion-body collapse in\">\n");
//																		response.write(
//																			"<div class=\"accordion-question\">\n");
//																		response.write(				                                                                           
//																				"<h1>Request to go to CTI to prepare applicable form W-8</h1>\n"+
//																			"</div>\n"+
//																			"</div>\n"+
//																			"<div class=\"accordion-heading\">\n"+
//																			"<label>\n"+
//																				"<p>\n"+
//									                         					"Foreign persons are generally subject to US withholding tax at the rate of "+
//									                         					"30% on their gross income they receive from US sources. You are required "+
//									                         					"to submit an electronic copy of signed W8 received from vendor in order to "+
//									                         					"establish Vendor foreign status and (a) make a valid claim for treaty "+
//									                         					"benefits to reduce or eliminate US withholding tax, (b) certify that "+
//									                         					"vendor income is effectively connected with the conduct of a trade or "+
//									                         					"business you have in the US to be exempt from US withholding tax, or "+
//									                         					"(c) certify that vendor is acting as an intermediary on behalf of the "+
//									                         					"beneficial owner of such income."+
//									                         					"</p>\n"+
//									                         					"<p>\n"+
//									                         					"Please follow the link below to complete your digital form W-8. You "+
//									                         					"will be asked a series of questions in order to create a digital form "+
//									                         					"W-8, you will need to come back to this page (VeRA: Vendor "+
//									                         					"Registration application Portal) to complete your registration."+
//									                         					"</p>\n"+
//									                         					"<p id=\"CTIURL\">"+
//									                         					"<a target=_blank href=\""+urlCTI+"\">"+urlCTI+"</a>\n"+
//									                         					"</p>\n"+
//									                         					"<p id=\"CTIREGCODE\">Client ID: "+
//									                         					ernamCTI+
//									                         					"    Registration Code: "+
//									                         					regCodeCTI+
//									                         					"</p>\n"+
//		                                                            		"</label>\n" +
//																			"</div>\n");
//																response.write(
//																		"</div>\n"+
//																		"</div>\n");
//							                         			
//							                         			
//																response.write(
//																	"</div>\n"+//control-group
//																	"</div>\n");//row-fluid
//																//End w8 prepare
//																response.write("</div>\n");//W8PREP
// 																End of Insert by Naga				      				                            
				      				                            

																response.write(
									                         			"<div id=\"W8VERIFY\" style=\"display:"+(showW8Verify?"block":"none")+";\">\n");
					      				                        //Begin w8 verification
			      				                                response.write(
			      				                                	"<div class=\"row-fluid\">\n"+
			      				                            		"<div class=\"control-group pull-left span12\">\n");
																response.write(
																		"<div class=\"accordion questions vendor\" style=\"display: block;\">\n"+
																		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																			"<div class=\"accordion-body collapse in\">\n");
																		response.write(
																			"<div class=\"accordion-question\">\n");
																		response.write(				                                                                           
																				"<h1>TAX verification is required for foreign vendors:</h1>\n"+
																				"</div>\n"+
																				"</div>\n"+
																				"<div class=\"accordion-heading\">\n"+
																				"<label>\n"+
																				"<p>\n"+
									                         					"Foreign persons are generally subject to US withholding tax at the rate of "+
									                         					"30% on their gross income they receive from US sources. You are required "+
									                         					"to submit an electronic copy of signed W8 received from vendor in order to "+
									                         					"establish Vendor foreign status and (a) make a valid claim for treaty "+
									                         					"benefits to reduce or eliminate US withholding tax, (b) certify that "+
									                         					"vendor income is effectively connected with the conduct of a trade or "+
									                         					"business you have in the US to be exempt from US withholding tax, or "+
									                         					"(c) certify that vendor is acting as an intermediary on behalf of the "+
									                         					"beneficial owner of such income."+
									                         					"</p>\n"+
									                         					"<p>\n"+
									                         					"W8 will be sent to TAX team for furthur verification in CTI application, "+
									                         					"Please make sure valid W8 form is uploaded. Invalid W8 will result request "+
									                         					"to reject by TAX team or cause delays in vendor setup."+
									                         					"</p>\n"+
		                                                            		"</label>\n" +
																			"</div>\n");
																response.write(
																		"</div>\n"+
																		"</div>\n");
																response.write(
																	"</div>\n"+//control-group
																	"</div>\n");//row-fluid
																//End w8 verification
																response.write(
																"<div class=\"row-fluid\">\n"+
																"<div class=\"span12\">\n"+
				                                                	"<div class=\"control-group\">\n"+
				                                                    	"<label class=\"control-label required-red\">Business Justification for requesting register on behalf vendor</label>\n"+
					                                                    "<div class=\"controls\">\n"+
					                                                    // Removed required=\"required\" Pranesh (04/17/2016)
					                                                    	"<textarea name=\"w8JustOnBehalfVen\" class=\"input-block-level justification-comment\" >"+w8JustOnBehalfVen+"</textarea>\n"+
//					                                                        "<input class=\"input-block-level special-char-validation\" required type=\"text\" name=\"w8JustOnBehalfVen\"  pattern=\"[a-zA-Z0-9.&'\\- ]+\" maxlength=\"500\" value=\""+w8JustOnBehalfVen+"\">\n"+
//					        				                                "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only &,- and '.</div>\n"+
					                                                    "</div>\n"+
				                                                    "</div>\n"+
																"</div>\n"+
																"</div>\n");
							                         			
																//Begin Upload w8
							                         			response.write(
						                         					"<div class=\"clearfix\"></div>\n"+
						                         					"<div class=\"control-group pull-left\">\n");
						                         			
							                         			if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R090")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V094"))) 				                                                 	                         			
					                         						response.write("<label class=\"control-label required-red\">\n");
					                         					else
					                         						response.write("<label class=\"control-label\">\n");
					                         					
			                                                        response.write(" Upload W8\n"+
						                                                    "</label>\n"+
					                                                 "<div class=\"controls\">\n");
					                                			if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R090")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V094"))) {
						                                          	if (FileNameW8.length()>1){
						                                          		response.write("<input disabled type=\"file\" name=\"taxw8\" fileType=\"W8\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
						                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
						                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDW8+"&filename="+FileNameW8+"\" target=\"_new\">"+FileNameW8+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDW8+"\"></i>");
					                                          	}
						                                          	 //code altered for tax tab issue ganesh27
//						                                           		else {
//						                                           		response.write("<input type=\"file\" name=\"taxw8\" fileType=\"W8\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");
//						                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
//						                                           }
						                                          	
						                                         
						                                          	else if(vendorIndvLocValue.equals("0000000001")) {
						                                          		response.write("<input type=\"file\" name=\"taxw8\" fileType=\"W8\"  data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
						                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
						                                           }
						                                          	else {			                                          		
						                                           		response.write("<input type=\"file\" name=\"taxw8\" fileType=\"W8\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");
						                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
						                                           }	
						                                         //end ganesh27 	
					                                			} else {
					                                				if (FileNameW8.length()>1){
						                                          		response.write("<input disabled type=\"file\" name=\"taxw8\" fileType=\"W8\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    					                                         
						                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDW8+"&filename="+FileNameW8+"\" target=\"_new\">"+FileNameW8+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDW8+"\"></i>");
						                                          	} else {
						                                           		response.write("<input type=\"file\" name=\"taxw8\" fileType=\"W8\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n");				                                                                    			                          
						                                           }
					                                				
					                                			}
					                                            response.write("<input type=\"hidden\" name=\"w8FileInfo\" value=\""+GUIDW8+"\">\n"+
			                                                    "</div>\n"+
				                                                "</div>\n"+
				                                                "<div class=\"clearfix\"></div>\n");
					                                            //End Upload w8
					                                            response.write("</div>\n");//W8VERIFY
					                       
		                                            response.write(
		                                            	"<div id=\"W9UPLOAD\" style=\"display:"+(showW9Upload?"block":"none")+";\">"+
		                                            	"<div>"+
					                                        "<div class=\"control-group pull-left\">\n");
							                        //End of Insert CTI w8 Foreign vendor ------------- 
		                                            
							                         			
				                         					// Naga ENHC0013685 Add Revenue Share Vendor type 091
				                         					// Naga ENHC0016458 Add Utility as required		
				                         					// Naga ENHC0016461 Add Legal Settlement and Contest Winner
		                                            		
		                                            	    // Added Production/Agreement( R018 ) - Pranesh(09-04-2016)-ENHC0016459
		                                            		
		                                            		// Added Production (R018)-Pranesh(04/16/2016)
				                         					if ((vendorId==null||!(vendorId.trim().length()>0))&&((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R090")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V094")) || (decisionVendorType.equalsIgnoreCase("R018"))|| (decisionVendorType.equalsIgnoreCase("R095"))   ))
				                         						response.write("<label class=\"control-label required-red\">\n");
				                         					else
				                         						response.write("<label class=\"control-label\">\n");
				                         					
				                         						if(vendorId!=null&&vendorId.trim().length()>0)
				                         							response.write(" Upload W9 ( Please upload new W9 form if there is change in TIN/SSN or legal name or primary Address )\n");
				                         						else
				                         							response.write(" Upload W9 \n");
		                                                        		
		                                                    response.write(    		
		                                                    "</label>\n"+
                                                     "<div class=\"controls\">\n");
		                                                    
		                                            // Begin of Insert by Naga ENHC0013668
		                                            // During the maintain all the forms to be optional
		                                            // Making W9 optional here.
                                                    if(vendorId!=null&&vendorId.trim().length()>0){
		                                				if (FileNameW9.length()>1){
			                                          		response.write("<input disabled type=\"file\" name=\"taxw9\" fileType=\"W9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    					                                         
			                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDW9+"&filename="+FileNameW9+"\" target=\"_new\">"+FileNameW9+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDW9+"\"></i>");
			                                          	} else {
			                                           		response.write("<input type=\"file\" name=\"taxw9\" fileType=\"W9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n");				                                                                    			                          
			                                           }                                                    	
                                                    }else
		                                            // End of Insert by Naga
		                                            // Naga ENHC0013685 Add Revenue Share Vendor type 091            
		                                            // Naga ENHC0016458 Add Utility as required
		                                            // Naga ENHC0016461 Add Legal Settlement and Contest Winner            
                                                    	// Added -R018,V018 - Pranesh(04/13/2016)
                                                    	// Added -R095,V095 - Pranesh(04/16/2016)
		                                			if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R090")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V094")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("V018")) || (decisionVendorType.equalsIgnoreCase("R095")) || (decisionVendorType.equalsIgnoreCase("V095"))    ) {
			                                          	if (FileNameW9.length()>1){
			                                          		response.write("<input disabled type=\"file\" name=\"taxw9\" fileType=\"W9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
			                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
			                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDW9+"&filename="+FileNameW9+"\" target=\"_new\">"+FileNameW9+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDW9+"\"></i>");
			                                          	} else {
			                                           		response.write("<input type=\"file\" name=\"taxw9\" fileType=\"W9\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");				                                                                    		
			                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
			                                           }
		                                			} else {
		                                				if (FileNameW9.length()>1){
			                                          		response.write("<input disabled type=\"file\" name=\"taxw9\" fileType=\"W9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    					                                         
			                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDW9+"&filename="+FileNameW9+"\" target=\"_new\">"+FileNameW9+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDW9+"\"></i>");
			                                          	} else {
			                                           		response.write("<input type=\"file\" name=\"taxw9\" fileType=\"W9\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n");				                                                                    			                          
			                                           }
		                                				
		                                			}
			                                            response.write("<input type=\"hidden\" name=\"w9FileInfo\" value=\""+GUIDW9+"\">\n"+
	                                                    "</div>\n"+
		                                                "</div>\n"+
		                                                "<a class=\"pull-right\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/W9.pdf\" target=\"_new\"><i class=\"icon-file\"></i>Download Blank W9</a>\n"+
		                                                "</div>"+
//		                                                "<div id=\"uploadneww9msg\">"+	// ENHC0013668
//		                                                "<div class=\"alert alert-success\" style=\"width: 85.5%; font-size: 10pt; display: block; word-wrap: break-word;\">Please upload new W9 Form</div>\n"+
//		                                                "</div>"+
		                                                "<div class=\"clearfix\"></div>\n");
		                                                // Begin of Insert by Naga ENHC0016461
		                                                if(decisionVendorType.equalsIgnoreCase("V093") || decisionVendorType.equalsIgnoreCase("R093")){
		                                                	
			                                                response.write(	
				                                            "<div class=\"control-group pull-left\">\n");
			                                          		 if(vendorId!=null&&vendorId.trim().length()>0){ 
              	                         						response.write("<label class=\"control-label \">\n");//ganesh DFCT0017545
			                                          		 }else{
	              	                         						response.write("<label class=\"control-label required-red \">\n");// ganesh DFCT0017545
	 
			                                          		 }
              	                         						response.write(" Upload Settlement Agreement\n"+"</label>\n"+
			                                          		 
	                                                        "<div class=\"controls\">\n");
	
				                                			if (FileNameLegal.length()>1){
					                                          		response.write("<input disabled type=\"file\" name=\"taxlegal\" fileType=\"LEG\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
					                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
					                                           		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDLEG+"&filename="+FileNameLegal+"\" target=\"_new\">"+FileNameLegal+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDLEG+"\"></i>");
					                                          	} else {
					                                          		
					                                          		 if(vendorId!=null&&vendorId.trim().length()>0){ 
							                                           		response.write("<input type=\"file\" name=\"taxlegal\" fileType=\"LEG\"  data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");			// added  DFCT0017545	ganesh                                                                 		
						                                                }
						                                                else{
					                                           		response.write("<input type=\"file\" name=\"taxlegal\" fileType=\"LEG\"  data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" required class=\"user-error \" />\n");	                                                              		
					                                                response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
						                                                	}	
					                                          		 }
				                                			
				                                            response.write("<input type=\"hidden\" name=\"legalFileInfo\" value=\""+GUIDLEG+"\">\n"+
		                                                    "</div>\n"+
			                                                "</div>\n"+
				                                            "<div class=\"clearfix\"></div>\n");
//		                                                    +
//			                                                "<hr>\n");
		                                                }
		                                                
		                                                	//Begin Of Insert by Pranesh(31-03-2016) - ENHC0016459
		                                                	// Posthumous Payments || Production/Contract/Agreement
		                                                	if(decisionVendorType.equalsIgnoreCase("R095") || decisionVendorType.equalsIgnoreCase("R018")){
		                                                	
			                                                response.write("<div class=\"control-group pull-left\">\n");
 				                                            
			                                                // Begin - Pranesh - (05/17/2016) - (Defect:15095)
			                                                if((vendorId!=null&&vendorId.trim().length()>0)){ 
			                                                	response.write("<label class=\"control-label\">\n"); 
				                                            }
			                                                else{
			                                                	response.write("<label class=\"control-label required-red\">\n");
				                                            }
			                                                // End - Pranesh - (05/17/2016) - (Defect:15095)
			                         						
			                         						//Begin Of Insert by Pranesh(01-04-2016) - ENHC0016459
			                         						if(decisionVendorType.equalsIgnoreCase("R095")){
			                         							
	                                                        response.write("Upload AFP/Support Document\n"+
	                                                        				"</label>\n"+
	                                                        "<div class=\"controls\">\n");
	                                                        
			                         						}else if(decisionVendorType.equalsIgnoreCase("R018")){
			                         							
			                         							response.write("Upload AFP/Contract/Agreement\n"+
			                         											"</label>\n"+
			                         						"<div class=\"controls\">\n");
			                         						//End Of Insert by Pranesh(01-04-2016) - ENHC0016459	
			                         						
			                         						}
				                                			if (FileNameLegal.length()>1){
				                                					// Begin - Pranesh - (05/17/2016) - (Defect:15095)
				                                                	if(vendorId!=null&&vendorId.trim().length()>0){ 
				                                                		response.write("<input type=\"file\" name=\"taxlegal\" fileType=\"LEG\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
				                                                	}
				                                                	else{
				                                                		response.write("<input disabled type=\"file\" name=\"taxlegal\" fileType=\"LEG\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
				                                                		response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
				                                                		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDLEG+"&filename="+FileNameLegal+"\" target=\"_new\">"+FileNameLegal+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUIDLEG+"\"></i>");
				                                                	}
					                                          	} else {
					                                                if(vendorId!=null&&vendorId.trim().length()>0){ 
					                                                	response.write("<input type=\"file\" name=\"taxlegal\" fileType=\"LEG\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"  />\n");// class=\"user-error\" removed DFCT0017545 ganesh
					                                                }
					                                                else{
					                                                	response.write("<input type=\"file\" name=\"taxlegal\" fileType=\"LEG\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");
					                                                	response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
					                                                }
					                                                // End - Pranesh - (05/17/2016) - (Defect:15095)
					                                           }
				                                			
				                                            response.write("<input type=\"hidden\" name=\"legalFileInfo\" value=\""+GUIDLEG+"\">\n"+
		                                                    "</div>\n"+
			                                                "</div>\n"+
				                                            "<div class=\"clearfix\"></div>\n");
//		                                                    +
//			                                                "<hr>\n");
		                                                }
                                                	// End Of Insert by Pranesh(31-03-2016) - ENHC0016459 
		                                                
		                                                
		                                                response.write(
		                                            // End of Insert by Naga
		                                                //Begin of Insert for CTI
		                                                "</div>\n"+
		                                                //End of Insert for CTI
		                                                //"<hr>\n"+ //before change - Pranesh - 31-03-2016 - ENHC0016459
		                                                "<hr>\n"); //after change - Pranesh - ENHC0016459
		                                                
		                                                //Begin Of Insert by Pranesh(31-03-2016) - ENHC0016459
		                                                if(decisionVendorType.equalsIgnoreCase("R095"))
		                                                response.write(
		                                                "<div class=\"california-alert\" style=\"display:none;\">\n");
		                                                else if(!(decisionVendorType.equalsIgnoreCase("R095")))
		                                                response.write(
		                                                "<div class=\"california-alert\" style=\"display:block;\">\n");
		                                                //End Of Insert by Pranesh(31-03-2016) - ENHC0016459
		                                                
		                                                response.write("<h3><i class=\"icon-bolt\"></i>Notice For Non California Resident Vendors</h3>\n"+
		                                                    "<div class=\"alert-message\">\n"+
		                                                        "<p>Vendors who provide independent services, win prizes as a contestant in a show, or rent real/personal property in California, must have a <strong>590 Form</strong> on file with NBCU or will be subject to <strong>7% CA Franchise tax withholding.</strong></p>\n"+
		                                                        "<div class=\"upload-forms\">\n"+
		                                                            "<div class=\"control-group pull-left\">\n"+
		                                                                "<label class=\"control-label\">\n"+
		                                                                    "Upload 590\n"+
		                                                                "</label>\n"+
		                                                                 "<div class=\"controls\">\n");				                                                                  
			                                                                    	if (FileName590.length()>1){	
			                                                                    		response.write("<input type=\"file\" disabled name=\"tax590\" fileType=\"590\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
			                                                                    		response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUID590+"&filename="+FileName590+"\" target=\"_new\">"+FileName590+"</a></span><i class=\"icon-remove remove-file "+hideButton+" \" fileId=\""+GUID590+"\"></i>");
			                                                                    	} else {
			                                                                     		response.write("<input type=\"file\" name=\"tax590\" fileType=\"590\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    						                                                                 		
			                                                                    	}
				                                                                response.write("<input type=\"hidden\" name=\"590FileInfo\" value=\""+GUID590+"\">\n"+
				                                                               "</div>\n"+
		                                                            "</div>\n"+
		                                                            "<a class=\"pull-right\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/CA_Form_590.PDF\" target=\"_new\"><i class=\"icon-file\"></i>Download Blank 590</a>\n"+
		                                                            "<div class=\"clearfix\"></div>\n"+
		                                                        "</div>\n"+
		                                                    "</div>\n"+
		                                                "</div>\n"+
		                                                
		                                                //Begin of Insert for CTI
		                                                "<div id=\"TAXCATEGORY\" style=\"display:"+(showTaxCategory?"block":"none")+";\">"+
		                                                //End of Insert for CTI
		                                                "<hr>\n"+
				                                        "<h1>Please select one of the following Tax Categories:</h1>\n"+
				                                        "<div class=\"row-fluid\">\n"+
				                                            "<div class=\"span12\">\n"+
				                                                "<div id=\"taxInfo\" class=\"btn-group vendor-accordion\" data-toggle=\"buttons-radio\">\n"+
			                                                        "<!-- taxID : "+taxID+":"+taxID.length()+" -->\n"+
		                                                        	"<div class=\"accordion-group\">\n");
				                                            		if (taxID.length()>1){
				                                                        response.write("<a class=\"btn btn-block btn-large btn-info "+disableButton+" active\" data-target=\"#taxid\" data-option=\"tax\" data-parent=\"#taxInfo\" id=\"tax-taxid\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>Tax ID</a>\n"+
				                                                        "<div id=\"taxid\" class=\"taxid-ssn in collapse\" style=\"height: auto;\">\n");
				                                                     } else {
				                                                        response.write("<a class=\"btn btn-block btn-large btn-info "+disableButton+" \" data-target=\"#taxid\" data-option=\"tax\" data-parent=\"#taxInfo\" id=\"tax-taxid\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>Tax ID</a>\n"+
				                                                        "<div id=\"taxid\" class=\"taxid-ssn collapse\">\n");				                                                    	 
				                                                     }
				                                                     response.write("<span></span>\n"+
			                                                        "<div class=\"hidden-form-elements taxSelector\">\n");
	                                                                    // Naga ENHC0013685 Add Revenue Share Vendor type 091
				                                                        // Naga ENHC0013683 Add Garnishment Vendor type 092
				                                                       // Added -R018,V018 - Pranesh(04/13/2016)
			                                                        	if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("V018")) || (decisionVendorType.equalsIgnoreCase("R090"))  ) {				                                                        		
			                                                        		response.write("<input type=\"radio\" name=\"taxSsn\" required class=\"tax\" value=\"tax\"/>\n"+
			                                                        		"<input type=\"radio\" name=\"taxSsn\" required class=\"ssn\" value=\"ssn\" />\n");
			                                                        	} else {
			                            				                    response.write("<input type=\"radio\" name=\"taxSsn\" class=\"tax\" value=\"tax\"/>\n"+
			                                                        		"<input type=\"radio\" name=\"taxSsn\" class=\"ssn\" value=\"ssn\" />\n");                            		
			                                                        	}
			                                                    
			                                                        	response.write("</div>\n"+					                                                            		
				                                                            "<div class=\"control-group pull-left\" style=\"width:97%\">\n");
        			                                                        	// Naga ENHC0013685 Add Revenue Share Vendor type 091
			                                                        	        // Naga ENHC0013683 Add Garnishment Vendor type 092
			                                                        	        // Added -R018,V018 - Pranesh(04/13/2016)
			                                                        			// Added -R095,V095 - Pranesh(04/16/2016)
			                                                        	
			                                                                    if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("V018")) || (decisionVendorType.equalsIgnoreCase("V095")) || (decisionVendorType.equalsIgnoreCase("R095")) || (decisionVendorType.equalsIgnoreCase("R090"))  ) 				                                                 	
			                                                                    	response.write("<label class=\"control-label required-red\">\n");
			                                                                    else 
			                                                                    	response.write("<label class=\"control-label\">\n");	
				                                                                
			                                                                    response.write(" Enter Tax-ID Number\n"+
				                                                                "</label>\n"+
				                                                                "<div class=\"controls\">\n");
			                                                                    // Naga ENHC0013685 Add Revenue Share Vendor type 091
			                                                                    // Naga ENHC0013683 Add Garnishment Vendor type 092 Removed
			                                                                    // Naga ENHC0016461 TIN / SSN is required for Legal Settlement and Contest Winner
			                                                                    // Naga ENHC0016458 TIN / SSN is required for Utility
			                                                    				response.write("<input type=\"hidden\" id=\"temp-tax-id\" name=\"taxId1\" value=\""+ temptaxID + "\" >\n"); // ganesh		
			                                                    				
			                                                    				// Added -R018 -Pranesh (04/15/2016) - ENHC0016459
				                                                                if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R090")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V094")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R095")) || (decisionVendorType.equalsIgnoreCase("V095")) || (decisionVendorType.equalsIgnoreCase("R090"))  )
				                                                                	{
				                                                                	// Added user-erro -Pranesh (04/15/2016) - ENHC0016459
				        				                                                 response.write("<input type=\"text\" id=\"tax-taxid-id\" class=\"tax-taxid user-error\" pattern=\"\\w{2}-\\w{7}\" size=\"10\" maxlength=\"10\" name=\"taxId1\" required value=\""+taxID+"\" placeholder=\"XX-XXXXXXX\">\n"); // ENHC0016170
//				        				                                                 response.write("<input type=\"text\" id=\"tax-taxid-id\" class=\"tax-taxid\" pattern=\"[0-9X]+\" size=\"11\" maxlength=\"9\" name=\"taxId1\" required value=\""+taxID+"\" placeholder=\"XXXXXXXXX\">\n"); // ENHC0016170

				                                                                	} else {
				                                                                         response.write("<input type=\"text\" id=\"tax-taxid-id\" class=\"tax-taxid\" pattern=\"\\w{2}-\\w{7}\" size=\"10\" maxlength=\"10\" name=\"taxId1\" value=\""+taxID+"\" placeholder=\"XX-XXXXXXX\">\n"); // ENHC0016170
//				                                                                         response.write("<input type=\"text\" id=\"tax-taxid-id\" class=\"tax-taxid\" pattern=\"[0-9X]+\" size=\"11\" maxlength=\"9\" name=\"taxId1\" value=\""+taxID+"\" placeholder=\"XXXXXXXXX\">\n"); // ENHC0016170
				                                                                	}	
				                                                                	response.write("<div></div>");			// Naga 999 - 2
				                                                                
				                                                                //Bug # 49 changes start here
				                                                                response.write("<div class=\"accordion questions contractor\">\n"+
				                                                                		"<div class=\"accordion-group\" style=\"display: block;\">\n"+
				                                                                		"<!--Pranesh(05/25/2016) "+requestType+" -->\n"+
				                                                                		"<div class=\"accordion-body collapse in\">\n");
				                                                                		String requiredLegalQuestionsNotification = "";
				                                                                		String solePropQRequired = "";
				                                                                		// Naga ENHC0013685 Add Revenue Share Vendor type 091
				                                                                		// Naga ENHC0013683 Add Garnishment Vendor type 092
				                                                                		// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
				                                                                		// Added R018 (04/13/2016)
				                                                                		if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090"))     )) {
				                                                                		
				                                                                		// Blocked - Pranesh - (05/23/2016)
				                                                                		//if ((!(vendorId!=null&&vendorId.trim().length()>0)) || ((vendorId!=null&&vendorId.trim().length()>0)) && ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020"))|| (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092")) || (decisionVendorType.equalsIgnoreCase("R018")) )) {
				                                                                		
				                                                                		// Added - Pranesh - (05/23/2016) - DFCT0017158
				                                                                			// IC question's not mandatory in invite request ( Invite - requestype=1,Robo || other - requestype=2 )  
				                                                                		//	if(!(requestType.equals("1"))){
//				                                                                				requiredLegalQuestionsNotification = "required-red";
//				                                                                				solePropQRequired = " required ";
//				                                                                			}else 
				                                                                				if(( vendorType.equals("010") || vendorType.equals("020") || vendorType.equals("090")|| vendorType.equals("018") )){ // Added - Pranesh (05/26/2016) - DFCT0017158
				                                                                				requiredLegalQuestionsNotification = "required-red";
				                                                                				solePropQRequired = "required";
				                                                                			}else{ // Added - Pranesh (05/26/2016) - DFCT0017158
				                                                                				requiredLegalQuestionsNotification = "";
				                                                                				solePropQRequired = "";
				                                                                			}
				                                                                		// Added - Pranesh - (05/23/2016) - DFCT0017158	 
				                                                                		
				                                                                		}
				                                                                		String solePropValue = "";
				                                                                		if (taxID.length()>1 && TaxCheckBoxeArray[0] == null)
				                                                                		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
				                                                                		else
				                                                                		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
				                                                                		
				                                                                		if(TaxCheckBoxeArray[0]!=null){
				                                                                			solePropValue = TaxCheckBoxeArray[0];
				                                                                		}
				                                                                		response.write(				                                                                           
				                                                                		//"Are you a Sole Proprietor?\n"+ // Naga ENHC0016123
				                                                                		"<!-- "+solePropValue+" -->\n"+
				                                                                		"Please select one of the following : \n"+ // Naga ENHC0016123
				                                                                		"</div>\n"+
				                                                                		"</div>\n"+
				                                                                		// Begin of Insert and Comment by Naga ENHC0016123
				                                                                		"<div class=\"accordion-heading\">\n"+
					                                                                		"<div id=\"solePropGroup\" class=\"btn-group\">\n" +
						                                                                		"<label>\n" +
						                                                                			"<input type=\"radio\" name=\"independantContractor\" value=\"1\" "+(solePropValue.equals("0000000001")?"checked":"")+"/>\n" +// solePropQRequired+ - Removed - Ganesh - (05/26/2016)-  DFCT0017158
						                                                                			"Individual/sole proprietor or single-member LLC\n"+
						                                                                		"</label>\n" +
						                                                                		"<label>\n" +
						                                                                			"<input type=\"radio\" name=\"independantContractor\" value=\"2\" "+(solePropValue.equals("0000000002")?"checked":"")+"/>\n" +
						                                                                			"Partnerships\n"+
						                                                                		"</label>\n"+
						                                                                		"<label>\n" +
						                                                                			"<input type=\"radio\" name=\"independantContractor\" value=\"3\" "+(solePropValue.equals("0000000003")?"checked":"")+"/>\n" +
						                                                                			"LLCs taxed as partnerships\n"+
						                                                                		"</label>\n"+
						                                                                		"<label>\n" +
						                                                                			"<input type=\"radio\" name=\"independantContractor\" value=\"4\" "+(solePropValue.equals("0000000004")?"checked":"")+"/>\n" +
						                                                                			"None of the above\n"+
						                                                                		"</label>\n"+
						                                                                	"</div>\n"+
				                                                                		"</div>\n");
				                                                                		/* 
				                                                                		"<div class=\"accordion-heading\">\n"+
				                                                                		"<!-- "+TaxCheckBoxeArray[0]+" -->\n"+
				                                                                		"<div id=\"solePropGroup\" class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
				                                                                		
				                                                                		String yesCheckedOrRequired = "";
				                                                                		String noCheckedOrRequired = "";
				                                                                		
				                                                                		if (TaxCheckBoxeArray[0] != null)
				                                                                		{
					                                                                		if (TaxCheckBoxeArray[0].equalsIgnoreCase("0000000001") )
					                                                                		{
					                                                                			response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
					                                                                			response.write("<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n");
					                                                                			yesCheckedOrRequired = "checked";
					                                                                		} 
					                                                                		else 
					                                                                		{
					                                                                			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
					                                                                			 response.write("<a class=\"btn no-answer active\"><i class=\"icon-ok-sign\"></i>No</a>\n");
					                                                                			 yesCheckedOrRequired = "checked";
					                                                                		}				                                                                            	
				                                                                		} 
				                                                                		else 
				                                                                		{
				                                                                			response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
				                                                                			response.write("<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n");   	
				                                                                			if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020"))) {				                                                        		
				                                                                				yesCheckedOrRequired 	= "required";
				                                                                				noCheckedOrRequired 	= "required";
				                                                                			}
				                                                                		}
				                                                                		
				                                                                		response.write("<div class=\"hidden-form-elements\">\n"+
				                                                                		"<input type=\"radio\" name=\"independantContractor\" value=\"1\" "+yesCheckedOrRequired+"/>\n");
				                                                                		*/ // End of Comment and Insert by Naga 
				                                                                		/*if (taxID.length()>1 && TaxCheckBoxeArray[0] == null){
				                                                                			response.write(" required ");
				                                                                		}
				                                                                		response.write("/>\n"+*/
				                                                                		
				                                                                		//response.write("<input type=\"radio\" name=\"independantContractor\" value=\"2\" "+noCheckedOrRequired+"/>\n"); // Naga ENHC0016123
				                                                                		
				                                                                		/*if (taxID.length()>1 && TaxCheckBoxeArray[0] == null){
				                                                                			response.write(" required ");
				                                                                		}
				                                                                		response.write("/>\n");*/
				                                                                			
				                                                                //response.write("</div>\n"); // Naga ENHC0016123
				                                                                //response.write("</div>\n"); // Naga ENHC0016123
				                                                                
																			response.write("</div>"+
																					//"</div>"+
																				"</div>\n");

				                                                                //Changes ends here
				                                                                
				                                                              response.write("</div>\n"+
			                                                            	"</div>\n"+
			                                                        	"</div>\n"+
				                                                    "</div>\n"+
				                                                    
				                                                    "<!-- sociaslSecurityNumber "+socialSecurityNumber+" : "+socialSecurityNumber.length()+" -->\n"+
				                                                    
				                                                    "<div class=\"accordion-group\">\n");
				                                                    if (socialSecurityNumber.length()>1){
				                                                        response.write("<a class=\"btn btn-block btn-large btn-info "+disableButton+" active\" data-target=\"#social\" data-option=\"ssn\" data-parent=\"#taxInfo\" id=\"tax-ssn\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>Social Security Number</a>\n"+
				                                                        "<div class=\"hidden-form-elements taxSelector\">\n");
				                                                        // Naga ENHC0013685 Add Revenue Share Vendor type 091
				                                                        // Naga ENHC0013685 Add Garnishment Vendor type 092
			                                                        	if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092"))) {				                                                        		
			                                                        		response.write("<input type=\"radio\" name=\"taxSsn\" required class=\"tax\" value=\"tax\" checked/>\n"+
			                                                        		"<input type=\"radio\" name=\"taxSsn\" required class=\"ssn\" value=\"ssn\" />\n");
			                                                        	} else {
			                            				                    response.write("<input type=\"radio\" name=\"taxSsn\" class=\"tax\" value=\"tax\" checked/>\n"+
			                                                        		"<input type=\"radio\" name=\"taxSsn\" class=\"ssn\" value=\"ssn\" />\n");                            		
			                                                        	}
				                                                    
				                                                        response.write("</div>\n"+
				                                                        "<div id=\"social\" class=\"taxid-ssn in collapse\" style=\"height: auto;\">\n");				                                                    	
				                                                    } else {
				                                                        response.write("<a class=\"btn btn-block btn-large btn-info "+disableButton+"\" data-target=\"#social\" data-option=\"ssn\" data-parent=\"#taxInfo\" id=\"tax-ssn\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>Social Security Number</a>\n"+
				                                                        "<div id=\"social\" class=\"taxid-ssn collapse\">\n"+				                                                    	
				                                                        "<div class=\"hidden-form-elements taxSelector\">\n");
				                                                            // Naga ENHC0013685 Add Revenue Share Vendor type 091
				                                                        	// Naga ENHC0013683 Add Garnishment Vendor type 092
				                                                        	if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092"))) {				                                                        		
				                                                        		response.write("<input type=\"radio\" name=\"taxSsn\" required class=\"tax\" value=\"tax\" checked/>\n"+
				                                                        		"<input type=\"radio\" name=\"taxSsn\" required class=\"ssn\" value=\"ssn\" />\n");
				                                                        	} else {
				                            				                    response.write("<input type=\"radio\" name=\"taxSsn\" class=\"tax\" value=\"tax\" checked/>\n"+
				                                                        		"<input type=\"radio\" name=\"taxSsn\" class=\"ssn\" value=\"ssn\" />\n");                            		
				                                                        	}
					                                                    
					                                                        response.write("</div>\n");
				                                                    }
				                                                                           
				                                                            response.write("<span></span>\n"+
				                                                            "<div class=\"control-group pull-left\">\n");
                                 			                                    // Naga ENHC0013685 Add Revenue Share Vendor type 091
				                                                            	// Naga ENHC0013683 Add Garnishment Vendor type 092
				                                                        
				                                                            String leqDisable="display:none;";// DFCT0017529 ganesh
				                                                            String leqDisabled="";
				                                                            if(disableButton.equalsIgnoreCase("disableButton"))
				                                                            {
				                                                            	 leqDisable="cursor: not-allowed;";
				                                                            	 leqDisabled="leqDisabled";
				                                                            }
				                                                           
				                                                           // end DFCT0017529 
				                                                            
				                                                            	// Added -R095,V095 - Pranesh(04/16/2016)
				                                                                if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030") || (decisionVendorType.equalsIgnoreCase("R090"))  || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V092")) || (decisionVendorType.equalsIgnoreCase("R092")) || (decisionVendorType.equalsIgnoreCase("R095")) || (decisionVendorType.equalsIgnoreCase("V095"))     ))  				                                                            
				                                                                	response.write("<label class=\"control-label required-red\">\n");
				                                                                else 
				                                                                	response.write("<label class=\"control-label\">\n");
				                                                                response.write("Enter Social Security Number\n"+
				                                                                "</label>\n"+
				                                                                "<div class=\"controls\">\n");
				                                                                // Naga ENHC0013685 Add Revenue Share Vendor type 091
			                                                                    // Naga ENHC0016461 TIN / SSN is required for Legal Settlement and Contest Winner
			                                                                    // Naga ENHC0016458 TIN / SSN is required for Utility
				                                                				response.write("<input type=\"hidden\" id=\"temp-social-id\" name=\"ssn1\" value=\""+ tempsocialSecurityNumber + "\" >\n"); // ganesh
				                                                				
				                                                				// Added -R018(04/15/2016) - Pranesh - ENHC0016459
				                                                                if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("V030")) ||  (decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) || (decisionVendorType.equalsIgnoreCase("V093")) || (decisionVendorType.equalsIgnoreCase("R093")) || (decisionVendorType.equalsIgnoreCase("V094")) || (decisionVendorType.equalsIgnoreCase("R094")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("R018"))  || (decisionVendorType.equalsIgnoreCase("R090")) )  
				                                                                {
				                                                                	// Added user-error - Pranesh(04/15/2016) - ENHC0016459
				                                                                    response.write("<input type=\"text\" id=\"tax-social-id\" class=\"tax-social user-error\" pattern=\"\\w{3}-\\w{2}-\\d{4}\" name=\"ssn1\" size=\"11\" maxlength=\"11\" placeholder=\"XXX-XX-XXXX\" required value=\""+socialSecurityNumber+"\">\n"); // ENHC0016170				                                                            	
//				                                                                    response.write("<input type=\"text\" id=\"tax-social-id\" class=\"tax-social\" pattern=\"[0-9X]+\" name=\"ssn1\" size=\"11\" maxlength=\"9\" placeholder=\"XXXXXXXXX\" required value=\""+socialSecurityNumber+"\">\n"); // ENHC0016170				                                                                    
				                                                                } else {
				                                                                    response.write("<input type=\"text\" id=\"tax-social-id\"class=\"tax-social\" pattern=\"\\w{3}-\\w{2}-\\d{4}\" name=\"ssn1\" size=\"11\" maxlength=\"11\" placeholder=\"XXX-XX-XXXX\" value=\""+socialSecurityNumber+"\">\n"); // ENHC0016170				                                                            					                                                                	
//				                                                                    response.write("<input type=\"text\" id=\"tax-social-id\"class=\"tax-social\" pattern=\"[0-9X]+\" name=\"ssn1\" size=\"11\" maxlength=\"9\" placeholder=\"XXXXXXXXX\" value=\""+socialSecurityNumber+"\">\n"); // ENHC0016170				                                                                    
				                                                                }
				                                                                response.write("<div></div>");			// Naga 999 - 2
						                                                    response.write("</div>\n"+
				                                                            "</div>\n"+
				                                                        "</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                        
				                                        "<!-- sociaslSecurityNumber "+socialSecurityNumber+" : "+socialSecurityNumber.length()+" -->\n"+
						                            "<div id=\"LEQ\"  style=\""+leqDisable+"\" >");// DFCT0017529 ganesh
						                            //Begin Of Insert by Pranesh(31-03-2016) - ENHC0016459
						                            
						                            //Before change - Pranesh(31-03-2016) - ENHC0016459   
						                            //response.write("<div class=\"accordion questions contractor\">\n"+
						                            
						                            if(decisionVendorType.equalsIgnoreCase("R095"))                
						                            //After change - Pranesh(31-03-2016) - ENHC0016459                       
						                    		response.write("<div class=\"accordion questions contractor\" style=\"display:none;\">\n");
						                            
						                            else if(!(decisionVendorType.equalsIgnoreCase("R095")))
		                                            response.write("<div class=\"accordion questions contractor\" style=\"display:block;\">\n");
						                    		
						                    		//Before change - Pranesh(31-03-2016) - ENHC0016459
						                    		//"<div class=\"accordion-group\" style=\"display: block;\">\n"+
						                            
						                            //End Of Insert by Pranesh(31-03-2016) - ENHC0016459
						                    		response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
						                    				"<!--Pranesh(05/23/2016) "+decisionVendorType+" -->\n"+
						                    				"<!--Pranesh(05/23/2016) "+requiredLegalQuestionsNotification+" -->\n"+
																"<div class=\"accordion-body collapse in\">\n");
																		if (TaxCheckBoxeArray[21] == null)
																			response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
																		else
																			response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																		response.write(
																		"Does NBCU tell you where, when and how to do work?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																		String yesCheckOrReq = "",noCheckOrReq = "";
																		
																		if (TaxCheckBoxeArray[21] != null){	
																			if (TaxCheckBoxeArray[21].equalsIgnoreCase("0000000001")){
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");	
																				yesCheckOrReq="checked";
																			} else {
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n"); 
																				 noCheckOrReq="checked";
																			}				                                                                            	
																		} else {
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");
																			 // ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			 
																			 // Added R018 - Pranesh(04/15/2016) - ENHC0016459 !(vendorId!=null&&vendorId.trim().length()>0)&&
																			 
																			 if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020"))||(decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090")) )) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																		}
																			response.write("<div class=\"hidden-form-elements\">\n"+
																			/*"<!--Pranesh(05/23/2016) "+decisionVendorType+":-:"+yesCheckOrReq+":-:"+noCheckOrReq+" -->\n"+*/
																			"<input type=\"radio\" name=\"taxSsnQ30\" value=\"1\"  "+yesCheckOrReq+" />\n"+
																			"<input type=\"radio\" name=\"taxSsnQ30\" value=\"2\"  "+noCheckOrReq+" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+                                          
															"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																"<div class=\"accordion-body collapse in\">\n");
																		if (TaxCheckBoxeArray[22] == null)
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																	else
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
																	response.write(
																		"Will you perform your work substantially on NBCU premises?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																	
																	yesCheckOrReq="";
																	noCheckOrReq ="";
																	
																		if (TaxCheckBoxeArray[22] != null){
																			if (TaxCheckBoxeArray[22].equalsIgnoreCase("0000000001")){
																				yesCheckOrReq="checked";
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");			                     				                                        	
																			} else {
																				noCheckOrReq="checked";
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																			}				                                                                            	
																		} else {
																			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			
																			// Added -R018 Pranesh(04/15/2016) - ENHC0016459
																			 if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) ||(decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020"))  || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090")) )) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																		}
																		response.write("<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"taxSsnQ31\" value=\"1\" "+yesCheckOrReq+"/>\n"+
																			"<input type=\"radio\" name=\"taxSsnQ31\" value=\"2\" "+noCheckOrReq+"/>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+  
															"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																"<div class=\"accordion-body collapse in\">\n");
																if (TaxCheckBoxeArray[23] == null)
																	response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																else
																	response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																response.write("If you know, are your duties or services identical to those performed by existing NBCU staff employees?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																	yesCheckOrReq="";
																	noCheckOrReq ="";
																		
																		if (TaxCheckBoxeArray[23] != null){
																			if (TaxCheckBoxeArray[23].equalsIgnoreCase("0000000001")){
																				yesCheckOrReq="checked";
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");			                     				                                        	
																			} else {
																				 noCheckOrReq="checked";
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																			}				                                                                            	
																		} else {
																			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			
																			// Added -R018 Pranesh(04/15/2016) - ENHC0016459
																			 if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) ||(decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020"))  || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090")) )) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																		}
																				response.write("<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"taxSsnQ32\" value=\"1\" "+yesCheckOrReq+"/>\n"+
																			"<input type=\"radio\" name=\"taxSsnQ32\" value=\"2\" "+noCheckOrReq+"/>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+  
															"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																"<div class=\"accordion-body collapse in\">\n");
																if (TaxCheckBoxeArray[24] == null)
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																	else
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																	response.write(
																		"Will you be paid by the hour or week (vs. flat fee for job/project)?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																	
																	yesCheckOrReq="";
																	noCheckOrReq ="";
																	
																		if (TaxCheckBoxeArray[24] != null){
																			if (TaxCheckBoxeArray[24].equalsIgnoreCase("0000000001")){
																				yesCheckOrReq="checked";
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");			                     				                                        	
																			} else {
																				noCheckOrReq="checked";
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																			}				                                                                            	
																		} else {
																			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			
																			// Added -R018 Pranesh(04/15/2016) - ENHC0016459
																			if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) ||(decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020"))  || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090")))) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																		}
																		response.write("<div class=\"hidden-form-elements\">\n"+
																		"<input type=\"radio\" name=\"taxSsnQ33\" value=\"1\" "+yesCheckOrReq+"/>\n"+
																		"<input type=\"radio\" name=\"taxSsnQ33\" value=\"2\" "+noCheckOrReq+"/>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+  
															"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																"<div class=\"accordion-body collapse in\">\n");
																if (TaxCheckBoxeArray[25] == null)
																	response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																else
																	response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
																response.write(
																		"Does NBCU furnish you with tools, equipment, materials, training and/or administrative support to perform your work?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																
																		yesCheckOrReq="";
																		noCheckOrReq ="";
																
																		if (TaxCheckBoxeArray[25] != null){
																			if (TaxCheckBoxeArray[25].equalsIgnoreCase("0000000001")){
																				yesCheckOrReq="checked";
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");			                     				                                        	
																			} else {
																				 noCheckOrReq="checked";
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																			}				                                                                            	
																		} else {
																			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			
																			// Added -R018 Pranesh (04/15/2016) - ENHC0016459
																			if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) ||(decisionVendorType.equalsIgnoreCase("R010")) || (decisionVendorType.equalsIgnoreCase("R020"))  || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R090")) )) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																		}
																		response.write("<div class=\"hidden-form-elements\">\n"+
																		"<input type=\"radio\" name=\"taxSsnQ34\" value=\"1\" "+yesCheckOrReq+"/>\n"+
																		"<input type=\"radio\" name=\"taxSsnQ34\" value=\"2\" "+noCheckOrReq+"/>\n"+		
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+	
															"<div class=\"accordion-group\" style=\"display: block;\">\n"+
																"<div class=\"accordion-body collapse in\">\n");
																	if (TaxCheckBoxeArray[26] == null)
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+" \">\n");
																	else
																		response.write("<div class=\"accordion-question "+requiredLegalQuestionsNotification+"\">\n");
																	response.write(
																		"Do you render services exclusively to NBCU?\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading "+leqDisabled+"\">\n"+//DFCT0017529 ganesh 
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n");
																		
																		yesCheckOrReq="";
																		noCheckOrReq ="";
																	
																		if (TaxCheckBoxeArray[26] != null){
																			if (TaxCheckBoxeArray[26].equalsIgnoreCase("0000000001")){
																				yesCheckOrReq="checked";
																				response.write("<a class=\"btn yes-answer active\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				response.write("<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n");			                     				                                        	
																			} else {
																				 noCheckOrReq="checked";
																				 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																				 response.write("<a class=\"btn active\"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																			}				                                                                            	
																		} else {
																			// ENHC0013668  IC Questionnaire is optional when in maintaining existing vendor
																			
																			// Added -R018 Pranesh(04/15/2016) - ENHC0016459
																			if (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V020")) ||(decisionVendorType.equalsIgnoreCase("R010"))  || (decisionVendorType.equalsIgnoreCase("R020"))  || (decisionVendorType.equalsIgnoreCase("R018")) ||(decisionVendorType.equalsIgnoreCase("R090"))  )) {				                                                        		
																				 yesCheckOrReq = "required";
																				 noCheckOrReq  = "required";
																			 }
																			 response.write("<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n");
																			 response.write("<a class=\"btn \"><i class=\"icon-ok-sign\"></i>No</a>\n");   				                                        	
																		}
																		response.write("<div class=\"hidden-form-elements\">\n"+
																				"<input type=\"radio\" name=\"taxSsnQ35\" value=\"1\"/ "+yesCheckOrReq+"/>\n"+
																				"<input type=\"radio\" name=\"taxSsnQ35\" value=\"2\" "+noCheckOrReq+"/>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n"+
														"</div>\n"+
														"</div>\n"+

					                                    "<hr>\n"+
					                                    "<div class=\"row-fluid\">\n"+
		                                                    "<div class=\"control-group span12\">\n"+
		                                                        "<label class=\"control-label required-red\">\n"+
		                                                            "Recipient Type\n"+
		                                                        "</label>\n"+
		                                                        "<div class=\"controls\">\n"+
		                                                            "<select id=\"recipientType\" name=\"taxRecipientType\" required=\"required\" class=\"input-block-level\">\n"+
		                                                            "<!-- "+taxRecipientType+"-->\n"+  
		                                                            "<option value=\"\">Select One</option>\n");
		    															for (int x = 0; x < arrayTaxRecipientType.length; x++) {
																				if (arrayTaxRecipientType[x][0].equalsIgnoreCase(taxRecipientType)){
																					response.write("<option value=\""+arrayTaxRecipientType[x][0]+" \" selected>"+arrayTaxRecipientType[x][1]+"</option>");
																				} else {
																					response.write("<option value=\""+arrayTaxRecipientType[x][0]+"\">"+arrayTaxRecipientType[x][1]+"</option>");
																				}																				
																		}                                               
		      				                                        response.write("</select>\n"+
		                                                        "</div>\n"+
		                                                    "</div>\n"+
		                                                "</div>\n"+
													//Begin of Insert for CTI
		                                            "</div>\n");
		                                            //End of Insert for CTI
		                                                //Req#603 START Code change by AGAMPA 23Feb2015
										response.write("<div id=\"SEP\" style=\"display:none;\">");
										if ((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("V080")) || (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R010")) ||(decisionVendorType.equalsIgnoreCase("R080")) || (decisionVendorType.equalsIgnoreCase("R090"))){
											response.write("<h1>Supplier Environmental Practices</h1>\n"+
														"<div class=\"accordion questions\" id=\"environmentalQuestions\">\n"+
														"<!-- "+environmentCodeofConduct+" -->\n"+
														"<!-- "+envrionmentSustainability+" -->\n"+
														"<!-- "+envrionmentSocialHealth+" -->\n"+
														"<!-- "+environmentOccupational+" -->\n");				                                        	
														if (environmentCodeofConduct){
														   response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">1</span>\n"+
																"<div class=\"accordion-body collapse in\">\n"+
																	"<div class=\"accordion-question\">\n"+
																		"Has the NBCUniversal Supplier developed a Code of Conduct  based on the International Labor Organization, United Nations Global Compact and other internationally recognized standards, to clarify company expectations in the areas of labor practices, health and safety, environmental management and business integrity?\n"+
																		"<div class=\"describe\" style=\"display: block;\">\n"+
																			"Describe\n"+
																		  "<textarea name=\"basicQ1Describe\" class=\"input-block-level\" required>"+environmentCodeofConductComment+"</textarea>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading\">\n"+
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
																		"<a class=\"btn yes-answer active\" active><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
																		"<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ1\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ1\" value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");			                                        
														} else {
															response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
															   // "<span class=\"badge badge-inverse\">2</span>\n"+
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
																		"<a class=\"btn active\" active><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ1\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ1\"  value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");
														}
														if (envrionmentSustainability){
														   response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">3</span>\n"+
																"<div class=\"accordion-body collapse in\">\n"+
																	"<div class=\"accordion-question\">\n"+
																		"Has  the NBCUniversal  Supplier established Environmental Sustainability improvement goals and objectives to manage the design and packaging of products;  reduce greenhouse gas emissions, waste and water usage?\n"+
																		"<div class=\"describe\" style=\"display: block;\">\n"+
																			"Describe\n"+
																		  "<textarea name=\"basicQ2Describe\" class=\"input-block-level\" required>"+envrionmentSustainabilityComment+"</textarea>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading\">\n"+
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
																		"<a class=\"btn yes-answer active\" active><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
																		"<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ2\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ2\" value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");			                                        
														} else {
															response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">4</span>\n"+
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
																		"<a class=\"btn active\" active><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ2\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ2\"  value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");
														}
														if (envrionmentSocialHealth){
														   response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">1</span>\n"+
																"<div class=\"accordion-body collapse in\">\n"+
																	"<div class=\"accordion-question\">\n"+
																		"Does the NBCUniversal Supplier maintain a social, health, safety and environmental compliance assurance audit program to monitor and verify  performance within company operations and in the supply chain?\n"+
																		"<div class=\"describe\" style=\"display: block;\">\n"+
																			"Describe\n"+
																		  "<textarea name=\"basicQ3Describe\" class=\"input-block-level\" required>"+envrionmentSocialHealthComment+"</textarea>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading\">\n"+
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
																		"<a class=\"btn yes-answer active\" active><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
																		"<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ3\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ3\" value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");			                                        
														} else {
															response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">2</span>\n"+
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
																		"<a class=\"btn active\" active><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ3\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ3\"  value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");
														}
														if (environmentOccupational){
														   response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">3</span>\n"+
																"<div class=\"accordion-body collapse in\">\n"+
																	"<div class=\"accordion-question\">\n"+
																		"Does the NBCUniversal Supplier maintain an Occupational, Health and Safety, Social and/or Environmental Management System that is certified to ISO 18001, ISO 14001, ISO 9001, REACH, RoHS and/or WEEE?\n"+
																		"<div class=\"describe\" style=\"display: block;\">\n"+
																			"Describe\n"+
																		  "<textarea name=\"basicQ4Describe\" class=\"input-block-level\" required>"+environmentOccupationalComment+"</textarea>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading\">\n"+
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
																		"<a class=\"btn yes-answer active\" active><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
																		"<a class=\"btn\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ4\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ4\" value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");			                                        
														} else {
															response.write("<div class=\"accordion-group\" style=\"display: block;\">\n"+
																//"<span class=\"badge badge-inverse\">4</span>\n"+
																"<div class=\"accordion-body collapse in\">\n"+
																	"<div class=\"accordion-question\">\n"+
																		"Does the NBCUniversal Supplier maintain an Occupational, Health and Safety, Social and/or Environmental Management System that is certified to ISO 18001, ISO 14001, ISO 9001, REACH, RoHS and/or WEEE?\n"+
																		"<div class=\"describe\">\n"+
																			"Describe\n"+
																		  "<textarea name=\"basicQ4Describe\" class=\"input-block-level\"></textarea>\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
																"<div class=\"accordion-heading\">\n"+
																	"<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
																		"<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes<span class=\"caret\"></span></a>\n"+
																		"<a class=\"btn active\" active><i class=\"icon-ok-sign\"></i>No</a>\n"+
																		"<div class=\"hidden-form-elements\">\n"+
																			"<input type=\"radio\" name=\"basicQ4\" value=\"yes\" />\n"+
																			"<input type=\"radio\" name=\"basicQ4\"  value=\"no\" />\n"+
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n"+
															"</div>\n");
														}
										response.write("</div>\n");
										}
	
		                         //Req#603 Continued 2
		                         response.write("</div>\n"+
		                        		 //Begin of Insert for CTI
                                         "<div id=\"TAXEXEMPT\" style=\"display:"+(showTaxCategory?"block":"none")+";\">"+
                                         //End of Insert for CTI
                                         // ENHC0019092 By Naga -- Add Numbers to Exempt questions to be in sync with W9
		                                    
                                         //Begin Of Insert by Pranesh(31-03-2016) - ENHC0016459
                                         
                                         //Before change - Pranesh(31-03-2016) - ENHC0016459
                                         //"<hr>\n"+
                                         
                                         //After change - Pranesh(31-03-2016) - ENHC0016459
                                         "<hr>\n");
		                                        
		                                //Before change - Pranesh(31-03-2016) - ENHC0016459
                                        //"<div class=\"row-fluid\" id=\"questionexpayee\">\n"+
		                         
		                         				if(decisionVendorType.equalsIgnoreCase("R095"))
		                         				//After change - Pranesh(31-03-2016)- ENHC0016459
		                         				response.write("<div class=\"row-fluid\" id=\"questionexpayee\" style=\"display:none;\">\n");
		                         						
		                                		else if(!(decisionVendorType.equalsIgnoreCase("R095")))
		                                		response.write("<div class=\"row-fluid\" id=\"questionexpayee\" style=\"display:block;\">\n");
		                         				
		                         			// start DFCT0017543 Ganesh
		                         				
		                         				String exemptRedCodeInput=""; 
		                         				if( vendorType.equalsIgnoreCase("010")|| vendorType.equalsIgnoreCase("018")|| vendorType.equalsIgnoreCase("020")|| vendorType.equalsIgnoreCase("030") || vendorType.equalsIgnoreCase("080") || vendorType.equalsIgnoreCase("090") || vendorType.equalsIgnoreCase("093") || vendorType.equalsIgnoreCase("094"))
		                         				{
		                         					exemptRedCodeInput="required";
		                         				}
		                         			// end DFCT0017543 Ganesh
	                         				response.write("<div class=\"control-group span12\">\n"+
                                                                "<label class=\"control-label required-red\">\n"+ // DFCT0017543 Ganesh
                                                                    "Exempt Payee Code\n"+
                                                                "</label>\n"+
                                                               // "<div class=\"alert alert-danger fileRequiredError taxexmpc\" style=\"margin-bottom: 20px;\">Please select an option</div>\n"+
                                                                // Removed "requied" -Pranesh (04/17/2016)
                                                                "<input type=\"text\" name=\"taxexmpc\" id=\"taxexmpc\" style=\"display:none\" value=\""+exempt+"\">\n"+
                                                                "<div class=\"controls\">\n"+
                                                                // Removed "required" - Pranesh (04/17/2016)
                                                                    "<div class=\"multi-line-select\">\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" "+exemptRedCodeInput+" value=\"1\" "+(exempt.equals("1")?"checked":"")+"/>\n"+ // DFCT0017543 Ganesh added required
                                                                            "1 - An organization exempt from tax under section 501(a), any IRA, or a custodial account under section 403(b)(7) if the account satisfies the requirements of section 401(f)(2)\n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"2\" "+(exempt.equals("2")?"checked":"")+"/>\n"+
                                                                            "2 - The United States or any of its agencies or instrumentalities\n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"3\" "+(exempt.equals("3")?"checked":"")+"/>\n"+
                                                                            "3 - A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"4\" "+(exempt.equals("4")?"checked":"")+"/>\n"+
                                                                            "4 - A foreign government or any of its political subdivisions, agencies, or instrumentalities \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"5\" "+(exempt.equals("5")?"checked":"")+"/>\n"+
                                                                            "5 - A corporation\n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"6\" "+(exempt.equals("6")?"checked":"")+"/>\n"+
                                                                            "6 - A dealer in securities or commodities required to register in the United States, the District of Columbia, or a possession of the United States \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"7\" "+(exempt.equals("7")?"checked":"")+"/>\n"+
                                                                            "7 - A futures commission merchant registered with the Commodity Futures Trading Commission \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"8\" "+(exempt.equals("8")?"checked":"")+"/>\n"+
                                                                            "8 - A real estate investment trust \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"9\" "+(exempt.equals("9")?"checked":"")+"/>\n"+
                                                                            "9 - An entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"10\" "+(exempt.equals("10")?"checked":"")+"/>\n"+
                                                                            "10 - A common trust fund operated by a bank under section 584(a) \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"11\" "+(exempt.equals("11")?"checked":"")+"/>\n"+
                                                                            "11 - A financial institution  \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"12\" "+(exempt.equals("12")?"checked":"")+"/>\n"+
                                                                            "12 - A middleman known in the investment community as a nominee or custodian   \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"13\" "+(exempt.equals("13")?"checked":"")+"/>\n"+
                                                                            "13 - A trust exempt from tax under section 664 or described in section 4947 \n"+
                                                                        "</label>\n"+
                                                                        "<label class=\"radio\">\n"+
                                                                            "<input type=\"radio\" name=\"exempt\" class=\"exempt\" value=\"14\" "+(exempt.equals("14")?"checked":"")+"/>\n"+
                                                                            "14 - Not Applicable \n"+
                                                                        "</label>\n"+
                                                                    "</div>\n"+
                                                                "</div>\n"+
                                                            "</div>\n"+
                                                        "</div>\n"+
                                                        "<hr>\n"+
                                                        // Begin of comment by Naga ENHC0018723
                                                        // Hide the facta questions
//                                                        "<div class=\"row-fluid\" id=\"questionfacta\">\n"+
//                                                            "<div class=\"control-group span12\">\n"+
//                                                            "<!-- retCT_ANSWER\n"+retCT_ANSWER+"\n-->"+
//                                                                "<label class=\"control-label required-red\">\n"+
//                                                                    "Exempt for FATCA Reporting \n"+
//                                                                "</label>\n"+
//                                                                //"<div class=\"alert alert-danger fileRequiredError taxexmfactc\" style=\"margin-bottom: 20px;\">Please select an option</div>\n"+
//                                                                "<input type=\"text\" required=\"required\" name=\"taxexmfactc\" id=\"taxexmfactc\" style=\"display:none\" value=\""+fatca+"\">\n"+
//                                                                "<div class=\"controls\">\n"+
//                                                                    "<div class=\"multi-line-select\" required>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"A\" "+(fatca.equals("A")?"checked":"")+"/>\n"+
//                                                                            "An organization exempt from tax under section 501(a), or any individual retirement plan as defined in section 7701(a)(37) \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"B\" "+(fatca.equals("B")?"checked":"")+"/>\n"+
//                                                                            "The United States or any of its agencies or instrumentalities \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"C\" "+(fatca.equals("C")?"checked":"")+"/>\n"+
//                                                                            "A state, the District of Columbia, a possession of the United States, or any of their political subdivisions, agencies, or instrumentalities\n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"D\" "+(fatca.equals("D")?"checked":"")+"/>\n"+
//                                                                            "A corporation the stock of which is regularly traded on one or more established securities markets, as described in Reg, section 1.1472-1(c)(1)(i) \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"E\" "+(fatca.equals("E")?"checked":"")+"/>\n"+
//                                                                            "A corporation that is a member of the same expanded affiliated group as a corporation described in Reg. section 1.1472-1(c)(1)(i) \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"F\" "+(fatca.equals("F")?"checked":"")+"/>\n"+
//                                                                            "A dealer in securities, commodities, or derivative financial instruments (including notional principal contracts, futures, forwards, and options) that is registered as such under the laws of the United States or any State \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"G\" "+(fatca.equals("G")?"checked":"")+"/>\n"+
//                                                                            "A real estate investment trust \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"H\" "+(fatca.equals("H")?"checked":"")+"/>\n"+
//                                                                            "A regulated investment company as defined in section 851 or an entity registered at all times during the tax year under the Investment Company Act of 1940 \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"I\" "+(fatca.equals("I")?"checked":"")+"/>\n"+
//                                                                            "A common trust fund as defined in section 584(a) \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"J\" "+(fatca.equals("J")?"checked":"")+"/>\n"+
//                                                                            "A bank as defined in section 581\n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"K\" "+(fatca.equals("K")?"checked":"")+"/>\n"+
//                                                                            "A broker  \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"L\" "+(fatca.equals("L")?"checked":"")+"/>\n"+
//                                                                            "A trust exempt from tax under section 664 or described in section 4947 \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"M\" "+(fatca.equals("M")?"checked":"")+"/>\n"+
//                                                                            "A tax-exempt trust under a section 403(b) plan or section 457(g) plan \n"+
//                                                                        "</label>\n"+
//                                                                        "<label class=\"radio\">\n"+
//                                                                            "<input type=\"radio\" name=\"facta\" class=\"facta\" value=\"N\" "+(fatca.equals("N")?"checked":"")+"/>\n"+
//                                                                            "Not Applicable \n"+
//                                                                        "</label>\n"+
//                                                                    "</div>\n"+
//                                                                "</div>\n"+
//                                                            "</div>\n"+
//                                                        "</div>\n"+
                                                        // End of comment by Naga
                                                        //Begin of Insert for CTI
                                                        "</div>\n");
		                         						//End of Insert for CTI		                                                
		                                            //Req#603 END
		                                            response.write("</div>\n");
	      				                           response.write("<div class=\"form-actions\">\n"+
				                                        "<a class=\"btn back\" href=\"#tab1\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n");
	      				                        
	      				                              // Added (095) - Pranesh (04/28/2015)-(Defect ID : 15048),for bypassing TERM tab screen
				                                        if (vendorType.equalsIgnoreCase("095")){
				                                        	response.write("<a class=\"btn btn-success continue\" href=\"#tab4\">Continue <i class=\"icon-angle-right\"></i></a>\n");
				                                        }else{
				                                        	response.write("<a class=\"btn btn-success continue\" href=\"#tab3\">Continue <i class=\"icon-angle-right\"></i></a>\n");
				                                        }
				                                     // Added (095) - Pranesh (04/28/2015)-(Defect ID : 15048)
				                                        
				                                      //Req#51 START Code change by AGAMPA
				                                      //  "<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                  response.write( "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
											
											
				                            "<div class=\"tab-pane fade\" id=\"tab3\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                     "<h1>Please select one of the following:</h1>\n"+
				                                     
				                                     // Begin of insert Pranesh(04/20/2016) - ENHC0018725
				                                     	//"<p>Note : In case of terms need to be change, Please contact NBCU Business</p>"+ // Blocked - Pranesh - (04/29/2016)-(Def : 15042)
				                                     // End of insert   Pranesh(04/20/2016) - ENHC0018725
				                                     	
				                                        "<div id=\"termsInfo\" class=\"btn-group vendor-accordion "+termsDisplayOnly+"\" data-toggle=\"buttons-radio\">\n"+				                                         
				                                            "<!-- "+terms+" -->\n");
				                                          for(int i = 0; i < retCT_ZTERMS.getNumRows() ; i++) {
				                                        	if (retCT_ZTERMS.getString("TEXT1").length()>0){
					                                        	 response.write("<div class=\"accordion-group\">\n");
					                                            //if (retCT_ZTERMS.getString("TERM_FLAG").equalsIgnoreCase("X")){
					                                        	 if (retCT_ZTERMS.getString("ZTERM").equalsIgnoreCase(terms)){
					                                            	response.write("<a class=\"btn btn-block btn-large btn-info "+toggleTerms+" active\" "+disableTerms+" id=\"terms-"+retCT_ZTERMS.getString("ZTERM")+"\" data-target=\"#terms"+i+"\" data-option=\""+retCT_ZTERMS.getString("ZTERM")+"\" data-parent=\"#termsInfo\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>"+retCT_ZTERMS.getString("TEXT1")+"</a>\n");
					                                            	response.write("<div id=\"terms"+i+"\" class=\"collapse in\" style=\"height: auto;\" >\n");
					                                            } else {
					                                            	// Added "style=display:none" to hide Pranesh(04/20/2016) - ENHC0018725
					                                            	// style=\"display:none;\" - Temp,removed
					                                            	response.write("<a class=\"btn btn-block btn-large btn-info "+toggleTerms+"\" "+disableTerms+" id=\"terms-"+retCT_ZTERMS.getString("ZTERM")+"\" data-target=\"#terms"+i+"\" data-option=\""+retCT_ZTERMS.getString("ZTERM")+"\" data-parent=\"#termsInfo\"><i class=\"icon-check-sign\"></i><i class=\"icon-sign-blank \"></i>"+retCT_ZTERMS.getString("TEXT1")+"</a>\n");
					                                            	response.write("<div id=\"terms"+i+"\" class=\"collapse\">\n");
					                                            }
					                                        	
					                                        	// Begin added Pranesh(04/20/2016) - ENHC0018725
					                                        	 if(userIsInternalEmployeeBuyer){
					                                        	      response.write("<span></span>\n"+
							                                            "<div class=\"terms-copy\" style=\"display:none;\">\n");// Need to implement hide,for hiding Terms & Conditions - Pranesh(04/20/2016)-(ENHC0018725)-Sourcing
					                                        	 }else{
					                                        	      response.write("<span></span>\n"+
							                                             "<div class=\"terms-copy\" style=\"display:none;\">\n");
					                                        	 }
					                                        	
					                                        	// Begin end Pranesh(04/20/2016) - ENHC0018725
					                                        	 
					                                        	 
					                                        	 response.write("<h4>Terms &amp; Conditions</h4>\n"+
																				"NBCUNIVERSAL PURCHASE ORDER TERMS AND CONDITIONS\n"+
																				"These Terms and Conditions are part of any Purchase Order issued by an NBCUniversal Media, LLC entity (\"NBCUniversal\"), and together, these Terms and Conditions and the Purchase Order constitute the agreement between NBCUniversal and Supplier for the purchase of goods and/or services set forth on the Purchase Order.  As used herein, \"Supplier\" shall mean the supplier named in the Purchase Order, and \"NBCUniversal\" shall mean the NBCUniversal Media, LLC entity issuing such Purchase Order.  Other capitalized terms not defined herein shall have the meanings assigned to them in the Purchase Order.  In the event the express terms of the Purchase Order conflict with these standard Terms and Conditions, the Purchase Order shall control.\n"+
																				"<br><br>\n"+
																				"1.   Acceptance.  Supplier may accept a Purchase Order, and any changes thereto requested in writing as provided below, by written or verbal notice to NBCUniversal, furnishing any goods (\"Goods\") or services (\"Services\") specified in the Purchase Order, accepting payment for Goods or Services, or any other act or omission that can reasonably be construed as acceptance of the Purchase Order.  By acceptance of this Purchase Order, Supplier agrees to comply fully with the terms and conditions of sale set forth in the Purchase Order and these Terms and Conditions, and any supplements hereto which are expressly incorporated by a reference herein.  Acceptance of this Purchase Order is expressly limited to the terms and conditions contained herein and none of Supplier's terms and conditions in acknowledging or accepting this Purchase Order shall apply.  Acceptance of the Goods or Services under this Purchase Order shall not constitute acceptance of Supplier's terms and conditions.  No employee, representative or agent of NBCUniversal has any authority to bind NBCUniversal to any terms except those specifically included in this Purchase Order or a written amendment hereto which is signed by an authorized representative of NBCUniversal's Sourcing Department.  Any different or contradictory terms in any Supplier-provided order form, rental agreement, or other document regarding an order, even if executed or agreed to by an NBCUniversal employee, representative or agent, shall be of no force or effect.  Supplier shall make no substitutions or changes to Goods as ordered unless NBCUniversal gives prior written consent to such substitution.  Shipments shall not vary from quantities specified herein unless agreed to in writing by NBCUniversal.  Supplier may not ship under reservation.  \n"+
																				"<br><br>\n"+
																				"2.   Delivery/Inspection/Rejection/Remedies.  Time is of the essence with respect to this Purchase Order.  If Supplier fails to make deliveries or perform the Services at the time agreed upon, or performs the work in such a fashion as to endanger its ability to make timely deliveries or render timely performance of Services, NBCUniversal shall have the right to cancel its entire order or the portion so delayed, and Supplier shall be liable for all costs and damages incurred by NBCUniversal as a result of such delay, including without limitation, costs of cover.  NBCUniversal may deduct the amount of such costs and damages from any payments owed to Supplier, and invoice Supplier for any excess, which excess Supplier shall pay within 30 days of such invoice date.  Goods purchased F.O.B. Supplier's plant or shipping point shall not be considered delivered until they reach the NBCUniversal receiving point as set forth herein or such other shipping point as may be agreed to in writing; however, NBCUniversal assumes responsibility at the F.O.B. point for transportation charges (where applicable), provided such Goods are either prepared and packed in accordance with the packaging specifications expressly set forth in this Purchase Order, or if none are expressly set forth, then as set forth below.  Supplier shall package all Goods for ease of handling and in such manner as to assure their protection during shipment and storage.  Supplier shall mark all packing slips and invoices with the applicable Purchase Order number.   All Goods shall be subject to inspection by NBCUniversal after delivery, and NBCUniversal may reject any Goods containing defective materials or workmanship or that do not conform to specifications or samples.  Final inspection and acceptance of Goods and Services shall be on NBCUniversal's premises unless otherwise agreed to in writing.  Nonconforming Goods shall be returned at full invoice price plus applicable shipping charges, and Supplier's account shall also be charged for the inbound transportation cost plus handling expense.  If any of the goods ordered are found at any time to be defective in material or workmanship, or otherwise not in conformity with the requirements of this Purchase Order or specifications set forth to Supplier from NBCUniversal, in addition to such other rights, remedies and choices as it may have by contract or by law, NBCUniversal, in its sole discretion, may: (a) reject and return such Goods as specified above; (b) require Supplier to remove and replace nonconforming Goods with Goods that conform to the Purchase Order or specifications set forth by NBCUniversal; or (c) require Supplier to repair nonconforming Goods at a facility designated by NBCUniversal.  If NBCUniversal elects option (b) above and Supplier fails to promptly make the necessary removal and replacement, NBCUniversal may in its sole discretion sort the Goods and return all nonconforming Goods to Supplier; Supplier shall pay the cost thereof.  Approval by NBCUniversal of Supplier's proposed design, test plans and/or procedures, manufacturing processes, methods, tooling or facilities shall not relieve Supplier from meeting all requirements of this Purchase Order.  The rights and remedies of NBCUniversal hereunder are cumulative and in addition to those which NBCUniversal may have under law or equity.  \n"+
																				"<br><br>\n"+
																				"3.   General Shipping Instructions.   For shipments for which NBCUniversal pays shipping charges:  (a) all shipments moving in one day to the same location via the same carrier shall be consolidated into one bill of lading.  Multiple packages in the same courier shipment shall be tied into bundles; (b) unless otherwise specifically instructed, shipments via limited liability carriers (e.g., UPS) and those subject to replacement value ratings shall be declared at the value, which will secure the lowest transportation charge; (c) Supplier shall comply with NBCUniversal's shipping and routing instructions.  Supplier shall not use premium cost transportation unless authorized by NBCUniversal; (d) losses and/or excess charges resulting from deviation from NBCUniversal's instructions will be charged to Supplier's account; (e) Supplier shall forward the receipt or bill of lading signed by the carrier with Supplier's invoice as evidence of shipment.  Supplier shall receive and retain mailing receipts for uninsured parcel post.  \n"+
																				"<br><br>\n"+
																				"4.   Services.   (a) All Services shall be provided according to the specifications agreed upon by the parties, and shall include all services customarily rendered by a contractor in Supplier's industry, including without limitation, when the Services include creative services, all services customarily provided by contractors in the motion picture, television, entertainment or media/communications industry, as applicable.  Supplier shall perform all Services in a good and workmanlike manner, in accordance with quality standards at least as high as current industry standards, and using personnel qualified and capable of performing the Services according to such standards.  Supplier shall coordinate with the NBCUniversal contact named in the Purchase Order as to scheduling and other specifications not set forth in the Purchase Order or otherwise agreed upon by the parties.  Supplier shall also provide reports of the type and at the frequency reasonably requested by NBCUniversal during the term of the engagement, and shall deliver to NBCUniversal those materials (the \"Materials\"), if any, specifically referenced in the Purchase Order, at the times identified therein or as otherwise reasonably requested by NBCUniversal.  The Services shall be non-exclusive (except as otherwise noted on the Purchase Order), but on a first-priority, in-person basis, during the term of the Supplier's engagement.  Supplier and its subcontractors shall comply with the rules set forth in the NBCUniversal Contractor Safety Policy if providing Services at any NBCUniversal premises, which Policy is incorporated into and made part hereof by this reference.  Supplier may obtain a copy of such Policy from the NBCUniversal contact upon request.   \n"+
																				"<br><br>\n"+
																				"(b) If the Purchase Order provides that the Services are to be performed by an identified artist (\"Artist\"), then the services of Artist will be furnished to NBCUniversal by Supplier and consequently all services and obligations performed by Artist pursuant to the Purchase Order shall be deemed to be performed by Artist at the discretion of Supplier and all compensation and other payments therefor shall be paid by NBCUniversal to Supplier.  \n"+
																				"<br><br>\n"+
																				"(c) NBCUniversal may at any time request that Supplier perform work in addition to the Services (\"Additional Services\") by submitting such a request to Supplier in writing, signed by an authorized representative of NBCUniversal's Sourcing Department.  Supplier may accept such request by written or verbal acceptance, performing the Additional Services, accepting payment therefor, or any other act or omission that can reasonably be construed as acceptance of such request.  Payment for such Additional Services shall be at the rate set forth by NBCUniversal in such request, or a different rate, provided NBCUniversal agrees to such different rate in writing prior to Supplier performing the Additional Services.  The rights and obligations of the parties set forth in these Terms and Conditions pertaining to Services shall also apply to Additional Services.  \n"+
																				"<br><br>\n"+
																				"(d) If at any time Supplier determines that it will have to incur unforeseen additional costs to complete the Services, whether due to changes requested by NBCUniversal or any other unforeseen change in circumstances, Supplier shall have an absolute obligation to notify NBCUniversal thereof and Supplier shall not charge any such additional costs to NBCUniversal, either directly or indirectly, unless it obtains NBCUniversal's prior written approval of such additional charge.  \n"+
																				"<br><br>\n"+
																				"(e) NBCUniversal may request the replacement of any worker provided by Supplier hereunder at any time for any lawful reason, and Supplier shall replace that worker as soon as possible with a worker whose performance is acceptable to NBCUniversal, at no additional charge to NBCUniversal.  If NBCUniversal requests that Supplier assign particular employees or subcontractors to perform services hereunder, Supplier shall use its best efforts to accommodate such request, and if it is unable to do so, it shall provide workers of like skill and experience to perform the services.\n"+
																				"<br><br>\n"+
																				"5.   Changes.   NBCUniversal may, at any time, by a written order, and without notice to sureties, make changes within the general scope of this Purchase Order in any one or more of the following:  (i) drawings, designs or specifications; (ii) method of shipment or packing; and (iii) place of delivery.  If any such change causes an increase or decrease in the cost of the item required for the performance of any part of the work under this Purchase Order, whether changed or not changed by any such Purchase Order, Supplier shall notify NBCUniversal of any proposed modifications to the price or delivery schedule, and upon NBCUniversal's acceptance of such proposal, this Purchase Order shall be deemed so modified.  NBCUniversal Engineering, Technical and other personnel may from time to time render assistance or give technical advice to or exchange information with Supplier's personnel concerning this Purchase Order or the Goods or Services to be furnished hereunder.  However, this shall not constitute a waiver with respect to any of Supplier's obligations or NBCUniversal's rights hereunder or be authority for any change in the Goods or Services called for hereunder.  Any waiver or change to be valid and binding upon NBCUniversal must be in writing and signed by an authorized representative of NBCUniversal's Sourcing Department.  In case of any doubt, Supplier should promptly consult NBCUniversal's Sourcing Department for further instructions.  In connection with any claim for adjustment under this paragraph, Supplier shall submit cost data in such form and detail as may reasonably be required by NBCUniversal; if this Purchase Order relates to a prime contract with the U.S. Government, Supplier shall, upon NBCUniversal's request, submit a Certificate of Current Cost or Pricing Data, in substantially the form set forth in Section 3-807.4 of the Armed Services Procurement Regulation, with respect to such data.  Where the cost of property made obsolete or excessive as a result of a change is included in Supplier's claim for adjustment pursuant to this paragraph, NBCUniversal shall have the right to prescribe the manner of disposition of such property.  Whenever any actual or potential event, including labor disputes, occurs that delays or threatens to delay the timely performance of this Purchase Order, Supplier shall give immediate notice to NBCUniversal in writing.  If Supplier does not comply with NBCUniversal's delivery schedule, NBCUniversal may require delivery by fastest way and charges resulting from the premium transportation must be fully prepaid and absorbed by Supplier.  \n"+
																				"<br><br>\n"+
																				"6.   Warranty.  Unless a different warranty is set forth on the face of the Purchase Order, Supplier warrants all Goods and/or Services delivered or provided hereunder to be free and clear of all liens, encumbrances, security interests or other claims, free from defect in design, materials or workmanship, and fit for the purpose intended for a period of twelve (12) months from delivery to, and acceptance by NBCUniversal, and to conform strictly to the specifications, drawings or sample specified or furnished and any supplementary documentation referenced herein.  In the event that any of the Goods are software, Supplier represents and warrants that such Goods do not contain any code, programming instruction, or set of instructions that is intentionally constructed to damage, interfere with or otherwise adversely affect operation of such Goods or other computer programming code, data files, or hardware without the consent and intent of NBCUniversal.  Supplier further warrants that it complies with the requirements of all applicable federal, state, provincial and local laws, rules, ordinances and regulations such as, but not limited to (with respect to Goods and Services produced and/or delivered in the United States), OSHA, Hazardous Materials Transportation Act, Dept. of Transportation \"Hours of Service\" restrictions for drivers, Toxic Substances Control Act and Consumer Product Safety Act.  This warranty shall survive any inspection, delivery or acceptance of the Goods or Services, or payment therefor by NBCUniversal.  \n"+
																				"<br><br>\n"+
																				"7.   Pricing.  Prices set forth in this Purchase Order shall be firm and fixed unless otherwise agreed to in writing by an authorized representative of NBCUniversal.  Unless otherwise provided herein, such prices include all applicable federal, state, provincial and local taxes.  Prices for Goods shall include all charges for packaging and transportation to F.O.B. point.\n"+
																				"<br><br>\n"+
																				"8.  Payment.  Payment for Goods and Services shall not be due and payable unless the delivery or performance of such Goods and Services has been completed, the Goods or Services have been accepted by NBCUniversal, and appropriate invoices have been received by NBCUniversal.  Unless otherwise specified on the Purchase Order, payment terms shall be 2.5% 15, net 75 days from NBCUniversal's receipt of a complete and accurate invoice.  The 2.5% 15 early pay discount period, and any other early pay or cash discount period applicable to the Purchase Order, shall start on the later to occur of (i) NBCUniversal's acceptance of the Goods or Services, or (ii) NBCUniversal's receipt of a complete and accurate invoice for the Goods or Services.  In the event that errors in invoicing cause a delay in payment, invoices will default to a payment term of 1.5% 30, Net 75.  Payment made by NBCUniversal in compliance with this Purchase Order shall be deemed full and complete compensation for the Goods or Services, Supplier's costs and expenses, and equipment supplied by Supplier, all as specified in this Purchase Order, and all rights granted to NBCUniversal hereunder.  It is expressly understood that should NBCUniversal for any reason whatsoever fail to make a payment required hereunder, then NBCUniversal shall not be deemed in default hereunder unless and until following such failure Supplier shall have given written notice demanding such payment and NBCUniversal shall have failed to make such payment within ten (10) days of NBCUniversal's receipt of such notice.  Supplier shall be solely liable for and shall pay all applicable federal, state, provincial and/or local taxes on all amounts earned pursuant to this Purchase Order.  Supplier further agrees to indemnify, defend (with counsel acceptable to NBCUniversal) and hold harmless NBCUniversal, and its licensees, employees, agents, successors and assigns from and against any and all liability (including attorneys' fees) that it or they may incur regarding the payment of taxes for Supplier's services.  \n"+
																				"<br><br>\n"+
																				"9.   Expenses.   Unless provided otherwise in the Purchase Order, expenses will only be reimbursed if they are (i) approved in advance in writing by an authorized representative of NBCUniversal's Sourcing Department; (ii) are incurred in connection with performing Services; and (iii) are reasonable, reimbursable and substantiated with satisfactory original documentation, all in accordance with NBCUniversal's Reimbursable Expenses Guidelines, which are incorporated herein by reference, and are available to Supplier upon request.  \n"+
																				"<br><br>\n"+
																				"10.   Withholds/Lien Releases.   NBCUniversal may withhold or nullify (whether or not on account of subsequent discovered evidence) the whole or part of any payment due hereunder to the extent reasonably necessary to protect NBCUniversal from loss due to:  (a) defective work not remedied; (b) claims filed or reasonable evidence indicating probable filings in connection with the Goods or Services; (c) failure of Supplier to make necessary payments to subcontractors for materials or labor furnished in connection with this Purchase Order; (d) reasonable doubt that this Purchase Order can be completed on time and/or for the balance then unpaid; or (e) as a setoff against any amount payable at any time by Supplier to NBCUniversal in connection with this Purchase Order.  When Supplier removes the grounds for a withheld payment, NBCUniversal shall pay the amounts withheld pursuant to the payment terms set forth in this Purchase Order.  NBCUniversal may require that each invoice comprising payments to subcontractors shall include appropriate conditional and unconditional waiver and lien releases from both Supplier and Supplier's subcontractors.  \n"+
																				"<br><br>\n"+
																				"11.  Term and Termination.   Unless otherwise expressly set forth herein, the Term of this Purchase Order shall be from the date of the Purchase Order until delivery and acceptance of all or Goods or completion of all the Services.  Notwithstanding the foregoing, NBCUniversal may terminate this Purchase Order at any time, for any reason, upon written notice to Supplier; provided, that if NBCUniversal terminates a Purchase Order for any reason other than Supplier's default, NBCUniversal shall pay to Supplier any and all sums that are due and payable for Goods delivered and accepted, and Services provided and accepted, through the date of termination and shall reimburse Supplier for expenses incurred in accordance with the Purchase Order through the date of such termination.  Should termination charges be contemplated, Supplier shall identify said charges within thirty days of termination and bear the burden of proof in justifying such charges.  NBCUniversal shall have no other obligation hereunder from and after the date of termination.  Termination for any reason shall not affect the rights granted to NBCUniversal by Supplier hereunder.  \n"+
																				"<br><br>\n"+
																				"12.   Bankruptcy.  In the event of any proceedings, voluntary or involuntary, in bankruptcy or insolvency by or against Supplier, including any reorganization or arrangement proceeding, or in the event of the appointment, with or without Supplier's consent, of an assignee for the benefit of creditors or a receiver, or in the event of any financial distress of Supplier which in NBCUniversal's reasonable judgment impairs Supplier's ability to perform under this Purchase Order, then NBCUniversal may cancel this Purchase Order for default and hold Supplier accountable for any additional costs or damages incurred by NBCUniversal.  NBCUniversal's only liability shall be payment in accordance with this Purchase Order for deliveries previously made or for Goods covered by this Purchase Order, then completed and subsequently delivered in accordance with the terms of this Purchase Order.  \n"+
																				"<br><br>\n"+
																				"13.  Supplier's Remedies.  Supplier's sole and exclusive remedy for NBCUniversal's breach, termination or cancellation of this Purchase Order or any term hereof (including any term pertaining to credit) shall be either: (i) the relief provided for in the applicable collective bargaining agreement, if any, or (ii) if no collective bargaining agreement applies, an action for damages; provided that the maximum amount payable by NBCUniversal under any theory shall be limited to the face amount of this Purchase Order.  Supplier irrevocably waives any right to equitable or injunctive relief.  \n"+
																				"<br><br>\n"+
																				"14.   Employer Obligations and Other Obligations.   \n"+
																				"<br>\n"+
																				"(a)  Labor Disputes.   Supplier agrees to conduct its operation and its relations with all of its employees and all of the employees of subcontractors so as not to interfere with, or cause labor or union friction with, any labor unions or personnel working at any NBCUniversal location.  If Supplier becomes involved in a labor dispute that may potentially injure or inconvenience NBCUniversal, Supplier shall comply with all NBCUniversal instructions regarding continued performance of this Purchase Order, and NBCUniversal shall have the right to terminate this Purchase Order immediately.  Supplier shall reimburse NBCUniversal for any costs incurred by NBCUniversal as a result of labor difficulties that arise in connection with Supplier's presence on NBCUniversal's premises.  \n"+
																				"<br>\n"+
																				"(b)  Guild Membership.   If NBCUniversal is a party to a collective bargaining agreement or organization (as defined and determined under the then-applicable law) representing persons performing services of the type and character required to be performed by Supplier hereunder and having jurisdiction on the premises where the Services are to be performed, then Supplier shall during Supplier's engagement hereunder, at Supplier's sole cost and expense, be a member in good standing of such labor organization.  \n"+
																				"<br>\n"+
																				"(c)  Immigration Laws.   For Goods and Services to be produced or provided in the United States, Supplier agrees to comply with the Immigration Reform and Control Act of 1986 with respect to all employees of Supplier.  Supplier hereby certifies that it has verified the identity and employment eligibility and completed an I-9 form for every employee that provides Goods or Services to NBCUniversal hereunder.  NBCUniversal reserves the right to audit Supplier's records supporting such certification, and to immediately terminate this Purchase Order if Supplier fails to provide legally adequate records as to each such employee.  NBCUniversal further reserves the right to require Supplier to provide such certification on a periodic basis during the Term of this Purchase Order, and to make payment of any amounts owed to Supplier at the time contingent upon Supplier providing such periodic certification.  \n"+
																				"<br>\n"+
																				"(d)   Equal Employment Opportunity.   For Goods and Services to be produced in the United States, Supplier, where required by law, hereby agrees to comply with Executive Order 11246, as amended, in its implementing regulations, including the Equal Opportunity Clause set forth in Section 202 of the order and 60-1.4(A) of the regulations of the Secretary of Labor, Title 42 CFR, Chapter 60, Parts 1-60, which are incorporated into this Purchase Order by reference.  In addition, this Purchase Order incorporates by reference the Affirmative Action Clauses of the Rehabilitation Act of 1973, at 41 CFR 60-741.4, and the Vietnam Era Veterans Readjustment Act of 1974, at 41 CFR 60-250.4.  \n"+
																				"<br>\n"+
																				"(e)   General Employer Obligations.   If Supplier is a corporation, firm or other entity, Supplier shall discharge all obligations of an employer to its employees providing any of the Services hereunder including, but not limited to, the payment to its employees of not less than the minimum compensation under any applicable law, guild or union agreement, the payment of pension, health and welfare contributions required under any applicable law, guild or union agreement, the withholding and reporting of contributions, insurance deductions and applicable taxes required by law, including payroll taxes and unemployment insurance, and providing satisfactory evidence to NBCUniversal from third parties furnishing services to Supplier for the purpose of enabling or assisting Supplier to perform hereunder.  In addition to, and not as a modification of, any term set forth in this Purchase Order, Supplier agrees that it will:  (i) provide workers a safe and healthy workplace, in full compliance with applicable law; (ii) only employ workers above the applicable minimum age requirement or the age of 16, whichever is higher; (iv) not utilize forced, prison, or indentured labor, or subject workers to any form of compulsion or coercion; (v) allow its workers to freely choose whether or not to organize or join associations for the purpose of collective bargaining as provided by local law; (vi) prohibit physical, sexual or psychological harassment or coercion of its workers; (vii) assure that workers are hired, paid and otherwise subject to terms and conditions of employment based on their ability to do the job, not on the basis of their personal characteristics such as race, national origin, sex, religion, ethnicity, disability, maternity, age, and other characteristics protected by local law (this does not bar compliance with affirmative preferences that may be required by local law); and (vii) require its sub-suppliers to conform to similar standards.\n"+
																				"<br>\n"+
																				"15.   Ownership of Work Product.  The results and proceeds of the Services, together with all ideas, preliminary work, drafts, revisions, versions, polishing, refinements, and all other tangible expressions thereof of whatever kind or nature (hereinafter collectively referred to as the \"Work\") shall be deemed a \"work made for hire\" specially ordered or commissioned by NBCUniversal.  NBCUniversal shall be deemed the author of the Work and shall own all right, title and interest throughout the universe, in perpetuity, in and to said Work, including without limitation the copyright or trademark therein and all renewals or extensions thereof, and the right to use, adapt and change said Work and to prepare derivative works therefrom in any and all media whether now known or hereafter devised.  In addition, NBCUniversal shall have the right, throughout the universe, in perpetuity, to use and reproduce, and license others to use and reproduce, Supplier's name, likeness and biographical data relating to Supplier in connection with the picture(s) as may be set forth in this Purchase Order (hereinafter \"Picture(s)\") and the advertising or exploitation thereof.  Should the Work or any part thereof ever be deemed not a \"work made for hire\", Supplier hereby assigns to NBCUniversal in perpetuity throughout the universe, all right, title and interest, including without limitation the copyright and trademark and all renewals and extensions thereof, all rights under worldwide copyright or trademark laws or treaties, in and to the Work and all components thereof whether heretofore or hereafter created and the right to use, adapt and change said work and to prepare derivative works therefrom in any and all media whether now known or hereafter devised.  Supplier waives all rights of \"droit moral\" or \"moral rights of authors or creators\" and/or any similar rights or principles of law that Supplier may now or hereafter have in the Work.  NBCUniversal shall have exclusive access in perpetuity to any materials derived from the Services performed hereunder.  All employees and subcontractors of Supplier performing Services hereunder (if any) shall assign to NBCUniversal the same rights to their results and proceeds as are set forth in this paragraph.  NBCUniversal is not obligated to use the Services or to produce, distribute or exploit the Picture(s), or if commenced, to continue the production, distribution, exploitation of the Picture(s) in any territory, or to use the Work in any manner whatsoever.  NBCUniversal shall have all approvals and controls with respect to the Picture(s).  If Supplier, pursuant to this Purchase Order, produces Goods whereon NBCUniversal's trademarks and/or copyrighted materials are affixed, then Supplier shall not produce any extras of such Goods over and above the quantity specified in this Purchase Order.  If extra or defective Goods are produced, Supplier shall immediately inform NBCUniversal and, as directed by NBCUniversal, either destroy all such extras and defectives, or ship all such extras and defectives to NBCUniversal at no cost to NBCUniversal.  Supplier shall not otherwise dispose of any extra or defective Goods.  \n"+
																				"<br><br>\n"+
																				"16.   Clearances.   NBCUniversal shall obtain and pay for all consents and approvals required with respect to clearances for all materials furnished by NBCUniversal to Supplier, unless agreed otherwise.  Supplier shall obtain any necessary consents and approvals from all other persons, firms or entities (\"clearances\") required with respect to the performance of the Services (including the delivery of materials, if any) to be provided by Supplier hereunder.  Supplier must advise NBCUniversal in writing of the required clearances and clear according to NBCUniversal's instructions.  With respect to clearances required, Supplier shall act in accordance with understandings reached with, or the instructions of NBCUniversal (or its legal counsel) regarding the matters for which consents are required.  Whenever clearances are required, final payment will be contingent upon submission to NBCUniversal of all required clearances.  Supplier acknowledges and confirms that clearances must encompass use by NBCUniversal in all media throughout the universe in perpetuity unless otherwise approved in writing by NBCUniversal.  \n"+
																				"<br><br>\n"+
																				"17.   Further Instruments.  Supplier shall execute such documents and do such other acts and deeds as may be reasonably required by NBCUniversal or its assignees or licensees to further evidence or effectuate NBCUniversal's rights hereunder, including without limitation the execution and delivery, at no cost to NBCUniversal, of any and all further documents necessary to confirm the ownership by NBCUniversal of the results and proceeds of the Services.  \n"+
																				"<br><br>\n"+
																				"18.   Supplier's Representations and Warranties.   Supplier represents and warrants that (a) it has the right to enter into and fully perform this Purchase Order and to grant the rights granted to agreed to be granted hereunder and will not do or permit any act which will interfere with or derogate from the full performance of the Services or the exercise of NBCUniversal's rights granted herein; (b) there is no outstanding contract or commitment or legal impediment of any kind which conflicts with this Purchase Order, or which may limit, restrict or impair the rights granted to NBCUniversal hereunder; and (c) Supplier is in compliance with all applicable federal, state and local laws and regulations including but not limited to those relating to labor and employment, environmental issues and immigration status of its employees.  Supplier further represents that with respect to the Materials, all of the Materials shall be Supplier's (or Supplier's employee's or subcontractor's) original creation (except for material in the public domain, material furnished by NBCUniversal or other material specifically requested by NBCUniversal which Supplier has informed NBCUniversal is not Supplier's or in the public domain); such Materials do not and will not defame, infringe upon, or violate any rights of any kind, including the right of privacy, of any person; and such Materials are not the subject of any litigation or claim that might give rise to litigation.  If Supplier is a corporation, firm or other entity, Supplier further represents and warrants that: (i) Supplier is in good standing under the laws of the state of its incorporation and is qualified to do business in every jurisdiction where its operations would require it to be so qualified, and has all necessary licenses or permits required to conduct its business and perform its obligations hereunder, copies of which shall be furnished to NBCUniversal upon request; (ii) Supplier has a written contract with the person or persons performing the Services on behalf of Supplier entitling Supplier to furnish such person's or persons' services pursuant to the provisions hereof in compliance with all applicable laws; (iii) Supplier is not now and will not during the full term hereof be in breach of or in default under said contract with such person or persons; and (iv) Supplier will fully discharge all of its obligations to the person or persons performing the Services.  Further, if Supplier is a corporation, firm or other entity and if Supplier or its successors should be dissolved or otherwise cease to exist, or for any reason should fail, refuse or neglect to perform, observe or comply with the terms and conditions of this Purchase Order, the person or persons who are to perform the Services hereunder shall at NBCUniversal's election be deemed to be engaged directly by NBCUniversal for the balance of the term of Supplier's engagement hereunder upon the terms and conditions set forth herein.  \n"+
																				"<br><br>\n"+
																				"19.   Indemnification.  Supplier shall indemnify, defend and hold harmless, NBCUniversal, its parent, subsidiary and affiliated companies, its and their officers, employees and agents (collectively, the \"Indemnified Parties\"), from any and all claims, demands, causes of action, liability, judgments, damages, costs and expenses (including reasonable attorneys' fees) asserted against, imposed upon or suffered by any Indemnified Party arising out of or resulting from performance by Supplier under this Purchase Order or the breach or alleged breach of any warranty, representation, or agreement herein, or any act or omission of Supplier, Supplier's employees, agents or subcontractors in connection with Supplier's performance hereunder, including but not limited to any claims for bodily injury, death or property damage, product liability, and infringement of any proprietary right, patent, copyright or trademark.  The foregoing indemnity shall include injury or death of any of Supplier's employees or any subcontractor's employees and shall not be limited in any way by an amount or type of damage, compensation, or benefits payable under any applicable workers' compensation, disability benefits or their similar employees benefit act.  This indemnity requires indemnification to the Indemnified Parties except to the extent of any Indemnified Party's partial or contributory negligence.  \n"+
																				"<br><br>\n"+
																				"20.   Insurance.   At least 5 days prior to the commencement of any work or the delivery of any materials, supplier shall provide NBCUniversal with a Certificate of Insurance indicating that the following coverages and limits are in full force during the term of this Purchase Order, in addition to the declaration page and the endorsement page of the policy showing NBCUniversal as an additional insured:\n"+
																				"<br>\n"+
																				"a)	Workers Compensation and Employers Liability:  (1) Statutory Workers Compensation (including occupational disease) in accordance with the laws of the State where the work will be performed and including the Other States Endorsement; and (2) Employers Liability Insurance with a limit of at least $1,000,000 each employee, $1,000,000 each accident, $1,000,000 policy limit.\n"+
																				"<br>\n"+
																				"b)	Commercial General Liability (\"CGL\"), on an occurrence basis, with a combined single limit for Bodily Injury, Personal Injury and Property Damage of at least $5,000,000 per occurrence.  The limit may be provided through a combination of primary and umbrella/excess liability policies. Coverage shall include at least the following: (1) Broad Form Property Damage including Completed Operations coverage; (2) Independent Contractors; (3) Blanket Written Contractual Liability covering all Indemnity Agreements; if not written on a blanket basis, it must be endorsed to cover indemnitees specified in the agreement;  (4) Endorsement naming NBCUniversal Media, LLC, its affiliates, and all owners, lessors and lessees of the premises covered by the agreement, as additional insureds and endorsement of specified owners and other additional insureds as may be required from time to time; coverage for the additional insureds shall apply on a primary basis irrespective of any other insurance, whether collectible or not; (5) standard extension without limitation; all exclusions not found within the Commercial General Liability form must be specified; (6) products/completed operations (2 year extension beyond completion of project); and (7) underground explosion and collapse hazards, where applicable.\n"+
																				"<br>\n"+
																				"c)	Commercial Automobile Liability (including all owned, leased, hired, and non-owned automobiles) with a combined single limit for Bodily Injury and Property Damage of at least $1,000,000 per occurrence.  The limit may be provided through a combination of primary and umbrella/excess liability policies.  \n"+
																				"<br>\n"+
																				"d)	Property insurance upon all tools, material and equipment (owned, borrowed or leased by you or your employees) to the full replacement value during the full term of the contract; policy shall cover \"all risk\"; waiver of subrogation favoring all additional insureds.  Supplier's failure to secure and maintain adequate coverage shall not obligate NBCUniversal, its agents or employees for any losses.\n"+
																				"<br>\n"+
																				"e)  	If requested, Professional Liability insurance covering the insured's liability for damages resulting from wrongful acts in the provision of, or failure to provide, professional services, with a limit of not less than $2,000,000 per claim.  If this policy is written on a claims-made basis, the retroactive date must precede the date of this Purchase Order and remain in effect for a period of not less than two (2) years from cancellation of this Purchase Order.\n"+
																				"<br> \n"+
																				"All policies shall be endorsed to require thirty (30) days written notice prior to cancellation, non-renewal or material modification, to be sent to NBCUniversal at address of the NBCUniversal facility where goods or services are to be provided or delivered, attention: Risk Management.  All insurance carriers must (i) be licensed in the State of in which the work is performed, and (ii) be rated at least \"A-X\" in the most current edition of Best's Insurance Reports.  \n"+
																				"<br><br> \n"+
																				"21.  Confidentiality.   Supplier acknowledges that in the performance of the Services, Supplier will have access to (i) trade secrets and confidential or proprietary business information of NBCUniversal or its parent or affiliated companies, which is not generally known and which gives NBCUniversal, its parent and affiliated companies an advantage over their competitors who do not know it, or the contents of any project and/or material handled by Supplier on NBCUniversal's behalf (such information referred to herein as \"NBCUniversal's Confidential Information\").   Supplier agrees to refrain at all times, either during or after the performance of the Services, hereunder, from using or disclosing to others NBCUniversal's Confidential Information except for the benefit of NBCUniversal and further to refrain from any other acts which would decrease the value of NBCUniversal's confidential information.  All NBCUniversal's Confidential Information, including but not limited to, any files, records, documents, drawings, specifications, prints, computer programs, customer lists, training materials, specific customer information, engineering studies, compilations of product research or marketing techniques provided by or relating to NBCUniversal or NBCUniversal's parent or affiliated companies, or coming into Supplier's possession in connection with the performance of its duties hereunder, shall remain the exclusive property of NBCUniversal.  Upon termination of this Purchase Order, Supplier shall return to NBCUniversal promptly any and all documents or items that are the property of NBCUniversal or contain or comprise NBCUniversal's Confidential Information.  This covenant of confidentiality shall survive the termination of this Purchase Order.  Unless NBCUniversal expressly agrees otherwise in writing, no information or knowledge disclosed to NBCUniversal by Supplier in the performance of or in connection with this Purchase Order shall be deemed to be confidential or proprietary, and any such information or knowledge shall be free from any restrictions as part of the consideration for this Purchase Order.  \n"+
																				"<br><br>\n"+
																				"22.  NBCUniversal-Provided Materials.   All materials, tools, plates, artwork, film, drawings, specifications and similar items furnished by NBCUniversal to Supplier or paid for by NBCUniversal pursuant to this Purchase Order are the sole property of NBCUniversal, shall be clearly identified as NBCUniversal property, shall be removable by NBCUniversal at no additional cost,  shall be used only in filling NBCUniversal Agreements, shall be inventoried by Supplier and kept separate from other such materials, shall be disposed of by Supplier as NBCUniversal shall direct, and may not be copied, duplicated or furnished to third parties, except with the prior written consent of NBCUniversal.  Supplier acknowledges that such materials may be protected by NBCUniversal under applicable copyright, patent and trademark laws.  Supplier shall exercise all due care in protecting the security and integrity of materials furnished to Supplier by NBCUniversal.  All such security procedures used by Supplier shall be subject to NBCUniversal's written approval.  In addition, NBCUniversal may, in its sole discretion, require that Supplier establish specific security procedures with respect to specific property and Supplier shall abide by such procedures.  Supplier shall be responsible for loss or damage to any such NBCUniversal property, excepting normal wear and tear, and shall furnish NBCUniversal a written inventory upon request.\n"+
																				"<br><br>\n"+
																				"23.   No Use of NBCUniversal Name or Marks.   Supplier shall have no right to use NBCUniversal's or its affiliates' names, trademarks, service marks, trade names, logos or other identifying information, or issue any news release, advertisement, publicity or promotional material regarding this Purchase Order (including denial or confirmation thereof), except as required to perform its obligations under this Purchase Order, without the prior written consent of NBCUniversal.  \n"+
																				"<br><br>\n"+
																				"24.   No Right to Bind NBCUniversal.   Supplier is not and in no way shall hold itself out as an agent or employee of NBCUniversal.  Supplier acknowledges that nothing in this Purchase Order gives Supplier the right to bind or commit NBCUniversal to any agreements with any third parties and Supplier shall not enter into any agreements with third parties to perform any of the Services without the prior written consent of NBCUniversal.  \n"+
																				"<br><br>\n"+
																				"25.   Independent Contractor.   Supplier is and shall be deemed to be an independent contractor of NBCUniversal and nothing contained herein shall be deemed to constitute a partnership between or a joint venture by the parties hereto, or constitute either party the employee or agent of the other.  Neither party shall hold itself out contrary to the terms of this paragraph and neither party shall become liable for any representations, act or omissions of the other contrary to the provisions hereof.  \n"+
																				"<br><br>\n"+
																				"26.   No Third-Party Beneficiaries.   The agreement between the parties hereto is not for the benefit of any third party and shall not be deemed to give any right or remedy to any such party whether referred to herein or not.  \n"+
																				"<br><br>\n"+
																				"27.   Assignment.   NBCUniversal shall have the right to assign this Purchase Order, and/or all or any part of NBCUniversal's rights hereunder, to any business entity or individual, and the Purchase Order shall be binding upon and inure to the benefit of NBCUniversal's licensees, successors and assigns.  Supplier may not assign the Purchase Order or any of Supplier's rights or obligations hereunder except with the prior written consent of NBCUniversal, which consent may be granted or withheld in NBCUniversal's sole discretion.  Payment to any permitted assignee of Supplier of any amounts hereunder shall be subject to set-off or recoupment for any present or future claim or claims which NBCUniversal may have against Supplier, except to the extent that such claims may be expressly waived in writing by NBCUniversal.  \n"+
																				"<br><br>\n"+
																				"28.   No Waiver.   No waiver by a party hereto of any failure by the other party to keep or perform any covenant or condition of this Purchase Order shall be deemed a waiver of any preceding, succeeding or continuing breach of the same or any other covenant or condition.  \n"+
																				"<br><br>\n"+
																				"29.   Notices.   All notices shall be furnished in writing and if to NBCUniversal, at the address shown on this Purchase Order to the attention of the NBCUniversal contact person designated on this Purchase Order, and if to Supplier, to the name and address of Supplier designated on this Purchase Order.  Notices may be delivered in person, by overnight delivery service, or by certified or registered mail, return receipt requested, and shall be deemed given on the date received (or on the date delivery is refused).  \n"+
																				"<br><br>\n"+
																				"30.   Choice of Law/Dispute Resolution.  This Purchase Order shall be construed in accordance with the laws of New York, unless the address shown for NBCUniversal on the Purchase Order is in California, in which case the laws of California shall apply.  Any dispute arising under this Purchase Order (\"Dispute\") shall be resolved solely according to the procedures set forth in this Section.  The parties shall first negotiate in good faith to resolve the Dispute, including escalation to representatives at least one level higher in each party's organization.  If the Dispute is not so resolved within 30 days, either party may initiate mediation of the Dispute by an active member of the bar of the state whose laws apply to this Purchase Order (NY or CA) (the \"applicable state\"), which bar member shall have substantial experience handling complex business transactions or litigation, and shall be selected by agreement of the parties or appointment by JAMS.  If the parties are unable to resolve the Dispute through mediation, either party may initiate arbitration of the Dispute, which shall be conducted according to the JAMS Comprehensive Arbitration Rules and Procedures (including the Optional Appeal Procedure) by a single neutral arbitrator appointed in accordance with the Rules.  Any appeal shall be heard and decided by a panel of three neutral arbitrators.  The single neutral arbitrator and the members of any Appeal Panel shall be active members of the applicable state bar with substantial experience handling complex business transactions or litigation.  The arbitration shall be conducted in New York County if New York law applies to this Purchase Order, or in Los Angeles County if California law applies, as determined by the first sentence of this Section.\n"+
																				"<br><br>\n"+
																				"31.  Supplementary Documentation/Complete Purchase Order/Modifications.   Any specifications, drawings, notes, instructions, engineering notices, or technical data referred to herein shall be deemed to be incorporated herein by reference as if fully set forth.  In case of any discrepancies or questions refer to NBCUniversal's Sourcing Department for decision, instructions or interpretation.  This Purchase Order, including these Terms and Conditions, contains the full and complete agreement between the parties regarding the subject matter hereof, and supersedes all prior agreements and understandings, whether written or oral, pertaining hereto, unless otherwise specifically mentioned in this Purchase Order.  The execution of this Purchase Order has not been induced by any representations, statements, warranties or agreements other than those set forth herein.  This Purchase Order may only be modified by a writing signed by an authorized agent of Supplier, and an authorized representative of NBCUniversal's Sourcing Department.  \n"+
																				"<br><br>\n"+
																				"32.  Environment; Health & Safety; Other Compliance.  (a)  In addition to, and not as a modification of, any term set forth above, Supplier agrees that it will:  (i) comply with laws and regulations protecting the environment, and will not adversely affect the environment of its local community; (ii) provide workers a safe and healthy workplace, in full compliance with applicable law; (iii) maintain and enforce a company policy requiring adherence to ethical business practices, including a prohibition on bribery of government officials; (iv) comply with all laws regarding intellectual property of others; (v) maintain security measures consistent with international standards for the protection of its operations and facilities against exploitation by criminal or terrorist individuals and organizations; and (vi) require its sub-suppliers to conform to similar standards.\n"+
																				"<br>\n"+
																				"(b)  Supplier represents, warrants and covenants that each chemical substance contained in Goods sold hereunder (i) is on the list of chemical substances compiled and published by (1) the Administrator of the US Environmental Protection Agency pursuant to the Toxic Substances Control Act (15 USC Section 2601 et. seq.), as amended; and (2) the equivalent lists in the other jurisdictions to which the Goods will be shipped to or through, or is exempt from the foregoing lists (and Supplier will provide reasonable proof of such exemption); (ii) is manufactured, imported, used and supplied in full compliance with the provisions of Regulation (EC) No. 1907/2006 (\"REACH\"), and (iii) is pre-registered or registered, if required, under REACH, is not restricted under Annex VII of REACH, and if subject to Authorization under REACH Annex XIV is authorized for NBCUniversal's use.  Supplier will provide NBCUniversal with reasonable supporting documentation of the foregoing.  \n"+
																				"<br>\n"+
																				"(c)  Supplier will immediately notify NBCUniversal if any of the Goods are manufactured by Supplier with, or contain, a substance officially proposed for listing on the list of substances meeting the criteria for authorization under REACH, and Supplier shall provide NBCUniversal with sufficient information to allow NBCUniversal to safely use the Goods and/or fulfill its obligations under REACH. \n"+
																				"<br>\n"+
																				"(d)  Supplier represents, warrants and covenants that none of the Goods contain any (i) lead, mercury, cadmium, hexavalent chromium, polybrominated biphenyls, polybrominated diphenyl ethers or any other hazardous substance the use of which is restricted under EU Directive 2002/95/EC (\"RoHS Directive\"), as amended; (ii) arsenic, asbestos, benzene, polychlorinated biphenyls or carbon tetrachloride; (iii) any chemical restricted under the Montreal Protocol on ozone-depleting substances; (iv) any substance listed on the REACH candidate list, subject to authorization and listed on Annex XIV of REACH, or restricted under Directive 76/769/EEC and/or Annex XVII of REACH; (v) any engineered nanoscale materials, or (vi) any other chemical or hazardous material the use of which is restricted in other jurisdictions to which or through which the Goods are likely to be shipped, unless NBCUniversal otherwise expressly agrees in writing.  Upon request from NBCUniversal, Supplier shall provide NBCUniversal with information regarding the chemical composition of any Goods.\n"+
																				"<br>\n"+
																				"(e)  Supplier represents and warrants that, except as expressly listed in writing to NBCUniversal, none of the Goods are (i) subject to electrical or electronic reuse or recycling take-back requirements pursuant to applicable law, or (ii) contain batteries or accumulators or are batteries or accumulators subject to labeling or take-back requirements pursuant to applicable law.  For any Goods covered by a take-back obligation (including the WEEE Directive), Supplier agrees to assume, for no additional consideration, responsibility for taking back such Goods upon request from NBCUniversal and handling them in accordance with applicable law.  \n"+
																				"<br>\n"+
																				"(f)  Supplier represents and warrants that it has developed and implemented security procedures in accordance with (i) the recommendations of the US Customs Service under the provisions of the Customs-Trade Partnership Against Terrorism (\"C-TPAT\") for Supplier's type of business, and (ii) the requirements or recommendations of the EU Authorized Economic Operator (\"AEO\") program. Supplier shall communicate C-TPAT or other relevant security recommendations to its sub-suppliers and transportation providers, and (ii) upon request of NBCUniversal, provide a written copy of its security procedures.  Supplier shall hold NBCUniversal harmless and reimburse NBCUniversal for all costs, losses, claims or penalties charged to or imposed upon NBCUniversal by reason of Supplier's (or any sub-supplier's) non-compliance with C-TPAT, AEO or any other relevant program.  \n"+
																				"<br>\n"+
																				"(g)  With respect to any Goods, Supplier will provide upon request (i) all applicable safety data sheets, and (ii) mandated labeling information, as required by applicable laws, rules and regulations. \n"+
																				"<br>\n"+
																				"(h)  Supplier shall permit NBCUniversal or its representatives to have reasonable access to the sites where the work under this Purchase Order is performed, or where the Goods are manufactured, in order to assess (i) work quality, (ii) conformance with NBCUniversal's specifications, and (iii) conformance with the terms of this Purchase Order.  \n"+                                                   
																				"</div>\n"+
					                                                "</div>\n"+
					                                            "</div>\n");					                           
					                                          }
				                                        	 retCT_ZTERMS.nextRow();
				                                          }
				                                          retCT_ZTERMS.firstRow();
				                                          response.write("<div class=\"hidden-form-elements\">\n");
				                                            for(int i = 0; i < retCT_ZTERMS.getNumRows() ; i++) {
				                                            	if (retCT_ZTERMS.getString("TEXT1").length()>0){
						                                        	if (retCT_ZTERMS.getString("ZTERM").equalsIgnoreCase(terms)){				                                            	
						                                        		response.write("<input type=\"radio\" required name=\"terms\" class=\""+retCT_ZTERMS.getString("ZTERM")+"\" value=\""+retCT_ZTERMS.getString("ZTERM")+"\" checked />\n");
						                                        	} else {
						                                        		response.write("<input type=\"radio\" required name=\"terms\" class=\""+retCT_ZTERMS.getString("ZTERM")+"\" value=\""+retCT_ZTERMS.getString("ZTERM")+"\" />\n");					                                        		
						                                        	}
				                                            	}
				                                            	
						                                        retCT_ZTERMS.nextRow();
				                                            };
				                                            response.write("</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    /* Comments added by AGAMPA to hide Receipient type in Terms tab
				                                    "<div class=\"row-fluid\">\n"+
	                                                    "<div class=\"control-group span12\">\n"+
	                                                        "<label class=\"control-label\">\n"+
	                                                            "Recipient Type\n"+
	                                                        "</label>\n"+
	                                                        "<div class=\"controls\">\n"+
	                                                            "<select name=\"recepientType\" name=\"recipientType\" required=\"required\" class=\"input-block-level\">\n"+
	                                                            "<!-- "+taxRecipientType+"-->\n"+  
	                                                            "<option value=\"\">Select One</option>\n");
	    															for (int x = 0; x < arrayTaxRecipientType.length; x++) {
																			if (arrayTaxRecipientType[x][0].equalsIgnoreCase(taxRecipientType)){
																				response.write("<option value=\""+arrayTaxRecipientType[x][0]+" \" selected>"+arrayTaxRecipientType[x][1]+"</option>");
																			} else {
																				response.write("<option value=\""+arrayTaxRecipientType[x][0]+"\">"+arrayTaxRecipientType[x][1]+"</option>");
																			}																				
																	}                                               
	      				                                        response.write("</select>\n"+
	                                                        "</div>\n"+
	                                                    "</div>\n"+
	                                                "</div>\n"+*/
				                                    "<div class=\"form-actions\">\n");
				                                    	// Begin of Comment and Insert by Naga ENHC0013683
				                                        //"<a class=\"btn back\" href=\"#tab2\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                    	
				                                    	if ( vendorType.equalsIgnoreCase("092")){
				                                    		response.write("<a class=\"btn back\" href=\"#tab1\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n");
				                                    	}else{
				                                    		response.write("<a class=\"btn back\" href=\"#tab2\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n");
				                                    	}
				                                    		response.write(
				                                        // End of Comment and Insert by Naga 
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<a class=\"btn btn-success continue\" href=\"#tab4\">Continue <i class=\"icon-angle-right\"></i></a>\n"+
				                                      //Req#51 START Code change by AGAMPA
				                                   //     "<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                            "<div class=\"tab-pane fade\" id=\"tab4\">\n"+
				                                "<div class=\"form\">\n"+
				                                    "<div class=\"container-fluid\">\n"+
				                                        "<div id=\"account-list-container\">\n"+
				                                         "<div id=\"all-accounts\" >\n"+
				                                            "<div id=\"primaryAccount\" >\n"+
				                                                "<div class=\"pseudo-legend accordion-heading\">\n"+
				                                                    "Primary Account\n"+
				                                                "<div class=\"btn-group\">\n"+
				                                                    "<div></div>\n");
				                                            
				                                            	String selectedValue = "";
				                                            	
				                                				response.write("<input type=\"hidden\" id=\"hidden-banking\" name=\"hidden-banking\"  value=\""+ tempbankingPrimaryBankAccount+ "\">\n"); //ganesh

				                                            	// Begin of comment by Naga ENHC0013683
				                                            	// For Vendor types Refund / Reimbursement ( 060 ) and Garnishment ( 092 ) Check should be defaulted
				                                            	// banking Primary Country cannot be initialized
				                                            	/*if(bankingPrimaryCountry ==null || bankingPrimaryCountry.length()<=0)
				                                            	{
				                                            		bankingPrimaryCountry = "US";
				                                            		selectedValue = "ACH";
				                                            	}*/
				                                            	// End of comment by Naga
				                                            
				                                            // Modified by CGUTJAHR : 1/15/15 : Enhancement #41	
				                                            		response.write("<!-- retCT_LFBK : "+retCT_LFBK+"-->+\n bankingPrimaryCountry "+bankingPrimaryCountry+" vendorType "+vendorType);
				                                                    // ENHC0013683 - Addition of Garnishment vendor type ( 092 )
				                                            		// Garnishment Vendor type should have Check Option
				                                            		// ENHC0016458 & ENHC0016461
				                                            		// Check has to be defaulted for Vendor type Government ( 050 ) , Political( 040 ), Utility ( 080 )
				                                            		// Legal Settlement (093 ) and Contest Winner ( 094 ).

				                                            		if (vendorType.contains("060") || 
				                                            			vendorType.contains("092") ||
				                                            			vendorType.contains("050") ||  
				                                            			vendorType.contains("040") ||
				                                            			vendorType.contains("080") ||
				                                            			vendorType.contains("093") ||
				                                            			vendorType.contains("094"))
				                                                    {
				                                                    	/** Comments added by AGAMPA for Issue # 57. If condition reversed to support for Vendor Status = Old Version 
				                                                    	if ( retCT_LFBK.getNumRows() == 0){
				                                                    		selectedValue = "CHECK";
				                                                    	} else {
					                                                    	if ((bankingPrimaryCountry.equals("US"))) {			                                                    		
					                                                    		selectedValue = "ACH";	
					                                                    	}
				                                                    	}Issue #57 code comments completes here*/
				                                                    	
				                                                    	//Issue 57 code changes starts here by AGAMPA
				                                                    	if ( retCT_LFBK.getNumRows() == 0)
				                                                    	{
				                                                    		if(vendorType.contains("080")){
				                                                    			bankingPrimaryCountry = "US";
							                                            		selectedValue = "ACH";
				                                                    		}else{
				                                                    			selectedValue = "CHECK";
				                                                    		}
				                                                    	}else if(bankingPrimaryCountry==null||bankingPrimaryCountry=="") { 
				                                                    		// ENHC0016461 Vendor types with checks and secondary account allowed, if primary bank type is not entered then it has to be defaulted to check
				                                                    		// I think its safe to make it check for all vendor types which make it here
				                                                    		selectedValue = "CHECK";
				                                                    		
				                                                    	}else if ((bankingPrimaryCountry.equals("US"))) 
				                                                    	{			                                                    		
				                                                    		selectedValue = "ACH";	
				                                                    	}else{
				                                                    		
//				                                                    		if(vendorType.contains("040") ||
//				                                                    		   vendorType.contains("080"))
				                                                    		if(vendorType.contains("050"))
				                                                    			selectedValue = "Wire";				// Only Government has Wire along with Check
				                                                    	}

				                                                    	//Issue # 57 code changes ends here.	

					                                            		response.write("<a class=\"btn btn-info dropdown-toggle "+disableButton+"\" data-toggle=\"dropdown\" href=\"#\">\n"+
					                                                        "<label class=\"type-text\">"+selectedValue+"</label>\n");
					                                            			if(!vendorType.contains("092")){		// ENHC0013683 by Naga. Only check for Garnishment
					                                            				// Begin of comment by Naga ENHC0016458 & ENHC0016461
					                                            				// Caret is always displayed as there more than one option in these cases
//						                                            			if (selectedValue.equalsIgnoreCase("wire"))
//						                                            				response.write("<span class=\"caret\" style=\"display: none;\"></span>\n");
//						                                            			else 
						                                            			// End of comment by Naga	
						                                            			 response.write("<span class=\"caret\" style=\"display: inline-block;\"></span>\n");
						                                                    	 response.write("</a>\n"+					                                                    	 
						                                            			"<ul class=\"dropdown-menu\">\n"+
						                                                        "<li>\n");
						                                                    	/* if ((bankingPrimaryCountry.equals("US"))) {		
						                                                            response.write("<a class=\"typeOption\">ACH</a>\n");
						                                                    	 } else {
						                                                    		 response.write("<a class=\"typeOption\">Wire</a>\n");	 
						                                                    	 }*/
						                                                    	response.write("<a class=\"typeOption\">ACH</a>\n");
						                                                	
						                                                        response.write("</li>\n"+
						                                                        "<li>\n"+
						                                                            "<a class=\"typeOption\">CHECK</a>\n"+
						                                                        "</li>\n");
						                                                        // Begin of Insert by Naga ENHC0016458
						                                                        // Wire should also be displayed for Political and Utility -- This is removed
						                                                        // Wire should be displayed for Government
//						                                                        if(vendorType.contains("040")|| vendorType.contains("080")){
						                                                        if(vendorType.contains("050")){						                                                        
								                                                    response.write(
								                                                    "<li>\n"+
							                                                            "<a class=\"typeOption\">Wire</a>\n"+
							                                                        "</li>\n");						                                                        	
						                                                        }
						                                                        // End of Insert
						                                                        response.write(
						                                                        "</ul>");	
					                                            			} else {									// ENHC0013683 by Naga. Only check for Garnishment
					                                            				response.write("</a>\n");
					                                            			}
				                                                    } 
			                                                    	// Begin of Insert by Naga ENHC0013668
			                                                    	// All Vendors with WIRE should be able to select Wire within US
				                                            		else{
				                                                    	if(bankingPrimaryCountry ==null || bankingPrimaryCountry.length()<=0)
						                                            	{
						                                            		bankingPrimaryCountry = "US";
						                                            		//Before change - Pranesh(31-03-2016) - ENHC0016459
						                                            		//selectedValue = "ACH";
						                                            		
						                                            		//Begin Of Insert by Pranesh(31-03-2016) - ENHC0016459(Made CHECK as default value for Charitable & Posthumous on Primary Account - Banking Tab)
						                                            		if( (vendorType.contains("095")) || (vendorType.contains("018")) || (vendorType.contains("030")) ){
					                                            				selectedValue = "CHECK";	
					                                            			}else{
					                                            				selectedValue = "ACH";
					                                            			}
						                                            		//End Of Insert by Pranesh(31-03-2016) - ENHC0016459

						                                            	}else{
						                                            		if(bankingPrimaryAccountType.contains("W")){
						                                            			selectedValue = "Wire";
						                                            		}else if(bankingPrimaryAccountType.contains("U")){
						                                            			selectedValue = "Wire";
						                                            		}else{
						                                            			selectedValue = "ACH";	
						                                            		}
						                                            	}
				                                                    	
					                                            		response.write("" +
					                                            			"<a class=\"btn btn-info dropdown-toggle "+disableButton+" \" data-toggle=\"dropdown\" href=\"#\">\n"+
					                                                        	"<label class=\"type-text\">"+selectedValue+"</label>\n"+
					                                                        	"<span class=\"caret\" style=\"display: inline-block;\"></span>\n"+
					                                            			"</a>\n"+
					                                            			/*
					                                            			//before change - Pranesh - 01-04-2016 - ENHC0016459
					                                            			"<ul class=\"dropdown-menu\">\n"+
						                                            			"<li>\n"+
						                                            				"<a class=\"typeOption\">ACH</a>\n"+
						                                            			"</li>\n"+
						                                            			"<li>\n"+
						                                            				"<a class=\"typeOption\">Wire</a>\n"+
						                                            			"</li>\n"+
					                                            			"</ul>");
					                                            			*/
					                                            			
					                                            		//After change - Pranesh(01-04-2016) - ENHC0016459
					                                            		//Begin Of Insert by Pranesh(01-04-2016) - ENHC0016459
					                                            		"<ul class=\"dropdown-menu\">\n");
				                                            			
				                                            			if((decisionVendorType.equalsIgnoreCase("R095")) || (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R030"))){
				                                            				response.write(
				                                            					"<li>\n"+
							                                            			"<a class=\"typeOption\">CHECK</a>\n"+
							                                            		"</li>\n"+
							                                            		"<li>\n"+
				                                            				    	"<a class=\"typeOption\">ACH</a>\n"+
				                                            				    "</li>\n"+
				                                            				    "<li>\n"+
				                                            			     		"<a class=\"typeOption\">Wire</a>\n"+
				                                            			     	"</li>\n"+
				                                            			     	"</ul>");
				                                            			   }else{
				                                            				 response.write(
				                                            				     "<li>\n"+
					                                            				    "<a class=\"typeOption\">ACH</a>\n"+
					                                            			     "</li>\n"+
					                                            			     "<li>\n"+
					                                            			     	"<a class=\"typeOption\">Wire</a>\n"+
					                                            			     "</li>\n"+
				                                            				 "</ul>");				
				                                            			   }
				                                            			
				                                            			//End Of Insert by Pranesh(01-04-2016) - ENHC0016459
				                                            			
				                                            		}
			                                                    	// End of Insert by Naga
				                                            		// Begin of comment by Naga ENHC0013668
//				                                            		else {
//				                                                    	// Begin of Insert by Naga ENHC0013683 
//				                                                    	// Moving this logic here which was before if
//				                                                    	if(bankingPrimaryCountry ==null || bankingPrimaryCountry.length()<=0)
//						                                            	{
//						                                            		bankingPrimaryCountry = "US";
//						                                            		selectedValue = "ACH";
//						                                            	}				                                                    	
//				                                                    	// End of Insert by Naga
//
//				                                                    	
//					                                            		response.write("<a class=\"btn btn-info dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">\n"+
//					                                                        "<label class=\"type-text\">"+((bankingPrimaryCountry.equals("US"))?"ACH":"Wire")+"</label>\n"+
//					                                                    "</a>\n");
//				                                                    }
				                                            		// End of comment by Naga
				                                                    // END
				                                                    response.write("<input type=\"hidden\" class=\"type\" name=\""+((bankingPrimaryCountry.equals("US"))?"ACH":"Wire")+"\" value=\"ACH\" />\n"+
				                                                    		"<input type=\"hidden\" class=\"type\" name=\"primaryBankingType\" id=\"primaryBankingType\" value=\""+selectedValue+"\">"+
				                                                "</div>\n"+
				                                                "</div>\n");
				                                            // Modified by CGUTJAHR : 1/15/15 : Enhancement #41
				                                             if ( (selectedValue.equalsIgnoreCase("Check")) ) {
				                                                response.write("<div class=\"address primary-item\" id=\"primary-account\" style=\"display: none;\">\n");
				                                             } else {
				                                            	response.write("<div class=\"address primary-item\" id=\"primary-account\">\n");	 
				                                             }
				                                         // END    		
				                                                    response.write("<div class=\"accordion-group\">\n"+
				                                                        "<div class=\"accordion-body collapse in\">\n"+
				                                                            "<div class=\"row-fluid\">\n"
				                                                                );
				                                                            // TODO : Primary Account
				                                                            
				                                                    		// Get last entry
				                                                    		//retCT_LFBK.lastRow();
				                                                    		
				                                                    		//if ( retCT_LFBK.getNumRows() > 0 )//Commented by AGAMPA as default country is selected to US. There is no need for this condition.
				                                                    		{
					                                                    		
				                                                    		response.write("<div class=\"span6\">\n"+
				                                                                    "<select class=\"input-block-level country\" required=\"required\" id=\"primary-account-country\" name=\"primary-account-country\">\n"+
				                                                                    "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
																						for (int x = 0; x < arrayCountryCode.length; x++) {
																							response.write("<option value=\""+arrayCountryCode[x][0]+"\""+(arrayCountryCode[x][0].equals(bankingPrimaryCountry)?"selected=\"selected\"":"")+">"+arrayCountryCode[x][1]+"</option>");																			
																						}
											                                    		response.write("</select>\n"+
				                                                                        //"<option value=\"US\">United States</option>\n"+
				                                                                        //"<option value=\"CA\">Canada</option>\n"+
				                                                                        //"<option value=\"MX\">Mexico</option>\n"+
				                                                                        //"<option value=\"AU\">Australia</option>\n"+
				                                                                        //"<option value=\"NZ\">New Zealand</option>\n"+
				                                                                        //"<option>Other</option>\n"+
				                                                                    //"</select>\n"+
				                                                                "</div>\n"+
				                                                            "</div>\n"+
				                                                            "<div class=\"row-fluid account-type\" id=\"bankRoutingAndAccountNumberArea\">\n");
				                                                    		
																				//bankingPrimaryRoutingBSB = retCT_LFBK.getString("BANKL");
																				//bankingPrimaryBankAccount = retCT_LFBK.getString("BANKN");
																				//bankingPrimarySWIFTAcocunt = retCT_LFBK.getString("BANKL");
																				//bankingPrimaryHolderName = retCT_LFBK.getString("KOINH");
																				//bankingPrimaryIBAN = "";

											                                    if (bankingPrimaryCountry.equalsIgnoreCase("US")) {
																			        response.write("<div class=\"row-fluid\">\n"+
																			            "<div class=\"span4\">\n");
																			            // TEST marker-1 for "ACH" - Pranesh(04/17/2016)
																			            response.write("<!-- Check ACH - 1 "+bankingRequiredDisplay+"-->");
																			            response.write("<!-- Check ACH - 2 "+checkAllowed+"-->");
																			            response.write("<div class=\"control-group\">\n"+
																			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Routing #</label>\n"+
																			                    "<div class=\"controls\">\n");
																			                     if (checkAllowed){
																			                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing. #\" value=\""+bankingPrimaryRoutingBSB+"\">\n");
																			                     } else {
																			                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n");			                    	 
																			                     }
																			                       response.write("</div>\n"+
																			                "</div>\n"+
																			            "</div>\n"+
																			            "<div class=\"span4\">\n"+
																			                "<div class=\"control-group\">\n"+
																			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
																			                    "<div class=\"controls\">\n");
																			                     if (checkAllowed){				                       
																			                        response.write("<input class=\"input-block-level header-input\" maxlength=\"17\" type=\"text\" id=\"banking-primary-AccountNum\" name=\"banking-primary-AccountNum\" placeholder=\"max 17 for US banks\" value=\""+bankingPrimaryBankAccount+"\">\n");//  maxlength=\"17\" added ganesh DFCT0017546
																			                     } else {
																			                        response.write("<input class=\"input-block-level header-input\"   maxlength=\"17\" required type=\"text\" id=\"banking-primary-AccountNum\" name=\"banking-primary-AccountNum\" placeholder=\"max 17 for US banks\" value=\""+bankingPrimaryBankAccount+"\">\n");	//  maxlength=\"17\" added ganesh DFCT0017546			                    	 
																			                     }
																			                response.write("</div>\n"+
																			                "</div>\n"+
																			            "</div>\n"+
																			            "<div class=\"span4\">\n"+
																			                "<div class=\"control-group\">\n"+
																			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Account Holder Name</label>\n"+
																			                    "<div class=\"controls\">\n");
																			                     if (checkAllowed){				                       
																			                        response.write("<input class=\"input-block-level holder-name\" type=\"text\" name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n");
																			                     } else {
																			                        response.write("<input class=\"input-block-level holder-name user-error\" required type=\"text\" name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n");				                    	 
																			                     }
																			                response.write("</div>\n"+
																			                "</div>\n"+
																			            "</div>\n"+
																			        //"</div>\n"+
																			       "</div>\n");
																				if((bankingPrimaryRoutingBSB.equals("") || bankingPrimaryBankAccount.equals("") || bankingPrimaryHolderName.equals("")))
																				response.write(
																			       "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
																					"\n");
																				else
																					response.write(
																			       	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
																					"\n");	
																				response.write(
																			       	"</div>\n"+
																					"\n");				                                                            
																			} else if ((bankingPrimaryCountry.equalsIgnoreCase("CA") || bankingPrimaryCountry.equalsIgnoreCase("MX"))){
																				showDifferentCurrencyMessage = true;
																				response.write("<div class=\"row-fluid\">\n"+
																		            "<div class=\"span4\">\n"+
																		               "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\">Bank Key</label>\n"+
																		                    "<div class=\"controls\">\n");
																			                     if (checkAllowed){
																			                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n");
																			                     } else {
																			                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n");			                    	 
																			                     }


																			                       response.write(
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
																		                    "<div class=\"controls\">\n");
																			                     if (checkAllowed){				                       
																			                    	 response.write("<input class=\"input-block-level header-input\" id=\"banking-primary-AccountNum\" type=\"text\" name=\"banking-primary-AccountNum\" placeholder=\"Bank Account #\" value=\""+ bankingPrimaryBankAccount+ "\">\n");// added id tag-ganesh	//  maxlength=\"17\" added ganesh DFCT0017546																                     
																			                    	 } else {
																			                        response.write("<input class=\"input-block-level header-input\" id=\"banking-primary-AccountNum\" required type=\"text\" name=\"banking-primary-AccountNum\" placeholder=\"Bank Account #\" value=\""+bankingPrimaryBankAccount+"\">\n");		 // id tag added ganesh		                    	 
																			                     }
																			                response.write("</div>\n"+
																		                "</div>\n"+
																		           	"</div>\n"+
																		           	 "<div class=\"span4\">\n"+
																		           	 	// Begin of Comment and Insert by Naga ENHC0013668
//																		                "<div class=\"control-group\">\n"+
//																	                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//																	                    "<div class=\"controls\">\n"+
//																	                        "<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
//																	                    "</div>\n"+
//																	                "</div>\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
																		                    "<div class=\"controls\">\n");
																		                    if (checkAllowed){
																		                    	response.write("<input class=\"input-block-level holder-name\" type=\"text\" name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n");
																		                    }else{
																		                    	response.write("<input class=\"input-block-level holder-name\" required type=\"text\" name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n");
																		                    }
																		                        
																		                    response.write("</div>\n"+
																		                "</div>\n"+																		           	 
																		           	 	// End of Comment and Insert by Naga
																		            "</div>\n"+
																		        "</div>\n");

																				if((bankingPrimaryRoutingBSB.equals("") || bankingPrimaryBankAccount.equals("")))
																					response.write(
																			        	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"
																					);	
																				else
																					response.write(
																			       	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
																					"\n");	
																			    // Begin of Insert by Naga ENHC0013668
																				response.write("<div class=\"row-fluid\">\n"+
																			           	 "<div class=\"span4\">\n"+
																			                "<div class=\"control-group\">\n"+
																			                	"<label class=\"control-label\">SWIFT Account #</label>\n"+
																			                	"<div class=\"controls\">\n"+
																		                        	"<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
																		                        "</div>\n"+
																		                    "</div>\n"+
																			            "</div>\n"+
																			        "</div>\n");																			                
																				    // End of Insert by Naga																				
																				response.write(
																			       	"</div>\n"+
																					"\n");	
																			} else if (bankingPrimaryCountry.equalsIgnoreCase("NZ") || bankingPrimaryCountry.equalsIgnoreCase("AU")) {
																				showDifferentCurrencyMessage = true;
																				response.write("<div class=\"row-fluid\">\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> BSB #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n"+
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level header-input\" type=\"text\" id=\"banking-primary-AccountNum\" name=\"banking-primary-AccountNum\" placeholder=\"Bank Account #\" value=\""+bankingPrimaryBankAccount+"\">\n"+ // id tag added ganesh
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		         // Begin of Comment and Insert by Naga ENHC0013668
//																		                "<div class=\"control-group\">\n"+
//																		                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//																		                    "<div class=\"controls\">\n"+
//																		                        "<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
//																		                    "</div>\n"+
//																		                "</div>\n"+
																		                "<div class=\"control-group\">\n"+
																	                    	"<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
																	                    	"<div class=\"controls\">\n"+
																	                        	"<input class=\"input-block-level holder-name\" type=\"text\" required name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n"+
																	                        "</div>\n"+
																	                    "</div>\n"+																		            
																		             // End of Comment and Insert by Naga																		                
																		            "</div>\n"+
																		        "</div>\n");																				
																				if((bankingPrimaryRoutingBSB.equals("") || bankingPrimaryBankAccount.equals("")))
																				response.write(
																		        	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"
																		        );
																				else
																					response.write(
																			       	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
																				"\n");
																				response.write(
																				"<div class=\"row-fluid\">\n"+
																			    // Begin of Insert by Naga ENHC0013668
																		           	 "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                	"<label class=\"control-label\">SWIFT Account #</label>\n"+
																		                	"<div class=\"controls\">\n"+
																	                        	"<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
																	                        "</div>\n"+
																	                    "</div>\n"+
																		            "</div>\n"+
																				    // End of Insert by Naga																				
																		            "<div class=\"span6\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                	// SWIFT and IBAN are optional always ENHC0013668
//																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> IBAN #</label>\n"+ // ENHC0013668
																		                	"<label class=\"control-label\"> IBAN #</label>\n"+ // ENHC0013668
																		                    "<div class=\"controls\">\n");
																							// Begin of Comment and Insert by Naga 
																							// SWIFT and IBAN are optional always ENHC0013668
//																		                    if ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
//																			                        response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
//																			                     } else {
//																		                       		response.write("<input class=\"input-block-level\" type=\"text\" required name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
//																			                     }
																		                   response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
																		                    // End of Comment and Insert by Naga
																		                   response.write("</div>\n"+
																		                "</div>\n"+
																		           "</div>\n"+
																		        "</div>\n");
																		        response.write(
																			       	"</div>\n"+
																					"\n");
																			}else
																			{
																				showDifferentCurrencyMessage = true;
																				
																				
		                                                           				response.write("<div class=\"row-fluid\">\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> BSB #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n"+
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\">  Bank Account #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level header-input\" required type=\"text\" id=\"banking-primary-AccountNum\" name=\"banking-primary-AccountNum\" placeholder=\"Bank Account #\" value=\""+bankingPrimaryBankAccount+"\">\n"+ // id tag added ganesh
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		            	// Begin of Comment and Insert by Naga ENHC0013668
//																		                "<div class=\"control-group\">\n"+
//																		                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//																		                    "<div class=\"controls\">\n"+
//																		                        "<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
//																		                    "</div>\n"+
//																		                "</div>\n"+
																	                	"<div class=\"control-group\">\n"+
																	                		"<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
																	                			"<div class=\"controls\">\n"+
																	                				"<input class=\"input-block-level holder-name\" required type=\"text\" name=\"banking-primary-HolderName\" placeholder=\"Account Holder Name\" value=\""+bankingPrimaryHolderName+"\">\n"+
																	                			"</div>\n"+
																	                	"</div>\n"+																		           	 
																	                	// End of Comment and Insert by Naga																		            
																		            "</div>\n"+
																		        "</div>\n");
																				if((bankingPrimaryRoutingBSB.equals("") || bankingPrimaryBankAccount.equals("")))
																				response.write(
																		        	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"
																		        );
																				else
																					
																					response.write(
																			       	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
																				"\n");
																				response.write(
																				"<div class=\"row-fluid\">\n"+
																					// Begin of Insert by Naga ENHC0013668
																		           	 "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                	"<label class=\"control-label\">SWIFT Account #</label>\n"+
																		                	"<div class=\"controls\">\n"+
																	                        	"<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
																	                        "</div>\n"+
																	                    "</div>\n"+
																		            "</div>\n"+
																		            // End of Insert by Naga																				
																		            "<div class=\"span6\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                	// SWIFT and IBAN are optional always ENHC0013668
//																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> IBAN #</label>\n"+ // ENHC0013668
																		                    "<label class=\"control-label \"> IBAN #</label>\n"+ // ENHC0013668
																		                    "<div class=\"controls\">\n");
																							// Begin of Comment and Insert by Naga ENHC0013668				
																							// SWIFT and IBAN are optional always ENHC0013668
																				
//																		                    if ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
//																			                        response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
//																			                     } else {
//																		                       		response.write("<input class=\"input-block-level\" type=\"text\" required name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
//																			                     }
																							response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
																							// End of Comment and Insert by Naga																		                    
																		                   response.write("</div>\n"+
																		                "</div>\n"+
																		           "</div>\n"+
																		        "</div>\n"+
																		    "</div>\n");
																			}
																		} 
				                                                    		/**Code commented by AGAMPA on 4-Mar-15. This else condition does not have all country specific logics.
				                                                    	else
				                                                    	{
																			showDifferentCurrencyMessage = true;

																			response.write("<div class=\"span6\">\n"+
		                                                                    "<select class=\"input-block-level country\" required=\"required\" name=\"primary-account-country\">\n"+
																			"<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
																				for (int x = 0; x < arrayCountryCode.length; x++) {
																					response.write("<option value=\""+arrayCountryCode[x][0]+"\""+(arrayCountryCode[x][0].equals(arrayCountryCode[0][0])?"selected=\"selected\"":"")+">"+arrayCountryCode[x][1]+"</option>");																			
																				}
									                                    		response.write("</select>\n"+
		                                                                "</div>\n"+
		                                                            "</div>\n" +
				                                                    "<div class=\"row-fluid account-type\" id=\"bankRoutingAndAccountNumberArea\">\n");

		                                                           				response.write("<div class=\"row-fluid\">\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> BSB #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-primary-RoutingNum\" placeholder=\"Bank Routing #\" value=\""+bankingPrimaryRoutingBSB+"\">\n"+
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level header-input\" type=\"text\" id=\"banking-primary-AccountNum\" name=\"banking-primary-AccountNum\" placeholder=\"Bank Account #\" value=\""+bankingPrimaryBankAccount+"\">\n"+ 
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		            "<div class=\"span4\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
																		                    "<div class=\"controls\">\n"+
																		                        "<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
																		                    "</div>\n"+
																		                "</div>\n"+
																		            "</div>\n"+
																		        "</div>\n");
																				if((bankingPrimaryRoutingBSB.equals("") || bankingPrimaryBankAccount.equals("")))
																				response.write(
																		        	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"
																		        );
																				else
																					response.write(
																			       	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
																				"\n");
																				response.write(
																				"<div class=\"row-fluid\">\n"+
																		            "<div class=\"span6\">\n"+
																		                "<div class=\"control-group\">\n"+
																		                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> IBAN #</label>\n"+
																		                    "<div class=\"controls\">\n");
																		                    if ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
																			                        response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
																			                     } else {
																		                       		response.write("<input class=\"input-block-level\" type=\"text\" required name=\"banking-primary-IbanNum\" placeholder=\"IBAN #\" value=\""+bankingPrimaryIBAN+"\">\n");
																			                     }
																		                   response.write("</div>\n"+
																		           "</div>\n"+
																		        "</div>\n" +
																		        "</div>\n"+
																		        "</div>\n");
																			}*/
 																			if (showDifferentCurrencyMessage) {
				                                                            	response.write("<div class=\"row-fluid accept-usd\">\n");				                                                            	
				                                                            } else {
				                                                            	response.write("<div class=\"row-fluid accept-usd\" style=\"display: none;\">\n");				                                                            
				                                                            }
				                                                            
				                                                            response.write("<div class=\"control-group span12 currency\">\n"+
				                                                                    "<label class=\"control-label\">\n"+
				                                                                        "Payment Currency different from Bank Country&apos;s Currency?\n"+
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n"+
				                                                                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                                                            "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                                                            "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                                                                        "</div>\n"+
				                                                                        "<div class=\"add-on currency-no\">\n"+
				                                                                            "<label>Intermediary Bank</label>\n"+
				                                                                            "<div class=\"int-container\">\n"+
				                                                                                "<!--<i class=\"icon-caret-up\"></i>-->\n"+
				                                                                                "<div class=\"i-bank-item row-fluid\">\n"+
				                                                                                    "<div class=\"accordion ibank-accordion\" id=\"ibank-accordion\">\n"+
				                                                                                        "<div class=\"accordion-group\">\n"+
				                                                                                            "<div class=\"accordion-heading active\">\n"+
				                                                                                                "<label class=\"accordion-toggle\" data-parent=\"#ibank-accordion\" href=\"#ibank1\">\n"+
				                                                                                                    "<div class=\"ibank-header\">New Intermediary Bank</div>\n"+
				                                                                                                    "<a class=\"btn btn-mini edit-ibank-item\" data-toggle=\"collapse\" data-target=\"#ibank1\" data-parent=\"#ibank-accordion\">\n"+
				                                                                                                    	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                                                                                                        "<i class=\"icon-pencil\"></i>Edit\n":
				                                                                                                        	"<i class=\"icon-chevron-sign-down\"></i> Expand\n")+ // ENHC0019060
				                                                                                                    "</a>\n"+
				                                                                                                    "<i class=\"icon-remove tip remove-ibank\" data-id=\"#ibank1\" title=\"Remove\"></i>\n"+
				                                                                                                "</label>\n"+
				                                                                                            "</div>\n"+
				                                                                                            "<div id=\"ibank1\" class=\"accordion-body collapse in\">\n"+
				                                                                                                "<div class=\"accordion-inner\">\n"+
				                                                                                                    "<div class=\"span4 select-currency\">\n"+
				                                                                                                        "<label>\n"+
				                                                                                                            "Currency\n"+
				                                                                                                        "</label>\n"+
				                                                                                                        "<select name=\"primary-int-currency1\" class=\"input-block-level\">\n"+
				                                                                                                            "<option value=\"\">Select Currency</option>\n"+
				                                                                                                            "<option value=\"usd\">USD</option>\n"+
				                                                                                                            "<option value=\"canadian\">CAD</option>\n"+
				                                                                                                            "<option value=\"peso\">MXN</option>\n"+
				                                                                                                            "<option value=\"australian\">ASD</option>\n"+
				                                                                                                            "<option value=\"newzealand\">NZD</option>\n"+
				                                                                                                        "</select>\n"+
				                                                                                                    "</div>\n"+
				                                                                                                    "<div class=\"span4 select-country\">\n"+
				                                                                                                        "<label>\n"+
				                                                                                                            "Country of Intermediary Bank\n"+
				                                                                                                        "</label>\n"+
				                                                                                                        "<select name=\"primary-int-country1\" class=\"input-block-level\">\n"+
				                                                                                                            "<option value=\"\">Select Country</option>\n"+
				                                                                                                            "<option value=\"us\">United States</option>\n"+
				                                                                                                            "<option value=\"canadian\">Canada</option>\n"+
				                                                                                                            "<option value=\"mexico\">Mexico</option>\n"+
				                                                                                                            "<option value=\"australia\">Australia</option>\n"+
				                                                                                                            "<option value=\"newzealand\">New Zealand</option>\n"+
				                                                                                                        "</select>\n"+
				                                                                                                    "</div>\n"+
				                                                                                                    "<div class=\"span4 ibank-account\">\n"+
				                                                                                                        "<label>\n"+
				                                                                                                            "Intermediary Bank Account #\n"+
				                                                                                                        "</label>\n"+
				                                                                                                        "<input name=\"primary-int-account1\" type=\"text\" class=\"input-block-level\" />\n"+
				                                                                                                    "</div>\n"+
				                                                                                                "</div>\n"+
				                                                                                            "</div>\n"+
				                                                                                        "</div>\n"+
				                                                                                    "</div>\n"+
				                                                                                "</div>\n"+
				                                                                            "</div>\n"+
				                                                                            /*"<div class=\"add-i-bank\">\n"+
				                                                                                "<hr />\n"+
				                                                                                "<a class=\"btn btn-primary\">\n"+
				                                                                                    "<i class=\"icon-plus\"></i>Add Intermediary Bank Info\n"+
				                                                                                "</a>\n"+
				                                                                            "</div>\n"+*/
				                                                                        "</div>\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                            "</div>");
				                                                            // Begin of Insert by Naga ENHC0013668
				                                                            // If it is vendor login banking forms are not required
				                                                            // Make them optional in that case
			                                                            
				                                                            if(requestType.equals("2") && !(vendorId!=null&&vendorId.trim().length()>0)){ 
				                                                            	bankingFormRequiredDisplay = "required-red";
				                                                            	bankingFormRequiredInput = "required";
				                                                            	applyUserError="user-error"; // Added - Pranesh -(05/03/2016) 	
				                                                            }
				                                                            else{
				                                                            	bankingFormRequiredDisplay = "";
				                                                            	bankingFormRequiredInput   = "";
				                                                            	applyUserError="";
				                                                            }
				                                                            
				                                                            // End of Insert by Naga ENHC0013668
				                                                            response.write("<div class=\"upload-forms\">\n"+
				                                                                "<div class=\"control-group pull-left\">\n"+
//				                                                                    "<label class=\"upload-label control-label "+bankingRequiredDisplay+"\">\n"+  ENHC0013668
				                                                                    "<label class=\"upload-label control-label "+bankingFormRequiredDisplay+"\">\n"+   // ENHC0013668
				                                                                        "Upload "+(bankingPrimaryCountry.equals("US")?(bankingPrimaryAccountType.contains("U")?"Wire":"ACH"):"Wire")+" Form\n"+
				                                                                        // Begin of Insert by Naga ENHC0013668
				                                                                        ((vendorId!=null&&vendorId.trim().length()>0)?"(<strong>Please upload new WIRE form if there is change in wire banking details</strong>)":"")+
				                                                                        // End of Insert by Naga
				                                                                    "</label>\n"+
				                                                                    "<div class=\"controls\">\n");
				                                                            		// Begin fo Comment and Insert by Naga ENHC0013668
				                                                            		// Consolidate this logic to make file required or not based on flags
				                                                            
//				                                                            		// Begin of Insert by Naga ENHC0013668
//						        		                                            // During the maintain all the forms to be optional
//						        		                                            // Making Primary ACH optional here.
//						                                                            if(vendorId!=null&&vendorId.trim().length()>0){
//																						if (FileNameACH.length()>1){
//
//																							response.write("<input type=\"file\" disabled name=\"primaryACH\" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
//																							response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDACH+"&filename="+FileNameACH+"\" target=\"_new\">"+FileNameACH+"</a></span><i class=\"icon-remove\" fileId=\""+GUIDACH+"\"></i>\n");
//																						} else {
//																							response.write("<input type=\"file\" name=\"primaryACH\" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
//																						}					                                                                        
//						                                                            }else
//				                                                            		// End of Insert by Naga
//				                                                            		// It is not the vendor type but request type which should control the required and not required logic Naga 998
////				                                                                    if(vendorType.equals("2") && (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){
//				                                                                    if(requestType.equals("2") && ((decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080")))){
//																							if (FileNameACH.length()>1){
//
//																								response.write("<input type=\"file\" disabled name=\"primaryACH\" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\"/>\n");		                                          				                                                                    		
//																								response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDACH+"&filename="+FileNameACH+"\" target=\"_new\">"+FileNameACH+"</a></span><i class=\"icon-remove\" fileId=\""+GUIDACH+"\"></i>");
//																							} else {
//
//																								response.write("<input type=\"file\" fileType=\"ACH\" name=\"primaryACH\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");				                                                                    				
//																							}				                    
//				                                                                    } else {
//				                                                                    	// By Naga 998 If it is vendor login then the form should not be required.
//																						if (FileNameACH.length()>1){
//
//																							response.write("<input type=\"file\" disabled name=\"primaryACH\" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
//																							// Comment the form is required message by Naga ENHC0015302
//																							response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDACH+"&filename="+FileNameACH+"\" target=\"_new\">"+FileNameACH+"</a></span><i class=\"icon-remove\" fileId=\""+GUIDACH+"\"></i>\n");
//																							//"<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
//																							
//																						} else {
//																							// Comment the form is required message by Naga ENHC0015302
//																							// Comment and Insert by Naga 998, ACH / Wire form is not required if it is vendor login 
////																							response.write("<input type=\"file\" name=\"primaryACH\" fileType=\"ACH\" class=\"user-error\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" class=\"user-error\" />\n");
//																							response.write("<input type=\"file\" name=\"primaryACH\" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
//																							//"<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");				                                                                    				
//																						}					                                                                        
//				                                                                    }
																					if (FileNameACH.length()>1){
																						response.write("<input type=\"file\" disabled name=\"primaryACH\" "+bankingFormRequiredInput+" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n"); // added "+bankingFormRequiredInput+" ganesh
																						response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid="+GUIDACH+"&filename="+FileNameACH+"\" target=\"_new\">"+FileNameACH+"</a></span><i class=\"icon-remove remove-file\" fileId=\""+GUIDACH+"\"></i>\n");
																						// Begin Added Pranesh(04/16/2016) - ENHC0016459
																							response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
																						// End   Added Pranesh(04/16/2016) - ENHC0016459
																					} else {
																						
																						response.write("<input type=\"file\" name=\"primaryACH\" "+bankingFormRequiredInput+" fileType=\"ACH\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
																						// Begin Added Pranesh(04/17/2016) - ENHC0016459
																							response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
																						// End   Added Pranesh(04/17/2016) - ENHC0016459
																					}						                                                            
				                                                                    response.write("<input type=\"hidden\" name=\"ACHFileInfo\" value=\""+GUIDACH+"\">\n"+
				                                                                    "</div>\n"+
				                                                                "</div>\n"+
				                                                                // Begin of Comment and Insert by Naga
//				                                                                "<a class=\"pull-right blank-download-template\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/ACH%20OnBoardingtemplate.doc\"  target=\"_new\"><i class=\"icon-file\"></i>Download Blank Form</a>\n"+
				                                                                (bankingPrimaryCountry.equals("US")?(bankingPrimaryAccountType.contains("U")?
				                                                                		"<a class=\"pull-right blank-download-template\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_US_OnBoardingtemplate.doc\"  target=\"_new\"><i class=\"icon-file\"></i>Download Blank Form</a>\n":
		                                                                				"<a class=\"pull-right blank-download-template\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/ACH%20OnBoardingtemplate.doc\"  target=\"_new\"><i class=\"icon-file\"></i>Download Blank Form</a>\n"):
		                                                                				"<a class=\"pull-right blank-download-template\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/WIRE_OnBoardingtemplate.doc\"  target=\"_new\"><i class=\"icon-file\"></i>Download Blank Form</a>\n")+
				                                                                // End of Comment and Insert by Naga
				                                                                "<div class=\"clearfix\"></div>\n"+
				                                                            "</div>\n");
				                                          response.write("</div>\n"+
				                                                    "</div>\n"+
				                                                "</div>\n"+
				                                            "</div>\n");
														// Tester
														response.write("</div>\n"+
				                                            "<div class=\"accordion\">\n");
				                                            // Modified by CGUTJAHR 1/13/15 : Enhancement #41
															// ENHC0013683 - Add Garnishment vendor type, Remove secondary bank account
															// ENHC0016461 - Add Contest Winner vendor type, Remove secondary bank account
				                                          		if ( !(vendorType.contains("060") || vendorType.contains("092") || vendorType.contains("094"))) {
				                                          			response.write("<h1 id=\"secondary-accounts-legend\" class=\"secondary-item-legend hide\">Secondary Accounts</h1>\n"+
				                                                "<div class=\"address secondary-item\" id=\"secondary-account\"></div>\n"+
					                                            "</div>\n"+
					                                            "<input id=\"secondary-address-order\" name=\"secondary-address-order\" type=\"hidden\" value=\"\">\n"+
					                                            "<hr>\n"+
				                                            "<div required=\"required\" class=\"input-append secondary-account-button\">"+
				                                       		"<select class=\"select-country country sec-account-country\">\n"+
				                                       		"<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.

				                                            for (int x = 0; x < arrayCountryCode.length; x++) {
//				                                            	// Only add US if it is political contribution ENHC0016458
//				                                            	if(vendorType.contains("040")){
//				                                            		if(!arrayCountryCode[x][0].equalsIgnoreCase("US"))
//				                                            			continue;
//				                                            	}
				                                            		
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
															}
				                                    		response.write("</select>\n"+
				                                                //    "<option value=\"US\">United States</option>\n"+
				                                                //    "<option value=\"CA\">Canada</option>\n"+
				                                                //    "<option value=\"MX\">Mexico</option>\n"+
				                                                //    "<option value=\"AU\">Australia</option>\n"+
				                                                //    "<option value=\"NZ\">New Zealand</option>\n"+
				                                                //    "<option>Other</option>\n"+
				                                                //"</select>\n"+
				                                                "<a id=\"add-sec-account-address\" class=\"btn btn-primary add-account add-item "+disableButton+" \"><i class=\"icon-plus\"></i>Add Account</a>\n"+
					                                            "</div>\n");
				                                         } else {
				                                        	 response.write("</div>\n"); 
				                                         }
				    				                        if ( (selectedValue.equalsIgnoreCase("Check")) && retCT_ADR6.getNumRows()<1 && arraySecondaryAccount==null ) { // retCT_ADR6.getNumRows() and arraySecondaryAccount added DFCT0017543 Ganesh
				                                                response.write("<div class=\"accordion payment-notifications\" id=\"payment-notifications\" style=\"display: none;\">\n");
				                                             } else {
				                                            	response.write("<div class=\"accordion payment-notifications\" id=\"payment-notifications\">\n");	 
				                                             }
				                                         // END                                          		
				                                            response.write("<h1>Payment Notifications</h1>\n"+
				                                                "<div class=\"accordion\" id=\"email-accordion\">\n" +
				                                                "<!-- "+retCT_ADR6.getNumRows()+":"+arraySecondaryAccount+" -->");
				                                                //TODO : Payment Notification
				                                                //Start Primary Contact (retCT_ADR6) 
				                                              int maxAddressRows = retCT_ADR6.getNumRows();
				                                              for(int i = 0; i < maxAddressRows ; i++) {
				                                            	// Begin of Insert by Naga 999
				                                               	String smtpAddress = retCT_ADR6.getString("SMTP_ADDR");
				                                            	if(smtpAddress==null || smtpAddress.equalsIgnoreCase("")){
				                                            		smtpAddress = "Email Address is Blank!";
																	response.write("<div class=\"accordion-group\">\n"+
																			"<div class=\"accordion-heading\">\n"+
																				"<div class=\"accordion-toggle\" data-parent=\"#email-accordion\">\n"+
																				"<div class=\"email-header "+bankingFormRequiredDisplay+"\">"+smtpAddress+"</div>\n"+ // Naga 999 Display smtpAddress here. // Added "bankingFormRequiredDisplay" - Pranesh(05/03/2016)//																						"<a class=\"btn btn-mini btn-primary edit-email-item\" data-toggle=\"collapse\" data-target=\"#emailContact-"+i+"\" data-parent=\"#email-accordion\">\n"+
//																						"<i class=\"icon-pencil\"></i>Edit\n"+
//																						"</a>\n"+
																						((i==0&&(!bankingRequiredDisplay.equalsIgnoreCase("")))?"":"<i class=\"icon-remove tip remove-email\" data-id=\"#emailContact-"+i+"\" title=\"Remove\"></i>\n")+
																					"</div>\n"+
																				"</div>\n"+
																				"<div id=\"emailContact-"+i+"\" class=\"accordion-body\" style=\"\">\n"+
																				"<div class=\"accordion-inner\">\n"+
																				// Begin of comment and Insert by Naga DFCT0015086
																				
																					//"<input type=\"email\" required=\"\" name=\"emailContact-"+i+"\" class=\"span6 \" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
																				"<input type=\"email\" "+bankingRequiredInput+" name=\"emailContact-"+i+"\" class=\"span6 "+applyUserError+"\" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
																				// Added "applyUserError" - Pranesh(05/03/2016)
																				// End of comment and Insert by Naga	
																				"</div>\n"+
																			"</div>\n"+
																		"</div>\n");		 		                                            		
				                                            	}else{
																	response.write("<div class=\"accordion-group\">\n"+
																	"<div class=\"accordion-heading\">\n"+
																		"<div class=\"accordion-toggle\" data-parent=\"#email-accordion\">\n"+
																			"<div class=\"email-header\">"+smtpAddress+"</div>\n"+	// Naga 999 Display smtpAddress here.
																				"<a class=\"btn btn-mini btn-primary edit-email-item collapsed\" data-toggle=\"collapse\" data-target=\"#emailContact-"+i+"\" data-parent=\"#email-accordion\">\n"+
																				((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
																				"<i class=\"icon-pencil\"></i>Edit\n":
																				"<i class=\"icon-chevron-sign-down\"></i> Expand\n")+ // ENHC0019060
																				"</a>\n"+
																				((i==0&&(!bankingRequiredDisplay.equalsIgnoreCase("")))?"":"<i class=\"icon-remove tip remove-email\" data-id=\"#emailContact-"+i+"\" title=\"Remove\"></i>\n")+
																			"</div>\n"+
																		"</div>\n"+
																		"<div id=\"emailContact-"+i+"\" class=\"accordion-body in collapse\" style=\"height: 0;\">\n"+
																		"<div class=\"accordion-inner\">\n"+
																		// Begin of comment and Insert by Naga DFCT0015086
																		
																			//"<input type=\"email\" required=\"\" name=\"emailContact-"+i+"\" class=\"span6 \" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
																		"<input type=\"email\" "+bankingRequiredInput+" name=\"emailContact-"+i+"\" class=\"span6 \" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
																		// End of comment and Insert by Naga	
																		"</div>\n"+
																	"</div>\n"+
																"</div>\n");				                                            		
				                                            	}
				                                            	// End of Insert by Naga DFCT0015088
//																response.write("<div class=\"accordion-group\">\n"+
//																	"<div class=\"accordion-heading\">\n"+
//																		"<div class=\"accordion-toggle\" data-parent=\"#email-accordion\">\n"+
//																			"<div class=\"email-header\">"+smtpAddress+"</div>\n"+	// Naga 999 Display smtpAddress here.
//																				"<a class=\"btn btn-mini btn-primary edit-email-item collapsed\" data-toggle=\"collapse\" data-target=\"#emailContact-"+i+"\" data-parent=\"#email-accordion\">\n"+
//																				"<i class=\"icon-pencil\"></i>Edit\n"+
//																				"</a>\n"+
//																				"<i class=\"icon-remove tip remove-email\" data-id=\"#emailContact-"+i+"\" title=\"Remove\"></i>\n"+
//																			"</div>\n"+
//																		"</div>\n"+
//																		"<div id=\"emailContact-"+i+"\" class=\"accordion-body in collapse\" style=\"height: 0;\">\n"+
//																		"<div class=\"accordion-inner\">\n"+
//																		// Begin of comment and Insert by Naga DFCT0015086
//																		
//																			//"<input type=\"email\" required=\"\" name=\"emailContact-"+i+"\" class=\"span6 \" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
//																		"<input type=\"email\" "+bankingRequiredInput+" name=\"emailContact-"+i+"\" class=\"span6 \" aria-invalid=\"true\" value=\""+retCT_ADR6.getString("SMTP_ADDR")+"\">\n"+
//																		// End of comment and Insert by Naga	
//																		"</div>\n"+
//																	"</div>\n"+
//																"</div>\n");
																retCT_ADR6.nextRow();
				                                              }
																// End
				                                                response.write("</div>\n"+
				                                                "<a class=\"btn btn-primary add-email-contact "+disableButton+" \">\n"+
				                                                    "<i class=\"icon-plus\"></i>Add Email Contact\n"+
				                                                "</a>\n"+
				                                            "</div>\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n");
				                                       // ENHC0016458 Government and Political go back to Tab1
				                                       // ENHC0016461 Legal Settlement and Contest Winner go back to Tab2
				                                       if ( vendorType.equalsIgnoreCase("060") || vendorType.equalsIgnoreCase("040") || vendorType.equalsIgnoreCase("050")){			                                    
				                                    	   response.write("<button class=\"btn back\" href=\"#tab1\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</button>\n");
				                                       }else if(vendorType.equalsIgnoreCase("093") || vendorType.equalsIgnoreCase("094") || vendorType.equalsIgnoreCase("095")){ // Added  - Pranesh(05/17/2016)-Defect : 15095
				                                    	   response.write("<button class=\"btn back\" href=\"#tab2\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</button>\n");
				                                       }else {
				                                    	   response.write("<button class=\"btn back\" href=\"#tab3\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</button>\n");				                                	   
				                                       }
				                                        response.write("<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n");				                                        
				                                       
				                                        
				                                        //Before change - Pranesh(05-04-2016) - ENHC0016459
				                                        //if ( vendorType.equalsIgnoreCase("060")){
				                                        
 				                                        //After change - Pranesh(05-04-2016) - ENHC0016459
				                                        //Begin Of Insert by Pranesh(05-04-2016) - ENHC0016459,placed submit button & coutinue button disabled for 095 vendor type @ banking tab
				                                        
				                                        
				                                        if ( vendorType.equalsIgnoreCase("060") || vendorType.equalsIgnoreCase("095")){
				                                        	response.write("<button name=\"action\" class=\"btn btn-success submit\" value=\"submit\" type=\"submit\">Submit <i class=\"icon-ok\"></i></button>\n");				                                        	
				                                        } else {
				                                        	response.write("<a class=\"btn btn-success continue\" href=\"#tab5\">Continue <i class=\"icon-angle-right\"></i></a>\n");		                                        	
				                                        }
				                                        //End Of Insert by Pranesh - 05-04-2016 - ENHC0016459
				                                        
				                                        
				                                      //Req#51 START Code change by AGAMPA 		
				                                      //  response.write("<a class=\"btn btn-success resubmit\" href=\"#\">Resubmit <i class=\"icon-ok\"></i></a>\n"+
				                                    response.write("</div>\n"+
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
				    
				                                            		String contactName = retCT_KNVK.getString("NAME1");
				                                            		String contactDepartment = retCT_KNVK.getString("ABTNR");
				                                            		String contactPhone = retCT_KNVK.getString("TELF1");
				                                            		String contactFax = retCT_KNVK.getString("FAX_NUMBER");				                                            		
				                                            		String contactEmail = retCT_KNVK.getString("SMTP_ADDR");	

															        response.write("<div class=\"accordion-group single-item\">\n"+
															            "<div class=\"accordion-heading\">\n"+
															                "<label class=\"item-label\">"+contactName+"</label>\n"+
															                "<i class=\"icon-remove tip\"  id=\"contactTemplateRmBtn\" data-id=\"view"+i+"\" title=\"Remove\"></i>\n"+
															                "<a class=\"btn btn-mini edit-item\"  id=\"contactTemplateBtn\" data-target=\"#contact-view"+i+"\" data-parent=\"#secondary-contact\">\n"+
															                	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
															                    "<i class=\"icon-pencil\"></i>Edit\n":
															                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+ // ENHC0019060
															                "</a>\n"+
															                "<div class=\"clearfix\"></div>\n"+
															            "</div>\n"+
															            "<div class=\"accordion-body collapse\" id=\"contact-view"+i+"\" style=\"height: 0px;\">\n"+
															                "<div class=\"row-fluid\">\n"+
															                    "<div class=\"span6\">\n"+
															                        "<div class=\"control-group\">\n");
															        				
															        				//Pranesh - Test (13-04-2016)-ENHC0016459
																					// Added 093- Pranesh - (04/29/2016)-(Def : 15047)
															                        if ( (vendorType.equalsIgnoreCase("095")) || (vendorType.equalsIgnoreCase("093")) ){
															    						contactsRequiredDisplay = "";
															    						contactsRequiredInput = "";					
															    					}
															                       
															                        //"<label class=\"control-label "+bankingRequiredDisplay+"\"> Name</label>\n"+ //ENHC0016458
															                        response.write("<label class=\"control-label "+contactsRequiredDisplay+"\"> Name</label>\n"+ //ENHC0016458
															                        "<div class=\"controls\">\n"+
//															                                "<input class=\"input-block-level header-input name\" "+bankingRequiredInput+" type=\"text\" name=\"contact-view"+i+"-Name\" placeholder=\"Name\" value=\""+contactName+"\">\n"+ //ENHC0016458
															                                "<input class=\"input-block-level header-input name\" "+contactsRequiredInput+" type=\"text\" name=\"contact-view"+i+"-Name\" placeholder=\"Name\" value=\""+contactName+"\">\n"+ //ENHC0016458
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                    "<div class=\"span6\">\n"+
															                        "<div class=\"control-group\">\n"+
//															                            "<label class=\"control-label "+bankingRequiredDisplay+"\">Email Address</label>\n"+ // ENHC0016458
															                            "<label class=\"control-label "+contactsRequiredDisplay+"\">Email Address</label>\n"+ // ENHC0016458
															                            "<div class=\"controls\">\n"+
//															                                "<input class=\"input-block-level email\" "+bankingRequiredInput+" type=\"email\" name=\"contact-view"+i+"-Email\" placeholder=\"Email\" value=\""+contactEmail+"\">\n"+ // ENHC0016458
															                                "<input class=\"input-block-level email\" "+contactsRequiredInput+" type=\"email\" name=\"contact-view"+i+"-Email\" placeholder=\"Email\" value=\""+contactEmail+"\">\n"+ // ENHC0016458															                                
															                            "</div>\n"+
															                        "</div>\n"+
															                    "</div>\n"+
															                "</div>\n"+
															                "<div class=\"row-fluid\">\n"+
															                    "<div class=\"span4\">\n"+
															                    
															                        "<div class=\"control-group\">\n"+
//															                            "<label class=\"control-label "+bankingRequiredDisplay+"\"> Phone Number</label>\n"+ // ENHC0016458
															                            "<label class=\"control-label "+contactsRequiredDisplay+"\"> Phone Number</label>\n"+ // ENHC0016458															                            
															                            "<div class=\"controls\">\n"+
//															                                "<input class=\"input-block-level phone\" "+bankingRequiredInput+" type=\"tel\" name=\"contact-view"+i+"-PhoneNum\" placeholder=\"Phone Number\" value=\""+contactPhone+"\">\n"+ // ENHC0016458
															                                "<input class=\"input-block-level phone num-hyphen-validation\" "+contactsRequiredInput+" type=\"tel\" name=\"contact-view"+i+"-PhoneNum\" pattern=\"[0-9- ]+\" placeholder=\"Phone Number\" value=\""+contactPhone+"\">\n"+ // ENHC0016458
															                                "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Only Numbers and - are allowed.</div>\n"+
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
//															                            "<label class=\"control-label "+bankingRequiredDisplay+"\">Department</label>\n"+ // ENHC0016458
															                            "<label class=\"control-label "+contactsRequiredDisplay+"\">Department</label>\n"+ // ENHC0016458
															                            "<div class=\"controls\">\n"+
//															                                "<select class=\"input-block-level department\" "+bankingRequiredInput+" name=\"contact-view"+i+"-Department\">\n"+ // ENHC0016458
															                                "<select class=\"input-block-level department\" "+contactsRequiredInput+" name=\"contact-view"+i+"-Department\">\n"+ // ENHC0016458
															        							"<option value=\"\">Select One</option>\n"+// Code added by AGAMPA on 2-28-2015.
															                                    "<!-- "+contactDepartment+" -->\n");
		                            															for (int x = 0; x < arrayContactDepartment.length; x++) {
																										if (arrayContactDepartment[x][0].equalsIgnoreCase(contactDepartment)){
																											response.write("<option value=\""+arrayContactDepartment[x][0]+" \" selected>"+arrayContactDepartment[x][1]+"</option>");
																										} else {
																											response.write("<option value=\""+arrayContactDepartment[x][0]+"\">"+arrayContactDepartment[x][1]+"</option>");
																										}																				
																								}                                               
				                      				                                        response.write("</select>\n"+
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
				                                       // "</div>\n"+
				                                        "<hr>\n"+
				                                        "<a class=\"btn btn-primary add-contact add-item "+disableButton+" \"><i class=\"icon-plus\"></i>Add Contact</a>\n"+
				                                    "</div>\n"+
				                                    "<div class=\"form-actions\">\n"+
				                                        "<a class=\"btn back\" href=\"#tab4\" data-toggle=\"tab\"><i class=\"icon-angle-left\"></i>Back</a>\n"+
				                                        "<button name=\"action\" class=\"btn btn-link save\" value=\"save\"><i class=\"icon-save\"></i>Save for Later</button>\n"+
				                                        "<button name=\"action\" class=\"btn btn-success submit\" value=\"submit\" type=\"submit\">Submit <i class=\"icon-ok\"></i></button>\n"+
				                                      //Req#51 START Code change by AGAMPA
				                                      //  "<button name=\"action\" class=\"btn btn-success resubmit\" value=\"resubmit\">Resubmit <i class=\"icon-ok\"></i></button>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
	          /*Added to fix UI*/   "</div>\n"+
				                    "<div class=\"span4 sidebar\">\n");
									
									String statusText = "";
									String statusLabelClasses = "badge";
									

									if(status.equalsIgnoreCase("0"))
									{
										//statusText changed by Kermel Ruperto 10-10-2014
										//statusText = "Invite Created";
										statusText = "Invite Pending Approval";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("1"))
									{
										statusText = "Invite Approved";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("2"))
									{
										statusText = "Invite Approved";		
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("3"))
									{
										statusText = "Invite Rejected";		
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("4"))
									{
										statusText = "Invite Approved";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("5"))
									{
										//statusText changed by Kermel Ruperto 10-10-2014
										//statusText = "Invite Sent";
										statusText = "Pending Vendor Action";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("6"))
									{
										statusText = "Invite Registered";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("7"))
									{
										statusText = "Invite Rejected";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("8"))
									{
										statusText = "Invite Pending Term Approval";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("A"))
									{
										//statusText changed by Kermel Ruperto 10-10-2014
										//statusText = "Approved";
										statusText = "In Review";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("F"))
									{
										statusText = "Failed";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("O"))
									{
										//statusText = "Old Version";		// Naga Enh 14 ENHC0015302, change the text of status
										statusText = "Request Cancelled"; 	// Naga Enh 14 ENHC0015302, change the text of status
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("P"))
									{
										statusText = "Completed";	
										statusLabelClasses += " badge-success";
									}
									// Begin of Insert by Naga ENHC0013658
									// Auto Reject ( D ) is similar to Rejected
									else if(status.equalsIgnoreCase("D"))
									{
										statusText = "Rejected";	
										statusLabelClasses += " badge-important";
									}									
									// End of Insert by Naga
									else if(status.equalsIgnoreCase("R"))
									{
										statusText = "Rejected";	
										statusLabelClasses += " badge-important";
									}
									else if(status.equalsIgnoreCase("S"))
									{
										//statusText changed by Kermel Ruperto 10-10-2014
										//statusText = "Saved";
										statusText = "Pending Submission";
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("T"))
									{
										statusText = "Pending Term Approval";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("W"))
									{
										statusText = "Pending Approval";	
										statusLabelClasses += " badge-success";
									}
									// Begin of Insert by Naga ENHC0016169
									// Two new statuses introduced	
									else if(status.equalsIgnoreCase("I"))
									{
										statusText = "Pending IC Approval";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("E"))
									{
										statusText = "Pending IC & Term Approval";	
										statusLabelClasses += " badge-success";
									}	
									else if(status.equalsIgnoreCase("M"))
									{
										statusText = "Pending Mgmt. Approval";	
										statusLabelClasses += " badge-success";
									}
									else if(status.equalsIgnoreCase("X"))
									{
										statusText = "W8 Validation Failed";	
										statusLabelClasses += " badge-important";
									}	
									else if(status.equalsIgnoreCase("Y"))
									{
										statusText = "IC Reject";	
										statusLabelClasses += " badge-important";
									}
									else if(status.equalsIgnoreCase("Z"))
									{
										statusText = "Pending W8 Validation";	
										statusLabelClasses += " badge-success";
									}									
									// End of Insert by Naga									
									//Begin of Insert CTI w8 Foreign vendor
									else if(status.equalsIgnoreCase("U"))
									{
										statusText = "Pending W8 Submission";
										statusLabelClasses += " badge-important";
									}
									else if(status.equalsIgnoreCase("V"))
									{
										statusText = "Pending TAX Review";
										statusLabelClasses += " badge-important";
									}
									//End of Insert CTI w8 Foreign vendor
									else if(status.equalsIgnoreCase("Draft"))
									{
										statusText = status;
										statusLabelClasses += " badge-warning";
									}
									else if(status.equalsIgnoreCase("Pending Approval"))
									{
										statusText = status;
										statusLabelClasses += " badge-primary";
									}
									else
									{
										statusText = status;
										statusLabelClasses += " badge-success";
									}
									
									boolean hasErrorMessages = !errorMessageList.isEmpty();
									
									if(hasErrorMessages)
									{
										statusLabelClasses += " shows-error-message";
									}
									
									response.write("<h3 id=\"vendor-status-label\" class=\""+statusLabelClasses+"\">"+statusText+"</h3>\n");
									
									if(hasErrorMessages)
									{
									
										response.write("<div id=\"errorMessages\" class=\"error-message-panel\">\n");
									
										int errorMessageListSize = errorMessageList.size();
										int lastErrorMessageIndex = errorMessageList.size() - 1;
										for(int i = 0; i < errorMessageListSize; i++)
										{
											response.write(errorMessageList.get(i));
											
											if( i != lastErrorMessageIndex )
											{
												response.write("<hr>");
											}
										}
										response.write("</div>\n");
									}
									
									response.write("<hr>\n"+
									
									//Begin Of Insert by Pranesh -(04/19/2016) - ENHC0019059
			                        
			                        "<div class=\"span4 sidebar\">\n"+
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
		                            "</div>\n");
									    
		                            response.write("<div class=\"span4 sidebar\">\n"+
		                            		"<h3><i class=\"icon-plus-sign-alt\"></i>FAQ's</h3>\n"+
		                            "<div class=\"accordion\" id=\"accordion2\">\n");
		                               
		                              while(tempVal<helps.length-1)  
		                              {             
		                            	  int tempClassId=tempVal;
		                                  response.write("<div class=\"accordion-group\">\n"+
		                                    "<div class=\"accordion-heading\">\n"+
		                                        "<a class=\"accordion-toggle collapsed\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapseOne"+tempClassId+"\">"+helps[tempVal]+"\n"+
		                                        "</a>\n"+
		                                    "</div>\n");
		                                  tempVal++;
		                                  response.write("<div id=\"collapseOne"+tempClassId+"\" class=\"accordion-body collapse\">\n"+
		                                        "<div class=\"accordion-inner\">\n"+helps[tempVal]+
		                                        "</div>\n"+
		                                    "</div>\n"+
		                                  "</div>\n");
		                                  tempVal++;
		                              }
		                              response.write("<p style=\"color: red;\">" +helps[tempVal]+"</p>\n"+ 
			                        			"</div>\n"+
			                                "</div>\n"+
		                        "</div>\n"+
		                    "</div>\n"+
		                "</div>\n"+
		            "</div>\n"+
		            // End of code Pranesh(04/19/2016)   BRD_ENHC0019059   
									

				        "<div class=\"footer\">\n"+
				            "<div class=\"container\">\n"+
				                "2014 NBCUniversal\n"+
				            "</div>\n"+
				        "</div>\n"+
						"<div id=\"validateAddress\" class=\"modal hide fade\">\n"+
						    "<div class=\"modal-header\">\n"+
						        "<h3>Error</h3>\n"+
						    "</div>\n"+
						    "<div class=\"modal-body\">\n"+
						        "<div>\n"+
						            "No matching city/state/zip was found, please check your address fields.\n"+
						        "</div>\n"+
						    "</div>\n"+
						    "<div class=\"modal-footer\">\n"+
					        "<a href=\"#\" id=\"taxCodeValidate\" class=\"btn btn-success continue\">Continue<i class=\"icon-angle-right\"></i></a>\n"+ // DFCT0016721- ganesh added id
					        "<a href=\"#\" class=\"btn btn-success cancel\">Cancel<i class=\"icon-cancel\"></i></a>\n"+// DFCT0016721- ganesh added cancel button
						    "</div>\n"+
						"</div>\n"+
						// Begin of Insert by Naga 1228
						"<div id=\"validateDelete\" class=\"modal hide fade\">\n"+
						    "<div class=\"modal-header\">\n"+
						        "<h3>Confirmation</h3>\n"+
						    "</div>\n"+
						    "<div class=\"modal-body\">\n"+
						        "<div>\n"+
						            "Are you sure you want to delete this record in SAP?\n"+
						        "</div>\n"+
						    "</div>\n"+
						    "<div class=\"modal-footer\">\n"+
							    "<a href=\"#\" class=\"btn ok\">Yes<i class=\"icon-ok\"></i></a>\n"+    
							    "<a href=\"#\" class=\"btn btn-primary cancel\">No<i class=\"icon-remove\"></i></a>\n"+
						    "</div>\n"+
					    "</div>\n"+						
						// End of Insert by Naga
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
					    		"<div>"+
					    			"<label class=\"rejectionReason-label\">Reason for Rejection</label>"+
							    	"<select name=\"rejectionReason\" class=\"rejectionReason\" required>\n" +
							    		"<option value=\"\">Select One</option>");
				                        
				                        
				                        
				                        
				                        
				                        
				                        
				                        if(!retCT_ANSWER.isEmpty()){
				                        	retCT_ANSWER.firstRow();
				                        	int maxRows = retCT_ANSWER.getNumRows();
				                        	for(int i=0;i<maxRows;i++){
				                        		if(retCT_ANSWER.getString("QGROUP").equals("00009")){
				                        			response.write("<option value=\""+retCT_ANSWER.getString("ACOMMENT")+"\">"+retCT_ANSWER.getString("ACOMMENT")+"</option>");
				                        		}
				                        		retCT_ANSWER.nextRow();
				                        	}
				                        }
				                        response.write(
							    	"</select>\n"+
						    	"</div>"+
					    		"<div>"+
					    			"<label class=\"approvalReason-label\">Reason for Approval</label>"+
							    	"<select name=\"approvalReason\" class=\"approvalReason\">\n" +
						    			"<option value=\"\">Select One</option>");
				                        if(!retCT_ANSWER.isEmpty()){
				                        	retCT_ANSWER.firstRow();
				                        	int maxRows = retCT_ANSWER.getNumRows();
				                        	for(int i=0;i<maxRows;i++){
				                        		if(retCT_ANSWER.getString("QGROUP").equals("00010")){
				                        			response.write("<option value=\""+retCT_ANSWER.getString("ACOMMENT")+"\">"+retCT_ANSWER.getString("ACOMMENT")+"</option>");
				                        		}
				                        		retCT_ANSWER.nextRow();
				                        	}
				                        }
				                        response.write(
							    	"</select>\n"+
						    	"</div>"+					    	
						    	"<div>"+
						    		"<label class=\"contactPerson-label\">HR Contact Person</label>"+
						    		"<div class=\"controls\">"+
						    		"<div class=\"input-append contactPersonHolder\">"+
							    	"<input type=\"text\" name=\"contactPerson\" class=\"contactPerson\">\n"+
							    	"</input>"+
			                        "<a class=\"btn fade\" id=\"searchContactPerson\" title=\"Search for Contact Person\">\n"+
		                        	"<i class=\"icon-search\"></i>\n"+
		                        	"</a>\n"+
		                        	"</div>"+
		                        	"</div>"+
	                        	"</div>"+
					    	"</div>\n"+
					    	"<div class=\"modal-footer\">\n"+
							    "<a href=\"#\" class=\"btn cancel\">Cancel</a>\n"+
							    "<a href=\"#\" class=\"btn btn-primary ok\">Approve</a>\n"+
					    	"</div>\n"+					    	
					    "</div>\n"+
					    // Modal window and search result templates
					    "<div class=\"modal hide fade\" id=\"contactPersonResults\">\n"+
					        "<div class=\"modal-header\">\n"+
					            "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
					            "<h3>HR Contact Person</h3>\n"+
					            "<br>"+
					        "</div>\n"+
//				        "<form>\n"+
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
//				        "</form>\n"+
					    "</div>\n"+
				        "<script type=\"text/template\" id=\"search-contact-template\">\n"+
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
				        "<div id=\"terms\" class=\"modal hide fade\">\n"+
				            "<div class=\"modal-header\">\n"+
				                "<h3>Terms &amp; Conditions</h3>\n"+
				            "</div>\n"+
				            "<div class=\"modal-body\">\n"+
				                "<div>\n"+
				                    "<i class=\"icon-new-icon\"></i>\n"+
												"SYSTEM ACCESS AND REGISTRATION AGREEMENT\n"+
									"This Agreement (hereinafter \"Agreement\" is between NBCUniversal Media, LLC. (\"NBCUniversal Media, LLC.\"), and the company that employs you or that you represent, through which you access CVMADVANTAGE (as defined below) (\"Company\"), and is effective as of the Effective Date (as defined below).\n"+
									"1.0 BACKGROUND\n"+
									"\n"+
									"1.1 NBCUniversal Media, LLC. is willing to provide Company access to CVMADVANTAGE (as defined below) by means of the Internet on the condition that Company enters into this Agreement, and complies with the terms and conditions hereof.\n"+
									"\n"+
									"1.2 Company agrees to use such access, and the information obtained through such access, in accordance with the terms and conditions provided in this Agreement.\n"+
									"\n"+
									"2.0 DEFINITIONS\n"+
									"\n"+
									"2.1 \"Authorized Users\" shall mean all employees of Company and Company's affiliated companies whom NBCUniversal Media, LLC. authorizes to access CVMADVANTAGE, pursuant to Section 3.4 of this Agreement.\n"+
									"\n"+
									"2.2 \"NBCUniversal Media, LLC. Information\" shall mean any and all technical, business, economic or descriptive information, data, concepts, or know-how that NBCUniversal Media, LLC. has disclosed or discloses to Company, its affiliates, and their respective officers, directors, managers, partners, employees or agents (collectively, \"Affiliates\") in any form including, without limitation, written, oral or by other visual display, or which Company or its Affiliates obtain through use of CVMADVANTAGE.\n"+
									"\n"+
									"2.3 \"Effective Date\" shall mean the date of the Company's acceptance of this Agreement, indicated by clicking the appropriate icon on this screen.\n"+
									"\n"+
									"2.4 \"CVMADVANTAGE\", the Supplier and Contractor Online Registry eVersion, shall mean the internet website designated in Section 3.1, consisting of a supplier and contractor registry and qualifications tracking system, CVMAdvantage, provided by CVM Solutions Inc., and associated data, together with any documentation provided by NBCUniversal Media, LLC. through such website.\n"+
									"\n"+
									"2.5 \"Password\" shall mean a character string initially provided, modified, and maintained by the Authorized User for establishing Authorized User authentication.\n"+
									"\n"+
									"2.6 \"NBCUniversal Media, LLC.\" shall mean NBCUniversal Media, LLC.\n"+
									"\n"+
									"2.7 \"Company\" shall be as defined above, and shall include its affiliates and subsidiaries.\n"+
									"\n"+
									"2.8 shall mean CVM Solutions, Inc.\n"+
									"\n"+
									"3.0 ACCESS RIGHTS\n"+
									"\n"+
									"3.1 NBCU, at its sole discretion, shall provide Company with access to those portions of CVMADVANTAGE necessary for Company to maintain its Company information on the website (the \"Purpose\"). Company shall allow various employees and other authorized representatives of Company to have access to various portions of CVMADVANTAGE on an \"as needed\" basis at Company's discretion, which access shall be controlled by the user identifier and password. Company shall obtain access to the CVMADVANTAGE through a designated website address, and Company's connection shall be made via the Internet using 128-bit encryption or higher. Company shall be responsible for securely maintaining and administering passwords and Company's access rights under this Agreement.\n"+
									"\n"+
									"3.2 Company's access to CVMADVANTAGE is provided on an \"as is\" basis and is limited to what is currently available through CVMADVANTAGE.\n"+
									"\n"+
									"3.3 Any access granted hereunder to CVMADVANTAGE is solely to Company and to those Authorized Users who have a legitimate need for such access to support the Purpose and who have been informed by Company of their rights and obligations under this Agreement. All Authorized Users provided with such access shall follow the terms of this Agreement and all applicable laws and government regulations, and shall follow NBCUniversal Media, LLC.'s company policies, including its Internet and Email Policies, a copy which is available at http://www.cvmsolutions.com/legal.php. Unauthorized access and improper use is prohibited. All users of this system are subject to having their activities on the system monitored\n"+
									"and recorded by NBCUniversal Media, LLC. in accordance with its policies. Except where stated in those policies and in accordance with applicable law, Authorized Users should have no expectation of privacy while using CVMADVANTAGE. Company shall be responsible for any breach of this Agreement by Authorized Users as set forth in this Agreement.\n"+
									"\n"+
									"3.4 Company shall use CVMADVANTAGE with all due skill, care, and diligence, and shall appoint only suitably qualified Authorized Users who meet the following requirements: \n"+
									"(a) The Authorized User is competent and qualified to perform the specific tasks assigned to him/her by Company;\n"+
									"(b) The Authorized User has been authorized in accordance with the provisions of Article 4.0 herein;\n"+
									"(c) The Authorized User has been adequately instructed by Company in the procedures and legal regulations relevant to the performance of the Company's obligations under this Agreement; and\n"+
									"(d) The Authorized User has received suitable training to enable him/her to use CVMADVANTAGE efficiently and effectively and with due care, skill, and diligence, including training regarding any user guide or support procedures which NBCUniversal Media, LLC. may, in its discretion, have provided hereunder.\n"+
									"\n"+
									"3.5 Authorized Users. Company shall submit its requests to NBCUniversal Media, LLC. with respect to access rights to CVMADVANTAGE under this Agreement. Company understands that NBCUniversal Media, LLC. must approve all non-employee representatives and agents of Company. Should access be requested for non-employee representatives and agents of Company, Company shall provide NBCUniversal Media, LLC. an explanation and any accompanying comments as to the need for such access. NBCUniversal Media, LLC. retains the right to refuse authorization to any person or party.\n"+
									"\n"+
									"3.6 Company's access to using CVMADVANTAGE or any part thereof may be terminated upon completion of the Purpose.\n"+
									"\n"+
									"3.7 Company shall not use or permit CVMADVANTAGE to be used for any reason not relating to the Purpose or not authorized by NBCUniversal Media, LLC. or for any unlawful purpose.\n"+
									"\n"+
									"3.8 NBCUniversal Media, LLC. has the right, at its sole discretion, to authorize or reject Company's nominees for Authorized Users. Without prejudice to Company's obligations, NBCUniversal Media, LLC. has the right, at its sole discretion, to remove an Authorized User by notifying Company that such Authorized User has been removed, and may do so without offering a reason. Company shall act in compliance with any request made by NBCUniversal Media, LLC. under this Article.\n"+
									"\n"+
									"4.0 ACCESS METHODS\n"+
									"\n"+
									"4.1 Unless otherwise agreed to in writing, each Authorized User will be given a user identifier (\"User Id\") and Password for its sole use, which may only be used by the individual concerned, to access CVMADVANTAGE. No person shall be given a User Id unless that person meets the requirements of Article 3.4 above. NBCUniversal Media, LLC. shall maintain and make available to Company an up-to-date list of the Authorized Users who have User Ids. Company shall not store such Passwords in its systems in clear text or other non-encrypted manner.\n"+
									"\n"+
									"4.2 The Company shall instruct the Authorized Users\n"+
									"(a) not to disclose or give their Password to any other person;\n"+
									"(b) not to store their Password in any data file;\n"+
									"(c) to use every reasonable effort to refrain from any action that could allow any person to get access to their Password;\n"+
									"(d) to make every reasonable effort to prevent any situation to occur that could allow any person to get access to their Password; and\n"+
									"(e) to report to NBCUniversal Media, LLC. any actual or suspected breach of password or any other unauthorized access.\n"+
									"\n"+
									"4.3 Company shall promptly inform NBCUniversal Media, LLC. of any intended and/or expected staff changes affecting the list of Authorized Users, including without limitation termination of Authorized Users or changed responsibilities. User Ids shall not be passed on from one Authorized User to another by Company or by the individuals concerned.\n"+
									"\n"+
									"4.4 Company recognizes the potential impact that a security breach may cause and will promptly notify NBCUniversal Media, LLC. of an actual or suspected security breach.\n"+
									"\n"+
									"4.5 NBCUniversal Media, LLC. reserves the right without offering any reason to revoke authorization of any Authorized User in the event of any breach of security regulations under this Agreement by the Authorized User concerned.\n"+
									"\n"+
									"4.6 NBCUniversal Media, LLC. reserves the right to log and monitor Company's and any Authorized User's use of CVMADVANTAGE at any time and without notice. Such monitoring may include inspection of any NBCUniversal Media, LLC. Information in Company's or the Authorized User's charge and/or any information, data, document, record or computer program relating in any way to Company's obligations under this Agreement. Company and the Authorized Users understand that any information disclosed within CVMADVANTAGE is disclosed without any expectation of privacy.\n"+
									"\n"+
									"5.0 REGISTRATION\n"+
									"\n"+
									"5.1 Company shall ensure accuracy when completing the registration profile and must keep this profile current. Neither NBCUniversal Media LLC., nor its employees, assumes any responsibility for the accuracy or completeness of the Company profile. Company certifies that all information is true and accurate, to the best of their knowledge. Company certifies that any individual who enter or changes the vendor master data is an authorized representative of the Company.\n"+
									"\n"+
									"5.2. Company's legal entity name and taxpayer ID number will be validated with the IRS database, and only exact matches will be approved for vendor creation or modification.\n"+
									"\n"+
									"5.3 Company acknowledges that all new entries and changes made to existing records will be effective within 72 hours after submission of the saved data. Changes made to address information, bank details, and payment terms will be applied to future invoice submissions. Invoices submitted prior to any changes made will be processed using the pre-existing information.\n"+
									"\n"+
									"5.4 Following registration, NBCUniversal Media LLC. will move Company onto an electronic payment method via Automated Clearing House (ACH) only after the successful transmission of $0.01 into the bank account provided.\n"+
									"\n"+
									"5.5 NBCUniversal Media LLC. reserves the right to make any and all necessary bank account changes in response to error notifications received from the National Automated Clearing House Association.\n"+
									"\n"+
									"6.0 VIRUS PROGRAM CONTAMINATION\n"+
									"\n"+
									"6.1 Company recognizes that computer \"viruses\" can be propagated over a link between two separate networks, and that such contamination can have serious operational and financial implications for both such networks.\n"+
									"\n"+
									"6.2 Company shall take all reasonable measures to prevent the introduction into and propagation of viruses in any of the networks owned by any of the parties that are used in connection with this Agreement.\n"+
									"\n"+
									"6.3 Company shall check all computer software files and computer data files to be provided to NBCUniversal Media, LLC. under this Agreement immediately prior to delivery. Company shall use every reasonable effort to ensure that such files and data are free from virus programs using virus detection software that is of a type, performance level and as up-to-date as typical in the industry on the date of delivery of such files or data. \n"+
									"\n"+
									"6.4 If Company detects in its network any virus that has directly or indirectly affected, or has the potential to directly or indirectly affect, NBCUniversal Media, LLC.'s network, Company shall, without prejudice to the rights of NBCUniversal Media, LLC.,  immediately notify NBCUniversal Media, LLC. that a virus has been detected giving details about the nature of the virus.\n"+
									"\n"+
									"6.5 For the purpose of this Article, viruses include network worms, Trojan horses, logic bombs and/or all other malicious modifications.\n"+
									"\n"+
									"7.0 CONFIDENTIALITY\n"+
									"\n"+
									"7.1 Company acknowledges that it may have received or may in the future receive NBCUniversal Media, LLC. information. Company agrees it will not disclose the NBCUniversal Media, LLC. information to anyone within its entity or organization or otherwise, except as may be necessary to perform approved job functions, and then only to those who have executed a confidentiality agreement with Company consistent with this Agreement. Company agrees that it will protect all such NBCUniversal Media, LLC. information as confidential and disclose and/or use it only as NBCUniversal Media, LLC. authorizes Company to do so. Company shall use its best efforts, including written agreements, to prevent its unauthorized disclosure to any third party. Company's obligations under this Section shall not apply to \n"+
									"(a) information which Company can show was in its possession prior to the earliest disclosure by NBCUniversal Media, LLC., provided that Company has the right of free and unlimited disclosure thereof; \n"+
									"(b) information which is or, prior to any disclosure by Company, becomes part of the public domain or literature without default by Company; \n"+
									"(c) information that Company can show was developed by the Company from independent information not subject to restrictions of confidentiality; or \n"+
									"(d) information which is or has been disclosed to Company by a third party, provided Company's use of such information is in accordance with any terms of confidentiality under which it is received. For purposes of this Section 7.1, NBCUniversal Media, LLC. information shall not be deemed \"part of the public domain or literature\" merely because it may be embraced by a more general disclosure or derived from combinations of disclosures generally. Further, no combination of features of NBCUniversal Media, LLC. information shall be within that exception merely because the individual items are, but only if the specific combination and the exact method of performance is public knowledge.\n"+
									"\n"+
									"7.2 Sections 7.1 through 7.2 hereof address NBCUniversal Media, LLC.'s disclosure of NBCUniversal Media, LLC. information to Company for the above purpose and do not give Company any immunity, license, ownership or implied rights to NBCUniversal Media, LLC. information or to any information based upon it. If Company and/or its employees conceive any improvement or invention, which incorporates or is based upon NBCUniversal Media, LLC.'s information, Company shall immediately describe the improvement or invention to NBCUniversal Media, LLC. in writing and cause the ownership of such improvement or invention to be assigned to NBCUniversal Media, LLC. Company and its employees shall cooperate with NBCUniversal Media, LLC. if NBCUniversal Media, LLC. requires Company's assistance in perfecting that ownership or in prosecuting any resulting patent.\n"+
									"\n"+
									"7.3 Company shall not place any confidential information owned by any third party (hereinafter \"Third Party Confidential Information\") on CVMADVANTAGE. In the event Company needs to place on CVMADVANTAGE Third Party Confidential Information, which is lawfully in the possession of Company, and which Company may rightfully disclose subject to obligations of confidentiality, Company shall first advise NBCUniversal Media, LLC. of the such need and request permission to place such information on CVMADVANTAGE. Such request for permission shall include notification that the information is confidential and proprietary to a Third Party, the nature of the information, and the terms under which NBCUniversal Media, LLC.  would be expected to receive such Third Party Confidential Information. NBCUniversal Media, LLC. retains the right at its sole discretion to deny all or part of any such request. Any approval of such request shall only be valid if in writing.\n"+
									"\n"+
									"7.4 When Company has completed its use of NBCUniversal Media, LLC. information for the purpose of this Agreement or upon NBCUniversal Media, LLC.'s request, Company shall promptly return all NBCUniversal Media, LLC. information provided hereunder. Further, Company shall destroy all copies, in whole or in part, including any notes based upon our NBCUniversal Media, LLC. information, retaining no information regarding NBCUniversal Media, LLC. information in tangible, electronic, magnetic, optical or any other form. Destruction of electronic information shall require the overwriting or reformatting of all applicable storage files. If requested by NBCUniversal Media, LLC., Company shall certify such destruction to NBCUniversal Media, LLC. in writing.\n"+
									"\n"+
									"7.5 The Company may desire to place information related to the Purpose online through CVMADVANTAGE. The Company understands that CVMADVANTAGE is an Internet-facing application, and subject to security risks inherent in such applications. While NBCUniversal Media, LLC. has taken certain steps to mitigate these risks, the Company understands and acknowledges that the use of CVMADVANTAGE is subject to certain inherent security vulnerabilities. Accordingly, the\n"+
									"Company agrees that it shall make an independent determination as to which Information in its possession, custody and control is appropriate for placing into CVMADVANTAGE, based upon the sensitivity of the information and upon the information regarding the security of CVMADVANTAGE stated herein.\n"+
									"\n"+
									"8.0 RESERVED.\n"+
									"\n"+
									"9.0 TERMINATION\n"+
									"\n"+
									"9.1 This Agreement shall take effect on the Effective Date and shall remain in force until terminated by either party for any reason, including, without limitation, as permitted pursuant to Section 9.3, below, upon written notice.\n"+
									"\n"+
									"9.2 NBCUniversal Media, LLC. may, by notice, at its sole discretion, suspend this Agreement either for a defined period specified in the notice or until NBCUniversal Media, LLC. withdraws the suspension.\n"+
									"\n"+
									"9.3 Notwithstanding any other provision in this Agreement, NBCUniversal Media, LLC. may terminate this Agreement immediately, in addition to any other rights or remedies it may have under law or equity, in the event of Company's failure to perform or breach of any or all of the provisions of the Agreement.\n"+
									"\n"+
									"9.4 The suspension, termination, or expiration of the Agreement shall not prejudice any rights or remedies accruing to NBCUniversal Media, LLC. in accordance with this Agreement before such suspension, termination or expiration, or relieve Company of any continuing obligations or liability under this Agreement, including but not limited to Company obligations and liabilities arising under Articles 7.0 and 11.0.\n"+
									"\n"+
									"10.0 AUDIT RIGHTS\n"+
									"\n"+
									"10.1 Company shall permit NBCUniversal Media, LLC. or its authorized representatives to carry out security or audit checks pertaining to the security and usage of the systems employed in the execution of this Agreement.\n"+
									"\n"+
									"10.2 Company shall cooperate with NBCUniversal Media, LLC. in carrying out such checks. In particular, NBCUniversal Media, LLC., or its duly authorized representatives, shall have access at all reasonable times on working days during working hours at Company's business premises to the Authorized Users together with records, books and correspondence and other papers and documentation, or media of any kind in possession of Company, or the Authorized Users, pertaining to this Agreement that are necessary for NBCUniversal Media, LLC. to carry out such checks. NBCUniversal Media, LLC., or its authorized representatives, shall have the right to reproduce or retain copies at its expense of any of the aforementioned documents.\n"+
									"\n"+
									"11.0 WARRANTY DISCLAIMER, INDEMNIFICATION AND LIMITATIONS ON LIABILITY\n"+
									"\n"+
									"11.1 To the maximum extent permitted by applicable law, Company shall be responsible for, and shall defend, indemnify and hold NBCUniversal Media, LLC. and NBCUniversal Media, LLC.'s affiliates, and its employees, directors, officers, representatives and agents (\"Indemnitees\") harmless from and against all claims, suits, liabilities, judgments, losses and expenses (including, without limitation, attorney's fees and costs of litigation, whether incurred for the defense of any indemnitee or for the enforcement of any indemnitee's indemnification rights hereunder) and any fines, penalties and assessments (collectively \"Damages\"), arising out of or resulting from any breach of this Agreement by Company, the Company's classification of Information, or any other act or omission of Company in regard to this Agreement.\n"+
									"\n"+
									"11.2 Within twenty-four (24) hours after Company becomes aware of any occurrence that may result in Damages, Company shall report the same to NBCUniversal Media, LLC. by telephone and shall promptly thereafter confirm the same by written notice, including all circumstances thereof known to Company or Company's employees or other representatives. NBCUniversal Media, LLC. shall have the right, at its expense, but not the duty, to participate in the defense and settlement of any such claim or litigation with attorneys of NBCUniversal Media, LLC.'s selection without relieving Company of any obligations hereunder. Company shall cooperate with NBCUniversal Media, LLC. in NBCUniversal Media, LLC.'s investigation and defense of any claim or suit.\n"+
									"\n"+
									"11.3 In the event of conflict between the indemnification terms of this Agreement and the indemnification terms of the contract between NBCUniversal Media, LLC. and Company, Article 11.0 of this Agreement shall control with respect to the subject matters of this Agreement. \n"+
									"\n"+
									"11.4 NBCUNIVERSAL MEDIA, LLC. SHALL NOT BE LIABLE TO COMPANY IN ANY EVENT FOR CONSEQUENTIAL LOSS OR DAMAGE FROM ANY CAUSE WHATSOEVER WHETHER ARISING OUT OF CONTRACT, TORT (INCLUDING NEGLIGENCE AND STRICT LIABILITY), WARRANTIES, STATUTE, OR OTHERWISE.\n"+
									"\n"+
									"11.5 The parties hereto do not intend that there be any third-party beneficiaries to this Agreement, including without limitation CVM.\n"+
									"\n"+
									"11.6 TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, ACCESS TO THE CVMADVANTAGE WEBSITE IS PROVIDED TO COMPANY \"AS IS\" WITHOUT ANY CONDITION OR WARRANTY WHATSOEVER. THE ENTIRE RISK ASSOCIATED WITH THE USE OF THE CVM SOFTWARE AND CVMADVANTAGE RESIDES WITH COMPANY. ALL OTHER CONDITIONS OR WARRANTIES, WHETHER EXPRESS, IMPLIED, OR STATUTORY, ARE DISCLAIMED, INCLUDING WITHOUT LIMITATION, ALL IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.\n"+
									"\n"+
									"11.7 LIMITATION OF LIABILITY. IN NO EVENT WILL NBCUniversal Media, LLC., CVM, OR THEIR RESPECTIVE LICENSORS OR SUPPLIERS BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF USE, BUSINESS INTERRUPTION, LOSS OF DATA, COST OF COVER OR INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL, OR PUNITIVE DAMAGES OF ANY KIND IN CONNECTION WITH OR ARISING OUT OF THE FURNISHING, PERFORMANCE OR USE OF THE CVM SOFTWARE, CVMADVANTAGE, OR THE SERVICES PERFORMED HEREUNDER, WHETHER ALLEGED AS A BREACH OF CONTRACT OR TORTIOUS CONDUCT, INCLUDING NEGLIGENCE, EVEN IF NBCUniversal Media, LLC., CVM, OR THEIR RESPECTIVE LICENSORS OR SUPPLIERS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. IN ADDITION, NBCUniversal Media, LLC., CVM AND THEIR RESPECTIVE LICENSORS AND SUPPLIERS WILL NOT BE LIABLE FOR ANY DAMAGES CAUSED BY DELAY IN DELIVERY OR FURNISHING THE SOFTWARE, CVMADVANTAGE, OR SAID SERVICES. THE LIABILITY OF NBCUniversal Media, LLC., CVM AND THEIR RESPECTIVE LICENSORS AND SUPPLIERS UNDER THIS AGREEMENT FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL, AND /OR PUNITIVE DAMAGES OF ANY KIND, INCLUDING, WITHOUT LIMITATION, RESTITUTION, WILL NOT, IN ANY EVENT, EXCEED TEN DOLLARS ($10.00), WHETHER ALLEGED AS A BREACH OF CONTRACT OR TORTIOUS CONDUCT, INCLUDING NEGLIGENCE, AND COMPANY RELEASES SUCH PARTIES FROM LIABILITY IN EXCESS OF SAID AMOUNT.\n"+
									"\n"+
									"12.0 GENERAL\n"+
									"\n"+
									"12.1 No Security Mechanism. Except with the other party's prior written consent, neither party shall install or cause to be installed into the other party's computer systems any hardware, software, electronic, or other security mechanism and shall use every reasonable effort so that any computer virus or other disablement, deactivation, deinstallation, damage or deletion\n"+
									"mechanism, which will hinder use of any of the other party's systems will not be installed into the other party's system.\n"+
									"\n"+
									"12.2 Assignment. Company may not assign this Agreement, by operation of law or otherwise, without NBCUniversal Media, LLC.'s prior written consent and any such attempt to assign the same without the prior written consent of NBCUniversal Media, LLC. shall be void and shall not be binding on NBCUniversal Media, LLC. NBCUniversal Media, LLC. may assign this Agreement to an entity which succeeds to the business or operations of NBCUniversal Media, LLC., or to any affiliate of NBCUniversal Media, LLC.\n"+
									"\n"+
									"12.3 Entireties. No representations, statements, warranties, or agreements other than those herein expressed have induced the making, execution, and delivery of this Agreement by Company. If any part, term, or provision of this Agreement shall be held illegal, unenforceable, or in conflict with any law of a federal, state, or local government having jurisdiction over this\n"+
									"Agreement, the validity of the remaining portion or portions shall not be affected thereby. This Agreement may be amended or modified only by an instrument of equal formality signed by duly authorized representatives of the respective parties. Notwithstanding the foregoing, the terms of this Agreement may be amended or modified by a written instrument (a \"Written Agreement\") signed before or after the Effective Date, and in the event of a conflict between the terms of this\n"+
									"Agreement and a Written Agreement, the Written Agreement shall govern and control. The parties acknowledge and agree that any terms and conditions including, but not limited to, those relating to releases from, indemnities against, and limitations of liability, which may require conspicuous identification under applicable law, have not been so identified by mutual agreement, and the parties have actual knowledge of the intent and effect of such terms and conditions. \n"+
									"\n"+
									"12.4 No Waiver. No failure, omission or delay by NBCUniversal Media, LLC., in exercising any right, power or privilege under this Agreement shall operate as a waiver thereof nor preclude exercise of any other or further right, power, or privilege hereunder.\n"+
									"\n"+
									"12.5 Headings Not Controlling. Headings used in this Agreement are for reference purposes only and shall not be used to modify the meaning of the terms and conditions of this Agreement.\n"+
									"\n"+
									"12.6 Remedies. Since money damages may not provide a sufficient remedy for a breach of the obligations hereunder, Company and NBCUniversal Media, LLC., and their officers, employees, or agents agree that at either party's sole election, the other party shall also be entitled to equitable remedies including injunction and related remedies for any such breach.\n"+
									"\n"+
									"12.7 Interpretation. The limitations of liability (including waivers of subrogation), indemnifications, and exclusive remedy provisions expressed throughout the Agreement shall apply even in the event of the default, negligence or strict liability, of either party hereto.\n"+
									"\n"+
									"12.8 Governing Law. This Agreement shall be construed and governed in accordance with the laws of the State of California, excluding the application of any choice of law rules, which may direct the application of the laws of another jurisdiction.\n"+
									"\n"+
									"12.9 Survival. All of the parties' obligations under this Agreement, which are intended to survive the termination, expiration or suspension of this Agreement including, but not limited to, obligations of confidentiality, nondisclosure, limitations of liability, and indemnification, shall survive any termination, expiration or suspension of this Agreement.\n"+
									"\n"+
									"12.10 Relationship of Parties.This Agreement shall not be construed to establish a joint venture, partnership or other formal business organization. Furthermore, the parties agree that this Agreement does not constitute a partnership for tax purposes. In the event that it is so construed, however, the parties agree to be excluded from the provisions of Subchapter K of the United States Internal Revenue Code of 1986, as amended. In no event shall such relationship constitute a partnership for U.S. federal income tax purposes.\n"+
									"\n"+
									"12.11 Notice. Any written notice by either party to the other shall be given by depositing it in the U.S. Mail, postage prepaid, if addressed to the Customer at the address provided to NBCUniversal Media, LLC. for this purpose, and if addressed to NBCUniversal Media, LLC. to:\n"+
									"\n"+
									"NBCUniversal Media, LLC.\n"+
									"30 Rockefeller Plaza\n"+
									"New York, NY 10112\n"+
									"Attn: Vice President, Sourcing\n"+
									"\n"+
									"Copy to:\n"+
									"Same address as above\n"+
									"Attn: Legal Dept.\n"+
									"\n"+
									"12.12 Force Majeure. The parties to this Agreement shall be excused from the performance of their respective obligations hereunder and to the extent that such performance is delayed, hindered, or prevented by causes reasonably beyond the control of the party to perform including, but not limited to, fire, explosion, strike, labor disputes, acts of God or any act or omission of any governmental authority. The party wishing to avail itself of the provisions of this Section 12.12 shall give notice in writing to the other party.\n"+
									"\n"+
									"12.13 Export Control. Both parties agree that they will abide by the United States Department of Commerce regulations concerning the export or re-export of United States source technical data, or the direct product thereof, to unauthorized destinations in respect of information supplied by NBCUniversal Media, LLC. or Company to the other hereunder. EFFECTIVE AS OF the Effective Date.\n"+	                    
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
				        // Begin of Insert by Naga ENHC0013673	1228
						"<div id=\"CTIModal\" class=\"modal hide fade\">\n"+		
					    "<div class=\"modal-header\">\n"+
//					        "<h3>Request to go to CTI to prepare applicable From W-8</h3>\n"+
					    "<h3>Request to go to CTI</h3>\n"+
					    "</div>\n"+
					    "<div class=\"modal-body\">\n"+
					        "<div>\n"+
								"<label>\n"+
									"<p>\n"+
			         					"Foreign persons are generally subject to US withholding tax at the rate of "+
			         					"30% on their gross income they receive from US sources. You are required "+
			         					"to submit an electronic copy of signed W8 received from vendor in order to "+
			         					"establish Vendor foreign status and (a) make a valid claim for treaty "+
			         					"benefits to reduce or eliminate US withholding tax, (b) certify that "+
			         					"vendor income is effectively connected with the conduct of a trade or "+
			         					"business you have in the US to be exempt from US withholding tax, or "+
			         					"(c) certify that vendor is acting as an intermediary on behalf of the "+
			         					"beneficial owner of such income."+
			         					"</p>\n"+
			         					"<p>\n"+
			         					"Please follow the link below to complete your digital form W-8. You "+
			         					"will be asked a series of questions in order to create a digital form "+
			         					"W-8, you will need to come back to this page (VeRA: Vendor "+
			         					"Registration application Portal) to complete your registration."+
		         					"</p>\n"+
		         					"<p id=\"CTIURL\">"+
		         					"<a target=_blank href=\""+urlCTI+"\">"+urlCTI+"</a>\n"+
		         					"</p>\n"+
		         					"<p id=\"CTIREGCODE\">Client ID: "+
		         					ernamCTI+
		         					"    Registration Code: "+
		         					regCodeCTI+
	         						"</p>\n"+
                        		"</label>\n" +         						
					        "</div>\n"+
					    "</div>\n"+
					    "<div class=\"modal-footer\">\n"+
					        "<a href=\"#\" class=\"btn btn-success continue\">Ok</a>\n"+
					    "</div>\n"+
					"</div>\n"+
				        // End of Insert by Naga
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
				                ((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                    "<i class=\"icon-pencil\"></i>Edit\n":
				                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+	// ENHC0019060
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse\" id=\"contact-<%- id %>\">\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span6\">\n"+
				                  
				                    //Begin Of Insert by Pranesh - (12-04-2016) - ENHC0016459
				                    	//Before change - Pranesh - (12-04-2016) - ENHC0016459
				                        //"<div class=\"control-group\">\n"+
				                    	"<div class=\"control-group\">\n");
				                        //Before change - Pranesh - (12-04-2016) - ENHC0016459
				                        //"<label class=\"control-label required-red\">Name</label>\n"+
				                        if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){
				               response.write("<label class=\"control-label\">Name</label>\n");
				                        }else{
				               response.write("<label class=\"control-label required-red\">Name</label>\n");
				                        }
				                        //Before change - Pranesh - (12-04-2016) - ENHC0016459    
				                        //"<div class=\"controls\">\n"+
				                        
				                        if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){
				               response.write("<div class=\"controls\">\n"+
				                               "<input class=\"input-block-level header-input name\" type=\"text\" name=\"contact-view<%- id %>-Name\" placeholder=\"Name\">\n"+
				                         "</div>\n");
				                        }else{
				               response.write("<div class=\"controls\">\n"+
						                               "<input class=\"input-block-level header-input name\" required type=\"text\" name=\"contact-view<%- id %>-Name\" placeholder=\"Name\">\n"+
						                  "</div>\n");             	
				                        }
				                //End Of Insert by Pranesh - (12-04-2016) - ENHC0016459         
				                         
				               response.write("</div>\n"+
				                    "</div>\n"+
				                    
				                    "<div class=\"span6\">\n"+
				                        "<div class=\"control-group\">\n");
				          //Begin Of Insert by Pranesh - (12-04-2016) - ENHC0016459
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){
				        	   response.write("<label class=\"control-label\">Email Address</label>\n");
				           				}else{
				        	   response.write("<label class=\"control-label required-red\">Email Address</label>\n");
				           				}
				           				
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){			
				               response.write("<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level email\" type=\"email\" name=\"contact-view<%- id %>-Email\" placeholder=\"Email\">\n"+
				                            "</div>\n");
				           				}else{
				               response.write("<div class=\"controls\">\n"+
					                             "<input class=\"input-block-level email\" required type=\"email\" name=\"contact-view<%- id %>-Email\" placeholder=\"Email\">\n"+
					                       "</div>\n");
				           				}
				           //End Of Insert by Pranesh - (12-04-2016) - ENHC0016459                 
				                            
				               response.write("</div>\n"+
				                    "</div>\n"+
				                    
				                "</div>\n"+
				                
				                "<div class=\"row-fluid\">\n"+
				                
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n");
				            //Begin Of Insert by Pranesh - (12-04-2016) - ENHC0016459
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){
				           					response.write("<label class=\"control-label\">Phone Number</label>\n");
				           				}else{
				           					response.write("<label class=\"control-label  required-red\">Phone Number</label>\n");
				           				}
				           
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){			
				           	   response.write("<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level phone num-hyphen-validation\" type=\"tel\" name=\"contact-view<%- id %>-PhoneNum\" pattern=\"[0-9- ]+\" placeholder=\"Phone Number\">\n"+
				                                "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Only Numbers and - are allowed.</div>\n"+					                                
				                            "</div>\n");
				           				}else{
				               response.write("<div class=\"controls\">\n"+
					                            "<input class=\"input-block-level phone num-hyphen-validation\" required type=\"tel\" name=\"contact-view<%- id %>-PhoneNum\" pattern=\"[0-9- ]+\" placeholder=\"Phone Number\">\n"+
					                            "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">Only Numbers and - are allowed.</div>\n"+					                                
					                       "</div>\n");
				           				}
				           	//End Of Insert by Pranesh - (12-04-2016) - ENHC0016459   
				                            
				                            
				           	   response.write("</div>\n"+
				                    "</div>\n"+
				                    
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Fax Number</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level fax\" type=\"tel\" name=\"contact-view<%- id %>-FaxNum\" placeholder=\"FaxNum\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n");
				            //Begin Of Insert by Pranesh - (12-04-2016) - ENHC0016459
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){
				           					response.write("<label class=\"control-label\">Department</label>\n");
				           				}else{
				           					response.write("<label class=\"control-label required-red\">Department</label>\n");
				           				}
				           	
				           				if(decisionVendorType.equalsIgnoreCase("R093") || decisionVendorType.equalsIgnoreCase("R095")){		
				           				
				           				response.write("<div class=\"controls\">\n"+
				                                "<select class=\"input-block-level department\" name=\"contact-view<%- id %>-Department\">\n"+
				                        		"<option value=\"\">Select One</option>\n");//Code added by AGAMPA on 2-19-2015.
				           				}else{
				           				response.write("<div class=\"controls\">\n"+
					                                "<select class=\"input-block-level department\" required name=\"contact-view<%- id %>-Department\">\n"+
					                        		"<option value=\"\">Select One</option>\n");//Code added by AGAMPA on 2-19-2015.
				           				}
				           	//End Of Insert by Pranesh - (12-04-2016) - ENHC0016459            
				                        
											for (int x = 0; x < arrayContactDepartment.length; x++) {
													if (arrayContactDepartment[x][0].equalsIgnoreCase("d")){
														response.write("<option value=\""+arrayContactDepartment[x][0]+" \" selected>"+arrayContactDepartment[x][1]+"</option>");
													} else {
														response.write("<option value=\""+arrayContactDepartment[x][0]+"\">"+arrayContactDepartment[x][1]+"</option>");
													}																				
											}                                               
	                                    response.write("</select>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				    // Begin of Insert by Naga ENHC0016458
					"\n"+
				    "<script type=\"text/template\" id=\"contactTemplateNonReq\">\n"+
				        "<div class=\"accordion-group single-item\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip\" data-id=\"<%- id %>\" title=\"Remove\"></i>\n"+
				                "<a class=\"btn btn-mini edit-item\" data-target=\"#contact-<%- id %>\" data-parent=\"#secondary-contact\">\n"+
				                	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                    "<i class=\"icon-pencil\"></i>Edit\n":
				                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+ // ENHC0019060
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse\" id=\"contact-<%- id %>\">\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span6\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Name</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level header-input name\" type=\"text\" name=\"contact-view<%- id %>-Name\" placeholder=\"Name\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span6\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Email Address</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level email\" type=\"email\" name=\"contact-view<%- id %>-Email\" placeholder=\"Email\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label \">Phone Number</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level phone\" type=\"tel\" name=\"contact-view<%- id %>-PhoneNum\" placeholder=\"Phone Number\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Fax Number</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<input class=\"input-block-level fax\" type=\"tel\" name=\"contact-view<%- id %>-FaxNum\" placeholder=\"FaxNum\">\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4\">\n"+
				                        "<div class=\"control-group\">\n"+
				                            "<label class=\"control-label\">Department</label>\n"+
				                            "<div class=\"controls\">\n"+
				                                "<select class=\"input-block-level department\" name=\"contact-view<%- id %>-Department\">\n"+
				                        		"<option value=\"\">Select One</option>\n");//Code added by AGAMPA on 2-19-2015.
											for (int x = 0; x < arrayContactDepartment.length; x++) {
													if (arrayContactDepartment[x][0].equalsIgnoreCase("d")){
														response.write("<option value=\""+arrayContactDepartment[x][0]+" \" selected>"+arrayContactDepartment[x][1]+"</option>");
													} else {
														response.write("<option value=\""+arrayContactDepartment[x][0]+"\">"+arrayContactDepartment[x][1]+"</option>");
													}																				
											}                                               
	                                    response.write("</select>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+				    	
				    // End of Insert by Naga
				"\n"+
				    "<script type=\"text/template\" id=\"secondaryAccountTemplate\">\n"+
				        "<div class=\"accordion-group single-item\" id=\"secondary-account-container-<%- id %>\" data-id=\"<%- id %>\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<div class=\"hidden-form-elements\">\n"+
				                    "<input type=\"text\" value=\"\" name=\"bankingSecondary-<%- id %>-Country\" />\n"+
				                "</div>\n");
//	                            // ENHC0016458 Political Contribution will only have ACH        
//	                            if ((decisionVendorType.equalsIgnoreCase("V040")) || (decisionVendorType.equalsIgnoreCase("R040"))){
//		                            response.write(
//		    				                "<div class=\"btn-group\">\n"+
//		    				                    "<a class=\"btn btn-info dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">\n"+
//		    				                        "<label class=\"type-text\">ACH</label>\n"+
////		    				                        "<span class=\"caret\"></span>\n"+
//		    				                    "</a>\n");
////		    				                    "<ul class=\"dropdown-menu\">\n"+
////		    				                        "<li>\n"+
////		    				                            "<a class=\"typeOption\">ACH</a>\n"+
////		    				                        "</li>\n"+
////		    				                        "<li>\n"+
////		    				                        	"<a class=\"typeOption\">Wire</a>\n"+
////		    			                            "</li>\n");
////
////		                                 			if ((decisionVendorType.equalsIgnoreCase("V070")) || (decisionVendorType.equalsIgnoreCase("R070"))){				                                             							  
////		                                 				response.write("<li>\n"+
////		                                                "<a class=\"typeOption\">Check</a>\n"+
////		                                                "</li>\n");	
////		                                 			}				                        
////			    				            response.write("</ul>\n");	                            	
//	                            }else{
		                            response.write(
	    				                "<div class=\"btn-group\">\n"+
	    				                    "<a class=\"btn btn-info dropdown-toggle "+disableButton+"\" data-toggle=\"dropdown\" href=\"#\">\n"+
	    				                        "<label class=\"type-text\"><%- (country === \"US\" && type.match(/[U]/))?\"WIRE\":\"ACH\" %></label>\n"+
	    				                        // Begin of Insert by Naga ENHC0013668
	    				                        // US Wire is allowed for specific vendors.
	    				                        
	    				                        // Added 095,018 Pranesh(04/16/2016) - ENHC0016459
	    				                        (((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("R010")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("R020")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R030")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V070")) || (decisionVendorType.equalsIgnoreCase("R070")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R090")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V095")) || (decisionVendorType.equalsIgnoreCase("R095")) ||
	    				                          (decisionVendorType.equalsIgnoreCase("V018")) || (decisionVendorType.equalsIgnoreCase("R018"))
	    				                        )
	    				                        		?"<span class=\"caret\"></span>\n":"")+
	    				                        // End of Insert by Naga	
//	    				                        "<span class=\"caret\"></span>\n"+
	    				                    "</a>\n");
					                        // Begin of Insert by Naga ENHC0013668
					                        // US Wire is allowed for trade Vendor.
		                            		
		                            		// Addedd (R095-Posthumous Payments)Pranesh - (04/13/2016) - ENHC0016459
		                            		// Addedd (R018-Production Vendor)Pranesh   - (04/16/2016) - ENHC0016459
		                            		if(((decisionVendorType.equalsIgnoreCase("V010")) || (decisionVendorType.equalsIgnoreCase("R010")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("V020")) || (decisionVendorType.equalsIgnoreCase("R020")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("V030")) || (decisionVendorType.equalsIgnoreCase("R030")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("V070")) || (decisionVendorType.equalsIgnoreCase("R070")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("V090")) || (decisionVendorType.equalsIgnoreCase("R090")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("V091")) || (decisionVendorType.equalsIgnoreCase("R091")) ||
		    				                          (decisionVendorType.equalsIgnoreCase("R018")) || (decisionVendorType.equalsIgnoreCase("R095")))){
		                            		response.write(
						                    "<ul class=\"dropdown-menu\">\n"+
						                        "<li>\n"+
						                            "<a class=\"secondaryTypeOption\">ACH</a>\n"+
						                        "</li>\n"+
						                        "<li>\n"+
						                        	"<a class=\"secondaryTypeOption\">Wire</a>\n"+
					                            "</li>\n"+
			    				            "</ul>\n");	
		                            		}
					                        // End of Insert by Naga			                            
//	    				                    "<ul class=\"dropdown-menu\">\n"+
//	    				                        "<li>\n"+
//	    				                            "<a class=\"typeOption\">ACH</a>\n"+
//	    				                        "</li>\n"+
//	    				                        "<li>\n"+
//	    				                        	"<a class=\"typeOption\">Wire</a>\n"+
//	    			                            "</li>\n");
//
//	                                 			if ((decisionVendorType.equalsIgnoreCase("V070")) || (decisionVendorType.equalsIgnoreCase("R070"))){				                                             							  
//	                                 				response.write("<li>\n"+
//	                                                "<a class=\"typeOption\">Check</a>\n"+
//	                                                "</li>\n");	
//	                                 			}				                        
//		    				            response.write("</ul>\n");
//	                            }
				            response.write(
				                "</div>\n"+
				                "<div class=\"hidden-form-elements\">\n"+
				                    "<input type=\"hidden\" name=\"bankingSecondary-<%- id %>-Country\" />\n"+
				                "</div>\n"+
				                "<input type=\"hidden\" class=\"type\" name=\"bankingSecondary-<%- id %>-Type\" value=\"<% if (id <= 9) { %>0<%- id%><% } else {%><%-id%><%}%>\" />\n"+
				                "<input type=\"hidden\" class=\"type-account\" name=\"bankingSecondaryAccount-<%- id %>-Type\" value=<%- (country === \"US\" && type.match(/[U]/))?\"WIRE\":\"ACH\" %> />\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip remove-secondary "+hideButton+" \" title=\"Remove\" data-id=\"<%- id %>\"></i>\n"+
				                "<a class=\"btn btn-mini edit-account edit-item accordion-toggle\" data-target=\"#secondary-account-<%- id %>\" data-parent=\"#secondary-account\">\n"+
				                	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                    "<i class=\"icon-pencil\"></i>Edit\n":
				                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+	// ENHC0019060
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body secondary-account collapse\" id=\"secondary-account-<%- id %>\">\n"+
				                "<div class=\"row-fluid\">\n"+
				                    "<div class=\"span6\">\n"+
				                    	// Change by Naga 999, change country to secondary-country
				                        "<select class=\"input-block-level secondary-country\" required=\"required\" name=\"secondary-account-<%- id %>-country\">\n"+
				                        "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
				                        for (int x = 0; x < arrayCountryCode.length; x++) {
				                        	response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
//				                        	// Only show US if it is political contribution ENHC0016458
//				                        	if ((decisionVendorType.equalsIgnoreCase("V040")) || (decisionVendorType.equalsIgnoreCase("R040"))){
//				                        		if(arrayCountryCode[x][0].equalsIgnoreCase("US"))
//				                        			response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
//				                        	}else{
//				                				response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");
//				                        	}
										}
	                                    response.write("</select>\n"+

				                            //"<option value=\"US\">United States</option>\n"+
				                            //"<option value=\"CA\">Canada</option>\n"+
				                            //"<option value=\"MX\">Mexico</option>\n"+
				                            //"<option value=\"AU\">Australia</option>\n"+
				                            //"<option value=\"NZ\">New Zealand</option>\n"+
				                            //"<option>Other</option>\n"+
				                        //"</select>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                "<div class=\"row-fluid account-type\"></div>\n"+
				                "<div class=\"row-fluid currency-container accept-usd\">\n"+
				                    "<div class=\"control-group span12 currency\">\n"+
				                        "<label class=\"control-label\">\n"+
				                            "Payment Currency different from Bank Country&apos;s Currency?\n"+
				                        "</label>\n"+
				                        "<div class=\"controls\">\n"+
				                            "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                                "<a class=\"btn yes-answer\"><i class=\"icon-ok-sign\"></i>Yes</a>\n"+
				                                "<a class=\"btn no-answer\"><i class=\"icon-ok-sign\"></i>No</a>\n"+
				                            "</div>\n"+
				                            "<div class=\"add-on currency-no\">\n"+
				                                "<label>Intermediary Bank</label>\n"+
				                                "<div class=\"int-container\">\n"+
				                                    "<div class=\"i-bank-item row-fluid\">\n"+
											"<div class=\"accordion ibank-accordion\" id=\"secondary<%- id %>-ibank-accordion\">\n"+
											    "<div class=\"accordion-group\">\n"+
											        "<div class=\"accordion-heading active\">\n"+
											            "<label class=\"accordion-toggle\" data-parent=\"#secondary<%- id %>-ibank-accordion\" href=\"#secondary<%- id %>-ibank1\">\n"+
											                "<div class=\"ibank-header\">New Intermediary Bank</div>\n"+
											                "<a class=\"btn btn-mini edit-ibank-item\" data-toggle=\"collapse\" data-target=\"#secondary<%- id %>-ibank1\" data-parent=\"#secondary<%- id %>-ibank-accordion\">\n"+
											                	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
											                    "<i class=\"icon-pencil\"></i>Edit\n":
											                    	"<i class=\"icon-chevron-sign-down\"></i> Expand\n")+	// ENHC0019060
											                "</a>\n"+
											                "<i class=\"icon-remove tip remove-ibank\" data-id=\"#secondary<%- id %>-ibank1\" title=\"Remove\"></i>\n"+
											            "</label>\n"+
											        "</div>\n"+
											        "<div id=\"secondary<%- id %>-ibank1\" class=\"accordion-body collapse in in\">\n"+
											            "<div class=\"accordion-inner\">\n"+
											                "<div class=\"span4 select-currency\">\n"+
				                                            "<label>\n"+
				                                                "Currency\n"+
				                                            "</label>\n"+
															"<select name=\"primary-int-currency1\" class=\"input-block-level\">\n"+
															    "<option value=\"\">Select Currency</option>\n"+
															    "<option value=\"usd\">USD</option>\n"+
															    "<option value=\"canadian\">CAD</option>\n"+
															    "<option value=\"peso\">MXN</option>\n"+
															    "<option value=\"australian\">ASD</option>\n"+
															    "<option value=\"newzealand\">NZD</option>\n"+
				                                            "</select>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"span4 select-country\">\n"+
				                                            "<label>\n"+
				                                                "Country of Intermediary Bank\n"+
				                                            "</label>\n"+
															"<select name=\"primary-int-country1\" class=\"input-block-level\">\n"+
															    "<option value=\"\">Select Country</option>\n"+
															    "<option value=\"us\">United States</option>\n"+
															    "<option value=\"ca\">Canada</option>\n"+
															    "<option value=\"mx\">Mexico</option>\n"+
															    "<option value=\"au\">Australia</option>\n"+
															    "<option value=\"nz\">New Zealand</option>\n"+
				                                            "</select>\n"+
				                                        "</div>\n"+
				                                        "<div class=\"span4 ibank-account\">\n"+
				                                            "<label>\n"+
				                                                "Intermediary Bank Account #\n"+
				                                            "</label>\n"+
				                                            "<input name=\"primary-account-<%- id %>-int-account1\" type=\"text\" class=\"input-block-level\" />\n"+
				                                          "</div>\n"+
												        "</div>\n"+
												       "</div>\n"+
												      "</div>\n"+
				                                     "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
/*				                                "<div class=\"add-i-bank\">\n"+
				                                    "<hr />\n"+
				                                    "<a class=\"btn btn-primary\">\n"+
				                                        "<i class=\"icon-plus\"></i>Add Intermediary Bank Info\n"+
				                                    "</a>\n"+
				                                "</div>\n"+*/
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                "</div>\n"+
				                
				                "<div class=\"upload-forms\">\n"+
				                "<div class=\"control-group pull-left\">\n"+
//				                    	(requestType.equals("2")?"<label class=\"upload-label control-label required-red\">\n":"<label class=\"upload-label control-label\">\n")+ // ENHC0013668
				                    	(!(requestType.equals("1")||(vendorId!=null&&vendorId.trim().length()>0))?"<label class=\"upload-label control-label required-red\">\n":"<label class=\"upload-label control-label\">\n")+ // ENHC0013668
				                            "Upload ACH Form\n"+
				                        "</label>\n"+
				                        "<div class=\"controls\">\n");
	                                    
                                		// Begin of Insert by Naga ENHC0013668
                                        // During the maintain all the forms to be optional
                                        // Making Secondary Accounts optional here.
                                        if(vendorId!=null && vendorId.trim().length()>0){
											response.write("<%	if ( fileName!=null ){ %>");
											response.write("<%	if ( fileName==\"null\" ){ %>");
												response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");														
											response.write("<%	} else { %>");
												response.write("<input type=\"file\" disabled name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
												response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid=<%- objectId %>&filename=<%- fileName %>\" target=\"_new\"><%- fileName %></a></span><i class=\"icon-remove remove-file\" fileId=\"<%- objectId %>\"></i>");
												
											response.write("<% } %> ");
										response.write("<%	} else { %>");
											response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");				                                                                    				
										response.write("<% } %> ");                                        	
                                        }else	                                    
				                        // Begin of Comment and Insert by Naga DFCT0013582            
			                            //"<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n"+
	                                    // Begin of Insert by Naga 998
	                                    // Forms are required only for non vendor login, for vendor login it is not. 
	                                    // Adding if and else
	                                    if(requestType.equals("2")){
											response.write("<%	if ( fileName!=null ){ %>");
											response.write("<%	if ( fileName==\"null\" ){ %>");
												response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" class=\"user-error\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");														
											response.write("<%	} else { %>");
												response.write("<input type=\"file\" disabled name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
												response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid=<%- objectId %>&filename=<%- fileName %>\" target=\"_new\"><%- fileName %></a></span><i class=\"icon-remove remove-file\" fileId=\"<%- objectId %>\"></i>");
											response.write("<% } %> ");
										response.write("<%	} else { %>");
											response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" class=\"user-error\" required data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");
											// Begin Added Pranesh(04/16/2016) - ENHC0016459
											response.write("<div class=\"alert alert-danger fileRequiredError\" style=\"margin-bottom: 20px; display: none;\">Form is required</div>\n");
											// End Added Pranesh(04/16/2016) - ENHC0016459
										response.write("<% } %> ");
										
	                                    }else{
											response.write("<%	if ( fileName!=null ){ %>");
											response.write("<%	if ( fileName==\"null\" ){ %>");
												response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");														
											response.write("<%	} else { %>");
												response.write("<input type=\"file\" disabled name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");		                                          				                                                                    		
												response.write("<span class=\"fileName\"><a href=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.displaycsdoc?documentid=<%- objectId %>&filename=<%- fileName %>\" target=\"_new\"><%- fileName %></a></span><i class=\"icon-remove remove-file\" fileId=\"<%- objectId %>\"></i>");
											response.write("<% } %> ");
										response.write("<%	} else { %>");
											response.write("<input type=\"file\" name=\"bankingSecondary-<%- id %>-AchForm\" fileType=\"<%- type %>\" data-url=\"/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.vra!2fcom.nbcu.iviews!2fcom.nbcu.services!2fcom.nbcu.managecsdoc\" />\n");				                                                                    				
										response.write("<% } %> ");	                                    	
	                                    }

										
										response.write(
														"<input type=\"hidden\" name=\"ACHFileInfo-<%- id %>\" value=\"<%- objectId %>\">\n"+
									     // End of Insert by Naga
				                         "</div>\n"+
				                    "</div>\n"+
				                    "<a class=\"pull-right blank-download-template\" href=\"/irj/go/km/docs/nbcu_km/Vendor%20Portal/Documents/ACH%20OnBoardingtemplate.doc\" target=\"_new\"><i class=\"icon-file\"></i>Download Blank Form</a>\n"+
				                    "<div class=\"clearfix\"></div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
				    "<script type=\"text/template\" id=\"secondaryAddressTemplate\">\n"+
				        "<div class=\"accordion-group\" id=\"secondary-address-group-<%- index %>\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<label class=\"item-label\"></label>\n"+
				                "<i class=\"icon-remove tip\" title=\"Remove\" data-index=\"<%- index %>\"></i>\n"+
				                "<a class=\"btn btn-mini edit-item\" href=\"#secondary-address-<%- id %>\" data-parent=\"#secondary-address\">\n"+
				                	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                    "<i class=\"icon-pencil\"></i>Edit\n":
				                    "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+ // ENHC0019060
				                "</a>\n"+
				                "<div class=\"clearfix\"></div>\n"+
				            "</div>\n"+
				            "<div class=\"accordion-body collapse secondary-item\" id=\"secondary-address-<%- id %>\">\n"+
				"\n"+
				                "<div class=\"accordion-inner\">\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label required-red\">Vendor Name</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level vendor-name special-char-validation\" required type=\"text\" name=\"secondaryAddress-view<%- id %>-vendorName\" maxlength=\"35\" pattern=\"[a-zA-Z0-9&'\\- \\/]+\" placeholder=\"Vendor Name\">\n"+
				                                	"<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 9.5pt;display: none;\">No special characters allowed only /,&,- and '.</div>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label \">Country</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<select class=\"input-block-level address-country\" name=\"secondaryAddress-view<%- id %>-country\">\n"+
				                                    "<option value=\"\">Select One</option>\n");//Req#50, Code added by AGAMPA on 2-19-2015.
				                                    		
															for (int x = 0; x < arrayCountryCode.length; x++) {
																response.write("<option value=\""+arrayCountryCode[x][0]+"\">"+arrayCountryCode[x][1]+"</option>");																			
															}
				                                    response.write("</select>\n"+
	                                                    //"<option value=\"US\">United States</option>\n"+
	                                                    //"<option value=\"CA\">Canada</option>\n"+
	                                                    //"<option value=\"MX\">Mexico</option>\n"+
	                                                    //"<option value=\"AU\">Australia</option>\n"+
	                                                    //"<option value=\"NZ\">New Zealand</option>\n"+
	                                                    //"<option>Other</option>\n"+
	                                                //"</select>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label required-red\">Address 1</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                	// Change by Naga ENHC0013660, remove alpha-num-validation from Address1 of both primary and secondary.
				                                	// Only slash should be allowed, previous change is corrected accordingly
				                                    //"<input class=\"input-block-level header-input address1 alpha-num-validation\" required type=\"text\" name=\"secondaryAddress-view<%- id %>-Address1\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Street Name\">\n"+
				                                	"<input class=\"input-block-level header-input address1 alpha-num-slash-validation\" maxlength=\"35\" required type=\"text\" name=\"secondaryAddress-view<%- id %>-Address1\" pattern=\"[a-zA-Z0-9-&/' ]+\" placeholder=\"Street Name\">\n"+ // Pranesh (04/26/2016) - Added (&/')in [a-zA-Z0-9 /],maxlength=35 - ENHC0013678
				                                    "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt; display: none;\">No special characters allowed only & ' /</div>"+   				                                
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
//				                                "<label class=\"control-label\">Address 2 <i class=\"icon-question-sign tip\" title=\"Building or Unit Number\" data-placement=\"right\"></i></label>\n"+	// ENHC0013668
				                            	"<label class=\"control-label\">Address 2 </label>\n"+	// ENHC0013668
				                                "<div class=\"controls\">\n"+
//				                                    "<input class=\"input-block-level address2 alpha-num-validation\" type=\"text\" name=\"secondaryAddress-view<%- id %>-Address2\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Building or Unit Number\">\n"+
//				                                "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Special character are not allowed.</div>"+    // ENHC0013668				                                
				                                	"<input class=\"input-block-level address2 alpha-num-slash-validation\" type=\"text\" maxlength=\"40\" name=\"secondaryAddress-view<%- id %>-Address2\"  pattern=\"[a-zA-Z0-9-&/' ]+\" >\n"+	// ENHC0013668 , Pranesh (04/26/2016) - Added (&/')in [a-zA-Z0-9 /],maxlength="40" - ENHC0013678
				                                    "<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only /</div>"+    // ENHC0013668
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
//				                                "<label class=\"control-label\">Address 3 <i class=\"icon-question-sign tip\" title=\"Suite or Room Number\" data-placement=\"right\"></i></label>\n"+ ENHC0013668
				                            "<label class=\"control-label\">Address 3 </label>\n"+ // ENHC0013668				                            
				                                "<div class=\"controls\">\n"+
//				                                    "<input class=\"input-block-level address3 alpha-num-validation\" type=\"text\" name=\"secondaryAddress-view<%- id %>-Address3\"  pattern=\"[a-zA-Z0-9 ]+\" placeholder=\"Suite or Room Number\">\n"+ ENHC0013668
//			                           			"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">Numbers and special character are not allowed.</div>"+ // ENHC0013668				                                
				                                "<input class=\"input-block-level address3 alpha-num-slash-validation\" type=\"text\" maxlength=\"40\" name=\"secondaryAddress-view<%- id %>-Address3\"  pattern=\"[a-zA-Z0-9-&/' ]+\" >\n"+ // ENHC0013668, Pranesh (04/26/2016) - Added (&/')in [a-zA-Z0-9 /],maxlength="40" - ENHC0013678
				                           			"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only /</div>"+     // ENHC0013668				                                
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"row-fluid validate-group\">\n"+
				                        "<div class=\"span6\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label required-red\">City</label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level city special-char-validation-exceptdash\" required type=\"text\" name=\"secondaryAddress-view<%- id %>-City\" pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"City\">\n"+
			                                        "<div class=\"alert alert-danger\" style=\"width: 85.5%; font-size: 10pt;display: none;\">No special characters allowed only - .</div>\n"+ 				                                
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label required-red\">State<span class=\"province-label hide\">/Province</span></label>\n"+
				                                "<div class=\"controls\">\n"+
				                                    "<select class=\"input-block-level state\" name=\"secondaryAddress-view<%- id %>-State\">\n"+
				                                        "<option value=\"\">Select State</option>\n"+
				                                        "<option>CA</option>\n"+
				                                    "</select>\n"+
				                                "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                        "<div class=\"span3\">\n"+
				                            "<div class=\"control-group\">\n"+
				                                "<label class=\"control-label zip required-red\" name=\"secondary<%- id %>Zip\">Zip<span class=\"postal-code-label hide\">/Postal</span> Code</label>\n"+  
				                                "<div class=\"controls\">\n"+
				                                    "<input class=\"input-block-level zip special-char-validation-exceptdash\" type=\"text\" required name=\"secondaryAddress-view<%- id %>-Zip\"  pattern=\"[a-zA-Z0-9- ]+\" placeholder=\"Zip-Code\">\n"+ // Pranesh Added "-" on parteen - Defect -15130 - (05/20/2016)
				          							"<div class=\"alert alert-danger\" style=\"width: 70.5%; font-size: 9pt; display: none;\">No special characters allowed only except -</div>"+ // Pranesh Defect : 15130 added line - (05/20/2016)
				                                    "<input type=\"hidden\" class=\"taxCode\"name=\"secondaryAddress-view<%- id %>-taxCode\">\n"+
				                                    "</div>\n"+
				                            "</div>\n"+
				                        "</div>\n"+
				                    "</div>\n"+
				                    "<div class=\"remit-option\">\n"+
				                        "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n"+
				                        	// By Naga ENHC0015302 -- Defaulted Remit
				                            "<button type=\"button\" class=\"btn btn-info remit-button "+disableButton+" active\"><i class=\"icon-check-sign\"></i>Remit</button>\n"+
				                            "<button type=\"button\" class=\"btn btn-info purchasing-button "+disableButton+" \"><i class=\"icon-check-sign\"></i>Purchasing</button>\n"+
				                            "<div class=\"hidden-form-elements\">\n"+
				                                "<input type=\"radio\" class=\"remit\" name=\"secondaryAddress-view<%- id %>-RemitPurchase\" value=\"remit\" checked/>\n"+
				                                "<input type=\"radio\" class=\"purchasing\" name=\"secondaryAddress-view<%- id %>-RemitPurchase\" value=\"purchasing\" />\n"+
				                            "</div>\n"+
				                            "<a href=\"#\" class=\"tip\" data-html=\"true\" data-placement=\"top\" data-title=\"<label>Remit</label><p>Decription of what a remit address is.</p><label>Purchasing</label><p>Decription of what a purchasing address is.</p>\">What is the difference?</a>\n"+
				                        "</div>\n"+
				                        "<div class=\"purchasing-contact\">\n"+
				                            "<span class=\"caret\"></span>\n"+
				                            "<div class=\"row-fluid\">\n"+
				                                "<div class=\"span6\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Email <i class=\"icon-question-sign tip\" data-placement=\"right\" title=\"Email address of the person who should receive an electronic copy of Purchase Order\"></i></label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<input class=\"input-block-level purchasing-email\" type=\"email\" name=\"secondaryAddress-view<%- id %>-purchasingEmail\" placeholder=\"person@email.com\">\n"+
				                                        "</div>\n"+
				                                    "</div>\n"+
				                                "</div>\n"+
				                                "<div class=\"span6\">\n"+
				                                    "<div class=\"control-group\">\n"+
				                                        "<label class=\"control-label\">Fax</label>\n"+
				                                        "<div class=\"controls\">\n"+
				                                            "<input class=\"input-block-level fax phone-number\" type=\"text\" name=\"secondaryAddress-view<%- id %>-purchasingFax\" placeholder=\"(XXX) XXX-XXXX\">\n"+
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
				
			// template for intermediary account	
				
				    "<script type=\"text/template\" id=\"intermediaryAccount\">\n"+
				        "<div class=\"accordion-group\">\n"+
				            "<div class=\"accordion-heading active\">\n"+
				                "<label class=\"accordion-toggle\" data-parent=\"<%- parent %>\" href=\"#<%- secondary %>ibank<%- count %>\">\n"+
				                    "<div class=\"ibank-header\">New Intermediary Bank</div>\n"+
				                    "<a class=\"btn btn-mini edit-ibank-item\" data-toggle=\"collapse\" data-target=\"#<%- secondary %>ibank<%- count %>\" data-parent=\"<%- parent %>\">\n"+
				                    	((!applicationMode.equalsIgnoreCase("locked"))?	// ENHC0019060
				                        "<i class=\"icon-pencil\"></i>Edit\n":
				                        "<i class=\"icon-chevron-sign-down\"></i> Expand\n")+	// ENHC0019060
				                    "</a>\n"+
				                    "<i class=\"icon-remove tip remove-ibank\" data-id=\"#<%- secondary %>ibank<%- count %>\" title=\"Remove\"></i>\n"+
				                "</label>\n"+
				            "</div>\n"+
				            "<div id=\"<%- secondary %>ibank<%- count %>\" class=\"accordion-body collapse in\">\n"+
				                "<div class=\"accordion-inner\">\n"+
				                    "<div class=\"span4 select-currency\">\n"+
				                        "<label>\n"+
				                            "Currency\n"+
				                        "</label>\n"+
				                        "<select name=\"<%- id %>-int-currency<%- count %>\" class=\"input-block-level\">\n"+
				                            "<option value=\"\">Select Currency</option>\n"+
				                            "<option value=\"usd\">USD</option>\n"+
				                            "<option value=\"canadian\">CAD</option>\n"+
				                            "<option value=\"peso\">MXN</option>\n"+
				                            "<option value=\"australian\">ASD</option>\n"+
				                            "<option value=\"newzealand\">NZD</option>\n"+
				                        "</select>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4 select-country\">\n"+
				                        "<label>\n"+
				                            "Country of Intermediary Bank\n"+
				                        "</label>\n"+
				                        "<select name=\"<%- id %>-int-country<%- count %>\" class=\"input-block-level\">\n"+
				                            "<option value=\"\">Select Country</option>\n"+
				                            "<option value=\"us\">United States</option>\n"+
				                            "<option value=\"canadian\">Canada</option>\n"+
				                            "<option value=\"mexico\">Mexico</option>\n"+
				                            "<option value=\"australia\">Australia</option>\n"+
				                            "<option value=\"newzealand\">New Zealand</option>\n"+
				                        "</select>\n"+
				                    "</div>\n"+
				                    "<div class=\"span4 ibank-account\">\n"+
				                        "<label>\n"+
				                            "Intermediary Bank Account #\n"+
				                        "</label>\n"+
				                        "<input name=\"<%- id %>-int-account<%- count %>\" type=\"text\" class=\"input-block-level\" />\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				    
			// template for usach	    
				    "<script id=\"banking-usach\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
			                 "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+// added by ganesh

				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Bank Routing #</label>\n"+
				                    "<div class=\"controls\">\n");
				                     response.write("<!-- Check Allowed Naga Primary ACH"+checkAllowed+"-->");				                                    
				                     // Begin of comment and Insert by Naga	DFCT0013582               
				                     /*if (checkAllowed){
				                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n");
				                     } else {
				                        response.write("<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n");			                    	 
				                     }*/
				                     response.write("<input class=\"input-block-level banking-routing-num user-error\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n");
				                     // End of comment and Insert by Naga
				                       response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n");
				                     // Begin of comment and Insert by Naga	DFCT0013582  
				                    /* if (checkAllowed){				                       

				                        response.write("<input class=\"input-block-level header-input\" type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n");
				                     } else {
				                        response.write("<input class=\"input-block-level header-input\" required type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n");				                    	 
				                     } */

				                     response.write("<input class=\"input-block-level header-input user-error\" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n");// ganesh

					            //     response.write("<input class=\"input-block-level header-input user-error\" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"); //commented by ganesh
				                     // End of comment and Insert by Naga//
				                response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                	// Added required-red dynamic - Pranesh(04/16/2016)
				                    "<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
				                    "<div class=\"controls\">\n");
				                	 // Begin of comment and Insert by Naga	DFCT0013582
				                     /* if (checkAllowed){				                       
	
				                        response.write("<input class=\"input-block-level holder-name\" type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");
				                     } else {
				                        response.write("<input class=\"input-block-level holder-name\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");				                    	 
				                     } */
			                		response.write("<input class=\"input-block-level holder-name user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");				                
				                     // End of comment and Insert by Naga
				                response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
				    "</script>\n"+
		
				    // template for uswire
				    
				    "<script id=\"banking-uswire\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Bank Routing #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582, All fields of Secondary accounts are mandatory
				                        "<input class=\"input-block-level banking-routing-num\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
					                "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

				                    	// By Naga DFCT0013582, All fields of Secondary accounts are mandatory
				                        "<input class=\"input-block-level header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Account Holder Name</label>\n"+
				                    "<div class=\"controls\">\n");
				                	 // Begin of comment and Insert by Naga	DFCT0013582
				                     /*if (checkAllowed){				                       
				                        response.write("<input class=\"input-block-level holder-name\" type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");
				                     } else {
				                        response.write("<input class=\"input-block-level holder-name\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");				                    	 
				                     }*/
				                	response.write("<input class=\"input-block-level holder-name\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");
				                     // End of comment and Insert by Naga
				                response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            //"<div class=\"span4\">\n"+
				            //    "<div class=\"control-group\">\n"+
				            //        "<label class=\"control-label\">SWIFT Account #</label>\n"+
				            //        "<div class=\"controls\">\n"+
				            //            "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
				            //        "</div>\n"+
				            //    "</div>\n"+
				            //"</div>\n"+
				        "</div>\n"+
				        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
				       /* "<div class=\"row-fluid\">\n"+
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">Email Contact</label>\n"+
				                    "<div class=\"controls\">\n"+
				                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+*/
				    "</script>\n"+
// template for camx
				    "<script id=\"banking-camx\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				           	"<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                	// By Naga 998 Typo error
//				                    "<label class=\"control-label required-red\"> SWIFT Account #</label>\n"+
				                	"<label class=\"control-label required-red\"> Bank Key</label>\n"+

				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
				                    	// Make all details of secondary account required 
				                    	// SWIFT and IBAN are now optional ENHC0013668
				                        "<input class=\"input-block-level banking-routing-num \" type=\"text\" name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
			                    		// Make all details of secondary account required	
					                "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<% -tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

				                        "<input class=\"input-block-level header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
//				            Begin of Comment and Insert by Naga ENHC0013668
//				            "<div class=\"span4\">\n"+
//				                "<div class=\"control-group\">\n"+
//				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//				                    "<div class=\"controls\">\n"+
//				                    	// By Naga DFCT0013582
//				                    	// Make all details of secondary account required				                    
//				                        "<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//				                    "</div>\n"+
//				                "</div>\n"+
//				            "</div>\n"+
				            "<div class=\"span4\">\n"+
			                	"<div class=\"control-group\">\n"+
			                    	"<label class=\"control-label required-red\">Account Holder Name</label>\n"+
			                    	"<div class=\"controls\">\n"+
			                        	"<input class=\"input-block-level \" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+// removed error class by ganesh
			                        "</div>\n"+
			                    "</div>\n"+
			                "</div>\n"+
//				            End of Comment and Insert by Naga				            
				        "</div>\n"+
				      	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
				      	// Begin of Insert by Naga ENHC0013668
				        "<div class=\"row-fluid\">\n"+
			            	"<div class=\"span4\">\n"+
			            		"<div class=\"control-group\">\n"+
			            			"<label class=\"control-label\">SWIFT Account #</label>\n"+
			            			"<div class=\"controls\">\n"+
			            			// SWIFT and IBAN are optional always ENHC0013668
			            			"<input class=\"input-block-level \" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
			            			"</div>\n"+
			            		"</div>\n"+
			            	"</div>\n"+				        
				        "</div>\n"+				        
				      	// End of Insert by Naga
				    "</script>\n"+
				"\n"+
				    "<script id=\"banking-aunz\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\"> BSB #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
				                    	// Make all details of secondary account required				                    
				                        "<input class=\"input-block-level banking-routing-num user-error\" required type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label required-red\">  Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
				                    	// Make all details of secondary account required	
					                "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

				                        "<input class=\"input-block-level header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				            	// Begin of Comment and Insert by Naga ENHC0013668
//				                "<div class=\"control-group\">\n"+
//				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//				                    "<div class=\"controls\">\n"+
//				                        "<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//				                    "</div>\n"+
//				                "</div>\n"+
			                	"<div class=\"control-group\">\n"+
			                    	"<label class=\"control-label required-red\">Account Holder Name</label>\n"+
			                    	"<div class=\"controls\">\n"+
			                        	"<input class=\"input-block-level \" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+// removed error class by ganesh
			                        "</div>\n"+
		                        "</div>\n"+				            
				             // End of Comment and Insert by Naga 				                
				            "</div>\n"+
				        "</div>\n"+
				        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
				        "<div class=\"row-fluid\">\n"+
		            	// Begin of Insert by Naga ENHC0013668				        
			            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// SWIFT and IBAN are optional always ENHC0013668
				                        "<input class=\"input-block-level \" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
				                    "</div>\n"+
				                "</div>\n"+
 				                
			            	"</div>\n"+
//		            		End of Insert by Naga
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                	// SWIFT and IBAN are optional always ENHC0013668
//				                    "<label class=\"control-label required-red\"> IBAN #</label>\n"+ // ENHC0013668
				                    "<label class=\"control-label \"> IBAN #</label>\n"+ // ENHC0013668
				                    "<div class=\"controls\">\n");
				                		// Begin of comment and insert by Naga DFCT0013582
				                		// Make all details of secondary account required				                
										/*if (vendorType.equals("2") && (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
				                       		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
					                     } else {
				                       		response.write("<input class=\"input-block-level\" required type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
					                     }*/
				                		// Now make SWIFT and IBAN are optional always ENHC0013668 :)
										response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
										// End of comment and insert by Naga
				                   response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
			// template for banking others
				
				    "<script id=\"banking-others\" type=\"text/template\">\n"+
				        "<div class=\"row-fluid\">\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Key</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
				                    	// Make all details of secondary account required				                    
				                        "<input class=\"input-block-level banking-routing-num \" "+bankingRequiredInput+" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				                "<div class=\"control-group\">\n"+
				                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
				                    "<div class=\"controls\">\n"+
				                    	// By Naga DFCT0013582
				                    	// Make all details of secondary account required		
					                "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%- tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

				                        //"<input class=\"input-block-level header-input\" "+bankingRequiredInput+" type=\"text\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+
				                    "<input class=\"input-block-level header-input\" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+// removed error class by ganesh
				                    "</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div class=\"span4\">\n"+
				            // Begin of Comment and Insert by Naga ENHC0013668
//				                "<div class=\"control-group\">\n"+
//				                    "<label class=\"control-label "+bankingRequiredDisplay+"\">SWIFT Account #</label>\n"+
//				                    "<div class=\"controls\">\n"+
//				                    	// By Naga DFCT0013582
//				                    	// Make all details of secondary account required				                    
//				                        //"<input class=\"input-block-level\" "+bankingRequiredInput+" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//				                    	"<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//				                    "</div>\n"+
//				                "</div>\n"+
				                "<div class=\"control-group\">\n"+
			                    	"<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
			                    	"<div class=\"controls\">\n"+
			                    	// Added user-error Pranesh(04/16/2016) - ENHC0016459
			                    		"<input class=\"input-block-level holder-name user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+// removed error class by ganesh
			                    	"</div>\n"+
			                    "</div>\n"+
		                // End of Comment and Insert by Naga ENHC0013668				            
				            "</div>\n"+
				        "</div>\n"+
				   		"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
				        "<div class=\"row-fluid\">\n"+
				        	// Begin of Insert by Naga ENHC0013668
				            "<div class=\"span4\">\n"+
				            	"<div class=\"control-group\">\n"+
			                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
			                    	"<div class=\"controls\">\n"+
			                    		"<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
			                    	"</div>\n"+
			                    "</div>\n"+
			                "</div>\n"+
				        	// End of Insert by Naga
				            "<div class=\"span6\">\n"+
				                "<div class=\"control-group\">\n"+
				                	// SWIFT and IBAN are optional always ENHC0013668
//				                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> IBAN #</label>\n"+	// ENHC0013668
				                    "<label class=\"control-label\"> IBAN #</label>\n"+	// ENHC0013668
				                    "<div class=\"controls\">\n");
				                		// Begin of comment and insert by Naga DFCT0013582
				                		// Make all details of secondary account required				                
				                   
					                    /*if (vendorType.equals("2") && (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
				                       		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
					                     } else {
				                       		response.write("<input class=\"input-block-level\" required type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
					                     }*/
				                   		// SWIFT and IBAN are optional always ENHC0013668
				                   		response.write("<input class=\"input-block-level \" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");				                   
				                   response.write("</div>\n"+
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				"\n"+
					// Begin of Insert by Naga 999
				    "<script id=\"banking-usach-primary\" type=\"text/template\">\n"+
			        "<div class=\"row-fluid\">\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                  //TEST marker2 "ACH" - Pranesh(13-04-2016)-ENHC0016459
			                    "<label class=\"control-label required-red\"> Bank Routing #</label>\n"+
			                    "<div class=\"controls\">\n");
				                 response.write("<!-- Check Allowed Naga Primary.. ACH"+checkAllowed+"-->");
			                     if (checkAllowed){
			                        response.write("<input class=\"input-block-level primary-banking-routing-num\" type=\"text\" name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing. #\" value=\"<%- routingNum %>\">\n");
			                     } else {
			                        response.write("<input class=\"input-block-level primary-banking-routing-num user-error\" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n");			                    	 
			                     }
			                       response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                // TEST - Pranesh(04/17/2016)
			                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
			                    "<div class=\"controls\">\n");
			                       response.write("<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"); // added by ganesh

			                     if (checkAllowed){				                       
			                        response.write("<input class=\"input-block-level primary-header-input\" type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n");
			                     } else {
			                        response.write("<input class=\"input-block-level primary-header-input user-error\" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n");				                    	 
			                     }
			                response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                	// Added required-red dynamic - Pranesh(04/16/2016)
			                    "<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
			                    "<div class=\"controls\">\n");
			                     if (checkAllowed){				                       
			                        response.write("<input class=\"input-block-level primary-holder-name\" type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");
			                     } else {
		                		response.write("<input class=\"input-block-level primary-holder-name user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");				                    	 
			                     } 
			                response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n"+
			        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
			    "</script>\n"+
			    "<script id=\"banking-uswire-primary\" type=\"text/template\">\n"+
			        "<div class=\"row-fluid\">\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\"> Bank Routing #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                        "<input class=\"input-block-level primary-banking-routing-num \" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                    "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh
			                        "<input class=\"input-block-level primary-header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\"> Account Holder Name</label>\n"+
			                    "<div class=\"controls\">\n");
			                     if (checkAllowed){				   
			                        response.write("<input class=\"input-block-level primary-holder-name\" type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");
			                     } else {
			                    	 response.write("<input class=\"input-block-level primary-holder-name user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n");				                    	 
			                     }
			                response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            //"<div class=\"span4\">\n"+
			            //    "<div class=\"control-group\">\n"+
			            //        "<label class=\"control-label\">SWIFT Account #</label>\n"+
			            //        "<div class=\"controls\">\n"+
			            //            "<input class=\"input-block-level\" type=\"text\" required name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\""+bankingPrimarySWIFTAcocunt+"\">\n"+
			            //        "</div>\n"+
			            //    "</div>\n"+
			            //"</div>\n"+
			        "</div>\n"+
			        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account #, Bank Routing # and Holder Name are required</div>\n"+
			       /* "<div class=\"row-fluid\">\n"+
			            "<div class=\"span6\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label\">Email Contact</label>\n"+
			                    "<div class=\"controls\">\n"+
			                        "<input class=\"input-block-level\" type=\"email\" required name=\"banking-<%- id %>-Email\" placeholder=\"Email Contact\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n"+*/
			    "</script>\n"+
	
			    "<script id=\"banking-camx-primary\" type=\"text/template\">\n"+
			        "<div class=\"row-fluid\">\n"+
			           	"<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                	// By Naga 998 Typo
//			                    "<label class=\"control-label required-red\"> SWIFT Account #</label>\n"+
		                		"<label class=\"control-label required-red\"> Bank Key</label>\n"+			                
			                    "<div class=\"controls\">\n"+
			                    	// SWIFT and IBAN are optional always ENHC0013668
			                        "<input class=\"input-block-level primary-banking-routing-num \" type=\"text\" name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\"> Bank Account #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                    "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<%-tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

			                        "<input class=\"input-block-level primary-header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			            	// Begin of Comment and Insert by Naga ENHC0013668
//			                "<div class=\"control-group\">\n"+
//			                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//			                    "<div class=\"controls\">\n"+
//			                        "<input class=\"input-block-level \" required type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//			                    "</div>\n"+
//			                "</div>\n"+
	                	"<div class=\"control-group\">\n"+
                    	"<label class=\"control-label required-red\">Account Holder Name</label>\n"+
                    	"<div class=\"controls\">\n"+
                        	"<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+
                        "</div>\n"+
                        "</div>\n"+				            
	             // End of Comment and Insert by Naga			            
			            "</div>\n"+
			        "</div>\n"+
			      	"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
			      	// Begin of Insert by Naga ENHC0013668
			        "<div class=\"row-fluid\">\n"+
		            	"<div class=\"span4\">\n"+
		            		"<div class=\"control-group\">\n"+
		            			"<label class=\"control-label\">SWIFT Account #</label>\n"+
		            			"<div class=\"controls\">\n"+
		            			// SWIFT and IBAN are optional always ENHC0013668
		            			"<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
		            			"</div>\n"+
		            		"</div>\n"+
		            	"</div>\n"+				        
			        "</div>\n"+				        
			      	// End of Insert by Naga			      	
			    "</script>\n"+
			"\n"+
			    "<script id=\"banking-aunz-primary\" type=\"text/template\">\n"+
			        "<div class=\"row-fluid\">\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\"> BSB #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                        "<input class=\"input-block-level primary-banking-routing-num \" required type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label required-red\">  Bank Account #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                    "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"<% -tempaccountNum %>\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

			                        "<input class=\"input-block-level primary-header-input \" required type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
		            	// Begin of Comment and Insert by Naga ENHC0013668			            
//			                "<div class=\"control-group\">\n"+
//			                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
//			                    "<div class=\"controls\">\n"+
//			                        "<input class=\"input-block-level \" required type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//			                    "</div>\n"+
//			                "</div>\n"+
			            "<div class=\"control-group\">\n"+
                    	"<label class=\"control-label required-red\">Account Holder Name</label>\n"+
                    	"<div class=\"controls\">\n"+
                        	"<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+
                        "</div>\n"+
                    "</div>\n"+				            
	             // End of Comment and Insert by Naga 				            			            
			            "</div>\n"+
			        "</div>\n"+
			        "<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
			        "<div class=\"row-fluid\">\n"+
	            	// Begin of Insert by Naga ENHC0013668				        
		            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label\">SWIFT Account #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                    	// SWIFT and IBAN are optional always ENHC0013668
			                        "<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
				                
		            	"</div>\n"+
//	            		End of Insert by Naga			        
			            "<div class=\"span6\">\n"+
			                "<div class=\"control-group\">\n"+
			                	// SWIFT and IBAN are optional always ENHC0013668
//			                    "<label class=\"control-label required-red\"> IBAN #</label>\n"+ // ENHC0013668
			                    "<label class=\"control-label\"> IBAN #</label>\n"+ // ENHC0013668
			                    "<div class=\"controls\">\n");
					             // Begin of Comment and Insert by Naga ENHC0013668				
					             // SWIFT and IBAN are optional always ENHC0013668
//									if (vendorType.equals("2") && (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){				                       
//			                       		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
//				                     } else {
//										response.write("<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
//				                     }
		                       		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
						             // End of Comment and Insert by Naga									
			                   response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n"+
			    "</script>\n"+
			"\n"+
			    "<script id=\"banking-others-primary\" type=\"text/template\">\n"+
			        "<div class=\"row-fluid\">\n"+
			            "<div class=\"span4\">\n"+
			            // TEST marker for "Wire" - Pranesh,(04/17/2016)
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Key</label>\n"+
			                    "<div class=\"controls\">\n"+
			                        "<input class=\"input-block-level primary-banking-routing-num \" "+bankingRequiredInput+" type=\"text\" required name=\"banking-<%- id %>-RoutingNum\" placeholder=\"Bank Routing #\" value=\"<%- routingNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			                "<div class=\"control-group\">\n"+
			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> Bank Account #</label>\n"+
			                    "<div class=\"controls\">\n"+
			                    "<input type=\"hidden\" id=\"hidden-banking-<%- id %>\" value=\"\" name=\"hidden-banking-<%- id %>\"  ></input>\n"+ // added by ganesh

			                        "<input class=\"input-block-level primary-header-input\" "+bankingRequiredInput+" type=\"text\" id=\"banking-<%- id %>-AccountNum\" name=\"banking-<%- id %>-AccountNum\" placeholder=\"Bank Account #\" value=\"<%- accountNum %>\">\n"+
			                    "</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			            "<div class=\"span4\">\n"+
			            	// Begin of Comment and Insert by Naga ENHC0013668
//			                "<div class=\"control-group\">\n"+
//			                    "<label class=\"control-label "+bankingRequiredDisplay+"\">SWIFT Account #</label>\n"+
//			                    "<div class=\"controls\">\n"+
//			                        "<input class=\"input-block-level\" "+bankingRequiredInput+" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
//			                    "</div>\n"+
//			                "</div>\n"+
			                "<div class=\"control-group\">\n"+
		                    	"<label class=\"control-label "+bankingRequiredDisplay+"\">Account Holder Name</label>\n"+
		                    	"<div class=\"controls\">\n"+
		                    		"<input class=\"input-block-level user-error holder-name\" required type=\"text\" name=\"banking-<%- id %>-HolderName\" placeholder=\"Account Holder Name\" value=\"<%- accountHolder %>\">\n"+
		                    	"</div>\n"+
	                    	"</div>\n"+			            
			            	// End of Comment and Insert by Naga ENHC0013668			            
			            "</div>\n"+
			        "</div>\n"+
			   		"<div  class=\"alert alert-danger bankRoutingOrAccountNumberWarning\" style=\"margin-bottom: 20px; display:none;\">Bank Account # and Bank Routing # required</div>\n"+
			        "<div class=\"row-fluid\">\n"+
			        	// Begin of Insert by Naga ENHC0013668
			            "<div class=\"span4\">\n"+
			            	"<div class=\"control-group\">\n"+
		                    "<label class=\"control-label \">SWIFT Account #</label>\n"+
		                    	"<div class=\"controls\">\n"+
		                    		"<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-SwiftNum\" placeholder=\"SWIFT Account #\" value=\"<%- swiftAccountNum %>\">\n"+
		                    	"</div>\n"+
		                    "</div>\n"+
		                "</div>\n"+
			        	// End of Insert by Naga			        
			            "<div class=\"span6\">\n"+
			                "<div class=\"control-group\">\n"+
			                	// SWIFT and IBAN are optional always ENHC0013668
//			                    "<label class=\"control-label "+bankingRequiredDisplay+"\"> IBAN #</label>\n"+ // ENHC0013668
			                    "<label class=\"control-label \"> IBAN #</label>\n"+ // ENHC0013668
			                    "<div class=\"controls\">\n");
					                // Begin of Comment and Insert by Naga ENHC0013668				
					                // SWIFT and IBAN are optional always ENHC0013668
			                   
//			                   		response.write("<!-- Check Allowed"+checkAllowed+"-->");
//				                    //Naga999if (vendorType.equals("2") && (decisionVendorType.equalsIgnoreCase("R030")) || (decisionVendorType.equalsIgnoreCase("R040")) || (decisionVendorType.equalsIgnoreCase("R050")) || (decisionVendorType.equalsIgnoreCase("R070")) ||  (decisionVendorType.equalsIgnoreCase("R080"))){
//			                   		if(checkAllowed){	
//			                       		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
//				                     } else {
//				                    	 response.write("<input class=\"input-block-level user-error\" required type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");
//				                     }
			                   		response.write("<input class=\"input-block-level\" type=\"text\" name=\"banking-<%- id %>-IbanNum\" placeholder=\"IBAN #\" value=\"<%- ibanNum %>\">\n");			                   
					                // End of Comment and Insert by Naga			                   		
			                   response.write("</div>\n"+
			                "</div>\n"+
			            "</div>\n"+
			        "</div>\n"+
			    "</script>\n"+
			    "\n"+				
					// End of Insert by Naga 999
				    "<script id=\"addEmailTemplate\" type=\"text/template\">\n"+
				        "<div class=\"accordion-group\">\n"+
				            "<div class=\"accordion-heading\">\n"+
				                "<div class=\"accordion-toggle\" data-parent=\"#email-accordion\">\n");
				                response.write("<!-- Check Allowed Pranesh.,"+bankingRequiredDisplay+"-->");
				                response.write("<div class=\"email-header "+bankingRequiredDisplay+"\">New Email Contact</div>\n"+
				                    //Commented by AGAMPA for Req # 603 on 27-Feb-2015
//				                    "<a class=\"btn btn-mini btn-primary edit-email-item\" data-toggle=\"collapse\" data-target=\"#emailContact-<%- count %>\" data-parent=\"#email-accordion\">\n"+
//				                        "<i class=\"icon-pencil\"></i>Edit\n"+
//				                    "</a>\n"+
				                    //Commented by AGAMPA for Req # 603 on 23-Feb-2015
				                    "<i class=\"icon-remove tip remove-email\" data-id=\"#emailContact-<%- count %>\" title=\"Remove\"></i>\n"+
				                "</div>\n"+
				            "</div>\n"+
				            "<div id=\"emailContact-<%- count %>\" class=\"accordion-body \">\n"+
				                "<div class=\"accordion-inner\">\n"+
				                    // Added Begin user-error Pranesh(04/16/2016) - ENHC0016459
				                    	"<input type=\"email\" "+bankingRequiredInput+" name=\"emailContact-<%- count %>\" class=\"span6 user-error\" />\n"+
				                    // Added End user-error Pranesh(04/16/2016) - ENHC0016459
				                "</div>\n"+
				            "</div>\n"+
				        "</div>\n"+
				    "</script>\n"+
				    
				    // Begin of Insert by Naga ENHC0013682
				    "<div class=\"modal hide fade\" id=\"cancelRequest\">\n"+
			        "<div class=\"modal-header\">\n"+
						"<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n"+
			            "<h3>Please select cancel reason</h3>\n"+
			        "</div>\n"+
			        "<form id=\"cancelRequest\">\n"+
//			        	"<input type=\"hidden\" name=\"invitenum\" id=\"invitenum\" value=\""+inviteNumber2+"\"/>\n"+
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
									"<option value=\"04\">Vendor already existing in SAP</option>\n"+
									"<option value=\"06\">Not a US Vendor</option>\n"+
									"<option value=\"07\">Vendor created via Support Central</option>\n"+
									"<option value=\"09\">Did not pass Independent Contractor review</option>\n"+
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
				    // End of Insert by Naga

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
				    "<script src=\"/"+ServerString+"/js/maskedInputX.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/securenumbermask.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.ui.widget.js\"></script>\n"+
					"<script src=\"/"+ServerString+"/js/jquery-ui-1.10.4.accordion.min.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.iframe-transport.js\"></script>\n"+
				    "<script src=\"/"+ServerString+"/js/jquery.fileupload.js\"></script>\n"+				    
				    "<script src=\"/"+ServerString+"/js/common.js\"></script>\n"+
				    // Begin of insert Pranesh - (04/20/2016)
				    	"<script src=\"/"+ServerString+"/js/BankAccounts.js\"></script>\n"+
				    // End of insert Pranesh - (04/20/2016)
				    "<script src=\"/"+ServerString+"/js/vendor.js?ver=1.1\"></script>\n"+
				    "<!-- Status : "+status+" -->\n"+				    
			    "<script>\n");
			        // Begin of Comment and Insert by Naga ENHC0019060           
                    // Change by Naga ENHC0013658 add "D" auto reject, which should work similar to Rejected "R"				                   	
//			        if ((status.equalsIgnoreCase("S")) ||(status.equalsIgnoreCase("F")) ||(status.equalsIgnoreCase("R")) || (status.equalsIgnoreCase("D")) || (status.equalsIgnoreCase("X")) || (status.equalsIgnoreCase("Y"))){
//		    			if (userIsInternalEmployeeInviter || userIsInternalEmployeeBuyer || userIsJointVenture){
//		    				response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"maintain\" });\n");
//		    			} else {
//		    				response.write("app.page.registrationView = new app.views.RegistrationView();\n");				    				
//		    			}
//		    		
//		    		} else {
//		    			response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"locked\" });\n");			    			
//		    		}
			        if(mode.equalsIgnoreCase("approval")){
			        	response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"approval\" });\n");
					} else if(applicationMode.equalsIgnoreCase("maintain")){
			        	response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"maintain\" });\n");
			        }else if(applicationMode.equalsIgnoreCase("locked")){
			        	response.write("app.page.registrationView = new app.views.RegistrationView({ mode: \"locked\" });\n");
			        }else{
			        	response.write("app.page.registrationView = new app.views.RegistrationView();\n");
			        }		    		
		    		// End of Comment and Insert by Naga

			    response.write("</script>\n");
			    
				        if ((request.getServletRequest().getServerName().equalsIgnoreCase("coding.nbcuni.com") || (request.getServletRequest().getServerName().equalsIgnoreCase("vendor.nbcuni.com")))) {
				          response.write("<script src=\"/"+ServerString+"/js/sap_portal_omniture.js\"></script>\n");
		                  response.write("<script  type='text/javascript'>\n"+
								"//Omniture Code start\n"+
								"s.pageName='VRA Maintain Vendor';\n"+  // Enter Page name to be tracked
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
				        }//
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