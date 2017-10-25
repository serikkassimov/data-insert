package kz.worldclass.finances.dao;

import java.util.List;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDictDao<T extends BaseDictEntity> extends AbstractDao<T> {
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_REQUIRED_CODES = "requiredCodes";
    private static final String PARAMETER_IGNORED_CODES = "ignoredCodes";
    private static final String PARAMETER_DISABLED = "disabled";
    
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    @Override
    public List<T> all() {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        return (List<T>) getCurrentSession()
                .createQuery(String.format("select t from %s t order by t.%s", getEntityClass().getName(), BaseDictEntity.PROP_CODE))
                .list();
    }
    
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    public List<T> allEnabled() {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        return (List<T>) getCurrentSession()
                .createQuery(String.format(
                        "select t from %s t where t.%s = :%s order by t.%s",
                        getEntityClass().getName(),
                        BaseDictEntity.PROP_DISABLED,
                        PARAMETER_DISABLED,
                        BaseDictEntity.PROP_CODE
                ))
                .setParameter(PARAMETER_DISABLED, false)
                .list();
    }
    
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    public List<T> allDisabled() {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        return (List<T>) getCurrentSession()
                .createQuery(String.format(
                        "select t from %s t where t.%s = :%s order by t.%s",
                        getEntityClass().getName(),
                        BaseDictEntity.PROP_DISABLED,
                        PARAMETER_DISABLED,
                        BaseDictEntity.PROP_CODE
                ))
                .setParameter(PARAMETER_DISABLED, true)
                .list();
    }
    
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @param disabled
     * @param requiredCodes
     * @param ignoredCodes
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    public List<T> filtered(Boolean disabled, String[] requiredCodes, String[] ignoredCodes) {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        
        boolean disabledPresent = (disabled != null);
        boolean requiredCodesPresent = ((requiredCodes != null) && (requiredCodes.length > 0));
        boolean ignoredCodesPresent = ((ignoredCodes != null) && (ignoredCodes.length > 0));
        
        StringBuilder sqlBuilder = new StringBuilder("select t from ").append(entityClass.getName()).append(" t");
        if (disabledPresent || requiredCodesPresent || ignoredCodesPresent) sqlBuilder.append(" where");
        if (disabledPresent) sqlBuilder.append(" t.").append(BaseDictEntity.PROP_DISABLED).append(" = :").append(PARAMETER_DISABLED);
        if (requiredCodesPresent) {
            if (disabledPresent) sqlBuilder.append(" and");
            sqlBuilder.append(" t.").append(BaseDictEntity.PROP_CODE).append(" in :").append(PARAMETER_REQUIRED_CODES);
        }
        if (ignoredCodesPresent) {
            if (disabledPresent || requiredCodesPresent) sqlBuilder.append(" and");
            sqlBuilder.append(" t.").append(BaseDictEntity.PROP_CODE).append(" not in :").append(PARAMETER_IGNORED_CODES);
        }
        sqlBuilder.append(" order by t.code");
        
        Query query = getCurrentSession().createQuery(sqlBuilder.toString());
        if (disabledPresent) query.setParameter(PARAMETER_DISABLED, disabled);
        if (requiredCodesPresent) query.setParameterList(PARAMETER_REQUIRED_CODES, requiredCodes);
        if (ignoredCodesPresent) query.setParameterList(PARAMETER_IGNORED_CODES, ignoredCodes);
        return query.list();
    }
    
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @param code
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    public T getByCode(String code) {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        return (T) getCurrentSession()
                .createQuery(String.format("select t from %s t where t.%s = :%s", entityClass.getName(), BaseDictEntity.PROP_CODE, PARAMETER_CODE))
                .setParameter(PARAMETER_CODE, code)
                .uniqueResult();
    }
}