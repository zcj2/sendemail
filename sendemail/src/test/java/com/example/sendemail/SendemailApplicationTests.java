package com.example.sendemail;

import com.example.sendemail.model.Person;
import com.example.sendemail.service.SendEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;


@SpringBootTest
class SendemailApplicationTests {

    @Autowired
    private SendEmailService sendEmailService;

    @Test
    void contextLoads() {
        try {
            //获取过生日的人员信息
            Map<String, Person> personMessage=sendEmailService.readTxt();
            //发送邮件
            for (Map.Entry<String, Person> entry : personMessage.entrySet()) {
                 sendEmailService.sendEmail(entry.getValue());
            }
        }catch (Exception e){
            System.out.println("发送邮件失败！");
        }

    }

}
