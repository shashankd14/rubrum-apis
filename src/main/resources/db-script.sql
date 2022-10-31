
INSERT INTO users(userid, user_name, `password`, email_id, role_id, enabled) VALUES(1, 'testuser', '$2a$10$WQdZcCN7rE8hauAhm00sC.gHhT/zKMGfTgy8IeX1kVVGXjFeUiMry', 'techurate@gmail.com', 'admin', 1);
commit;

#pwd : admin@123

# CREATE TABLE IF NOT EXISTS `product_company_details` (
#   `id` int NOT NULL AUTO_INCREMENT,
#   `company_name` varchar(255) DEFAULT NULL,
#   `createdby` int DEFAULT NULL,
#   `createdon` datetime(6) DEFAULT NULL,
#   `email` varchar(255) DEFAULT NULL,
#   `gstn` varchar(255) DEFAULT NULL,
#   `updatedby` int DEFAULT NULL,
#   `updatedon` datetime(6) DEFAULT NULL,
#   `address_branch_id` int DEFAULT NULL,
#   `address_office_id` int DEFAULT NULL,
#   PRIMARY KEY (`id`),
#   FOREIGN KEY (`address_branch_id`) REFERENCES `product_address` (`addressid`),
#   FOREIGN KEY (`address_office_id`) REFERENCES `product_address` (`addressid`)
# );

# INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('101,MG road','Bangalore','Karnataka','560019');
# INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('234, Whitefield','Bangalore','Karnataka','560020');
# INSERT INTO ASPEN.PRODUCT_COMPANY_DETAILS (id,company_name,createdby,createdon,email,gstn,updatedby,updatedon,address_branch_id,address_office_id)
# values (1,'Aspen Steel',1,null,'gautam-aggarwal@gmail.com','29AABCU9603R1ZJ',1,null,3,4);

# INSERT INTO aspen.product_company_details (id,company_name,createdby,createdon,email,gstn,updatedby,updatedon)
# values (1,'Aspen Steel',1,null,'gautam-aggarwal@gmail.com','29AABCU9603R1ZJ',1,null);
# UPDATE aspen.product_company_details SET address_branch_id = 3,address_office_id = 4 WHERE id = 1;

CREATE TABLE IF NOT EXISTS `product_part_details`
(
    `id`              INT(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `part_details_id` VARCHAR(255) NOT NULL,
    `target_weight`   FLOAT        ,
    `length`          FLOAT        ,
    `created_by`      int         DEFAULT NULL,
    `created_on`      datetime(6) DEFAULT NULL,
    `updated_by`      int         DEFAULT NULL,
    `updated_on`      datetime(6) DEFAULT NULL,
    `is_deleted`      BIT         DEFAULT 0
);

--ALTER TABLE aspen.product_instruction
--    ADD COLUMN `part_details_id` INT(11),
--    ADD FOREIGN KEY `part_details_id` (part_details_id) REFERENCES aspen.`product_part_details` (id);

ALTER TABLE aspen.product_tblinwardentry ADD COLUMN `available_length` FLOAT;






=======================Report Query =======================

1. STOCK REPORT:
			
		CREATE view stock_report_vw AS

			select ROW_NUMBER() OVER(ORDER BY coilnumber) AS id, coilnumber as coilNumber,customerbatchid as customerBatchId, 
			material_desc as material_desc, material_grade as material_grade,
			 round(fthickness,2) as fthickness,round(fwidth,2) fwidth,round(flength,2) flength,
			 round(grossweight,2) as net_weight, 
             inward_status inward_status,round(in_stock_weight,2) in_stock_weight,round(Unprocessed_Weight,2) unprocessed_weight,
             npartyid party_id
			 from
			(
			select inwardid, coilnumber,customerbatchid, material_desc, material_grade,
			 fthickness,fwidth,flength,grossweight, Unprocessed_Weight,in_stock_weight,inward_status,
			 packet_id, thickness, plannedwidth,plannedlength, plannedweight, process_status,instruction_status,classification_tag,
			 enduser_tag_name, actualweight,WIP_Weight, Ready_to_weight,FG_Weight, siltcutcnt,npartyid
			 from
			(
            
            select * from (
            
            SELECT
				 inwardid, '' coilnumber,'' customerbatchid,'' material_desc,'' material_grade,
				 '' fthickness,'' fwidth,'' flength,'' grossweight,'' as Unprocessed_Weight,'' in_stock_weight,'' inward_status,
				 instructionid packet_id, '' thickness, plannedwidth, plannedlength, plannedweight,
				(select processname from product_process where processid=child.processid) as process_status ,
                (select statusname from product_status where statusid=child.status) as instruction_status,
				(select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag,
				'' enduser_tag_name, actualweight,               
                '' as  WIP_Weight, '' as  Ready_to_weight,'' as FG_Weight, parentgroupid,
				(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt ,
                npartyid

			FROM
			   product_tblinwardentry parent, product_instruction child 
               where child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status!=4 ) a  where 1=1 AND
					CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END
			   
				union
			   
			   SELECT
				inwardentryid, coilnumber, customerbatchid,
				(select vdescription from product_tblmatdescription where nmatid=inwrd.nmatid) as  material_desc,
				(select gradename from product_material_grades where gradeid=inwrd.materialgradeid) as  material_grade,
				fthickness,fwidth,flength,grossweight, fpresent as Unprocessed_Weight,in_stock_weight,
				(select statusname from product_status where statusid=vstatus) as inward_status,
				'' packet_id,
				'' thickness, '' plannedwidth, '' plannedlength, '' plannedweight,'' process_status,
                '' as instruction_status,
				'' as classification_tag , '' enduser_tag_name,'' actualweight,
                
				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status=2 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  WIP_Weight,

				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0  and parent.inwardentryid = child.inwardid and child.status=3 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  Ready_to_weight,
				 (SELECT sum(actualweight) FROM product_instruction inss where inss.inwardid=inwrd.inwardentryid and status!=4) as FG_Weight, 

                0 as siltcutcnt,null parentgroupid, npartyid

			FROM
				product_tblinwardentry inwrd where vstatus!=4 
				) a  order by inwardid desc, packet_id asc

			) b where 1=1;
			
2. FG REPORT:


		CREATE view fg_report_vw AS

		 select ROW_NUMBER() OVER(ORDER BY coilnumber) AS id, coilnumber,customerbatchid, material_desc, material_grade,
			 round(fthickness,2) fthickness, round(fwidth,2) as fwidth, round(flength,2) flength,             
              round(FG_Weight,2) fg_weight,
			 packet_id,  round(thickness,2) as thickness,  round(plannedwidth,2) planned_width, 
             round(plannedlength,2) planned_length,  round(plannedweight,2) planned_weight, 
              round(actualweight,2) actual_weight, process_status,instruction_status,classification_tag,
			 enduser_tag_name,npartyid as party_id from
			(
			select inwardid, coilnumber,customerbatchid, material_desc, material_grade,
			 fthickness,fwidth,flength,grossweight, Unprocessed_Weight,in_stock_weight,inward_status,
			 packet_id, thickness, plannedwidth,plannedlength, plannedweight, process_status,instruction_status,classification_tag,
			 enduser_tag_name, actualweight,WIP_Weight, Ready_to_weight,FG_Weight, siltcutcnt,npartyid
			 from
			(
            
            select * from (
            
            SELECT
				 inwardid, '' coilnumber,'' customerbatchid,'' material_desc,'' material_grade,
				 '' fthickness,'' fwidth,'' flength,'' grossweight,'' as Unprocessed_Weight,'' in_stock_weight,'' inward_status,
				 instructionid packet_id, '' thickness, plannedwidth, plannedlength, plannedweight,
				(select processname from product_process where processid=child.processid) as process_status ,
                (select statusname from product_status where statusid=child.status) as instruction_status,
				(select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag,
				(select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,	
				actualweight,               
                '' as  WIP_Weight, '' as  Ready_to_weight,'' as FG_Weight, parentgroupid,
				(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt,
				npartyid

			FROM
			   product_tblinwardentry parent, product_instruction child 
               where child.status=3 and 
               child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status!=4 ) a  where 1=1 AND
					CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END
			   
				union
			   
			   SELECT
				inwardentryid, coilnumber, customerbatchid,
				(select vdescription from product_tblmatdescription where nmatid=inwrd.nmatid) as  material_desc,
				(select gradename from product_material_grades where gradeid=inwrd.materialgradeid) as  material_grade,
				fthickness,fwidth,flength,grossweight, fpresent as Unprocessed_Weight,in_stock_weight,
				(select statusname from product_status where statusid=vstatus) as inward_status,
				'' packet_id,
				'' thickness, '' plannedwidth, '' plannedlength, '' plannedweight,'' process_status,
                '' as instruction_status,
				'' as classification_tag , '' enduser_tag_name,'' actualweight,
                
				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status=2 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  WIP_Weight,

				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0  and parent.inwardentryid = child.inwardid and child.status=3 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  Ready_to_weight,
				 (SELECT sum(actualweight) FROM product_instruction inss where inss.inwardid=inwrd.inwardentryid and status!=4) as FG_Weight, 

                0 as siltcutcnt,null parentgroupid,npartyid

			FROM
				product_tblinwardentry inwrd where 
                
                vstatus=3 
				) a  order by inwardid desc, packet_id asc

			) b where 1=1;


3. WIP REPORT: (IN-PROGRESS)


		CREATE view wip_report_vw AS

		 select ROW_NUMBER() OVER(ORDER BY coilnumber) AS id, coilnumber,customerbatchid, material_desc, material_grade,
			  round(fthickness,2) fthickness, round(fwidth,2) fwidth, round(flength,2) flength, 
			  round(grossweight,2) as net_weight,
            round(in_stock_weight,2) in_stock_weight, round(WIP_Weight,2) as wip_weight,
             round(thickness,2) thickness,  round(plannedwidth,2) planned_width
			 , round(plannedlength,2) planned_length,  round(plannedweight,2) planned_weight, inward_status,
			 classification_tag, enduser_tag_name, npartyid as party_id
			 from
			(
			select inwardid, coilnumber,customerbatchid, material_desc, material_grade,
			 fthickness,fwidth,flength,grossweight, Unprocessed_Weight,in_stock_weight,inward_status,
			 packet_id, thickness, plannedwidth,plannedlength, plannedweight, process_status,instruction_status,classification_tag,
			 enduser_tag_name, actualweight,WIP_Weight, Ready_to_weight,FG_Weight, siltcutcnt,npartyid
			 from
			(
            
            select * from (
            
            SELECT
				 inwardid, '' coilnumber,'' customerbatchid,'' material_desc,'' material_grade,
				 '' fthickness,'' fwidth,'' flength,'' grossweight,'' as Unprocessed_Weight,'' in_stock_weight,'' inward_status,
				 instructionid packet_id, '' thickness, plannedwidth, plannedlength, plannedweight,
				(select processname from product_process where processid=child.processid) as process_status ,
                (select statusname from product_status where statusid=child.status) as instruction_status,
				(select classification_name from product_packet_classification where classification_id=child.packet_classification_id) as classification_tag,
				(select tag_name from product_enduser_tags where tag_id=child.enduser_tag_id) as enduser_tag_name,				
				actualweight,               
                '' as  WIP_Weight, '' as  Ready_to_weight,'' as FG_Weight, parentgroupid,
				(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt,
				npartyid				

			FROM
			   product_tblinwardentry parent, product_instruction child 
               where child.status=2 and 
               child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status!=4 ) a  where 1=1 AND
					CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END
			   
				union
			   
			   SELECT
				inwardentryid, coilnumber, customerbatchid,
				(select vdescription from product_tblmatdescription where nmatid=inwrd.nmatid) as  material_desc,
				(select gradename from product_material_grades where gradeid=inwrd.materialgradeid) as  material_grade,
				fthickness,fwidth,flength,grossweight, fpresent as Unprocessed_Weight,in_stock_weight,
				(select statusname from product_status where statusid=vstatus) as inward_status,
				'' packet_id,
				'' thickness, '' plannedwidth, '' plannedlength, '' plannedweight,'' process_status,
                '' as instruction_status,
				'' as classification_tag , '' enduser_tag_name,'' actualweight,
                
				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0 and parent.inwardentryid = child.inwardid and child.status=2 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  WIP_Weight,

				(select sum(plannedweight) from (            
				SELECT plannedweight,(SELECT count(distinct inss.instructionid) cnt FROM product_instruction inss where inss.inwardid=parent.inwardentryid  and status!=4 and inss.parentgroupid=child.groupid) as siltcutcnt 
					FROM product_tblinwardentry parent, product_instruction child 
					where child.isdeleted=0  and parent.inwardentryid = child.inwardid and child.status=3 and parent.coilnumber=inwrd.coilnumber) a  
					where 1=1 AND CASE WHEN siltcutcnt >0 THEN 1=2 ELSE 1=1 END  ) as  Ready_to_weight,
				 (SELECT sum(actualweight) FROM product_instruction inss where inss.inwardid=inwrd.inwardentryid and status!=4) as FG_Weight, 

                0 as siltcutcnt,null parentgroupid,npartyid

			FROM
				product_tblinwardentry inwrd where                
                vstatus=2 
				) a  order by inwardid desc, packet_id asc

			) b where 1=1;
			
			
			
			