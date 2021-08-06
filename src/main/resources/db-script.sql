CREATE TABLE `product_company_details` (
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
  KEY `FKl7ak3phr8s5g6xqmgcfdkv05i` (`address_branch_id`),
  KEY `FKalfdagyluj7p10q63op6sdkow` (`address_office_id`),
  CONSTRAINT `FKalfdagyluj7p10q63op6sdkow` FOREIGN KEY (`address_office_id`) REFERENCES `product_address` (`addressId`),
  CONSTRAINT `FKl7ak3phr8s5g6xqmgcfdkv05i` FOREIGN KEY (`address_branch_id`) REFERENCES `product_address` (`addressId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('101,MG road','Bangalore','Karnataka','560019');
INSERT INTO ASPEN.PRODUCT_ADDRESS (details,city,state,pincode) values ('234','Whitefield','Bangalore','Karnataka','560020');