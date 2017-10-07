package kz.worldclass.finances.data.entity.base;

import java.util.Collection;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@MappedSuperclass
public class BaseTreeDictEntity<T extends BaseTreeDictEntity> extends BaseDictEntity {
    public static final String COL_PARENT_ID = "PARENT_ID";
    public static final String PROP_PARENT = "parent";
    
    @ManyToOne
    @JoinColumn(
            name = COL_PARENT_ID, nullable = true, unique = false, insertable = true, updatable = true,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT)
    )
    private T parent;
    
    @OneToMany(mappedBy = PROP_PARENT)
    private Collection<T> children;

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public Collection<T> getChildren() {
        return children;
    }

    public void setChildren(Collection<T> children) {
        this.children = children;
    }
}