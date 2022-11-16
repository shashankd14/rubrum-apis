package com.steel.product.application.dto.party;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.steel.product.application.dto.address.AddressDto;
import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartyDto {

	@JsonProperty("nPartyId")
	private Integer nPartyId;

	private String partyName;

	private String partyNickname;

	private String contactName;

	private String contactNumber;

	private String gstNumber;

	private String panNumber;

	private String tanNumber;

	private String email1;

	private String email2;

	private String phone1;

	private String phone2;

	private AddressDto address1;

	private AddressDto address2;

	private List<PacketClassificationRequest> tags;

	private List<EndUserTagsRequest> endUserTags;

	private List<QualityPartyMappingRequestNew> templateIdList;

}
