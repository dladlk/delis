package dk.erst.delis.persistence.rule.transformation;

import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.rule.transformation.RuleDocumentTransformationConstants.*;

/**
 * @author funtusthan, created by 15.01.19
 */

public class RuleDocumentTransformationSpecification implements AbstractSpecification<RuleDocumentTransformation, RuleDocumentTransformationFilterModel> {

    @Override
    public Specification<RuleDocumentTransformation> generateCriteriaPredicate(RuleDocumentTransformationFilterModel ruleDocumentTransformationFilterModel) {

        return (Specification<RuleDocumentTransformation>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            String containsLikePattern;

            if (Objects.nonNull(ruleDocumentTransformationFilterModel.getStart()) && Objects.nonNull(ruleDocumentTransformationFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(root.get(CREATE_TIME_FIELD), ruleDocumentTransformationFilterModel.getStart(), ruleDocumentTransformationFilterModel.getEnd()));
            }

            if (Objects.nonNull(ruleDocumentTransformationFilterModel.getDocumentFormatFamiliesFrom())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_FORMAT_FAMILY_FROM_FIELD), ruleDocumentTransformationFilterModel.getDocumentFormatFamiliesFrom()));
            }

            if (Objects.nonNull(ruleDocumentTransformationFilterModel.getDocumentFormatFamiliesTo())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_FORMAT_FAMILY_TO_FIELD), ruleDocumentTransformationFilterModel.getDocumentFormatFamiliesTo()));
            }

            if (StringUtils.isNotBlank(ruleDocumentTransformationFilterModel.getActive())) {
                predicates.add(criteriaBuilder.equal(root.get(ACTIVE_FIELD), Boolean.parseBoolean(ruleDocumentTransformationFilterModel.getActive())));
            }

            if (StringUtils.isNotBlank(ruleDocumentTransformationFilterModel.getRootPath())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(ruleDocumentTransformationFilterModel.getRootPath());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ROOT_PATH_FIELD)), containsLikePattern));
            }

            if (StringUtils.isNotBlank(ruleDocumentTransformationFilterModel.getConfig())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(ruleDocumentTransformationFilterModel.getConfig());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(CONFIG_FIELD)), containsLikePattern));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
