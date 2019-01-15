package dk.erst.delis.persistence;

import dk.erst.delis.data.entities.AbstractEntity;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author funtusthan, created by 13.01.19
 */

public interface AbstractSpecification<E extends AbstractEntity, F extends AbstractFilterModel> {

    Specification<E> generateCriteriaPredicate(F filterModel);
}
