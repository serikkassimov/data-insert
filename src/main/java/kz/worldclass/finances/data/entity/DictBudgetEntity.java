package kz.worldclass.finances.data.entity;

import javax.persistence.*;

import static kz.worldclass.finances.data.entity.DictBudgetEntity.*;
import kz.worldclass.finances.data.entity.base.BaseTreeDictEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_CODE,
                    unique = true,
                    columnList = COL_CODE
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_PARENT_ID,
                    unique = false,
                    columnList = COL_PARENT_ID
            ),
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_OUTGO,
                    unique = false,
                    columnList = COL_OUTGO
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + COL_CODE,
                    columnNames = {COL_CODE}
            )
        }
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class DictBudgetEntity extends BaseTreeDictEntity<DictBudgetEntity> {
    public static final String TABLE = "DICT_BUDGET";
    public static final String COL_OUTGO = "OUTGO";
    
    @Column(name = COL_OUTGO, nullable = false, unique = false, insertable = true, updatable = true)
    private Boolean outgo;

    public Boolean getOutgo() {
        return outgo;
    }

    public void setOutgo(Boolean outgo) {
        this.outgo = outgo;
    }
}