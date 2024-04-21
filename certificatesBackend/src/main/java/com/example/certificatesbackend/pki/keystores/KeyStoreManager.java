package com.example.certificatesbackend.pki.keystores;

import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Component
public class KeyStoreManager {
    private KeyStore keyStore;
    public static  String KEYSTORE_PATH = "src/main/resources/keystores";
    public void createKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(KEYSTORE_PATH + fileName + ".jks"), password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
