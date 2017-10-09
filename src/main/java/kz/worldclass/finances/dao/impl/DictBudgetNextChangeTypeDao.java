package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictBudgetNextChangeTypeDao extends AbstractDictDao<DictBudgetNextChangeTypeEntity> {
    @Override
    protected Class<DictBudgetNextChangeTypeEntity> getEntityClass() {
        return DictBudgetNextChangeTypeEntity.class;
    }
}