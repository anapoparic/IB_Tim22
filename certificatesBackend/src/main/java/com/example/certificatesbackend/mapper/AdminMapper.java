package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.dto.AdminDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AdminMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public AdminMapper(ModelMapper modelMapper) {
        AdminMapper.modelMapper = modelMapper;
    }
    public static Admin toEntity(AdminDTO dto) {
        return modelMapper.map(dto, Admin.class);
    }

    public static AdminDTO toDto(Admin entity) {
        return modelMapper.map(entity, AdminDTO.class);
    }

}
