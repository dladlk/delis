package dk.erst.delis.data.entities.tokens;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "oauth_refresh_token")
public class OAuthRefreshToken {

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Lob
    @Column(name = "oauth2_refresh_token", nullable = false)
    private byte[] oAuth2RefreshToken;

    @Lob
    @Column(name = "oauth2_authentication", nullable = false)
    private byte[] oAuth2Authentication;
}
