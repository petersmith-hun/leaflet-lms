package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.lms.web.interceptor.CommonPageDataInterceptor;
import hu.psprog.leaflet.lms.web.interceptor.GeneralStatusSetterInterceptor;
import hu.psprog.leaflet.lms.web.interceptor.ModelAndViewDebuggerInterceptor;
import hu.psprog.leaflet.lms.web.menu.interceptor.SystemMenuInterceptor;
import hu.psprog.leaflet.lms.web.support.thymeleaf.ZonedDateTimeFormatter;
import hu.psprog.leaflet.lms.web.support.thymeleaf.markdown.ExtendedLayoutDialect;
import hu.psprog.leaflet.lms.web.support.thymeleaf.markdown.ResourcePathResolver;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Spring Web MVC configuration.
 *
 * @author Peter Smith
 */
@Configuration
public class WebMVCConfiguration implements WebMvcConfigurer {

    @Autowired
    private WebAppResources webAppResources;

    @Autowired
    private SystemMenuInterceptor systemMenuInterceptor;

    @Autowired
    private GeneralStatusSetterInterceptor generalStatusSetterInterceptor;

    @Autowired
    private Optional<ModelAndViewDebuggerInterceptor> modelAndViewDebuggerInterceptor;

    @Autowired
    private CommonPageDataInterceptor commonPageDataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        modelAndViewDebuggerInterceptor.ifPresent(registry::addInterceptor);
        registry.addInterceptor(systemMenuInterceptor);
        registry.addInterceptor(generalStatusSetterInterceptor);
        registry.addInterceptor(commonPageDataInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new ZonedDateTimeFormatter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        webAppResources.getResources()
                .forEach(resource -> registry
                        .addResourceHandler(resource.getHandler())
                        .addResourceLocations(resource.getLocation()));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayHttpMessageConverter());
        converters.add(resourceHttpMessageConverter());
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

    @Bean
    public Parser commonmarkParser() {
        return Parser.builder().build();
    }

    @Bean
    public HtmlRenderer commonmarkHtmlRenderer() {
        return HtmlRenderer.builder().build();
    }

    @Bean
    @Primary
    public LayoutDialect layoutDialect(Parser commonmarkParser, HtmlRenderer commonmarkHtmlRenderer, ResourcePathResolver resourcePathResolver) {
        return new ExtendedLayoutDialect(commonmarkParser, commonmarkHtmlRenderer, resourcePathResolver);
    }

    private List<MediaType> supportedMediaTypes() {
        return Arrays.asList(
                MediaType.IMAGE_JPEG,
                MediaType.IMAGE_PNG,
                MediaType.IMAGE_GIF,
                MediaType.APPLICATION_OCTET_STREAM);
    }

}
