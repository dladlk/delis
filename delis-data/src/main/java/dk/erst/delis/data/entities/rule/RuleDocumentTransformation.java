package dk.erst.delis.data.entities.rule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RuleDocumentTransformation extends AbstractEntity {

	@Column(nullable = false)
	private boolean active;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentFormatFamily documentFormatFamilyFrom;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentFormatFamily documentFormatFamilyTo;
	
	@Column(nullable = false)
	private String rootPath;
	
	@Column(nullable = true)
	private String config;
}
