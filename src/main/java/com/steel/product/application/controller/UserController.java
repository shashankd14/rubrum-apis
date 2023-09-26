package com.steel.product.application.controller;

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

import com.steel.product.application.dto.admin.CreateUserRequest;
import com.steel.product.application.entity.AdminUserEntity;
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
    public ResponseEntity<Object> getAllUser( @RequestHeader HttpHeaders requestHeader )
	{
		ResponseEntity<Object> response = null;

		List<AdminUserEntity> userInfos = null;
		try {
			userInfos = userService.getAllActiveUserInfo();
		} catch (Exception e) {
			response = new ResponseEntity<>("{\"status\": \"fail\", \"message\": \"" + e.getMessage() + "\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response = new ResponseEntity<>(userInfos, new HttpHeaders(), HttpStatus.OK);
		return response;
	}

    @PostMapping( "/signup" )
    public AdminUserEntity signup( @RequestBody CreateUserRequest userRecord ) throws MockException
    {
    	AdminUserEntity existingUser = userService.getUserEntityByUserName( userRecord.getUserName() );
        if ( existingUser == null ) {
        	AdminUserEntity existingUser1 = userService.getUserEntityByEmail( userRecord.getEmailId() );
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

    @PutMapping( "/update" )
    public AdminUserEntity updateUser( @RequestBody CreateUserRequest userRecord )
    {
        return userService.addUser( userRecord );
    }

	@PutMapping("/changePassword")
	public AdminUserEntity updateUserPassword(@RequestBody AdminUserEntity userRecord) throws MockException {
		try {
			return userService.updatePassword(userRecord);
		} catch (Exception e) {

			List<String> errors = new ArrayList<>();
			errors.add("Please Enter valid data.");
			throw new MockException("MSG-0007", errors);

		}

	}

    @PutMapping( "/changeRole/{id}" )
    public AdminUserEntity updateUserRole( @RequestBody AdminUserEntity userRecord, @PathVariable Integer id )
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
    public ResponseEntity< AdminUserEntity > getUserById(HttpServletRequest request, @PathVariable Integer id )
    {
    	
        AdminUserEntity AdminUserEntity = userService.getUserInfoById( id );
        if ( AdminUserEntity == null )
        {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT );
        }
        return new ResponseEntity<>( AdminUserEntity, HttpStatus.OK );
    }

    @RequestMapping( "/me" )
    public ResponseEntity< AdminUserEntity > profile()
    {
        User user = ( User ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AdminUserEntity profile = userService.getUserEntityByUserName( user.getUsername() );

        return ResponseEntity.ok( profile );
    }
}
