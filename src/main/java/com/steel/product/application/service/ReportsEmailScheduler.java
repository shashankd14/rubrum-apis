package com.steel.product.application.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

	@Value("${email.monthwise_summary_report_scheduler}")
	private boolean monthwiseSummaryReportScheduler;

	@Autowired
	PartyDetailsRepository partyRepo;
	
	@Autowired
	ReportsService reportsService;
	
	@Scheduled(cron = "${email.reportScheduleTime}")
	public void sendNotificationAlert() {
		if (!apiAlertRequired) {
			return;
		}

		String strDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")); // ✅ modern API
		logger.info("sendDailyNotificationAlert started — date={}", strDate);

		// ✅ filter eligible parties at DB level
		List<Party> partyList = partyRepo.findAllDailyReportParties();

		for (Party party : partyList) {
			if (!StringUtils.hasText(party.getEmail1()) || !StringUtils.hasText(party.getDailyReportsList())) {
				continue;
			}
			try {
				
				//party.setEmail1("kanakadri32@gmail.com");
				//party.setEmail2("aspen bidadi <aspen.bidadi@gmail.com>");
				//if(party.getnPartyId() == 36){
					logger.info("sendDailyNotificationAlert started — date={}", strDate);
					mailSender.sendMail(party, strDate);
					logger.info("Daily report sent — getPartyName={}, email={}", party.getPartyName(), party.getEmail1());
					Thread.sleep(200); // throttle SMTP — remove if not needed
				//}
			} catch (Exception e) {
				logger.error("Failed to send daily report — partyId={}, email={}, error={}", party.getnPartyId(), party.getEmail1(), e.getMessage());
			}
		}
		logger.info("sendNotificationAlert completed — processed {} parties", partyList.size());
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
			//logger.info("currentYear  == " + currentYear+", month  == " + month);
			List<Party> partyList = partyRepo.findAllParties();
			for (Party party : partyList) {
				if (party.getEmail1() != null && party.getEmail1().length() > 0 && party.getMonthlyReportsList() != null && party.getMonthlyReportsList().length() > 0) {
					mailSender.sendMonthlyReportsMail(party, month, currentYear);
					Thread.sleep(200);
				}
			}
		}
	}

	@Scheduled(cron = "${email.monthwise_summary_report_ScheduleTime}")
	public void monthwiseSummaryReportScheduleTime() throws InterruptedException {
		if (monthwiseSummaryReportScheduler) {
			logger.info("monthwiseSummaryReportScheduleTime apiAlertRequired == " + apiAlertRequired);
			LocalDate currentDate = LocalDate.now();
			LocalDate previousMonth = currentDate.minusMonths(1);
			Integer currentYear = currentDate.getYear();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");
			Integer month = Integer.parseInt(previousMonth.format(formatter));
			if(month==12) {
				currentYear = currentYear - 1;
			}
			
			logger.info("currentYear  == " + currentYear + ", month  == " + month);
			List<Integer> partyIdList = new ArrayList<Integer>();
			partyIdList.add(36);
			partyIdList.add(37);
			partyIdList.add(38);
			partyIdList.add(39);
			
			reportsService.getMonthlySummaryReport(partyIdList, month, currentYear);
			
		}
	}

}