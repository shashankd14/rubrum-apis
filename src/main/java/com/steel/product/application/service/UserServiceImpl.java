package com.steel.product.application.service;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public AdminUserEntity saveStatus(AdminUserEntity user) {
        return (AdminUserEntity) this.userRepo.save(user);
    }

    public List<AdminUserEntity> getAllUsers() {
        return (List<AdminUserEntity>) this.userRepo.findAll();
    }

    public AdminUserEntity getUserById(int id) {
    Optional<AdminUserEntity> result = this.userRepo.findById(Integer.valueOf(id));
    AdminUserEntity user = null;
    if (result.isPresent()) {
      user = result.get();
    } else {
      throw new RuntimeException("Did not find user id - " + id);
    } 
    return user;
  }
}
