package kz.worldclass.finances.controlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import kz.worldclass.finances.auth.CommonRememberServices;
import kz.worldclass.finances.data.dto.auth.PublicAuthenticationDto;
import kz.worldclass.finances.data.dto.Dtos;
import kz.worldclass.finances.data.dto.auth.LoginResultEnumDto;
import kz.worldclass.finances.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Autowired
    private RememberMeServices rememberMeServices;
    @Autowired
    private CommonRememberServices commonRememberServices;
    
    @RequestMapping(value = "/info", produces = "application/json; charset=utf-8")
    public PublicAuthenticationDto authInfo() {
        PublicAuthenticationDto result = Dtos.complete(SecurityContextHolder.getContext().getAuthentication());
        return result;
    }
    
    @RequestMapping(value = "/login", produces = "application/json; charset=utf-8")
    public LoginResultEnumDto login(
            @RequestParam(value = "login") String login,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "remember-me", required = false) String rememberMe
    ) {
        boolean remember = (rememberMe != null) && (!rememberMe.isEmpty());
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        try {
            authentication = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (remember) rememberMeServices.loginSuccess(request, response, authentication);
            else commonRememberServices.commonLoginSuccess(request, response, authentication);
        } catch (AuthenticationException exception) {
            if (LOGGER.isErrorEnabled()) LOGGER.error("authentication failed", exception);
            if (remember) rememberMeServices.loginFail(request, response);
            else commonRememberServices.commonLoginFail(request, response);
            
            if (exception instanceof BadCredentialsException) return LoginResultEnumDto.BAD_CREDENTIALS;
            else return LoginResultEnumDto.EXCEPTION;
        }
        return LoginResultEnumDto.SUCCESS;
    }
    
    @RequestMapping(value = "/logout", produces = "application/json; charset=utf-8")
    public String logout() {
        rememberMeServices.loginSuccess(request, response, null);
        commonRememberServices.commonLoginSuccess(request, response, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "SUCCESS";
    }
    
    @RequestMapping(value = "/test", produces = "application/json; charset=utf-8")
    public String test() {
        return "" + authenticationManager;
    }
}