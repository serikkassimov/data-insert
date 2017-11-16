package kz.worldclass.finances.data.dto.results.expensesrequest;

import kz.worldclass.finances.data.dto.entity.BudgetNextChangeItemDto;
import kz.worldclass.finances.data.dto.entity.DictOrgDto;

public class GetAffiliateDataResult {
    public static enum Type {
        SUCCESS, NO_DATE, NO_LOGIN, USER_NOT_FOUND, ORG_NOT_FOUND, CHANGE_TYPE_NOT_FOUND
    }

    public Type type;
    public BudgetNextChangeItemDto[] items;
    public DictOrgDto dictOrgDto;

    public GetAffiliateDataResult(Type type) {
        this.type = type;
        this.items = null;
    }

    public GetAffiliateDataResult(BudgetNextChangeItemDto[] items) {
        this.type = Type.SUCCESS;
        this.items = items;
    }

    public GetAffiliateDataResult(BudgetNextChangeItemDto[] items, DictOrgDto dictOrgDto) {
        this.type = Type.SUCCESS;
        this.items = items;
        this.dictOrgDto = dictOrgDto;
    }
}