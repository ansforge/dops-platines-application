/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.gson.Gson;
import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ProjectResultDto;
import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
@DatabaseSetup(value = "data-session.xml", type = DatabaseOperation.REFRESH)
@Order(12)
public class SessionControllerTest {

  /** Injecter l'instance de WebApplicationContext. */
  @Autowired private WebApplicationContext webApplicationContext;

  /** Sauvegarder l'objet mock mvc. */
  private MockMvc mockMvc;

  private Gson gson = new Gson();

  /** Initialiser et builde le mockmvc. */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  // FIXME : vrais CT ? Réactiver !!!
  //    @Test
  //    @WithUserDetails(value = "manager", userDetailsServiceBeanName = "myUserDetails")
  //    public void getSessionDetailsSuccess() throws Exception {
  //        Long id = 21l;
  //        mockMvc.perform(MockMvcRequestBuilders.get("/secure/testSession/detail/" +
  // id)).andExpect(status().is2xxSuccessful()).andDo(print());
  //    }
  //
  //    @Test
  //    @WithUserDetails(value = "manager", userDetailsServiceBeanName = "myUserDetails")
  //    public void duplicateSession() throws Exception {
  //        Long id = 22l;
  //        String token = "manager 1234";
  //        mockMvc.perform(MockMvcRequestBuilders.get("/secure/testSession/duplicate/" +
  // id).header("Authorization", token)).andExpect(status().is2xxSuccessful()).andDo(print());
  //
  //    }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void createSession() throws Exception {
    final TestSessionDto session = new TestSessionDto();
    final ApplicationDto application = new ApplicationDto();
    final VersionDto version = new VersionDto();
    final ThemeDto famille = new ThemeDto();
    final SimulatedServiceDto simulatedService = new SimulatedServiceDto();
    final UserDto user = new UserDto();

    user.setId(4l);
    user.setName("user");
    user.setMail("user@user");

    famille.setId(1l);
    famille.setName("famille");
    Set<UserDto> users = new HashSet<>();
    users.add(user);
    famille.setUsers(users);

    simulatedService.setId(2l);
    simulatedService.setName("systeme");
    simulatedService.setTheme(famille);

    version.setId(3l);
    version.setDescription("desc");
    version.setLabel("version");
    version.setService(simulatedService);

    application.setId(1l);
    application.setDescription("desc");
    application.setName("name");
    application.setRole(Role.CLIENT);
    application.setUrl("url");
    application.setUser(user);

    session.setDescription("session nouvelle");
    session.setApplication(application);
    session.setVersion(version);
    session.setProjectResults(Collections.<ProjectResultDto>emptyList());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/secure/testSession/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(gson.toJson(session)))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andDo(MockMvcResultHandlers.print());
  }
}
