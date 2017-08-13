package auth;


import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Serik on 12.03.2016.
 */
@Repository
public class UserDAOImpl {
    private String mailPart = "@kazatomprom.kz";
    public User loadUserByUsername(final String username) {
        //Write your DB call code to get the user details from DB,But I am just hard coding the user
        if (username.toLowerCase().equals("admin")) {
            return createAdminUser();
        }
        User user = new User();
        user.setFirstName("kb");
        user.setLastName("gc");
        user.setUsername("dsadykov@kazatomprom.kz");
        user.setPassword("1234");
        Role r = new Role();
        r.setName("ROLE_USER");
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        user.setAuthorities(roles);
        return user;
    }

    private User createAdminUser() {
        User user = new User();
        user.setFirstName("administrator");
        user.setLastName("admin");
        user.setUsername("admin");
        user.setPassword("Qwerty765%");
        Role r = new Role();
        r.setName("ROLE_USER");
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        r = new Role();
        r.setName("ROLE_ADMIN");
        roles.add(r);
        user.setAuthorities(roles);
        return user;

    }

    public User createUser(String username, String password) {
        if (!checkUser(username))  {
            return null;
        }
        User user = new User();
        user.setFirstName(username);
        user.setLastName(username);
        user.setUsername(username);
        user.setPassword(password);
        Role r = new Role();
        r.setName("ROLE_USER");
        List<Role> roles = new ArrayList<Role>();
        roles.add(r);
        user.setAuthorities(roles);
        return user;
    }

    private boolean checkUser(String username) {
        boolean result = false;
        for (String user : getUserList()) {
            if (user.equals(username)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private List<String> getUserList() {
        ArrayList<String> list = new ArrayList<>();
        try (FileReader reader = new FileReader("kapuser.txt")) {
            try (BufferedReader br = new BufferedReader(reader)) {
                String line = br.readLine();
                while (line != null) {
                    list.add(line+mailPart);
                    line = br.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
