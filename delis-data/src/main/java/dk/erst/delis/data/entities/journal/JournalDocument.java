package dk.erst.delis.data.entities.journal;

import javax.persistence.*;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JournalDocument extends AbstractEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DOCUMENT_ID", nullable = false)
	private Document document;
	
	@Column(nullable = false, updatable = false)
	private boolean success;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private DocumentProcessStepType type;
	
	@Column(nullable = false)
	private String message;
	
	@Column(nullable = true)
	private Long durationMs;
}
