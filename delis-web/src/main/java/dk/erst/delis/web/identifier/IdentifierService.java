package dk.erst.delis.web.identifier;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.JournalIdentifierDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;

@Service
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
        return identifierDaoRepository.findById(id).orElse(null);
    }

    public Iterator<Identifier> findAll () {
        return identifierDaoRepository.findAll(Sort.by("id")).iterator();
    }

    public Iterator<Identifier> findByOrganisation (Organisation organisation) {
        return identifierDaoRepository.findByOrganisation(organisation).iterator();
    }

    public int updateStatuses (List<Long> idList, IdentifierStatus status, IdentifierPublishingStatus publishStatus) {
        AtomicInteger count = new AtomicInteger(0);
        if (idList.size() > 0) {
            Iterable<Identifier> identifierList = identifierDaoRepository.findAllById(idList);
            identifierList.forEach(identifier -> {identifier.setStatus(status); identifier.setPublishingStatus(publishStatus); noticeInJournal(status, publishStatus, identifier);count.getAndIncrement();});
            identifierDaoRepository.saveAll(identifierList);
        }
        return count.get();
    }

    public int updateStatus (Long id, IdentifierStatus status, IdentifierPublishingStatus publishStatus) {
        int count = 0;
        Identifier identifier = identifierDaoRepository.findById(id).get();
        if (identifier != null) {
            identifier.setStatus(status);
            identifier.setPublishingStatus(publishStatus);
            identifierDaoRepository.save(identifier);
            noticeInJournal(status, publishStatus, identifier);
            count++;
        }

        return count;
    }

    private void noticeInJournal(IdentifierStatus status, IdentifierPublishingStatus publishStatus, Identifier identifier) {
        JournalIdentifier updateRecord = new JournalIdentifier();
        updateRecord.setIdentifier(identifier);
        updateRecord.setOrganisation(identifier.getOrganisation());
        updateRecord.setMessage(MessageFormat.format("Updated by user manually. Set status={0} and publishStatus={1}.", status, publishStatus));
        journalIdentifierDaoRepository.save(updateRecord);
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
