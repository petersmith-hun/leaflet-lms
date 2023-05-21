package hu.psprog.leaflet.lms.web.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link CommonPageDataInterceptor}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class CommonPageDataInterceptorTest {

    private static final String APPLICATION_VERSION = "appVersion";
    private static final Map<String, String> COMMON_PAGE_DATA_MAP = Map.of("footerAppVersion", APPLICATION_VERSION);

    private CommonPageDataInterceptor commonPageDataInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BuildProperties buildProperties;

    @BeforeEach
    public void setup() {
        given(buildProperties.getVersion()).willReturn(APPLICATION_VERSION);
        commonPageDataInterceptor = new CommonPageDataInterceptor(Optional.of(buildProperties));
    }

    @Test
    public void shouldPostHandleAddApplicationVersionToModelAndView() {

        // given
        ModelAndView modelAndView = new ModelAndView();
        given(request.getDispatcherType()).willReturn(DispatcherType.REQUEST);

        // when
        commonPageDataInterceptor.postHandle(request, response, null, modelAndView);

        // then
        assertThat(modelAndView.getModel().size(), equalTo(3));
        assertThat(modelAndView.getModelMap().get("common"), equalTo(COMMON_PAGE_DATA_MAP));
        assertThat(modelAndView.getModelMap().get("request"), equalTo(request));
        assertThat(modelAndView.getModelMap().get("response"), equalTo(response));
    }


    @Test
    public void shouldPostHandleDoNothingIfDispatcherTypeIsNotRequest() {

        // given
        ModelAndView modelAndView = new ModelAndView();
        given(request.getDispatcherType()).willReturn(DispatcherType.ERROR);

        // when
        commonPageDataInterceptor.postHandle(request, null, null, modelAndView);

        // then
        assertThat(modelAndView.isEmpty(), is(true));
    }

    @Test
    public void shouldPostHandleDoNothingIfModelAndViewIsNull() {

        // given
        given(request.getDispatcherType()).willReturn(DispatcherType.REQUEST);

        // when
        commonPageDataInterceptor.postHandle(request, null, null, null);

        // then
        // do nothing
    }
}
