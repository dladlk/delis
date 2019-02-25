package dk.erst.delis.persistence.specification;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.AbstractSpecification;
import dk.erst.delis.util.SpecificationUtil;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author funtusthan, created by 16.01.19
 */

public class AbstractSpecificationProcess<E extends AbstractEntity> implements AbstractSpecification<E> {

    @Override
    public Specification<E> generateCriteriaPredicate(WebRequest request, Class<E> entityClass) {
        return (Specification<E>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = SpecificationUtil.generateSpecificationPredicates(request, entityClass, new ArrayList<>(), root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Specification<E> generateCriteriaPredicate(WebRequest request) {
        return null;
    }
}
