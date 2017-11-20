package kz.worldclass.finances.data.dto.results.xincoming;

import kz.worldclass.finances.data.dto.entity.XIncomingDto;

public class GetDataResult {
    public static enum Type {
        SUCCESS, NO_LOGIN, USER_NOT_FOUND, ORG_NOT_FOUND
    }
    
    public Type type;
    public XIncomingDto[] items;
    
    public GetDataResult(Type type) {
        this.type = type;
        this.items = null;
    }
    
    public GetDataResult(XIncomingDto... items) {
        this.items = items;
        this.type = Type.SUCCESS;
    }
}