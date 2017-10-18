package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import kz.worldclass.finances.dao.impl.BudgetNextChangeDao;
import kz.worldclass.finances.dao.impl.BudgetNextChangeItemDao;
import kz.worldclass.finances.dao.impl.DictBudgetDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeStateDao;
import kz.worldclass.finances.dao.impl.DictBudgetNextChangeTypeDao;
import kz.worldclass.finances.dao.impl.DictBudgetStoreTypeDao;
import kz.worldclass.finances.dao.impl.DictCurrencyDao;
import kz.worldclass.finances.dao.impl.DictOrgDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.data.dto.entity.BudgetNextChangeDto;
import kz.worldclass.finances.data.dto.entity.BudgetNextChangeItemDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateGetDataResult;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateSaveDataResult;
import kz.worldclass.finances.data.entity.BudgetNextChangeEntity;
import kz.worldclass.finances.data.entity.BudgetNextChangeItemEntity;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeStateEntity;
import kz.worldclass.finances.data.entity.DictBudgetNextChangeTypeEntity;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CashReportService {
    private static final String CASH_REPORT = "CASH_REPORT";
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
    private DictOrgDao dictOrgDao;
    @Autowired
    private UserDao userDao;
    
    /**
     * 
     * @param storeTypeEntity
     * @param budgetEntity
     * @param currencyEntity
     * @return 
     * @throws NullPointerException one of <code>[storeTypeEntity, budgetEntity, currencyEntity]</code> is null
     */
    private static String dataKey(DictBudgetStoreTypeEntity storeTypeEntity, DictBudgetEntity budgetEntity, DictCurrencyEntity currencyEntity) {
        Objects.requireNonNull(storeTypeEntity, "store type is null");
        Objects.requireNonNull(budgetEntity, "budget is null");
        Objects.requireNonNull(currencyEntity, "currency is null");
        
        return new StringBuilder()
                .append(storeTypeEntity.getId())
                .append('-')
                .append(budgetEntity.getId())
                .append('-')
                .append(currencyEntity.getId())
                .toString();
    }
    
    public List<BaseDictDto> getDictBudgetStoreTypes() {
        List<BaseDictDto> result = new ArrayList<>();
        for (DictBudgetStoreTypeEntity entity: dictBudgetStoreTypeDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictBudgetDto> getDictBudgets() {
        List<DictBudgetDto> result = new ArrayList<>();
        for (DictBudgetEntity entity: dictBudgetDao.fetchEnabledIncomingLeafs()) result.add(Dtos.less(entity));
        return result;
    }
    
    public List<DictCurrencyDto> getDictCurrencies() {
        List<DictCurrencyDto> result = new ArrayList<>();
        for (DictCurrencyEntity entity: dictCurrencyDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public AffiliateGetDataResult getAffiliateData(String login) {
        if ((login == null) || login.isEmpty()) return new AffiliateGetDataResult(AffiliateGetDataResult.Type.NO_LOGIN);
        
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return new AffiliateGetDataResult(AffiliateGetDataResult.Type.USER_NOT_FOUND);
        
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return new AffiliateGetDataResult(AffiliateGetDataResult.Type.ORG_NOT_FOUND);
        
        DictBudgetNextChangeTypeEntity changeTypeEntity = dictBudgetNextChangeTypeDao.getByCode(CASH_REPORT);
        if (changeTypeEntity == null) return new AffiliateGetDataResult(AffiliateGetDataResult.Type.CHANGE_TYPE_NOT_FOUND);
        
        BudgetNextChangeEntity changeEntity = budgetNextChangeDao.fetchOneForOrgAndType(orgEntity, changeTypeEntity);
        if (changeEntity == null) return new AffiliateGetDataResult(AffiliateGetDataResult.Type.SUCCESS);
        
        BudgetNextChangeDto changeDto = Dtos.complete(changeEntity);
        
        List<BudgetNextChangeItemDto> changeItemDtos = new ArrayList<>();
        for (BudgetNextChangeItemEntity changeItemEntity: changeEntity.getItems()) changeItemDtos.add(Dtos.complete(changeItemEntity));
        changeDto.items = changeItemDtos.toArray(new BudgetNextChangeItemDto[changeItemDtos.size()]);
        
        return new AffiliateGetDataResult(changeDto);
    }
    
    public AffiliateSaveDataResult saveAffiliateData(String login, BudgetNextChangeDto dto) {
        if ((login == null) || login.isEmpty()) return AffiliateSaveDataResult.NO_LOGIN;
        
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return AffiliateSaveDataResult.USER_NOT_FOUND;
        
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return AffiliateSaveDataResult.ORG_NOT_FOUND;
        
        DictBudgetNextChangeTypeEntity changeTypeEntity = dictBudgetNextChangeTypeDao.getByCode(CASH_REPORT);
        if (changeTypeEntity == null) return AffiliateSaveDataResult.CHANGE_TYPE_NOT_FOUND;
        
        Map<Long, DictBudgetEntity> dictBudgetMap = new HashMap<>();
        Map<Long, DictCurrencyEntity> dictCurrencyMap = new HashMap<>();
        Map<Long, DictBudgetStoreTypeEntity> dictBudgetStoreTypeMap = new HashMap<>();
        Map<String, BudgetNextChangeItemDto> itemDtoMap = new HashMap<>();
        
        if (dto.items != null) {
            for (BudgetNextChangeItemDto itemDto: dto.items) {
                if (itemDto == null) return AffiliateSaveDataResult.NULL_ITEM;
                if (itemDto.itemValue == null) return AffiliateSaveDataResult.NO_VALUE;
                if (itemDto.itemValue < 0) return AffiliateSaveDataResult.NEGATIVE_VALUE;
                
                if ((itemDto.budgetType == null) || (itemDto.budgetType.id == null)) return AffiliateSaveDataResult.NO_BUDGET_TYPE;
                DictBudgetEntity dictBudgetEntity = dictBudgetDao.get(itemDto.budgetType.id);
                if (dictBudgetEntity == null) return AffiliateSaveDataResult.BUDGET_TYPE_NOT_FOUND;
                dictBudgetMap.put(dictBudgetEntity.getId(), dictBudgetEntity);
                
                if ((itemDto.currency == null) || (itemDto.currency.id == null)) return AffiliateSaveDataResult.NO_CURRENCY;
                DictCurrencyEntity dictCurrencyEntity = dictCurrencyDao.get(itemDto.currency.id);
                if (dictCurrencyEntity == null) return AffiliateSaveDataResult.CURRENCY_NOT_FOUND;
                dictCurrencyMap.put(dictCurrencyEntity.getId(), dictCurrencyEntity);
                
                if ((itemDto.storeType == null) || (itemDto.storeType.id == null)) return AffiliateSaveDataResult.NO_STORE_TYPE;
                DictBudgetStoreTypeEntity dictBudgetStoreTypeEntity = dictBudgetStoreTypeDao.get(itemDto.storeType.id);
                if (dictBudgetStoreTypeEntity == null) return AffiliateSaveDataResult.STORE_TYPE_NOT_FOUND;
                dictBudgetStoreTypeMap.put(dictBudgetStoreTypeEntity.getId(), dictBudgetStoreTypeEntity);
                
                itemDtoMap.put(dataKey(dictBudgetStoreTypeEntity, dictBudgetEntity, dictCurrencyEntity), itemDto);
            }
        }
        
        DictBudgetNextChangeStateEntity stateEntity = dictBudgetNextChangeStateDao.getByCode(NEW);
        if (stateEntity == null) return AffiliateSaveDataResult.STATE_NOT_FOUND;
        
        BudgetNextChangeEntity changeEntity = budgetNextChangeDao.fetchOneForOrgAndType(orgEntity, changeTypeEntity);
        boolean newChange = (changeEntity == null);
        if (newChange) {
            changeEntity = new BudgetNextChangeEntity();
            changeEntity.setOrg(orgEntity);
            changeEntity.setType(changeTypeEntity);
        }
        changeEntity.setState(stateEntity);
        budgetNextChangeDao.save(changeEntity);
        
        Set<BudgetNextChangeItemEntity> deletedItemSet = new HashSet<>();
        if (!newChange) {
            for (BudgetNextChangeItemEntity itemEntity: changeEntity.getItems()) {
                String key = dataKey(itemEntity.getStoreType(), itemEntity.getBudgetType(), itemEntity.getCurrency());
                BudgetNextChangeItemDto itemDto = itemDtoMap.remove(key);
                
                if (itemDto == null) deletedItemSet.add(itemEntity);
                else {
                    itemEntity.setItemValue(itemDto.itemValue);
                    budgetNextChangeItemDao.save(itemEntity);
                }
            }
        }
        
        for (BudgetNextChangeItemDto itemDto: itemDtoMap.values()) {
            BudgetNextChangeItemEntity itemEntity = new BudgetNextChangeItemEntity();
            itemEntity.setChange(changeEntity);
            itemEntity.setBudgetType(dictBudgetMap.get(itemDto.budgetType.id));
            itemEntity.setCurrency(dictCurrencyMap.get(itemDto.currency.id));
            itemEntity.setStoreType(dictBudgetStoreTypeMap.get(itemDto.storeType.id));
            itemEntity.setItemValue(itemDto.itemValue);
            budgetNextChangeItemDao.save(itemEntity);
        }
        
        for (BudgetNextChangeItemEntity itemEntity: deletedItemSet) budgetNextChangeItemDao.delete(itemEntity);
        
        return AffiliateSaveDataResult.SUCCESS;
    }
}