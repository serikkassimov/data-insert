package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetNextChangeItemEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetNextChangeItemDao extends AbstractDao<BudgetNextChangeItemEntity> {
    @Override
    protected Class<BudgetNextChangeItemEntity> getEntityClass() {
        return BudgetNextChangeItemEntity.class;
    }
}