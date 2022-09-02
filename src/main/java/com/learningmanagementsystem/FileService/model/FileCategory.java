package com.learningmanagementsystem.FileService.model;

import io.swagger.annotations.ApiModel;

@ApiModel("The File Category indicates the base folder all sub-folders containing the different files can be stored")
public enum FileCategory {
    NOTES, SYLLABUSES, IMAGES , QUESTIONS, ANSWERS
}
