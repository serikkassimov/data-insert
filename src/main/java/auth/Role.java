package auth;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Serik on 12.03.2016.
 */
public class Role implements GrantedAuthority {

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthority() {
        return this.name;
    }
}
