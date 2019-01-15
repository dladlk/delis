package dk.erst.delis.service.auth;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author funtusthan, created by 12.01.19
 */

@Getter
class AuthTokenMap {

    private static Map<Long, AuthTokenData> authMap = Collections.synchronizedMap(new HashMap<>());

    static Map<Long, AuthTokenData> getAuthMap() {
        return authMap;
    }

    static void putAuthTokenData(Long id, AuthTokenData authTokenData) {
        authMap.put(id, authTokenData);
    }
}
