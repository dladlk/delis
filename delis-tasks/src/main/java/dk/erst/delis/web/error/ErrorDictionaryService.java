package dk.erst.delis.web.error;

import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ErrorDictionaryService {
    private ErrorDictionaryDaoRepository errorDictionaryDaoRepository;
    private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;



    @Autowired
    public ErrorDictionaryService(ErrorDictionaryDaoRepository errorDictionaryDaoRepository,
                                  JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository
                                  ) {
        this.errorDictionaryDaoRepository = errorDictionaryDaoRepository;
        this.journalDocumentErrorDaoRepository = journalDocumentErrorDaoRepository;
    }

    public List<Document> documentList (Long errorDictionaryId) {
        List<Document> documentList = journalDocumentErrorDaoRepository.loadDocumentByErrorId(errorDictionaryId);
        return documentList;
    }

    public Iterable<ErrorDictionary> errorDictionaryList () {
        Iterable<ErrorDictionary> dictionaryList = errorDictionaryDaoRepository.findAll();
        for (ErrorDictionary errorDictionary : dictionaryList) {
            errorDictionary.setLocation(errorDictionary.getLocation());
        }
        return dictionaryList;
    }

    public ErrorDictionaryData getErrorDictionaryWithStats (long id) {
        ErrorDictionary errorDictionary = errorDictionaryDaoRepository.findById(id).get();
        ErrorDictionaryData errorData = new ErrorDictionaryData();
        BeanUtils.copyProperties(errorDictionary, errorData);

        errorData.setLocation(errorDictionary.getLocation());

        Integer documentCountByErrorId = journalDocumentErrorDaoRepository.getDocumentCountByErrorId(id);
        errorData.setCount(documentCountByErrorId);
        Date documentMinDate = journalDocumentErrorDaoRepository.getDocumentMinDate(id);
        errorData.setStartDate(documentMinDate);
        Date documentMaxDate = journalDocumentErrorDaoRepository.getDocumentMaxDate(id);
        errorData.setEndDate(documentMaxDate);

        return errorData;
    }
}
