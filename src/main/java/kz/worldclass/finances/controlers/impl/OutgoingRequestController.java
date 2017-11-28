package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetStoreTypeDto;
import kz.worldclass.finances.data.dto.entity.DictOrgPartDto;
import kz.worldclass.finances.data.dto.entity.OutgoingRequestDto;
import kz.worldclass.finances.data.dto.entity.OutgoingRequestRowDto;
import kz.worldclass.finances.data.dto.results.outgoingrequest.GetAffiliateDataResult;
import kz.worldclass.finances.data.dto.results.outgoingrequest.SaveAffiliateDataResult;
import kz.worldclass.finances.services.OutgoingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/outgoing-request")
public class OutgoingRequestController extends AbstractRestController {
    @Autowired
    private OutgoingRequestService service;
    
    @RequestMapping(value = "/dict/budgets", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetDto> dictBudgets() {
        return service.getDictBudgets();
    }
    
    @RequestMapping(value = "/dict/budget-store-types", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetStoreTypeDto> dictBudgetStoreTypes() {
        return service.getDictBudgetStoreTypes();
    }
    
    @RequestMapping(value = "/dict/org-parts", produces = APPLICATION_JSON_UTF_8)
    public List<DictOrgPartDto> dictOrgParts() {
        return service.getDictOrgParts();
    }
    
    @RequestMapping(value = "/affiliate/data/get", produces = APPLICATION_JSON_UTF_8)
    public GetAffiliateDataResult affiliateDataGet(
            @RequestParam(value = "date") Long dateMillis
    ) {
        String login = login();
        if ((login == null) || login.isEmpty()) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_LOGIN);
        
        if (dateMillis == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_DATE);
        
        return service.getDataResult(login, onlyDate(dateMillis).getTime());
    }
    
    @RequestMapping(value = "/affiliate/data/save", produces = APPLICATION_JSON_UTF_8, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public SaveAffiliateDataResult affiliateDataSave(
            @RequestBody(required = false) OutgoingRequestDto dto
    ) {
        String login = login();
        if ((login == null) || login.isEmpty()) return SaveAffiliateDataResult.NO_LOGIN;
        
        if (dto == null) return SaveAffiliateDataResult.NO_DATA;
        if (dto.date == null) return SaveAffiliateDataResult.NO_DATE;
        dto.date = onlyDate(dto.date).getTimeInMillis();
        return service.saveAffiliateData(login(), dto);
    }
}