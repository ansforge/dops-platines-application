/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IApplicationDao;
import fr.asipsante.platines.dao.ICertificateDao;
import fr.asipsante.platines.dao.IChainOfTrustDao;
import fr.asipsante.platines.dao.IUserDao;
import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.CertificateDto;
import fr.asipsante.platines.dto.ChainOfTrustDto;
import fr.asipsante.platines.dto.ChainOfTrustListDto;
import fr.asipsante.platines.entity.Application;
import fr.asipsante.platines.entity.Certificate;
import fr.asipsante.platines.entity.ChainOfTrust;
import fr.asipsante.platines.exception.BrokenRuleException;
import fr.asipsante.platines.mail.entity.CertificateState;
import fr.asipsante.platines.service.ApplicationService;
import fr.asipsante.platines.service.mapper.ApplicationDtoMapper;
import fr.asipsante.platines.service.mapper.CertificateDtoMapper;
import fr.asipsante.platines.service.mapper.ChainOfTrustDtoMapper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aboittiaux
 */
@Service(value = "applicationService")
public class ApplicationServiceImpl implements ApplicationService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

  /** applicationDtoMapper. */
  @Autowired
  @Qualifier("applicationDtoMapper")
  private ApplicationDtoMapper applicationDtoMapper;

  /** applicationDtoMapper. */
  @Autowired
  @Qualifier("chainOfTrustDtoMapper")
  private ChainOfTrustDtoMapper chainOfTrustDtoMapper;

  /** applicationDtoMapper. */
  @Autowired
  @Qualifier("certificateDtoMapper")
  private CertificateDtoMapper certificateDtoMapper;

  /** IApplicationDao. */
  @Autowired
  @Qualifier("applicationDao")
  private IApplicationDao applicationDao;

  /** IUserDao. */
  @Autowired
  @Qualifier("userRefactoringDao")
  private IUserDao userDao;

  /** IChainOfTrustDao. */
  @Autowired
  @Qualifier("chainOfTrustDao")
  private IChainOfTrustDao chainOfTrustDao;

  /** ICertificateDao. */
  @Autowired
  @Qualifier("certificateDao")
  private ICertificateDao certificateDao;

  /** Instantiates a new Application service. */
  public ApplicationServiceImpl() {
    super();
  }

  @Override
  @Transactional
  public List<ApplicationListDto> getApplications() {
    return getListApplicationDto(applicationDao.getAll());
  }

  @Override
  @Transactional
  public List<ApplicationListDto> getApplicationsByUser(Long idUser) {
    final List<ApplicationListDto> applicationsDto = new ArrayList<>();
    final List<Application> applications = applicationDao.getApplicationsByUser(idUser);
    applications.forEach(
        application ->
            applicationsDto.add(applicationDtoMapper.convertToApplicationListDto(application)));
    return applicationsDto;
  }

  @Override
  @Transactional
  public void createApplication(ApplicationDto applicationDto) {
    final Application application = applicationDtoMapper.convertToApplication(applicationDto);
    application.setUser(userDao.getUserByEmail(applicationDto.getUser().getMail()));
    if (applicationDto.getChainOfTrustDto() != null) {
      application.setChainOfTrust(
          chainOfTrustDao.getById(applicationDto.getChainOfTrustDto().getId()));
    } else {
      application.setChainOfTrust(null);
    }
    applicationDao.save(application);
  }

  @Override
  @Transactional
  public void updateApplication(ApplicationDto applicationDto) {
    final Application application = applicationDtoMapper.convertToApplication(applicationDto);
    if (applicationDto.getChainOfTrustDto() == null) {
      application.setChainOfTrust(null);
    }
    applicationDao.update(application);
  }

  @Override
  @Transactional
  public void deleteApplication(Long idApplication) {
    applicationDao.delete(applicationDao.getById(idApplication));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ApplicationDto getById(Long applicationId) {
    final Application application = applicationDao.getById(applicationId);
    return applicationDtoMapper.convertToApplicationDto(application);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Long createChainOfTrust(ChainOfTrustDto chainOfTrustDto) {
    ChainOfTrust chainOfTrust = chainOfTrustDtoMapper.convertToChainOfTrust(chainOfTrustDto);
    chainOfTrust.setUser(userDao.getUserByEmail(chainOfTrustDto.getUser().getMail()));
    chainOfTrust = chainOfTrustDao.saveAndGet(chainOfTrust);
    return chainOfTrust.getId();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateChainOfTrust(ChainOfTrustDto chainOfTrustDto) {
    final ChainOfTrust chainOfTrust = chainOfTrustDtoMapper.convertToChainOfTrust(chainOfTrustDto);
    chainOfTrustDao.update(chainOfTrust);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteChainOfTrust(Long idChainOfTrust) {
    final List<Application> applications = applicationDao.getApplicationsByIdChain(idChainOfTrust);
    if (applications.isEmpty()) {
      final List<Certificate> certificates = certificateDao.getAllByIdChain(idChainOfTrust);
      for (final Certificate certificate : certificates) {
        certificateDao.delete(certificate);
      }
      chainOfTrustDao.delete(chainOfTrustDao.getById(idChainOfTrust));
    } else {
      throw new BrokenRuleException(
          "TrustChainRemovalRule",
          "La chaine de confiance est utilisée par au moins une application");
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public List<ChainOfTrustListDto> getChainOfTrustListDto() {
    final List<ChainOfTrust> chainOfTrusts = chainOfTrustDao.getAll();
    return createListOfChainOfTrustListDto(chainOfTrusts);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public List<ChainOfTrustListDto> getChainOfTrustListDtoByUser(Long idUser) {
    final List<ChainOfTrust> chainOfTrusts = chainOfTrustDao.getChainByUser(idUser);
    return createListOfChainOfTrustListDto(chainOfTrusts);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public ChainOfTrustDto getChainOfTrustById(Long idChain) {
    final ChainOfTrust chainOfTrust = chainOfTrustDao.getById(idChain);
    final ChainOfTrustDto chainOfTrustDto =
        chainOfTrustDtoMapper.convertToChainOfTrustDto(chainOfTrust);
    final List<CertificateDto> certificateDtos = new ArrayList<>();
    final List<Certificate> certificates = certificateDao.getAllByIdChain(idChain);
    for (final Certificate certificate : certificates) {
      certificateDtos.add(certificateDtoMapper.convertToDto(certificate));
    }
    chainOfTrustDto.setCertificate(certificateDtos);
    return chainOfTrustDto;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public CertificateState createCertificate(CertificateDto certificateDto) {
    final Certificate certif = certificateDtoMapper.convertToCertificate(certificateDto);
    final CertificateState certificateState = getValidityDateFromPem(certificateDto.getPem());
    if (certificateState.getValidityDate() != null) {
      certif.setValidityDate(certificateState.getValidityDate());
      certificateDao.save(certif);
    }
    return certificateState;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public CertificateDto getCertificateById(Long idCertificate) {
    return certificateDtoMapper.convertToDto(certificateDao.getById(idCertificate));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public CertificateState updateCertificate(CertificateDto certificateDto) {
    final CertificateState certificateState = getValidityDateFromPem(certificateDto.getPem());
    if (certificateState.getValidityDate() != null) {
      final Certificate certificate = certificateDtoMapper.convertToCertificate(certificateDto);
      certificate.setValidityDate(certificateState.getValidityDate());
      certificateDao.update(certificate);
    }
    return certificateState;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteCertificate(Long idCertficate) {
    final Certificate cert = new Certificate();
    cert.setId(idCertficate);
    certificateDao.delete(cert);
  }

  private List<ChainOfTrustListDto> createListOfChainOfTrustListDto(
      List<ChainOfTrust> chainOfTrusts) {
    final List<ChainOfTrustListDto> chainOfTrustListDtos = new ArrayList<>();
    for (final ChainOfTrust chainOfTrust : chainOfTrusts) {
      final ChainOfTrustListDto chainOfTrustListDto =
          chainOfTrustDtoMapper.convertToChainOfTrustListDto(chainOfTrust);
      List<Certificate> certifs = certificateDao.getAllByIdChain(chainOfTrust.getId());
      for (final Certificate certif : certifs) {
        if (certif.getValidityDate() == null) {
          certif.setValidityDate(getValidityDateFromPem(certif.getPem()).getValidityDate());
          certificateDao.update(certif);
        }
      }
      certifs = certificateDao.getAllByIdChain(chainOfTrust.getId());
      final Predicate<Certificate> predicate = c -> c.getValidityDate().before(new Date());
      if (certifs.isEmpty()) {
        chainOfTrustListDto.setValide(false);
      } else {
        if (certifs.stream().noneMatch(predicate)) {
          chainOfTrustListDto.setValide(true);
        }
      }

      chainOfTrustListDtos.add(chainOfTrustListDto);
    }
    return chainOfTrustListDtos;
  }

  private CertificateState getValidityDateFromPem(String pem) {
    String strEncoded;
    final CertificateState certificateState = new CertificateState();
    try {
      strEncoded = Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));
      final byte[] decodedCertificate = Base64.getDecoder().decode(strEncoded.getBytes());
      final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      final java.security.cert.Certificate certificate =
          certificateFactory.generateCertificate(new ByteArrayInputStream(decodedCertificate));
      final X509Certificate x509Certificate = (X509Certificate) certificate;
      certificateState.setValidityDate(x509Certificate.getNotAfter());
    } catch (final CertificateException e) {
      certificateState.setMsgError("Echec lors de la validation du pem");
      LOGGER.error("Echec lors de la validation du pem : {}", e.getMessage());
    }
    return certificateState;
  }

  private List<ApplicationListDto> getListApplicationDto(List<Application> applications) {
    final List<ApplicationListDto> applicationsList = new ArrayList<>();
    for (final Application application : applications) {
      applicationsList.add(applicationDtoMapper.convertToApplicationListDto(application));
    }
    return applicationsList;
  }
}
