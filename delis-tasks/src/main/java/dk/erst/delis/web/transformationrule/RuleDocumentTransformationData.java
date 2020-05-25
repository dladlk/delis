package dk.erst.delis.web.transformationrule;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RuleDocumentTransformationData {
    private Long id;

    private boolean active;
    
    @NotNull
    private DocumentFormatFamily documentFormatFamilyFrom;
    
    @NotNull
    private DocumentFormatFamily documentFormatFamilyTo;
    @NotEmpty
    @Size(max = 250)
    private String rootPath;
    
    private String config;
}
