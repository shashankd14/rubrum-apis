package com.steel.product.application.service;

import java.util.HashMap;
import java.util.Map;
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
	
	@Value( "${email.monthlyEmailBody}" )
    private String monthlyEmailBody;
	
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

		logger.info("******MailSender.sendDailyMail**************");
		boolean mailStts=false;
		try {
			logger.info("Party name is : "+party.getPartyName()+", DailyReportsList == "+party.getDailyReportsList());

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0 && party.getDailyReportsList().contains("STOCKREPORT")) {
				mailStts = true;
				reportsService.createStockReport(party.getnPartyId(), strDate, helper);
				reportsService.createStockDetailsReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0	&& party.getDailyReportsList().contains("FGREPORT")) {
				mailStts = true;
				reportsService.createFGReport(party.getnPartyId(), strDate, helper);
				reportsService.createEndUserTagWiseFGReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0	&& party.getDailyReportsList().contains("WIPREPORT")) {
				mailStts = true;
				reportsService.createWIPReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0 && party.getDailyReportsList().contains("STOCKSUMMARYREPORT")) {
				mailStts = true;
				reportsService.createStockSummaryReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0	&& party.getDailyReportsList().contains("RMREPORT")) {
				mailStts = true;
				reportsService.createRMReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0	&& party.getDailyReportsList().contains("MONTHWISE_PLAN_TRACKER")) {
				mailStts = true;
				reportsService.createMonthwisePlanTrackerReport( party.getnPartyId(), strDate, helper);
			}
			helper.setFrom(fromMailId);			
			if (party.getEmail1() != null && party.getEmail1().length() > 0) {
				StringTokenizer st = new StringTokenizer(party.getEmail1(), ",");
				while (st.hasMoreTokens()) {
					String ccEmailId = st.nextToken();
					helper.setTo(ccEmailId);
					helper.addCc(ccEmailId);
				}
			}			
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
	
	public void sendMonthlyReportsMail(Party party, Integer month, Integer year) {

		logger.info("******MailSender.sendMonthlyReportsMail**************");
		boolean mailStts=false;
		try {
			
			Map<Integer, String> months =new HashMap<>();
			months.put(1, "January");
			months.put(2, "February");
			months.put(3, "March");
			months.put(4, "April");
			months.put(5, "May");
			months.put(6, "June");
			months.put(7, "July");
			months.put(8, "August");
			months.put(9, "September");
			months.put(10, "October");
			months.put(11, "November");
			months.put(12, "December");
			
			logger.info("Party Name is : "+party.getPartyName()+",  MonthlyReportsList == "+party.getMonthlyReportsList());

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			if (party.getMonthlyReportsList() != null && party.getMonthlyReportsList().length() > 0) {

				if (party.getMonthlyReportsList().contains("INWARDREPORT")) {
					mailStts = true;
					reportsService.createInwardMonthlyReport(party.getnPartyId(), helper, month, months, year);
				}
				if (party.getMonthlyReportsList().contains("STOCKREPORT")) {
					mailStts = true;
					reportsService.createStockMonthlyReport(party.getnPartyId(), helper, month, months);
				}
				if (party.getMonthlyReportsList().contains("OUTWARDREPORT")) {
					mailStts = true;
					reportsService.createOutwardMonthlyReport(party.getnPartyId(), helper, month, months, year);
				}
				if (party.getMonthlyReportsList().contains("PROCESSINGREPORT")) {
					mailStts = true;
					reportsService.createProcessingMonthlyReport(party.getnPartyId(), helper, month, months, year);
					reportsService.createFinishingMonthlyReport(party.getnPartyId(), helper, month, months, year);
				}
			}
			helper.setFrom(fromMailId);
			
			if (party.getEmail1() != null && party.getEmail1().length() > 0) {
				StringTokenizer st = new StringTokenizer(party.getEmail1(), ",");
				while (st.hasMoreTokens()) {
					String ccEmailId = st.nextToken();
					helper.setTo(ccEmailId);
					helper.addCc(ccEmailId);
				}
			}
			
			if (party.getEmail2() != null && party.getEmail2().length() > 0) {
				StringTokenizer st = new StringTokenizer(party.getEmail2(), ",");
				while (st.hasMoreTokens()) {
					String ccEmailId = st.nextToken();
					helper.addCc(ccEmailId);
				}
			}
			helper.setSubject(party.getPartyName() +" - Monthly Reports for the month of - "+months.get(month));
			helper.setText(monthlyEmailBody, true);
			if(mailStts) {
				javaMailSender.send(message);
			}
			logger.info("Email Sent Successfully to "+party.getEmail1());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("MailSender.sendMonthlyReportsMail: "+e.getMessage());
		}
	}

}
