package com.imwenwen.studycode.binlog;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/7/6 10:25
 * @version: 1.0
 */
@Data
public class BeforeAndAfterSqlDTO implements Serializable {

    @ApiModelProperty(value = "原始sql")
    private String beforeSql;

    @ApiModelProperty(value = "回滚sql建议")
    private List<String> afterSqlList;

}
