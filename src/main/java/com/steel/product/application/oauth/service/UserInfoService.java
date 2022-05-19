package com.steel.product.application.oauth.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.User;

@Service
@Transactional
public class UserInfoService
{

    @Autowired
    private UserRepository userDetailsRepository;

    public User getUserInfoByEmail( String email )
    {
        return userDetailsRepository.findByEmail( email );
    }

    public User getUserInfoByUserName( String userName )
    {
        short enabled = 1;
        return userDetailsRepository.findByUserNameAndEnabled( userName, enabled );
    }

    public List< User > getAllActiveUserInfo()
    {
        return userDetailsRepository.findAllByEnabled( ( short ) 1 );
    }

    public User getUserInfoById( Integer id )
    {
        return userDetailsRepository.findByUserId( id );
    }

    public User addUser( User userInfo )
    {
        userInfo.setPassword( new BCryptPasswordEncoder().encode( userInfo.getPassword() ) );
//    	userInfo.setPassword(  userInfo.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public User updateUser( Integer id, User userRecord )
    {
    	User userInfo = userDetailsRepository.findByUserId( id );
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

    public User updatePassword( Integer id, User userRecord )
    {
    	User userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setPassword( userRecord.getPassword() );
        return userDetailsRepository.save( userInfo );
    }

    public User updateRole( Integer id, User userRecord )
    {
    	User userInfo = userDetailsRepository.findByUserId( id );
        userInfo.setRole( userRecord.getRole() );
        return userDetailsRepository.save( userInfo );
    }
}
