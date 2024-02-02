package com.steel.product.application.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.entity.Party;

@Component
public class ReportsEmailScheduler {

	static Logger logger = LoggerFactory.getLogger(ReportsEmailScheduler.class);

	@Autowired
	@Qualifier("apiEmailReports")
	public MailSender mailSender;

	@Value("${email.sendReportEmailRequired}")
	private boolean apiAlertRequired;

	@Autowired
	PartyDetailsRepository partyRepo;

	@Autowired
	ReportsServiceImpl service;

	@Scheduled(cron = "${email.reportScheduleTime}")
	public void sendNotificationAlert() throws InterruptedException {

		if (apiAlertRequired) {
			logger.info("sendDailyNotificationAlert apiAlertRequired == " + apiAlertRequired);
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			String strDate = dateFormat.format(date);

			List<Party> partyList = partyRepo.findAll();
			for (Party party : partyList) {
				if (party.getEmail1() != null && party.getEmail1().length() > 0 && party.getDailyReportsList() != null && party.getDailyReportsList().length() > 0) {
					mailSender.sendMail(party, strDate);
					Thread.sleep(200);
				}
			}
		}
	}

	@Scheduled(cron = "${email.reportsMonthlyScheduleTime}")
	public void sendMonthlyNotifications() throws InterruptedException {
		if (apiAlertRequired) {
			logger.info("sendMonthlyNotifications apiAlertRequired == " + apiAlertRequired);
			LocalDate currentDate = LocalDate.now();
			LocalDate previousMonth = currentDate.minusMonths(1);
			Integer currentYear = currentDate.getYear();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");
			Integer month = Integer.parseInt(previousMonth.format(formatter));
			if(month==12) {
				 currentYear = currentYear-1;
			}
			logger.info("currentYear  == " + currentYear);
			logger.info("month  == " + month);
			List<Party> partyList = partyRepo.findAll();
			for (Party party : partyList) {
				if (party.getEmail1() != null && party.getEmail1().length() > 0 && party.getMonthlyReportsList() != null && party.getMonthlyReportsList().length() > 0) {
					mailSender.sendMonthlyReportsMail(party, month, currentYear);
					Thread.sleep(200);
				}
			}
		}
	}

}