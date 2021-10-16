package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
@ExtendWith(MockitoExtension.class)
public class StackAdminServiceClientImplTest {

    private static final RegisteredServices REGISTERED_SERVICES = new RegisteredServices(Arrays.asList("SVC1", "SVC2", "SVC3"));
    private static final List<Container> CONTAINER_LIST = Collections.singletonList(Container.getBuilder().withId("1234").build());
    private static final GenericType<List<Container>> CONTAINER_LIST_GENERIC_TYPE = new GenericType<>() {};
    private static final String REGISTRY_ID = "registry-1";
    private static final String REPOSITORY_ID = "repository-1";
    private static final String TAG = "1.0";
    private static final String REGISTERED_SERVICES_ENDPOINT = "/registered/services/endpoint";
    private static final String EXISTING_CONTAINERS_ENDPOINT = "/existing/containers/endpoint";
    private static final String DOCKER_REGISTRY_BROWSER_ENDPOINT = "/docker/registry/browser/endpoint";
    private static final String DOCKER_REGISTRY_BROWSER_REGISTRY_ENDPOINT =
            String.format("%s/%s", DOCKER_REGISTRY_BROWSER_ENDPOINT, REGISTRY_ID);
    private static final String DOCKER_REGISTRY_BROWSER_REPOSITORY_ENDPOINT =
            String.format("%s/%s/%s", DOCKER_REGISTRY_BROWSER_ENDPOINT, REGISTRY_ID, REPOSITORY_ID);
    private static final String DOCKER_REGISTRY_BROWSER_TAG_ENDPOINT =
            String.format("%s/%s/%s/%s", DOCKER_REGISTRY_BROWSER_ENDPOINT, REGISTRY_ID, REPOSITORY_ID, TAG);
    private static final String API_KEY_FOR_REGISTERED_SERVICES = "api-key-for-registered-services";
    private static final String API_KEY_FOR_CONTAINERS = "api-key-for-stack-status";
    private static final String API_KEY_HEADER = "X-Api-Key";
    private static final DockerRegistryContent FALLBACK_DOCKER_REGISTRY_CONTENT =
            new DockerRegistryContent("unknown", Collections.emptyList());
    private static final DockerRepository FALLBACK_DOCKER_REPOSITORY =
            new DockerRepository("unknown", "unknown", Collections.emptyList());


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
        given(stackStatusConfigModel.getDefaultEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_REGISTERED_SERVICES);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_REGISTERED_SERVICES)).willReturn(builder);
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
        given(stackStatusConfigModel.getDefaultEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_REGISTERED_SERVICES);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_REGISTERED_SERVICES)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetRegisteredServicesReturnNullWhenClientThrowsException() {

        // given
        given(stackStatusConfigModel.getDefaultEndpoint()).willReturn(REGISTERED_SERVICES_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_REGISTERED_SERVICES);
        given(client.target(REGISTERED_SERVICES_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_REGISTERED_SERVICES)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldGetExistingContainersReturnWithSuccess() {

        // given
        given(dockerClusterStatusConfigModel.getDefaultEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(dockerClusterStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
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
        given(dockerClusterStatusConfigModel.getDefaultEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(dockerClusterStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetExistingContainersReturnEmptyListWhenClientThrowsException() {

        // given
        given(dockerClusterStatusConfigModel.getDefaultEndpoint()).willReturn(EXISTING_CONTAINERS_ENDPOINT);
        given(dockerClusterStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(EXISTING_CONTAINERS_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        verifyNoMoreInteractions(response);
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnWithSuccess() {

        // given
        Map<String, String> responseBody = Map.of("registry-1", "http://localhost");
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);
        given(response.readEntity(new GenericType<Map<String, String>>(){})).willReturn(responseBody);

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(responseBody));
        verify(response).close();
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnFallbackValueForNonSuccessfulResponse() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(Collections.emptyMap()));
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnFallbackValueOnException() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(Collections.emptyMap()));
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetDockerRepositoriesReturnWithSuccess() {

        // given
        DockerRegistryContent responseBody = new DockerRegistryContent(REGISTRY_ID, Collections.emptyList());
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REGISTRY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);
        given(response.readEntity(new GenericType<DockerRegistryContent>(){})).willReturn(responseBody);

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(responseBody));
        verify(response).close();
    }

    @Test
    public void shouldGetDockerRepositoriesReturnFallbackValueForNonSuccessfulResponse() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REGISTRY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REGISTRY_CONTENT));
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetDockerRepositoriesReturnFallbackValueOnException() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REGISTRY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REGISTRY_CONTENT));
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnWithSuccess() {

        // given
        DockerRepository responseBody = new DockerRepository(REGISTRY_ID, REPOSITORY_ID, Collections.emptyList());
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REPOSITORY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);
        given(response.readEntity(new GenericType<DockerRepository>(){})).willReturn(responseBody);

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(responseBody));
        verify(response).close();
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnFallbackValueForNonSuccessfulResponse() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REPOSITORY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.get()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REPOSITORY));
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnFallbackValueOnException() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_REPOSITORY_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).get();

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REPOSITORY));
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldDeleteDockerImageByTagProcessRequestWithSuccess() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_TAG_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.delete()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        verify(response).getStatusInfo();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldDeleteDockerImageByTagSilentlyFailForNonSuccessfulResponse() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_TAG_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        given(builder.delete()).willReturn(response);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        verify(response).getStatusInfo();
        verify(response).getStatus();
        verify(response).close();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void shouldDeleteDockerImageByTagSilentlyFailOnException() {

        // given
        given(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint()).willReturn(DOCKER_REGISTRY_BROWSER_ENDPOINT);
        given(stackStatusConfigModel.getApiKey()).willReturn(API_KEY_FOR_CONTAINERS);
        given(client.target(DOCKER_REGISTRY_BROWSER_TAG_ENDPOINT)).willReturn(webTarget);
        given(webTarget.request()).willReturn(builder);
        given(builder.header(API_KEY_HEADER, API_KEY_FOR_CONTAINERS)).willReturn(builder);
        doThrow(RuntimeException.class).when(builder).delete();

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        verifyNoMoreInteractions(response);
    }
}
