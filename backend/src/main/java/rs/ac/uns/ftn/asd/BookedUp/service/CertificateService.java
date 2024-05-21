package rs.ac.uns.ftn.asd.BookedUp.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import rs.ac.uns.ftn.asd.BookedUp.dto.CertificateDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.SignedCertificateDTO;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Service
public class CertificateService {

    private final WebClient webClient;

    public CertificateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://localhost:8081").build();
    }

    public CertificateDTO downloadCertificate(String alias){
        SignedCertificateDTO certificatePemDTO = sendCertificateDownloadRequest(alias);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, getPublicKeyFromPem());

            String hash1 = hashSHA256(certificatePemDTO.getPemCertificate());
            String hash2 = new String(cipher.doFinal(certificatePemDTO.getDigitalSignature()), StandardCharsets.UTF_8);
            if(hash1.equals(hash2)){
                return new CertificateDTO(certificatePemDTO.getPemCertificate());
            }else{
                throw new Exception("Invalid digital signature");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public SignedCertificateDTO sendCertificateDownloadRequest(String alias) {
        return webClient.get()
                .uri("/certificate/download/"+alias)
                .retrieve()
                .bodyToMono(SignedCertificateDTO.class)
                .block();
    }

    public PublicKey getPublicKeyFromPem() {
        String pem = getPemFile();
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCertificateToFile(String pemCertificate, String alias) {
        try {
            File file = new File("src/main/resources/https/" + alias + "-certificate.pem");
            Files.write(file.toPath(), pemCertificate.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error saving certificate to file", e);
        }
    }


    public String getPemFile() {

        try {
            return  new String(Files.readAllBytes(new File("src/main/resources/https/public.pem").toPath()),
                    Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String hashSHA256(String input){

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = md.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
