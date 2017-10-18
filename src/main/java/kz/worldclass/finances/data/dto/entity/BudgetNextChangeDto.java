package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class BudgetNextChangeDto extends BaseDto {
    public DictOrgDto org;
    public DictBudgetNextChangeTypeDto type;
    public DictBudgetNextChangeStateDto state;
    public NoteDto note;
    
    public BudgetNextChangeItemDto[] items;
}