package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import kz.worldclass.finances.dao.impl.*;
import kz.worldclass.finances.data.dto.entity.*;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateSaveDataResult;
import kz.worldclass.finances.data.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CashDataService {
    private static final String CURRENCY_CODE_KZT = "KZT";
    
    @Autowired
    private DictBudgetDao dictBudgetDao;
    @Autowired
    private DictOrgDao dictOrgDao ;
    @Autowired
    private BudgetHistoryItemDao budgetHistoryItemDao;
    @Autowired
    private BudgetNextChangeItemDao budgetNextChangeItemDao;
    @Autowired
    private UserDao userDao;
    
    public List<DictBudgetDto> getEnabledIncomingLeafs() {
        List<DictBudgetDto> result = new ArrayList<>();
        for (DictBudgetEntity entity: dictBudgetDao.fetchEnabledIncomingLeafs()) result.add(Dtos.less(entity));
        return result;
    }

    public List<BudgetHistoryItemDto> getHistoryItems(Date startDate, Date endDate) {
        List<BudgetHistoryItemDto> result = new ArrayList<>();
        for (BudgetHistoryItemEntity entity: budgetHistoryItemDao.fetchBetweenDates(CURRENCY_CODE_KZT, startDate, endDate)) {
            BudgetHistoryItemDto dto = Dtos.less(entity);
            result.add(dto);
            dto.history = Dtos.less(entity.getHistory());
            if (entity.getHistory() != null) {
                dto.history.org = Dtos.less(entity.getHistory().getOrg());
            }
            dto.budgetType = Dtos.less(entity.getBudgetType());
            dto.currency = Dtos.less(entity.getCurrency());
            dto.storeType = Dtos.less(entity.getStoreType());
        }
        return result;
    }


    public List<BudgetHistoryItemDto> getHistoryItems(Date currDate, String login) {
        List<BudgetHistoryItemDto> result = new ArrayList<>();
        UserEntity userEntity = userDao.fetchOneByLogin(login);
        if (userEntity == null) return null;
        DictOrgEntity orgEntity = userEntity.getOrg();
        if (orgEntity == null) return null;
        for (BudgetHistoryItemEntity entity: budgetHistoryItemDao.fetchByDate(CURRENCY_CODE_KZT, currDate, orgEntity)) {
            BudgetHistoryItemDto dto = Dtos.less(entity);
            result.add(dto);
            dto.history = Dtos.less(entity.getHistory());
            if (entity.getHistory() != null) {
                dto.history.org = Dtos.less(entity.getHistory().getOrg());
            }
            dto.budgetType = Dtos.less(entity.getBudgetType());
            dto.currency = Dtos.less(entity.getCurrency());
            dto.storeType = Dtos.less(entity.getStoreType());
        }
        return result;
    }

    public List<BudgetNextChangeItemDto> getNextItems(Date startDate, Date endDate) {
        List<BudgetNextChangeItemDto> result = new ArrayList<>();
        for (BudgetNextChangeItemEntity entity: budgetNextChangeItemDao.fetchBetweenDates(CURRENCY_CODE_KZT, startDate, endDate)) {
            BudgetNextChangeItemDto dto = Dtos.less(entity);
            result.add(dto);
            dto.change = Dtos.less(entity.getChange());
            if (entity.getChange() != null) {
                dto.change.org = Dtos.less(entity.getChange().getOrg());
            }
            dto.budgetType = Dtos.less(entity.getBudgetType());
            dto.currency = Dtos.less(entity.getCurrency());
            dto.storeType = Dtos.less(entity.getStoreType());
        }
        return result;
    }


    public List<DictOrgDto> getOrgs() {
        List<DictOrgDto> result = new ArrayList<>();
        for (DictOrgEntity entity: dictOrgDao.allEnabled()) result.add(Dtos.less(entity));
        return result;
    }

    public List<DictBudgetDto> getBudget() {
        List<DictBudgetDto> result = new ArrayList<>();
        for (DictBudgetEntity entity: dictBudgetDao.allEnabled()) result.add(Dtos.complete(entity));
        return result;
    }
}