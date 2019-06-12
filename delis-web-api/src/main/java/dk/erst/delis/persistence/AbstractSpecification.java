package dk.erst.delis.persistence;

import dk.erst.delis.data.entities.AbstractEntity;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

public interface AbstractSpecification<E extends AbstractEntity> {

    Specification<E> generateCriteriaPredicate(WebRequest request, Class<E> entityClass, Long orgId);
    Specification<E> generateCriteriaPredicate(WebRequest request, Long orgId);
}
