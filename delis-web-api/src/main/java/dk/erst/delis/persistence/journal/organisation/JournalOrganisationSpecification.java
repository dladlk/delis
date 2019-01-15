package dk.erst.delis.persistence.journal.organisation;

import dk.erst.delis.data.entities.journal.JournalOrganisation;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.journal.organisation.JournalOrganisationConstants.*;

/**
 * @author funtusthan, created by 14.01.19
 */

public class JournalOrganisationSpecification implements AbstractSpecification<JournalOrganisation, JournalOrganisationFilterModel> {

    @Override
    public Specification<JournalOrganisation> generateCriteriaPredicate(JournalOrganisationFilterModel journalOrganisationFilterModel) {

        return (Specification<JournalOrganisation>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(journalOrganisationFilterModel.getOrganisation())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalOrganisationFilterModel.getOrganisation());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ORGANIZATION_FIELD).get("name")), containsLikePattern));
            }

            if (Objects.nonNull(journalOrganisationFilterModel.getDurationMs())) {
                predicates.add(criteriaBuilder.equal(root.get(DURATION_MS_FIELD), journalOrganisationFilterModel.getDurationMs()));
            }

            if (StringUtils.isNotBlank(journalOrganisationFilterModel.getMessage())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalOrganisationFilterModel.getMessage());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(MESSAGE_FIELD)), containsLikePattern));
            }

            if (Objects.nonNull(journalOrganisationFilterModel.getStart()) && Objects.nonNull(journalOrganisationFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(
                        root.get(CREATE_TIME_FIELD), journalOrganisationFilterModel.getStart(), journalOrganisationFilterModel.getEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
