package dk.erst.delis.data.entities.document;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentExportStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * Indicates, that document was delivered and its delivery should be tracked in some way.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(indexes = {

		@Index(name = "DE_STATUS", columnList = "STATUS", unique = false),

		@Index(name = "DE_ORGANISATION_ID", columnList = "ORGANISATION_ID", unique = false),

		@Index(name = "DE_DOCUMENT_ID", columnList = "DOCUMENT_ID", unique = false),

})
public class DocumentExport extends AbstractEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DOCUMENT_ID", nullable = false)
	private Document document;

	@Column(nullable = false, name = "STATUS")
	@Enumerated(EnumType.STRING)
	private DocumentExportStatus status;

	@Column(nullable = false)
	private Date exportDate;

	@Column(nullable = false, length = 255)
	private String exportFileName;

	@Column(nullable = false)
	private long exportSize;

	@Column(nullable = true)
	private Date lastCheckDate;

	@Column(nullable = true)
	private Date deliveredDate;

}
