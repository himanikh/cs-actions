package io.cloudslang.content.excel.actions;

import io.cloudslang.content.excel.services.ExcelServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.cloudslang.content.constants.OutputNames.EXCEPTION;
import static io.cloudslang.content.constants.OutputNames.RETURN_CODE;
import static io.cloudslang.content.constants.OutputNames.RETURN_RESULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AddExcelDataTest {

    public static final String SHEET0 = "Sheet0";
    public static final String SHEET1 = "Sheet1";
    public static final String SHEET2 = "Sheet2";
    public static final String SHEET3 = "Sheet3";
    public static final String SHEET4 = "Sheet4";
    public static final String INVALID_SHEET = "invalid_sheet";
    public static final String FILE_NAME = System.getProperty("java.io.tmpdir") + "testFile.xls";
    private static AddExcelData toTest;
    private static Sheet worksheet;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    /**
     * Create an Excel document, add 3 sheets and write some information in each (5 rows and 10 columns)
     * @throws IOException
     */
    public static void setUp() throws IOException {
        worksheet = mock(Sheet.class);
        doReturn(2).when(worksheet).getLastRowNum();
    }

    @Before
    public void before() throws IOException {
        toTest = new AddExcelData();

        // generate excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet;
        workbook.createSheet(SHEET0);
        HSSFRow rowhead;

        sheet = workbook.createSheet(SHEET1);
        for (int i = 0; i < 5; i++) {
            rowhead = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                rowhead.createCell(j).setCellValue(22);
            }
        }

        //set a formula cell
        String strFormula = "SUM(A1:A3)";
        rowhead = sheet.createRow(5);
        HSSFCell cell = rowhead.createCell(5);
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula(strFormula);

        //set string cells
        sheet = workbook.createSheet(SHEET2);
        for (int i = 0; i < 5; i++) {
            rowhead = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                rowhead.createCell(j).setCellValue("abc");
            }
        }

        sheet = workbook.createSheet(SHEET3);
        for (int i = 0; i < 5; i++) {
            rowhead = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                rowhead.createCell(j).setCellValue(0);
            }
        }

        sheet = workbook.createSheet(SHEET4);
        for (int i = 0; i < 5; i++) {
            rowhead = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                rowhead.createCell(j).setCellValue(0);
            }
        }

        writeWorkbook(workbook, FILE_NAME);
    }

    @After
    /**
     * delete the excel document that was created at setUp.
     */
    public void CleanUp() {
        File f = new File(FILE_NAME);
        f.delete();
    }

    @Test
    /**
     * Test execute method on an existing xml documment.
     * this method overwrite old data with row_input_data.
     * @throws Exception
     */
    public void testExecute() throws Exception {

        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET1, "unu, doi, trei", "1,2,3,4;5,6,7,8", "1,2", "", ";", ",", "true");
        assertEquals("0", result.get(RETURN_CODE));
        assertNull(result.get(EXCEPTION));

        FileInputStream fis = new FileInputStream(new File(FILE_NAME));
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheet(SHEET1);
        HSSFRow row = sheet.getRow(1);

        HSSFCell cell = row.getCell(0);
        assertEquals(1, (long) cell.getNumericCellValue());

        cell = row.getCell(1);
        assertEquals(2, (long) cell.getNumericCellValue());

        cell = row.getCell(2);
        assertEquals(3, (long) cell.getNumericCellValue());

        cell = row.getCell(3);
        assertEquals(4, (long) cell.getNumericCellValue());

        cell = row.getCell(4);
        assertEquals(22, (long) cell.getNumericCellValue());

        row = sheet.getRow(2);
        cell = row.getCell(0);
        assertEquals(5, (long) cell.getNumericCellValue());
        cell = row.getCell(1);
        assertEquals(6, (long) cell.getNumericCellValue());
    }

    @Test
    /**
     * Test execute method on an existing xml documment.
     * this method overwrite old data with row_input_data.
     * with header
     * @throws Exception
     */
    public void testExecute0() throws Exception {
        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET0, "unu, doi, trei", "1,2,3,4;5,6,7,8", "1,2", "", ";", ",", "true");
        assertEquals("0", result.get(RETURN_CODE));
        assertNull(result.get(EXCEPTION));

        FileInputStream fis = new FileInputStream(new File(FILE_NAME));
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheet(SHEET0);
        HSSFRow row = sheet.getRow(0);

        HSSFCell cell = row.getCell(0);
        assertEquals("unu", cell.getStringCellValue());

        cell = row.getCell(1);
        assertEquals("doi", cell.getStringCellValue());

        cell = row.getCell(2);
        assertEquals("trei", cell.getStringCellValue());

        cell = row.getCell(3);
        assertNull(cell);

        row = sheet.getRow(2);
        cell = row.getCell(0);
        assertEquals(1, (long) cell.getNumericCellValue());
        cell = row.getCell(1);
        assertEquals(2, (long) cell.getNumericCellValue());
        cell = row.getCell(2);
        assertEquals(3, (long) cell.getNumericCellValue());
        cell = row.getCell(3);
        assertEquals(4, (long) cell.getNumericCellValue());

        row = sheet.getRow(3);
        cell = row.getCell(0);
        assertEquals(5, (long) cell.getNumericCellValue());
    }

    @Test
    /**
     * test SetHeader data on sheet2 of an existing xml document.
     * this will overrwrite first row of the document.
     * @throws IOException
     */
    public void testSetHeaderRow() throws IOException {
        FileInputStream fis = new FileInputStream(new File(FILE_NAME));
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheet(SHEET2);
        String headerData = "ana, are, 7.2, mere";
        String delimiter = ",";

        ExcelServiceImpl.setHeaderRow(sheet, headerData, delimiter);

        HSSFRow row = sheet.getRow(0);
        HSSFCell cell = row.getCell(0);
        assertEquals("ana", cell.getStringCellValue());
        cell = row.getCell(1);
        assertEquals("are", cell.getStringCellValue());
        cell = row.getCell(2);
        assertEquals(7, (long) cell.getNumericCellValue());
        cell = row.getCell(3);
        assertEquals("mere", cell.getStringCellValue());
        cell = row.getCell(4);
        assertNull(cell);
    }

    @Test
    /**
     * test SetDataRows in sheet3.
     *
     * @throws IOException
     */
    public void testSetDataRows() throws IOException {
        FileInputStream fis = new FileInputStream(new File(FILE_NAME));
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheet(SHEET3);
        String rowData = "ana,are,7.2,mere;radu,n-are";
        String rowDelimiter = ";";
        String columnDelimiter = ",";
        int startRowIndex = 4;
        int startColumnIndex = 3;

        ExcelServiceImpl.setDataRows(sheet, rowData, rowDelimiter, columnDelimiter, startRowIndex, startColumnIndex);

        HSSFRow row = sheet.getRow(4);
        HSSFCell cell = row.getCell(3);
        assertEquals("ana", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals("are", cell.getStringCellValue());
        cell = row.getCell(5);
        assertEquals(7, (long) cell.getNumericCellValue());
        cell = row.getCell(6);
        assertEquals("mere", cell.getStringCellValue());
        cell = row.getCell(7);
        assertEquals(0, (long) cell.getNumericCellValue()); // old data
        row = sheet.getRow(5);
        cell = row.getCell(3);
        assertEquals("radu", cell.getStringCellValue());
        cell = row.getCell(4);
        assertEquals("n-are", cell.getStringCellValue());
    }

    @Test
    /**
     * Test execute method with empty excelFileName input.
     * @throws Exception
     */
    public void testExecute2() throws Exception {

        Map<String, String> result;
        result = toTest.execute("", SHEET0, "unu, doi, trei", "1,2,3,4;5,6,7,8", "1,2", "", ";", ",", "true");
        assertEquals("-1", result.get(RETURN_CODE));
        assertEquals("The excelFilName is required.", result.get(RETURN_RESULT));
    }


    @Test
    /**
     * Test execute method with invalid format.
     * @throws Exception
     */
    public void testExecute3() throws Exception {
        File fileWithInvalidFormat = new File(System.getProperty("java.io.tmpdir") + "testFile.txt");
        fileWithInvalidFormat.createNewFile();

        Map<String, String> result;
        result = toTest.execute(System.getProperty("java.io.tmpdir") + "testFile.txt", SHEET0, "unu, doi, trei", "1,2,3,4;5,6,7,8", "1,2", "", ";", ",", "true");

        assertEquals("-1", result.get(RETURN_CODE));
        assertEquals("Invalid file for Excel documents. Expecting file name with extension XLS , XLSX or XLSM.", result.get(RETURN_RESULT));

        fileWithInvalidFormat.delete();
    }

    @Test
    /**
     * Test execute method with inexistent sheet.
     * @throws Exception
     */
    public void testExecute4() throws Exception {
        Map<String, String> result;
        result = toTest.execute(System.getProperty("java.io.tmpdir") + "testFile.txt", INVALID_SHEET, "unu, doi, trei", "1,2,3,4;5,6,7,8", "1,2", "", ";", ",", "true");

        assertEquals("-1", result.get(RETURN_CODE));
        assertEquals("Worksheet " + INVALID_SHEET + " does not exist.", result.get(RETURN_RESULT));
    }

    @Test
    /**
     * Test execute method with null row data.
     * @throws Exception
     */
    public void testExecute5() throws Exception {
        Map<String, String> result;
        result = toTest.execute(System.getProperty("java.io.tmpdir") + "testFile.txt", INVALID_SHEET, "unu, doi, trei", "", "1,2", "", ";", ",", "true");

        assertEquals("-1", result.get(RETURN_CODE));
        assertEquals("Data that should be added or modified in the document is not given.", result.get(RETURN_RESULT));
    }


    /**
     * Test the processIndex method for rows
     */
    @Test
    public void testProcessIndex() {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{1, 2, 5, 6, 7, 8, 9}));
        String index = "1,2 , 5:8,9";

        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, "", "", "", true, false);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    /**
     * Test the processIndex method for rows with default -> append
     */
    @Test
    public void testProcessIndex2() {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{2, 3, 4}));
        String index = "";
        String rowData = "a,b|c,d|e,f";
        doReturn(1).when(worksheet).getLastRowNum();

        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, rowData, "\\|", ",", true, false);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    /**
     * Test the processIndex method for columns
     */
    @Test
    public void testProcessIndex3() {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{1, 2, 5, 6, 7, 8, 9}));
        String index = "1,2 , 5:8,9";

        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, "", "", "", false, false);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    /**
     * Test the processIndex method for columns with default
     */
    @Test
    public void testProcessIndex4() {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{0, 1}));
        String index = "";
        String rowData = "a,b|c,d|e,f";

        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, rowData, "\\|", ",", false, false);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    /**
     * Test the processIndex method for rows with header data
     */
    @Test
    public void testProcessIndex5() {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{2, 3, 6, 7, 8, 9, 10}));
        String index = "1,2 , 5:8,9";
        String rowData = "a,b|c,d|e,f";

        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, rowData, "\\|", ",", true, true);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    @Test
    public void testProcessIndex6() {
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{0, 1}));
        String index = "0:1";
        String rowData = "a,s,d,f|c,d,f";
        List<Integer> result = ExcelServiceImpl.processIndex(index, worksheet, rowData, "\\|", ",", false, true);
        assertNotNull(result);
        assertEquals(indexList, result);
    }

    /**
     * Tests the shiftRows method with 3 shifts
     */
    @Test
    public void testShiftRows() {
        reset(worksheet);
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{0, 1, 2, 3, 4, 5, 6, 7}));
        ExcelServiceImpl.shiftRows(worksheet, indexList);
        verify(worksheet, times(1)).shiftRows(eq(0), eq(0), eq(8), eq(false), eq(true));
    }

    /**
     * Tests the shiftRows method with 3 shifts
     */
    @Test
    public void testShiftRows2() {
        reset(worksheet);
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{0, 1, 5, 6, 7, 8, 9, 12}));
        ExcelServiceImpl.shiftRows(worksheet, indexList);
        verify(worksheet, atLeastOnce()).shiftRows(anyInt(), anyInt(), anyInt(), anyBoolean(), anyBoolean());
    }

    /**
     * Tests the shiftRows method with excel document
     *
     * @throws java.io.IOException
     */
    @org.junit.Test
    public void testShiftRows3() throws IOException {
        // apache poi works with 0 index based excel files (Microsoft Excel file starts with index 1)
        Workbook workbook = getWorkbook(FILE_NAME);
        Sheet sheet = workbook.getSheet(SHEET4);
        List<Integer> indexList = java.util.Arrays.asList(ArrayUtils.toObject(new int[]{0}));
        ExcelServiceImpl.shiftRows(sheet, indexList);
        writeWorkbook(workbook, FILE_NAME);
        assertNotNull(worksheet);

        workbook = getWorkbook(FILE_NAME);
        sheet = workbook.getSheet(SHEET4);
        assertNotNull(sheet);
        assertEquals(0, (int) sheet.getRow(1).getCell(0).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(2).getCell(0).getNumericCellValue());
        assertNull(sheet.getRow(0).getCell(0));
        assertNull(sheet.getRow(0).getCell(1));
        assertNull(sheet.getRow(0).getCell(2));
        assertNull(sheet.getRow(0).getCell(3));
    }

    /**
     * Tests the execute method with default values -> append
     *
     * @throws Exception
     */
    @Test
    public void testExecute6() throws Exception {
        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET4, "", "1,2,3,4|5,6,7,8", "", "", "|", ",", "");

        assertEquals("0", result.get(RETURN_CODE));

        Workbook workbook = getWorkbook(FILE_NAME);
        Sheet sheet = workbook.getSheet(SHEET4);
        assertNotNull(sheet);
        assertEquals(0, (int) sheet.getRow(4).getCell(0).getNumericCellValue());
        assertEquals(1, (int) sheet.getRow(5).getCell(0).getNumericCellValue());
        assertEquals(5, (int) sheet.getRow(6).getCell(0).getNumericCellValue());
    }

    /**
     * Tests the execute method with insert at specified rows and default columns
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testExecute7() throws Exception {
        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET4, "", "1,2,3,4|5,6,7,8", "4,5", "", "|", ",", "");

        assertEquals("0", result.get(RETURN_CODE));

        Workbook workbook = getWorkbook(FILE_NAME);
        Sheet sheet = workbook.getSheet(SHEET4);
        assertNotNull(sheet);
        assertEquals(0, (int) sheet.getRow(3).getCell(0).getNumericCellValue());
        assertEquals(1, (int) sheet.getRow(4).getCell(0).getNumericCellValue());
        assertEquals(5, (int) sheet.getRow(5).getCell(0).getNumericCellValue());
    }

    /**
     * Tests the execute method with insert at specified rows and specified columns
     *
     * @throws Exception
     */
    @Test
    public void testExecute8() throws Exception {
        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET4, "", "1,2,3,4|5,6,7,8", "4,5", "1:3,5", "|", ",", "");

        assertEquals("0", result.get(RETURN_CODE));

        Workbook workbook = getWorkbook(FILE_NAME);
        Sheet sheet = workbook.getSheet(SHEET4);
        assertNotNull(sheet);
        assertEquals(0, (int) sheet.getRow(3).getCell(1).getNumericCellValue());
        assertEquals(1, (int) sheet.getRow(4).getCell(1).getNumericCellValue());
        assertEquals(5, (int) sheet.getRow(5).getCell(1).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(6).getCell(1).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(3).getCell(3).getNumericCellValue());
        assertEquals(3, (int) sheet.getRow(4).getCell(3).getNumericCellValue());
        assertEquals(7, (int) sheet.getRow(5).getCell(3).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(6).getCell(3).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(3).getCell(5).getNumericCellValue());
        assertEquals(4, (int) sheet.getRow(4).getCell(5).getNumericCellValue());
        assertEquals(8, (int) sheet.getRow(5).getCell(5).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(3).getCell(5).getNumericCellValue());
    }

    /**
     * Tests the execute method with insert at specified rows and specified columns
     * test with overwrite
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testExecute9() throws Exception {
        Map<String, String> result;
        result = toTest.execute(FILE_NAME, SHEET4, "", "1,2,3,4|5,6,7,8", "4,5", "1:3,5", "|", ",", "true");

        assertEquals("0", result.get(RETURN_CODE));

        Workbook workbook = getWorkbook(FILE_NAME);
        Sheet sheet = workbook.getSheet(SHEET4);
        assertNotNull(sheet);
        assertEquals(0, (int) sheet.getRow(3).getCell(1).getNumericCellValue());
        assertEquals(1, (int) sheet.getRow(4).getCell(1).getNumericCellValue());
        assertEquals(5, (int) sheet.getRow(5).getCell(1).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(3).getCell(3).getNumericCellValue());
        assertEquals(3, (int) sheet.getRow(4).getCell(3).getNumericCellValue());
        assertEquals(7, (int) sheet.getRow(5).getCell(3).getNumericCellValue());
        assertEquals(0, (int) sheet.getRow(3).getCell(5).getNumericCellValue());
        assertEquals(4, (int) sheet.getRow(4).getCell(5).getNumericCellValue());
        assertEquals(8, (int) sheet.getRow(5).getCell(5).getNumericCellValue());
        assertNull(sheet.getRow(7));
    }

    /**
     * Reads an excel file and returns the workbook.
     *
     * @param filePath Excel file path.
     * @return Workbook from the excel file.
     * @throws IOException
     */
    private Workbook getWorkbook(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        fis.close();
        return workbook;
    }

    /**
     * Writes a workbook to the specified excel file.
     *
     * @param workbook To be written on file.
     * @param filePath Excel file path.
     * @throws IOException
     */
    private void writeWorkbook(Workbook workbook, String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
    }

}
