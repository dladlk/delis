package dk.erst.delis.persistence.document;

import dk.erst.delis.data.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static dk.erst.delis.persistence.document.DocumentConstants.*;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

public class DocumentSpecification {

    public static Specification<Document> generateDocumentCriteriaPredicate(
            String organisation,
            String receiver,
            List<DocumentStatus> documentStatuses,
            List<DocumentErrorCode> lastErrors,
            String senderName,
            List<DocumentFormat> documentFormats,
            List<DocumentType> documentTypes,
            Date start, Date end) {

        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {

            String containsLikePattern = null;
            List<Predicate> predicates = new ArrayList<>();

            Predicate organisationPredicate = null;
            Predicate receiverPredicate = null;
            Predicate documentStatusPredicate = null;
            Predicate lastErrorPredicate = null;
            Predicate ingoingDocumentFormatPredicate = null;
            Predicate documentTypePredicate = null;
            Predicate senderNamePredicate = null;
            Predicate createTimePredicate = null;

            if (StringUtils.isNotBlank(organisation)) {
                containsLikePattern = getContainsLikePattern(organisation);
                organisationPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(ORGANIZATION_FIELD).get("name")), containsLikePattern);
                predicates.add(organisationPredicate);
            }

            if (StringUtils.isNotBlank(receiver)) {
                containsLikePattern = getContainsLikePattern(receiver);
                receiverPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(RECEIVER_IDENTIFIER_FIELD).get("uniqueValueType")), containsLikePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(RECEIVER_IDENTIFIER_FIELD).get("name")), containsLikePattern)
                );
                predicates.add(receiverPredicate);
            }

            if (CollectionUtils.isNotEmpty(documentStatuses)) {
                Path<DocumentStatus> documentStatusPath = root.get(DOCUMENT_STATUS_FIELD);
                documentStatusPredicate = documentStatusPath.in(documentStatuses);
                predicates.add(documentStatusPredicate);
            }

//            if (CollectionUtils.isNotEmpty(lastErrors)) {
//                Path<DocumentErrorCode> lastErrorPath = root.get(LAST_ERROR_FIELD);
//                lastErrorPredicate = lastErrorPath.in(lastErrors);
//                predicates.add(lastErrorPredicate);
//            }

            if (CollectionUtils.isNotEmpty(documentFormats)) {
                Path<DocumentFormat> ingoingDocumentFormatPath = root.get(INGOING_DOCUMENT_FORMAT_FIELD);
                ingoingDocumentFormatPredicate = ingoingDocumentFormatPath.in(documentFormats);
                predicates.add(ingoingDocumentFormatPredicate);
            }

            if (CollectionUtils.isNotEmpty(documentTypes)) {
                Path<DocumentType> documentTypePath = root.get(DOCUMENT_TYPE_FIELD);
                documentTypePredicate = documentTypePath.in(documentTypes);
                predicates.add(documentTypePredicate);
            }

            if (Objects.nonNull(start) && Objects.nonNull(end)) {
                createTimePredicate = criteriaBuilder.between(root.get(CREATE_TIME_FIELD), start, end);
                predicates.add(createTimePredicate);
            }

            if (StringUtils.isNotBlank(senderName)) {
                containsLikePattern = getContainsLikePattern(senderName);
                senderNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(SENDER_NAME_FIELD)), containsLikePattern);
                predicates.add(senderNamePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
