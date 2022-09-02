package com.learningmanagementsystem.FileService.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadFileResponse {

    private String fileName;
    private String fileType;
    @ApiModelProperty("File size is in bytes")
    private Long fileSize;
    private String fileDownloadUri;
}
