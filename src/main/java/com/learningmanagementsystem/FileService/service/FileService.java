package com.learningmanagementsystem.FileService.service;

import com.learningmanagementsystem.FileService.model.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    public void saveFile(MultipartFile file, String courseName, String fileCategory);
    public UploadFileResponse getCourseMaterial(String courseName, String fileName, String fileCategory);
    public List<UploadFileResponse> getCourseMaterials(String courseName, String fileCategory);
    public Resource loadFileAsResource(String courseName, String fileCategory, String fileName);
    public void deleteFile(String courseName, String fileName, String fileCategory);
    public void deleteDirectory(String courseName, String fileCategory);

}
