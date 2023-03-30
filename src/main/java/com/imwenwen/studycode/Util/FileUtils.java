package com.imwenwen.studycode.Util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/3/27 18:00
 * @version: 1.0
 */
public class FileUtils {
    public static String readJsonFile(String filename){
        String jsonString = "";
        File jsonFile = new File(filename);
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((ch = reader.read()) != -1){
                stringBuffer.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonString = stringBuffer.toString();
        } catch (FileNotFoundException e){
            JSONObject notFoundJson = new JSONObject();
            notFoundJson.put("code",1111);
            notFoundJson.put("msg","该地区GeoJson文件不存在！");
            return "notFoundJson";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
