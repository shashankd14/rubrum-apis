package com.steel.product.application.util;

import com.steel.product.application.entity.Instruction;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class CSVUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);

    public File generateStockReportCSV(String[] headers, List<InwardEntry> inwardEntries, HashMap<Integer,Double> unprocessedWeights) {
        LOGGER.info("inside generate stock report csv for "+inwardEntries.size()+" entries");
        String fileName = "Aspen-stock-report-"+ LocalDateTime.now()+".csv";
        Path path = Paths.get("./");
        Path csvPath = path.resolve(fileName);
        LOGGER.info("inwards: "+inwardEntries.size());
        try(BufferedWriter bufferedWriter = Files.newBufferedWriter(csvPath)) {
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.EXCEL.withHeader(headers));
            for(InwardEntry inw:inwardEntries){
                    csvPrinter.printRecord(inw.getCoilNumber(), inw.getCustomerBatchId(),
                            inw.getMaterial().getDescription(), inw.getMaterialGrade().getGradeName(), inw.getfThickness(), inw.getfWidth(),
                            inw.getfLength(), inw.getfQuantity(), inw.getStatus().getStatusName(),inw.getInStockWeight(),
                            inw.getfThickness(),inw.getfWidth(),Math.round(inw.getAvailableLength()),unprocessedWeights.get(inw.getInwardEntryId()) != null ?
                                    Math.round(inw.getInStockWeight() - unprocessedWeights.get(inw.getInwardEntryId())) : inw.getInStockWeight(),
                            "", "", ""
                            , "", "", "", "");
                for(Instruction ins:inw.getInstructions()){
                        if (ins.getStatus().getStatusId() < 4 && !ins.getIsDeleted()) {
                            csvPrinter.printRecord(inw.getCoilNumber(), inw.getCustomerBatchId(),
                                    inw.getMaterial().getDescription(), inw.getMaterialGrade().getGradeName(), "", "",
                                    "", "", "","","","","","", ins.getInstructionId(), inw.getfThickness(), ins.getStatus().getStatusId() > 2 ? ins.getActualWidth() : ins.getPlannedWidth()
                                    , ins.getStatus().getStatusId() > 2 ? ins.getActualLength() : ins.getPlannedLength(), ins.getStatus().getStatusId() > 2 ? ins.getActualWeight() : ins.getPlannedWeight(), ins.getStatus().getStatusName(), ins.getPacketClassification() != null ? ins.getPacketClassification().getClassificationName() : "");
                    }
                }
            }
            csvPrinter.flush();
            LOGGER.info(fileName+" generated ok !");
            return csvPath.toFile();
        }catch (Exception e){
            LOGGER.error("error is generating csv "+e.getMessage());
            csvPath.toFile().delete();
            e.printStackTrace();
        }
        return null;
    }

}

