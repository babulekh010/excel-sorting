package kg.cbk.ExcelSortingApi.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String readAndSort(MultipartFile file) throws IOException;

}
