package com.steel.product.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.steel.product.application.entity.MonthlySummaryReportEntity;

@Repository
public interface MonthlySummaryReportRepository extends JpaRepository<MonthlySummaryReportEntity, Integer> {

	@Query(value =
	        "SELECT "
	      + "(SELECT ROUND(COALESCE(SUM(in_stock_weight),0),3) "
	      + " FROM product_tblinwardentry inw WHERE inw.npartyid IN (:partyIdList)) AS Opening_Stock, "

		  //RM WISE AND GRADE WISE break up 
	      //------------------------------------
		  
	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
	      + " AND npartyid IN (:partyIdList)) AS RM_CRCOIL, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
	      + " AND npartyid IN (:partyIdList)) AS RM_CRSHEET, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
	      + " AND npartyid IN (:partyIdList)) AS RM_HRPOCOIL, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
	      + " AND npartyid IN (:partyIdList)) AS RM_HR, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
	      + " AND npartyid IN (:partyIdList)) AS RM_GPCOIL,"

	      //WIP Classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS WIP_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS WIP_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS WIP_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS WIP_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS WIP_GPCOIL, "

	      //FG classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS FG_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS FG_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS FG_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS FG_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS FG_GPCOIL, "
	      
	      //EDGE TRIMS Classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS EDGETRIMS_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS EDGETRIMS_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS EDGETRIMS_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS EDGETRIMS_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS EDGETRIMS_GPCOIL, "
	      

	      // CUT ENDS Classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS CUTENDS_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS CUTENDS_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS CUTENDS_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS CUTENDS_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS CUTENDS_GPCOIL, "

	      // Defective(HANDLING) (DEFFECTVE SHEETS) Classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveHANDLING_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveHANDLING_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveHANDLING_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveHANDLING_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveHANDLING_GPCOIL, "
	      
	      // Defective (RM)  (RM DEFECT (OVAL SHAPE, EXCESS OD, TELESCOPICITY) Classification WISE AND GRADE WISE break up 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveRM_CRCOIL, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%CR SH%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveRM_CRSHEET, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%HRPO COIL%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveRM_HRPOCOIL, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription like '%HR COIL%' or vdescription like '%HR SHEE%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveRM_HR, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND nmatid IN (SELECT nmatid FROM product_tblmatdescription WHERE vdescription LIKE '%GP%') "
		  + " AND inw.npartyid IN (:partyIdList)) AS DefectiveRM_GPCOIL "
	      , nativeQuery = true)
	List<Object[]> getOpeningStock( @Param("partyIdList") List<Integer> partyIdList);
	

	@Query(value =
	        "SELECT "
		  //RM WISE Cam wise report 
	      //------------------------------------
		  
	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE REMARKS like '%PURUSHOTHAM%' "
	      + " AND npartyid IN (:partyIdList)) AS CAM_RM_PURUSHOTHAM, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE REMARKS like '%UMESH%' "
	      + " AND npartyid IN (:partyIdList)) AS CAM_RM_UMESH, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE REMARKS like '%DI%' "
	      + " AND npartyid IN (:partyIdList)) AS CAM_RM_DIWAKAR, "

	      + "(SELECT ROUND(COALESCE(SUM(fpresent),0),3) "
	      + " FROM product_tblinwardentry "
	      + " WHERE REMARKS like '%NIRAJ%' "
	      + " AND npartyid IN (:partyIdList)) AS CAM_RM_NIRAJ, "

	      //WIP Classification WISE  Cam wise report 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_WIP_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_WIP_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_WIP_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 58 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_WIP_NIRAJ, " 

	      //FG classification WISE  Cam wise report 
	      //-----------------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_FG_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_FG_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_FG_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 1 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_FG_NIRAJ, " 
	      
	      //EDGE TRIMS Classification WISE  Cam wise report 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_EDGETRIMS_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_EDGETRIMS_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_EDGETRIMS_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 2 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_EDGETRIMS_HR, " 
	      

	      // CUT ENDS Classification WISE  Cam wise report 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_CUTENDS_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_CUTENDS_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_CUTENDS_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 4 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_CUTENDS_NIRAJ, " 

	      // Defective(HANDLING) (DEFFECTVE SHEETS) Classification WISE  Cam wise report 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveHANDLING_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveHANDLING_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveHANDLING_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 49 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveHANDLING_NIRAJ, " 
	      
	      // Defective (RM)  (RM DEFECT (OVAL SHAPE, EXCESS OD, TELESCOPICITY) Classification WISE  Cam wise report 
	      //------------------------------------

		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.REMARKS like '%PURUSHOTHAM%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveRM_PURUSHOTHAM, "
	      
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.REMARKS like '%UMESH%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveRM_UMESH, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.REMARKS like '%DI%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveRM_DI, "
		
		  + " (SELECT ROUND(COALESCE(SUM(ins.actualweight),0),3) "
		  + " FROM product_tblinwardentry inw "
		  + " JOIN product_instruction ins ON ins.inwardid = inw.inwardentryid "
		  + " WHERE ins.status <> 4 AND ins.packet_classification_id = 77 "
	      + " AND inw.REMARKS like '%NIRAJ%' "
		  + " AND inw.npartyid IN (:partyIdList)) AS CAM_DefectiveRM_NIRAJ" 
	      , nativeQuery = true)
	List<Object[]> getOpeningCamStock( @Param("partyIdList") List<Integer> partyIdList);
	
	
	
	
	

}
