package com.steel.product.application.util;

import com.steel.product.application.entity.InwardEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CSVUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public File generateStockReportCSV(String[] headers, List<InwardEntry> inwardEntries) {
        LOGGER.info("inside generate stock report csv for "+inwardEntries.size()+" entries");
        String fileName = "Aspen-stock-report-"+ LocalDateTime.now()+".csv";
        Path path = Paths.get("./");
        Path csvPath = path.resolve(fileName);
        try(BufferedWriter bufferedWriter = Files.newBufferedWriter(csvPath)) {
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(headers));
            int sno = 0;
            for(InwardEntry inw:inwardEntries){
                csvPrinter.printRecord(++sno,inw.getCoilNumber(),inw.getCustomerBatchId(),sdf.format(inw.getdReceivedDate()),sdf.format(inw.getCreatedOn()),
                        inw.getMaterial().getDescription(),inw.getMaterialGrade().getGradeName(),inw.getfThickness(),inw.getfWidth(),
                        inw.getfLength(),inw.getMaterial().getMaterialCode(),inw.getInStockWeight());
            }
            csvPrinter.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        LOGGER.info(fileName+" generated ok !");
        return csvPath.toFile();
    }

}
