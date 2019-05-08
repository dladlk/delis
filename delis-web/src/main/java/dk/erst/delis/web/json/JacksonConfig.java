package dk.erst.delis.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dk.erst.delis.data.enums.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.ArrayList;

@Configuration
public class JacksonConfig {

    public static class NamedSerializer extends StdSerializer<Named> {
        public NamedSerializer() {
            super(Named.class);
        }

        public NamedSerializer(Class t) {
            super(t);
        }

        public void serialize(Named o, JsonGenerator generator,
                              SerializerProvider provider)
                throws IOException, JsonProcessingException {
            generator.writeString(o.getName());
        }
    }

    public class DelisJsonSerializer extends SimpleSerializers {


        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            if (type.isEnumType() &&  type.isTypeOrSubTypeOf(Named.class)) {
                return new NamedSerializer();
            }

            return super.findSerializer(config, type, beanDesc);
        }

    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        ArrayList<Module> modules = new ArrayList<>();

        SimpleModule delisSerializerModule = new SimpleModule();
        delisSerializerModule.setSerializers(new DelisJsonSerializer());
        modules.add(delisSerializerModule);

        return Jackson2ObjectMapperBuilder.json()
                .modules(modules)
                 .build();
    }
}
