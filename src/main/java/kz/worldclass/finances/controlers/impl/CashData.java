package kz.worldclass.finances.controlers.impl;

import kz.worldclass.finances.controlers.AbstractRestController;
import kz.worldclass.finances.data.dto.entity.*;
import kz.worldclass.finances.data.dto.entity.base.BaseDictDto;
import kz.worldclass.finances.data.dto.results.cashreport.AffiliateGetDataResult;
import kz.worldclass.finances.data.dto.results.dict.*;
import kz.worldclass.finances.data.dto.results.expensesrequest.GetAffiliateDataResult;
import kz.worldclass.finances.data.entity.DictOrgEntity;
import kz.worldclass.finances.services.*;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.UnexpectedPropertySetTypeException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.rmi.MarshalledObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "/cash")
public class CashData extends AbstractRestController {
    private static final String REPORT_KEY_DATE = "~DATE~";

    @Autowired
    private DictService service;
    @Autowired
    private CashDataService cashDataService;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private CashReportService cashReportService;
    @Autowired
    private ExpensesRequestService expensesRequestService;

    ArrayList<Map<String, Object>> globalData = new ArrayList<>();

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

    List<DictOrgDto> dictOrgs;

    public HSSFWorkbook doreport4(Calendar startDateCalendar, Calendar endDateCalendar) {
        HSSFWorkbook book1 = null;
        book1 = getHssfWorkbookTemplate(book1);
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        SummaryInformation summaryInfo = workbook.getSummaryInformation();
        summaryInfo.setAuthor("robot");
        summaryInfo.setCreateDateTime(new Date());
        summaryInfo.setEditTime(new Date().getTime());
        dictOrgs = cashDataService.getOrgs();
        for (DictOrgDto dictOrg : dictOrgs) {
            if (dictOrg.code.equals("HQ")) {
                dictOrgs.remove(dictOrg);
                break;
            }
        }
        HSSFSheet hssfSheet = null;
        HSSFSheet sheet = null;

        List<BudgetHistoryItemDto> items = cashDataService.getHistoryItems(startDateCalendar.getTime(), endDateCalendar.getTime());

        sheet = book1.getSheetAt(0);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        doSheet1(hssfSheet, startDateCalendar, endDateCalendar, dictOrgs.get(1), items);

        sheet = book1.getSheetAt(1);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        doSheet1(hssfSheet, startDateCalendar, endDateCalendar, dictOrgs.get(0), items);

        sheet = book1.getSheetAt(2);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        doSheet1(hssfSheet, startDateCalendar, endDateCalendar, dictOrgs.get(3), items);

        sheet = book1.getSheetAt(3);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        doSheet1(hssfSheet, startDateCalendar, endDateCalendar, dictOrgs.get(2), items);

        sheet = book1.getSheetAt(4);
        ExcelCopy.copySheets(workbook.createSheet(sheet.getSheetName()), sheet, true);
        sheet = book1.getSheetAt(5);
        ExcelCopy.copySheets(workbook.createSheet(sheet.getSheetName()), sheet, true);

        sheet = book1.getSheetAt(6);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        doSheet6(hssfSheet, startDateCalendar, endDateCalendar);
//
//        doSheet2(workbook);
//        doSheet3(workbook);
//        doSheet4(workbook);
        return workbook;
    }

    private void doSheet6(HSSFSheet hssfSheet, Calendar startDateCalendar, Calendar endDateCalendar) {
        HSSFSheet sheet;
        int rownum;
        HSSFRow row;
        Cell cell;
        sheet = hssfSheet;
        rownum = 7;
        ArrayList<Map<String, Object>> data = loadDataFor_4_Rep(startDateCalendar, endDateCalendar);
        ArrayList<Integer> rowsNumUpper = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            sheet.shiftRows(rownum, sheet.getLastRowNum() + 1, 1);
            row = sheet.getRow(rownum++);
            ExcelCopy.copyRow(sheet, sheet, sheet.getRow(4), row);
            rowsNumUpper.add(rownum);
            HSSFRow sumRowUp = row;
            cell = row.getCell(1);
            cell.setCellValue((String) datum.get("code"));
            cell = row.getCell(3);
            cell.setCellValue((String) datum.get("name"));
            ArrayList<Map<String, Object>> child = (ArrayList<Map<String, Object>>) datum.get("child");
            ArrayList<Integer> rowsNumUp = new ArrayList<>();
            for (Map<String, Object> children : child) {
                sheet.shiftRows(rownum, sheet.getLastRowNum() + 1, 1);
                row = sheet.getRow(rownum++);
                ExcelCopy.copyRow(sheet, sheet, sheet.getRow(5), row);
                rowsNumUp.add(rownum);
                HSSFRow sumRow = row;
                cell = row.getCell(2);
                cell.setCellValue((String) children.get("code"));

                cell = row.getCell(3);
                cell.setCellValue((String) children.get("name"));

                ArrayList<Map<String, Object>> subchild = (ArrayList<Map<String, Object>>) children.get("child");
                ArrayList<Integer> rowsNum = new ArrayList<>();
                for (Map<String, Object> subchildren : subchild) {
                    sheet.shiftRows(rownum, sheet.getLastRowNum() + 1, 1);
                    row = sheet.getRow(rownum++);
                    ExcelCopy.copyRow(sheet, sheet, sheet.getRow(6), row);
                    rowsNum.add(rownum);
                    cell = row.getCell(0);
                    cell.setCellValue((Date) subchildren.get("date"));
                    cell.getCellType();
                    cell = row.getCell(3);
                    cell.setCellValue((String) subchildren.get("note"));
                    Long org = ((BigInteger) subchildren.get("org")).longValue();
                    int cellCol = 4;
                    switch (org.intValue()) {
                        case 1:
                            cellCol = 4;
                            break;
                        case 3:
                            cellCol = 7;
                            break;
                        case 4:
                            cellCol = 10;
                            break;
                        case 5:
                            cellCol = 13;
                            break;
                    }
                    cell = row.getCell(cellCol);
                    cell.setCellValue((Double) subchildren.get("item_value"));

                }
                String columns = "EFGHIJKLMNOP";
                for (int i = 0; i < columns.length(); i++) {
                    char c = columns.charAt(i);
                    cell = sumRow.getCell(i + 4);
                    String f = "";
                    for (int j = 0; j < rowsNum.size(); j++) {
                        f = f + c + rowsNum.get(j);
                        if (j < rowsNum.size() - 1) {
                            f = f + "+";
                        }
                    }
                    if (rowsNum.size() > 0) {
                        cell.setCellFormula(f);

                    }
                }
            }
            String columns = "EFGHIJKLMNOP";
            for (int i = 0; i < columns.length(); i++) {
                char c = columns.charAt(i);
                cell = sumRowUp.getCell(i + 4);
                String f = "";
                for (int j = 0; j < rowsNumUp.size(); j++) {
                    f = f + c + rowsNumUp.get(j);
                    if (j < rowsNumUp.size() - 1) {
                        f = f + "+";
                    }
                }
                if (rowsNumUp.size() > 0) {
                    cell.setCellFormula(f);

                }
            }
        }
        String columns = "EFGHIJKLMNOP";
        // sheet.shiftRows(rownum, sheet.getLastRowNum()+1, 1);
        row = sheet.getRow(rownum++);
        cell = row.getCell(3);
        cell.setCellValue("ИТОГО");

        for (int i = 0; i < columns.length(); i++) {
            char c = columns.charAt(i);
            cell = row.getCell(i + 4);
            String f = "";
            for (int j = 0; j < rowsNumUpper.size(); j++) {
                f = f + c + rowsNumUpper.get(j);
                if (j < rowsNumUpper.size() - 1) {
                    f = f + "+";
                }
            }
            if (rowsNumUpper.size() > 0) {
                cell.setCellFormula(f);

            }
        }
        sheet.shiftRows(4, sheet.getLastRowNum(), -1);
        sheet.shiftRows(4, sheet.getLastRowNum(), -1);
        sheet.shiftRows(4, sheet.getLastRowNum(), -1);
    }


    private void doSheet3(HSSFWorkbook workbook) {
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet;
        int rownum;
        HSSFRow row;
        Cell cell;
        sheet = workbook.createSheet("Остатки");
        rownum = 1;
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("Входящие остатки на 1/10/2017");
        rownum++;
        row = sheet.createRow(rownum++);
        cell = row.createCell(2);
        cell.setCellValue("Фактические остатки ДС");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("sumCellDataGreen"));
        }
        row.getCell(4).setCellValue("АСТАНА");
        row.getCell(5).setCellValue("АКТОБЕ");
        row.getCell(6).setCellValue("КАРАГНДА");
        row.getCell(7).setCellValue("АТЫРАУ");
        row.getCell(8).setCellValue("ПО СЕТИ");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("МЕГАФИТНЕС");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("касса");
        row.getCell(3).setCellValue("собственные");
        row.getCell(8).setCellFormula("SUM(E7:H7)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(3).setCellValue("Федерация Аэробики");
        row.getCell(8).setCellFormula("SUM(E8:H8)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("банк");
        row.getCell(3).setCellValue("собственные");
        row.getCell(8).setCellFormula("SUM(E9:H9)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(3).setCellValue("руб в эквиваленте тг");
        row.getCell(8).setCellFormula("SUM(E10:H10)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("ЦБ  тенге");
        row.getCell(8).setCellFormula("SUM(E11:H11)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("ЦБ долл");
        row.getCell(8).setCellFormula("SUM(E12:H12)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("ККБ долл");
        row.getCell(8).setCellFormula("SUM(E13:H13)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("ЦБ руб");
        row.getCell(8).setCellFormula("SUM(E14:H14)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("терминал");
        row.getCell(8).setCellFormula("SUM(E14:H14)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(2).setCellValue("терминал");
        row.getCell(8).setCellFormula("SUM(E15:H15)");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 7; i++) {
            cell = row.createCell(i + 2);
            cell.setCellStyle(styleMap.get("dataCell"));
        }
        row.getCell(4).setCellFormula("SUM(E7:E15)");
        row.getCell(5).setCellFormula("SUM(F7:F15)");
        row.getCell(6).setCellFormula("SUM(G7:G15)");
        row.getCell(7).setCellFormula("SUM(H7:H15)");
        row.getCell(8).setCellFormula("SUM(I7:I15)");
    }

    private void doSheet4(HSSFWorkbook workbook, Calendar startDateCalendar, Calendar endDateCalendar) {
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet;
        int rownum;
        HSSFRow row;
        Cell cell;
        sheet = workbook.createSheet("4_свод");
        rownum = 1;
        row = sheet.createRow(rownum++);
        cell = row.createCell(3);
        cell.setCellValue("Расходование ДС");
        row = sheet.createRow(rownum++);
        cell = row.createCell(1);
        cell.setCellValue("Расход");
        row.createCell(4).setCellValue("АСТАНА");
        row.createCell(7).setCellValue("АКТОБЕ");
        row.createCell(10).setCellValue("КАРАГНДА");
        row.createCell(13).setCellValue("АТЫРАУ");
        row = sheet.createRow(rownum++);
        for (int i = 0; i < 17; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styleMap.get("sumCellDataGreen"));
        }
        row.getCell(0).setCellValue("Дата");
        row.getCell(1).setCellValue("код статьи");
        row.getCell(3).setCellValue("Статья бюджета");
        row.getCell(4).setCellValue("Собственные");
        row.getCell(5).setCellValue("СПА");
        row.getCell(6).setCellValue("КАФЕ");
        row.getCell(7).setCellValue("Собственные");
        row.getCell(8).setCellValue("СПА");
        row.getCell(9).setCellValue("КАФЕ");
        row.getCell(10).setCellValue("Собственные");
        row.getCell(11).setCellValue("СПА");
        row.getCell(12).setCellValue("КАФЕ");
        row.getCell(13).setCellValue("Собственные");
        row.getCell(14).setCellValue("СПА");
        row.getCell(15).setCellValue("КАФЕ");
        ArrayList<Map<String, Object>> data = loadDataFor_4_Rep(startDateCalendar, endDateCalendar);
        ArrayList<Integer> rowsNumUpper = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            row = sheet.createRow(rownum++);
            rowsNumUpper.add(rownum);
            HSSFRow sumRowUp = row;
            cell = row.createCell(1);
            cell.setCellValue((String) datum.get("code"));
            cell.setCellStyle(styleMap.get("sumCellGrey"));
            cell = row.createCell(3);
            cell.setCellValue((String) datum.get("name"));
            cell.setCellStyle(styleMap.get("sumCellGrey"));
            ArrayList<Map<String, Object>> child = (ArrayList<Map<String, Object>>) datum.get("child");
            ArrayList<Integer> rowsNumUp = new ArrayList<>();
            for (Map<String, Object> children : child) {
                row = sheet.createRow(rownum++);
                rowsNumUp.add(rownum);
                HSSFRow sumRow = row;
                cell = row.createCell(2);
                cell.setCellValue((String) children.get("code"));
                cell.setCellStyle(styleMap.get("sumCellGrey"));
                cell = row.createCell(3);
                cell.setCellValue((String) children.get("name"));
                cell.setCellStyle(styleMap.get("sumCellGrey"));
                ArrayList<Map<String, Object>> subchild = (ArrayList<Map<String, Object>>) children.get("child");
                ArrayList<Integer> rowsNum = new ArrayList<>();
                for (Map<String, Object> subchildren : subchild) {
                    row = sheet.createRow(rownum++);
                    rowsNum.add(rownum);
                    CellStyle cellStyle = workbook.createCellStyle();
                    CreationHelper createHelper = workbook.getCreationHelper();
                    cellStyle.setDataFormat(
                            createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
                    cell = row.createCell(0);
                    cell.setCellValue((Date) subchildren.get("date"));
                    cell.setCellStyle(cellStyle);
                    cell.getCellType();
                    cell = row.createCell(3);
                    cell.setCellValue((String) subchildren.get("note"));
                    Long org = ((BigInteger) subchildren.get("org")).longValue();
                    int cellCol = 4;
                    switch (org.intValue()) {
                        case 1:
                            cellCol = 4;
                            break;
                        case 3:
                            cellCol = 7;
                            break;
                        case 4:
                            cellCol = 10;
                            break;
                        case 5:
                            cellCol = 13;
                            break;
                    }
                    cell = row.createCell(cellCol);
                    cell.setCellValue((Double) subchildren.get("item_value"));

                }
                String columns = "EFGHIJKLMNOP";
                for (int i = 0; i < columns.length(); i++) {
                    char c = columns.charAt(i);
                    cell = sumRow.createCell(i + 4);
                    String f = "";
                    for (int j = 0; j < rowsNum.size(); j++) {
                        f = f + c + rowsNum.get(j);
                        if (j < rowsNum.size() - 1) {
                            f = f + "+";
                        }
                    }
                    if (rowsNum.size() > 0) {
                        cell.setCellFormula(f);
                        cell.setCellStyle(styleMap.get("sumCellGrey"));
                    }
                }
            }
            String columns = "EFGHIJKLMNOP";
            for (int i = 0; i < columns.length(); i++) {
                char c = columns.charAt(i);
                cell = sumRowUp.createCell(i + 4);
                String f = "";
                for (int j = 0; j < rowsNumUp.size(); j++) {
                    f = f + c + rowsNumUp.get(j);
                    if (j < rowsNumUp.size() - 1) {
                        f = f + "+";
                    }
                }
                if (rowsNumUp.size() > 0) {
                    cell.setCellFormula(f);
                    cell.setCellStyle(styleMap.get("sumCellGrey"));
                }
            }
        }
        String columns = "EFGHIJKLMNOP";
        row = sheet.createRow(rownum++);
        cell = row.createCell(3);
        cell.setCellValue("ИТОГО");
        cell.setCellStyle(styleMap.get("sumCellGrey"));
        for (int i = 0; i < columns.length(); i++) {
            char c = columns.charAt(i);
            cell = row.createCell(i + 4);
            String f = "";
            for (int j = 0; j < rowsNumUpper.size(); j++) {
                f = f + c + rowsNumUpper.get(j);
                if (j < rowsNumUpper.size() - 1) {
                    f = f + "+";
                }
            }
            if (rowsNumUpper.size() > 0) {
                cell.setCellFormula(f);
                cell.setCellStyle(styleMap.get("sumCellGrey"));
            }
        }
    }

    private ArrayList<Map<String, Object>> loadDataFor_4_Rep(Calendar startDateCalendar, Calendar endDateCalendar) {
        Map<Long, Object> budRows = new HashMap<>();
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        List<DictBudgetDto> budgetDtoList = cashDataService.getBudget();
        for (DictBudgetDto dictBudgetDto : budgetDtoList) {
            if (dictBudgetDto.outgo) {
                if (dictBudgetDto.parent.id == null) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("code", dictBudgetDto.code);
                    row.put("id", dictBudgetDto.id);
                    row.put("name", dictBudgetDto.name);
                    ArrayList<Map<String, Object>> child = new ArrayList<>();
                    row.put("child", child);
                    res.add(row);
                    budRows.put(dictBudgetDto.id, row);
                }
            }
        }
        for (DictBudgetDto dictBudgetDto : budgetDtoList) {
            if (dictBudgetDto.outgo) {
                if (dictBudgetDto.parent.id != null) {
                    Map<String, Object> row = new HashMap<>();
                    for (Map<String, Object> addrows : res) {
                        if (addrows.get("id").equals(dictBudgetDto.parent.id)) {
                            ArrayList<Map<String, Object>> child = (ArrayList<Map<String, Object>>) addrows.get("child");
                            child.add(row);
                            row.put("code", dictBudgetDto.code);
                            row.put("id", dictBudgetDto.id);
                            row.put("name", dictBudgetDto.name);
                            child = new ArrayList<>();
                            row.put("child", child);
                            budRows.put(dictBudgetDto.id, row);
                            break;
                        }
                    }

                    //   res.add(row);
                }
            }
        }
        addDBData(res, budRows, startDateCalendar, endDateCalendar);
        return res;
    }

    private void addDBData(ArrayList<Map<String, Object>> res, Map<Long, Object> budRows, Calendar startDateCalendar, Calendar endDateCalendar) {
        Session session = sessionFactory.openSession();
        String sql = "select\n" +
                "    nc.CHANGE_DATE,\n" +
                "    nc.ORG_ID,\n" +
                "    nci.ITEM_VALUE,\n" +
                "    nci.BUDGET_TYPE_ID,\n" +
                "    n.NOTE_VALUE\n" +
                "from\n" +
                "    DICT_BUDGET_NEXT_CHANGE_TYPE dnct,\n" +
                "    BUDGET_NEXT_CHANGE nc,\n" +
                "    BUDGET_NEXT_CHANGE_ITEM nci,\n" +
                "    NOTES n\n" +
                "where\n" +
                "    dnct.CODE = 'EXPENSES_REQUEST'\n" +
                "    and\n" +
                "    nc.TYPE_ID = dnct.ID\n" +
                "    and\n" +
                "    nci.CHANGE_ID = nc.ID \n" +
                "and \n" +
                "nci.NOTE_ID=n.id and nc.CHANGE_DATE>=:p1 and nc.CHANGE_DATE<:p2 order by nc.CHANGE_DATE";
        List<Object[]> objects = session.createSQLQuery(sql)
                .setDate("p1", startDateCalendar.getTime()).setDate("p2", endDateCalendar.getTime()).list();
        session.close();
        for (Object[] data : objects) {
            Long id = ((BigInteger) data[3]).longValue();
            Map<String, Object> row = (Map<String, Object>) budRows.get(id);
            if (row != null) {
                ArrayList<Map<String, Object>> child = (ArrayList<Map<String, Object>>) row.get("child");
                Map<String, Object> subrow = new HashMap<>();
                subrow.put("org", data[1]);
                subrow.put("date", data[0]);
                subrow.put("item_value", data[2]);
                subrow.put("note", data[4]);
                child.add(subrow);
            }
        }
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

    private ArrayList<Map<String, String>> getIncomeData6() {
        ArrayList<Map<String, String>> mapArrayList = new ArrayList<>();

        mapArrayList.add(doMapRowFormula(1, "Продажа клубных карт", "B"));
        mapArrayList.add(doMapRowFormula(1, "Фитнес услуги", "E"));
        mapArrayList.add(doMapRowFormula(1, "Прочее", "FG"));
        mapArrayList.add(doMapRowFormula(1, "СПА салон", "C"));
        mapArrayList.add(doMapRowFormula(1, "БАР", "D"));
        mapArrayList.add(doMapRowFormula(1, "Магазин", "H"));
        mapArrayList.add(doMapRowFormula(1, "Размещение рекламы в клубе", "Z"));
        mapArrayList.add(doMapRowFormula(1, "Фитнес услуги Radisson", "Z"));
        mapArrayList.add(doMapRowFormula(1, "Спортивное питание", "I"));

        return mapArrayList;
    }

    private Map<String, String> doMapRowFormula(int num, String name, String colChar) {
        Map<String, String> map = new HashMap<>();
        map.put("num", "" + num);
        map.put("name", name);
        for (Map<String, Object> datum : globalData) {
            String org = ((HSSFSheet) datum.get("sheet")).getSheetName();
            String f = "";
            for (int i = 0; i < colChar.length(); i++) {
                char c = colChar.charAt(i);
                f = f + "'" + org + "'!" + c + datum.get("lastRow");
                if (i < colChar.length() - 1) {
                    f = f + "+";
                }
            }
            map.put(org, f);
        }
        return map;

    }

    private void doSheet2(HSSFWorkbook workbook) {
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet;
        int rownum;
        HSSFRow row;
        Cell cell;
        sheet = workbook.createSheet("СВОД № 6");
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
            for (int i = 3; i < 18; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styleMap.get("dataCell"));
            }
            cell = row.getCell(4);
            cell.setCellFormula(mapRow.get(dictOrgs.get(1).name));
            cell = row.getCell(7);
            cell.setCellFormula(mapRow.get(dictOrgs.get(0).name));
            cell = row.getCell(10);
            cell.setCellFormula(mapRow.get(dictOrgs.get(3).name));
            cell = row.getCell(13);
            cell.setCellFormula(mapRow.get(dictOrgs.get(2).name));
            cell = row.getCell(16);
            String r = "" + (row.getRowNum() + 1);
            cell.setCellFormula("E" + r + "+H" + r + "+K" + r + "+N" + r);
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
        cell.setCellValue("Расход - Данные условные");
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
            coastRowsAgg.add(Integer.valueOf(row.getRowNum() + 1));
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
        columns = "EHKNQ";
        row = sheet.createRow(rownum++);
        cell = row.createCell(0);
        cell.setCellValue("ВСЕГО");
        cell.setCellStyle(styleMap.get("sumCellGrey"));
        for (int i = 0; i < columns.length(); i++) {
            char c = columns.charAt(i);
            cell = row.createCell(4 + i * 3);
            String formula = "";
            for (int j = 0; j < coastRowsAgg.size(); j++) {
                formula = formula + c + coastRowsAgg.get(j);
                if (j < coastRowsAgg.size() - 1) {
                    formula = formula + "+";
                }
            }
            cell.setCellFormula(formula);
            cell.setCellStyle(styleMap.get("sumCellGrey"));
        }
    }

    private ArrayList<Map<String, Double>> getData(String storeTypeCode, Calendar startDateCalendar, Calendar endDateCalendar, List<DictBudgetDto> dictBudgets, List<BudgetHistoryItemDto> items, DictOrgDto orgDto) {
        Calendar current = new GregorianCalendar();
        current.setTimeInMillis(startDateCalendar.getTimeInMillis());

        Calendar next = new GregorianCalendar();
        next.setTimeInMillis(current.getTimeInMillis());
        next.set(Calendar.DATE, next.get(Calendar.DATE) + 1);

        ArrayList<Map<String, Double>> mapArrayList = new ArrayList<>();
        while (current.before(endDateCalendar)) {
            long currentMillis = current.getTimeInMillis();
            long nextMillis = next.getTimeInMillis();

            Map<String, Double> map = new HashMap<>();
            mapArrayList.add(map);

            map.put(REPORT_KEY_DATE, new Long(currentMillis).doubleValue());

            for (DictBudgetDto dictBudget : dictBudgets) {
                Double sum = 0D;
                for (BudgetHistoryItemDto item : items) {
                    if (
                            ((storeTypeCode == null) || (storeTypeCode.equals(item.storeType.code)))
                                    && (dictBudget.code.equals(item.budgetType.code))
                                    && (item.history.changeDate >= currentMillis)
                                    && (item.history.changeDate < nextMillis)
                                    && (item.itemValue != null)
                                    && (item.history.org.code.equals(orgDto.code))
                            ) {
                        if (dictBudget.outgo) sum -= item.itemValue;
                        else sum += item.itemValue;
                    }
                }

                map.put(dictBudget.code, sum);
            }

            current.set(Calendar.DATE, current.get(Calendar.DATE) + 1);
            next.set(Calendar.DATE, next.get(Calendar.DATE) + 1);
        }
        return mapArrayList;
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

    private void doSheet1(HSSFSheet sheet, Calendar startDateCalendar, Calendar endDateCalendar, DictOrgDto org, List<BudgetHistoryItemDto> items) {
        List<DictBudgetDto> dictBudgets = cashDataService.getEnabledIncomingLeafs();
        doIncomeRows(sheet, startDateCalendar, endDateCalendar, org, dictBudgets, items, 4, "CASH");
        doIncomeRows(sheet, startDateCalendar, endDateCalendar, org, dictBudgets, items, 41, "CASHLESS_BANK");
        doIncomeRows(sheet, startDateCalendar, endDateCalendar, org, dictBudgets, items, 78, "CASHLESS_TERMINAL");


    }

    private void doIncomeRows(HSSFSheet sheet, Calendar startDateCalendar, Calendar endDateCalendar, DictOrgDto org, List<DictBudgetDto> dictBudgets, List<BudgetHistoryItemDto> items, int rowmum, String type) {
        ArrayList<Map<String, Double>> maps = getData(type, startDateCalendar, endDateCalendar, dictBudgets, items, org);
        HSSFRow row = null;
        HSSFCell cell = null;
        for (Map<String, Double> dataRow : maps) {
            row = sheet.getRow(rowmum++);
            cell = row.getCell(0);

            Double dateDouble = dataRow.get(REPORT_KEY_DATE);
            if (dateDouble != null) {
                long dateMillis = dateDouble.longValue();
                Date date = new Date(dateMillis);
                cell.setCellValue(date);
            }

            int i = 0;
            for (i = 0; i < dictBudgets.size(); i++) {
                cell = row.getCell(i + 1);
                cell.setCellValue(dataRow.get(dictBudgets.get(i).code));
            }
        }
    }


    private void doSheet1(HSSFWorkbook workbook, Calendar startDateCalendar, Calendar endDateCalendar, DictOrgDto org) {
        short width = 5024;
        Map<String, HSSFCellStyle> styleMap = setStyles(workbook);
        HSSFSheet sheet = workbook.createSheet(org.name);
        Map<String, Object> glob = new HashMap<>();
        glob.put("sheet", sheet);
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
                getData("CASH", startDateCalendar, endDateCalendar, dictBudgets, items, org)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "безналичных", "Мега-Фитнес", period, dictBudgets,
                getData("CASHLESS_BANK", startDateCalendar, endDateCalendar, dictBudgets, items, org)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "POS-терминалу", "Мега-Фитнес", period, dictBudgets,
                getData("CASHLESS_TERMINAL", startDateCalendar, endDateCalendar, dictBudgets, items, org)
        );
        rownum = doReportPart(
                styleMap, sheet, rownum, "всего", "Мега-Фитнес", period, dictBudgets,
                getData(null, startDateCalendar, endDateCalendar, dictBudgets, items, org)
        );
        glob.put("lastRow", rownum - 2);
        globalData.add(glob);
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
            cell.setCellValue(new Date(doubleMap.get(REPORT_KEY_DATE).longValue()));
            int i = 0;
            for (i = 0; i < dictBudgets.size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(doubleMap.get(dictBudgets.get(i).code));
                cell.setCellStyle(styleMap.get("dataCell"));
            }
            cell = row.createCell(i + 1);
            cell.setCellFormula("SUM(B" + rownum + ":I" + rownum + ")");
            cell.setCellStyle(styleMap.get("dataCell"));
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
    public void report(
            @RequestParam(name = "start", required = false) Long startDateMillis,
            @RequestParam(name = "end", required = false) Long endDateMillis
    ) {
        HSSFWorkbook workbook = getWorkbook(startDateMillis, endDateMillis);
        try (ServletOutputStream sout = response.getOutputStream()) {
            String file_name = "report_N_4";
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

    @RequestMapping(value = "/report_zip", produces = "application/zip")
    public void reportzip(
            @RequestParam(name = "start", required = false) Long startDateMillis,
            @RequestParam(name = "end", required = false) Long endDateMillis
    ) throws IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=report_N_4.xls.zip");
        HSSFWorkbook workbook = getWorkbook(startDateMillis, endDateMillis);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            zipOutputStream.putNextEntry(new ZipEntry("report_N_4.xls"));
            workbook.write(zipOutputStream);
            zipOutputStream.flush();
        }

      /*  try (ZipOutputStream zippedOUt = new ZipOutputStream(response.getOutputStream())) {
            ZipEntry e = new ZipInputStreamZipEntrySource.FakeZipEntry();
            // Configure the zip entry, the properties of the file
            e.setSize(resource.contentLength());
            e.setTime(System.currentTimeMillis());
            // etc.
            zippedOUt.putNextEntry(e);
            // And the content of the resource:
            StreamUtils.copy(resource.getInputStream(), zippedOut);
            zippedOUt.closeEntry();
            zippedOUt.finish();
        } catch (Exception e) {
            // Do something with Exception
        }*/
    }

    @RequestMapping(value = "/cash_report")
    public void cash_report(
            @RequestParam(name = "start", required = false) Long startDateMillis
    ) {
        HSSFWorkbook workbook = getCashWorkbook(startDateMillis);
        try (ServletOutputStream sout = response.getOutputStream()) {
            String file_name = "cash_report";
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

    @RequestMapping(value = "/request_report")
    public void request_report(
            @RequestParam(name = "start", required = false) Long startDateMillis
    ) {
        HSSFWorkbook workbook = getRequestWorkbook(startDateMillis);
        try (ServletOutputStream sout = response.getOutputStream()) {
            String file_name = "request_report";
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

    private HSSFWorkbook getCashWorkbook(Long startDateMillis) {
        Calendar endDateCalendar;
        int startMonth = Calendar.MONTH;

        Calendar startDateCalendar;
        if (startDateMillis == null) {
            startDateCalendar = new GregorianCalendar();
            // startDateCalendar.setTimeInMillis(endDateCalendar.getTimeInMillis());
            startDateCalendar.set(startMonth, startDateCalendar.get(startMonth) - 1);
        } else {
            startDateCalendar = onlyDate(startDateMillis);
        }

        return doCashReport(startDateCalendar);
    }

    private HSSFWorkbook getRequestWorkbook(Long startDateMillis) {
        Calendar endDateCalendar;
        int startMonth = Calendar.MONTH;

        Calendar startDateCalendar;
        if (startDateMillis == null) {
            startDateCalendar = new GregorianCalendar();
            // startDateCalendar.setTimeInMillis(endDateCalendar.getTimeInMillis());
            startDateCalendar.set(startMonth, startDateCalendar.get(startMonth) - 1);
        } else {
            startDateCalendar = onlyDate(startDateMillis);
        }

        return doRequestReport(startDateCalendar);
    }

    private HSSFWorkbook doCashReport(Calendar startDateCalendar) {
        HSSFWorkbook book1 = null;
        book1 = getHssfWorkbookTemplate(book1);

        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        SummaryInformation summaryInfo = workbook.getSummaryInformation();
        summaryInfo.setAuthor("robot");
        summaryInfo.setCreateDateTime(new Date());
        summaryInfo.setEditTime(new Date().getTime());
        dictOrgs = cashDataService.getOrgs();
        for (DictOrgDto dictOrg : dictOrgs) {
            if (dictOrg.code.equals("HQ")) {
                dictOrgs.remove(dictOrg);
                break;
            }
        }
        HSSFSheet hssfSheet = null;
        HSSFSheet sheet = null;
        AffiliateGetDataResult dataResult = cashReportService.getAffiliateData(login(), startDateCalendar.getTime());
        sheet = book1.getSheetAt(8);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        if (dataResult.data != null) {
            List<BudgetNextChangeItemDto> items = Arrays.asList(dataResult.data.items);
            doCashSheet(hssfSheet, items);
        }
        return workbook;
    }

    private HSSFWorkbook doRequestReport(Calendar startDateCalendar) {
        HSSFWorkbook book1 = null;
        book1 = getHssfWorkbookTemplate(book1);

        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        SummaryInformation summaryInfo = workbook.getSummaryInformation();
        summaryInfo.setAuthor("robot");
        summaryInfo.setCreateDateTime(new Date());
        summaryInfo.setEditTime(new Date().getTime());
        dictOrgs = cashDataService.getOrgs();
        for (DictOrgDto dictOrg : dictOrgs) {
            if (dictOrg.code.equals("HQ")) {
                dictOrgs.remove(dictOrg);
                break;
            }
        }
        HSSFSheet hssfSheet = null;
        HSSFSheet sheet = null;
        GetAffiliateDataResult dataResult = expensesRequestService.getAffiliateData(login(), startDateCalendar.getTime());


        sheet = book1.getSheetAt(9);
        hssfSheet = workbook.createSheet(sheet.getSheetName());
        ExcelCopy.copySheets(hssfSheet, sheet, true);
        if (dataResult.items != null) {
            List<BudgetNextChangeItemDto> items = Arrays.asList(dataResult.items);
            doRequestSheet(hssfSheet, items, startDateCalendar, dataResult.dictOrgDto);
        }
        return workbook;
    }

    private HSSFWorkbook getHssfWorkbookTemplate(HSSFWorkbook book1) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("template.xls").getFile());
            //  FileInputStream fileInputStream = new FileInputStream("template.xls");
            book1 = new HSSFWorkbook(new FileInputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return book1;
    }

    private void doCashSheet(HSSFSheet hssfSheet, List<BudgetNextChangeItemDto> items) {
        HSSFCell cell = hssfSheet.getRow(0).getCell(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date currDate = new Date();
        currDate.setTime(items.get(0).change.changeDate);
        String date = dateFormat.format(currDate);
        cell.setCellValue("ОТЧЕТ за " + date);
        addCellValue(hssfSheet, items, 1, 3, 35);
        addCellValue(hssfSheet, items, 1, 4, 38);
        addCellValue(hssfSheet, items, 1, 5, 34);
        addCellValue(hssfSheet, items, 1, 6, 36);
        addCellValue(hssfSheet, items, 1, 7, 37);
        addCellValue(hssfSheet, items, 1, 8, 39);
        addCellValue(hssfSheet, items, 1, 9, 40);
        addCellValue(hssfSheet, items, 1, 10, 41);
        addCellValue(hssfSheet, items, 3, 13, 35);
        addCellValue(hssfSheet, items, 3, 14, 38);
        addCellValue(hssfSheet, items, 3, 15, 34);
        addCellValue(hssfSheet, items, 3, 16, 36);
        addCellValue(hssfSheet, items, 3, 17, 37);
        addCellValue(hssfSheet, items, 3, 18, 39);
        addCellValue(hssfSheet, items, 3, 19, 40);
        addCellValue(hssfSheet, items, 3, 20, 41);
        addCellValue(hssfSheet, items, 2, 23, 35);
        addCellValue(hssfSheet, items, 2, 24, 36);
        addCellValue(hssfSheet, items, 2, 25, 37);
        addCellValue(hssfSheet, items, 2, 26, 41);
    }

    private void doRequestSheet(HSSFSheet sheet, List<BudgetNextChangeItemDto> items, Calendar startDateCalendar, DictOrgDto dictOrgDto) {
        HSSFCell cell = sheet.getRow(0).getCell(2);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date currDate = new Date();
        currDate.setTime(startDateCalendar.getTimeInMillis());
        String date = dateFormat.format(currDate);
        cell.setCellValue(date);
        HSSFRow row = null;
        int rownum = 3;
        sheet.getRow(2).getCell(3).setCellValue(dictOrgDto.name);
        for (BudgetNextChangeItemDto item : items) {
            sheet.shiftRows(rownum, sheet.getLastRowNum() + 1, 1);
            row = sheet.getRow(rownum++);
            ExcelCopy.copyRow(sheet, sheet, sheet.getRow(rownum), row);
            row.getCell(1).setCellValue(rownum - 3);
            row.getCell(3).setCellValue(item.note.noteValue);
            if (item.storeType.id == 1) {
                row.getCell(4).setCellValue(item.itemValue);
            }
            if (item.storeType.id == 2) {
                row.getCell(5).setCellValue(item.itemValue);
            }
        }
        row = sheet.getRow(rownum+2);
        cell = row.getCell(4);
        cell.setCellFormula("SUM(E"+4+":E"+rownum+")");
        cell = row.getCell(5);
        cell.setCellFormula("SUM(F"+4+":F"+rownum+")");
        cell = row.getCell(6);
        cell.setCellFormula("SUM(G"+4+":G"+rownum+")");

    }

    private void addCellValue(HSSFSheet hssfSheet, List<BudgetNextChangeItemDto> items, int storeType, int rowNum, int budType) {
        hssfSheet.getRow(rowNum).getCell(2).setCellValue(findInItems(items, storeType, budType));
    }

    private Double findInItems(List<BudgetNextChangeItemDto> items, int storeType, int budType) {
        Double res = 0D;
        for (BudgetNextChangeItemDto item : items) {
            if ((item.storeType.id.intValue() == storeType) & (item.budgetType.id.intValue() == budType)) {
                return item.itemValue.doubleValue();
            }
        }
        return res;
    }

    private HSSFWorkbook getWorkbook(@RequestParam(name = "start", required = false) Long startDateMillis, @RequestParam(name = "end", required = false) Long endDateMillis) {
        Calendar endDateCalendar;
        int startMonth = Calendar.MONTH;
        if (endDateMillis == null) {
            endDateMillis = System.currentTimeMillis();
            endDateCalendar = onlyDate(endDateMillis);
            endDateCalendar.set(startMonth, endDateCalendar.get(startMonth) + 1);
            endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            endDateCalendar = onlyDate(endDateMillis);
        }

        Calendar startDateCalendar;
        if (startDateMillis == null) {
            startDateCalendar = new GregorianCalendar();
            startDateCalendar.setTimeInMillis(endDateCalendar.getTimeInMillis());
            startDateCalendar.set(startMonth, startDateCalendar.get(startMonth) - 1);
        } else {
            startDateCalendar = onlyDate(startDateMillis);
        }

        if (startDateCalendar.after(endDateCalendar)) {
            Calendar temp = startDateCalendar;
            startDateCalendar = endDateCalendar;
            endDateCalendar = temp;
        }
        return doreport4(startDateCalendar, endDateCalendar);
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
        styleBody = workbook.createCellStyle();
        styleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleBody.setFont(fontBold);
        styleBody.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        styleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleBody.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styleBody.setFillForegroundColor(HSSFColor.GREEN.index);
        styleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleMap.put("sumCellDataGreen", styleBody);
        return styleMap;
    }


}