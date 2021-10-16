package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link DashboardFacadeImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class DashboardFacadeImplTest {

    private static final List<String> REGISTERED_SERVICES = Arrays.asList("SVC1", "SVC2", "SVC3");

    @Mock
    private StackAdminServiceClient stackAdminServiceClient;

    @Mock
    private StackStatusConfigModel stackStatusConfigModel;

    @InjectMocks
    private DashboardFacadeImpl dashboardFacade;

    @Test
    public void shouldGetRegisteredServicesReturnServiceListIfEnabledAndClientReturnsData() {

        // given
        given(stackStatusConfigModel.isEnabled()).willReturn(true);
        given(stackAdminServiceClient.getRegisteredServices()).willReturn(new RegisteredServices(REGISTERED_SERVICES));

        // when
        List<String> result = dashboardFacade.getRegisteredServices();

        // then
        assertThat(result, equalTo(REGISTERED_SERVICES));
    }

    @Test
    public void shouldGetRegisteredServicesReturnEmptyListIfEnabledButClientReturnsNull() {

        // given
        given(stackStatusConfigModel.isEnabled()).willReturn(true);
        given(stackAdminServiceClient.getRegisteredServices()).willReturn(null);

        // when
        List<String> result = dashboardFacade.getRegisteredServices();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetRegisteredServicesReturnEmptyListIfDisabled() {

        // given
        given(stackStatusConfigModel.isEnabled()).willReturn(false);

        // when
        List<String> result = dashboardFacade.getRegisteredServices();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
        verifyNoInteractions(stackAdminServiceClient);
    }
}
