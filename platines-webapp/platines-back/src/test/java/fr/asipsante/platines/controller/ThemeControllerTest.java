/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.gson.Gson;
import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.testutils.AtLeast;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
@DatabaseSetup(value = "donnees-theme.xml", type = DatabaseOperation.REFRESH)
@Order(10)
public class ThemeControllerTest {

  /** Injecter l'instance de WebApplicationContext. */
  @Autowired private WebApplicationContext webApplicationContext;

  /** Sauvegarder l'objet mockMvc. */
  private MockMvc mockMvc;

  private Gson gson = new Gson();

  /** Initialiser et builder notre mockMVC. */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @WithUserDetails(value = "user@user", userDetailsServiceBeanName = "myUserDetails")
  public void authorizationUser() {
    final Long id = 11L;
    try {
      mockMvc.perform(get("/secure/family/get/" + id)).andDo(print());
    } catch (final Exception e) {
      Assertions.assertEquals(
          e.getCause().getClass(), AccessDeniedException.class, "access denied expected");
    }
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void authorizationAdmin() throws Exception {
    final Long id = 11L;
    mockMvc.perform(get("/secure/family/get/" + id)).andExpect(status().isOk()).andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateFamilySuccess() throws Exception {
    ThemeDto family = new ThemeDto();
    family.setName("PlatineS");
    family.setVisibility(true);
    family.setId(12l);
    family.setUsers(Collections.<UserDto>emptySet());
    String familyJson = gson.toJson(family);
    mockMvc
        .perform(
            post("/secure/family/update")
                .content(familyJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateSystemSuccess() throws Exception {
    ThemeDto family = new ThemeDto();
    family.setName("PLATINES");
    family.setVisibility(true);
    family.setId(12l);
    family.setUsers(Collections.<UserDto>emptySet());
    SimulatedServiceDto system = new SimulatedServiceDto();
    system.setId(10l);
    system.setTheme(family);
    system.setName("Systeme simule");

    String systemJson = gson.toJson(system);
    mockMvc
        .perform(
            post("/secure/system/update")
                .content(systemJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void singleCoverageFoundOnRORFamily() throws Exception {
    mockMvc
        .perform(get("/secure/family/role-coverage/getAll").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[?(@.family.id == 11)].coveredRoles.length()")
                .value(1))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[?(@.family.id == 11)].coveredRoles[0]")
                .value(Role.CLIENT.name()))
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void allCoverageFoundOnPLATINESFamily() throws Exception {
    mockMvc
        .perform(get("/secure/family/role-coverage/getAll").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[?(@.family.id == 12)].coveredRoles.length()")
                .value(2))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                    "$[?(@.family.id == 12)].coveredRoles[?(@ == \"CLIENT\")]")
                .exists())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                    "$[?(@.family.id == 12)].coveredRoles[?(@ == \"SERVER\")]")
                .exists())
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void deleteWorks() throws Exception {
    final int kennyThemeId = 13;
    // precondition : existence du thème id=13
    mockMvc
        .perform(get("/secure/family/get/" + kennyThemeId))
        .andExpect(status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Kenny"))
        .andDo(print());
    mockMvc
        .perform(delete("/secure/family/delete/" + kennyThemeId))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());
    // postcondition : plus de thème id=13
    mockMvc
        .perform(get("/secure/family/get/" + kennyThemeId))
        .andExpect(status().is(404))
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void getAll() throws Exception {
    mockMvc
        .perform(get("/secure/family/getAll/"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(AtLeast.atLeast(3)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id==11)]name").value("ROR"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id==12)]name").value("PLATINES"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id==13)]name").value("Kenny"))
        .andDo(print());
  }
}
