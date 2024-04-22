package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.ConfirmationToken;
import rs.ac.uns.ftn.asd.BookedUp.repository.IConfirmationTokenRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.IConfirmationTokenService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService implements IConfirmationTokenService {

    @Autowired
    IConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
