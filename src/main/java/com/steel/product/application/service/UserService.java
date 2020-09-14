package com.steel.product.application.service;

import com.steel.product.application.entity.User;
import java.util.List;

public interface UserService {
  User saveStatus(User paramUser);
  
  List<User> getAllUsers();
  
  User getUserById(int paramInt);
}
