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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

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

            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true, new BasicConstraints(true));
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));


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

            if (template == Template.CA) {
                certGen.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
                certGen.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));
//                certGen.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(issuer..getPublic()));  //ciji key treba da bude
                certGen.addExtension(X509Extension.certificatePolicies, false, new CertificatePolicies(new PolicyInformation(new ASN1ObjectIdentifier("1.3.6.1.4.1.99999.1"), new DERSequence())));
                certGen.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage));
            } else if (template == Template.INTERMEDIATE) {
                certGen.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
                certGen.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(true));
                certGen.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));
//                certGen.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier());
                certGen.addExtension(X509Extension.certificatePolicies, false, new CertificatePolicies(new PolicyInformation(new ASN1ObjectIdentifier("1.3.6.1.4.1.99999.2"), new DERSequence())));
                certGen.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage));
            } else if (template == Template.END_ENTITY) {
                certGen.addExtension(X509Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
                certGen.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(false));
                certGen.addExtension(X509Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(subject.getPublicKey().getEncoded()));
//                certGen.addExtension(X509Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(keyPair.getPublic()));
                certGen.addExtension(X509Extension.certificatePolicies, false, new CertificatePolicies(new PolicyInformation(new ASN1ObjectIdentifier("1.3.6.1.4.1.99999.3"), new DERSequence())));
                certGen.addExtension(X509Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage));
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
        }
        return null;
    }
}
