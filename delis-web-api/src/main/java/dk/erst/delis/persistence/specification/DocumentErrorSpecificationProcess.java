package dk.erst.delis.persistence.specification;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.util.SpecificationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DocumentErrorSpecificationProcess implements AbstractSpecification<Document> {

    @Override
    public Specification<Document> generateCriteriaPredicate(WebRequest request, Long orgId) {
        if (Objects.nonNull(orgId)) {
            return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.addAll(SpecificationUtil.generateSpecificationPredicatesByOrganisation(orgId, Document.class, root, criteriaBuilder));
                predicates.addAll(getPredicatesByDocumentStatusError(new ArrayList<>(), root, criteriaBuilder));
                predicates.addAll(SpecificationUtil.generateSpecificationPredicates(request, Document.class, root, criteriaBuilder));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        } else {
            return getDefaultSpecification(request);
        }
    }

    @Override
    public Specification<Document> generateCriteriaPredicate(WebRequest request, Class<Document> entityClass, Long orgId) {
        return null;
    }

    private Specification<Document> getDefaultSpecification(WebRequest request) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = getPredicatesByDocumentStatusError(new ArrayList<>(), root, criteriaBuilder);
            predicates.addAll(SpecificationUtil.generateSpecificationPredicates(request, Document.class, root, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Predicate> getPredicatesByDocumentStatusError(List<Predicate> predicates, Root<Document> root, CriteriaBuilder criteriaBuilder) {
        String status = Arrays.stream(Document.class.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()) && Objects.equals(DocumentStatus.class, field.getType()))
                .map(Field::getName)
                .findFirst().orElse(null);
        if (StringUtils.isNotBlank(status)) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(status), DocumentStatus.VALIDATE_ERROR),
                    criteriaBuilder.equal(root.get(status), DocumentStatus.LOAD_ERROR),
                    criteriaBuilder.equal(root.get(status), DocumentStatus.UNKNOWN_RECEIVER)
            ));
        }
        return predicates;
    }
}
