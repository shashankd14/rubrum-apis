package com.steel.product.application.dto.pricemaster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PriceCalculateResponseDTO {

	private boolean validationStatus;

	private String remarks;

	private List<PriceCalculateDTO> priceDetailsList =new ArrayList<>();

	/** Coil wise total no of packets and total weight in the delivery */
	private List<CoilWisePacketSummaryDTO> coilWiseSummaryList = new ArrayList<>();

	/** Total no of packets in the delivery */
	private Integer totalNoOfPackets = 0;

	/** Total weight of the delivery */
	private BigDecimal totalWeight = BigDecimal.ZERO;
}
