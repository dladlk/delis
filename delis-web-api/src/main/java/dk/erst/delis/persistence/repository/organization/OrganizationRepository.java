package dk.erst.delis.persistence.repository.organization;

import dk.erst.delis.data.entities.organisation.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Iehor Funtusov, created by 04.01.19
 */

@Repository
public interface OrganizationRepository extends JpaRepository<Organisation, Long> {

    @Query("select distinct (o.name) from Organisation o")
    List<String> findDistinctName();
}
