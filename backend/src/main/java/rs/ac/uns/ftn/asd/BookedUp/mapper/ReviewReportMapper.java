package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;

@Component
public class ReviewReportMapper  {

    private static ModelMapper modelMapper;

    @Autowired
    public ReviewReportMapper(ModelMapper modelMapper) {
        ReviewReportMapper.modelMapper = modelMapper;
    }
    public static ReviewReport toEntity(ReviewReportDTO dto) {
        return modelMapper.map(dto, ReviewReport.class);
    }

    public static ReviewReportDTO toDto(ReviewReport entity) {
        return modelMapper.map(entity, ReviewReportDTO.class);
    }
//    ReviewMapper reviewMapper = new ReviewMapper();
//
//    @Override
//    public ReviewReport toEntity(ReviewReportDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        Review reportedReview = reviewMapper.toEntity(dto.getReportedReviewDTO());
//
//
//        ReviewReport reviewReport = new ReviewReport();
//        reviewReport.setId(dto.getId());
//        reviewReport.setReportedReview(reportedReview);
//        reviewReport.setReason(dto.getReason());
//        reviewReport.setStatus(dto.isStatus());
//
//        return reviewReport;
//    }
//
//    @Override
//    public ReviewReportDTO toDto(ReviewReport entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        ReviewDTO reviewDTO = reviewMapper.toDto(entity.getReportedReview());
//
//        ReviewReportDTO reviewReportDTO = new ReviewReportDTO();
//        reviewReportDTO.setId(entity.getId());
//        reviewReportDTO.setReportedReviewDTO(reviewDTO);
//        reviewReportDTO.setReason(entity.getReason());
//        reviewReportDTO.setStatus(entity.isStatus());
//
//        return reviewReportDTO;
//    }
}
