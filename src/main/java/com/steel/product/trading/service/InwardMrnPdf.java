package com.steel.product.trading.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.steel.product.trading.dto.InwardTradingResponse;
import com.steel.product.trading.dto.InwardTradingChildResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Renders the persisted receipt, with all weights expressed in kilograms. */
public final class InwardMrnPdf {
    private static final BaseColor INK = new BaseColor(35, 45, 60);
    private static final BaseColor MUTED = new BaseColor(100, 110, 125);
    private InwardMrnPdf() { }

    public static byte[] render(InwardTradingResponse receipt) throws DocumentException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 40);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override public void onEndPage(PdfWriter pdfWriter, Document doc) {
                ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_RIGHT,
                    new Phrase("Page " + pdfWriter.getPageNumber(), font(8, false, MUTED)), doc.right(), 22, 0);
            }
        });
        document.addTitle("Material Receipt Note " + text(receipt.getInwardNo()));
        document.open();
        Paragraph title = new Paragraph("MATERIAL RECEIPT NOTE", font(18, true, INK));
        title.setSpacingAfter(6); document.add(title);
        Paragraph reference = new Paragraph(text(receipt.getInwardNo()), font(12, true, new BaseColor(0, 110, 230)));
        reference.setSpacingAfter(18); document.add(reference);
        PdfPTable metadata = new PdfPTable(2); metadata.setWidthPercentage(100);
        detail(metadata, "Vendor", receipt.getVendorName()); detail(metadata, "Received date", date(receipt.getReceivedDate()));
        detail(metadata, "PO number", receipt.getPoNumber()); detail(metadata, "Vendor batch", receipt.getVendorBatchNo());
        detail(metadata, "Location", receipt.getLocationName()); detail(metadata, "Purpose", receipt.getPurposeType());
        detail(metadata, "Vehicle", receipt.getVehicleNo()); detail(metadata, "Consignment", receipt.getConsignmentId());
        detail(metadata, "Document", text(receipt.getDocumentType()) + " / " + text(receipt.getDocumentNo())); detail(metadata, "Document date", date(receipt.getDocumentDate()));
        metadata.setSpacingAfter(18); document.add(metadata);

        PdfPTable items = new PdfPTable(new float[] { 3.3f, 1f, 1.3f, 1.3f, 1.5f }); items.setWidthPercentage(100);
        for (String heading : new String[] { "Material / Heat / Pack", "Pieces", "Net (KG)", "Theo. / Invoice (KG)", "Value (INR)" }) cell(items, heading, true);
        items.setHeaderRows(1);
        if (receipt.getItemsList() != null) for (InwardTradingChildResponse item : receipt.getItemsList()) {
            String name = text(item.getItemName());
            if (item.getHeatNo() != null && !item.getHeatNo().isEmpty()) name += "\nHeat: " + item.getHeatNo();
            if (item.getBundleNumbers() != null && !item.getBundleNumbers().isEmpty()) name += "\nPack: " + item.getBundleNumbers();
            cell(items, name, false); cell(items, text(item.getActualNoofPieces()), false);
            cell(items, number(item.getNetWeight()), false); cell(items, number(item.getTheoreticalWeight()), false); cell(items, number(item.getVolume()), false);
        }
        items.setSpacingAfter(16); document.add(items);
        PdfPTable totals = new PdfPTable(2); totals.setWidthPercentage(100);
        detail(totals, "Net inward quantity (KG)", number(receipt.getTotalWeight()));
        detail(totals, "Item total (INR)", number(receipt.getTotalVolume()));
        detail(totals, "Freight (INR)", number(receipt.getFreightCharges())); detail(totals, "Loading (INR)", number(receipt.getLoadingCharges()));
        detail(totals, "Insurance (INR)", number(receipt.getInsuranceAmount())); detail(totals, "Weighment (INR)", number(receipt.getWeightmenCharges()));
        detail(totals, "CGST (INR)", number(receipt.getCgst())); detail(totals, "SGST (INR)", number(receipt.getSgst()));
        detail(totals, "IGST (INR)", number(receipt.getIgst())); detail(totals, "Grand total (INR)", number(receipt.getTotalInwardVolume()));
        totals.setSpacingAfter(16); document.add(totals);
        section(document, "QUALITY CHECK");
        PdfPTable quality = new PdfPTable(2); quality.setWidthPercentage(100);
        detail(quality, "QC status", receipt.getQcStatus()); detail(quality, "Certificate number", receipt.getQcCertificateNo());
        detail(quality, "Yield strength (MPa)", optionalNumber(receipt.getQcYieldStrength())); detail(quality, "Ultimate tensile strength (MPa)", optionalNumber(receipt.getQcTensileStrength()));
        detail(quality, "Elongation (%)", optionalNumber(receipt.getQcElongation())); detail(quality, "Remarks", receipt.getQcRemark());
        quality.setSpacingAfter(16); document.add(quality);
        section(document, "RECONCILIATION");
        PdfPTable recovery = new PdfPTable(2); recovery.setWidthPercentage(100);
        detail(recovery, "Deduct freight", Boolean.TRUE.equals(receipt.getDeductFreight()) ? "Yes" : "No");
        detail(recovery, "Raise debit note", Boolean.TRUE.equals(receipt.getRaiseDebitNote()) ? "Yes" : "No");
        detail(recovery, "Observation", receipt.getReconciliationRemark()); recovery.getDefaultCell().setBorder(Rectangle.NO_BORDER); recovery.completeRow(); document.add(recovery);
        document.close(); return output.toByteArray();
    }

    private static Font font(float size, boolean bold, BaseColor color) { return new Font(Font.FontFamily.HELVETICA, size, bold ? Font.BOLD : Font.NORMAL, color); }
    private static String text(Object value) { return value == null || value.toString().trim().isEmpty() ? "-" : value.toString(); }
    private static String number(BigDecimal value) { return value == null ? "0" : value.stripTrailingZeros().toPlainString(); }
    private static String optionalNumber(BigDecimal value) { return value == null ? "-" : number(value); }
    private static String date(Date value) { return value == null ? "-" : new SimpleDateFormat("dd MMM yyyy").format(value); }
    private static void detail(PdfPTable table, String label, Object value) {
        PdfPCell cell = new PdfPCell(); cell.setBorder(Rectangle.NO_BORDER); cell.setPadding(7);
        cell.addElement(new Phrase(label, font(8, false, MUTED))); cell.addElement(new Phrase(text(value), font(10, false, INK))); table.addCell(cell);
    }
    private static void cell(PdfPTable table, String value, boolean heading) {
        PdfPCell cell = new PdfPCell(new Phrase(text(value), font(8, heading, heading ? MUTED : INK)));
        cell.setPadding(7); cell.setBorderColor(new BaseColor(230, 233, 238));
        if (heading) cell.setBackgroundColor(new BaseColor(247, 248, 250)); table.addCell(cell);
    }
    private static void section(Document document, String title) throws DocumentException {
        Paragraph paragraph = new Paragraph(title, font(10, true, INK)); paragraph.setSpacingAfter(8); document.add(paragraph);
    }
}
