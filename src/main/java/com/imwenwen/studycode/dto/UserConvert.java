package com.imwenwen.studycode.dto;

import com.imwenwen.studycode.entity.User;
import com.imwenwen.studycode.request.dto.MyDTO;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/6/5 14:13
 * @version: 1.0
 */
public class UserConvert implements DTOConvert<MyDTO, User>{
    @Override
    public User convertBy(MyDTO myDTO) {
        User user = new User();
        user.setName(myDTO.getName());
        user.setNickName("nick:"+myDTO.getName());
        return user;
    }
}
