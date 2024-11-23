package com.datmai.moviereservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.datmai.moviereservation.service.FileService;
import com.datmai.moviereservation.util.dto.file.ResUploadFileDTO;
import com.datmai.moviereservation.util.error.FileUploadException;
import com.datmai.moviereservation.util.format.ApiMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Get env variable
    @Value("${hiendat.upload-file.base-uri}")
    private String baseUri;

    @PostMapping("/files")
    @ApiMessage("Upload single file successfully")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, FileUploadException {

        // Check if file is empty
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty, please upload your file");
        }

        // Validate extension
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg");
        if (!this.fileService.isValidExtension(file, allowedExtensions)) {
            throw new FileUploadException(
                    "Invalid file type based on extension, only allow " + allowedExtensions.toString());
        }

        // Check file size
        if (!this.fileService.isFileSizeValid(file)) {
            throw new FileUploadException("File size exceed limit 50MB");
        }

        // Create directory is not exist
        this.fileService.createUploadFolder(baseUri + folder);

        // Upload file into folder
        String uploadedFile = this.fileService.store(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadedFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

}
