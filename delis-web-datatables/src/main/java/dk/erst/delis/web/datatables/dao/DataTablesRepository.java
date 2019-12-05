package dk.erst.delis.web.datatables.dao;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import dk.erst.delis.web.datatables.data.DataTablesOutput;
import dk.erst.delis.web.datatables.data.PageData;

@NoRepositoryBean
public interface DataTablesRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

	  DataTablesOutput<T> findAll(PageData pageData, ICriteriaCustomizer<T> customizer);
	  
}
