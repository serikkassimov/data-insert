package kz.worldclass.finances.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface CommonRememberServices {
    void updateTokens(HttpServletRequest request, HttpServletResponse response);
    void commonLoginFail(HttpServletRequest request, HttpServletResponse response);
    void commonLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication);
}