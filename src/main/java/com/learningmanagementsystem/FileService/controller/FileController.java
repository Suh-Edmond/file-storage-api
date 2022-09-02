package com.learningmanagementsystem.FileService.controller;


import com.learningmanagementsystem.FileService.exception.FileStorageException;
import com.learningmanagementsystem.FileService.model.FileCategory;
import com.learningmanagementsystem.FileService.model.MessageResponse;
import com.learningmanagementsystem.FileService.model.UploadFileResponse;
import com.learningmanagementsystem.FileService.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/protected/")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(path ="upload-file")
    public ResponseEntity<MessageResponse> uploadFiles(@RequestParam("directory") String directory,
                                                           @RequestParam("fileCategory") FileCategory fileCategory,
                                                           @RequestPart(value = "file") MultipartFile multipartFile){

        this.fileService.saveFile(multipartFile, directory, fileCategory.toString());
        return new ResponseEntity<>(new MessageResponse("success", "File uploaded successfully",new Date()), HttpStatus.OK);
    }

    @GetMapping(path = "downloadFile")
    @ApiOperation("Upload a files to a directory")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName") String fileName,
                                          @RequestParam("directory") String directory,
                                          @RequestParam("fileCategory") FileCategory fileCategory, HttpServletRequest httpServletRequest){
        Resource resource = this.fileService.loadFileAsResource(directory, fileCategory.toString(), fileName);
        String contentType;
        try {
            contentType = httpServletRequest.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
           throw new FileStorageException("File content not error");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).
                header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+ resource.getFilename() + "\"").body(resource);
    }

    @GetMapping("file")
    @ApiOperation("Fetch a file in a directory")
    public ResponseEntity<UploadFileResponse> getFile(@RequestParam("directory") String directory,
                                                             @RequestParam("fileCategory") FileCategory fileCategory,
                                                             @RequestParam String fileName){
        UploadFileResponse response = this.fileService.getFile(directory, fileName, fileCategory.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("files")
    @ApiOperation("Fetch all files in a directory")
    public ResponseEntity<List<UploadFileResponse>> getFiles(@RequestParam("directory") String directory,
                                                                       @RequestParam("fileCategory") FileCategory fileCategory){
        List<UploadFileResponse> uploadFileResponseList = this.fileService.getFiles(directory, fileCategory.toString());
        return new ResponseEntity<>(uploadFileResponseList, HttpStatus.OK);
    }

    @DeleteMapping("file")
    @ApiOperation("Delete a file in a directory")
    public ResponseEntity<?> deleteFile(@RequestParam("directory") String directory,
                                        @RequestParam("fileCategory") FileCategory fileCategory,
                                        @RequestParam String fileName){
        this.fileService.deleteFile(directory, fileName, fileCategory.toString());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("directory")
    @ApiOperation("Delete a directory")
    public ResponseEntity<?> deleteDirectory(@RequestParam("directory") String directory,
                                             @RequestParam("fileCategory") FileCategory fileCategory){
        this.fileService.deleteDirectory(directory, fileCategory.toString());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
