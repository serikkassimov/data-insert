package kz.worldclass.finances.data.dto.auth;

public class PublicAuthenticationDto {
    public boolean authenticated;
    public boolean anonymousUser;
    public PublicUserDto principal;
    public RoleDto[] authorities;
}