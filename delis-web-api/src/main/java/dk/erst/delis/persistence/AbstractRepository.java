package dk.erst.delis.persistence;

import dk.erst.delis.data.entities.AbstractEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;

/**
 * @author funtusthan, created by 13.01.19
 */

@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    @Query("select min (ae.createTime) from #{#entityName} ae")
    Date findMinCreateTime();

    @Query("select max (ae.createTime) from #{#entityName} ae")
    Date findMaxCreateTime();
}
