package hu.psprog.leaflet.lms.web.interceptor;

import hu.psprog.leaflet.lms.service.domain.common.ResponseStatus;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static hu.psprog.leaflet.lms.web.interceptor.GeneralStatusSetterInterceptor.VALIDATION_FAILED_ATTRIBUTE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link GeneralStatusSetterInterceptor}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class GeneralStatusSetterInterceptorTest {

    private static final String REDIRECTION_VIEW = "redirect:/home";
    private static final String NON_REDIRECTION_VIEW = "/home";
    private static final String STATUS_ATTRIBUTE = "generalStatus";

    @Mock
    private ModelAndView modelAndView;

    @Mock
    private Map<String, Object> model;

    @InjectMocks
    private GeneralStatusSetterInterceptor generalStatusSetterInterceptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotSetGeneralStatusWhenModelAndViewIsNull() throws Exception {

        // when
        generalStatusSetterInterceptor.postHandle(null, null, null, null);

        // then
        // no exception expected
    }

    @Test
    public void shouldNotSetGeneralStatusForRedirectionView() throws Exception {

        // given
        given(modelAndView.getViewName()).willReturn(REDIRECTION_VIEW);

        // when
        generalStatusSetterInterceptor.postHandle(null, null, null, modelAndView);

        // then
        verify(modelAndView, never()).addObject(anyString(), any());
    }

    @Test
    @Parameters(source = GeneralStatusSetterInterceptorParameters.class)
    public void shouldSetGeneralStatus(HttpStatus status, boolean isValidationFailureSet, ResponseStatus expectedResponseStatus) throws Exception {

        // given
        prepareModelAndView(status, isValidationFailureSet);

        // when
        generalStatusSetterInterceptor.postHandle(null, null, null, modelAndView);

        // then
        verify(modelAndView).addObject(STATUS_ATTRIBUTE, expectedResponseStatus);
    }

    private void prepareModelAndView(HttpStatus status, boolean isValidationFailureSet) {
        given(modelAndView.getViewName()).willReturn(NON_REDIRECTION_VIEW);
        given(modelAndView.getModel()).willReturn(model);
        given(model.containsKey(VALIDATION_FAILED_ATTRIBUTE)).willReturn(isValidationFailureSet);
        given(modelAndView.getStatus()).willReturn(status);
    }

    public static class GeneralStatusSetterInterceptorParameters {

        public static Object[][] provideParameters() {

            return new Object[][] {
                    {null,                              false,  ResponseStatus.OK},
                    {HttpStatus.PERMANENT_REDIRECT,     false,  ResponseStatus.OK},
                    {HttpStatus.NOT_FOUND,              false,  ResponseStatus.ERROR},
                    {HttpStatus.INTERNAL_SERVER_ERROR,  false,  ResponseStatus.ERROR},
                    {null,                              true,   ResponseStatus.VALIDATION_FAILURE},
                    {HttpStatus.BAD_REQUEST,            false,  ResponseStatus.VALIDATION_FAILURE},
                    {HttpStatus.NOT_FOUND,              true,   ResponseStatus.VALIDATION_FAILURE},
                    {HttpStatus.INTERNAL_SERVER_ERROR,  true,   ResponseStatus.VALIDATION_FAILURE}};
        }
    }
}