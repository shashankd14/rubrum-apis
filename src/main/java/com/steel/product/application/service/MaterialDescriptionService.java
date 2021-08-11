package com.steel.product.application.service;

import com.steel.product.application.dto.material.MaterialInputDto;
import com.steel.product.application.dto.material.MaterialResponseDto;
import com.steel.product.application.entity.Material;
import java.util.List;

public interface MaterialDescriptionService {
  Material saveMatDesc(MaterialInputDto materialInputDto);
  
  List<MaterialResponseDto> getAllMatDesc();
  
  Material getMatById(int paramInt);
}
