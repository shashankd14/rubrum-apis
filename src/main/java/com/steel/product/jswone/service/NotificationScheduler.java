package com.steel.product.jswone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.steel.product.application.util.CommonUtil;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class NotificationScheduler {

	@Value("${email.soUpdateStatusFlag}")
	private boolean soUpdateStatusFlag;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	SalesOrderJswService salesOrderJswService;

	@Scheduled(cron = "${email.soUpdateStatusTime}")
	public void soUpdateStatusTime() throws InterruptedException {

		if (soUpdateStatusFlag) {
			salesOrderJswService.updateCPStatus(1);
		}
	}

}