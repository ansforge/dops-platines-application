/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IFunctionalDataDao;
import fr.asipsante.platines.dto.FunctionalDataDto;
import fr.asipsante.platines.entity.FunctionalData;
import fr.asipsante.platines.service.HomeService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author apierre
 */
@Service(value = "homeService")
public class HomeServiceImpl implements HomeService {

  /** KEY_TEXT_HOME_PAGE. */
  private static final String KEY_TEXT_HOME_PAGE = "textHomePage";

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(HomeServiceImpl.class);

  /** IFunctionalDataDao. */
  @Autowired
  @Qualifier("functionalDataDao")
  private IFunctionalDataDao functionalDataDao;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public FunctionalDataDto updateProperties(String content) {
    final FunctionalData functionalData = functionalDataDao.getProperties();
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final FunctionalDataDto functionalDataDto = new FunctionalDataDto();

    if (functionalData.getXmlProp() != null) {
      final Properties props = functionalData.getProps();
      if (props.getProperty(KEY_TEXT_HOME_PAGE) != null) {
        props.setProperty(KEY_TEXT_HOME_PAGE, content);
      } else {
        props.put(KEY_TEXT_HOME_PAGE, content);
      }
      final SimpleDateFormat formater = new SimpleDateFormat("'le' dd-MM-yyyy 'à' HH:mm");
      try {
        props.storeToXML(
            os, "Modification du contenu de la page d'accueil " + formater.format(new Date()));
      } catch (final IOException e) {
        LOGGER.info(e.getMessage());
      }
      functionalData.setXmlProp(os.toByteArray());
      functionalDataDao.persist(functionalData);
      functionalDataDto.setContent(content);
    }
    return functionalDataDto;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public FunctionalDataDto getFunctionalData() {
    final FunctionalDataDto functionalDataDto = new FunctionalDataDto();
    final FunctionalData functionalData = functionalDataDao.getProperties();
    if (functionalData.getXmlProp() != null) {
      functionalDataDto.setContent(functionalData.getProps().getProperty(KEY_TEXT_HOME_PAGE));
    } else {
      functionalDataDto.setContent("");
    }
    return functionalDataDto;
  }
}
