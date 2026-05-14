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

public class requestactions extends AbstractPortalComponent
{
    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
    	String returnCode = "";
    	String resultAction = "";
    	String windowMessage = "";
    	String result = "";
    	String requestNumber = request.getParameter("requestnum");
		String operationKey = request.getParameter("operation");
		String cancelCode	= request.getParameter("cancelCode");
		String mailid       = request.getParameter("venmail");
		String comments     = request.getParameter("decisionComments"); 	// ENHC0019060//DFCT0017924 
		String contactPerson = request.getParameter("contactPerson");		// ENHC0019060
		String approvalReason = request.getParameter("approvalReason");		// ENHC0019060
		String rejectionReason = request.getParameter("rejectionReason");	// ENHC0019060
		if(comments!=null)
		{
			comments     = specialCharRemoval(comments); 	// ENHC0019060//DFCT0017924 
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
			
			if(requestNumber != null && requestNumber.trim().length()>0 && operationKey != null && operationKey.trim().length()>0)
			{
				try 
				{
				
					//get a client service
					IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
					JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
		
					// connect to SAP system
					client.connect();
					IRepository m_Repository = JCO.createRepository("repository", client);
					
					// ENHC0013682 -- A new operation, resend approval(A) is added
					// ENHC0019060 -- Operations Approve ( 1 ) and Reject ( 2 ) are added
					if(operationKey.equals("C") || operationKey.equals("R") || operationKey.equals("A") || operationKey.equals("1") || operationKey.equals("2"))
					{
						IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("ZSFI_I507_VRA_REQUEST_ACTION");
						
						JCO.Function function = new JCO.Function(rfcFunctionTemplate);
						JCO.ParameterList importList = function.getImportParameterList();
						
						importList.setValue(requestNumber, "IM_REQUEST_ID");
						importList.setValue(operationKey, "IM_OPERATION_MODE");
						if(operationKey!=null&&operationKey.equals("1")&&
								 approvalReason!=null&&approvalReason.trim().length()>0 ){
							importList.setValue(approvalReason,"IM_COMMENT");				// ENHC0019060
						}else if(operationKey!=null&&operationKey.equals("2")&&
								 rejectionReason!=null&&rejectionReason.trim().length()>0 ){
							importList.setValue(rejectionReason,"IM_COMMENT");				// ENHC0019060
						}else if(comments!=null&&comments.trim().length()>0){
							importList.setValue(comments,"IM_COMMENT");				// ENHC0019060
						}
						// Set the contact person
						if(contactPerson!=null&&contactPerson.trim().length()>0){	// ENHC0019060
							importList.setValue(contactPerson,"IM_CONTACT");
						}
						importList.setValue(userId,"IM_SSO_ID");				// ENHC0019060
						
						if(operationKey.equals("C"))
						{
							importList.setValue(cancelCode, "IM_CANCEL_RESN");
						}
						
						client.execute(function);
						
						String retMsg		= function.getExportParameterList().getString("EX_MESSAGE");	

						result = "{\"code\":\""+returnCode+"\",\"message\":\""+retMsg+"\"}";
					}					
				} 
				catch (Exception ex)
				{
					ex.printStackTrace();
					returnCode = "1";
					windowMessage = ex.getLocalizedMessage();				
					result = "{\"code\":\""+returnCode+"\",\"message\":\""+windowMessage+"\"}";	
				}
			}
			else
			{
				returnCode = "1";
				windowMessage = "Invalid input parameters. Please correct and resubmit";
				result = "{\"code\":\""+returnCode+"\",\"message\":\""+windowMessage+"\"}";	
			}
			
			if(result.length()>0)
			{
				response.write(result);
			}
			else
			{
				returnCode = "1";
				windowMessage = "Invalid input parameters. Please correct and resubmit Key="+operationKey+" CCode= "+cancelCode;
				result = "{\"code\":\""+returnCode+"\",\"message\":\""+windowMessage+"\"}";	

				response.write(result);				
			}
			
			if ( kmLoggingActive.equalsIgnoreCase("true"))
			{				
				String DateTime = kmlogger.GetTimeDate(); 						
				kmlogger.logmessage("RequestActions ","Request Number "+requestNumber+" Operation Mode "+operationKey+" Result "+result);				
			}			
    	} 
    	catch (Exception ey) 
    	{		
				ey.printStackTrace();
				returnCode = "1";
				windowMessage = "Exception "+ey.getMessage();
				result = "{\"code\":\""+returnCode+"\",\"message\":\""+windowMessage+"\"}";	

				res.write(result);				
		}
    }
  //Ganesh added for Special Char removal DFCT0017924
    private String specialCharRemoval(String inputStringLine)
    {
    	
    	
		   inputStringLine = inputStringLine.replace("\"", " ");
		  // inputStringLine = inputStringLine.replaceAll("[~`!*()<>'/|#$%^&@{}_]", " ");
		   inputStringLine = inputStringLine.replace("\'", " ");	
		   inputStringLine = inputStringLine.replace("\\", " ");	
		   inputStringLine = inputStringLine.replace("\n", " ");	
		   inputStringLine = inputStringLine.replace("\r", " ");
		   inputStringLine = inputStringLine.replace("\t", " ");		
		   inputStringLine = inputStringLine.replace( (char)145,(char)' ');
		   inputStringLine = inputStringLine.replace( (char)146,(char)' ');
		   inputStringLine = inputStringLine.replace( (char)147, (char)' ');
		   inputStringLine = inputStringLine.replace( (char)148, (char)' ');
		   inputStringLine = inputStringLine.replace( (char)150, (char)' ' );		
		   inputStringLine = inputStringLine.replace( (char)8211, (char)' ' ); // em dash??    
		   inputStringLine = inputStringLine.replace( (char)8216, (char)' '); // left single quote
		   inputStringLine = inputStringLine.replace( (char)8217, (char)' '); // right single quote
		   inputStringLine = inputStringLine.replace( (char)8220, (char)' '); // left double
		   inputStringLine = inputStringLine.replace( (char)8221, (char)' '); // right double
		
    	return inputStringLine;
    }
 // Ganesh special char end DFCT0017924
}