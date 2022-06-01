package com.steel.product.application.entity;

import com.steel.product.application.dto.material.MaterialResponseDetailsDto;
import com.steel.product.application.dto.material.MaterialResponseDto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_tblmatdescription")
public class Material {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nmatid")
	private int matId;

	@Column(name = "vdescription")
	private String description;

	@Column(name = "createdby")
	private int createdBy;

	@Column(name = "updatedby")
	private int updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdon")
	private Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedon")
	private Date updatedOn;

	@Column(name = "hsn_Code")
	private String hsnCode;

	@Column(name = "isdeleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@OneToMany(mappedBy = "material", cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	private List<InwardEntry> inwardEntry;

	@OneToMany(mappedBy = "parentMaterial", cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	private List<MaterialGrade> materialGrade;

	@OneToMany(mappedBy = "materialType")
	private List<Rates> rates;

	@Column(name = "material_Code")
	private String materialCode;

	public int getMatId() {
		return this.matId;
	}

	public void setMatId(int matId) {
		this.matId = matId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return this.updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<InwardEntry> getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(List<InwardEntry> inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

	public List<MaterialGrade> getMaterialGrade() {
		return materialGrade;
	}

	public void setMaterialGrade(List<MaterialGrade> materialGrade) {
		this.materialGrade = materialGrade;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public static MaterialResponseDto valueOf(Material material, InwardEntry inwardEntry) {
		MaterialResponseDto materialResponseDto = new MaterialResponseDto();
		materialResponseDto.setMatId(material.getMatId());
		materialResponseDto.setDescription(material.getDescription());
		if (inwardEntry != null) {
			materialResponseDto.setMaterialGrade(MaterialGrade.valueOf(inwardEntry.getMaterialGrade()));
		}
		materialResponseDto.setHsnCode(material.getHsnCode());
		materialResponseDto.setMaterialCode(material.getMaterialCode());
		return materialResponseDto;
	}

	public static MaterialResponseDetailsDto valueOfMat(Material material) {
		MaterialResponseDetailsDto materialResponseDetailsDto = new MaterialResponseDetailsDto();
		materialResponseDetailsDto.setMatId(material.getMatId());
		materialResponseDetailsDto.setDescription(material.getDescription());
		materialResponseDetailsDto.setMaterialGrade(material.getMaterialGrade().stream().filter(m -> m != null)
				.map(m -> MaterialGrade.valueOf(m)).collect(Collectors.toList()));
		materialResponseDetailsDto.setHsnCode(material.getHsnCode());
		materialResponseDetailsDto.setMaterialCode(material.getMaterialCode());
		materialResponseDetailsDto.setDeleted(material.getIsDeleted());
		materialResponseDetailsDto.setCreatedOn(material.getCreatedOn());
		return materialResponseDetailsDto;
	}

}
