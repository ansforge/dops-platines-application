/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.listener;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.monitor.WsdlMonitorMessageExchange;
import com.eviware.soapui.model.mock.MockResult;
import com.eviware.soapui.model.mock.MockRunListener;
import com.eviware.soapui.model.mock.MockRunner;
import fr.asipsante.listener.entity.ServerOperation;
import fr.asipsante.listener.publisher.MockResultsPublisher;
import fr.asipsante.listener.publisher.impl.PlatinesPublisher;
import java.util.Date;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpRequest;

public class PlatinesListener implements MockRunListener {

  private static final String SESSION = System.getenv("SESSION");
  private MockResultsPublisher publisher;

  @Override
  public MockResult onMockRequest(
      MockRunner mockRunner, HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  @Override
  public void beforeRoute(
      WsdlProject wsdlProject,
      ServletRequest servletRequest,
      ServletResponse servletResponse,
      HttpRequest httpRequest) {}

  @Override
  public void afterRoute(
      WsdlProject wsdlProject,
      ServletRequest servletRequest,
      ServletResponse servletResponse,
      HttpRequest httpRequest,
      WsdlMonitorMessageExchange wsdlMonitorMessageExchange) {}

  @Override
  public void onMockResult(MockResult result) {
    ServerOperation platinesHistory = new ServerOperation();
    if (SESSION != null) {
      platinesHistory.setUuidSession(SESSION);
    }

    if (result != null) {

      if (result.getMockResponse() != null) {
        platinesHistory.setOperation(result.getMockOperation().getName());
        platinesHistory.setOperationDate(new Date(result.getTimestamp()));
        platinesHistory.setTimeTaken(result.getTimeTaken());
        platinesHistory.setRequest(result.getMockRequest().getRequestContent());
        platinesHistory.setResponse(result.getResponseContent());
        platinesHistory.setResponseName(result.getMockResponse().getName());
      }

      if (System.getenv("PLATINES_PUBLISHER") != null) {
        publisher = new PlatinesPublisher();
        publisher.publish(platinesHistory);
      }
    }
  }

  @Override
  public void onMockRunnerStart(MockRunner runner) {}

  @Override
  public void onMockRunnerStop(MockRunner runner) {}
}
