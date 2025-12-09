/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IFunctionalDataDao;
import fr.asipsante.platines.entity.FunctionalData;
import fr.asipsante.platines.entity.enums.KeyPreference;
import fr.asipsante.platines.exception.BrokenRuleException;
import fr.asipsante.platines.service.PreferencesService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aboittiaux
 */
@Service
public class PreferencesServiceImpl implements PreferencesService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(PreferencesServiceImpl.class);

  /** IFunctionalDataDao. */
  @Autowired
  @Qualifier("functionalDataDao")
  private IFunctionalDataDao functionalDataDao;

  /*
   * (non-Javadoc)
   *
   * @see fr.asipsante.platines.service.IPreferencesService#getPreferences()
   */
  @Transactional
  @Override
  public Properties getPreferences() {
    FunctionalData fd = functionalDataDao.getProperties();
    if (fd == null) {
      fd = create();
      fd = functionalDataDao.persistAndGet(fd);
    }

    return fd.getProps();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * fr.asipsante.platines.service.IPreferencesService#updatePreferences(java.util
   * .Map)
   */
  @Transactional
  @Override
  public void updatePreferences(Map<String, Object> preference) {
    FunctionalData functionalData = functionalDataDao.getProperties();
    if (functionalData == null) {
      functionalData = create();
    }
    final Properties props = functionalData.getProps();
    for (Map.Entry<String, Object> entry : preference.entrySet()) {
      final String key = entry.getKey();
      Object value = entry.getValue();
      value = checkProperty(key, value);
      props.setProperty(key, value.toString());
    }
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final SimpleDateFormat formater = new SimpleDateFormat("'le' dd-MM-yyyy 'à' HH:mm");
    try {
      props.storeToXML(
          os,
          "Modification des la propriétés "
              + preference.keySet()
              + " "
              + formater.format(new Date()));
      functionalData.setXmlProp(os.toByteArray());
      functionalDataDao.persist(functionalData);
    } catch (final IOException e) {
      LOGGER.error("Erreur lors de la modification d'une préférence", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * fr.asipsante.platines.service.IPreferencesService#addPreference(java.util.
   * Map)
   */
  @Transactional
  @Override
  public void addPreference(Map<String, Object> preference) {
    FunctionalData functionalData = functionalDataDao.getProperties();
    if (functionalData == null) {
      functionalData = create();
    }
    final Properties props = functionalData.getProps();
    for (Map.Entry<String, Object> entry : preference.entrySet()) {
      final String key = entry.getKey();
      final Object value = entry.getValue();
      checkProperty(key, value);

      props.put(key, value);
    }
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final SimpleDateFormat formater = new SimpleDateFormat("'le' dd-MM-yyyy 'à' HH:mm");
    try {
      props.storeToXML(
          os,
          "Modification de la propriété "
              + preference.keySet()
              + " "
              + formater.format(new Date()));
      functionalData.setXmlProp(os.toByteArray());
      functionalDataDao.persist(functionalData);
    } catch (final IOException e) {
      LOGGER.error("Erreur lors de la modification d'une préférence", e);
    }
  }

  private Object checkProperty(String key, Object value) {
    try {
      if (KeyPreference.TEXT_HOME_PAGE.getValue().equals(key)) {
        // Empty on purpose
      } else if (KeyPreference.BLOCKED_ACCOUNT_DURATION.getValue().equals(key)) {
        value = Long.parseLong((String) value);
      } else if (KeyPreference.LIMIT_TRY_AUTHENTICATION.getValue().equals(key)) {
        value = Long.parseLong((String) value);
      } else if (KeyPreference.TOKEN_DURATION.getValue().equals(key)) {
        value = Long.parseLong((String) value);
        if (((Long) value) <= 0) {
          throw new BrokenRuleException(
              "PreferenceValidityRule", "La durée du token doit être un nombre supérieur à zéro.");
        }
      } else if (KeyPreference.PROVISIONNING_TIMEOUT.getValue().equals(key)) {
        value = Long.parseLong((String) value);
      } else if (KeyPreference.LIST_EMAIL_NOTIFICATION.getValue().equals(key)) {

        final Pattern pattern =
            Pattern.compile(
                "^([a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*;?\\s*)+$");
        value = ((String) value).trim();
        final Matcher matcher = pattern.matcher((CharSequence) value);
        if (!matcher.find()) {
          throw new BrokenRuleException(
              "PreferenceValidityRule",
              "La liste des adresses mail ne convient pas au format attendu");
        }
      }
    } catch (NumberFormatException ne) {
      throw new BrokenRuleException(
          "PreferenceValidityRule", "La valeur attendue est un nombre entier.", ne);
    }
    return value;
  }

  private FunctionalData create() {
    return new FunctionalData();
  }
}
