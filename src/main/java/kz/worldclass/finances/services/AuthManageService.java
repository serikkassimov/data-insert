package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kz.worldclass.finances.dao.impl.DictOrgDao;
import kz.worldclass.finances.dao.impl.DictRoleDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.dao.impl.UserRoleLinkDao;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;
import kz.worldclass.finances.data.dto.entity.DictRoleDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.UserDto;
import kz.worldclass.finances.data.dto.results.authmanage.LockUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.SaveUserException;
import kz.worldclass.finances.data.dto.results.authmanage.SaveUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.UnlockUserResult;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import kz.worldclass.finances.data.entity.UserRoleLinkEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthManageService {
    @Autowired
    private DictOrgDao dictOrgDao;
    @Autowired
    private DictRoleDao dictRoleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleLinkDao userRoleLinkDao;
    
    public List<UserDto> getUsers() {
        List<UserDto> result = new ArrayList<>();
        for (UserEntity userEntity: userDao.all()) result.add(Dtos.complete(userEntity));
        return result;
    }
    
    public List<DictRoleDto> getRoles() {
        List<DictRoleDto> result = new ArrayList<>();
        for (DictRoleEntity entity: dictRoleDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictOrgDto> getOrgs() {
        List<DictOrgDto> result = new ArrayList<>();
        for (DictOrgEntity entity: dictOrgDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public SaveUserResult saveUser(UserDto userDto) throws SaveUserException {
        UserEntity userEntity;
        if (userDto.id == null) userEntity = new UserEntity();
        else {
            userEntity = userDao.get(userDto.id);
            if (userEntity == null) return SaveUserResult.USER_NOT_FOUND;
        }

        UserEntity loginOwnerEntity = userDao.fetchOneByLogin(userDto.login);
        if ((loginOwnerEntity != null) && (!loginOwnerEntity.getId().equals(userEntity.getId()))) throw new SaveUserException(SaveUserResult.LOGIN_BUSY);

        DictOrgEntity orgEntity = dictOrgDao.get(userDto.org.id);
        if (orgEntity == null) throw new SaveUserException(SaveUserResult.ORG_NOT_FOUND);

        userEntity.setLogin(userDto.login);
        userEntity.setPassword(userDto.password);
        userEntity.setFirstname(userDto.firstname);
        userEntity.setLastname(userDto.lastname);
        userEntity.setPatronymic(userDto.patronymic);
        userEntity.setEmail(userDto.email);
        userEntity.setOrg(orgEntity);
        userEntity.setLocked(userDto.locked);
        
        if (userEntity.getId() == null) userDao.save(userEntity);
        
        if (userEntity.getRoleLinks() == null) userEntity.setRoleLinks(new ArrayList<UserRoleLinkEntity>());

        Map<Long, UserRoleLinkEntity> redundantLinkMap = new HashMap<>();
        for (UserRoleLinkEntity linkEntity: userEntity.getRoleLinks()) redundantLinkMap.put(linkEntity.getRole().getId(), linkEntity);

        Set<Long> missingRoleIds = new HashSet<>();
        if (userDto.roles != null) {
            for (DictRoleDto roleDto: userDto.roles) {
                if ((roleDto != null) && (roleDto.id != null)) {
                    if (redundantLinkMap.containsKey(roleDto.id)) redundantLinkMap.remove(roleDto.id);
                    else missingRoleIds.add(roleDto.id);
                }
            }
        }

        for (UserRoleLinkEntity linkEntity: redundantLinkMap.values()) userRoleLinkDao.delete(linkEntity);
        for (Long missingRoleId: missingRoleIds) {
            DictRoleEntity roleEntity = dictRoleDao.get(missingRoleId);
            if (roleEntity == null) throw new SaveUserException(SaveUserResult.ROLE_NOT_FOUND);
            
            UserRoleLinkEntity linkEntity = new UserRoleLinkEntity();
            linkEntity.setUser(userEntity);
            linkEntity.setRole(roleEntity);
            userRoleLinkDao.save(linkEntity);
        }

        userDao.save(userEntity);
        
        return SaveUserResult.SUCCESS;
    }
    
    public LockUserResult lockUser(Long id) {
        UserEntity userEntity = userDao.get(id);
        if (userEntity == null) return LockUserResult.NOT_FOUND;
        
        userEntity.setLocked(true);
        userDao.save(userEntity);
        return LockUserResult.SUCCESS;
    }
    
    public UnlockUserResult unlockUser(Long id) {
        UserEntity userEntity = userDao.get(id);
        if (userEntity == null) return UnlockUserResult.NOT_FOUND;
        
        
        userEntity.setLocked(false);
        userDao.save(userEntity);
        return UnlockUserResult.SUCCESS;
    }
}