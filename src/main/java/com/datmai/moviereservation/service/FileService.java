package com.datmai.moviereservation.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    // Get env variable
    @Value("${hiendat.upload-file.base-uri}")
    private String baseUri;

    public void createUploadFolder(String folder) throws URISyntaxException {
        URI uri = new URI(folder); // file:/// ...
        Path path = Paths.get(uri); // C:/ ...
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException,
            IOException {
        // create unique filename
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseUri + folder + "/" + finalName);
        Path path = Paths.get(uri);

        // Because we use input stream, we need to close it after using. For simplicity,
        // use try function to close automatically
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public boolean isValidExtension(MultipartFile file, List<String> allowedExtensions) {
        String fileName = file.getOriginalFilename();
        boolean isValidExtension = allowedExtensions.stream().anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
        if (!isValidExtension) {
            return false;
        }
        return true;
    }

    public boolean isFileSizeValid(MultipartFile file) {
        long maxSize = 50 * 1024 * 1024; // 50MB in bytes
        if (file.getSize() > maxSize) {
            return false;
        }
        return true;
    }
}
