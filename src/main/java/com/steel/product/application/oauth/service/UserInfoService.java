package com.steel.product.application.oauth.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.UserEntity;

@Service
@Transactional
public class UserInfoService
{

    @Autowired
    private UserRepository userDetailsRepository;

    public UserEntity getUserInfoByUserName( String userName )
    {
        short enabled = 1;
        return userDetailsRepository.findByUserNameAndEnabled( userName, enabled );
    }

    public List< UserEntity > getAllActiveUserInfo()
    {
        return userDetailsRepository.findAllByEnabled( ( short ) 1 );
    }

    public UserEntity getUserInfoById( Integer id )
    {
        return userDetailsRepository.findByUserId( id );
    }

    public UserEntity addUser( UserEntity userInfo )
    {
        userInfo.setPassword( new BCryptPasswordEncoder().encode( userInfo.getPassword() ) );
//    	userInfo.setPassword(  userInfo.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public UserEntity updateUser( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setUserName( userRecord.getUserName() );
        userInfo.setPassword( userRecord.getPassword() );
        userInfo.setRole( userRecord.getRole() );
        userInfo.setEnabled( userRecord.getEnabled() );
        return userDetailsRepository.save( userInfo );
    }

    public void deleteUser( Integer id )
    {
        userDetailsRepository.deleteById( id );
    }

    public UserEntity updatePassword( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setPassword( userRecord.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public UserEntity updateRole( Integer id, UserEntity userRecord )
    {
    	UserEntity userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setRole( userRecord.getRole() );
        return userDetailsRepository.save( userInfo );
    }
}
