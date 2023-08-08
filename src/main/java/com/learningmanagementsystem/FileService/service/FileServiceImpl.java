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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FileServiceImpl implements  FileService{
    private final String baseDir = "uploads";

    @Override
    public UploadFileResponse saveFile(MultipartFile file, String directory, String fileCategory) {
        String  finalDirectory = directory.replaceAll(" ", "");
        Path uploadDir = Paths.get(this.baseDir + "/" + fileCategory +  "/" + finalDirectory ).normalize();
        if(!Files.exists(uploadDir)){
            try {
                Files.createDirectories(uploadDir);
            }catch (IOException e) {
                throw  new CustomIOException("could not create folder to store the file");
            }
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if(fileName.contains("..")){
            throw new FileStorageException("File contains invalid character");
        }
        Path targetLocation = uploadDir.resolve(fileName);
        try {
           Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
           throw new FileStorageException("Could not upload file");
        }

        return getFile(directory, fileName, fileCategory);
    }

    @Override
    public UploadFileResponse getFile(String courseName, String fileName, String fileCategory) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        String originalDirName = courseName.replaceAll(" ", "");
        String basePath = "/api/v1/protected/downloadFile";
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(basePath).queryParam("directory", originalDirName).
                 queryParam("fileName", fileName).
                 queryParam("fileCategory", fileCategory).toUriString();
        Path filePath = Paths.get(this.baseDir + "/" + fileCategory + "/" +originalDirName).resolve(fileName).normalize();

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
    public List<UploadFileResponse> getFiles(String directory, String fileCategory) {
        String finalCourseName = directory.replaceAll(" ", "");
        Path directoryPath = Paths.get(this.baseDir + "/" + fileCategory + "/" + finalCourseName).normalize();
        List<UploadFileResponse> uploadFileResponses = new ArrayList<>();
        if(Files.exists(directoryPath)){
            try {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);
                for(Path path: directoryStream){
                    String[] splitPath = path.toString().split("/");
                    int splitPathLength = splitPath.length;
                    String fileName = splitPath[splitPathLength-1];
                    UploadFileResponse response = this.getFile(directory, fileName, fileCategory);
                    uploadFileResponses.add(response);
                }
            } catch (IOException e) {
                throw new CustomIOException("Could not locate directory path");
            }
        }
        return uploadFileResponses;
    }

    @Override
    public Resource loadFileAsResource(String directory, String fileCategory, String fileName) {
        String originalDirName = directory.replaceAll(" ", "");
        Resource resource;

        Path path = Paths.get(this.baseDir + "/" + fileCategory + "/" + originalDirName).
                resolve(fileName).normalize();
        if(!Files.exists(path)){
            throw new ResourceNotFoundException("File does not exist");
        }
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
    public void deleteFile(String directory, String fileName, String fileCategory) {
        String originalDirName = directory.replaceAll(" ", "");
        Path  path = Paths.get(this.baseDir + "/" + fileCategory + "/" + originalDirName ).resolve(fileName).normalize();
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ResourceNotFoundException("File does not exist");
        }
    }

    @Override
    public void deleteDirectory(String directory, String fileCategory) {
        String originalDirName = directory.replaceAll(" ", "");
        Path path = Paths.get(this.baseDir + "/" + fileCategory).resolve(originalDirName).normalize();
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Directory does not exist");
        }
    }


}
