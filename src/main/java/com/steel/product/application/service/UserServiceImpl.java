package com.steel.product.application.service;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.User;
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

    public User saveStatus(User user) {
        return (User) this.userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return this.userRepo.findAll();
    }

    public User getUserById(int id) {
    Optional<User> result = this.userRepo.findById(Integer.valueOf(id));
    User user = null;
    if (result.isPresent()) {
      user = result.get();
    } else {
      throw new RuntimeException("Did not find user id - " + id);
    } 
    return user;
  }
}
