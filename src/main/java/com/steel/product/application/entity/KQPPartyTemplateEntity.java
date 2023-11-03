package com.steel.product.application.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.dto.quality.KQPPartyMappingResponse;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;

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

	@Column(name = "enduser_tag_id_list")
	private String endUserTagIdList;

	@Column(name = "thickness_list")
	private String thicknessList;

	@Column(name = "width_list")
	private String widthList;

	@Column(name = "length_list")
	private String lengthList;

	@Column(name = "mat_grade_id_list")
	private String matGradeIdList;

	@Column(name = "any_party_flag")
	private String anyPartyFlag;
	
	@Column(name = "any_matgrade_flag")
	private String anyMatgradeFlag;
	
	@Column(name = "any_endusertag_flag")
	private String anyEndusertagFlag;
	
	@Column(name = "any_width_flag")
	private String anyWidthFlag;
	
	@Column(name = "any_length_flag")
	private String anyLengthFlag;

	@Column(name = "any_thickness_flag")
	private String anyThicknessFlag;
	
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
		dtoResponse.setPartyName(entity.getParty().getPartyName());
		dtoResponse.setUserId( entity.getKqpEntity().getCreatedBy());
		dtoResponse.setStageName( entity.getKqpEntity().getStageName());
		dtoResponse.setKqpName(entity.getKqpEntity().getKqpName());
		dtoResponse.setKqpId(entity.getKqpEntity().getKqpId());
		dtoResponse.setEndUserTagIdList( entity.getEndUserTagIdList() );
		dtoResponse.setMatGradeIdList( entity.getMatGradeIdList() );
		dtoResponse.setThicknessList( entity.getThicknessList() );
		dtoResponse.setWidthList( entity.getWidthList());
		dtoResponse.setLengthList( entity.getLengthList());
		dtoResponse.setAnyPartyFlag( entity.getAnyPartyFlag());
		dtoResponse.setAnyMatgradeFlag( entity.getAnyMatgradeFlag());
		dtoResponse.setAnyEndusertagFlag( entity.getAnyEndusertagFlag());
		dtoResponse.setAnyLengthFlag( entity.getAnyLengthFlag());
		dtoResponse.setAnyThicknessFlag( entity.getAnyThicknessFlag());
		dtoResponse.setAnyWidthFlag ( entity.getAnyWidthFlag());
		return dtoResponse;
	}

}