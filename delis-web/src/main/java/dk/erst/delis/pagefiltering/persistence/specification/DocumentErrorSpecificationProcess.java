package dk.erst.delis.pagefiltering.persistence.specification;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.pagefiltering.persistence.AbstractSpecification;
import dk.erst.delis.pagefiltering.util.SpecificationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author funtusthan, created by 31.01.19
 */
public class DocumentErrorSpecificationProcess implements AbstractSpecification<Document> {

    @Override
    public Specification<Document> generateCriteriaPredicate(WebRequest request) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

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

            predicates.addAll(SpecificationUtil.generateSpecificationPredicates(request, Document.class, new ArrayList<>(), root, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Specification<Document> generateCriteriaPredicate(WebRequest request, Class<Document> entityClass) {
        return null;
    }
}
