package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
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

    @Mock
    private SystemConfigurationFacade systemConfigurationFacade;

    @InjectMocks
    private SystemConfigurationController systemConfigurationController;

    @Test
    public void shouldShowSEOConfigurationForm() throws CommunicationFailureException {

        // when
        systemConfigurationController.showSEOConfigurationForm();

        // then
        verifyViewCreated("seo_editor_form");
        verifyFieldsSet("seo");
    }

    @Test
    public void shouldProcessSEOConfigurationChange() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder().build();

        // when
        systemConfigurationController.processSEOConfigurationChange(seoConfiguration);

        // then
        verify(systemConfigurationFacade).processUpdateSEOConfiguration(seoConfiguration);
        verifyRedirectionCreated("/system/seo");
    }

    @Override
    String controllerViewGroup() {
        return SYSTEM;
    }
}