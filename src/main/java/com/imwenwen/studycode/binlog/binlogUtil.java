package com.imwenwen.studycode.binlog;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/7/6 10:24
 * @version: 1.0
 */
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.shyiko.mysql.binlog.event.EventType.*;
import static com.github.shyiko.mysql.binlog.event.EventType.isDelete;

@Slf4j
public class binlogUtil {

    /**
     * 拼接dbTable
     */
    public static String getdbTable(String db, String table) {
        return db + "-" + table;
    }

    /**
     * 获取columns集合
     */
    public static Map<String, Colum> getColMap(Conf conf, String db, String table) throws ClassNotFoundException {
        try {
//            Class.forName("com.mysql.jdbc.Driver");
            // 保存当前注册的表的colum信息
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + conf.getHost() + ":" + conf.getPort() + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                    conf.getUsername(), conf.getPasswd());
            // 执行sql
            String preSql = "SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, DATA_TYPE, ORDINAL_POSITION, COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? and TABLE_NAME = ?";
            PreparedStatement ps = connection.prepareStatement(preSql);
            ps.setString(1, db);
            ps.setString(2, table);
            ResultSet rs = ps.executeQuery();
            Map<String, Colum> map = new HashMap<>(rs.getRow());
            while (rs.next()) {
                String schema = rs.getString("TABLE_SCHEMA");
                String tableName = rs.getString("TABLE_NAME");
                String column = rs.getString("COLUMN_NAME");
                int idx = rs.getInt("ORDINAL_POSITION");
                String dataType = rs.getString("DATA_TYPE");
                String key = rs.getString("COLUMN_KEY");
                if (column != null && idx >= 1) {
                    map.put(column, new Colum(schema, tableName, idx - 1, column, dataType,key)); // sql的位置从1开始
                }
            }
            System.out.println(map);
            ps.close();
            rs.close();
            return map;
        } catch (SQLException e) {
            System.out.println("错误"+e);
        }
        return null;
    }

    public static Map<String, Map<String, Colum>> getDbTableCols(String db, String table,Conf conf) throws Exception {
        Map<String, Map<String, Colum>> dbTableCols = new HashMap<>();
        String dbTable = getdbTable(db, table);
        // 获取字段集合
        Map<String, Colum> cols = getColMap(conf, db, table);
        // 保存字段信息
        dbTableCols.put(dbTable, cols);
        return dbTableCols;
    }

    /**
     * 获取操作类型
     *
     * @param binLogItem
     * @return
     */
    public static Integer getOptType(BinLogItem binLogItem) {
        // 获取操作类型
        EventType eventType = binLogItem.getEventType();
        if (isWrite(eventType)) {
            return 1;
        }
        if (isUpdate(eventType)) {
            return 2;
        }
        if (isDelete(eventType)) {
            return 3;
        }
        return null;
    }

    public static BeforeAndAfterSqlDTO getSql(BinLogItem binLogItem){
        BeforeAndAfterSqlDTO beforeAndAfterSqlDTO = new BeforeAndAfterSqlDTO();
        if (ObjectUtil.isNotNull(binLogItem)){
            Integer optType = getOptType(binLogItem);
            String[] dbTableArr =binLogItem.getDbTable().split("-");
            String db = dbTableArr[0];
            String table = dbTableArr[1];
            String beforeSql = "";
            String afterSql = "";
            switch (optType){
                case 1:
                    getSqlByInsertType(binLogItem, beforeAndAfterSqlDTO, db, table);
                    break;
                case 2:
                    getSqlByUpdateType(binLogItem, beforeAndAfterSqlDTO, db, table);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("非法操作类型");
                    break;
            }
            return beforeAndAfterSqlDTO;
        }
        return beforeAndAfterSqlDTO;
    }

    //未完成
    private static void getSqlByUpdateType(BinLogItem binLogItem, BeforeAndAfterSqlDTO beforeAndAfterSqlDTO, String db, String table) {
        String afterSql;
        String beforeSql;

        if (ObjectUtil.isNotNull(binLogItem.getAfter())){
            List<String> valueList = Lists.newArrayList();
            List<String> columList = Lists.newArrayList();
            AtomicReference<String> keyColum = new AtomicReference<>("");
            binLogItem.getAfter().forEach((key,value) -> {
                Colum colum = binLogItem.getColums().get(key);
                String str = "";
                if (ObjectUtil.isNull(value)){
                    str = "null";
                }else {
                    str = value.toString();
                }
                if (colum.getDataType().equals("varchar")){
                    str = "'" + str +"'";
                }
                valueList.add(str);
                columList.add(key);
                if (colum.getKey().equals("PRI")){
                    if (colum.getDataType().equals("varchar")){
                        keyColum.set(colum.getColName() + "= '" + value + "';");
                    }else {
                        keyColum.set(colum.getColName() + "= " + value + ";");
                    }
                }
            });

            String valueStr = JSONUtil.toJsonStr(valueList).replace("[","(").replace("]",")");
            valueStr = StringUtils.remove(valueStr,'"');
            String columStr = JSONUtil.toJsonStr(columList).replace("[","(").replace("]",")").replace('"','`');
            beforeSql = "UPDATE `" + db + "`.`" + table + "` " + columStr + " VALUES " + valueStr + ";" ;
            afterSql = "DELETE FROM `" + db + "`.`" + table + "` WHERE " + keyColum;
        }
    }

    private static void getSqlByInsertType(BinLogItem binLogItem, BeforeAndAfterSqlDTO beforeAndAfterSqlDTO, String db, String table) {
        String afterSql;
        String beforeSql;
        List<String> valueList = Lists.newArrayList();
        List<String> columList = Lists.newArrayList();
        AtomicReference<String> keyColum = new AtomicReference<>("");
        binLogItem.getAfter().forEach((key, value) -> {
            Colum colum = binLogItem.getColums().get(key);
            String str = "";
            if (ObjectUtil.isNull(value)){
                str = "null";
            }else {
                str = value.toString();
            }
            if (colum.getDataType().equals("varchar")){
                str = "'" + str +"'";
            }
            valueList.add(str);
            columList.add(key);
            if (colum.getKey().equals("PRI")){
                if (colum.getDataType().equals("varchar")){
                    keyColum.set(colum.getColName() + "= '" + value + "';");
                }else {
                    keyColum.set(colum.getColName() + "= " + value + ";");
                }
            }
        });
        String valueStr = JSONUtil.toJsonStr(valueList).replace("[","(").replace("]",")");
        valueStr = StringUtils.remove(valueStr,'"');
        String columStr = JSONUtil.toJsonStr(columList).replace("[","(").replace("]",")").replace('"','`');
        beforeSql = "INSERT INTO `" + db + "`.`" + table + "` " + columStr + " VALUES " + valueStr + ";" ;
        afterSql = "DELETE FROM `" + db + "`.`" + table + "` WHERE " + keyColum;
        if (StringUtils.isNotBlank(beforeSql)){
            beforeAndAfterSqlDTO.setBeforeSql(beforeSql);
        }
        if (StringUtils.isNotBlank(afterSql)){
            List<String> afterSqlList = Lists.newArrayList();
            afterSqlList.add(afterSql);
            beforeAndAfterSqlDTO.setAfterSqlList(afterSqlList);
        }
    }

    public static void readBinLogInfo(BinaryLogFileReader reader, String dbTable, String db, String table, Conf conf,Long startTime,Long endTime) throws Exception {
        Map<String, Map<String, Colum>> dbTableCols = getDbTableCols(db, table, conf);
        BlockingQueue<BinLogItem> queue = new ArrayBlockingQueue<>(1024);
        try {
            for (Event event; (event = reader.readEvent()) != null;){
                if (ObjectUtil.isNotNull(startTime)){
                    if (startTime>event.getHeader().getTimestamp()){
                        continue;
                    }
                }
                if (ObjectUtil.isNotNull(endTime)){
                    if (endTime<event.getHeader().getTimestamp()){
                        continue;
                    }
                }
                EventType eventType = event.getHeader().getEventType();
                if (eventType == EventType.TABLE_MAP) {
                    TableMapEventData tableData = event.getData();
                    dbTable = getdbTable(db, table);
                }
                // 只处理添加删除更新三种操作
                if (isWrite(eventType) || isUpdate(eventType) || isDelete(eventType)) {
                    if (isWrite(eventType)) {
                        WriteRowsEventData data = event.getData();
                        for (Serializable[] row : data.getRows()) {
                            if (dbTableCols.containsKey(dbTable)) {
                                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, dbTableCols.get(dbTable), eventType);
                                item.setDbTable(dbTable);
                                queue.add(item);
                            }
                        }
                    }
                    if (isUpdate(eventType)) {
                        UpdateRowsEventData data = event.getData();
                        for (Map.Entry<Serializable[], Serializable[]> row : data.getRows()) {
                            if (dbTableCols.containsKey(dbTable)) {
                                BinLogItem item = BinLogItem.itemFromUpdate(row, dbTableCols.get(dbTable), eventType);
                                item.setDbTable(dbTable);
                                queue.add(item);
                            }
                        }
                    }
                    if (isDelete(eventType)) {
                        DeleteRowsEventData data = event.getData();
                        for (Serializable[] row : data.getRows()) {
                            if (dbTableCols.containsKey(dbTable)) {
                                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, dbTableCols.get(dbTable), eventType);
                                item.setDbTable(dbTable);
                                queue.add(item);
                            }
                        }
                    }
                }
                if (queue.size()>0){
                    try {
                        BinLogItem item = queue.take();
//                        System.out.println(item);
                        BeforeAndAfterSqlDTO beforeAndAfterSqlDTO = getSql(item);
                        if (ObjectUtil.isNotNull(beforeAndAfterSqlDTO)){
                            if (StringUtils.isNotBlank(beforeAndAfterSqlDTO.getBeforeSql())){
                                System.out.println("原始sql: " + beforeAndAfterSqlDTO.getBeforeSql());
                            }
                            if (CollectionUtil.isNotEmpty(beforeAndAfterSqlDTO.getAfterSqlList())){
                                beforeAndAfterSqlDTO.getAfterSqlList().forEach(sqlStr -> {
                                    if (StringUtils.isNotBlank(sqlStr)){
                                        System.out.println("回滚sql建议: " + sqlStr);
                                    }
                                });
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }finally {
            reader.close();
        }
    }
}
