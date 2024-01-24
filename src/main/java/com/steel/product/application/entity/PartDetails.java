package com.steel.product.application.entity;

import lombok.Getter;
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
@Table(name = "product_part_details")
public class PartDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_details_id")
    private String partDetailsId;

    @Column(name = "target_weight")
    private Float targetWeight;

    @Column(name = "length")
    private Float length;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private Date updatedOn;

    @OneToMany(mappedBy = "partDetails", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<Instruction> instructions;

    @Column(name = "is_deleted", columnDefinition = "bit default 0")
    private Boolean isDeleted;
    
    @Column(name = "pdf_s3_url")
    private String pdfS3Url;

    @Column(name = "labelpdf_wip_s3_url")
    private String labelpdfWipS3Url;

    @Column(name = "labelpdf_fg_s3_url")
    private String labelpdfFgS3Url;

    @Column(name = "label_updated_time")
    private Date labelUpdatedTime;

    public void addInstruction(Instruction instruction) {
        if (this.instructions == null) {
            this.instructions = new HashSet<>();
        }
        this.getInstructions().add(instruction);
        instruction.setPartDetails(this);
    }

    public void removeInstruction(Instruction instruction) {
        this.instructions.remove(instruction);
        instruction.setPartDetails(null);
    }
    
}
