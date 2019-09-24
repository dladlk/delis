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
			
			String[] columnList = new String[] {"document.id", "document.receved_date", "document.issue_date", "document.status", "documnet.type", "document.format", "document.last_error", "receiver.country", "receiver.number", "receiver.name", "sender.country", "sender.number", "sender.name", "error.id", "error.code", "error.type", "error.flag", "error.message", "error.location", "document.error.location", "document.error.time", "document.error.type"};
			for (String column : columnList) {
				writer.write(column);
				writer.write(";");
			}
			writer.write("\r\n");
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
