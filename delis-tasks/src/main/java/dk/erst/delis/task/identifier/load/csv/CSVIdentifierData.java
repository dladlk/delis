package dk.erst.delis.task.identifier.load.csv;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CSVIdentifierData {

	@CsvBindByName(column="EAN")
	private String ean;
	
	@CsvBindByName
	private String name;
}