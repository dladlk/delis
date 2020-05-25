package dk.erst.delis.web.datatables.data;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTablesOutput<T> {

	private List<T> data;
	private long recordsTotal;
	private String error;
}
