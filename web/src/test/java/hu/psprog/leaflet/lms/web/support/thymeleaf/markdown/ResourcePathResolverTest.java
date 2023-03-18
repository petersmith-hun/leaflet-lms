package hu.psprog.leaflet.lms.web.support.thymeleaf.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link ResourcePathResolver}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ResourcePathResolverTest {

    private static final String RESOURCE_SERVER_URL = "file:///resource-svc";
    private static final String UNRESOLVED_MARKDOWN = "this is a markdown source [with a resource]({resource-server-url}/res-01)";
    private static final String EXPECTED_RESOLVED_MARKDOWN = "this is a markdown source [with a resource](file:///resource-svc/res-01)";

    private ResourcePathResolver resourcePathResolver;

    @BeforeEach
    public void setup() {
        resourcePathResolver = new ResourcePathResolver(RESOURCE_SERVER_URL);
    }

    @Test
    public void shouldResolveInMarkdownSource() {

        // when
        String result = resourcePathResolver.resolveInMarkdownSource(UNRESOLVED_MARKDOWN);

        // then
        assertThat(result, equalTo(EXPECTED_RESOLVED_MARKDOWN));
    }
}
