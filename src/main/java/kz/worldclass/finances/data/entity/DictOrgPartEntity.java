package kz.worldclass.finances.data.entity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import static kz.worldclass.finances.data.entity.DictOrgPartEntity.*;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE,
        indexes = {
            @Index(
                    name = IND + SEP + TABLE + SEP + COL_CODE,
                    unique = true,
                    columnList = COL_CODE
            )
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = UNQ + SEP + TABLE + SEP + COL_CODE,
                    columnNames = COL_CODE
            )
        }
)
@SequenceGenerator(schema = SCHEMA, name = SEQ, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class DictOrgPartEntity extends BaseDictEntity {
    public static final String TABLE = "DICT_ORG_PART";
}