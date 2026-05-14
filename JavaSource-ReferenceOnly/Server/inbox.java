package com.nbcu.html5_vra.portalservices;
 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.html5_vra.portalservices.tools.kmlogger;
import com.nbcu.html5_vra.portalservices.tools.tools;
import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

public class inbox extends AbstractPortalComponent
{
  String WFSystemAlias = "SAP_R3";
	
   public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {

	   String strStatus = "";
	   //Req#1,2,3,4,5  START. Modified by AGAMPA 23-Feb-2015
	   String allowedActions = "", editOrDisplay="";
	   //Req#1,2,3,4,5  ENDS here.
	   String strStatusFromRFC = "";
	   JCO.Table errorLogTable = null;
	   JCO.Table ret = null;
	   //Begin of Insert CTI w8 Foreign vendor
	   JCO.Table ret_CTI = null;
	   //Begin of Insert CTI w8 Foreign vendor
    	
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
			
			IFunctionTemplate rfcFunctionTemplate = m_Repository.getFunctionTemplate("Z_SF_I504_DISPLAY_INBOX_W2W");

			JCO.Function function = new JCO.Function(rfcFunctionTemplate);
			JCO.ParameterList importList = function.getImportParameterList();
			importList.setValue(userId, "I_SSO");
 
			client.execute(function);
			
			
			HashMap<String, ArrayList<String>> errorLogMap = new HashMap<String, ArrayList<String>>();
			
			errorLogTable = function.getExportParameterList().getTable("IT_EMSG");
			
			int maxRows = 500;
			
			if (errorLogTable.getNumRows() < maxRows)
				maxRows = errorLogTable.getNumRows();
				
			for(int i = 0; i < maxRows; i++) {
				String requestId = errorLogTable.getString("REQST");
				// Begin of Insert by Naga ENHC0013658
				if(requestId==null || requestId.equals("0000000000")){
					// Empty request id, indicates invite
					// Populate invite id
					requestId = errorLogTable.getString("ZZSF_VRA_EMLID")+"01";
				}
				
				
				// End of Insert by Naga
				
				if(errorLogMap.containsKey(requestId)){
					errorLogMap.get(requestId).add(errorLogTable.getString("MESSAGE"));
				}
				else{
					ArrayList<String> errorMessageList = new ArrayList<String>();
					errorMessageList.add(errorLogTable.getString("MESSAGE"));
					errorLogMap.put(requestId, errorMessageList);
				}
			
				errorLogTable.nextRow();
			}
			
			//Begin of Insert CTI w8 Foreign vendor
			ret_CTI = function.getExportParameterList().getTable("IT_CTI");
			for (int i=0;i<ret_CTI.getNumRows(); i++){
				String reqId = ret_CTI.getString("REQST");
				String regCodeCTI = ret_CTI.getString("RCODE");
				String urlCTI = ret_CTI.getString("URL");
				if(errorLogMap.containsKey(reqId)){
					errorLogMap.get(reqId).add(urlCTI+"\nRegistratin Code: "+regCodeCTI);
				}
				else{
					ArrayList<String> errorMessageList = new ArrayList<String>();
					errorMessageList.add(urlCTI+"\nRegistratin Code: "+regCodeCTI);
					errorLogMap.put(reqId, errorMessageList);
				}
				ret_CTI.nextRow();
			}
			//End of Insert CTI w8 Foreign vendor
			
			ret = function.getExportParameterList().getTable("IT_INVITE_REQ");			
			
			maxRows = 500;
			
			if (ret.getNumRows() < maxRows)
				maxRows = ret.getNumRows();
			
			response.write("[");
				
			/**
			 * Allowed Actions is the indicator to show Cancel, Resend or Send Invitation actions. Based on the matrix provided, following logic
			 * determines which action button should be displayed. 
			 * C - Show Cancel Button
			 * R - Show Resent Button
			 * S - Show Send Button
			 * ""(blank) - Hide all buttons.
			 * 
			 * Button Type EDIT or DISPLAY code
			 * D - Show Display Button
			 * E - Show Edit Button
			 */
			
			for(int i = 0; i < maxRows; i++) {
				
				strStatusFromRFC = ret.getString("INVSTAT");
				//Req#1,2,3,4,5  START. Modified by AGAMPA 23-Feb-2015. Added allowedActions in each condition.
				if (strStatusFromRFC.equalsIgnoreCase("0"))
				{
					//strStatus = "Invite Created"; changed by Kermel Ruperto
					strStatus = "Invite Pending Approval";
					allowedActions="CA";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("1"))
				{
					strStatus = "Invite Approved";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("2"))
				{
					strStatus = "Invite Approved";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("3"))
				{
					strStatus = "Invite Rejected";
					allowedActions="CS";
					editOrDisplay="E";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("4"))
				{
					strStatus = "Invite Approved";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("5"))
				{
					//strStatus = "Invite Sent"; changed by Kermel Ruperto
					strStatus = "Pending Vendor Action";
					allowedActions="CR";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("6"))
				{
					strStatus = "Invite Registered";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("7"))
				{
					strStatus = "Email Address Failure";//Email Address Failure from Invite Rejected
					allowedActions="CS";
					editOrDisplay="E";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("8"))
				{
					strStatus = "Invite Pending Term Approval";
					allowedActions="CA";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("S"))
				{
					//strStatus = "Saved"; changed by Kermel Ruperto 10-13-2014
					strStatus = "Pending Submission";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("A"))
				{
					//strStatus = "Approved"; changed by Kermel Ruperto 10-13-2014
					strStatus = "In Review";
					allowedActions="";
					editOrDisplay="";
				}
				// Begin of Insert by Naga ENHC0013658
				// New Code D - Auto Rejected
				else if (strStatusFromRFC.equalsIgnoreCase("D"))
				{
					strStatus = "Rejected";
					allowedActions="C";
					editOrDisplay="D";
				}				
				// End of Insert by Naga
				
				else if (strStatusFromRFC.equalsIgnoreCase("R"))
				{
					strStatus = "Rejected";
					allowedActions="C";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("P"))
				{
					strStatus = "Completed";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("F"))
				{
					strStatus = "Failed";
					allowedActions="C";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("T"))
				{
					strStatus = "Pending Term Approval";	
					allowedActions="";
					editOrDisplay="";
				} 
				else if (strStatusFromRFC.equalsIgnoreCase("W"))
				{
					strStatus = "Pending Approval";	
					allowedActions="";
					editOrDisplay="";
				} 
				else if (strStatusFromRFC.equalsIgnoreCase("O"))
				{
					//strStatus = "Old Version"; // Naga Status text has to changed
					strStatus = "Request Cancelled"; // Naga Status text has to changed
					
					allowedActions="";
					editOrDisplay="";
				} 
				// Begin of Insert by Naga ENHC0016169
				else if (strStatusFromRFC.equalsIgnoreCase("I"))
				{
					strStatus = "Pending IC Approval";
					allowedActions="CA";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("E"))
				{
					strStatus = "Pending IC & Term Approval";
					allowedActions="";
					editOrDisplay="";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("M"))
				{
					strStatus = "Pending Mgmt. Approval";
					allowedActions="CA";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("X"))
				{
					strStatus = "W8 Validation Failed";
					allowedActions="";
					editOrDisplay="E";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("Y"))
				{
					strStatus = "IC Reject";
					allowedActions="";
					editOrDisplay="E";
				}	
				else if (strStatusFromRFC.equalsIgnoreCase("Z"))
				{
					strStatus = "Pending W8 Validation";
					allowedActions="";
					editOrDisplay="D";
				}				
				// End of Insert by Naga 
				else if (strStatusFromRFC.equalsIgnoreCase("9"))
				{
					strStatus = "Invite Cancelled";
					allowedActions="S";
					editOrDisplay="E";
				}
				//Begin of Insert CTI w8 Foreign vendor questions
				else if (strStatusFromRFC.equalsIgnoreCase("U"))
				{
					strStatus = "Pending W8 Submission";
					allowedActions="CA";
					editOrDisplay="D";
				}
				else if (strStatusFromRFC.equalsIgnoreCase("V"))
				{
					strStatus = "Pending TAX Review";
					allowedActions="CA";
					editOrDisplay="D";
				}
				//End of Insert CTI w8 Foreign vendor questions
				else 
				{
					strStatus = strStatusFromRFC;
					allowedActions="";
					editOrDisplay="";
				}
//				//TESTONLY
//				allowedActions="CSR";
//				editOrDisplay="D";
				
				//Req#1,2,3,4,5  ENDS here.
				
				String displayDate = "";
				displayDate = tools.displayPrettyDate(ret.getString("CHANGE_DATE"));
				
				String requestId = ret.getString("REQST");
				String requestIdSource = ret.getString("SOURCE");
				
				///Req#13 START. Modified by AGAMPA 17-Feb-2015(Adding invId with temp value TBD.				
				//String invId = "TBD";//ret.getString("INV");
				response.write("{\"id\": \""+requestId+"\",\"vendorNum\": \""+ret.getString("LIFNR")+"\",\"date\": \""+displayDate+"\",\"name\": \""+ret.getString("VEND_NAME")+"\",\"status\": {\"type\": \"Activity\",\"text\": \""+strStatus+"\"},\"source\": \""+ret.getString("SOURCE")+"\",\"actionkey\": \""+allowedActions+"\",\"btnType\": \""+editOrDisplay+"\" ");
				///Req#13 END.
				
				if(errorLogMap.containsKey(requestId))
				{
					List<String> errorMessageList = errorLogMap.get(requestId);
				
					if(!errorMessageList.isEmpty())
					{
						response.write(",\"errorMessages\": [");
							
							int errorMessageListSize = errorMessageList.size();
							int lastErrorMessageIndex = errorMessageList.size() - 1;
							
							for(int j = 0; j < errorMessageListSize; j++)
							{
								// Begin of Insert by Naga 999
								String errorMessage = errorMessageList.get(j);
								
								//Ganesh added for Special Char removal
								   errorMessage = errorMessage.replace("\"", " ");
								   errorMessage = errorMessage.replace("\'", " ");	
								   errorMessage = errorMessage.replace("\\", " ");	
								   errorMessage = errorMessage.replace("\n", " ");	
								   errorMessage = errorMessage.replace("\r", " ");
								   errorMessage = errorMessage.replace("\t", " ");		
								   errorMessage = errorMessage.replace( (char)145,(char)' ');
								   errorMessage = errorMessage.replace( (char)146,(char)' ');
								   errorMessage = errorMessage.replace( (char)147, (char)' ');
								   errorMessage = errorMessage.replace( (char)148, (char)' ');
								   errorMessage = errorMessage.replace( (char)150, (char)' ' );		
								   errorMessage = errorMessage.replace( (char)8211, (char)' ' ); // em dash??    
								   errorMessage = errorMessage.replace( (char)8216, (char)' '); // left single quote
								   errorMessage = errorMessage.replace( (char)8217, (char)' '); // right single quote
								   errorMessage = errorMessage.replace( (char)8220, (char)' '); // left double
								   errorMessage = errorMessage.replace( (char)8221, (char)' '); // right double
								// Ganesh special char end
								

								response.write("\""+ errorMessage+"\"");
								// End of Insert by Naga 999								
								if(j != lastErrorMessageIndex)
								{
									response.write(",");
								}
							}
						
						response.write("]");
					}					
				}
				if(requestIdSource.contains("Invite"))
				{
				requestId=requestId+"01";
				if(errorLogMap.containsKey(requestId))
				{
					List<String> errorMessageList = errorLogMap.get(requestId);
				
					if(!errorMessageList.isEmpty())
					{
						response.write(",\"errorMessages\": [");
							
							int errorMessageListSize = errorMessageList.size();
							int lastErrorMessageIndex = errorMessageList.size() - 1;
							
							for(int j = 0; j < errorMessageListSize; j++)
							{
								// Begin of Insert by Naga 999
								String errorMessage = errorMessageList.get(j);
								
								//Ganesh added for Special Char removal
								   errorMessage = errorMessage.replace("\"", " ");
								   errorMessage = errorMessage.replace("\'", " ");	
								   errorMessage = errorMessage.replace("\\", " ");	
								   errorMessage = errorMessage.replace("\n", " ");	
								   errorMessage = errorMessage.replace("\r", " ");
								   errorMessage = errorMessage.replace("\t", " ");		
								   errorMessage = errorMessage.replace( (char)145,(char)' ');
								   errorMessage = errorMessage.replace( (char)146,(char)' ');
								   errorMessage = errorMessage.replace( (char)147, (char)' ');
								   errorMessage = errorMessage.replace( (char)148, (char)' ');
								   errorMessage = errorMessage.replace( (char)150, (char)' ' );		
								   errorMessage = errorMessage.replace( (char)8211, (char)' ' ); // em dash??    
								   errorMessage = errorMessage.replace( (char)8216, (char)' '); // left single quote
								   errorMessage = errorMessage.replace( (char)8217, (char)' '); // right single quote
								   errorMessage = errorMessage.replace( (char)8220, (char)' '); // left double
								   errorMessage = errorMessage.replace( (char)8221, (char)' '); // right double
								// Ganesh special char end
								

								response.write("\""+ errorMessage+"\"");
								// End of Insert by Naga 999								
								if(j != lastErrorMessageIndex)
								{
									response.write(",");
								}
							}
						
						response.write("]");
					}					
				}
				}
		
				response.write("}");		
				
				if (!ret.isLastRow() && i != (maxRows-1)){
					response.write(",");
				}
				ret.nextRow();
			}
					
			response.write("]");
										
			client.disconnect();

			
			if ( kmLoggingActive.equalsIgnoreCase("true")){	
				String DateTime = kmlogger.GetTimeDate(); 
				kmlogger.logmessage("VRA_Inbox",DateTime+","+userId);
			}
	
    	} catch (Exception ey) {
    			ey.printStackTrace();
				res.write("Exception:"+ey.toString());
		}
	}
}