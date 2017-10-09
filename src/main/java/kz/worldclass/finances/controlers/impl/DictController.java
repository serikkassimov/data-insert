package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.DisableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.EnableBaseResult;
import kz.worldclass.finances.data.dto.results.dict.SaveBaseResult;
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
    private static final String BASE = "/base";
    private static final String DICT_TYPE = "dictType";
    private static final String DICT_TYPE_PATH = "/{" + DICT_TYPE + "}";
    private static final String DISABLE = "/disable";
    private static final String ENABLE = "/enable";
    private static final String ID = "id";
    private static final String LIST = "/list";
    private static final String SAVE = "/save";
    
    @Autowired
    private DictService service;
    
    @RequestMapping(value = BASE + DICT_TYPE_PATH + LIST, produces = APPLICATION_JSON_UTF_8)
    public List<BaseDictDto> baseList(
            @PathVariable(name = DICT_TYPE) String dictType
    ) {
        return service.getBaseList(dictType);
    }
    
    @RequestMapping(value = BASE + DICT_TYPE_PATH + SAVE, method = RequestMethod.POST, produces = APPLICATION_JSON_UTF_8, consumes = APPLICATION_JSON)
    public SaveBaseResult saveBase(
            @PathVariable(name = DICT_TYPE) String dictType,
            @RequestBody(required = false) BaseDictDto dto
    ) {
        if (dto == null) return SaveBaseResult.NO_DATA;
        if ((dto.code == null) || dto.code.isEmpty()) return SaveBaseResult.NO_CODE;
        return service.saveBase(dictType, dto);
    }
    
    @RequestMapping(value = BASE + DICT_TYPE_PATH + ENABLE, produces = APPLICATION_JSON_UTF_8)
    public EnableBaseResult enableBase(
            @PathVariable(name = DICT_TYPE) String dictType,
            @RequestParam(name = ID, required = false) Long id
    ) {
        if (id == null) return EnableBaseResult.NO_ID;
        return service.enableBase(dictType, id);
    }
    
    @RequestMapping(value = BASE + DICT_TYPE_PATH + DISABLE, produces = APPLICATION_JSON_UTF_8)
    public DisableBaseResult disableBase(
            @PathVariable(name = DICT_TYPE) String dictType,
            @RequestParam(name = ID, required = false) Long id
    ) {
        if (id == null) return DisableBaseResult.NO_ID;
        return service.disableBase(dictType, id);
    }
}