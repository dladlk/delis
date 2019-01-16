package dk.erst.delis.service.rule.transformation;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

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
    public PageContainer<RuleDocumentTransformation> getAllAfterFilteringAndSorting(int page, int size, WebRequest request) {

        long collectionSize = ruleDocumentTransformationRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, ruleDocumentTransformationRepository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(RuleDocumentTransformation.class, sort, request, page, size, collectionSize, ruleDocumentTransformationRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(RuleDocumentTransformation.class, request, page, size, collectionSize, ruleDocumentTransformationRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDocumentTransformation getOneById(long id) {
        RuleDocumentTransformation ruleDocumentTransformation = ruleDocumentTransformationRepository.findById(id).orElse(null);
        if (ruleDocumentTransformation == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "ruleDocumentTransformation not found by id")));
        }
        return ruleDocumentTransformation;
    }
}
