package kz.worldclass.finances.controlers.impl;

import kz.worldclass.finances.controlers.AbstractRestController;
import kz.worldclass.finances.data.dto.entity.DictBudgetDto;
import kz.worldclass.finances.data.dto.entity.DictCurrencyDto;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.dict.*;
import kz.worldclass.finances.services.DictService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kz.worldclass.finances.data.dto.entity.BudgetHistoryItemDto;
import kz.worldclass.finances.services.CashDataService;

@RestController
@RequestMapping(value = "/cash")
public class CashData extends AbstractRestController {
    private static final String REPORT_KEY_DATE = "~DATE~";

    @Autowired
    private DictService service;
    @Autowired
    private CashDataService cashDataService;
    
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


    public HSSFWorkbook doreport4(Calendar startDateCalendar, Calendar endDateCalendar) {
        short width = 5024;
        HSSFWorkbook workbook = new HSSFWorkbook();
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet = workbook.createSheet("Филиал");
        sheet.setColumnWidth(0, width);
        sheet.setColumnWidth(1, width);
        sheet.setColumnWidth(2, width);
        sheet.setColumnWidth(3, width);
        sheet.setColumnWidth(4, width);
        sheet.setColumnWidth(5, width);
        sheet.setColumnWidth(6, width);
        sheet.setColumnWidth(7, width);
        sheet.setColumnWidth(8, width);
        sheet.setColumnWidth(9, width);
        
        List<DictBudgetDto> dictBudgets = cashDataService.getEnabledIncomingLeafs();
        
        List<BudgetHistoryItemDto> items = cashDataService.getHistoryItems(startDateCalendar.getTime(), endDateCalendar.getTime());
        
        // 1 строка
        int rownum = 0;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String period = new StringBuilder("с ")
                .append(dateFormat.format(startDateCalendar.getTime()))
                .append(" по ")
                .append(dateFormat.format(endDateCalendar.getTime()))
                .toString();
        rownum = doReportPart(
                styleMap, sheet, rownum, "наличных", "Мега-Фитнес", period, dictBudgets,
                getData("CASH", startDateCalendar, endDateCalendar, dictBudgets, items)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "безналичных", "Мега-Фитнес", period, dictBudgets,
                getData("CASHLESS_BANK", startDateCalendar, endDateCalendar, dictBudgets, items)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "POS-терминалу", "Мега-Фитнес", period, dictBudgets,
                getData("CASHLESS_TERMINAL", startDateCalendar, endDateCalendar, dictBudgets, items)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "всего", "Мега-Фитнес", period, dictBudgets,
                getData(null, startDateCalendar, endDateCalendar, dictBudgets, items)
        );
        return workbook;
    }
    
    private ArrayList<Map<String, Double>> getData(String storeTypeCode, Calendar startDateCalendar, Calendar endDateCalendar, List<DictBudgetDto> dictBudgets, List<BudgetHistoryItemDto> items) {
        Calendar current = new GregorianCalendar();
        current.setTimeInMillis(startDateCalendar.getTimeInMillis());
        
        Calendar next = new GregorianCalendar();
        next.setTimeInMillis(startDateCalendar.getTimeInMillis());
        next.set(Calendar.DATE, next.get(Calendar.DATE) + 1);
        
        long dayMillis = 1000L * 60 * 60 * 24;
            
        long excelDateStart;
        try {
            excelDateStart = new SimpleDateFormat("dd.MM.yyyy").parse("30.12.1899").getTime() / dayMillis;
        } catch (ParseException exception) {
            throw new RuntimeException("cannot parse excel start date");
        }
        
        ArrayList<Map<String, Double>> mapArrayList = new ArrayList<>();
        while (current.before(endDateCalendar)) {
            long currentMillis = current.getTimeInMillis();
            long nextMillis = next.getTimeInMillis();
            
            Map<String, Double> map = new HashMap<>();
            mapArrayList.add(map);
            
            map.put(REPORT_KEY_DATE, new Long(current.getTimeInMillis() / dayMillis - excelDateStart).doubleValue());
            
            for (DictBudgetDto dictBudget: dictBudgets) {
                Double sum = 0D;
                for (BudgetHistoryItemDto item: items) {
                    if (
                            ((storeTypeCode == null) || (storeTypeCode.equals(item.storeType.code)))
                            && (dictBudget.code.equals(item.budgetType.code))
                            && (item.history.changeDate >= currentMillis)
                            && (item.history.changeDate < nextMillis)
                            && (item.itemValue != null)
                    ) {
                        sum += item.itemValue;
                    }
                }
                
                map.put(dictBudget.code, sum);
            }
            
            current.set(Calendar.DATE, current.get(Calendar.DATE) + 1);
            next.set(Calendar.DATE, next.get(Calendar.DATE) + 1);
        }
        return mapArrayList;
    }

    private int doReportPart(Map<String, HSSFCellStyle> styleMap, HSSFSheet sheet, int rownum, String type, String org, String period, List<DictBudgetDto> dictBudgets, ArrayList<Map<String, Double>> data) {
        HSSFRow row = sheet.createRow(rownum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("Таблица № 1");
        cell.setCellStyle(styleMap.get("caption"));
        // 2 строка
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Поступление " + type + "  денежных средств " + org + " за период ");
        cell.setCellStyle(styleMap.get("caption"));
        // 3 строка
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue(period);
        cell.setCellStyle(styleMap.get("caption"));
        // 4 строка
        row = sheet.createRow(rownum++);
        row.setHeight((short) 600);
        cell = row.createCell(0);
        cell.setCellValue("Дата");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(1);
        cell.setCellValue("Кл. Карты");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(2);
        cell.setCellValue("СПА");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(3);
        cell.setCellValue("Бар");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(4);
        cell.setCellValue("Фит. Услуги ");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(5);
        cell.setCellValue("Депозит");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(6);
        cell.setCellValue("Прочие");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(7);
        cell.setCellValue("Магазин");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(8);
        cell.setCellValue("Спортивное питание");
        cell.setCellStyle(styleMap.get("headerCell"));
        cell = row.createCell(9);
        cell.setCellValue("Всего нал");
        cell.setCellStyle(styleMap.get("headerCell"));
        int firstRowForFormula = rownum + 1;
        for (Map<String, Double> doubleMap : data) {
            row = sheet.createRow(rownum++);
//            for (int i = 0; i < 10; i++) {
//                cell = row.createCell(i);
//                cell.setCellValue(doubleMap.get("" + i));
//                if (i == 0) {
//                    cell.setCellStyle(styleMap.get("dateCell"));
//                } else {
//                    cell.setCellStyle(styleMap.get("dataCell"));
//                }
//            }
            cell = row.createCell(0);
            cell.setCellStyle(styleMap.get("dateCell"));
            cell.setCellValue(doubleMap.get(REPORT_KEY_DATE));
            for (int i = 0; i < dictBudgets.size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(doubleMap.get(dictBudgets.get(i).code));
                cell.setCellStyle(styleMap.get("dataCell"));
            }
        }
        int lastRowForFormula = rownum;
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Итого");
        cell.setCellStyle(styleMap.get("sumCell"));
        String columns = "BCDEFGHIJ";
        for (int i = 0; i < columns.length(); i++) {
            char c= columns.charAt(i);
            String strFormula = "SUM("+ c + firstRowForFormula + ":"+ c + lastRowForFormula + ")";
            cell = row.createCell(i+1);
            cell.setCellFormula(strFormula);
            cell.setCellStyle(styleMap.get("sumCellData"));
        }

        rownum = rownum + 2;
        return rownum;
    }

    @RequestMapping(value = "/report")
    public void report(
            @RequestParam(name = "start", required = false) Long startDateMillis,
            @RequestParam(name = "end", required = false) Long endDateMillis
    ) {
        Calendar endDateCalendar;
        if (endDateMillis == null) {
            endDateMillis = System.currentTimeMillis();
            endDateCalendar = onlyDate(endDateMillis);
            endDateCalendar.set(Calendar.MONTH, endDateCalendar.get(Calendar.MONTH) + 1);
            endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            endDateCalendar = onlyDate(endDateMillis);
        }
        
        Calendar startDateCalendar;
        if (startDateMillis == null) {
            startDateCalendar = new GregorianCalendar();
            startDateCalendar.setTimeInMillis(endDateCalendar.getTimeInMillis());
            startDateCalendar.set(Calendar.MONTH, startDateCalendar.get(Calendar.MONTH) - 1);
        } else {
            startDateCalendar = onlyDate(startDateMillis);
        }
        
        if (startDateCalendar.after(endDateCalendar)) {
            Calendar temp = startDateCalendar;
            startDateCalendar = endDateCalendar;
            endDateCalendar = temp;
        }
        
        try (ServletOutputStream sout = response.getOutputStream()) {
            HSSFWorkbook workbook = doreport4(startDateCalendar, endDateCalendar);
            String file_name = "report";
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Content-Disposition", "inline;filename=" + file_name + ".xls");
            response.setContentType("application/vnd.ms-excel");
            workbook.write(sout);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private Map<String, HSSFCellStyle> setStyles(HSSFWorkbook workbook) {
        Map<String, HSSFCellStyle> styleMap = new HashMap<>();
        HSSFFont fontBold = workbook.createFont();
        fontBold.setFontName("Arial Cyr");
        fontBold.setFontHeightInPoints((short) 8);
        fontBold.setBold(true);
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial Cyr");
        font.setFontHeightInPoints((short) 8);


        HSSFFont fontCaption1 = workbook.createFont();
        fontCaption1.setColor(HSSFColor.RED.index);
        HSSFCellStyle styleCaption1 = workbook.createCellStyle();
        styleCaption1.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        styleCaption1.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
        styleCaption1.setFillPattern(HSSFCellStyle.LESS_DOTS);
        styleCaption1.setFont(fontCaption1);
        styleCaption1.setWrapText(true);
        styleMap.put("styleCaption1", styleCaption1);

        HSSFCellStyle styleCaption = workbook.createCellStyle();
        styleCaption.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleCaption.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleCaption.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleCaption.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleCaption.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleCaption.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleCaption.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleCaption.setFont(font);
        styleCaption.setWrapText(true);
        styleMap.put("styleCaption", styleCaption);

        HSSFCellStyle styleCapVert = workbook.createCellStyle();
        styleCapVert.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleCapVert.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleCapVert.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleCapVert.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleCapVert.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleCapVert.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleCapVert.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleCapVert.setRotation((short) 90);
        styleCapVert.setFont(font);
        styleCapVert.setWrapText(true);
        styleMap.put("styleCapVert", styleCapVert);

        HSSFCellStyle styleCapVertCentr = workbook.createCellStyle();
        styleCapVertCentr.cloneStyleFrom(styleCapVert);
        styleCapVertCentr.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleCapVertCentr.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleMap.put("styleCapVertCentr", styleCapVertCentr);


        HSSFCellStyle styleBody = workbook.createCellStyle();
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setWrapText(true);
        styleMap.put("styleBody", styleBody);

        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(fontBold);
        styleMap.put("caption", styleBody);

        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setWrapText(true);
        styleMap.put("headerCell", styleBody);
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(font);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        short df = workbook.createDataFormat().getFormat("dd.mm.yyyy");
        styleBody.setDataFormat(df);
        styleBody.setWrapText(true);
        styleMap.put("dateCell", styleBody);
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleBody.setFont(font);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styleMap.put("dataCell", styleBody);
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THICK);
        styleMap.put("sumCell", styleBody);
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THICK);
        styleBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styleMap.put("sumCellData", styleBody);
        return styleMap;
    }


}