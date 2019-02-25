package dk.erst.delis.service.content.rule.transformation;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.persistence.repository.rule.transformation.RuleDocumentTransformationRepository;
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
public class RuleDocumentTransformationServiceImpl implements RuleDocumentTransformationService {

    private final RuleDocumentTransformationRepository ruleDocumentTransformationRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public RuleDocumentTransformationServiceImpl(RuleDocumentTransformationRepository ruleDocumentTransformationRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.ruleDocumentTransformationRepository = ruleDocumentTransformationRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<RuleDocumentTransformation> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(RuleDocumentTransformation.class, webRequest, ruleDocumentTransformationRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDocumentTransformation getOneById(long id) {
        return (RuleDocumentTransformation) abstractGenerateDataService.getOneById(id, RuleDocumentTransformation.class, ruleDocumentTransformationRepository);
    }
}
