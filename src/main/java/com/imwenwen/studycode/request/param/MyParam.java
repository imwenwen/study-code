package com.imwenwen.studycode.request.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/3/29 20:04
 * @version: 1.0
 */
@Data
public class MyParam implements Serializable {

    private String name;

    private Long age;

    private String optUid;

    private String author;


    private Integer doc;
}
