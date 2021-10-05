CREATE TABLE IF NOT EXISTS `PRODUCT_COMPANY_DETAILS`
(
    `id`                int(11) NOT NULL AUTO_INCREMENT,
    `company_name`      varchar(255) DEFAULT NULL,
    `createdby`         int          DEFAULT NULL,
    `createdon`         datetime(6)  DEFAULT NULL,
    `email`             varchar(255) DEFAULT NULL,
    `gstn`              varchar(255) DEFAULT NULL,
    `updatedby`         int          DEFAULT NULL,
    `updatedon`         datetime(6)  DEFAULT NULL,
    `address_branch_id` int          DEFAULT NULL,
    `address_office_id` int          DEFAULT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`address_branch_id`) REFERENCES `product_address` (`addressid`),
    FOREIGN KEY (`address_office_id`) REFERENCES `product_address` (`addressid`)
);

# INSERT INTO ASPEN.PRODUCT_ADDRESS (details, city, state, pincode)
# values ('101,MG road', 'Bangalore', 'Karnataka', '560019');
# INSERT INTO ASPEN.PRODUCT_ADDRESS (details, city, state, pincode)
# values ('234, Whitefield', 'Bangalore', 'Karnataka', '560020');
# INSERT INTO ASPEN.PRODUCT_COMPANY_DETAILS (id, company_name, createdby, createdon, email, gstn, updatedby, updatedon,
#                                            address_branch_id, address_office_id)
# values (1, 'Aspen Steel', 1, null, 'gautam-aggarwal@gmail.com', '29AABCU9603R1ZJ', 1, null, 47, 48);

# ALTER TABLE ASPEN.product_tblinwardentry ADD COLUMN `in_stock_weight` float;
# ALTER TABLE ASPEN.product_instruction ADD COLUMN `is_slit_and_cut` boolean;

CREATE TABLE IF NOT EXISTS `PRODUCT_INSTRUCTION_PLAN`
(
    `ID`                  INT(11)      NOT NULL AUTO_INCREMENT,
    `INSTRUCTION_PLAN_ID` VARCHAR(255) NOT NULL,
    `TARGET_WEIGHT`       float        NOT NULL,
    `NO_OF_PARTS`         int          NOT NULL,
    `IS_EQUAL`            boolean      NOT NULL DEFAULT FALSE,
    `PROCESS_ID`          int          NOT NULL,
    `CREATED_BY`          int                   DEFAULT NULL,
    `UPDATED_BY`          int                   DEFAULT NULL,
    `CREATED_ON`          datetime(6),
    `UPDATED_ON`          datetime(6),
    `IS_DELETED`          boolean               DEFAULT FALSE,
    PRIMARY KEY (`ID`),
    FOREIGN KEY (`PROCESS_ID`) REFERENCES aspen.`product_process` (`processid`)
);


# ALTER TABLE ASPEN.product_instruction ADD COLUMN `instruction_plan_id` INT(11);
# ALTER TABLE ASPEN.product_instruction ADD CONSTRAINT `INSTRUCTION_PLAN_ID` FOREIGN KEY (`instruction_plan_id`) REFERENCES PRODUCT_INSTRUCTION_PLAN(`ID`);