package controlers;


import entity.EntityFilials;
import entity.EntityIndicators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.expression.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import services.DataService;
import services.FilialService;
import services.IndicatorSevice;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by serik on 22.03.17.
 */

@RestController
@ComponentScan({"services"})
public class Controler {
    @Autowired
    FilialService filialService;
    @Autowired
    IndicatorSevice indicatorSevice;
    @Autowired
    DataService dataService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("index");
        return res;
    }

    @RequestMapping(value = {"/cash-balance"}, method = RequestMethod.GET)
    public ModelAndView cashBalance(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("cash-balance");
        return res;
    }

    @RequestMapping(value = {"/cash-balance-all"}, method = RequestMethod.GET)
    public ModelAndView cashBalanceAll(HttpServletResponse response) throws ParseException {
        ModelAndView res = new ModelAndView("cash-balance-all");
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

    @RequestMapping(value = {"/save-data"}, method = RequestMethod.POST)
    public void save_data(@RequestBody Map param,
            HttpServletResponse response) throws ParseException {
        dataService.addNewDataRow(param);
    }

    @RequestMapping(value = {"/get-all-data"}, method = RequestMethod.GET)
    public String get_all_data(HttpServletResponse response) throws ParseException {
        return dataService.getAllData();
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
        return ""; //dataService.getFilialData();
    }
}
