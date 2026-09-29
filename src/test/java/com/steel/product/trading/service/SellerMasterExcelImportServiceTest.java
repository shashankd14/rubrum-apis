package com.steel.product.trading.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.persistence.EntityManager;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.web.server.ResponseStatusException;

import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.controller.MaterialController;
import com.steel.product.application.aop.CustomErrorHandler;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.repository.CategoryRepository;
import com.steel.product.trading.repository.SubCategoryRepository;
import com.steel.product.trading.request.*;

class SellerMasterExcelImportServiceTest {
    private MaterialMasterService material;
    private VendorService vendor;
    private LocationService location;
    private CustomerService customer;
    private WeighbridgeService weighbridge;
    private CategoryRepository categories;
    private SubCategoryRepository subcategories;
    private EntityManager entityManager;
    private SellerMasterExcelImportService importer;
    private RecordingTransactionManager transactions;

    @BeforeEach
    void setUp() {
        material = mock(MaterialMasterService.class);
        vendor = mock(VendorService.class);
        location = mock(LocationService.class);
        customer = mock(CustomerService.class);
        weighbridge = mock(WeighbridgeService.class);
        categories = mock(CategoryRepository.class);
        subcategories = mock(SubCategoryRepository.class);
        entityManager = mock(EntityManager.class);
        when(material.save(any(), isNull(), isNull())).thenReturn(ResponseEntity.ok("{}"));
        when(vendor.save(any())).thenReturn(ResponseEntity.ok("{}"));
        when(location.save(any())).thenReturn(ResponseEntity.ok("{}"));
        when(customer.save(any())).thenReturn(ResponseEntity.ok("{}"));
        when(weighbridge.save(any())).thenReturn(ResponseEntity.ok("{}"));
        SellerMasterExcelImportService service = new SellerMasterExcelImportService(material, vendor, location,
                customer, weighbridge, categories, subcategories, entityManager);
        transactions = new RecordingTransactionManager();
        ProxyFactory factory = new ProxyFactory(service);
        factory.setProxyTargetClass(true);
        factory.addAdvice(new TransactionInterceptor(transactions, new AnnotationTransactionAttributeSource()));
        importer = (SellerMasterExcelImportService) factory.getProxy();
    }

    @Test
    void importsAllFiveSheetsAndCommits() throws Exception {
        MockMultipartFile file = workbook(new String[][][] {
            {{"Material", "itemName", "itemCode", "canBeProcessed", "perMeter"}, {"", "Steel", "001", "yes", "12.50"}},
            {{"Vendor", "vendorName", "processTags", "additionalContacts", "emailId"}, {"", "Supplier", "Cutting,Slitting", "[{\"contactName\":\"A\",\"phoneNo\":\"9876543210\"}]", "sales@example.com"}},
            {{"Location", "locationName", "branchType", "contactName", "phoneNo"}, {"", "Warehouse", "WAREHOUSE", "B", "9876543211"}},
            {{"Customer", "customerName", "additionalAddresses"}, {"", "Buyer", "[{\"address1\":\"Road\",\"city\":\"Pune\",\"state\":\"Maharashtra\",\"pincode\":\"411001\"}]"}},
            {{"Weighbridge", "weighbridgeName", "capacityInTons"}, {"", "Scale", ""}}
        });
        Map<String, Object> result = importer.importWorkbook(file, 7);
        assertEquals(5, result.get("totalImported"));
        assertEquals(1, transactions.commits);
        assertEquals(0, transactions.rollbacks);
        ArgumentCaptor<MaterialMasterRequest> item = ArgumentCaptor.forClass(MaterialMasterRequest.class);
        verify(material).save(item.capture(), isNull(), isNull());
        assertEquals("001", item.getValue().getItemCode());
        assertEquals(7, item.getValue().getUserId());
        assertTrue(item.getValue().getCanBeProcessed());
        ArgumentCaptor<LocationRequest> branch = ArgumentCaptor.forClass(LocationRequest.class);
        verify(location).save(branch.capture());
        assertEquals("WAREHOUSE", branch.getValue().getBranchType());
        assertEquals("B", branch.getValue().getAdditionalContacts().get(0).getContactName());
        verify(entityManager, times(5)).flush();
    }

    @Test
    void createsCategoryAndSubcategoryAndMergesTechnicalParams() throws Exception {
        when(categories.save(any())).thenAnswer(call -> {
            CategoryEntity category = call.getArgument(0);
            category.setCategoryId(10);
            return category;
        });
        when(subcategories.save(any())).thenAnswer(call -> {
            SubCategoryEntity subcategory = call.getArgument(0);
            subcategory.setSubcategoryId(20);
            return subcategory;
        });
        importer.importWorkbook(workbook(new String[][][] {
            {{"Material", "itemName", "itemCode", "category", "subCategory", "technicalSpecs", "unitWeights"},
             {"", "Steel", "ST01", "Metal", "Sheet", "{\"thickness\":2}", "[]"}}
        }), 7);
        ArgumentCaptor<MaterialMasterRequest> item = ArgumentCaptor.forClass(MaterialMasterRequest.class);
        verify(material).save(item.capture(), isNull(), isNull());
        assertEquals(10, item.getValue().getCategoryId());
        assertEquals(20, item.getValue().getSubCategoryId());
        assertTrue(item.getValue().getAdditionalParams().contains("\"technicalSpecs\""));
    }

    @Test
    void rollsBackEarlierSavesWhenLaterServiceRejectsDuplicate() throws Exception {
        when(customer.save(any())).thenReturn(ResponseEntity.status(500).body("{\"message\":\"Entered Customer Name already used\"}"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Vendor", "vendorName"}, {"", "Supplier"}},
            {{"Customer", "customerName"}, {"", "Duplicate"}}
        }), 7));
        assertTrue(ex.getReason().contains("Customer row 2"));
        assertTrue(ex.getReason().contains("already used"));
        verify(vendor).save(any());
        assertEquals(1, transactions.rollbacks);
        assertEquals(0, transactions.commits);
    }

    @Test
    void rejectsPopulatedUnsupportedTemplateField() throws Exception {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Weighbridge", "weighbridgeName", "capacityInTons"}, {"", "Scale", "100"}}
        }), 7));
        assertTrue(ex.getReason().contains("capacityInTons"));
        verifyNoInteractions(weighbridge);
    }

    @Test
    void rejectsInvalidBooleanAndJson() throws Exception {
        for (String[] field : new String[][] {{"canBeProcessed", "maybe"}, {"unitWeights", "{}"}, {"technicalSpecs", "broken"}, {"perMeter", "-1"}}) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
                {{"Material", "itemName", "itemCode", field[0]}, {"", "Steel", "ST01", field[1]}}
            }), 7));
            assertTrue(ex.getReason().contains(field[0]));
        }
        verifyNoInteractions(material);
    }

    @Test
    void rejectsEmptyTemplateMissingNameAndIdHeaders() throws Exception {
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Vendor", "vendorName"}, {"", ""}}
        }), 7));
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Vendor", "vendorName", "phoneNo"}, {"", "", "9876543210"}}
        }), 7));
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Vendor", "vendorName", "vendorId"}, {"", "Supplier", "3"}}
        }), 7));
        verifyNoInteractions(vendor);
    }

    @Test
    void rejectsInvalidFileAndUser() {
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(
                new MockMultipartFile("file", "data.xlsx", "application/octet-stream", new byte[] {1, 2, 3}), 7));
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(
                new MockMultipartFile("file", "data.txt", "text/plain", new byte[] {1}), 7));
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(null, 0));
    }

    @Test
    void rejectsConflictingMaterialParamsAndSubcategoryWithoutCategory() throws Exception {
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Material", "itemName", "itemCode", "technicalSpecs", "additionalParams"},
             {"", "Steel", "ST01", "{\"thickness\":2}", "{\"technicalSpecs\":{\"thickness\":3}}"}}
        }), 7));
        assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Material", "itemName", "itemCode", "subCategory"}, {"", "Steel", "ST01", "Sheet"}}
        }), 7));
        verifyNoInteractions(material);
    }

    @Test
    void rejectsFormulaCellsAndReportsDatabaseFailureRow() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vendor");
            sheet.createRow(0).createCell(0).setCellValue("vendorName");
            sheet.createRow(1).createCell(0).setCellFormula("1+1");
            workbook.write(output);
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(
                    new MockMultipartFile("file", "masters.xlsx", "application/octet-stream", output.toByteArray()), 7));
            assertTrue(ex.getReason().contains("Vendor row 2"));
            verifyNoInteractions(vendor);
        }
        doThrow(new IllegalStateException("constraint failed")).when(entityManager).flush();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> importer.importWorkbook(workbook(new String[][][] {
            {{"Vendor", "vendorName"}, {"", "Supplier"}}
        }), 7));
        assertTrue(ex.getReason().contains("Vendor row 2"));
        assertEquals(0, transactions.commits);
        assertEquals(2, transactions.rollbacks);
    }

    @Test
    void acceptsMultipartUploadThroughMaterialControllerAndReturnsBadRequestForInvalidRows() throws Exception {
        MaterialController controller = new MaterialController();
        ReflectionTestUtils.setField(controller, "sellerMasterExcelImportService", importer);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new CustomErrorHandler()).build();
        mvc.perform(multipart("/material/master/import").file(workbook(new String[][][] {
            {{"Vendor", "vendorName"}, {"", "Supplier"}}
        })).param("userId", "7"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalImported").value(1))
                .andExpect(jsonPath("$.imported.Vendor").value(1));
        mvc.perform(multipart("/material/master/import").file(workbook(new String[][][] {
            {{"Vendor", "vendorName", "phoneNo"}, {"", "", "9876543210"}}
        })).param("userId", "7"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Vendor row 2: Name is required; Material also requires itemCode"));
    }

    private MockMultipartFile workbook(String[][][] sheets) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (String[][] data : sheets) {
                Sheet sheet = workbook.createSheet(data[0][0]);
                for (int r = 0; r < data.length; r++) {
                    Row row = sheet.createRow(r);
                    for (int c = 1; c < data[r].length; c++) {
                        row.createCell(c - 1).setCellValue(data[r][c]);
                    }
                }
            }
            workbook.write(output);
            return new MockMultipartFile("file", "Seller_Master_Simple.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", output.toByteArray());
        }
    }

    @Test
    void preservesStoredNumericWeightWhenExcelDisplayRoundsIt() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Material");
            Row headers = sheet.createRow(0);
            headers.createCell(0).setCellValue("itemName");
            headers.createCell(1).setCellValue("itemCode");
            headers.createCell(2).setCellValue("perMeter");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("Steel");
            row.createCell(1).setCellValue("ST01");
            row.createCell(2).setCellValue(12.345);
            org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("0"));
            row.getCell(2).setCellStyle(style);
            workbook.write(output);
            importer.importWorkbook(new MockMultipartFile("file", "masters.xlsx", "application/octet-stream", output.toByteArray()), 7);
            ArgumentCaptor<MaterialMasterRequest> item = ArgumentCaptor.forClass(MaterialMasterRequest.class);
            verify(material).save(item.capture(), isNull(), isNull());
            assertEquals(new java.math.BigDecimal("12.345"), item.getValue().getPerMeter());
        }
    }

    private static class RecordingTransactionManager extends AbstractPlatformTransactionManager {
        int commits;
        int rollbacks;
        @Override protected Object doGetTransaction() { return new Object(); }
        @Override protected void doBegin(Object transaction, TransactionDefinition definition) { }
        @Override protected void doCommit(DefaultTransactionStatus status) { commits++; }
        @Override protected void doRollback(DefaultTransactionStatus status) { rollbacks++; }
    }
}
