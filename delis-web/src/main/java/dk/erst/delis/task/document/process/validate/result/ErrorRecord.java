package dk.erst.delis.task.document.process.validate.result;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.AllArgsConstructor;
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

    public ErrorRecord(DocumentErrorCode errorType, String code, String message, String flag, String location) {
        this.errorType = errorType;
        this.code = code;
        this.message = message;
        this.flag = flag;
        this.location = cleanupLocation(location);
    }

    private String cleanupLocation (String location) {
        if (location == null) return null;

        String s = location.replaceAll("\\[(.*?)\\]", "").replaceAll("\\/\\*\\:", "/");
        return s;
    }
}
