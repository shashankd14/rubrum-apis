package com.steel.product.jswone.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.lowagie.text.DocumentException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.steel.product.application.dto.pdf.PdfDto;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.service.InwardEntryService;
import com.steel.product.application.service.PartyDetailsService;
import com.steel.product.application.service.PdfService;
import com.steel.product.application.service.StatusService;
import com.steel.product.jswone.entity.BrandMasterJswEntity;
import com.steel.product.jswone.entity.LeafCategoryJswEntity;
import com.steel.product.jswone.entity.CategoryMasterJswEntity;
import com.steel.product.jswone.entity.CoatingtypeMasterJswEntity;
import com.steel.product.jswone.entity.FormMasterJswEntity;
import com.steel.product.jswone.entity.GradeMasterJswEntity;
import com.steel.product.jswone.entity.InwardFileDataEntity;
import com.steel.product.jswone.entity.MaterialMasterFileDataEntity;
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
import com.steel.product.jswone.repository.InwardFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterFiledataRepository;
import com.steel.product.jswone.repository.MaterialMasterJswRepository;
import com.steel.product.jswone.repository.MaterialMasterJswSpecification;
import com.steel.product.jswone.repository.ProductMasterJswRepository;
import com.steel.product.jswone.repository.SubCategoryJswRepository;
import com.steel.product.jswone.repository.SubGradeJswRepository;
import com.steel.product.jswone.repository.SurfacetypeMasterJswRepository;
import com.steel.product.jswone.repository.UomMasterJswRepository;
import com.steel.product.jswone.request.InwardFileDataDTO;
import com.steel.product.jswone.request.MaterialMasterFileDataDTO;
import com.steel.product.jswone.request.MaterialSearchPageRequest;
import com.steel.product.jswone.request.MaterialUploadRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MaterialUploadServiceImpl implements MaterialUploadService {

	@Autowired
	MaterialMasterFiledataRepository repository;

	@Autowired
	PartyDetailsService partyDetailsService;

	@Autowired
	StatusService statusService;

	@Autowired
	InwardFiledataRepository inwardFiledataRepository;

	@Autowired
	MaterialMasterJswRepository materialMasterRepository;

	@Autowired
	InwardEntryService inwdEntrySvc;

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
	PdfService pdfService;

	@Autowired
	LeafCategoryJswRepository leafCategoryJswRepository;

	@Autowired
	BrandMasterJswRepository brandRepository;

	@Value("${fileUploadPath}")
	private String fileUploadPath;
	
	@Value("${inwardFileUploadPath}")
	private String inwardFileUploadPath;
	 
	@Override
	public ResponseEntity<Object> uploadcsv(MaterialUploadRequest request) throws Exception, FileNotFoundException {
		log.info("******MaterialUploadService.uploadcsv*****");
		 
		try {
			String newFileName = new File(fileUploadPath).getName(); // .replace(".", "_" + new Date()+ ".");

			List<MaterialMasterFileDataDTO> products =  mmFileDetails();
			List<MaterialMasterFileDataEntity> productList =  new ArrayList<>();

			System.out.println("Hi size "+products.size());
			for (MaterialMasterFileDataDTO dto : products) {
				MaterialMasterFileDataEntity dest=new MaterialMasterFileDataEntity();
				BeanUtils.copyProperties(dto, dest);
				try {
					dest.setFilename(newFileName);
					productList.add(dest);
					repository.save (dest);
				} catch (Exception e) {
					System.out.println("error while save --  "+e.getMessage());
				}
			}
			//repository.saveAll(productList);
			List<MaterialMasterFileDataEntity> listFileData =repository.findAll();
			List<MaterialMasterJswEntity> materialMasterList = new ArrayList<>();

			for (MaterialMasterFileDataEntity sourceEntity : listFileData) {
				MaterialMasterJswEntity destEntity = new MaterialMasterJswEntity();
				BeanUtils.copyProperties(sourceEntity, destEntity);

				if(sourceEntity.getLength()!=null && sourceEntity.getLength().length() >0 ) {
					destEntity.setLength(new BigDecimal(sourceEntity.getLength()));
				} else {
					destEntity.setLength(BigDecimal.ZERO);
				}
				if(sourceEntity.getWidth() !=null && sourceEntity.getWidth().length() >0 ) {
					destEntity.setWidth(new BigDecimal(sourceEntity.getWidth()));
				} else {
					destEntity.setWidth(BigDecimal.ZERO);
				}
				if(sourceEntity.getThickness() !=null && sourceEntity.getThickness().length() >0 ) {
					destEntity.setThickness(new BigDecimal(sourceEntity.getThickness()));
				} else {
					destEntity.setThickness(BigDecimal.ZERO);
				}
				if (sourceEntity.getODiameter() != null && sourceEntity.getODiameter().length() > 0) {
					destEntity.setODiameter(new BigDecimal(sourceEntity.getODiameter()));
				} else {
					destEntity.setODiameter(BigDecimal.ZERO);
				}
				if (sourceEntity.getNb() != null && sourceEntity.getNb().length() > 0) {
					destEntity.setNb(new BigDecimal(sourceEntity.getNb()));
				} else {
					destEntity.setNb(BigDecimal.ZERO);
				}
				if (sourceEntity.getIDiameter() != null && sourceEntity.getIDiameter().length() > 0) {
					destEntity.setIDiameter(new BigDecimal(sourceEntity.getIDiameter()));
				} else {
					destEntity.setIDiameter(BigDecimal.ZERO);
				}
				
				if(!(sourceEntity.getBrand()!=null && sourceEntity.getBrand().length()>0)) {
					sourceEntity.setBrand("UnBrand");
				} 
				// Brand Master 
				destEntity.setCategoryId(setCategoryMaster(sourceEntity.getCategory()));
				destEntity.setSubcategoryId(setSubCategoryMaster(sourceEntity.getSubcategory(), destEntity.getCategoryId()));
				destEntity.setLeafcategoryId(setLeafCategoryMaster(sourceEntity.getLeafcategory(), destEntity.getSubcategoryId()));
				destEntity.setBrandId( setBrandNameMaster(sourceEntity.getBrand(), destEntity.getLeafcategoryId()));
				// Product Master 
				destEntity.setProducttypeId(setProductMaster(sourceEntity.getSubgrade(), destEntity));
				destEntity.setGradeId(setGradeMaster(sourceEntity.getGrade(), destEntity.getProducttypeId()));
				destEntity.setSubgradeId(setSubGradeMaster(sourceEntity.getSubgrade(), destEntity.getGradeId()));
				destEntity.setCoatingtypeId(setCoatingtypeMaster( sourceEntity.getCoatingtype(), destEntity.getProducttypeId() ));
				destEntity.setSurfacetypeId(setSurfacetypeMaster(sourceEntity.getSurfacetype(), destEntity.getProducttypeId()));
				destEntity.setUomId(setUomMaster(sourceEntity.getUom(), destEntity.getProducttypeId()));
				destEntity.setFormId(setFormMaster(sourceEntity.getForm(), destEntity.getProducttypeId()));
				materialMasterList.add(destEntity);
				try {
					materialMasterRepository.save(destEntity);
				} catch (Exception e) {
					System.out.println("error while save --  "+e.getMessage());
				}
			}
			//materialMasterRepository.saveAll(materialMasterList);
			log.info("File Uploaded Successfully. Count is == " + products.size());

			return new ResponseEntity<Object>("{\"status\": \"success\", \"message\": \"File Uploaded Successfully.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch( Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Failed to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private List<MaterialMasterFileDataDTO> mmFileDetails( ) {
		// 1.read the file via csv reader
		CSVReader reader = null;
		try {
			reader = new CSVReaderBuilder(new FileReader(fileUploadPath)).build();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 2.convert into java object
		CsvToBean<MaterialMasterFileDataDTO> csvToBean = new CsvToBeanBuilder<MaterialMasterFileDataDTO>(reader).withSkipLines(1)
				.withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(true).withType(MaterialMasterFileDataDTO.class)
				.build();
		return csvToBean.parse();
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

	private Integer setUomMaster(String uom, Integer producttypeId) {
		Integer pk = 0;
		try {
			if (uom != null && uom.length() > 0) {

				List<UomMasterJswEntity> oldList = uomRepository.findByUomName(uom);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getUomId();
				} else {
					UomMasterJswEntity entity = new UomMasterJswEntity();
					entity.setUomName(uom);
					entity.setProductId( producttypeId);
					uomRepository.save(entity);
					pk = entity.getUomId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setFormMaster(String form, Integer producttypeId) {
		Integer pk = 0;
		try {
			if (form != null && form.length() > 0) {

				List<FormMasterJswEntity> oldList = formRepository.findByFormName(form);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getFormId();
				} else {
					FormMasterJswEntity entity = new FormMasterJswEntity();
					entity.setFormName(form);
					entity.setProductId( producttypeId);
					formRepository.save(entity);
					pk = entity.getFormId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setGradeMaster(String grade, Integer producttypeId) {
		Integer pk = 0;
		try {
			if (grade != null && grade.length() > 0) {
				List<GradeMasterJswEntity> oldList = gradeRepository.findByGradeName(grade);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getGradeId();
				} else {
					GradeMasterJswEntity entity = new GradeMasterJswEntity();
					entity.setGradeName(grade);
					entity.setProductId( producttypeId);
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
					entity.setBrandId(mmEntity.getBrandId());
					productRepository.save(entity);
					pk = entity.getProductId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}

	private Integer setSurfacetypeMaster(String surfacetype, Integer producttypeId ) {
		Integer pk = 0;
		try {
			if (surfacetype != null && surfacetype.length() > 0) {

				List<SurfacetypeMasterJswEntity> oldList = surfacetypeRepository.findBySurfacetypeName(surfacetype);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getSurfacetypeId();
				} else {
					SurfacetypeMasterJswEntity entity = new SurfacetypeMasterJswEntity();
					entity.setSurfacetypeName( surfacetype);
					entity.setProductId( producttypeId);
					surfacetypeRepository.save(entity);
					pk = entity.getSurfacetypeId() ;
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}
	private Integer setCoatingtypeMaster(String coatingtype, Integer producttypeId) {
		Integer pk = 0;
		try {
			if (coatingtype != null && coatingtype.length() > 0) {

				List<CoatingtypeMasterJswEntity> oldList = coatingtypeRepository.findByCoatingtype(coatingtype);
				if (oldList != null && oldList.size() > 0) {
					pk = oldList.get(0).getCoatingtypeId();
				} else {
					CoatingtypeMasterJswEntity entity = new CoatingtypeMasterJswEntity();
					entity.setCoatingtype(coatingtype);
					entity.setProductId( producttypeId);
					coatingtypeRepository.save(entity);
					pk = entity.getCoatingtypeId();
				}
			}
		} catch (Exception e) {
		}
		return pk;
	}
	
	@Override
	public Page<Object[]> materialSearchBymmid(MaterialSearchPageRequest request) {
		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());

		Page<Object[]> packetsList = materialMasterRepository.materialSearchBymmid(request.getMmid(), pageable);
		return packetsList;
	}

	@Override
	public Page<MaterialMasterJswEntity> materialSearch(MaterialSearchPageRequest request) {
		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());

		MaterialMasterJswSpecification spec = new MaterialMasterJswSpecification(request);

		Page<MaterialMasterJswEntity> pageResult = materialMasterRepository.findAll(spec, pageable);

		return pageResult;
	}
	

	@Override
	public Page<Object[]> materialSearch1(MaterialSearchPageRequest request) {
		Pageable pageable = PageRequest.of((request.getPageNo() - 1), request.getPageSize());
		
		if(!(request.getLength() !=null && request.getLength().compareTo(BigDecimal.ZERO) > 0)) {
			request.setLength(BigDecimal.ZERO);
		}	
		if(!(request.getWidth() !=null && request.getWidth().compareTo(BigDecimal.ZERO) > 0)) {
			request.setWidth(BigDecimal.ZERO);
		}	
		if(!(request.getThickness()!=null && request.getThickness().compareTo(BigDecimal.ZERO) > 0)) {
			request.setThickness(BigDecimal.ZERO);
		} 
		Page<Object[]> packetsList = materialMasterRepository.materialSearch(
				//request.getSearchText(), 
				request.getLength(), 
				request.getWidth(),
				request.getThickness(),
				//request.getNb(), 
				//request.getIDiameter(),
				//request.getODiameter(),
				request.getCategoryId(), 
				request.getSubcategoryId(), 
				request.getLeafcategoryId(), 
				request.getBrandId(), 
				request.getProducttypeId(), 
				request.getGradeId(), 
				request.getSubgradeId(), 
				request.getFormId(), 
				request.getUomId(), 
				request.getSurfacetypeId(), 
				request.getCoatingtypeId(), 
				pageable);
		return packetsList;
	}

	@Override
	public ResponseEntity<Object> uploadInwardData(MaterialUploadRequest request) throws Exception, FileNotFoundException {
		log.info("******MaterialUploadService.uploadInwardData*****");
		 
		try {
			String newFileName = new File(inwardFileUploadPath).getName();

			List<InwardFileDataDTO> products =  inwardFileDetails();
			List<InwardFileDataEntity> productList =  new ArrayList<>();

			System.out.println("Hi size "+products.size());
			for (InwardFileDataDTO dto : products) {
				InwardFileDataEntity dest=new InwardFileDataEntity();
				BeanUtils.copyProperties(dto, dest);
				try {
					dest.setFilename(newFileName);
					productList.add(dest);
					inwardFiledataRepository.save (dest);
				} catch (Exception e) {
					System.out.println("error while save --  "+e.getMessage());
				}
			}
			List<InwardFileDataEntity> listFileData = inwardFiledataRepository.findAll();
			for (InwardFileDataEntity sourceEntity : listFileData) {
				saveInwardEntry(sourceEntity);
			}
			log.info("File Uploaded Successfully. Count is == " + products.size());
			return new ResponseEntity<Object>("{\"status\": \"success\", \"message\": \"File Uploaded Successfully.\"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>("{\"status\": \"failed\", \"message\": \"Failed to Uploaded a file.\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Integer saveInwardEntry(InwardFileDataEntity inward) {
		InwardEntry inwardEntry = new InwardEntry();
		Integer inwardEnrtyId = 0;
		System.out.println("DTO details " + inward);
		MaterialMasterJswEntity mmObj = null;
		try {

			List<MaterialMasterJswEntity> mmList = materialMasterRepository.findByMmId(inward.getMmid());
			if (mmList != null && mmList.size() > 0) {
				mmObj = mmList.get(0);

				int userId = 1;
				inwardEntry.setInwardEntryId(0);
				inwardEntry.setPurposeType("STEEL SERVICE CENTRE");
				inwardEntry.setParty(this.partyDetailsService.getPartyById(137));
				inwardEntry.setCoilNumber(inward.getCoilno());
				inwardEntry.setBatchNumber(inward.getBatchnumber());

				if (inward.getReceiveddate() != null) {
					System.out.println("date is == " + inward.getReceiveddate());
					DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date date = sourceFormat.parse(inward.getReceiveddate());
					inwardEntry.setdReceivedDate(date);
				} else {
					inwardEntry.setdReceivedDate(new Date());
				}
				inwardEntry.setvLorryNo(inward.getVehicleno());
				inwardEntry.setvInvoiceNo(inward.getInvoicenumber());
				// inwardEntry.setdInvoiceDate(Timestamp.valueOf(inward.getInvoicenumber()ceDate()));

				inwardEntry.setCustomerCoilId("");
				inwardEntry.setCustomerInvoiceNo(inward.getInvoicenumber());
				inwardEntry.setCustomerBatchId(inward.getCustbatchno());

				inwardEntry.setMmId(inward.getMmid());
				// inwardEntry.setMaterial(this.matDescService.getMatById(inward.getMaterialId()));
				// inwardEntry.setMaterialGrade(matGradeService.getById(inward.getMaterialGradeId()));
				inwardEntry.setfWidth(mmObj.getWidth().floatValue());
				inwardEntry.setfThickness(mmObj.getThickness().floatValue());
				inwardEntry.setfLength(mmObj.getLength().floatValue());
				inwardEntry.setAvailableLength(mmObj.getLength().floatValue());
				inwardEntry.setfQuantity(Float.valueOf(inward.getPresentweight()));
				inwardEntry.setFpresent( Float.valueOf(inward.getPresentweight()));
				inwardEntry.setGrossWeight(Float.valueOf(inward.getGrossweight()));

				float fLength;
				try {
					float fConstant = 7.85f;
					fLength = (Float.valueOf(inward.getGrossweight())
							/ ((mmObj.getThickness().floatValue() * fConstant *  mmObj.getWidth().floatValue())/1000) * 1000);
				} catch (Exception e) {
					fLength=mmObj.getLength().floatValue();
				}
				inwardEntry.setfLength(fLength );
				// inwardEntry.setStatus(this.statusService.getStatusById(inward.getStatusId()));
				inwardEntry.setStatus(this.statusService.getStatusById(1));

				inwardEntry.setvProcess("");
				inwardEntry.setTdcNo(inward.getTdcno());
				inwardEntry.setValueOfGoods(Float.valueOf(inward.getValueofgoods()));
				inwardEntry.setBilledweight(0);
				inwardEntry.setParentCoilNumber(null);
				inwardEntry.setvParentBundleNumber(0);
				inwardEntry.setRemarks("Migration");
				inwardEntry.setIsDeleted(Boolean.valueOf(false));
				inwardEntry.setCreatedOn(new Date());
				inwardEntry.setUpdatedOn(new Date());
				inwardEntry.setCreatedBy(userId);
				inwardEntry.setUpdatedBy(userId);
				inwardEntry.setTestCertificateNumber(inward.getTestcertificateno());
				InwardEntry savedInwardEntry = inwdEntrySvc.saveEntry(inwardEntry);
				if (savedInwardEntry != null && savedInwardEntry.getInwardEntryId() > 0) {
					try {
						inwardEnrtyId = savedInwardEntry.getInwardEntryId();

						PdfDto pdfDto = new PdfDto();
						pdfDto.setInwardId(inwardEnrtyId);
						Path file = null;
						byte[] bytes = null;
						StringBuilder builder = new StringBuilder();

						file = Paths.get(pdfService.generatePdf(pdfDto).getAbsolutePath());
						bytes = Files.readAllBytes(file);
						builder.append(Base64.getEncoder().encodeToString(bytes));
					} catch (IOException | DocumentException | org.dom4j.DocumentException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inwardEnrtyId;
	}
	
	private List<InwardFileDataDTO> inwardFileDetails( ) {
		// 1.read the file via csv reader
		CSVReader reader = null;
		try {
			reader = new CSVReaderBuilder(new FileReader(inwardFileUploadPath)).build();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 2.convert into java object
		CsvToBean<InwardFileDataDTO> csvToBean = new CsvToBeanBuilder<InwardFileDataDTO>(reader).withSkipLines(1)
				.withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(true).withType(InwardFileDataDTO.class)
				.build();
		return csvToBean.parse();
	}
	
}
