package kz.worldclass.finances.data.entity;

import kz.worldclass.finances.data.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

import static kz.worldclass.finances.data.entity.BudgetBalanceEntity.*;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_STORE_TYPE_ID,
                    unique = false,
                    columnList = COL_ORG_ID + SEP_COL + COL_STORE_TYPE_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_STORE_TYPE_ID,
                    unique = false,
                    columnList = COL_ORG_ID + SEP_COL + COL_STORE_TYPE_ID + SEP_COL + COL_SAVE_DATETIME
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetBalanceEntity extends BaseEntity {
    public static final String TABLE = "BUDGET_BALANCE";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_STORE_TYPE_ID = "STORE_TYPE_ID";
    public static final String COL_SAVE_DATETIME = "SAVE_DATETIME";
    public static final String COL_NOTE_ID = "NOTE_ID";
    public static final String COL_ON_DATE = "ON_DATE";
    public static final String COL_ITEM_VALUE = "ITEM_VALUE";

    
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
            name = COL_STORE_TYPE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_STORE_TYPE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetStoreTypeEntity storeType;
    
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
    
    @Temporal(TemporalType.DATE)
    @Column(name = COL_ON_DATE, nullable = false)
    private Date onDate;



    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
    }

    public Date getSaveDatetime() {
        return saveDatetime;
    }

    public void setSaveDatetime(Date saveDatetime) {
        this.saveDatetime = saveDatetime;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public NoteEntity getNote() {
        return note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;
    }

    @Column(name = COL_ITEM_VALUE, precision = BUDGET_PRECISION, scale = BUDGET_SCALE)
    private Double itemValue;
    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public DictBudgetStoreTypeEntity getStoreType() {
        return storeType;
    }

    public void setStoreType(DictBudgetStoreTypeEntity storeType) {
        this.storeType = storeType;
    }

}