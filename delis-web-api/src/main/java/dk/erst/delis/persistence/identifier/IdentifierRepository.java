package dk.erst.delis.persistence.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, Long> { }
