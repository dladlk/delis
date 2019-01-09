package dk.erst.delis.data;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Document {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = true)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "RECEIVER_IDENTIFIER_ID", nullable = true)
	private Identifier receiverIdentifier;

	@Column(name = "CREATE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createTime;

	@Column(nullable = false)
	private String ingoingRelativePath;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentStatus documentStatus;

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

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentFormat ingoingDocumentFormat;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentType documentType;

	@Column(nullable = true)
	private String documentId;

	@Column(nullable = true)
	private String documentDate;

	@Column(nullable = false)
	private String messageId;

	@Column(nullable = true)
	private String outgoingRelativePath;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentErrorCode lastError;
}
