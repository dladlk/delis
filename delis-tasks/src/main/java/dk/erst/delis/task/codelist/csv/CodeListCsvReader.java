package dk.erst.delis.task.codelist.csv;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import dk.erst.delis.task.codelist.CodeList;

public class CodeListCsvReader<T> {

	public List<T> readCodeList(CodeList codeList, Path path, Class<? extends T> dataClass) {
		Charset charset = StandardCharsets.UTF_8;

		HeaderColumnNameTranslateMappingStrategy<T> strategy = buildMappingStrategy(codeList, dataClass);

		try (FileInputStream inputStream = new FileInputStream(path.toFile())) {

			CsvToBeanBuilder<T> beanBuilder = new CsvToBeanBuilder<>(new InputStreamReader(inputStream, charset));
			beanBuilder.withMappingStrategy(strategy);
			beanBuilder.withSeparator(';');

			CsvToBean<T> csv = beanBuilder.build();
			return csv.parse();
		} catch (Exception e) {
			throw new RuntimeException("Failed to read codeList "+codeList +" by path "+path, e);
		}
	}

	private HeaderColumnNameTranslateMappingStrategy<T> buildMappingStrategy(CodeList codeList, Class<? extends T> dataClass) {
		HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
		Map<String, String> columnMapping = new HashMap<>();

		String[][] columnMappingConfig = CodeListCsvMapping.getColumnMapping(codeList);
		for (int i = 0; i < columnMappingConfig.length; i++) {
			columnMapping.put(columnMappingConfig[i][1], columnMappingConfig[i][0]);
		}
		strategy.setType(dataClass);
		strategy.setColumnMapping(columnMapping);
		return strategy;
	}
	
}
