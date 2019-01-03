package dk.erst.delis.task.identifier.publish;

import dk.erst.delis.data.Identifier;
import dk.erst.delis.task.codelist.CodeListDict;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.dummy.SmpPublishDataDummyService;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifierPublishDataService {

	private CodeListDict codeListDict;

	private SmpPublishDataDummyService dummyService = new SmpPublishDataDummyService();

	@Autowired
	public IdentifierPublishDataService(CodeListDict codeListDict) {
		this.codeListDict = codeListDict;
	}

	public SmpPublishData buildPublishData(Identifier identifier) {
		SmpPublishData d = new SmpPublishData();

		String icdValue = codeListDict.getIdentifierTypeIcdValue(identifier.getType());
		if (icdValue == null) {
			throw new RuntimeException("Identifier type " + identifier.getType() + " is unknown in ICD code lists for identifier " + identifier);
		}

		d.setParticipantIdentifier(ParticipantIdentifier.of(icdValue + ":" + identifier.getValue()));
		d.setServiceList(dummyService.buildDummyServiceList());

		return d;
	}
}
