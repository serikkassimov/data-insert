package kz.worldclass.finances.dao.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.hibernate.HibernateQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import kz.worldclass.finances.auth.CustomRememberMeServices;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.PersistentTokenEntity;
import kz.worldclass.finances.data.entity.QPersistentTokenEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PersistentTokenDao extends AbstractDao<PersistentTokenEntity> {
    @Override
    protected Class<PersistentTokenEntity> getEntityClass() {
        return PersistentTokenEntity.class;
    }
    
    public PersistentTokenEntity fetchOneBySeries(String series) {
        HibernateQuery<PersistentTokenEntity> query = getQueryFactory()
                .select(QPersistentTokenEntity.persistentTokenEntity)
                .from(QPersistentTokenEntity.persistentTokenEntity)
                .where(QPersistentTokenEntity.persistentTokenEntity.series.eq(series));
        log(query);
        return query.fetchOne();
    }
    
    public Collection<PersistentTokenEntity> fetchExpired(UserEntity userEntity) {
        if (userEntity == null) return new ArrayList<>();
        else {
            Date rememberedExpirationDate = new Date(System.currentTimeMillis() - CustomRememberMeServices.REMEMBERED_TOKEN_LIFETIME);
            Date commonExpirationDate = new Date(System.currentTimeMillis() - CustomRememberMeServices.COMMON_COOKIE_LIFETIME);
            
            HibernateQuery<PersistentTokenEntity> query = getQueryFactory()
                    .select(QPersistentTokenEntity.persistentTokenEntity)
                    .from(QPersistentTokenEntity.persistentTokenEntity)
                    .where(
                            QPersistentTokenEntity.persistentTokenEntity.user.eq(userEntity),
                            new BooleanBuilder(
                                    new BooleanBuilder(QPersistentTokenEntity.persistentTokenEntity.remembered.eq(true))
                                        .and(QPersistentTokenEntity.persistentTokenEntity.tokenDate.loe(rememberedExpirationDate))
                            ).or(
                                    new BooleanBuilder(QPersistentTokenEntity.persistentTokenEntity.remembered.eq(false))
                                        .and(QPersistentTokenEntity.persistentTokenEntity.tokenDate.loe(commonExpirationDate))
                            )
                    );
            log(query);
            return query.fetch();
        }
    }
}