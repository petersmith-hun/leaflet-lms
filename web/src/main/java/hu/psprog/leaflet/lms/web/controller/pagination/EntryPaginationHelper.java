package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Pagination helper implementation for entries.
 * Uses values from {@link OrderBy.Entry} enum.
 *
 * @author Peter Smith
 */
@Component
public class EntryPaginationHelper extends AbstractStandardPaginationHelper<OrderBy.Entry> {

    @Override
    public OrderBy.Entry mapOrderBy(Optional<String> optionalOrderBy) {
        return mapToEntryOrderBy(optionalOrderBy.orElse(defaultOrderBy()));
    }

    private OrderBy.Entry mapToEntryOrderBy(String orderBy) {
        return Stream.of(OrderBy.Entry.values())
                .filter(entry -> entry.getField().equals(orderBy.toLowerCase()))
                .findFirst()
                .orElse(OrderBy.Entry.CREATED);
    }
}
