package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractTreeDictDao;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictBudgetDao extends AbstractTreeDictDao<DictBudgetEntity> {
    @Override
    protected Class<DictBudgetEntity> getEntityClass() {
        return DictBudgetEntity.class;
    }
}