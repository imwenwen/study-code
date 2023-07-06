package com.imwenwen.studycode.binlog;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/7/6 10:23
 * @version: 1.0
 */
@Data
@ApiModel(value = "binlog解析-数据库表字段对象")
public class Colum {

    public int inx;
    public String colName; // 列名
    public String dataType; // 类型
    public String schema; // 数据库
    public String table; // 表
    public String key; // 主键

    public Colum(String schema, String table, int idx, String colName, String dataType, String key) {
        this.schema = schema;
        this.table = table;
        this.key = key;
        this.colName = colName;
        this.dataType = dataType;
        this.inx = idx;
    }
}
