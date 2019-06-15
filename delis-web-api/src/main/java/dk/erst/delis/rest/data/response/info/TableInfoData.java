package dk.erst.delis.rest.data.response.info;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author funtusthan, created by 19.01.19
 */

@Getter
@Setter
public class TableInfoData {

    private String entityName;
    private Map<String, List<String>> entityEnumInfo;
}
