package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.UserReport;
import rs.ac.uns.ftn.asd.BookedUp.repository.IUserReportRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserReportService implements ServiceInterface<UserReport> {

    @Autowired
    private IUserReportRepository repository;

    @Override
    public Collection<UserReport> getAll() {
        return repository.findAll();
    }
    @Override
    public UserReport getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public UserReport create(UserReport userReport) throws Exception {
        if (userReport.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        return repository.save(userReport);
    }

    @Override
    public UserReport save(UserReport userReport) throws Exception {
        return repository.save(userReport);
    }

//    @Override
//    public UserReportDTO update(UserReportDTO userReportDTO) throws Exception {
//        UserReport userReport = userReportMapper.toEntity(userReportDTO);
//        UserReport userReportToUpdate= repository.findById(userReport.getId()).orElse(null);
//        if (userReportToUpdate == null) {
//            throw new Exception("The requested entity was not found.");
//        }
//        userReportToUpdate.setReason(userReport.getReason());
//        userReportToUpdate.setReportedUser(userReport.getReportedUser());
//        userReportToUpdate.setStatus(userReport.isStatus());
//
//        UserReport updatedUserreport = repository.save(userReportToUpdate);
//        return userReportMapper.toDto(updatedUserreport);
//    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }


    public Collection<User> getAllReportedUsers() {
        return repository.findAllReportedUsers();
    }

    // UserReportService.java
    public Collection<String> getReportReasonsForUser(Long reportUserId) {
        Collection<UserReport> userReportsForUser = getAll().stream()
                .filter(userReport -> userReport.getReportedUser().getId().equals(reportUserId))
                .collect(Collectors.toList());

        return userReportsForUser.stream()
                .map(UserReport::getReason)
                .collect(Collectors.toList());
    }

}