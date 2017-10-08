package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictRoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictRoleDao extends AbstractDictDao<DictRoleEntity> {
    @Override
    protected Class<DictRoleEntity> getEntityClass() {
        return DictRoleEntity.class;
    }
}