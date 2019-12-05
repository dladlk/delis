package dk.erst.delis.web.identifier;

import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;

@Repository
public interface IdentifierDataTableRepository extends DataTablesRepository<Identifier, Long> {
}
