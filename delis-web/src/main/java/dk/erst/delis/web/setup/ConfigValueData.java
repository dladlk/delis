package dk.erst.delis.web.setup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigValueData {
    private String description;
    private String key;
    private Object value;
    private String typeName;
}
