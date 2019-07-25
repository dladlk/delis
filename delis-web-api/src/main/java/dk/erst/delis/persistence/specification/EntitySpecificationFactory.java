package dk.erst.delis.persistence.specification;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.persistence.AbstractSpecification;

import java.util.Objects;

public class EntitySpecificationFactory<E extends AbstractEntity> {

    public AbstractSpecification<? extends AbstractEntity> generateSpecification(EntitySpecification entitySpecification) {
        if (Objects.equals(entitySpecification, EntitySpecification.FLAG_ERRORS_DOCUMENT)) {
            return new DocumentErrorSpecificationProcess();
        } else return new AbstractSpecificationProcess<E>();
    }
}
