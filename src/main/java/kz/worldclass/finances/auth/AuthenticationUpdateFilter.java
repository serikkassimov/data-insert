package kz.worldclass.finances.auth;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AuthenticationUpdateFilter extends GenericFilterBean {
    @Autowired
    private RememberMeServices rememberMeServices;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = rememberMeServices.autoLogin((HttpServletRequest) request, (HttpServletResponse) response);
        if (authentication != null) SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}