package com.steel.product.application.service;

import com.steel.product.application.dao.DeliveryDetailsRepository;
import com.steel.product.application.dto.TallyBillingInvoiceListDTO;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryItemDetails;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.pricemaster.PriceCalculateDTO;
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;
import com.steel.product.application.entity.InwardEntry;
import com.steel.product.application.entity.Status;
import com.steel.product.application.entity.UserPartyMap;
import com.steel.product.application.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryDetailsServiceImpl implements DeliveryDetailsService{

    private final static Logger LOGGER = LoggerFactory.getLogger(DeliveryDetailsServiceImpl.class);

    private DeliveryDetailsRepository deliveryDetailsRepo;

    private InstructionService instructionService;

    private StatusService statusService;

    private InwardEntryService inwardEntryService;

    private PriceMasterService priceMasterService;
    
	private CommonUtil commonUtil;

	@Autowired
	public DeliveryDetailsServiceImpl(DeliveryDetailsRepository deliveryDetailsRepo,
			InstructionService instructionService, StatusService statusService, InwardEntryService inwardEntryService,
			PriceMasterService priceMasterService, CommonUtil commonUtil) {
		this.deliveryDetailsRepo = deliveryDetailsRepo;
		this.instructionService = instructionService;
		this.statusService = statusService;
		this.inwardEntryService = inwardEntryService;
		this.priceMasterService = priceMasterService;
		this.commonUtil = commonUtil;
	}

    @Override
    public List<Instruction> getAll() {
        return deliveryDetailsRepo.deliveredItems();
    }

    @Override
    public List<Instruction> getInstructionsByDeliveryId(int deliveryId) {
        return deliveryDetailsRepo.deliveredItemsById(deliveryId);
    }

    @Override
    public DeliveryDetails getById(int theId) {

        Optional<DeliveryDetails> result = deliveryDetailsRepo.findById(theId);

        DeliveryDetails delivery = null;

        if (result.isPresent()) {
            delivery = result.get();
        }
        else {
            // we didn't find the employee
            throw new RuntimeException("Did not find delivery id - " + theId);
        }

        return delivery;
    }

    @Override
    @Transactional
    public DeliveryDetails save(DeliveryDto deliveryDto, int userId) {
        LOGGER.info("in save delivery api");
        List<DeliveryItemDetails> deliveryItemDetails;
        DeliveryDetails delivery;
        if(deliveryDto.getDeliveryId() != null){
            LOGGER.info("Updating delivery with id "+deliveryDto.getDeliveryId());
            delivery = getById(deliveryDto.getDeliveryId());
            if(deliveryDto.getCustomerInvoiceNo() != null) {
                delivery.setCustomerInvoiceNo(deliveryDto.getCustomerInvoiceNo());
            }
            if(deliveryDto.getCustomerInvoiceDate() != null){
                delivery.setCustomerInvoiceDate(deliveryDto.getCustomerInvoiceDate());
            }
            deliveryDetailsRepo.save(delivery);
            return delivery;
        }
        LOGGER.info("adding new delivery with id");
        delivery = new DeliveryDetails();

        delivery.setCreatedBy(userId);
        delivery.setUpdatedBy(userId);
        delivery.setVehicleNo(deliveryDto.getVehicleNo());
        delivery.setPackingRateId( deliveryDto.getPackingRateId());

        deliveryItemDetails = deliveryDto.getDeliveryItemDetails();

        float inStockWeight = 0f, weightToDeliver = 0f, parentWeight = 0f;
        Integer deliveredStatusId = 4;
        Status deliveredStatus = statusService.getStatusById(deliveredStatusId);
        Integer readyToDeliverStatusId = 3;
        
        List<Integer> statusIdList=new ArrayList<>();
        statusIdList.add(readyToDeliverStatusId);
        statusIdList.add(deliveredStatusId);
        Map<Integer, String> instructionRemarksMap = deliveryItemDetails.stream().filter(d -> d.getRemarks() != null).collect(Collectors.toMap(d -> d.getInstructionId(), d -> d.getRemarks()));
        
        List<Instruction> instructions = instructionService.findAllByInstructionIdInAndStatus(deliveryItemDetails.stream().map(d -> d.getInstructionId()).collect(Collectors.toList()), statusIdList);
        
        instructions.forEach(ins -> ins.setStatus(deliveredStatus));
        instructions.forEach(ins -> ins.setPriceDetails( priceMasterService.calculateInstructionPrice(ins, deliveryDto.getPackingRateId())));
        instructions = instructionService.saveAll(instructions);

        InwardEntry inwardEntry;
        Instruction parentInstruction;
        List<Instruction> groupInstructions = null;
        Set<Instruction> parentGroupInstructions = null;
        Set<Instruction> childrenInstructions;
        Set<InwardEntry> inwardEntryList = new HashSet<>();
        Integer parentGroupId;
        boolean isAnyInstructionNotDelivered = true;
        try {
            for (Instruction instruction : instructions) {
                inwardEntry = instruction.getInwardId();
                parentInstruction = instruction.getParentInstruction();
                if (inwardEntry == null) {
                    inwardEntry = parentInstruction.getInwardId();
                }
                parentGroupId = instruction.getParentGroupId();
                inStockWeight = inwardEntry.getInStockWeight();
                if (parentGroupId != null) {
                    LOGGER.info("instruction has inward id,parentGroupId " + inwardEntry.getInwardEntryId() + " " + parentGroupId);
                    if(parentGroupInstructions == null || !parentGroupInstructions.contains(instruction)) {
                        parentGroupInstructions = new HashSet<>();
                        parentGroupInstructions.addAll(instructionService.findAllByParentGroupId(parentGroupId));
                        LOGGER.info("total parent group instructions "+parentGroupInstructions.size());
                    }
                    isAnyInstructionNotDelivered = parentGroupInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus));
                    if (!isAnyInstructionNotDelivered) {
                        if(groupInstructions == null || (groupInstructions != null && !groupInstructions.isEmpty() &&!groupInstructions.get(0).getGroupId().equals(parentGroupId))) {
                            groupInstructions = instructionService.findAllByGroupId(parentGroupId);
                            groupInstructions.forEach(ins -> ins.setStatus(deliveredStatus));
                        }
                    }
                    weightToDeliver = instruction.getActualWeight();
                } else if (parentInstruction != null) {
                    LOGGER.info("instruction has parent instruction id " + instruction.getParentInstruction().getInstructionId());
                        weightToDeliver = instruction.getActualWeight();
                        childrenInstructions = parentInstruction.getChildInstructions();
                        LOGGER.info("parent instruction has " + childrenInstructions.size() + " children");
                        isAnyInstructionNotDelivered = childrenInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus));
                        if (!isAnyInstructionNotDelivered) {
                            LOGGER.info("setting parent instruction status delivered as all children of parent instruction " + parentInstruction.getInstructionId() + " have status delivered");
                            parentInstruction.setStatus(deliveredStatus);
                        }
                        LOGGER.info("no status change for parent instruction " + parentInstruction.getInstructionId() + " as all children not delivered");

                } else {
                	
                    LOGGER.info("instruction has inward id " + inwardEntry.getInwardEntryId());
                	//parentWeight = inwardEntry.getInStockWeight();
                    if(instruction.getProcess().getProcessId() == 7 ) {
                        weightToDeliver = instruction.getPlannedWeight();
                    } else {
                        weightToDeliver = instruction.getActualWeight();
                    }
                    childrenInstructions = instruction.getChildInstructions();
                    if (childrenInstructions != null && !childrenInstructions.isEmpty()) {
                        LOGGER.info("inward id" + inwardEntry.getInwardEntryId() + " is a parent instruction with " + childrenInstructions.size() + " children");
                        if (childrenInstructions.stream().anyMatch(ins -> !ins.getStatus().equals(deliveredStatus))) {
                            throw new RuntimeException("instruction with id " + instruction.getInstructionId() + " has undelivered children instructions");
                        }

                    }
                }
//                    else {
//                        LOGGER.error("No inward id or parent instruction id found in instruction with id " + instruction.getInstructionId());
//                        throw new RuntimeException("No inward id or parent instruction id found in instruction with id " + instruction.getInstructionId());
//                    }
//                if(weightToDeliver > parentWeight){
//                    LOGGER.error("weight to deliver "+weightToDeliver+" exceeds parent weight "+parentWeight);
//                    throw new RuntimeException("weight to deliver "+weightToDeliver+" exceeds parent weight "+parentWeight);
//                }
                if (weightToDeliver > inStockWeight) {
                    LOGGER.error("weight to deliver " + weightToDeliver + " exceeds in stock weight " + inStockWeight);
                    throw new RuntimeException("weight to deliver " + weightToDeliver + " exceeds in stock weight " + inStockWeight);
                }
                inwardEntry.setInStockWeight(inStockWeight - weightToDeliver);
                if (Math.abs(inwardEntry.getInStockWeight()) < 1f) {
                    inwardEntry.setStatus(deliveredStatus);
                }
                inwardEntryList.add(inwardEntry);
                instruction.setRemarks(instructionRemarksMap.get(instruction.getInstructionId()));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        delivery.addAllInstructions(instructions);
        float totalWeight = deliveryItemDetails.stream().reduce(0f,(sum,d) -> sum + d.getWeight().floatValue(),Float::sum);
        delivery.setTotalWeight(totalWeight);
        LOGGER.info("saving "+inwardEntryList.size()+" inward entries");
        inwardEntryService.saveAll(inwardEntryList);
        LOGGER.info("saving delivery details");
        deliveryDetailsRepo.save(delivery);
        return delivery;
    }

    @Override
    @Transactional
    public void deleteById(Integer deliveryId) {
        LOGGER.info("inside delete delivery api for delivery id "+deliveryId);
        List<Instruction> instructions = this.findInstructionsByDeliveryId(deliveryId);
        DeliveryDetails deliveryDetails = instructions.get(0).getDeliveryDetails();
        LOGGER.info("deleting delivery "+deliveryDetails.getDeliveryId());
        Integer deliveredStatusId = 4,readyToDeliverStatusId = 3;
        Status deliveredStatus = statusService.getStatusById(deliveredStatusId);
        Status readyToDeliverStatus = statusService.getStatusById(readyToDeliverStatusId);

        InwardEntry inwardEntry = null;
        Instruction parentInstruction = null;
        Integer parentGroupId;
        List<Instruction> groupInstructions = null;

        for(Instruction ins:instructions){
            LOGGER.info("deleting delivery for instruction id "+ins.getInstructionId());
            inwardEntry = ins.getInwardId();
            parentInstruction = ins.getParentInstruction();
            if(inwardEntry == null){
                LOGGER.info("setting inward from parent instruction "+parentInstruction.getInstructionId());
                inwardEntry = parentInstruction.getInwardId();
            }
            parentGroupId = ins.getParentGroupId();
            if(inwardEntry != null && parentGroupId != null){
                LOGGER.info("deleting delivery for parent group instruction with id, "+parentGroupId+" instruction id "+ins.getInstructionId());
                if(groupInstructions == null || (groupInstructions != null && !groupInstructions.isEmpty() &&!groupInstructions.get(0).getGroupId().equals(parentGroupId))){
                    LOGGER.info("fetching group instructions for parent group id "+parentGroupId);
                    groupInstructions = instructionService.findAllByGroupId(parentGroupId);
                    LOGGER.info("group instructions size "+groupInstructions.size());
                    if(groupInstructions.get(0).getStatus().equals(deliveredStatus)) {
                        LOGGER.info("setting group instructions status from delivered to readyToDeliver");
                        groupInstructions.forEach(groupIns -> groupIns.setStatus(readyToDeliverStatus));
                    }
                }
            }
            else if(parentInstruction != null){
                LOGGER.info("instruction has parent instruction id "+parentInstruction.getInstructionId());
                if(parentInstruction.getStatus().equals(deliveredStatus)){
                    parentInstruction.setStatus(readyToDeliverStatus);
                }
            }
            else if(inwardEntry != null){
                LOGGER.info("instruction has inward id " + inwardEntry.getInwardEntryId());
                inwardEntry.setStatus(readyToDeliverStatus);
            }
            else{
                LOGGER.error("No inward id or parent instruction id found in instruction with id " + ins.getInstructionId());
                throw new RuntimeException("No inward id or parent instruction id found in instruction with id " + ins.getInstructionId());
            }
            ins.setStatus(readyToDeliverStatus);
            Float inStockWeight = inwardEntry.getInStockWeight();
            inwardEntry.setInStockWeight(inStockWeight + ins.getActualWeight());
            LOGGER.info("adding instruction actual weight "+ins.getActualWeight()+" and setting inward inStock weight from "+inStockWeight+" to "+(inStockWeight + ins.getActualWeight()));
            if(inwardEntry.getStatus().equals(deliveredStatus)){
                LOGGER.info("setting inward " + inwardEntry.getInwardEntryId() + " status to ready to deliver");
                inwardEntry.setStatus(readyToDeliverStatus);
            }
            deliveryDetails.removeInstruction(ins);
        }
        deliveryDetailsRepo.deleteById(deliveryId);
    }

    @Override
    public List<DeliveryPacketsDto> deliveryList() {
        List<DeliveryDetails> inwardEntryList = deliveryDetailsRepo.findAllDeliveries();
        LOGGER.info("Delivery details list size "+inwardEntryList.size());
        return inwardEntryList.stream().map(inw -> new DeliveryPacketsDto(inw)).collect(Collectors.toList());
    }
    
    @Override
    public Page<DeliveryDetails> deliveryListPagination(int pageNo, int pageSize, String searchText, String partyId) {
    	Pageable pageable = PageRequest.of((pageNo-1), pageSize);
    	
		if(partyId!=null && partyId.length()>0) {
	    	Page<DeliveryDetails> deliveryList = deliveryDetailsRepo.findAllDeliveries(searchText, Integer.parseInt(partyId), pageable);
	        LOGGER.info("Delivery details list size "+deliveryList.getSize());
	        return deliveryList;
		} else {
			AdminUserEntity adminUserEntity = commonUtil.getUserDetails();
			if(adminUserEntity.getUserPartyMap()!=null && adminUserEntity.getUserPartyMap().size()>0) {
				List<Integer> partyIds=new ArrayList<>();
				for (UserPartyMap userPartyMap : adminUserEntity.getUserPartyMap()) {
					partyIds.add(userPartyMap.getPartyId());
					LOGGER.info("In partyIds === "+partyIds);
				}
				Page<DeliveryDetails> deliveryList = deliveryDetailsRepo.findAllDeliveries(searchText, partyIds, pageable);
				return deliveryList;
			} else {
				Page<DeliveryDetails> deliveryList = deliveryDetailsRepo.findAllDeliveries(searchText, pageable);
		        LOGGER.info("Delivery details list size "+deliveryList.getSize());
		        return deliveryList;
			}
		}
    }

    @Override
    public List<Instruction> findInstructionsByDeliveryId(Integer deliveryId) {
        return deliveryDetailsRepo.findInstructionsByDeliveryId(deliveryId);
    }

    @Override
    public Float findInstructionByInwardIdAndInstructionId(Integer inwardId,Integer instructionId) {
        return deliveryDetailsRepo.findInstructionByInwardIdAndInstructionId(inwardId,instructionId);
    }

	@Override
	public Page<DeliveryDetails> billingInvoiceList(int pageNo, int pageSize) {
		if (pageSize > 10) {
			pageSize = 10;
		}
		Pageable pageable = PageRequest.of((pageNo - 1), pageSize);
		List<Object[]> kk = deliveryDetailsRepo.findAllDeliveriesForBilling(pageable);
		List<DeliveryDetails> billingInvoiceList = new ArrayList<>();

		for (Object[] objs : kk) {
			DeliveryDetails invoiceListDTO = new DeliveryDetails();
			invoiceListDTO.setDeliveryId( objs[0] != null ? (Integer) objs[0] : null);
			billingInvoiceList.add(invoiceListDTO);
		}
		
		Page<DeliveryDetails> page = new PageImpl<>(billingInvoiceList);
		return page;
	}

	@Override
	public List<TallyBillingInvoiceListDTO> billingDCDetails(List<Integer> dcIds) {
		List<TallyBillingInvoiceListDTO> billingInvoiceList = new ArrayList<>();

		List<Object[]> results = deliveryDetailsRepo.billingInvoiceList(dcIds);
		for (Object[] objs : results) {
			TallyBillingInvoiceListDTO invoiceListDTO = new TallyBillingInvoiceListDTO();
			invoiceListDTO.setVoucherRef(objs[0] != null ? (Integer) objs[0] : null);
			invoiceListDTO.setDcNo(objs[1] != null ? (Integer) objs[1] : null);
			invoiceListDTO.setDcDate(objs[2] != null ? (String) objs[2] : null);
			invoiceListDTO.setVoucherType(objs[3] != null ? (String) objs[3] : null);
			invoiceListDTO.setCustomerCode(objs[4] != null ? (String) objs[4] : null);
			invoiceListDTO.setCustomerName(objs[5] != null ? (String) objs[5] : null);
			invoiceListDTO.setCustomerMobileNo(objs[6] != null ? (String) objs[6] : null);
			invoiceListDTO.setUnderGroup(objs[7] != null ? (String) objs[7] : null);
			invoiceListDTO.setAddress1("" + objs[8] != null ? (String) objs[8] : null);
			invoiceListDTO.setAddress2("" + objs[9] != null ? (String) objs[9] : null);
			invoiceListDTO.setAddress3("" + objs[10] != null ? (String) objs[10] : null);
			invoiceListDTO.setCity("" + objs[11] != null ? (String) objs[11] : null);
			invoiceListDTO.setPincode(objs[12] != null ? (String) objs[12] : null);
			invoiceListDTO.setState(objs[13] != null ? (String) objs[13] : null);
			invoiceListDTO.setGstno(objs[14] != null ? (String) objs[14] : null);
			invoiceListDTO.setProductNo(objs[15] != null ? (String) objs[15] : null);
			invoiceListDTO.setProductDesc(objs[16] != null ? (String) objs[16] : null);
			invoiceListDTO.setCoilNo(objs[17] != null ? (String) objs[17] : null);
			invoiceListDTO.setCustomerBatchNo(objs[18] != null ? (String) objs[18] : null);
			invoiceListDTO.setMaterialGrade(objs[19] != null ? (String) objs[19] : null);
			invoiceListDTO.setMaterialDesc(objs[20] != null ? (String) objs[20] : null);
			invoiceListDTO.setHsnCode(objs[21] != null ? (String) objs[21] : null);
			Float fThickness = objs[22] != null ? (Float) objs[22] : null;
			Float actualWidth = objs[23] != null ? (Float) objs[23] : null;
			Float actualLength = objs[24] != null ? (Float) objs[24] : null;
			Float actualWeight = objs[27] != null ? (Float) objs[27] : null;
			invoiceListDTO.setThickness(fThickness);
			invoiceListDTO.setWidth(actualWidth);
			invoiceListDTO.setLength(actualLength);
			invoiceListDTO.setGodown(objs[25] != null ? (String) objs[25] : null);
			invoiceListDTO.setUom(objs[26] != null ? (String) objs[26] : null);
			invoiceListDTO.setQuantity(actualWeight);

			Integer packingRateId = objs[28] != null ? (Integer) objs[28] : 0;
			Integer partyId = objs[29] != null ? (Integer) objs[29] : 0;
			Integer processId = objs[30] != null ? (Integer) objs[30] : 0;
			Integer materialGradeId = objs[31] != null ? (Integer) objs[31] : 0;
			String priceDetails = objs[32] != null ? (String) objs[32] : "";
			Integer plannedNoOfPieces = objs[33] != null ? (Integer) objs[33] : 0;
			Integer noofPlans = objs[34] != null ? ((BigInteger) objs[34]).intValue() : 0;
			Integer partDetailsId = objs[35] != null ? (Integer) objs[35] : 0;
			String companyGSTIN = objs[36] != null ? (String) objs[36] : "29";
			String partyGSTIN = objs[37] != null ? (String) objs[37] : "29";

			PriceCalculateDTO priceCalculateDTO = priceMasterService.calculateInstructionWisePrice(partyId,
					BigDecimal.valueOf(fThickness), processId, materialGradeId, packingRateId,
					invoiceListDTO.getQuantity(), actualLength, plannedNoOfPieces, noofPlans, partDetailsId.longValue());

			BigDecimal amount =new BigDecimal("0.00");
			
			if(priceCalculateDTO!=null && priceCalculateDTO.getBasePrice() !=null) {
				invoiceListDTO.setBasePrice( priceCalculateDTO.getBasePrice());
				amount=amount.add(invoiceListDTO.getBasePrice());
			}
			if(priceCalculateDTO!=null && priceCalculateDTO.getAdditionalPrice() != null) {
				invoiceListDTO.setAdditionalPrice( priceCalculateDTO.getAdditionalPrice());
				amount=amount.add(invoiceListDTO.getAdditionalPrice());
			}
			if(priceCalculateDTO!=null && priceCalculateDTO.getPackingPrice() != null) {
				invoiceListDTO.setPackingRate(  priceCalculateDTO.getPackingPrice());
				amount=amount.add(invoiceListDTO.getPackingRate());
			}
			BigDecimal totalAmount = new BigDecimal(BigInteger.ZERO, 3);
			if(amount!=null ) {
				amount = amount.setScale(3, RoundingMode.HALF_EVEN); 
				invoiceListDTO.setRate(amount);
				totalAmount = (amount.multiply(BigDecimal.valueOf(actualWeight)));
				totalAmount = totalAmount.divide(BigDecimal.valueOf(1000));
				invoiceListDTO.setAmount(totalAmount);
				invoiceListDTO.setTotal( totalAmount);
			}
			invoiceListDTO.setGstPercentage(new BigDecimal("12.00"));
			BigDecimal grandTotal = new BigDecimal(BigInteger.ZERO,  2);

			if(companyGSTIN.equals(partyGSTIN)) {
				BigDecimal cgst = totalAmount.multiply(new BigDecimal("6.00")).divide(new BigDecimal("100.00"));
				BigDecimal sgst = totalAmount.multiply(new BigDecimal("6.00")).divide(new BigDecimal("100.00"));

				invoiceListDTO.setCgst(cgst.setScale(3, RoundingMode.HALF_EVEN)); 
				invoiceListDTO.setSgst(sgst.setScale(3, RoundingMode.HALF_EVEN)); 
				grandTotal=grandTotal.add(totalAmount).add(invoiceListDTO.getCgst()).add(invoiceListDTO.getSgst());
			} else {
				BigDecimal igst = totalAmount.multiply(new BigDecimal("12.00")).divide(new BigDecimal("100.00"));
				invoiceListDTO.setIgst(igst.setScale(3, RoundingMode.HALF_EVEN)); 
				grandTotal=grandTotal.add(totalAmount).add(invoiceListDTO.getIgst());
			}
			grandTotal=grandTotal.setScale(3, RoundingMode.HALF_EVEN);
			invoiceListDTO.setTotal( grandTotal);
			/*invoiceListDTO.setLedger1(objs[34] != null ? (String) objs[34] : null);
			invoiceListDTO.setLedger2(objs[35] != null ? (String) objs[35] : null);
			invoiceListDTO.setLedger3(objs[36] != null ? (String) objs[36] : null);
			invoiceListDTO.setLedger4(objs[37] != null ? (String) objs[37] : null);
			invoiceListDTO.setLedger5(objs[38] != null ? (String) objs[38] : null);
			invoiceListDTO.setLedger6(objs[39] != null ? (String) objs[39] : null);
			invoiceListDTO.setLedger7(objs[40] != null ? (String) objs[40] : null);
			invoiceListDTO.setRoundOff(objs[41] != null ? (String) objs[41] : null);*/
			invoiceListDTO.setRemarks("");
			billingInvoiceList.add(invoiceListDTO);
		}
		return billingInvoiceList;
	}
	
	@Override
    public PriceCalculateResponseDTO validatePriceMapping(DeliveryDto deliveryDto, int userId) {
        LOGGER.info("in validatePriceMapping delivery api");
        List<DeliveryItemDetails> deliveryItemDetails = deliveryDto.getDeliveryItemDetails();
    	boolean mainStts = false;
    	PriceCalculateResponseDTO priceCalculateResponseDTO= new PriceCalculateResponseDTO();
        List<PriceCalculateDTO> priceDetailsList=new ArrayList<>();
        List<Integer> statusIdList=new ArrayList<>();
        statusIdList.add(4);statusIdList.add(3);
        
        List<Instruction> instructions = instructionService.findAllByInstructionIdInAndStatus(deliveryItemDetails.stream().map(d -> d.getInstructionId()).collect(Collectors.toList()), statusIdList);
        
		for (Instruction instruction : instructions) {
			boolean innerStts = false;
			InwardEntry inwardEntry = instruction.getInwardId();
			
			/*PriceCalculateDTO priceCalculateDTO = priceMasterService.calculateInstructionWisePrice(
					inwardEntry.getParty().getnPartyId(), BigDecimal.valueOf(inwardEntry.getfThickness()),
					instruction.getProcess().getProcessId(), inwardEntry.getMaterialGrade().getGradeId(),
					deliveryDto.getPackingRateId(), instruction.getActualWeight(), instruction.getActualLength(),
					instruction.getPlannedNoOfPieces(), inwardEntry.getInstructions().size(),
					instruction.getPartDetails().getId());*/
			
			PriceCalculateDTO priceCalculateDTO = priceMasterService.calculateInstructionWisePrice(instruction, deliveryDto.getPackingRateId());

			priceCalculateDTO.setCoilNo(inwardEntry.getCoilNumber());
			priceCalculateDTO.setCustomerBatchNo(inwardEntry.getCustomerBatchId());
			priceCalculateDTO.setInstructionId(instruction.getInstructionId());
			priceCalculateDTO.setThickness(BigDecimal.valueOf(inwardEntry.getfThickness()));
			priceCalculateDTO.setMatGradeName(inwardEntry.getMaterialGrade().getGradeName());
			priceCalculateDTO.setActualWeight(instruction.getActualWeight());

			BigDecimal amount =new BigDecimal("0.00");
			
			if(priceCalculateDTO!=null && priceCalculateDTO.getBasePrice() !=null) {
				amount=amount.add(priceCalculateDTO.getBasePrice());
			}
			if(priceCalculateDTO!=null && priceCalculateDTO.getAdditionalPrice() != null) {
				amount=amount.add(priceCalculateDTO.getAdditionalPrice());
			}
			if(priceCalculateDTO!=null && priceCalculateDTO.getPackingPrice() != null) {
				amount=amount.add(priceCalculateDTO.getPackingPrice() );
			}
			if(amount!=null ) {
				priceCalculateDTO.setRate(amount);
				BigDecimal totalAmount = new BigDecimal(BigInteger.ZERO,  2);
				totalAmount = (amount.multiply(BigDecimal.valueOf(priceCalculateDTO.getActualWeight())));
				totalAmount = totalAmount.divide(BigDecimal.valueOf(1000));
				priceCalculateDTO.setTotalPrice(totalAmount);
			}
			if (priceCalculateDTO.getBasePrice() != null && priceCalculateDTO.getBasePrice().compareTo(BigDecimal.ZERO) > 0) {
				if (priceCalculateDTO.getPackingPrice() != null && priceCalculateDTO.getPackingPrice().compareTo(BigDecimal.ZERO) > 0) {
					innerStts = true;
				}
			}
			priceDetailsList.add(priceCalculateDTO);
			mainStts = innerStts;
		}
        
        priceCalculateResponseDTO.setValidationStatus(mainStts);
        if(priceCalculateResponseDTO.isValidationStatus()) {
            priceCalculateResponseDTO.setRemarks("Thickness range found for all selected packets");
        } else {
            priceCalculateResponseDTO.setRemarks("This thickness range already has a value (rate) defined. Please recheck.");
        }
        priceCalculateResponseDTO.setPriceDetailsList(priceDetailsList);
       
        return priceCalculateResponseDTO;
    }

	
	
}
