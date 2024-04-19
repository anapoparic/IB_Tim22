package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.dto.CertificateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public CertificateMapper(ModelMapper modelMapper) {
        CertificateMapper.modelMapper = modelMapper;
    }
    public static Certificate toEntity(CertificateDTO dto) {
        return modelMapper.map(dto, Certificate.class);
    }

    public static CertificateDTO toDto(Certificate entity) {
        return modelMapper.map(entity, CertificateDTO.class);
    }

}
