/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.soapui;

import fr.asipsante.platines.dto.PropertyDto;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.executor.model.TestCaseDetail;
import fr.asipsante.platines.executor.model.TestStepDetail;
import fr.asipsante.platines.executor.model.TestSuiteDetail;
import fr.asipsante.platines.utils.CommentsRemover;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
@ConditionalOnProperty(value = "project.builder", havingValue = "soapui", matchIfMissing = true)
public class SoapUIProjectBuilder implements ProjectBuilder {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SoapUIProjectBuilder.class);

  /** Message d'erreur pour la creation du dom. */
  private static final String ERROR_CREATE_DOM =
      "Erreur lors de la création du dom du parser Soapui ";

  /** Factory utilisée pour le parsing des documents XML */
  private DocumentBuilderFactory dbf;

  /** Document builder utilisé */
  private DocumentBuilder domBuilder;

  public SoapUIProjectBuilder() {
    try {
      this.dbf = DocumentBuilderFactory.newInstance();
      this.domBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      LOGGER.error(ERROR_CREATE_DOM, e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public ProjectDetail getProjectDetail(byte[] projectFile) {

    return read(projectFile);
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getMockContextPath(byte[] projectFile) {
    Document document = getDocument(projectFile);
    List<String> context = new ArrayList<>();
    NodeList nodeListMockService =
        document.getElementsByTagName(SoapUIXmlElements.ROLE_PROJET.getLabel());
    context.addAll(addContext(nodeListMockService));
    nodeListMockService =
        document.getElementsByTagName(SoapUIXmlElements.ROLE_PROJET_REST.getLabel());
    context.addAll(addContext(nodeListMockService));
    return context;
  }

  private ProjectDetail read(byte[] projectFile) {
    Document document = getDocument(projectFile);
    ProjectDetail projectDetail = new ProjectDetail();
    // Récupération de la premiere node qui fait référence au projet SoapUI
    final Node projectNode = document.getFirstChild();
    projectDetail.setProperties(getCustomProperties(projectNode));
    if (document.getElementsByTagName(SoapUIXmlElements.ROLE_PROJET.getLabel()).item(0) != null
        || document.getElementsByTagName(SoapUIXmlElements.ROLE_PROJET_REST.getLabel()).item(0)
            != null) {
      projectDetail.setRole(Role.SERVER);
    } else {
      projectDetail.setRole(Role.CLIENT);
    }
    projectDetail.setName(
        projectNode
            .getAttributes()
            .getNamedItem(SoapUIXmlElements.ATTRIBUTE_NAME.getLabel())
            .getTextContent());
    if (projectNode.getNodeType() == Node.ELEMENT_NODE) {
      Element projectElement = (Element) projectNode;
      if (projectElement.getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel()).item(0)
          != null) {
        String description =
            projectElement
                .getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel())
                .item(0)
                .getTextContent();
        projectDetail.setDescription(CommentsRemover.stripMultiLineComments(description));
      }
      // Récupération de tout les Eléments test Suite du projet SoapUI
      NodeList testSuites =
          projectElement.getElementsByTagName(SoapUIXmlElements.TEST_SUITE.getLabel());
      for (int i = 0; i < testSuites.getLength(); i++) {
        projectDetail.getTestSuiteDetail().add(mapTestSuite(testSuites.item(i)));
      }
    }
    return projectDetail;
  }

  private Document getDocument(byte[] projectFile) {
    Document document = null;
    try {
      document = domBuilder.parse(new ByteArrayInputStream(projectFile));
      document.getDocumentElement().normalize();
    } catch (final SAXException | IOException e) {
      LOGGER.error(ERROR_CREATE_DOM, e);
    }
    return document;
  }

  private List<String> addContext(NodeList mockServices) {
    List<String> context = new ArrayList<>();
    Node nodeMockService = null;
    for (int i = 0; i < mockServices.getLength(); i++) {
      nodeMockService = mockServices.item(i);
      context.add(
          nodeMockService
              .getAttributes()
              .getNamedItem(SoapUIXmlElements.PATH_ATTRIBUTE.getLabel())
              .getNodeValue());
    }

    return context;
  }

  /**
   * Récupération des customs properties du projet.
   *
   * @param soapuiDocument
   */
  private Set<PropertyDto> getCustomProperties(Node projectNode) {
    Set<PropertyDto> propertiesDto = new HashSet<>();
    final NodeList children = projectNode.getChildNodes();
    Node node = null;
    NodeList properties = null;
    for (int i = 0; i < children.getLength(); i++) {
      node = children.item(i);
      if (node.getNodeName().equals(SoapUIXmlElements.PROPERTIES.getLabel())) {
        properties = node.getChildNodes();
      }
    }
    for (int i = 0; i < properties.getLength(); i++) {
      final Node property = properties.item(i);

      final NodeList propertyAttributes = property.getChildNodes();
      final PropertyDto propertyDto = new PropertyDto();
      for (int j = 0; j < propertyAttributes.getLength(); j++) {
        final Node propertyAttribute = propertyAttributes.item(j);
        if (propertyAttribute.getNodeName().equals(SoapUIXmlElements.PROPERTY_NAME.getLabel())) {
          propertyDto.setKey(propertyAttribute.getTextContent());
        } else if (propertyAttribute
            .getNodeName()
            .equals(SoapUIXmlElements.PROPERTY_VALUE.getLabel())) {
          propertyDto.setValue(propertyAttribute.getTextContent());
        }
        propertiesDto.add(propertyDto);
      }
    }
    return propertiesDto;
  }

  /**
   * Récupération des détails de chaque test Suite.
   *
   * @param idTestSuite the id test suite
   * @return the test suite
   */
  private TestSuiteDetail mapTestSuite(Node testSuiteNode) {
    final TestSuiteDetail testSuiteDetail = new TestSuiteDetail();
    if (testSuiteNode.getNodeType() == Node.ELEMENT_NODE) {
      final Element suiteElement = (Element) testSuiteNode;
      testSuiteDetail.setName(
          suiteElement.getAttribute(SoapUIXmlElements.ATTRIBUTE_NAME.getLabel()));
      if (suiteElement.getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel()).item(0)
          != null) {
        String description =
            suiteElement
                .getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel())
                .item(0)
                .getTextContent();
        testSuiteDetail.setDescription(CommentsRemover.stripMultiLineComments(description));
      }
      NodeList testCases =
          suiteElement.getElementsByTagName(SoapUIXmlElements.TEST_CASE.getLabel());
      for (int i = 0; i < testCases.getLength(); i++) {
        testSuiteDetail.getListTestCase().add(mapTestCase(testCases.item(i)));
      }
    }
    return testSuiteDetail;
  }

  /**
   * Gets the test case.
   *
   * @param idTestCase the id test case
   * @param testSuite the test suite
   * @return the test case
   */
  private TestCaseDetail mapTestCase(Node testCaseNode) {
    final TestCaseDetail testCaseDetail = new TestCaseDetail();
    if (testCaseNode.getNodeType() == Node.ELEMENT_NODE) {
      final Element caseElement = (Element) testCaseNode;
      testCaseDetail.setName(caseElement.getAttribute(SoapUIXmlElements.ATTRIBUTE_NAME.getLabel()));
      if (caseElement.getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel()).item(0)
          != null) {
        String description =
            caseElement
                .getElementsByTagName(SoapUIXmlElements.DESCRIPTION.getLabel())
                .item(0)
                .getTextContent();
        testCaseDetail.setDescription(CommentsRemover.stripMultiLineComments(description));
      }
      final NodeList properties =
          caseElement.getElementsByTagName(SoapUIXmlElements.PROPERTIES.getLabel());
      for (int x = 0; x < properties.getLength(); x++) {
        final Node property = properties.item(x);
        if (property.getNodeType() == Node.ELEMENT_NODE) {
          final Element propertyElement = (Element) property;
          for (int i = 0;
              i
                  < propertyElement
                      .getElementsByTagName(SoapUIXmlElements.PROPERTY.getLabel())
                      .getLength();
              i++) {
            if ("responseName"
                .equals(
                    propertyElement
                        .getElementsByTagName(SoapUIXmlElements.PROPERTY_NAME.getLabel())
                        .item(i)
                        .getTextContent())) {
              testCaseDetail.setResponseName(
                  propertyElement
                      .getElementsByTagName(SoapUIXmlElements.PROPERTY_VALUE.getLabel())
                      .item(i)
                      .getTextContent());
            }
          }
        }
      }
      NodeList testSteps = caseElement.getElementsByTagName(SoapUIXmlElements.TEST_STEP.getLabel());
      for (int i = 0; i < testSteps.getLength(); i++) {
        testCaseDetail.getListTestSteps().addAll(mapTestStep(testSteps.item(i)));
      }
    }
    return testCaseDetail;
  }

  /**
   * Map the TestStepConfig incoming element if it is supported/wanted in the outgoing model.
   *
   * @param testStepNode
   * @return the testStepDetail in a list or an emptyList if step do not match
   */
  private List<TestStepDetail> mapTestStep(Node testStepNode) {
    List<TestStepDetail> result = new ArrayList<>(1);
    if (testStepNode.getNodeType() == Node.ELEMENT_NODE) {
      final Element stepElement = (Element) testStepNode;
      for (TestStepMapping targetType : TestStepMapping.values()) {
        if (targetType.matches(stepElement)) {
          result.add(targetType.map(stepElement));
        }
      }
      if (result.isEmpty()) {
        LOGGER.debug("Ignoring unwanted step of type {}", stepElement.getAttribute("type"));
      }
    }
    return result;
  }
}
