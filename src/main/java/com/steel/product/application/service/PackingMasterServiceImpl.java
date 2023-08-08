package com.steel.product.application.service;

import com.steel.product.application.dao.PackingBucketRepository;
import com.steel.product.application.dao.PackingItemRepository;
import com.steel.product.application.dao.PackingRateRepository;
import com.steel.product.application.dto.packingmaster.PackingBucketRequest;
import com.steel.product.application.dto.packingmaster.PackingBucketResponse;
import com.steel.product.application.dto.packingmaster.PackingItemRequest;
import com.steel.product.application.dto.packingmaster.PackingItemResponse;
import com.steel.product.application.dto.packingmaster.PackingRateMasterRequest;
import com.steel.product.application.dto.packingmaster.PackingRateMasterResponse;
import com.steel.product.application.entity.PackingBucketChildEntity;
import com.steel.product.application.entity.PackingBucketEntity;
import com.steel.product.application.entity.PackingItemEntity;
import com.steel.product.application.entity.PackingRateEntity;

import lombok.extern.log4j.Log4j2;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PackingMasterServiceImpl implements PackingMasterService {

	@Autowired
	PackingItemRepository packingItemRepository;

	@Autowired
	PackingRateRepository packingRateRepository;

	@Autowired
	PackingBucketRepository packingBucketRepository;
	
	@Autowired
	AdditionalPriceMasterService additionalPriceMasterService;

	@Override
	public ResponseEntity<Object> save(PackingItemRequest packingItemRequest, int userId) {

		ResponseEntity<Object> response = null;

		List<PackingItemEntity> list = packingItemRepository.findByPackingItemId(packingItemRequest.getPackingItemId());
		PackingItemEntity checkpackingItemEntity=null;
		
		PackingItemEntity packingItemEntity = new PackingItemEntity();
		if (packingItemRequest.getId() != null && packingItemRequest.getId() > 0) {
			packingItemEntity.setItemId(packingItemRequest.getId());
			
			if(list!=null && list.size()>0) {
				checkpackingItemEntity=list.get(0);
				if(checkpackingItemEntity!=null && packingItemRequest.getId() != checkpackingItemEntity.getItemId() ) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Item already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
				}
			}
		} else {
			if(list!=null && list.size()>0) {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Item already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
		}
		packingItemEntity.setPackingItemId(packingItemRequest.getPackingItemId());
		packingItemEntity.setDescription(packingItemRequest.getDescription() );
		packingItemEntity.setUnit(packingItemRequest.getUnit());
		packingItemEntity.setCreatedBy(userId);
		packingItemEntity.setUpdatedBy(userId);
		packingItemEntity.setCreatedOn(new Date());
		packingItemEntity.setUpdatedOn(new Date());
		packingItemRepository.save (packingItemEntity);
		if (packingItemRequest.getId() != null && packingItemRequest.getId() > 0) {
			log.info("Packing Item details updated successfully");
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Item details updated successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} else {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Item details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> delete(int id) {
		ResponseEntity<Object> response = null;
		try {
			packingItemRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Item details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public PackingItemResponse getById(int id) {
		PackingItemResponse resp = new PackingItemResponse();
		Optional<PackingItemEntity> list = packingItemRepository.findById(id);
		if (list.isPresent()) {
			PackingItemEntity entity = list.get();
			resp = PackingItemEntity.valueOf(entity) ;
		}
		return resp;
	}

	@Override
	public List<PackingItemResponse> getAllItemDetails() {

		List<PackingItemResponse> instructionList = packingItemRepository.findAll().stream()
				.map(i -> PackingItemEntity.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public ResponseEntity<Object> saveBucket(PackingBucketRequest packingBucketRequest, int userId) {

		ResponseEntity<Object> response = null;
		PackingBucketEntity packingBucketEntity = new PackingBucketEntity();

		List<PackingBucketEntity> list = packingBucketRepository.findByPackingBucketId(packingBucketRequest.getPackingBucketId());
		PackingBucketEntity checkPackingBucketEntity=null;
		
		if (packingBucketRequest.getBucketId() != null && packingBucketRequest.getBucketId() > 0) {
			packingBucketEntity.setBucketId(packingBucketRequest.getBucketId());
			
			if(list!=null && list.size()>0) {
				checkPackingBucketEntity=list.get(0);
				if(checkPackingBucketEntity!=null && packingBucketRequest.getBucketId() != checkPackingBucketEntity.getBucketId() ) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Bucket already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
				}
			}
			packingBucketRepository.deleteByBucketId(packingBucketRequest.getBucketId());
		} else {
			if(list!=null && list.size()>0) {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Bucket already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
		}
		packingBucketEntity.setPackingBucketId(packingBucketRequest.getPackingBucketId() );
		packingBucketEntity.setPackingBucketDesc( packingBucketRequest.getPackingBucketDesc() );
		
		for (Integer itemId : packingBucketRequest.getPackingItemIdList()) {
			PackingBucketChildEntity childEntity = new PackingBucketChildEntity();
			childEntity.getItemEntity().setItemId(itemId);
			packingBucketEntity.getItemList().add(childEntity);
		}
		packingBucketEntity.setQty( packingBucketRequest.getQty() );
		packingBucketEntity.setCreatedBy(userId);
		packingBucketEntity.setUpdatedBy(userId);
		packingBucketEntity.setCreatedOn(new Date());
		packingBucketEntity.setUpdatedOn(new Date());
		packingBucketRepository.save (packingBucketEntity);
		if (packingBucketRequest.getBucketId() != null && packingBucketRequest.getBucketId() > 0) {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Bucket details updated successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} else {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Bucket details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		}
		return response;
	}

	@Override
	public PackingBucketResponse getByBucketId(int id) {
		PackingBucketResponse resp = new PackingBucketResponse();
		Optional<PackingBucketEntity> list = packingBucketRepository.findById(id);
		if (list.isPresent()) {
			PackingBucketEntity entity = list.get();
			resp = PackingBucketEntity.valueOf(entity) ;
		}
		return resp;
	}

	@Override
	public ResponseEntity<Object> deleteBucket(int id) {
		ResponseEntity<Object> response = null;
		try {
			packingBucketRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Bucket deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public List<PackingBucketResponse> getAllBucketList() {

		List<PackingBucketResponse> instructionList = packingBucketRepository.findAll().stream()
				.map(i -> PackingBucketEntity.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public ResponseEntity<Object> save(PackingRateMasterRequest packingRateMasterRequest, int userId) {

		ResponseEntity<Object> response = null;

		List<PackingRateEntity> list = packingRateRepository.findByPartyIdAndBucketId(packingRateMasterRequest.getPackingBucketId(), packingRateMasterRequest.getPartyId());
		PackingRateEntity checkpackingItemEntity=null;
		
		PackingRateEntity packingRateEntity = new PackingRateEntity();
		if (packingRateMasterRequest.getPackingRateId() != null && packingRateMasterRequest.getPackingRateId() > 0) {
			packingRateEntity.setPackingRateId(packingRateMasterRequest.getPackingRateId());
			
			if(list!=null && list.size()>0) {
				checkpackingItemEntity=list.get(0);
				if (checkpackingItemEntity != null
					&& packingRateMasterRequest.getPackingBucketId() != checkpackingItemEntity.getBucketEntity().getBucketId()   
					&& packingRateMasterRequest.getPartyId() != checkpackingItemEntity.getParty().getnPartyId()   
					) {
					return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Rate already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
				}
			}
		} else {
			if(list!=null && list.size()>0) {
				return new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"Entered Packing Rate already exists..! \"}", new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
		}
		packingRateEntity.getBucketEntity().setBucketId(packingRateMasterRequest.getPackingBucketId());
		packingRateEntity.setPackingRate( packingRateMasterRequest.getPackingRate() );
		packingRateEntity.setPackingRateDesc( packingRateMasterRequest.getPackingRateDesc());
		packingRateEntity.getParty().setnPartyId( packingRateMasterRequest.getPartyId() );
		packingRateEntity.setCreatedBy(userId);
		packingRateEntity.setUpdatedBy(userId);
		packingRateEntity.setCreatedOn(new Date());
		packingRateEntity.setUpdatedOn(new Date());
		packingRateRepository.save (packingRateEntity);
		if (packingRateMasterRequest.getPackingRateId() != null && packingRateMasterRequest.getPackingRateId() > 0) {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Rate details updated successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} else {
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Rate details saved successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		}
		return response;
	}

	@Override
	public ResponseEntity<Object> deleteRate(int id) {
		ResponseEntity<Object> response = null;
		try {
			packingRateRepository.deleteById(id);
			response = new ResponseEntity<>("{\"status\": \"success\", \"message\": \"Packing Rate details deleted successfully..! \"}", new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	@Override
	public PackingRateMasterResponse getByIdRate(int id) {
		PackingRateMasterResponse resp = null;
		Optional<PackingRateEntity> list = packingRateRepository.findById(id);
		if (list.isPresent()) {
			PackingRateEntity entity = list.get();
			resp = PackingRateEntity.valueOf(entity) ;
		}
		return resp;
	}

	@Override
	public List<PackingRateMasterResponse> getAllRateList() {

		List<PackingRateMasterResponse> instructionList = packingRateRepository.findAll().stream()
				.map(i -> PackingRateEntity.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

	@Override
	public List<PackingRateMasterResponse> getAllRateListPartyWise(int partyId) {

		List<PackingRateMasterResponse> instructionList = packingRateRepository.findByPartyId(partyId).stream()
				.map(i -> PackingRateEntity.valueOf(i)).collect(Collectors.toList());

		return instructionList;
	}

}
