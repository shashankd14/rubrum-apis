package com.steel.product.application.dao;

import com.steel.product.application.entity.UserEntity;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends CrudRepository< UserEntity, Integer >
{
    public UserEntity findByUserNameAndEnabled( String userName, short enabled );

    public UserEntity findByEmail( String email );

    public List< UserEntity > findAllByEnabled( short enabled );

    public void deleteById( Integer id );

    public Optional<UserEntity> findByUserName( String name );

    public UserEntity findByUserId( Integer userId );
}