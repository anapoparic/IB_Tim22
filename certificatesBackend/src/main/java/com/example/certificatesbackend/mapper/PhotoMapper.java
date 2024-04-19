package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.Photo;
import com.example.certificatesbackend.dto.PhotoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PhotoMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public PhotoMapper(ModelMapper modelMapper) {
        PhotoMapper.modelMapper = modelMapper;
    }
    public static Photo toEntity(PhotoDTO dto) {
        return modelMapper.map(dto, Photo.class);
    }

    public static PhotoDTO toDto(Photo entity) {
        return modelMapper.map(entity, PhotoDTO.class);
    }

}
