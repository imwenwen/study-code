package com.imwenwen.studycode.controller;

import com.imwenwen.studycode.dto.MenuDao;
import com.imwenwen.studycode.entity.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2022/12/6 17:07
 * @version: 1.0
 */
@RestController
@RequestMapping("/base")
public class BaseController {

    @Autowired
    private MenuDao menuDao;
    @RequestMapping("/getMenuTree")
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
}
