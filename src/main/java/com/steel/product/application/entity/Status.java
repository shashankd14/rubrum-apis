package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.steel.product.application.dto.status.StatusDto;
import com.steel.product.application.entity.InwardEntry;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "product_status")
public class Status {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "statusid")
	private int statusId;

	@Column(name = "statusname")
	private String statusName;

	@JsonBackReference
	@OneToMany(mappedBy = "status", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
			CascadeType.REFRESH },fetch = FetchType.LAZY)
	private List<InwardEntry> inwardEntry;

	@JsonBackReference
	@OneToMany(mappedBy = "status", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
			CascadeType.REFRESH },fetch = FetchType.LAZY)
	private List<Instruction> instruction;

	public void addInstruction(Instruction instruction){
		if(this.getInstruction() == null){
			this.instruction = new ArrayList<>();
		}
		this.getInstruction().add(instruction);
		instruction.setStatus(this);
	}

	public void removeInstruction(Instruction instruction){
		this.getInstruction().remove(instruction);
		instruction.setStatus(null);
	}

	public void addInwardEntry(InwardEntry inward){
		if(this.getInwardEntry() == null){
			this.inwardEntry = new ArrayList<>();
		}
		this.getInwardEntry().add(inward);
		inward.setStatus(this);
	}

	public void removeInwardEntry(InwardEntry inwardEntry){
		this.getInwardEntry().remove(inwardEntry);
		inwardEntry.setStatus(null);
	}

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public List<InwardEntry> getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(List<InwardEntry> inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

	public List<Instruction> getInstruction() {
		return instruction;
	}

	public void setInstruction(List<Instruction> instruction) {
		this.instruction = instruction;
	}

	public static StatusDto valueOf(Status status){
		StatusDto statusDto = new StatusDto();
		statusDto.setStatusId(status.getStatusId());
		statusDto.setStatusName(status.getStatusName());
		return statusDto;
	}

}
