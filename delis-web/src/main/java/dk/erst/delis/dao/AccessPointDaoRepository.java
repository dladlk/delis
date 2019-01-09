package dk.erst.delis.dao;

import dk.erst.delis.data.AccessPoint;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccessPointDaoRepository extends PagingAndSortingRepository<AccessPoint, Long> {

    AccessPoint findByUrl(String url);
}
