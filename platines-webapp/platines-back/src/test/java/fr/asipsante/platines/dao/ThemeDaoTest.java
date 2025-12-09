/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/app-config.xml"})
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class
})
@Transactional
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "donnees-theme.xml", type = DatabaseOperation.REFRESH)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Order(5)
public class ThemeDaoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeDaoTest.class);

  @Autowired private IThemeDao themeDao;

  @Test
  @Transactional
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void getById() {
    final Long id = 11l;
    final String name = "ROR";
    final Theme family = themeDao.getById(id);
    assertTrue("Nom attendu ROR", name.equals(family.getName()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void getAll() {

    final String name =
        SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    LOGGER.debug("authentication : " + name);
    final List<Theme> families = themeDao.getAll();
    LOGGER.info("nb famille attendue : " + families.size());
    Assertions.assertTrue(families.size() >= 2, "Deux familles attendues ou plus");
    Assertions.assertTrue(
        families.stream().anyMatch(f -> "ROR".equals(f.getName())), "ROR attendu");
    Assertions.assertTrue(
        families.stream().anyMatch(f -> "RER".equals(f.getName())), "RER attendu");
  }
}
