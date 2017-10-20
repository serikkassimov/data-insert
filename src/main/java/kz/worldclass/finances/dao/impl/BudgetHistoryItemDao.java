package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetHistoryItemEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetHistoryItemDao extends AbstractDao<BudgetHistoryItemEntity> {
    @Override
    protected Class<BudgetHistoryItemEntity> getEntityClass() {
        return BudgetHistoryItemEntity.class;
    }
}