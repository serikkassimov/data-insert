package kz.worldclass.finances.data.entity.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseDictEntity extends BaseEntity {
    public static final String COL_CODE = "CODE";
    public static final String COL_NAME = "NAME";
    public static final String PROP_CODE = "code";
    
    @Column(name = COL_CODE, nullable = false, unique = true, insertable = true, updatable = false, length = 50)
    private String code;
    
    @Column(name = COL_NAME)
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}