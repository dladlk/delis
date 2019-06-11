package dk.erst.delis.data.entities.document;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import dk.erst.delis.data.entities.AbstractCreateEntity;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * Each document has a bunch of related file data - initial SBD, payload inside it, initial metadata (for Domibus file system plugin) 
 * 
 * or receipt files (for Oxalis), also as intermediate formats and result outgoing format.
 * 
 * For support purposes, these files has sense to keep at least for some period of time.
 * 
 * A new table DocumentBytes is supposed to contain all different types of file data for each document.
 * 
 * As the structore of table is not yet defined, it is not marked as Entity yet.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class DocumentBytes extends AbstractCreateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DOCUMENT_ID", nullable = false)
	private Document document;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private DocumentBytesType type;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 30)
	private DocumentFormat format;

	@Column(nullable = false)
	private long size;
}
