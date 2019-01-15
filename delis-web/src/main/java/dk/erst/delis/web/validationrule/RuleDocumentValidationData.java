package dk.erst.delis.web.validationrule;

import dk.erst.delis.data.DocumentFormat;
import dk.erst.delis.data.RuleDocumentValidationType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RuleDocumentValidationData {
    private Long id;

    private boolean active;
    private int priority;
    private DocumentFormat documentFormat;
    private RuleDocumentValidationType validationType;
    private String rootPath;
    private String config;
}
