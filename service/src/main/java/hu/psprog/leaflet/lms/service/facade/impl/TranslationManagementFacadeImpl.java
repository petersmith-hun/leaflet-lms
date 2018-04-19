package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.translations.TranslationPackUploadRequestModel;
import hu.psprog.leaflet.lms.service.facade.TranslationManagementFacade;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackCreationRequest;
import hu.psprog.leaflet.translation.api.domain.TranslationPackMetaInfo;
import hu.psprog.leaflet.translation.client.TranslationServiceClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TranslationManagementFacade}.
 *
 * @author Peter Smith
 */
@Service
public class TranslationManagementFacadeImpl implements TranslationManagementFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationManagementFacadeImpl.class);
    private static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
    private static final String FAILED_TO_READ_INPUT_CSV = "Failed to read input CSV";
    private static final String LINE_SPLIT_EXPRESSION = ";";

    private TranslationServiceClient translationServiceClient;

    @Autowired
    public TranslationManagementFacadeImpl(TranslationServiceClient translationServiceClient) {
        this.translationServiceClient = translationServiceClient;
    }

    @Override
    public List<TranslationPackMetaInfo> getPacks() throws CommunicationFailureException {
        return translationServiceClient.listStoredPacks();
    }

    @Override
    public TranslationPack getPack(UUID packID) throws CommunicationFailureException {
        return translationServiceClient.getPackByID(packID);
    }

    @Override
    public UUID processCreatePack(TranslationPackUploadRequestModel translationPackUploadRequestModel) throws CommunicationFailureException {

        TranslationPackCreationRequest translationPackCreationRequest = new TranslationPackCreationRequest();
        translationPackCreationRequest.setPackName(translationPackUploadRequestModel.getPackName());
        translationPackCreationRequest.setLocale(translationPackUploadRequestModel.getLocale());
        translationPackCreationRequest.setDefinitions(extractDefinitions(translationPackUploadRequestModel));

        if (translationPackCreationRequest.getDefinitions().isEmpty()) {
            throw new IllegalArgumentException("Translation pack definitions cannot be empty");
        }

        return Optional.ofNullable(translationServiceClient.createTranslationPack(translationPackCreationRequest))
                .map(TranslationPackMetaInfo::getId)
                .orElse(null);
    }

    @Override
    public void processDeletePack(UUID packID) throws CommunicationFailureException {
        translationServiceClient.deleteTranslationPack(packID);
    }

    @Override
    public boolean processChangePackStatus(UUID packID) throws CommunicationFailureException {
        return translationServiceClient.changePackStatus(packID).isEnabled();
    }

    private Map<String, String> extractDefinitions(TranslationPackUploadRequestModel translationPackUploadRequestModel) {

        try {
            InputStream inputStream = translationPackUploadRequestModel.getDefinitions().getInputStream();
            return IOUtils.readLines(inputStream, UTF_8_CHARSET).stream()
                    .map(line -> TemporalDefinition.build(line.split(LINE_SPLIT_EXPRESSION)))
                    .filter(TemporalDefinition::isValid)
                    .collect(Collectors.toMap(TemporalDefinition::getKey, TemporalDefinition::getValue));
        } catch (IOException exc) {
            LOGGER.error(FAILED_TO_READ_INPUT_CSV, exc);
            throw new IllegalArgumentException(FAILED_TO_READ_INPUT_CSV);
        }
    }

    private static class TemporalDefinition {

        private String key;
        private String value;

        String getKey() {
            return key;
        }

        String getValue() {
            return value;
        }

        private TemporalDefinition() {
        }

        boolean isValid() {
            return Objects.nonNull(key);
        }

        static TemporalDefinition build(String[] splitLine) {

            TemporalDefinition temporalDefinition = new TemporalDefinition();
            if (splitLine.length == 2) {
                temporalDefinition.key = splitLine[0];
                temporalDefinition.value = splitLine[1];
            } else {
                LOGGER.warn("Ignoring invalid definition [{}]", Arrays.toString(splitLine));
            }

            return temporalDefinition;
        }
    }
}
