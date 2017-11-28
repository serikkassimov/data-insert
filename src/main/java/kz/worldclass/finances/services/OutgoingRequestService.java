package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kz.worldclass.finances.dao.impl.DictBudgetDao;
import kz.worldclass.finances.dao.impl.DictBudgetStoreTypeDao;
import kz.worldclass.finances.dao.impl.DictCurrencyDao;
import kz.worldclass.finances.dao.impl.DictOrgPartDao;
import kz.worldclass.finances.dao.impl.OutgoingRequestCellDao;
import kz.worldclass.finances.dao.impl.OutgoingRequestDao;
import kz.worldclass.finances.dao.impl.OutgoingRequestRowDao;
import kz.worldclass.finances.dao.impl.UserDao;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetStoreTypeDto;
import kz.worldclass.finances.data.dto.entity.DictOrgPartDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.dto.entity.OutgoingRequestCellDto;
import kz.worldclass.finances.data.dto.entity.OutgoingRequestDto;
import kz.worldclass.finances.data.dto.entity.OutgoingRequestRowDto;
import kz.worldclass.finances.data.dto.results.outgoingrequest.GetAffiliateDataResult;
import kz.worldclass.finances.data.dto.results.outgoingrequest.SaveAffiliateDataResult;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
import kz.worldclass.finances.data.entity.DictBudgetStoreTypeEntity;
import kz.worldclass.finances.data.entity.DictCurrencyEntity;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.data.entity.DictOrgPartEntity;
import kz.worldclass.finances.data.entity.OutgoingRequestCellEntity;
import kz.worldclass.finances.data.entity.OutgoingRequestEntity;
import kz.worldclass.finances.data.entity.OutgoingRequestRowEntity;
import kz.worldclass.finances.data.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OutgoingRequestService {
    private static final String KZT = "KZT";
    
    @Autowired
    private DictBudgetDao dictBudgetDao;
    @Autowired
    private DictBudgetStoreTypeDao dictBudgetStoreTypeDao;
    @Autowired
    private DictCurrencyDao dictCurrencyDao;
    @Autowired
    private DictOrgPartDao dictOrgPartDao;
    @Autowired
    private OutgoingRequestDao outgoingRequestDao;
    @Autowired
    private OutgoingRequestCellDao outgoingRequestCellDao;
    @Autowired
    private OutgoingRequestRowDao outgoingRequestRowDao;
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
    
    public List<DictOrgPartDto> getDictOrgParts() {
        List<DictOrgPartDto> result = new ArrayList<>();
        for (DictOrgPartEntity entity: dictOrgPartDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }
    
    public GetAffiliateDataResult getDataResult(String login, Date date) {
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_USER);
        
        DictOrgEntity userOrg = userEntity.getOrg();
        if (userOrg == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_ORG);
        
        List<OutgoingRequestRowDto> result = new ArrayList<>();
        OutgoingRequestEntity outgoingRequestEntity = outgoingRequestDao.fetchOneByOrgAndDate(userOrg, date);
        if (outgoingRequestEntity != null) {
            for (OutgoingRequestRowEntity rowEntity: outgoingRequestEntity.getRows()) {
                OutgoingRequestRowDto rowDto = Dtos.less(rowEntity);
                result.add(rowDto);
                rowDto.budget = Dtos.less(rowEntity.getBudget());
                
                List<OutgoingRequestCellDto> cellDtos = new ArrayList<>();
                for (OutgoingRequestCellEntity cellEntity: rowEntity.getCells()) {
                    OutgoingRequestCellDto cellDto = Dtos.less(cellEntity);
                    cellDtos.add(cellDto);
                    cellDto.orgPart = Dtos.less(cellEntity.getOrgPart());
                    cellDto.storeType = Dtos.less(cellEntity.getStoreType());
                }
                rowDto.cells = cellDtos.toArray(new OutgoingRequestCellDto[cellDtos.size()]);
            }
        }
        return new GetAffiliateDataResult(result.toArray(new OutgoingRequestRowDto[result.size()]));
    }
    
    public SaveAffiliateDataResult saveAffiliateData(String login, OutgoingRequestDto dto) {
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return SaveAffiliateDataResult.NO_USER;
        
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return SaveAffiliateDataResult.NO_ORG;
        
        DictCurrencyEntity dictCurrencyEntity = dictCurrencyDao.getByCode(KZT);
        if (dictCurrencyEntity == null) return SaveAffiliateDataResult.NO_CURRENCY;
        
        Map<Long, DictBudgetEntity> budgetMap = new HashMap<>();
        Map<Long, DictBudgetStoreTypeEntity> budgetStoreTypeMap = new HashMap<>();
        Map<Long, DictOrgPartEntity> orgPartMap = new HashMap<>();
        
        Date date = new Date(dto.date);
        
        OutgoingRequestEntity outgoingRequestEntity = outgoingRequestDao.fetchOneByOrgAndDate(orgEntity, date);
        Map<Long, OutgoingRequestRowEntity> deletedRowMap = new HashMap<>();
        Map<Long, OutgoingRequestCellEntity> deletedCellMap = new HashMap<>();
        if (outgoingRequestEntity == null) {
            outgoingRequestEntity = new OutgoingRequestEntity();
            outgoingRequestEntity.setDate(date);
            outgoingRequestEntity.setOrg(orgEntity);
        } else {
            for (OutgoingRequestRowEntity rowEntity: outgoingRequestEntity.getRows()) {
                deletedRowMap.put(rowEntity.getId(), rowEntity);
                for (OutgoingRequestCellEntity cellEntity: rowEntity.getCells()) deletedCellMap.put(cellEntity.getId(), cellEntity);
            }
        }
        
        if ((dto.rows != null) && (dto.rows.length > 0)) {
            for (OutgoingRequestRowDto rowDto: dto.rows) {
                if (rowDto == null) return SaveAffiliateDataResult.NULL_ROW;
                if (rowDto.budget == null) return SaveAffiliateDataResult.NULL_BUDGET;
                if (rowDto.budget.id == null) return SaveAffiliateDataResult.NULL_BUDGET_ID;
                
                DictBudgetEntity dictBudgetEntity = dictBudgetDao.get(rowDto.budget.id);
                if (dictBudgetEntity == null) return SaveAffiliateDataResult.BUDGET_NOT_FOUND;
                
                budgetMap.put(dictBudgetEntity.getId(), dictBudgetEntity);
                
                if ((rowDto.cells != null) && (rowDto.cells.length > 0)) {
                    for (OutgoingRequestCellDto cellDto: rowDto.cells) {
                        if (cellDto == null) return SaveAffiliateDataResult.NULL_CELL;
                        if (cellDto.orgPart == null) return SaveAffiliateDataResult.NULL_ORG_PART;
                        if (cellDto.orgPart.id == null) return SaveAffiliateDataResult.NULL_ORG_PART_ID;
                        if (cellDto.storeType == null) return SaveAffiliateDataResult.NULL_STORE_TYPE;
                        if (cellDto.storeType.id == null) return SaveAffiliateDataResult.NULL_STORE_TYPE_ID;
                        
                        DictOrgPartEntity dictOrgPartEntity = dictOrgPartDao.get(cellDto.orgPart.id);
                        if (dictOrgPartEntity == null) return SaveAffiliateDataResult.ORG_PART_NOT_FOUND;
                        
                        DictBudgetStoreTypeEntity dictBudgetStoreTypeEntity = dictBudgetStoreTypeDao.get(cellDto.storeType.id);
                        if (dictBudgetStoreTypeEntity == null) return SaveAffiliateDataResult.STORE_TYPE_NOT_FOUND;
                        
                        orgPartMap.put(dictOrgPartEntity.getId(), dictOrgPartEntity);
                        budgetStoreTypeMap.put(dictBudgetStoreTypeEntity.getId(), dictBudgetStoreTypeEntity);
                    }
                }
            }
        }
        
        if (outgoingRequestEntity.getId() == null) outgoingRequestDao.saveOrUpdate(outgoingRequestEntity);
        
        if ((dto.rows != null) && (dto.rows.length > 0)) {
            for (OutgoingRequestRowDto rowDto: dto.rows) {
                OutgoingRequestRowEntity rowEntity = null;
                if (rowDto.id != null) rowEntity = deletedRowMap.remove(rowDto.id);
                
                if (rowEntity == null) {
                    rowEntity = new OutgoingRequestRowEntity();
                    rowEntity.setRequest(outgoingRequestEntity);
                }
                rowEntity.setBudget(budgetMap.get(rowDto.budget.id));
                rowEntity.setNote(rowDto.note);
                rowEntity.setOrderNumber(rowDto.orderNumber);
                
                outgoingRequestRowDao.saveOrUpdate(rowEntity);
                
                if ((rowDto.cells != null) && (rowDto.cells.length > 0)) {
                    for (OutgoingRequestCellDto cellDto: rowDto.cells) {
                        OutgoingRequestCellEntity cellEntity = null;
                        if (cellDto.id != null) deletedCellMap.remove(cellDto.id);
                        
                        if (cellEntity == null) {
                            cellEntity = new OutgoingRequestCellEntity();
                            cellEntity.setRow(rowEntity);
                        }
                        cellEntity.setCurrency(dictCurrencyEntity);
                        cellEntity.setOrgPart(orgPartMap.get(cellDto.orgPart.id));
                        cellEntity.setStoreType(budgetStoreTypeMap.get(cellDto.storeType.id));
                        cellEntity.setValue(cellDto.value);
                        
                        outgoingRequestCellDao.saveOrUpdate(cellEntity);
                    }
                }
            }
        }
        
        for (OutgoingRequestCellEntity cellEntity: deletedCellMap.values()) outgoingRequestCellDao.delete(cellEntity);
        for (OutgoingRequestRowEntity rowEntity: deletedRowMap.values()) outgoingRequestRowDao.delete(rowEntity);
        
        return SaveAffiliateDataResult.SUCCESS;
    }
}