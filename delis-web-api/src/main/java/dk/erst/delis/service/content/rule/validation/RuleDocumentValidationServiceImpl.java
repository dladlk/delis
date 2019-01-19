package dk.erst.delis.service.content.rule.validation;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.persistence.rule.validation.RuleDocumentValidationRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 15.01.19
 */

@Service
public class RuleDocumentValidationServiceImpl implements RuleDocumentValidationService {

    private final RuleDocumentValidationRepository ruleDocumentValidationRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public RuleDocumentValidationServiceImpl(RuleDocumentValidationRepository ruleDocumentValidationRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.ruleDocumentValidationRepository = ruleDocumentValidationRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<RuleDocumentValidation> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(RuleDocumentValidation.class, webRequest, ruleDocumentValidationRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDocumentValidation getOneById(long id) {
        return (RuleDocumentValidation) abstractGenerateDataService.getOneById(id, RuleDocumentValidation.class, ruleDocumentValidationRepository);
    }
}
