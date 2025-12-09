/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.ITestCertificateDao;
import fr.asipsante.platines.dto.PemTestCertificate;
import fr.asipsante.platines.dto.TestCertificateDto;
import fr.asipsante.platines.entity.TestCertificate;
import fr.asipsante.platines.exception.CertificatePlatinesException;
import fr.asipsante.platines.service.TestCertificateService;
import fr.asipsante.platines.service.mapper.TestCertificateDtoMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "TestCertificateService")
public class TestCertificateServiceImpl implements TestCertificateService {

  /** KEY_STORE_TYPE. */
  private static final String KEY_STORE_TYPE = "PKCS12";

  /** BEGIN_CERT. */
  private static final String CERT_HEADER = "-----BEGIN CERTIFICATE-----";

  /** END_CERT. */
  private static final String CERT_FOOTER = "-----END CERTIFICATE-----";

  /** LINE_SEPARATOR. */
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  /** LINE_LENGTH. */
  private static final int LINE_LENGTH = 64;

  /** KEY_STORE_ERROR. */
  private static final String KEY_STORE_ERROR = "Can not instanciate the key store.";

  /** PASSWORD_CONVERT_ERROR. */
  private static final String STRING_CONVERSION_ERROR =
      "Can not convert the password from String to char Array.";

  /** CERTIFICATE_LOAD_ERROR. */
  private static final String CERTIFICATE_LOAD_ERROR = "Can not load the certificate.";

  /** GET_KEY_ENTRY_ERROR. */
  private static final String GET_KEY_ENTRY_ERROR = "Can not get the key entry.";

  private static final String SERVER_OID = "1.3.6.1.5.5.7.3.1";
  private static final String CLIENT_OID = "1.3.6.1.5.5.7.3.2";
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(TestCertificateServiceImpl.class);

  /** The password to create keystores */
  private final char[] genericSslPasswordData;

  /** Test certificate DAO. */
  @Autowired
  @Qualifier("testCertificateDao")
  private ITestCertificateDao testCertificateDao;

  @Autowired
  @Qualifier("testCertificateDtoMapper")
  private TestCertificateDtoMapper testCertificateDtoMapper;

  public TestCertificateServiceImpl(@Value("${generic.ssl.password}") String genericSslPassword) {
    genericSslPasswordData =
        Objects.requireNonNull(
                genericSslPassword,
                "The SSL_GENERIC_PASSWORD variable or an override through the generic.ssl.password"
                    + " property is mandatory.")
            .toCharArray();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<TestCertificateDto> getAllTestCertificates() {
    final List<TestCertificateDto> testCertificatesDto = new ArrayList<>();
    final List<TestCertificate> testCertificates = testCertificateDao.getAll();
    testCertificates.forEach(
        certificate -> testCertificatesDto.add(testCertificateDtoMapper.convertToDto(certificate)));
    return testCertificatesDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public TestCertificateDto getTestCertificateById(Long id) {
    return testCertificateDtoMapper.convertToDto(testCertificateDao.getById(id));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteTestCertificateById(Long id) {
    final TestCertificate certificate = testCertificateDao.getById(id);
    testCertificateDao.delete(certificate);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void saveTestCertificate(TestCertificateDto testCertificateDto) {
    testCertificateDao.save(testCertificateDtoMapper.convertToEntity(testCertificateDto));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateTestCertificate(TestCertificateDto testCertificateDto) {
    final TestCertificate testCertificate = testCertificateDao.getById(testCertificateDto.getId());
    testCertificate.setDescription(testCertificateDto.getDescription());
    testCertificate.setDownloadable(testCertificateDto.isDownloadable());
    testCertificate.setFileName(testCertificateDto.getFileName());
    if (testCertificateDto.getFile() != null) {
      testCertificate.setFile(testCertificateDto.getFile());
    }
    testCertificate.setValidityDate(testCertificateDto.getValidityDate());
    testCertificate.setState(testCertificateDto.getState());
    testCertificateDao.update(testCertificate);
  }

  /** {@inheritDoc} */
  @Override
  public Date getCertificateDateEndValidity(MultipartFile certificateFile, String password) {
    Date endValidity = null;
    X509Certificate certificate;
    final KeyStore ks;
    try (InputStream input = new ByteArrayInputStream(certificateFile.getBytes())) {

      ks = KeyStore.getInstance(KEY_STORE_TYPE);
      ks.load(input, password.toCharArray());
      final Enumeration<String> aliases = ks.aliases();

      while (aliases.hasMoreElements()) {
        final String alias = aliases.nextElement();
        certificate = (X509Certificate) ks.getCertificate(alias);
        endValidity = certificate.getNotAfter();
      }

    } catch (final KeyStoreException e) {
      throw new CertificatePlatinesException(e, KEY_STORE_ERROR);
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new CertificatePlatinesException(e, STRING_CONVERSION_ERROR);
    } catch (final CertificateException e) {
      throw new CertificatePlatinesException(e, CERTIFICATE_LOAD_ERROR);
    }

    return endValidity;
  }

  /**
   * Returns type of the certificate passed in parameter, CLIENT or SERVER.
   *
   * @param certificateFile
   * @param password
   * @return String type of the certificate
   */
  @Override
  public String getCertificateType(MultipartFile certificateFile, String password) {
    final KeyStore ks;
    try (InputStream input = new ByteArrayInputStream(certificateFile.getBytes())) {

      ks = KeyStore.getInstance(KEY_STORE_TYPE);
      ks.load(input, password.toCharArray());
      final Enumeration<String> aliases = ks.aliases();

      while (aliases.hasMoreElements()) {
        final String alias = aliases.nextElement();
        org.bouncycastle.asn1.x509.Certificate bcCert =
            org.bouncycastle.asn1.x509.Certificate.getInstance(
                ASN1Primitive.fromByteArray(ks.getCertificate(alias).getEncoded()));
        X509CertificateHolder certHolder = new X509CertificateHolder(bcCert);

        try {
          DLSequence extendedKeyUsage =
              (DLSequence) certHolder.getExtension(Extension.extendedKeyUsage).getParsedValue();
          List<ASN1Encodable> oids = Arrays.asList(extendedKeyUsage.toArray());
          if (oids.contains(new ASN1ObjectIdentifier(SERVER_OID))) return "SERVER";
          else if (oids.contains(new ASN1ObjectIdentifier(CLIENT_OID))) return "CLIENT";
        } catch (NullPointerException e) {
          LOGGER.info(
              "No extended key usage found in certificate, setting default value to SIGNATURE");
        }
        return "SIGNATURE";
      }
    } catch (final KeyStoreException e) {
      throw new CertificatePlatinesException(e, KEY_STORE_ERROR);
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new CertificatePlatinesException(e, STRING_CONVERSION_ERROR);
    } catch (final CertificateException e) {
      throw new CertificatePlatinesException(e, CERTIFICATE_LOAD_ERROR);
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public byte[] changeCertificatePassword(MultipartFile certificateFile, String password) {

    final ByteArrayOutputStream certificateOutput = new ByteArrayOutputStream();
    final KeyStore ks;

    try (InputStream input = new ByteArrayInputStream(certificateFile.getBytes())) {

      ks = KeyStore.getInstance(KEY_STORE_TYPE);
      ks.load(input, password.toCharArray());
      final Enumeration<String> aliases = ks.aliases();
      while (aliases.hasMoreElements()) {
        final String alias = aliases.nextElement();
        final PrivateKeyEntry keyEntry =
            (PrivateKeyEntry)
                ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
        final PrivateKey privateKey = keyEntry.getPrivateKey();
        final Certificate[] certificate = ks.getCertificateChain(alias);
        ks.setKeyEntry(alias, privateKey, genericSslPasswordData, certificate);
      }
      ks.store(certificateOutput, genericSslPasswordData);
    } catch (final KeyStoreException e) {
      throw new CertificatePlatinesException(e, KEY_STORE_ERROR);
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new CertificatePlatinesException(e, STRING_CONVERSION_ERROR);
    } catch (final CertificateException e) {
      throw new CertificatePlatinesException(e, CERTIFICATE_LOAD_ERROR);
    } catch (final UnrecoverableEntryException e) {
      throw new CertificatePlatinesException(e, GET_KEY_ENTRY_ERROR);
    }

    return certificateOutput.toByteArray();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public PemTestCertificate getPublicCertificate(Long idCertficate) {

    final TestCertificate testCertificate = testCertificateDao.getById(idCertficate);
    final PemTestCertificate pemTestCertificate = new PemTestCertificate();
    final InputStream in = new ByteArrayInputStream(testCertificate.getFile());
    Base64.Encoder encoder;
    byte[] rawCrtText;
    try {
      final KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
      ks.load(in, genericSslPasswordData);
      Certificate certi = null;
      final Enumeration<String> e = ks.aliases();

      while (e.hasMoreElements()) {
        final String alias = e.nextElement();
        certi = ks.getCertificate(alias);
      }
      encoder = Base64.getMimeEncoder(LINE_LENGTH, LINE_SEPARATOR.getBytes());
      assert certi != null;
      rawCrtText = certi.getEncoded();
      final String encodedCertText = new String(encoder.encode(rawCrtText));
      final String prettifiedCert =
          CERT_HEADER + LINE_SEPARATOR + encodedCertText + LINE_SEPARATOR + CERT_FOOTER;
      pemTestCertificate.setPem(prettifiedCert);
      pemTestCertificate.setFileName(testCertificate.getFileName());
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new CertificatePlatinesException(e, "Certificate extraction error.");
    }
    return pemTestCertificate;
  }
}
