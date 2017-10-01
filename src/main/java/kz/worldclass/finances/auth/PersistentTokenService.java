package kz.worldclass.finances.auth;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

public interface PersistentTokenService {
    public PersistentRememberMeToken createNewRememberMeToken(String username);
    public PersistentRememberMeToken createNewCommonToken(String username);
    public void removeToken(String series);
}