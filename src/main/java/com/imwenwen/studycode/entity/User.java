package com.imwenwen.studycode.entity;

import com.example.DoNotParamAdvice;
import lombok.Data;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/2/23 16:02
 * @version: 1.0
 */
@Data
public class User {

    @DoNotParamAdvice
    private String name;
    private String nickName;
}
