package dk.erst.delis.persistence.rule.transformation;

import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 15.01.19
 */

@Getter
@Setter
public class RuleDocumentTransformationFilterModel extends AbstractFilterModel {

    private String active;
    private String rootPath;
    private String config;
    private DocumentFormatFamily documentFormatFamiliesFrom;
    private DocumentFormatFamily documentFormatFamiliesTo;
}
