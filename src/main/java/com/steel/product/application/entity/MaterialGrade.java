package com.steel.product.application.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_material_grades")
public class MaterialGrade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gradeid")
	private int gradeId;

	@JsonBackReference
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	@JoinColumn(name = "nmatid")
	private Material parentMaterial;

	@Column(name = "gradename")
	private String gradeName;

	@JsonBackReference
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

}
