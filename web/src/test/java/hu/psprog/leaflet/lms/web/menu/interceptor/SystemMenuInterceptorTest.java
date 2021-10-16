package hu.psprog.leaflet.lms.web.menu.interceptor;

import hu.psprog.leaflet.lms.web.menu.domain.MenuItem;
import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link SystemMenuInterceptor}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class SystemMenuInterceptorTest {

    private static final List<MenuItem> MENU_ITEM_LIST = Collections.singletonList(MenuItem.getBuilder().withName("test").build());
    private static final String MENU_MODEL_ATTRIBUTE = "menu";

    @Mock
    private SystemMenu systemMenu;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private SystemMenuInterceptor systemMenuInterceptor;

    @Test
    public void shouldAddSystemMenuToModelAndView() throws Exception {

        // given
        ModelAndView modelAndView = new ModelAndView("testModel");
        given(systemMenu.getMenu()).willReturn(MENU_ITEM_LIST);

        // when
        systemMenuInterceptor.postHandle(request, response, handler, modelAndView);

        // then
        verify(systemMenu).getMenu();
        assertThat(modelAndView.getModel().get(MENU_MODEL_ATTRIBUTE), equalTo(MENU_ITEM_LIST));
    }

    @Test
    public void shouldNotTryToAddSystemMenuIfModelAndViewIsNull() throws Exception {

        // given
        ModelAndView modelAndView = null;

        // when
        systemMenuInterceptor.postHandle(request, response, handler, modelAndView);

        // then
        verifyNoInteractions(systemMenu);
    }
}