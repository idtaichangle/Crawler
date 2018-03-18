package com.cvnavi.util;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.config.Config;

public class MailSender {
	
	static Logger log = LogManager.getLogger(MailSender.class);

	public static void sendMail(String subject, String body) {
		log.debug(subject);
		try {
			Properties props = new Properties();
			// 开启debug调试
			// props.setProperty("mail.debug", "true");
			// 发送服务器需要身份验证
			props.setProperty("mail.smtp.auth", "true");
			// 设置邮件服务器主机名
			props.setProperty("mail.host", Config.mailSmtpHost);
			// 发送邮件协议名称
			props.setProperty("mail.transport.protocol", "smtp");

			// 设置环境信息
			Session session = Session.getInstance(props);
			// 创建邮件对象
			Message msg = new MimeMessage(session);
			msg.setSubject(subject);
			// 设置邮件内容
			msg.setText(body);
			// 设置发件人
			msg.setFrom(new InternetAddress(Config.mailFrom));

			Transport transport = session.getTransport();
			// 连接邮件服务器
			transport.connect(Config.mailUser, Config.mailPassword);
			// 发送邮件
			transport.sendMessage(msg, new Address[] { new InternetAddress(Config.mailTo) });
			// 关闭连接
			transport.close();
		} catch (MessagingException ex) {
			log.error(ex);
		}
	}
}
