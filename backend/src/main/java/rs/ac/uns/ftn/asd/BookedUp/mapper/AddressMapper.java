package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.Address;
import rs.ac.uns.ftn.asd.BookedUp.domain.Photo;
import rs.ac.uns.ftn.asd.BookedUp.dto.AddressDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.PhotoDTO;

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
