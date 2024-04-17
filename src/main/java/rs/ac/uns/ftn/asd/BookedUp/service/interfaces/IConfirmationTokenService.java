package rs.ac.uns.ftn.asd.BookedUp.service.interfaces;

import rs.ac.uns.ftn.asd.BookedUp.domain.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IConfirmationTokenService {

     void saveConfirmationToken(ConfirmationToken token) ;

     Optional<ConfirmationToken> getToken(String token);

     int setConfirmedAt(String token);
}
