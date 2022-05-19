package com.steel.product.application.oauth.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.steel.product.application.entity.UserEntity;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    private UserInfoService userInfoDAO;

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException
    {
        UserEntity userInfo = userInfoDAO.getUserInfoByUserName( username );
        if(userInfo!=null) {
        	GrantedAuthority authority = new SimpleGrantedAuthority( userInfo.getRole() );
            return new User( userInfo.getEmail(), userInfo.getPassword(), Arrays.asList( authority ) );
        } else {
        	throw new UsernameNotFoundException("Could not find user with name :: "+username);
        }
    }
}