package dk.erst.delis.web.user;

import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.web.datatables.dao.DataTablesRepository;

@Repository
public interface UserDataTableRepository extends DataTablesRepository<User, Long> {

}
