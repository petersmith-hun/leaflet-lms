package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.api.rest.response.common.PaginationDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.lms.web.controller.pagination.model.PaginationAttributes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

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
@ExtendWith(MockitoExtension.class)
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

    @ParameterizedTest
    @MethodSource("orderByMappingFromOptionalDataProvider")
    public void shouldMapOrderByFromOptional(Optional<String> orderBy, OrderBy.Entry expected) {

        // when
        OrderBy.Entry result = entryPaginationHelper.mapOrderBy(orderBy);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("orderByMappingFromHttpServletRequestDataProvider")
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

    @ParameterizedTest
    @MethodSource("pageNumberDataProvider")
    public void shouldExtractPage(Optional<Integer> page, int expected) {

        // when
        int result = entryPaginationHelper.extractPage(page);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("orderDirectionMappingFromOptionalDataProvider")
    public void shouldMapOrderDirectionFromOptional(Optional<String> direction, OrderDirection expected) {

        // when
        OrderDirection result = entryPaginationHelper.mapOrderDirection(direction);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("orderDirectionMappingFromHttpServletRequestDataProvider")
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

    @ParameterizedTest
    @MethodSource("limitFromOptionalDataProvider")
    public void shouldGetLimitFromOptional(Optional<Integer> limit, int expected) {

        // when
        int result = entryPaginationHelper.getLimit(limit);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("limitFromHttpServletRequestDataProvider")
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

    private static Stream<Arguments> pageNumberDataProvider() {

        return Stream.of(
                Arguments.of(Optional.of(10), 10),
                Arguments.of(Optional.of(1), 1),
                Arguments.of(Optional.empty(), 1),
                Arguments.of(Optional.ofNullable(null), 1)
        );
    }

    private static Stream<Arguments> limitFromOptionalDataProvider() {

        return Stream.of(
                Arguments.of(Optional.of(30), 30),
                Arguments.of(Optional.ofNullable(null), DEFAULT_LIMIT),
                Arguments.of(Optional.empty(), DEFAULT_LIMIT)
        );
    }

    private static Stream<Arguments> limitFromHttpServletRequestDataProvider() {

        return Stream.of(
                Arguments.of("30", 30),
                Arguments.of(null, DEFAULT_LIMIT)
        );
    }

    private static Stream<Arguments> orderDirectionMappingFromOptionalDataProvider() {

        return Stream.of(
                Arguments.of(Optional.of("desc"), OrderDirection.DESC),
                Arguments.of(Optional.of("DESC"), OrderDirection.DESC),
                Arguments.of(Optional.of("non-existing"), OrderDirection.ASC),
                Arguments.of(Optional.ofNullable(null), DEFAULT_ORDER_DIRECTION),
                Arguments.of(Optional.empty(), DEFAULT_ORDER_DIRECTION)
        );
    }

    private static Stream<Arguments> orderDirectionMappingFromHttpServletRequestDataProvider() {

        return Stream.of(
                Arguments.of("desc", OrderDirection.DESC),
                Arguments.of("DESC", OrderDirection.DESC),
                Arguments.of("asc", OrderDirection.ASC),
                Arguments.of("non-existing", DEFAULT_ORDER_DIRECTION),
                Arguments.of(null, DEFAULT_ORDER_DIRECTION)
        );
    }

    private static Stream<Arguments> orderByMappingFromOptionalDataProvider() {

        return Stream.of(
                Arguments.of(Optional.of("title"), OrderBy.Entry.TITLE),
                Arguments.of(Optional.of("TITLE"), OrderBy.Entry.TITLE),
                Arguments.of(Optional.of("CREATED"), OrderBy.Entry.CREATED),
                Arguments.of(Optional.of("non-existing"), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(Optional.empty(), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(Optional.ofNullable(null), OrderBy.Entry.valueOf(DEFAULT_ORDER_BY))
        );
    }

    private static Stream<Arguments> orderByMappingFromHttpServletRequestDataProvider() {

        return Stream.of(
                Arguments.of("title", OrderBy.Entry.TITLE),
                Arguments.of("TITLE", OrderBy.Entry.TITLE),
                Arguments.of("CREATED", OrderBy.Entry.CREATED),
                Arguments.of("non-existing", OrderBy.Entry.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(null, OrderBy.Entry.valueOf(DEFAULT_ORDER_BY))
        );
    }
}