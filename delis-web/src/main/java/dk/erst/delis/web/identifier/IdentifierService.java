package dk.erst.delis.web.identifier;

import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class IdentifierService {
    private IdentifierDaoRepository identifierDaoRepository;
    private JournalIdentifierDaoRepository journalIdentifierDaoRepository;

    @Autowired
    public IdentifierService(IdentifierDaoRepository identifierDaoRepository,
                             JournalIdentifierDaoRepository journalIdentifierDaoRepository) {
        this.identifierDaoRepository = identifierDaoRepository;
        this.journalIdentifierDaoRepository = journalIdentifierDaoRepository;
    }

    public Identifier findOne (Long id) {
        return identifierDaoRepository.findById(id).get();
    }

    public Iterator<Identifier> findAll () {
        return identifierDaoRepository.findAll(Sort.by("id")).iterator();
    }

    public Iterator<Identifier> findByOrganisation (Organisation organisation) {
        return identifierDaoRepository.findByOrganisation(organisation).iterator();
    }

    public int updateStatuses (List<Long> idList, IdentifierStatus status, IdentifierPublishingStatus publishStatus) {
        int count = 0;
        if (idList.size() > 0) {
            List<Identifier> identifierList = identifierDaoRepository.findAllById(idList);
            identifierList.forEach(identifier -> {identifier.setStatus(status); identifier.setPublishingStatus(publishStatus);});
            count = identifierList.size();
            identifierDaoRepository.saveAll(identifierList);
        }
        return count;
    }

    public int updateStatus (Long id, IdentifierStatus status, IdentifierPublishingStatus publishStatus) {
        int count = 0;
        Identifier identifier = identifierDaoRepository.findById(id).get();
        if (identifier != null) {
            identifier.setStatus(status);
            identifier.setPublishingStatus(publishStatus);
            identifierDaoRepository.save(identifier);
            count++;
        }

        return count;
    }

    List<JournalIdentifier> getJournalRecords (Identifier identifier) {
        return journalIdentifierDaoRepository.findTop5ByIdentifierOrderByIdDesc(identifier);
    }

    public int markAsDeleted (Long id) {
        int count = 0;

        Identifier identifier = identifierDaoRepository.findById(id).get();
        if (identifier != null) {
            identifier.setStatus(IdentifierStatus.DELETED);
            identifierDaoRepository.save(identifier);
            count++;
        }

        return count;
    }
}
