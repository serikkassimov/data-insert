package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class BudgetHistoryDto extends BaseDto {
    public Long changeDate;
    
    public DictOrgDto org;
    public DictBudgetNextChangeTypeDto type;
    public NoteDto note;
}