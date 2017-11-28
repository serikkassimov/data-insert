package kz.worldclass.finances.data.dto.results.outgoingrequest;

import kz.worldclass.finances.data.dto.entity.OutgoingRequestRowDto;

public class GetAffiliateDataResult {
    public static enum Type {
        SUCCESS, NO_LOGIN, NO_DATE, NO_USER, NO_ORG
    }
    
    public Type type;
    public OutgoingRequestRowDto[] rows;
    
    public GetAffiliateDataResult(Type type) {
        this.type = type;
        this.rows = null;
    }
    
    public GetAffiliateDataResult(OutgoingRequestRowDto[] rows) {
        this.type = Type.SUCCESS;
        this.rows = rows;
    }
}