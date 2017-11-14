package hu.psprog.leaflet.lms.web.menu.interceptor;

import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC interceptor to add {@link SystemMenu} instance to the {@link ModelAndView} on every request.
 *
 * @author Peter Smith
 */
@Component
public class SystemMenuInterceptor extends HandlerInterceptorAdapter {

    private static final String MENU = "menu";

    private SystemMenu systemMenu;

    @Autowired
    public SystemMenuInterceptor(SystemMenu systemMenu) {
        this.systemMenu = systemMenu;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        modelAndView.addObject(MENU, systemMenu.getMenu());

        super.postHandle(request, response, handler, modelAndView);
    }
}
