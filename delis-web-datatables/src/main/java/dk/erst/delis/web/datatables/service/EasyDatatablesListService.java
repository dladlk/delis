package dk.erst.delis.web.datatables.service;

import dk.erst.delis.web.datatables.data.DataTablesData;
import dk.erst.delis.web.datatables.data.PageData;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface EasyDatatablesListService<T> {

    DataTablesOutput<T> getDataTablesOutput(DataTablesData<T> data, DataTablesRepository<T, Long> repository);
    DataTablesOutput<T> getDataTablesOutput(PageData pageData);
}
