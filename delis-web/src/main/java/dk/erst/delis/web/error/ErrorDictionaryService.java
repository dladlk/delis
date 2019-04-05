package dk.erst.delis.web.error;

import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
        ErrorDictionaryData errorDate = new ErrorDictionaryData();
        BeanUtils.copyProperties(errorDictionary, errorDate);

        errorDate.setLocation(errorDictionary.getLocation());

        Integer documentCountByErrorId = journalDocumentErrorDaoRepository.getDocumentCountByErrorId(id);
        errorDate.setCount(documentCountByErrorId);
        Date documentMinDate = journalDocumentErrorDaoRepository.getDocumentMinDate(id);
        errorDate.setStartDate(documentMinDate);
        Date documentMaxDate = journalDocumentErrorDaoRepository.getDocumentMaxDate(id);
        errorDate.setEndDate(documentMaxDate);

        return errorDate;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public class ErrorDictionaryData {
        private Long id;
        private DocumentErrorCode errorType;
        private String code;
        private String message;
        private String flag;
        private String location;
        private Integer count;
        private Date startDate;
        private Date endDate;
    }

}
