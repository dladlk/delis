package dk.erst.delis.dao;

import dk.erst.delis.data.entities.identifier.Identifier;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface IdentifierTableRepository extends DataTablesRepository<Identifier, Long> {

}
