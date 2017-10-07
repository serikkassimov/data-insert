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
import static kz.worldclass.finances.data.entity.BudgetEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + CONS_ORG_CURR_STORE,
                    unique = true,
                    columnList = COL_ORG_ID + SEP_COL + COL_CURRENCY_ID + SEP_COL + COL_STORE_TYPE_ID
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + CONS_ORG_CURR_STORE,
                    columnNames = {COL_ORG_ID, COL_CURRENCY_ID, COL_STORE_TYPE_ID}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class BudgetEntity extends BaseEntity {
    public static final String TABLE = "BUDGET";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_CURRENCY_ID = "CURRENCY_ID";
    public static final String COL_STORE_TYPE_ID = "STORE_TYPE_ID";
    public static final String COL_CURRENT_VALUE = "CURRENT_VALUE";
    public static final String CONS_ORG_CURR_STORE = "ORG_CURR_STORE";
    
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
    
    @Column(name = COL_CURRENT_VALUE, nullable = false, precision = BUDGET_PRECISION, scale = BUDGET_SCALE)
    private Double currentValue;

    public DictOrgEntity getOrg() {
        return org;
    }

    public void setOrg(DictOrgEntity org) {
        this.org = org;
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

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }
}