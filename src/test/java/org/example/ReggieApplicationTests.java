package org.example;

import org.example.utils.SMSUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1(){
        String fileName = "askidkl.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }

    @Test
    void testAliyun(){
        SMSUtils.sendMessage("阿里云短信测试","SMS_154950909","17306093289","1694");
    }

}
