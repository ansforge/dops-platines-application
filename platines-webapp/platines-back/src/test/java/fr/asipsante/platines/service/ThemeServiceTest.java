/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.dto.FamilyTreeDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

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
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "donnees-theme.xml", type = DatabaseOperation.REFRESH)
@Order(3)
public class ThemeServiceTest {

  @Autowired ThemeService themeService;

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testGetAllFamilyAdmin() {
    final List<ThemeDto> families = themeService.getAllFamilies();
    Assertions.assertEquals(3, families.size(), "On doit voir tous les thèmes.");
  }

  @Test
  @WithUserDetails(value = "user@user", userDetailsServiceBeanName = "myUserDetails")
  public void testGetAllFamilyUser() {
    final List<ThemeDto> families = themeService.getAllFamilies();
    Assertions.assertEquals(
        2, families.size(), "Seules les thèmes associés 1 et 254 doivent remonter.");
    final long them1Id = 1l;
    Assertions.assertTrue(
        families.stream().map(t -> t.getId()).anyMatch(id -> id == them1Id),
        "Doit contenir le Thème " + them1Id);
    final long them2Id = 254l;
    Assertions.assertTrue(
        families.stream().map(t -> t.getId()).anyMatch(id -> id == them2Id),
        "Doit contenir le Thème " + them2Id);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testGetAllFamilyTreeAdmin() {
    final List<FamilyTreeDto> families = themeService.getAllFamiliesAsTree();
    Assertions.assertEquals(3, families.size(), "On doit voir tous les thèmes.");
  }
}
