package dk.erst.delis.persistence.repository.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.persistence.AbstractRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentifierRepository extends AbstractRepository<Identifier> {

    @Query("select distinct (o.name) from Identifier o where o.organisation.id = :organisationId")
    List<String> findDistinctNameByOrganisation(@Param("organisationId") Long organisationId);
}
