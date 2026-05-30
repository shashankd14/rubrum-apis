package com.steel.product.application.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "monthly_summary_report")
public class MonthlySummaryReportEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "current_month")
	private int currentMonth;

	@Column(name = "current_year")
	private int currentYear;

	@Column(name = "current_stock")
	private BigDecimal currentStock;

	@Column(name = "rm_crcoil")
	private BigDecimal rmCrcoil;

	@Column(name = "rm_crsheet")
	private BigDecimal rmCrsheet;

	@Column(name = "rm_hrpocoil")
	private BigDecimal rmHrpocoil;

	@Column(name = "rm_hr")
	private BigDecimal rmHr;

	@Column(name = "rm_gpcoil")
	private BigDecimal rmGpcoil;

	@Column(name = "wip_crcoil")
	private BigDecimal wipCrcoil;

	@Column(name = "wip_crsheet")
	private BigDecimal wipCrsheet;

	@Column(name = "wip_hrpocoil")
	private BigDecimal wipHrpocoil;

	@Column(name = "wip_hr")
	private BigDecimal wipHr;

	@Column(name = "wip_gpcoil")
	private BigDecimal wipGpcoil;

	@Column(name = "fg_crcoil")
	private BigDecimal fgCrcoil;

	@Column(name = "fg_crsheet")
	private BigDecimal fgCrsheet;

	@Column(name = "fg_hrpocoil")
	private BigDecimal fgHrpocoil;

	@Column(name = "fg_hr")
	private BigDecimal fgHr;

	@Column(name = "fg_gpcoil")
	private BigDecimal fgGpcoil;

	@Column(name = "edgetrims_crcoil")
	private BigDecimal edgetrimsCrcoil;

	@Column(name = "edgetrims_crsheet")
	private BigDecimal edgetrimsCrsheet;

	@Column(name = "edgetrims_hrpocoil")
	private BigDecimal edgetrimsHrpocoil;

	@Column(name = "edgetrims_hr")
	private BigDecimal edgetrimsHr;

	@Column(name = "edgetrims_gpcoil")
	private BigDecimal edgetrimsGpcoil;

	@Column(name = "cutends_crcoil")
	private BigDecimal cutendsCrcoil;

	@Column(name = "cutends_crsheet")
	private BigDecimal cutendsCrsheet;

	@Column(name = "cutends_hrpocoil")
	private BigDecimal cutendsHrpocoil;

	@Column(name = "cutends_hr")
	private BigDecimal cutendsHr;

	@Column(name = "cutends_gpcoil")
	private BigDecimal cutendsGpcoil;

	@Column(name = "defectivehandling_crcoil")
	private BigDecimal defectiveHandlingCrcoil;

	@Column(name = "defectivehandling_crsheet")
	private BigDecimal defectiveHandlingCrsheet;

	@Column(name = "defectivehandling_hrpocoil")
	private BigDecimal defectiveHandlingHrpocoil;

	@Column(name = "defectivehandling_hr")
	private BigDecimal defectiveHandlingHr;

	@Column(name = "defectivehandling_gpcoil")
	private BigDecimal defectiveHandlingGpcoil;

	@Column(name = "defectiverm_crcoil")
	private BigDecimal defectiveRmCrcoil;

	@Column(name = "defectiverm_crsheet")
	private BigDecimal defectiveRmCrsheet;

	@Column(name = "defectiverm_hrpocoil")
	private BigDecimal defectiveRmHrpocoil;

	@Column(name = "defectiverm_hr")
	private BigDecimal defectiveRmHr;

	@Column(name = "defectiverm_gpcoil")
	private BigDecimal defectiveRmGpcoil;
	

	@Column(name = "rm_cam_purushotham")
	private BigDecimal rmCamPurushotham;

	@Column(name = "rm_cam_umesh")
	private BigDecimal rmCamUmesh;

	@Column(name = "rm_cam_divakar")
	private BigDecimal rmCamDivakar;

	@Column(name = "rm_cam_niraj")
	private BigDecimal rmCamNiraj;

	@Column(name = "wip_cam_purushotham")
	private BigDecimal wipCamPurushotham;

	@Column(name = "wip_cam_umesh")
	private BigDecimal wipCamUmesh;

	@Column(name = "wip_cam_divakar")
	private BigDecimal wipCamDivakar;

	@Column(name = "wip_cam_niraj")
	private BigDecimal wipCamNiraj;

	@Column(name = "fg_cam_purushotham")
	private BigDecimal fgCamPurushotham;

	@Column(name = "fg_cam_umesh")
	private BigDecimal fgCamUmesh;

	@Column(name = "fg_cam_divakar")
	private BigDecimal fgCamDivakar;

	@Column(name = "fg_cam_niraj")
	private BigDecimal fgCamNiraj;

	@Column(name = "edgetrims_cam_purushotham")
	private BigDecimal edgetrimsCamPurushotham;

	@Column(name = "edgetrims_cam_umesh")
	private BigDecimal edgetrimsCamUmesh;

	@Column(name = "edgetrims_cam_divakar")
	private BigDecimal edgetrimsCamDivakar;

	@Column(name = "edgetrims_cam_niraj")
	private BigDecimal edgetrimsCamNiraj;

	@Column(name = "cutends_cam_purushotham")
	private BigDecimal cutendsCamPurushotham;

	@Column(name = "cutends_cam_umesh")
	private BigDecimal cutendsCamUmesh;

	@Column(name = "cutends_cam_divakar")
	private BigDecimal cutendsCamDivakar;

	@Column(name = "cutends_cam_niraj")
	private BigDecimal cutendsCamNiraj;

	@Column(name = "defhandling_cam_purushotham")
	private BigDecimal defhandlingCamPurushotham;

	@Column(name = "defhandling_cam_umesh")
	private BigDecimal defhandlingCamUmesh;

	@Column(name = "defhandling_cam_divakar")
	private BigDecimal defhandlingCamDivakar;

	@Column(name = "defhandling_cam_niraj")
	private BigDecimal defhandlingCamNiraj;

	@Column(name = "defrm_cam_purushotham")
	private BigDecimal defrmCamPurushotham;

	@Column(name = "defrm_cam_umesh")
	private BigDecimal defrmCamUmesh;

	@Column(name = "defrm_cam_divakar")
	private BigDecimal defrmCamDivakar;

	@Column(name = "defrm_cam_niraj")
	private BigDecimal defrmCamNiraj;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
}