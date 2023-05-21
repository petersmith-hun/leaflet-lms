package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.BDDMockito.given;

/**
 * Base test for pagination helper tests.
 *
 * @author Peter Smith
 */
public abstract class PaginationHelperBaseTest {

    static final int DEFAULT_LIMIT = 10;
    static final String DEFAULT_ORDER_BY = "CREATED";
    static final OrderDirection DEFAULT_ORDER_DIRECTION = OrderDirection.ASC;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private PaginationDefaults paginationDefaults;

    MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        given(paginationDefaults.getLimit()).willReturn(DEFAULT_LIMIT);
        given(paginationDefaults.getOrderBy()).willReturn(DEFAULT_ORDER_BY);
        given(paginationDefaults.getOrderDirection()).willReturn(DEFAULT_ORDER_DIRECTION);
        request = new MockHttpServletRequest();
    }
}
