package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.List;
import kz.worldclass.finances.dao.impl.DictOrgDao;
import kz.worldclass.finances.dao.impl.DictRoleDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;
import kz.worldclass.finances.data.dto.entity.DictRoleDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.UserDto;
import kz.worldclass.finances.data.dto.results.authmanage.LockUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.SaveUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.UnlockUserResult;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import kz.worldclass.finances.data.entity.UserEntity;
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
    
    public List<UserDto> getUsers() {
        List<UserDto> result = new ArrayList<>();
        for (UserEntity userEntity: userDao.all()) result.add(Dtos.complete(userEntity));
        return result;
    }
    
    public List<DictRoleDto> getRoles() {
        List<DictRoleDto> result = new ArrayList<>();
        for (DictRoleEntity entity: dictRoleDao.all()) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictOrgDto> getOrgs() {
        List<DictOrgDto> result = new ArrayList<>();
        for (DictOrgEntity entity: dictOrgDao.all()) result.add(Dtos.less(entity));
        return result;
    }
    
    public SaveUserResult saveUser(UserDto userDto) {
        UserEntity userEntity;
        if (userDto.id == null) userEntity = new UserEntity();
        else userEntity = userDao.get(userDto.id);
        
        UserEntity loginOwnerEntity = userDao.fetchOneByLogin(userDto.login);
        if ((loginOwnerEntity != null) && (!loginOwnerEntity.getId().equals(userEntity.getId()))) return SaveUserResult.LOGIN_BUSY;
        
        DictOrgEntity orgEntity = dictOrgDao.get(userDto.org.id);
        if (orgEntity == null) return SaveUserResult.ORG_NOT_FOUND;
        
        userEntity.setLogin(userDto.login);
        userEntity.setPassword(userDto.password);
        userEntity.setFirstname(userDto.firstname);
        userEntity.setLastname(userDto.lastname);
        userEntity.setPatronymic(userDto.patronymic);
        userEntity.setEmail(userDto.email);
        userEntity.setOrg(orgEntity);
        
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