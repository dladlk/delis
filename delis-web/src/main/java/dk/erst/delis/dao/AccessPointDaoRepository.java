package dk.erst.delis.dao;


import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.enums.access.AccessPointType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AccessPointDaoRepository extends PagingAndSortingRepository<AccessPoint, Long> {

    List<AccessPoint> findByType(AccessPointType type);
}
