package hu.psprog.leaflet.lms.web.support.thymeleaf.markdown;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Resolves path of downloadable contents.
 *
 * @author Peter Smith
 */
@Component
public class ResourcePathResolver {

    private static final String RESOURCE_SERVER_URL_PART = "{resource-server-url}";

    private final String resourceServerUrl;

    public ResourcePathResolver(@Value("${webapp.resource-server-url}") String resourceServerUrl) {
        this.resourceServerUrl = resourceServerUrl;
    }

    /**
     * Resolves (image) resources embedded in articles' markdown source by replacing {@code {resource-server-url}} placeholder in image links.
     *
     * @param rawContent markdown article source
     * @return markdown article source with resolved resource server
     */
    public String resolveInMarkdownSource(String rawContent) {
        return StringUtils.replace(rawContent, RESOURCE_SERVER_URL_PART, resourceServerUrl);
    }
}
