package dk.erst.delis.persistence.rule.validation;

import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.persistence.AbstractFilterModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author funtusthan, created by 15.01.19
 */

@Getter
@Setter
public class RuleDocumentValidationFilterModel extends AbstractFilterModel {

    private String active;
    private String rootPath;
    private String config;
    private Integer priority;
    private DocumentFormat documentFormat;
    private RuleDocumentValidationType validationType;
}
