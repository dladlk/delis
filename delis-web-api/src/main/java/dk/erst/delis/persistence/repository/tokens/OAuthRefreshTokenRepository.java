package dk.erst.delis.persistence.repository.tokens;

import dk.erst.delis.data.entities.tokens.OAuthRefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken, String> {

    OAuthRefreshToken findByTokenId(String tokenId);
}
