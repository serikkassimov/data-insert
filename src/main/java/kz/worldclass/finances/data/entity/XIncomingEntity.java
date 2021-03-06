package kz.worldclass.finances.data.entity;

import java.util.Date;
import javax.persistence.*;

import static kz.worldclass.finances.data.entity.XIncomingEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(schema = SCHEMA, name = TABLE)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class XIncomingEntity extends BaseEntity {
    public static final String TABLE = "X_INCOMING";
    public static final String COL_ORG_ID = "ORG_ID";
    public static final String COL_TRANS_ORG_ID = "TRANS_ORG_ID";
    public static final String COL_DATE = "C_DATE";
    public static final String COL_NOTE = "NOTE";
    public static final String COL_VALUE = "C_VALUE";
    public static final String COL_ORDER_NUMBER = "ORDER_NUMBER";
    public static final String COL_CASH = "CASH";
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_ORG_ID, nullable = false, foreignKey = @ForeignKey(name = FK + SEP + TABLE + SEP + COL_ORG_ID))
    private DictOrgEntity org;
    
    @Temporal(TemporalType.DATE)
    @Column(name = COL_DATE, nullable = false)
    private Date date;
    
    @Column(name = COL_NOTE)
    private String note;
    
    @Column(name = COL_VALUE, nullable = false)
    private Double value;
    
    @Column(name = COL_ORDER_NUMBER, nullable = false)
    private Long orderNumber;
    
    @Column(name = COL_CASH, nullable = false)
    private Boolean cash;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = COL_TRANS_ORG_ID, nullable = false,
            foreignKey = @ForeignKey(
                    name = FK + SEP + TABLE + SEP + COL_TRANS_ORG_ID,
                    value = ConstraintMode.CONSTRAINT
            )
    )
    private DictOrgEntity transOrg;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Boolean getCash() {
        return cash;
    }

    public void setCash(Boolean cash) {
        this.cash = cash;
    }

    public DictOrgEntity getTransOrg() {
        return transOrg;
    }

    public void setTransOrg(DictOrgEntity transOrg) {
        this.transOrg = transOrg;
    }



}