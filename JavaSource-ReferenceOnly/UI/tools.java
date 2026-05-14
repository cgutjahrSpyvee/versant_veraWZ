package com.nbcu.vra.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.UMFactory;
import com.sapportals.portal.navigation.INavigationNode;
import com.sapportals.portal.navigation.INavigationService;
import com.sapportals.portal.navigation.NavigationEventsHelperService;
import com.sapportals.portal.navigation.NavigationNodes;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.IServiceProfile;
import com.sapportals.portal.prt.service.jco.IJCOClientService;
import com.sapportals.portal.prt.service.landscape.IEPSystem;
import com.sapportals.portal.prt.service.landscape.ILandscapeService;
import com.sapportals.portal.prt.session.IUserContext;
import com.sapportals.wcm.repository.ICollection;
import com.sapportals.wcm.repository.IPropertyName;
import com.sapportals.wcm.repository.IResourceFactory;
import com.sapportals.wcm.repository.IResourceList;
import com.sapportals.wcm.repository.IResourceListIterator;
import com.sapportals.wcm.repository.PropertyName;
import com.sapportals.wcm.repository.ResourceContext;
import com.sapportals.wcm.repository.ResourceFactory;
import com.sapportals.wcm.util.uri.RID;
import com.sapportals.wcm.util.usermanagement.WPUMFactory;

public class tools
{
	
	private static Hashtable getEnvironment(IPortalComponentRequest request) {

		Hashtable environment = new Hashtable();
		IUserContext userContext = request.getUser();
		
		NavigationEventsHelperService helperService =(NavigationEventsHelperService)PortalRuntime.getRuntimeResources().getService("com.sap.portal.navigation.helperservice.navigation_events_helper");
		IServiceProfile profile = helperService.getContext().getServiceProfile();
		String desktopFilterMode= profile.getProperty("FilterbyDesktopView");
		environment.put("desktopFilterMode", desktopFilterMode);

		if (userContext != null) {
			environment.put("NavigationPrincipal", userContext);
			String user = userContext.getUniqueName();

			if (user != null && !user.equals("")) {
				environment.put("User", user);

			}
		}
		return environment;
	}
	
	
	private static String getSystemURL(String sysId, IPortalComponentRequest request){
		String result = "";

		ILandscapeService landscapeService = (ILandscapeService) request.getService(ILandscapeService.KEY);

		// get the system information from landscape service
		IEPSystem system = landscapeService.getEPSystem(sysId);

		String WASHost = system.getAttribute("wap.WAS.hostname");
		String WASProtocol = system.getAttribute("wap.WAS.protocol");
		String WASPath = system.getAttribute("wap.WAS.path");		 

		result = WASProtocol+"://"+WASHost+WASPath;
		
		return result;
	}
	
	public static INavigationNode getNavNode(IPortalComponentRequest request, String nodeName) {

		INavigationService service = (INavigationService) PortalRuntime.getRuntimeResources().getService(INavigationService.KEY);
		INavigationNode initialNodes = null;

		//initialNodes = service.getInitialNodes(getEnvironment(request));
		initialNodes = service.getNode(getEnvironment(request),nodeName);

		return initialNodes;
	}
	
	public static NavigationNodes getTopNodes(IPortalComponentRequest request) throws NamingException {

		INavigationService service = (INavigationService) PortalRuntime.getRuntimeResources().getService(INavigationService.KEY);
		NavigationNodes initialNodes = null;

		initialNodes = service.getInitialNodes(getEnvironment(request));

		return initialNodes;
	}	

	private static IResourceList getKMContent(String strRID, String strUsername) {
		IResourceList resList = null;
		
		try {

			RID aRid = RID.getRID(strRID);

			com.sap.security.api.IUser portalUser = UMFactory.getUserFactory().getUserByLogonID(strUsername);

			com.sapportals.portal.security.usermanagement.IUser sapUser = WPUMFactory.getUserFactory().getEP5User(portalUser);
			sapUser.setTransientAttribute("j_authscheme", "basicauthentication");
			sapUser.setTransientAttribute("MYSAPSSO2_STRING", IPrincipal.DEFAULT_NAMESPACE);

			ResourceContext ctxt = new ResourceContext(sapUser);

			IResourceFactory aResourceFactory = ResourceFactory.getInstance();

			ICollection aCollection = (ICollection) aResourceFactory.getResource(aRid, ctxt);

			IPropertyName propName=new PropertyName("http://sapportals.com/xmlns/cm","displayname");

			if (aCollection!=null){
				resList = aCollection.getChildren();
			} 


		} catch (Exception e){
			e.printStackTrace();
		}

		return resList;
	}		

	public static String displayIViews(INavigationNode navigationIVNode,IPortalComponentRequest request){
		String result = "";
		String servername = request.getServletRequest().getServerName();
		
		String userName = request.getUser().getUniqueName();
		
		//Handle different iView types
		try {
			if (navigationIVNode.getAttributeValue("CodeLink").toString().indexOf("BSP")>=0){

				result = result+"<li><a href='"+getSystemURL(navigationIVNode.getAttributeValue("System").toString(),request)+""+navigationIVNode.getAttributeValue("CustomerNamespace")+"/"+navigationIVNode.getAttributeValue("Application")+"/"+navigationIVNode.getAttributeValue("PageId")+"'><i class='icon-chevron-right'></i> <strong>"+navigationIVNode.getTitle(request.getLocale())+"</strong></a></li>";

			} else if (navigationIVNode.getAttributeValue("CodeLink").toString().indexOf("urliviews")>=0){

				result = result+"<li><a href='"+navigationIVNode.getAttributeValue("url")+"'><i class='icon-chevron-right'></i> <strong>"+navigationIVNode.getTitle(request.getLocale())+"</strong></a></li>";
				
			} else if (navigationIVNode.getAttributeValue("CodeLink").toString().indexOf("km.cm.navigation")>=0) {
				IResourceListIterator resItrDocs = null;
				String strRID = navigationIVNode.getAttributeValue("path").toString();
				result = result+"<!-- Km Start -->";
				result = result+"<div class='row'>";
				result = result+"<div class='span10'>";
				result = result+"<div class='breadcrumb'>";
				result = result+"<button type='button' class='close' data-dismiss='alert'>&times;</button>";
				result = result+"<h3>"+navigationIVNode.getTitle(request.getLocale())+"</h3>";
 
				resItrDocs = getKMContent(strRID,userName).listIterator();

				while(resItrDocs.hasNext()){

					com.sapportals.wcm.repository.IResource restemp = resItrDocs.next();

					if(!restemp.isCollection() && (restemp.getLastModified()!=null)) {	
					

						DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
						
						String AccessURL;
						try {
							AccessURL = "/irj/go/km/docs"+restemp.getAccessRID().toString();
						} catch (Exception ue){
							AccessURL = ue.getLocalizedMessage();
						}

						result = result+"<p><strong>"+restemp.getDisplayName()+" - " +dateFormat.format(restemp.getLastModified())+"</strong> - "+restemp.getDescription()+"</p>";
						//result = result+"<p><a href='"+AccessURL+"' class='btn btn-primary btn-small' data-toggle='modal'>Read &raquo;</a>";
						//result = result+"</div>";

					} else if (restemp.isCollection() && (restemp.getLastModified()!=null)){

						result = result+"<br>Folder: "+restemp.getDisplayName();
					}

				}										    		
				
				result = result+"</div></div></div>";  
				result = result+"<!-- Km End -->";

			} else if (navigationIVNode.getAttributeValue("CodeLink").toString().indexOf("uwl_placeholder")>=0){
		
				// Data
				
				IPortalComponentProfile profile = request.getComponentContext().getProfile();
				String WFSystemAlias = "SAP_R3";
				JCO.Table ret = null;
				
				try {
					//get a client service
					IJCOClientService clientService = (IJCOClientService) PortalRuntime.getRuntimeResources().getService(IJCOClientService.KEY);
	
					JCO.Client client = clientService.getJCOClient(WFSystemAlias, request);
	
					// connect to SAP system
					 client.connect();
	
					IRepository m_Repository = JCO.createRepository("repository", client);
	
					IFunctionTemplate Z_SF_I485_DISPLAY_LIST = m_Repository.getFunctionTemplate("Z_SF_I485_DISPLAY_INBOX");
	
					JCO.Function function = new JCO.Function(Z_SF_I485_DISPLAY_LIST);
					JCO.ParameterList importList = function.getImportParameterList();
		 			importList.setValue(userName, "I_SSO_ID");
		 			
					client.execute(function);
	
					client.disconnect();
				
					ret = function.getTableParameterList().getTable("T_WORKITEM");
				
				} catch (Exception e) {
				  result = result+"WFException: "+e.getMessage()+" ";
				  e.printStackTrace();
				}			
				result = result+"<div class='app_header_div'>";
				result = result+"<h1 class='app_header_text lead'>CAM Workflow</h1>";
				result = result+"</div>";
				result = result+"<div class='container'> ";
				result = result+" <table class='table table-hover'>";
				result = result+" <thead>";
				result = result+"<tr>";
	            result = result+"<td>Workflow Number</td>";				
	            result = result+"<td>Invoice Number</td>";
	            result = result+"<td>Due Date</td>";
	            result = result+"<td>Amount</td>";           
				result = result+"</tr>";
				result = result+" </thead>";
				result = result+"    <tbody>";
			
				for(int i = 0; i < ret.getNumRows(); i++) {
					result = result+" <tr>";
					result = result+"<td><a href='/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2fcom.nbcu.html5_workflow_item?id="+ret.getString("WI_ID")+"'>"+ret.getString("WI_ID")+"</a></td><td>"+ret.getString("DOC_NUM")+"</td><td>"+ret.getString("DUE_DATE")+"</td><td>"+ret.getString("AMT")+"</td>";
					result = result+"</tr>";
	
					ret.nextRow();
				}
				
				result = result+"</tr> ";                   
				result = result+"</tbody>";
				result = result+"</table> ";       
				result = result+"</div>";
				
			} else if (navigationIVNode.getAttributeValue("CodeLink").toString().indexOf("html5portal")>=0){

				result = result+"<li><a href='/irj/servlet/prt/portal/prtroot/pcd!3aportal_content!2fcom.nbcu.html5_portal!2fiviews!2f"+navigationIVNode.getAttributeValue("com.sap.portal.pcm.idPrefix")+"."+navigationIVNode.getAttributeValue("com.sap.portal.pcm.idName")+"'><strong>"+navigationIVNode.getTitle(request.getLocale())+"</strong></a></li>";
		
			} else {
				result = "iView Type if not supported..yet";
			}
		} catch (NoSuchAttributeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getLocalizedMessage();
		}

		return result;
	}
	
	   public static String formatDatefromSAPtoJson(String inputDate) {
    	 String outputDate = null;
    	 String[] arr = inputDate.split("-");
    	 
    	 // Year - Month - Day
    	 outputDate = arr[0]+arr[2]+arr[1];
    	 
    	 if (outputDate.equalsIgnoreCase("00000000")) {
    		 outputDate = "99999999";
    	 }
    	 
		return outputDate;
	}
	   
	   public static String[][] setupMinorityCodeArray() {
		   String[][] arrayInternal = new String [8][2]; 
			arrayInternal[0][0] = "LO";
			arrayInternal[0][1] = "LGBT Owned";
			arrayInternal[1][0] = "MA";
			arrayInternal[1][1] = "Minority Owned - Asian/Pacific Islander";
			arrayInternal[2][0] = "MB";
			arrayInternal[2][1] = "Minority Owned - Black/African AmericanSamoa";
			arrayInternal[3][0] = "MH";
			arrayInternal[3][1] = "Minority Owned - Hispanic/Latino";
			arrayInternal[4][0] = "MN";
			arrayInternal[4][1] = "Minority Owned - American Indian/Alaskan";
			arrayInternal[5][0] = "VO";
			arrayInternal[5][1] = "Veteran Owned";
			arrayInternal[6][0] = "WO";
			arrayInternal[6][1] = "Woman Owned";
			arrayInternal[7][0] = "ZZ";
			arrayInternal[7][1] = "Non Diverse";
			sortBidimentionalArray(arrayInternal, 0);
			
			
		return arrayInternal;
	}

	   public static String[][] setupIndustryCodeArray() {
		   String[][] arrayInternal = new String [55][2];
		   
			arrayInternal[0][0]="10";
			arrayInternal[0][1]="Plants/Animals&Splys";
			arrayInternal[1][0]="11";
			arrayInternal[1][1]="Mineral/Textile/Scra";
			arrayInternal[2][0]="12";
			arrayInternal[2][1]="Mfg/Indus. Chemicals";
			arrayInternal[3][0]="13";
			arrayInternal[3][1]="Mfg Rubber/Plastic";
			arrayInternal[4][0]="14";
			arrayInternal[4][1]="Paper Matls/Products";
			arrayInternal[5][0]="15";
			arrayInternal[5][1]="Fuels/Additives/Lube";
			arrayInternal[6][0]="20";
			arrayInternal[6][1]="Mining Machs/Splys";
			arrayInternal[7][0]="21";
			arrayInternal[7][1]="Farm/Fish Machs/Sply";
			arrayInternal[8][0]="22";
			arrayInternal[8][1]="Road/Bldg Machs/Sply";
			arrayInternal[9][0]="23";
			arrayInternal[9][1]="Mfg/Indus Machs/Sply";
			arrayInternal[10][0]="24";
			arrayInternal[10][1]="Matl/Store/Whse Mach";
			arrayInternal[11][0]="25";
			arrayInternal[11][1]="All vehicles&MRO";
			arrayInternal[12][0]="26";
			arrayInternal[12][1]="Power Gen&Dist Machs";
			arrayInternal[13][0]="27";
			arrayInternal[13][1]="Tools/General Machs";
			arrayInternal[14][0]="30";
			arrayInternal[14][1]="Road/Bldg MRO Splys";
			arrayInternal[15][0]="31";
			arrayInternal[15][1]="Mfg Components&Splys";
			arrayInternal[16][0]="32";
			arrayInternal[16][1]="Electron Comps&Splys";
			arrayInternal[17][0]="39";
			arrayInternal[17][1]="Light/Electrcal Fixt";
			arrayInternal[18][0]="40";
			arrayInternal[18][1]="HVAC/Plmb Equip";
			arrayInternal[19][0]="41";
			arrayInternal[19][1]="Lab/Test/Measr Equip";
			arrayInternal[20][0]="42";
			arrayInternal[20][1]="Med Equip/Splys";
			arrayInternal[21][0]="43";
			arrayInternal[21][1]="Computer Comps/Splys";
			arrayInternal[22][0]="44";
			arrayInternal[22][1]="Office Equip/Splys";
			arrayInternal[23][0]="45";
			arrayInternal[23][1]="Print/Phot/AV Eq&Sy";
			arrayInternal[24][0]="46";
			arrayInternal[24][1]="SafetySsecure Eq&Sy";
			arrayInternal[25][0]="47";
			arrayInternal[25][1]="Clean/Envir Equip/Sy";
			arrayInternal[26][0]="48";
			arrayInternal[26][1]="Food Svcs Equip/Sply";
			arrayInternal[27][0]="49";
			arrayInternal[27][1]="Sports/Rec Equip/Spl";
			arrayInternal[28][0]="50";
			arrayInternal[28][1]="Food/Bev/Tobcco Prod";
			arrayInternal[29][0]="51";
			arrayInternal[29][1]="Drug/Pharm Prods";
			arrayInternal[30][0]="52";
			arrayInternal[30][1]="Home Appl/Cons Elec";
			arrayInternal[31][0]="53";
			arrayInternal[31][1]="Clothes/Pers Care/Hy";
			arrayInternal[32][0]="54";
			arrayInternal[32][1]="Jewel/Gems/Time Prod";
			arrayInternal[33][0]="55";
			arrayInternal[33][1]="Published Products";
			arrayInternal[34][0]="56";
			arrayInternal[34][1]="Furniture&Access";
			arrayInternal[35][0]="60";
			arrayInternal[35][1]="Musical/Toy/Art/Educ";
			arrayInternal[36][0]="70";
			arrayInternal[36][1]="Farm/Fish Contr Svcs";
			arrayInternal[37][0]="71";
			arrayInternal[37][1]="Mining/Oil/Gas Svcs";
			arrayInternal[38][0]="72";
			arrayInternal[38][1]="Road/Bldg/Maint Svcs";
			arrayInternal[39][0]="73";
			arrayInternal[39][1]="Mfg/Indus Svcs";
			arrayInternal[40][0]="76";
			arrayInternal[40][1]="Indus Clean Svcs";
			arrayInternal[41][0]="77";
			arrayInternal[41][1]="Environ Svcs";
			arrayInternal[42][0]="78";
			arrayInternal[42][1]="Trans/Store/Mail Svc";
			arrayInternal[43][0]="80";
			arrayInternal[43][1]="Mgmt/Bus/Prof Svcs";
			arrayInternal[44][0]="81";
			arrayInternal[44][1]="Resch/Eng/Comp Svcs";
			arrayInternal[45][0]="82";
			arrayInternal[45][1]="Arts/Edit/Media Svcs";
			arrayInternal[46][0]="83";
			arrayInternal[46][1]="Utility/Telcom Svcs";
			arrayInternal[47][0]="84";
			arrayInternal[47][1]="Finance/Insur Svcs";
			arrayInternal[48][0]="85";
			arrayInternal[48][1]="Healthcare Services";
			arrayInternal[49][0]="86";
			arrayInternal[49][1]="Training/Educ Svcs";
			arrayInternal[50][0]="90";
			arrayInternal[50][1]="T&E/Food/Lodge Svcs";
			arrayInternal[51][0]="91";
			arrayInternal[51][1]="Personal/Home Svcs";
			arrayInternal[52][0]="92";
			arrayInternal[52][1]="Safety/Secure Svcs";
			arrayInternal[53][0]="93";
			arrayInternal[53][1]="Political/Civic Svcs";
			arrayInternal[54][0]="94";
			arrayInternal[54][1]="Clubs/Orgs/Assoc";
			//Added by Jorge Sort array by A-Z
    	 	sortBidimentionalArray(arrayInternal,1);
		return arrayInternal;
	}

	   public static String[][] setupCountryCodeArray() {
		   String[][] arrayInternal = new String [242][2];

		   arrayInternal[0][0]="AD";
		   arrayInternal[0][1]="Andorra";		   
		   arrayInternal[1][0]="AE";
		   arrayInternal[1][1]="Utd.Arab Emir.";
		   arrayInternal[2][0]="AF";
		   arrayInternal[2][1]="Afghanistan";
		   arrayInternal[3][0]="AG";
		   arrayInternal[3][1]="Antigua/Barbuda";
		   arrayInternal[4][0]="AI";
		   arrayInternal[4][1]="Anguilla";
		   arrayInternal[5][0]="AL";
		   arrayInternal[5][1]="Albania";
		   arrayInternal[6][0]="AM";
		   arrayInternal[6][1]="Armenia";
		   arrayInternal[7][0]="AN";
		   arrayInternal[7][1]="Dutch Antilles";
		   arrayInternal[8][0]="AO";
		   arrayInternal[8][1]="Angola";
		   arrayInternal[9][0]="AQ";
		   arrayInternal[9][1]="Antarctica";
		   arrayInternal[10][0]="AR";
		   arrayInternal[10][1]="Argentina";
		   arrayInternal[11][0]="AS";
		   arrayInternal[11][1]="Samoa, American";
		   arrayInternal[12][0]="AT";
		   arrayInternal[12][1]="Austria";
		   arrayInternal[13][0]="AU";
		   arrayInternal[13][1]="Australia";
		   arrayInternal[14][0]="AW";
		   arrayInternal[14][1]="Aruba";
		   arrayInternal[15][0]="AZ";
		   arrayInternal[15][1]="Azerbaijan";
		   arrayInternal[16][0]="BA";
		   arrayInternal[16][1]="Bosnia-Herz.";
		   arrayInternal[17][0]="BB";
		   arrayInternal[17][1]="Barbados";
		   arrayInternal[18][0]="BD";
		   arrayInternal[18][1]="Bangladesh";
		   arrayInternal[19][0]="BE";
		   arrayInternal[19][1]="Belgium";
		   arrayInternal[20][0]="BF";
		   arrayInternal[20][1]="Burkina-Faso";
		   arrayInternal[21][0]="BG";
		   arrayInternal[21][1]="Bulgaria";
		   arrayInternal[22][0]="BH";
		   arrayInternal[22][1]="Bahrain";
		   arrayInternal[23][0]="BI";
		   arrayInternal[23][1]="Burundi";
		   arrayInternal[24][0]="BJ";
		   arrayInternal[24][1]="Benin";
		   arrayInternal[25][0]="BM";
		   arrayInternal[25][1]="Bermuda";
		   arrayInternal[26][0]="BN";
		   arrayInternal[26][1]="Brunei Dar-es-S";
		   arrayInternal[27][0]="BO";
		   arrayInternal[27][1]="Bolivia";
		   arrayInternal[28][0]="BR";
		   arrayInternal[28][1]="Brazil";
		   arrayInternal[29][0]="BS";
		   arrayInternal[29][1]="Bahamas";
		   arrayInternal[30][0]="BT";
		   arrayInternal[30][1]="Bhutan";
		   arrayInternal[31][0]="BV";
		   arrayInternal[31][1]="Bouvet Island";
		   arrayInternal[32][0]="BW";
		   arrayInternal[32][1]="Botswana";
		   arrayInternal[33][0]="BY";
		   arrayInternal[33][1]="White Russia";
		   arrayInternal[34][0]="BZ";
		   arrayInternal[34][1]="Belize";
		   arrayInternal[35][0]="CA";
		   arrayInternal[35][1]="Canada";
		   arrayInternal[36][0]="CC";
		   arrayInternal[36][1]="Coconut Islands";
		   arrayInternal[37][0]="CF";
		   arrayInternal[37][1]="Central Afr.Rep";
		   arrayInternal[38][0]="CG";
		   arrayInternal[38][1]="Congo";
		   arrayInternal[39][0]="CH";
		   arrayInternal[39][1]="Switzerland";
		   arrayInternal[40][0]="CI";
		   arrayInternal[40][1]="Ivory Coast";
		   arrayInternal[41][0]="CK";
		   arrayInternal[41][1]="Cook Islands";
		   arrayInternal[42][0]="CL";
		   arrayInternal[42][1]="Chile";
		   arrayInternal[43][0]="CM";
		   arrayInternal[43][1]="Cameroon";
		   arrayInternal[44][0]="CN";
		   arrayInternal[44][1]="China";
		   arrayInternal[45][0]="CO";
		   arrayInternal[45][1]="Colombia";
		   arrayInternal[46][0]="CR";
		   arrayInternal[46][1]="Costa Rica";
		   arrayInternal[47][0]="CU";
		   arrayInternal[47][1]="Cuba";
		   arrayInternal[48][0]="CV";
		   arrayInternal[48][1]="Cape Verde";
		   arrayInternal[49][0]="CX";
		   arrayInternal[49][1]="Christmas Islnd";
		   arrayInternal[50][0]="CY";
		   arrayInternal[50][1]="Cyprus";
		   arrayInternal[51][0]="CZ";
		   arrayInternal[51][1]="Czech Republic";
		   arrayInternal[52][0]="DE";
		   arrayInternal[52][1]="Germany";
		   arrayInternal[53][0]="DJ";
		   arrayInternal[53][1]="Djibouti";
		   arrayInternal[54][0]="DK";
		   arrayInternal[54][1]="Denmark";
		   arrayInternal[55][0]="DM";
		   arrayInternal[55][1]="Dominica";
		   arrayInternal[56][0]="DO";
		   arrayInternal[56][1]="Dominican Rep.";
		   arrayInternal[57][0]="DZ";
		   arrayInternal[57][1]="Algeria";
		   arrayInternal[58][0]="EC";
		   arrayInternal[58][1]="Ecuador";
		   arrayInternal[59][0]="EE";
		   arrayInternal[59][1]="Estonia";
		   arrayInternal[60][0]="EG";
		   arrayInternal[60][1]="Egypt";
		   arrayInternal[61][0]="EH";
		   arrayInternal[61][1]="West Sahara";
		   arrayInternal[62][0]="ER";
		   arrayInternal[62][1]="Eritrea";
		   arrayInternal[63][0]="ES";
		   arrayInternal[63][1]="Spain";
		   arrayInternal[64][0]="ET";
		   arrayInternal[64][1]="Ethiopia";
		   arrayInternal[65][0]="FI";
		   arrayInternal[65][1]="Finland";
		   arrayInternal[66][0]="FJ";
		   arrayInternal[66][1]="Fiji";
		   arrayInternal[67][0]="FK";
		   arrayInternal[67][1]="Falkland Islnds";
		   arrayInternal[68][0]="FM";
		   arrayInternal[68][1]="Micronesia";
		   arrayInternal[69][0]="FO";
		   arrayInternal[69][1]="Faroe Islands";
		   arrayInternal[70][0]="FR";
		   arrayInternal[70][1]="France";
		   arrayInternal[71][0]="GA";
		   arrayInternal[71][1]="Gabon";
		   arrayInternal[72][0]="GB";
		   arrayInternal[72][1]="United Kingdom";	
		   arrayInternal[73][0]="GD";
		   arrayInternal[73][1]="Grenada";
		   arrayInternal[74][0]="GE";
		   arrayInternal[74][1]="Georgia";
		   arrayInternal[75][0]="GF";
		   arrayInternal[75][1]="French Guayana";
		   arrayInternal[76][0]="GH";
		   arrayInternal[76][1]="Ghana";
		   arrayInternal[77][0]="GI";
		   arrayInternal[77][1]="Gibraltar";
		   arrayInternal[78][0]="GL";
		   arrayInternal[78][1]="Greenland";
		   arrayInternal[79][0]="GM";
		   arrayInternal[79][1]="Gambia";
		   arrayInternal[80][0]="GN";
		   arrayInternal[80][1]="Guinea";
		   arrayInternal[81][0]="GP";
		   arrayInternal[81][1]="Guadeloupe";
		   arrayInternal[82][0]="GQ";
		   arrayInternal[82][1]="Equatorial Gui.";
		   arrayInternal[83][0]="GR";
		   arrayInternal[83][1]="Greece";
		   arrayInternal[84][0]="GS";
		   arrayInternal[84][1]="S. Sandwich Ins";
		   arrayInternal[85][0]="GT";
		   arrayInternal[85][1]="Guatemala";
		   arrayInternal[86][0]="GU";
		   arrayInternal[86][1]="Guam";
		   arrayInternal[87][0]="GW";
		   arrayInternal[87][1]="Guinea-Bissau";
		   arrayInternal[88][0]="GY";
		   arrayInternal[88][1]="Guyana";
		   arrayInternal[89][0]="HK";
		   arrayInternal[89][1]="Hong Kong";
		   arrayInternal[90][0]="HM";
		   arrayInternal[90][1]="Heard/McDon.Isl";
		   arrayInternal[91][0]="HN";
		   arrayInternal[91][1]="Honduras";
		   arrayInternal[92][0]="HR";
		   arrayInternal[92][1]="Croatia";
		   arrayInternal[93][0]="HT";
		   arrayInternal[93][1]="Haiti";
		   arrayInternal[94][0]="HU";
		   arrayInternal[94][1]="Hungary";
		   arrayInternal[95][0]="ID";
		   arrayInternal[95][1]="Indonesia";
		   arrayInternal[96][0]="IE";
		   arrayInternal[96][1]="Ireland";
		   arrayInternal[97][0]="IL";
		   arrayInternal[97][1]="Israel";
		   arrayInternal[98][0]="IN";
		   arrayInternal[98][1]="India";
		   arrayInternal[99][0]="IO";
		   arrayInternal[99][1]="Brit.Ind.Oc.Ter";
		   arrayInternal[100][0]="IQ";
		   arrayInternal[100][1]="Iraq";
		   arrayInternal[101][0]="IR";
		   arrayInternal[101][1]="Iran";
		   arrayInternal[102][0]="IS";
		   arrayInternal[102][1]="Iceland";
		   arrayInternal[103][0]="IT";
		   arrayInternal[103][1]="Italy";
		   arrayInternal[104][0]="JE";
		   arrayInternal[104][1]="Jersey";
		   arrayInternal[105][0]="JM";
		   arrayInternal[105][1]="Jamaica";
		   arrayInternal[106][0]="JO";
		   arrayInternal[106][1]="Jordan";
		   arrayInternal[107][0]="JP";
		   arrayInternal[107][1]="Japan";
		   arrayInternal[108][0]="KE";
		   arrayInternal[108][1]="Kenya";
		   arrayInternal[109][0]="KG";
		   arrayInternal[109][1]="Kyrgyzstan";
		   arrayInternal[110][0]="KH";
		   arrayInternal[110][1]="Cambodia";
		   arrayInternal[111][0]="KI";
		   arrayInternal[111][1]="Kiribati";
		   arrayInternal[112][0]="KM";
		   arrayInternal[112][1]="Comoros";
		   arrayInternal[113][0]="KN";
		   arrayInternal[113][1]="St Kitts&Nevis";
		   arrayInternal[114][0]="KP";
		   arrayInternal[114][1]="North Korea";
		   arrayInternal[115][0]="KR";
		   arrayInternal[115][1]="South Korea";
		   arrayInternal[116][0]="KW";
		   arrayInternal[116][1]="Kuwait";
		   arrayInternal[117][0]="KY";
		   arrayInternal[117][1]="Cayman Islands";
		   arrayInternal[118][0]="KZ";
		   arrayInternal[118][1]="Kazakhstan";
		   arrayInternal[119][0]="LA";
		   arrayInternal[119][1]="Laos";
		   arrayInternal[120][0]="LB";
		   arrayInternal[120][1]="Lebanon";
		   arrayInternal[121][0]="LC";
		   arrayInternal[121][1]="St. Lucia";
		   arrayInternal[122][0]="LI";
		   arrayInternal[122][1]="Liechtenstein";
		   arrayInternal[123][0]="LK";
		   arrayInternal[123][1]="Sri Lanka";
		   arrayInternal[124][0]="LR";
		   arrayInternal[124][1]="Liberia";
		   arrayInternal[125][0]="LS";
		   arrayInternal[125][1]="Lesotho";
		   arrayInternal[126][0]="LT";
		   arrayInternal[126][1]="Lithuania";
		   arrayInternal[127][0]="LU";
		   arrayInternal[127][1]="Luxembourg";
		   arrayInternal[128][0]="LV";
		   arrayInternal[128][1]="Latvia";
		   arrayInternal[129][0]="LY";
		   arrayInternal[129][1]="Libya";
		   arrayInternal[130][0]="MA";
		   arrayInternal[130][1]="Morocco";
		   arrayInternal[131][0]="MC";
		   arrayInternal[131][1]="Monaco";
		   arrayInternal[132][0]="MD";
		   arrayInternal[132][1]="Moldavia";
		   arrayInternal[133][0]="MG";
		   arrayInternal[133][1]="Madagascar";
		   arrayInternal[134][0]="MH";
		   arrayInternal[134][1]="Marshall Islnds";
		   arrayInternal[135][0]="MK";
		   arrayInternal[135][1]="Macedonia";
		   arrayInternal[136][0]="ML";
		   arrayInternal[136][1]="Mali";
		   arrayInternal[137][0]="MM";
		   arrayInternal[137][1]="Myanmar";
		   arrayInternal[138][0]="MN";
		   arrayInternal[138][1]="Mongolia";
		   arrayInternal[139][0]="MO";
		   arrayInternal[139][1]="Macau";
		   arrayInternal[140][0]="MP";
		   arrayInternal[140][1]="N.Mariana Islnd";
		   arrayInternal[141][0]="MQ";
		   arrayInternal[141][1]="Martinique";
		   arrayInternal[142][0]="MR";
		   arrayInternal[142][1]="Mauretania";
		   arrayInternal[143][0]="MS";
		   arrayInternal[143][1]="Montserrat";
		   arrayInternal[144][0]="MT";
		   arrayInternal[144][1]="Malta";
		   arrayInternal[145][0]="MU";
		   arrayInternal[145][1]="Mauritius";
		   arrayInternal[146][0]="MV";
		   arrayInternal[146][1]="Maldives";
		   arrayInternal[147][0]="MW";
		   arrayInternal[147][1]="Malawi";
		   arrayInternal[148][0]="MX";
		   arrayInternal[148][1]="Mexico";
		   arrayInternal[149][0]="MY";
		   arrayInternal[149][1]="Malaysia";
		   arrayInternal[150][0]="MZ";
		   arrayInternal[150][1]="Mozambique";
		   arrayInternal[151][0]="NA";
		   arrayInternal[151][1]="Namibia";
		   arrayInternal[152][0]="NC";
		   arrayInternal[152][1]="New Caledonia";
		   arrayInternal[153][0]="NE";
		   arrayInternal[153][1]="Niger";
		   arrayInternal[154][0]="NF";
		   arrayInternal[154][1]="Norfolk Island";
		   arrayInternal[155][0]="NG";
		   arrayInternal[155][1]="Nigeria";
		   arrayInternal[156][0]="NI";
		   arrayInternal[156][1]="Nicaragua";
		   arrayInternal[157][0]="NL";
		   arrayInternal[157][1]="Netherlands";
		   arrayInternal[158][0]="NO";
		   arrayInternal[158][1]="Norway";
		   arrayInternal[159][0]="NP";
		   arrayInternal[159][1]="Nepal";
		   arrayInternal[160][0]="NR";
		   arrayInternal[160][1]="Nauru";
		   arrayInternal[161][0]="NU";
		   arrayInternal[161][1]="Niue Islands";
		   arrayInternal[162][0]="NZ";
		   arrayInternal[162][1]="New Zealand";
		   arrayInternal[163][0]="OM";
		   arrayInternal[163][1]="Oman";
		   arrayInternal[164][0]="PA";
		   arrayInternal[164][1]="Panama";
		   arrayInternal[165][0]="PE";
		   arrayInternal[165][1]="Peru";
		   arrayInternal[166][0]="PF";
		   arrayInternal[166][1]="Frenc.Polynesia";
		   arrayInternal[167][0]="PG";
		   arrayInternal[167][1]="Papua Nw Guinea";
		   arrayInternal[168][0]="PH";
		   arrayInternal[168][1]="Philippines";
		   arrayInternal[169][0]="PK";
		   arrayInternal[169][1]="Pakistan";
		   arrayInternal[170][0]="PL";
		   arrayInternal[170][1]="Poland";
		   arrayInternal[171][0]="PM";
		   arrayInternal[171][1]="St.Pier,Miquel.";
		   arrayInternal[172][0]="PN";
		   arrayInternal[172][1]="Pitcairn Islnds";
		   arrayInternal[173][0]="PR";
		   arrayInternal[173][1]="Puerto Rico";
		   arrayInternal[174][0]="PT";
		   arrayInternal[174][1]="Portugal";
		   arrayInternal[175][0]="PW";
		   arrayInternal[175][1]="Palau";
		   arrayInternal[176][0]="PY";
		   arrayInternal[176][1]="Paraguay";
		   arrayInternal[177][0]="QA";
		   arrayInternal[177][1]="Qatar";
		   arrayInternal[178][0]="RE";
		   arrayInternal[178][1]="Reunion";
		   arrayInternal[179][0]="RO";
		   arrayInternal[179][1]="Romania";
		   arrayInternal[180][0]="RS";
		   arrayInternal[180][1]="Serbia";
		   arrayInternal[181][0]="RU";
		   arrayInternal[181][1]="Russian Fed.";
		   arrayInternal[182][0]="RW";
		   arrayInternal[182][1]="Ruanda";
		   arrayInternal[183][0]="SA";
		   arrayInternal[183][1]="Saudi Arabia";
		   arrayInternal[184][0]="SB";
		   arrayInternal[184][1]="Solomon Islands";
		   arrayInternal[185][0]="SC";
		   arrayInternal[185][1]="Seychelles";
		   arrayInternal[186][0]="SD";
		   arrayInternal[186][1]="Sudan";
		   arrayInternal[187][0]="SE";
		   arrayInternal[187][1]="Sweden";
		   arrayInternal[188][0]="SG";
		   arrayInternal[188][1]="Singapore";
		   arrayInternal[189][0]="SH";
		   arrayInternal[189][1]="St. Helena";
		   arrayInternal[190][0]="SI";
		   arrayInternal[190][1]="Slovenia";
		   arrayInternal[191][0]="SJ";
		   arrayInternal[191][1]="Svalbard";
		   arrayInternal[192][0]="SK";
		   arrayInternal[192][1]="Slovakia";
		   arrayInternal[193][0]="SL";
		   arrayInternal[193][1]="Sierra Leone";
		   arrayInternal[194][0]="SM";
		   arrayInternal[194][1]="San Marino";
		   arrayInternal[195][0]="SN";
		   arrayInternal[195][1]="Senegal";
		   arrayInternal[196][0]="SO";
		   arrayInternal[196][1]="Somalia";
		   arrayInternal[197][0]="SR";
		   arrayInternal[197][1]="Suriname";
		   arrayInternal[198][0]="SS";
		   arrayInternal[198][1]="South Sudan";
		   arrayInternal[199][0]="ST";
		   arrayInternal[199][1]="S.Tome,Principe";
		   arrayInternal[200][0]="SV";
		   arrayInternal[200][1]="El Salvador";
		   arrayInternal[201][0]="SY";
		   arrayInternal[201][1]="Syria";
		   arrayInternal[202][0]="SZ";
		   arrayInternal[202][1]="Swaziland";
		   arrayInternal[203][0]="TC";
		   arrayInternal[203][1]="Turksh Caicosin";
		   arrayInternal[204][0]="TD";
		   arrayInternal[204][1]="Chad";
		   arrayInternal[205][0]="TF";
		   arrayInternal[205][1]="French S.Territ";
		   arrayInternal[206][0]="TG";
		   arrayInternal[206][1]="Togo";
		   arrayInternal[207][0]="TH";
		   arrayInternal[207][1]="Thailand";
		   arrayInternal[208][0]="TJ";
		   arrayInternal[208][1]="Tajikstan";
		   arrayInternal[209][0]="TK";
		   arrayInternal[209][1]="Tokelau Islands";
		   arrayInternal[210][0]="TM";
		   arrayInternal[210][1]="Turkmenistan";
		   arrayInternal[211][0]="TN";
		   arrayInternal[211][1]="Tunisia";
		   arrayInternal[212][0]="TO";
		   arrayInternal[212][1]="Tonga";
		   arrayInternal[213][0]="TP";
		   arrayInternal[213][1]="East Timor";
		   arrayInternal[214][0]="TR";
		   arrayInternal[214][1]="Turkey";
		   arrayInternal[215][0]="TT";
		   arrayInternal[215][1]="Trinidad,Tobago";
		   arrayInternal[216][0]="TV";
		   arrayInternal[216][1]="Tuvalu";
		   arrayInternal[217][0]="TW";
		   arrayInternal[217][1]="Taiwan";
		   arrayInternal[218][0]="TZ";
		   arrayInternal[218][1]="Tanzania";
		   arrayInternal[219][0]="UA";
		   arrayInternal[219][1]="Ukraine";
		   arrayInternal[220][0]="UG";
		   arrayInternal[220][1]="Uganda";
		   arrayInternal[221][0]="UM";
		   arrayInternal[221][1]="Minor Outl.Ins.";		
		   arrayInternal[222][0]="UY";
		   arrayInternal[222][1]="Uruguay";
		   arrayInternal[223][0]="UZ";
		   arrayInternal[223][1]="Uzbekistan";
		   arrayInternal[224][0]="VA";
		   arrayInternal[224][1]="Vatican City";
		   arrayInternal[225][0]="VC";
		   arrayInternal[225][1]="St. Vincent";
		   arrayInternal[226][0]="VE";
		   arrayInternal[226][1]="Venezuela";
		   arrayInternal[227][0]="VG";
		   arrayInternal[227][1]="Brit.Virgin Is.";
		   arrayInternal[228][0]="VI";
		   arrayInternal[228][1]="Amer.Virgin Is.";
		   arrayInternal[229][0]="VN";
		   arrayInternal[229][1]="Vietnam";
		   arrayInternal[230][0]="VU";
		   arrayInternal[230][1]="Vanuatu";
		   arrayInternal[231][0]="WF";
		   arrayInternal[231][1]="Wallis,Futuna";
		   arrayInternal[232][0]="WS";
		   arrayInternal[232][1]="Western Samoa";
		   arrayInternal[233][0]="XM";
		   arrayInternal[233][1]="Montenegro";
		   arrayInternal[234][0]="YE";
		   arrayInternal[234][1]="Yemen";
		   arrayInternal[235][0]="YT";
		   arrayInternal[235][1]="Mayotte";
		   arrayInternal[236][0]="YU";
		   arrayInternal[236][1]="Yugoslavia";
		   arrayInternal[237][0]="ZA";
		   arrayInternal[237][1]="South Africa";
		   arrayInternal[238][0]="ZF";
		   arrayInternal[238][1]="ZForeign";
		   arrayInternal[239][0]="ZM";
		   arrayInternal[239][1]="Zambia";
		   arrayInternal[240][0]="ZW";
		   arrayInternal[240][1]="Zimbabwe";

		   arrayInternal[241][0]="AA";
		   arrayInternal[241][1]="AAAAAA";
		   //Added By Jorge Sort the array by A-Z
		   sortBidimentionalArray(arrayInternal, 1);

		   arrayInternal[0][0]="US";
		   arrayInternal[0][1]="United States";

		   return arrayInternal;
	   }

	  public static String[][] setupRecipientTypeArray() {
		   String[][] arrayInternal = new String [10][2];
					   
			arrayInternal[0][0]="01";
			arrayInternal[0][1]="Individual";
			arrayInternal[1][0]="02";
			arrayInternal[1][1]="Corporation";
			arrayInternal[2][0]="03";
			arrayInternal[2][1]="Partnership";
			arrayInternal[3][0]="04";
			arrayInternal[3][1]="Fiduciary";
			arrayInternal[4][0]="05";
			arrayInternal[4][1]="Nominee";
			arrayInternal[5][0]="06";
			arrayInternal[5][1]="Government or int.organization";
			arrayInternal[6][0]="07";
			arrayInternal[6][1]="'Tax exempt' organization";
			arrayInternal[7][0]="08";
			arrayInternal[7][1]="Private foundation";
			arrayInternal[8][0]="09";
			arrayInternal[8][1]="Artist or athlete";
			arrayInternal[9][0]="20";
			arrayInternal[9][1]="Type of Recipient Unknown";

		return arrayInternal;
	}
	  	
	public static String[][] setupTaxExemptArray() {
			   String[][] arrayInternal = new String [8][2];

				arrayInternal[0][0]="0000000001";
				arrayInternal[0][1]="501(c)(1)";
				arrayInternal[1][0]="0000000002";
				arrayInternal[1][1]="501(c)(2)";
				arrayInternal[2][0]="0000000003";
				arrayInternal[2][1]="501(c)(3)";
				arrayInternal[3][0]="0000000004";
				arrayInternal[3][1]="501(c)(4)";
				arrayInternal[4][0]="0000000005";
				arrayInternal[4][1]="501(c)(5)";
				arrayInternal[5][0]="0000000006";
				arrayInternal[5][1]="501(c)(6)";
				arrayInternal[6][0]="0000000098";
				arrayInternal[6][1]="International";
				arrayInternal[7][0]="0000000099";
				arrayInternal[7][1]="Other";	
			return arrayInternal;
		}	  

	public static String[][] setupTaxRecipientTypeArray() {
			   String[][] arrayInternal = new String [7][2];

				arrayInternal[0][0]="01";
				arrayInternal[0][1]="Individual / Sole Proprietor";
				arrayInternal[1][0]="02";
				arrayInternal[1][1]="C Corporation";
				arrayInternal[2][0]="03";
				arrayInternal[2][1]="S Corporation";
				arrayInternal[3][0]="04";
				arrayInternal[3][1]="Partnership";
				arrayInternal[4][0]="05";
				arrayInternal[4][1]="Trust / Estate";
				arrayInternal[5][0]="06";
				arrayInternal[5][1]="Limited Liability Company";
				arrayInternal[6][0]="07";
				arrayInternal[6][1]="Other";	
			return arrayInternal;
		}	

	public static String[][] setupOrganizationFocusArray() {
			   String[][] arrayInternal = new String [15][2];		   
				arrayInternal[0][0]="0000000001";
				arrayInternal[0][1]="College/University/School";
				arrayInternal[1][0]="0000000002";
				arrayInternal[1][1]="General Education";
				arrayInternal[2][0]="0000000003";
				arrayInternal[2][1]="Environment";
				arrayInternal[3][0]="0000000004";
				arrayInternal[3][1]="Health/Medical/Disease-Related";
				arrayInternal[4][0]="0000000005";
				arrayInternal[4][1]="Industry/Trade";
				arrayInternal[5][0]="0000000006";
				arrayInternal[5][1]="Religious";
				arrayInternal[6][0]="0000000007";
				arrayInternal[6][1]="Veteran";
				arrayInternal[7][0]="0000000008";
				arrayInternal[7][1]="Human Rights";
				arrayInternal[8][0]="0000000010";
				arrayInternal[8][1]="Social Service";
				arrayInternal[9][0]="00000000011";
				arrayInternal[9][1]="Community Development";
				arrayInternal[10][0]="00000000012";
				arrayInternal[10][1]="Civic Engagement";
				arrayInternal[11][0]="00000000013";
				arrayInternal[11][1]="Youth/Family";
				arrayInternal[12][0]="00000000014";
				arrayInternal[12][1]="International Relief/Development";
				arrayInternal[13][0]="00000000015";
				arrayInternal[13][1]="Arts/Media";
				arrayInternal[14][0]="0000000099";
				arrayInternal[14][1]="Other";	
			return arrayInternal;
		}	 
	
	public static String[][] setupCompanyScaleArray() {
			   String[][] arrayInternal = new String [4][2];	

				arrayInternal[0][0]="0000000001";
				arrayInternal[0][1]="International";
				arrayInternal[1][0]="0000000002";
				arrayInternal[1][1]="National";
				arrayInternal[2][0]="0000000003";
				arrayInternal[2][1]="State";
				arrayInternal[3][0]="0000000004";
				arrayInternal[3][1]="Local";

			return arrayInternal;
		}	
	
	public static String[][] setupFPNADesigArray() {
			   String[][] arrayInternal = new String [5][2];	
		   
				arrayInternal[0][0]="0000000001";
				arrayInternal[0][1]="Arts/Culture";
				arrayInternal[1][0]="0000000002";
				arrayInternal[1][1]="Civic/Community";
				arrayInternal[2][0]="0000000003";
				arrayInternal[2][1]="Educational";
				arrayInternal[3][0]="0000000004";
				arrayInternal[3][1]="Health/Human Service";
				arrayInternal[4][0]="0000000099";
				arrayInternal[4][1]="Other";				

			return arrayInternal;
		}

	public static String[][] setupContactDepartmentArray() {
			   String[][] arrayInternal = new String [25][2];	
		   
				arrayInternal[0][0]="0001";
				arrayInternal[0][1]="Managing Director";
				arrayInternal[1][0]="0002";
				arrayInternal[1][1]="Purchasing";
				arrayInternal[2][0]="0003";
				arrayInternal[2][1]="Sales";
				arrayInternal[3][0]="0004";
				arrayInternal[3][1]="Organization";
				arrayInternal[4][0]="0005";
				arrayInternal[4][1]="Administration";
				arrayInternal[5][0]="0006";
				arrayInternal[5][1]="Production";
				arrayInternal[6][0]="0007";
				arrayInternal[6][1]="Quality assurance";
				arrayInternal[7][0]="0008";
				arrayInternal[7][1]="Secretaries";
				arrayInternal[8][0]="0009";
				arrayInternal[8][1]="Financial department";
				arrayInternal[9][0]="0010";
				arrayInternal[9][1]="Legal department";
				arrayInternal[10][0]="0011";
				arrayInternal[10][1]="Applier";
				arrayInternal[11][0]="0012";
				arrayInternal[11][1]="Collection";
				arrayInternal[12][0]="0013";
				arrayInternal[12][1]="Credit";
				arrayInternal[13][0]="0014";
				arrayInternal[13][1]="Ship To";
				arrayInternal[14][0]="0015";
				arrayInternal[14][1]="Inv C/O";
				arrayInternal[15][0]="0016";
				arrayInternal[15][1]="Indirect Customer";
				arrayInternal[16][0]="0017";
				arrayInternal[16][1]="Trade Spend";
				arrayInternal[17][0]="0018";
				arrayInternal[17][1]="Treasury";
				arrayInternal[18][0]="0019";
				arrayInternal[18][1]="Media Buying";
				arrayInternal[19][0]="0020";
				arrayInternal[19][1]="Collection - DIGITAL";
				arrayInternal[20][0]="0021";
				arrayInternal[20][1]="Collection - NETWORK";
				arrayInternal[21][0]="0022";
				arrayInternal[21][1]="Collection - CABLE";
				arrayInternal[22][0]="0023";
				arrayInternal[22][1]="Collection - TVS";
				arrayInternal[23][0]="ZALT";
				arrayInternal[23][1]="Alternate Billing";
				arrayInternal[24][0]="ZCFS";
				arrayInternal[24][1]="CFS Department";			

			return arrayInternal;
		}	
	
	public static String[][] setupUSStatesArray() {
			   String[][] arrayInternal = new String [59][2];	
		   
				arrayInternal[0][0]="AA";
				arrayInternal[0][1]="ArmedForcesAmerica";
				arrayInternal[1][0]="AE";
				arrayInternal[1][1]="ArmedForces(other)";
				arrayInternal[2][0]="AK";
				arrayInternal[2][1]="Alaska";
				arrayInternal[3][0]="AL";
				arrayInternal[3][1]="Alabama";
				arrayInternal[4][0]="AP";
				arrayInternal[4][1]="ArmedForcesPacific";
				arrayInternal[5][0]="AR";
				arrayInternal[5][1]="Arkansas";
				arrayInternal[6][0]="AS";
				arrayInternal[6][1]="AmericanSamoa";
				arrayInternal[7][0]="AZ";
				arrayInternal[7][1]="Arizona";
				arrayInternal[8][0]="CA";
				arrayInternal[8][1]="California";
				arrayInternal[9][0]="CO";
				arrayInternal[9][1]="Colorado";
				arrayInternal[10][0]="CT";
				arrayInternal[10][1]="Connecticut";
				arrayInternal[11][0]="DC";
				arrayInternal[11][1]="DistrictofColumbia";
				arrayInternal[12][0]="DE";
				arrayInternal[12][1]="Delaware";
				arrayInternal[13][0]="FL";
				arrayInternal[13][1]="Florida";
				arrayInternal[14][0]="GA";
				arrayInternal[14][1]="Georgia";
				arrayInternal[15][0]="GU";
				arrayInternal[15][1]="Guam";
				arrayInternal[16][0]="HI";
				arrayInternal[16][1]="Hawaii";
				arrayInternal[17][0]="IA";
				arrayInternal[17][1]="Iowa";
				arrayInternal[18][0]="ID";
				arrayInternal[18][1]="Idaho";
				arrayInternal[19][0]="IL";
				arrayInternal[19][1]="Illinois";
				arrayInternal[20][0]="IN";
				arrayInternal[20][1]="Indiana";
				arrayInternal[21][0]="KS";
				arrayInternal[21][1]="Kansas";
				arrayInternal[22][0]="KY";
				arrayInternal[22][1]="Kentucky";
				arrayInternal[23][0]="LA";
				arrayInternal[23][1]="Louisiana";
				arrayInternal[24][0]="MA";
				arrayInternal[24][1]="Massachusetts";
				arrayInternal[25][0]="MD";
				arrayInternal[25][1]="Maryland";
				arrayInternal[26][0]="ME";
				arrayInternal[26][1]="Maine";
				arrayInternal[27][0]="MI";
				arrayInternal[27][1]="Michigan";
				arrayInternal[28][0]="MN";
				arrayInternal[28][1]="Minnesota";
				arrayInternal[29][0]="MO";
				arrayInternal[29][1]="Missouri";
				arrayInternal[30][0]="MP";
				arrayInternal[30][1]="NorthernMarianaIsl";
				arrayInternal[31][0]="MS";
				arrayInternal[31][1]="Mississippi";
				arrayInternal[32][0]="MT";
				arrayInternal[32][1]="Montana";
				arrayInternal[33][0]="NC";
				arrayInternal[33][1]="NorthCarolina";
				arrayInternal[34][0]="ND";
				arrayInternal[34][1]="NorthDakota";
				arrayInternal[35][0]="NE";
				arrayInternal[35][1]="Nebraska";
				arrayInternal[36][0]="NH";
				arrayInternal[36][1]="NewHampshire";
				arrayInternal[37][0]="NJ";
				arrayInternal[37][1]="NewJersey";
				arrayInternal[38][0]="NM";
				arrayInternal[38][1]="NewMexico";
				arrayInternal[39][0]="NV";
				arrayInternal[39][1]="Nevada";
				arrayInternal[40][0]="NY";
				arrayInternal[40][1]="NewYork";
				arrayInternal[41][0]="OH";
				arrayInternal[41][1]="Ohio";
				arrayInternal[42][0]="OK";
				arrayInternal[42][1]="Oklahoma";
				arrayInternal[43][0]="OR";
				arrayInternal[43][1]="Oregon";
				arrayInternal[44][0]="PA";
				arrayInternal[44][1]="Pennsylvania";
				arrayInternal[45][0]="PR";
				arrayInternal[45][1]="PuertoRico";
				arrayInternal[46][0]="RI";
				arrayInternal[46][1]="RhodeIsland";
				arrayInternal[47][0]="SC";
				arrayInternal[47][1]="SouthCarolina";
				arrayInternal[48][0]="SD";
				arrayInternal[48][1]="SouthDakota";
				arrayInternal[49][0]="TN";
				arrayInternal[49][1]="Tennessee";
				arrayInternal[50][0]="TX";
				arrayInternal[50][1]="Texas";
				arrayInternal[51][0]="UT";
				arrayInternal[51][1]="Utah";
				arrayInternal[52][0]="VA";
				arrayInternal[52][1]="Virginia";
				arrayInternal[53][0]="VI";
				arrayInternal[53][1]="VirginIslands";
				arrayInternal[54][0]="VT";
				arrayInternal[54][1]="Vermont";
				arrayInternal[55][0]="WA";
				arrayInternal[55][1]="Washington";
				arrayInternal[56][0]="WI";
				arrayInternal[56][1]="Wisconsin";
				arrayInternal[57][0]="WV";
				arrayInternal[57][1]="WestVirginia";
				arrayInternal[58][0]="WY";
				arrayInternal[58][1]="Wyoming";				

			return arrayInternal;
		}	

	  // Added by CMG, general JSON cleaner.
	   public static String jsonCleaner(String inputString){
		   String resultString = "";	   
		   
			   resultString = inputString;
		   		//Strip Double Quotes
			   resultString = resultString.replace("\"", "");
			   
			   // Strip Single Quote
			   resultString = resultString.replace("\'", "");	
			   
			   // Strip \ 
			   resultString = resultString.replace("\\", "");	
			   
			   // Strip RETURN 
			   resultString = resultString.replace("\n", " ");	
			   resultString = resultString.replace("\r", " ");
			   
			   // Strip \TAB 
			   resultString = resultString.replace("\t", "");		
			   
			   resultString = resultString.replace( (char)145, (char)' ');
				
			   resultString = resultString.replace( (char)8216, (char)' '); // left single quote
				
			   resultString = resultString.replace( (char)146, (char)' ');
				
			   resultString = resultString.replace( (char)8217, (char)' '); // right single quote
				
			   resultString = resultString.replace( (char)147, (char)' ');
				
			   resultString = resultString.replace( (char)148, (char)' ');
			
			   resultString = resultString.replace( (char)8220, (char)' '); // left double
				
			   resultString = resultString.replace( (char)8221, (char)' '); // right double
				
			   resultString = resultString.replace( (char)8211, (char)'-' ); // em dash??    
				
			   resultString = resultString.replace( (char)150, (char)'-' );				   
			   
		   return resultString;
	    }	
	
		//Added by Jorge 
		//This method sort a Bidimentional array by A-Z
		private static void sortBidimentionalArray(String[][] arrayInternal, final int index)
		{

			Arrays.sort(arrayInternal, new Comparator<String[]>() {
			    @Override
			    public int compare(String[] s1, String[] s2) {
			        return s1[index].compareTo(s2[index]);
			    }
			});

		}
		//This method sort a Bidimentional array by A-Z
		public static void sortSecondaryAccount(String[][] arrayInternal)
		{

			Arrays.sort(arrayInternal, new Comparator<String[]>() {
			    @Override
			    public int compare(String[] s1, String[] s2) {
			    	String first = s1[7];
			    	String second = s2[7];
			       return ((first.charAt(0) =='W' || first.charAt(0) == 'U')?first.substring(1):first).compareTo((second.charAt(0)=='W' || second.charAt(0)=='U')?second.substring(1):second);
			    }
			});

		}
}

