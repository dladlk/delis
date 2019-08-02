package dk.erst.delis.config.security;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import dk.erst.delis.data.entities.tokens.OAuthAccessToken;
import dk.erst.delis.data.entities.tokens.OAuthRefreshToken;
import dk.erst.delis.persistence.repository.tokens.OAuthAccessTokenRepository;
import dk.erst.delis.persistence.repository.tokens.OAuthRefreshTokenRepository;

@Transactional
public class CustomTokenStore implements TokenStore {

    private static final Log LOG = LogFactory.getLog(CustomTokenStore.class);

    private OAuthAccessTokenRepository oAuthAccessTokenRepository;
    private OAuthRefreshTokenRepository oAuthRefreshTokenRepository;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    CustomTokenStore(OAuthAccessTokenRepository oAuthAccessTokenRepository, OAuthRefreshTokenRepository oAuthRefreshTokenRepository) {
        this.oAuthAccessTokenRepository = oAuthAccessTokenRepository;
        this.oAuthRefreshTokenRepository = oAuthRefreshTokenRepository;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return (token != null) ? this.readAuthentication(token.getValue()) : null;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;
        OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByAccessToken(token);

        if (Objects.isNull(oAuthAccessToken)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for token " + token);
            }
            return null;
        }
        try {
            authentication = this.deserializeAuthentication(oAuthAccessToken.getOAuth2Authentication());
        } catch (IllegalArgumentException var5) {
            LOG.warn("Failed to deserialize authentication for " + token, var5);
            this.removeAccessToken(token);
        }

        return authentication;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        long startTime = System.nanoTime();
        long currentTime = startTime;
        String refreshToken = token.getRefreshToken().getValue();
        if (Objects.nonNull(this.readAccessToken(token.getValue()))) {
            currentTime = logDuration(currentTime, "storeAccessToken. Step 1. readAccessToken took: ");
            this.removeAccessToken(token.getValue());
        }
        currentTime = logDuration(currentTime, "storeAccessToken. Step 2. removeAccessToken took: ");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByAccessToken(token.getValue());
        if (Objects.isNull(oAuthAccessToken)) {
            oAuthAccessToken = new OAuthAccessToken();
        }
        oAuthAccessToken.setAccessToken(token.getValue());
        oAuthAccessToken.setRefreshToken(refreshToken);
        String authenticationKey = this.authenticationKeyGenerator.extractKey(authentication);
        oAuthAccessToken.setAuthenticationKey(authenticationKey);
        currentTime = logDuration(currentTime, "storeAccessToken. Step 3. extractKey took: ");
        oAuthAccessToken.setOAuth2AccessToken(this.serializeAccessToken(token));
        oAuthAccessToken.setOAuth2Authentication(this.serializeAuthentication(authentication));
        oAuthAccessToken.setUserId(userDetails.getId());
        currentTime = logDuration(currentTime, "storeAccessToken. Step 4. findOne took: ");
        oAuthAccessToken.setClient(authentication.getOAuth2Request().getClientId());

        oAuthAccessTokenRepository.save(oAuthAccessToken);

        logDuration(currentTime, "storeAccessToken. Step 5. save took: ");
        logDuration(startTime, "storeAccessToken. Everything took: ");
    }

    @Override
    public OAuth2AccessToken readAccessToken(String token) {
        long startTime = System.nanoTime();
        long currentTime = startTime;
        OAuth2AccessToken auth2AccessToken = null;

        OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByAccessToken(token);
        currentTime = logDuration(currentTime, "readAccessToken. Step 1. findByAccessToken took: ");
        if (Objects.isNull(oAuthAccessToken)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for token " + token);
            }
            return null;
        }
        try {
            auth2AccessToken = this.deserializeAccessToken(oAuthAccessToken.getOAuth2AccessToken());
        } catch (IllegalArgumentException var5) {
            LOG.warn("Failed to deserialize authentication for " + token, var5);
            this.removeAccessToken(token);
        }
        logDuration(currentTime, "readAccessToken. Step 2. DeserializeAccessToken took: ");
        logDuration(startTime, "storeAccessToken. Everything took: ");
        return auth2AccessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        this.removeAccessToken(token.getValue());
    }

    private void removeAccessToken(String tokenValue) {
        long currentTime = System.nanoTime();
        try {
            OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByAccessToken(tokenValue);
            if (Objects.nonNull(oAuthAccessToken)) {
                OAuth2RefreshToken oAuth2RefreshToken = this.readRefreshToken(oAuthAccessToken.getRefreshToken());
                if (oAuth2RefreshToken != null) {
                    this.removeRefreshToken(oAuth2RefreshToken);
                }
                oAuthAccessTokenRepository.delete(oAuthAccessToken);
            }
        } catch (Exception e) {
            LOG.error("removeAccessToken failed: " + e.getMessage(), e);
        } finally {
            logDuration(currentTime, "removeAccessToken. deleteByAccessToken took: ");
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        long startTime = System.nanoTime();
        OAuthRefreshToken oAuthRefreshToken = new OAuthRefreshToken();
        oAuthRefreshToken.setTokenId(refreshToken.getValue());
        oAuthRefreshToken.setOAuth2RefreshToken(this.serializeRefreshToken(refreshToken));
        oAuthRefreshToken.setOAuth2Authentication(this.serializeAuthentication(authentication));
        oAuthRefreshTokenRepository.save(oAuthRefreshToken);
        logDuration(startTime, "storeRefreshToken. Everything took: ");
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        long startTime = System.nanoTime();
        long currentTime = startTime;
        OAuth2RefreshToken oAuth2RefreshToken = null;
        OAuthRefreshToken oAuthRefreshToken = oAuthRefreshTokenRepository.findByTokenId(token);
        currentTime = logDuration(currentTime, "readRefreshToken. Step 1. findOne took: ");
        if (Objects.isNull(oAuthRefreshToken)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find refresh token for token " + token);
            }
            return null;
        }
        try {
            oAuth2RefreshToken = this.deserializeRefreshToken(oAuthRefreshToken.getOAuth2RefreshToken());
        } catch (IllegalArgumentException var5) {
            LOG.warn("Failed to deserialize authentication for " + token, var5);
            this.removeAccessToken(token);
        }
        logDuration(currentTime, "readRefreshToken. Step 2. deserializeRefreshToken took: ");
        logDuration(startTime, "readRefreshToken. Everything took: ");
        return oAuth2RefreshToken;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        long startTime = System.nanoTime();
        OAuth2Authentication authentication = null;

        OAuthRefreshToken oAuthRefreshToken = oAuthRefreshTokenRepository.findByTokenId(refreshToken.getValue());
        long currentTime = logDuration(startTime,
                "readAuthenticationForRefreshToken. step 1. readAuthenticationForRefreshToken took: ");
        if (Objects.isNull(oAuthRefreshToken)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find refresh token for token " + refreshToken.getValue());
            }
            return null;
        }

        try {
            authentication = this.deserializeAuthentication(oAuthRefreshToken.getOAuth2Authentication());
        } catch (IllegalArgumentException var5) {
            LOG.warn("Failed to deserialize access token for " + refreshToken.getValue(), var5);
            this.removeRefreshToken(refreshToken);
        }
        logDuration(currentTime, "readAuthenticationForRefreshToken. step 2. removeRefreshToken took: ");
        logDuration(startTime, "readAuthenticationForRefreshToken. Everything took: ");
        return authentication;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        long startTime = System.nanoTime();
        OAuthRefreshToken oAuthRefreshToken = oAuthRefreshTokenRepository.findByTokenId(refreshToken.getValue());
        long currentTime = logDuration(startTime,
                "removeRefreshToken. step 1. findOne took: ");
        if (Objects.nonNull(oAuthRefreshToken)) {
            oAuthRefreshTokenRepository.delete(oAuthRefreshToken);
        }
        logDuration(currentTime, "removeRefreshToken. step 2. delete took: ");
        logDuration(startTime, "removeRefreshToken. Everything took: ");
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        long startTime = System.nanoTime();
        try {
            OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findByRefreshToken(oAuth2RefreshToken.getValue());
            if (Objects.nonNull(oAuthAccessToken)) {
                oAuthAccessTokenRepository.delete(oAuthAccessToken);
            }
        } catch (Exception e) {
            LOG.error("removeAccessTokenUsingRefreshToken. Exception:", e);
        }
        logDuration(startTime, "removeAccessTokenUsingRefreshToken. Everything took: ");
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        long startTime = System.nanoTime();
        long currentTime = startTime;
        OAuth2AccessToken accessToken;

        String key = this.authenticationKeyGenerator.extractKey(authentication);
        if (StringUtils.isBlank(key)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for authentication key " + key);
            }
            return null;
        }
        OAuthAccessToken oAuthAccessToken = oAuthAccessTokenRepository.findTopByAuthenticationKeyOrderByIdDesc(key);
        currentTime = logDuration(currentTime, "getAccessToken. Step 1. findByAuthenticationKey took: ");
        if (Objects.isNull(oAuthAccessToken)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Failed to find access token for authentication key " + key);
            }
            return null;
        }
        try {
            accessToken = this.deserializeAccessToken(oAuthAccessToken.getOAuth2AccessToken());
        } catch (Exception e) {
            LOG.warn("Failed to deserialize authentication for " + key, e);
            return null;
        }

        if (Objects.nonNull(accessToken) && !key.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(accessToken.getValue())))) {
            this.removeAccessToken(accessToken.getValue());
            this.storeAccessToken(accessToken, authentication);
        }
        logDuration(currentTime, "getAccessToken. Step 2. extractKey took: ");
        logDuration(startTime, "getAccessToken. Everything took: ");
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String client, String userName) {
        LOG.error("findTokensByClientIdAndUserName");
        return Collections.emptyList();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String client) {
        long startTime = System.nanoTime();
        List<OAuthAccessToken> accessTokenList = oAuthAccessTokenRepository.findByClient(client);
        List<OAuth2AccessToken> result = accessTokenList.stream()
                .map(t -> this.deserializeAccessToken(t.getOAuth2AccessToken()))
                .collect(toList());
        logDuration(startTime, "findTokensByClientId. Everything took: ");
        return result;
    }

    private byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    private byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    private byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return (OAuth2AccessToken) SerializationUtils.deserialize(token);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return (OAuth2RefreshToken) SerializationUtils.deserialize(token);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return (OAuth2Authentication) SerializationUtils.deserialize(authentication);
    }

    private long logDuration(long startTime, String logMessagePrefix) {
        long currentTime = System.nanoTime();
        long durationInMillis = Duration.ofNanos(currentTime - startTime).toMillis();
        LOG.debug(logMessagePrefix + durationInMillis + " ms.");
        return currentTime;
    }
}
