package dk.erst.delis.persistence.rule.validation;

import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.rule.validation.RuleDocumentValidationConstants.*;

/**
 * @author funtusthan, created by 15.01.19
 */

public class RuleDocumentValidationSpecification implements AbstractSpecification<RuleDocumentValidation, RuleDocumentValidationFilterModel> {

    @Override
    public Specification<RuleDocumentValidation> generateCriteriaPredicate(RuleDocumentValidationFilterModel ruleDocumentValidationFilterModel) {

        return (Specification<RuleDocumentValidation>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            String containsLikePattern;

            if (Objects.nonNull(ruleDocumentValidationFilterModel.getStart()) && Objects.nonNull(ruleDocumentValidationFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(root.get(CREATE_TIME_FIELD), ruleDocumentValidationFilterModel.getStart(), ruleDocumentValidationFilterModel.getEnd()));
            }

            if (StringUtils.isNotBlank(ruleDocumentValidationFilterModel.getRootPath())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(ruleDocumentValidationFilterModel.getRootPath());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ROOT_PATH_FIELD)), containsLikePattern));
            }

            if (StringUtils.isNotBlank(ruleDocumentValidationFilterModel.getActive())) {
                predicates.add(criteriaBuilder.equal(root.get(ACTIVE_FIELD), Boolean.parseBoolean(ruleDocumentValidationFilterModel.getActive())));
            }

            if (StringUtils.isNotBlank(ruleDocumentValidationFilterModel.getConfig())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(ruleDocumentValidationFilterModel.getConfig());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(CONFIG_FIELD)), containsLikePattern));
            }

            if (Objects.nonNull(ruleDocumentValidationFilterModel.getPriority())) {
                predicates.add(criteriaBuilder.equal(root.get(PRIORITY_FIELD), ruleDocumentValidationFilterModel.getPriority()));
            }

            if (Objects.nonNull(ruleDocumentValidationFilterModel.getDocumentFormat())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_FORMAT_FIELD), ruleDocumentValidationFilterModel.getDocumentFormat()));
            }

            if (Objects.nonNull(ruleDocumentValidationFilterModel.getValidationType())) {
                predicates.add(criteriaBuilder.equal(root.get(VALIDATION_TYPE_FIELD), ruleDocumentValidationFilterModel.getValidationType()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
