package dk.erst.delis.data.entities.document;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import dk.erst.delis.data.entities.AbstractCreateEntity;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class SendDocumentBytes extends AbstractCreateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DOCUMENT_ID", nullable = false)
	private SendDocument document;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private SendDocumentBytesType type;

	@Column(nullable = false)
	private long size;
}
