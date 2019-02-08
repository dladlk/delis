package dk.erst.delis.service.content.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.persistence.repository.journal.document.JournalDocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

@Service
public class JournalDocumentServiceImpl implements JournalDocumentService {

    private final JournalDocumentRepository journalDocumentRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalDocumentServiceImpl(JournalDocumentRepository journalDocumentRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalDocumentRepository = journalDocumentRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalDocument> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalDocument.class, webRequest, journalDocumentRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalDocument getOneById(long id) {
        return (JournalDocument) abstractGenerateDataService.getOneById(id, JournalDocument.class, journalDocumentRepository);
    }

	@Override
	public List<JournalDocument> getByDocument(long documentId) {;
		return journalDocumentRepository.findAllByDocumentIdOrderByIdAsc(documentId);
	}
}
