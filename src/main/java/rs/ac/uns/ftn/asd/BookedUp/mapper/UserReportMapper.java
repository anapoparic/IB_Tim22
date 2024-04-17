package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.Review;
import rs.ac.uns.ftn.asd.BookedUp.domain.ReviewReport;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.UserReport;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReviewDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReviewReportDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserReportDTO;

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
//    @Autowired
//    UserMapper userMapper;
//    @Override
//    public UserReport toEntity(UserReportDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        User reportedUser = userMapper.toEntity(dto.getReportedUserDTO());
//
//
//        UserReport userReport = new UserReport();
//        userReport.setId(dto.getId());
//        userReport.setReportedUser(reportedUser);
//        userReport.setReason(dto.getReason());
//        userReport.setStatus(dto.isStatus());
//
//        return userReport;
//    }
//
//    @Override
//    public UserReportDTO toDto(UserReport entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        UserDTO userDTO = userMapper.toDto(entity.getReportedUser());
//
//        UserReportDTO userReportDTO = new UserReportDTO();
//        userReportDTO.setId(entity.getId());
//        userReportDTO.setReportedUserDTO(userDTO);
//        userReportDTO.setReason(entity.getReason());
//        userReportDTO.setStatus(entity.isStatus());
//
//        return userReportDTO;
//    }
}
