package com.steel.product.application.service;

import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.util.CSVUtil;
import com.steel.product.application.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService{

    private final InwardEntryService inwardEntryService;
    private final CSVUtil csvUtil;
    private final EmailUtil emailUtil;

    @Autowired
    public EmailServiceImpl(InwardEntryService inwardEntryService, CSVUtil csvUtil, EmailUtil emailUtil) {
        this.inwardEntryService = inwardEntryService;
        this.csvUtil = csvUtil;
        this.emailUtil = emailUtil;
    }

    @Override
    public void sendEmail() throws MessagingException {

        String d = "2021-09-23"+" 00:00:00";
        String dd = "2021-09-27"+" 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d1 = null,d2 = null;
        try {
            d1 = sdf.parse(d);
            d2 = sdf.parse(dd);
        }catch (Exception e){
            e.printStackTrace();
        }
        List<InwardEntry> inwardEntries = inwardEntryService.findInwardByPartyIdAndCreatedOnBetween(2,d1,d2);
        String email = inwardEntries.get(0).getParty().getEmail1();
        System.out.println(email);
        System.out.println(inwardEntries.size());
        String[] headers = {"S.No","Coil No","Customer Batch No","Inward date","Process date","Material Description","Material Grade",
                "thickness","width","length","Form of material","Quantity in stock"};
        Path p = csvUtil.generateCSV(headers,inwardEntries);
        emailUtil.sendEmail(p);
    }
}
