package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_packet_classification")
public class PacketClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_id")
    private String classificationId;

    @Column(name = "classification_name")
    private String classificationName;

    @JsonBackReference
    @OneToMany(mappedBy = "packetClassification")
    private List<Instruction> instructionClass;

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
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
}
