package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetEntity;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.QBudgetEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDao extends AbstractDao<BudgetEntity> {
    @Override
    protected Class<BudgetEntity> getEntityClass() {
        return BudgetEntity.class;
    }
    
    public BudgetEntity fetchOneForOrgCurrencyStoreType(DictOrgEntity org, DictCurrencyEntity currency, DictBudgetStoreTypeEntity storeType) {
        HibernateQuery<BudgetEntity> query = getQueryFactory()
                .select(QBudgetEntity.budgetEntity)
                .from(QBudgetEntity.budgetEntity)
                .where(
                        QBudgetEntity.budgetEntity.org.eq(org),
                        QBudgetEntity.budgetEntity.currency.eq(currency),
                        QBudgetEntity.budgetEntity.storeType.eq(storeType)
                );
        log(query);
        return query.fetchOne();
    }
}