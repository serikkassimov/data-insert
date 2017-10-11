package kz.worldclass.finances.dao;

import java.util.List;
import kz.worldclass.finances.data.entity.base.BaseTreeDictEntity;

public abstract class AbstractTreeDictDao<T extends BaseTreeDictEntity> extends AbstractDictDao<T> {
    /**
     * <i><b>Note:</b> uses {@link #getCurrentSession()}, see errors which may be thrown</i>
     * @return 
     * @throws IllegalStateException one of:
     *  <ul>
     *      <li>{@link #getEntityClass()} returned <code>null</code></li>
     *  </ul>
     */
    public List<T> allRoots() {
        Class<T> entityClass = getEntityClass();
        if (entityClass == null) throw new IllegalStateException("getEntityClass() returned null");
        return getCurrentSession()
                .createQuery(String.format("select t from %s t where t.parent is null order by t.code", entityClass.getName()))
                .list();
    }
}