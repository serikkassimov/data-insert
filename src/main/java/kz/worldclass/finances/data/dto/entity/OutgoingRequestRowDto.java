package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class OutgoingRequestRowDto extends BaseDto {
    public String note;
    public Long orderNumber;
    
    public OutgoingRequestDto request;
    public DictBudgetDto budget;
    
    public OutgoingRequestCellDto[] cells;
}