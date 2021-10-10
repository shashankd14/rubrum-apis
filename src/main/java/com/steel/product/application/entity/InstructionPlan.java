package com.steel.product.application.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "PRODUCT_INSTRUCTION_PLAN")
public class InstructionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSTRUCTION_PLAN_ID")
    private String instructionPlanId;

    @Column(name = "TARGET_WEIGHT")
    private Float targetWeight;

    @Column(name = "LENGTH")
    private Float length;

    @Column(name = "IS_EQUAL", columnDefinition = "boolean default false")
    private Boolean isEqual;

    @Column(name = "NO_OF_PARTS")
    private Integer noOfParts;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "UPDATED_BY")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "CREATED_ON")
    private Date createdOn;

    @UpdateTimestamp
    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "IS_DELETED", columnDefinition = "boolean default false")
    private Boolean isDeleted;
}
