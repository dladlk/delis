package dk.erst.delis.web.datatables.service;

import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.dao.ICriteriaCustomizer;
import dk.erst.delis.web.datatables.data.DataTablesOutput;
import dk.erst.delis.web.datatables.data.PageData;

public interface EasyDatatablesListService<T> {

	DataTablesOutput<T> getDataTablesOutput(PageData pageData, DataTablesRepository<T, Long> repository, ICriteriaCustomizer<T> customizer);

	DataTablesOutput<T> getDataTablesOutput(PageData pageData);
	
}
