package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.UserReport;
import com.example.certificatesbackend.dto.UserReportDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserReportMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public UserReportMapper(ModelMapper modelMapper) {
        UserReportMapper.modelMapper = modelMapper;
    }
    public static UserReport toEntity(UserReportDTO dto) {
        return modelMapper.map(dto, UserReport.class);
    }

    public static UserReportDTO toDto(UserReport entity) {
        return modelMapper.map(entity, UserReportDTO.class);
    }
}
