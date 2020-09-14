package com.steel.product.application.service;

import com.steel.product.application.entity.Material;
import java.util.List;

public interface MaterialDescriptionService {
  Material saveMatDesc(Material paramMaterial);
  
  List<Material> getAllMatDesc();
  
  Material getMatById(int paramInt);
}
