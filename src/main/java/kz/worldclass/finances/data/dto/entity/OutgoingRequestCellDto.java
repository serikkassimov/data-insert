package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class OutgoingRequestCellDto extends BaseDto {
    public Double value;
    
    public OutgoingRequestRowDto row;
    public DictBudgetStoreTypeDto storeType;
    public DictCurrencyDto currency;
    public DictOrgPartDto orgPart;
}