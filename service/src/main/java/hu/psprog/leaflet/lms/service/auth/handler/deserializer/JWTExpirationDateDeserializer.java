package hu.psprog.leaflet.lms.service.auth.handler.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * Deserializer to parse expiration date received in seconds.
 *
 * @author Peter Smith
 */
public class JWTExpirationDateDeserializer extends JsonDeserializer<Date> {

    private static final String MILLISECONDS_MULTIPLIER = "000";

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new Date(extractTimestamp(jsonParser));
    }

    private long extractTimestamp(JsonParser jsonParser) throws IOException {
        return Long.parseLong(jsonParser.getText().trim() + MILLISECONDS_MULTIPLIER);
    }
}
