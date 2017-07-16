package hu.psprog.leaflet.lms.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Map;

/**
 * Spring Web MVC configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebMvc
public class WebMVCConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private WebAppResources webAppResources;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        webAppResources.getResources()
                .forEach((resourceHandler, resourceLocation) -> registry
                        .addResourceHandler(resourceHandler)
                        .addResourceLocations(resourceLocation));
    }

    @Component
    @ConfigurationProperties(prefix = "webapp")
    class WebAppResources {

        private Map<String, String> resources;

        public Map<String, String> getResources() {
            return resources;
        }

        public void setResources(Map<String, String> resources) {
            this.resources = resources;
        }
    }
}
