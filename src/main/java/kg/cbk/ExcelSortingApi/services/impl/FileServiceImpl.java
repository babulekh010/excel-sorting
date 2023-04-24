package kg.cbk.ExcelSortingApi.services.impl;

import kg.cbk.ExcelSortingApi.services.FileService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class FileServiceImpl implements FileService {
    @Override
    public String readAndSort(MultipartFile file) throws IOException {

        // Преобразование данных из Excel таблицы в List

        ArrayList<Integer> column1 = new ArrayList<>();
        ArrayList<Integer> column2 = new ArrayList<>();
        ArrayList<String> headers = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (row.getRowNum() == 0) {
                    headers.add(cell.getStringCellValue());
                } else {
                    if (cell.getColumnIndex() == 0) {
                        column1.add((int) cell.getNumericCellValue());
                    } else if (cell.getColumnIndex() == 1) {
                        column2.add((int) cell.getNumericCellValue());
                    }
                }
            }
        }

        // Сравнение двух списков (вычисление разницы)

        ArrayList<Integer> firstColumnSorted = (ArrayList<Integer>) CollectionUtils.subtract(column1, column2);
        ArrayList<Integer> secondColumnSorted = (ArrayList<Integer>) CollectionUtils.subtract(column2, column1);

        // Создание Excel файла для хранения результата

        return writeDataToFile(headers, firstColumnSorted, secondColumnSorted);

    }

    private String writeDataToFile(List<String> headers,
                                 List<Integer> firstColumn,
                                 List<Integer> secondColumn) throws IOException {

        XSSFWorkbook res = new XSSFWorkbook();
        Sheet s = res.createSheet();

        Row row = s.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }

        if (firstColumn.size() > secondColumn.size()) {
            for (int i = 0; i < firstColumn.size(); i++) {
                Row r = s.createRow(i + 1);
                if (i < secondColumn.size()) {
                    r.createCell(0).setCellValue(firstColumn.get(i));
                    r.createCell(1).setCellValue(secondColumn.get(i));
                }
                r.createCell(0).setCellValue(firstColumn.get(i));
            }
        } else {
            for (int i = 0; i < secondColumn.size(); i++) {
                Row r = s.createRow(i + 1);
                if (i < firstColumn.size()) {
                    r.createCell(0).setCellValue(firstColumn.get(i));
                    r.createCell(1).setCellValue(secondColumn.get(i));
                }
                r.createCell(1).setCellValue(secondColumn.get(i));
            }
        }

        File file = new File("/home/elina/Desktop/excel_files/" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-SS")) +
                "File.xlsx");

        FileOutputStream out = new FileOutputStream(file);
        res.write(out);
        out.close();

        return file.getAbsolutePath();

    }

}
