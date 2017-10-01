package kz.worldclass.finances.data.entity;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.DictRoleEntity.*;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
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
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = 10)
@SuppressWarnings("PersistenceUnitPresent")
public class DictRoleEntity extends BaseDictEntity {
    public static final String TABLE = "DICT_ROLE";
    
    @OneToMany(mappedBy = UserRoleLinkEntity.PROP_ROLE)
    private Collection<UserRoleLinkEntity> userLinks;

    public Collection<UserRoleLinkEntity> getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(Collection<UserRoleLinkEntity> userLinks) {
        this.userLinks = userLinks;
    }
}