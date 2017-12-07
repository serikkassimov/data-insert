package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BudgetNextChangeItemDao extends AbstractDao<BudgetNextChangeItemEntity> {
    @Override
    protected Class<BudgetNextChangeItemEntity> getEntityClass() {
        return BudgetNextChangeItemEntity.class;
    }

    public List<BudgetNextChangeItemEntity> fetchBetweenDates(String currencyCode, Date startDate, Date endDate) {
        HibernateQuery<BudgetNextChangeItemEntity> query = getQueryFactory()
                .select(QBudgetNextChangeItemEntity.budgetNextChangeItemEntity)
                .from(QBudgetNextChangeItemEntity.budgetNextChangeItemEntity)
                .where(
                        QBudgetNextChangeItemEntity.budgetNextChangeItemEntity.currency.code.eq(currencyCode),
                        QBudgetNextChangeItemEntity.budgetNextChangeItemEntity.change.changeDate.goe(startDate),
                        QBudgetNextChangeItemEntity.budgetNextChangeItemEntity.change.changeDate.lt(endDate)
                );
        log(query);
        return query.fetch();
    }
}