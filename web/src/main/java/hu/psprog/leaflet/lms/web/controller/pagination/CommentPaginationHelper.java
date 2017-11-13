package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Pagination helper implementation for comments.
 * Uses values from {@link OrderBy.Comment} enum.
 *
 * @author Peter Smith
 */
@Component
public class CommentPaginationHelper extends PaginationHelper<OrderBy.Comment> {

    @Override
    public OrderBy.Comment mapOrderBy(Optional<String> optionalOrderBy) {
        return mapToCommentOrderBy(optionalOrderBy.orElse(defaultOrderBy()));
    }

    private OrderBy.Comment mapToCommentOrderBy(String orderBy) {
        return Stream.of(OrderBy.Comment.values())
                .filter(comment -> comment.getField().equals(orderBy.toLowerCase()))
                .findFirst()
                .orElse(OrderBy.Comment.CREATED);
    }
}
