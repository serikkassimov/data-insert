package kz.worldclass.finances.data.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import static kz.worldclass.finances.data.entity.OutgoingRequestRowEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = @Index(
                name = IND + SEP + TABLE + SEP + COL_REQUEST_ID,
                unique = false,
                columnList = COL_REQUEST_ID
        )
)
@SequenceGenerator(schema = SCHEMA, name = SEQ, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class OutgoingRequestRowEntity extends BaseEntity {
    public static final String TABLE = "OUTGOING_REQUEST_ROW";
    public static final String COL_REQUEST_ID = "REQUEST_ID";
    public static final String COL_BUDGET_ID = "BUDGET_ID";
    public static final String COL_NOTE = "NOTE";
    public static final String COL_ORDER_NUMBER = "ORDER_NUMBER";
    public static final String PROP_REQUEST = "request";
    
    @Column(name = COL_NOTE)
    private String note;
    
    @Column(name = COL_ORDER_NUMBER, nullable = false)
    private Long orderNumber;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_REQUEST_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_REQUEST_ID
    ))
    private OutgoingRequestEntity request;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = COL_BUDGET_ID, nullable = false, foreignKey = @ForeignKey(
            name = FK + SEP + TABLE + SEP + COL_BUDGET_ID
    ))
    private DictBudgetEntity budget;
    
    @OneToMany(mappedBy = OutgoingRequestCellEntity.PROP_ROW)
    private Collection<OutgoingRequestCellEntity> cells;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OutgoingRequestEntity getRequest() {
        return request;
    }

    public void setRequest(OutgoingRequestEntity request) {
        this.request = request;
    }

    public DictBudgetEntity getBudget() {
        return budget;
    }

    public void setBudget(DictBudgetEntity budget) {
        this.budget = budget;
    }

    public Collection<OutgoingRequestCellEntity> getCells() {
        return cells;
    }

    public void setCells(Collection<OutgoingRequestCellEntity> cells) {
        this.cells = cells;
    }
}