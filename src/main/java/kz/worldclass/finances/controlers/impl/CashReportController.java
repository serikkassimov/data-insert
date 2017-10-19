package kz.worldclass.finances.controlers.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import kz.worldclass.finances.data.dto.entity.BudgetNextChangeDto;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateGetDataResult;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateSaveDataResult;
import kz.worldclass.finances.services.CashReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cash-report")
public class CashReportController extends AbstractRestController {
    @Autowired
    private CashReportService service;
    
    private static Calendar onlyDate(Long millis) {
        if (millis == null) millis = System.currentTimeMillis();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar;
    }
    
    @RequestMapping(value = "/dict-budget-store-types", produces = APPLICATION_JSON_UTF_8)
    public List<BaseDictDto> dictBudgetStoreTypes() {
        return service.getDictBudgetStoreTypes();
    }
    
    @RequestMapping(value = "/dict-budgets", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetDto> dictBudgets() {
        return service.getDictBudgets();
    }
    
    @RequestMapping(value = "/dict-currencies", produces = APPLICATION_JSON_UTF_8)
    public List<DictCurrencyDto> dictCurrencies() {
        return service.getDictCurrencies();
    }
    
    @RequestMapping(value = "/affiliate/data/get", produces = APPLICATION_JSON_UTF_8)
    public AffiliateGetDataResult affiliateDataGet(
            @RequestParam(name = "date", required = false) Long dateMillis
    ) {
        Calendar dataCalendar = onlyDate(dateMillis);
        return service.getAffiliateData(login(), dataCalendar.getTime());
    }
    
    @RequestMapping(value = "/affiliate/data/save", produces = APPLICATION_JSON_UTF_8, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public AffiliateSaveDataResult affiliateDataSave(
            @RequestBody(required = false) BudgetNextChangeDto dto
    ) {
        if (dto == null) return AffiliateSaveDataResult.NO_DATA;
        if (dto.changeDate == null) return AffiliateSaveDataResult.NO_DATE;
        dto.changeDate = onlyDate(dto.changeDate).getTimeInMillis();
        return service.saveAffiliateData(login(), dto);
    }
}