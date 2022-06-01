package com.steel.product.application.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.dto.LoginRequest;
import com.steel.product.application.entity.UserEntity;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.exception.RecordAlreadyExistsException;
import com.steel.product.application.oauth.service.UserInfoService;
import com.steel.product.application.response.LoginResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Log4j2
@Tag(name = "Login Controller", description = "Login Controller")
@RequestMapping("/login")
public class UserController
{
    @Autowired
    private UserInfoService userService;

    /**
	 * Login Operation
	 * 
	 * @param servletResponse
	 * @param loginReq
	 * @return
	 * @throws AccessDeniedException
	 * @throws MaintenanceException
	 * @throws PropertyNotFoundException
	 * @throws UserAccountBlockedException
	 * @throws UserAccountLockedException
	 * @throws UserAccountInActiveException
	 */
	@PostMapping
	public LoginResponse login(@Valid @RequestBody LoginRequest loginReq) throws MockException  {

		log.debug("*** Login Method Invoked ***");
		return userService.login(loginReq);
	}
	
    @GetMapping( "/user" )
    public Object getAllUser( @RequestHeader HttpHeaders requestHeader )
    {
        List< UserEntity > userInfos = userService.getAllActiveUserInfo();
        if ( userInfos == null || userInfos.isEmpty() )
        {
            return new ResponseEntity< Void >( HttpStatus.NO_CONTENT );
        }
        return userInfos;
    }

    @PostMapping( "/signup" )
    public UserEntity signup( @RequestBody UserEntity userRecord )
    {
    	UserEntity existingUser = userService.getUserInfoByUserName( userRecord.getUserName() );
        if ( existingUser == null )
            return userService.addUser( userRecord );
        else
            throw new RecordAlreadyExistsException( "User is already available" );
    }

    @PutMapping( "/user/{id}" )
    public UserEntity updateUser( @RequestBody UserEntity userRecord, @PathVariable Integer id )
    {
        return userService.updateUser( id, userRecord );
    }

    @PutMapping( "/user/changePassword/{id}" )
    public UserEntity updateUserPassword( @RequestBody UserEntity userRecord, @PathVariable Integer id )
    {
        return userService.updatePassword( id, userRecord );
    }

    @PutMapping( "/user/changeRole/{id}" )
    public UserEntity updateUserRole( @RequestBody UserEntity userRecord, @PathVariable Integer id )
    {
        return userService.updateRole( id, userRecord );
    }

    @DeleteMapping( "/user/{id}" )
    public void deleteUser( @PathVariable Integer id )
    {
        userService.deleteUser( id );
    }

    @GetMapping( "/user/{id}" )
    public ResponseEntity< UserEntity > getUserById( @PathVariable Integer id )
    {
        UserEntity UserEntity = userService.getUserInfoById( id );
        if ( UserEntity == null )
        {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT );
        }
        return new ResponseEntity<>( UserEntity, HttpStatus.OK );
    }

    @RequestMapping( "/user/me" )
    public ResponseEntity< UserEntity > profile()
    {
        User user = ( User ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity profile = userService.getUserInfoByUserName( user.getUsername() );

        return ResponseEntity.ok( profile );
    }
}
