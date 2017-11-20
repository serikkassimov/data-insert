package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.Date;
import java.util.List;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.QXIncomingEntity;
import kz.worldclass.finances.data.entity.XIncomingEntity;
import org.springframework.stereotype.Repository;

@Repository
public class XIncomingDao extends AbstractDao<XIncomingEntity> {
    @Override
    protected Class<XIncomingEntity> getEntityClass() {
        return XIncomingEntity.class;
    }
    
    public List<XIncomingEntity> fetchByOrgAndDate(DictOrgEntity org, Date date) {
        HibernateQuery<XIncomingEntity> query = getQueryFactory()
                .select(QXIncomingEntity.xIncomingEntity)
                .from(QXIncomingEntity.xIncomingEntity)
                .where(
                        QXIncomingEntity.xIncomingEntity.org.eq(org),
                        QXIncomingEntity.xIncomingEntity.date.eq(date)
                );
        log(query);
        return query.fetch();
    }
}