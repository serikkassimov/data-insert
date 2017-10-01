package kz.worldclass.finances.data.dto.auth;

public class PublicUserDto {
    public String email;
    public String firstName;
    public String lastName;
    public String username;
    public boolean accountNonExpired;
    public boolean accountNonLocked;
    public boolean credentialsNonExpired;
    public boolean enabled;
    public RoleDto[] authorities;
}