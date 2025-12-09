/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IPropertyDao;
import fr.asipsante.platines.entity.Property;
import org.springframework.stereotype.Repository;

/**
 * @author aboittiaux
 */
@Repository(value = "propertyDao")
public class PropertyDaoImpl extends GenericDaoImpl<Property, Long> implements IPropertyDao {}
