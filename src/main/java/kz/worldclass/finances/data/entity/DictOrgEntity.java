package kz.worldclass.finances.data.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.DictOrgEntity.*;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;

@Entity
@Table(
        name = TABLE, schema = SCHEMA,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_CODE,
                    unique = true,
                    columnList = COL_CODE
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + COL_CODE,
                    columnNames = {COL_CODE}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class DictOrgEntity extends BaseDictEntity {
    public static final String TABLE = "DICT_ORG";
    
    @OneToMany(mappedBy = UserEntity.PROP_ORG)
    private Collection<UserEntity> users;

    public Collection<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }
}