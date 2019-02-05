package dk.erst.delis.data.entities.journal;

import dk.erst.delis.data.entities.AbstractEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@Setter
@EqualsAndHashCode (callSuper = false)
@NoArgsConstructor
@Entity
public class JournalDocumentError extends AbstractEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private JournalDocument journalDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	private ErrorDictionary errorDictionary;

	@Column
	private String location;
}
