package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.LogViewerPaginationHelper;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link SystemConfigurationController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class SystemConfigurationControllerTest extends AbstractControllerTest {

    private static final String SYSTEM = "system";
    private static final String PATH_SYSTEM_SEO = "/system/seo";

    private static final String VIEW_SEO_EDITOR_FORM = "seo_editor_form";
    private static final String VIEW_LOGS = "logs";
    private static final String VIEW_FAILOVER = "failover";
    private static final String VIEW_DOCKER = "docker";
    private static final String VIEW_DOCKER_REGISTRY_BROWSER = "docker_registry_browser";

    private static final String FIELD_STATUS = "status";
    private static final String FIELD_ORDER_BY_OPTIONS = "orderByOptions";
    private static final String FIELD_ORDER_DIRECTION_OPTIONS = "orderDirectionOptions";
    private static final String FIELD_LOGS = "logs";
    private static final String FIELD_PAGINATION = "pagination";
    private static final String FIELD_EXISTING_CONTAINERS = "existingContainers";
    private static final String FIELD_REGISTRIES = "registries";
    private static final String FIELD_REPOSITORIES = "repositories";
    private static final String FIELD_TAGS = "tags";
    private static final String FIELD_CURRENT_REGISTRY = "currentRegistry";
    private static final String FIELD_CURRENT_REPOSITORY = "currentRepository";

    private static final String REGISTRY_ID = "registry-1";
    private static final String REPOSITORY_ID = "repository-1";
    private static final String TAG = "1.0";
    private static final String TLQL_LOG_QUERY = "search with conditions";

    @Mock
    private SystemConfigurationFacade systemConfigurationFacade;

    @Mock
    private LogViewerPaginationHelper logViewerPaginationHelper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SystemConfigurationController systemConfigurationController;

    @Test
    public void shouldShowSEOConfigurationForm() throws CommunicationFailureException {

        // when
        systemConfigurationController.showSEOConfigurationForm();

        // then
        verifyViewCreated(VIEW_SEO_EDITOR_FORM);
        verifyFieldsSet(FIELD_SEO);
    }

    @Test
    public void shouldProcessSEOConfigurationChange() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder().build();

        // when
        systemConfigurationController.processSEOConfigurationChange(seoConfiguration, redirectAttributes);

        // then
        verify(systemConfigurationFacade).processUpdateSEOConfiguration(seoConfiguration);
        verifyRedirectionCreated(PATH_SYSTEM_SEO);
    }

    @Test
    public void shouldProcessSEOConfigurationChangeHandleValidationFailure() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder().build();
        doThrow(new ValidationFailureException(response)).when(systemConfigurationFacade).processUpdateSEOConfiguration(seoConfiguration);

        // when
        systemConfigurationController.processSEOConfigurationChange(seoConfiguration, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(seoConfiguration);
        verifyRedirectionCreated(PATH_SYSTEM_SEO);
    }

    @Test
    public void shouldShowFailoverStatus() throws CommunicationFailureException {

        // when
        systemConfigurationController.showFailoverStatus();

        // then
        verifyViewCreated(VIEW_FAILOVER);
        verifyFieldsSet(FIELD_STATUS);
        verify(systemConfigurationFacade).getFailoverStatus();
    }

    @Test
    public void shouldShowLogQueryForm() throws CommunicationFailureException {

        // when
        systemConfigurationController.showRetrievedLogs(new LogRequest(), request);

        // then
        verifyViewCreated(VIEW_LOGS);
        verifyFieldsSet(FIELD_ORDER_BY_OPTIONS, FIELD_ORDER_DIRECTION_OPTIONS, FIELD_LOGS, FIELD_PAGINATION);
        verifyNoInteractions(systemConfigurationFacade);
    }

    @Test
    public void shouldShowLogQueryFormV2() throws CommunicationFailureException {

        // when
        systemConfigurationController.showRetrievedLogs(Optional.empty());

        // then
        verifyViewCreated(VIEW_LOGS);
        verifyFieldsSet(FIELD_ORDER_BY_OPTIONS, FIELD_ORDER_DIRECTION_OPTIONS, FIELD_LOGS);
        verifyNoInteractions(systemConfigurationFacade);
    }

    @Test
    public void shouldShowLogQueryFormWithRetrievedLogs() throws CommunicationFailureException {

        // given
        LogRequest logRequest = new LogRequest();
        logRequest.setQueried(true);

        // when
        systemConfigurationController.showRetrievedLogs(logRequest, request);

        // then
        verifyViewCreated(VIEW_LOGS);
        verifyFieldsSet(FIELD_ORDER_BY_OPTIONS, FIELD_ORDER_DIRECTION_OPTIONS, FIELD_LOGS, FIELD_PAGINATION);
        verify(systemConfigurationFacade).getLogs(logRequest);
        verify(logViewerPaginationHelper).extractPaginationAttributes(request);
    }

    @Test
    public void shouldShowLogQueryFormV2WithRetrievedLogs() throws CommunicationFailureException {

        // when
        systemConfigurationController.showRetrievedLogs(Optional.of(TLQL_LOG_QUERY));

        // then
        verifyViewCreated(VIEW_LOGS);
        verifyFieldsSet(FIELD_ORDER_BY_OPTIONS, FIELD_ORDER_DIRECTION_OPTIONS, FIELD_LOGS);
        verify(systemConfigurationFacade).getLogs(TLQL_LOG_QUERY);
    }

    @Test
    public void shouldShowDockerClusterStatus() {

        // when
        systemConfigurationController.showDockerClusterStatus();

        // then
        verifyViewCreated(VIEW_DOCKER);
        verifyFieldsSet(FIELD_EXISTING_CONTAINERS);
        verify(systemConfigurationFacade).getExistingContainers();
    }

    @Test
    public void shouldShowConfiguredDockerRegistryList() {

        // when
        systemConfigurationController.showConfiguredDockerRegistryList();

        // then
        verifyViewCreated(VIEW_DOCKER_REGISTRY_BROWSER);
        verifyFieldsSet(FIELD_REGISTRIES);
        verify(systemConfigurationFacade).getConfiguredRegistries();
    }

    @Test
    public void shouldShowRepositories() {

        // when
        systemConfigurationController.showRepositories(REGISTRY_ID);

        // then
        verifyViewCreated(VIEW_DOCKER_REGISTRY_BROWSER);
        verifyFieldsSet(FIELD_REPOSITORIES, FIELD_CURRENT_REGISTRY);
        verify(systemConfigurationFacade).getDockerRepositories(REGISTRY_ID);
    }

    @Test
    public void shouldShowRepositoryTags() {

        // when
        systemConfigurationController.showRepositoryTags(REGISTRY_ID, REPOSITORY_ID);

        // then
        verifyViewCreated(VIEW_DOCKER_REGISTRY_BROWSER);
        verifyFieldsSet(FIELD_TAGS, FIELD_CURRENT_REGISTRY, FIELD_CURRENT_REPOSITORY);
        verify(systemConfigurationFacade).getDockerRepositoryDetails(REGISTRY_ID, REPOSITORY_ID);
    }

    @Test
    public void shouldDeleteImageByTag() {

        // when
        systemConfigurationController.processImageDeletionByTag(REGISTRY_ID, REPOSITORY_ID, TAG);

        // then
        verifyRedirectionCreated("/system/docker/registry?registry=registry-1&repository=repository-1");
        verify(systemConfigurationFacade).deleteDockerImageByTag(REGISTRY_ID, REPOSITORY_ID, TAG);
    }

    @Override
    String controllerViewGroup() {
        return SYSTEM;
    }
}