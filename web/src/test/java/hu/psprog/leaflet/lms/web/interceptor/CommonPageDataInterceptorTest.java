package hu.psprog.leaflet.lms.web.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link CommonPageDataInterceptor}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonPageDataInterceptorTest {

    private static final String APPLICATION_VERSION = "appVersion";
    private static final Map<String, String> COMMON_PAGE_DATA_MAP = Map.of("footerAppVersion", APPLICATION_VERSION);

    private CommonPageDataInterceptor commonPageDataInterceptor;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        commonPageDataInterceptor = new CommonPageDataInterceptor(APPLICATION_VERSION);
    }

    @Test
    public void shouldPostHandleAddApplicationVersionToModelAndView() throws Exception {

        // given
        ModelAndView modelAndView = new ModelAndView();
        given(request.getDispatcherType()).willReturn(DispatcherType.REQUEST);

        // when
        commonPageDataInterceptor.postHandle(request, null, null, modelAndView);

        // then
        assertThat(modelAndView.getModel().size(), equalTo(1));
        assertThat(modelAndView.getModelMap().get("common"), equalTo(COMMON_PAGE_DATA_MAP));
    }


    @Test
    public void shouldPostHandleDoNothingIfDispatcherTypeIsNotRequest() throws Exception {

        // given
        ModelAndView modelAndView = new ModelAndView();
        given(request.getDispatcherType()).willReturn(DispatcherType.ERROR);

        // when
        commonPageDataInterceptor.postHandle(request, null, null, modelAndView);

        // then
        assertThat(modelAndView.isEmpty(), is(true));
    }

    @Test
    public void shouldPostHandleDoNothingIfModelAndViewIsNull() throws Exception {

        // given
        given(request.getDispatcherType()).willReturn(DispatcherType.REQUEST);

        // when
        commonPageDataInterceptor.postHandle(request, null, null, null);

        // then
        // do nothing
    }
}
