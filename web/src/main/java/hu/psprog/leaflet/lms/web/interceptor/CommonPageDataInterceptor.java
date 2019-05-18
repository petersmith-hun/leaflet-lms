package hu.psprog.leaflet.lms.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * Interceptor implementation that adds common variable to the model.
 *
 * @author Peter Smith
 */
@Component
public class CommonPageDataInterceptor extends HandlerInterceptorAdapter {

    private static final String APP_VERSION = "${app.version}";
    private static final String APP_VERSION_ATTRIBUTE = "footerAppVersion";
    private static final String COMMON_ATTRIBUTE = "common";

    private Map<String, String> commonPageData;

    @Autowired
    public CommonPageDataInterceptor(@Value(APP_VERSION) String applicationVersion) {
        commonPageData = Map.of(APP_VERSION_ATTRIBUTE, applicationVersion);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isRequestDispatcher(request) && Objects.nonNull(modelAndView)) {
            setApplicationVersion(modelAndView);
        }
    }

    private boolean isRequestDispatcher(HttpServletRequest request) {
        return request.getDispatcherType() == DispatcherType.REQUEST;
    }

    private void setApplicationVersion(ModelAndView modelAndView) {
        modelAndView.addObject(COMMON_ATTRIBUTE, commonPageData);
    }
}
