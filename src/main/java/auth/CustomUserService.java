package auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Serik on 12.03.2016.
 */
@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserDAOImpl userDao;


    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.loadUserByUsername(username);
    }

    public boolean hasUserOrg(String username, String org_id) {
        return org_id.equals("3");
    }

    public User createUser(String username, String password) {
        return userDao.createUser(username, password);
    }
}