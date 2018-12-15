package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.failover.api.client.FailoverClient;
import hu.psprog.leaflet.failover.api.domain.FailoverStatus;
import hu.psprog.leaflet.failover.api.domain.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import hu.psprog.leaflet.tlp.api.client.TLPClient;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    private static final StatusResponse FAILOVER_STATUS_RESPONSE = StatusResponse.getBuilder()
            .withStatus(FailoverStatus.STANDBY)
            .build();
    private static final LogRequest LOG_REQUEST = new LogRequest();
    private static final LogEventPage LOG_EVENT_PAGE_RESPONSE = LogEventPage.getBuilder().build();

    @Mock
    private SEOConfigurationDCPAdapter seoConfigurationDCPAdapter;

    @Mock
    private FailoverClient failoverClient;

    @Mock
    private TLPClient tlpClient;

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

    @Test
    public void shouldGetFailoverStatus() throws CommunicationFailureException {

        // given
        given(failoverClient.getFailoverStatus()).willReturn(FAILOVER_STATUS_RESPONSE);

        // when
        StatusResponse result = systemConfigurationFacade.getFailoverStatus();

        // then
        assertThat(result, equalTo(FAILOVER_STATUS_RESPONSE));
    }

    @Test
    public void shouldGetLogs() throws CommunicationFailureException {

        // given
        given(tlpClient.getLogs(LOG_REQUEST)).willReturn(LOG_EVENT_PAGE_RESPONSE);

        // when
        LogEventPage result = systemConfigurationFacade.getLogs(LOG_REQUEST);

        // then
        assertThat(result, equalTo(LOG_EVENT_PAGE_RESPONSE));
    }
}