package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.Date;
import java.util.List;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetHistoryItemEntity;
import kz.worldclass.finances.data.entity.QBudgetHistoryItemEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetHistoryItemDao extends AbstractDao<BudgetHistoryItemEntity> {
    @Override
    protected Class<BudgetHistoryItemEntity> getEntityClass() {
        return BudgetHistoryItemEntity.class;
    }
    
    public List<BudgetHistoryItemEntity> fetchBetweenDates(String currencyCode, Date startDate, Date endDate) {
        HibernateQuery<BudgetHistoryItemEntity> query = getQueryFactory()
                .select(QBudgetHistoryItemEntity.budgetHistoryItemEntity)
                .from(QBudgetHistoryItemEntity.budgetHistoryItemEntity)
                .where(
                        QBudgetHistoryItemEntity.budgetHistoryItemEntity.currency.code.eq(currencyCode),
                        QBudgetHistoryItemEntity.budgetHistoryItemEntity.history.changeDate.goe(startDate),
                        QBudgetHistoryItemEntity.budgetHistoryItemEntity.history.changeDate.lt(endDate)
                );
        log(query);
        return query.fetch();
    }
}