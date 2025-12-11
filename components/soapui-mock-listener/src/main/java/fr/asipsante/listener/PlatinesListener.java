package fr.asipsante.listener;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eviware.soapui.model.mock.MockResult;
import com.eviware.soapui.model.mock.MockRunListener;
import com.eviware.soapui.model.mock.MockRunner;

import fr.asipsante.listener.entity.ServerOperation;
import fr.asipsante.listener.publisher.IPublisher;
import fr.asipsante.listener.publisher.impl.PlatinesPublisher;

public class PlatinesListener implements MockRunListener {

    private static final String SESSION = System.getenv("SESSION");
    private IPublisher publisher;

    public MockResult onMockRequest(MockRunner mockRunner, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

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

    public void onMockRunnerStart(MockRunner runner) {
    }

    public void onMockRunnerStop(MockRunner runner) {
    }
}
