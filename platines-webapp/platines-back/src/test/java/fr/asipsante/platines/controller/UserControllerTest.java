/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.gson.Gson;
import fr.asipsante.platines.dto.ProfileDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.testutils.AtLeast;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
@DatabaseSetup(value = "donnees-user.xml", type = DatabaseOperation.REFRESH)
@Order(10)
public class UserControllerTest {
  /** Injecter l'instance de WebApplicationContext. */
  @Autowired private WebApplicationContext webApplicationContext;

  /** Sauvegarder l'objet mockMvc. */
  private MockMvc mockMvc;

  /** entity manager. */
  @PersistenceContext private EntityManager entityManager;

  private Gson gson = new Gson();

  /** Initialiser et builder notre mockMVC. */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  // FIXME : ce test devrait éclater
  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void getAll() throws Exception {
    mockMvc
        .perform(get("/secure/user/getAll/"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(AtLeast.atLeast(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id==4)]mail").value("user@user"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.id==3)]mail").value("admin@admin"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[?(@.id==3)].families[?(@.id==11)].name").value("ROR"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[?(@.id==4)].families[?(@.id==11)].name").value("ROR"))
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateUser() throws Exception {
    final long userId = 4L;
    String userJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + userId))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userDto = new ObjectMapper().readValue(userJson, UserDto.class);
    Assertions.assertNotEquals("ANS", userDto.getFirm()); // Sanity test
    userDto.setFirm("ANS");

    final String jsonContent = new ObjectMapper().writeValueAsString(userDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/update")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());

    String userActualJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + userId))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userActualDto = new ObjectMapper().readValue(userActualJson, UserDto.class);
    Assertions.assertEquals("ANS", userActualDto.getFirm()); // Real check
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void createUser() throws Exception {

    List ids = entityManager.createNativeQuery("select id from utilisateur").getResultList();
    LoggerFactory.getLogger(UserControllerTest.class)
        .info(
            "Existing user ids {}",
            ids.stream().map(id -> Long.parseLong(id.toString())).collect(Collectors.toList()));
    UserDto newUser =
        new UserDto(
            null,
            "ann@lyse",
            "Lyse",
            "Ann",
            "ACME corp",
            new Date(),
            new ProfileDto(2L, "user"),
            Set.of());
    final String jsonContent = new ObjectMapper().writeValueAsString(newUser);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/add")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());

    this.mockMvc
        .perform(get("/secure/user/getAll/"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(AtLeast.atLeast(3)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.mail=='ann@lyse')]").exists())
        .andDo(print());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateUserFamiliesWithSpecificMethod() throws Exception {

    String userJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userDto = new ObjectMapper().readValue(userJson, UserDto.class);
    final int initialNbFamilies = userDto.getFamilies().size();

    Assertions.assertFalse(
        userDto.getFamilies().isEmpty(),
        "Expected the user to be linked with at least one family"); // Sanity testing
    userDto.setFamilies(Set.of());

    final String jsonContent = new ObjectMapper().writeValueAsString(userDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/updateFamilies")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());

    String userActualJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userActualDto = new ObjectMapper().readValue(userActualJson, UserDto.class);
    Assertions.assertTrue(userActualDto.getFamilies().isEmpty()); // Real check

    // And back
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/updateFamilies")
                .content(userJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());

    String userActualJson2 =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userActualDto2 = new ObjectMapper().readValue(userActualJson2, UserDto.class);
    Assertions.assertEquals(initialNbFamilies, userActualDto2.getFamilies().size());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateUserFamiliesWithUserUpdate() throws Exception {

    String userJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userDto = new ObjectMapper().readValue(userJson, UserDto.class);
    final int initialNbFamilies = userDto.getFamilies().size();

    Assertions.assertFalse(
        userDto.getFamilies().isEmpty(),
        "Expected the user to be linked with at least one family"); // Sanity testing
    userDto.setFamilies(Set.of());

    final String jsonContent = new ObjectMapper().writeValueAsString(userDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/update")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());

    String userActualJson =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userActualDto = new ObjectMapper().readValue(userActualJson, UserDto.class);
    Assertions.assertTrue(userActualDto.getFamilies().isEmpty()); // Real check

    // And back
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/user/update")
                .content(userJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andDo(print());

    String userActualJson2 =
        this.mockMvc
            .perform(get("/secure/user/get/" + 4L))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final UserDto userActualDto2 = new ObjectMapper().readValue(userActualJson2, UserDto.class);
    Assertions.assertEquals(initialNbFamilies, userActualDto2.getFamilies().size());
  }
}
