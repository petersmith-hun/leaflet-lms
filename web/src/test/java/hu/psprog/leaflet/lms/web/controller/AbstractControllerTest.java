package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.BaseController.FLASH_MESSAGE;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Base test for controllers.
 *
 * @author Peter Smith
 */
public abstract class AbstractControllerTest {

    static final Long USER_ID = 5L;
    static final Optional<Integer> PAGE = Optional.of(1);
    static final Optional<Integer> LIMIT = Optional.of(10);

    static final String VIEW_LIST = "list";
    static final String VIEW_DETAILS = "details";
    static final String VIEW_EDIT_FORM = "edit_form";
    static final String VIEW_DELETE_FORM = "delete_form";

    static final String FIELD_CONTENT = "content";
    static final String FIELD_PAGINATION = "pagination";
    static final String FIELD_SEO = "seo";

    private static final String VIEW_NAME_FORMAT = "view/%s/%s";

    @Mock
    HttpServletRequest request;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    private ModelAndViewFactory modelAndViewFactory;

    @Mock
    private ModelAndViewFactory.ModelAndViewWrapper modelAndViewWrapper;

    @Mock
    private ModelAndView modelAndView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        given(modelAndViewFactory.createForView(anyString())).willReturn(modelAndViewWrapper);
        given(modelAndViewWrapper.withAttribute(anyString(), anyString())).willReturn(modelAndViewWrapper);
        given(modelAndViewWrapper.build()).willReturn(modelAndView);
    }

    void verifyViewCreated(String name) {
        verify(modelAndViewFactory).createForView(String.format(VIEW_NAME_FORMAT, controllerViewGroup(), name));
    }

    void verifyRedirectionCreated(String to) {
        verify(modelAndViewFactory).createRedirectionTo(to);
    }

    void verifyFieldsSet(String... fieldName) {
        Arrays.stream(fieldName)
                .forEach(field -> verify(modelAndViewWrapper).withAttribute(eq(field), anyString()));
    }

    void verifyFlashMessageSet() {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), anyString());
    }

    void verifyStatusFlashMessage(boolean enabled) {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), contains(enabled ? "enabled" : "disabled"));
    }

    void verifyDeletionFlashMessage(boolean permanent) {
        verify(redirectAttributes).addFlashAttribute(eq(FLASH_MESSAGE), contains(permanent ? "permanently" : "logically"));
    }

    void verifyUserLoggedOut() throws ServletException {
        verify(request).logout();
    }

    abstract String controllerViewGroup();
}
