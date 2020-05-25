package dk.erst.delis.data.entities.rule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RuleDocumentValidation extends AbstractCreateUpdateEntity implements IRuleDocument {

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
	
	public DocumentErrorCode buildErrorCode() {
		if (this.getDocumentFormat() != null) {
			if (this.getValidationType().isXSD()) {
				switch (this.getDocumentFormat().getDocumentFormatFamily()){
				case BIS3:
					return DocumentErrorCode.BIS3_XSD;
				case OIOUBL:
					return DocumentErrorCode.OIOUBL_XSD;
				case CII:
					return DocumentErrorCode.CII_XSD;
				case BIS3_IR:
					return DocumentErrorCode.BIS3_XSD;
				default:
					return DocumentErrorCode.OTHER;	
				}
			} else {
				switch (this.getDocumentFormat().getDocumentFormatFamily()){
				case BIS3:
					return DocumentErrorCode.BIS3_SCH;
				case OIOUBL:
					return DocumentErrorCode.OIOUBL_SCH;
				case CII:
					return DocumentErrorCode.CII_SCH;
				case BIS3_IR:
					return DocumentErrorCode.BIS3_SCH;
				default:
					return DocumentErrorCode.OTHER;	
				}
			}
		}
		return DocumentErrorCode.OTHER;
	}

	public boolean isEqualData(IRuleDocument rd) {
		if (!(rd instanceof RuleDocumentValidation)) {
			return false;
		}
		RuleDocumentValidation r = (RuleDocumentValidation)rd;
		
		if (this.active != r.active)
			return false;
		if (this.documentFormat != r.documentFormat)
			return false;
		if (this.priority != r.priority)
			return false;
		if (this.validationType != r.validationType)
			return false;
		if (!this.rootPath.equals(r.rootPath))
			return false;
		return true;
	}
}
