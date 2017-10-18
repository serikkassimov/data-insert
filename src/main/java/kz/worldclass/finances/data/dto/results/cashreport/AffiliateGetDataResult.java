package kz.worldclass.finances.data.dto.results.cashreport;

import kz.worldclass.finances.data.dto.entity.BudgetNextChangeDto;

public class AffiliateGetDataResult {
    public static enum Type {
        SUCCESS, NO_LOGIN, USER_NOT_FOUND, ORG_NOT_FOUND, CHANGE_TYPE_NOT_FOUND
    }
    
    public Type type;
    public BudgetNextChangeDto data;
    
    public AffiliateGetDataResult(Type type) {
        this.type = type;
        this.data = null;
    }
    
    public AffiliateGetDataResult(BudgetNextChangeDto data) {
        this.type = Type.SUCCESS;
        this.data = data;
    }
}