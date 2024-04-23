package com.example.certificatesbackend.pki.certificates;

import com.example.certificatesbackend.domain.enums.Template;
import com.example.certificatesbackend.pki.data.Issuer;
import com.example.certificatesbackend.pki.data.Subject;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import static com.example.certificatesbackend.constants.Constants.KEYSTORE_PASSWORD;

@Component
public class CertificateGenerator {
    public CertificateGenerator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate generateRootCertificate(Subject subject, KeyPair keyPair, Date startDate, Date endDate, String serialNumber) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(keyPair.getPrivate());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(subject.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    keyPair.getPublic());

            certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

            certGen.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.cRLSign | KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.keyAgreement
                            | KeyUsage.dataEncipherment | KeyUsage.decipherOnly | KeyUsage.encipherOnly
                            | KeyUsage.keyEncipherment | KeyUsage.nonRepudiation));

            KeyPurposeId[] allKeyPurposes = { KeyPurposeId.id_kp_codeSigning, KeyPurposeId.id_kp_clientAuth,
                    KeyPurposeId.id_kp_timeStamping, KeyPurposeId.id_kp_emailProtection, KeyPurposeId.id_kp_serverAuth,
                    KeyPurposeId.id_kp_OCSPSigning, KeyPurposeId.id_kp_sbgpCertAAServerAuth };

            certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(allKeyPurposes));
            DERSequence subjectAlternativeNames = new DERSequence(
                    new ASN1Encodable[] { new GeneralName(GeneralName.dNSName, "*.localhost"),
                            new GeneralName(GeneralName.dNSName, "localhost") });
            certGen.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAlternativeNames);


            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);

        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static X509Certificate generateCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber, Template template) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            if (template == Template.ROOT) {
                // ROOT certificate extension handling
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(0)); // Root sertifikat ima basicConstraints: CA=true, pathLenConstraint=0
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign)); // Korenski sertifikat može potpisivati druge sertifikate i CRL-ove

                // Extended Key Usage za korenski sertifikat
                KeyPurposeId[] rootKeyPurposes = { KeyPurposeId.anyExtendedKeyUsage }; // Korenski sertifikat može biti korišćen za bilo koju svrhu
                certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(rootKeyPurposes));
                // Define key usage for CA
                // Subject Key Identifier ekstenzija
                certGen.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));

            } else if (template == Template.INTERMEDIATE) {
                // INTERMEDIATE certificate extension handling
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true)); // Posrednički sertifikat ima basicConstraints: CA=true

                // Extended Key Usage za posrednički sertifikat
                KeyPurposeId[] intermediateKeyPurposes = { KeyPurposeId.anyExtendedKeyUsage }; // Posrednički sertifikat može biti korišćen za bilo koju svrhu
                certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(intermediateKeyPurposes)); // Define key usage for CA

                certGen.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));


            } else if (template == Template.END_ENTITY) {
                // END_ENTITY certificate extension handling
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(false)); // Završni sertifikat nema basicConstraints ekstenziju ili je postavljena na CA=false

                // Extended Key Usage za završni sertifikat
                KeyPurposeId[] endEntityKeyPurposes = { KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth }; // Završni sertifikat je namenjen za server i klijent autentikaciju
                certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(endEntityKeyPurposes));

                // Subject Alternative Name za završni sertifikat
                GeneralName[] endEntityNames = {
                        new GeneralName(GeneralName.dNSName, "*.localhost"), // Primer Subject Alternative Name za web servere
                        new GeneralName(GeneralName.rfc822Name, "localhost") // Primer Subject Alternative Name za email servere
                };
                DERSequence endEntitySAN = new DERSequence(endEntityNames);
                certGen.addExtension(Extension.subjectAlternativeName, false, endEntitySAN);

                certGen.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));



            }


            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);

        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Extension createAuthorityKeyIdentifier(PublicKey issuerPublicKey) throws IOException {
        byte[] authorityKeyIdentifier = calculateAuthorityKeyIdentifier(issuerPublicKey);
        return new Extension(Extension.authorityKeyIdentifier, false, authorityKeyIdentifier);
    }

    private static byte[] calculateAuthorityKeyIdentifier(PublicKey issuerPublicKey) {
        byte[] keyHash = calculateSHA1(issuerPublicKey.getEncoded());
        return keyHash;
    }


    public static Extension createSubjectKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        byte[] subjectKeyIdentifier = calculateSubjectKeyIdentifier(subjectPublicKeyInfo);
        return new Extension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier);
    }

    private static byte[] calculateSubjectKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        byte[] keyBytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        byte[] keyHash = calculateSHA1(keyBytes);
        byte[] ski = new byte[8];
        System.arraycopy(keyHash, 0, ski, 0, 8);
        return ski;
    }

    private static byte[] calculateSHA1(byte[] data) {
        SHA1Digest sha1 = new SHA1Digest();
        sha1.update(data, 0, data.length);
        byte[] hash = new byte[sha1.getDigestSize()];
        sha1.doFinal(hash, 0);
        return hash;
    }




}
