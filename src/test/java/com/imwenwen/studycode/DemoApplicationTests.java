package com.imwenwen.studycode;

import com.imwenwen.studycode.service.DisruptorMqService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StudyCodeApplication.class)
public class DemoApplicationTests {

    @Autowired
    private DisruptorMqService disruptorMqService;
    /**
     * 项目内部使用Disruptor做消息队列
     * @throws Exception
     */
    @Test
    public void sayHelloMqTest() throws Exception{

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
            threadPool.execute(() -> {
                for (int i = 0;i < 10; i++) {
                    try {
                        disruptorMqService.sayHelloMq("今天是12.30,请注意这是今年的最后一天哦!");
                        log.info("消息队列已发送完毕");
                        //这里停止2000ms是为了确定是处理消息是异步的
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


    }
}