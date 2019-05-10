package dk.erst.delis.web.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@UtilityClass
public class DataTableUtil {

    public DataTablesOutput reinitializationRecordsTotal(DataTablesOutput output) {
        if (output.getData() != null && output.getData().isEmpty()) {
            output.setRecordsTotal(0L);
        }
        return output;
    }
}
