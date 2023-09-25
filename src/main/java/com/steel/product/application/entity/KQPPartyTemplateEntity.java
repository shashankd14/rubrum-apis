package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "quality_kqp_partymap")
@Data
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class KQPPartyTemplateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kqp_id")
	private KQPEntity kqpEntity=new KQPEntity();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "npartyid")
	private Party party = new Party();

	@Column(name = "enduser_tag_id")
	private Integer endUserTagId;

	@Column(name = "thickness")
	private BigDecimal thickness;

	@Column(name = "width")
	private BigDecimal width;

	@Column(name = "length")
	private BigDecimal length;

	@Column(name = "mat_grade_id")
	private Integer matGradeId;

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

	public static KQPPartyMappingResponse valueOf(KQPPartyTemplateEntity entity) {
		KQPPartyMappingResponse dtoResponse = new KQPPartyMappingResponse();
		dtoResponse.setId( entity.getId());
		dtoResponse.setPartyId(entity.getParty().getnPartyId());
		try {
			dtoResponse.setPartyName(entity.getParty().getPartyName());
		} catch (Exception e) {
		}
		dtoResponse.setKqpName(entity.getKqpEntity().getKqpName());
		dtoResponse.setKqpId(entity.getKqpEntity().getKqpId());
		dtoResponse.setEndUserTagId(entity.getEndUserTagId());
		dtoResponse.setMatGradeId(entity.getMatGradeId());
		dtoResponse.setThickness(entity.getThickness());
		dtoResponse.setWidth(entity.getWidth() );
		dtoResponse.setLength(entity.getLength());
		return dtoResponse;
	}

}