package dk.erst.delis.service.content.journal.organisation;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.persistence.journal.organisation.JournalOrganisationRepository;
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
public class JournalOrganisationServiceImpl implements JournalOrganisationService {

    private final JournalOrganisationRepository journalOrganisationRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public JournalOrganisationServiceImpl(JournalOrganisationRepository journalOrganisationRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.journalOrganisationRepository = journalOrganisationRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<JournalOrganisation> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalOrganisation.class, webRequest, journalOrganisationRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalOrganisation getOneById(long id) {
        return (JournalOrganisation) abstractGenerateDataService.getOneById(id, JournalOrganisation.class, journalOrganisationRepository);
    }
}
