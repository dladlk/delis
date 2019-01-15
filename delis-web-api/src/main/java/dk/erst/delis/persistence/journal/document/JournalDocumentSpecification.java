package dk.erst.delis.persistence.journal.document;

import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.journal.document.JournalDocumentConstants.*;

/**
 * @author funtusthan, created by 13.01.19
 */

public class JournalDocumentSpecification implements AbstractSpecification<JournalDocument, JournalDocumentFilterModel> {

    @Override
    public Specification<JournalDocument> generateCriteriaPredicate(JournalDocumentFilterModel journalDocumentFilterModel) {

        return (Specification<JournalDocument>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(journalDocumentFilterModel.getOrganisation())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalDocumentFilterModel.getOrganisation());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ORGANIZATION_FIELD).get("name")), containsLikePattern));
            }

            if (StringUtils.isNotBlank(journalDocumentFilterModel.getDocument())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalDocumentFilterModel.getDocument());
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(DOCUMENT_FIELD).get("senderName")), containsLikePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(DOCUMENT_FIELD).get("receiverName")), containsLikePattern)
                ));
            }

            if (Objects.nonNull(journalDocumentFilterModel.getType())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_PROCESS_STEP_TYPE_FIELD), journalDocumentFilterModel.getType()));
            }

            if (StringUtils.isNotBlank(journalDocumentFilterModel.getSuccess())) {
                predicates.add(criteriaBuilder.equal(root.get(SUCCESS_FIELD), Boolean.parseBoolean(journalDocumentFilterModel.getSuccess())));
            }

            if (Objects.nonNull(journalDocumentFilterModel.getDurationMs())) {
                predicates.add(criteriaBuilder.equal(root.get(DURATION_MS_FIELD), journalDocumentFilterModel.getDurationMs()));
            }

            if (StringUtils.isNotBlank(journalDocumentFilterModel.getMessage())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(journalDocumentFilterModel.getMessage());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(MESSAGE_FIELD)), containsLikePattern));
            }

            if (Objects.nonNull(journalDocumentFilterModel.getStart()) && Objects.nonNull(journalDocumentFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(
                        root.get(CREATE_TIME_FIELD), journalDocumentFilterModel.getStart(), journalDocumentFilterModel.getEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
