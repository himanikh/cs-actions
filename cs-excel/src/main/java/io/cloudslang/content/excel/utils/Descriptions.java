package io.cloudslang.content.excel.utils;

public class Descriptions {
    public static class Common {
        public static final String EXCEL_FILE_NAME_DESC = "The absolute path to the new Excel document.\n" +
                "Examples: c:\\temp\\test.xls";
        public static final String WORKSHEET_NAME_DESC = "The name of Excel worksheet";
    }

    public static class GetCell {
        public static final String HAS_HEADER_DESC = "If Yes, then the first row of the document is expected to be the header row. \n" +
                "Valid values: yes, no\n" +
                "Default value: Yes";
        public static final String FIRST_ROW_INDEX_DESC = "The index of the first row in the Excel worksheet, including the header row.\n" +
                "Default value: 0";
        public static final String ROW_INDEX_DESC = "A list of row indexes.\n" +
                "Examples: 1:3, 10, 15:20,25\n" +
                "Default Value: from the index of the first row  to the index of the last row in the Excel worksheet.";
        public static final String COLUMN_INDEX_DESC = " A list of column indexes.\n" +
                "Examples: 1:3, 10, 15:20,25\n" +
                "Default value: from 0 to the index of the last column in the Excel worksheet.";
        public static final String ROW_DELIMITER_DESC = "The delimiter used to separate the rows of the returnResult.\n" +
                "Default value: | (pipe)";
        public static final String COLUMN_DELIMITER_DESC = "The delimiter used to separate the columns of the returnResult.\n" +
                "Default value: , (comma)";
        public static final String RETURN_RESULT_DESC = "This is the primary output. Returns the cell data retrieved from Excel document.";
        public static final String HEADER_DESC = "A delimited list of column names of data being returned if hasHeader is set to Yes. ";
        public static final String ROWS_COUNT_DESC = "The number of the rows returned.";
        public static final String COLUMNS_COUNT_DESC = "The number of the columns returned.";


    }

    public static class GetRowIndexbyCondition {
        public static final String HAS_HEADER_DESC = "If Yes, then the first row of the document is expected to be the header row. \n" +
                "Valid values: yes, no\n" +
                "Default value: Yes";
        public static final String FIRST_ROW_INDEX_DESC = "The index of the first row in the Excel worksheet, including the header row.\n" +
                "Default value: 0";
        public static final String COLUMN_INDEX_TO_QUERY_DESC = "The column index to search in.";
        public static final String OPERATOR_DESC = " operator - The math operators. \n" +
                "Valid values: ==, != for string comparison; ==, !=, <,<=,>,>= for numeric comparison.\n" +
                "Default vaue: ==";
        public static final String VALUE_DESC = "The value to search in the specified column. If left blank, it means an empty value.\n";
        public static final String RETURN_RESULT_DESC = "This is the primary result. Return a list of row indexes that satisfied the specified condition.";
        public static final String ROWS_COUNT_DESC = "The number of the row indexes returned.";

    }
}