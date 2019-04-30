package dk.erst.delis.pagefiltering.persistence.specification;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.pagefiltering.persistence.AbstractSpecification;

import java.util.Objects;

/**
 * @author funtusthan, created by 31.01.19
 */

public class EntitySpecificationFactory<E extends AbstractEntity> {

    public AbstractSpecification<? extends AbstractEntity> generateSpecification(EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.FLAG_ERRORS_DOCUMENT)) {
            return new DocumentErrorSpecificationProcess();
        } else return new AbstractSpecificationProcess<E>();
    }
}
