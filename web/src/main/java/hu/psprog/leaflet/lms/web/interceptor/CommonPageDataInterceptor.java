package hu.psprog.leaflet.lms.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Interceptor implementation that adds common variable to the model.
 *
 * @author Peter Smith
 */
@Component
public class CommonPageDataInterceptor extends HandlerInterceptorAdapter {

    private static final String APP_VERSION = "${app.version}";
    private static final String APP_VERSION_ATTRIBUTE = "app_version";

    private String applicationVersion;

    @Autowired
    public CommonPageDataInterceptor(@Value(APP_VERSION) String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (Objects.nonNull(modelAndView)) {
            setApplicationVersion(modelAndView);
        }
    }

    private void setApplicationVersion(ModelAndView modelAndView) {
        modelAndView.addObject(APP_VERSION_ATTRIBUTE, applicationVersion);
    }
}
