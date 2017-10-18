package kz.worldclass.finances.controlers.impl;

import kz.worldclass.finances.controlers.AbstractRestController;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.*;
import kz.worldclass.finances.services.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/cash")
public class CashData extends AbstractRestController {
    @Autowired
    private DictService service;
    
    @RequestMapping(value = "/list", produces = APPLICATION_JSON_UTF_8)
    public List<BaseDictDto> baseList(
            @PathVariable(name = "dictType") String dictType
    ) {
        return service.getBaseList(dictType);
    }
    
    @RequestMapping(value = "/save", produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON, method = RequestMethod.POST)
    public SaveBaseDictResult saveBase(
            @RequestBody(required = false) Map dto
    ) {
        System.out.println(dto);
        if (dto == null) return SaveBaseDictResult.NO_DATA;

        return SaveBaseDictResult.SUCCESS;
    }
    

}