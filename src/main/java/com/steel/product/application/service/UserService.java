package com.steel.product.application.service;

import com.steel.product.application.entity.AdminUserEntity;
import java.util.List;

public interface UserService {
  AdminUserEntity saveStatus(AdminUserEntity paramUser);
  
  List<AdminUserEntity> getAllUsers();
  
  AdminUserEntity getUserById(int paramInt);
}
