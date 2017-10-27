package kz.worldclass.finances.data.dto.results.expensesrequest;

import kz.worldclass.finances.data.dto.entity.BudgetNextChangeItemDto;

public class GetAffiliateDataResult {
    public static enum Type {
        SUCCESS, NO_DATE, NO_LOGIN, USER_NOT_FOUND, ORG_NOT_FOUND, CHANGE_TYPE_NOT_FOUND
    }
    
    public Type type;
    public BudgetNextChangeItemDto[] items;
    
    public GetAffiliateDataResult(Type type) {
        this.type = type;
        this.items = null;
    }
    
    public GetAffiliateDataResult(BudgetNextChangeItemDto[] items) {
        this.type = Type.SUCCESS;
        this.items = items;
    }
}