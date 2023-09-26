package com.steel.product.application.service;

import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component("apiEmailReports")
public class MailSender {

	static Logger logger = LoggerFactory.getLogger(MailSender.class);

	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	ReportsService reportsService;
	
	@Value( "${spring.mail.username}" )
    private String fromMailId;
	
	@Value( "${email.text}" )
    private String emailBody;
	
	private VelocityEngine velocityEngine;
	
	@Autowired 
	Environment env;
	
	@PostConstruct
    private void init()
    {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        velocityEngine.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
    }
	
	public void sendMail(String partyName, String emailId1, String emailId2, String strDate, int partyId) {

		logger.info("******MailSender.sendMail**************");
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			reportsService.createStockReport(partyId, strDate, helper);
			reportsService.createFGReport(partyId, strDate, helper);
			reportsService.createWIPReport(partyId, strDate, helper);
			reportsService.createStockSummaryReport( partyId, strDate, helper);
			reportsService.createRMReport( partyId, strDate, helper);
			helper.setFrom(fromMailId);
			helper.setTo(emailId1);
			
			StringTokenizer st = new StringTokenizer(emailId2, ",");
			while (st.hasMoreTokens()) {
				String ccEmailId=st.nextToken();
				helper.addCc(ccEmailId);
			}
			helper.setSubject(partyName +" - Daily Reports on "+strDate);
			helper.setText(emailBody, true);
			javaMailSender.send(message);
			logger.info("Email Sent Successfully to "+emailId1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("MailSender.Fail1: "+e.getMessage());
		}
	}

}
