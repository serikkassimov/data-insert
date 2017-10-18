package kz.worldclass.finances.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import kz.worldclass.finances.auth.PersistentTokenService;
import kz.worldclass.finances.auth.Role;
import kz.worldclass.finances.auth.User;
import kz.worldclass.finances.dao.impl.PersistentTokenDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import kz.worldclass.finances.data.entity.PersistentTokenEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import kz.worldclass.finances.data.entity.UserRoleLinkEntity;
import kz.worldclass.finances.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Serik on 12.03.2016.
 */
@Service
@Transactional
public class AuthServiceImpl implements UserDetailsService, PersistentTokenRepository, PersistentTokenService, AuthService {
    @Autowired
    private PersistentTokenDao persistentTokenDao;
    @Autowired
    private UserDao userDao;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userDao.fetchOneByLogin(username);
        if (userEntity == null) throw new UsernameNotFoundException(String.format("no user with login \"%s\"", username));
        userDao.refresh(userEntity);
        
        User user = new User();
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        
        user.setEmail(userEntity.getEmail());
        user.setFirstName(userEntity.getFirstname());
        user.setLastName(userEntity.getLastname());
        user.setPassword(userEntity.getPassword());
        user.setUsername(userEntity.getLogin());
        user.setAccountNonLocked(!userEntity.getLocked());
        
        if (userEntity.getLocked()) {
            Role role = new Role();
            role.setName(Role.ANONYMOUS);
            user.setAuthorities(Arrays.asList(role));
        } else {
            Set<String> addedRoles = new HashSet<>();
            List<Role> roles = new ArrayList<>();
            for (UserRoleLinkEntity linkEntity: userEntity.getRoleLinks()) {
                DictRoleEntity roleEntity = linkEntity.getRole();

                Role role = new Role();
                role.setName(roleEntity.getCode());
                roles.add(role);
                addedRoles.add(roleEntity.getCode());
            }
            
            DictOrgEntity orgEntity = userEntity.getOrg();
            if ((orgEntity != null) && orgEntity.isHq()) {
                if (!addedRoles.contains(Role.HQ)) {
                    Role role = new Role();
                    role.setName(Role.HQ);
                    roles.add(role);
                }
            }
            
            user.setAuthorities(roles);
        }
        
        Collection<PersistentTokenEntity> expiredTokenEntitys =
                persistentTokenDao.fetchExpired(userEntity);
        if ((expiredTokenEntitys != null) && (!expiredTokenEntitys.isEmpty())) {
            for (PersistentTokenEntity expiredTokenEntity: expiredTokenEntitys) persistentTokenDao.delete(expiredTokenEntity);
        }
        
        return user;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        throw new UnsupportedOperationException("not supported (use createNewPersistentToken())");
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentTokenEntity tokenEntity = persistentTokenDao.fetchOneBySeries(series);
        if (tokenEntity == null) throw new IllegalStateException(String.format("no token with series \"%s\"", series));
        tokenEntity.setTokenValue(tokenValue);
        tokenEntity.setTokenDate(new Date());
        persistentTokenDao.merge(tokenEntity);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentTokenEntity tokenEntity = persistentTokenDao.fetchOneBySeries(seriesId);
        if (tokenEntity == null) return null;
        else {
            UserEntity userEntity = tokenEntity.getUser();
            if (userEntity == null) return null;
            else {
                String login = userEntity.getLogin();
                if ((login == null) || login.isEmpty()) return null;
                else return new PersistentRememberMeToken(login, seriesId, tokenEntity.getTokenValue(), tokenEntity.getTokenDate());
            }
        }
    }

    @Override
    public void removeUserTokens(String username) {
        UserEntity userEntity = userDao.fetchOneByLogin(username);
        if (userEntity != null) {
            for (PersistentTokenEntity tokenEntity: userEntity.getPersistentTokens()) persistentTokenDao.delete(tokenEntity);
        }
    }

    @Override
    public PersistentRememberMeToken createNewRememberMeToken(String username) {
        UserEntity userEntity = userDao.fetchOneByLogin(username);
        if (userEntity == null) return null;
        else {
            PersistentTokenEntity tokenEntity = new PersistentTokenEntity();
            tokenEntity.setSeries("" + System.currentTimeMillis() + Thread.currentThread().getId());
            tokenEntity.setTokenDate(new Date());
            tokenEntity.setTokenValue("");
            tokenEntity.setUser(userEntity);
            tokenEntity.setRemembered(true);
            
            persistentTokenDao.persist(tokenEntity);
            
            tokenEntity.setSeries(new UUID(tokenEntity.getId(), new Random(System.currentTimeMillis()).nextLong()).toString());
            
            persistentTokenDao.merge(tokenEntity);
            
            return new PersistentRememberMeToken(username, tokenEntity.getSeries(), tokenEntity.getTokenValue(), tokenEntity.getTokenDate());
        }
    }

    @Override
    public PersistentRememberMeToken createNewCommonToken(String username) {
        UserEntity userEntity = userDao.fetchOneByLogin(username);
        if (userEntity == null) return null;
        else {
            PersistentTokenEntity tokenEntity = new PersistentTokenEntity();
            tokenEntity.setSeries("" + System.currentTimeMillis() + Thread.currentThread().getId());
            tokenEntity.setTokenDate(new Date());
            tokenEntity.setTokenValue("");
            tokenEntity.setUser(userEntity);
            tokenEntity.setRemembered(false);
            
            persistentTokenDao.persist(tokenEntity);
            
            tokenEntity.setSeries(new UUID(tokenEntity.getId(), new Random(System.currentTimeMillis()).nextLong()).toString());
            
            persistentTokenDao.merge(tokenEntity);
            
            return new PersistentRememberMeToken(username, tokenEntity.getSeries(), tokenEntity.getTokenValue(), tokenEntity.getTokenDate());
        }
    }

    @Override
    public void removeToken(String series) {
        PersistentTokenEntity tokenEntity = persistentTokenDao.fetchOneBySeries(series);
        if (tokenEntity != null) persistentTokenDao.delete(tokenEntity);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) return null;
        
        return (User) principal;
    }
    
    @Override
    public DictOrgDto getCurrentUserOrg() {
        User user = getCurrentUser();
        if (user == null) return null;
        
        String login = user.getUsername();
        if ((login == null) || login.isEmpty()) return null;
        
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return null;
        
        return Dtos.less(userEntity.getOrg());
    }
}