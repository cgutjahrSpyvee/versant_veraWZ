package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

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

public class managecsdoc extends AbstractPortalComponent
{
    public void doContent(IPortalComponentRequest req, IPortalComponentResponse res)
    {
		String ServerString = "com.nbcu.html5portal";
		String WFSystemAlias = "SAP_R3";
		
    	try {
    		
    		HttpServletRequest request = req.getServletRequest();
   			IPortalComponentProfile profile = req.getComponentContext().getProfile();
   			
    		HttpServletResponse resp = req.getServletResponse(true);
			PrintWriter response = resp.getWriter();

			String objectKey = "";
			String objectType = "";
			String inputAction = "";
			String fileName = null;	
			String fileExt = null;
			String fileFullName = null;
			String fileType = "";
			String returnedDocId = "";
			String vendorId     = "";		// By Naga, DFCT0013688
			byte[] fileContent = null;
			long fileSize = 0;		

			// KM Logging Switch
			String kmLoggingActive = profile.getProperty("KMLoggingActive");	
						
			//Get User ID
			String userId = req.getUser().getName();			
		
			try {	
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				List<FileItem> fields = upload.parseRequest(request);
				//response.write("Number of fields: " + fields.size() + "<br/><br/>");
				Iterator<FileItem> it = fields.iterator();
				
				if (!it.hasNext()) {
					response.write("No fields found");
					return;
				}
				
				while (it.hasNext()) {
					FileItem fileItem = it.next();
					
					if (fileItem.isFormField()) {
						
						if (fileItem.getFieldName().equalsIgnoreCase("action")){
							
							if (fileItem.getString().length()>0){
								inputAction = fileItem.getString();									
							}

						} else if (fileItem.getFieldName().equalsIgnoreCase("id")){
							
							if (fileItem.getString().length()>0){
								objectKey = fileItem.getString();									
							}
							
						} else if (fileItem.getFieldName().equalsIgnoreCase("filename")){
							
							if (fileItem.getString().length()>0){
								fileName = fileItem.getString();									
							}
							
						} else if (fileItem.getFieldName().equalsIgnoreCase("fileType")){
							
							if (fileItem.getString().length()>0){
								fileType = fileItem.getString();									
							}
														
						} else if (fileItem.getFieldName().equalsIgnoreCase("objtype")){
							
							if (fileItem.getString().length()>0){
								objectType = fileItem.getString();									
							}
                        // Begin of Insert by Naga DFCT0013688								
						// fix production issue to handle attachments scenario when there is no open request.	
							
						} else if (fileItem.getFieldName().equalsIgnoreCase("vendorId")){
							if (fileItem.getString().length()>0){
								vendorId = fileItem.getString();									
							}
						// End of Insert by Naga	
						}
						
					
					} else {
						
						if (fileName == null) {
							fileName = fileItem.getName();
							
							// Strip the Path from IE
							if (fileName != null) {
								fileName = FilenameUtils.getName(fileName);
							}
						} else {
							fileFullName = fileItem.getName();
							
							int i = fileFullName.lastIndexOf('.');
							
							if (i > 0) {
							    fileExt = fileFullName.substring(i+1);
							    fileName = fileName+"."+fileExt;
							}

						}
						
						fileContent = fileItem.get();
						fileSize = fileItem.getSize();
						
					}
				}
				
				if (inputAction.equalsIgnoreCase("upload")){				
					
					//get a client service
					IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
					JCO.Client client = clientService.getJCOClient(WFSystemAlias, req);
		
					// connect to SAP system
					 client.connect();
		
					IRepository m_RepositoryLock = JCO.createRepository("repository", client);
		
					IFunctionTemplate ZZFI_GOS_ATTACHMENT_SAVE = m_RepositoryLock.getFunctionTemplate("ZZFI_I508_VERA_ATTACHMENT_SAVE");
		
					JCO.Function functionLock = new JCO.Function(ZZFI_GOS_ATTACHMENT_SAVE);
					JCO.ParameterList importListLock = functionLock.getImportParameterList();
					JCO.ParameterList exportListLock = functionLock.getExportParameterList();
					
					importListLock.setValue(fileName, "I_FILENAME");
					importListLock.setValue(objectType, "I_OBJTYPE");
		 	 		importListLock.setValue(objectKey, "I_OBJKEY");		
		 	 		importListLock.setValue(fileType, "I_FILETYPE");	
		 	 		importListLock.setValue(vendorId, "I_VENDOR");	// By Naga DFCT0013688
		 	 		importListLock.setValue("UHP", "I_SYSTEM_ID");
		 	 		
		 			importListLock.setValue(fileContent, "I_CONTENT_HEX");	
		 		
					client.execute(functionLock);
					
					returnedDocId = exportListLock.getString("E_ATTACHMENT_ID");
					client.disconnect();
				}
	
					response.write("{\"name\":\""+fileName+"\",\"size\":\""+fileSize+"\",\"id\":\""+returnedDocId+"\"}");	

			} catch (Exception ex) {
				response.write("Error:"+ex.getLocalizedMessage());
				ex.printStackTrace();
			}
				
			
			if ( kmLoggingActive.equalsIgnoreCase("true")){	
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("ManageCSDoc",DateTime+","+userId+","+inputAction+","+fileName+","+objectType+","+objectKey);
			}
			
    	} catch (Exception e){
    		e.printStackTrace();
			res.write("Error"+ e.getLocalizedMessage());

    	}
    }
}