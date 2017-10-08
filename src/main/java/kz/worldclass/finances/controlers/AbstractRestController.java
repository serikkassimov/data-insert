package kz.worldclass.finances.controlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kz.worldclass.finances.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstractRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestController.class);
    
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF_8 = "application/json; charset=utf-8";
    
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    
    protected User user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (LOGGER.isWarnEnabled()) LOGGER.warn("no authentication in SecurityContextHolder");
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal == null) return null;
        else if (principal instanceof User) return (User) principal;
        else if (principal instanceof AnonymousAuthenticationToken) return null;
        else {
            if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format(
                    "authentication.principal is %s, not %s",
                    principal.getClass().getName(),
                    User.class.getName()
            ));
            return null;
        }
    }
}