package dk.erst.delis.web.identifier;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.identifier.Identifier;

@Repository
public interface IdentifierDataTableRepository extends DataTablesRepository<Identifier, Long> {
}
