package dk.erst.delis.data.entities.journal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@WebApiContent
@Table(indexes = {

		@Index(name = "I_HASH", columnList = "HASH") })
public class ErrorDictionary extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DocumentErrorCode errorType;

	/*
	 * Enum returns different hashCode values - calculate hashCode basing on name of Enum
	 */
	@Transient
	public String getErrorTypeString() {
		return errorType != null ? errorType.name() : "";
	}

	@Column(nullable = false, length = 50)
	private String code;

	@Column(nullable = false, length = 1024)
	private String message;

	@Column(length = 20)
	private String flag;

	@Column(length = 500)
	private String location;

	@Column(name = "HASH")
	private int hash;

	public int calculateHash() {
		return this.hashCode();
	}

	public boolean isWarning() {
		return isWarningFlag(this.flag);
	}

	public static boolean isWarningFlag(String flag) {
		return flag != null && flag.equalsIgnoreCase("warning");
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ErrorDictionary))
			return false;
		ErrorDictionary other = (ErrorDictionary) o;
		if (!other.canEqual(this))
			return false;
		Object this$code = getCode(), other$code = other.getCode();
		if ((this$code == null) ? (other$code != null) : !this$code.equals(other$code))
			return false;
		Object this$message = getMessage(), other$message = other.getMessage();
		if ((this$message == null) ? (other$message != null) : !this$message.equals(other$message))
			return false;
		Object this$flag = getFlag(), other$flag = other.getFlag();
		if ((this$flag == null) ? (other$flag != null) : !this$flag.equals(other$flag))
			return false;
		Object this$location = getLocation(), other$location = other.getLocation();
		if ((this$location == null) ? (other$location != null) : !this$location.equals(other$location))
			return false;
		Object this$$getErrorTypeString = getErrorTypeString(), other$$getErrorTypeString = other.getErrorTypeString();
		return !((this$$getErrorTypeString == null) ? (other$$getErrorTypeString != null) : !this$$getErrorTypeString.equals(other$$getErrorTypeString));
	}

	protected boolean canEqual(Object other) {
		return other instanceof ErrorDictionary;
	}

	@Override
	public int hashCode() {
		int PRIME = 59;
		int result = 1;
		Object $code = getCode();
		result = result * PRIME + (($code == null) ? 43 : $code.hashCode());
		Object $message = getMessage();
		result = result * PRIME + (($message == null) ? 43 : $message.hashCode());
		Object $flag = getFlag();
		result = result * PRIME + (($flag == null) ? 43 : $flag.hashCode());
		Object $location = getLocation();
		result = result * PRIME + (($location == null) ? 43 : $location.hashCode());
		Object $$getErrorTypeString = getErrorTypeString();
		return result * PRIME + (($$getErrorTypeString == null) ? 43 : $$getErrorTypeString.hashCode());
	}

}
