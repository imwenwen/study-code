package com.imwenwen.studycode.mapstruct;

import com.imwenwen.studycode.request.dto.MyDTO;
import com.imwenwen.studycode.request.param.MyParam;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2023/3/29 20:02
 * @version: 1.0
 */
@Mapper(componentModel = "spring")
public interface TestMapstruct {
    @Mapping(target = "optId",source = "optUid")
    @Mapping(target = "author", defaultValue = "imwenwen")
    @Mapping(target = "id", constant = "10L")
    @Mapping(target = "docx",expression = "java(param.getDoc()==0?\"成功\":\"失败\")")
    MyDTO toDto(MyParam param);


    @InheritInverseConfiguration
    MyParam toparam(MyDTO myDTO);
}
