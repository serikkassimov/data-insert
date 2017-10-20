package kz.worldclass.finances.data.dto.results.cashreport;

import kz.worldclass.finances.data.dto.entity.BudgetNextChangeDto;

public class HqGetDataResult {
    public static enum Type {
        SUCCESS, NO_ORG_ID, ORG_NOT_FOUND, CHANGE_TYPE_NOT_FOUND
    }
    
    public Type type;
    public BudgetNextChangeDto data;
    
    public HqGetDataResult(Type type) {
        this.type = type;
        this.data = null;
    }
    
    public HqGetDataResult(BudgetNextChangeDto data) {
        this.type = Type.SUCCESS;
        this.data = data;
    }
}