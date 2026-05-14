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
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sapportals.portal.prt.component.*;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class roleCheck
{
	String ServerString = "com.nbcu.html5_vra";

    public boolean Check(IPortalComponentRequest request)
    {
    		
    	IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		IUser userObject = request.getUser();
		String userId = userObject.getUniqueName();	
		boolean status=false;

    	try {
	
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
						status=true;
						break;
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z:SRM30:BUYER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						status=true;
						break;
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_JVM")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						status=true;
						break;				
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_SOURCING")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						status=true;
						break;		
					} else if ((retUserRoles.getString("ROLENAME").equalsIgnoreCase("Z_I485_VENDOR_INVITER")) && (retUserRoles.getString("ROLEFLAG").equalsIgnoreCase("X"))){
						status=true;
						break;		
					}
					
					retUserRoles.nextRow();
				}	

				
				
				
			} catch (Exception e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
				response.write(components.displayErrorContainer(request,"inviter",e1.getMessage()));
			}

		} catch (Exception e) {
			
			e.printStackTrace();

		} 
		return status;
    }
    
}