package hu.psprog.leaflet.lms.service.util;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class that can calculate the entity assignments differences.
 * It can be used between a local (LMS) request object and a Leaflet response object.
 * Resulting lists can be used to handle entity assignments - attaching and detaching entity connections.
 *
 * @author Peter Smith
 */
@Component
public class EntityConnectionDifferenceCalculator {

    /**
     * Builds a difference checker context for given lists.
     *
     * @param modificationRequest entity assignment list as local request (ex.: assigned IDs retrieved from a form)
     * @param currentConnections list of currently existing assignments (assignments returned by Leaflet)
     * @param modelMapper a stream mapper function that is able to map values in currentConnections list to the values in modificationRequest list
     * @param <M> type of items in local request list
     * @param <C> types of items in stored connections list
     * @return initialized {@link EntityConnectionContext} object
     */
    public <M, C extends BaseBodyDataModel> EntityConnectionContext<M, C> createContextFor(List<M> modificationRequest, List<C> currentConnections, Function<C, M> modelMapper) {
        return new EntityConnectionContext<>(modificationRequest, currentConnections, modelMapper);
    }

    /**
     * Class to describe an entity connection context.
     * Must contain a list of items from a local request, a list of items retrieved from Leaflet backend, and a mapper as above.
     *
     * @param <M> type of items in local request list
     * @param <C> types of items in stored connections list
     */
    public static class EntityConnectionContext<M, C extends BaseBodyDataModel> {

        private final List<M> modificationRequestConnectionList;
        private final List<C> currentConnectionList;
        private final Function<C, M> modelMapper;

        private EntityConnectionContext(List<M> modificationRequestConnectionList, List<C> currentConnectionList, Function<C, M> modelMapper) {
            this.modificationRequestConnectionList = modificationRequestConnectionList;
            this.currentConnectionList = currentConnectionList;
            this.modelMapper = modelMapper;
        }

        /**
         * Collects items that are part of the local request list, but aren't part of stored list.
         * These items must be attached.
         *
         * @return list of items to attach
         */
        public List<M> collectForAttach() {

            List<M> result;
            if (CollectionUtils.isEmpty(currentConnectionList)) {
                result = modificationRequestConnectionList;
            } else {
                result = modificationRequestConnectionList.stream()
                        .filter(checkedItem -> currentConnectionList.stream()
                                .map(modelMapper)
                                .noneMatch(existingItem -> existingItem.equals(checkedItem)))
                        .collect(Collectors.toList());
            }

            return result;
        }

        /**
         * Collects items that are part of the stored list, but aren't part of the local request list.
         * These items must be detached.
         *
         * @return list of items to detach
         */
        public List<M> collectForDetach() {

            List<M> result = Collections.emptyList();
            if (Objects.nonNull(currentConnectionList)) {
                result = currentConnectionList.stream()
                        .map(modelMapper)
                        .filter(existingItem -> modificationRequestConnectionList.stream()
                                .noneMatch(currentItem -> currentItem.equals(existingItem)))
                        .collect(Collectors.toList());
            }

            return result;
        }
    }
}
