package com.steel.product.application.mapper;

import com.steel.product.application.dto.address.AddressDto;
import com.steel.product.application.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressDto addressDto);
}
