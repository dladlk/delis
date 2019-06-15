package dk.erst.delis.data.entities.tokens;

import dk.erst.delis.data.entities.AbstractCreateEntity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author funtusthan, created by 21.03.19
 */

@Getter
@Setter
@Entity
@Table(name = "oauth_access_token")
public class OAuthAccessToken extends AbstractCreateEntity {

    @Column(name = "access_token", nullable = false, unique = true)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "client", nullable = false)
    private String client;

    @Column(name = "authentication_key", nullable = false)
    private String authenticationKey;

    @Lob
    @Column(name = "oauth2_access_token", nullable = false)
    private byte[] oAuth2AccessToken;

    @Lob
    @Column(name = "oauth2_authentication", nullable = false)
    private byte[] oAuth2Authentication;

    public OAuthAccessToken () {
        super();
    }
}
