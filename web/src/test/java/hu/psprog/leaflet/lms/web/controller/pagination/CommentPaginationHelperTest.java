package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_BY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link CommentPaginationHelper}.
 * 
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class CommentPaginationHelperTest extends PaginationHelperBaseTest {

    @InjectMocks
    private CommentPaginationHelper commentPaginationHelper;

    @ParameterizedTest
    @MethodSource("fromOptionalDataProvider")
    public void shouldMapOrderByFromOptional(Optional<String> orderBy, OrderBy.Comment expected) {

        // when
        OrderBy.Comment result = commentPaginationHelper.mapOrderBy(orderBy);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("fromHttpServletRequestDataProvider")
    public void shouldMapOrderByFromHttpServletRequest(String orderBy, OrderBy.Comment expected) {

        // given
        request.setParameter(PARAMETER_ORDER_BY, orderBy);

        // when
        String result = commentPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(expected.name()));
    }

    @Test
    public void shouldMapOrderByFromHttpServletRequestWithMissingParameter() {

        // given
        request.removeAllParameters();

        // when
        String result = commentPaginationHelper.mapOrderBy(request);

        // then
        assertThat(result, equalTo(DEFAULT_ORDER_BY));
    }

    private static Stream<Arguments> fromOptionalDataProvider() {
        
        return Stream.of(
                Arguments.of(Optional.of("id"), OrderBy.Comment.ID),
                Arguments.of(Optional.of("ID"), OrderBy.Comment.ID),
                Arguments.of(Optional.of("CREATED"), OrderBy.Comment.CREATED),
                Arguments.of(Optional.of("non-existing"), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(Optional.empty(), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(Optional.ofNullable(null), OrderBy.Comment.valueOf(DEFAULT_ORDER_BY))
        );
    }

    private static Stream<Arguments> fromHttpServletRequestDataProvider() {

        return Stream.of(
                Arguments.of("id", OrderBy.Comment.ID),
                Arguments.of("ID", OrderBy.Comment.ID),
                Arguments.of("CREATED", OrderBy.Comment.CREATED),
                Arguments.of("non-existing", OrderBy.Comment.valueOf(DEFAULT_ORDER_BY)),
                Arguments.of(null, OrderBy.Comment.valueOf(DEFAULT_ORDER_BY))
        );
    }
}