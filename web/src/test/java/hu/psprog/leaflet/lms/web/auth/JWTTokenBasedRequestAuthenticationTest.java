package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Map;

import static hu.psprog.leaflet.lms.web.auth.mock.MockedJWTUserSecurityContextFactory.TOKEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link JWTTokenBasedRequestAuthentication}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class JWTTokenBasedRequestAuthenticationTest {

    private static final String HEADER_PARAMETER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_VALUE = "Bearer " + TOKEN;

    @InjectMocks
    private JWTTokenBasedRequestAuthentication requestAuthentication;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockedJWTUser
    public void shouldReturnRequestAuthenticationHeaderMap() {

        // when
        Map<String, String> result = requestAuthentication.getAuthenticationHeader();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(HEADER_PARAMETER_AUTHORIZATION), equalTo(AUTHORIZATION_VALUE));
    }
}