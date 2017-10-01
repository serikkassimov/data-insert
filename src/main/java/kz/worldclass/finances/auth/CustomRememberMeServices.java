package kz.worldclass.finances.auth;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomRememberMeServices implements RememberMeServices, CommonRememberServices {
    private static final boolean COOKIE_HTTP_ONLY = true;
    private static final boolean COOKIE_SECURE = false;
    private static final String REMEMBERED_COOKIE_NAME = "R_COOKIE";
    private static final int REMEMBERED_COOKIE_MAX_AGE = 60 * 60 * 24 * 31; // 31 days
    private static final String COMMON_COOKIE_NAME = "C_COOKIE";
    private static final int COMMON_COOKIE_MAX_AGE = 60 * 20; // 20 minutes
    
    public static final long REMEMBERED_TOKEN_LIFETIME = REMEMBERED_COOKIE_MAX_AGE * 1000L;
    public static final long COMMON_COOKIE_LIFETIME = COMMON_COOKIE_MAX_AGE * 1000L;
    
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PersistentTokenService persistentTokenService;
    
    /**
     * 
     * @param request
     * @return 
     * @throws NullPointerException request is null
     */
    private static String getRememberMeSeries(HttpServletRequest request) {
        Objects.requireNonNull(request, "request is null");
        
        String result = null;
        
        Cookie[] cookies = request.getCookies();
        if ((cookies != null) && (cookies.length > 0)) {
            for (Cookie cookie: cookies) {
                if (REMEMBERED_COOKIE_NAME.equals(cookie.getName())) result = cookie.getValue();
            }
        }
        
        if ((result != null) && result.isEmpty()) result = null;
        
        return result;
    }
    
    /**
     * 
     * @param request
     * @return 
     * @throws NullPointerException request is null
     */
    private static String getCommonSeries(HttpServletRequest request) {
        Objects.requireNonNull(request, "request is null");
        
        String result = null;
        
        Cookie[] cookies = request.getCookies();
        if ((cookies != null) && (cookies.length > 0)) {
            for (Cookie cookie: cookies) {
                if (COMMON_COOKIE_NAME.equals(cookie.getName())) result = cookie.getValue();
            }
        }
        
        if ((result != null) && result.isEmpty()) result = null;
        
        return result;
    }
    
    /**
     * 
     * @param request
     * @param series
     * @return 
     * @throws NullPointerException request is null
     */
    private static Cookie createRememberMeCookie(HttpServletRequest request, String series) {
        Objects.requireNonNull(request, "request is null");
        
        Cookie result = new Cookie(REMEMBERED_COOKIE_NAME, series);
        result.setHttpOnly(COOKIE_HTTP_ONLY);
        result.setMaxAge(REMEMBERED_COOKIE_MAX_AGE);
        result.setPath(request.getContextPath());
        result.setSecure(COOKIE_SECURE);
        return result;
    }
    
    /**
     * 
     * @param request
     * @param series
     * @return 
     * @throws NullPointerException request is null
     */
    private static Cookie createCommonCookie(HttpServletRequest request, String series) {
        Objects.requireNonNull(request, "request is null");
        
        Cookie result = new Cookie(COMMON_COOKIE_NAME, series);
        result.setHttpOnly(COOKIE_HTTP_ONLY);
        result.setMaxAge(COMMON_COOKIE_MAX_AGE);
        result.setPath(request.getContextPath());
        result.setSecure(COOKIE_SECURE);
        return result;
    }
    
    /**
     * 
     * @param request
     * @param response 
     * @throws NullPointerException request or response are null
     */
    private static void removeRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
        Objects.requireNonNull(request, "request is null");
        Objects.requireNonNull(response, "response is null");
        
        Cookie cookie = createRememberMeCookie(request, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    /**
     * 
     * @param request
     * @param response 
     * @throws NullPointerException request or response are null
     */
    private static void removeCommonCookie(HttpServletRequest request, HttpServletResponse response) {
        Objects.requireNonNull(request, "request is null");
        Objects.requireNonNull(response, "response is null");
        
        Cookie cookie = createCommonCookie(request, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    
    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        String series = getRememberMeSeries(request);
        
        if ((series == null) || (series.isEmpty())) ;
        else {
            PersistentRememberMeToken token = persistentTokenRepository.getTokenForSeries(series);
            if (token == null) {
                removeRememberMeCookie(request, response);
                return null;
            } else {
                String username = token.getUsername();
                if ((username == null) || (username.isEmpty())) {
                    removeRememberMeCookie(request, response);
                    return null;
                } else {
                    if ((token.getDate() == null) || (token.getDate().getTime() < System.currentTimeMillis() - REMEMBERED_COOKIE_MAX_AGE * 1000L)) {
                        removeRememberMeCookie(request, response);
                        return null;
                    } else {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (userDetails == null) {
                            removeRememberMeCookie(request, response);
                            return null;
                        } else {
                            persistentTokenRepository.updateToken(token.getSeries(), token.getTokenValue(), token.getDate());
                            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), authorities);
                            return authentication;
                        }
                    }
                }
            }
        }
        
        series = getCommonSeries(request);
        
        if ((series == null) || (series.isEmpty())) return null;
        else {
            PersistentRememberMeToken token = persistentTokenRepository.getTokenForSeries(series);
            if (token == null) {
                removeCommonCookie(request, response);
                return null;
            } else {
                String username = token.getUsername();
                if ((username == null) || (username.isEmpty())) {
                    removeCommonCookie(request, response);
                    return null;
                } else {
                    if ((token.getDate() == null) || (token.getDate().getTime() < System.currentTimeMillis() - COMMON_COOKIE_MAX_AGE * 1000L)) {
                        removeCommonCookie(request, response);
                        return null;
                    } else {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (userDetails == null) {
                            removeCommonCookie(request, response);
                            return null;
                        } else {
                            persistentTokenRepository.updateToken(token.getSeries(), token.getTokenValue(), token.getDate());
                            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), authorities);
                            return authentication;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        removeRememberMeCookie(request, response);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String series = getRememberMeSeries(request);
        if (series != null) {
            if (successfulAuthentication == null) {
                persistentTokenService.removeToken(series);
                removeRememberMeCookie(request, response);
            }
            else persistentTokenRepository.updateToken(series, "", new Date());
        } else {
            if (successfulAuthentication == null) {
                removeRememberMeCookie(request, response);
            } else {
                String username = successfulAuthentication.getName();
                if ((username == null) || username.isEmpty()) {
                    removeRememberMeCookie(request, response);
                } else {
                    PersistentRememberMeToken token = persistentTokenService.createNewRememberMeToken(username);
                    if (token == null) {
                        removeRememberMeCookie(request, response);
                    } else {
                        Cookie cookie = createRememberMeCookie(request, token.getSeries());
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }

    @Override
    public void updateTokens(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void commonLoginFail(HttpServletRequest request, HttpServletResponse response) {
        removeCommonCookie(request, response);
    }

    @Override
    public void commonLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String series = getCommonSeries(request);
        if (series != null) {
            if (successfulAuthentication == null) {
                persistentTokenService.removeToken(series);
                removeCommonCookie(request, response);
            }
            else persistentTokenRepository.updateToken(series, "", new Date());
        } else {
            if (successfulAuthentication == null) {
                removeCommonCookie(request, response);
            } else {
                String username = successfulAuthentication.getName();
                if ((username == null) || username.isEmpty()) {
                    removeCommonCookie(request, response);
                } else {
                    PersistentRememberMeToken token = persistentTokenService.createNewCommonToken(username);
                    if (token == null) {
                        removeCommonCookie(request, response);
                    } else {
                        Cookie cookie = createCommonCookie(request, token.getSeries());
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }
}