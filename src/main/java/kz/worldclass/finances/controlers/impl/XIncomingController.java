package kz.worldclass.finances.controlers.impl;

import java.util.List;
import kz.worldclass.finances.controlers.AbstractRestController;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON;
import static kz.worldclass.finances.controlers.AbstractRestController.APPLICATION_JSON_UTF_8;
import kz.worldclass.finances.data.dto.entity.XIncomingDto;
import kz.worldclass.finances.data.dto.results.xincoming.GetDataResult;
import kz.worldclass.finances.data.dto.results.xincoming.SaveDataResult;
import kz.worldclass.finances.services.XIncomingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/x-incoming")
public class XIncomingController extends AbstractRestController {
    @Autowired
    private XIncomingService service;
    
    @RequestMapping(value = "/data", produces = APPLICATION_JSON_UTF_8)
    public GetDataResult data(
            @RequestParam(name = "date", required = false) Long dateMillis
    ) {
        String login = login();
        if ((login == null) || login.isEmpty()) return new GetDataResult(GetDataResult.Type.NO_LOGIN);
        
        if (dateMillis == null) dateMillis = System.currentTimeMillis();
        return service.getData(onlyDate(dateMillis).getTime(), login());
    }
    
    @RequestMapping(value = "/save", produces = APPLICATION_JSON_UTF_8, method = RequestMethod.POST, consumes = APPLICATION_JSON)
    public SaveDataResult save(
            @RequestParam(name = "date", required = false) Long dateMillis,
            @RequestBody(required = false) List<XIncomingDto> dtos
    ) {
        if (dtos == null) return SaveDataResult.NO_DATA;
        
        String login = login();
        if ((login == null) || login.isEmpty()) return SaveDataResult.NO_LOGIN;
        
        if (dateMillis == null) dateMillis = System.currentTimeMillis();
        
        return service.saveData(onlyDate(dateMillis).getTime(), dtos, login);
    }
}