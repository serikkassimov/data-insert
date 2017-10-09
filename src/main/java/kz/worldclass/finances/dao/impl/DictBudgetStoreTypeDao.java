package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictBudgetStoreTypeDao extends AbstractDictDao<DictBudgetStoreTypeEntity> {
    @Override
    protected Class<DictBudgetStoreTypeEntity> getEntityClass() {
        return DictBudgetStoreTypeEntity.class;
    }
}