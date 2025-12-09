/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IUserFamilyDao;
import fr.asipsante.platines.entity.UserFamily;
import org.springframework.stereotype.Repository;

/**
 * This class provides the basic implementations needed to make Object-Relational operations on
 * databases, using the Criteria specification.
 *
 * @author apierre
 */
@Repository(value = "userFamilyDao")
public class UserFamilyDaoImpl extends GenericDaoImpl<UserFamily, Long> implements IUserFamilyDao {}
