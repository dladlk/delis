package dk.erst.delis.web.error;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import lombok.Getter;
import lombok.Setter;

@Service
public class ErrorDictionaryService {

	private ErrorDictionaryDaoRepository errorDictionaryDaoRepository;
	private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;

	@Autowired
	public ErrorDictionaryService(ErrorDictionaryDaoRepository errorDictionaryDaoRepository, JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository) {
		this.errorDictionaryDaoRepository = errorDictionaryDaoRepository;
		this.journalDocumentErrorDaoRepository = journalDocumentErrorDaoRepository;
	}

	public List<Document> documentList(Long errorDictionaryId) {
		List<Document> documentList = journalDocumentErrorDaoRepository.loadDocumentByErrorId(errorDictionaryId);
		return documentList;
	}

	public ErrorDictionary getErrorDictionary(long id) {
		return errorDictionaryDaoRepository.findById(id).get();
	}

	public ErrorDictionaryStat getErrorDictionaryStat(long id) {
		ErrorDictionaryStat errorData = new ErrorDictionaryStat();

		Integer documentCountByErrorId = journalDocumentErrorDaoRepository.getDocumentCountByErrorId(id);
		errorData.setCount(documentCountByErrorId);
		Date documentMinDate = journalDocumentErrorDaoRepository.getDocumentMinDate(id);
		errorData.setStartDate(documentMinDate);
		Date documentMaxDate = journalDocumentErrorDaoRepository.getDocumentMaxDate(id);
		errorData.setEndDate(documentMaxDate);

		return errorData;
	}

	@Getter
	@Setter
	public static class ErrorDictionaryStat {
		private Integer count;
		private Date startDate;
		private Date endDate;

	}
}
