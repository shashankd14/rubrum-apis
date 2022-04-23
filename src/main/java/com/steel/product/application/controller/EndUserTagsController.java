package com.steel.product.application.controller;

import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.entity.EndUserTagsEntity;
import com.steel.product.application.service.EndUserTagsService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "End User Tags", description = "End User Tags")
@RequestMapping({ "/endusertags" })
public class EndUserTagsController {

	@Resource
	private EndUserTagsService endUserTagsService;

	@GetMapping({ "/list" })
	public ResponseEntity<Object> getAllTags() {
		try {
			return new ResponseEntity(endUserTagsService.getAllEndUserTags(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/getById/{tagId}" })
	public ResponseEntity<Object> getAddressById(@PathVariable("tagId") int tagId) {
		try {
			EndUserTagsEntity endUserTagsEntity = new EndUserTagsEntity();
			endUserTagsEntity = endUserTagsService.getEndUserTagsById(tagId);
			return new ResponseEntity(endUserTagsEntity, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getByPartyId/{partyId}")
	public List<EndUserTagsResponse> getAddressById(@PathVariable("partyId") Integer partyId) {
		return endUserTagsService.getAllEndUserTagsByPartyId(partyId);
	}

	@PostMapping("/save")
	public String saveEndUserTags(@RequestBody List<EndUserTagsRequest> endUserTagsRequests) {
		return endUserTagsService.saveEndUserTags(endUserTagsRequests);
	}

	@PutMapping("/update")
	public String updateEndUserTags(@RequestBody List<EndUserTagsRequest> endUserTagsRequests) {
		return endUserTagsService.saveEndUserTags(endUserTagsRequests);
	}

}
