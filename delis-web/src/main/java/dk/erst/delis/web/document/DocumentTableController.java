package dk.erst.delis.web.document;

import dk.erst.delis.dao.DocumentTableRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.web.util.DataTableUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class DocumentTableController {

    private static final String CREATE_TIME = "createTime";
    private static final String TODAY = "Today";
    private static final String LAST_WEEK = "Last week";
    private static final String LAST_MONTH = "Last month";

    private DocumentTableRepository repository;

    public DocumentTableController(DocumentTableRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/document/table", method = RequestMethod.POST)
    public DataTablesOutput listPOST(@Valid @RequestBody DataTablesInput input) {
        Column column = input.getColumns().get(1);
        String value = column.getSearch().getValue();
        if (StringUtils.isNotEmpty(value)) {
            Specification<Document> additionalSpecification = getDateRangeSpec(CREATE_TIME, value);
            column.getSearch().setValue("");
            return DataTableUtil.reinitializationRecordsTotal(repository.findAll(input, additionalSpecification));
        } else {
            return DataTableUtil.reinitializationRecordsTotal(repository.findAll(input));
        }
    }

    private Specification<Document> getDateRangeSpec(final String field, String value) {
        final Date startDate;
        final Date endDate = new Date();

        if (TODAY.equalsIgnoreCase(value)) {
            startDate = DateUtil.getDayStartDate(endDate);
        } else if (LAST_WEEK.equalsIgnoreCase(value)) {
            startDate = DateUtil.getWeekStartDate(endDate);
        } else if (LAST_MONTH.equalsIgnoreCase(value)) {
            startDate = DateUtil.getMonthStartDate(endDate);
        } else {
            startDate = endDate;
        }
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.between(root.get(field), startDate, endDate);
    }
}
