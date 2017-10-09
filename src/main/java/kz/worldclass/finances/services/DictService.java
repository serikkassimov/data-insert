package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.List;
import kz.worldclass.finances.dao.AbstractDictDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeStateDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeTypeDao;
import kz.worldclass.finances.dao.impl.DictBudgetStoreTypeDao;
import kz.worldclass.finances.dao.impl.DictOrgDao;
import kz.worldclass.finances.dao.impl.DictRoleDao;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.DisableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.EnableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.SaveBaseResult;
import kz.worldclass.finances.data.entity.base.BaseDictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DictService {
    private static final String BUDGET_NEXT_CHANGE_STATES = "budgetNextChangeStates";
    private static final String BUDGET_NEXT_CHANGE_TYPES = "budgetNextChangeTypes";
    private static final String BUDGET_STORE_TYPES = "budgetStoreTypes";
    private static final String ORGS = "orgs";
    private static final String ROLES = "roles";
    
    @Autowired
    private DictBudgetNextChangeStateDao dictBudgetNextChangeStateDao;
    @Autowired
    private DictBudgetNextChangeTypeDao dictBudgetNextChangeTypeDao;
    @Autowired
    private DictBudgetStoreTypeDao dictBudgetStoreTypeDao;
    @Autowired
    private DictRoleDao dictRoleDao;
    @Autowired
    private DictOrgDao dictOrgDao;
    
    private AbstractDictDao getDao(String dictType) {
        switch (dictType) {
            case BUDGET_NEXT_CHANGE_STATES:
                return dictBudgetNextChangeStateDao;
            case BUDGET_NEXT_CHANGE_TYPES:
                return dictBudgetNextChangeTypeDao;
            case BUDGET_STORE_TYPES:
                return dictBudgetStoreTypeDao;
            case ORGS:
                return dictOrgDao;
            case ROLES:
                return dictRoleDao;
            default:
                throw new IllegalArgumentException(String.format("unknown dict type \"%s\"", dictType));
        }
    }
    
    public List<BaseDictDto> getBaseList(String dictType) {
        AbstractDictDao dao = getDao(dictType);
        
        List<BaseDictDto> result = new ArrayList<>();
        for (BaseDictEntity entity: (List<BaseDictEntity>) dao.all()) {
            BaseDictDto dto = new BaseDictDto();
            Dtos.copy(entity, dto);
            result.add(dto);
        }
        return result;
    }
    
    public SaveBaseResult saveBase(String dictType, BaseDictDto dto) {
        AbstractDictDao dao = getDao(dictType);
        
        BaseDictEntity entity;
        if (dto.id == null) entity = (BaseDictEntity) dao.newEntity();
        else {
            entity = (BaseDictEntity) dao.get(dto.id);
            if (entity == null) return SaveBaseResult.NOT_FOUND;
        }
        
        entity.setCode(dto.code);
        entity.setName(dto.name);
        entity.setDisabled(dto.disabled);
        
        dao.save(entity);
        
        return SaveBaseResult.SUCCESS;
    }
    
    public EnableBaseResult enableBase(String dictType, Long id) {
        AbstractDictDao dao = getDao(dictType);
        
        BaseDictEntity entity = (BaseDictEntity) dao.get(id);
        if (entity == null) return EnableBaseResult.NOT_FOUND;
        
        entity.setDisabled(false);
        dao.save(entity);
        
        return EnableBaseResult.SUCCESS;
    }
    
    public DisableBaseResult disableBase(String dictType, Long id) {
        AbstractDictDao dao = getDao(dictType);
        
        BaseDictEntity entity = (BaseDictEntity) dao.get(id);
        if (entity == null) return DisableBaseResult.NOT_FOUND;
        
        entity.setDisabled(true);
        dao.save(entity);
        
        return DisableBaseResult.SUCCESS;
    }
}