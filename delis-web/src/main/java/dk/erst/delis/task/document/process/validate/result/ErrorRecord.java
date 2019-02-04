package dk.erst.delis.task.document.process.validate.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorRecord {
    private String code;
    private String message;
    private String flag;
    private String location;
}
