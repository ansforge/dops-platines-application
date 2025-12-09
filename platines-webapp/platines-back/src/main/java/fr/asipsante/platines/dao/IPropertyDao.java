/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.Property;

/**
 * Interface provides methods required to link {@link Property} entity to a data source.
 *
 * @author aboittiaux
 */
public interface IPropertyDao extends IGenericDao<Property, Long> {}
