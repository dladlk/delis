package dk.erst.delis.data.entities.journal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@WebApiContent
@Table(indexes = {

		@Index(name="I_HASH", columnList="HASH")
})
public class ErrorDictionary extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@EqualsAndHashCode.Include
	private DocumentErrorCode errorType;
	
	@Column(nullable = false, length = 50)
	@EqualsAndHashCode.Include
	private String code;
	
	@Column(nullable = false, length = 1024)
	@EqualsAndHashCode.Include
	private String message;
	
	@Column(length = 20)
	@EqualsAndHashCode.Include
	private String flag;

	@Column(length = 500)
	@EqualsAndHashCode.Include
	private String location;
	
	@Column(name="HASH")
	private int hash;

	public int calculateHash() {
		return this.hashCode();
	}
	
	public static boolean isWarningFlag(String flag) {
		return flag != null && flag.equalsIgnoreCase("warning");
	}
}
