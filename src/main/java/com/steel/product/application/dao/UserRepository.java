package com.steel.product.application.dao;

import com.steel.product.application.entity.User;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends CrudRepository< User, Integer >
{
    public User findByUserNameAndEnabled( String userName, short enabled );

    public User findByEmail( String email );

    public List< User > findAllByEnabled( short enabled );

    public void deleteById( Integer id );

    public Optional<User> findByUserName( String name );

    public User findByUserId( Integer userId );
}