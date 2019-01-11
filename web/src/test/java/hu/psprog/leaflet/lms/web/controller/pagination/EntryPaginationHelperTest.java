package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.api.rest.response.common.PaginationDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.lms.web.controller.pagination.model.PaginationAttributes;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_LIMIT;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_BY;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_DIRECTION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link EntryPaginationHelper}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class EntryPaginationHelperTest extends PaginationHelperBaseTest {

    private static final int PAGE_COUNT = 3;
    private static final int PAGE_NUMBER = 2;
    private static final boolean HAS_NEXT = true;
    private static final boolean HAS_PREVIOUS = true;
    private static final WrapperBodyDataModel<?> WRAPPER_BODY_DATA_MODEL = WrapperBodyDataModel.getBuilder()
            .withPagination(PaginationDataModel.getBuilder()
                    .withPageCount(PAGE_COUNT)
                    .withPageNumber(PAGE_NUMBER)
                    .withHasNext(HAS_NEXT)
                    .withHasPrevious(HAS_PREVIOUS)
                    .build())
            .build();
    private static final String LIMIT = "20";
    private static final String ORDER_BY = "TITLE";
    private static final String ORDER_DIRECTION = "ASC";

    @InjectMocks
    private EntryPaginationHelper entryPaginationHelper;

    @Test
    @Parameters(source = OrderByMappingParameterProvider.class, method = "fromOptional")
    public void shouldMapOrderByFromOptional(Optional<String> orderBy, OrderBy.Entry expected) {

        // when
        OrderBy.Entry result = entryPaginationHelper.mapOrderBy(orderBy);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = OrderByMappingParameterProvider.class, method = "fromHttpServletRequest")
    public void shouldMapOrderByFromHttpServletRequest(String orderBy, OrderBy.Entry expected) {

        // given
        request.setParameter(PARAMETER_ORDER_BY, orderBy);

        // when
        String result = entryPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(expected.name()));
    }

    @Test
    public void shouldMapOrderByFromHttpServletRequestWithMissingParameter() {

        // given
        request.removeAllParameters();

        // when
        String result = entryPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(DEFAULT_ORDER_BY));
    }

    @Test
    @Parameters(source = PageNumberParameterProvider.class)
    public void shouldExtractPage(Optional<Integer> page, int expected) {

        // when
        int result = entryPaginationHelper.extractPage(page);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = OrderDirectionMappingParameterProvider.class, method = "fromOptional")
    public void shouldMapOrderDirectionFromOptional(Optional<String> direction, OrderDirection expected) {

        // when
        OrderDirection result = entryPaginationHelper.mapOrderDirection(direction);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = OrderDirectionMappingParameterProvider.class, method = "fromHttpServletRequest")
    public void shouldMapOrderDirectionFromHttpServletRequest(String direction, OrderDirection expected) {

        // given
        request.setParameter(PARAMETER_ORDER_DIRECTION, direction);

        // when
        String result = entryPaginationHelper.mapOrderDirection(request);

        // then
        assertThat(result, equalTo(expected.name()));
    }

    @Test
    public void shouldMapOrderDirectionFromHttpServletRequestWithMissingParameter() {

        // given
        request.removeAllParameters();

        // when
        String result = entryPaginationHelper.mapOrderDirection(request);

        // then
        assertThat(result, equalTo(DEFAULT_ORDER_DIRECTION.name()));
    }

    @Test
    @Parameters(source = LimitParameterProvider.class, method = "fromOptional")
    public void shouldGetLimitFromOptional(Optional<Integer> limit, int expected) {

        // when
        int result = entryPaginationHelper.getLimit(limit);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = LimitParameterProvider.class, method = "fromHttpServletRequest")
    public void shouldGetLimitFromHttpServletRequest(String limit, int expected) {

        // given
        request.setParameter(PARAMETER_LIMIT, limit);

        // when
        int result = entryPaginationHelper.getLimit(request);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    public void shouldGetLimitFromHttpServletRequestWithMissingParameter() {

        // given
        request.removeAllParameters();

        // when
        int result = entryPaginationHelper.getLimit(request);

        // then
        assertThat(result, equalTo(DEFAULT_LIMIT));
    }

    @Test
    public void shouldExtractPaginationAttributes() {

        // given
        request.setParameter("limit", LIMIT);
        request.setParameter("orderBy", ORDER_BY);
        request.setParameter("orderDirection", ORDER_DIRECTION);

        // when
        PaginationAttributes result = entryPaginationHelper.extractPaginationAttributes(WRAPPER_BODY_DATA_MODEL, request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getLimit(), equalTo(Integer.valueOf(LIMIT)));
        assertThat(result.getOrderBy(), equalTo(ORDER_BY));
        assertThat(result.getOrderDirection(), equalTo(ORDER_DIRECTION));
        assertThat(result.getPageCount(), equalTo(PAGE_COUNT));
        assertThat(result.getPageNumber(), equalTo(PAGE_NUMBER));
        assertThat(result.isHasNext(), equalTo(HAS_NEXT));
        assertThat(result.isHasPrevious(), equalTo(HAS_PREVIOUS));
    }

    public static class PageNumberParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {Optional.of(10), 10},
                    new Object[] {Optional.of(1), 1},
                    new Object[] {Optional.empty(), 1},
                    new Object[] {Optional.ofNullable(null), 1},
            };
        }
    }

    public static class LimitParameterProvider {

        public static Object[] fromOptional() {
            return new Object[] {
                    new Object[] {Optional.of(30), 30},
                    new Object[] {Optional.ofNullable(null), DEFAULT_LIMIT},
                    new Object[] {Optional.empty(), DEFAULT_LIMIT}
            };
        }

        public static Object[] fromHttpServletRequest() {
            return new Object[] {
                    new Object[] {"30", 30},
                    new Object[] {null, DEFAULT_LIMIT},
            };
        }
    }

    public static class OrderDirectionMappingParameterProvider {

        public static Object[] fromOptional() {
            return new Object[] {
                    new Object[] {Optional.of("desc"), OrderDirection.DESC},
                    new Object[] {Optional.of("DESC"), OrderDirection.DESC},
                    new Object[] {Optional.of("non-existing"), OrderDirection.ASC},
                    new Object[] {Optional.ofNullable(null), DEFAULT_ORDER_DIRECTION},
                    new Object[] {Optional.empty(), DEFAULT_ORDER_DIRECTION}
            };
        }

        public static Object[] fromHttpServletRequest() {
            return new Object[] {
                    new Object[] {"desc", OrderDirection.DESC},
                    new Object[] {"DESC", OrderDirection.DESC},
                    new Object[] {"asc", OrderDirection.ASC},
                    new Object[] {"non-existing", DEFAULT_ORDER_DIRECTION},
                    new Object[] {null, DEFAULT_ORDER_DIRECTION}
            };
        }
    }

    public static class OrderByMappingParameterProvider {

        public static Object[] fromOptional() {
            return new Object[] {
                    new Object[] {Optional.of("title"), OrderBy.Entry.TITLE},
                    new Object[] {Optional.of("TITLE"), OrderBy.Entry.TITLE},
                    new Object[] {Optional.of("CREATED"), OrderBy.Entry.CREATED},
                    new Object[] {Optional.of("non-existing"), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {Optional.empty(), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {Optional.ofNullable(null), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)},
            };
        }

        public static Object[] fromHttpServletRequest() {
            return new Object[] {
                    new Object[] {"title", OrderBy.Entry.TITLE},
                    new Object[] {"TITLE", OrderBy.Entry.TITLE},
                    new Object[] {"CREATED", OrderBy.Entry.CREATED},
                    new Object[] {"non-existing", OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)},
                    new Object[] {null, OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)}
            };
        }
    }
}