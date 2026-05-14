package com.nbcu.vra.tools;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class mailsender {
	
	/**
		*
		* @param to String
		* @param from String
		* @param sub String
		* @param msg String
		* @param smtpServer String
		* @return void
	*/
	public static String sendMessage(String to, String from, String sub, String msg){
		  return  Send(from, to, sub, msg);
	}
	
	/**
		* This method is used to make the e-mail body and send mail
		*
		* @param to String
		* @param from String
		* @param sub String
		* @param msg String
		* @param smtpServer String
		* @return void
	*/
	
	public static String Send(String From, String To, String Subject, String msgText1 ) {
 
	   int ErrorStatus = 0; 
	   
	   Properties props = System.getProperties();
	   props.put("mail.smtp.host", "ExchCasHubAn01.fisker.local");
	   props.put("mail.smtp.auth", "true");
	   
	   Authenticator auth = (new mailsender()).new SMTPAuthenticator();
	   
	   Session session = Session.getInstance(props, auth);

	   try {
		   
	 
		   MimeMessage msg = new MimeMessage(session);
		   msg.setFrom(new InternetAddress(From));
		   InternetAddress[] address = {new InternetAddress(To)};
		   msg.setRecipients(Message.RecipientType.TO, address);
		   msg.setSubject(Subject);

		   MimeBodyPart mbp1 = new MimeBodyPart();
		   mbp1.setText(msgText1);
		   
		   Multipart mp = new MimeMultipart();
		   mp.addBodyPart(mbp1);
		   
		   msg.setContent(mp);
		
		   msg.setSentDate(new Date());
 
		  
		   Transport.send(msg);
		   return "";
	   } catch (Exception mex) {
		   return mex.toString();
	   }
   }
   
   private class SMTPAuthenticator extends javax.mail.Authenticator
   {

	   public PasswordAuthentication getPasswordAuthentication()
	   {
		   String username = "SapLDAP";
		   String password = "$$801S@P";
		   return new PasswordAuthentication(username, password);
	   }
   }

 
}