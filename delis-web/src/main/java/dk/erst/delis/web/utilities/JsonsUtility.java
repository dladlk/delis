package dk.erst.delis.web.utilities;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.Named;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component("jsons")
@Slf4j
public class JsonsUtility {
	
	public static interface INamed4Json {
		public String getValue();
		public String getName();
	}

	@Data
	public static class Named4Json implements INamed4Json {
		private String name;
		private String value;
		
		public static Named4Json of(String name, String value) {
			Named4Json n = new Named4Json();
			n.setName(name);
			n.setValue(value);
			return n;
		}
	}

	public String json(Object o) {
		ObjectMapper om = new ObjectMapper();

		SimpleModule module = new SimpleModule();
		module.addSerializer(Named.class, new NamedEnumSerializer());
		module.addSerializer(AbstractEntity.class, new NamedEntitySerializer());
		module.addSerializer(INamed4Json.class, new Named4JsonSerializer());
		
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

	public static class NamedEntitySerializer extends JsonSerializer<AbstractEntity> {

		@Override
		public void serialize(AbstractEntity value, JsonGenerator jGen, SerializerProvider serializers) throws IOException {
			jGen.writeStartObject();
			jGen.writeStringField("value", String.valueOf(value.getId()));
			String name = ((Organisation) value).getName();
			jGen.writeStringField("name", name);
			jGen.writeEndObject();
		}
	}

	public static class Named4JsonSerializer extends JsonSerializer<INamed4Json> {
		
		@Override
		public void serialize(INamed4Json value, JsonGenerator jGen, SerializerProvider serializers) throws IOException {
			jGen.writeStartObject();
			jGen.writeStringField("value", value.getValue());
			jGen.writeStringField("name", value.getName());
			jGen.writeEndObject();
		}
	}

}
