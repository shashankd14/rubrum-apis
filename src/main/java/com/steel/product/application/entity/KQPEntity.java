package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.KQPResponse;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "quality_kqp_dtls")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class KQPEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kqp_id")
	private Integer kqpId;

	@Column(name = "kqp_name")
	private String kqpName;
	
	@Column(name = "kqp_desc")
	private String kqpDesc;
	
	@Column(name = "kqp_summary")
	private String kqpSummary;
	
	@Column(name = "stage_name")
	private String stageName;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

	public static KQPResponse valueOf(KQPEntity kqpEntity){
		KQPResponse dtoResponse = new KQPResponse();
		dtoResponse.setKqpId( kqpEntity.getKqpId());
		dtoResponse.setKqpName(kqpEntity.getKqpName());
		dtoResponse.setKqpDesc(kqpEntity.getKqpDesc());
		dtoResponse.setKqpSummary( kqpEntity.getKqpSummary());
		dtoResponse.setStageName(kqpEntity.getStageName());
		dtoResponse.setCreatedBy(kqpEntity.getCreatedBy());
		dtoResponse.setUpdatedBy(kqpEntity.getUpdatedBy());
		dtoResponse.setCreatedOn(kqpEntity.getCreatedOn());
		dtoResponse.setUpdatedOn(kqpEntity.getUpdatedOn());
        return dtoResponse;
    }

}