package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.ReasonForRevoke;
import com.example.certificatesbackend.domain.enums.Template;
import com.example.certificatesbackend.dto.SignedCertificateDTO;
import com.example.certificatesbackend.pki.certificates.CertificateGenerator;
import com.example.certificatesbackend.pki.data.Issuer;
import com.example.certificatesbackend.pki.data.Subject;
import com.example.certificatesbackend.pki.keystores.KeyStoreReader;
import com.example.certificatesbackend.pki.keystores.KeyStoreRepository;
import com.example.certificatesbackend.pki.keystores.KeyStoreWriter;
import com.example.certificatesbackend.repository.ICertificateRepository;
import com.example.certificatesbackend.repository.ICertificateRequestRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

import static com.example.certificatesbackend.constants.Constants.*;
import static com.example.certificatesbackend.pki.certificates.CertificateGenerator.generateRootCertificate;

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

    @Autowired
    public ICertificateRequestRepository requestRepository;


    public Collection<Certificate> getAll() {
        return repository.findByActive(true);
    }

    public Collection<Certificate> getAllRoot() {
        return repository.findAllByTemplateAndActive(Template.ROOT, true);
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
            if (child.isActive()) {
                children.add(child);
                if (!child.getIssuerAlias().equals(child.getAlias())) { // if certificate sign itself
                    // Add the child certificate to the collection of children

                    // Recursively find all children of the current child
                    findAllChildrenRecursive(child, children);
                }
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
        System.out.print(request.getCommonName() + "          " + issuerAlias);

        Subject subject = createSubject(request);
        KeyPair keyPair = generateKeyPair();
        assert keyPair != null;
        subject.setPublicKey(keyPair.getPublic());

        java.security.cert.Certificate[] certificatesChain = storeReader.getCertificateChain(KEYSTORE_PATH, KEYSTORE_PASSWORD, issuerAlias);
        Issuer issuer = storeReader.getIssuer(KEYSTORE_PATH, issuerAlias, KEYSTORE_PASSWORD.toCharArray(), KEYSTORE_PASSWORD.toCharArray());
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

        java.security.cert.Certificate[] newCertificateChain = ArrayUtils.insert(0, certificatesChain, certificate);

        storeWriter.loadKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());
//        storeWriter.write(alias, keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), certificate);
        storeWriter.writeChain(alias, keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), newCertificateChain);
        storeWriter.saveKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());

        Certificate newCertificate = new Certificate(validFrom, validTo, alias, issuerAlias,
                false, null, Template.valueOf(template), request.getCommonName(), request.getOrganization(), request.getUnit(), request.getCountry(), request.getEmail(),
                true);

        repository.save(newCertificate);

        if (request.getId() != null) {
            requestService.delete(request.getId());
        }


//        String cert_pem_path = "src/main/resources/pem/certificate.crt";
//        saveCertToPemFile(certificate, cert_pem_path);

//        String pk_pem_path ="src/main/resources/pem/" + alias + "_pk.pem";
//
//        savePrivateKeyToPemFile(keyPair.getPrivate(), pk_pem_path);


        return newCertificate;
    }

    public void saveCertToPemFile(X509Certificate certificate, String pem_path) throws IOException {
        FileOutputStream fout = new FileOutputStream(pem_path);
        StringWriter writer = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(certificate);
        pemWriter.flush();
        pemWriter.close();
        fout.write(writer.toString().getBytes());
        fout.close();
    }

    public SignedCertificateDTO getCertificatePem(String alias) throws Exception {
        if(repository.findCertificateByAlias(alias)==null){
            if (requestRepository.findRequestByAlias(alias)!=null)
                throw new Exception("Your certificate is not approved yet");
            else {
                throw new Exception("There is no request with your alias");
            }
        }
        System.out.println("ALIASssssss " + alias);
        java.security.cert.Certificate certificate = storeReader.readCertificate(KEYSTORE_PATH, KEYSTORE_PASSWORD,
                alias);

        String pemCertificate;
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            outStream.write("-----BEGIN CERTIFICATE-----\n".getBytes());
            outStream.write(Base64.getEncoder().encode(certificate.getEncoded()));
            outStream.write("\n-----END CERTIFICATE-----\n".getBytes());
            pemCertificate = outStream.toString();
            PrivateKey key = storeReader.readPrivateKey(KEYSTORE_PATH, KEYSTORE_PASSWORD, alias, KEYSTORE_PASSWORD);
            Cipher cipher = Cipher.getInstance("RSA/ECB/NOPADDING");
            cipher.init(Cipher.ENCRYPT_MODE,key );

            SignedCertificateDTO signedCertificateDto = new SignedCertificateDTO();
            signedCertificateDto.setCertificatePem(pemCertificate);
            signedCertificateDto.setDigitalSignature(cipher.doFinal(this.hashSHA256(pemCertificate).getBytes(StandardCharsets.UTF_8)));

            return signedCertificateDto;



        } catch (CertificateEncodingException | IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return null;
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

    public X509Certificate createRootCertificate(Certificate certificate, String uid) throws ParseException, IOException, OperatorCreationException, CRLException {
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

//        storeWriter.saveKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());

        // Generate the certificate
        X509Certificate createdCertificate = generateRootCertificate(subject, keyPair, validFrom, validTo, serialNumber);


        // Write the certificate to the KeyStore
        storeWriter.loadKeyStore(null, KEYSTORE_PASSWORD.toCharArray());
        storeWriter.write(certificate.getAlias(), keyPair.getPrivate(), KEYSTORE_PASSWORD.toCharArray(), createdCertificate);
        storeWriter.saveKeyStore(KEYSTORE_PATH, KEYSTORE_PASSWORD.toCharArray());


        Certificate newCertificate = new Certificate(validFrom, validTo, certificate.getAlias(), certificate.getIssuerAlias(),
                false, null, Template.ROOT, certificate.getCommonName(), certificate.getOrganization(), certificate.getOrganizationUnit(), certificate.getCountry(), certificate.getOwnerEmail(),
                true);
        repository.save(newCertificate);

        saveCRLFile(keyPair.getPrivate(), subject.getX500Name());

//        String root_cert_pem_path = "src/main/resources/pem root/root.crt";
//        saveCertToPemFile(createdCertificate, root_cert_pem_path);
//
//        String root_pk_pem_path = "src/main/resources/pem root/root_pk.pem";
//        savePrivateKeyToPemFile(keyPair.getPrivate(), root_pk_pem_path);

        return createdCertificate;
    }

    private void saveCRLFile(PrivateKey pk, X500Name issuerName)
            throws CRLException, IOException, OperatorCreationException {
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerName, new Date());
        crlBuilder.setNextUpdate(new Date(System.currentTimeMillis() + 86400 * 1000));

        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        contentSignerBuilder.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        X509CRLHolder crlHolder = crlBuilder.build(contentSignerBuilder.build(pk));
        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        X509CRL crl = converter.getCRL(crlHolder);

        byte[] bytes = crl.getEncoded();

        OutputStream os = new FileOutputStream(ROOT_CRL_PATH);
        os.write(bytes);
        os.close();
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

    public void revokeCertificate(Integer id, ReasonForRevoke reason) throws Exception {

        File file = new File(ROOT_CRL_PATH);

        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Stvorena nova datoteka: " + file.getAbsolutePath());
        }

        Certificate certificate = repository.findById(id);

        if (certificate == null) {
            throw new Exception("Certificate with given id doesn't exist");
        }

        Issuer issuer = storeReader.readIssuerFromStore(KEYSTORE_PATH, certificate.getIssuerAlias(), KEYSTORE_PASSWORD.toCharArray(),
                KEYSTORE_PASSWORD.toCharArray());

        byte[] bytes = Files.readAllBytes(file.toPath());

        X509CRLHolder holder = new X509CRLHolder(bytes);
        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(holder);

        java.security.cert.Certificate cer = storeReader.readCertificate(KEYSTORE_PATH, KEYSTORE_PASSWORD, certificate.getAlias());

        JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder((X509Certificate) cer);

        crlBuilder.addCRLEntry(certHolder.getSerialNumber(), new Date(), reason.ordinal());
        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        contentSignerBuilder.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        X509CRLHolder crlHolder = crlBuilder.build(contentSignerBuilder.build(issuer.getPrivateKey()));
        JcaX509CRLConverter converter = new JcaX509CRLConverter();
        converter.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        X509CRL crl = converter.getCRL(crlHolder);

        bytes = crl.getEncoded();

        OutputStream os = new FileOutputStream(ROOT_CRL_PATH);
        os.write(bytes);
        os.close();

        certificate.setRevoked(true);
        certificate.setReason(reason);
        repository.save(certificate);

    }
    public Optional<Certificate> getAliasByCommonName(String commonName){
        return repository.findByCommonName(commonName);
    }

    public String getAliasByOwnerEmail(String ownerEmail){
        Certificate certificate = repository.findByOwnerEmail(ownerEmail).orElse(null);
        assert certificate != null;
        return certificate.getAlias();
    }

    public boolean existsActiveRequestByEmail(String email) {
        return repository.findByOwnerEmailAndActive(email, true).isPresent();
    }


}
