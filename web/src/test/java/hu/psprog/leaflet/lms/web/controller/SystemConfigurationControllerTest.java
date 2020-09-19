package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.LogViewerPaginationHelper;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link SystemConfigurationController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SystemConfigurationControllerTest extends AbstractControllerTest {

    private static final String SYSTEM = "system";
    private static final String PATH_SYSTEM_SEO = "/system/seo";

    private static final String VIEW_SEO_EDITOR_FORM = "seo_editor_form";
    private static final String VIEW_LOGS = "logs";
    private static final String VIEW_FAILOVER = "failover";
    private static final String VIEW_DOCKER = "docker";

    private static final String FIELD_STATUS = "status";
    private static final String FIELD_ORDER_BY_OPTIONS = "orderByOptions";
    private static final String FIELD_ORDER_DIRECTION_OPTIONS = "orderDirectionOptions";
    private static final String FIELD_LOGS = "logs";
    private static final String FIELD_PAGINATION = "pagination";
    private static final String FIELD_EXISTING_CONTAINERS = "existingContainers";

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
        verifyFieldsSet(FIELD_ORDER_BY_OPTIONS, FIELD_ORDER_DIRECTION_OPTIONS, FIELD_LOGS);
        verifyZeroInteractions(systemConfigurationFacade);
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
    public void shouldShowDockerClusterStatus() {

        // when
        systemConfigurationController.showDockerClusterStatus();

        // then
        verifyViewCreated(VIEW_DOCKER);
        verifyFieldsSet(FIELD_EXISTING_CONTAINERS);
        verify(systemConfigurationFacade).getExistingContainers();
    }

    @Override
    String controllerViewGroup() {
        return SYSTEM;
    }
}