package com.nbcu.vra.tools;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sapportals.portal.security.usermanagement.IUser;
import com.sapportals.wcm.repository.Content;
import com.sapportals.wcm.repository.ICollection;
import com.sapportals.wcm.repository.IResourceFactory;
import com.sapportals.wcm.repository.ResourceContext;
import com.sapportals.wcm.repository.ResourceFactory;
import com.sapportals.wcm.util.uri.RID;
import com.sapportals.wcm.util.usermanagement.WPUMFactory;


public class kmlogger {
	
	public static String logmessage(String strLogTitle,String strLogEntry){
		  return  KmLogFile(strLogTitle,strLogEntry);
	}
	

	private static String KmLogFile(String strLogTitle, String strLogEntry){
	  DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	  Date date = new Date();
	  String strResult="";
		
	  try {
		  RID aRid = RID.getRID("/fisker_portal/Administrative Content/Logfiles");
		  IUser user = WPUMFactory.getServiceUserFactory().getServiceUser("cmadmin_service");
		  ResourceContext ctxt = new ResourceContext(user);
		
		  IResourceFactory aResourceFactory = ResourceFactory.getInstance();

		  ICollection aCollection = (ICollection) aResourceFactory.getResource(aRid, ctxt);
			   
		  if (!aResourceFactory.checkExistence(RID.getRID("/fisker_portal/Administrative Content/Logfiles/"+strLogTitle+"-"+dateFormat.format(date)+".txt"),ctxt)){
				
			  String strStatus = strLogEntry+"\n";
			  ByteArrayInputStream inpStatus = new ByteArrayInputStream(strStatus.getBytes());

			  Content aContent = new Content(inpStatus, "text/plain", -1L);
			  aCollection.createResource(strLogTitle+"-"+dateFormat.format(date)+".txt",null,aContent);
				
			  aContent.close();
				
		  } else {
				
			  String strStatus = strLogEntry+"\n";
			  ByteArrayInputStream inpStatus = new ByteArrayInputStream(strStatus.getBytes());

			  InputStream inpStatus2 = aResourceFactory.getResource(RID.getRID("/fisker_portal/Administrative Content/Logfiles/"+strLogTitle+"-"+dateFormat.format(date)+".txt"),ctxt).getContent().getInputStream();
					
			  SequenceInputStream cContent = new SequenceInputStream(inpStatus,inpStatus2);
			
			  Content aContent = new Content(cContent, "text/plain", -1L);
				
			  aResourceFactory.getResource(RID.getRID("/fisker_portal/Administrative Content/Logfiles/"+strLogTitle+"-"+dateFormat.format(date)+".txt"),ctxt).updateContent(aContent);
				
			  aContent.close();
		  }

	  } catch (Exception e){
		  e.printStackTrace();
		  strResult=e.toString();
	  }
	  return strResult;
	}
 
}