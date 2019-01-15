package dk.erst.delis.web.transformationrule;

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
    private DocumentFormatFamily documentFormatFamilyFrom;
    private DocumentFormatFamily documentFormatFamilyTo;
    private String rootPath;
    private String config;
}
