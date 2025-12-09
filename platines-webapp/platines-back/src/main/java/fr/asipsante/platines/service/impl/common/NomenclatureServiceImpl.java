/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.INomenclatureDao;
import fr.asipsante.platines.entity.Nomenclature;
import fr.asipsante.platines.exception.NomenclatureException;
import fr.asipsante.platines.service.NomenclatureService;
import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link NomenclatureService}.
 *
 * @author apierre
 */
@Service(value = "NomenclatureService")
public class NomenclatureServiceImpl implements NomenclatureService {

  /** NOMENCLATURES_LOAD_ERROR. */
  private static final String NOMENCLATURES_LOAD_ERROR = "Can not load the nomenclature.";

  /** Nomenclature DAO. */
  @Autowired
  @Qualifier("nomenclatureDao")
  private INomenclatureDao nomenclatureDao;

  /** DateConverter. */
  @Autowired private DateConverter dateConverter;

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void saveNomenclature(MultipartFile file, String fileName) {
    try {
      final Nomenclature nomenclature = new Nomenclature();
      nomenclature.setFile(file.getBytes());
      nomenclature.setFileName(fileName);
      nomenclature.setDateUpload(dateConverter.convertToUTC(new Date()));
      nomenclatureDao.save(nomenclature);
    } catch (final IOException e) {
      throw new NomenclatureException(e, NOMENCLATURES_LOAD_ERROR);
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public Date getLastUpdateNomenclature() {
    return nomenclatureDao.getLastUpdateNomenclature();
  }

  @Override
  public Nomenclature getLastNomenclature() {
    return nomenclatureDao.getLastNomenclature();
  }
}
