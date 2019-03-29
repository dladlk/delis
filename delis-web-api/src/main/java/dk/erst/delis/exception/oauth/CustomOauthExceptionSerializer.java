package dk.erst.delis.exception.oauth;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author funtusthan, created by 28.03.19
 */

public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("code", HttpStatus.UNAUTHORIZED.value());
        jsonGenerator.writeBooleanField("status", false);
        jsonGenerator.writeObjectField("data", new Date());
        jsonGenerator.writeObjectField("errors", Arrays.asList(value.getOAuth2ErrorCode(), value.getMessage()));
        if (Objects.nonNull(value.getAdditionalInformation())) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }
        jsonGenerator.writeEndObject();
    }
}
