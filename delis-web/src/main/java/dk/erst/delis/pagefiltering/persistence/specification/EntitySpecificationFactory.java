package dk.erst.delis.pagefiltering.persistence.specification;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.persistence.AbstractSpecification;

import java.util.Objects;

/**
 * @author funtusthan, created by 31.01.19
 */

public class EntitySpecificationFactory<E extends AbstractEntity> {

    @SuppressWarnings("unchecked")
	public AbstractSpecification<E> generateSpecification(EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.FLAG_ERRORS_DOCUMENT)) {
            return (AbstractSpecification<E>) new DocumentErrorSpecificationProcess();
        } else return new AbstractSpecificationProcess<E>();
    }
}
