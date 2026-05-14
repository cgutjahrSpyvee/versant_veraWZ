package com.nbcu.vra.tools;

import com.nbcu.vra.ui.components;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IUser;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class help {

	public static String[] helpContent(IPortalComponentRequest request,String code)
	{


		IPortalComponentProfile profile = request.getComponentContext().getProfile();
		String WFSystemAlias = "SAP_R3";
		IUser userObject = request.getUser();
		String userId = userObject.getUniqueName();

	// tables 
		JCO.Table totalTableOfQns = null;
		int totalRows=0;
		String tempString="";
		String[] qnsArray =null;
		
		try {
			IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
			JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
			client.connect();

			IRepository m_Repository = null;
			IFunctionTemplate SAP_FUNCTION = null;

			m_Repository = JCO.createRepository("repository", client);
			
			//Get User Roles
			IRepository m_RepositoryRoles = JCO.createRepository("repository", client);
			IFunctionTemplate Z_SF_I504_DISPLAY_INBOX_W2W = m_RepositoryRoles.getFunctionTemplate("Z_SF_I504_DISPLAY_INBOX_W2W");

			JCO.Function helpQns = new JCO.Function(Z_SF_I504_DISPLAY_INBOX_W2W);
			JCO.ParameterList importList = helpQns.getImportParameterList();
			importList.setValue(userId, "I_SSO");

			client.execute(helpQns);
			
			
			totalTableOfQns=helpQns.getExportParameterList().getTable("ET_HMSG");
			totalRows=totalTableOfQns.getNumRows();
			int codeBasedQns=0;
			
			
			
			for(int i=0;i<totalRows;i++)
			{
				
			if(totalTableOfQns.getValue("QGROUP").equals(code))	
			{
				codeBasedQns++;
			}
			totalTableOfQns.nextRow();
			}
			
			qnsArray=new String[codeBasedQns];
			totalTableOfQns.firstRow();
			codeBasedQns=0;
			for(int i=0;i<totalRows;i++)
			{
				
			if(totalTableOfQns.getValue("QGROUP").equals(code))
			{
				qnsArray[codeBasedQns]=totalTableOfQns.getValue("QUESTION4").toString();
				codeBasedQns++;		
			}

			totalTableOfQns.nextRow();
			}
			
			client.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		//	response.write(components.displayErrorContainer(request,"Help Section:", e.getMessage()));
			tempString=e.toString();
		}
		return qnsArray;

	
	}
	
}
