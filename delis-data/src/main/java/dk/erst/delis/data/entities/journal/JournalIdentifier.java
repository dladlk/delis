package dk.erst.delis.data.entities.journal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.identifier.Identifier;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@WebApiContent
@EntityListeners(AuditingEntityListener.class)
public class JournalIdentifier extends AbstractEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "IDENTIFIER_ID", nullable = false)
	private Identifier identifier;
	
	@Column(nullable = false)
	private String message;
	
	@Column(nullable = true)
	private Long durationMs;
}
