package kz.worldclass.finances.data.entity.base;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity implements Serializable {
    public static final String SEQ = "SEQ";
    public static final String SEP = "_";
    public static final String SEP_COL = ",";
    public static final String SCHEMA = "worlddata";
    public static final String COL_ID = "ID";
    public static final String IND = "IND";
    public static final String UNQ = "UNQ";
    public static final String FK = "FK";
    public static final int ALLOCATION_SIZE_DEFAULT = 10;
    public static final int BUDGET_PRECISION = 14;
    public static final int BUDGET_SCALE = 2;
    
    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.AUTO)
    @Column(name = COL_ID)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
