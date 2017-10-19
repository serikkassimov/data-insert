package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.Date;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.BudgetNextChangeEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeTypeEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.QBudgetNextChangeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetNextChangeDao extends AbstractDao<BudgetNextChangeEntity> {
    @Override
    protected Class<BudgetNextChangeEntity> getEntityClass() {
        return BudgetNextChangeEntity.class;
    }
    
    public BudgetNextChangeEntity fetchOneForOrgTypeDate(DictOrgEntity org, DictBudgetNextChangeTypeEntity type, Date date) {
        HibernateQuery<BudgetNextChangeEntity> query = getQueryFactory()
                .select(QBudgetNextChangeEntity.budgetNextChangeEntity)
                .from(QBudgetNextChangeEntity.budgetNextChangeEntity)
                .where(
                        QBudgetNextChangeEntity.budgetNextChangeEntity.org.eq(org),
                        QBudgetNextChangeEntity.budgetNextChangeEntity.type.eq(type),
                        QBudgetNextChangeEntity.budgetNextChangeEntity.changeDate.eq(date)
                );
        log(query);
        return query.fetchOne();
    }
}