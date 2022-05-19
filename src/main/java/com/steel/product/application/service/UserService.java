package com.steel.product.application.service;

import com.steel.product.application.entity.UserEntity;
import java.util.List;

public interface UserService {
  UserEntity saveStatus(UserEntity paramUser);
  
  List<UserEntity> getAllUsers();
  
  UserEntity getUserById(int paramInt);
}
