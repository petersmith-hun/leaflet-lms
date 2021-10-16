package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.lms.web.controller.pagination.model.LogViewerPaginationAttributes;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link LogViewerPaginationHelper}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class LogViewerPaginationHelperTest extends PaginationHelperBaseTest {

    private static final String LIMIT = "10";
    private static final String ORDER_BY = "CONTENT";
    private static final String ORDER_DIRECTION = "ASC";
    private static final String SOURCE = "leaflet";
    private static final String LEVEL = "INFO";
    private static final String CONTENT = "log content";
    private static final String FROM = "2019-01-10";
    private static final String TO = "2019-01-11";
    private static final String DEFAULT_ORDER_BY = "TIMESTAMP";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LogViewerPaginationHelper logViewerPaginationHelper;

    @Test
    public void shouldExtractPaginationAttributes() {

        // given
        given(request.getParameter("limit")).willReturn(LIMIT);
        given(request.getParameter("orderBy")).willReturn(ORDER_BY);
        given(request.getParameter("orderDirection")).willReturn(ORDER_DIRECTION);
        given(request.getParameter("source")).willReturn(SOURCE);
        given(request.getParameter("level")).willReturn(LEVEL);
        given(request.getParameter("content")).willReturn(CONTENT);
        given(request.getParameter("from")).willReturn(FROM);
        given(request.getParameter("to")).willReturn(TO);

        // when
        LogViewerPaginationAttributes result = logViewerPaginationHelper.extractPaginationAttributes(request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getLimit(), equalTo(Integer.valueOf(LIMIT)));
        assertThat(result.getOrderBy(), equalTo(ORDER_BY));
        assertThat(result.getOrderDirection(), equalTo(ORDER_DIRECTION));
        assertThat(result.getSource(), equalTo(SOURCE));
        assertThat(result.getLevel(), equalTo(LEVEL));
        assertThat(result.getContent(), equalTo(CONTENT));
        assertThat(result.getFrom(), equalTo(FROM));
        assertThat(result.getTo(), equalTo(TO));
    }

    @Test
    public void shouldExtractPaginationAttributesWithEmptyDefaults() {

        // when
        LogViewerPaginationAttributes result = logViewerPaginationHelper.extractPaginationAttributes(request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getLimit(), equalTo(DEFAULT_LIMIT));
        assertThat(result.getOrderBy(), equalTo(DEFAULT_ORDER_BY));
        assertThat(result.getOrderDirection(), equalTo(DEFAULT_ORDER_DIRECTION.name()));
        assertThat(result.getSource(), equalTo(StringUtils.EMPTY));
        assertThat(result.getLevel(), equalTo(StringUtils.EMPTY));
        assertThat(result.getContent(), equalTo(StringUtils.EMPTY));
        assertThat(result.getFrom(), equalTo(StringUtils.EMPTY));
        assertThat(result.getTo(), equalTo(StringUtils.EMPTY));
    }
}
