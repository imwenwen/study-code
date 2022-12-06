package com.imwenwen.studycode.dto;

import com.imwenwen.studycode.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2022/12/6 17:18
 * @version: 1.0
 */
@Mapper
public interface MenuDao {
    /**
     * 根据父类id查询子类菜单
     * @param pid
     * @return
     */
    List<Menu> selectByPid(Integer pid);

    /**
     * 查询所有的菜单
     * @return
     */
    List<Menu> selectAll();

    /**
     * 查询除了一级菜单以外的菜单
     * @return
     */
    List<Menu> selectAllNotBase();
}
