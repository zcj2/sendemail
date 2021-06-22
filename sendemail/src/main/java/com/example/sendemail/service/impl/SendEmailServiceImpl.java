package com.example.sendemail.service.impl;

import com.example.sendemail.model.Person;
import com.example.sendemail.service.SendEmailService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${mail.transport.protocol}")
    private String protocol;

    @Value("${mail.smtp.auth}")
    private String auth;

    @Value("${phraseWord}")
    private String phraseWord;



    //读取TXT文件内容,获取过生日员工的Map
    public Map<String, Person> readTxt() throws IOException, URISyntaxException {
        String s = "";
        //格式化当天日期
        String strDateFormat = "MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String today =sdf.format(new Date());

        String path = this.getClass().getClassLoader().getResource("birthday.txt").toURI().getPath();
        System.out.println(path);
        //读取文件内容
        InputStreamReader in = new InputStreamReader(new FileInputStream(path), "UTF-8");

        BufferedReader br = new BufferedReader(in);

        Map<String, Person> personMessage = new HashMap<String,Person>();

        while ((s = br.readLine()) != null) {
            String []message=s.split(",");
            //如果员工信息完整，获取生日和邮箱
            if(message.length==4){
                //判断日期是否与生日相同
                if(message[2].substring(5).equals(today)){
                    Person person=new Person();
                    person.setJobNum(message[0]);
                    person.setPersonName(message[1]);
                    person.setBirthday(message[2]);
                    person.setEmail(message[3]);
                    personMessage.put(message[0],person);
                }
            }
        }
       return personMessage;
    }

    //发送邮件
    public void sendEmail(Person person){

        String from = username;            //发送人
        String to = person.getEmail();     //收件人
        String subject = "生日快乐";        //主题
        String smtpHost = host;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", protocol); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", smtpHost); // 发件人的邮箱的 SMTP服务器地址
        props.setProperty("mail.smtp.auth", auth); // 请求认证，参数名称与具体实现有关

        // 创建Session实例对象
        Session session = Session.getDefaultInstance(props);
        // 创建MimeMessage实例对象
        MimeMessage message = new MimeMessage(session);
        // 设置发件人
        try {
            message.setFrom(new InternetAddress(from));
            // 设置收件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            // 设置发送日期
            message.setSentDate(new Date());
            // 设置邮件主题
            message.setSubject(subject);
            // 设置纯文本内容的邮件正文
            message.setText(person.getPersonName()+ phraseWord);
            // 保存并生成最终的邮件内容
            message.saveChanges();
            // 设置为debug模式, 可以查看详细的发送 log
            session.setDebug(true);
            // 获取Transport对象
            Transport transport = session.getTransport(protocol);
            // 第2个参数需要填写的是QQ邮箱的SMTP的授权码，什么是授权码，它又是如何设置？
            transport.connect(from, password);
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println("发送完成");
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
