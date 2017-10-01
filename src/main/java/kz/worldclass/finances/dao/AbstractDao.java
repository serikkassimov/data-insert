package kz.worldclass.finances.dao;

import com.querydsl.core.dml.DMLClause;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.hibernate.HibernateQueryFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDao<T> {
    private static final Logger QUERY_LOGGER = LoggerFactory.getLogger(AbstractDao.class.getPackage().getName() + ".QUERY");
    
    @Autowired
    protected SessionFactory sessionFactory;
    
    protected abstract Class<T> getEntityClass();
    
    /**
     * 
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    protected Session getCurrentSession() {
        if (sessionFactory == null) throw new IllegalStateException("session factory is null");
        Session result = sessionFactory.getCurrentSession();
        if (result == null) throw new IllegalStateException("current session is null");
        return result;
    }
    
    /**
     * 
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    protected final HibernateQueryFactory getQueryFactory() {
        return new HibernateQueryFactory(getCurrentSession());
    }
    
    protected final void log(HibernateQuery<?> query) {
        if ((query != null) && QUERY_LOGGER.isDebugEnabled()) QUERY_LOGGER.debug(query.toString());
    }
    
    protected final void log(DMLClause dmlClause) {
        if ((dmlClause != null) && QUERY_LOGGER.isDebugEnabled()) QUERY_LOGGER.debug(dmlClause.toString());
    }
    
    /**
     * 
     * @param id
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public T get(Long id) {
        return (T) getCurrentSession().get(getEntityClass(), id);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void persist(T entity) {
        getCurrentSession().persist(entity);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void merge(T entity) {
        getCurrentSession().merge(entity);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void save(T entity) {
        getCurrentSession().save(entity);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void update(T entity) {
        getCurrentSession().update(entity);
    }
    
    /**
     * 
     * @param entity 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>session factory is null</li>
     *      <li>current session is null</li>
     *  </ul>
     */
    public void saveOrUpdate(T entity) {
        getCurrentSession().saveOrUpdate(entity);
    }
}