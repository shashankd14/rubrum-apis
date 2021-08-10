package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_packet_classification")
public class PacketClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_id")
    private Integer classificationId;

    @Column(name = "classification_name")
    private String classificationName;

    @JsonBackReference
    @OneToMany(mappedBy = "packetClassification")
    private List<Instruction> instructionClass;

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public List<Instruction> getInstructionClass() {
        return instructionClass;
    }

    public void setInstructionClass(List<Instruction> instructionClass) {
        this.instructionClass = instructionClass;
    }

    public void addInstruction(Instruction instruction){
        this.getInstructionClass().add(instruction);
        instruction.setPacketClassification(this);
    }

    public void removeInstruction(Instruction instruction){
        this.getInstructionClass().remove(instruction);
        instruction.setPacketClassification(null);
    }
}
