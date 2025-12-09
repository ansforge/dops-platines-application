/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Habituellement un DTO ne mérite pas de test, mais celui-ci comporte désormais un attribut
 * calculé. Test sur cette règle.
 *
 * @author edegenetais
 */
public class TestSessionListDtoTest {
  @Test
  public void clientApplicationHasServerTestSession() {
    TestSessionListDto dto = new TestSessionListDto();
    final ApplicationListDto applicationListDto = new ApplicationListDto();
    applicationListDto.setRole(Role.CLIENT);
    dto.setApplication(applicationListDto);
    Assertions.assertEquals(Role.SERVER, dto.getSimulatedRole());
  }

  @Test
  public void serverApplicationHasClientTestSession() {
    TestSessionListDto dto = new TestSessionListDto();
    final ApplicationListDto applicationListDto = new ApplicationListDto();
    applicationListDto.setRole(Role.SERVER);
    dto.setApplication(applicationListDto);
    Assertions.assertEquals(Role.CLIENT, dto.getSimulatedRole());
  }
}
