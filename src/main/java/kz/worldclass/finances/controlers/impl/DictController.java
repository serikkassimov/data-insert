package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.DisableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.EnableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.SaveBaseDictResult;
import kz.worldclass.finances.data.dto.results.dict.SaveCurrencyResult;
import kz.worldclass.finances.data.dto.results.dict.SaveDictBudgetResult;
import kz.worldclass.finances.services.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/dict")
public class DictController extends AbstractRestController {
    @Autowired
    private DictService service;
    
    @RequestMapping(value = "/base/{dictType}/list", produces = APPLICATION_JSON_UTF_8)
    public List<BaseDictDto> baseList(
            @PathVariable(name = "dictType") String dictType
    ) {
        return service.getBaseList(dictType);
    }
    
    @RequestMapping(value = "/base/{dictType}/save", produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON, method = RequestMethod.POST)
    public SaveBaseDictResult saveBase(
            @PathVariable(name = "dictType") String dictType,
            @RequestBody(required = false) BaseDictDto dto
    ) {
        if (dto == null) return SaveBaseDictResult.NO_DATA;
        if ((dto.code == null) || dto.code.isEmpty()) return SaveBaseDictResult.NO_CODE;
        if (dto.disabled == null) return SaveBaseDictResult.NO_DISABLED;
        return service.saveBase(dictType, dto);
    }
    
    @RequestMapping(value = "/base/{dictType}/enable", produces = APPLICATION_JSON_UTF_8)
    public EnableBaseResult enableBase(
            @PathVariable(name = "dictType") String dictType,
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return EnableBaseResult.NO_ID;
        return service.enableBase(dictType, id);
    }
    
    @RequestMapping(value = "/base/{dictType}/disable", produces = APPLICATION_JSON_UTF_8)
    public DisableBaseResult disableBase(
            @PathVariable(name = "dictType") String dictType,
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return DisableBaseResult.NO_ID;
        return service.disableBase(dictType, id);
    }
    
    @RequestMapping(value = "/budget/list", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetDto> budgetList() {
        return service.getDictBudgets();
    }
    
    @RequestMapping(value = "/budget/tree", produces = APPLICATION_JSON_UTF_8)
    public List<DictBudgetDto> budgetTree() {
        return service.getDictBudgetTree();
    }
    
    @RequestMapping(value = "/budget/save", produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON, method = RequestMethod.POST)
    public SaveDictBudgetResult budgetSave(
            @RequestBody(required = false) DictBudgetDto dto
    ) {
        if (dto == null) return SaveDictBudgetResult.NO_DATA;
        if ((dto.code == null) || dto.code.isEmpty()) return SaveDictBudgetResult.NO_CODE;
        if (dto.disabled == null) return SaveDictBudgetResult.NO_DISABLED;
        if (dto.outgo == null) return SaveDictBudgetResult.NO_OUTGO;
        return service.saveDictBudget(dto);
    }
    
    @RequestMapping(value = "/currency/list", produces = APPLICATION_JSON_UTF_8)
    public List<DictCurrencyDto> currencyList() {
        return service.getDictCurrencies();
    }
    
    @RequestMapping(value = "/currency/save", produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON, method = RequestMethod.POST)
    public SaveCurrencyResult currencySave(
            @RequestBody(required = false) DictCurrencyDto dto
    ) {
        if (dto == null) return SaveCurrencyResult.NO_DATA;
        if ((dto.code == null) || dto.code.isEmpty()) return SaveCurrencyResult.NO_CODE;
        if (dto.disabled == null) return SaveCurrencyResult.NO_DISABLED;
        if (dto.symbol == null) return SaveCurrencyResult.NO_SYMBOL;
        return service.saveDictCurrency(dto);
    }
}