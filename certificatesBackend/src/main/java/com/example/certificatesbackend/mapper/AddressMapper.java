package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.Address;
import com.example.certificatesbackend.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public AddressMapper(ModelMapper modelMapper) {
        AddressMapper.modelMapper = modelMapper;
    }
    public static Address toEntity(AddressDTO dto) { return modelMapper.map(dto, Address.class);
    }

    public static AddressDTO toDto(Address entity) {
        return modelMapper.map(entity, AddressDTO.class);
    }
}