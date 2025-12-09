/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.TestCertificate;

/**
 * Interface provides methods required to link {@link TestCertificate} entity to a data source.
 *
 * @author apierre
 */
public interface ITestCertificateDao extends IGenericDao<TestCertificate, Long> {}
