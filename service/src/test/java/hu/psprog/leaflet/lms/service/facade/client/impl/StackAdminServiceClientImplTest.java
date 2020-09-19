package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link StackAdminServiceClientImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class StackAdminServiceClientImplTest {

    private static final RegisteredServices REGISTERED_SERVICES = new RegisteredServices(Arrays.asList("SVC1", "SVC2", "SVC3"));
    private static final List<Container> CONTAINER_LIST = Collections.singletonList(Container.getBuilder().withId("1234").build());
    private static final GenericType<List<Container>> CONTAINER_LIST_GENERIC_TYPE = new GenericType<>() {};
    private static final String REGISTERED_SERVICES_ENDPOINT = "/registered/services/endpoint";
    private static final String EXISTING_CONTAINERS_ENDPOINT = "/existing/containers/endpoint";

    @Mock
    private Client client;

    @Mock
    private Response response;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder builder;

    @Mock
    private StackStatusConfigModel stackStatusConfigModel;

    @Mock
    private DockerClusterStatusConfigModel dockerClusterStatusConfigModel;

    @InjectMocks
    private StackAdminServiceClientImpl stackAdminServiceClient;

    @Test
    public void shouldGetRegisteredServicesReturnWithSuccess() {

        // given
        given(stackStatusConfigModel.getRegisteredServicesEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);
        given(response.readEntity(RegisteredServices.class)).willReturn(REGISTERED_SERVICES);

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, equalTo(REGISTERED_SERVICES));
    }

    @Test
    public void shouldGetRegisteredServicesReturnNullForNonSuccessfulResponse() {

        // given
        given(stackStatusConfigModel.getRegisteredServicesEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetRegisteredServicesReturnNullWhenClientThrowsException() {

        // given
        given(stackStatusConfigModel.getRegisteredServicesEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldGetExistingContainersReturnWithSuccess() {

        // given
        given(dockerClusterStatusConfigModel.getExistingContainersEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);
        given(response.readEntity(CONTAINER_LIST_GENERIC_TYPE)).willReturn(CONTAINER_LIST);

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        assertThat(result, equalTo(CONTAINER_LIST));
    }

    @Test
    public void shouldGetExistingContainersReturnEmptyListForNonSuccessfulResponse() {

        // given
        given(dockerClusterStatusConfigModel.getExistingContainersEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verifyNoMoreInteractions(response);
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetExistingContainersReturnEmptyListWhenClientThrowsException() {

        // given
        given(dockerClusterStatusConfigModel.getExistingContainersEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        verifyNoMoreInteractions(response);
        assertThat(result, equalTo(Collections.emptyList()));
    }
}
