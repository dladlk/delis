package dk.erst.delis.dao;

import dk.erst.delis.data.AccessPoint;
import dk.erst.delis.data.AccessPointType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AccessPointDaoRepository extends PagingAndSortingRepository<AccessPoint, Long> {

    List<AccessPoint> findByType(AccessPointType type);
}
