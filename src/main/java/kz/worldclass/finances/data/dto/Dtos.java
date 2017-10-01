package kz.worldclass.finances.data.dto;

import kz.worldclass.finances.data.dto.auth.PublicUserDto;
import kz.worldclass.finances.data.dto.auth.PublicAuthenticationDto;
import kz.worldclass.finances.data.dto.auth.RoleDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kz.worldclass.finances.auth.Role;
import kz.worldclass.finances.auth.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Dtos {
    public static final String ANONYMOUS_USER = "anonymousUser";
    
    public static PublicAuthenticationDto less(Authentication source) {
        PublicAuthenticationDto result = new PublicAuthenticationDto();
        if (source != null) {
            result.authenticated = source.isAuthenticated();
            result.anonymousUser = ANONYMOUS_USER.equals(source.getPrincipal());
        }
        return result;
    }
    
    public static PublicAuthenticationDto complete(Authentication source) {
        PublicAuthenticationDto result = less(source);
        if (source != null) {
            Object principal = source.getPrincipal();
            if ((principal != null) && (principal instanceof User)) result.principal = complete((User) principal);
            
            Collection<? extends GrantedAuthority> authorities = source.getAuthorities();
            if ((authorities != null) && (!authorities.isEmpty())) {
                List<RoleDto> roleDtos = new ArrayList<>();
                for (GrantedAuthority authority: authorities) roleDtos.add(complete(authority));
                result.authorities = roleDtos.toArray(new RoleDto[roleDtos.size()]);
            }
        }
        return result;
    }
    
    public static PublicUserDto less(User source) {
        PublicUserDto result = new PublicUserDto();
        if (source != null) {
            result.email = source.getEmail();
            result.firstName = source.getFirstName();
            result.lastName = source.getLastName();
            result.username = source.getUsername();
            result.accountNonExpired = source.isAccountNonExpired();
            result.accountNonLocked = source.isAccountNonLocked();
            result.credentialsNonExpired = source.isCredentialsNonExpired();
            result.enabled = source.isEnabled();
        }
        return result;
    }
    
    public static PublicUserDto complete(User source) {
        PublicUserDto result = less(source);
        if (source != null) {
            List<Role> roles = source.getAuthorities();
            if ((roles != null) && (!roles.isEmpty())) {
                List<RoleDto> roleDtos = new ArrayList<>();
                for (Role role: roles) roleDtos.add(complete(role));
                result.authorities = roleDtos.toArray(new RoleDto[roleDtos.size()]);
            }
        }
        return result;
    }
    
    public static RoleDto less(GrantedAuthority source) {
        RoleDto result = new RoleDto();
        if (source != null) {
            result.name = source.getAuthority();
        }
        return result;
    }
    
    public static RoleDto complete(GrantedAuthority source) {
        RoleDto result = less(source);
        return result;
    }
    
    public static RoleDto less(Role source) {
        RoleDto result = new RoleDto();
        if (source != null) {
            result.name = source.getAuthority();
        }
        return result;
    }
    
    public static RoleDto complete(Role source) {
        RoleDto result = less(source);
        return result;
    }
}