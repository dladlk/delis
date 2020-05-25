package dk.erst.delis.data.entities.rule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RuleDocumentTransformation extends AbstractCreateUpdateEntity implements IRuleDocument {

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
	
	public boolean isEqualData(IRuleDocument rd) {
		if (!(rd instanceof RuleDocumentTransformation)) {
			return false;
		}
		RuleDocumentTransformation r = (RuleDocumentTransformation)rd;
		
		if (this.active != r.active)
			return false;
		if (this.documentFormatFamilyFrom != r.documentFormatFamilyFrom)
			return false;
		if (this.documentFormatFamilyTo != r.documentFormatFamilyTo)
			return false;
		if (!this.rootPath.equals(r.rootPath))
			return false;
		return true;
	}
}
