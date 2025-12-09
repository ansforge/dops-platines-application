/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Certificate;
import java.util.List;

/**
 * Interface provides methods required to link {@link Certificate} entity to a data source.
 *
 * @author aboittiaux
 */
public interface ICertificateDao extends IGenericDao<Certificate, Long> {

  /**
   * Gets all certificates for a trustChain.
   *
   * @param idChain, the trustChain id
   * @return all the certificates
   */
  List<Certificate> getAllByIdChain(Long idChain);
}
