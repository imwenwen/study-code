package com.imwenwen.studycode.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.DoNotParamAdvice;
import com.imwenwen.studycode.Util.FileUtils;
import com.imwenwen.studycode.dto.MenuDao;
import com.imwenwen.studycode.entity.Comment;
import com.imwenwen.studycode.entity.Menu;
import com.imwenwen.studycode.entity.User;
import com.imwenwen.studycode.mapstruct.TestMapstruct;
import com.imwenwen.studycode.request.dto.MyDTO;
import com.imwenwen.studycode.request.param.MyParam;
import com.imwenwen.studycode.service.DisruptorMqService;
import com.sxc.ShortLinkGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2022/12/6 17:07
 * @version: 1.0
 */
@RestController
@RequestMapping("/base")
@Slf4j
public class BaseController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private DisruptorMqService disruptorMqService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TestMapstruct testMapstruct;

    @Autowired
    ShortLinkGenerator shortLinkGenerator;

    final String ORDER_KEY = "order:";
    @RequestMapping(value ="/getMenuTree",method = RequestMethod.GET)
    public List<Menu> getMenuTree(){
        List<Menu> menusBase = menuDao.selectByPid(0);
        List<Menu> menuLNotBase = menuDao.selectAllNotBase();
        for (Menu menu : menusBase) {
            List<Menu> menus = iterateMenus(menuLNotBase, menu.getId());
            menu.setMenuChildren(menus);
        }
        return  menusBase;
    }

    public List<Menu> iterateMenus(List<Menu> menuVoList, String pid){
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menuVoList) {
            //获取菜单的id
            String menuid = menu.getId();
            //获取菜单的父id
            String parentid = menu.getPid();
            if(StringUtils.isNotBlank(parentid)){
                if(parentid.equals(pid)){
                    //递归查询当前子菜单的子菜单
                    List<Menu> iterateMenu = iterateMenus(menuVoList,menuid);
                    menu.setMenuChildren(iterateMenu);
                    result.add(menu);
                }
            }
        }
        return result;
    }

    @RequestMapping(value = "/testDisruptorMq",method = RequestMethod.GET)
    public void testDisruptorMq() throws InterruptedException {
        disruptorMqService.sayHelloMq("今天是12.30,请注意这是今年的最后一天哦!");
        log.info("消息队列已发送完毕");
        //这里停止2000ms是为了确定是处理消息是异步的
        Thread.sleep(2000);
    }



    @RequestMapping(value ="/testTrim/{str}",method = RequestMethod.GET)
    public User testTrim(@PathVariable(value = "str") @DoNotParamAdvice String str) {
        User user = new User();
        user.setName(str);
        user.setNickName("111"+str);

        return user;

    }

    @RequestMapping(value ="/testTrim02",method = RequestMethod.GET)
    public User testTrim02(@RequestBody User user) {
        return user;

    }


    @RequestMapping(value = "/testRedisToAdd",method = RequestMethod.POST)
    public String testRedisToAdd() {
        int keyId = ThreadLocalRandom.current().nextInt(1000)+1;
        String orderNo = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(ORDER_KEY+keyId,"京东订单"+ orderNo);
        return "=====>编号"+keyId+"的订单流水生成";
    }

    @RequestMapping(value ="/testRedisToGet/{keyId}",method = RequestMethod.GET)
    public String testRedisToGet(@PathVariable() Integer keyId) {
      return  (String) redisTemplate.opsForValue().get(ORDER_KEY+keyId);
    }


    @GetMapping(value ="/getComments")
    public String getComments() throws IOException {
        String fileUrl ="/Users/chenwenwei/Desktop/myProject/study-code/src/main/resources/static/test.json";

        String jsonObject = FileUtils.readJsonFile(fileUrl);

        JSONObject jsonObj = JSON.parseObject(jsonObject);
        JSONArray arr = jsonObj.getJSONArray("comments");

       List<String>commentsList = new ArrayList<String>();

        for (Object obj : arr) {
            JSONObject json = (JSONObject) obj;
            String comment = json.getString("content");
            commentsList.add(comment);
        }

        for (String s : commentsList) {
            System.out.println(s);
        }
        return JSON.toJSONString(commentsList);
    }



    @PostMapping(value = "/testMapstruct")
    public String testMapstruct(@RequestBody @Validated MyParam myParam){

        MyDTO myDTO = testMapstruct.toDto(myParam);
        System.out.println(myDTO.toString());

        MyParam param = testMapstruct.toparam(myDTO);
        System.out.println(param.toString());

        return "成功！";
    }

    @GetMapping(value = "/testSession")
    public String testSession(HttpServletRequest request, HttpServletResponse response){

        Cookie cookie = new Cookie("userId","imwenwen");
        response.addCookie(cookie);
        request.getSession().setAttribute("userId","imwenwen");

        return "成功！";
    }



    @ResponseBody
    @RequestMapping("/genUrl")
    public String generate(@RequestParam("url") String url) {
        System.out.println("longLink="+url);
      return shortLinkGenerator.generateShortLink(url);
    }

//    业务系统 给 //http://localhost:8080/base/genUrl?url=
//    翻译成 //  bmgeoo
    // 客户短配置锻炼服务前缀 prefix：http://localhost:8080/base/find?key=
    // 设计统一降级页面，传入降级页面地址来跳
    //prefix + ?key=bmgeoo
    //redirect https://juejin.cn/post/7210967900421111866?searchId=20230809101217424213A73C24271E9797"
    //http://localhost:8080/base/find?shortUrl=jmqiii
    @ResponseBody
    @RequestMapping("/find")
    public String find(@RequestParam("shortUrl") String shortUrl,HttpServletResponse response) {
           System.out.println("shortUrl"+shortUrl);
            String longLink = shortLinkGenerator.getLongLink(shortUrl);
            System.out.println("longLink="+longLink);
            return longLink;
    }

}
