package com.steel.product.application.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	@Value( "${email.sendReportEmailRequired}" )
    private boolean apiAlertRequired;

	@Autowired
	PartyDetailsRepository partyRepo;

	@Autowired
	ReportsServiceImpl service;
	
	@Scheduled(cron = "${email.reportScheduleTime}")
	public void sendNotificationAlert() {
		
		if(apiAlertRequired) {
			logger.info( "apiAlertRequired == "+apiAlertRequired);
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			String strDate = dateFormat.format(date);
			
			List<Party> partyList = partyRepo.findAll();
			for (Party party : partyList) {
				if (party.getEmail1()!=null && party.getEmail1().length()>0) {
					mailSender.sendMail(party.getPartyName(), party.getEmail1(), party.getEmail2(), strDate, party.getnPartyId());
				}
			}
		
		}
	}
}