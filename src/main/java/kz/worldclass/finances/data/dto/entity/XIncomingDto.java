package kz.worldclass.finances.data.dto.entity;

import kz.worldclass.finances.data.dto.entity.base.BaseDto;

public class XIncomingDto extends BaseDto {
    public Long date;
    public String note;
    public Double value;
    public Long orderNumber;
    public Boolean cash;
    
    public DictOrgDto org;
}