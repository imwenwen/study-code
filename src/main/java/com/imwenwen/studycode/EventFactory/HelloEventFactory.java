package com.imwenwen.studycode.EventFactory;

import com.imwenwen.studycode.Model.MessageModel;
import com.lmax.disruptor.EventFactory;

/**
 * @author: Imwenwen
 * @description: TODO
 * @date: 2022/12/30 10:30
 * @version: 1.0
 */
public class HelloEventFactory implements EventFactory<MessageModel> {
    @Override
    public MessageModel newInstance() {
        return new MessageModel();
    }
}
