package kz.worldclass.finances.controlers.impl;

import kz.worldclass.finances.controlers.AbstractRestController;
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
import java.awt.image.AreaAveragingScaleFilter;
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        doSheet1(workbook);
        doSheet2(workbook);
        return workbook;
    }

    private void doSheet2(HSSFWorkbook workbook) {
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet;
        int rownum;
        HSSFRow row;
        Cell cell;
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
        rownum++;
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Приход");
        cell.setCellStyle(styleMap.get("captionCenter"));
        doIncomeHeader(sheet, row, 3, "АСТАНА");
        doIncomeHeader(sheet, row, 6, "АКТОБЕ");
        doIncomeHeader(sheet, row, 9, "КАРАГАНДА");
        doIncomeHeader(sheet, row, 12, "АТЫРАУ");
        doIncomeHeader(sheet, row, 15, "ПО СЕТИ");
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
        cell = row.createCell(2);
        cell.setCellValue("Назначение");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
        doIncomeTableHeader(row, 3, HSSFColor.AQUA.index);
        doIncomeTableHeader(row, 6, HSSFColor.OLIVE_GREEN.index);
        doIncomeTableHeader(row, 9, HSSFColor.BRIGHT_GREEN.index);
        doIncomeTableHeader(row, 12, HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
        doIncomeTableHeader(row, 15, HSSFColor.LIGHT_YELLOW.index);

        ArrayList<Map<String, String>> mapArrayList = getIncomeData6();
        int firstRowForFormula = rownum + 1;
        for (Map<String, String> mapRow : mapArrayList) {
            row = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
            cell = row.createCell(0);
            cell.setCellValue(mapRow.get("num"));
            cell.setCellStyle(styleMap.get("dateCell"));
            cell = row.createCell(2);
            cell.setCellValue(mapRow.get("name"));
            cell.setCellStyle(styleMap.get("dataCellLeft"));
            cell = row.createCell(3);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(4);
            cell.setCellValue(Double.parseDouble(mapRow.get("ASTANA")));
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(5);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(6);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(7);
            cell.setCellValue(Double.parseDouble(mapRow.get("AKTOBE")));
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(8);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(9);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(10);
            cell.setCellValue(Double.parseDouble(mapRow.get("KARAGANDA")));
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(11);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(12);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(13);
            cell.setCellValue(Double.parseDouble(mapRow.get("ATYRAU")));
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(14);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(15);
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(16);
            cell.setCellValue(Double.parseDouble(mapRow.get("ATYRAU")));
            cell.setCellStyle(styleMap.get("dataCell"));
            cell = row.createCell(17);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        int lastRowForFormula = rownum;
        row = sheet.createRow(rownum++);
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
        cell = row.createCell(0);
        cell.setCellStyle(styleMap.get("sumCellGrey"));
        cell = row.createCell(2);
        cell.setCellValue("ВСЕГО ДОХОДЫ:");
        cell.setCellStyle(styleMap.get("sumCellGrey"));
        String columns = "DEFGHIJKLMNOPQR";
        for (int i = 0; i < columns.length(); i++) {
            char c = columns.charAt(i);
            String strFormula = "SUM(" + c + firstRowForFormula + ":" + c + lastRowForFormula + ")";
            cell = row.createCell(i + 3);
            cell.setCellFormula(strFormula);
        }

        rownum++;
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 2));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Расход");
        cell.setCellStyle(styleMap.get("captionCenter"));
        doIncomeHeader(sheet, row, 3, "АСТАНА");
        doIncomeHeader(sheet, row, 6, "АКТОБЕ");
        doIncomeHeader(sheet, row, 9, "КАРАГАНДА");
        doIncomeHeader(sheet, row, 12, "АТЫРАУ");
        doIncomeHeader(sheet, row, 15, "ПО СЕТИ");
        sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("код статьи");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
        cell = row.createCell(2);
        cell.setCellValue("название");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
        doIncomeTableHeader(row, 3, HSSFColor.AQUA.index);
        doIncomeTableHeader(row, 6, HSSFColor.OLIVE_GREEN.index);
        doIncomeTableHeader(row, 9, HSSFColor.BRIGHT_GREEN.index);
        doIncomeTableHeader(row, 12, HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
        doIncomeTableHeader(row, 15, HSSFColor.LIGHT_YELLOW.index);
        ArrayList<Map<String, Object>> coasts = getCosts();
        ArrayList<Integer> coastRowsAgg = new ArrayList<>();
        for (Map<String, Object> coast : coasts) {
            row = sheet.createRow(rownum++);
            coastRowsAgg.add(Integer.valueOf(row.getRowNum()));
            cell = row.createCell(0);
            cell.setCellStyle(styleMap.get("captionCenter"));
            cell.setCellValue((String) coast.get("code"));
            cell = row.createCell(2);
            cell.setCellValue((String) coast.get("name"));
            ArrayList<Map<String, String>> subCoasts = (ArrayList<Map<String, String>>) coast.get("subRows");
            columns = "EHKNQ";

            for (int i = 0; i < columns.length(); i++) {
                char c = columns.charAt(i);
                cell = row.createCell(4 + i * 3);
                String rowNum = "" + (row.getRowNum() + 2);
                String rowNum1 = "" + (row.getRowNum() + 1 + subCoasts.size());
                String formula = "SUM(" + c + rowNum + ":" + c + rowNum1 + ")";
                cell.setCellFormula(formula);
            }
            for (Map<String, String> subCoast : subCoasts) {
                row = sheet.createRow(rownum++);
                cell = row.createCell(1);
                cell.setCellValue(subCoast.get("code"));
                cell = row.createCell(2);
                cell.setCellValue((String) subCoast.get("name"));
                cell = row.createCell(4);
                cell.setCellValue(Double.parseDouble(subCoast.get("val1")));
                cell = row.createCell(7);
                cell.setCellValue(Double.parseDouble(subCoast.get("val2")));
                cell = row.createCell(10);
                cell.setCellValue(Double.parseDouble(subCoast.get("val3")));
                cell = row.createCell(13);
                cell.setCellValue(Double.parseDouble(subCoast.get("val4")));
                cell = row.createCell(16);
                String rowNum = "" + (row.getRowNum() + 1);
                String formula = "E" + rowNum + "+H" + rowNum + "+K" + rowNum + "+N" + rowNum + "";
                cell.setCellFormula(formula);
            }
        }
        for (Integer integer : coastRowsAgg) {
            System.out.println("rows="+integer);
        }

    }

    private ArrayList<Map<String, Object>> getCosts() {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("code", "1000");
        row.put("name", "Аренда");
        ArrayList<Map<String, String>> subRowsList = new ArrayList<>();
        Map<String, String> subRow = new HashMap<>();
        subRow.put("code", "1010");
        subRow.put("name", "Аренда склада");
        subRow.put("val1", "12000");
        subRow.put("val2", "5200");
        subRow.put("val3", "1520");
        subRow.put("val4", "14000");
        subRowsList.add(subRow);
        subRow = new HashMap<>();
        subRow.put("code", "1020");
        subRow.put("name", "Аренда помещений");
        subRow.put("val1", "12000");
        subRow.put("val2", "3500");
        subRow.put("val3", "9520");
        subRow.put("val4", "54000");
        subRowsList.add(subRow);
        subRow = new HashMap<>();
        subRow.put("code", "1030");
        subRow.put("name", "Аренда квартиры");
        subRow.put("val1", "120");
        subRow.put("val2", "7500");
        subRow.put("val3", "9550");
        subRow.put("val4", "5500");
        subRowsList.add(subRow);
        row.put("subRows", subRowsList);
        result.add(row);
        row = new HashMap<>();
        row.put("code", "2000");
        row.put("name", "Аренда");
        subRowsList = new ArrayList<>();
        subRow = new HashMap<>();
        subRow.put("code", "2010");
        subRow.put("name", "Аренда склада");
        subRow.put("val1", "12000");
        subRow.put("val2", "5200");
        subRow.put("val3", "1520");
        subRow.put("val4", "14000");
        subRowsList.add(subRow);
        subRow = new HashMap<>();
        subRow.put("code", "2020");
        subRow.put("name", "Аренда помещений");
        subRow.put("val1", "12000");
        subRow.put("val2", "3500");
        subRow.put("val3", "9520");
        subRow.put("val4", "54000");
        subRowsList.add(subRow);
        subRow = new HashMap<>();
        subRow.put("code", "2030");
        subRow.put("name", "Аренда квартиры");
        subRow.put("val1", "120");
        subRow.put("val2", "7500");
        subRow.put("val3", "9550");
        subRow.put("val4", "5500");
        subRowsList.add(subRow);
        row.put("subRows", subRowsList);
        result.add(row);
        return result;
    }

    private void doIncomeTableHeader(HSSFRow row, int col, short colorIndex) {
        HSSFWorkbook workbook = row.getSheet().getWorkbook();
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        Cell cell;
        cell = row.createCell(col);
        cell.setCellValue("план");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
        HSSFCellStyle styleBody = workbook.createCellStyle();
        styleBody.cloneStyleFrom(styleMap.get("captionCenterTable"));
        styleBody.setFillForegroundColor(colorIndex);
        styleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cell = row.createCell(col + 1);
        cell.setCellValue("факт");
        cell.setCellStyle(styleBody);
        cell = row.createCell(col + 2);
        cell.setCellValue("%");
        cell.setCellStyle(styleMap.get("captionCenterTable"));
    }

    private void doIncomeHeader(HSSFSheet sheet, HSSFRow row, int col, String filial) {
        Map<String, HSSFCellStyle> styleMap = setStyles(sheet.getWorkbook());
        Cell cell;
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), col, col + 2));
        cell = row.createCell(col);
        cell.setCellValue(filial);
        cell.setCellStyle(styleMap.get("captionCenter"));
    }

    private void doSheet1(HSSFWorkbook workbook) {
        short width = 5024;
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

    private ArrayList<Map<String, String>> getIncomeData6() {
        ArrayList<Map<String, String>> mapArrayList = new ArrayList<>();
        mapArrayList.add(doMapRow(1, "Продажа клубных карт", 54564, 89766, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(2, "Фитнес услуги", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(3, "Прочее", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(4, "СПА салон", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(5, "БАР", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(6, "Магазин", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(7, "Размещение рекламы в клубе", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(8, "Фитнес услуги Radisson", 54564.5, 897665.0, 8789789.7, 6464.8));
        mapArrayList.add(doMapRow(9, "Спортивное питание", 54564.5, 897665.0, 8789789.7, 6464.8));
        return mapArrayList;
    }

    private Map<String, String> doMapRow(int num, String name, double val1, double val2, double val3, double val4) {
        Map<String, String> map = new HashMap<>();
        map.put("num", "" + num);
        map.put("name", name);
        map.put("ASTANA", String.valueOf(val1));
        map.put("AKTOBE", String.valueOf(val2));
        map.put("KARAGANDA", String.valueOf(val3));
        map.put("ATYRAU", String.valueOf(val4));
        return map;

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
            char c = columns.charAt(i);
            String strFormula = "SUM(" + c + firstRowForFormula + ":" + c + lastRowForFormula + ")";
            cell = row.createCell(i + 1);
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
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setFont(fontBold);
        styleMap.put("captionCenterTable", styleBody);

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
        styleBody.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        styleBody.setFont(font);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styleMap.put("dataCellLeft", styleBody);
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
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleMap.put("sumCellGrey", styleBody);
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styleBody.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        styleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleMap.put("sumCellDataGrey", styleBody);
        return styleMap;
    }


}