package kz.worldclass.finances.data.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import kz.worldclass.finances.data.entity.base.BaseEntity;
import static kz.worldclass.finances.data.entity.UserEntity.*;

@Entity
@Table(
        name = TABLE, schema = SCHEMA,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_LOGIN,
                    unique = true,
                    columnList = COL_LOGIN
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + COL_LOGIN,
                    columnNames = {COL_LOGIN}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class UserEntity extends BaseEntity {
//    public static final String TABLE = "USER"; // могут быть ошибки при создании таблицы
    public static final String TABLE = "T_USER";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_LOGIN = "LOGIN";
    public static final String COL_PASSWORD = "PASSWORD";
    public static final String COL_FIRSTNAME = "FIRSTNAME";
    public static final String COL_LASTNAME = "LASTNAME";
    public static final String COL_PATRONYMIC = "PATRONYMIC";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_LOCKED = "LOCKED";
    public static final String PROP_ORG = "org";
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_ORG_ID, nullable = false,
            foreignKey = @ForeignKey(name = FK + SEP + TABLE + SEP + COL_ORG_ID, value = ConstraintMode.CONSTRAINT)
    )
    private DictOrgEntity org;
    
    @Column(name = COL_LOGIN, length = 50, nullable = false, unique = true)
    private String login;
    
    @Column(name = COL_PASSWORD, length = 50, nullable = false)
    private String password;
    
    @Column(name = COL_FIRSTNAME, nullable = false)
    private String firstname;
    
    @Column(name = COL_LASTNAME)
    private String lastname;
    
    @Column(name = COL_PATRONYMIC)
    private String patronymic;
    
    @Column(name = COL_EMAIL)
    private String email;
    
    @Column(name = COL_LOCKED, nullable = false)
    private Boolean locked;
    
    @OneToMany(mappedBy = UserRoleLinkEntity.PROP_USER)
    private Collection<UserRoleLinkEntity> roleLinks;
    
    @OneToMany(mappedBy = PersistentTokenEntity.PROP_USER)
    private Collection<PersistentTokenEntity> persistentTokens;

    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Collection<UserRoleLinkEntity> getRoleLinks() {
        return roleLinks;
    }

    public void setRoleLinks(Collection<UserRoleLinkEntity> roleLinks) {
        this.roleLinks = roleLinks;
    }

    public Collection<PersistentTokenEntity> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Collection<PersistentTokenEntity> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }
}