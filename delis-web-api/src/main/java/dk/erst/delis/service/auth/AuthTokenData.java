package dk.erst.delis.service.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author funtusthan, created by 12.01.19
 */

@Getter
@Setter
class AuthTokenData {

    private String token;
    private Date expired;
}
