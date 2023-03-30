package com.imwenwen.studycode.request.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/3/29 20:03
 * @version: 1.0
 */
@Data
public class MyDTO implements Serializable {
    private String name;

    private Long age;

    private String optId;

    private String author;

    private Long id;

    private Integer doc;

    private String docx;
}
