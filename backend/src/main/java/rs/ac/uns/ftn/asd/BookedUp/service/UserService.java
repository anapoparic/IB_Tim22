package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.repository.IUserRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements ServiceInterface<User> {
    @Autowired
    private IUserRepository repository;

    @Autowired
    private ReservationService reservationService;

    @Override
    public Collection<User> getAll() {
        Collection<User> users = repository.findAll();
        return users;
    }

    @Override
    public User getById(Long id) {
        User user = repository.findById(id).orElse(null);
        return user;
    }

    @Override
    public User create(User user) throws Exception {
        if (user.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        System.out.println(user.getAddress().getId());
        return repository.save(user);
    }


    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }


    public void blockUser(User user) {
        user.setBlocked(true);

        if(user.getRole()== Role.GUEST){
            reservationService.rejectReservationsForGuest(user.getId());
        }
        repository.save(user);
    }


    public void unblockUser(User user) {
        user.setBlocked(false);
        repository.save(user);
    }

    public boolean authenticateUser(String email, String password) {
        return true;
    }

    public boolean registerGuest(UserDTO userDTO) {
        //make sure to put user.role to guest
        return true;
    }

    public boolean registerHost(UserDTO userDTO) {
        //make sure to put user.role to host
        return false;
    }

    public User getByEmail(String email) {
        User user = repository.findByEmail(email).orElse(null);
        return user;
    }

    public Collection<User> getActiveAll() {
        Collection<User> users = repository.findAll();
        UserReportService reportService = new UserReportService();
        Collection<UserReport> reports = reportService.getAll();

        Collection<User> activeUsers = users.stream()
                .filter(user -> reports.stream()
                        .noneMatch(report -> report.getReportedUser().getId().equals(user.getId()) && report.isStatus()))
                .collect(Collectors.toList());

        return activeUsers;
    }

    public Collection<User> getBlockedAll() {
        return repository.findAllBlockedUsers();
    }

    public Collection<User> getReportedAll() {
        Collection<User> users = repository.findAll();
        UserReportService reportService = new UserReportService();
        Collection<UserReport> reports = reportService.getAll();

        Collection<User> reportedUsers = users.stream()
                .filter(user -> reports.stream()
                        .anyMatch(report -> report.getReportedUser().getId().equals(user.getId()) && !report.isStatus()))
                .collect(Collectors.toList());

        return reportedUsers;
    }
}
