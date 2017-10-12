package kz.worldclass.finances.dao.impl;

import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import org.springframework.stereotype.Repository;

@Repository
public class DictCurrencyDao extends AbstractDictDao<DictCurrencyEntity> {
    @Override
    protected Class<DictCurrencyEntity> getEntityClass() {
        return DictCurrencyEntity.class;
    }
}