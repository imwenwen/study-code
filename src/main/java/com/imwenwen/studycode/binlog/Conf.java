package com.imwenwen.studycode.binlog;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/7/6 10:23
 * @version: 1.0
 */
@Data
@ApiModel(value = "binlog解析-数据库连接信息")
public class Conf {

    @Value("${binlog.datasource.host}")
    private String host;

    @Value("${binlog.datasource.port}")
    private int port;

    @Value("${binlog.datasource.username}")
    private String username;

    @Value("${binlog.datasource.passwd}")
    private String passwd;

    @Value("${binlog.db}")
    private String db;

    @Value("${binlog.table}")
    private String table;

}
