package dk.erst.delis.task.document;

import dk.erst.delis.dao.ErrorDictionaryDaoRepository;
import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.dao.JournalDocumentErrorDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.entities.journal.JournalDocumentError;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
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
                	DocumentErrorCode errorType = document.getLastError();
                	if (errorType == null) {
                		errorType = DocumentErrorCode.OTHER;
                	}
                    for(ErrorRecord errorRecord : errorRecords) {
                        String code = cutString(errorRecord.getCode(), 50);
                        String flag = cutString(errorRecord.getFlag(), 20);
                        String location = cutString(errorRecord.getLocation(), 500);
                        String message = cutString(errorRecord.getMessage(), 1024);

                        ErrorDictionary errorDictionary = new ErrorDictionary();
                        errorDictionary.setErrorType(errorType);
                        errorDictionary.setCode(code);
                        errorDictionary.setFlag(flag);
                        errorDictionary.setLocation(location);
                        errorDictionary.setMessage(message);
                        
                        int hash = errorDictionary.calculateHash();
                        errorDictionary.setHash(hash);

                        ErrorDictionary existingError = null;
                        List<ErrorDictionary> existingErrorList = errorDictionaryDaoRepository.findAllByHash(hash);
                        /*
                         * Hash code can overlap for different values - check that contents actually equal
                         */
                        for (ErrorDictionary e : existingErrorList) {
							if (e.equals(errorDictionary)) {
								existingError = e;
								break;
							}
						}
                        
                        if (existingError == null) {
                            errorDictionaryDaoRepository.save(errorDictionary);
                        } else {
                        	errorDictionary = existingError;
                        }

                        JournalDocumentError journalDocumentError = new JournalDocumentError();
                        journalDocumentError.setErrorDictionary(errorDictionary);
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
            return cutString(message, 255);
        }
        return "";
    }
    
    private String cutString(String message, int maxLength) {
    	if (message != null) {
	        if (message.length() > maxLength) {
	            return message.substring(0, maxLength - 5)+"...";
	        }
    	}
        return message;
    }
}
