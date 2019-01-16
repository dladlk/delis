package dk.erst.delis.service.rule.validation;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.rule.validation.RuleDocumentValidationRepository;
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
    public PageContainer<RuleDocumentValidation> getAllAfterFilteringAndSorting(int page, int size, WebRequest request) {

        long collectionSize = ruleDocumentValidationRepository.count();
        if (collectionSize == 0) {
            return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, ruleDocumentValidationRepository);
        }

        String sort = WebRequestUtil.existSortParameter(request);
        if (StringUtils.isNotBlank(sort)) {
            return abstractGenerateDataService.sortProcess(RuleDocumentValidation.class, sort, request, page, size, collectionSize, ruleDocumentValidationRepository);
        } else {
            return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(RuleDocumentValidation.class, request, page, size, collectionSize, ruleDocumentValidationRepository);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RuleDocumentValidation getOneById(long id) {
        RuleDocumentValidation ruleDocumentValidation = ruleDocumentValidationRepository.findById(id).orElse(null);
        if (ruleDocumentValidation == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "ruleDocumentValidation not found by id")));
        }
        return ruleDocumentValidation;
    }
}
