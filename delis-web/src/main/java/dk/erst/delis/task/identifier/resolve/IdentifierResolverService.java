package dk.erst.delis.task.identifier.resolve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.dao.IdentifierRepository;
import dk.erst.delis.data.Identifier;
import dk.erst.delis.task.codelist.CodeListDict;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IdentifierResolverService {

	private CodeListDict codeListDict;
	private IdentifierRepository identifierRepository;

	@Autowired
	public IdentifierResolverService(IdentifierRepository identifierRepository, CodeListDict codeListDict) {
		this.identifierRepository = identifierRepository;
		this.codeListDict = codeListDict;
	}

	public Identifier resolve(String schemeId, String id) {
		log.info("Resolve schemeId = " + schemeId + ", id = " + id);
		String type = codeListDict.getIdentifierTypeSchemeID(schemeId);
		log.info("Converted schemeId " + schemeId + " to type " + type);

		Identifier identifier = identifierRepository.findByValueAndType(id, type);
		log.info("Resolved by type " + type + " and value " + id + " identifier " + identifier);

		return identifier;
	}
}
