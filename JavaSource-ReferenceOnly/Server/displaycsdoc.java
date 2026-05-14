package com.nbcu.html5_vra.portalservices;
 
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.nbcu.html5_vra.portalservices.tools.kmlogger;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientPoolEntry;
import com.sapportals.portal.prt.service.jco.IJCOClientService;


public class displaycsdoc extends AbstractPortalComponent
{
    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    	String WFSystemAlias = "SAP_R3";
    	String inputObjFileName = request.getParameter("filename");
    	String inputObjType = request.getParameter("objtype");
    	String inputObjKey = request.getParameter("objkey");
    	String inputDocumentId = request.getParameter("documentid");
    	String srcSystem = "UHP";
		String fileID = null;
		String fileExt = null;
		String fileName = null;  
   		boolean foundFileName = false;	
   		JCO.ParameterList ret = null;
    	
    	if (inputObjFileName.contains(".")){
    		String tempInputObjFileName = inputObjFileName;
    		inputObjFileName = tempInputObjFileName.substring(0, tempInputObjFileName.lastIndexOf('.'));
    		fileExt = tempInputObjFileName.substring(tempInputObjFileName.lastIndexOf('.')+1,tempInputObjFileName.lastIndexOf('.')+4);
    	}

    	try {

			HttpServletResponse resp = request.getServletResponse(true);
			IPortalComponentProfile profile = request.getComponentContext().getProfile();		

			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	
		
			//Get User ID
			String userId = request.getUser().getName();
			
			if (inputDocumentId != null) {
				foundFileName = true; 
				fileID = inputDocumentId;
			}
			
				//get a client service
				IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
				IJCOClientPoolEntry clientPoolEntry = clientService.getJCOClientPoolEntry(WFSystemAlias, request);
				JCO.Client client = clientPoolEntry.getJCOClient();		
				
				IRepository m_Repository = JCO.createRepository("repository", client);
							
				if (inputDocumentId == null ){

					IFunctionTemplate ZZ_GOS_ATTACHMENT_GET_LIST = m_Repository.getFunctionTemplate("ZZ_GOS_ATTACHMENT_GET_LIST");
					JCO.Function functionList = new JCO.Function(ZZ_GOS_ATTACHMENT_GET_LIST);			

					JCO.ParameterList importList = functionList.getImportParameterList();				
					importList.setValue(inputObjType, "I_OBJTYPE");
					importList.setValue(inputObjKey, "I_OBJKEY");
					importList.setValue(srcSystem, "I_SYSTEM_ID");

					client.execute(functionList);

					JCO.Table retList = functionList.getExportParameterList().getTable("T_ATTACHMENTS");	

					for(int i = 0; i < retList.getNumRows(); i++) {

						if (retList.getString("NAME").equalsIgnoreCase(inputObjFileName)){
							foundFileName = true;
							fileName = retList.getString("NAME");
							fileID = retList.getString("ID");
							fileExt = retList.getString("TYPE");
							break;
						}

						retList.nextRow();
					}
				} 
			
			if (foundFileName){

				IFunctionTemplate ZZ_FI_GOS_GET_ATTACHMENT = m_Repository.getFunctionTemplate("ZZ_FI_GOS_GET_ATTACHMENT");
				JCO.Function function = new JCO.Function(ZZ_FI_GOS_GET_ATTACHMENT);
				JCO.Structure importStruct = function.getImportParameterList().getStructure("I_ATTACHMENT");
				JCO.ParameterList importListDoc = function.getImportParameterList();			
				
				importStruct.setValue(fileExt,"TYPE");
				importStruct.setValue(fileID, "ID");
				importListDoc.setValue(srcSystem, "I_SYSTEM_ID");
	
				client.execute(function);
				clientPoolEntry.release();
			
				ret = function.getExportParameterList();
				
				InputStream in = ret.getBinaryStream("E_FILE_CONTENT_HEX");
			
				resp.setContentType(ret.getString("MIME_FILE_TYPE"));
				resp.setHeader("Content-Disposition", "filename="+fileName+"."+fileExt.toLowerCase());

				OutputStream out = resp.getOutputStream();

				IOUtils.copy(in, out);
				out.flush();
				in.close();
				out.close();	
				
			} else {
				Writer response = res.getWriter();
				response.write("No Documents found");		
			}

			if ( kmLoggingActive.equalsIgnoreCase("true")){	
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("Service-VeraDisplayCSService",DateTime+","+userId+","+inputObjFileName+","+inputObjType+","+inputObjKey+","+srcSystem+","+foundFileName+","+fileID+","+fileExt+","+ret);
			}	
			
    	} catch (Exception ey) {
			
			ey.printStackTrace();
			res.write("Exception:"+ey.toString());
		}
    }
}