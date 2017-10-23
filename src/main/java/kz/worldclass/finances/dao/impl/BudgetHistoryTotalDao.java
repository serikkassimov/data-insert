package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetHistoryTotalEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetHistoryTotalDao extends AbstractDao<BudgetHistoryTotalEntity> {
    @Override
    protected Class<BudgetHistoryTotalEntity> getEntityClass() {
        return BudgetHistoryTotalEntity.class;
    }
}