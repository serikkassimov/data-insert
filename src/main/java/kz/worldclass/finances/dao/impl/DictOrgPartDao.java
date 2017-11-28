package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictOrgPartEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictOrgPartDao extends AbstractDictDao<DictOrgPartEntity> {
    @Override
    protected Class<DictOrgPartEntity> getEntityClass() {
        return DictOrgPartEntity.class;
    }
}