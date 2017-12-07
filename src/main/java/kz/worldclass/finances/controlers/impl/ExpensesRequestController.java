package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;

import kz.worldclass.finances.data.dto.entity.*;
import kz.worldclass.finances.data.dto.results.expensesrequest.GetAffiliateDataResult;
import kz.worldclass.finances.data.dto.results.expensesrequest.SaveAffiliateDataResult;
import kz.worldclass.finances.services.ExpensesRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/expenses-request")
public class ExpensesRequestController extends AbstractRestController {
    @Autowired
    private ExpensesRequestService service;
    
    @RequestMapping(value = "/dict/budgets", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetDto> dictBudgets() {
        return service.getDictBudgets();
    }
    
    @RequestMapping(value = "/dict/budget-store-types", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetStoreTypeDto> dictBudgetStoreTypes() {
        return service.getDictBudgetStoreTypes();
    }
    
    @RequestMapping(value = "/dict/currencies", produces = APPLICATION_JSON_UTF_8)
    public List<DictCurrencyDto> dictCurrencies() {
        return service.getDictCurrencies();
    }

    @RequestMapping(value = "/dict/orgs", produces = APPLICATION_JSON_UTF_8)
    public List<DictOrgDto> dictOrgs() {
        return service.getDictOrgs();
    }
    
    @RequestMapping(value = "/affiliate/data/get", produces = APPLICATION_JSON_UTF_8)
    public GetAffiliateDataResult affiliateDataGet(
            @RequestParam(name = "date", required = false) Long dateMillis
    ) {
        if (dateMillis == null) return new GetAffiliateDataResult(GetAffiliateDataResult.Type.NO_DATE);
        return service.getAffiliateData(login(), onlyDate(dateMillis).getTime());
    }
    
    @RequestMapping(value = "/affiliate/data/save", produces = APPLICATION_JSON_UTF_8, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public SaveAffiliateDataResult affiliateDataSave(
            @RequestBody(required = false) BudgetNextChangeDto dto
    ) {
        if (dto == null) return SaveAffiliateDataResult.NO_DATA;
        if (dto.changeDate == null) return SaveAffiliateDataResult.NO_DATE;
        dto.changeDate = onlyDate(dto.changeDate).getTimeInMillis();
        return service.saveAffiliateData(login(), dto);
    }
}