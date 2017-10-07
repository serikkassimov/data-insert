package kz.worldclass.finances.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import static kz.worldclass.finances.data.entity.NoteEntity.*;
import kz.worldclass.finances.data.entity.base.BaseEntity;

@Entity
@Table(
        schema = SCHEMA, name = TABLE
)
@SequenceGenerator(name = SEQ, schema = SCHEMA, sequenceName = SEQ + SEP + TABLE, allocationSize = ALLOCATION_SIZE_DEFAULT)
@SuppressWarnings("PersistenceUnitPresent")
public class NoteEntity extends BaseEntity {
    public static final String TABLE = "NOTES";
    public static final String COL_NOTE_VALUE = "NOTE_VALUE";
    
    @Lob
    @Column(name = COL_NOTE_VALUE, nullable = false)
    private String noteValue;

    public String getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(String noteValue) {
        this.noteValue = noteValue;
    }
}