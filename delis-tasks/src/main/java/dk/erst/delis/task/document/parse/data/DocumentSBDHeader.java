package dk.erst.delis.task.document.parse.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import network.oxalis.vefa.peppol.common.model.ArgumentIdentifier;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.InstanceIdentifier;
import network.oxalis.vefa.peppol.common.model.InstanceType;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;

@Data
public class DocumentSBDHeader implements Serializable {

	private static final long serialVersionUID = -8289688752723141958L;

	private ParticipantIdentifier sender;
	private ParticipantIdentifier receiver;
	private ProcessIdentifier process;
	private DocumentTypeIdentifier documentType;
	private InstanceIdentifier identifier;
	private InstanceType instanceType;
	private Date creationTimestamp;
	private List<ArgumentIdentifier> arguments;

	public DocumentSBDHeader(Header header) {
		this.sender = header.getSender();
		this.receiver = header.getReceiver();
		this.process = header.getProcess();
		this.documentType = header.getDocumentType();
		this.identifier = header.getIdentifier();
		this.instanceType = header.getInstanceType();
		this.creationTimestamp = header.getCreationTimestamp();
		this.arguments = header.getArguments();
	}
}
