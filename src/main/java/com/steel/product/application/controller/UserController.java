package com.steel.product.application.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.steel.product.application.entity.UserEntity;
import com.steel.product.application.exception.MockException;
import com.steel.product.application.oauth.service.UserInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin
@Log4j2
@Tag(name = "User Controller", description = "User Controller")
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UserInfoService userService;

    @GetMapping( "/getAll" )
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
    public UserEntity signup( @RequestBody UserEntity userRecord ) throws MockException
    {
    	UserEntity existingUser = userService.getUserEntityByUserName( userRecord.getUserName() );
        if ( existingUser == null ) {
        	UserEntity existingUser1 = userService.getUserEntityByEmail( userRecord.getEmail() );
			if (existingUser1 != null) {
				List<String> errors = new ArrayList<>();
				errors.add("Email Id is already mapped with other user");
				throw new MockException("MSG-0005", errors);
			}
            return userService.addUser( userRecord );
        } else {
        	List<String> errors = new ArrayList<>();
			errors.add( "User is already available" );
			throw new MockException("MSG-0006", errors);
        }
    }

    @PutMapping( "/updateUser" )
    public UserEntity updateUser( @RequestBody UserEntity userRecord)
    {
        return userService.updateUser(userRecord );
    }

	@PutMapping("/changePassword")
	public UserEntity updateUserPassword(@RequestBody UserEntity userRecord) throws MockException {
		try {
			return userService.updatePassword(userRecord);
		} catch (Exception e) {

			List<String> errors = new ArrayList<>();
			errors.add("Please Enter valid data.");
			throw new MockException("MSG-0007", errors);

		}

	}

    @PutMapping( "/changeRole/{id}" )
    public UserEntity updateUserRole( @RequestBody UserEntity userRecord, @PathVariable Integer id )
    {
        return userService.updateRole( id, userRecord );
    }

    @DeleteMapping( "/{id}" )
    public void deleteUser( @PathVariable Integer id ) throws MockException
    {
        try {
			userService.deleteUser( id );
		} catch (Exception e) {

        	List<String> errors = new ArrayList<>();
			errors.add( "Please Enter valid data." );
			throw new MockException("MSG-0007", errors);
        
		}
    }

    @GetMapping( "/{id}" )
    public ResponseEntity< UserEntity > getUserById(HttpServletRequest request, @PathVariable Integer id )
    {
    	
		Principal principal = request.getUserPrincipal();

		System.out.println("hi kanak == "+principal.getName());
    	    
        UserEntity UserEntity = userService.getUserInfoById( id );
        if ( UserEntity == null )
        {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT );
        }
        return new ResponseEntity<>( UserEntity, HttpStatus.OK );
    }

    @RequestMapping( "/me" )
    public ResponseEntity< UserEntity > profile()
    {
        User user = ( User ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserEntity profile = userService.getUserEntityByUserName( user.getUsername() );

        return ResponseEntity.ok( profile );
    }
}
