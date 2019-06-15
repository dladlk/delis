package dk.erst.delis.service.security;

import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.util.SecurityUtil;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Organisation getOrganisation() {
        User user = userRepository.findById(SecurityUtil.getUserId()).get();
        Organisation organisation = user.getOrganisation();
        if (organisation == null) {
            throw new RestForbiddenException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "the request was valid, but the server is refusing action")));
        }
        return organisation;
    }
}
