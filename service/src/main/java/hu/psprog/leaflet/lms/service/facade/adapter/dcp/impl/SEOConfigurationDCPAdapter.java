package hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl;

import hu.psprog.leaflet.api.rest.response.dcp.DCPDataModel;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.DCPAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link DCPAdapter} implementation to collect SEO settings stored in DCP.
 *
 * @author Peter Smith
 */
@Component
public class SEOConfigurationDCPAdapter extends AbstractDCPAdapter<SEOConfiguration> {

    private enum SEOKey {
        PAGE_TITLE(SEOConfiguration::getPageTitle),
        META_TITLE(SEOConfiguration::getDefaultTitle),
        META_DESCRIPTION(SEOConfiguration::getDefaultDescription),
        META_KEYWORDS(SEOConfiguration::getDefaultKeywords);

        private Function<SEOConfiguration, String> domainMapper;

        SEOKey(Function<SEOConfiguration, String> domainMapper) {
            this.domainMapper = domainMapper;
        }

        Function<SEOConfiguration, String> getDomainMapper() {
            return domainMapper;
        }
    }

    @Override
    List<DCPDataModel> mapToDataModel(SEOConfiguration data) {
        return Stream.of(SEOKey.values())
                .map(key -> DCPDataModel.getBuilder()
                        .withKey(key.name())
                        .withValue(key.getDomainMapper().apply(data))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    SEOConfiguration collectToDomain(List<DCPDataModel> dataItems) {
        return SEOConfiguration.getBuilder()
                .withPageTitle(extractValue(dataItems, SEOKey.PAGE_TITLE))
                .withDefaultTitle(extractValue(dataItems, SEOKey.META_TITLE))
                .withDefaultDescription(extractValue(dataItems, SEOKey.META_DESCRIPTION))
                .withDefaultKeywords(extractValue(dataItems, SEOKey.META_KEYWORDS))
                .build();
    }

    @Override
    Supplier<SEOConfiguration> emptyInstance() {
        return SEOConfiguration::new;
    }
}
