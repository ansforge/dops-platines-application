/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Resource;
import java.util.List;

/**
 * Interface provides methods required to link {@link Resource} entity to a data source.
 *
 * @author cnader
 */
public interface IResourceDao extends IGenericDao<Resource, Long> {

  List<Resource> getResourcesByAssociation(Long associationId);

  Resource getLastNomenclature(long[] associationsIds);

  List<Resource> getResourcesByAssociations(long[] associationsIds);
}
