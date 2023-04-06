package dk.erst.delis.task.identifier.publish.data;

import java.util.List;

import lombok.Data;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;

@Data
public class SmpPublishData {

	private ParticipantIdentifier participantIdentifier;
	private List<SmpPublishServiceData> serviceList;

}
