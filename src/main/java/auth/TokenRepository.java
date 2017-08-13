package auth;

import entity.TokensEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by serik on 05.04.16.
 */
@Component
public class TokenRepository implements PersistentTokenRepository {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {
        TokensEntity tokensEntity = new TokensEntity();
        tokensEntity.setSeries(persistentRememberMeToken.getSeries());
        tokensEntity.setUsername(persistentRememberMeToken.getUsername());
        tokensEntity.setTokenValue(persistentRememberMeToken.getTokenValue());
        tokensEntity.setTokenDate(new Date());
        Session session = sessionFactory.openSession();
        session.saveOrUpdate(tokensEntity);
        session.flush();
        session.close();
    }

    @Override
    public void updateToken(String s, String s1, Date date) {
       /* PersistentRememberMeToken token = tokenHashMap.get(s);
        PersistentRememberMeToken newToken =  new PersistentRememberMeToken(token.getUsername(),s, s1, date);
        tokenHashMap.put(s, newToken);*/
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(TokensEntity.class);
        criteria.add(Restrictions.eq("series", s));
        TokensEntity tokensEntity = (TokensEntity) criteria.uniqueResult();
        tokensEntity.setTokenDate(date);
        tokensEntity.setTokenValue(s1);
        session.saveOrUpdate(tokensEntity);
        session.flush();
        session.close();
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String s) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(TokensEntity.class);
        criteria.add(Restrictions.eq("series", s));
        TokensEntity tokensEntity = (TokensEntity) criteria.uniqueResult();
        session.close();
        PersistentRememberMeToken meToken = null;
        if (tokensEntity != null) {
            meToken = new PersistentRememberMeToken(tokensEntity.getUsername()
                    , tokensEntity.getSeries()
                    , tokensEntity.getTokenValue()
                    , tokensEntity.getTokenDate());
        }

        return meToken;
    }

    @Override
    public void removeUserTokens(String s) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(TokensEntity.class);
        criteria.add(Restrictions.eq("series", s));
        TokensEntity tokensEntity = (TokensEntity) criteria.uniqueResult();
        if (tokensEntity!=null) {
            session.delete(tokensEntity);
        }
        session.flush();
        session.close();
    }
}

