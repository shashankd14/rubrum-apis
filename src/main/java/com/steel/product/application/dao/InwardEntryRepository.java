package com.steel.product.application.dao;

import com.steel.product.application.entity.InwardEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;
import static org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH;

@Repository
public interface InwardEntryRepository extends JpaRepository<InwardEntry, Integer> {

    @Query(nativeQuery = true, value = "select * from aspen.product_tblinwardentry where nPartyId= :partyId order by dReceivedDate ")
    List<InwardEntry> getInwardEntriesByPartyId(@Param("partyId") Integer paramInteger);

    @Query("select inw from InwardEntry inw where (inw.coilNumber like %:searchText% or "
    		+ " inw.customerBatchId like %:searchText% or inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) "
    		+ " order by inwardEntryId desc")
    Page<InwardEntry> findAll(@Param("searchText") String searchText, Pageable pageable);
    
	@Query("select inw from InwardEntry inw where (inw.coilNumber like %:searchText% or "
			+ " inw.customerBatchId like %:searchText% or inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) "
			+ " and inw.party.nPartyId in :partyIds order by inwardEntryId desc")
	Page<InwardEntry> findAll(@Param("searchText") String searchText, @Param("partyIds") List<Integer> partyIds, Pageable pageable);
    
	@Query("select inw from InwardEntry inw where (inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
			+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId=:partyId order by inwardEntryId desc")
	Page<InwardEntry> findAll(@Param("searchText") String searchText, @Param("partyId") int partyId, Pageable pageable);
    
    @Query("select inw from InwardEntry inw order by inwardEntryId desc")
    List<InwardEntry> findAll();

    @Query(nativeQuery = true, value = "SELECT coilNumber FROM product_tblinwardentry WHERE coilNumber = :coilNumber")
    String isCoilNumberPresent(@Param("coilNumber") String paramString);

    @Query(nativeQuery = true, value = "SELECT customerbatchid FROM product_tblinwardentry WHERE customerbatchid = :customerbatchid limit 1")
    String isCustomerBatchIdPresent(@Param("customerbatchid") String customerbatchId);

    @Query("select inw from InwardEntry inw left join fetch inw.instructions ins where inw.coilNumber = :coilNumber order by ins.instructionId desc")
    <T> Optional<InwardEntry> findByCoilNumber(@Param("coilNumber")String coilNumber);

    @Query("select DISTINCT(inw) from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins join fetch ins.deliveryDetails dd where dd is not null" +
            " and ins.instructionId in :instructionIds and" +
            " ins.status.statusId = 4 order by ins.instructionId asc")
    public List<InwardEntry> findDeliveryItemsByInstructionIds(@Param("instructionIds")List<Integer> instructionIds);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade where inw.inwardEntryId = :inwardId")
    public Optional<InwardEntry> findById(@Param("inwardId")Integer inwardId);

    @Query("select inw from InwardEntry inw join fetch inw.party join fetch inw.material join fetch inw.materialGrade join fetch inw.instructions ins join fetch ins.childInstructions")
    List<InwardEntry> findAllInwards();

    @Query("select distinct inw from InwardEntry inw left join fetch inw.party party left join fetch inw.material mat " +
            "left join fetch inw.materialGrade matG left join fetch inw.instructions ins left join fetch ins.packetClassification " +
            "where party.nPartyId = :partyId and inw.isDeleted is false and inw.status < 4 and inw.inStockWeight > 0 and ins.groupId is null " +
            "or ins is null order by inw.inwardEntryId")
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "10"),
            @QueryHint(name = HINT_PASS_DISTINCT_THROUGH,value = "false")
    })
    List<InwardEntry> findInwardByPartyId(Integer partyId);

    @Query(value="SELECT DISTINCT part.part_details_id, part.pdf_s3_url, part.qrcode_s3_url FROM product_part_details part INNER JOIN product_instruction ins ON part.id = ins.part_details_id INNER JOIN product_tblinwardentry inward ON ins.inwardid = inward.inwardentryid WHERE ins.inwardid = :inwardId", nativeQuery = true)
    List<Object[]> getPlanPDFs(@Param("inwardId") Integer inwardId);

	@Modifying
	@Transactional
	@Query("update InwardEntry set pdfS3Url=:url where inwardEntryId= :inwardId ")
	public void updateS3InwardPDF(@Param("inwardId") Integer inwardId, @Param("url") String url);

    @Query(nativeQuery = true, value = "SELECT pdf_s3_url FROM product_tblinwardentry WHERE inwardentryid = :inwardId")
    public String getS3URL(@Param("inwardId") Integer inwardId);
    
    @Query(nativeQuery = true, value = "SELECT qrcode_s3_url FROM product_tblinwardentry WHERE inwardentryid = :inwardId")
    public String getQRCodeS3URL(@Param("inwardId") Integer inwardId);
    
    @Query(nativeQuery = true, value = "SELECT qrcode_editfinish_s3_url FROM product_tblinwardentry WHERE inwardentryid = :inwardId")
    public String getQRCodeEditFinishS3URL(@Param("inwardId") Integer inwardId);

    @Query("select distinct pd from DeliveryDetails pd join fetch pd.instructions ins join ins.inwardId where ins.inwardId.inwardEntryId= :inwardId")
    List<Object[]> getDCPDFs(@Param("inwardId") Integer inwardId);
    
    @Query("select distinct pd from DeliveryDetails pd where pd.deliveryId in :dcidsList")
    List<Object[]> getDCALLPDFs( @Param("dcidsList") List<Integer> dcidsList);

	@Query("select inw from InwardEntry inw where inw.status.statusId=2 and (inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
			+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId=:partyId order by inwardEntryId desc")
	Page<InwardEntry> findAllWIP(@Param("searchText") String searchText, @Param("partyId") int partyId, Pageable pageable);
    
	@Query("select inw from InwardEntry inw where inw.status.statusId=2 and (inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
			+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId in :partyIds order by inwardEntryId desc")
	Page<InwardEntry> findAllWIP(@Param("searchText") String searchText, @Param("partyIds") List<Integer> partyIds, Pageable pageable);
    
    @Query("select inw from InwardEntry inw where inw.status.statusId=2 and (inw.coilNumber like %:searchText% or "
    		+ " inw.customerBatchId like %:searchText% or inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) "
    		+ " order by inwardEntryId desc")
    Page<InwardEntry> findAllWIP(@Param("searchText") String searchText, Pageable pageable);
    
    @Modifying
	@Transactional
	@Query("update InwardEntry inw set inw.status.statusId=:status where inw.inwardEntryId= :inwardId ")
	public void updateInwardStatus(@Param("inwardId") Integer inwardId, @Param("status") Integer status);
    
    public static final String GET_INWARD_DETAILS = " select min(stts) from ( "
			+ " SELECT distinct inwardentryid as inwardid, child.status as stts, "
			+ " (SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt  "
			+ " FROM product_tblinwardentry parent, product_instruction child  "
			+ " where child.isdeleted=0 and parent.inwardentryid = child.inwardid ) a  where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END and inwardid=:inwardId ";

	@Query(value = GET_INWARD_DETAILS, nativeQuery = true)
	public List<Object[]> getCoilStatus(@Param("inwardId") Integer inwardId);
	
	@Query(value = "SELECT distinct coilnumber `Coil No`,customerbatchid `Batch No`," + 
			" (select desc2.vdescription from product_tblmatdescription desc2 where desc2.nmatid=inward.nmatid) as material_desc," + 
			" (select aa.`gradename` from `product_material_grades` aa where aa.`gradeid` = `inward`.`materialgradeid`) AS `material_grade`," + 
			" ROUND(`fthickness`, 2) as `fthickness`, round(fquantity,2) as `net weight`, " +
			" ROUND(`grossweight`, 2) as `grossweight`, ROUND(`fwidth`, 2) as `fwidth` " +
			" FROM product_tblinwardentry inward WHERE inwardentryid=:inwardId ", nativeQuery = true)
	List<Object[]> getQRCodeDetails(@Param("inwardId") Integer inwardId);

	@Modifying
	@Transactional
	@Query("update InwardEntry set qrcodeS3Url=:url where inwardEntryId= :inwardId ")
	public void updateQRCodeS3InwardPDF(@Param("inwardId") Integer inwardId, @Param("url") String url);

	@Modifying
	@Transactional
	@Query("update InwardEntry set qrcodeEditfinishS3Url=:url where inwardEntryId= :inwardId ")
	public void updateQRCodeEditFinish(@Param("inwardId") Integer inwardId, @Param("url") String url);


}