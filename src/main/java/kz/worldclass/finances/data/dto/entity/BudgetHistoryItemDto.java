package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class BudgetHistoryItemDto extends BaseDto {
    public Double itemValue;
    
    public BudgetHistoryDto history;
    public DictCurrencyDto currency;
    public DictBudgetStoreTypeDto storeType;
    public DictBudgetDto budgetType;
    public NoteDto note;
}