package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

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

public class htmlhelper extends AbstractPortalComponent
{
  String WFSystemAlias = "SAP_R3";
	
   public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {

    	String strLogMessasge = "";
	   	String inputFunction = request.getParameter("type");
    	String inputQuery = request.getParameter("query");
    	String inputCompanyCode = request.getParameter("CompanyCode");
    	String inputDate = request.getParameter("date");
//    	String inputVendor = request.getParameter("vendor");
    	String inputVendor = request.getParameter("vendorId");
    	String inputVendorType = request.getParameter("vendorType");
    	String inputRequestType = request.getParameter("requestType");
    	String inputMFC = request.getParameter("MFC");
    	String inputCountry = request.getParameter("c");
    	String inputCity = request.getParameter("city");  
    	String inputState = request.getParameter("state");     	
     	String inputZip = request.getParameter("zip");
     	String inputTaxId = request.getParameter("taxid");
     	String vendorCat = request.getParameter("vendorCat");
     	//Begin of Insert CTI w8 Foreign vendor
     	String taxCountryCTI = request.getParameter("taxCountryCTI");
     	String reqIdCTI = request.getParameter("reqIdCTI");
     	String sso = request.getParameter("SSO");
     	String reqTypeCTI = request.getParameter("reqTypeCTI");
		String vendEntQ1 = request.getParameter("vendorEntity");
		String vendEntQ2 = request.getParameter("vendorEntityLoc");
		String vendEntQ3 = request.getParameter("vendorIndvLoc");
		String vendEntQ4 = request.getParameter("vendorIndvResidence");
		String vendEntQ5 = request.getParameter("vendorIndvPresence");     	
     	//End of Insert CTI w8 Foreign vendor
    	
    	if (inputQuery != null){
			inputQuery = inputQuery.toUpperCase();
		}
    	
		if (inputCompanyCode != null){
			inputCompanyCode = inputCompanyCode.toUpperCase();
		}
	
		if (inputVendor != null){
			inputVendor = inputVendor.toUpperCase();
		}
 
		if (inputMFC != null){
			inputMFC = inputMFC.toUpperCase();
		}
		
    	JCO.Table ret = null;
    	
    	try {
			
    		HttpServletResponse resp = request.getServletResponse(true);
  			IPortalComponentProfile profile = request.getComponentContext().getProfile();
  			
    		PrintWriter response = resp.getWriter();

			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	
			    		
			//Get User ID
			String userId = request.getUser().getName();
			
			//get a client service
			IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
			JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);

			// connect to SAP system
			client.connect();

			// Query
			IRepository m_Repository = JCO.createRepository("repository", client);

			if (inputFunction.equalsIgnoreCase("coCodes")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I511_CO_PO_W2W");

				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();
				importList.setValue(inputQuery, "I_BUKRS");
				importList.setValue("X", "I_FLAG");
				
				client.execute(function);
				
				ret = function.getExportParameterList().getTable("IT_COMPANY_PO");			
				
				int maxRows = 500;
				
				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				
				response.write("[");
					
				for(int i = 0; i < maxRows; i++) {
					response.write("{\"Code\":\""+ret.getString("WERKS")+"\",");	
					response.write("\"Description\":\""+ret.getString("BUTXT")+"\"}");
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
						
				response.write("]");
											
				client.disconnect();
						
			}  else if (inputFunction.equalsIgnoreCase("vendorNumber")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I485_VENDOR_LOOKUP");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				importList.setValue(inputQuery, "I_VENDOR");	
 
				client.execute(function);
				ret = function.getTableParameterList().getTable("T_VENDOR_DTLS");			
				
				int maxRows = 500;
				
				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				
				response.write("[");
				
				for(int i = 0; i < maxRows; i++) {
					response.write("{\"Code\":\""+ret.getString("LIFNR")+"\",");	
					response.write("\"Description\":\""+ret.getString("NAME1")+"\"}");
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
				
				response.write("]");
											
				client.disconnect();
				
			// TODO : Approver List	
			} else if (inputFunction.equalsIgnoreCase("displayApproverList")){
				 ArrayList<String> companyCodes = new ArrayList<String>();
				 
		 		try {
			    	String[] urlParamters = request.getServletRequest().getParameterValues("query[]");						    	
			      
				     for (int i = 0; i < urlParamters.length; i++) {
					     companyCodes.add(urlParamters[i]);					     
					 }
				         
			    } catch (Exception coCodeEx){			    	
			    	throw new Exception (coCodeEx.toString());						    	
			    }

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SFI_I486_APPVR_CAM");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				JCO.Table T_CCODE = function.getImportParameterList().getTable("T_CCODE");
				JCO.Table IT_INVLD_SSO = function.getImportParameterList().getTable("IT_INVLD_SSO");
				importList.setValue("S", "I_ATYP");	

  				
				//Loop Through Company Codes
				for (int i = 0; i < companyCodes.size(); i++) {
					T_CCODE.appendRow();
					T_CCODE.setValue(companyCodes.get(i).toUpperCase(), "");							
				}	
				
				IT_INVLD_SSO.appendRow();
				IT_INVLD_SSO.setValue("I", "SIGN");	
				IT_INVLD_SSO.setValue("EQ", "OPTION");	
				IT_INVLD_SSO.setValue(userId, "LOW");	
				
				client.execute(function);
				ret = function.getExportParameterList().getTable("T_APR_LST");			
				
				int maxRows = 500;
				
				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				
				response.write("[");
					
				for(int i = 0; i < maxRows; i++) {
					response.write("{\"sso\":\""+ret.getString("APRVR")+"\",");	
				response.write("\"firstname\":\""+ret.getString("FIRTST_NAME")+"\",");	
					response.write("\"lastname\":\""+ret.getString("LAST_NAME")+"\"}");
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
				
				response.write("]");
				
				client.disconnect();
				
				strLogMessasge = strLogMessasge+"companyCodes:"+companyCodes+T_CCODE;
								
			// TODO : Validate City State
			} else if (inputFunction.equalsIgnoreCase("validateCityState")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I481_VRA_TXJCD");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				importList.setValue(inputCountry, "I_COUNTRY");	
 				importList.setValue(inputState, "I_STATE");	
 				importList.setValue(inputCity, "I_CITY");	
  				importList.setValue(inputZip, "I_ZIPCODE");	
  				
				client.execute(function);
				ret = function.getExportParameterList().getTable("IT_DISP_TXJCD");			
				
				int maxRows = 500;
				
				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				
				response.write("[{\"code\":\"1\",\"message\":[");
				
				for(int i = 0; i < maxRows; i++) {				
					response.write("{\"city\":\""+ret.getString("CITY")+"\",");	
					response.write("\"state\":\""+ret.getString("STATE")+"\",");						
					response.write("\"zip\":\""+ret.getString("ZIPCODE")+"\",");	
					response.write("\"taxCode\":\""+ret.getString("TXJCD")+"\",");	
					response.write("\"country\":\"United States\"}");					
					
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
				
				response.write("]}]");	
				
				client.disconnect();
				
				//Begin of Insert CTI w8 Foreign vendor
				//CTI Call
			} else if (inputFunction.equalsIgnoreCase("CTICall")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("ZSFI_I507_CTI_CALL");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();
				JCO.Table retET_CTI = function.getImportParameterList().getTable("CT_CTI");
				JCO.Table retET_ANSWER = function.getImportParameterList().getTable("CT_ANSWER");
				if (taxCountryCTI != null){
					retET_CTI.appendRow();
					retET_CTI.setValue(taxCountryCTI, "TCNTY");
					retET_CTI.setValue(reqIdCTI, "REQST");
				}
				if (vendEntQ1 != null){
					retET_ANSWER.appendRow();
					retET_ANSWER.setValue("00008", "QGROUP");
					retET_ANSWER.setValue("0001", "QSEQNR");
					retET_ANSWER.setValue(vendEntQ1, "ANSWER");
					retET_ANSWER.setValue(reqIdCTI, "REQST");
				}
				if (vendEntQ2 != null){
					retET_ANSWER.appendRow();
					retET_ANSWER.setValue("00008", "QGROUP");
					retET_ANSWER.setValue("0002", "QSEQNR");
					retET_ANSWER.setValue(vendEntQ2, "ANSWER");
					retET_ANSWER.setValue(reqIdCTI, "REQST");
				}
				if (vendEntQ3 != null){
					retET_ANSWER.appendRow();
					retET_ANSWER.setValue("00008", "QGROUP");
					retET_ANSWER.setValue("0003", "QSEQNR");
					retET_ANSWER.setValue(vendEntQ3, "ANSWER");
					retET_ANSWER.setValue(reqIdCTI, "REQST");
				}
				if (vendEntQ4 != null){
					retET_ANSWER.appendRow();
					retET_ANSWER.setValue("00008", "QGROUP");
					retET_ANSWER.setValue("0004", "QSEQNR");
					retET_ANSWER.setValue(vendEntQ4, "ANSWER");
					retET_ANSWER.setValue(reqIdCTI, "REQST");
				}
				if (vendEntQ5 != null){
					retET_ANSWER.appendRow();
					retET_ANSWER.setValue("00008", "QGROUP");
					retET_ANSWER.setValue("0005", "QSEQNR");
					retET_ANSWER.setValue(vendEntQ5, "ANSWER");
					retET_ANSWER.setValue(reqIdCTI, "REQST");
				}				
				importList.setValue(sso, "IM_SSO");
				importList.setValue(reqTypeCTI, "IM_REQTYP");
  				
				client.execute(function);
				String returnMessage = function.getExportParameterList().getString("EX_MESSAGE");
				ret = function.getExportParameterList().getTable("CT_CTI");
				
				int maxRows = 500;
				
				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				
				response.write("[{\"message\":\""+returnMessage+"\",\"CTI\":[");
				
				for(int i = 0; i < maxRows; i++) {				
//					response.write("{\"clientId\":\""+ret.getString("ERNAM")+"\",");
					response.write("{\"clientId\":\""+ret.getString("ACC_NUM")+"\",");
					response.write("\"registrationcode\":\""+ret.getString("RCODE")+"\",");	
					response.write("\"url\":\""+ret.getString("URL")+"\",");						
					response.write("\"taxCountry\":\""+ret.getString("TCNTY")+"\"}");
					
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
				
				response.write("]}]");
				
				client.disconnect();
				//End of Insert CTI w8 Foreign vendor
				
			// Begin of Insert by Naga	999 - 2
				// TODO : Validate Tax ID / SSN number
			} else if (inputFunction.equalsIgnoreCase("validatetaxid")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SFI_I508_VRA_TAXIDVALIDATE");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				importList.setValue(inputTaxId, "E_ZZSF_VRA_TAXID");
				importList.setValue(inputVendor, "E_VENDOR");

  				
				client.execute(function);
				String returnStatus = function.getExportParameterList().getString("I_ZZSF_VRA_MSGID");
				String returnMessage = function.getExportParameterList().getString("I_ZZSF_VRA_MSGVNDR");
				
				response.write("{\"returnStatus\":\""+returnStatus+"\",\"returnMessage\":\""+returnMessage+"\"}");

				
				client.disconnect();
			// End of Insert by Naga 999 - 2	
			// TODO : Inviter/Request Payment Terms	
			}
//				else if (inputFunction.equalsIgnoreCase("test")){
//				String screenFlag = "I";
//
//				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I512_PAYMENTS_W2W");
//				
//				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
//				JCO.ParameterList importList = function.getImportParameterList();	
//				importList.setValue(screenFlag, "I_SCREEN_FLAG");	
// 				importList.setValue(inputVendorType, "I_VENDOR_TYPE");	
// 				importList.setValue(vendorCat, "I_VENDOR_CAT");	
// 				
//				client.execute(function);
//				ret = function.getExportParameterList().getTable("IT_PAYMENT_TERMS");			
//				
//				int maxRows = 500;
//				
//				if (ret.getNumRows() < maxRows)
//					maxRows = ret.getNumRows();
//				
//				response.write("[");
//				for(int i = 0; i < maxRows; i++) {
//				
//					response.write("{\"Key\":\""+ret.getString("ZTERM")+"\",");	
//					response.write("\"Description\":\""+ret.getString("TEXT1")+"\"}");					
//					
//					if (!ret.isLastRow() && i != (maxRows-1)){
//						response.write(",");
//					}
//					ret.nextRow();
//				}
//				
//				response.write("]");	
//				
//				client.disconnect();
//							
//			} 
			//Start  ENHC0021829
			else if (inputFunction.equalsIgnoreCase("PaymentTerms")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I477_VENDOR_TYPE_DETAIL");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				importList.setValue(userId, "I_SSO_ID");
				client.execute(function);
				ret = function.getExportParameterList().getTable("IT_PAYMENT_TERM");			
				
				int maxRows = 3000;
				int vendorCount=0;
 				String[][] paymentArray=null;

				if (ret.getNumRows() < maxRows)
					maxRows = ret.getNumRows();
				for(int i = 0; i < maxRows; i++) {
					if(inputVendorType.trim().equals(ret.getString("VEND_TYPE").trim()))
					{

							vendorCount=vendorCount+1;

					}
					ret.nextRow();
					}
				paymentArray=new String[vendorCount][2];
				int vendorCounttemp=0;
				ret.firstRow();
				for(int i = 0; i < maxRows; i++) {
					if(inputVendorType.trim().equals(ret.getString("VEND_TYPE").trim()))
					{
						paymentArray[vendorCounttemp][0]=ret.getString("ZTERM");
						paymentArray[vendorCounttemp][1]=ret.getString("TEXT1");
							vendorCounttemp++;
					}
					ret.nextRow();
					}


				ret.firstRow();
				response.write("[");
//				response.write("{\"Key\":\""+maxRows+vendorCounttemp+"\",");	
//				response.write("\"Description\":\""+maxRows+"\"},");
//				int tempvendorCount1=0;
				for(int i = 0; i < paymentArray.length; i++) {
					response.write("{\"Key\":\""+paymentArray[i][0]+"\",");	
					response.write("\"Description\":\""+paymentArray[i][1]+"\"}");	
					if (!ret.isLastRow() && i != (paymentArray.length-1)){
						response.write(",");
					}
					
					ret.nextRow();
				}
				
				response.write("]");	
				
						
			} 
			else if (inputFunction.equalsIgnoreCase("roleCheck")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I477_VENDOR_TYPE_DETAIL");
				
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				JCO.ParameterList importList = function.getImportParameterList();	
				importList.setValue(userId, "I_SSO_ID");
				importList.setValue(inputVendorType, "I_VEND_TYPE");
				client.execute(function);
				ret = function.getTableParameterList().getTable("T_RETURN");			
				ret.firstRow();
				String message="success";
				if(ret.getNumRows()>0)
					message=ret.getString("MESSAGE").trim();
				response.write("[");
				response.write("{\"Message\":\""+message+"\"}");	
				response.write("]");	
			}
			//End  ENHC0021829
			else if (inputFunction.equalsIgnoreCase("getProvinces")){

				if (inputCountry.equalsIgnoreCase("us")){
					
					response.write("[{ \"code\": \"AA\", \"description\": \"Armed Forces America\" },{ \"code\": \"AE\", \"description\": \"Armed Forces (other)\" },{ \"code\": \"AK\", \"description\": \"Alaska\" },{ \"code\": \"AL\", \"description\": \"Alabama\" },{ \"code\": \"AP\", \"description\": \"Armed Forces Pacific\" },{ \"code\": \"AR\", \"description\": \"Arkansas\" },{ \"code\": \"AS\", \"description\": \"American Samoa\" },{ \"code\": \"AZ\", \"description\": \"Arizona\" },{ \"code\": \"CA\", \"description\": \"California\" },{ \"code\": \"CO\", \"description\": \"Colorado\" },{ \"code\": \"CT\", \"description\": \"Connecticut\" },{ \"code\": \"DC\", \"description\": \"District of Columbia\" },{ \"code\": \"DE\", \"description\": \"Delaware\" },{ \"code\": \"FL\", \"description\": \"Florida\" },{ \"code\": \"GA\", \"description\": \"Georgia\" },{ \"code\": \"GU\", \"description\": \"Guam\" },{ \"code\": \"HI\", \"description\": \"Hawaii\" },{ \"code\": \"IA\", \"description\": \"Iowa\" },{ \"code\": \"ID\", \"description\": \"Idaho\" },{ \"code\": \"IL\", \"description\": \"Illinois\" },{ \"code\": \"IN\", \"description\": \"Indiana\" },{ \"code\": \"KS\", \"description\": \"Kansas\" },{ \"code\": \"KY\", \"description\": \"Kentucky\" },{ \"code\": \"LA\", \"description\": \"Louisiana\" },{ \"code\": \"MA\", \"description\": \"Massachusetts\" },{ \"code\": \"MD\", \"description\": \"Maryland\" },{ \"code\": \"ME\", \"description\": \"Maine\" },{ \"code\": \"MI\", \"description\": \"Michigan\" },{ \"code\": \"MN\", \"description\": \"Minnesota\" },{ \"code\": \"MO\", \"description\": \"Missouri\" },{ \"code\": \"MP\", \"description\": \"Northern Mariana Isl\" },{ \"code\": \"MS\", \"description\": \"Mississippi\" },{ \"code\": \"MT\", \"description\": \"Montana\" },{ \"code\": \"NC\", \"description\": \"North Carolina\" },{ \"code\": \"ND\", \"description\": \"North Dakota\" },{ \"code\": \"NE\", \"description\": \"Nebraska\" },{ \"code\": \"NH\", \"description\": \"New Hampshire\" },{ \"code\": \"NJ\", \"description\": \"New Jersey\" },{ \"code\": \"NM\", \"description\": \"New Mexico\" },{ \"code\": \"NV\", \"description\": \"Nevada\" },{ \"code\": \"NY\", \"description\": \"New York\" },{ \"code\": \"OH\", \"description\": \"Ohio\" },{ \"code\": \"OK\", \"description\": \"Oklahoma\" },{ \"code\": \"OR\", \"description\": \"Oregon\" },{ \"code\": \"PA\", \"description\": \"Pennsylvania\" },{ \"code\": \"PR\", \"description\": \"Puerto Rico\" },{ \"code\": \"RI\", \"description\": \"Rhode Island\" },{ \"code\": \"SC\", \"description\": \"South Carolina\" },{ \"code\": \"SD\", \"description\": \"South Dakota\" },{ \"code\": \"TN\", \"description\": \"Tennessee\" },{ \"code\": \"TX\", \"description\": \"Texas\" },{ \"code\": \"UT\", \"description\": \"Utah\" },{ \"code\": \"VA\", \"description\": \"Virginia\" },{ \"code\": \"VI\", \"description\": \"Virgin Islands\" },{ \"code\": \"VT\", \"description\": \"Vermont\" },{ \"code\": \"WA\", \"description\": \"Washington\" },{ \"code\": \"WI\", \"description\": \"Wisconsin\" },{ \"code\": \"WV\", \"description\": \"West Virginia\" },{ \"code\": \"WY\", \"description\": \"Wyoming\" }]");
					
				} else if (inputCountry.equalsIgnoreCase("ca")){
					
					response.write("[{ \"code\": \"AB\", \"description\": \"Alberta\" },{ \"code\": \"BC\", \"description\": \"British Columbia\" },{ \"code\": \"MB\", \"description\": \"Manitoba\" },{ \"code\": \"NB\", \"description\": \"New Brunswick\" },{ \"code\": \"NF\", \"description\": \"Newfoundland\" },{ \"code\": \"NR\", \"description\": \"No Region\" },{ \"code\": \"NS\", \"description\": \"Nova Scotia\" },{ \"code\": \"NT\", \"description\": \"Northwest Territory\" },{ \"code\": \"NU\", \"description\": \"Nunavut\" },{ \"code\": \"ON\", \"description\": \"Ontario\" },{ \"code\": \"PE\", \"description\": \"Prince Edward Island\" },{ \"code\": \"QC\", \"description\": \"Quebec\" },{ \"code\": \"SK\", \"description\": \"Saskatchewan\" },{ \"code\": \"YT\", \"description\": \"Yukon Territory\" }]");
					
				} else if (inputCountry.equalsIgnoreCase("mx")){
					
					response.write("[{ \"code\": \"AG\", \"description\": \"Aguascalientes\" },{ \"code\": \"BC\", \"description\": \"Baja California\" },{ \"code\": \"BC\", \"description\": \"Baja California S\" },{ \"code\": \"CH\", \"description\": \"Chihuahua\" },{ \"code\": \"CH\", \"description\": \"Chiapas\" },{ \"code\": \"CM\", \"description\": \"Campeche\" },{ \"code\": \"CO\", \"description\": \"Coahuila\" },{ \"code\": \"CO\", \"description\": \"Colima\" },{ \"code\": \"DF\", \"description\": \"Distrito Federal\" },{ \"code\": \"DG\", \"description\": \"Durango\" },{ \"code\": \"GR\", \"description\": \"Guerrero\" },{ \"code\": \"GT\", \"description\": \"Guanajuato\" },{ \"code\": \"HG\", \"description\": \"Hidalgo\" },{ \"code\": \"JA\", \"description\": \"Jalisco\" },{ \"code\": \"MC\", \"description\": \"Michoacán\" },{ \"code\": \"ME\", \"description\": \"Estado de México\" },{ \"code\": \"MO\", \"description\": \"Morelos\" },{ \"code\": \"NA\", \"description\": \"Nayarit\" },{ \"code\": \"NL\", \"description\": \"Nuevo Léon\" },{ \"code\": \"NR\", \"description\": \"No Region\" },{ \"code\": \"OA\", \"description\": \"Oaxaca\" },{ \"code\": \"PU\", \"description\": \"Puebla\" },{ \"code\": \"QR\", \"description\": \"Quintana Roo\" },{ \"code\": \"QR\", \"description\": \"Querétaro\" },{ \"code\": \"SI\", \"description\": \"Sinaloa\" },{ \"code\": \"SL\", \"description\": \"San Luis Potosí\" },{ \"code\": \"SO\", \"description\": \"Sonora\" },{ \"code\": \"TA\", \"description\": \"Tabasco\" },{ \"code\": \"TL\", \"description\": \"Tlaxcala\" },{ \"code\": \"TM\", \"description\": \"Tamaulipas\" },{ \"code\": \"VE\", \"description\": \"Veracruz\" },{ \"code\": \"YU\", \"description\": \"Yucatán\" },{ \"code\": \"ZA\", \"description\": \"Zacatecas\" }]");
					
				} else if (inputCountry.equalsIgnoreCase("NZ")){			
	
					response.write("[{ \"code\": \"AK\", \"description\": \"Auckland\" },{ \"code\": \"BO\", \"description\": \"Bay of Plenty\" },{ \"code\": \"CA\", \"description\": \"Canterbury\" },{ \"code\": \"HA\", \"description\": \"Hawke´s Bay\" },{ \"code\": \"MA\", \"description\": \"Manawatu-Wanganui\" },{ \"code\": \"NR\", \"description\": \"No Region\" },{ \"code\": \"NT\", \"description\": \"Northland\" },{ \"code\": \"OT\", \"description\": \"Otago\" },{ \"code\": \"ST\", \"description\": \"Southland\" },{ \"code\": \"TA\", \"description\": \"Taranaki\" },{ \"code\": \"WA\", \"description\": \"Waikato\" },{ \"code\": \"WE\", \"description\": \"West Coast\" },{ \"code\": \"WL\", \"description\": \"Wellington\" }]");					

				} else if (inputCountry.equalsIgnoreCase("AU")){
					
					response.write("[{ \"code\": \"AC\", \"description\": \"Aust Capital Terr\" },{ \"code\": \"NR\", \"description\": \"No Region\" },{ \"code\": \"NS\", \"description\": \"New South Wales\" },{ \"code\": \"NT\", \"description\": \"Northern Territory\" },{ \"code\": \"QL\", \"description\": \"Queensland\" },{ \"code\": \"SA\", \"description\": \"South Australia\" },{ \"code\": \"TA\", \"description\": \"Tasmania\" },{ \"code\": \"VI\", \"description\": \"Victoria\" },{ \"code\": \"WA\", \"description\": \"Western Australia\" }]");					
				}
				
//            Begin of Insert by Naga ENHC0013660				
			} else if (inputFunction.equalsIgnoreCase("getCountry")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("ZSFI_I507_GET_COUNTRIES_STATES");
				int maxRows; 
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				client.execute(function);
				

				ret = function.getExportParameterList().getTable("EX_T_COUNTRIES");					
				maxRows = ret.getNumRows();
				
				response.write("[");
				for(int i = 0;i<maxRows; i++){
					response.write("{\"country\":\""+ret.getString("LAND1")+"\",");
					response.write("\"description\":\""+ret.getString("LANDX")+"\"}");	
					
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();					
				}
				response.write("]");
				client.disconnect();							
			
			} else if (inputFunction.equalsIgnoreCase("getRegion")){

				IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("ZSFI_I507_GET_COUNTRIES_STATES");
				int maxRows; 
				JCO.Function function = new JCO.Function(rfcFunctionTemplate);
				client.execute(function);
				ret = function.getExportParameterList().getTable("EX_T_STATES");
				maxRows = ret.getNumRows();
					
				response.write("[");
				for(int i = 0; i < maxRows; i++) {
					
					response.write("{\"country\":\""+ret.getString("LAND1")+"\",");
					response.write("\"region\":\""+ret.getString("BLAND")+"\",");
					response.write("\"description\":\""+ret.getString("BEZEI")+"\"}");					
						
					if (!ret.isLastRow() && i != (maxRows-1)){
						response.write(",");
					}
					ret.nextRow();
				}
					
				response.write("]");					

				client.disconnect();
//				End of Insert by Naga		 */						
				
			}

			
			if ( kmLoggingActive.equalsIgnoreCase("true")){	
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("VRA_PortalHTMLHelperServices",DateTime+","+userId+","+inputFunction+","+inputQuery+","+inputCompanyCode+","+inputDate+","+inputVendor+","+inputMFC);
			}
	
    	} catch (Exception ey) {
    			ey.printStackTrace();
				res.write("Exception:"+ey.toString());
		}
	}
}