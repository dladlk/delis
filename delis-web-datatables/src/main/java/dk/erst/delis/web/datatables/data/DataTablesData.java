package dk.erst.delis.web.datatables.data;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTablesData<E> {

	private DataTablesInput input;
	private Specification<E> specification;
}
