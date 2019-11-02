package dk.erst.delis.service.security;

import dk.erst.delis.data.entities.tokens.OAuthAccessToken;
import dk.erst.delis.data.entities.tokens.OAuthRefreshToken;
import dk.erst.delis.persistence.repository.tokens.OAuthAccessTokenRepository;
import dk.erst.delis.persistence.repository.tokens.OAuthRefreshTokenRepository;
import dk.erst.delis.util.SecurityUtil;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TokenService {

    @Value("${security.client.id}")
    private String id;

    private final TokenStore tokenStore;
    private final ConsumerTokenServices consumerTokenServices;
    private final OAuthAccessTokenRepository oAuthAccessTokenRepository;
    private final OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    @Autowired
    public TokenService(
            TokenStore tokenStore,
            ConsumerTokenServices consumerTokenServices,
            OAuthAccessTokenRepository oAuthAccessTokenRepository,
            OAuthRefreshTokenRepository oAuthRefreshTokenRepository) {
        this.tokenStore = tokenStore;
        this.consumerTokenServices = consumerTokenServices;
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.oAuthRefreshTokenRepository = oAuthRefreshTokenRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void removeAccessToken(OAuth2AccessToken token) {
        OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByAccessToken(token.getValue());
        if (oAuthAccessToken != null) {
            OAuthRefreshToken oAuthRefreshToken = oAuthRefreshTokenRepository.findByTokenId(oAuthAccessToken.getRefreshToken());
            if (oAuthRefreshToken != null) {
                oAuthRefreshTokenRepository.delete(oAuthRefreshToken);
            }
        	oAuthAccessTokenRepository.delete(oAuthAccessToken);
        }
        clearToken(token);
    }

    public void removeAccessToken() {
        clear(SecurityUtil.getUsername());
    }

    public void removeAccessToken(String username) {
        clear(username);
    }

    private void clear(String username){
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(id, username);
        if (CollectionUtils.isNotEmpty(tokens)){
            tokens.forEach(this::clearToken);
        }
    }

    private void clearToken(OAuth2AccessToken token){
        consumerTokenServices.revokeToken(token.getValue());
        tokenStore.removeRefreshToken(token.getRefreshToken());
        tokenStore.removeAccessToken(token);
    }
}
