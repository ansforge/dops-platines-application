/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import static org.junit.Assert.assertTrue;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@DatabaseSetup(value = "donnees-userDao.xml", type = DatabaseOperation.REFRESH)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Order(9)
public class UserDaoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);

  @Autowired
  @Qualifier("userRefactoringDao")
  private IUserDao userDao;

  @Autowired private IThemeDao themeDao;

  @Test
  @Transactional
  @WithUserDetails(value = "mylimited@user", userDetailsServiceBeanName = "myUserDetails")
  public void getById() {
    final Long userId = 7l;
    final Long familyId = 1l;
    final User user = userDao.getById(userId);
    final Theme family = themeDao.getById(familyId);
    user.addFamily(family);
    assertTrue(
        "Nom attendu pour la famille à associer à l'utilisateur",
        "famille".equals(family.getName()));

    userDao.save(user);
  }

  @Test
  @WithUserDetails(value = "mylimited@user", userDetailsServiceBeanName = "myUserDetails")
  public void getAll() {

    final String name =
        SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    LOGGER.debug("authentication : " + name);
    final List<User> users = userDao.getAll();
    LOGGER.info("nb famille attendue : " + users.size());
    Assertions.assertEquals(1, users.size(), "Nombre d'utilisateurs attendus");
  }
}
