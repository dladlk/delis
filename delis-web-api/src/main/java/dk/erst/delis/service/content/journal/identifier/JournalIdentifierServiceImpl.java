package dk.erst.delis.service.content.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.persistence.journal.identifier.JournalIdentifierRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 14.01.19
 */

@Service
public class JournalIdentifierServiceImpl implements JournalIdentifierService {

    private final JournalIdentifierRepository journalIdentifierRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalIdentifierServiceImpl(JournalIdentifierRepository journalIdentifierRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalIdentifierRepository = journalIdentifierRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalIdentifier> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalIdentifier.class, webRequest, journalIdentifierRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalIdentifier getOneById(long id) {
        return (JournalIdentifier) abstractGenerateDataService.getOneById(id, JournalIdentifier.class, journalIdentifierRepository);
    }
}
