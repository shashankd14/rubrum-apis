package com.steel.product.application.service;

import com.steel.product.application.dto.TallyBillingInvoiceListDTO;
import com.steel.product.application.dto.delivery.DeliveryDto;
import com.steel.product.application.dto.delivery.DeliveryPacketsDto;
import com.steel.product.application.dto.delivery.TallyUpdateSttsRequestDTO;
import com.steel.product.application.dto.pricemaster.PriceCalculateResponseDTO;
import com.steel.product.application.entity.DeliveryDetails;
import com.steel.product.application.entity.Instruction;

import java.util.List;

import org.springframework.data.domain.Page;

public interface DeliveryDetailsService {

    public List<Instruction> getAll();

    public List<Instruction> getInstructionsByDeliveryId(int deliveryId);

    public DeliveryDetails getById(int theId);

    public DeliveryDetails save(DeliveryDto deliveryDto, int userId);

    public void deleteById(Integer id);

    public List<DeliveryPacketsDto> deliveryList();

    public List<Instruction> findInstructionsByDeliveryId(Integer deliveryId);

    public Float findInstructionByInwardIdAndInstructionId(Integer inwardId,Integer instructionId);

	Page<DeliveryDetails> deliveryListPagination(int pageNo, int pageSize, String searchText, String partyId);

	public Page<DeliveryDetails> billingInvoiceList(int pageNo, int pageSize);

	public List<TallyBillingInvoiceListDTO> billingDCDetails(List<Integer> dcIds);

	public PriceCalculateResponseDTO validatePriceMapping(DeliveryDto deliveryDto, int userId);

	DeliveryDetails updateTallyStatus(TallyUpdateSttsRequestDTO deliveryDto);
}
