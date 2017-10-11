package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.DisableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.EnableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.SaveBaseResult;
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
    public SaveBaseResult saveBase(
            @PathVariable(name = "dictType") String dictType,
            @RequestBody(required = false) BaseDictDto dto
    ) {
        if (dto == null) return SaveBaseResult.NO_DATA;
        if ((dto.code == null) || dto.code.isEmpty()) return SaveBaseResult.NO_CODE;
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
        return service.saveDictBudget(dto);
    }
    
    @RequestMapping(value = "/budget/enable", produces = APPLICATION_JSON_UTF_8)
    public EnableBaseResult budgetEnable(
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return EnableBaseResult.NO_ID;
        return service.enableDictBudget(id);
    }
    
    @RequestMapping(value = "/budget/disable", produces = APPLICATION_JSON_UTF_8)
    public DisableBaseResult budgetDisable(
            @RequestParam(name = "id", required = false) Long id
    ) {
        if (id == null) return DisableBaseResult.NO_ID;
        return service.disableDictBudget(id);
    }
}