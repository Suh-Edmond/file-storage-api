package com.learningmanagementsystem.FileService.controller;


import com.learningmanagementsystem.FileService.exception.FileStorageException;
import com.learningmanagementsystem.FileService.model.FileCategory;
import com.learningmanagementsystem.FileService.model.MessageResponse;
import com.learningmanagementsystem.FileService.model.UploadFileResponse;
import com.learningmanagementsystem.FileService.service.FileService;
import com.learningmanagementsystem.FileService.service.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/protected/")
@RequiredArgsConstructor
public class FileController {


    private final FileService fileService;

    @PostMapping(path ="upload-files")
    public ResponseEntity<MessageResponse> uploadFiles(@RequestParam("courseName") String courseName,
                                                           @RequestParam("fileCategory") FileCategory fileCategory,
                                                           @RequestPart(value = "file") MultipartFile multipartFile){

        this.fileService.saveFile(multipartFile, courseName, fileCategory.toString());
        return new ResponseEntity<>(new MessageResponse("success", "File uploaded successfully",new Date()), HttpStatus.OK);
    }

    @GetMapping(path = "downloadFile")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName,
                                          @RequestParam("courseName") String courseName,
                                          @RequestParam("fileCategory") FileCategory fileCategory, HttpServletRequest httpServletRequest){
        Resource resource = this.fileService.loadFileAsResource(courseName, fileCategory.toString(), fileName);
        String contentType = null;
        try {
            contentType = httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
           throw new FileStorageException("File content not error");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).
                header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+ resource.getFilename() + "\"").body(resource);
    }

    @GetMapping("course-material")
    public ResponseEntity<UploadFileResponse> getCourseMaterial(@RequestParam("courseName") String courseName,
                                                             @RequestParam("fileCategory") FileCategory fileCategory,
                                                             @RequestParam String fileName){
        UploadFileResponse response = this.fileService.getCourseMaterial(courseName, fileName, fileCategory.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("course-materials")
    public ResponseEntity<List<UploadFileResponse>> getCourseMaterials(@RequestParam("courseName") String courseName, @RequestParam("fileCategory") FileCategory fileCategory){
        List<UploadFileResponse> uploadFileResponseList = this.fileService.getCourseMaterials(courseName, fileCategory.toString());
        return new ResponseEntity<>(uploadFileResponseList, HttpStatus.OK);
    }

    @DeleteMapping("course-material")
    public ResponseEntity<?> deleteFile(@RequestParam("courseName") String courseName, @RequestParam("fileCategory") FileCategory fileCategory, @RequestParam String fileName){
        this.fileService.deleteFile(courseName, fileName, fileCategory.toString());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("course-material-directory")
    public ResponseEntity<?> deleteDirectory(@RequestParam("courseName") String courseName, @RequestParam("fileCategory") FileCategory fileCategory){
        this.fileService.deleteDirectory(courseName, fileCategory.toString());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
