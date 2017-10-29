package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kz.worldclass.finances.dao.impl.BudgetNextChangeDao;
import kz.worldclass.finances.dao.impl.BudgetNextChangeItemDao;
import kz.worldclass.finances.dao.impl.DictBudgetDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeStateDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeTypeDao;
import kz.worldclass.finances.dao.impl.DictBudgetStoreTypeDao;
import kz.worldclass.finances.dao.impl.DictCurrencyDao;
import kz.worldclass.finances.dao.impl.NoteDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.data.dto.entity.BudgetNextChangeDto;
import kz.worldclass.finances.data.dto.entity.BudgetNextChangeItemDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetStoreTypeDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.results.expensesrequest.GetAffiliateDataResult;
import kz.worldclass.finances.data.dto.results.expensesrequest.SaveAffiliateDataResult;
import kz.worldclass.finances.data.entity.BudgetNextChangeEntity;
import kz.worldclass.finances.data.entity.BudgetNextChangeItemEntity;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeStateEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeTypeEntity;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.NoteEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExpensesRequestService {
    private static final String EXPENSES_REQUEST = "EXPENSES_REQUEST";
    private static final String NEW = "NEW";
    
    @Autowired
    private BudgetNextChangeDao budgetNextChangeDao;
    @Autowired
    private BudgetNextChangeItemDao budgetNextChangeItemDao;
    @Autowired
    private DictBudgetDao dictBudgetDao;
    @Autowired
    private DictBudgetNextChangeStateDao dictBudgetNextChangeStateDao;
    @Autowired
    private DictBudgetNextChangeTypeDao dictBudgetNextChangeTypeDao;
    @Autowired
    private DictBudgetStoreTypeDao dictBudgetStoreTypeDao;
    @Autowired
    private DictCurrencyDao dictCurrencyDao;
    @Autowired
    private NoteDao noteDao;
    @Autowired
    private UserDao userDao;
    
    public List<DictBudgetDto> getDictBudgets() {
        List<DictBudgetDto> result = new ArrayList<>();
        for (DictBudgetEntity entity: dictBudgetDao.fetchEnabledOutgoingLeafs()) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictBudgetStoreTypeDto> getDictBudgetStoreTypes() {
        List<DictBudgetStoreTypeDto> result = new ArrayList<>();
        for (DictBudgetStoreTypeEntity entity: dictBudgetStoreTypeDao.filtered(false, null, new String[] {"CASHLESS_TERMINAL"})) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictCurrencyDto> getDictCurrencies() {
        List<DictCurrencyDto> result = new ArrayList<>();
        for (DictCurrencyEntity entity: dictCurrencyDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public GetAffiliateDataResult getAffiliateData(String login, Date date) {
        if ((login == null) || login.isEmpty()) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_LOGIN);
        
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.USER_NOT_FOUND);
        
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.ORG_NOT_FOUND);
        
        DictBudgetNextChangeTypeEntity changeTypeEntity = dictBudgetNextChangeTypeDao.getByCode(EXPENSES_REQUEST);
        if (changeTypeEntity == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.CHANGE_TYPE_NOT_FOUND);
        
        BudgetNextChangeEntity changeEntity = budgetNextChangeDao.fetchOneForOrgTypeDate(orgEntity, changeTypeEntity, date);
        if (changeEntity == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.SUCCESS);
        else {
            Map<Long, List<BudgetNextChangeItemDto>> map = new TreeMap<>();
            for (BudgetNextChangeItemEntity entity: changeEntity.getItems()) {
                BudgetNextChangeItemDto dto = Dtos.less(entity);
                dto.budgetType = Dtos.less(entity.getBudgetType());
                dto.currency = Dtos.less(entity.getCurrency());
                dto.note = Dtos.less(entity.getNote());
                dto.storeType = Dtos.less(entity.getStoreType());
                
                List<BudgetNextChangeItemDto> itemDtos = map.get(dto.budgetType.id);
                if (itemDtos == null) {
                    itemDtos = new ArrayList<>();
                    map.put(dto.budgetType.id, itemDtos);
                }
                itemDtos.add(dto);
            }
            
            List<BudgetNextChangeItemDto> result = new ArrayList<>();
            for (List<BudgetNextChangeItemDto> itemDtos: map.values()) result.addAll(itemDtos);
            
            return new GetAffiliateDataResult(result.toArray(new BudgetNextChangeItemDto[result.size()]));
        }
    }
    
    public SaveAffiliateDataResult saveAffiliateData(String login, BudgetNextChangeDto dto) {
        if ((login == null) || login.isEmpty()) return SaveAffiliateDataResult.NO_LOGIN;
        
        if (dto.changeDate == null) return SaveAffiliateDataResult.NO_DATE;
        Date changeDate = new Date(dto.changeDate);
        
        Map<Long, DictBudgetEntity> budgetMap = new HashMap<>();
        Map<Long, DictCurrencyEntity> currencyMap = new HashMap<>();
        Map<Long, DictBudgetStoreTypeEntity> storeTypeMap = new HashMap<>();
        
        List<BudgetNextChangeItemDto> changeItemDtos = new ArrayList<>();
        if (dto.items != null) {
            for (BudgetNextChangeItemDto itemDto: dto.items) {
                if (itemDto != null) {
                    if ((itemDto.budgetType == null) || (itemDto.budgetType.id == null)) return SaveAffiliateDataResult.NO_BUDGET;
                    if ((itemDto.currency == null) || (itemDto.currency.id == null)) return SaveAffiliateDataResult.NO_CURRENCY;
                    if ((itemDto.storeType == null) || (itemDto.storeType.id == null)) return SaveAffiliateDataResult.NO_STORE_TYPE;
                    if (itemDto.itemValue != null) {
                        if (itemDto.itemValue < 0D) return SaveAffiliateDataResult.NEGATIVE_VALUE;
                        else if (itemDto.itemValue > 0D) {
                            DictBudgetEntity budgetEntity = dictBudgetDao.get(itemDto.budgetType.id);
                            if (budgetEntity == null) return SaveAffiliateDataResult.BUDGET_NOT_FOUND;
                            
                            DictCurrencyEntity currencyEntity = dictCurrencyDao.get(itemDto.currency.id);
                            if (currencyEntity == null) return SaveAffiliateDataResult.CURRENCY_NOT_FOUND;
                            
                            DictBudgetStoreTypeEntity storeTypeEntity = dictBudgetStoreTypeDao.get(itemDto.storeType.id);
                            if (storeTypeEntity == null) return SaveAffiliateDataResult.STORE_TYPE_NOT_FOUND;
                            
                            budgetMap.put(budgetEntity.getId(), budgetEntity);
                            currencyMap.put(currencyEntity.getId(), currencyEntity);
                            storeTypeMap.put(storeTypeEntity.getId(), storeTypeEntity);
                            
                            changeItemDtos.add(itemDto);
                        }
                    }
                }
            }
        }
        
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return SaveAffiliateDataResult.USER_NOT_FOUND;
        
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return SaveAffiliateDataResult.ORG_NOT_FOUND;
        
        DictBudgetNextChangeTypeEntity changeTypeEntity = dictBudgetNextChangeTypeDao.getByCode(EXPENSES_REQUEST);
        if (changeTypeEntity == null) return SaveAffiliateDataResult.CHANGE_TYPE_NOT_FOUND;
        
        DictBudgetNextChangeStateEntity stateEntity = dictBudgetNextChangeStateDao.getByCode(NEW);
        if (stateEntity == null) return SaveAffiliateDataResult.STATE_NOT_FOUND;
        
        BudgetNextChangeEntity changeEntity = budgetNextChangeDao.fetchOneForOrgTypeDate(orgEntity, changeTypeEntity, changeDate);
        if (changeEntity == null) {
            changeEntity = new BudgetNextChangeEntity();
            changeEntity.setChangeDate(changeDate);
            changeEntity.setOrg(orgEntity);
            changeEntity.setState(stateEntity);
            changeEntity.setType(changeTypeEntity);
            budgetNextChangeDao.save(changeEntity);
        }
        
        Map<Long, BudgetNextChangeItemEntity> redundantItemMap = new HashMap<>();
        if (changeEntity.getItems() != null) {
            for (BudgetNextChangeItemEntity changeItemEntity: changeEntity.getItems()) redundantItemMap.put(changeItemEntity.getId(), changeItemEntity);
        }
        
        List<BudgetNextChangeItemEntity> savedChangeItemEntities = new ArrayList<>();
        for (BudgetNextChangeItemDto changeItemDto: changeItemDtos) {
            BudgetNextChangeItemEntity changeItemEntity = null;
            for (BudgetNextChangeItemEntity existentChangeItemEntity: redundantItemMap.values()) {
                if (
                        existentChangeItemEntity.getBudgetType().getId().equals(changeItemDto.budgetType.id)
                        && existentChangeItemEntity.getCurrency().getId().equals(changeItemDto.currency.id)
                        && existentChangeItemEntity.getStoreType().getId().equals(changeItemDto.storeType.id)
                ) {
                    changeItemEntity = existentChangeItemEntity;
                    break;
                }
            }
            
            if (changeItemEntity == null) {
                changeItemEntity = new BudgetNextChangeItemEntity();
                changeItemEntity.setChange(changeEntity);
            } else {
                redundantItemMap.remove(changeItemEntity.getId());
            }
            
            changeItemEntity.setBudgetType(budgetMap.get(changeItemDto.budgetType.id));
            changeItemEntity.setCurrency(currencyMap.get(changeItemDto.currency.id));
            changeItemEntity.setStoreType(storeTypeMap.get(changeItemDto.storeType.id));
            changeItemEntity.setItemValue(changeItemDto.itemValue);
            
            NoteEntity noteEntity = changeItemEntity.getNote();
            
            if ((changeItemDto.note != null) && (changeItemDto.note.noteValue != null) && (!changeItemDto.note.noteValue.isEmpty())) {
                if (noteEntity == null) noteEntity = new NoteEntity();
                noteEntity.setNoteValue(changeItemDto.note.noteValue);
                noteDao.save(noteEntity);
                changeItemEntity.setNote(noteEntity);
            } else {
                changeItemEntity.setNote(null);
                noteDao.delete(noteEntity);
            }
            
            savedChangeItemEntities.add(changeItemEntity);
        }
        
        for (BudgetNextChangeItemEntity changeItemEntity: redundantItemMap.values()) {
            NoteEntity noteEntity = changeItemEntity.getNote();
            if (noteEntity != null) noteDao.delete(noteEntity);
            budgetNextChangeItemDao.delete(changeItemEntity);
            System.out.println("deleted " + changeItemEntity.getId());
        }
        
        for (BudgetNextChangeItemEntity changeItemEntity: savedChangeItemEntities) {
            if (changeItemEntity.getNote() != null) noteDao.save(changeItemEntity.getNote());
            budgetNextChangeItemDao.save(changeItemEntity);
        }
        
        return SaveAffiliateDataResult.SUCCESS;
    }
}