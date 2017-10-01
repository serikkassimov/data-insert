package kz.worldclass.finances.auth;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Serik on 12.03.2016.
 */
public class Role implements GrantedAuthority {
    private static final String PREFIX = "ROLE_";
    
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return PREFIX + this.name;
    }
}
