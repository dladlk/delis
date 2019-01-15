package dk.erst.delis.service.rule.transformation;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationFilterModel;
import dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationRepository;
import dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationSpecification;
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

import static dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationConstants.*;

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
            return new PageContainer<>();
        }

        List<String> filters = WebRequestUtil.existParameters(request);

        if (CollectionUtils.isNotEmpty(filters)) {

            RuleDocumentTransformationFilterModel filterModel = new RuleDocumentTransformationFilterModel();

            DateRequestModel dateRequestModel = WebRequestUtil.generateDateRequestModel(request);
            if (Objects.nonNull(dateRequestModel)) {
                filterModel.setStart(dateRequestModel.getStart());
                filterModel.setEnd(dateRequestModel.getEnd());
            } else {
                filterModel.setStart(ruleDocumentTransformationRepository.findMinCreateTime());
                filterModel.setEnd(ruleDocumentTransformationRepository.findMaxCreateTime());
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
                if (key.equals(DOCUMENT_FORMAT_FAMILY_FROM_FIELD)) {
                    filterModel.setDocumentFormatFamiliesFrom(DocumentFormatFamily.valueOf(request.getParameter(DOCUMENT_FORMAT_FAMILY_FROM_FIELD)));
                }
                if (key.equals(DOCUMENT_FORMAT_FAMILY_TO_FIELD)) {
                    filterModel.setDocumentFormatFamiliesTo(DocumentFormatFamily.valueOf(request.getParameter(DOCUMENT_FORMAT_FAMILY_TO_FIELD)));
                }
            }

            String sort = WebRequestUtil.existSortParameter(filters);

            if (StringUtils.isNotBlank(sort)) {
                return abstractGenerateDataService.sortProcess(RuleDocumentTransformation.class, sort, request, page, size, collectionSize, ruleDocumentTransformationRepository, filterModel, new RuleDocumentTransformationSpecification());
            } else {
                return abstractGenerateDataService.getDefaultDataPageContainerWithoutSorting(page, size, collectionSize, ruleDocumentTransformationRepository, filterModel, new RuleDocumentTransformationSpecification());
            }
        }

        return abstractGenerateDataService.getDefaultDataPageContainer(page, size, collectionSize, ruleDocumentTransformationRepository);
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
