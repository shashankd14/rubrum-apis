package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.endusertags.EndUserTagsRequest;
import com.steel.product.application.dto.packetClassification.PacketClassificationRequest;
import com.steel.product.application.dto.party.PartyDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "product_tblpartydetails")
public class Party {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "npartyid")
	private Integer nPartyId;

	@Column(name = "partyname")
	private String partyName;

	@Column(name = "partynickname")
	private String partyNickname;

	@Column(name = "contactname")
	private String contactName;

	@Column(name = "contactnumber")
	private String contactNumber;

	@Column(name = "gstnumber")
	private String gstNumber;

	@Column(name = "pannumber")
	private String panNumber;

	@Column(name = "tannumber")
	private String tanNumber;

	@Column(name = "email1")
	private String email1;

	@Column(name = "email2")
	private String email2;

	@Column(name = "phone1")
	private String phone1;

	@Column(name = "phone2")
	private String phone2;

	@ManyToOne(cascade = { CascadeType.PERSIST,CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH },fetch = FetchType.LAZY)
	@JoinColumn(name = "address1")
	private Address address1;

	@ManyToOne(cascade = { CascadeType.PERSIST,CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH },fetch = FetchType.LAZY)
	@JoinColumn(name = "address2")
	private Address address2;

	@Column(name = "createdby")
	private int createdBy;

	@Column(name = "updatedby")
	private int updatedBy;

	@Column(name = "createdon",updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updatedon")
	@UpdateTimestamp
	private Date updatedOn;

	@Column(name = "isdeleted", columnDefinition = "BIT DEFAULT 0")
	private Boolean isDeleted;

	@JsonManagedReference(value = "party-inward")
	@OneToMany(mappedBy = "party")
	private List<InwardEntry> inwardEntry;

	@OneToMany(mappedBy = "partyRates")
	@JsonManagedReference(value = "party-rates")
	private List<Rates> rates;

	@ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
	@JoinTable(name="product_classification_party",
	joinColumns = @JoinColumn(name="party_id"),
	inverseJoinColumns = @JoinColumn(name="classificationId"))
	private Set<PacketClassification> packetClassificationTags = new HashSet<>();

	@ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
	@JoinTable(name="product_endusertag_party",
	joinColumns = @JoinColumn(name="party_id"),
	inverseJoinColumns = @JoinColumn(name="tagId"))
	private Set<EndUserTagsEntity> endUserTags = new HashSet<>();

	public void addEndUserTags(EndUserTagsEntity endUserTagsEntity){
		this.endUserTags.add(endUserTagsEntity);
		endUserTagsEntity.getParties().add(this);
	}

	public void removeEndUserTags(EndUserTagsEntity endUserTagsEntity){
		this.endUserTags.remove(endUserTagsEntity);
		endUserTagsEntity.getParties().remove(this);
	}
	
	public void addPacketClassification(PacketClassification packetClassification){
		this.packetClassificationTags.add(packetClassification);
		packetClassification.getParties().add(this);
	}

	public void removePacketClassification(PacketClassification packetClassification){
		this.packetClassificationTags.remove(packetClassification);
		packetClassification.getParties().remove(this);
	}

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

	public Address getAddress1() {
		return address1;
	}

	public void setAddress1(Address address1) {
		this.address1 = address1;
	}

	public void setAddress2(Address address2) {
		this.address2 = address2;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Boolean getDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean deleted) {
		isDeleted = deleted;
	}

	public List<InwardEntry> getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(List<InwardEntry> inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

	public List<Rates> getRates() {
		return rates;
	}

	public void setRates(List<Rates> rates) {
		this.rates = rates;
	}

	public Address getAddress2() {
		return address2;
	}

	public Set<PacketClassification> getPacketClassificationTags() {
		return packetClassificationTags;
	}

	public void setPacketClassificationTags(Set<PacketClassification> packetClassificationTags) {
		this.packetClassificationTags = packetClassificationTags;
	}

	public Set<EndUserTagsEntity> getPacketEndUserTags() {
		return endUserTags;
	}

	public void setPacketEndUserTags(Set<EndUserTagsEntity> endUserTags) {
		this.endUserTags = endUserTags;
	}

	public static PartyDto valueOf(Party party){
		PartyDto partyDto = new PartyDto();
		partyDto.setPartyName(party.getPartyName());
		partyDto.setPartyId(party.getnPartyId().toString());
		partyDto.setPartyNickname(party.getPartyNickname());
		if(party.getAddress1() != null) {
			partyDto.setAddress1(Address.valueOf(party.getAddress1()));
		}
		if(party.getAddress2() != null) {
			partyDto.setAddress2(Address.valueOf(party.getAddress2()));
		}
		partyDto.setContactName(party.getContactName());
		partyDto.setContactNumber(party.getContactNumber());
		partyDto.setEmail1(party.getEmail1());
		partyDto.setEmail2(party.getEmail2());
		partyDto.setGstNumber(party.getGstNumber());
		partyDto.setPanNumber(party.getPanNumber());
		partyDto.setPhone1(party.getPhone1());
		partyDto.setPhone2(party.getPhone2());
		partyDto.setTanNumber(party.getTanNumber());
		List<PacketClassificationRequest> list = new ArrayList<>();
		for(PacketClassification pc: party.getPacketClassificationTags()){
			PacketClassificationRequest req = new PacketClassificationRequest();
			req.setClassificationId(pc.getClassificationId());
			req.setClassificationName(pc.getClassificationName());
			list.add(req);
		}
		partyDto.setTags(list);
		
		List<EndUserTagsRequest> endUserTagsList = new ArrayList<>();
		for(EndUserTagsEntity pc: party.getPacketEndUserTags()){
			EndUserTagsRequest req = new EndUserTagsRequest();
			req.setTagId(pc.getTagId());
			req.setTagName( pc.getTagName());
			endUserTagsList.add(req);
		}
		partyDto.setEndUserTags(endUserTagsList);
		return partyDto;
	}
}
