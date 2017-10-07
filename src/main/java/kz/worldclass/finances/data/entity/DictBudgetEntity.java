package kz.worldclass.finances.data.entity;

import kz.worldclass.finances.data.entity.base.BaseDictEntity;

import javax.persistence.*;
import java.util.Collection;

import static kz.worldclass.finances.data.entity.DictBudgetEntity.*;

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
public class DictBudgetEntity extends BaseDictEntity {
    public static final String TABLE = "DICT_BUDGET";
    
    @OneToMany(mappedBy = UserRoleLinkEntity.PROP_ROLE)
    private Collection<UserRoleLinkEntity> userLinks;

    public Collection<UserRoleLinkEntity> getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(Collection<UserRoleLinkEntity> userLinks) {
        this.userLinks = userLinks;
    }
}