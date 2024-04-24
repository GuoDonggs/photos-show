package com.owofurry.furry.img.utils;

import com.owofurry.furry.img.exception.SystemRunningException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MailUtil {
    private static final String DEFAULT_HTML_CENTER =
            """
                          <h3 class="ttile">${title}</h3>
                          <div class="text">${text}</div>
                          <div class="notice">${notice}</div>
                    """;
    private static final StringBuilder pre;
    private static final StringBuilder end;
    private static JavaMailSender sender;
    private static String from;

    static {
        try {
            ClassPathResource htmlRes = new ClassPathResource("/static/defaultHtml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(htmlRes.getInputStream()));
            pre = new StringBuilder();
            end = new StringBuilder();
            StringBuilder t = pre;
            String temp;
            while ((temp = reader.readLine()) != null) {
                if (temp.matches(".*\\$\\{.*}.*")) {
                    t = end;
                } else {
                    t.append(temp);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init(JavaMailSender mailSender, String fromMail) {
        sender = mailSender;
        from = fromMail;
    }

    @SneakyThrows
    public static void sendText(String text, String receivingMail) {
        MimeMessageHelper helper = getMimeMessage();
        helper.setTo(receivingMail);
        helper.setText(text);
        helper.setFrom(from, "OWOFURRY");
        sender.send(helper.getMimeMessage());
    }

    @SneakyThrows
    public static void sendHtml(String html, String subject, String receivingMail) {
        MimeMessageHelper helper = getMimeMessage();
        helper.setTo(receivingMail);
        helper.setSubject(subject);
        helper.setFrom(from, "OWOFURRY");
        helper.setText(html, true);
        sender.send(helper.getMimeMessage());
    }

    @SneakyThrows
    public static void sendDefaultHtml(String title,
                                       String content,
                                       String notice,
                                       String receivingMail) {
        String html = pre + DEFAULT_HTML_CENTER
                .replace("${title}", title)
                .replace("${text}", content)
                .replace("${notice}", notice) +
                end;
        MimeMessageHelper helper = getMimeMessage();
        helper.setTo(receivingMail);
        helper.setSubject(title);
        helper.setFrom(from, "OWOFURRY");
        helper.setText(html, true);
        sender.send(helper.getMimeMessage());
    }


    private static MimeMessageHelper getMimeMessage() {
        if (sender != null) {
            MimeMessage message = sender.createMimeMessage();
            return new MimeMessageHelper(message);
        } else {
            throw new SystemRunningException("sender not init");
        }
    }


}
