package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_BY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link CommentPaginationHelper}.
 * 
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class CommentPaginationHelperTest extends PaginationHelperBaseTest {

    @InjectMocks
    private CommentPaginationHelper commentPaginationHelper;

    @Test
    @Parameters(source = OrderByMappingParameterProvider.class, method = "fromOptional")
    public void shouldMapOrderByFromOptional(Optional<String> orderBy, OrderBy.Comment expected) {

        // when
        OrderBy.Comment result = commentPaginationHelper.mapOrderBy(orderBy);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = OrderByMappingParameterProvider.class, method = "fromHttpServletRequest")
    public void shouldMapOrderByFromHttpServletRequest(String orderBy, OrderBy.Comment expected) {

        // given
        request.setParameter(PARAMETER_ORDER_BY, orderBy);

        // when
        OrderBy.Comment result = commentPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    public void shouldMapOrderByFromHttpServletRequestWithMissingParameter() {

        // given
        request.removeAllParameters();

        // when
        OrderBy.Comment result = commentPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)));
    }

    public static class OrderByMappingParameterProvider {

        public static Object[] fromOptional() {
            return new Object[] {
                    new Object[] {Optional.of("id"), OrderBy.Comment.ID},
                    new Object[] {Optional.of("ID"), OrderBy.Comment.ID},
                    new Object[] {Optional.of("CREATED"), OrderBy.Comment.CREATED},
                    new Object[] {Optional.of("non-existing"), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {Optional.empty(), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {Optional.ofNullable(null), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)},
            };
        }

        public static Object[] fromHttpServletRequest() {
            return new Object[] {
                    new Object[] {"id", OrderBy.Comment.ID},
                    new Object[] {"ID", OrderBy.Comment.ID},
                    new Object[] {"CREATED", OrderBy.Comment.CREATED},
                    new Object[] {"non-existing", OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {null, OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)}
            };
        }
    }
}