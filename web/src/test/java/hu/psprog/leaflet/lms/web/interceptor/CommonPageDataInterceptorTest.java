package hu.psprog.leaflet.lms.web.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link CommonPageDataInterceptor}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonPageDataInterceptorTest {

    private static final String APPLICATION_VERSION = "appVersion";

    private CommonPageDataInterceptor commonPageDataInterceptor;

    @Before
    public void setup() {
        commonPageDataInterceptor = new CommonPageDataInterceptor(APPLICATION_VERSION);
    }

    @Test
    public void shouldPostHandleAddApplicationVersionToModelAndView() throws Exception {

        // given
        ModelAndView modelAndView = new ModelAndView();

        // when
        commonPageDataInterceptor.postHandle(null, null, null, modelAndView);

        // then
        assertThat(modelAndView.getModel().size(), equalTo(1));
        assertThat(modelAndView.getModelMap().get("app_version"), equalTo(APPLICATION_VERSION));
    }

    @Test
    public void shouldPostHandleDoNothingIfModelAndViewIsNull() throws Exception {

        // when
        commonPageDataInterceptor.postHandle(null, null, null, null);

        // then
        // do nothing
    }
}
