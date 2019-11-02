package dk.erst.delis.web.utilities;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import dk.erst.delis.data.enums.Named;
import lombok.extern.slf4j.Slf4j;

@Component("jsons")
@Slf4j
public class JsonsUtility {

	public String json(Object o) {
		ObjectMapper om = new ObjectMapper();

		SimpleModule module = new SimpleModule();
		module.addSerializer(Named.class, new NamedEnumSerializer());
		
		om.registerModule(module);

		StringWriter sw = new StringWriter();
		try {
			om.writer().writeValue(sw, o);
		} catch (Exception e) {
			log.error("Failed to JSON serialize " + o, e);
			return e.getMessage();
		}
		return sw.toString();
	}

	public static class NamedEnumSerializer extends JsonSerializer<Named> {

		@Override
		public void serialize(Named value, JsonGenerator jGen, SerializerProvider serializers) throws IOException {
			jGen.writeStartObject();
			jGen.writeStringField("value", ((Enum<?>) value).name());
			jGen.writeStringField("name", value.getName());
			jGen.writeEndObject();
		}
	}

}
