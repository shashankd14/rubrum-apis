package com.steel.product.application.entity;

import com.steel.product.application.dto.instructionPlan.InstructionPlanDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_INSTRUCTION_PLAN")
public class InstructionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSTRUCTION_PLAN_ID", nullable = false)
    private String instructionPlanId;

    @Column(name = "TARGET_WEIGHT", nullable = false)
    private Float targetWeight;

    @Column(name = "NO_OF_PARTS", nullable = false)
    private Integer noOfParts;

    @Column(name = "IS_EQUAL", columnDefinition = "boolean default false", nullable = false)
    private Boolean isEqual;

    @OneToMany(mappedBy = "instructionPlan", fetch = FetchType.LAZY)
    private Set<Instruction> instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_ID", nullable = false)
    private Process process;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "UPDATED_BY")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "CREATED_ON", updatable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "IS_DELETED", columnDefinition = "boolean default false")
    private Boolean isDeleted;

    public void addInstruction(Instruction instruction) {
        if (this.instructions == null) {
            this.instructions = new HashSet<>();
        }
        this.instructions.add(instruction);
        instruction.setInstructionPlan(this);
    }

    public void removeInstruction(Instruction instruction) {
        this.instructions.remove(instruction);
        instruction.setInstructionPlan(null);
    }

    public static InstructionPlan entityOf(InstructionPlanDto instructionPlanDto) {
        InstructionPlan instructionPlan = new InstructionPlan();
        instructionPlan.setTargetWeight(instructionPlanDto.getTargetWeight());
        instructionPlan.setNoOfParts(instructionPlanDto.getNoOfParts());
        instructionPlan.setIsEqual(instructionPlanDto.getIsEqual());
        instructionPlan.setCreatedBy(instructionPlanDto.getCreatedBy());
        instructionPlan.setUpdatedBy(instructionPlanDto.getUpdatedBy());
        return instructionPlan;
    }
}
