/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.soapui;

import fr.asipsante.platines.executor.model.TestStepDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Test unitaire complémentaire pour les invariants de cette classe.
 *
 * @author edegenetais
 */
public class TestStepConfigMappingTest {
  @ParameterizedTest(name = "onlyOneShouldMatch 4 {0}")
  @ValueSource(strings = {"restrequest", "request", "assertionteststep"})
  public void onlyOneShouldMatch(String type) {
    Element cfg = getStepMock(type);
    int matchCount = 0;
    for (TestStepMapping m : TestStepMapping.values()) {
      if (m.matches(cfg)) {
        matchCount++;
      }
    }
    Assertions.assertEquals(1, matchCount, "Ambigous match for " + type);
  }

  private Element getStepMock(String type) {
    Element cfg = Mockito.mock(Element.class);
    Mockito.when(cfg.getAttribute("type")).thenReturn(type);
    return cfg;
  }

  @ParameterizedTest(name = "{0} should throw if not match")
  @EnumSource(TestStepMapping.class)
  public void shouldRefuseInputIfNotMatch(TestStepMapping testedConfigMapping) {
    final Element neverMatches = getStepMock("@thisWilleNeverMatch@");
    Assertions.assertFalse(
        testedConfigMapping.matches(neverMatches),
        "Invalid test : the never matching elemnt matches on " + testedConfigMapping.name());
    // Test that the expected exception is thrown
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          TestStepDetail d = testedConfigMapping.map(neverMatches);
          LoggerFactory.getLogger(TestStepConfigMappingTest.class)
              .debug("Should reject the never mapping element but emitted " + d);
        });
  }
}
