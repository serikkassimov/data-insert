package kz.worldclass.finances.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import kz.worldclass.finances.dao.impl.BudgetHistoryItemDao;
import kz.worldclass.finances.dao.impl.DictBudgetDao;
import kz.worldclass.finances.data.dto.entity.BudgetHistoryItemDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.Dtos;
import kz.worldclass.finances.data.entity.BudgetHistoryItemEntity;
import kz.worldclass.finances.data.entity.DictBudgetEntity;
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
    private BudgetHistoryItemDao budgetHistoryItemDao;
    
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
            dto.budgetType = Dtos.less(entity.getBudgetType());
            dto.currency = Dtos.less(entity.getCurrency());
            dto.storeType = Dtos.less(entity.getStoreType());
        }
        return result;
    }
}