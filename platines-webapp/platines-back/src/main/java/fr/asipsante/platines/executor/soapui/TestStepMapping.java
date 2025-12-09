/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.soapui;

import fr.asipsante.platines.executor.model.TestStepDetail;
import org.w3c.dom.Element;

/**
 * This enum describes test steps we need to map into {@link
 * fr.asipsante.platines.executor.model.ProjectDetail}.
 *
 * @author ericdegenetais
 */
enum TestStepMapping {
  restrequest,
  request,
  assertionteststep, // NOSONAR yes this violates ordinary naming conventions, on purpose, to make
  // the code simpler and easier to read.

  NONE {
    @Override
    public boolean matches(Element candidate) {
      return false;
    }
  };

  /**
   * Check if the candidate inpupt matches the target step type.
   *
   * @param candidate the input to check.
   * @return <code>true</code> if the candidate matches.
   */
  public boolean matches(Element candidate) {
    return name().equals(candidate.getAttribute("type"));
  }

  /**
   * Map the input as a {@link TestStepDetail}.
   *
   * @param input the input.
   * @return the result.
   * @throws IllegalArgumentException if the input does not match the target type. See {@link
   *     #matches(com.eviware.soapui.config.TestStepConfig)}
   */
  public TestStepDetail map(Element input) {
    if (matches(input)) {
      TestStepDetail stepDetail = new TestStepDetail();
      stepDetail.setName(input.getAttribute("name"));
      if (input.getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel()).item(0) != null) {
        stepDetail.setDescription(
            input
                .getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel())
                .item(0)
                .getTextContent());
      }
      return stepDetail;
    } else {
      throw new IllegalArgumentException(
          "The input object doesn't match this steptype " + name() + "\n" + input);
    }
  }
}
