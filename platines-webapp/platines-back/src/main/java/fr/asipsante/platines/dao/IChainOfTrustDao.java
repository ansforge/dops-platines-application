/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.ChainOfTrust;
import java.util.List;

/**
 * Interface provides methods required to link {@link ChainOfTrust} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IChainOfTrustDao extends IGenericDao<ChainOfTrust, Long> {

  /**
   * Gets all the trustChains for a user.
   *
   * @param idUser, the user id
   * @return all the trust chain
   */
  List<ChainOfTrust> getChainByUser(Long idUser);
}
