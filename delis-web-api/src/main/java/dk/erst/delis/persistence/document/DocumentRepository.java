package dk.erst.delis.persistence.document;

import dk.erst.delis.data.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author Iehor Funtusov, created by 03.01.19
 */

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    @Query("select min (d.createTime) from Document d")
    Date findMinCreateTime();

    @Query("select max (d.createTime) from Document d")
    Date findMaxCreateTime();
}
