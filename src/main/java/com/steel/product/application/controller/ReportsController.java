package com.steel.product.application.controller;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.entity.Party;
import com.steel.product.application.service.MailSender;
import com.steel.product.application.service.ReportsService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Reports")
@CrossOrigin
public class ReportsController {

    private final ReportsService reportsService;
    
	@Autowired
	@Qualifier("apiEmailReports")
	public MailSender mailSender;

	@Autowired
	PartyDetailsRepository partyRepo;
	
    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @PostMapping("")
    public String generateAndMailStockReport(@RequestBody StockReportRequest stockReportRequest){
        return reportsService.generateAndMailStockReport(stockReportRequest);
    }

	@GetMapping("/sendReportEmail/{partyId}")
	public void sendNotificationAlert(@PathVariable("partyId") int partyId) {

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String strDate = dateFormat.format(date);

		List<Party> partyList = partyRepo.findAll();
		for (Party party : partyList) {
			if (party.getEmail1() != null && party.getEmail1().length() > 0 && party.getnPartyId() == partyId) {
				mailSender.sendMail(party.getPartyName(), party.getEmail1(), party.getEmail2(), strDate, party.getnPartyId());
			}
		}

	}

}
