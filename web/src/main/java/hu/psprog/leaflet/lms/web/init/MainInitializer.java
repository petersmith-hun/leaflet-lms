package hu.psprog.leaflet.lms.web.init;

import hu.psprog.leaflet.lms.web.config.ApplicationContextConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Application context initializer (instead of web.xml).
 *
 * @author Peter Smith
 */
public class MainInitializer implements WebApplicationInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainInitializer.class);

    public static final String FILTER_CHARACTER_ENCODING = "CharacterEncodingFilter";
    public static final String FILTER_MAP_ALL_URL = "/*";
    public static final String FILTER_SPRING_SECURITY = "springSecurityFilterChain";
    public static final String SERVLET_NAME = "lms-web-dispatcher";
    public static final String SERVLET_MAPPING = "/";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        LOGGER.info("Starting Leaflet Management System application...");

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ApplicationContextConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        ServletRegistration.Dynamic servlet = servletContext.addServlet(SERVLET_NAME, dispatcherServlet);

        servlet.addMapping(SERVLET_MAPPING);
        servlet.setAsyncSupported(true);
        servlet.setLoadOnStartup(1);

        FilterRegistration characterEncodingFilter = servletContext.addFilter(FILTER_CHARACTER_ENCODING, new CharacterEncodingFilter());
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");

        FilterRegistration securityFilterChain = servletContext.addFilter(FILTER_SPRING_SECURITY, new DelegatingFilterProxy("springSecurityFilterChain"));
        securityFilterChain.addMappingForUrlPatterns(null, false, FILTER_MAP_ALL_URL);
    }
}
