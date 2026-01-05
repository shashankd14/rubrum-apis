package com.steel.product.application.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.steel.product.application.dto.support.ContactUsDTO;
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
	
	@Value( "${email.contactusemailtemplatepath}" )
    private String filePath;
	
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
			
			/* Below Reports are not required for JSWONE*/
			/* 
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("STOCKREPORT")) {
				mailStts = true;
				reportsService.createStockReport(party.getnPartyId(), strDate, helper);
				reportsService.createStockDetailsReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("RMREPORT")) {
				mailStts = true;
				reportsService.createRMReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0
					&& party.getDailyReportsList().contains("MONTHWISE_PLAN_TRACKER")) {
				mailStts = true;
				reportsService.createMonthwisePlanTrackerReport(party.getnPartyId(), strDate, helper);
			}*/
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
					&& party.getDailyReportsList().contains("STOCKREPORT")) {
				mailStts = true;
				reportsService.createStockSummaryReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList().contains("INWARDREPORT")) {
				mailStts = true;
				reportsService.createInwardReport(party.getnPartyId(), strDate, helper);
			} 
			if (party.getDailyReportsList().contains("OUTWARDREPORT_PACKETWISE")) {
				mailStts = true;
				reportsService.createOutwardReport(party.getnPartyId(), strDate, helper);
			}
			if (party.getDailyReportsList().contains("OUTWARDREPORT_SUMMARY")) {
				mailStts = true;
				reportsService.createOutwardSummaryReport(party.getnPartyId(), strDate, helper);
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
	
	public ResponseEntity<Object> sendContactUsMail(ContactUsDTO contactUsDTO) {
		logger.info("******MailSender.sendContactUsMail **************");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			logger.info("Name is : " + contactUsDTO.getName());
			MimeMessage message = javaMailSender.createMimeMessage();

			StringWriter writer = new StringWriter();
			Map<Object, Object> map = new HashMap<>();
			map.put("name", (contactUsDTO.getName() == null ? "" : contactUsDTO.getName()));
			map.put("email", (contactUsDTO.getEmail() == null ? "" : contactUsDTO.getEmail()));
			map.put("contact", (contactUsDTO.getContact() == null ? "" : contactUsDTO.getContact()));
			map.put("company", (contactUsDTO.getCompany() == null ? "" : contactUsDTO.getCompany()));
			map.put("message", (contactUsDTO.getMessage() == null ? "" : contactUsDTO.getMessage()));
			VelocityContext context = new VelocityContext(map);
			Template template = velocityEngine.getTemplate(filePath);
			if (template != null) {
				template.merge(context, writer);
			}
			String emailBody = writer.toString();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(fromMailId);
			helper.setTo(contactUsDTO.getEmail());
			helper.setSubject("Enquiry");
			helper.setText(emailBody, true);
			javaMailSender.send(message);
			logger.info("Email Sent Successfully to " + contactUsDTO.getEmail());
		} catch (Exception e) {
			logger.info("MailSender.Fail1: "+e.getMessage());
			return new ResponseEntity<>("{\"status\": \"fail\", \"message\":\"failed to sent email..!\"}", headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(
		    "{\"status\":\"success\",\"message\":\"We received your details and we will contact you shortly..!\"}",
		    headers,
		    HttpStatus.OK
		);
	}

	/*
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
	}*/

}
