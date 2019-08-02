package dk.erst.delis.service.content.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.service.content.AbstractService;

import org.springframework.web.context.request.WebRequest;

public interface JournalIdentifierDelisWebApiService extends AbstractService<JournalIdentifier> {

    ListContainer<JournalIdentifier> getByIdentifier(WebRequest webRequest, long identifierId);
}
