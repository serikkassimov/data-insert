package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class UserDto extends BaseDto {
    public String login;
    public String password;
    public String firstname;
    public String lastname;
    public String patronymic;
    public String email;
    public Boolean locked;
    
    public DictOrgDto org;
    public DictRoleDto[] roles;
}