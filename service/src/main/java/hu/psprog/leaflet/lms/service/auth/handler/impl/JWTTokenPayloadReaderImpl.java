package hu.psprog.leaflet.lms.service.auth.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.psprog.leaflet.lms.service.auth.handler.JWTTokenPayloadReader;
import hu.psprog.leaflet.lms.service.auth.user.AuthenticationUserDetailsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

/**
 * Implementation of {@link JWTTokenPayloadReader} interface.
 *
 * @author Peter Smith
 */
@Component
public class JWTTokenPayloadReaderImpl implements JWTTokenPayloadReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTTokenPayloadReaderImpl.class);
    private static final String TOKEN_PAYLOAD_SPLIT_REGEX_PATTERN = "\\.";

    private ObjectMapper objectMapper;

    @Autowired
    public JWTTokenPayloadReaderImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthenticationUserDetailsModel readPayload(String token) {

        String[] tokenParts = token.split(TOKEN_PAYLOAD_SPLIT_REGEX_PATTERN);
        String payload = new String(Base64.getDecoder().decode(tokenParts[1]));

        try {
            return objectMapper.readValue(payload, AuthenticationUserDetailsModel.class);
        } catch (IOException e) {
            LOGGER.error("Could not read JWT payload", e);
            return new AuthenticationUserDetailsModel();
        }
    }
}
