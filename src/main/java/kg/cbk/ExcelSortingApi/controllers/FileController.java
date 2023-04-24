package kg.cbk.ExcelSortingApi.controllers;


import kg.cbk.ExcelSortingApi.services.FileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/excel/sort-data")
    public HttpEntity<ByteArrayResource> getExcelFile(@RequestPart MultipartFile multipartFile) throws IOException {
        String myFile = fileService.readAndSort(multipartFile);
        byte[] excelContent = Files.readAllBytes(Path.of(myFile));

        org.springframework.http.HttpHeaders header = new org.springframework.http.HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + myFile.substring(32));

        return new HttpEntity<>(new ByteArrayResource(excelContent), header);
    }

}
