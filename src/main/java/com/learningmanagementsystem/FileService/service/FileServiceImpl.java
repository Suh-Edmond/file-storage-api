package com.learningmanagementsystem.FileService.service;

import com.learningmanagementsystem.FileService.exception.CustomIOException;
import com.learningmanagementsystem.FileService.exception.FileStorageException;
import com.learningmanagementsystem.FileService.exception.ResourceNotFoundException;
import com.learningmanagementsystem.FileService.model.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements  FileService{

    private final String baseDir = "uploads";
    private final String basePath = "/api/v1/protected/downloadFile";

    @Override
    public void saveFile(MultipartFile file, String courseName, String fileCategory) {
        String  finalCourseName = courseName.replaceAll(" ", "");
        Path courseUploadDir = Paths.get(this.baseDir + "/" + fileCategory +  "/" + finalCourseName ).normalize();
        if(!Files.exists(courseUploadDir)){
            try {
                Files.createDirectories(courseUploadDir);
            }catch (IOException e) {
                throw  new CustomIOException("could not create folder to store the file");
            }
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if(fileName.contains("..")){
            throw new FileStorageException("File contains invalid character");
        }
        Path targetLocation = courseUploadDir.resolve(fileName);
        try {
           Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
           throw new FileStorageException("Could not upload file");
        }
    }

    @Override
    public UploadFileResponse getCourseMaterial(String courseName, String fileName, String fileCategory) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        String originalCourseName = courseName.replaceAll(" ", "");
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(this.basePath).queryParam("courseName", originalCourseName).
                 queryParam("fileName", fileName).
                 queryParam("fileCategory", fileCategory).toUriString();
        Path filePath = Paths.get(this.baseDir + "/" + fileCategory + "/" +originalCourseName).resolve(fileName).normalize();
        if(!Files.exists(filePath)){
            throw  new ResourceNotFoundException("File does not exist");
        }
        uploadFileResponse.setFileDownloadUri(downloadUri);
        uploadFileResponse.setFileName(fileName);
        uploadFileResponse.setFileType(fileName.split("\\.")[1]);
        try {
            uploadFileResponse.setFileSize(Files.size(filePath.toAbsolutePath()));
        } catch (IOException e) {
            throw new CustomIOException("File path not found");
        }
        return uploadFileResponse;
    }

    @Override
    public List<UploadFileResponse> getCourseMaterials(String courseName, String fileCategory) {
        String finalCourseName = courseName.replaceAll(" ", "");
        Path directoryPath = Paths.get(this.baseDir + "/" + fileCategory + "/" + finalCourseName).normalize();
        List<UploadFileResponse> uploadFileResponses = new ArrayList<>();
        if(Files.exists(directoryPath)){
            try {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);
                for(Path path: directoryStream){
                    String[] splitPath = path.toString().split("/");
                    int splitPathLength = splitPath.length;
                    String fileName = splitPath[splitPathLength-1];
                    UploadFileResponse response = this.getCourseMaterial(courseName, fileName, fileCategory);
                    uploadFileResponses.add(response);
                }
            } catch (IOException e) {
                throw new CustomIOException("Could not locate course directory path");
            }
        }
        return uploadFileResponses;
    }

    @Override
    public Resource loadFileAsResource(String courseName, String fileCategory, String fileName) {
        String originalCourseName = courseName.replaceAll(" ", "");
        Path path = Paths.get(this.baseDir + "/" + fileCategory + "/" + originalCourseName).
                resolve(fileName).normalize();
        if(!Files.exists(path)){
            throw new ResourceNotFoundException("File does not exist");
        }
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException("File path is invalid");
        }
        if(resource.exists()){
                return resource;
            }else {
                throw new ResourceNotFoundException("File not found"+ fileName);
            }

    }

    @Override
    public void deleteFile(String courseName, String fileName, String fileCategory) {
        String originalCourseName = courseName.replaceAll(" ", "");
        Path  path = Paths.get(this.baseDir + "/" + fileCategory + "/" + originalCourseName ).resolve(fileName).normalize();
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ResourceNotFoundException("File does not exist");
        }
    }

    @Override
    public void deleteDirectory(String courseName, String fileCategory) {
        String originalCourseName = courseName.replaceAll(" ", "");
        Path path = Paths.get(this.baseDir + "/" + fileCategory).resolve(originalCourseName).normalize();
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Directory does not exist");
        }
    }


}
