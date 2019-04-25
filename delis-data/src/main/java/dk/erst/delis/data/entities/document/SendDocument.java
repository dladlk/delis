package dk.erst.delis.data.entities.document;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentType;
import dk.erst.delis.data.enums.document.SendDocumentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@WebApiContent
@EntityListeners(AuditingEntityListener.class)
public class SendDocument extends AbstractCreateUpdateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = true)
	private Organisation organisation;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SendDocumentStatus documentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private DocumentType documentType;

	@Column(nullable = true, length = 50)
	private String receiverIdRaw;

	@Column(nullable = true, length = 50)
	private String senderIdRaw;

	@Column(nullable = true, length = 50)
	private String documentId;

	@Column(nullable = true, length = 10)
	private String documentDate;

	@Column(nullable = true, length = 50)
	private String sentMessageId;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date deliveredTime;
    
}
