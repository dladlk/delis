package dk.erst.delis.service.auth;

import dk.erst.delis.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author funtusthan, created by 12.01.19
 */

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final long ONE_HOURS = 60;

    private static Set<String> getIgnorePaths() {
        Set<String> ignorePaths = new HashSet<>();
        ignorePaths.add("/rest/security/signin");
        ignorePaths.add("/rest/table-info/enums");
        ignorePaths.add("/rest/table-info/organizations");
        return ignorePaths;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        String path = httpServletRequest.getServletPath();

        if (getIgnorePaths().stream().anyMatch(s -> s.startsWith(path))) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String token = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            log.warn("Authorization token is not present. path " + path);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is not present");
            return;
        }
        AuthTokenData data = AuthTokenMap.getAuthMap()
                .values()
                .stream()
                .filter(authTokenData -> Objects.equals(token, authTokenData.getToken()))
                .findFirst().orElse(null);
        if (Objects.isNull(data)) {
            log.warn("invalid token. path " + path + " token: " + token);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "invalid authentication token");
            return;
        }
        if (DateUtil.rangeHoursDate(data.getExpired()) > ONE_HOURS) {
            log.warn("Authorization token is expired. path " + path + " token: " + token);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization token is expired");
            return;
        }
        httpServletRequest.setAttribute("token", token);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
