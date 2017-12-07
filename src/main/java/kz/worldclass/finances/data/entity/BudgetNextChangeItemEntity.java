package kz.worldclass.finances.data.entity;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.BudgetNextChangeItemEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_CHANGE_ID,
                    unique = false,
                    columnList = COL_CHANGE_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_CH_CUR_ST_BUD,
                    unique = true,
                    columnList = COL_CHANGE_ID + SEP_COL + COL_CURRENCY_ID + SEP_COL + COL_STORE_TYPE_ID + SEP_COL + COL_BUDGET_TYPE_ID
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + CONS_CH_CUR_ST_BUD,
                    columnNames = {COL_CHANGE_ID, COL_CURRENCY_ID, COL_STORE_TYPE_ID, COL_BUDGET_TYPE_ID}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetNextChangeItemEntity extends BaseEntity {
    public static final String TABLE = "BUDGET_NEXT_CHANGE_ITEM";
    public static final String COL_CHANGE_ID = "CHANGE_ID";
    public static final String COL_CURRENCY_ID = "CURRENCY_ID";
    public static final String COL_STORE_TYPE_ID = "STORE_TYPE_ID";
    public static final String COL_BUDGET_TYPE_ID = "BUDGET_TYPE_ID";
    public static final String COL_SUB_BUDGET_TYPE_ID = "SUB_BUDGET_TYPE_ID";
    public static final String COL_ITEM_VALUE = "ITEM_VALUE";
    public static final String COL_NOTE_ID = "NOTE_ID";
    public static final String PROP_CHANGE = "change";
    public static final String CONS_CH_CUR_ST_BUD = "CH_CUR_ST_BUD";
    public static final String COL_ORG_ID = "ORG_ID";
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_CHANGE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_CHANGE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private BudgetNextChangeEntity change;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_CURRENCY_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_CURRENCY_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictCurrencyEntity currency;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_STORE_TYPE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_STORE_TYPE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetStoreTypeEntity storeType;
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_BUDGET_TYPE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_BUDGET_TYPE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetEntity budgetType;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_SUB_BUDGET_TYPE_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_SUB_BUDGET_TYPE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictBudgetEntity subBudgetType;

    @Column(name = COL_ITEM_VALUE, nullable = false, precision = BUDGET_PRECISION, scale = BUDGET_SCALE)
    private Double itemValue;
    
    @ManyToOne
    @JoinColumn(
            name = COL_NOTE_ID,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_NOTE_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private NoteEntity note;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_ORG_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_ORG_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictOrgEntity org;

    public BudgetNextChangeEntity getChange() {
        return change;
    }

    public void setChange(BudgetNextChangeEntity change) {
        this.change = change;
    }

    public DictCurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(DictCurrencyEntity currency) {
        this.currency = currency;
    }

    public DictBudgetStoreTypeEntity getStoreType() {
        return storeType;
    }

    public void setStoreType(DictBudgetStoreTypeEntity storeType) {
        this.storeType = storeType;
    }

    public DictBudgetEntity getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(DictBudgetEntity budgetType) {
        this.budgetType = budgetType;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public NoteEntity getNote() {
        return note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;
    }

    public DictBudgetEntity getSubBudgetType() {
        return subBudgetType;
    }

    public void setSubBudgetType(DictBudgetEntity subBudgetType) {
        this.subBudgetType = subBudgetType;
    }

    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
    }

}