package dk.erst.delis.persistence.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.journal.identifier.JournalIdentifierConstants.*;

/**
 * @author funtusthan, created by 14.01.19
 */

public class JournalIdentifierSpecification implements AbstractSpecification<JournalIdentifier, JournalIdentifierFilterModel> {

    @Override
    public Specification<JournalIdentifier> generateCriteriaPredicate(JournalIdentifierFilterModel journalIdentifierFilterModel) {

        return (Specification<JournalIdentifier>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(journalIdentifierFilterModel.getOrganisation())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalIdentifierFilterModel.getOrganisation());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ORGANIZATION_FIELD).get("name")), containsLikePattern));
            }

            if (StringUtils.isNotBlank(journalIdentifierFilterModel.getIdentifier())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalIdentifierFilterModel.getIdentifier());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(IDENTIFIER_FIELD).get("name")), containsLikePattern));
            }

            if (Objects.nonNull(journalIdentifierFilterModel.getDurationMs())) {
                predicates.add(criteriaBuilder.equal(root.get(DURATION_MS_FIELD), journalIdentifierFilterModel.getDurationMs()));
            }

            if (StringUtils.isNotBlank(journalIdentifierFilterModel.getMessage())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalIdentifierFilterModel.getMessage());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(MESSAGE_FIELD)), containsLikePattern));
            }

            if (Objects.nonNull(journalIdentifierFilterModel.getStart()) && Objects.nonNull(journalIdentifierFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(
                        root.get(CREATE_TIME_FIELD), journalIdentifierFilterModel.getStart(), journalIdentifierFilterModel.getEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
