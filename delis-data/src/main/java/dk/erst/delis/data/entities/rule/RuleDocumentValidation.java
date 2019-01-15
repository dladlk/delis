package dk.erst.delis.data.entities.rule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RuleDocumentValidation extends AbstractEntity {

	@Column(nullable = false)
	private boolean active;
	
	@Column(nullable = false)
	private int priority;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentFormat documentFormat;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RuleDocumentValidationType validationType;
	
	@Column(nullable = false)
	private String rootPath;
	
	@Column(nullable = true)
	private String config;
}
