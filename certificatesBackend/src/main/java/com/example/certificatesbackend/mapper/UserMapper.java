package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {

        UserMapper.modelMapper = modelMapper;

    }
    public static User toEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public static UserDTO toDto(User entity) {
        return modelMapper.map(entity, UserDTO.class);
    }

}
