package com.learningmanagementsystem.FileService.service;

import com.learningmanagementsystem.FileService.model.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    void saveFile(MultipartFile file, String directory, String fileCategory);
    UploadFileResponse getFile(String directory, String fileName, String fileCategory);
    List<UploadFileResponse> getFiles(String directory, String fileCategory);
    Resource loadFileAsResource(String directory, String fileCategory, String fileName);
    void deleteFile(String directory, String fileName, String fileCategory);
    void deleteDirectory(String directory, String fileCategory);

}
