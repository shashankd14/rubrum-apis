package com.steel.product.jswone.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/** Raw rows of the FG stock upload file, stored as they are in the file, plus the generated MM_ID. */
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Data
@Entity
@Table(name = "jsw_fg_file_data")
public class FgFileDataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fg_id")
	private Integer fgId;

	// ---- file columns, as uploaded ----
	@Column(name = "coil_number")
	private String coilNumber;

	@Column(name = "customer_batch_id")
	private String customerBatchId;

	@Column(name = "finishing_date")
	private String finishingDate;

	@Column(name = "current_dt")
	private String currentDate;

	@Column(name = "coil_age")
	private String coilAge;

	@Column(name = "no_of_pieces")
	private String noOfPieces;

	@Column(name = "material_desc")
	private String materialDesc;

	@Column(name = "material_grade")
	private String materialGrade;

	@Column(name = "mother_coil_no")
	private String motherCoilNo;

	@Column(name = "tdc_no")
	private String tdcNo;

	@Column(name = "packet_id")
	private String packetId;

	@Column(name = "thickness")
	private String thickness;

	@Column(name = "actual_width")
	private String actualWidth;

	@Column(name = "actual_length")
	private String actualLength;

	@Column(name = "actual_weight")
	private String actualWeight;

	@Column(name = "classification_tag")
	private String classificationTag;

	@Column(name = "end_user_tag")
	private String endUserTag;

	// ---- derived ----
	/** COIL / SHEET (No'of Pieces > 0 = SHEET) */
	@Column(name = "form")
	private String form;

	@Column(name = "mm_id")
	private String mmId;

	@Column(name = "mm_description")
	private String mmDescription;

	@Column(name = "row_no")
	private Integer rowNo;

	/** SUCCESS / ERROR */
	@Column(name = "upload_status")
	private String uploadStatus;

	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "filename")
	private String filename;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;
}
