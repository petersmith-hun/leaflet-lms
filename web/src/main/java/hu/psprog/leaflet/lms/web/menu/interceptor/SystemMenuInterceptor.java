package hu.psprog.leaflet.lms.web.menu.interceptor;

import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * Spring MVC interceptor to add {@link SystemMenu} instance to the {@link ModelAndView} on every request.
 *
 * @author Peter Smith
 */
@Component
public class SystemMenuInterceptor implements HandlerInterceptor {

    private static final String MENU = "menu";

    private final SystemMenu systemMenu;

    @Autowired
    public SystemMenuInterceptor(SystemMenu systemMenu) {
        this.systemMenu = systemMenu;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        if (Objects.nonNull(modelAndView)) {
            modelAndView.addObject(MENU, systemMenu.getMenu());
        }
    }
}
