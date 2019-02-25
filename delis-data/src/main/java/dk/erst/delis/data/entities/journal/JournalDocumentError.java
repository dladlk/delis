package dk.erst.delis.data.entities.journal;

import dk.erst.delis.data.entities.AbstractCreateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class JournalDocumentError extends AbstractCreateEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private JournalDocument journalDocument;

	@ManyToOne(fetch = FetchType.LAZY)
	private ErrorDictionary errorDictionary;

}
