package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.Date;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.OutgoingRequestEntity;
import kz.worldclass.finances.data.entity.QOutgoingRequestEntity;
import org.springframework.stereotype.Repository;

@Repository
public class OutgoingRequestDao extends AbstractDao<OutgoingRequestEntity> {
    @Override
    protected Class<OutgoingRequestEntity> getEntityClass() {
        return OutgoingRequestEntity.class;
    }
    
    public OutgoingRequestEntity fetchOneByOrgAndDate(DictOrgEntity org, Date date) {
        HibernateQuery<OutgoingRequestEntity> query = getQueryFactory()
                .select(QOutgoingRequestEntity.outgoingRequestEntity)
                .from(QOutgoingRequestEntity.outgoingRequestEntity)
                .where(
                        QOutgoingRequestEntity.outgoingRequestEntity.org.eq(org),
                        QOutgoingRequestEntity.outgoingRequestEntity.date.eq(date)
                );
        log(query);
        return query.fetchOne();
    }
}