package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;

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

