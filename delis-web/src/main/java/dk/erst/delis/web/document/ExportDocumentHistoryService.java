package dk.erst.delis.web.document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.DocumentDaoRepository;

@Service
public class ExportDocumentHistoryService {

	private DocumentDaoRepository documentDao;

	@Autowired
	public ExportDocumentHistoryService(DocumentDaoRepository documentDao) {
		this.documentDao = documentDao;
	}

	public int generateExportFile(File file) throws IOException {
		int count = 0;
		try (FileWriter writer = new FileWriter(file)) {
			List<Object[]> list = documentDao.loadDocumentHistory();
			for (Object[] row : list) {
				for (Object cell : row) {
					if (cell != null) {
						writer.write(String.valueOf(cell));
					}
					writer.write(";");
				}
				writer.write("\r\n");
			}
			count = list.size();
		}
		return count;
	}
}
