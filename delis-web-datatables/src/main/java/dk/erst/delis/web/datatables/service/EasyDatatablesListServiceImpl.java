package dk.erst.delis.web.datatables.service;

import org.springframework.stereotype.Service;

import dk.erst.delis.web.datatables.dao.DataTablesRepository;
import dk.erst.delis.web.datatables.dao.ICriteriaCustomizer;
import dk.erst.delis.web.datatables.data.DataTablesOutput;
import dk.erst.delis.web.datatables.data.PageData;

@Service
public class EasyDatatablesListServiceImpl<T> implements EasyDatatablesListService<T> {

	public DataTablesOutput<T> getDataTablesOutput(PageData pageData, DataTablesRepository<T, Long> repository, ICriteriaCustomizer<T> customizer) {
		DataTablesOutput<T> dto = new DataTablesOutput<T>();
		
		try {
			dto = repository.findAll(pageData, customizer);
		} catch (Exception e) {
			dto = new DataTablesOutput<>();
			dto.setError(e.getMessage());
		}
		return dto;
	}

	@Override
	public DataTablesOutput<T> getDataTablesOutput(PageData pageData) {
		return null;
	}

}
