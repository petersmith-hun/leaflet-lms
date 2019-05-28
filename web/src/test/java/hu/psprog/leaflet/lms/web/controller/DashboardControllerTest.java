package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link DashboardController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockedJWTUser(userID = 5L)
public class DashboardControllerTest extends AbstractControllerTest {

    private static final String VIEW_GROUP = "dashboard";
    private static final String VIEW_HOME = "home";
    private static final String FIELD_REGISTERED_SERVICES = "registeredServices";

    @Mock
    private DashboardFacade dashboardFacade;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    public void shouldHomeRenderDashboard() {

        // when
        dashboardController.home();

        // then
        verify(dashboardFacade).getRegisteredServices();
        verifyViewCreated(VIEW_HOME);
        verifyFieldsSet(FIELD_REGISTERED_SERVICES);
    }

    @Override
    String controllerViewGroup() {
        return VIEW_GROUP;
    }
}
