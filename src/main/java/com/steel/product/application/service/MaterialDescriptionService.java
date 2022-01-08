package com.steel.product.application.service;

import com.steel.product.application.dto.material.MaterialRequestDto;
import com.steel.product.application.dto.material.MaterialResponseDetailsDto;
import com.steel.product.application.entity.Material;
import java.util.List;

public interface MaterialDescriptionService {
  Material saveMatDesc(MaterialRequestDto materialRequestDto);
  
  List<MaterialResponseDetailsDto> getAllMatDesc();
  
  Material getMatById(int paramInt);

  Material findByDesc(String desc);
}
