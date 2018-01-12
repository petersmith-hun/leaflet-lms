package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SystemConfigurationFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class SystemConfigurationFacadeImplTest {

    @Mock
    private SEOConfigurationDCPAdapter seoConfigurationDCPAdapter;

    @InjectMocks
    private SystemConfigurationFacadeImpl systemConfigurationFacade;

    @Test
    public void shouldGetCurrentSEOConfiguration() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder()
                .withPageTitle("page title")
                .build();
        given(seoConfigurationDCPAdapter.collect()).willReturn(seoConfiguration);

        // when
        SEOConfiguration result = systemConfigurationFacade.getCurrentSEOConfiguration();

        // then
        assertThat(result, equalTo(seoConfiguration));
        verify(seoConfigurationDCPAdapter).collect();
    }

    @Test
    public void shouldUpdateSEOConfiguration() throws CommunicationFailureException {

        // given
        SEOConfiguration seoConfiguration = SEOConfiguration.getBuilder().build();

        // when
        systemConfigurationFacade.processUpdateSEOConfiguration(seoConfiguration);

        // then
        verify(seoConfigurationDCPAdapter).update(seoConfiguration);
    }
}