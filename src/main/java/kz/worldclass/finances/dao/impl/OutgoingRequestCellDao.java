package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.OutgoingRequestCellEntity;
import org.springframework.stereotype.Repository;

@Repository
public class OutgoingRequestCellDao extends AbstractDao<OutgoingRequestCellEntity> {
    @Override
    protected Class<OutgoingRequestCellEntity> getEntityClass() {
        return OutgoingRequestCellEntity.class;
    }
}