package dk.erst.delis.persistence;

import dk.erst.delis.data.entities.AbstractEntity;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface AbstractSpecification<E extends AbstractEntity> {

    Specification<E> generateCriteriaPredicate(WebRequest request, Class<E> entityClass);
}
