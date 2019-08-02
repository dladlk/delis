package dk.erst.delis.web.datatables.service;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import dk.erst.delis.web.datatables.data.DataTablesData;
import dk.erst.delis.web.datatables.data.PageData;

public interface EasyDatatablesListService<T> {

	DataTablesOutput<T> getDataTablesOutput(DataTablesData<T> data, DataTablesRepository<T, Long> repository);

	DataTablesOutput<T> getDataTablesOutput(PageData pageData);
}
