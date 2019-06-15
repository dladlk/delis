package dk.erst.delis.data.entities.document;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.data.enums.document.DocumentType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@WebApiContent
@EntityListeners(AuditingEntityListener.class)
public class Document extends AbstractCreateUpdateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = true)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "RECEIVER_IDENTIFIER_ID", nullable = true)
	private Identifier receiverIdentifier;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentStatus documentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentErrorCode lastError;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentFormat ingoingDocumentFormat;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentType documentType;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String receiverIdRaw;

	@Column(nullable = true)
	private String receiverName;

	@Column(nullable = true)
	private String receiverCountry;

	@Column(nullable = true)
	private String senderIdRaw;

	@Column(nullable = true)
	private String senderName;

	@Column(nullable = true)
	private String senderCountry;

	@Column(nullable = true)
	private String documentId;

	@Column(nullable = true)
	private String documentDate;

	@Column(nullable = false)
	private String messageId;
}
