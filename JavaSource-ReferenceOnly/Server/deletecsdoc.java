package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nbcu.html5_vra.portalservices.tools.*;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class deletecsdoc extends AbstractPortalComponent
{
   public void doContent(IPortalComponentRequest req, IPortalComponentResponse res)
    {

		String WFSystemAlias = "SAP_R3";
		
    	try {
    		
    		HttpServletRequest request = req.getServletRequest();
			IPortalComponentProfile profile = req.getComponentContext().getProfile();	    	
			
    		HttpServletResponse resp = req.getServletResponse(true);
			PrintWriter response = resp.getWriter();

			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	

	    	String objectType = request.getParameter("objtype");
	    	String objectKey = request.getParameter("objkey");
	    	String inputDocumentId = request.getParameter("documentid");
			String srcSystem = "UHP";			
			String fileName = null;				
			boolean foundFileName = false;
		    String fileID = null;
		    String fileExt = null;
				    
			//Get User ID
			String userId = req.getUser().getName();			

			if (inputDocumentId != null) {
				foundFileName = true; 
				fileID = inputDocumentId;
			}
			
		    //get a client service
			IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
			JCO.Client client = clientService.getJCOClient(WFSystemAlias, req);

			// connect to SAP system
			client.connect();
				 
			IRepository m_RepositoryLock = JCO.createRepository("repository", client);

			
			if (inputDocumentId == null ){
				
				IFunctionTemplate ZZ_GOS_ATTACHMENT_GET_LIST = m_RepositoryLock.getFunctionTemplate("ZZ_GOS_ATTACHMENT_GET_LIST");
				JCO.Function functionList = new JCO.Function(ZZ_GOS_ATTACHMENT_GET_LIST);			
				
				JCO.ParameterList importList = functionList.getImportParameterList();				
				importList.setValue(objectType, "I_OBJTYPE");
				importList.setValue(objectKey, "I_OBJKEY");
				importList.setValue(srcSystem, "I_SYSTEM_ID");
						
				client.execute(functionList);
	
				JCO.Table retList = functionList.getExportParameterList().getTable("T_ATTACHMENTS");													 
	
				for(int i = 0; i < retList.getNumRows(); i++) {
				
					if (retList.getString("NAME").equalsIgnoreCase(fileName)){
						foundFileName = true;
						//fileName = retList.getString("NAME");
						fileID = retList.getString("ID");
						break;
					}
					
					retList.nextRow();
					
				}
			}
			if (foundFileName){
			
				IFunctionTemplate ZZ_FI_GOS_ATTACHMENT_DELETE = m_RepositoryLock.getFunctionTemplate("ZZ_FI_GOS_ATTACHMENT_DELETE");
	
				JCO.Function functionLock = new JCO.Function(ZZ_FI_GOS_ATTACHMENT_DELETE);
				JCO.ParameterList importListLock = functionLock.getImportParameterList();
				importListLock.setValue(fileID, "I_ATTACHMENT_ID");
				importListLock.setValue(objectType, "I_OBJTYPE");
	 	 		importListLock.setValue(objectKey, "I_OBJKEY");	
	 	 		importListLock.setValue(srcSystem, "I_SYSTEM_ID");
			
				client.execute(functionLock);
			}
			
			client.disconnect();
			
			if (foundFileName){
				response.write("{\"code\":\"0\",\"message\":\"File Deleted\"}");
			} else {
				response.write("{\"code\":\"1\",\"message\":\"File Not Found\"}");				
			}
			
			if ( kmLoggingActive.equalsIgnoreCase("true")){	
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("Service-VeraDeleteCSDoc",DateTime+","+userId+","+fileID+","+fileName+","+objectType+","+objectKey+","+srcSystem+","+foundFileName);
			}
			
    	} catch (Exception e){
    		e.printStackTrace();
			res.write("Error"+ e.getLocalizedMessage());
    	}

    }
    
    public static Map<String, String> getQueryMap(String query)
		{
    		query = query.substring( query.indexOf('?') + 1 );
    		String[] params = query.split("&");
		    Map<String, String> map = new HashMap<String, String>();
		    for (String param : params)
		    {
		        String name = param.split("=")[0];
		        String value = param.split("=")[1];
		        map.put(name, value);
		    }
		    return map;
		}
    
}
   
   