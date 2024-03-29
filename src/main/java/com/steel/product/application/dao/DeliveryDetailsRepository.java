package com.steel.product.application.dao;

import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.transaction.Transactional;

public interface DeliveryDetailsRepository extends JpaRepository<DeliveryDetails, Integer> {

    @Query(" from Instruction where deliveryId != NULL AND status = 4")
    public List<Instruction> deliveredItems();

    @Query(" from Instruction where deliveryId =:deliveryId")
    public List<Instruction> deliveredItemsById(@Param("deliveryId") int deliveryId);

    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null and "
    		+ " ( inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
    		+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId=:partyId group by inw, dd")
    public Page<DeliveryDetails> findAllDeliveries(@Param("searchText") String searchText, @Param("partyId") int partyId, Pageable pageable);

    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null and "
    		+ " ( inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
    		+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) and inw.party.nPartyId in :partyIds group by inw, dd")
	public Page<DeliveryDetails> findAllDeliveries(@Param("searchText") String searchText,
			@Param("partyIds") List<Integer> partyIds, Pageable pageable);
    
    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null and "
    		+ " ( inw.coilNumber like %:searchText% or inw.customerBatchId like %:searchText% or "
    		+ " inw.customerInvoiceNo like %:searchText% or inw.party.partyName like %:searchText% ) group by inw, dd")
    public Page<DeliveryDetails> findAllDeliveries(@Param("searchText") String searchText, Pageable pageable);

    @Query("select dd from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw where ins.deliveryDetails is not null group by inw, dd")
    public List<DeliveryDetails> findAllDeliveries();
    
    @Query("select ins from Instruction ins left join fetch ins.parentInstruction inner join fetch ins.deliveryDetails dd where dd.deliveryId = :deliveryId")
    List<Instruction> findInstructionsByDeliveryId(@Param("deliveryId")Integer deliveryId);//delete delivery

//    @Query("select sum(ins.actualWeight) from Instruction ins where ins.inwardId.inwardEntryId = :inwardId group by ins.inwardId")
    @Query("select ins.actualWeight from Instruction ins where ins.inwardId.inwardEntryId = :inwardId and instructionId = :instructionId")
    public Float findInstructionByInwardIdAndInstructionId(@Param("inwardId")Integer inwardId, @Param("instructionId")Integer instructionId);

	@Modifying
	@Transactional
	@Query("update DeliveryDetails set pdfS3Url=:url where deliveryId= :deliveryId ")
	public void updateS3DCPDF(@Param("deliveryId") Integer deliveryId, @Param("url") String url);

    @Query("select distinct dd.deliveryId, ins.inwardId.coilNumber from DeliveryDetails dd join dd.instructions ins join ins.inwardId inw "
    		+ " where ins.deliveryDetails is not null and dd.tallyStatus='PENDING' "
    		+ " order by dd.deliveryId desc ")
	public Page<DeliveryDetails> findAllDeliveriesForBillingNew(Pageable pageable);
    
	@Query(value="SELECT DISTINCT dc.deliveryid, inward.coilnumber\r\n" + 
			"FROM\r\n" + 
			"    product_tblinwardentry inward,\r\n" + 
			"    product_tblpartydetails party,\r\n" + 
			"    product_instruction instr,\r\n" + 
			"    product_tbl_delivery_details dc\r\n" + 
			"WHERE\r\n" + 
			"    party.npartyid = inward.npartyid\r\n" + 
			"        AND inward.inwardentryid = instr.inwardid\r\n" + 
			"        AND instr.deliveryid = dc.deliveryid and dc.tally_status='PENDING' order by dc.deliveryid desc", nativeQuery = true)
	public List<Object[]> findAllDeliveriesForBilling(Pageable pageable);

    @Query(value="SELECT DISTINCT instructionid, dc.deliveryid, DATE_FORMAT( dc.createdon, '%d-%m-%Y') ,\r\n " + 
    		"    'DC' AS voucher_type, '' as custcode, partyname,\r\n " + 
    		"    phone1, 'Sundry Debtors' AS Under_Group,\r\n " + 
    		"    (SELECT details FROM  product_address adds WHERE adds.addressid = party.address1 limit 1) as address1, " + 
    		"    concat(' ','', address2),  '' as address3, " + 
    		"    (SELECT city FROM  product_address adds WHERE adds.addressid = party.address1 limit 1) as city, " + 
    		"    (SELECT concat('','', pincode)  FROM  product_address adds WHERE adds.addressid = party.address1 limit 1) as pincode, " + 
    		"    (SELECT state FROM  product_address adds WHERE adds.addressid = party.address1 limit 1) as state, " + 
    		"    '' AS gstno, '' AS Product_NO, " + 
    		"    case when processid=1 then 'CUTTING AND PACKING'\r\n  " + 
    		"    when processid=2 then 'SLITTING AND PACKING'  \r\n" + 
    		"    when processid=3 then 'SLIT AND CUT AND PACKING'\r\n " + 
    		"    when processid=7 then 'HANDLING AND PACKING'  \r\n" + 
    		"    when processid=8 then 'FULL HANDLING AND PACKING'  \r\n" + 
    		"    end as Product_Desc, \r\n" + 
    		"    coilnumber, customerbatchid, \r\n" + 
    		"    (select gradename from product_material_grades  grd where grd.gradeid=inward.materialgradeid) as `Material_Grade`,\r\n" + 
    		"    (select vdescription from product_tblmatdescription mat where mat.nmatid=inward.nmatid) as 'Material_Desc',\r\n" + 
    		"    (select mat.hsn_code from product_tblmatdescription mat where mat.nmatid=inward.nmatid) as 'hsn_code',\r\n " + 
    		"    fthickness,actualwidth, \r\n " + 
    		"    actuallength,'Main location' as godown, 'MT' as uom, actualweight, \r\n" + 
    		"    dc.packing_rate_id, inward.npartyid, instr.processid, inward.materialgradeid, \r\n" + 
    		"    price_details, plannednoofpieces,  \r\n" + 
    		"    (select count(ins.instructionid) from product_instruction ins where ins.inwardid=inward.inwardentryid) as 'noofPlans', part_details_id, \r\n " + 
    		"    (select SUBSTRING(gstn, 1, 2)  from product_company_details where id=1) as companygstin, SUBSTRING(gstnumber, 1, 2) \r\n " + 
    		" FROM product_tblinwardentry inward, " + 
    		"    product_tblpartydetails party, " + 
    		"    product_instruction instr, " + 
    		"    product_tbl_delivery_details dc  \r\n" + 
    		" WHERE party.npartyid = inward.npartyid  " + 
    		"        AND inward.inwardentryid = instr.inwardid \r\n" + 
    		"        AND instr.deliveryid = dc.deliveryid and dc.deliveryid in (:dcIDs) \r\n", nativeQuery = true)
    public List<Object[]> billingInvoiceList(@Param("dcIDs") List<Integer> dcIDs);
    
    @Modifying
   	@Transactional
   	@Query("update DeliveryDetails dc set dc.tallyStatus = 'COMPLETED', dc.tallyDate = CURRENT_TIMESTAMP where dc.deliveryId in (:dcList) ")
   	public void updateTallyStatus(@Param("dcList") List<Integer> dcList);
    
}