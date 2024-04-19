package com.example.certificatesbackend.mapper;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.dto.CertificateDTO;
import com.example.certificatesbackend.dto.CertificateRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CertificateRequestMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public CertificateRequestMapper(ModelMapper modelMapper) {
        CertificateRequestMapper.modelMapper = modelMapper;
    }
    public static CertificateRequest toEntity(CertificateRequestDTO dto) {
        return modelMapper.map(dto, CertificateRequest.class);
    }

    public static CertificateRequestDTO toDto(CertificateRequest entity) {
        return modelMapper.map(entity, CertificateRequestDTO.class);
    }

}
