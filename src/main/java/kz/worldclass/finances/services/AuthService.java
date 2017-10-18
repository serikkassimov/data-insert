package kz.worldclass.finances.services;

import kz.worldclass.finances.auth.User;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;

public interface AuthService {
    public User getCurrentUser();
    public DictOrgDto getCurrentUserOrg();
}