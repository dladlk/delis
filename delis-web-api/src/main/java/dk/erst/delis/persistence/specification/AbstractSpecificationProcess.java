package dk.erst.delis.persistence.specification;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.util.SpecificationUtil;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;

public class AbstractSpecificationProcess<E extends AbstractEntity> implements AbstractSpecification<E> {

    @Override
    public Specification<E> generateCriteriaPredicate(WebRequest request, Class<E> entityClass, Long orgId) {
        if (Objects.nonNull(orgId)) {
            return (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = SpecificationUtil.generateSpecificationPredicatesByOrganisation(orgId, entityClass, root, criteriaBuilder);
                predicates.addAll(SpecificationUtil.generateSpecificationPredicates(request, entityClass, root, criteriaBuilder));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        } else {
            return getDefaultSpecification(request, entityClass);
        }
    }

    @Override
    public Specification<E> generateCriteriaPredicate(WebRequest request, Long orgId) {
        return null;
    }

    private Specification<E> getDefaultSpecification(WebRequest request, Class<E> entityClass) {
        return (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = SpecificationUtil.generateSpecificationPredicates(request, entityClass, root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
