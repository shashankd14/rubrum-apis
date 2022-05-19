package com.steel.product.application.service;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.UserEntity;
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

    public UserEntity saveStatus(UserEntity user) {
        return (UserEntity) this.userRepo.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return (List<UserEntity>) this.userRepo.findAll();
    }

    public UserEntity getUserById(int id) {
    Optional<UserEntity> result = this.userRepo.findById(Integer.valueOf(id));
    UserEntity user = null;
    if (result.isPresent()) {
      user = result.get();
    } else {
      throw new RuntimeException("Did not find user id - " + id);
    } 
    return user;
  }
}
