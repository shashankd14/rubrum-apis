CREATE TABLE IF NOT EXISTS `product_company_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) DEFAULT NULL,
  `createdby` int DEFAULT NULL,
  `createdon` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gstn` varchar(255) DEFAULT NULL,
  `updatedby` int DEFAULT NULL,
  `updatedon` datetime(6) DEFAULT NULL,
  `address_branch_id` int DEFAULT NULL,
  `address_office_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`address_branch_id`) REFERENCES `product_address` (`addressid`),
  FOREIGN KEY (`address_office_id`) REFERENCES `product_address` (`addressid`)
);

# INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('101,MG road','Bangalore','Karnataka','560019');
# INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('234, Whitefield','Bangalore','Karnataka','560020');
# INSERT INTO ASPEN.PRODUCT_COMPANY_DETAILS (id,company_name,createdby,createdon,email,gstn,updatedby,updatedon,address_branch_id,address_office_id)
# values (1,'Aspen Steel',1,null,'gautam-aggarwal@gmail.com','29AABCU9603R1ZJ',1,null,3,4);

INSERT INTO aspen.product_company_details (id,company_name,createdby,createdon,email,gstn,updatedby,updatedon)
values (1,'Aspen Steel',1,null,'gautam-aggarwal@gmail.com','29AABCU9603R1ZJ',1,null);
UPDATE aspen.product_company_details SET address_branch_id = 3,address_office_id = 4 WHERE id = 1;