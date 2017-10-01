package kz.worldclass.finances.dao.impl;

import com.querydsl.jpa.hibernate.HibernateQuery;
import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.QUserEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao<UserEntity> {
    @Override
    protected Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }
    
    public UserEntity fetchOneByLogin(String login) {
        if (login == null) return null;
        HibernateQuery<UserEntity> query = getQueryFactory()
                .select(QUserEntity.userEntity)
                .from(QUserEntity.userEntity)
                .where(QUserEntity.userEntity.login.eq(login));
        log(query);
        return query.fetchOne();
    }
}