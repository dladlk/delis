package dk.erst.delis.task.identifier.load.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.task.identifier.load.AbstractIdentifierStreamReader;

public class CSVIdentifierStreamReader extends AbstractIdentifierStreamReader {

	private InputStream inputStream;
	private Charset charset;
	private Iterator<CSVIdentifierData> iterator;
	private char separator;

	public CSVIdentifierStreamReader(InputStream inputStream, Charset charset, char separator) {
		this.inputStream = inputStream;
		this.charset = charset;
		this.separator = separator;
	}
	
	@Override
	public void start() {
	    CsvToBeanBuilder<CSVIdentifierData> beanBuilder = new CsvToBeanBuilder<CSVIdentifierData>(new InputStreamReader(inputStream, charset));
	    beanBuilder.withType(CSVIdentifierData.class);
	    beanBuilder.withSeparator(separator);
	    
	    CsvToBean<CSVIdentifierData> csv = beanBuilder.build();
	    iterator = csv.iterator();
	}
	
	public boolean hasNext() {
		return this.iterator.hasNext();
	}
	
	@Override
	public Identifier next() {
		CSVIdentifierData v = this.iterator.next();
		if (v != null) {
			Identifier i = new Identifier();
			i.setName(v.getName());
			i.setValue(v.getEan());
			return i;
		}
		return null;
	}
}
