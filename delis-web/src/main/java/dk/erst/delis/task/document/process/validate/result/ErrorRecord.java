package dk.erst.delis.task.document.process.validate.result;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorRecord {
	
	private DocumentErrorCode errorType;
    private String code;
    private String message;
    private String flag;
    private String location;
    private String detailedLocation;

    public ErrorRecord(DocumentErrorCode errorType, String code, String message, String flag, String location) {
        this.errorType = errorType;
        this.code = code;
        this.message = message;
        this.flag = flag;
        this.detailedLocation = cleanupNamespaces(location);
        this.location = cleanupIndexes(this.detailedLocation);
    }

    private String cleanupNamespaces (String location) {
        if (location == null) return null;

        String s = location.replaceAll("\\[namespace(.*?)\\]", "").replaceAll("\\/\\*\\:", "/");
        return s;
    }

    private String cleanupIndexes (String location) {
        if (location == null) return null;

        String s = location.replaceAll("\\[([0-9]?)\\]", "");
        return s;
    }
}
