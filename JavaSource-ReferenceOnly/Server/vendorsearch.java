package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.html5_vra.portalservices.tools.kmlogger;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.*;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class vendorsearch extends AbstractPortalComponent
{
    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    	String returnCode = "";
    	String resultAction = "";
    	String windowMessage = "";
    	String result = "";
    	String objectid = request.getParameter("id");
		String action = request.getParameter("Action");

		String vendorName = request.getParameter("name");
		String vendorTin = request.getParameter("tin");
		String vendorPrimaryAddress1 = request.getParameter("primaryAddress1");
		//String vendorPrimaryAddress2 = request.getParameter("primaryAddress2");
		//String vendorPrimaryAddress3 = request.getParameter("primaryAddress3");
		String vendorPrimaryCountry = request.getParameter("primaryCountry");
		String vendorPrimaryAddressCity = request.getParameter("primaryAddressCity");
		String vendorPrimaryAddressState = request.getParameter("primaryAddressState");
		String vendorPrimaryAddressZip = request.getParameter("primaryAddressZip");
		String vendorNumber            = request.getParameter("vendor-number");
		
		if (vendorPrimaryAddressState.equalsIgnoreCase("select")){
			vendorPrimaryAddressState = "";
		}
		
		String WFSystemAlias = "SAP_R3";


    	try {
			
    		HttpServletResponse resp = request.getServletResponse(true);
  			IPortalComponentProfile profile = request.getComponentContext().getProfile();
  			
			PrintWriter response = resp.getWriter();
			
			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	
			
			//Get User ID
			String userId = request.getUser().getName();

			//Logic
			returnCode = "0";
			try {
			
			// Added Pranesh(04/25/2016) - ENHC0018725
				String userType = "";
				String paymentTermUserType = "";
				boolean userIsJointVenture = false;
				boolean userIsExternalVendor = false;
				boolean userIsInternalEmployeeBuyer = false;
				boolean userIsInternalEmployeeInviter =  false;
			 // Added Pranesh(04/25/2016) - ENHC0018725
				
				
				
				
			//get a client service
			IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
			JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);

			// connect to SAP system
			client.connect();
			IRepository m_Repository = JCO.createRepository("repository", client);

			IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I513_SEARCH_VENDOR_W2W");

			JCO.Function function = new JCO.Function(rfcFunctionTemplate);
			JCO.ParameterList importList = function.getImportParameterList();
			importList.setValue(vendorNumber, "I_LIFNR");
			importList.setValue(vendorName, "I_NAME");
			importList.setValue(vendorTin, "I_TIN");
			importList.setValue(vendorPrimaryAddress1, "I_ADDR1");			
			importList.setValue(vendorPrimaryAddressCity, "I_CITY");
			importList.setValue(vendorPrimaryAddressState, "I_STATE");
			importList.setValue(vendorPrimaryAddressZip, "I_ZIP");
			importList.setValue(vendorPrimaryCountry, "I_COUNTRY");	
			
			client.execute(function);
			
			// Added Pranesh(04/25/2016) - ENHC0018725
			IFunctionTemplate Z_SF_I477_GET_USER_ROLES = m_Repository.getFunctionTemplate("Z_SF_I477_GET_USER_ROLES");

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
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					paymentTermUserType = "2";
					userIsInternalEmployeeBuyer = true;
					userType = "2";
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("C:SRM_BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeBuyer = true;
					paymentTermUserType = "2";
					userType = "2";							
				} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeInviter = true;
					userType = "1";						
				}
				// Begin of Insert by Naga ENHC0016164
				else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsInternalEmployeeBuyer = true;
					paymentTermUserType = "2";
					userType = "2";							
				}					
				// Handle joint venture users
				else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
					userIsJointVenture = true;
					paymentTermUserType = "4";
					userType = "4";							
				}					
				// End of Insert by Naga
				retUserRoles.nextRow();
			}
		    // End Pranesh(04/25/2016) - ENHC0018725
			
			
			JCO.Table ret = function.getExportParameterList().getTable("IT_VENDOR_SRCH");	
			
			
			int maxRows = 500;
			
			if (ret.getNumRows() < maxRows)
				maxRows = ret.getNumRows();
			
			response.write("[");
				
			for(int i = 0; i < maxRows; i++) {
				String statusText = ret.getString("STATS");
				if(statusText==null||statusText.equalsIgnoreCase("")){
					statusText = "";
				}
				String vendorType = ret.getString("VEND_TYPE");
				
//				if(vendorType==null||vendorType.equalsIgnoreCase("")){
//					vendorType = "";
//				}				
				response.write("{\"id\": \""+ret.getString("LIFNR")+"\",\"vendorNum\":\""+ret.getString("LIFNR")+"\",\"remitNum\":\""+ret.getString("EMPFK")+"\",\"name\": \""+ret.getString("NAME1")+"\",\"tin\": \""+ret.getString("TIN")+"\",\"address\": \""+ret.getString("ADDR1")+"\",\n"+"\"city\":\""+ret.getString("CITY")+"\",\"postalCode\": \""+ret.getString("ZIP")+"\",\"country\":\""+ret.getString("COUNTRY")+"\",\"status\": {\"type\": \"Activity\",\"text\": \""+statusText+"\"},\"vendorType\": \""+vendorType+"\",\"state\": \""+ret.getString("STATE")+"\",\"reqType\":\""+ret.getString("REQTY")+"\"}");
				
				/*
				 *  Blocked Temp Pranesh (04/29/2016) - Defect ID : 15051
					//response.write("{\"id\": \""+ret.getString("LIFNR")+"\",\"vendorNum\":\""+ret.getString("LIFNR")+"\",\"remitNum\":\""+ret.getString("EMPFK")+"\",\"name\": \""+ret.getString("NAME1")+"\",\"tin\": \""+ret.getString("TIN")+"\",\"address\": \""+ret.getString("ADDR1")+"\",\n"+"\"city\":\""+ret.getString("CITY")+"\",\"postalCode\": \""+ret.getString("ZIP")+"\",\"country\":\""+ret.getString("COUNTRY")+"\",\"status\": {\"type\": \"Activity\",\"text\": \""+statusText+"\"},\"vendorType\": \""+vendorType+"\",\"state\": \""+ret.getString("STATE")+"\",\"reqType\":\""+ret.getString("REQTY")+"\",\"isSourcingRelevant\":\""+ret.getString("KONZS")+"\",\"isSourcing\":\""+userIsInternalEmployeeBuyer+"\"}");
				*/
				
				
				
				// Added \"isSourcing\":\""+userIsInternalEmployeeBuyer+"\", to pass user type to UI ( is Sourcing Vendor or Not ) - Pranesh(04/25/2016)-ENHC0018725
				// Added ,\"isSourcingRelevant\":\""+ret.getString("KONZS")+"\", to get Sourcing Relevant value                    - Pranesh(04/21/2016)-ENHC0018725
				
				if (!ret.isLastRow() && i != (maxRows-1)){
					response.write(",");
				}
				ret.nextRow();
			}
					
			response.write("]");
				
			} catch (Exception ex){
				ex.printStackTrace();
				returnCode = "1";
				windowMessage = ex.getLocalizedMessage();
				
				result = "{\"code\":\""+returnCode+"\",\"message\":\""+windowMessage+"\"}";
	
			}
		
			response.write(result);

			
			if ( kmLoggingActive.equalsIgnoreCase("true")){				
				String DateTime = kmlogger.GetTimeDate(); 						
				kmlogger.logmessage("VRA_VendorSearch",DateTime+","+vendorName+","+vendorTin+","+vendorPrimaryAddress1+","+vendorPrimaryAddressCity+","+vendorPrimaryAddressState+","+vendorPrimaryAddressZip+","+vendorPrimaryCountry);
			}
			
    	} catch (Exception ey) {		
				ey.printStackTrace();
				res.write("Exception:"+ey.toString());
		}
    }
}