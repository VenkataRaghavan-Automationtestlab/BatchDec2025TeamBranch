package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * ExcelUtils
 * =====================================================================
 *
 * Utility class for reading Excel (.xlsx) files using Apache POI.
 *
 * Responsibilities:
 *  - Read data from Excel sheets
 *  - Convert all cell values to String safely
 *  - Skip header row by default (data-driven testing)
 *
 * Common usage:
 *  - TestNG @DataProvider
 *  - External test data management
 * =====================================================================
 */
public final class ExcelUtils {

    // Prevent instantiation
    private ExcelUtils() {}

    /**
     * Reads an Excel sheet and returns all rows as String arrays.
     * The first row (header) is skipped by default.
     *
     * @param path      absolute or relative path to Excel file
     * @param sheetName name of the sheet to read
     * @return List of rows, each row represented as String[]
     */
    public static List<String[]> readSheet(String path, String sheetName) {

        List<String[]> rows = new ArrayList<>();

        try (InputStream is = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException(
                        "Sheet not found: " + sheetName
                );
            }

            int firstDataRow = sheet.getFirstRowNum() + 1; // skip header
            int lastRow = sheet.getLastRowNum();

            for (int i = firstDataRow; i <= lastRow; i++) {

                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                int lastCell = row.getLastCellNum();
                List<String> cellValues = new ArrayList<>();

                for (int j = 0; j < lastCell; j++) {

                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellValues.add(getCellValueAsString(cell));
                }

                rows.add(cellValues.toArray(new String[0]));
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read Excel file [" + path + "] sheet [" + sheetName + "]",
                    e
            );
        }

        return rows;
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Converts a Cell value into String safely.
     * Handles all POI-supported cell types.
     */
    private static String getCellValueAsString(Cell cell) {

        return switch (cell.getCellType()) {

            case STRING -> cell.getStringCellValue().trim();

            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }

            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());

            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }

            case BLANK, _NONE, ERROR -> "";

            default -> cell.toString().trim();
        };
    }
}
