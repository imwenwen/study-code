package com.imwenwen.studycode.binlog;

import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

import java.io.File;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/7/6 10:30
 * @version: 1.0
 */
public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println("---------------------------------   开始执行  ---------------------------------------");
        String filePath = "/Users/chenwenwei/Desktop/mysql-bin.000001";
        File binlogFile = new File(filePath);
        EventDeserializer eventDeserializer = new EventDeserializer();
        BinaryLogFileReader reader = new BinaryLogFileReader(binlogFile, eventDeserializer);
        String dbTable = "test";
        String db = "imwenwen";
        String table = "test";
        Conf conf = new Conf();
        conf.setHost("106.15.170.237");
        conf.setPort(13306);
        conf.setUsername("root");
        conf.setPasswd("Cww2021..");
        binlogUtil.readBinLogInfo(reader, dbTable, db, table, conf,1688611430000L,1788611430000L);
        System.out.println("---------------------------------   执行完成  ---------------------------------------");
    }
}
