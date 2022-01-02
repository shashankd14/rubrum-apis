package com.steel.product.application.util;

import com.steel.product.application.entity.InwardEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CSVUtil {

    public Path generateCSV(String[] headers, List<InwardEntry> inwardEntries) {
        String fileName = "stock-report.csv";
        Path path = Paths.get("./");
        Path csvPath = path.resolve(fileName);
        try(BufferedWriter bufferedWriter = Files.newBufferedWriter(csvPath)) {
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader(headers));
            int sno = 0;
            for(InwardEntry inw:inwardEntries){
                csvPrinter.printRecord(sno++,inw.getCoilNumber(),inw.getCustomerBatchId(),inw.getCreatedOn(),inw.getUpdatedOn(),
                        inw.getMaterial().getDescription(),inw.getMaterialGrade().getGradeName(),inw.getfThickness(),inw.getfWidth(),
                        inw.getfLength(),"",inw.getInStockWeight());
            }
            csvPrinter.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        return csvPath;
    }

}
