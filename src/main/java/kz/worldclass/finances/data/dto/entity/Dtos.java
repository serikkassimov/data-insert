package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import kz.worldclass.finances.data.entity.UserRoleLinkEntity;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;
import kz.worldclass.finances.data.entity.base.BaseEntity;

public class Dtos {
    /**
     * 
     * @param source
     * @param target 
     * @throws NullPointerException target is null
     */
    public static void copy(BaseEntity source, BaseDto target) {
        Objects.requireNonNull(target, "target is null");
        if (source != null) target.id = source.getId();
    }
    
    /**
     * 
     * @param source
     * @param target 
     * @throws NullPointerException target is null
     */
    public static void copy(BaseDictEntity source, BaseDictDto target) {
        Objects.requireNonNull(target, "target is null");
        copy((BaseEntity) source, (BaseDto) target);
        if (source != null) {
            target.code = source.getCode();
            target.name = source.getName();
        }
    }
    
    public static DictOrgDto less(DictOrgEntity source) {
        DictOrgDto result = new DictOrgDto();
        copy(source, result);
        if (source != null) {}
        return result;
    }
    
    public static DictOrgDto complete(DictOrgEntity source) {
        DictOrgDto result = less(source);
        if (source != null) {}
        return result;
    }
    
    public static DictRoleDto less(DictRoleEntity source) {
        DictRoleDto result = new DictRoleDto();
        copy(source, result);
        if (source != null) {}
        return result;
    }
    
    public static DictRoleDto complete(DictRoleEntity source) {
        DictRoleDto result = less(source);
        if (source != null) {}
        return result;
    }
    
    public static UserDto less(UserEntity source) {
        UserDto result = new UserDto();
        copy(source, result);
        if (source != null) {
            result.login = source.getLogin();
            result.password = source.getPassword();
            result.firstname = source.getFirstname();
            result.lastname = source.getLastname();
            result.patronymic = source.getPatronymic();
            result.email = source.getEmail();
            result.locked = source.getLocked();
        }
        return result;
    }
    
    public static UserDto complete(UserEntity source) {
        UserDto result = less(source);
        if (source != null) {
            List<DictRoleDto> roleDtos = new ArrayList<>();
            Collection<UserRoleLinkEntity> roleLinkEntitys = source.getRoleLinks();
            if (roleLinkEntitys != null) {
                for (UserRoleLinkEntity linkEntity: roleLinkEntitys) {
                    if (linkEntity != null) {
                        DictRoleEntity roleEntity = linkEntity.getRole();
                        if (roleEntity != null) roleDtos.add(complete(roleEntity));
                    }
                }
            }
            result.roles = roleDtos.toArray(new DictRoleDto[roleDtos.size()]);
            
            result.org = complete(source.getOrg());
        }
        return result;
    }
}