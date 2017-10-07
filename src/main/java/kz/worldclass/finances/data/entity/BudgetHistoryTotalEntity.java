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
import static kz.worldclass.finances.data.entity.BudgetHistoryTotalEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_HISTORY_ID,
                    unique = false,
                    columnList = COL_HISTORY_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_HIS_CUR_ST,
                    unique = true,
                    columnList = COL_HISTORY_ID + SEP_COL + COL_CURRENCY_ID + SEP_COL + COL_STORE_TYPE_ID
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + CONS_HIS_CUR_ST,
                    columnNames = {COL_HISTORY_ID, COL_CURRENCY_ID, COL_STORE_TYPE_ID}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetHistoryTotalEntity extends BaseEntity {
    public static final String TABLE = "BUDGET_HISTORY_TOTAL";
    public static final String COL_HISTORY_ID = "HISTORY_ID";
    public static final String COL_CURRENCY_ID = "CURRENCY_ID";
    public static final String COL_STORE_TYPE_ID = "STORE_TYPE_ID";
    public static final String COL_OLD_VALUE = "OLD_VALUE";
    public static final String COL_NEW_VALUE = "NEW_VALUE";
    public static final String CONS_HIS_CUR_ST = "HIS_CUR_ST";
    
    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_HISTORY_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_HISTORY_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private BudgetHistoryEntity history;
    
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
    
    @Column(name = COL_OLD_VALUE, nullable = false, precision = BUDGET_PRECISION, scale = BUDGET_SCALE)
    private Double oldValue;
    
    @Column(name = COL_NEW_VALUE, nullable = false, precision = BUDGET_PRECISION, scale = BUDGET_SCALE)
    private Double newValue;

    public BudgetHistoryEntity getHistory() {
        return history;
    }

    public void setHistory(BudgetHistoryEntity history) {
        this.history = history;
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

    public Double getOldValue() {
        return oldValue;
    }

    public void setOldValue(Double oldValue) {
        this.oldValue = oldValue;
    }

    public Double getNewValue() {
        return newValue;
    }

    public void setNewValue(Double newValue) {
        this.newValue = newValue;
    }
}