package dk.erst.delis.task.document.process.validate.result;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorRecord {
	
	private DocumentErrorCode errorType;
    private String code;
    private String message;
    private String flag;
    private String location;
}
