package com.steel.product.application.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dao.PartyDetailsRepository;
import com.steel.product.application.dto.report.StockReportRequest;
import com.steel.product.application.dto.support.ContactUsDTO;
import com.steel.product.application.entity.Party;
import com.steel.product.application.entity.StockSummaryReportViewEntity;
import com.steel.product.application.service.MailSender;
import com.steel.product.application.service.ReportsService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
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

	@GetMapping("/reports/sendReportEmail/{partyId}")
	public ResponseEntity<Object> sendNotificationAlert(@PathVariable("partyId") int partyId) {

		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String strDate = dateFormat.format(date);

		List<Party> partyList = partyRepo.findAll();
		for (Party party : partyList) {
			if (party.getEmail1() != null && party.getEmail1().length() > 0 && party.getnPartyId() == partyId) {
				mailSender.sendMail(party, strDate);
			}
		}
		return new ResponseEntity<>("{\"status\": \"success\", \"message\":\"Email Report Sent Successfully..!\"}", new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping("/reports/reconcile/{coilNumber}")
	public ResponseEntity<Object> reconcileReport(@PathVariable("coilNumber") String partyId) {

		List<StockSummaryReportViewEntity> kk = reportsService.reconcileReport(partyId);

		return new ResponseEntity<>(kk, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/support/contactus")
	public ResponseEntity<Object> contactUs(@RequestBody ContactUsDTO contactUsDTO) {
		return mailSender.sendContactUsMail(contactUsDTO);
	}

}
