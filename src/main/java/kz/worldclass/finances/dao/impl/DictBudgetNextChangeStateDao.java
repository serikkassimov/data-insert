package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeStateEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictBudgetNextChangeStateDao extends AbstractDictDao<DictBudgetNextChangeStateEntity>{
    @Override
    protected Class<DictBudgetNextChangeStateEntity> getEntityClass() {
        return DictBudgetNextChangeStateEntity.class;
    }
}