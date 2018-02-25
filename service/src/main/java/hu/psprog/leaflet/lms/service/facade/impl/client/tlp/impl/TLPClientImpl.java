package hu.psprog.leaflet.lms.service.facade.impl.client.tlp.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.lms.service.domain.tlp.LogRequest;
import hu.psprog.leaflet.lms.service.exception.TLPCommunicationException;
import hu.psprog.leaflet.lms.service.facade.impl.client.tlp.TLPClient;
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
 * Implementation of {@link TLPClient}.
 *
 * @author Peter Smith
 */
@Component
public class TLPClientImpl implements TLPClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TLPClientImpl.class);
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception occurred while requesting logs.";

    private static final String QUERY_PARAM_PAGE = "page";
    private static final String QUERY_PARAM_LIMIT = "limit";
    private static final String QUERY_PARAM_ORDER_BY = "orderBy";
    private static final String QUERY_PARAM_ORDER_DIRECTION = "orderDirection";
    private static final String QUERY_PARAM_SOURCE = "source";
    private static final String QUERY_PARAM_LEVEL = "level";
    private static final String QUERY_PARAM_FROM = "from";
    private static final String QUERY_PARAM_TO = "to";
    private static final String QUERY_PARAM_CONTENT = "content";

    private WebTarget webTarget;
    private String logsUrl;

    @Autowired
    public TLPClientImpl(@Value("${tlp.logs-url}") String logsUrl) {
        this.logsUrl = logsUrl;
    }

    @PostConstruct
    public void initClient() {
        webTarget = ClientBuilder.newBuilder()
                .register(new JacksonJsonProvider(configureObjectMapper()))
                .build()
                .target(logsUrl);
    }

    @Override
    public Response getLogs(LogRequest logRequest) throws TLPCommunicationException {

        Response response;
        try {
            response = webTarget
                    .queryParam(QUERY_PARAM_PAGE, logRequest.getPage())
                    .queryParam(QUERY_PARAM_LIMIT, logRequest.getLimit())
                    .queryParam(QUERY_PARAM_ORDER_BY, logRequest.getOrderBy())
                    .queryParam(QUERY_PARAM_ORDER_DIRECTION, logRequest.getOrderDirection())
                    .queryParam(QUERY_PARAM_SOURCE, logRequest.getSource())
                    .queryParam(QUERY_PARAM_LEVEL, logRequest.getLevel())
                    .queryParam(QUERY_PARAM_FROM, logRequest.getFrom())
                    .queryParam(QUERY_PARAM_TO, logRequest.getTo())
                    .queryParam(QUERY_PARAM_CONTENT, logRequest.getContent())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new TLPCommunicationException("Failed to retrieve logs - application response is " + response.getStatus());
            }
        } catch (RuntimeException exc) {
            LOGGER.error(UNEXPECTED_EXCEPTION, exc);
            throw new TLPCommunicationException(UNEXPECTED_EXCEPTION, exc);
        }

        return response;
    }

    private ObjectMapper configureObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }
}
