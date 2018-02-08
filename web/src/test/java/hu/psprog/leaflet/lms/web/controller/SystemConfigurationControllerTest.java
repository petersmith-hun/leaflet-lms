package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SystemConfigurationController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SystemConfigurationControllerTest extends AbstractControllerTest {

    private static final String SYSTEM = "system";
    private static final String VIEW_SEO_EDITOR_FORM = "seo_editor_form";
    private static final String PATH_SYSTEM_SEO = "/system/seo";
    private static final String VIEW_FAILOVER = "failover";
    private static final String FIELD_STATUS = "status";

    @Mock
    private SystemConfigurationFacade systemConfigurationFacade;

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
        systemConfigurationController.processSEOConfigurationChange(seoConfiguration);

        // then
        verify(systemConfigurationFacade).processUpdateSEOConfiguration(seoConfiguration);
        verifyRedirectionCreated(PATH_SYSTEM_SEO);
    }

    @Test
    public void shouldShowFailoverStatus() throws FailoverCommunicationException {

        // when
        systemConfigurationController.showFailoverStatus();

        // then
        verifyViewCreated(VIEW_FAILOVER);
        verifyFieldsSet(FIELD_STATUS);
        verify(systemConfigurationFacade).getFailoverStatus();
    }

    @Override
    String controllerViewGroup() {
        return SYSTEM;
    }
}