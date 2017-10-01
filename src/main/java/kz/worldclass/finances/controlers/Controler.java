package kz.worldclass.finances.controlers;


import kz.worldclass.finances.data.entity.EntityFilials;
import kz.worldclass.finances.data.entity.EntityIndicators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.expression.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import kz.worldclass.finances.services.DataService;
import kz.worldclass.finances.services.FilialService;
import kz.worldclass.finances.services.IndicatorSevice;
import kz.worldclass.finances.services.UtilService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by serik on 22.03.17.
 */

//@RestController
//@ComponentScan({"kz.worldclass.finances.services"})
public class Controler {
    @Autowired
    FilialService filialService;
    @Autowired
    IndicatorSevice indicatorSevice;
    @Autowired
    DataService dataService;
    @Autowired
    UtilService utilService;

    @RequestMapping(value = { "/index"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("index");
        return res;
    }

    @RequestMapping(value = { "/main"}, method = RequestMethod.GET)
    public ModelAndView main(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("main");
        return res;
    }

    @RequestMapping(value = {"/cash-balance"}, method = RequestMethod.GET)
    public ModelAndView cashBalance(
            @RequestParam(value = "filial", required = true) final int filial,
            HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("cash-balance");
        res.addObject("filial", filial);
        return res;
    }

    @RequestMapping(value = {"/", "/cash-balance-all"}, method = RequestMethod.GET)
    public ModelAndView cashBalanceAll(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("cash-balance-all");
        return res;
    }

    @RequestMapping(value = {"/incomes"}, method = RequestMethod.GET)
    public ModelAndView incomes(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("incomes");
        res.addObject("filial", 1);
        return res;
    }

    @RequestMapping(value = {"/settings"}, method = RequestMethod.GET)
    public ModelAndView settings(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("settings");
        return res;
    }

    @RequestMapping(value = {"/get-filials"}, method = RequestMethod.GET)
    public String get_filials(HttpServletResponse response) throws ParseException {
        return filialService.getAllFilials();
    }

    @RequestMapping(value = {"/get-indicators"}, method = RequestMethod.GET)
    public String get_indicators(HttpServletResponse response) throws ParseException {
        return indicatorSevice.getAllFilials();
    }

    @RequestMapping(value = {"/get-columns"}, method = RequestMethod.GET)
    public String get_columns(HttpServletResponse response) throws ParseException {
        return indicatorSevice.getElementsGson(3);
    }

    @RequestMapping(value = {"/get-income-types"}, method = RequestMethod.GET)
    public String get_income_types(HttpServletResponse response) throws ParseException {
        return indicatorSevice.getElementsGson(2);
    }

    @RequestMapping(value = {"/get-months"}, method = RequestMethod.GET)
    public String get_months(HttpServletResponse response) throws ParseException {
        return utilService.getMonths();
    }

    @RequestMapping(value = {"/save-data"}, method = RequestMethod.POST)
    public void save_data(@RequestBody Map param,
            HttpServletResponse response) throws ParseException {
        dataService.addNewDataRow(param);
    }

    @RequestMapping(value = {"/get-all-data"}, method = RequestMethod.GET)
    public String get_all_data(HttpServletResponse response) throws ParseException {
        return dataService.getFilialData(99);
    }

    @RequestMapping(value = {"/save-filial"}, method = RequestMethod.POST)
    public EntityFilials save_filial(@RequestBody Map param,
                                     HttpServletResponse response) throws ParseException {
        return filialService.addNewFilial(param);
    }
    @RequestMapping(value = {"/delete-filial"}, method = RequestMethod.POST)
    public EntityFilials delete_filial(@RequestBody Map param,
                                     HttpServletResponse response) throws ParseException {
        return filialService.deleteFilial(param);
    }
    @RequestMapping(value = {"/save-indicator"}, method = RequestMethod.POST)
    public EntityIndicators save_indicator(@RequestBody Map param,
                                           HttpServletResponse response) throws ParseException {
        return indicatorSevice.addNewIndicator(param);
    }
    @RequestMapping(value = {"/delete-indicator"}, method = RequestMethod.POST)
    public EntityIndicators delete_indicator(@RequestBody Map param,
                                       HttpServletResponse response) throws ParseException {
        return indicatorSevice.deleteIndicator(param);
    }
    @RequestMapping(value = {"/get-filial-data/{id}"}, method = RequestMethod.GET)
    public String get_filial_data(
            @PathVariable Integer id,
            HttpServletResponse response) throws ParseException {
        return dataService.getFilialData(id);
    }
    @RequestMapping(value = {"/get-month-data/{filial}/{month}/{type}"}, method = RequestMethod.GET)
    public String get_month_data(
            @PathVariable Integer filial,@PathVariable Integer month,@PathVariable Integer type,
            HttpServletResponse response) throws ParseException {
        return dataService.getIncomesData(filial, month, type);
    }
}
