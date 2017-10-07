package kz.worldclass.finances.data.entity;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.UserRoleLinkEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_USER_ID,
                    unique = false,
                    columnList = COL_USER_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_US_ROL,
                    unique = true,
                    columnList = COL_USER_ID + SEP_COL + COL_ROLE_ID
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + CONS_US_ROL,
                    columnNames = {COL_USER_ID, COL_ROLE_ID}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class UserRoleLinkEntity extends BaseEntity {
    public static final String TABLE = "USER_ROLE_LINK";
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_ROLE_ID = "ROLE_ID";
    public static final String PROP_USER = "user";
    public static final String PROP_ROLE = "role";
    public static final String CONS_US_ROL = "US_ROL";
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_USER_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_USER_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private UserEntity user;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_ROLE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_ROLE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictRoleEntity role;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DictRoleEntity getRole() {
        return role;
    }

    public void setRole(DictRoleEntity role) {
        this.role = role;
    }
}