package com.steel.product.application.mapper.impl;

import com.steel.product.application.dto.address.AddressDto;
import com.steel.product.application.entity.Address;
import com.steel.product.application.mapper.AddressMapper;
import org.springframework.stereotype.Component;

/*@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-27T10:12:33+0530",
    comments = "version: 1.5.0.Beta1, compiler: javac, environment: Java 1.8.0_111 (Oracle Corporation)"
)*/
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public Address toEntity(AddressDto addressDto) {
        if ( addressDto == null ) {
            return null;
        }

        Address address = new Address();

        if ( addressDto.getAddressId() != null ) {
            address.setAddressId( addressDto.getAddressId() );
        }
        address.setDetails( addressDto.getDetails() );
        address.setCity( addressDto.getCity() );
        address.setState( addressDto.getState() );
        address.setPincode( addressDto.getPincode() );

        return address;
    }
}
