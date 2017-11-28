package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDao;
import kz.worldclass.finances.data.entity.OutgoingRequestRowEntity;
import org.springframework.stereotype.Repository;

@Repository
public class OutgoingRequestRowDao extends AbstractDao<OutgoingRequestRowEntity> {
    @Override
    protected Class<OutgoingRequestRowEntity> getEntityClass() {
        return OutgoingRequestRowEntity.class;
    }
}