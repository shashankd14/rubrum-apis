package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.material.MaterialDto;

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
  
  @Column(name = "isdeleted", columnDefinition = "BIT")
  private Boolean isDeleted;
  
  @JsonBackReference
  @OneToMany(mappedBy = "material", 
  		cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  private List<InwardEntry> inwardEntry;
  
  @JsonManagedReference
  @OneToMany(mappedBy = "parentMaterial", 
  		cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  private List<MaterialGrade> materialGrade;

  @JsonBackReference(value = "material-rates")
  @OneToMany(mappedBy = "materialType")
  private List<Rates> rates;
  
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

  public static MaterialDto valueOf(Material material,InwardEntry inwardEntry){
    MaterialDto materialDto = new MaterialDto();
    materialDto.setMaterialId(material.getMatId());
    materialDto.setMaterial(material.getDescription());
//    materialDto.setGrade(material.getMaterialGrade().stream().map(mg -> mg.getGradeName()).collect(Collectors.toList()));
    materialDto.setMaterialGradeDto(MaterialGrade.valueOf(inwardEntry.getMaterialGrade()));
    return materialDto;
  }
  
  
}
