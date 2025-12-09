/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IAssociationDao;
import fr.asipsante.platines.entity.Association;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link IAssociationDao}.
 *
 * @author cnader
 */
@Repository(value = "associationDao")
public class AssociationDaoImpl extends GenericDaoImpl<Association, Long>
    implements IAssociationDao {}
