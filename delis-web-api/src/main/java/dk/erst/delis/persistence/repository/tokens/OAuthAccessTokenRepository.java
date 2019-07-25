package dk.erst.delis.persistence.repository.tokens;

import dk.erst.delis.data.entities.tokens.OAuthAccessToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, Long> {

    OAuthAccessToken findByAccessToken(String accessToken);
    OAuthAccessToken findByRefreshToken(String refreshToken);
    OAuthAccessToken findByUserId(Long userId);
    OAuthAccessToken findTopByAuthenticationKeyOrderByIdDesc(String authenticationKey);

    List<OAuthAccessToken> findByClient(String client);
}
