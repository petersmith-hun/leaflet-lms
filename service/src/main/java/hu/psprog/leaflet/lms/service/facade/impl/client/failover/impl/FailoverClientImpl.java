package hu.psprog.leaflet.lms.service.facade.impl.client.failover.impl;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
import hu.psprog.leaflet.lms.service.facade.impl.client.failover.FailoverClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Implementation of {@link FailoverClient}.
 *
 * @author Peter Smith
 */
@Component
public class FailoverClientImpl implements FailoverClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailoverClientImpl.class);
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception occurred while requesting failover status.";

    private WebTarget webTarget;
    private String failoverStatusUrl;

    @Autowired
    public FailoverClientImpl(@Value("${failover.status-url}") String failoverStatusUrl) {
        this.failoverStatusUrl = failoverStatusUrl;
    }

    @PostConstruct
    public void initClient() {
        webTarget = ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .build()
                .target(failoverStatusUrl);
    }

    @Override
    public Response getFailoverStatus() throws FailoverCommunicationException {

        Response response;
        try {
            response = webTarget
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new FailoverCommunicationException("Failed to retrieve failover status - application response is " + response.getStatus());
            }
        } catch (RuntimeException exc) {
            LOGGER.error(UNEXPECTED_EXCEPTION, exc);
            throw new FailoverCommunicationException(UNEXPECTED_EXCEPTION, exc);
        }

        return response;
    }
}
