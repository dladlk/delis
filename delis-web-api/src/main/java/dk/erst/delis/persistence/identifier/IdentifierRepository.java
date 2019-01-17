package dk.erst.delis.persistence.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

@Repository
public interface IdentifierRepository extends AbstractRepository<Identifier> { }
