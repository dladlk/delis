package dk.erst.delis.data;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
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
	private Date createTime;
	
	@Column(nullable = false)
	private String ingoingRelativePath;

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
	
	@Column(nullable = true)
	private DocumentFormat ingoingDocumentFormat;
	
	@Column(nullable = true)
	private String documentId;
	
	@Column(nullable = true)
	private String documentDate;

	@Column(nullable = false)
	private String messageId;
	
	@Column(nullable = true)
	private String outgoingRelativePath;
}
