package com.steel.product.application.dao;

import com.steel.product.application.entity.AdminUserEntity;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends CrudRepository< AdminUserEntity, Integer >
{
    public AdminUserEntity findByUserNameAndEnabled( String userName, short enabled );

    public AdminUserEntity findByEmailId( String email );

    public List< AdminUserEntity > findAllByEnabled( short enabled );

    public void deleteById( Integer id );

    public Optional<AdminUserEntity> findByUserName( String name );

    public AdminUserEntity findByUserId( Integer userId );
    
    @Modifying
	@Transactional
	@Query("delete from UserPartyMap where userId = :userId")
	public void deletePartyMapByUserId(@Param("userId") Integer userId );

    @Modifying
	@Transactional
	@Query("delete from UserRoleMap where userId = :userId")
	public void deleteRoleMapByUserId(@Param("userId") Integer userId );

    
    
}