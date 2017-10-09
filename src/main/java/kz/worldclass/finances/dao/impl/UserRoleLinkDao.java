package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.UserRoleLinkEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleLinkDao extends AbstractDao<UserRoleLinkEntity> {
    @Override
    protected Class<UserRoleLinkEntity> getEntityClass() {
        return UserRoleLinkEntity.class;
    }
}