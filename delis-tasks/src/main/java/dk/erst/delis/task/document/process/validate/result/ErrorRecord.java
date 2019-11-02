package dk.erst.delis.task.document.process.validate.result;

import dk.erst.delis.data.entities.journal.ErrorDictionary;
import dk.erst.delis.data.entities.journal.IErrorInfo;
import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorRecord implements IErrorInfo {

	private DocumentErrorCode errorType;
	private String code;
	private String message;
	private String flag;
	private String location;
	private String detailedLocation;
	private Long errorDictionaryId;

	public ErrorRecord(DocumentErrorCode errorType, String code, String message, String flag, String location) {
		this.errorType = errorType;
		this.code = code;
		this.message = message;
		this.flag = flag;
		this.detailedLocation = cleanupNamespaces(location);
		this.location = cleanupIndexes(this.detailedLocation);
	}
	
	public ErrorRecord(DocumentErrorCode errorType, String code, String message, String flag, String location, String detailedLocation) {
		this.errorType = errorType;
		this.code = code;
		this.message = message;
		this.flag = flag;
		this.location = location;
		this.detailedLocation = detailedLocation;
	}
	
	public ErrorRecord(ErrorDictionary dict, String detailedLocation) {
		this.errorType = dict.getErrorType();
		this.code = dict.getCode();
		this.message = dict.getMessage();
		this.flag = dict.getFlag();
		this.location = dict.getLocation();
		this.detailedLocation = detailedLocation;
		this.errorDictionaryId = dict.getId();
	}

	public static String cleanupNamespaces(String location) {
		if (location == null)
			return null;

		return location.replaceAll("\\[namespace(.*?)\\]", "").replaceAll("\\/\\*\\:", "/");
	}

	public static String cleanupIndexes(String location) {
		if (location == null)
			return null;

		return location.replaceAll("\\[\\d+\\]", "");
	}

	public boolean isWarning() {
		return ErrorDictionary.isWarningFlag(flag);
	}
}
