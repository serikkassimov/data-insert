package kz.worldclass.finances.data.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.PersistentTokenEntity.*;
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
                    name = IND + SEP + TABLE + SEP + COL_SERIES,
                    unique = true,
                    columnList = COL_SERIES
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + COL_SERIES,
                    columnNames = {COL_SERIES}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class PersistentTokenEntity extends BaseEntity {
    public static final String TABLE = "PERSISTENT_TOKEN";
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_SERIES = "SERIES";
    public static final String COL_TOKEN_VALUE = "TOKEN_VALUE";
    public static final String COL_TOKEN_DATE = "TOKEN_DATE";
    public static final String COL_REMEMBERED = "REMEMBERED";
    public static final String PROP_USER = "user";
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_USER_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_USER_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private UserEntity user;
    
    @Column(name = COL_SERIES, length = 50, nullable = false, unique = true)
    private String series;
    
    @Column(name = COL_TOKEN_VALUE, length = 50)
    private String tokenValue;
    
    @Column(name = COL_TOKEN_DATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenDate;
    
    @Column(name = COL_REMEMBERED, nullable = false)
    private Boolean remembered;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }

    public Boolean getRemembered() {
        return remembered;
    }

    public void setRemembered(Boolean remembered) {
        this.remembered = remembered;
    }
}