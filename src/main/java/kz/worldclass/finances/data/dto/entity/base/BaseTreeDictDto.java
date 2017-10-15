package kz.worldclass.finances.data.dto.entity.base;

import java.util.List;

public class BaseTreeDictDto<T extends BaseTreeDictDto> extends BaseDictDto {
    public T parent;
    public List<T> children;
}