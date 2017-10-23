package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetHistoryDao extends AbstractDao<BudgetHistoryEntity> {
    @Override
    protected Class<BudgetHistoryEntity> getEntityClass() {
        return BudgetHistoryEntity.class;
    }
}