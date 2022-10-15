package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.GenericType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent.FALLBACK_DOCKER_REGISTRY_CONTENT;
import static hu.psprog.leaflet.lms.service.domain.system.DockerRepository.FALLBACK_DOCKER_REPOSITORY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
    private static final GenericType<Map<String, String>> REGISTRIES_GENERIC_TYPE = new GenericType<>() {};
    private static final String REGISTRY_ID = "registry-1";
    private static final String REPOSITORY_ID = "repository-1";
    private static final String TAG = "1.0";

    @Mock
    private BridgeClient bridgeClient;

    @Captor
    private ArgumentCaptor<RESTRequest> restRequestCaptor;

    @InjectMocks
    private StackAdminServiceClientImpl stackAdminServiceClient;

    @Test
    public void shouldGetRegisteredServicesReturnWithSuccess() throws CommunicationFailureException {

        // given
        given(bridgeClient.call(any(RESTRequest.class), eq(RegisteredServices.class))).willReturn(REGISTERED_SERVICES);

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, equalTo(REGISTERED_SERVICES));

        verify(bridgeClient).call(restRequestCaptor.capture(), eq(RegisteredServices.class));
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.GET));
        assertThat(request.getPath(), equalTo(LSASPath.REGISTERED_SERVICES));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldGetRegisteredServicesReturnNullForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(RequestProcessingFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(RegisteredServices.class));

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldGetRegisteredServicesReturnNullWhenClientThrowsException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(RegisteredServices.class));

        // when
        RegisteredServices result = stackAdminServiceClient.getRegisteredServices();

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldGetExistingContainersReturnWithSuccess() throws CommunicationFailureException {

        // given
        given(bridgeClient.call(any(RESTRequest.class), eq(CONTAINER_LIST_GENERIC_TYPE))).willReturn(CONTAINER_LIST);

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        assertThat(result, equalTo(CONTAINER_LIST));

        verify(bridgeClient).call(restRequestCaptor.capture(), eq(CONTAINER_LIST_GENERIC_TYPE));
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.GET));
        assertThat(request.getPath(), equalTo(LSASPath.CONTAINERS));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldGetExistingContainersReturnEmptyListForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(RequestProcessingFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(CONTAINER_LIST_GENERIC_TYPE));

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetExistingContainersReturnEmptyListWhenClientThrowsException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(CONTAINER_LIST_GENERIC_TYPE));

        // when
        List<Container> result = stackAdminServiceClient.getExistingContainers();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnWithSuccess() throws CommunicationFailureException {

        // given
        Map<String, String> responseBody = Map.of("registry-1", "http://localhost");

        given(bridgeClient.call(any(RESTRequest.class), eq(REGISTRIES_GENERIC_TYPE))).willReturn(responseBody);

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(responseBody));

        verify(bridgeClient).call(restRequestCaptor.capture(), eq(REGISTRIES_GENERIC_TYPE));
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.GET));
        assertThat(request.getPath(), equalTo(LSASPath.REGISTRY));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnFallbackValueForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(ResourceNotFoundException.class).when(bridgeClient).call(any(RESTRequest.class), eq(REGISTRIES_GENERIC_TYPE));

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(Collections.emptyMap()));
    }

    @Test
    public void shouldGetConfiguredRegistriesReturnFallbackValueOnException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(REGISTRIES_GENERIC_TYPE));

        // when
        Map<String, String> result = stackAdminServiceClient.getConfiguredRegistries();

        // then
        assertThat(result, equalTo(Collections.emptyMap()));
    }

    @Test
    public void shouldGetDockerRepositoriesReturnWithSuccess() throws CommunicationFailureException {

        // given
        DockerRegistryContent responseBody = new DockerRegistryContent(REGISTRY_ID, Collections.emptyList());

        given(bridgeClient.call(any(RESTRequest.class), eq(DockerRegistryContent.class))).willReturn(responseBody);

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(responseBody));

        verify(bridgeClient).call(restRequestCaptor.capture(), eq(DockerRegistryContent.class));
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.GET));
        assertThat(request.getPath(), equalTo(LSASPath.REGISTRY_REPOSITORIES));
        assertThat(request.getPathParameters().size(), equalTo(1));
        assertThat(request.getPathParameters().get("registryID"), equalTo(REGISTRY_ID));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldGetDockerRepositoriesReturnFallbackValueForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(ResourceNotFoundException.class).when(bridgeClient).call(any(RESTRequest.class), eq(DockerRegistryContent.class));

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REGISTRY_CONTENT));
    }

    @Test
    public void shouldGetDockerRepositoriesReturnFallbackValueOnException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(DockerRegistryContent.class));

        // when
        DockerRegistryContent result = stackAdminServiceClient.getDockerRepositories(REGISTRY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REGISTRY_CONTENT));
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnWithSuccess() throws CommunicationFailureException {

        // given
        DockerRepository responseBody = new DockerRepository(REGISTRY_ID, REPOSITORY_ID, Collections.emptyList());

        given(bridgeClient.call(any(RESTRequest.class), eq(DockerRepository.class))).willReturn(responseBody);

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(responseBody));

        verify(bridgeClient).call(restRequestCaptor.capture(), eq(DockerRepository.class));
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.GET));
        assertThat(request.getPath(), equalTo(LSASPath.REGISTRY_REPOSITORIES_TAGS));
        assertThat(request.getPathParameters().size(), equalTo(2));
        assertThat(request.getPathParameters().get("registryID"), equalTo(REGISTRY_ID));
        assertThat(request.getPathParameters().get("repositoryID"), equalTo(REPOSITORY_ID));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnFallbackValueForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(ResourceNotFoundException.class).when(bridgeClient).call(any(RESTRequest.class), eq(DockerRepository.class));

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REPOSITORY));
    }

    @Test
    public void shouldGetDockerRepositoryTagsReturnFallbackValueOnException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class), eq(DockerRepository.class));

        // when
        DockerRepository result = stackAdminServiceClient.getDockerRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        assertThat(result, equalTo(FALLBACK_DOCKER_REPOSITORY));
    }

    @Test
    public void shouldDeleteDockerImageByTagProcessRequestWithSuccess() throws CommunicationFailureException {

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        verify(bridgeClient).call(restRequestCaptor.capture());
        RESTRequest request = restRequestCaptor.getValue();
        assertThat(request.getMethod(), equalTo(RequestMethod.DELETE));
        assertThat(request.getPath(), equalTo(LSASPath.REGISTRY_REPOSITORIES_TAGS_TAG));
        assertThat(request.getPathParameters().size(), equalTo(3));
        assertThat(request.getPathParameters().get("registryID"), equalTo(REGISTRY_ID));
        assertThat(request.getPathParameters().get("repositoryID"), equalTo(REPOSITORY_ID));
        assertThat(request.getPathParameters().get("tagID"), equalTo(TAG));
        assertThat(request.isAuthenticationRequired(), is(true));
    }

    @Test
    public void shouldDeleteDockerImageByTagSilentlyFailForNonSuccessfulResponse() throws CommunicationFailureException {

        // given
        doThrow(ResourceNotFoundException.class).when(bridgeClient).call(any(RESTRequest.class));

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        // failing silently
    }

    @Test
    public void shouldDeleteDockerImageByTagSilentlyFailOnException() throws CommunicationFailureException {

        // given
        doThrow(CommunicationFailureException.class).when(bridgeClient).call(any(RESTRequest.class));

        // when
        stackAdminServiceClient.deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        // failing silently
    }
}
