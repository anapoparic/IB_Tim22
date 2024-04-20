package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.Template;
import com.example.certificatesbackend.dto.CertificateRequestDTO;
import com.example.certificatesbackend.pki.certificates.CertificateGenerator;
import com.example.certificatesbackend.pki.data.Issuer;
import com.example.certificatesbackend.pki.data.Subject;
import com.example.certificatesbackend.pki.keystores.KeyStoreReader;
import com.example.certificatesbackend.pki.keystores.KeyStoreWriter;
import com.example.certificatesbackend.repository.ICertificateRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

import static com.example.certificatesbackend.constants.Constants.KEYSTORE_PASSWORD;
import static com.example.certificatesbackend.constants.Constants.KEYSTORE_PATH;

@Service
public class CertificateService  {

    @Autowired
    private ICertificateRepository repository;

    @Autowired
    public KeyStoreWriter storeWriter;

    @Autowired
    public KeyStoreReader storeReader;

    @Autowired
    public CertificateRequestService requestService;

    public Collection<Certificate> getAll() {
        return repository.findAll();
    }

    public Certificate getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Certificate create(CertificateRequest request, String alias, String issuerAlias, String template) throws Exception {

        Subject subject = createSubject(request);
        KeyPair keyPair = generateKeyPair();
        assert keyPair != null;
        subject.setPublicKey(keyPair.getPublic());

        java.security.cert.Certificate[] certificatesChain = storeReader.getCertificateChain(KEYSTORE_PATH, KEYSTORE_PASSWORD, issuerAlias);
        Issuer issuer = storeReader.getIssuer(KEYSTORE_PATH, KEYSTORE_PASSWORD, issuerAlias);
        String serialNumber = String.valueOf(System.currentTimeMillis());

        Date validFrom = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validFrom);
        calendar.add(Calendar.MONTH, 13);
        Date validTo = calendar.getTime();

        X509Certificate certificate = null;

        if (Template.valueOf(template) == Template.CA) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.CA);
        } else if (Template.valueOf(template) == Template.END_ENTITY) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.END_ENTITY);
        } else if (Template.valueOf(template) == Template.INTERMEDIATE) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.INTERMEDIATE);
        } else {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.CA);
        }

        java.security.cert.Certificate[] newChain = Arrays.copyOf(certificatesChain, certificatesChain.length + 1);
        newChain[0] = certificate;

        storeWriter.loadKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());
        storeWriter.writeChain(alias, keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), newChain);
        storeWriter.saveKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());

        Certificate newCertificate = new Certificate(validFrom, validTo, alias, issuerAlias,
                false, null, Template.valueOf(template), request.getCommonName(), request.getOrganization(), request.getEmail(),
                true);
        repository.save(newCertificate);
        requestService.delete(request.getId());
        //treba dodati funkciju export (videti kod milice) -> zasto?
        //jer kada kreiramo sertifikat novi mi ga zapisujemo u keystore sa ostalima, ali nam treba taj sertifikat
        //odredjenom formatu (kao opipljiv), znaci u formatu certificate.CRT
        //CRT - dobijas sertifikat kao sto imas u sistemu one sertifikate takav format imas i to je taj sertifikat
        //u keystore ti samo belezis info o kreiranom sertifikatu kao sto su javni kljuc, privatni, serijski broj itd...

        return newCertificate;


    }

    public void delete(Long id) throws Exception {
        Certificate cer = repository.findById(id)
                .orElseThrow(() -> new Exception("Certificate with given id doesn't exist"));
        cer.setActive(false);
        repository.save(cer);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;

    }

    private Subject createSubject(CertificateRequest request) throws java.text.ParseException {

        KeyPair keyPairValues = generateKeyPair();

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, request.getCommonName());
        builder.addRDN(BCStyle.SURNAME, request.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, request.getGivenName());
        builder.addRDN(BCStyle.O, request.getOrganization());
        builder.addRDN(BCStyle.E, request.getEmail());
        builder.addRDN(BCStyle.UID, request.getUid());
        Subject subject = new Subject(keyPairValues.getPublic(), builder.build());
        return subject;
    }

}
