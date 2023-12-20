package com.steel.product.application.dto.delivery;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DeliveryPDFRequestDTO {

	private List<Integer> dcIds = new ArrayList<>();
}
