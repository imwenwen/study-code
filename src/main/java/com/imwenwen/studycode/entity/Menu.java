package com.imwenwen.studycode.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2022/12/6 17:11
 * @version: 1.0
 */
@Data
@NoArgsConstructor
public class Menu implements Serializable {

    private String  id;
    private String name;
    private String pid;
    private List<Menu> menuChildren;


}
