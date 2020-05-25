package dk.erst.delis.web.validationrule;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RuleDocumentValidationData {
    private Long id;

    private boolean active;
    
    @Min(1)
    @Max(100)
    private int priority;
    
    @NotNull
    private DocumentFormat documentFormat;
    @NotNull
    private RuleDocumentValidationType validationType;
    @NotEmpty
    @Size(max = 250)
    private String rootPath;
    
    private String config;
}
