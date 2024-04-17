package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.DateRange;
import rs.ac.uns.ftn.asd.BookedUp.dto.DateRangeDTO;

@Component
public class DateRangeMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public DateRangeMapper(ModelMapper modelMapper) {
        DateRangeMapper.modelMapper = modelMapper;
    }
    public static DateRange toEntity(DateRangeDTO dto) {
        return modelMapper.map(dto, DateRange.class);
    }

    public static DateRangeDTO toDto(DateRange entity) {
        return modelMapper.map(entity, DateRangeDTO.class);
    }
}
