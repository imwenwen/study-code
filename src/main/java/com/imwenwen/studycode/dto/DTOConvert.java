package com.imwenwen.studycode.dto;

public interface DTOConvert<S,T> {
  T convertBy(S s);
}
