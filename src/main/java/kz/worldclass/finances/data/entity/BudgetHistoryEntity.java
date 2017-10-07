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
import static kz.worldclass.finances.data.entity.BudgetHistoryEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_ORG_TYPE,
                    unique = false,
                    columnList = COL_ORG_ID + SEP_COL + COL_TYPE_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_ORG_TYPE_SAVE,
                    unique = false,
                    columnList = COL_ORG_ID + SEP_COL + COL_TYPE_ID + SEP_COL + COL_SAVE_DATETIME
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetHistoryEntity extends BaseEntity {
    public static final String TABLE = "BUDGET_HISTORY";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_TYPE_ID = "TYPE_ID";
    public static final String COL_SAVE_DATETIME = "SAVE_DATETIME";
    public static final String COL_NOTE_ID = "NOTE_ID";
    public static final String CONS_ORG_TYPE = "ORG_TYPE";
    public static final String CONS_ORG_TYPE_SAVE = "ORG_TYPE_SAVE";
    
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
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = COL_SAVE_DATETIME, nullable = false)
    private Date saveDatetime;
    
    @ManyToOne
    @JoinColumn(
            name = COL_NOTE_ID,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_NOTE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private NoteEntity note;

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

    public Date getSaveDatetime() {
        return saveDatetime;
    }

    public void setSaveDatetime(Date saveDatetime) {
        this.saveDatetime = saveDatetime;
    }

    public NoteEntity getNote() {
        return note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;
    }
}