package dk.erst.delis.task.document;

import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalDocumentService {
    private JournalDocumentDaoRepository journalDocumentDaoRepository;
    private JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository;
    private ErrorDictionaryDaoRepository errorDictionaryDaoRepository;

    @Autowired
    public JournalDocumentService(JournalDocumentDaoRepository journalDocumentDaoRepository,
                                  JournalDocumentErrorDaoRepository journalDocumentErrorDaoRepository,
                                  ErrorDictionaryDaoRepository errorDictionaryDaoRepository) {
        this.journalDocumentDaoRepository = journalDocumentDaoRepository;
        this.journalDocumentErrorDaoRepository = journalDocumentErrorDaoRepository;
        this.errorDictionaryDaoRepository = errorDictionaryDaoRepository;
    }

    public void saveDocumentStep(Document document, List<DocumentProcessStep> stepList) {
        if (stepList != null && !stepList.isEmpty()) {
            for (DocumentProcessStep step : stepList) {
                JournalDocument journalDocument = new JournalDocument();
                journalDocument.setSuccess(step.isSuccess());
                journalDocument.setType(step.getStepType());
                journalDocument.setCreateTime(step.getStartTime());
                journalDocument.setDurationMs(step.getDuration());
                journalDocument.setDocument(document);
                journalDocument.setOrganisation(document.getOrganisation());
                journalDocument.setMessage(buildJournalMessage(step));
                journalDocumentDaoRepository.save(journalDocument);

                List<ErrorRecord> errorRecords = step.getErrorRecords();
                if (errorRecords.size() > 0) {
                    for(ErrorRecord errorRecord : errorRecords) {
                        String code = errorRecord.getCode();
                        String flag = errorRecord.getFlag();
                        String location = errorRecord.getLocation();
                        String message = errorRecord.getMessage();

                        ErrorDictionary errorFromDictionary = errorDictionaryDaoRepository.findFirstByCode(code);
                        if (errorFromDictionary == null) {
                            errorFromDictionary = new ErrorDictionary();
                            errorFromDictionary.setCode(code);
                            errorFromDictionary.setFlag(flag);
                            errorFromDictionary.setMessage(cutString255(message));
                            errorDictionaryDaoRepository.save(errorFromDictionary);
                        }

                        JournalDocumentError journalDocumentError = new JournalDocumentError();
                        journalDocumentError.setLocation(cutString255(location));
                        journalDocumentError.setErrorDictionary(errorFromDictionary);
                        journalDocumentError.setJournalDocument(journalDocument);
                        journalDocumentErrorDaoRepository.save(journalDocumentError);
                    }
                }
            }
        }
    }

    private String buildJournalMessage(DocumentProcessStep step) {
        if (step != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(step.getDescription());
            if (step.getMessage() != null) {
                sb.append(": ");
                sb.append(step.getMessage());
            }
            String message = sb.toString();
            return cutString255(message);
        }
        return "";
    }

    private String cutString255(String message) {
        if (message.length() > 255) {
            return message.substring(0, 250)+"...";
        }
        return message;
    }
}
