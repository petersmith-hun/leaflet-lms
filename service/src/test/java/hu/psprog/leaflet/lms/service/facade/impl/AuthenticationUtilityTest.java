package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.auth.handler.JWTTokenPayloadReader;
import hu.psprog.leaflet.lms.service.auth.util.AbstractTokenRelatedTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link AuthenticationUtility}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class AuthenticationUtilityTest extends AbstractTokenRelatedTest {

    private static final String EXPECTED_DATE = "2018-01-07 12:12:01+0000";

    @Mock
    private JWTTokenPayloadReader jwtTokenPayloadReader;

    @InjectMocks
    private AuthenticationUtility authenticationUtility;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        given(jwtTokenPayloadReader.readPayload(TOKEN)).willReturn(prepareAuthenticationUserDetailsModel(EXPECTED_DATE));
    }

    @Test
    public void shouldCreateTemporalAuthentication() {

        // when
        Authentication result = authenticationUtility.createTemporal(TOKEN);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getCredentials(), equalTo(TOKEN));
    }

    @Test
    public void shouldCreateAndStoreTemporalAuthentication() {

        // when
        authenticationUtility.createAndStoreTemporal(TOKEN);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials(), equalTo(TOKEN));
    }

    @Test
    public void shouldReplaceAuthentication() {

        // given
        String username = "Replaced authentication";

        // when
        authenticationUtility.replace(username, TOKEN);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials(), equalTo(TOKEN));
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), equalTo(username));
    }
}