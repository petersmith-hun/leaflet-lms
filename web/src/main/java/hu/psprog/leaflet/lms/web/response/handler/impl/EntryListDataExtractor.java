package hu.psprog.leaflet.lms.web.response.handler.impl;

import hu.psprog.leaflet.lms.web.response.model.entry.EntryResponseModel;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Extracts a {@link List} of {@link EntryResponseModel}.
 *
 * @author Peter Smith
 */
@Component
public class EntryListDataExtractor extends AbstractResponseDataExtractor<List<EntryResponseModel>> {

    private static final String TITLE = "title";
    private static final String CREATED = "created";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String USERNAME = "username";
    private static final String CATEGORY = "category";
    private static final String ENTRIES = "entries";

    @Override
    public List<EntryResponseModel> extract(Map<String, Object> response) {
        List<EntryResponseModel> entryResponseModelList = new LinkedList<>();
        Map<String, Object> content = extractBody(response);
        if (Objects.nonNull(content)) {
            List<Map<String, Object>> entries = extractList(content, ENTRIES);
            entries.forEach(item -> entryResponseModelList.add(new EntryResponseModel.Builder()
                    .withTitle(extractString(item, TITLE))
                    .withCreated(extractString(item, CREATED))
                    .withLastModified(extractString(item, LAST_MODIFIED))
                    .withAuthor(extractString(extractNode(item, USER), USERNAME))
                    .withCategoryID(extractInteger(extractNode(item, CATEGORY), ID))
                    .build())
            );
        }

        return entryResponseModelList;
    }
}
