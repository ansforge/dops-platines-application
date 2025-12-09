/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author edegenetais
 */
/**
 * @author edegenetais
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class
})
@ContextConfiguration(locations = {"/app-config.xml"})
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "donnees-version.xml", type = DatabaseOperation.REFRESH)
public class VersionControllerTest {
  /** Injecter l'instance de WebApplicationContext. */
  @Autowired private WebApplicationContext webApplicationContext;

  /** Sauvegarder l'objet mockMvc. */
  private MockMvc mockMvc;

  /** Initialiser et builder notre mockMVC. */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @ParameterizedTest
  @CsvSource({"1,systeme1.v1", "11,systeme.v0"})
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void getVersionByThemeId(long themeId, String expectedName) throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/secure/version/getVersions/" + themeId))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].label").value(expectedName));
  }
}
