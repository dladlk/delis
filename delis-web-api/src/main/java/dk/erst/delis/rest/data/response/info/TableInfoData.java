package dk.erst.delis.rest.data.response.info;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TableInfoData {

    private String entityName;
    private Map<String, List<EnumInfo>> entityEnumInfo;
}
