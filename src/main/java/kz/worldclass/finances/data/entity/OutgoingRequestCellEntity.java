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
import static kz.worldclass.finances.data.entity.OutgoingRequestCellEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_ROW_ID,
                    unique = false,
                    columnList = COL_ROW_ID
            )
        }
)
@SequenceGenerator(schema = SCHEMA, name = SEQ, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class OutgoingRequestCellEntity extends BaseEntity {
    public static final String TABLE = "OUTGOING_REQUEST_CELL";
    public static final String COL_ROW_ID = "ROW_ID";
    public static final String COL_STORE_TYPE_ID = "STORE_TYPE_ID";
    public static final String COL_CURRENCY_ID = "CURRENCY_ID";
    public static final String COL_ORG_PART_ID = "ORG_PART_ID";
    public static final String COL_VALUE = "C_VALUE";
    public static final String PROP_ROW = "row";
    
    @Column(name = COL_VALUE, precision = BUDGET_PRECISION, scale = BUDGET_SCALE, nullable = false)
    private Double value;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_ROW_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_ROW_ID, value = ConstraintMode.CONSTRAINT
    ))
    private OutgoingRequestRowEntity row;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_STORE_TYPE_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_STORE_TYPE_ID, value = ConstraintMode.CONSTRAINT
    ))
    private DictBudgetStoreTypeEntity storeType;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_CURRENCY_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_CURRENCY_ID, value = ConstraintMode.CONSTRAINT
    ))
    private DictCurrencyEntity currency;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_ORG_PART_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_ORG_PART_ID, value = ConstraintMode.CONSTRAINT
    ))
    private DictOrgPartEntity orgPart;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public OutgoingRequestRowEntity getRow() {
        return row;
    }

    public void setRow(OutgoingRequestRowEntity row) {
        this.row = row;
    }

    public DictBudgetStoreTypeEntity getStoreType() {
        return storeType;
    }

    public void setStoreType(DictBudgetStoreTypeEntity storeType) {
        this.storeType = storeType;
    }

    public DictCurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(DictCurrencyEntity currency) {
        this.currency = currency;
    }

    public DictOrgPartEntity getOrgPart() {
        return orgPart;
    }

    public void setOrgPart(DictOrgPartEntity orgPart) {
        this.orgPart = orgPart;
    }
}