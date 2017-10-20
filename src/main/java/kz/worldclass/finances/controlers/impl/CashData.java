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
import java.util.ArrayList;
import java.util.HashMap;
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


    public HSSFWorkbook doreport4() {
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
        // 1 строка
        int rownum = 0;
        HSSFRow row = sheet.createRow(rownum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("Таблица № 1");
        cell.setCellStyle(styleMap.get("caption"));
        String period = "с 01.07.17 по 31.07.17";
        rownum = doReportPart(styleMap, sheet, rownum, "наличных", "Мега-Фитнес", period, getData());
        rownum = doReportPart(styleMap, sheet, rownum, "безналичных", "Мега-Фитнес", period, getData());
        rownum = doReportPart(styleMap, sheet, rownum, "POS-терминалу", "Мега-Фитнес", period, getData());
        rownum = doReportPart(styleMap, sheet, rownum, "всего", "Мега-Фитнес", period, getData());
        sheet = workbook.createSheet("Свод 6");
        rownum = 0;
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Анализ финансово-хозяйственной деятельности ");
        cell.setCellStyle(styleMap.get("caption"));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("ТОО \"Мега Фитнес\" за октябрь 2017 г");
        cell.setCellStyle(styleMap.get("caption"));
        return workbook;
    }


    private ArrayList<Map<String, Double>> getData() {
        ArrayList<Map<String, Double>> mapArrayList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Double> map = new HashMap<>();
            map.put("0", 4564564D);
            map.put("1", 4564564D);
            map.put("2", 4564564D);
            map.put("3", 4564564D);
            map.put("4", 4564564D);
            map.put("5", 4564564D);
            map.put("6", 4564564D);
            map.put("7", 4564564D);
            map.put("8", 4564564D);
            map.put("9", 4564564D);
            mapArrayList.add(map);
        }
        return mapArrayList;
    }

    private int doReportPart(Map<String, HSSFCellStyle> styleMap, HSSFSheet sheet, int rownum, String type, String org, String period, ArrayList<Map<String, Double>> data) {
        HSSFRow row;
        Cell cell;
        // 2 строка
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Поступление " + type + "  денежных средств " + org + " за период ");
        cell.setCellStyle(styleMap.get("captionCenter"));
        // 3 строка
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue(period);
        cell.setCellStyle(styleMap.get("captionCenter"));
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
            for (int i = 0; i < 10; i++) {
                cell = row.createCell(i);
                cell.setCellValue(doubleMap.get("" + i));
                if (i == 0) {
                    cell.setCellStyle(styleMap.get("dateCell"));
                } else {
                    cell.setCellStyle(styleMap.get("dataCell"));
                }
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
    public void report() {
        try (ServletOutputStream sout = response.getOutputStream()) {
            HSSFWorkbook workbook = doreport4();
            String file_name = "report";
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Content-Disposition", "inline;filename=" + file_name + ".xls");
            response.setContentType("application/vnd.ms-excel");
            workbook.write(sout);
        } catch (IOException e) {
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

        HSSFCellStyle styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        styleBody.setFont(fontBold);
        styleMap.put("caption", styleBody);

        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(fontBold);
        styleMap.put("captionCenter", styleBody);

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