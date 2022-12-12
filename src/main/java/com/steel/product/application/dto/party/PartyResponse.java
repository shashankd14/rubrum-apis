package com.steel.product.application.dto.party;

import com.steel.product.application.dto.address.AddressDto;
import com.steel.product.application.dto.endusertags.EndUserTagsResponse;
import com.steel.product.application.dto.packetClassification.PacketClassificationResponse;
import com.steel.product.application.dto.quality.QualityPartyMappingRequestNew;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class PartyResponse {

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
    private List<PacketClassificationResponse> packetClassificationTags;
    private List<EndUserTagsResponse> endUserTags;
	private List<QualityPartyMappingRequestNew> templateIdList = new ArrayList<>();
    
    public Integer getnPartyId() {
        return nPartyId;
    }

    public void setnPartyId(Integer nPartyId) {
        this.nPartyId = nPartyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyNickname() {
        return partyNickname;
    }

    public void setPartyNickname(String partyNickname) {
        this.partyNickname = partyNickname;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getTanNumber() {
        return tanNumber;
    }

    public void setTanNumber(String tanNumber) {
        this.tanNumber = tanNumber;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public AddressDto getAddress1() {
        return address1;
    }

    public void setAddress1(AddressDto address1) {
        this.address1 = address1;
    }

    public AddressDto getAddress2() {
        return address2;
    }

    public void setAddress2(AddressDto address2) {
        this.address2 = address2;
    }

    public List<PacketClassificationResponse> getPacketClassificationTags() {
        return packetClassificationTags;
    }

    public void setPacketClassificationTags(List<PacketClassificationResponse> packetClassificationTags) {
        this.packetClassificationTags = packetClassificationTags;
    }

	public List<EndUserTagsResponse> getEndUserTags() {
		return endUserTags;
	}

	public void setEndUserTags(List<EndUserTagsResponse> endUserTags) {
		this.endUserTags = endUserTags;
	}

	public List<QualityPartyMappingRequestNew> getTemplateIdList() {
		return templateIdList;
	}

	public void setTemplateIdList(List<QualityPartyMappingRequestNew> templateIdList) {
		this.templateIdList = templateIdList;
	}
    
    
    
}
