package com.example.sendemail.controller;

import com.example.sendemail.model.Person;
import com.example.sendemail.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Map;


@Configuration
@EnableScheduling
public class SendEmailTask {
//    @Value("${mainCronEx}")
//    private  String mainCronEx;

    @Autowired
    private SendEmailService sendEmailService;

    private static Logger logger = LoggerFactory.getLogger(SendEmailTask.class);

    //定时任务,每10秒执行一次
    @Scheduled(cron = "${mainCronEx}")
    private void sendEmail() {
        try {
            //获取过生日的人员信息
            Map<String, Person> personMessage = sendEmailService.readTxt();
            //发送邮件
            for (Map.Entry<String, Person> entry : personMessage.entrySet()) {
                sendEmailService.sendEmail(entry.getValue());
            }
        }catch (Exception e){
            logger.info("发送邮件失败！");
        }
    }
}
