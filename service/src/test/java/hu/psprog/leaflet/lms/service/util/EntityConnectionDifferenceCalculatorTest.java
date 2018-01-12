package hu.psprog.leaflet.lms.service.util;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link EntityConnectionDifferenceCalculator}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityConnectionDifferenceCalculatorTest {

    private static final Function<TestModel, Long> TEST_MODEL_MAPPER = TestModel::getId;

    @InjectMocks
    private EntityConnectionDifferenceCalculator calculator;

    @Test
    public void shouldAttachAllItems() {

        // given
        List<Long> modification = prepareModification(1L, 5L);
        List<TestModel> current = Collections.emptyList();

        // when
        List<Long> result = prepareContext(modification, current).collectForAttach();


        // then
        assertThat(result, equalTo(modification));
    }

    @Test
    public void shouldDetachAllItems() {

        // given
        List<Long> modification = Collections.emptyList();
        List<TestModel> current = prepareCurrent(1L, 5L);

        // when
        List<Long> result = prepareContext(modification, current).collectForDetach();

        // then
        assertThat(result, equalTo(current.stream().map(TestModel::getId).collect(Collectors.toList())));
    }

    @Test
    public void shouldReturnEmptyListOnDetachWithNullCurrentList() {

        // given
        List<Long> modification = Collections.emptyList();
        List<TestModel> current = null;

        // when
        List<Long> result = prepareContext(modification, current).collectForDetach();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldAttachOnlyTheNewItem() {

        // given
        List<Long> modification = prepareModification(1L, 5L);
        List<TestModel> current = prepareCurrent(1L, 4L);

        // when
        List<Long> result = prepareContext(modification, current).collectForAttach();

        // then
        assertThat(result, equalTo(Collections.singletonList(5L)));
    }

    @Test
    public void shouldNotDetachAnything() {

        // given
        List<Long> modification = prepareModification(1L, 5L);
        List<TestModel> current = prepareCurrent(1L, 4L);

        // when
        List<Long> result = prepareContext(modification, current).collectForDetach();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldDetachRemovedItems() {

        // given
        List<Long> modification = prepareModification(2L, 3L);
        List<TestModel> current = prepareCurrent(1L, 5L);

        // when
        List<Long> result = prepareContext(modification, current).collectForDetach();

        // then
        assertThat(result, equalTo(Arrays.asList(1L, 4L, 5L)));
    }

    @Test
    public void shouldAttachNewItemsMixedScenario() {

        // given
        List<Long> modification = prepareModification(3L, 6L);
        List<TestModel> current = prepareCurrent(1L, 5L);

        // when
        List<Long> result = prepareContext(modification, current).collectForAttach();

        // then
        assertThat(result, equalTo(Collections.singletonList(6L)));
    }

    @Test
    public void shouldDetachRemovedItemsMixedScenario() {

        // given
        List<Long> modification = prepareModification(3L, 6L);
        List<TestModel> current = prepareCurrent(1L, 5L);

        // when
        List<Long> result = prepareContext(modification, current).collectForDetach();

        // then
        assertThat(result, equalTo(Arrays.asList(1L, 2L)));
    }

    private EntityConnectionDifferenceCalculator.EntityConnectionContext<Long, TestModel> prepareContext(List<Long> modification, List<TestModel> current) {
        return calculator.createContextFor(modification, current, TEST_MODEL_MAPPER);
    }

    private List<Long> prepareModification(Long from, Long to) {
        return LongStream.range(from, to + 1L)
                .boxed()
                .collect(Collectors.toList());
    }

    private List<TestModel> prepareCurrent(Long from, Long to) {
        return LongStream.range(from, to + 1L)
                .boxed()
                .map(TestModel::new)
                .collect(Collectors.toList());
    }

    private class TestModel extends BaseBodyDataModel {

        private Long id;

        public TestModel(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}