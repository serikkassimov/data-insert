package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictOrgDao extends AbstractDictDao<DictOrgEntity> {
    @Override
    protected Class<DictOrgEntity> getEntityClass() {
        return DictOrgEntity.class;
    }
}