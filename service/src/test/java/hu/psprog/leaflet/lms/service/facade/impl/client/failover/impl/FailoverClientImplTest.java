package hu.psprog.leaflet.lms.service.facade.impl.client.failover.impl;

import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link FailoverClientImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class FailoverClientImplTest {

    private static final String FAILOVER_STATUS_URL = "local.dev/status";

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder invocationBuilder;

    @Mock
    private Response response;

    private FailoverClientImpl failoverClient;

    @Before
    public void setup() {
        failoverClient = new FailoverClientImpl(FAILOVER_STATUS_URL);
    }

    @Test
    public void shouldInitClient() {

        // when
        failoverClient.initClient();

        // then
        WebTarget currentWebTarget = getWebTarget();
        assertThat(currentWebTarget, notNullValue());
        assertThat(currentWebTarget.getUri(), equalTo(URI.create(FAILOVER_STATUS_URL)));
    }

    @Test
    public void shouldGetFailoverStatus() throws FailoverCommunicationException {

        // given
        setMockWebTarget();
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        given(invocationBuilder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        Response result = failoverClient.getFailoverStatus();

        // then
        assertThat(result, equalTo(response));
    }

    @Test(expected = FailoverCommunicationException.class)
    public void shouldGetFailoverStatusThrowFailoverCommunicationExceptionOnUnsuccessfulResponse() throws FailoverCommunicationException {

        // given
        setMockWebTarget();
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        given(invocationBuilder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        failoverClient.getFailoverStatus();

        // then
        // exception expected
    }

    @Test(expected = FailoverCommunicationException.class)
    public void shouldGetFailoverStatusThrowFailoverCommunicationExceptionOnRuntimeException() throws FailoverCommunicationException {

        // given
        setMockWebTarget();
        doThrow(RuntimeException.class).when(webTarget).request(MediaType.APPLICATION_JSON_TYPE);

        // when
        failoverClient.getFailoverStatus();

        // then
        // exception expected
    }

    private void setMockWebTarget() {
        ReflectionUtils.setField(getWebTargetField(), failoverClient, webTarget);
    }

    private WebTarget getWebTarget() {
        return (WebTarget) ReflectionUtils.getField(getWebTargetField(), failoverClient);
    }

    private Field getWebTargetField() {

        Field webTargetField = ReflectionUtils.findField(FailoverClientImpl.class, "webTarget");
        webTargetField.setAccessible(true);

        return webTargetField;
    }
}