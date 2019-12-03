package dk.erst.delis.web.user;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import dk.erst.delis.data.entities.user.User;

@Repository
public interface UserDataTableRepository extends DataTablesRepository<User, Long> {

}
