package hu.psprog.leaflet.lms.web.interceptor;

import hu.psprog.leaflet.lms.service.domain.common.ResponseStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.stream.Stream;

import static hu.psprog.leaflet.lms.web.interceptor.GeneralStatusSetterInterceptor.VALIDATION_FAILED_ATTRIBUTE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link GeneralStatusSetterInterceptor}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class GeneralStatusSetterInterceptorTest {

    private static final String REDIRECTION_VIEW = "redirect:/home";
    private static final String NON_REDIRECTION_VIEW = "/home";
    private static final String STATUS_ATTRIBUTE = "generalStatus";

    @Mock(lenient = true)
    private ModelAndView modelAndView;

    @Mock(lenient = true)
    private Map<String, Object> model;

    @InjectMocks
    private GeneralStatusSetterInterceptor generalStatusSetterInterceptor;

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

    @ParameterizedTest
    @MethodSource("generalStatusSetterInterceptorDataProvider")
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

    private static Stream<Arguments> generalStatusSetterInterceptorDataProvider() {

        return Stream.of(
                Arguments.of(null,                              false,  ResponseStatus.OK),
                Arguments.of(HttpStatus.PERMANENT_REDIRECT,     false,  ResponseStatus.OK),
                Arguments.of(HttpStatus.NOT_FOUND,              false,  ResponseStatus.ERROR),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR,  false,  ResponseStatus.ERROR),
                Arguments.of(null,                              true,   ResponseStatus.VALIDATION_FAILURE),
                Arguments.of(HttpStatus.BAD_REQUEST,            false,  ResponseStatus.VALIDATION_FAILURE),
                Arguments.of(HttpStatus.NOT_FOUND,              true,   ResponseStatus.VALIDATION_FAILURE),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR,  true,   ResponseStatus.VALIDATION_FAILURE)
        );
    }
}