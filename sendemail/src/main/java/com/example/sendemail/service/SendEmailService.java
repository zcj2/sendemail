package com.example.sendemail.service;

import com.example.sendemail.model.Person;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface SendEmailService {
    //读取TXT文件内容,获取过生日员工的Map
    public Map<String, Person> readTxt() throws IOException, URISyntaxException;
    //发送邮件
    public void sendEmail(Person person);
}
