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

//	@EmbeddedId
//	private JournalDocumentErrorDictionaryId mappingId;

	@ManyToOne(fetch = FetchType.LAZY)
//	@MapsId("journalDocumentId")
	private JournalDocument journalDocument;

	@ManyToOne(fetch = FetchType.LAZY)
//	@MapsId("errorDictionaryId")
	private ErrorDictionary errorDictionary;

	@Column
	private String location;

//	public JournalDocumentError(JournalDocument journalDocument, ErrorDictionary errorDictionary) {
//		this.journalDocument = journalDocument;
//		this.errorDictionary = errorDictionary;
//		this.mappingId = new JournalDocumentErrorDictionaryId(journalDocument.getId(), errorDictionary.getId());
//	}
}
