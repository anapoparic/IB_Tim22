package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> ret = userRepository.findByEmail(email);

        if (!ret.isEmpty() && ret.get().isActive() ) {
            return org.springframework.security.core.userdetails.User.withUsername(email).password(ret.get().getPassword()).roles(ret.get().getRole().toString()).build();
        }
        throw new UsernameNotFoundException("User not found with this username: " + email);
    }
//    public String signUpUser(User user) {
//        boolean userExists = userRepository
//                .findByEmail(user.getEmail())
//                .isPresent();
//
//        if (userExists) {
//            // TODO check of attributes are the same and
//            // TODO if email not confirmed send confirmation email.
//
//            throw new IllegalStateException("email already taken");
//        }
//
//        String encodedPassword = bCryptPasswordEncoder
//                .encode(user.getPassword());
//
//        user.setPassword(encodedPassword);
//
//        userRepository.save(user);
//
//        String token = UUID.randomUUID().toString();
//
//
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                token,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(24),
//                user
//        );
//
//        confirmationTokenService.saveConfirmationToken(
//                confirmationToken);
//
////        TODO: SEND EMAIL
//
//        return token;
//    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}