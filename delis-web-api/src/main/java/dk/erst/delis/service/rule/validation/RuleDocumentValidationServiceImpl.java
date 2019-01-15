package dk.erst.delis.service.rule.validation;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.rule.validation.RuleDocumentValidationFilterModel;
import dk.erst.delis.persistence.rule.validation.RuleDocumentValidationRepository;
import dk.erst.delis.persistence.rule.validation.RuleDocumentValidationSpecification;
import dk.erst.delis.rest.data.request.param.DateRequestModel;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;
import dk.erst.delis.util.WebRequestUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.rule.validation.RuleDocumentValidationConstants.*;

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
            return new PageContainer<>();
        }

        List<String> filters = WebRequestUtil.existParameters(request);

        if (CollectionUtils.isNotEmpty(filters)) {

            RuleDocumentValidationFilterModel filterModel = new RuleDocumentValidationFilterModel();

            DateRequestModel dateRequestModel = WebRequestUtil.generateDateRequestModel(request);
            if (Objects.nonNull(dateRequestModel)) {
                filterModel.setStart(dateRequestModel.getStart());
                filterModel.setEnd(dateRequestModel.getEnd());
            } else {
                filterModel.setStart(ruleDocumentValidationRepository.findMinCreateTime());
                filterModel.setEnd(ruleDocumentValidationRepository.findMaxCreateTime());
            }

            for (String key : filters) {
                if (key.equals(ACTIVE_FIELD)) {
                    filterModel.setActive(request.getParameter(ACTIVE_FIELD));
                }
                if (key.equals(ROOT_PATH_FIELD)) {
                    filterModel.setRootPath(request.getParameter(ROOT_PATH_FIELD));
                }
                if (key.equals(CONFIG_FIELD)) {
                    filterModel.setConfig(request.getParameter(CONFIG_FIELD));
                }
                if (key.equals(PRIORITY_FIELD)) {
                    filterModel.setConfig(request.getParameter(PRIORITY_FIELD));
                }
                if (key.equals(DOCUMENT_FORMAT_FIELD)) {
                    filterModel.setDocumentFormat(DocumentFormat.valueOf(request.getParameter(DOCUMENT_FORMAT_FIELD)));
                }
                if (key.equals(VALIDATION_TYPE_FIELD)) {
                    filterModel.setValidationType(RuleDocumentValidationType.valueOf(request.getParameter(VALIDATION_TYPE_FIELD)));
                }
            }

            String sort = WebRequestUtil.existSortParameter(filters);

            if (StringUtils.isNotBlank(sort)) {
                return abstractGenerateDataService.sortProcess(RuleDocumentValidation.class, sort, request, page, size, collectionSize, ruleDocumentValidationRepository, filterModel, new RuleDocumentValidationSpecification());
            } else {
                return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, ruleDocumentValidationRepository, filterModel, new RuleDocumentValidationSpecification());
            }
        }

        return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, ruleDocumentValidationRepository);
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
