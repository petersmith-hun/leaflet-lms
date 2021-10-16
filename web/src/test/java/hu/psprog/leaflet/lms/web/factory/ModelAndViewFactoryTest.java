package hu.psprog.leaflet.lms.web.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link ModelAndViewFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ModelAndViewFactoryTest {

    private static final String TEST_VIEW = "testView";
    private static final String REDIRECT_VIEW_NAME = "redirect:" + TEST_VIEW;
    private static final String ATTRIBUTE_1 = "attribute-1";
    private static final String ATTRIBUTE_2 = "attribute-2";
    private static final String VALUE_FOR_ATTRIBUTE_1 = "value-for-attribute-1";
    private static final String VALUE_FOR_ATTRIBUTE_2 = "value-for-attribute-2";

    @InjectMocks
    private ModelAndViewFactory modelAndViewFactory;

    @Test
    public void shouldCreateViewWithoutAttributes() {

        // when
        ModelAndView result = modelAndViewFactory.createForView(TEST_VIEW)
                .build();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getViewName(), equalTo(TEST_VIEW));
        assertThat(result.getModel().isEmpty(), is(true));
    }

    @Test
    public void shouldCreateViewWithAttributes() {

        // when
        ModelAndView result = modelAndViewFactory.createForView(TEST_VIEW)
                .withAttribute(ATTRIBUTE_1, VALUE_FOR_ATTRIBUTE_1)
                .withAttribute(ATTRIBUTE_2, VALUE_FOR_ATTRIBUTE_2)
                .build();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getViewName(), equalTo(TEST_VIEW));
        assertThat(result.getModel().size(), equalTo(2));
        assertThat(result.getModel().get(ATTRIBUTE_1), equalTo(VALUE_FOR_ATTRIBUTE_1));
        assertThat(result.getModel().get(ATTRIBUTE_2), equalTo(VALUE_FOR_ATTRIBUTE_2));
    }

    @Test
    public void shouldCreateRedirectionView() {

        // when
        ModelAndView result = modelAndViewFactory.createRedirectionTo(TEST_VIEW);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getViewName(), equalTo(REDIRECT_VIEW_NAME));
    }
}