package com.imwenwen.studycode.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/3/27 17:24
 * @version: 1.0
 */
@Data
public class Comment implements Serializable {

    private String date;

    private String content;

}
