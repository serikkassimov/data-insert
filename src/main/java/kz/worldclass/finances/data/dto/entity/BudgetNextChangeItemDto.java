package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class BudgetNextChangeItemDto extends BaseDto {
    public Double itemValue;
    
    public BudgetNextChangeDto change;
    public DictCurrencyDto currency;
    public DictBudgetStoreTypeDto storeType;
    public DictBudgetDto budgetType;
    public DictOrgDto org;
    public DictBudgetDto budgetSubType;
    public NoteDto note;
}