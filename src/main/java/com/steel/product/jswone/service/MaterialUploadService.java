package com.steel.product.jswone.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.entity.BrandMasterJswEntity;
import com.steel.product.jswone.entity.LeafCategoryJswEntity;
import com.steel.product.jswone.entity.CategoryMasterJswEntity;
import com.steel.product.jswone.entity.CoatingtypeMasterJswEntity;
import com.steel.product.jswone.entity.FormMasterJswEntity;
import com.steel.product.jswone.entity.GradeMasterJswEntity;
import com.steel.product.jswone.entity.MaterialMasterFiledataEntity;
import com.steel.product.jswone.entity.MaterialMasterJswEntity;
import com.steel.product.jswone.entity.ProductMasterJswEntity;
import com.steel.product.jswone.entity.SubCategoryJswEntity;
import com.steel.product.jswone.entity.SubgradeMasterJswEntity;
import com.steel.product.jswone.entity.SurfacetypeMasterJswEntity;
import com.steel.product.jswone.entity.UomMasterJswEntity;
import com.steel.product.jswone.repository.BrandMasterJswRepository;
import com.steel.product.jswone.repository.LeafCategoryJswRepository;
import com.steel.product.jswone.repository.CategoryMasterRepository;
import com.steel.product.jswone.repository.CoatingtypeMasterJswRepository;
import com.steel.product.jswone.repository.FormMasterJswRepository;
import com.steel.product.jswone.repository.GradeMasterJswRepository;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.ProductMasterJswRepository;
import com.steel.product.jswone.repository.SubCategoryJswRepository;
import com.steel.product.jswone.repository.SubGradeJswRepository;
import com.steel.product.jswone.repository.SurfacetypeMasterJswRepository;
import com.steel.product.jswone.repository.UomMasterJswRepository;
import com.steel.product.jswone.request.MaterialUploadRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MaterialUploadService {

	@Autowired
	MaterialMasterFiledataRepository repository;

	@Autowired
	MaterialMasterJswRepository materialMasterRepository;

	@Autowired
	CategoryMasterRepository categoryRepository;

	@Autowired
	CoatingtypeMasterJswRepository coatingtypeRepository;

	@Autowired
	SurfacetypeMasterJswRepository surfacetypeRepository;

	@Autowired
	UomMasterJswRepository uomRepository;

	@Autowired
	FormMasterJswRepository formRepository;

	@Autowired
	GradeMasterJswRepository gradeRepository;

	@Autowired
	SubGradeJswRepository subGradeJswRepository;

	@Autowired
	ProductMasterJswRepository productRepository;

	@Autowired
	SubCategoryJswRepository subCategoryRepository;

	@Autowired
	LeafCategoryJswRepository leafCategoryJswRepository;

	@Autowired
	BrandMasterJswRepository brandRepository;
	
	public ResponseEntity<Object> upload(MaterialUploadRequest request) throws Exception, FileNotFoundException {
		log.info("******MaterialUploadService.upload*****");

		String newFileName = request.getFile().getOriginalFilename(); // .replace(".", "_" + new Date()+ ".");

		String fileUploadPath = "E:\\WS_SampleRubrum\\jswone\\uploaded_files";
		File directory = new File(fileUploadPath);
		// get all the files from a directory
		File[] fList2 = directory.listFiles();

		boolean fileExists = Arrays.stream(fList2).anyMatch(file -> file.getName().equals(newFileName));

		if (fileExists) {
			log.info("file name already exists - " + newFileName);
		}

		Path path1 = Paths.get(fileUploadPath + "/" + newFileName);

		try {
			Files.write(path1, request.getFile().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<MaterialMasterFiledataEntity> products = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(new File(fileUploadPath + "/" + newFileName));
				Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			if (rowIterator.hasNext())
				rowIterator.next(); // Skip header
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
					break;
				}
				 
				MaterialMasterFiledataEntity product = new MaterialMasterFiledataEntity();
				try {
					product.setMmId(row.getCell(0).getStringCellValue());
					product.setMmDescription(row.getCell(1).getStringCellValue());
					product.setCategory(getStringValue(row.getCell(2)));
					product.setSubcategory(getStringValue(row.getCell(3)));
					product.setLeafcategory(getStringValue(row.getCell(4)));
					product.setForm(getStringValue(row.getCell(5)));
					product.setProducttype(getStringValue(row.getCell(6)));
					product.setGrade(getStringValue(row.getCell(7)));
					product.setSubgrade(getStringValue(row.getCell(8)));
					product.setBrand(getStringValue(row.getCell(9)));
					product.setDiameter(getStringValue(row.getCell(10)));
					product.setThickness(getBigDecimalValue(row.getCell(11)));
					product.setWidth(getBigDecimalValue(row.getCell(12)));
					product.setLength(getBigDecimalValue(row.getCell(13)));
					product.setCoatingtype(getStringValue(row.getCell(14)));
					product.setSpangletype(getStringValue(row.getCell(15)));
					product.setColour(getStringValue(row.getCell(16)));
					product.setUom(getStringValue(row.getCell(17)));
					product.setHsn(Double.parseDouble(getNumberValue(row.getCell(18))));
					product.setTax(Double.parseDouble(getNumberValue(row.getCell(19))));
					product.setVariantKey(Double.parseDouble(getNumberValue(row.getCell(20))));
					product.setFilename(newFileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				products.add(product);
			}
			repository.saveAll(products);
			List<MaterialMasterFiledataEntity> listFileData =repository.findAll();
			List<MaterialMasterJswEntity> materialMasterList = new ArrayList<>();

			for (MaterialMasterFiledataEntity sourceEntity : listFileData) {
				MaterialMasterJswEntity destEntity = new MaterialMasterJswEntity();
				BeanUtils.copyProperties(sourceEntity, destEntity);

				if(!(sourceEntity.getBrand()!=null && sourceEntity.getBrand().length()>0)) {
					sourceEntity.setBrand("UnBrand");
				} 
				// Brand Master 
				destEntity.setCategoryId(setCategoryMaster(sourceEntity.getCategory()));
				destEntity.setSubcategoryId(setSubCategoryMaster(sourceEntity.getSubcategory(), destEntity.getCategoryId()));
				destEntity.setLeafcategoryId(setLeafCategoryMaster(sourceEntity.getLeafcategory(), destEntity.getSubcategoryId()));
				destEntity.setBrandId( setBrandNameMaster(sourceEntity.getBrand(), destEntity.getLeafcategoryId()));
				// Product Master 
				destEntity.setCoatingtypeId(setCoatingtypeMaster( sourceEntity.getCoatingtype() ));
				destEntity.setSurfacetypeId(setSurfacetypeMaster(sourceEntity.getSurfacetype()));
				destEntity.setUomId(setUomMaster(sourceEntity.getUom()));
				destEntity.setFormId(setFormMaster(sourceEntity.getForm()));
				destEntity.setGradeId(setGradeMaster(sourceEntity.getGrade()));
				destEntity.setSubgradeId(setSubGradeMaster(sourceEntity.getSubgrade(), destEntity.getGradeId()));
				destEntity.setProducttypeId(setProductMaster(sourceEntity.getSubgrade(), destEntity));
				materialMasterList.add(destEntity);
			}
			materialMasterRepository.saveAll(materialMasterList);
			log.info("File Uploaded Successfully. Count is == " + products.size());

			return new ResponseEntity<Object>("{\"status\": \"success\", \"message\": \"File Uploaded Successfully.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Failed to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static BigDecimal getBigDecimalValue(Cell cell) {
		if (cell == null || cell.getCellType() != CellType.NUMERIC) {
			return BigDecimal.ZERO; // or handle null cases appropriately
		}
		return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
	}

	public static String getStringValue(Cell cell) {
		try {
			if (cell == null || cell.getCellType() != CellType.STRING) {
				return "";
			}
			return cell.getStringCellValue();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private Integer setCategoryMaster(String categoryName) {
		Integer pk = 0;
		try {
			if (categoryName != null && categoryName.length() > 0) {
				
				List<CategoryMasterJswEntity> oldList = categoryRepository.findByCategoryName(categoryName);
				if(oldList!=null && oldList.size() > 0) {
					pk = oldList.get(0).getCategoryId();
				} else {
					CategoryMasterJswEntity entity = new CategoryMasterJswEntity();
					entity.setCategoryName(categoryName);
					entity.setCategoryDesc("");
					categoryRepository.save(entity);
					pk = entity.getCategoryId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setSubCategoryMaster(String subCategoryName, Integer categoryId) {
		Integer pk = 0;
		try {
			if (subCategoryName != null && subCategoryName.length() > 0) {
				List<SubCategoryJswEntity> oldList = subCategoryRepository.findBySubcategoryName(subCategoryName);
				if(oldList!=null && oldList.size() > 0) {
					pk = oldList.get(0).getSubcategoryId();
				} else {
					SubCategoryJswEntity entity = new SubCategoryJswEntity();
					entity.setSubcategoryName(subCategoryName);
					entity.setCategoryId(categoryId);
					subCategoryRepository.save(entity);
					pk = entity.getSubcategoryId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setLeafCategoryMaster(String leafcategoryName, Integer subCategoryId) {
		Integer pk = 0;
		try {
			if (leafcategoryName != null && leafcategoryName.length() > 0) {
				List<LeafCategoryJswEntity> oldList = leafCategoryJswRepository.findByLeafcategoryName(leafcategoryName);
				if(oldList!=null && oldList.size() > 0) {
					pk = oldList.get(0).getSubcategoryId();
				} else {
					LeafCategoryJswEntity entity = new LeafCategoryJswEntity();
					entity.setLeafcategoryName(leafcategoryName);
					entity.setSubcategoryId( subCategoryId);
					leafCategoryJswRepository.save(entity);
					pk = entity.getSubcategoryId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}
	
	private Integer setBrandNameMaster(String brandName, Integer leafcategoryId) {
		Integer pk = 0;
		try {
			if (brandName != null && brandName.length() > 0) {
				List<BrandMasterJswEntity> oldList = brandRepository.findByBrandName(brandName);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getBrandId();
				} else {
					BrandMasterJswEntity entity = new BrandMasterJswEntity();
					entity.setBrandName( brandName);
					entity.setLeafcategoryId( leafcategoryId);
					brandRepository.save(entity);
					pk = entity.getBrandId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setUomMaster(String uom) {
		Integer pk = 0;
		try {
			if (uom != null && uom.length() > 0) {

				List<UomMasterJswEntity> oldList = uomRepository.findByUomName(uom);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getUomId();
				} else {
					UomMasterJswEntity entity = new UomMasterJswEntity();
					entity.setUomName(uom);
					uomRepository.save(entity);
					pk = entity.getUomId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setFormMaster(String form) {
		Integer pk = 0;
		try {
			if (form != null && form.length() > 0) {

				List<FormMasterJswEntity> oldList = formRepository.findByFormName(form);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getFormId();
				} else {
					FormMasterJswEntity entity = new FormMasterJswEntity();
					entity.setFormName(form);
					formRepository.save(entity);
					pk = entity.getFormId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setGradeMaster(String grade) {
		Integer pk = 0;
		try {
			if (grade != null && grade.length() > 0) {

				List<GradeMasterJswEntity> oldList = gradeRepository.findByGradeName(grade);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getGradeId();
				} else {
					GradeMasterJswEntity entity = new GradeMasterJswEntity();
					entity.setGradeName(grade);
					gradeRepository.save(entity);
					pk = entity.getGradeId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setSubGradeMaster(String subgrade, Integer gradeId) {
		Integer pk = 0;
		try {
			if (subgrade != null && subgrade.length() > 0) {

				List<SubgradeMasterJswEntity> oldList = subGradeJswRepository.findBySubgradeName(subgrade);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getSubgradeId();
				} else {
					SubgradeMasterJswEntity entity = new SubgradeMasterJswEntity();
					entity.setSubgradeName(subgrade);
					entity.setGradeId( gradeId);
					subGradeJswRepository.save(entity);
					pk = entity.getSubgradeId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}
	
	private Integer setProductMaster(String productName, MaterialMasterJswEntity mmEntity) {
		Integer pk = 0;
		try {
			if (productName != null && productName.length() > 0) {

				List<ProductMasterJswEntity> oldList = productRepository.findByProductName(productName);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getProductId();
				} else {
					ProductMasterJswEntity entity = new ProductMasterJswEntity();
					entity.setProductName(productName);
					entity.setGradeId(mmEntity.getGradeId());
					entity.setSubgradeId(mmEntity.getSubgradeId());
					entity.setFormId(mmEntity.getFormId());
					entity.setUomId(mmEntity.getUomId());
					entity.setSurfaceId(mmEntity.getSurfacetypeId());
					entity.setCoatingtypeId(mmEntity.getCoatingtypeId());
					productRepository.save(entity);
					pk = entity.getProductId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setSurfacetypeMaster(String surfacetype ) {
		Integer pk = 0;
		try {
			if (surfacetype != null && surfacetype.length() > 0) {

				List<SurfacetypeMasterJswEntity> oldList = surfacetypeRepository.findBySurfacetypeName(surfacetype);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getSurfacetypeId();
				} else {
					SurfacetypeMasterJswEntity entity = new SurfacetypeMasterJswEntity();
					entity.setSurfacetypeName( surfacetype);
					surfacetypeRepository.save(entity);
					pk = entity.getSurfacetypeId() ;
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}
	private Integer setCoatingtypeMaster(String coatingtype) {
		Integer pk = 0;
		try {
			if (coatingtype != null && coatingtype.length() > 0) {

				List<CoatingtypeMasterJswEntity> oldList = coatingtypeRepository.findByCoatingtype(coatingtype);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getCoatingtypeId();
				} else {
					CoatingtypeMasterJswEntity entity = new CoatingtypeMasterJswEntity();
					entity.setCoatingtype(coatingtype);
					coatingtypeRepository.save(entity);
					pk = entity.getCoatingtypeId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private String getNumberValue(Cell cell) {
		if (cell == null)
			return "";

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		default:
			return "";
		}
	}


}
