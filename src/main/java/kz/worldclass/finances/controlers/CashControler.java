package kz.worldclass.finances.controlers;


import kz.worldclass.finances.data.entity.EntityFilials;
import kz.worldclass.finances.data.entity.EntityIndicators;
import kz.worldclass.finances.services.DataService;
import kz.worldclass.finances.services.FilialService;
import kz.worldclass.finances.services.IndicatorSevice;
import kz.worldclass.finances.services.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by serik on 22.03.17.
 */

//@RestController
//@ComponentScan({"kz.worldclass.finances.services"})
public class CashControler {

    @RequestMapping(value = {"/incomes"}, method = RequestMethod.GET)
    public ModelAndView incomes(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("incomes");
        res.addObject("filial", 1);
        return res;
    }




}
