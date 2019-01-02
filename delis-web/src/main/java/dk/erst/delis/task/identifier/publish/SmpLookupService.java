package dk.erst.delis.task.identifier.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

@Service
public class SmpLookupService {

	private ConfigBean configBean;

	@Autowired
	public SmpLookupService(ConfigBean configBean) {
		this.configBean = configBean;
	}
	
	public SmpPublishData lookup(ParticipantIdentifier identifier) {
		return null;
	}
}
