package dk.erst.delis.persistence.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.persistence.AbstractSpecificationUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.document.DocumentConstants.*;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

public class DocumentSpecification implements AbstractSpecification<Document, DocumentFilterModel> {

    @Override
    public Specification<Document> generateCriteriaPredicate(DocumentFilterModel documentFilterModel) {

        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern;
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(documentFilterModel.getOrganisation())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(documentFilterModel.getOrganisation());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ORGANIZATION_FIELD).get("name")), containsLikePattern));
            }

            if (StringUtils.isNotBlank(documentFilterModel.getReceiver())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(documentFilterModel.getReceiver());
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get(RECEIVER_IDENTIFIER_FIELD).get("uniqueValueType")), containsLikePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get(RECEIVER_IDENTIFIER_FIELD).get("name")), containsLikePattern)
                        ));
            }

            if (Objects.nonNull(documentFilterModel.getDocumentStatus())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_STATUS_FIELD), documentFilterModel.getDocumentStatus()));
            }

            if (Objects.nonNull(documentFilterModel.getLastError())) {
                predicates.add(criteriaBuilder.equal(root.get(LAST_ERROR_FIELD), documentFilterModel.getLastError()));
            }

            if (Objects.nonNull(documentFilterModel.getIngoingDocumentFormat())) {
                predicates.add(criteriaBuilder.equal(root.get(INGOING_DOCUMENT_FORMAT_FIELD), documentFilterModel.getIngoingDocumentFormat()));
            }

            if (Objects.nonNull(documentFilterModel.getDocumentType())) {
                predicates.add(criteriaBuilder.equal(root.get(DOCUMENT_TYPE_FIELD), documentFilterModel.getDocumentType()));
            }

            if (Objects.nonNull(documentFilterModel.getStart()) && Objects.nonNull(documentFilterModel.getEnd())) {
                predicates.add(criteriaBuilder.between(root.get(CREATE_TIME_FIELD), documentFilterModel.getStart(), documentFilterModel.getEnd()));
            }

            if (StringUtils.isNotBlank(documentFilterModel.getSenderName())) {
                containsLikePattern = AbstractSpecificationUtil.getContainsLikePattern(documentFilterModel.getSenderName());
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(SENDER_NAME_FIELD)), containsLikePattern));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
