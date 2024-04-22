package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.Admin;
import rs.ac.uns.ftn.asd.BookedUp.repository.IAdminRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.util.Collection;

@Service
public class AdminService implements ServiceInterface<Admin> {
    @Autowired
    private IAdminRepository repository;

    @Override
    public Collection<Admin> getAll() {
        return repository.findAllAdmins();
    }

    @Override
    public Admin getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Admin create(Admin admin) throws Exception {
        if (admin.getId() != null) {
            throw new Exception("Id must be null when persisting a new entity.");
        }
        return repository.save(admin);
    }

    @Override
    public Admin save(Admin admin) throws Exception {
        return repository.save(admin);
    }

//    @Override
//    public AdminDTO update(AdminDTO adminDTO) throws Exception {
//        Admin admin = adminMapper.toEntity(adminDTO);
//        Admin adminToUpdate = repository.findById(adminDTO.getId()).orElse(null);
//        if (adminToUpdate == null) {
//            throw new Exception("The requested entity was not found.");
//        }
//
//        adminToUpdate.setFirstName(admin.getFirstName());
//        adminToUpdate.setLastName(admin.getLastName());
//        adminToUpdate.setAddress(admin.getAddress());
//        adminToUpdate.setEmail(admin.getEmail());
//        adminToUpdate.setPassword(admin.getPassword());
//        adminToUpdate.setPhone(admin.getPhone());
//        adminToUpdate.setVerified(admin.isVerified());
//        adminToUpdate.setProfilePicture(admin.getProfilePicture());
//        adminToUpdate.setLastPasswordResetDate(admin.getLastPasswordResetDate());
//        adminToUpdate.setUserReports(admin.getUserReports());
//        adminToUpdate.setReviewReports(admin.getReviewReports());
//        adminToUpdate.setRequests(admin.getRequests());
//
//        adminToUpdate.setAuthority(admin.getAuthority());
//        adminToUpdate.setProfilePicture(admin.getProfilePicture());
//        adminToUpdate.setVerified(admin.isVerified());
//        adminToUpdate.setLastPasswordResetDate(admin.getLastPasswordResetDate());
//
//        Admin updatedAdmin = repository.save(adminToUpdate);
//        return adminMapper.toDto(updatedAdmin);
//    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

//    public void updateAdminInformation(AdminDTO adminDTO, UserDTO userDTO) throws Exception {
//        if (adminDTO != null && userDTO != null) {
//            // Check and update specific fields based on your logic
//            if (userDTO.getFirstName() != null) {
//                adminDTO.setFirstName(userDTO.getFirstName());
//            }
//            if (userDTO.getLastName() != null) {
//                adminDTO.setLastName(userDTO.getLastName());
//            }
//            Admin admin = adminMapper.toEntity(adminDTO);
//            repository.update(admin);
//        }
//    }

}
