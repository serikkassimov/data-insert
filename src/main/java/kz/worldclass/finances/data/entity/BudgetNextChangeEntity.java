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
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.BudgetNextChangeEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_ORG_ID,
                    unique = false,
                    columnList = COL_ORG_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_ORG_TYPE_DATE,
                    unique = true,
                    columnList = COL_ORG_ID + SEP_COL + COL_TYPE_ID + SEP_COL + COL_CHANGE_DATE
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + CONS_ORG_TYPE_DATE,
                    columnNames = {COL_ORG_ID, COL_TYPE_ID, COL_CHANGE_DATE}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetNextChangeEntity extends BaseEntity {
    public static final String TABLE = "BUDGET_NEXT_CHANGE";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_TYPE_ID = "TYPE_ID";
    public static final String COL_STATE_ID = "STATE_ID";
    public static final String COL_NOTE_ID = "NOTE_ID";
    public static final String COL_CHANGE_DATE = "CHANGE_DATE";
    public static final String CONS_ORG_TYPE_DATE = "ORG_TYPE_DATE";
    
    @Temporal(TemporalType.DATE)
    @Column(name = COL_CHANGE_DATE, nullable = false)
    private Date changeDate;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_ORG_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_ORG_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictOrgEntity org;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_TYPE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_TYPE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetNextChangeTypeEntity type;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_STATE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_STATE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetNextChangeStateEntity state;
    
    @ManyToOne
    @JoinColumn(
            name = COL_NOTE_ID,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_NOTE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private NoteEntity note;
    
    @OneToMany(mappedBy = BudgetNextChangeItemEntity.PROP_CHANGE)
    private Collection<BudgetNextChangeItemEntity> items;

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
    }

    public DictBudgetNextChangeTypeEntity getType() {
        return type;
    }

    public void setType(DictBudgetNextChangeTypeEntity type) {
        this.type = type;
    }

    public DictBudgetNextChangeStateEntity getState() {
        return state;
    }

    public void setState(DictBudgetNextChangeStateEntity state) {
        this.state = state;
    }

    public NoteEntity getNote() {
        return note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;
    }

    public Collection<BudgetNextChangeItemEntity> getItems() {
        return items;
    }

    public void setItems(Collection<BudgetNextChangeItemEntity> items) {
        this.items = items;
    }
}