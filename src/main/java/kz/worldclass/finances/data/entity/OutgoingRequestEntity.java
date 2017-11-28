package kz.worldclass.finances.data.entity;

import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import static kz.worldclass.finances.data.entity.OutgoingRequestEntity.*;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_ORG_ID + SEP + COL_DATE,
                    unique = true,
                    columnList = COL_ORG_ID + SEP_COL + COL_DATE
            )
        }
)
@SequenceGenerator(schema = SCHEMA, name = SEQ, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class OutgoingRequestEntity extends BaseEntity {
    public static final String TABLE = "OUTGOING_REQUEST";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_DATE = "C_DATE";
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_ORG_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_ORG_ID, value = ConstraintMode.CONSTRAINT
    ))
    private DictOrgEntity org;
    
    @Temporal(TemporalType.DATE)
    @Column(name = COL_DATE, nullable = false)
    private Date date;
    
    @OneToMany(mappedBy = OutgoingRequestRowEntity.PROP_REQUEST)
    private Collection<OutgoingRequestRowEntity> rows;

    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<OutgoingRequestRowEntity> getRows() {
        return rows;
    }

    public void setRows(Collection<OutgoingRequestRowEntity> rows) {
        this.rows = rows;
    }
}