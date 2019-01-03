package dk.erst.delis.persistence.document;

import dk.erst.delis.data.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


}
