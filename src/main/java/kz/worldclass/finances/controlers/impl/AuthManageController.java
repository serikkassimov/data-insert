package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;
import kz.worldclass.finances.data.dto.entity.DictRoleDto;
import kz.worldclass.finances.data.dto.entity.UserDto;
import kz.worldclass.finances.data.dto.results.authmanage.LockUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.SaveUserException;
import kz.worldclass.finances.data.dto.results.authmanage.SaveUserResult;
import kz.worldclass.finances.data.dto.results.authmanage.UnlockUserResult;
import kz.worldclass.finances.services.AuthManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth-manage")
public class AuthManageController extends AbstractRestController {
    @Autowired
    private AuthManageService service;
    
    @RequestMapping(value = "/users", produces = APPLICATION_JSON_UTF_8)
    public List<UserDto> users() {
        return service.getUsers();
    }
    
    @RequestMapping(value = "/roles", produces = APPLICATION_JSON_UTF_8)
    public List<DictRoleDto> roles() {
        return service.getRoles();
    }
    
    @RequestMapping(value = "/orgs", produces = APPLICATION_JSON_UTF_8)
    public List<DictOrgDto> orgs() {
        return service.getOrgs();
    }
    
    @RequestMapping(value = "/save-user", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON)
    public SaveUserResult saveUser(
            @RequestBody(required = false) UserDto userDto
    ) {
        if (userDto == null) return SaveUserResult.NO_DATA;
        if ((userDto.login == null) || userDto.login.isEmpty()) return SaveUserResult.NO_LOGIN;
        if ((userDto.password == null) || userDto.password.isEmpty()) return SaveUserResult.NO_PASSWORD;
        if ((userDto.org == null) || (userDto.org.id == null)) return SaveUserResult.NO_ORG;
        try {
            return service.saveUser(userDto);
        } catch (SaveUserException exception) {
            return exception.getResult();
        }
    }
    
    @RequestMapping(value = "/lock", produces = APPLICATION_JSON_UTF_8)
    public LockUserResult lock(
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return LockUserResult.NO_ID;
        return service.lockUser(id);
    }
    
    @RequestMapping(value = "/unlock", produces = APPLICATION_JSON_UTF_8)
    public UnlockUserResult unlock(
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return UnlockUserResult.NO_ID;
        return service.unlockUser(id);
    }
}