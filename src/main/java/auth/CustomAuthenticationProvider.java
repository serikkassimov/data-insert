package auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Serik on 12.03.2016.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserService userService;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        User user = null;
        if (username.toLowerCase().equals("admin")) {
            user = userService.loadUserByUsername(username);
        } else {
            if (authenticateJndi(username, password)) {
                user = userService.createUser(username, password);
            }
        }

        if (user == null || !user.getUsername().equalsIgnoreCase(username)) {
            throw new BadCredentialsException("Пользователь с таким логином не найден в списке");
        }

        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        System.out.println("Logined username = " + username + " at " + new Date());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, password, authorities);
        return token;
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

    public boolean authenticateJndi(String username, String password) {
        try {
            Hashtable<String, String> env1 = new Hashtable<String, String>();
            env1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env1.put(Context.SECURITY_AUTHENTICATION, "simple");
            env1.put(Context.SECURITY_PRINCIPAL, username);
            //env1.put(Context.SECURITY_PRINCIPAL, "uid="+username+",ou=users,dc=kazatomprom,dc=kz");
            env1.put(Context.SECURITY_CREDENTIALS, password);
            env1.put(Context.PROVIDER_URL, "ldap://kzast01-adc-01.kazatomprom.kz:389");
            //env1.put(Context.PROVIDER_URL, "ldap://10.60.6.34:389");
            try {
                    //Connect with ldap
                    new InitialLdapContext(env1, null);

                //Connection succeeded
                System.out.println("Connection succeeded! " + username);
                return true;
            } catch (AuthenticationException e) {

                //Connection failed
                System.out.println("Connection failed! " + username);
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }


}