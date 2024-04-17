package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.PriceChange;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.PriceChangeDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;

@Component
public class PriceChangeMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public PriceChangeMapper(ModelMapper modelMapper) {
        PriceChangeMapper.modelMapper = modelMapper;
    }
    public static PriceChange toEntity(PriceChangeDTO dto) {
        return modelMapper.map(dto, PriceChange.class);
    }

    public static PriceChangeDTO toDto(PriceChange entity) {
        return modelMapper.map(entity, PriceChangeDTO.class);
    }
}
