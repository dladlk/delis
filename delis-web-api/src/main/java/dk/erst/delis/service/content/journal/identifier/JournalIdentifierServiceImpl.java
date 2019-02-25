package dk.erst.delis.service.content.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.persistence.repository.journal.identifier.JournalIdentifierRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.util.ClassLoaderUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author funtusthan, created by 14.01.19
 */

@Service
public class JournalIdentifierServiceImpl implements JournalIdentifierService {

    private final JournalIdentifierRepository journalIdentifierRepository;
    private final AbstractGenerateDataService<JournalIdentifierRepository, JournalIdentifier> abstractGenerateDataService;

    @Autowired
    public JournalIdentifierServiceImpl(
            JournalIdentifierRepository journalIdentifierRepository,
            AbstractGenerateDataService<JournalIdentifierRepository, JournalIdentifier> abstractGenerateDataService) {
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
        return abstractGenerateDataService.getOneById(id, JournalIdentifier.class, journalIdentifierRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public ListContainer<JournalIdentifier> getByIdentifier(WebRequest webRequest, long identifierId) {
        long collectionSize = journalIdentifierRepository.countByIdentifierId(identifierId);
        if (collectionSize == 0) {
            return new ListContainer<>(Collections.emptyList());
        }
        String[] strings = Objects.requireNonNull(webRequest.getParameter("sort")).split("_");
        for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(JournalIdentifier.class) ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Objects.equals(strings[1].toUpperCase(), field.getName().toUpperCase())) {
                    if (Objects.equals(strings[2], "Asc")) {
                        return new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by(field.getName()).ascending()));
                    } else {
                        return new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by(field.getName()).descending()));
                    }
                }
            }
        }
        return  new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by("id").ascending()));
    }
}
