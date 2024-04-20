package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;

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
