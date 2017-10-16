package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.List;
import kz.worldclass.finances.dao.AbstractTreeDictDao;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import kz.worldclass.finances.data.entity.QDictBudgetEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictBudgetDao extends AbstractTreeDictDao<DictBudgetEntity> {
    @Override
    protected Class<DictBudgetEntity> getEntityClass() {
        return DictBudgetEntity.class;
    }
    
    public List<DictBudgetEntity> fetchEnabledIncomingLeafs() {
        HibernateQuery<DictBudgetEntity> query = getQueryFactory()
                .select(QDictBudgetEntity.dictBudgetEntity)
                .from(QDictBudgetEntity.dictBudgetEntity)
                .where(
                        QDictBudgetEntity.dictBudgetEntity.disabled.eq(false),
                        QDictBudgetEntity.dictBudgetEntity.outgo.eq(false),
                        QDictBudgetEntity.dictBudgetEntity.children.isEmpty()
                );
        log(query);
        return query.fetch();
    }
}