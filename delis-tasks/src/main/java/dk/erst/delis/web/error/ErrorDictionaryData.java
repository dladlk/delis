package dk.erst.delis.web.error;

import dk.erst.delis.data.enums.document.DocumentErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ErrorDictionaryData {
    private Long id;
    private DocumentErrorCode errorType;
    private String code;
    private String message;
    private String flag;
    private String location;
    private Integer count;
    private Date startDate;
    private Date endDate;
}
