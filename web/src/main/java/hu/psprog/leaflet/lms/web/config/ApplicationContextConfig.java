package hu.psprog.leaflet.lms.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.RequestContextFilter;

import java.util.Set;

/**
 * Main application context configuration.
 *
 * @author Peter Smith
 */
@Configuration
public class ApplicationContextConfig {

    @Bean
    public RequestContextFilter requestContextFilter() {
        OrderedRequestContextFilter orderedRequestContextFilter = new OrderedRequestContextFilter();
        orderedRequestContextFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return orderedRequestContextFilter;
    }

    @Bean
    @Autowired
    @Primary
    public ConversionServiceFactoryBean lmsConversionService(final Set<Converter> converters) {

        ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
        conversionServiceFactoryBean.setConverters(converters);

        return conversionServiceFactoryBean;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer applicationConfigPropertySource() {

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("version.properties"));

        return configurer;
    }
}
