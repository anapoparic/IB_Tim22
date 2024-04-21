package com.example.certificatesbackend.service;

import com.example.certificatesbackend.constants.Constants;
import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.Template;
import com.example.certificatesbackend.dto.CertificateRequestDTO;
import com.example.certificatesbackend.pki.certificates.CertificateGenerator;
import com.example.certificatesbackend.pki.data.Issuer;
import com.example.certificatesbackend.pki.data.Subject;
import com.example.certificatesbackend.pki.keystores.KeyStoreManager;
import com.example.certificatesbackend.pki.keystores.KeyStoreReader;
import com.example.certificatesbackend.pki.keystores.KeyStoreWriter;
import com.example.certificatesbackend.repository.ICertificateRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.*;

import static com.example.certificatesbackend.constants.Constants.*;
import static com.example.certificatesbackend.pki.certificates.CertificateGenerator.generateRootCertificate;

@Service
public class CertificateService  {

    @Autowired
    private ICertificateRepository repository;

    @Autowired
    private KeyStoreManager keyStoreManager;

    @Autowired
    public KeyStoreWriter storeWriter;

    @Autowired
    public KeyStoreReader storeReader;

    @Autowired
    public CertificateRequestService requestService;

    public int keystoreCounter = 0;

    public Collection<Certificate> getAll() {
        return repository.findAll();
    }

    public Collection<Certificate> getAllRoot() {
        return repository.findAllByTemplate(Template.ROOT);
    }

    public Collection<Certificate> findAllChildren(Integer rootId) {
        Certificate root = repository.findById(rootId);

        if (root == null) {
            return new ArrayList<>(); // Return an empty list if the root certificate is not found
        }

        Collection<Certificate> children = new ArrayList<>();
        findAllChildrenRecursive(root, children);

        return children;
    }

    private void findAllChildrenRecursive(Certificate parent, Collection<Certificate> children) {
        // Find all direct children of the current parent certificate
        Collection<Certificate> directChildren = repository.findAllByIssuerAlias(parent.getAlias());

        for (Certificate child : directChildren) {
            children.add(child);
            if(!child.getIssuerAlias().equals(child.getAlias())){ // if certificate sign itself
                // Add the child certificate to the collection of children

                // Recursively find all children of the current child
                findAllChildrenRecursive(child, children);
            }
        }
    }

    public Collection<Certificate> findPathToRoot(Integer certificateId) {
        LinkedList<Certificate> pathToRoot = new LinkedList<>();
        Certificate currentCertificate = repository.findById(certificateId);

        while (currentCertificate != null) {
            pathToRoot.addFirst(currentCertificate); // Dodajemo na početak liste
            if (currentCertificate.getTemplate().equals(Template.ROOT)) {
                break; // Ako je root, prekidamo petlju
            }
            // Pronalazimo roditelja trenutnog sertifikata
            Optional<Certificate> parentOptional = repository.findByAlias(currentCertificate.getIssuerAlias());
            // Ako roditelj ne postoji, prekidamo petlju
            currentCertificate = parentOptional.orElse(null);
        }

        return pathToRoot;
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

        if (Template.valueOf(template) == Template.ROOT) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.ROOT);
        } else if (Template.valueOf(template) == Template.END_ENTITY) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.END_ENTITY);
        } else if (Template.valueOf(template) == Template.INTERMEDIATE) {
            certificate = CertificateGenerator.generateCertificate(subject, issuer, validFrom, validTo, serialNumber, Template.INTERMEDIATE);
        }

        java.security.cert.Certificate[] newChain = Arrays.copyOf(certificatesChain, certificatesChain.length + 1);
        newChain[0] = certificate;

        storeWriter.loadKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());
        storeWriter.writeChain(alias, keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), newChain);
        storeWriter.saveKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());

        Certificate newCertificate = new Certificate(validFrom, validTo, alias, issuerAlias,
                false, null, Template.valueOf(template), request.getCommonName(), request.getOrganization(), request.getUnit(), request.getCountry(), request.getEmail(),
                true);
        repository.save(newCertificate);
        requestService.delete(request.getId());

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
        builder.addRDN(BCStyle.SURNAME, request.getLastName());
        builder.addRDN(BCStyle.GIVENNAME, request.getFirstName());
        builder.addRDN(BCStyle.O, request.getOrganization());
        builder.addRDN(BCStyle.OU, request.getUnit());
        builder.addRDN(BCStyle.COUNTRY_OF_RESIDENCE, request.getCountry());
        builder.addRDN(BCStyle.E, request.getEmail());
        builder.addRDN(BCStyle.UID, request.getUid());
        Subject subject = new Subject(keyPairValues.getPublic(), builder.build());
        return subject;
    }

//    public X509Certificate createRootCertificate(Certificate certificate, String uid) throws ParseException, IOException {
//        KeyPair keyPair = generateKeyPair();
//        Subject subject = createRootSubject(keyPair, certificate.getCommonName(), "Admin", "Adminic", certificate.getOrganization(), certificate.getOrganizationUnit(),
//                certificate.getCountry(), certificate.getOwnerEmail(), uid);
//        assert keyPair != null;
//
//        Date validFrom = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(validFrom);
//        calendar.add(Calendar.MONTH, 13);
//        Date validTo = calendar.getTime();
//        String serialNumber = String.valueOf(System.currentTimeMillis());
//
//        X509Certificate createdCertificate = generateRootCertificate(subject, keyPair, validFrom, validTo, serialNumber);
//
//        String keystoreFileName = "keystore_" + keystoreCounter + ".jks";
//        keystoreCounter++;
//
//
//        storeWriter.loadKeyStore(null, KEYSTORE_PASSWORD.toCharArray());
//        storeWriter.write(certificate.getAlias(), keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), createdCertificate);
//        storeWriter.saveKeyStore(keystoreFileName, KEYSTORE_PASSWORD.toCharArray());
//
//        Certificate newCertificate = new Certificate(validFrom, validTo, certificate.getAlias(), certificate.getIssuerAlias(),
//                false, null, Template.ROOT, certificate.getCommonName(), certificate.getOrganization(), certificate.getOrganizationUnit(), certificate.getCountry(), certificate.getOwnerEmail(),
//                true);
//        repository.save(newCertificate);
//
//
//        return createdCertificate;
//    }
//
//    private Subject createRootSubject(KeyPair keyPairValues, String commonName, String surname, String givenName, String organization,
//                                      String organizationalUnit, String countryOfResidence, String email, String uid) throws java.text.ParseException {
//
//        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
//        builder.addRDN(BCStyle.CN, commonName);
//        builder.addRDN(BCStyle.SURNAME, surname);
//        builder.addRDN(BCStyle.GIVENNAME, givenName);
//        builder.addRDN(BCStyle.O, organization);
//        builder.addRDN(BCStyle.OU, organizationalUnit);
//        builder.addRDN(BCStyle.COUNTRY_OF_RESIDENCE, countryOfResidence);
//        builder.addRDN(BCStyle.E, email);
//        builder.addRDN(BCStyle.UID, uid);
//        Subject subject = new Subject(keyPairValues.getPublic(), builder.build());
//        return subject;
//    }
//

    public X509Certificate createRootCertificate(Certificate certificate, String uid) throws ParseException, IOException {
        KeyPair keyPair = generateKeyPair();
        Subject subject = createRootSubject(keyPair, certificate.getCommonName(), "Admin", "Adminic", certificate.getOrganization(), certificate.getOrganizationUnit(),
                certificate.getCountry(), certificate.getOwnerEmail(), uid);
        assert keyPair != null;

        Date validFrom = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validFrom);
        calendar.add(Calendar.MONTH, 13);
        Date validTo = calendar.getTime();
        String serialNumber = String.valueOf(System.currentTimeMillis());

        X509Certificate createdCertificate = generateRootCertificate(subject, keyPair, validFrom, validTo, serialNumber);

        String keystoreFileName = "keystore_" + keystoreCounter + ".jks";
        keystoreCounter++;


        storeWriter.loadKeyStore(null, KEYSTORE_PASSWORD.toCharArray());
        storeWriter.write(certificate.getAlias(), keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), createdCertificate);
        storeWriter.saveKeyStore(keystoreFileName, KEYSTORE_PASSWORD.toCharArray());

        Certificate newCertificate = new Certificate(validFrom, validTo, certificate.getAlias(), certificate.getIssuerAlias(),
                false, null, Template.ROOT, certificate.getCommonName(), certificate.getOrganization(), certificate.getOrganizationUnit(), certificate.getCountry(), certificate.getOwnerEmail(),
                true);
        repository.save(newCertificate);


        return createdCertificate;
    }

    private Subject createRootSubject(KeyPair keyPairValues, String commonName, String surname, String givenName, String organization,
                                      String organizationalUnit, String countryOfResidence, String email, String uid) throws java.text.ParseException {

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, commonName);
        builder.addRDN(BCStyle.SURNAME, surname);
        builder.addRDN(BCStyle.GIVENNAME, givenName);
        builder.addRDN(BCStyle.O, organization);
        builder.addRDN(BCStyle.OU, organizationalUnit);
        builder.addRDN(BCStyle.COUNTRY_OF_RESIDENCE, countryOfResidence);
        builder.addRDN(BCStyle.E, email);
        builder.addRDN(BCStyle.UID, uid);
        Subject subject = new Subject(keyPairValues.getPublic(), builder.build());
        return subject;
    }

    public List<java.security.cert.Certificate> getAllCertificates(String keyStoreFile, String keyStorePass) {
        List<java.security.cert.Certificate> certificates = new ArrayList<>();
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                java.security.cert.Certificate cert = ks.getCertificate(alias);
                certificates.add(cert);
            }
        } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException |
                 IOException e) {
            e.printStackTrace();
        }
        return certificates;
    }
}
