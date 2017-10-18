package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.List;
import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.QDictOrgEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictOrgDao extends AbstractDictDao<DictOrgEntity> {
    @Override
    protected Class<DictOrgEntity> getEntityClass() {
        return DictOrgEntity.class;
    }
    
    public List<DictOrgEntity> allEnabledExceptCodes(String... exceptedCodes) {
        HibernateQuery<DictOrgEntity> query = getQueryFactory()
                .select(QDictOrgEntity.dictOrgEntity)
                .from(QDictOrgEntity.dictOrgEntity)
                .where(
                        QDictOrgEntity.dictOrgEntity.disabled.eq(false),
                        QDictOrgEntity.dictOrgEntity.code.notIn(exceptedCodes)
                );
        log(query);
        return query.fetch();
    }
}