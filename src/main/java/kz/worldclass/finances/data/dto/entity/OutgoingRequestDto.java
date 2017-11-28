package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class OutgoingRequestDto extends BaseDto {
    public Long date;
    
    public DictOrgDto org;
    
    public OutgoingRequestRowDto[] rows;
}