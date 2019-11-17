package dk.erst.delis.task.identifier.load.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierValueType;
import dk.erst.delis.task.identifier.load.AbstractIdentifierStreamReader;
import dk.erst.delis.task.identifier.load.IdentifierLoadService;
import dk.erst.delis.task.identifier.load.IdentifierValidator;

public class CSVIdentifierStreamReader extends AbstractIdentifierStreamReader {
	
	protected static final String END_FORMAT_DESCRIPTION = "CSV file is not valid: ";

	private static final String COLUMN_NUMBER = "number";
	private static final String COLUMN_NAME = "name";

	private static final Set<String> EXPECTED_COLUMN_SET = new LinkedHashSet<String>(Arrays.asList(new String[] { COLUMN_NUMBER, COLUMN_NAME }));

	private Iterator<Identifier> iterator;

	public CSVIdentifierStreamReader(InputStream inputStream, Charset charset, char separator) throws IdentifierListParseException {
		List<Identifier> loadedList = this.readStream(inputStream, charset, separator);
		this.iterator = loadedList.iterator();
	}

	private String collectLine(Collection<String> c, char separator) {
		return c.stream().collect(Collectors.joining(String.valueOf(separator)));
	}

	private String collectLine(String[] c, char separator) {
		return Arrays.asList(c).stream().collect(Collectors.joining(String.valueOf(separator)));
	}

	private static class ParseData {
		int indexNumber = -1;
		int indexName = -1;
		int maxIndex = -1;
		int lineIndex = -1;
		char separator;
	}
	
	protected List<Identifier> readStream(InputStream inputStream, Charset charset, char separator) throws IdentifierListParseException {
		CSVReaderBuilder builder = new CSVReaderBuilder(new InputStreamReader(inputStream, charset));
		builder.withCSVParser(new CSVParserBuilder().withSeparator(separator).build());
		CSVReader csv = builder.build();
		Map<String, Integer> columnIndexMap = null;

		List<Identifier> parsedList = new ArrayList<Identifier>();

		String[] lastLine = null;
		
		ParseData pd = new ParseData();
		pd.separator = separator;
		
		for (String[] curLine : csv) {
			if (columnIndexMap == null) {
				columnIndexMap = buildColumnIndexMap(curLine);
				if (!columnIndexMap.keySet().containsAll(EXPECTED_COLUMN_SET)) {
					String expectedColumnLine = collectLine(EXPECTED_COLUMN_SET, pd.separator);
					String parsedColumnLine = collectLine(Arrays.asList(curLine), pd.separator);
					String errorMessage = String.format("CSV file is not valid: expected column names are not found. Expected columns: '%s', parsed: '%s'", expectedColumnLine, parsedColumnLine);
					throw new IdentifierListParseException(errorMessage);
				}

				pd.indexNumber = columnIndexMap.get(COLUMN_NUMBER);
				pd.indexName = columnIndexMap.get(COLUMN_NAME);
				pd.maxIndex = Math.max(pd.indexName, pd.indexNumber);
			} else {
				/*
				 * Last line of CSV should contain single value with number of passed identifiers 
				 */
				if (lastLine != null) {
					Identifier i = processLine(pd, lastLine);
					parsedList.add(i);
				}
				lastLine = curLine;
			}
			pd.lineIndex++;
		}
		
		if (lastLine == null) {
			String errorMessage = String.format(END_FORMAT_DESCRIPTION);
			throw new IdentifierListParseException(errorMessage);
		}
		String lastValue = lastLine[0];
		boolean allRestEmpty = true;
		for (int i = 1; i < lastLine.length; i++) {
			if (!StringUtils.isBlank(lastLine[i])) {
				allRestEmpty = false;
				break;
			}
		}
		String parsedColumnLine = collectLine(Arrays.asList(lastLine), pd.separator);
		if (!allRestEmpty) {
			String errorMessage = String.format(END_FORMAT_DESCRIPTION + ", but last line other columns are not empty: '%s'", parsedColumnLine);
			throw new IdentifierListParseException(errorMessage);
		}
		int parsedCount = -1;
		try {
			parsedCount = Integer.parseInt(lastValue);
		} catch (Exception e) {
			String errorMessage = String.format(END_FORMAT_DESCRIPTION + ", but last line contains not a valid number: %s", lastValue);
			throw new IdentifierListParseException(errorMessage);
		}
		
		if (parsedList.size() != parsedCount) {
			String errorMessage = String.format(END_FORMAT_DESCRIPTION + ", but given total number is %d, although passed %d identifiers", parsedCount, parsedList.size());
			throw new IdentifierListParseException(errorMessage);
		}

		return parsedList;
	}

	public Identifier processLine(ParseData pd, String[] strings) throws IdentifierListParseException {
		if (strings.length <= pd.maxIndex) {
			String errorMessage = String.format("CSV file is not valid: line %d contains less than %d elements: '%s'", pd.lineIndex, pd.maxIndex + 1, collectLine(strings, pd.separator));
			throw new IdentifierListParseException(errorMessage);
		}
		Identifier i = new Identifier();
		i.setValue(strings[pd.indexNumber]);
		i.setName(strings[pd.indexName]);
		
		IdentifierValueType identifierType = IdentifierLoadService.defineIdentifierType(i);
		if (identifierType == null) {
			String errorMessage = String.format("CSV file is not valid: line %d contains identifier, whose type cannot be resolved: '%s'", pd.lineIndex, collectLine(strings, pd.separator));
			throw new IdentifierListParseException(errorMessage);
		}
		if (!IdentifierValidator.isValid(identifierType, i.getValue())) {
			String errorMessage = String.format("CSV file is not valid: line %d contains identifier, which is not valid: '%s'", pd.lineIndex, collectLine(strings, pd.separator));
			throw new IdentifierListParseException(errorMessage);
		}
		return i;
	}

	public Map<String, Integer> buildColumnIndexMap(String[] strings) {
		Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
		for (int i = 0; i < strings.length; i++) {
			String value = strings[i];
			columnIndexMap.put(value.trim().toLowerCase(), i);
		}
		return columnIndexMap;
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public Identifier next() {
		return this.iterator.next();
	}
}
