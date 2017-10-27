package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.NoteEntity;
import org.springframework.stereotype.Repository;

@Repository
public class NoteDao extends AbstractDao<NoteEntity> {
    @Override
    protected Class<NoteEntity> getEntityClass() {
        return NoteEntity.class;
    }
}