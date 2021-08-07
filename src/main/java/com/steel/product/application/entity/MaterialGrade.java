package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.materialGradeDto.MaterialGradeDto;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_material_grades")
public class MaterialGrade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gradeid")
	private int gradeId;

	@JsonBackReference(value="material-grade")
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "nmatid")
	private Material parentMaterial;

	@Column(name = "gradename")
	private String gradeName;

	@JsonManagedReference(value="inward-grade")
	@OneToMany(mappedBy = "materialGrade", 
			cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	private List<InwardEntry> inwardEntry;
	
	

	public int getGradeId() {
		return this.gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	public Material getParentMaterial() {
		return parentMaterial;
	}

	public void setParentMaterial(Material parentMaterial) {
		this.parentMaterial = parentMaterial;
	}

	public String getGradeName() {
		return this.gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public List<InwardEntry> getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(List<InwardEntry> inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

	public static MaterialGradeDto valueOf(MaterialGrade materialGrade){
		MaterialGradeDto materialGradeDto = new MaterialGradeDto();
		materialGradeDto.setMaterialGradeId(materialGrade.getGradeId());
		materialGradeDto.setGradeName(materialGrade.getGradeName());
		return materialGradeDto;
	}


}
