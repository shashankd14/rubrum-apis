package com.steel.product.application.controller;

import com.steel.product.application.dto.lamination.LaminationChargesRequest;
import com.steel.product.application.dto.lamination.LaminationChargesResponse;
import com.steel.product.application.entity.LaminationStaticDataEntity;
import com.steel.product.application.service.LaminationChargesService;
import com.steel.product.application.util.CommonUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Lamination Charges Master", description = "Lamination Charges Master")
@RequestMapping({ "/lamination" })
public class LaminationChargesController {

	@Autowired
	private LaminationChargesService laminationChargesService;
	
	@Autowired
	private CommonUtil commonUtil;

	@PostMapping(value = "/save", produces = "application/json")
	public ResponseEntity<Object> save(@RequestBody List<LaminationChargesRequest> laminationChargesRequest) {
		int userId = commonUtil.getUserId();
		List<LaminationChargesRequest> endUserTagsRequestsNew = new ArrayList<>();
		for (LaminationChargesRequest req : laminationChargesRequest) {
			req.setUserId(userId);
			endUserTagsRequestsNew.add(req);
		}
		return laminationChargesService.save(endUserTagsRequestsNew);
	}

	@PutMapping(value = "/update", produces = "application/json")
	public ResponseEntity<Object> update(@RequestBody List<LaminationChargesRequest> laminationChargesRequest) {
		int userId = commonUtil.getUserId();
		List<LaminationChargesRequest> endUserTagsRequestsNew = new ArrayList<>();
		for (LaminationChargesRequest req : laminationChargesRequest) {
			req.setUserId(userId);
			endUserTagsRequestsNew.add(req);
		}
		return laminationChargesService.save(endUserTagsRequestsNew);
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> delete(@PathVariable("id") int id) {
		return laminationChargesService.delete(id);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Object> getById(@PathVariable("id") int id) {
		LaminationChargesResponse resp = laminationChargesService.getById(id);
		return new ResponseEntity<Object>(resp, HttpStatus.OK);
	}

	@GetMapping(value = "/party/{partyId}", produces = "application/json")
	public List<LaminationChargesResponse> getPartyById(@PathVariable("partyId") Integer partyId) {
		List<LaminationChargesResponse> list = laminationChargesService.getByPartyId(partyId);
		return list;
	}

	@GetMapping(value = "/getLaminationChargesList", produces = "application/json")
	public List<LaminationStaticDataEntity> getLaminationChargesList() {
		List<LaminationStaticDataEntity> list = laminationChargesService.getLaminationDetails();
		return list;
	}

	@GetMapping(produces = "application/json")
	public List<LaminationChargesResponse> getAllItemDetails() {
		List<LaminationChargesResponse> list = laminationChargesService.getAllLaminationDetails();
		return list;
	}

}
