package dk.erst.delis.task.document;

import dk.erst.delis.dao.JournalDocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalDocumentService {
    private JournalDocumentDaoRepository journalDocumentDaoRepository;

    @Autowired
    public JournalDocumentService(JournalDocumentDaoRepository journalDocumentDaoRepository) {
        this.journalDocumentDaoRepository = journalDocumentDaoRepository;
    }

    public void saveDocumentStep(Document document, List<DocumentProcessStep> stepList) {
        if (stepList != null && !stepList.isEmpty()) {
            for (DocumentProcessStep step : stepList) {
                JournalDocument j = new JournalDocument();
                j.setSuccess(step.isSuccess());
                j.setType(step.getStepType());
                j.setCreateTime(step.getStartTime());
                j.setDurationMs(step.getDuration());
                j.setDocument(document);
                j.setOrganisation(document.getOrganisation());
                j.setMessage(buildJournalMessage(step));
                journalDocumentDaoRepository.save(j);
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
            if (message.length() > 255) {
                return message.substring(0, 250)+"...";
            }
            return message;
        }
        return "";
    }
}
