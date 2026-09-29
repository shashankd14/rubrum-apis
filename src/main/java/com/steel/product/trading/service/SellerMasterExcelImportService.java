package com.steel.product.trading.service;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.steel.product.trading.entity.CategoryEntity;
import com.steel.product.trading.entity.SubCategoryEntity;
import com.steel.product.trading.repository.CategoryRepository;
import com.steel.product.trading.repository.SubCategoryRepository;
import com.steel.product.trading.request.BaseRequest;
import com.steel.product.trading.request.CustomerRequest;
import com.steel.product.trading.request.ContactMasterRequest;
import com.steel.product.trading.request.LocationRequest;
import com.steel.product.trading.request.MaterialMasterRequest;
import com.steel.product.trading.request.VendorRequest;
import com.steel.product.trading.request.WeighbridgeRequest;

@Service
public class SellerMasterExcelImportService {
    private final MaterialMasterService materialService;
    private final VendorService vendorService;
    private final LocationService locationService;
    private final CustomerService customerService;
    private final WeighbridgeService weighbridgeService;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final EntityManager entityManager;
    private final ObjectMapper mapper = new ObjectMapper();

    public SellerMasterExcelImportService(MaterialMasterService materialService, VendorService vendorService,
            LocationService locationService, CustomerService customerService, WeighbridgeService weighbridgeService,
            CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
            EntityManager entityManager) {
        this.materialService = materialService;
        this.vendorService = vendorService;
        this.locationService = locationService;
        this.customerService = customerService;
        this.weighbridgeService = weighbridgeService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.entityManager = entityManager;
    }

    // Existing save services participate in this transaction; any failed row rolls back the entire upload.
    @Transactional
    public Map<String, Object> importWorkbook(MultipartFile file, Integer userId) {
        if (userId == null || userId <= 0) {
            throw invalid("userId must be a positive integer");
        }
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw invalid("Upload a non-empty .xlsx file");
        }
        Map<String, Integer> counts = new LinkedHashMap<>();
        DataFormatter formatter = new DataFormatter(Locale.ROOT);
        try (InputStream input = file.getInputStream(); Workbook workbook = WorkbookFactory.create(input)) {
            for (Sheet sheet : workbook) {
                String name = sheet.getSheetName();
                Class<? extends BaseRequest> type = requestType(name);
                Map<Integer, String> headers = headers(sheet, type, formatter);
                int count = 0;
                for (Row row : sheet) {
                    if (row.getRowNum() == 0 || isBlank(row, formatter)) {
                        continue;
                    }
                    String context = name + " row " + (row.getRowNum() + 1) + ": ";
                    try {
                        Map<String, String> values = new LinkedHashMap<>();
                        for (Cell cell : row) {
                            String value = text(cell, formatter);
                            if (!value.isEmpty()) {
                                String header = headers.get(cell.getColumnIndex());
                                if (header == null) {
                                    throw invalid("Data in a column without a header");
                                }
                                PropertyDescriptor property = BeanUtils.getPropertyDescriptor(type, header);
                                if (cell.getCellType() == CellType.NUMERIC && property != null
                                        && (property.getPropertyType() == Integer.class || property.getPropertyType() == BigDecimal.class)) {
                                    // Numeric fields use stored values, without display rounding or thousands separators.
                                    value = NumberToTextConverter.toText(cell.getNumericCellValue());
                                }
                                values.put(header, value);
                            }
                        }
                        BaseRequest request = request(values, type);
                        request.setUserId(userId);
                        ResponseEntity<Object> result = save(name, request, values, userId);
                        if (result == null || !result.getStatusCode().is2xxSuccessful()) {
                            String message = "Unable to save master data";
                            if (result != null && result.getBody() != null) {
                                JsonNode body = mapper.readTree(result.getBody().toString());
                                message = body.path("message").asText(message).trim();
                            }
                            throw invalid(message);
                        }
                        // Surface database constraints against the correct row before reporting success.
                        entityManager.flush();
                        count++;
                    } catch (ResponseStatusException ex) {
                        throw invalid(context + ex.getReason());
                    } catch (Exception ex) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                context + "Invalid data or database constraint failure", ex);
                    }
                }
                counts.put(name, count);
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read Excel workbook", ex);
        }
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            throw invalid("Workbook contains no data rows; fill the template before uploading");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "success");
        result.put("message", "Seller master data imported successfully");
        result.put("totalImported", total);
        result.put("imported", counts);
        return result;
    }

    private Class<? extends BaseRequest> requestType(String name) {
        switch (name) {
            case "Material": return MaterialMasterRequest.class;
            case "Vendor": return VendorRequest.class;
            case "Location": return LocationRequest.class;
            case "Customer": return CustomerRequest.class;
            case "Weighbridge": return WeighbridgeRequest.class;
            default: throw invalid("Unsupported sheet: " + name);
        }
    }

    private Map<Integer, String> headers(Sheet sheet, Class<?> type, DataFormatter formatter) {
        Map<Integer, String> headers = new LinkedHashMap<>();
        Row row = sheet.getRow(0);
        if (row == null) {
            throw invalid(sheet.getSheetName() + ": Header row is required");
        }
        for (Cell cell : row) {
            String header = text(cell, formatter);
            if (header.isEmpty()) {
                continue;
            }
            if (headers.containsValue(header)) {
                throw invalid(sheet.getSheetName() + ": Duplicate header " + header);
            }
            boolean templateExtra = (type == MaterialMasterRequest.class
                    && Arrays.asList("category", "subCategory").contains(header))
                    || (type == VendorRequest.class && Arrays.asList("includeRatesinDc", "purchaseReport").contains(header))
                    || (type == WeighbridgeRequest.class && "capacityInTons".equals(header));
            PropertyDescriptor property = BeanUtils.getPropertyDescriptor(type, header);
            if (!templateExtra && (property == null || property.getWriteMethod() == null
                    || (header.endsWith("Id") && property.getPropertyType() == Integer.class)
                    || Arrays.asList("ipAddress", "requestId").contains(header))) {
                throw invalid(sheet.getSheetName() + ": Unsupported header " + header);
            }
            headers.put(cell.getColumnIndex(), header);
        }
        String required = type == MaterialMasterRequest.class ? "itemName"
                : sheet.getSheetName().toLowerCase(Locale.ROOT) + "Name";
        if (!headers.containsValue(required)
                || (type == MaterialMasterRequest.class && !headers.containsValue("itemCode"))) {
            throw invalid(sheet.getSheetName() + ": Missing required name/code header");
        }
        return headers;
    }

    private BaseRequest request(Map<String, String> values, Class<? extends BaseRequest> type) throws Exception {
        ObjectNode data = mapper.createObjectNode();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (type == MaterialMasterRequest.class && ("category".equals(key) || "subCategory".equals(key))) {
                continue;
            }
            PropertyDescriptor property = BeanUtils.getPropertyDescriptor(type, key);
            if (property == null) {
                throw invalid(key + " has no backend storage field; leave it blank");
            }
            Class<?> fieldType = property.getPropertyType();
            try {
                if (fieldType == Boolean.class) {
                    if (!Arrays.asList("true", "false", "yes", "no", "1", "0").contains(value.toLowerCase(Locale.ROOT))) {
                        throw invalid(key + " must be true/false, yes/no or 1/0");
                    }
                    data.put(key, Arrays.asList("true", "yes", "1").contains(value.toLowerCase(Locale.ROOT)));
                } else if (fieldType == Integer.class) {
                    data.put(key, new BigDecimal(value).intValueExact());
                } else if (fieldType == BigDecimal.class) {
                    BigDecimal number = new BigDecimal(value);
                    if (number.signum() < 0) {
                        throw invalid(key + " must be non-negative");
                    }
                    data.put(key, number);
                } else if (List.class.isAssignableFrom(fieldType)) {
                    JsonNode json = mapper.readTree(value);
                    if (json == null || !json.isArray()) {
                        throw invalid(key + " must be a JSON array");
                    }
                    data.set(key, json);
                } else if (Arrays.asList("applicableProcesses", "technicalSpecs", "unitWeights", "additionalParams").contains(key)) {
                    JsonNode json = mapper.readTree(value);
                    boolean object = "technicalSpecs".equals(key) || "additionalParams".equals(key);
                    if (json == null || (object ? !json.isObject() : !json.isArray())) {
                        throw invalid(key + " must be a JSON " + (object ? "object" : "array"));
                    }
                    data.put(key, mapper.writeValueAsString(json));
                } else {
                    data.put(key, value);
                }
            } catch (ResponseStatusException ex) {
                throw ex;
            } catch (Exception ex) {
                throw invalid("Invalid value for " + key);
            }
        }
        String name = type == MaterialMasterRequest.class ? "itemName"
                : type.getSimpleName().replace("Request", "").toLowerCase(Locale.ROOT) + "Name";
        if (!values.containsKey(name) || (type == MaterialMasterRequest.class && !values.containsKey("itemCode"))) {
            throw invalid("Name is required; Material also requires itemCode");
        }
        return mapper.treeToValue(data, type);
    }

    private ResponseEntity<Object> save(String name, BaseRequest request, Map<String, String> values, Integer userId) {
        switch (name) {
            case "Material":
                MaterialMasterRequest material = (MaterialMasterRequest) request;
                resolveCategories(material, values, userId);
                normalizeMaterialParams(material);
                return materialService.save(material, null, null);
            case "Vendor": return vendorService.save((VendorRequest) request);
            case "Location":
                LocationRequest location = (LocationRequest) request;
                // Location stores contacts in trading_contact_master, rather than on the location entity.
                if (values.keySet().stream().anyMatch(Arrays.asList("contactName", "phoneNo", "contactNo", "emailId")::contains)) {
                    if (location.getContactName() == null || location.getPhoneNo() == null) {
                        throw invalid("Location contactName and phoneNo are required when contact details are supplied");
                    }
                    ContactMasterRequest contact = new ContactMasterRequest();
                    contact.setContactName(location.getContactName());
                    contact.setPhoneNo(location.getPhoneNo());
                    contact.setAlternatePhoneNo(location.getContactNo());
                    contact.setEmailId(location.getEmailId());
                    List<ContactMasterRequest> contacts = new ArrayList<>();
                    contacts.add(contact);
                    if (location.getAdditionalContacts() != null) {
                        contacts.addAll(location.getAdditionalContacts());
                    }
                    location.setAdditionalContacts(contacts);
                }
                return locationService.save(location);
            case "Customer": return customerService.save((CustomerRequest) request);
            default: return weighbridgeService.save((WeighbridgeRequest) request);
        }
    }

    private void resolveCategories(MaterialMasterRequest request, Map<String, String> values, Integer userId) {
        String categoryName = values.get("category");
        String subcategoryName = values.get("subCategory");
        if (categoryName == null) {
            if (subcategoryName != null) {
                throw invalid("category is required when subCategory is supplied");
            }
            return;
        }
        List<CategoryEntity> categories = categoryRepository.findByCategoryName(categoryName);
        if (categories.size() > 1) {
            throw invalid("Ambiguous category name: " + categoryName);
        }
        CategoryEntity category;
        if (categories.isEmpty()) {
            category = new CategoryEntity();
            category.setCategoryName(categoryName);
            category.setIsDeleted(false);
            category.setCreatedBy(userId);
            category = categoryRepository.save(category);
        } else {
            category = categories.get(0);
        }
        request.setCategoryId(category.getCategoryId());
        if (subcategoryName != null) {
            List<SubCategoryEntity> subcategories = subCategoryRepository.findBySubCategoryNameforInsert(
                    subcategoryName, category.getCategoryId());
            if (subcategories.size() > 1) {
                throw invalid("Ambiguous subCategory name: " + subcategoryName);
            }
            SubCategoryEntity subcategory;
            if (subcategories.isEmpty()) {
                subcategory = new SubCategoryEntity();
                subcategory.setSubcategoryName(subcategoryName);
                subcategory.setCategoryId(category.getCategoryId());
                subcategory.setIsDeleted(false);
                subcategory.setCreatedBy(userId);
                subcategory = subCategoryRepository.save(subcategory);
            } else {
                subcategory = subcategories.get(0);
            }
            request.setSubCategoryId(subcategory.getSubcategoryId());
        }
    }

    // Match /material/save: additionalParams contains the technicalSpecs and unitWeights used by the UI.
    private void normalizeMaterialParams(MaterialMasterRequest request) {
        try {
            ObjectNode params = request.getAdditionalParams() == null ? mapper.createObjectNode()
                    : (ObjectNode) mapper.readTree(request.getAdditionalParams());
            for (String key : Arrays.asList("technicalSpecs", "unitWeights")) {
                String standalone = "technicalSpecs".equals(key) ? request.getTechnicalSpecs() : request.getUnitWeights();
                JsonNode nested = params.get(key);
                JsonNode supplied = standalone == null ? null : mapper.readTree(standalone);
                if (nested != null && ("technicalSpecs".equals(key) ? !nested.isObject() : !nested.isArray())) {
                    throw invalid("additionalParams." + key + " has an invalid JSON type");
                }
                if (supplied != null) {
                    if (nested != null && !nested.equals(supplied)) {
                        throw invalid(key + " conflicts with additionalParams." + key);
                    }
                    params.set(key, supplied);
                }
            }
            JsonNode technicalSpecs = params.get("technicalSpecs");
            if (technicalSpecs != null && technicalSpecs.has("customParameters")
                    && !technicalSpecs.get("customParameters").isArray()) {
                throw invalid("technicalSpecs.customParameters must be a JSON array");
            }
            request.setAdditionalParams(mapper.writeValueAsString(params));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw invalid("Invalid additionalParams");
        }
    }

    private boolean isBlank(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            if (!text(cell, formatter).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String text(Cell cell, DataFormatter formatter) {
        if (cell.getCellType() == CellType.FORMULA || cell.getCellType() == CellType.ERROR) {
            throw invalid(cell.getSheet().getSheetName() + " row " + (cell.getRowIndex() + 1)
                    + ": Formula/error cells are unsupported; paste values instead");
        }
        return formatter.formatCellValue(cell).trim();
    }

    private ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
