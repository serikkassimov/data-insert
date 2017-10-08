package kz.worldclass.finances.dao;

import java.util.List;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDictDao<T extends BaseDictEntity> extends AbstractDao<T> {
    private static final String PARAMETER_CODE = "code";
    
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