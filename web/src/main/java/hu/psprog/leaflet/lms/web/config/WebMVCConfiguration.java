package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.lms.web.menu.interceptor.SystemMenuInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private SystemMenuInterceptor systemMenuInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(systemMenuInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        webAppResources.getResources()
                .forEach((resourceHandler, resourceLocation) -> registry
                        .addResourceHandler(resourceHandler)
                        .addResourceLocations(resourceLocation));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayHttpMessageConverter());
        converters.add(resourceHttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {

        ByteArrayHttpMessageConverter messageConverter = new ByteArrayHttpMessageConverter();
        messageConverter.setSupportedMediaTypes(supportedMediaTypes());

        return messageConverter;
    }

    @Bean
    public ResourceHttpMessageConverter resourceHttpMessageConverter() {

        ResourceHttpMessageConverter messageConverter = new ResourceHttpMessageConverter();
        messageConverter.setSupportedMediaTypes(supportedMediaTypes());

        return messageConverter;
    }

    private List<MediaType> supportedMediaTypes() {
        return Arrays.asList(
                MediaType.IMAGE_JPEG,
                MediaType.IMAGE_PNG,
                MediaType.IMAGE_GIF,
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.MULTIPART_FORM_DATA);
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
