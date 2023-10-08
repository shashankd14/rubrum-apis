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

import com.steel.product.application.entity.Party;

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
	
	public void sendMail(Party party, String strDate) {

		logger.info("******MailSender.sendMail**************");
		boolean mailStts=false;
		try {
			System.out.println("Party name is : "+party.getPartyName()+", partyId == "+party.getnPartyId());

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("STOCKREPORT")) {
				mailStts = true;
				reportsService.createStockReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("FGREPORT")) {
				mailStts = true;
				reportsService.createFGReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("WIPREPORT")) {
				mailStts = true;
				reportsService.createWIPReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("STOCKSUMMARYREPORT")) {
				mailStts = true;
				reportsService.createStockSummaryReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("RMREPORT")) {
				mailStts = true;
				reportsService.createRMReport(party.getnPartyId(), strDate, helper);
			}
			helper.setFrom(fromMailId);
			helper.setTo(party.getEmail1());
			if (party.getEmail2() != null && party.getEmail2().length() > 0) {
				StringTokenizer st = new StringTokenizer(party.getEmail2(), ",");
				while (st.hasMoreTokens()) {
					String ccEmailId = st.nextToken();
					helper.addCc(ccEmailId);
				}
			}
			helper.setSubject(party.getPartyName() +" - Daily Reports on "+strDate);
			helper.setText(emailBody, true);
			if(mailStts) {
				javaMailSender.send(message);
			}
			logger.info("Email Sent Successfully to "+party.getEmail1());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("MailSender.Fail1: "+e.getMessage());
		}
	}

}
