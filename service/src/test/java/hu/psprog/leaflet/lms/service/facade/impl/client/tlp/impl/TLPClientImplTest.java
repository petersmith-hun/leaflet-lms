package hu.psprog.leaflet.lms.service.facade.impl.client.tlp.impl;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.lms.service.domain.tlp.LogRequest;
import hu.psprog.leaflet.lms.service.exception.TLPCommunicationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TLPClientImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TLPClientImplTest {

    private static final String TLP_LOGS_URL = "local.dev/logs";
    private static final LogRequest LOG_REQUEST = new LogRequest();

    private static final List<String> EXPECTED_QUERY_PARAMS = Arrays.asList("page", "limit", "orderBy", "orderDirection", "source", "level", "from", "to", "content");

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder invocationBuilder;

    @Mock
    private Response response;

    private TLPClientImpl tlpClient;

    @Before
    public void setup() {
        tlpClient = new TLPClientImpl(TLP_LOGS_URL);
        given(webTarget.queryParam(anyString(), any())).willReturn(webTarget);
    }

    @Test
    public void shouldInitClient() {

        // when
        tlpClient.initClient();

        // then
        WebTarget currentWebTarget = getWebTarget();
        assertThat(currentWebTarget, notNullValue());
        assertThat(currentWebTarget.getUri(), equalTo(URI.create(TLP_LOGS_URL)));
        assertThat(currentWebTarget.getConfiguration().isRegistered(JacksonJsonProvider.class), is(true));
    }

    @Test
    public void shouldGetLogs() throws TLPCommunicationException {

        // given
        setMockWebTarget();
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        given(invocationBuilder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        Response result = tlpClient.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(response));
        EXPECTED_QUERY_PARAMS.forEach(param -> verify(webTarget).queryParam(eq(param), any()));
    }

    @Test(expected = TLPCommunicationException.class)
    public void shouldGetLogsThrowTLPCommunicationExceptionOnUnsuccessfulResponse() throws TLPCommunicationException {

        // given
        setMockWebTarget();
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        given(invocationBuilder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        tlpClient.getLogs(LOG_REQUEST);

        // then
        // exception expected
    }

    @Test(expected = TLPCommunicationException.class)
    public void shouldGetLogsThrowTLPCommunicationExceptionOnRuntimeException() throws TLPCommunicationException {

        // given
        setMockWebTarget();
        doThrow(RuntimeException.class).when(webTarget).request(MediaType.APPLICATION_JSON_TYPE);

        // when
        tlpClient.getLogs(LOG_REQUEST);

        // then
        // exception expected
    }

    private void setMockWebTarget() {
        ReflectionUtils.setField(getWebTargetField(), tlpClient, webTarget);
    }

    private WebTarget getWebTarget() {
        return (WebTarget) ReflectionUtils.getField(getWebTargetField(), tlpClient);
    }

    private Field getWebTargetField() {

        Field webTargetField = ReflectionUtils.findField(TLPClientImpl.class, "webTarget");
        webTargetField.setAccessible(true);

        return webTargetField;
    }
}